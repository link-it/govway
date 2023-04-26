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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaApplicativaAutorizzazioneServiziApplicativi;
import org.openspcoop2.core.config.PortaApplicativaAutorizzazioneServizioApplicativo;
import org.openspcoop2.core.config.PortaApplicativaAutorizzazioneSoggetti;
import org.openspcoop2.core.config.PortaApplicativaAutorizzazioneSoggetto;
import org.openspcoop2.core.config.PortaApplicativaAutorizzazioneToken;
import org.openspcoop2.core.config.PortaApplicativaAzione;
import org.openspcoop2.core.config.PortaApplicativaBehaviour;
import org.openspcoop2.core.config.PortaApplicativaServizio;
import org.openspcoop2.core.config.PortaApplicativaServizioApplicativo;
import org.openspcoop2.core.config.PortaApplicativaServizioApplicativoConnettore;
import org.openspcoop2.core.config.PortaApplicativaSoggettoVirtuale;
import org.openspcoop2.core.config.PortaTracciamento;
import org.openspcoop2.core.config.Proprieta;
import org.openspcoop2.core.config.ResponseCachingConfigurazione;
import org.openspcoop2.core.config.Ruolo;
import org.openspcoop2.core.config.Scope;
import org.openspcoop2.core.config.Soggetto;
import org.openspcoop2.core.config.Trasformazioni;
import org.openspcoop2.core.config.ValidazioneContenutiApplicativi;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.config.constants.PortaApplicativaAzioneIdentificazione;
import org.openspcoop2.core.config.constants.RuoloTipoMatch;
import org.openspcoop2.core.config.constants.RuoloTipologia;
import org.openspcoop2.core.config.constants.ScopeTipoMatch;
import org.openspcoop2.core.config.driver.DriverConfigurazioneException;
import org.openspcoop2.core.config.driver.DriverConfigurazioneNotFound;
import org.openspcoop2.core.config.driver.ExtendedInfoManager;
import org.openspcoop2.core.config.driver.FiltroRicercaPorteApplicative;
import org.openspcoop2.core.config.driver.IDServizioUtils;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.id.IDConnettore;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.id.IdentificativiErogazione;
import org.openspcoop2.core.mapping.DBMappingUtils;
import org.openspcoop2.core.mapping.MappingErogazionePortaApplicativa;
import org.openspcoop2.utils.jdbc.JDBCUtilities;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.SQLObjectFactory;

/**
 * DriverConfigurazioneDB_porteApplicativeDriver
 * 
 * 
 * @author Sandra Giangrandi (sandra@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DriverConfigurazioneDB_porteApplicativeDriver {

	private DriverConfigurazioneDB driver = null;
	private DriverConfigurazioneDB_porteDriver porteDriver = null;
	private DriverConfigurazioneDB_soggettiDriver soggettiDriver = null;
	
	protected DriverConfigurazioneDB_porteApplicativeDriver(DriverConfigurazioneDB driver) {
		this.driver = driver;
		this.porteDriver = new DriverConfigurazioneDB_porteDriver(driver);
		this.soggettiDriver = new DriverConfigurazioneDB_soggettiDriver(driver);
	}
	
	protected IDPortaApplicativa getIDPortaApplicativa(String nome) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		
		String nomeMetodo = "getIDPortaApplicativa";
		
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

		this.driver.log.debug("operazione this.driver.atomica = " + this.driver.atomica);

		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("nome_porta = ?");
			sqlQuery = sqlQueryObject.createSQLQuery();
			stm = con.prepareStatement(sqlQuery);

			stm.setString(1, nome);

			this.driver.log.debug("eseguo query : " + DBUtils.formatSQLString(sqlQuery, nome));
			rs = stm.executeQuery();

			IDPortaApplicativa idPA = null;
			if (rs.next()) {
				
				idPA = new IDPortaApplicativa();
				
				idPA.setNome(rs.getString("nome_porta"));
				
				IdentificativiErogazione idErogazione = new IdentificativiErogazione();
				
				IDSoggetto idSoggettoProprietario = null;
				try{
					long idSoggProprietario = rs.getLong("id_soggetto");
					idSoggettoProprietario = this.soggettiDriver.getIdSoggetto(idSoggProprietario,con);
				} catch (Exception se) {
					throw new Exception(se.getMessage()); // mappo NotFound in una eccezione generica. Il Soggetto proprietario deve esistere
				}
				

				IDSoggetto idSoggettoVirtuale = null;
				String tipoSoggVirt=rs.getString("tipo_soggetto_virtuale");
				String nomeSoggVirt=rs.getString("nome_soggetto_virtuale");
				if(nomeSoggVirt!=null && !nomeSoggVirt.equals("") && tipoSoggVirt!=null && !tipoSoggVirt.equals("")){
					idSoggettoVirtuale = new IDSoggetto(tipoSoggVirt, nomeSoggVirt);
				}
				idErogazione.setSoggettoVirtuale(idSoggettoVirtuale);

				String tipoServizio = rs.getString("tipo_servizio");
				String nomeServizio = rs.getString("servizio");
				Integer versioneServizio = rs.getInt("versione_servizio");

				String nomeProprietarioServizio = null;
				String tipoProprietarioServizio = null;
				if(nomeSoggVirt!=null && !nomeSoggVirt.equals("") && tipoSoggVirt!=null && !tipoSoggVirt.equals("")){
					nomeProprietarioServizio=nomeSoggVirt;
					tipoProprietarioServizio=tipoSoggVirt;
				}else{
					nomeProprietarioServizio=idSoggettoProprietario.getNome();
					tipoProprietarioServizio=idSoggettoProprietario.getTipo();
				}

				long idServizioPA=-1;
				try {
					idServizioPA = DBUtils.getIdServizio(nomeServizio, tipoServizio, versioneServizio, nomeProprietarioServizio, tipoProprietarioServizio, con, this.driver.tipoDB,this.driver.tabellaSoggetti);
				} catch (Exception e) {
					// NON Abilitare il log, poiche' la tabella servizi puo' non esistere per il driver di configurazione 
					// in un database che non ' quello della controlstation ma quello pdd.
					//this.driver.log.info(e);
				}
				IDServizio idServizio = null;
				if( (idServizioPA>0) || (nomeServizio!=null && !nomeServizio.equals("") && tipoServizio!=null && !tipoServizio.equals("")) ){
					idServizio = IDServizioUtils.buildIDServizio(tipoServizio, nomeServizio, 
							new IDSoggetto(tipoProprietarioServizio,nomeProprietarioServizio), versioneServizio);
				}
				
				String azione = rs.getString("azione");
				if(azione!=null && !"".equals(azione) && idServizio!=null){
					idServizio.setAzione(azione);
				}
				idErogazione.setIdServizio(idServizio);

				
				idPA.setIdentificativiErogazione(idErogazione);	
			}
			else{
				throw new DriverConfigurazioneNotFound("PortaApplicativa ["+nome+"] non esistente");
			}

			return idPA;

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
	
	protected PortaApplicativa getPortaApplicativa(IDPortaApplicativa idPA) throws DriverConfigurazioneException,DriverConfigurazioneNotFound {

		if (idPA == null)
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getPortaApplicativa] Parametro idPA Non Valido");

		String nome = idPA.getNome();

		if ((nome == null)){
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getPortaApplicativa] Parametri non Validi");
		}
		
		Connection con = null;
		PortaApplicativa pa = null;
		long idPortaApplicativa = 0;
		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("getPortaApplicativa(idPortaApplicativa)");

			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getPortaApplicativa] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.log.debug("operazione this.driver.atomica = " + this.driver.atomica);

		boolean trovato = false;
		try {

			try{
				idPortaApplicativa = DBUtils.getIdPortaApplicativa(nome, con, this.driver.tipoDB);
			} catch (Exception se) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getPortaApplicativa] Exception: " + se.getMessage(),se);
			}
			if(idPortaApplicativa>0){
				trovato = true;
			}
			
		} catch (Exception se) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getPortaApplicativa] Exception: " + se.getMessage(),se);
		} finally {

			this.driver.closeConnection(con);
		}

		if (trovato) {
			pa=this.driver.getPortaApplicativa(idPortaApplicativa);

		} else {
			throw new DriverConfigurazioneNotFound("PortaApplicativa ["+nome+"] non esistente");
		}

		return pa;
	}
	
	
	protected List<PortaApplicativa> getPorteApplicative(IDServizio idServizio, boolean ricercaPuntuale) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		return this.getEnginePortaApplicativa(idServizio, ricercaPuntuale, null);
	}
	
	protected List<PortaApplicativa> getPorteApplicativeVirtuali(IDSoggetto soggettoVirtuale,IDServizio idServizio, boolean ricercaPuntuale) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		return this.getEnginePortaApplicativa(idServizio, ricercaPuntuale, soggettoVirtuale);
	}
	
		
	private List<PortaApplicativa> getEnginePortaApplicativa(IDServizio service,boolean ricercaPuntuale, IDSoggetto soggettoVirtuale) throws DriverConfigurazioneException, DriverConfigurazioneNotFound {

		if (service==null)
			throw new DriverConfigurazioneException("[getPortaApplicativa] Parametro idServizio Non Valido");

		IDSoggetto soggettoErogatore = service.getSoggettoErogatore();
		if ((soggettoErogatore == null) || (service == null))
			throw new DriverConfigurazioneException("[getPortaApplicativa] Parametri Non Validi");
		String servizio = service.getNome();
		String tipoServizio = service.getTipo();
		Integer versioneServizio = service.getVersione();
		String azione = service.getAzione();
		if ((servizio == null) || (tipoServizio == null))
			throw new DriverConfigurazioneException("[getPortaApplicativa] Parametri (Servizio) Non Validi");

		if(soggettoVirtuale!=null){
			if(soggettoVirtuale.getTipo()==null || soggettoVirtuale.getNome()==null)
				throw new DriverConfigurazioneException("[getPortaApplicativa] Parametri (Soggetto Virtuale) non validi");
		}

		// Devi cercare una porta applicativa che appartiene al soggetto
		// erogatore ( tipo+nome)
		// e che possiede il tipoServizio,servizio e azione specificata
		// Nota, siccome l'azione e' opzionale e puo' essere null,
		// Se l'azione e' null, chiaramente la ricerca consiste nel trovare una
		// PA con quel servizio (tipo+nome)
		// Se l'azione non e' null, la ricerca deve prima essere effettuata
		// anche con l'azione, e se questa fallisce,
		// viene effettuata allora senza l'azione controllando solo il servizio.

		// Nel caso la ricerca non sia puntuale, se non si trovano PA con un'azione specifica, si prova a cercarla con azione non definita. 

		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;
		String sqlQuery = null;

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("getPortaApplicativa(idPortaApplicativa)");

			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getPortaApplicativa] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.log.debug("operazione this.driver.atomica = " + this.driver.atomica);

		List<Long> idPorteApplicative = new ArrayList<Long>();
		try {
			// Soggetto Proprietario della Porta Applicativa
			long idSoggErog = 0;
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(this.driver.tabellaSoggetti);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("nome_soggetto=?");
			sqlQueryObject.addWhereCondition("tipo_soggetto=?");
			sqlQueryObject.setANDLogicOperator(true);
			sqlQuery = sqlQueryObject.createSQLQuery();

			stm = con.prepareStatement(sqlQuery,ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			stm.setString(1, soggettoErogatore.getNome());
			stm.setString(2, soggettoErogatore.getTipo());
			rs = stm.executeQuery();

			this.driver.log.debug("eseguo query soggetto" + DBUtils.formatSQLString(sqlQuery, soggettoErogatore.getNome() ,soggettoErogatore.getTipo() ));

			if (rs.next()) {
				idSoggErog = rs.getLong("id");
				rs.close();
				stm.close();
			} else
				throw new DriverConfigurazioneNotFound("[DriverConfigurazioneDB::getPortaApplicativa] Nessuno soggetto trovato [" + soggettoErogatore.getNome() + "," + soggettoErogatore.getTipo() + "].");

			// Eventuale SoggettoVirtuale
			long idSoggVirtuale = 0;
			if(soggettoVirtuale!=null){
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(this.driver.tabellaSoggetti);
				sqlQueryObject.addSelectField("*");
				sqlQueryObject.addWhereCondition("nome_soggetto=?");
				sqlQueryObject.addWhereCondition("tipo_soggetto=?");
				sqlQueryObject.setANDLogicOperator(true);
				sqlQuery = sqlQueryObject.createSQLQuery();

				stm = con.prepareStatement(sqlQuery,ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
				stm.setString(1, soggettoVirtuale.getNome());
				stm.setString(2, soggettoVirtuale.getTipo());
				rs = stm.executeQuery();

				this.driver.log.debug("eseguo query soggetto virtuale" + DBUtils.formatSQLString(sqlQuery, soggettoVirtuale.getNome() ,soggettoVirtuale.getTipo() ));

				if (rs.next()) {
					idSoggVirtuale = rs.getLong("id");
				} 
				rs.close();
				stm.close();
				//else
				//	throw new DriverConfigurazioneNotFound("[DriverConfigurazioneDB::getPortaApplicativa] Nessuno soggetto virtuale trovato [" + soggettoVirtuale.getNome() + "," + soggettoVirtuale.getTipo() + "].");
				// L'ID non c'e' per forza. Nel database pdd, il soggetto virtuale non c'e'.
			}


			this.driver.log.debug("eseguo soggetto:" +idSoggErog);

			if (azione == null || azione.trim().equals("")) {
				this.driver.log.debug("ricerca PA con azione == null, soggettoVirtuale="+soggettoVirtuale);
				if(soggettoVirtuale==null) {
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
					sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE);
					sqlQueryObject.addSelectField("*");
					sqlQueryObject.addWhereCondition("id_soggetto = ?");
					sqlQueryObject.addWhereCondition("tipo_servizio = ?");
					sqlQueryObject.addWhereCondition("servizio = ?");
					sqlQueryObject.addWhereCondition("versione_servizio = ?");
					sqlQueryObject.addWhereCondition(false, "mode_azione IS NULL", "mode_azione<>?");
					sqlQueryObject.addWhereCondition(false, "azione IS NULL", "azione = ?", "azione = ?");
					sqlQueryObject.setANDLogicOperator(true);
					sqlQuery = sqlQueryObject.createSQLQuery();
				} else {
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
					sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE);
					sqlQueryObject.addSelectField("*");
					sqlQueryObject.addWhereCondition("id_soggetto = ?");
					sqlQueryObject.addWhereCondition(false, "id_soggetto_virtuale = ?", "tipo_soggetto_virtuale = ? AND nome_soggetto_virtuale = ?");
					sqlQueryObject.addWhereCondition("tipo_servizio = ?");
					sqlQueryObject.addWhereCondition("servizio = ?");
					sqlQueryObject.addWhereCondition("versione_servizio = ?");
					sqlQueryObject.addWhereCondition(false, "mode_azione IS NULL", "mode_azione<>?");
					sqlQueryObject.addWhereCondition(false, "azione IS NULL", "azione = ?", "azione = ?");
					sqlQueryObject.setANDLogicOperator(true);
					sqlQuery = sqlQueryObject.createSQLQuery();
				}

				stm = con.prepareStatement(sqlQuery,ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

				if(soggettoVirtuale==null){
					int index = 1;
					stm.setLong(index++, idSoggErog);
					stm.setString(index++, tipoServizio);
					stm.setString(index++, servizio);
					stm.setInt(index++, versioneServizio);
					stm.setString(index++, PortaApplicativaAzioneIdentificazione.DELEGATED_BY.getValue());
					stm.setString(index++, "");
					stm.setString(index++, "-");
					this.driver.log.debug("eseguo query " + DBUtils.formatSQLString(sqlQuery, idSoggErog, tipoServizio, servizio,versioneServizio,"","-"));
				}else{
					int index = 1;
					stm.setLong(index++, idSoggErog);
					stm.setLong(index++, idSoggVirtuale);
					stm.setString(index++, soggettoVirtuale.getTipo());
					stm.setString(index++, soggettoVirtuale.getNome());
					stm.setString(index++, tipoServizio);
					stm.setString(index++, servizio);
					stm.setInt(index++, versioneServizio);
					stm.setString(index++, PortaApplicativaAzioneIdentificazione.DELEGATED_BY.getValue());
					stm.setString(index++, "");
					stm.setString(index++, "-");
					this.driver.log.debug("eseguo query " + DBUtils.formatSQLString(sqlQuery, idSoggErog, soggettoErogatore.getTipo(),soggettoErogatore.getNome(),
							tipoServizio, servizio,versioneServizio,"","-"));
				}



			} else {
				this.driver.log.debug("ricerca PA con azione != null, soggettoVirtuale="+soggettoVirtuale);
				if(soggettoVirtuale==null) {
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
					sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE);
					sqlQueryObject.addSelectField("*");
					sqlQueryObject.addWhereCondition("id_soggetto=?");
					sqlQueryObject.addWhereCondition("tipo_servizio=?");
					sqlQueryObject.addWhereCondition("servizio=?");
					sqlQueryObject.addWhereCondition("versione_servizio = ?");
					sqlQueryObject.addWhereCondition(false, "mode_azione IS NULL", "mode_azione<>?");
					sqlQueryObject.addWhereCondition("azione=?");
					sqlQueryObject.setANDLogicOperator(true);
					sqlQuery = sqlQueryObject.createSQLQuery();
				} else {
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
					sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE);
					sqlQueryObject.addSelectField("*");
					sqlQueryObject.addWhereCondition("id_soggetto = ?");
					sqlQueryObject.addWhereCondition(false, "id_soggetto_virtuale = ?", "tipo_soggetto_virtuale = ? AND nome_soggetto_virtuale = ?");
					sqlQueryObject.addWhereCondition("tipo_servizio = ?");
					sqlQueryObject.addWhereCondition("servizio = ?");
					sqlQueryObject.addWhereCondition("versione_servizio = ?");
					sqlQueryObject.addWhereCondition(false, "mode_azione IS NULL", "mode_azione<>?");
					sqlQueryObject.addWhereCondition("azione = ?");
					sqlQueryObject.setANDLogicOperator(true);
					sqlQuery = sqlQueryObject.createSQLQuery();
				}

				stm = con.prepareStatement(sqlQuery,ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

				if(soggettoVirtuale==null){
					int index = 1;
					stm.setLong(index++, idSoggErog);
					stm.setString(index++, tipoServizio);
					stm.setString(index++, servizio);
					stm.setInt(index++, versioneServizio);
					stm.setString(index++, PortaApplicativaAzioneIdentificazione.DELEGATED_BY.getValue());
					stm.setString(index++, azione);
					this.driver.log.debug("eseguo query " + DBUtils.formatSQLString(sqlQuery, idSoggErog, tipoServizio, servizio, versioneServizio, azione));
				}else
				{
					int index = 1;
					stm.setLong(index++, idSoggErog);
					stm.setLong(index++, idSoggVirtuale);
					stm.setString(index++, soggettoVirtuale.getTipo());
					stm.setString(index++, soggettoVirtuale.getNome());
					stm.setString(index++, tipoServizio);
					stm.setString(index++, servizio);
					stm.setInt(index++, versioneServizio);
					stm.setString(index++, PortaApplicativaAzioneIdentificazione.DELEGATED_BY.getValue());
					stm.setString(index++, azione);
					this.driver.log.debug("eseguo query " + DBUtils.formatSQLString(sqlQuery, idSoggErog, tipoServizio, servizio, versioneServizio, azione));
				}
			}


			rs = stm.executeQuery();

			while(rs.next()){
				long idPortaApplicativa = rs.getLong("id");
				idPorteApplicative.add(idPortaApplicativa);
			}
			rs.close();
			stm.close();
				

			this.driver.log.debug("ricerca puntuale="+ricercaPuntuale);
			if(!ricercaPuntuale){
				if (idPorteApplicative.size()==0 && azione != null) {
					this.driver.log.debug("ricerca PA con azione != null ma con solo il servizio, soggettoVirtuale="+soggettoVirtuale);
			
					// in questo caso provo a cercaredelle porte applicative solo
					// con id_soggetto tipo_servizio e servizio
					if(soggettoVirtuale==null) {
						sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
						sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE);
						sqlQueryObject.addSelectField("*");
						sqlQueryObject.addWhereCondition("id_soggetto = ?");
						sqlQueryObject.addWhereCondition("tipo_servizio = ?");
						sqlQueryObject.addWhereCondition("servizio = ?");
						sqlQueryObject.addWhereCondition("versione_servizio = ?");
						sqlQueryObject.addWhereCondition(false, "mode_azione IS NULL", "mode_azione<>?");
						sqlQueryObject.addWhereCondition(false, "azione IS NULL", "azione = ?", "azione = ?");
						sqlQueryObject.setANDLogicOperator(true);
						sqlQuery = sqlQueryObject.createSQLQuery();
					} else {
						sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
						sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE);
						sqlQueryObject.addSelectField("*");
						sqlQueryObject.addWhereCondition("id_soggetto = ?");
						sqlQueryObject.addWhereCondition(false, "id_soggetto_virtuale = ?", "tipo_soggetto_virtuale = ? AND nome_soggetto_virtuale = ?");
						sqlQueryObject.addWhereCondition("tipo_servizio = ?");
						sqlQueryObject.addWhereCondition("servizio = ?");
						sqlQueryObject.addWhereCondition("versione_servizio = ?");
						sqlQueryObject.addWhereCondition(false, "mode_azione IS NULL", "mode_azione<>?");
						sqlQueryObject.addWhereCondition(false, "azione IS NULL", "azione = ?", "azione = ?");
						sqlQueryObject.setANDLogicOperator(true);
						sqlQuery = sqlQueryObject.createSQLQuery();
					}

					stm = con.prepareStatement(sqlQuery,ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

					int index = 1;
					if(soggettoVirtuale==null){
						stm.setLong(index++, idSoggErog);
						stm.setString(index++, tipoServizio);
						stm.setString(index++, servizio);
						stm.setInt(index++, versioneServizio);
						stm.setString(index++, PortaApplicativaAzioneIdentificazione.DELEGATED_BY.getValue());
						stm.setString(index++, "");
						stm.setString(index++, "-");
					}else{
						stm.setLong(index++, idSoggErog);
						stm.setLong(index++, idSoggVirtuale);
						stm.setString(index++, soggettoVirtuale.getTipo());
						stm.setString(index++, soggettoVirtuale.getNome());
						stm.setString(index++, tipoServizio);
						stm.setString(index++, servizio);
						stm.setInt(index++, versioneServizio);
						stm.setString(index++, PortaApplicativaAzioneIdentificazione.DELEGATED_BY.getValue());
						stm.setString(index++, "");
						stm.setString(index++, "-");
					}


					this.driver.log.debug("eseguo query " + DBUtils.formatSQLString(sqlQuery, idSoggErog, tipoServizio, servizio));

					rs = stm.executeQuery();

					while(rs.next()){
						long idPortaApplicativa = rs.getLong("id");
						idPorteApplicative.add(idPortaApplicativa);
					}
					
				}
			}

		} catch (SQLException se) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getPortaApplicativa] SqlException: " + se.getMessage(),se);
		} catch (DriverConfigurazioneNotFound e) {
			throw new DriverConfigurazioneNotFound(e);
		}catch (Exception se) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getPortaApplicativa] Exception: " + se.getMessage(),se);
		}finally {
			//Chiudo statement and resultset
			JDBCUtilities.closeResources(rs, stm);
			this.driver.closeConnection(con);
		}

		if(idPorteApplicative.size()<=0){
			throw new DriverConfigurazioneNotFound("Porte Applicative non esistenti");
		}
		List<PortaApplicativa> pa = new ArrayList<PortaApplicativa>();
		for (Long idPA : idPorteApplicative) {
			pa.add(this.getPortaApplicativa(idPA));
		}

		return pa;
	}

	protected void createPortaApplicativa(PortaApplicativa aPA) throws DriverConfigurazioneException {
		if (aPA == null)
			throw new DriverConfigurazioneException("Porta Applicativa non valida");
		if (aPA.getNome() == null || aPA.getNome().equals(""))
			throw new DriverConfigurazioneException("Nome Porta Applicativa non valido");
		if (aPA.getNomeSoggettoProprietario() == null || aPA.getNomeSoggettoProprietario().equals(""))
			throw new DriverConfigurazioneException("Nome Soggetto Proprietario Porta Applicativa non valido");
		if (aPA.getTipoSoggettoProprietario() == null || aPA.getTipoSoggettoProprietario().equals(""))
			throw new DriverConfigurazioneException("Tipo Soggetto Proprietario Porta Applicativa non valido");

		Connection con = null;
		boolean error = false;

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("createPortaApplicativa");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::createPortaApplicativa] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.log.debug("operazione this.driver.atomica = " + this.driver.atomica);

		try {
			this.driver.log.debug("CRUDPortaApplicativa type = 1");

			DriverConfigurazioneDB_porteApplicativeLIB.CRUDPortaApplicativa(1, aPA, con);

			this.driver.log.debug("Creazione PortaApplicativa [" + aPA.getId() + "] completato.");

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::createPortaApplicativa] Errore durante la creazione della PortaApplicativa : " + qe.getMessage(),qe);
		} finally {

			this.driver.closeConnection(error,con);
		}
	}
	
	protected void updatePortaApplicativa(PortaApplicativa aPA) throws DriverConfigurazioneException {
		if (aPA == null)
			throw new DriverConfigurazioneException("Porta Applicativa non valida");
		if (aPA.getNome() == null || aPA.getNome().equals(""))
			throw new DriverConfigurazioneException("Nome Porta Applicativa non valido");
		if (aPA.getNomeSoggettoProprietario() == null || aPA.getNomeSoggettoProprietario().equals(""))
			throw new DriverConfigurazioneException("Nome Soggetto Proprietario Porta Applicativa non valido");
		if (aPA.getTipoSoggettoProprietario() == null || aPA.getTipoSoggettoProprietario().equals(""))
			throw new DriverConfigurazioneException("Tipo Soggetto Proprietario Porta Applicativa non valido");

		Connection con = null;
		boolean error = false;

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("updatePortaApplicativa");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::updatePortaApplicativa] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.log.debug("operazione this.driver.atomica = " + this.driver.atomica);

		try {
			this.driver.log.debug("CRUDPortaApplicativa type = 2");

			long id = DriverConfigurazioneDB_porteApplicativeLIB.CRUDPortaApplicativa(2, aPA, con);

			this.driver.log.debug("Aggiornamento PortaApplicativa [" + id + "] completato.");

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::updatePortaApplicativa] Errore durante l'update della PortaApplicativa : " + qe.getMessage(),qe);
		} finally {

			this.driver.closeConnection(error,con);
		}
	}

	protected void deletePortaApplicativa(PortaApplicativa aPA) throws DriverConfigurazioneException {
		if (aPA == null)
			throw new DriverConfigurazioneException("Porta Applicativa non valida");
		if (aPA.getNome() == null || aPA.getNome().equals(""))
			throw new DriverConfigurazioneException("Nome Porta Applicativa non valido");
		if (aPA.getNomeSoggettoProprietario() == null || aPA.getNomeSoggettoProprietario().equals(""))
			throw new DriverConfigurazioneException("Nome Soggetto Proprietario Porta Applicativa non valido");
		if (aPA.getTipoSoggettoProprietario() == null || aPA.getTipoSoggettoProprietario().equals(""))
			throw new DriverConfigurazioneException("Tipo Soggetto Proprietario Porta Applicativa non valido");

		Connection con = null;
		boolean error = false;

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("deletePortaApplicativa");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::deletePortaApplicativa] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.log.debug("operazione this.driver.atomica = " + this.driver.atomica);

		try {
			this.driver.log.debug("CRUDPortaApplicativa type = 3");

			long id = DriverConfigurazioneDB_porteApplicativeLIB.CRUDPortaApplicativa(3, aPA, con);

			this.driver.log.debug("Cancellazione PortaApplicativa [" + id + "] completato.");

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::deletePortaApplicativa] Errore durante la cancellazione della PortaApplicativa : " + qe.getMessage(),qe);
		} finally {

			this.driver.closeConnection(error,con);
		}
	}

	protected Map<IDSoggetto, PortaApplicativa> getPorteApplicative_SoggettiVirtuali(IDServizio idServizio) throws DriverConfigurazioneException,DriverConfigurazioneNotFound {

		this.driver.log.debug("metodo getPorteApplicative_SoggettiVirtuali in esecuzione...");

		if (idServizio == null)
			throw new DriverConfigurazioneException("[getPortaApplicativa_SoggettiVirtuali] Parametro idServizio Non Valido");
		if (idServizio.getSoggettoErogatore() == null)
			throw new DriverConfigurazioneException("[getPortaApplicativa_SoggettiVirtuali] Parametro Soggetto Erogatore Non Valido");
		Map<IDSoggetto, PortaApplicativa> paConSoggetti = new HashMap<IDSoggetto, PortaApplicativa>();
		IDSoggetto soggettoVirtuale = idServizio.getSoggettoErogatore();
		String servizio = idServizio.getNome();
		String tipoServizio = idServizio.getTipo();
		Integer versioneServizio = idServizio.getVersione();
		String azione = idServizio.getAzione();
		if ((servizio == null) || (tipoServizio == null))
			throw new DriverConfigurazioneException("[getPortaApplicativa_SoggettiVirtuali] Parametri (Servizio) Non Validi");

		/*
		 * // Devi cercare tutte le porte applicative che hanno come soggetto
		 * virtuale il soggettoVirtuale // con servizio,tipoServizio e azione
		 * come specificato nel precendente metodo.
		 *  // Per ogni porta applicativa che trovi devi creare un IDSoggetto
		 * del soggetto proprietario della PA trovata IDSoggetto sogg = new
		 * IDSoggetto(tipo,nome); // Riempi i dati della PA trovata come detto
		 * nel precedente metodo. PortaApplicativa pa = new PortaApplicativa();
		 * pa.setDescrizione(descrizione); ........ // aggiungi
		 * paConSoggetti.put(IDSoggetto,pa);
		 */
		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;
		String sqlQuery = null;
		ResultSet rs1=null;
		PreparedStatement stm1 = null;
		ResultSet rs2=null;
		PreparedStatement stm2 = null;

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("getPorteApplicative_SoggettiVirtuali");

			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getPorteApplicative_SoggettiVirtuali] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.log.debug("operazione this.driver.atomica = " + this.driver.atomica);

		try {
			String nomeSoggVirt = soggettoVirtuale.getNome();
			String tipoSoggVirt = soggettoVirtuale.getTipo();

			if (nomeSoggVirt == null || nomeSoggVirt.equals("") || tipoSoggVirt == null || tipoSoggVirt.equals(""))
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getPorteApplicative_SoggettiVirtuali] Parametri SoggettoVirtuale non corretti.");

			//Prendo la lista dei soggetti presenti
			ArrayList<Long> soggettiList = new ArrayList<Long>();
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(this.driver.tabellaSoggetti);
			sqlQueryObject.addSelectField("id");
			sqlQuery = sqlQueryObject.createSQLQuery();
			stm = con.prepareStatement(sqlQuery);
			rs = stm.executeQuery();
			while (rs.next()) {
				soggettiList.add(rs.getLong(1));
			}
			rs.close();
			stm.close();

			//controllo le porte applicative dei soggetti che soddisfano i criteri di ricerca
			for (Long idSoggetto : soggettiList) {
				//Azione non settata eseguo query con azione NULL
				if (azione == null || azione.trim().equals("")) {
					this.driver.log.debug("azione == null");
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
					sqlQueryObject.addFromTable(this.driver.tabellaSoggetti);
					sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE);
					//sqlQueryObject.addSelectField("*");
					sqlQueryObject.addSelectAliasField(CostantiDB.PORTE_APPLICATIVE+".descrizione", "descrizionePA");
					sqlQueryObject.addSelectAliasField(CostantiDB.PORTE_APPLICATIVE+".id", "id_porta_applicativa");
					sqlQueryObject.addSelectAliasField(this.driver.tabellaSoggetti+".id", "id_proprietario");
					sqlQueryObject.addWhereCondition(this.driver.tabellaSoggetti+".id = "+CostantiDB.PORTE_APPLICATIVE+".id_soggetto");
					sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE+".nome_soggetto_virtuale = ?");
					sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE+".tipo_soggetto_virtuale = ?");
					sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE+".tipo_servizio = ?");
					sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE+".servizio = ?");
					sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE+".versione_servizio = ?");
					sqlQueryObject.addWhereCondition(false, CostantiDB.PORTE_APPLICATIVE+".azione IS NULL", CostantiDB.PORTE_APPLICATIVE+".azione = ?", CostantiDB.PORTE_APPLICATIVE+".azione = ?");
					sqlQueryObject.addWhereCondition(this.driver.tabellaSoggetti+".id = ?");
					sqlQueryObject.setANDLogicOperator(true);
					sqlQuery = sqlQueryObject.createSQLQuery();

					stm = con.prepareStatement(sqlQuery, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

					int index = 1;
					stm.setString(index++, nomeSoggVirt);
					stm.setString(index++, tipoSoggVirt);
					stm.setString(index++, tipoServizio);
					stm.setString(index++, servizio);
					stm.setInt(index++, versioneServizio);
					stm.setString(index++, "");
					stm.setString(index++, "-");
					stm.setLong(index++, idSoggetto);

					this.driver.log.debug("eseguo query " + DBUtils.formatSQLString(sqlQuery, nomeSoggVirt, tipoSoggVirt, tipoServizio, servizio));
				} else {
					//cerco porta applicativa con soggetto virtuale x con servizio y con azione z
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
					sqlQueryObject.addFromTable(this.driver.tabellaSoggetti);
					sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE);
					//sqlQueryObject.addSelectField("*");
					sqlQueryObject.addSelectAliasField(CostantiDB.PORTE_APPLICATIVE+".descrizione", "descrizionePA");
					sqlQueryObject.addSelectAliasField(CostantiDB.PORTE_APPLICATIVE+".id", "id_porta_applicativa");
					sqlQueryObject.addSelectAliasField(this.driver.tabellaSoggetti+".id", "id_proprietario");
					sqlQueryObject.addWhereCondition(this.driver.tabellaSoggetti+".id = "+CostantiDB.PORTE_APPLICATIVE+".id_soggetto");
					sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE+".nome_soggetto_virtuale = ?");
					sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE+".tipo_soggetto_virtuale = ?");
					sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE+".tipo_servizio = ?");
					sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE+".servizio = ?");
					sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE+".versione_servizio = ?");
					sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE+".azione = ?");
					sqlQueryObject.addWhereCondition(this.driver.tabellaSoggetti+".id = ?");
					sqlQueryObject.setANDLogicOperator(true);
					sqlQuery = sqlQueryObject.createSQLQuery();

					stm = con.prepareStatement(sqlQuery, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
					
					int index = 1;
					stm.setString(index++, nomeSoggVirt);
					stm.setString(index++, tipoSoggVirt);
					stm.setString(index++, tipoServizio);
					stm.setString(index++, servizio);
					stm.setInt(index++, versioneServizio);
					stm.setString(index++, azione);
					stm.setLong(index++, idSoggetto);

					this.driver.log.debug("eseguo query " + DBUtils.formatSQLString(sqlQuery, nomeSoggVirt, tipoSoggVirt, tipoServizio, servizio, versioneServizio, azione));
				}

				rs = stm.executeQuery();

				boolean trovato = rs.next();

				if (!trovato && azione != null) {

					this.driver.log.debug("Cerco PA generica (Azione non definita)");

					rs.close();
					stm.close();
					// in questo caso provo a cercare delle porte applicative solo
					// con id_soggetto tipo_servizio e servizio
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
					sqlQueryObject.addFromTable(this.driver.tabellaSoggetti);
					sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE);
					//sqlQueryObject.addSelectField("*");
					sqlQueryObject.addSelectAliasField(CostantiDB.PORTE_APPLICATIVE+".descrizione", "descrizionePA");
					sqlQueryObject.addSelectAliasField(CostantiDB.PORTE_APPLICATIVE+".id", "id_porta_applicativa");
					sqlQueryObject.addSelectAliasField(this.driver.tabellaSoggetti+".id", "id_proprietario");
					sqlQueryObject.addWhereCondition(this.driver.tabellaSoggetti+".id = "+CostantiDB.PORTE_APPLICATIVE+".id_soggetto");
					sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE+".nome_soggetto_virtuale = ?");
					sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE+".tipo_soggetto_virtuale = ?");
					sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE+".tipo_servizio = ?");
					sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE+".servizio = ?");
					sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE+".versione_servizio = ?");
					sqlQueryObject.addWhereCondition(false, CostantiDB.PORTE_APPLICATIVE+".azione IS NULL", CostantiDB.PORTE_APPLICATIVE+".azione = ?", CostantiDB.PORTE_APPLICATIVE+".azione = ?");
					sqlQueryObject.addWhereCondition(this.driver.tabellaSoggetti+".id = ?");
					sqlQueryObject.setANDLogicOperator(true);
					sqlQuery = sqlQueryObject.createSQLQuery();
					this.driver.log.debug("eseguo query " + DBUtils.formatSQLString(sqlQuery, nomeSoggVirt, tipoSoggVirt, tipoServizio, servizio, versioneServizio));
					this.driver.log.debug("QUERY RAW: "+sqlQuery);

					stm = con.prepareStatement(sqlQuery, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
					
					int index = 1;
					stm.setString(index++, nomeSoggVirt);
					stm.setString(index++, tipoSoggVirt);
					stm.setString(index++, tipoServizio);
					stm.setString(index++, servizio);
					stm.setInt(index++, versioneServizio);
					stm.setString(index++, "");
					stm.setString(index++, "-");
					stm.setLong(index++, idSoggetto);



					rs = stm.executeQuery();

					trovato = rs.next();
				}
				this.driver.log.debug("Ripristino rs");
				// ripristino il cursore del resultset
				rs.beforeFirst();

				// devo iterare su tutte le pa se ho trovato delle pa
				this.driver.log.debug("Itero rs...");
				while (trovato && rs.next()) {

					this.driver.log.debug("PortaApplicativa, raccolta dati");

					long idPortaApplicativa = rs.getLong("id_porta_applicativa");
					this.driver.log.debug("PortaApplicativa, raccolta dati id["+idPortaApplicativa+"] in corso...");
					PortaApplicativa pa = this.getPortaApplicativa(idPortaApplicativa,con);
					this.driver.log.debug("PortaApplicativa, raccolta dati id["+idPortaApplicativa+"] effettuata.");


					long idSoggettoProprietario = rs.getLong("id_proprietario");
					this.driver.log.debug("PortaApplicativa, raccolta dati soggetto id["+idSoggettoProprietario+"] in corso...");

					// recupero dati del soggetto proprietario
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
					sqlQueryObject.addFromTable(this.driver.tabellaSoggetti);
					sqlQueryObject.addSelectField("*");
					sqlQueryObject.addWhereCondition("id = ?");
					sqlQueryObject.setANDLogicOperator(true);
					sqlQuery = sqlQueryObject.createSQLQuery();
					stm1 = con.prepareStatement(sqlQuery);
					stm1.setLong(1, idSoggettoProprietario);
					rs1 = stm1.executeQuery();

					IDSoggetto soggettoProprietario = null;
					if (rs1.next()) {
						soggettoProprietario = new IDSoggetto(rs1.getString("tipo_soggetto"), rs1.getString("nome_soggetto"));
					} else {
						throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getPorteApplicative_SoggettiVirtuali] Impossibile trovare le informazioni del soggetto proprietario della PA.");
					}
					rs1.close();
					stm1.close();

					this.driver.log.debug("PortaApplicativa, raccolta dati soggetto id["+idSoggettoProprietario+"] completata.");

					// aggiungo la pa e il soggetto all'hashtable
					paConSoggetti.put(soggettoProprietario, pa);

				}//chiudo while

				rs.close();
				stm.close();

			}//chiudo for

			if(paConSoggetti.size() == 0)
				throw new DriverConfigurazioneNotFound("[getPortaApplicativa_SoggettiVirtuali] Porte applicative di soggetti virtuali non esistenti.");

			return paConSoggetti;

		} catch (SQLException se) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getPorteApplicative_SoggettiVirtuali] SqlException: " + se.getMessage(),se);
		} catch (DriverConfigurazioneNotFound de) {
			throw de;
		}catch (Exception se) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getPorteApplicative_SoggettiVirtuali] Exception: " + se.getMessage(),se);
		}finally {
			//Chiudo statement and resultset
			JDBCUtilities.closeResources(rs2, stm2);
			JDBCUtilities.closeResources(rs1, stm1);
			JDBCUtilities.closeResources(rs, stm);
			this.driver.closeConnection(con);
		}

	}
	
	
	
	protected List<IDPortaApplicativa> porteApplicativeWithApplicativoErogatore(IDServizioApplicativo idSA) throws DriverConfigurazioneException {
		String nomeMetodo = "porteApplicativeWithApplicativoErogatore";
		
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

		this.driver.log.debug("operazione this.driver.atomica = " + this.driver.atomica);

		List<IDPortaApplicativa> list = new ArrayList<IDPortaApplicativa>();
		try {
			long idSAlong = DBUtils.getIdServizioApplicativo(idSA.getNome(), idSA.getIdSoggettoProprietario().getTipo(), idSA.getIdSoggettoProprietario().getNome(), con, this.driver.tipoDB);
			
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addSelectField("nome_porta");
			sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_SA);
			sqlQueryObject.setANDLogicOperator(true);
			sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE_SA+".id_porta="+CostantiDB.PORTE_APPLICATIVE+".id");
			sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE_SA+".id_servizio_applicativo=?");
			
			stmt = con.prepareStatement(sqlQueryObject.createSQLQuery());
			stmt.setLong(1, idSAlong);

			risultato = stmt.executeQuery();
			while (risultato.next()) {
				String nomePorta = risultato.getString("nome_porta");
				IDPortaApplicativa idPA = new IDPortaApplicativa();
				idPA.setNome(nomePorta);
				list.add(idPA);
			}
			risultato.close();
			stmt.close();


		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
		} finally {

			//Chiudo statement and resultset
			JDBCUtilities.closeResources(risultato, stmt);

			this.driver.closeConnection(error,con);
		}
		
		return list;
	}
	
	protected PortaApplicativa getPortaApplicativa(long id) throws DriverConfigurazioneException,DriverConfigurazioneNotFound {
		return getPortaApplicativa(id,null);
	}
	protected PortaApplicativa getPortaApplicativa(long id,Connection conParam) throws DriverConfigurazioneException,DriverConfigurazioneNotFound {

		if (id <= 0)
			throw new DriverConfigurazioneException("[getPortaApplicativa] L'id della Porta Applicativa deve essere > 0.");

		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;
		PreparedStatement stm1 = null;
		ResultSet rs1 = null;
		PreparedStatement stm2 = null;
		ResultSet rs2 = null;
		String sqlQuery = null;

		if(conParam!=null){
			con = conParam;
		}
		else if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("getPortaApplicativa(longId)");

			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getPortaApplicativa] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.log.debug("operazione this.driver.atomica = " + this.driver.atomica);

		try {
			long idPortaApplicativa = id;
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE);
			sqlQueryObject.addFromTable(this.driver.tabellaSoggetti);
			sqlQueryObject.addSelectField("nome_porta");
			sqlQueryObject.addSelectField("tipo_soggetto");
			sqlQueryObject.addSelectField("nome_soggetto");
			sqlQueryObject.addSelectField("nome_soggetto_virtuale");
			sqlQueryObject.addSelectField("tipo_soggetto_virtuale");
			sqlQueryObject.addSelectField("servizio");
			sqlQueryObject.addSelectField("tipo_servizio");
			sqlQueryObject.addSelectField("versione_servizio");
			sqlQueryObject.addSelectField("ricevuta_asincrona_asim");
			sqlQueryObject.addSelectField("ricevuta_asincrona_sim");
			sqlQueryObject.addSelectField("integrazione");
			sqlQueryObject.addSelectField("scadenza_correlazione_appl");
			sqlQueryObject.addSelectField("allega_body");
			sqlQueryObject.addSelectField("scarta_body");
			sqlQueryObject.addSelectField("gestione_manifest");
			sqlQueryObject.addSelectField("azione");
			sqlQueryObject.addSelectField("mode_azione");
			sqlQueryObject.addSelectField("pattern_azione");
			sqlQueryObject.addSelectField("nome_porta_delegante_azione");
			sqlQueryObject.addSelectField("force_interface_based_azione");
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
			sqlQueryObject.addSelectAliasField(CostantiDB.PORTE_APPLICATIVE+".id", "idPA");
			sqlQueryObject.addSelectAliasField(this.driver.tabellaSoggetti+".id", "idSoggetto");
			sqlQueryObject.addSelectAliasField(CostantiDB.PORTE_APPLICATIVE+".descrizione", "descrizionePorta");
			sqlQueryObject.addSelectField("stateless");
			sqlQueryObject.addSelectField("behaviour");
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
			sqlQueryObject.addSelectField("id_sa_default");
			sqlQueryObject.addSelectField("canale");
			sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE+".id_soggetto = "+this.driver.tabellaSoggetti+".id");
			sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE+".id = ?");
			sqlQueryObject.setANDLogicOperator(true);
			sqlQuery = sqlQueryObject.createSQLQuery();

			stm = con.prepareStatement(sqlQuery);
			stm.setLong(1, idPortaApplicativa);

			rs = stm.executeQuery();

			if (rs.next()) {

				PortaApplicativa pa = new PortaApplicativa();
				pa.setId(rs.getLong("idPA"));
				long idSoggetto = rs.getLong("idSoggetto");
				pa.setIdSoggetto(idSoggetto);
				pa.setDescrizione(rs.getString("descrizionePorta"));
				pa.setNome(rs.getString("nome_porta"));
				pa.setTipoSoggettoProprietario(rs.getString("tipo_soggetto"));
				pa.setNomeSoggettoProprietario(rs.getString("nome_soggetto"));

				pa.setOptions(rs.getString("options"));
				
				pa.setIdAccordo(rs.getLong("id_accordo"));
				pa.setIdPortType(rs.getLong("id_port_type"));

				PortaApplicativaSoggettoVirtuale paSoggVirt = null;
				String nomeSoggVirt=rs.getString("nome_soggetto_virtuale");
				String tipoSoggVirt=rs.getString("tipo_soggetto_virtuale");
				/*
				long idSoggVirt = -1;
				try{
					idSoggVirt = DBUtils.getIdSoggetto(nomeSoggVirt, tipoSoggVirt, con, this.driver.tipoDB,this.driver.tabellaSoggetti);
				}catch (Exception e) {
					log.error(e);
				}
				 */
				if(nomeSoggVirt!=null && !nomeSoggVirt.equals("") && tipoSoggVirt!=null && !tipoSoggVirt.equals("")){
					long idSoggVirt = -1;
					try{
						idSoggVirt = DBUtils.getIdSoggetto(nomeSoggVirt, tipoSoggVirt, con, this.driver.tipoDB,this.driver.tabellaSoggetti);
					}catch (Exception e) {
						this.driver.log.error(e.getMessage(),e);
					}
					paSoggVirt=new PortaApplicativaSoggettoVirtuale();
					paSoggVirt.setId(idSoggVirt);
					paSoggVirt.setNome(nomeSoggVirt);
					paSoggVirt.setTipo(tipoSoggVirt);
				}
				pa.setSoggettoVirtuale(paSoggVirt);

				PortaApplicativaServizio paServizio = null;
				String nomeServizio = rs.getString("servizio");
				String tipoServizioPA = rs.getString("tipo_servizio");
				Integer versioneServizioPA = rs.getInt("versione_servizio");

				String nomeProprietarioServizio = null;
				String tipoProprietarioServizio = null;
				if(nomeSoggVirt!=null && !nomeSoggVirt.equals("") && tipoSoggVirt!=null && !tipoSoggVirt.equals("")){
					nomeProprietarioServizio=nomeSoggVirt;
					tipoProprietarioServizio=tipoSoggVirt;
				}else{
					nomeProprietarioServizio=pa.getNomeSoggettoProprietario();
					tipoProprietarioServizio=pa.getTipoSoggettoProprietario();
				}

				long idServizioPA=-1;
				try {
					idServizioPA = DBUtils.getIdServizio(nomeServizio, tipoServizioPA, versioneServizioPA, nomeProprietarioServizio, tipoProprietarioServizio, con, this.driver.tipoDB,this.driver.tabellaSoggetti);
				} catch (Exception e) {
					// NON Abilitare il log, poiche' la tabella servizi puo' non esistere per il driver di configurazione 
					// in un database che non ' quello della controlstation ma quello pdd.
					//this.driver.log.info(e);
				}
				if( (idServizioPA>0) || (nomeServizio!=null && !nomeServizio.equals("") && tipoServizioPA!=null && !tipoServizioPA.equals("")) ){
					paServizio = new PortaApplicativaServizio();
					paServizio.setNome(nomeServizio);
					paServizio.setTipo(tipoServizioPA);
					paServizio.setVersione(versioneServizioPA);
					paServizio.setId(idServizioPA);
				}
				pa.setServizio(paServizio);

				
				String azione = rs.getString("azione");
				String modeAzione = rs.getString("mode_azione");
				PortaApplicativaAzione paAzione=null;
				if ((azione != null && !"-".equals(azione) && !"".equals(azione)) || (modeAzione!=null && !"".equals(modeAzione)) ) {
					paAzione=new PortaApplicativaAzione();
					paAzione.setNome(azione);
					paAzione.setIdentificazione(PortaApplicativaAzioneIdentificazione.toEnumConstant(modeAzione));
					paAzione.setPattern(rs.getString("pattern_azione"));
					paAzione.setNomePortaDelegante(rs.getString("nome_porta_delegante_azione"));
					paAzione.setForceInterfaceBased(DriverConfigurazioneDB_LIB.getEnumStatoFunzionalita(rs.getString("force_interface_based_azione")));
					pa.setAzione(paAzione);
				}
				
				//ricevuta asincrona_(a)simmetrica
				pa.setRicevutaAsincronaAsimmetrica(DriverConfigurazioneDB_LIB.getEnumStatoFunzionalita(rs.getString("ricevuta_asincrona_asim")));
				pa.setRicevutaAsincronaSimmetrica(DriverConfigurazioneDB_LIB.getEnumStatoFunzionalita(rs.getString("ricevuta_asincrona_sim")));

				//integrazione
				pa.setIntegrazione(rs.getString("integrazione"));

				//scadenza correlazione applicativa
				String scadenzaCorrelazione = rs.getString("scadenza_correlazione_appl");
				CorrelazioneApplicativa corr= null;

				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_CORRELAZIONE);
				sqlQueryObject.addSelectField("*");
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQueryObject.addOrderBy("id");
				sqlQueryObject.setSortType(true);
				String queryCorrApp = sqlQueryObject.createSQLQuery();
				PreparedStatement stmCorrApp = con.prepareStatement(queryCorrApp);
				stmCorrApp.setLong(1, idPortaApplicativa);
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
					corr.addElemento(cae);
				}
				rsCorrApp.close();
				stmCorrApp.close();
				if(corr!=null && scadenzaCorrelazione!=null && !scadenzaCorrelazione.equals(""))
					corr.setScadenza(scadenzaCorrelazione);
				pa.setCorrelazioneApplicativa(corr);
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
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_CORRELAZIONE_RISPOSTA);
				sqlQueryObject.addSelectField("*");
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQueryObject.addOrderBy("id");
				sqlQueryObject.setSortType(true);
				queryCorrApp = sqlQueryObject.createSQLQuery();
				stmCorrApp = con.prepareStatement(queryCorrApp);
				stmCorrApp.setLong(1, idPortaApplicativa);
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
				pa.setCorrelazioneApplicativaRisposta(corrApplRisposta);



				// Gestione funzionalita' Attachments
				pa.setAllegaBody(DriverConfigurazioneDB_LIB.getEnumStatoFunzionalita(rs.getString("allega_body")));
				pa.setScartaBody(DriverConfigurazioneDB_LIB.getEnumStatoFunzionalita(rs.getString("scarta_body")));
				pa.setGestioneManifest(DriverConfigurazioneDB_LIB.getEnumStatoFunzionalita(rs.getString("gestione_manifest")));

				// Stateless
				pa.setStateless(DriverConfigurazioneDB_LIB.getEnumStatoFunzionalita(rs.getString("stateless")));
				
				// Behaviour
				String behaviour = rs.getString("behaviour");
				if(behaviour!=null && !"".equals(behaviour)) {
					pa.setBehaviour(new PortaApplicativaBehaviour());
					pa.getBehaviour().setNome(behaviour);
				}

				// Autorizzazione
				pa.setAutenticazione(rs.getString("autenticazione"));
				pa.setAutenticazioneOpzionale(DriverConfigurazioneDB_LIB.getEnumStatoFunzionalita(rs.getString("autenticazione_opzionale")));
				
				// GestioneToken
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

					pa.setGestioneToken(gestioneToken);
				}
				
				// Autorizzazione
				pa.setAutorizzazione(rs.getString("autorizzazione"));
				pa.setXacmlPolicy(rs.getString("autorizzazione_xacml"));
				pa.setAutorizzazioneContenuto(rs.getString("autorizzazione_contenuto"));

				
				// Ricerca Porta Azione Delegata
				if(rs.getString("ricerca_porta_azione_delegata")!=null){
					pa.setRicercaPortaAzioneDelegata(DriverConfigurazioneDB_LIB.getEnumStatoFunzionalita(rs.getString("ricerca_porta_azione_delegata")));
				}
				
				// Tracciamento
				String msg_diag_severita = rs.getString("msg_diag_severita");
				String tracciamento_esiti = rs.getString("tracciamento_esiti");
				if(msg_diag_severita!=null || tracciamento_esiti!=null) {
					PortaTracciamento tracciamento = new PortaTracciamento();
					tracciamento.setSeverita(DriverConfigurazioneDB_LIB.getEnumSeverita(msg_diag_severita));
					tracciamento.setEsiti(tracciamento_esiti);
					pa.setTracciamento(tracciamento);
				}
				
				// Stato
				if(rs.getString("stato")!=null){
					pa.setStato(DriverConfigurazioneDB_LIB.getEnumStatoFunzionalita(rs.getString("stato")));
				}


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
					pa.setValidazioneContenutiApplicativi(val);
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
					if(pa.getRuoli()==null){
						pa.setRuoli(new AutorizzazioneRuoli());
					}
					pa.getRuoli().setMatch(RuoloTipoMatch.toEnumConstant(ruoliMatch));
				}
				
				// Token SA Stato
				String tokenSaStato = rs.getString("token_sa_stato");
				if( (tokenSaStato!=null && !"".equals(tokenSaStato)) ){
					if(pa.getAutorizzazioneToken()==null){
						pa.setAutorizzazioneToken(new PortaApplicativaAutorizzazioneToken());
					}
					pa.getAutorizzazioneToken().setAutorizzazioneApplicativi((DriverConfigurazioneDB_LIB.getEnumStatoFunzionalita(tokenSaStato)));
				}
				
				// Token Ruoli
				String tokenRuoliStato = rs.getString("token_ruoli_stato");
				String tokenRuoliMatch = rs.getString("token_ruoli_match");
				String tokenRuoliTipologia = rs.getString("token_ruoli_tipologia");
				if( (tokenRuoliStato!=null && !"".equals(tokenRuoliStato)) ||
						(tokenRuoliMatch!=null && !"".equals(tokenRuoliMatch)) ||
						(tokenRuoliTipologia!=null && !"".equals(tokenRuoliTipologia)) ){
					if(pa.getAutorizzazioneToken()==null){
						pa.setAutorizzazioneToken(new PortaApplicativaAutorizzazioneToken());
					}
					pa.getAutorizzazioneToken().setAutorizzazioneRuoli(DriverConfigurazioneDB_LIB.getEnumStatoFunzionalita(tokenRuoliStato));
					if((tokenRuoliMatch!=null && !"".equals(tokenRuoliMatch))) {
						if(pa.getAutorizzazioneToken().getRuoli()==null){
							pa.getAutorizzazioneToken().setRuoli(new AutorizzazioneRuoli());
						}
						pa.getAutorizzazioneToken().getRuoli().setMatch(RuoloTipoMatch.toEnumConstant(tokenRuoliMatch));
					}
					if((tokenRuoliTipologia!=null && !"".equals(tokenRuoliTipologia))) {
						pa.getAutorizzazioneToken().setTipologiaRuoli(RuoloTipologia.toEnumConstant(tokenRuoliTipologia));
					}
				}
				
				// ScopeMatch
				String scopeStato = rs.getString("scope_stato");
				String scopeMatch = rs.getString("scope_match");
				if( (scopeStato!=null && !"".equals(scopeStato)) || (scopeMatch!=null && !"".equals(scopeMatch)) ){
					if(pa.getScope()==null){
						pa.setScope(new AutorizzazioneScope());
					}
					pa.getScope().setStato(DriverConfigurazioneDB_LIB.getEnumStatoFunzionalita(scopeStato));
					pa.getScope().setMatch(ScopeTipoMatch.toEnumConstant(scopeMatch));
				}
				
				
				// Gestione CORS
				String corsStato = rs.getString("cors_stato");
				if(corsStato!=null && !"".equals(corsStato)) {
					pa.setGestioneCors(new CorsConfigurazione());
					this.porteDriver.readConfigurazioneCors(pa.getGestioneCors(), rs);
				}
				
				// Gestione CacheResponse
				String responseCacheStato = rs.getString("response_cache_stato");
				if(responseCacheStato!=null && !"".equals(responseCacheStato)) {
					pa.setResponseCaching(new ResponseCachingConfigurazione());
					this.porteDriver.readResponseCaching(idPortaApplicativa, false, false, pa.getResponseCaching(), rs, con);
				}
				
				// Servizio Applicativo di default
				long id_sa_default = rs.getLong("id_sa_default");
				
				// Canali
				String canale = rs.getString("canale");
				pa.setCanale(canale);
				
				rs.close();
				stm.close();

				
				// Servizio Applicativo di default
				if(id_sa_default>0) {
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
					sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
					sqlQueryObject.addSelectField("nome");
					sqlQueryObject.addWhereCondition("id=?");
					sqlQuery = sqlQueryObject.createSQLQuery();
					stm = con.prepareStatement(sqlQuery);
					stm.setLong(1, id_sa_default);

					this.driver.log.debug("eseguo query : " + DBUtils.formatSQLString(sqlQuery, idPortaApplicativa));
					rs = stm.executeQuery();

					// Request Flow Parameter
					if (rs.next()) {
						String nome = rs.getString("nome");
						pa.setServizioApplicativoDefault(nome);
					}
					rs.close();
					stm.close();
				}
				
				
				// Trasformazioni
				Trasformazioni trasformazioni = DriverConfigurazioneDB_trasformazioniLIB.readTrasformazioni(idPortaApplicativa, false, con);
				if(trasformazioni!=null) {
					pa.setTrasformazioni(trasformazioni);
				}
				
				
				if(paAzione!=null) {
					// lista azioni
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
					sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_AZIONI);
					sqlQueryObject.addSelectField("*");
					sqlQueryObject.addWhereCondition("id_porta=?");
					sqlQuery = sqlQueryObject.createSQLQuery();
					stm = con.prepareStatement(sqlQuery);
					stm.setLong(1, idPortaApplicativa);

					this.driver.log.debug("eseguo query : " + DBUtils.formatSQLString(sqlQuery, idPortaApplicativa));
					rs = stm.executeQuery();

					// Request Flow Parameter
					while (rs.next()) {
						paAzione.addAzioneDelegata(rs.getString("azione"));
					}
					rs.close();
					stm.close();
				}
				
				
				
				// stato security
				if (CostantiConfigurazione.ABILITATO.toString().equalsIgnoreCase(security)) {
					pa.setStatoMessageSecurity(CostantiConfigurazione.ABILITATO.toString());
				}else{
					pa.setStatoMessageSecurity(CostantiConfigurazione.DISABILITATO.toString());
				}
				
				// lista wss
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_MESSAGE_SECURITY_REQUEST);
				sqlQueryObject.addSelectField("*");
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLQuery();
				stm1 = con.prepareStatement(sqlQuery);
				stm1.setLong(1, idPortaApplicativa);

				this.driver.log.debug("eseguo query : " + DBUtils.formatSQLString(sqlQuery, idPortaApplicativa));
				rs1 = stm1.executeQuery();

				// Request Flow Parameter
				MessageSecurityFlowParameter secRfp;
				while (rs1.next()) {
					secRfp = new MessageSecurityFlowParameter();
					secRfp.setNome(rs1.getString("nome"));
					secRfp.setValore(rs1.getString("valore"));
					if(messageSecurity==null){
						messageSecurity = new MessageSecurity();
					}
					if(messageSecurity.getRequestFlow()==null){
						messageSecurity.setRequestFlow(new MessageSecurityFlow());
					}
					messageSecurity.getRequestFlow().addParameter(secRfp);
				}
				rs1.close();
				stm1.close();

				// Response Flow Parameter
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_MESSAGE_SECURITY_RESPONSE);
				sqlQueryObject.addSelectField("*");
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLQuery();
				stm1 = con.prepareStatement(sqlQuery);
				stm1.setLong(1, idPortaApplicativa);

				this.driver.log.debug("eseguo query : " + DBUtils.formatSQLString(sqlQuery, idPortaApplicativa));
				rs1 = stm1.executeQuery();

				MessageSecurityFlowParameter secResfp;
				while (rs1.next()) {
					secResfp = new MessageSecurityFlowParameter();
					secResfp.setNome(rs1.getString("nome"));
					secResfp.setValore(rs1.getString("valore"));
					if(messageSecurity==null){
						messageSecurity = new MessageSecurity();
					}
					if(messageSecurity.getResponseFlow()==null){
						messageSecurity.setResponseFlow(new MessageSecurityFlow());
					}
					messageSecurity.getResponseFlow().addParameter(secResfp);
				}
				rs1.close();
				stm1.close();


				// setto il messageSecurity
				pa.setMessageSecurity(messageSecurity);
				
				
				// mtom
				
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_MTOM_REQUEST);
				sqlQueryObject.addSelectField("*");
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLQuery();
				stm1 = con.prepareStatement(sqlQuery);
				stm1.setLong(1, idPortaApplicativa);

				this.driver.log.debug("eseguo query : " + DBUtils.formatSQLString(sqlQuery, idPortaApplicativa));
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
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_MTOM_RESPONSE);
				sqlQueryObject.addSelectField("*");
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLQuery();
				stm1 = con.prepareStatement(sqlQuery);
				stm1.setLong(1, idPortaApplicativa);

				this.driver.log.debug("eseguo query : " + DBUtils.formatSQLString(sqlQuery, idPortaApplicativa));
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
				pa.setMtomProcessor(mtomProcessor);
				

				// pa.addServizioApplicativo(servizioApplicativo); .... solo i
				// NOMI!!!
				// servizi applicativi
				long idSA = 0;
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_SA);
				sqlQueryObject.addSelectField("*");
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLQuery();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaApplicativa);
				rs = stm.executeQuery();

				// per ogni entry con id_porta == idPortaApplicativa
				// prendo l'id del servizio associato, recupero il nome e
				// aggiungo
				// il servizio applicativo alla PortaDelegata da ritornare
				while (rs.next()) {
					long idSA_PA = rs.getLong("id");
					idSA = rs.getLong("id_servizio_applicativo");

					String nomeConnettore = rs.getString("connettore_nome");
					int notificaConnettore = rs.getInt("connettore_notifica");
					String descrizioneConnettore = rs.getString("connettore_descrizione");
					String statoConnettore = rs.getString("connettore_stato");
					String schedulingConnettore = rs.getString("connettore_scheduling");
					String filtriConnettore = rs.getString("connettore_filtri");
					String codaConnettore = rs.getString("connettore_coda");
					String prioritaConnettore = rs.getString("connettore_priorita");
					int maxPrioritaConnettore = rs.getInt("connettore_max_priorita");
					
					if (idSA != 0) {
						sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
						sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
						sqlQueryObject.addSelectField("nome");
						sqlQueryObject.addWhereCondition("id=?");
						sqlQuery = sqlQueryObject.createSQLQuery();
						stm1 = con.prepareStatement(sqlQuery);
						stm1.setLong(1, idSA);

						this.driver.log.debug("eseguo query : " + DBUtils.formatSQLString(sqlQuery, idSA));

						rs1 = stm1.executeQuery();

						PortaApplicativaServizioApplicativo servizioApplicativo = null;
						if (rs1.next()) {
							// setto solo il nome come da specifica
							servizioApplicativo = new PortaApplicativaServizioApplicativo();
							servizioApplicativo.setId(idSA_PA);
							servizioApplicativo.setIdServizioApplicativo(idSA); 
							servizioApplicativo.setNome(rs1.getString("nome"));
							
							if(nomeConnettore!=null && !"".equals(nomeConnettore)) {
								servizioApplicativo.setDatiConnettore(new PortaApplicativaServizioApplicativoConnettore());
								servizioApplicativo.getDatiConnettore().setNome(nomeConnettore);
								servizioApplicativo.getDatiConnettore().setNotifica(notificaConnettore == CostantiDB.TRUE);
								servizioApplicativo.getDatiConnettore().setDescrizione(descrizioneConnettore);
								if(statoConnettore!=null) {
									// prende il default
									servizioApplicativo.getDatiConnettore().setStato(DriverConfigurazioneDB_LIB.getEnumStatoFunzionalita(statoConnettore));
								}
								if(schedulingConnettore!=null) {
									// prende il default
									servizioApplicativo.getDatiConnettore().setScheduling(DriverConfigurazioneDB_LIB.getEnumStatoFunzionalita(schedulingConnettore));
								}
								servizioApplicativo.getDatiConnettore().setCoda(codaConnettore);
								servizioApplicativo.getDatiConnettore().setPriorita(prioritaConnettore);
								servizioApplicativo.getDatiConnettore().setPrioritaMax(maxPrioritaConnettore == CostantiDB.TRUE);
								
								List<String> l = DBUtils.convertToList(filtriConnettore);
								if(!l.isEmpty()) {
									servizioApplicativo.getDatiConnettore().setFiltroList(l);
								}
								
								Proprieta prop = null;
								sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
								sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_SA_PROPS);
								sqlQueryObject.addSelectField("*");
								sqlQueryObject.addWhereCondition("id_porta=?");
								sqlQuery = sqlQueryObject.createSQLQuery();
								stm2 = con.prepareStatement(sqlQuery);
								stm2.setLong(1, idSA_PA);
								rs2=stm2.executeQuery();
								while (rs2.next()) {
									prop = new Proprieta();
									prop.setId(rs2.getLong("id"));
									prop.setNome(rs2.getString("nome"));
									prop.setValore(rs2.getString("valore"));
									servizioApplicativo.getDatiConnettore().addProprieta(prop);
								}
								rs2.close();
								stm2.close();
							}
							
							pa.addServizioApplicativo(servizioApplicativo);
						}
						rs1.close();
						stm1.close();
						
						
					}
				}
				rs.close();
				stm.close();
				
				
				if(pa.getBehaviour()!=null) {
					// behaviour prop
					Proprieta prop = null;
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
					sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_BEHAVIOUR_PROPS);
					sqlQueryObject.addSelectField("*");
					sqlQueryObject.addWhereCondition("id_porta=?");
					sqlQuery = sqlQueryObject.createSQLQuery();
					stm = con.prepareStatement(sqlQuery);
					stm.setLong(1, idPortaApplicativa);
					rs=stm.executeQuery();
					while (rs.next()) {
						prop = new Proprieta();
						prop.setId(rs.getLong("id"));
						prop.setNome(rs.getString("nome"));
						prop.setValore(rs.getString("valore"));
						pa.getBehaviour().addProprieta(prop);
					}
					rs.close();
					stm.close();
				}
				
				
				// autenticazione prop
				Proprieta prop = null;
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_AUTENTICAZIONE_PROP);
				sqlQueryObject.addSelectField("*");
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLQuery();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaApplicativa);
				rs=stm.executeQuery();
				while (rs.next()) {
					prop = new Proprieta();
					prop.setId(rs.getLong("id"));
					prop.setNome(rs.getString("nome"));
					prop.setValore(rs.getString("valore"));
					pa.addProprietaAutenticazione(prop);
				}
				rs.close();
				stm.close();
				
				
				
				// autorizzazione prop
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_AUTORIZZAZIONE_PROP);
				sqlQueryObject.addSelectField("*");
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLQuery();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaApplicativa);
				rs=stm.executeQuery();
				while (rs.next()) {
					prop = new Proprieta();
					prop.setId(rs.getLong("id"));
					prop.setNome(rs.getString("nome"));
					prop.setValore(rs.getString("valore"));
					pa.addProprietaAutorizzazione(prop);
				}
				rs.close();
				stm.close();
				
				
				
				
				// autorizzazione per contenuti prop
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_AUTORIZZAZIONE_CONTENUTI_PROP);
				sqlQueryObject.addSelectField("*");
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQueryObject.addOrderBy("id", true); // per preservare l'ordine di inserimento per l'autorizzazione per contenuti built-in visualizzata
				sqlQuery = sqlQueryObject.createSQLQuery();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaApplicativa);
				rs=stm.executeQuery();
				while (rs.next()) {
					prop = new Proprieta();
					prop.setId(rs.getLong("id"));
					prop.setNome(rs.getString("nome"));
					prop.setValore(rs.getString("valore"));
					pa.addProprietaAutorizzazioneContenuto(prop);
				}
				rs.close();
				stm.close();
				
				
				
				
				
				// rate limiting prop
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_RATE_LIMITING_PROP);
				sqlQueryObject.addSelectField("*");
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLQuery();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaApplicativa);
				rs=stm.executeQuery();
				while (rs.next()) {
					prop = new Proprieta();
					prop.setId(rs.getLong("id"));
					prop.setNome(rs.getString("nome"));
					prop.setValore(rs.getString("valore"));
					pa.addProprietaRateLimiting(prop);
				}
				rs.close();
				stm.close();
				
				
				
				
				
				// pa.addSetProperty(setProperty); .....
				prop = null;
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_PROP);
				sqlQueryObject.addSelectField("*");
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLQuery();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaApplicativa);
				rs=stm.executeQuery();
				while (rs.next()) {
					prop = new Proprieta();
					prop.setId(rs.getLong("id"));
					prop.setNome(rs.getString("nome"));
					prop.setValore(rs.getString("valore"));
					pa.addProprieta(prop);
				}
				rs.close();
				stm.close();
				
				
								
				// ruoli
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_RUOLI);
				sqlQueryObject.addSelectField("*");
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLQuery();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaApplicativa);
				rs = stm.executeQuery();

				while (rs.next()) {
					
					if(pa.getRuoli()==null){
						pa.setRuoli(new AutorizzazioneRuoli());
					}
					
					Ruolo ruolo = new Ruolo();
					ruolo.setNome(rs.getString("ruolo"));
					pa.getRuoli().addRuolo(ruolo);
				
				}
				rs.close();
				stm.close();
				
				
				
				
				// scope
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_SCOPE);
				sqlQueryObject.addSelectField("*");
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLQuery();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaApplicativa);
				rs = stm.executeQuery();

				while (rs.next()) {
					
					if(pa.getScope()==null){
						pa.setScope(new AutorizzazioneScope());
					}
					
					Scope scope = new Scope();
					scope.setNome(rs.getString("scope"));
					pa.getScope().addScope(scope);
				
				}
				rs.close();
				stm.close();
				
				
				
				// soggetti
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_SOGGETTI);
				sqlQueryObject.addSelectField("*");
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLQuery();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaApplicativa);
				rs = stm.executeQuery();

				while (rs.next()) {
					
					if(pa.getSoggetti()==null){
						pa.setSoggetti(new PortaApplicativaAutorizzazioneSoggetti());
					}
					
					PortaApplicativaAutorizzazioneSoggetto soggetto = new PortaApplicativaAutorizzazioneSoggetto();
					soggetto.setTipo(rs.getString("tipo_soggetto"));
					soggetto.setNome(rs.getString("nome_soggetto"));
					pa.getSoggetti().addSoggetto(soggetto);
				
				}
				rs.close();
				stm.close();
				
				
				
				// applicativi autorizzti
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_SA_AUTORIZZATI);
				sqlQueryObject.addSelectField("*");
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLQuery();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaApplicativa);
				rs = stm.executeQuery();

				// per ogni entry con id_porta == idPortaApplicativa
				// prendo l'id del servizio associato, recupero il nome e
				// aggiungo
				// il servizio applicativo alla PortaDelegata da ritornare
				while (rs.next()) {
					long idSA_autorizzato = rs.getLong("id_servizio_applicativo");

					if (idSA_autorizzato != 0) {
						sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
						sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
						sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
						sqlQueryObject.addSelectField("nome");
						sqlQueryObject.addSelectField("tipo_soggetto");
						sqlQueryObject.addSelectField("nome_soggetto");
						sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".id=?");
						sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".id_soggetto="+CostantiDB.SOGGETTI+".id");
						sqlQueryObject.setANDLogicOperator(true);
						sqlQuery = sqlQueryObject.createSQLQuery();
						stm1 = con.prepareStatement(sqlQuery);
						stm1.setLong(1, idSA_autorizzato);

						this.driver.log.debug("eseguo query : " + DBUtils.formatSQLString(sqlQuery, idSA_autorizzato));

						rs1 = stm1.executeQuery();

						PortaApplicativaAutorizzazioneServizioApplicativo servizioApplicativo = null;
						if (rs1.next()) {
							// setto solo il nome come da specifica
							servizioApplicativo = new PortaApplicativaAutorizzazioneServizioApplicativo();
							servizioApplicativo.setId(idSA_autorizzato);
							servizioApplicativo.setNome(rs1.getString("nome"));
							servizioApplicativo.setTipoSoggettoProprietario(rs1.getString("tipo_soggetto"));
							servizioApplicativo.setNomeSoggettoProprietario(rs1.getString("nome_soggetto"));
							if(pa.getServiziApplicativiAutorizzati()==null) {
								pa.setServiziApplicativiAutorizzati(new PortaApplicativaAutorizzazioneServiziApplicativi());
							}
							pa.getServiziApplicativiAutorizzati().addServizioApplicativo(servizioApplicativo);
						}
						rs1.close();
						stm1.close();
					}
				}
				rs.close();
				stm.close();
				
				
				
				
				// applicativi autorizzti via token
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_TOKEN_SA);
				sqlQueryObject.addSelectField("*");
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLQuery();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaApplicativa);
				rs = stm.executeQuery();

				// per ogni entry con id_porta == idPortaApplicativa
				// prendo l'id del servizio associato, recupero il nome e
				// aggiungo
				// il servizio applicativo alla PortaDelegata da ritornare
				while (rs.next()) {
					long idSA_autorizzato = rs.getLong("id_servizio_applicativo");

					if (idSA_autorizzato != 0) {
						sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
						sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
						sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
						sqlQueryObject.addSelectField("nome");
						sqlQueryObject.addSelectField("tipo_soggetto");
						sqlQueryObject.addSelectField("nome_soggetto");
						sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".id=?");
						sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".id_soggetto="+CostantiDB.SOGGETTI+".id");
						sqlQueryObject.setANDLogicOperator(true);
						sqlQuery = sqlQueryObject.createSQLQuery();
						stm1 = con.prepareStatement(sqlQuery);
						stm1.setLong(1, idSA_autorizzato);

						this.driver.log.debug("eseguo query : " + DBUtils.formatSQLString(sqlQuery, idSA_autorizzato));

						rs1 = stm1.executeQuery();

						PortaApplicativaAutorizzazioneServizioApplicativo servizioApplicativo = null;
						if (rs1.next()) {
							// setto solo il nome come da specifica
							servizioApplicativo = new PortaApplicativaAutorizzazioneServizioApplicativo();
							servizioApplicativo.setId(idSA_autorizzato);
							servizioApplicativo.setNome(rs1.getString("nome"));
							servizioApplicativo.setTipoSoggettoProprietario(rs1.getString("tipo_soggetto"));
							servizioApplicativo.setNomeSoggettoProprietario(rs1.getString("nome_soggetto"));
							if(pa.getAutorizzazioneToken()==null) {
								pa.setAutorizzazioneToken(new PortaApplicativaAutorizzazioneToken());
							}
							if(pa.getAutorizzazioneToken().getServiziApplicativi()==null) {
								pa.getAutorizzazioneToken().setServiziApplicativi(new PortaApplicativaAutorizzazioneServiziApplicativi());
							}
							pa.getAutorizzazioneToken().getServiziApplicativi().addServizioApplicativo(servizioApplicativo);
						}
						rs1.close();
						stm1.close();
					}
				}
				rs.close();
				stm.close();
				
				
				
				// ruoli (token)
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_TOKEN_RUOLI);
				sqlQueryObject.addSelectField("*");
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLQuery();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaApplicativa);
				rs = stm.executeQuery();

				while (rs.next()) {
					
					if(pa.getAutorizzazioneToken()==null) {
						pa.setAutorizzazioneToken(new PortaApplicativaAutorizzazioneToken());
					}
					if(pa.getAutorizzazioneToken().getRuoli()==null){
						pa.getAutorizzazioneToken().setRuoli(new AutorizzazioneRuoli());
					}
					
					Ruolo ruolo = new Ruolo();
					ruolo.setNome(rs.getString("ruolo"));
					pa.getAutorizzazioneToken().getRuoli().addRuolo(ruolo);
				
				}
				rs.close();
				stm.close();
				
				
				
				
				// dump_config
				DumpConfigurazione dumpConfig = DriverConfigurazioneDB_dumpLIB.readDumpConfigurazione(con, pa.getId(), CostantiDB.DUMP_CONFIGURAZIONE_PROPRIETARIO_PA);
				pa.setDump(dumpConfig);
				
				
				// Handlers
				ConfigurazioneMessageHandlers requestHandlers = DriverConfigurazioneDB_handlerLIB.readConfigurazioneMessageHandlers(con, null, pa.getId(), true);
				ConfigurazioneMessageHandlers responseHandlers = DriverConfigurazioneDB_handlerLIB.readConfigurazioneMessageHandlers(con, null, pa.getId(), false);
				if(requestHandlers!=null || responseHandlers!=null) {
					pa.setConfigurazioneHandler(new ConfigurazionePortaHandler());
					pa.getConfigurazioneHandler().setRequest(requestHandlers);
					pa.getConfigurazioneHandler().setResponse(responseHandlers);
				}
				
				
				
				// attributeAuthority
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_ATTRIBUTE_AUTHORITY);
				sqlQueryObject.addSelectField("*");
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLQuery();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaApplicativa);
				rs = stm.executeQuery();

				while (rs.next()) {
					
					String nome = rs.getString("nome");
					String attributi = rs.getString("attributi");
					AttributeAuthority aa = new AttributeAuthority();
					aa.setNome(nome);
					aa.setAttributoList(DBUtils.convertToList(attributi));
					pa.addAttributeAuthority(aa);
				
				}
				rs.close();
				stm.close();
				
				
				
				
				// *** Aggiungo extInfo ***
				
				this.driver.log.debug("ExtendedInfo ... ");
				ExtendedInfoManager extInfoManager = ExtendedInfoManager.getInstance();
				IExtendedInfo extInfoConfigurazioneDriver = extInfoManager.newInstanceExtendedInfoPortaApplicativa();
				if(extInfoConfigurazioneDriver!=null){
					List<Object> listExtInfo = extInfoConfigurazioneDriver.getAllExtendedInfo(con, this.driver.log, pa);
					if(listExtInfo!=null && listExtInfo.size()>0){
						for (Object object : listExtInfo) {
							pa.addExtendedInfo(object);
						}
					}
				}

				
				return pa;
			} else {
				throw new DriverConfigurazioneNotFound("Porta Applicativa non esistente");
			}
		} catch (SQLException se) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getPortaApplicativa] SqlException: " + se.getMessage(),se);
		} catch (DriverConfigurazioneNotFound e) {
			throw new DriverConfigurazioneNotFound(e);
		}catch (Exception se) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getPortaApplicativa] Exception: " + se.getMessage(),se);
		}finally {
			//Chiudo statement and resultset
			JDBCUtilities.closeResources(rs, stm);
			JDBCUtilities.closeResources(rs1, stm1);
			this.driver.closeConnection(conParam, con);
		}
	}
	
	protected List<String> nomiProprietaPA(String filterSoggettoTipo, String filterSoggettoNome, List<String> tipoServiziProtocollo) throws DriverConfigurazioneException {
		String queryString;

		Connection con = null;
		boolean error = false;
		PreparedStatement stmt=null;
		ResultSet risultato=null;
		ArrayList<String> lista = new ArrayList<>();

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("nomiProprietaPA");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::nomiProprietaPA] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.log.debug("operazione this.driver.atomica = " + this.driver.atomica);

		try {

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_PROP);
			sqlQueryObject.setSelectDistinct(true);
			sqlQueryObject.addSelectField(CostantiDB.PORTE_APPLICATIVE_PROP + ".nome");
			sqlQueryObject.addOrderBy(CostantiDB.PORTE_APPLICATIVE_PROP + ".nome"); 
			sqlQueryObject.setSortType(true);
			sqlQueryObject.setANDLogicOperator(true);
			
			if((filterSoggettoNome!=null && !"".equals(filterSoggettoNome)) || (tipoServiziProtocollo != null && tipoServiziProtocollo.size() > 0)) {
				sqlQueryObject.addFromTable(CostantiDB.MAPPING_EROGAZIONE_PA);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI);
				
				sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE_PROP+".id_porta="+CostantiDB.PORTE_APPLICATIVE+".id");
				sqlQueryObject.addWhereCondition(CostantiDB.MAPPING_EROGAZIONE_PA+".id_porta="+CostantiDB.PORTE_APPLICATIVE+".id");
				sqlQueryObject.addWhereCondition(CostantiDB.MAPPING_EROGAZIONE_PA+".id_erogazione="+CostantiDB.SERVIZI+".id");
				
			}
			
			if((filterSoggettoNome!=null && !"".equals(filterSoggettoNome))) {
				sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
				sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI+".id_soggetto = "+CostantiDB.SOGGETTI+".id");
				
				sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".tipo_soggetto=?");
				sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".nome_soggetto=?");
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
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::nomiProprietaPA] Errore : " + qe.getMessage(),qe);
		} finally {
			//Chiudo statement and resultset
			JDBCUtilities.closeResources(risultato, stmt);
			this.driver.closeConnection(error,con);
		}
	}
	
	protected List<String> porteApplicativeRateLimitingValoriUnivoci(String pName) throws DriverConfigurazioneException {
		
		Connection con = null;
		boolean error = false;
		PreparedStatement stmt=null;
		ResultSet risultato=null;
		ArrayList<String> lista = new ArrayList<>();

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("porteApplicativeRateLimitingValoriUnivoci");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::porteApplicativeRateLimitingValoriUnivoci] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.log.debug("operazione this.driver.atomica = " + this.driver.atomica);

		try {

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_RATE_LIMITING_PROP);
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
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::porteApplicativeRateLimitingValoriUnivoci] Errore : " + qe.getMessage(),qe);
		} finally {
			//Chiudo statement and resultset
			JDBCUtilities.closeResources(risultato, stmt);
			this.driver.closeConnection(error,con);
		}
	}
	
	
	protected List<PortaApplicativa> porteAppWithServizio(long idSoggettoErogatore, String tipoServizio,String nomeServizio, Integer versioneServizio) throws DriverConfigurazioneException {
		String nomeMetodo = "porteAppWithTipoNomeServizio";
		String queryString;

		Connection con = null;
		boolean error = false;
		PreparedStatement stmt=null;
		ResultSet risultato=null;
		ArrayList<PortaApplicativa> lista = new ArrayList<PortaApplicativa>();

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("porteAppWithTipoNomeServizio");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.log.debug("operazione this.driver.atomica = " + this.driver.atomica);

		try {

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("tipo_servizio = ?");
			sqlQueryObject.addWhereCondition("servizio = ?");
			sqlQueryObject.addWhereCondition("versione_servizio = ?");
			sqlQueryObject.addWhereCondition("id_soggetto = ?");
			sqlQueryObject.setANDLogicOperator(true);
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			int index = 1;
			stmt.setString(index++, tipoServizio);
			stmt.setString(index++, nomeServizio);
			stmt.setInt(index++, versioneServizio);
			stmt.setLong(index++, idSoggettoErogatore);
			risultato = stmt.executeQuery();

			PortaApplicativa pa;
			while (risultato.next()) {
				pa = getPortaApplicativa(risultato.getLong("id"),con);
				lista.add(pa);
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

	protected List<PortaApplicativa> porteAppWithIdServizio(long idServizio) throws DriverConfigurazioneException {
		String nomeMetodo = "porteAppWithIdServizio";
		String queryString;

		Connection con = null;
		boolean error = false;
		PreparedStatement stmt=null;
		ResultSet risultato=null;
		ArrayList<PortaApplicativa> lista = new ArrayList<PortaApplicativa>();

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("porteAppWithIdServizio");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.log.debug("operazione this.driver.atomica = " + this.driver.atomica);

		try {

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("id_servizio = ?");
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idServizio);
			risultato = stmt.executeQuery();

			PortaApplicativa pa;
			while (risultato.next()) {
				pa = getPortaApplicativa(risultato.getLong("id"),con);
				lista.add(pa);
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
	
	protected boolean existsPortaApplicativa(IDPortaApplicativa idPA) throws DriverConfigurazioneException {

		try{
			return getPortaApplicativa(idPA)!=null;
		}catch (DriverConfigurazioneNotFound e) {
			return false;
		}
	}

	

	protected List<PortaApplicativa> getPorteApplicativeWithServizio(Long idServizio, String tiposervizio, String nomeservizio, Integer versioneServizio,
			Long idSoggetto, String tiposoggetto, String nomesoggetto) throws DriverConfigurazioneException {

		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;
		String sqlQuery = "";
		ArrayList<PortaApplicativa> lista = new ArrayList<PortaApplicativa>();

		try {
			
			if (this.driver.atomica) {
				try {
					con = this.driver.getConnectionFromDatasource("getPorteApplicativeWithServizio");
				} catch (Exception e) {
					throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getPorteApplicativeWithServizio] Exception accedendo al datasource :" + e.getMessage(),e);

				}

			} else
				con = this.driver.globalConnection;
			
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition(false, "id_servizio = ?", "tipo_servizio = ? AND servizio = ? AND versione_servizio = ?");
			sqlQueryObject.addWhereCondition(false, "id_soggetto = ?", "id_soggetto_virtuale = ?", "tipo_soggetto_virtuale = ? AND nome_soggetto_virtuale = ?");
			sqlQueryObject.setANDLogicOperator(true);
			sqlQuery = sqlQueryObject.createSQLQuery();
			stm = con.prepareStatement(sqlQuery);

			int index = 1;
			stm.setLong(index++, idServizio);
			stm.setString(index++, tiposervizio);
			stm.setString(index++, nomeservizio);
			stm.setInt(index++, versioneServizio);
			stm.setLong(index++, idSoggetto);
			stm.setLong(index++, idSoggetto);
			stm.setString(index++, tiposoggetto);
			stm.setString(index++, nomesoggetto);

			this.driver.log.debug("eseguo query : " + DBUtils.formatSQLString(sqlQuery, idServizio, tiposervizio, nomeservizio, versioneServizio, idSoggetto, idSoggetto, tiposoggetto, nomesoggetto));
			rs = stm.executeQuery();

			while (rs.next()) {
				PortaApplicativa pa = new PortaApplicativa();
				pa.setNome(rs.getString("nome_porta"));
				lista.add(pa);
			}

			return lista;
		} catch (Exception qe) {
			throw new DriverConfigurazioneException(qe);
		} finally {

			JDBCUtilities.closeResources(rs, stm);
			
			this.driver.closeConnection(con);

		}

	}

	protected PortaApplicativa getPortaApplicativaWithSoggettoAndServizio(String nome, Long idSoggetto, Long idServizio, 
			String tipoServizio, String nomeServizio, Integer versioneServizio) throws DriverConfigurazioneException {

		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;
		String sqlQuery = "";
		PortaApplicativa pa = new PortaApplicativa();

		try {
			
			if (this.driver.atomica) {
				try {
					con = this.driver.getConnectionFromDatasource("getPortaApplicativaWithSoggettoAndServizio");
				} catch (Exception e) {
					throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getPorteApplicativeWithSoggettoAndServizio] Exception accedendo al datasource :" + e.getMessage(),e);

				}

			} else
				con = this.driver.globalConnection;
			
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("nome_porta = ?");
			sqlQueryObject.addWhereCondition(false, "id_soggetto = ? AND tipo_servizio = ? AND servizio=? AND versione_servizio=? AND id_soggetto_virtuale <= ?",
					"id_soggetto = ? AND id_servizio = ? AND id_soggetto_virtuale <= ?", 
					"id_soggetto_virtuale = ? AND tipo_servizio = ? AND servizio=? AND versione_servizio=? ",
					"id_soggetto_virtuale = ? AND id_servizio = ?");
			sqlQueryObject.setANDLogicOperator(true);
			sqlQuery = sqlQueryObject.createSQLQuery();
			stm = con.prepareStatement(sqlQuery);

			int index = 1;
			
			stm.setString(index++, nome);

			stm.setLong(index++, idSoggetto);
			stm.setString(index++, tipoServizio);
			stm.setString(index++, nomeServizio);
			stm.setInt(index++, versioneServizio);
			stm.setLong(index++, 0);

			stm.setLong(index++, idSoggetto);
			stm.setLong(index++, idServizio);
			stm.setLong(index++, 0);

			stm.setLong(index++, idSoggetto);
			stm.setString(index++, tipoServizio);
			stm.setString(index++, nomeServizio);
			stm.setInt(index++, versioneServizio);

			stm.setLong(index++, idSoggetto);
			stm.setLong(index++, idServizio);

			this.driver.log.debug("eseguo query : " + DBUtils.formatSQLString(sqlQuery, idSoggetto, idServizio, -1, idSoggetto, idServizio));
			rs = stm.executeQuery();

			if (rs.next()) {
				Soggetto sogg = this.soggettiDriver.getSoggetto(rs.getLong("id_soggetto"));
				pa.setId(rs.getLong("id"));
				pa.setNome(rs.getString("nome_porta"));
				pa.setIdSoggetto(rs.getLong("id_soggetto"));
				pa.setNomeSoggettoProprietario(sogg.getNome());
				pa.setTipoSoggettoProprietario(sogg.getTipo());
			}else{
				throw new Exception("PA non trovata ["+DBUtils.formatSQLString(sqlQuery, idSoggetto, idServizio, -1, idSoggetto, idServizio)+"]");
			}

			return pa;
		} catch (Exception qe) {
			throw new DriverConfigurazioneException(qe);
		} finally {

			JDBCUtilities.closeResources(rs, stm);
			
			this.driver.closeConnection(con);

		}

	}

	// NOTA: Metodo non sicuro!!! Possono esistere piu' azioni di port type diversi o accordi diversi !!!!!
	protected List<IDPortaApplicativa> getPortaApplicativaAzione(String nome) throws DriverConfigurazioneException, DriverConfigurazioneNotFound {
		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;
		String sqlQuery = "";

		List<IDPortaApplicativa> id = new ArrayList<IDPortaApplicativa>();
		try {

			if (this.driver.atomica) {
				try {
					con = this.driver.getConnectionFromDatasource("getPortaApplicativaAzione");
				} catch (Exception e) {
					throw new DriverConfigurazioneException("[DriverConfigurazioneDB::existsPortaApplicativaAzione] Exception accedendo al datasource :" + e.getMessage(),e);

				}

			} else
				con = this.driver.globalConnection;
			
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.setANDLogicOperator(true);
			sqlQueryObject.addWhereCondition("azione=?");
			sqlQuery = sqlQueryObject.createSQLQuery();
			stm = con.prepareStatement(sqlQuery);

			stm.setString(1, nome);

			this.driver.log.debug("eseguo query : " + DBUtils.formatSQLString(sqlQuery, nome));
			rs = stm.executeQuery();

			while (rs.next()){
				IDPortaApplicativa idPA = new IDPortaApplicativa();
				idPA.setNome(rs.getString("nome_porta"));
				id.add(idPA);
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
			throw new DriverConfigurazioneNotFound("Porte Applicative che possiedono l'azione ["+nome+"] non esistenti");
		}

	}

	// NOTA: Metodo non sicuro!!! Possono esistere piu' azioni di port type diversi o accordi diversi !!!!!
	protected boolean existsPortaApplicativaAzione(String nome) throws DriverConfigurazioneException {

		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;
		String sqlQuery = "";

		try {
			
			if (this.driver.atomica) {
				try {
					con = this.driver.getConnectionFromDatasource("existsPortaApplicativaAzione");
				} catch (Exception e) {
					throw new DriverConfigurazioneException("[DriverConfigurazioneDB::existsPortaApplicativaAzione] Exception accedendo al datasource :" + e.getMessage(),e);

				}

			} else
				con = this.driver.globalConnection;
			
			boolean esiste = false;
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("azione=?");
			sqlQuery = sqlQueryObject.createSQLQuery();
			stm = con.prepareStatement(sqlQuery);

			stm.setString(1, nome);

			this.driver.log.debug("eseguo query : " + DBUtils.formatSQLString(sqlQuery, nome));
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
	
	
	
	protected List<PortaApplicativa> getPorteApplicativaByIdProprietario(long idProprietario) throws DriverConfigurazioneException{
		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;
		String sqlQuery = "";

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("getPorteApplicativaByIdProprietario");
			} catch (Exception e) {
				throw new DriverConfigurazioneException(
						"[DriverConfigurazioneDB::getPorteApplicativaByIdProprietario] Exception accedendo al datasource :"
								+ e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;
		ArrayList<PortaApplicativa> lista = null;
		try {

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("id_soggetto=?");
			sqlQueryObject.setANDLogicOperator(true);
			sqlQuery = sqlQueryObject.createSQLQuery();
			stm = con.prepareStatement(sqlQuery);

			stm.setLong(1, idProprietario);
			rs = stm.executeQuery();
			lista=new ArrayList<PortaApplicativa>();
			while(rs.next()){
				long id=rs.getLong("id");
				PortaApplicativa pd = this.getPortaApplicativa(id);
				lista.add(pd);
			}

			return lista;
		} catch (Exception qe) {
			throw new DriverConfigurazioneException(qe);
		} finally {

			JDBCUtilities.closeResources(rs, stm);
			
			this.driver.closeConnection(con);

		}

	}

	/**
	 * Recupera tutte le porte applicative relative al servizio idServizio, erogato dal soggetto erogatore del servizio.
	 * @param idSE
	 * @return List<PortaApplicativa>
	 * @throws DriverConfigurazioneException
	 */
	protected List<PortaApplicativa> getPorteApplicative(IDServizio idSE) throws DriverConfigurazioneException{
		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;
		String sqlQuery = "";

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("getPorteApplicative(idServizio)");
			} catch (Exception e) {
				throw new DriverConfigurazioneException(
						"[DriverConfigurazioneDB::getPorteApplicative] Exception accedendo al datasource :"
								+ e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;
		ArrayList<PortaApplicativa> lista = null;
		try {
			IDSoggetto erogatore = idSE.getSoggettoErogatore();
			String tipoServizio = idSE.getTipo();
			String nomeServizio = idSE.getNome();
			Integer versioneServizio = idSE.getVersione();

			long idSoggetto = DBUtils.getIdSoggetto(erogatore.getNome(), erogatore.getTipo(), con, this.driver.tipoDB);
			long idServizio = DBUtils.getIdServizio(nomeServizio, tipoServizio, versioneServizio, erogatore.getNome(), erogatore.getTipo(), con, this.driver.tipoDB);

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("id_soggetto=?");
			sqlQueryObject.addWhereCondition(false,"id_servizio=?","tipo_servizio=? AND servizio=?");

			sqlQueryObject.setANDLogicOperator(true);
			sqlQuery = sqlQueryObject.createSQLQuery();
			stm = con.prepareStatement(sqlQuery);

			stm.setLong(1, idSoggetto);
			stm.setLong(2, idServizio);
			stm.setString(3, tipoServizio);
			stm.setString(4, nomeServizio);
			rs = stm.executeQuery();
			lista=new ArrayList<PortaApplicativa>();
			while(rs.next()){
				long id=rs.getLong("id");
				PortaApplicativa pd = this.getPortaApplicativa(id);
				lista.add(pd);
			}

			return lista;
		} catch (Exception qe) {
			throw new DriverConfigurazioneException(qe);
		} finally {

			JDBCUtilities.closeResources(rs, stm);
			
			this.driver.closeConnection(con);

		}

	}
	
	protected List<PortaApplicativa> getPorteApplicativeBySoggetto(long idSoggetto) throws DriverConfigurazioneException {
		String nomeMetodo = "getPorteApplicativeBySoggetto";

		Connection con = null;
		boolean error = false;
		PreparedStatement stmt=null;
		ResultSet risultato=null;
		ArrayList<PortaApplicativa> lista = new ArrayList<PortaApplicativa>();

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource(nomeMetodo);
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.log.debug("operazione this.driver.atomica = " + this.driver.atomica);

		try {

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE);
			sqlQueryObject.addSelectField("id");
			sqlQueryObject.addWhereCondition("id_soggetto=?");
			String queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idSoggetto);
			risultato = stmt.executeQuery();

			while (risultato.next()) {

				Long id = risultato.getLong("id");
				lista.add(this.getPortaApplicativa(id));

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
	
	protected List<PortaApplicativa> getPorteApplicativeBySoggettoVirtuale(IDSoggetto soggettoVirtuale) throws DriverConfigurazioneException {

		Connection con = null;
		boolean error = false;
		PreparedStatement stm=null;
		ResultSet rs=null;
		ArrayList<PortaApplicativa> lista = new ArrayList<PortaApplicativa>();

		if (this.driver.atomica) {
			try {
				con = (Connection) this.driver.getConnectionFromDatasource("getPorteApplicativeBySoggettoVirtuale");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getPorteApplicativeBySoggettoVirtuale] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.log.debug("operazione this.driver.atomica = " + this.driver.atomica);

		try {

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE);
			sqlQueryObject.addSelectField("id");
			sqlQueryObject.addWhereCondition("tipo_soggetto_virtuale is not null");
			sqlQueryObject.addWhereCondition("nome_soggetto_virtuale is not null");
			sqlQueryObject.addWhereCondition("tipo_soggetto_virtuale = ?");
			sqlQueryObject.addWhereCondition("nome_soggetto_virtuale = ?");
			sqlQueryObject.setANDLogicOperator(true);
			String queryString = sqlQueryObject.createSQLQuery();

			stm = con.prepareStatement(queryString);
			stm.setString(1, soggettoVirtuale.getTipo());
			stm.setString(2, soggettoVirtuale.getNome());

			rs = stm.executeQuery();
			while (rs.next()){
				lista.add(getPortaApplicativa(rs.getLong("id"),con));
			}
			rs.close();
			stm.close();

			return lista;

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getPorteApplicativeBySoggettoVirtuale] Errore : " + qe.getMessage(),qe);
		} finally {
			//Chiudo statement and resultset
			JDBCUtilities.closeResources(rs, stm);

			this.driver.closeConnection(error,con);
		}

	}
	
	protected List<IDPortaApplicativa> getAllIdPorteApplicative(
			FiltroRicercaPorteApplicative filtroRicerca) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{

		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;
		List<String> nomiPA = null;
		
		this.driver.log.debug("getAllIdPorteApplicative...");

		try {
			this.driver.log.debug("operazione atomica = " + this.driver.atomica);
			// prendo la connessione dal pool
			if (this.driver.atomica)
				con = this.driver.getConnectionFromDatasource("getAllIdPorteApplicative");
			else
				con = this.driver.globalConnection;

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE);
			if(filtroRicerca!=null && (filtroRicerca.getTipoSoggetto()!=null || filtroRicerca.getNomeSoggetto()!=null) ){
				sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
			}
			sqlQueryObject.addSelectField(CostantiDB.PORTE_APPLICATIVE+".nome_porta");
			if(filtroRicerca!=null && (filtroRicerca.getTipoSoggetto()!=null || filtroRicerca.getNomeSoggetto()!=null) ){
				sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE+".id_soggetto = "+CostantiDB.SOGGETTI+".id");
			}
			if(filtroRicerca!=null){
				if(filtroRicerca.getIdRuolo()!=null){
					sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_RUOLI);
					sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE_RUOLI+".id_porta = "+CostantiDB.PORTE_APPLICATIVE+".id");
				}
			}
			if(filtroRicerca!=null){
				if(filtroRicerca.getIdScope()!=null){
					sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_SCOPE);
					sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE_SCOPE+".id_porta = "+CostantiDB.PORTE_APPLICATIVE+".id");
				}
			}
			if(filtroRicerca!=null){
				if(filtroRicerca.getIdSoggettoAutorizzato()!=null &&
						(filtroRicerca.getIdSoggettoAutorizzato().getTipo()!=null || 
						filtroRicerca.getIdSoggettoAutorizzato().getNome()!=null)){
					sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_SOGGETTI);
					sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE_SOGGETTI+".id_porta = "+CostantiDB.PORTE_APPLICATIVE+".id");
				}
			}
			if(filtroRicerca!=null){
				if(filtroRicerca.getIdServizioApplicativoAutorizzato()!=null &&
						filtroRicerca.getIdServizioApplicativoAutorizzato().getNome()!=null &&
						filtroRicerca.getIdServizioApplicativoAutorizzato().getIdSoggettoProprietario()!=null &&
						filtroRicerca.getIdServizioApplicativoAutorizzato().getIdSoggettoProprietario().getTipo()!=null &&
						filtroRicerca.getIdServizioApplicativoAutorizzato().getIdSoggettoProprietario().getNome()!=null
						){
					sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_SA_AUTORIZZATI);					
					sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE_SA_AUTORIZZATI+".id_porta = "+CostantiDB.PORTE_APPLICATIVE+".id");
				}
			}
			if(filtroRicerca!=null){
				if(filtroRicerca.getIdServizioApplicativoAutorizzatoToken()!=null &&
						filtroRicerca.getIdServizioApplicativoAutorizzatoToken().getNome()!=null &&
						filtroRicerca.getIdServizioApplicativoAutorizzatoToken().getIdSoggettoProprietario()!=null &&
						filtroRicerca.getIdServizioApplicativoAutorizzatoToken().getIdSoggettoProprietario().getTipo()!=null &&
						filtroRicerca.getIdServizioApplicativoAutorizzatoToken().getIdSoggettoProprietario().getNome()!=null
						){
					sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_TOKEN_SA);					
					sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE_TOKEN_SA+".id_porta = "+CostantiDB.PORTE_APPLICATIVE+".id");
				}
			}
			if(filtroRicerca!=null){
				if(filtroRicerca.getIdRuoloToken()!=null){
					sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_TOKEN_RUOLI);
					sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE_TOKEN_RUOLI+".id_porta = "+CostantiDB.PORTE_APPLICATIVE+".id");
				}
			}
			if(filtroRicerca!=null){
				if(filtroRicerca.getIdSoggettoRiferitoApplicabilitaTrasformazione()!=null &&
						(filtroRicerca.getIdSoggettoRiferitoApplicabilitaTrasformazione().getTipo()!=null || 
						filtroRicerca.getIdSoggettoRiferitoApplicabilitaTrasformazione().getNome()!=null)){
					sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI);
					sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI_SOGGETTI);
					sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI+".id_porta = "+CostantiDB.PORTE_APPLICATIVE+".id");
					sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI_SOGGETTI+".id_trasformazione = "+CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI+".id");
				}
			}
			boolean applicabilitaTrasformazioneTrasporto = false;
			boolean applicabilitaTrasformazioneSiaTrasportoCheToken = false;
			if(filtroRicerca!=null){
				if(filtroRicerca.getIdServizioApplicativoRiferitoApplicabilitaTrasformazione()!=null &&
						filtroRicerca.getIdServizioApplicativoRiferitoApplicabilitaTrasformazione().getNome()!=null &&
						filtroRicerca.getIdServizioApplicativoRiferitoApplicabilitaTrasformazione().getIdSoggettoProprietario()!=null &&
						filtroRicerca.getIdServizioApplicativoRiferitoApplicabilitaTrasformazione().getIdSoggettoProprietario().getTipo()!=null &&
						filtroRicerca.getIdServizioApplicativoRiferitoApplicabilitaTrasformazione().getIdSoggettoProprietario().getNome()!=null
						){
					sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI);
					sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI_SA);
					sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI+".id_porta = "+CostantiDB.PORTE_APPLICATIVE+".id");
					sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI_SA+".id_trasformazione = "+CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI+".id");
					applicabilitaTrasformazioneTrasporto = true;
				}
			}
			if(filtroRicerca!=null){
				if(filtroRicerca.getIdServizioApplicativoTokenRiferitoApplicabilitaTrasformazione()!=null &&
						filtroRicerca.getIdServizioApplicativoTokenRiferitoApplicabilitaTrasformazione().getNome()!=null &&
						filtroRicerca.getIdServizioApplicativoTokenRiferitoApplicabilitaTrasformazione().getIdSoggettoProprietario()!=null &&
						filtroRicerca.getIdServizioApplicativoTokenRiferitoApplicabilitaTrasformazione().getIdSoggettoProprietario().getTipo()!=null &&
						filtroRicerca.getIdServizioApplicativoTokenRiferitoApplicabilitaTrasformazione().getIdSoggettoProprietario().getNome()!=null
						){
					if(applicabilitaTrasformazioneTrasporto) {
						applicabilitaTrasformazioneSiaTrasportoCheToken = true;
					}
					else {
						sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI);
						sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI_SA);
						sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI+".id_porta = "+CostantiDB.PORTE_APPLICATIVE+".id");
						sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI_SA+".id_trasformazione = "+CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI+".id");
					}
				}
			}
			boolean porteDelegatePerAzioni = false;
			if(filtroRicerca!=null && filtroRicerca.getNomePortaDelegante()!=null) {
				porteDelegatePerAzioni = true;
				if(filtroRicerca.getAzione()!=null) {
					sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_AZIONI);
					sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE_AZIONI+".id_porta = "+CostantiDB.PORTE_APPLICATIVE+".id");
				}
			}

			if(filtroRicerca!=null){
				// Filtro By Data
				if(filtroRicerca.getMinDate()!=null)
					sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE+".ora_registrazione > ?");
				if(filtroRicerca.getMaxDate()!=null)
					sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE+".ora_registrazione < ?");
				if(filtroRicerca.getTipoSoggetto()!=null)
					sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".tipo_soggetto = ?");
				if(filtroRicerca.getNomeSoggetto()!=null)
					sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".nome_soggetto = ?");
				if(filtroRicerca.getTipoSoggettoVirtuale()!=null)
					sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE+".tipo_soggetto_virtuale = ?");
				if(filtroRicerca.getNomeSoggettoVirtuale()!=null)
					sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE+".nome_soggetto_virtuale = ?");
				if(filtroRicerca.getTipoServizio()!=null)
					sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE+".tipo_servizio = ?");
				if(filtroRicerca.getNomeServizio()!=null)
					sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE+".servizio = ?");
				if(filtroRicerca.getVersioneServizio()!=null)
					sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE+".versione_servizio = ?");
				if(!porteDelegatePerAzioni && filtroRicerca.getAzione()!=null)
					sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE+".azione = ?");
				if(filtroRicerca.getNome()!=null)
					sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE+".nome_porta = ?");
				if(filtroRicerca.getIdRuolo()!=null)
					sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE_RUOLI+".ruolo = ?");
				if(filtroRicerca.getIdScope()!=null)
					sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE_SCOPE+".scope = ?");
				if(filtroRicerca.getIdSoggettoAutorizzato()!=null &&
						(filtroRicerca.getIdSoggettoAutorizzato().getTipo()!=null || 
						filtroRicerca.getIdSoggettoAutorizzato().getNome()!=null)){
					if(filtroRicerca.getIdSoggettoAutorizzato().getTipo()!=null) {
						sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE_SOGGETTI+".tipo_soggetto= ?");
					}
					if(filtroRicerca.getIdSoggettoAutorizzato().getNome()!=null) {
						sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE_SOGGETTI+".nome_soggetto= ?");
					}
				}
				if(filtroRicerca!=null){
					if(filtroRicerca.getIdServizioApplicativoAutorizzato()!=null &&
							filtroRicerca.getIdServizioApplicativoAutorizzato().getNome()!=null &&
							filtroRicerca.getIdServizioApplicativoAutorizzato().getIdSoggettoProprietario()!=null &&
							filtroRicerca.getIdServizioApplicativoAutorizzato().getIdSoggettoProprietario().getTipo()!=null &&
							filtroRicerca.getIdServizioApplicativoAutorizzato().getIdSoggettoProprietario().getNome()!=null
							){
						sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE_SA_AUTORIZZATI+".id_servizio_applicativo = ?");
					}
				}
				if(filtroRicerca!=null){
					if(filtroRicerca.getIdServizioApplicativoAutorizzatoToken()!=null &&
							filtroRicerca.getIdServizioApplicativoAutorizzatoToken().getNome()!=null &&
							filtroRicerca.getIdServizioApplicativoAutorizzatoToken().getIdSoggettoProprietario()!=null &&
							filtroRicerca.getIdServizioApplicativoAutorizzatoToken().getIdSoggettoProprietario().getTipo()!=null &&
							filtroRicerca.getIdServizioApplicativoAutorizzatoToken().getIdSoggettoProprietario().getNome()!=null
							){
						sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE_TOKEN_SA+".id_servizio_applicativo = ?");
					}
				}
				if(filtroRicerca.getIdRuoloToken()!=null)
					sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE_TOKEN_RUOLI+".ruolo = ?");
				if(filtroRicerca.getIdSoggettoRiferitoApplicabilitaTrasformazione()!=null &&
						(filtroRicerca.getIdSoggettoRiferitoApplicabilitaTrasformazione().getTipo()!=null || 
						filtroRicerca.getIdSoggettoRiferitoApplicabilitaTrasformazione().getNome()!=null)){
					if(filtroRicerca.getIdSoggettoRiferitoApplicabilitaTrasformazione().getTipo()!=null) {
						sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI_SOGGETTI+".tipo_soggetto= ?");
					}
					if(filtroRicerca.getIdSoggettoRiferitoApplicabilitaTrasformazione().getNome()!=null) {
						sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI_SOGGETTI+".nome_soggetto= ?");
					}
				}
				if(applicabilitaTrasformazioneSiaTrasportoCheToken) {
					sqlQueryObject.addWhereCondition(false,
							CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI_SA+".id_servizio_applicativo = ?",
							CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI_SA+".id_servizio_applicativo = ?");
				}
				else {
					if(filtroRicerca!=null){
						if(filtroRicerca.getIdServizioApplicativoRiferitoApplicabilitaTrasformazione()!=null &&
								filtroRicerca.getIdServizioApplicativoRiferitoApplicabilitaTrasformazione().getNome()!=null &&
								filtroRicerca.getIdServizioApplicativoRiferitoApplicabilitaTrasformazione().getIdSoggettoProprietario()!=null &&
								filtroRicerca.getIdServizioApplicativoRiferitoApplicabilitaTrasformazione().getIdSoggettoProprietario().getTipo()!=null &&
								filtroRicerca.getIdServizioApplicativoRiferitoApplicabilitaTrasformazione().getIdSoggettoProprietario().getNome()!=null
								){
							sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI_SA+".id_servizio_applicativo = ?");
						}
					}
					if(filtroRicerca!=null){
						if(filtroRicerca.getIdServizioApplicativoTokenRiferitoApplicabilitaTrasformazione()!=null &&
								filtroRicerca.getIdServizioApplicativoTokenRiferitoApplicabilitaTrasformazione().getNome()!=null &&
								filtroRicerca.getIdServizioApplicativoTokenRiferitoApplicabilitaTrasformazione().getIdSoggettoProprietario()!=null &&
								filtroRicerca.getIdServizioApplicativoTokenRiferitoApplicabilitaTrasformazione().getIdSoggettoProprietario().getTipo()!=null &&
								filtroRicerca.getIdServizioApplicativoTokenRiferitoApplicabilitaTrasformazione().getIdSoggettoProprietario().getNome()!=null
								){
							sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI_SA+".id_servizio_applicativo = ?");
						}
					}
				}
				if(filtroRicerca.getStato()!=null)
					sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE+".stato = ?");
				if(porteDelegatePerAzioni) {
					sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE+".nome_porta_delegante_azione = ?");
					if(filtroRicerca.getAzione()!=null)
						sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE_AZIONI+".azione = ?");
				}
			}

			sqlQueryObject.setANDLogicOperator(true);
			String sqlQuery = sqlQueryObject.createSQLQuery();
			this.driver.log.debug("eseguo query : " + sqlQuery );
			stm = con.prepareStatement(sqlQuery);
			int indexStmt = 1;
			if(filtroRicerca!=null){
				if(filtroRicerca.getMinDate()!=null){
					this.driver.log.debug("minDate stmt.setTimestamp("+filtroRicerca.getMinDate()+")");
					stm.setTimestamp(indexStmt, new Timestamp(filtroRicerca.getMinDate().getTime()));
					indexStmt++;
				}
				if(filtroRicerca.getMaxDate()!=null){
					this.driver.log.debug("maxDate stmt.setTimestamp("+filtroRicerca.getMaxDate()+")");
					stm.setTimestamp(indexStmt, new Timestamp(filtroRicerca.getMaxDate().getTime()));
					indexStmt++;
				}	
				if(filtroRicerca.getTipoSoggetto()!=null){
					this.driver.log.debug("tipoSoggetto stmt.setString("+filtroRicerca.getTipoSoggetto()+")");
					stm.setString(indexStmt, filtroRicerca.getTipoSoggetto());
					indexStmt++;
				}
				if(filtroRicerca.getNomeSoggetto()!=null){
					this.driver.log.debug("nomeSoggetto stmt.setString("+filtroRicerca.getNomeSoggetto()+")");
					stm.setString(indexStmt, filtroRicerca.getNomeSoggetto());
					indexStmt++;
				}	
				if(filtroRicerca.getTipoSoggettoVirtuale()!=null){
					this.driver.log.debug("tipoSoggettoVirtuale stmt.setString("+filtroRicerca.getTipoSoggettoVirtuale()+")");
					stm.setString(indexStmt, filtroRicerca.getTipoSoggettoVirtuale());
					indexStmt++;
				}
				if(filtroRicerca.getNomeSoggettoVirtuale()!=null){
					this.driver.log.debug("nomeSoggettoVirtuale stmt.setString("+filtroRicerca.getNomeSoggettoVirtuale()+")");
					stm.setString(indexStmt, filtroRicerca.getNomeSoggettoVirtuale());
					indexStmt++;
				}	
				if(filtroRicerca.getTipoServizio()!=null){
					this.driver.log.debug("tipoServizio stmt.setString("+filtroRicerca.getTipoServizio()+")");
					stm.setString(indexStmt, filtroRicerca.getTipoServizio());
					indexStmt++;
				}
				if(filtroRicerca.getNomeServizio()!=null){
					this.driver.log.debug("nomeServizio stmt.setString("+filtroRicerca.getNomeServizio()+")");
					stm.setString(indexStmt, filtroRicerca.getNomeServizio());
					indexStmt++;
				}	
				if(filtroRicerca.getVersioneServizio()!=null){
					this.driver.log.debug("versioneServizio stmt.setInt("+filtroRicerca.getVersioneServizio()+")");
					stm.setInt(indexStmt, filtroRicerca.getVersioneServizio());
					indexStmt++;
				}
				if(!porteDelegatePerAzioni && filtroRicerca.getAzione()!=null){
					this.driver.log.debug("azione stmt.setString("+filtroRicerca.getAzione()+")");
					stm.setString(indexStmt, filtroRicerca.getAzione());
					indexStmt++;
				}	
				if(filtroRicerca.getNome()!=null){
					this.driver.log.debug("nome stmt.setString("+filtroRicerca.getNome()+")");
					stm.setString(indexStmt, filtroRicerca.getNome());
					indexStmt++;
				}
				if(filtroRicerca.getIdRuolo()!=null){
					this.driver.log.debug("ruolo stmt.setString("+filtroRicerca.getIdRuolo().getNome()+")");
					stm.setString(indexStmt, filtroRicerca.getIdRuolo().getNome());
					indexStmt++;
				}
				if(filtroRicerca.getIdScope()!=null){
					this.driver.log.debug("scope stmt.setString("+filtroRicerca.getIdScope().getNome()+")");
					stm.setString(indexStmt, filtroRicerca.getIdScope().getNome());
					indexStmt++;
				}
				if(filtroRicerca.getIdSoggettoAutorizzato()!=null &&
						(filtroRicerca.getIdSoggettoAutorizzato().getTipo()!=null || 
						filtroRicerca.getIdSoggettoAutorizzato().getNome()!=null)){
					if(filtroRicerca.getIdSoggettoAutorizzato().getTipo()!=null) {
						this.driver.log.debug("idSoggettoAutorizzazione.tipo stmt.setString("+filtroRicerca.getIdSoggettoAutorizzato().getTipo()+")");
						stm.setString(indexStmt, filtroRicerca.getIdSoggettoAutorizzato().getTipo());
						indexStmt++;
					}
					if(filtroRicerca.getIdSoggettoAutorizzato().getNome()!=null) {
						this.driver.log.debug("idSoggettoAutorizzazione.nome stmt.setString("+filtroRicerca.getIdSoggettoAutorizzato().getNome()+")");
						stm.setString(indexStmt, filtroRicerca.getIdSoggettoAutorizzato().getNome());
						indexStmt++;
					}
				}
				if(filtroRicerca!=null){
					if(filtroRicerca.getIdServizioApplicativoAutorizzato()!=null &&
							filtroRicerca.getIdServizioApplicativoAutorizzato().getNome()!=null &&
							filtroRicerca.getIdServizioApplicativoAutorizzato().getIdSoggettoProprietario()!=null &&
							filtroRicerca.getIdServizioApplicativoAutorizzato().getIdSoggettoProprietario().getTipo()!=null &&
							filtroRicerca.getIdServizioApplicativoAutorizzato().getIdSoggettoProprietario().getNome()!=null
							){
						long idSA = DBUtils.getIdServizioApplicativo(filtroRicerca.getIdServizioApplicativoAutorizzato().getNome(), 
								filtroRicerca.getIdServizioApplicativoAutorizzato().getIdSoggettoProprietario().getTipo(), filtroRicerca.getIdServizioApplicativoAutorizzato().getIdSoggettoProprietario().getNome(), con, this.driver.tipoDB);
						this.driver.log.debug("idServizioApplicativoAutorizzato stmt.setLong("+idSA
								+") (getIdBy Nome["+filtroRicerca.getIdServizioApplicativoAutorizzato().getNome()
								+"] tipoSoggetto["+filtroRicerca.getIdServizioApplicativoAutorizzato().getIdSoggettoProprietario().getTipo()+
								"] nomeSoggetto["+filtroRicerca.getIdServizioApplicativoAutorizzato().getIdSoggettoProprietario().getNome()+"])");
						stm.setLong(indexStmt, idSA);
						indexStmt++;
					}
				}
				if(filtroRicerca!=null){
					if(filtroRicerca.getIdServizioApplicativoAutorizzatoToken()!=null &&
							filtroRicerca.getIdServizioApplicativoAutorizzatoToken().getNome()!=null &&
							filtroRicerca.getIdServizioApplicativoAutorizzatoToken().getIdSoggettoProprietario()!=null &&
							filtroRicerca.getIdServizioApplicativoAutorizzatoToken().getIdSoggettoProprietario().getTipo()!=null &&
							filtroRicerca.getIdServizioApplicativoAutorizzatoToken().getIdSoggettoProprietario().getNome()!=null
							){
						long idSA = DBUtils.getIdServizioApplicativo(filtroRicerca.getIdServizioApplicativoAutorizzatoToken().getNome(), 
								filtroRicerca.getIdServizioApplicativoAutorizzatoToken().getIdSoggettoProprietario().getTipo(), filtroRicerca.getIdServizioApplicativoAutorizzatoToken().getIdSoggettoProprietario().getNome(), con, this.driver.tipoDB);
						this.driver.log.debug("idServizioApplicativoAutorizzatoToken stmt.setLong("+idSA
								+") (getIdBy Nome["+filtroRicerca.getIdServizioApplicativoAutorizzatoToken().getNome()
								+"] tipoSoggetto["+filtroRicerca.getIdServizioApplicativoAutorizzatoToken().getIdSoggettoProprietario().getTipo()+
								"] nomeSoggetto["+filtroRicerca.getIdServizioApplicativoAutorizzatoToken().getIdSoggettoProprietario().getNome()+"])");
						stm.setLong(indexStmt, idSA);
						indexStmt++;
					}
				}
				if(filtroRicerca.getIdRuoloToken()!=null){
					this.driver.log.debug("ruoloToken stmt.setString("+filtroRicerca.getIdRuoloToken().getNome()+")");
					stm.setString(indexStmt, filtroRicerca.getIdRuoloToken().getNome());
					indexStmt++;
				}
				if(filtroRicerca.getIdSoggettoRiferitoApplicabilitaTrasformazione()!=null &&
						(filtroRicerca.getIdSoggettoRiferitoApplicabilitaTrasformazione().getTipo()!=null || 
						filtroRicerca.getIdSoggettoRiferitoApplicabilitaTrasformazione().getNome()!=null)){
					if(filtroRicerca.getIdSoggettoRiferitoApplicabilitaTrasformazione().getTipo()!=null) {
						this.driver.log.debug("idSoggettoTrasformazioni.tipo stmt.setString("+filtroRicerca.getIdSoggettoRiferitoApplicabilitaTrasformazione().getTipo()+")");
						stm.setString(indexStmt, filtroRicerca.getIdSoggettoRiferitoApplicabilitaTrasformazione().getTipo());
						indexStmt++;
					}
					if(filtroRicerca.getIdSoggettoRiferitoApplicabilitaTrasformazione().getNome()!=null) {
						this.driver.log.debug("idSoggettoTrasformazioni.nome stmt.setString("+filtroRicerca.getIdSoggettoRiferitoApplicabilitaTrasformazione().getNome()+")");
						stm.setString(indexStmt, filtroRicerca.getIdSoggettoRiferitoApplicabilitaTrasformazione().getNome());
						indexStmt++;
					}
				}
				if(filtroRicerca!=null){
					if(filtroRicerca.getIdServizioApplicativoRiferitoApplicabilitaTrasformazione()!=null &&
							filtroRicerca.getIdServizioApplicativoRiferitoApplicabilitaTrasformazione().getNome()!=null &&
							filtroRicerca.getIdServizioApplicativoRiferitoApplicabilitaTrasformazione().getIdSoggettoProprietario()!=null &&
							filtroRicerca.getIdServizioApplicativoRiferitoApplicabilitaTrasformazione().getIdSoggettoProprietario().getTipo()!=null &&
							filtroRicerca.getIdServizioApplicativoRiferitoApplicabilitaTrasformazione().getIdSoggettoProprietario().getNome()!=null
							){
						long idSA = DBUtils.getIdServizioApplicativo(filtroRicerca.getIdServizioApplicativoRiferitoApplicabilitaTrasformazione().getNome(), 
								filtroRicerca.getIdServizioApplicativoRiferitoApplicabilitaTrasformazione().getIdSoggettoProprietario().getTipo(), filtroRicerca.getIdServizioApplicativoRiferitoApplicabilitaTrasformazione().getNome(), con, this.driver.tipoDB);
						this.driver.log.debug("idServizioApplicativoTrasformazioni stmt.setLong("+idSA
								+") (getIdBy Nome["+filtroRicerca.getIdServizioApplicativoRiferitoApplicabilitaTrasformazione().getNome()
								+"] tipoSoggetto["+filtroRicerca.getIdServizioApplicativoRiferitoApplicabilitaTrasformazione().getIdSoggettoProprietario().getTipo()+
								"] nomeSoggetto["+filtroRicerca.getIdServizioApplicativoRiferitoApplicabilitaTrasformazione().getIdSoggettoProprietario().getNome()+"])");
						stm.setLong(indexStmt, idSA);
						indexStmt++;
					}
				}
				if(filtroRicerca!=null){
					if(filtroRicerca.getIdServizioApplicativoTokenRiferitoApplicabilitaTrasformazione()!=null &&
							filtroRicerca.getIdServizioApplicativoTokenRiferitoApplicabilitaTrasformazione().getNome()!=null &&
							filtroRicerca.getIdServizioApplicativoTokenRiferitoApplicabilitaTrasformazione().getIdSoggettoProprietario()!=null &&
							filtroRicerca.getIdServizioApplicativoTokenRiferitoApplicabilitaTrasformazione().getIdSoggettoProprietario().getTipo()!=null &&
							filtroRicerca.getIdServizioApplicativoTokenRiferitoApplicabilitaTrasformazione().getIdSoggettoProprietario().getNome()!=null
							){
						long idSA = DBUtils.getIdServizioApplicativo(filtroRicerca.getIdServizioApplicativoTokenRiferitoApplicabilitaTrasformazione().getNome(), 
								filtroRicerca.getIdServizioApplicativoTokenRiferitoApplicabilitaTrasformazione().getIdSoggettoProprietario().getTipo(), filtroRicerca.getIdServizioApplicativoTokenRiferitoApplicabilitaTrasformazione().getNome(), con, this.driver.tipoDB);
						this.driver.log.debug("idServizioApplicativoTokenTrasformazioni stmt.setLong("+idSA
								+") (getIdBy Nome["+filtroRicerca.getIdServizioApplicativoTokenRiferitoApplicabilitaTrasformazione().getNome()
								+"] tipoSoggetto["+filtroRicerca.getIdServizioApplicativoTokenRiferitoApplicabilitaTrasformazione().getIdSoggettoProprietario().getTipo()+
								"] nomeSoggetto["+filtroRicerca.getIdServizioApplicativoTokenRiferitoApplicabilitaTrasformazione().getIdSoggettoProprietario().getNome()+"])");
						stm.setLong(indexStmt, idSA);
						indexStmt++;
					}
				}
				if(filtroRicerca.getStato()!=null){
					this.driver.log.debug("stato stmt.setString("+filtroRicerca.getStato().getValue()+")");
					stm.setString(indexStmt, filtroRicerca.getStato().getValue());
					indexStmt++;
				}
				if(porteDelegatePerAzioni) {
					this.driver.log.debug("nomePortaDelegata stmt.setString("+filtroRicerca.getNomePortaDelegante()+")");
					stm.setString(indexStmt, filtroRicerca.getNomePortaDelegante());
					indexStmt++;
					if(filtroRicerca.getAzione()!=null) {
						this.driver.log.debug("azione stmt.setString("+filtroRicerca.getAzione()+")");
						stm.setString(indexStmt, filtroRicerca.getAzione());
						indexStmt++;
					}
				}
			}
			rs = stm.executeQuery();
			nomiPA = new ArrayList<>();
			while (rs.next()) {
				nomiPA.add(rs.getString("nome_porta"));
			}
			
		}catch(DriverConfigurazioneNotFound de){
			throw de;
		}
		catch(Exception e){
			throw new DriverConfigurazioneException("getAllIdPorteApplicative error",e);
		} finally {

			//Chiudo statement and resultset
			JDBCUtilities.closeResources(rs, stm);

			this.driver.closeConnection(con);

		}

		if(nomiPA == null || nomiPA.size()<=0){
			if(filtroRicerca!=null)
				throw new DriverConfigurazioneNotFound("PorteApplicative non trovate che rispettano il filtro di ricerca selezionato: "+filtroRicerca.toString());
			else
				throw new DriverConfigurazioneNotFound("PorteApplicative non trovate");
		}else{
			List<IDPortaApplicativa> idsPA = new ArrayList<IDPortaApplicativa>();
			for (String nomePortaApplicativa : nomiPA) {
				idsPA.add(this.getIDPortaApplicativa(nomePortaApplicativa));
			}
			return idsPA;
		}
	}
	
	
	protected List<IDConnettore> getConnettoriConsegnaNotifichePrioritarie(String queueName) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;

		this.driver.log.debug("getConnettoriConsegnaNotifichePrioritarie...");

		try {
			this.driver.log.debug("operazione atomica = " + this.driver.atomica);
			// prendo la connessione dal pool
			if (this.driver.atomica)
				con = this.driver.getConnectionFromDatasource("getConnettoriConsegnaNotifichePrioritarie");
			else
				con = this.driver.globalConnection;

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_SA);
			sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
			sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
			sqlQueryObject.addSelectField(CostantiDB.SERVIZI_APPLICATIVI+".nome");
			sqlQueryObject.addSelectField(CostantiDB.SOGGETTI+".tipo_soggetto");
			sqlQueryObject.addSelectField(CostantiDB.SOGGETTI+".nome_soggetto");
			sqlQueryObject.addSelectField(CostantiDB.PORTE_APPLICATIVE_SA+".connettore_nome");
			if(queueName!=null) {
				sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE_SA+".connettore_coda = ?");
			}
			sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE_SA+".connettore_max_priorita = ?");
			sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE_SA+".id_servizio_applicativo = "+CostantiDB.SERVIZI_APPLICATIVI+".id");
			sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".id_soggetto = "+CostantiDB.SOGGETTI+".id");
			sqlQueryObject.setANDLogicOperator(true);
			
			String sqlQuery = sqlQueryObject.createSQLQuery();
			this.driver.log.debug("eseguo query : " + sqlQuery );
			stm = con.prepareStatement(sqlQuery);
			
			int indexStmt = 1;
			if(queueName!=null) {
				this.driver.log.debug("queue stmt.setString("+queueName+")");
				stm.setString(indexStmt, queueName);
				indexStmt++;
			}
			this.driver.log.debug("connettore_max_priorita stmt.setInt("+CostantiDB.TRUE+")");
			stm.setInt(indexStmt, CostantiDB.TRUE);
			indexStmt++;
			
			rs = stm.executeQuery();
			List<IDConnettore> idsSA = new ArrayList<IDConnettore>();
			while (rs.next()) {
				IDSoggetto idS = new IDSoggetto(rs.getString("tipo_soggetto"),rs.getString("nome_soggetto"));
				IDConnettore idSA = new IDConnettore();
				idSA.setIdSoggettoProprietario(idS);
				idSA.setNome(rs.getString("nome"));
				idSA.setNomeConnettore(rs.getString("connettore_nome"));
				if(idSA.getNomeConnettore()==null) {
					idSA.setNomeConnettore(CostantiConfigurazione.NOME_CONNETTORE_DEFAULT);
				}
				idsSA.add(idSA);
			}
			if(idsSA.size()==0){
				if(queueName!=null)
					throw new DriverConfigurazioneNotFound("Connettori non trovati per la coda '"+queueName+"'");
				else
					throw new DriverConfigurazioneNotFound("Connettori non trovati");
			}else{
				return idsSA;
			}
		}catch(DriverConfigurazioneNotFound de){
			throw de;
		}
		catch(Exception e){
			throw new DriverConfigurazioneException("getConnettoriConsegnaNotifichePrioritarie error",e);
		} finally {

			//Chiudo statement and resultset
			JDBCUtilities.closeResources(rs, stm);

			this.driver.closeConnection(con);

		}
	}
	
	protected int resetConnettoriConsegnaNotifichePrioritarie(String queueName) throws DriverConfigurazioneException {
		Connection con = null;
		PreparedStatement stm = null;
		
		this.driver.log.debug("resetConnettoriConsegnaNotifichePrioritarie...");

		try {
			this.driver.log.debug("operazione atomica = " + this.driver.atomica);
			// prendo la connessione dal pool
			if (this.driver.atomica)
				con = this.driver.getConnectionFromDatasource("getConnettoriConsegnaNotifichePrioritarie");
			else
				con = this.driver.globalConnection;

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addUpdateTable(CostantiDB.PORTE_APPLICATIVE_SA);
			sqlQueryObject.addUpdateField("connettore_max_priorita", "?");
			if(queueName!=null) {
				sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE_SA+".connettore_coda = ?");
			}
			sqlQueryObject.setANDLogicOperator(true);
			
			String sqlQuery = sqlQueryObject.createSQLUpdate();
			this.driver.log.debug("eseguo query : " + sqlQuery );
			stm = con.prepareStatement(sqlQuery);
			
			int indexStmt = 1;
			this.driver.log.debug("connettore_max_priorita stmt.setInt("+CostantiDB.FALSE+")");
			stm.setInt(indexStmt, CostantiDB.FALSE);
			indexStmt++;
			if(queueName!=null) {
				this.driver.log.debug("queue stmt.setString("+queueName+")");
				stm.setString(indexStmt, queueName);
				indexStmt++;
			}

			
			int rows = stm.executeUpdate();
			return rows;
			
		}
		catch(Exception e){
			throw new DriverConfigurazioneException("resetConnettoriConsegnaNotifichePrioritarie error",e);
		} finally {

			//Chiudo statement and resultset
			JDBCUtilities.closeResources(stm);

			this.driver.closeConnection(con);

		}
	}
	
	protected List<PortaApplicativa> getPorteApplicativeByPolicyGestioneToken(String nome) throws DriverConfigurazioneException{
		String nomeMetodo = "getPorteApplicativeByPolicyGestioneToken";

		Connection con = null;
		boolean error = false;
		PreparedStatement stmt=null;
		ResultSet risultato=null;
		ArrayList<PortaApplicativa> lista = new ArrayList<PortaApplicativa>();

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource(nomeMetodo);
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.log.debug("operazione this.driver.atomica = " + this.driver.atomica);

		try {

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE);
			sqlQueryObject.addSelectField("id");
			sqlQueryObject.addWhereCondition("token_policy=?");
			String queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setString(1, nome);
			risultato = stmt.executeQuery();

			while (risultato.next()) {

				Long id = risultato.getLong("id");
				lista.add(this.getPortaApplicativa(id));

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
	
	protected MappingErogazionePortaApplicativa getMappingErogazione(IDServizio idServizio, IDPortaApplicativa idPortaApplicativa) throws DriverConfigurazioneException,DriverConfigurazioneNotFound {
		
		Connection con = null;
		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("getMappingErogazione");

			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getMappingErogazione] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		try {
			
			return DBMappingUtils.getMappingErogazione(idServizio, idPortaApplicativa, con, this.driver.tipoDB);

		} catch (Exception se) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getMappingErogazione] Exception: " + se.getMessage(),se);
		} finally {
			
			this.driver.closeConnection(con);
		}
		
	}
	
	protected static List<String> normalizeConnettoriMultpliById(List<String> sa, PortaApplicativa pa){
		
		if(pa==null || pa.getBehaviour()==null || pa.sizeServizioApplicativoList()<=0) {
			return null;
		}
		
		List<String> lId = new ArrayList<>();
		Map<String, String> m = new HashMap<>();
		for (String s : sa) {
			for (PortaApplicativaServizioApplicativo pasa : pa.getServizioApplicativoList()) {
				if(pasa!=null && pasa.getNome()!=null && pasa.getNome().equals(s)) {
					if(pasa.getId()==null || pasa.getId()<=0) {
						return null;
					}
					long id = pasa.getId();
					String nomeConnettore = CostantiConfigurazione.NOME_CONNETTORE_DEFAULT;
					if(pasa!=null && pasa.getDatiConnettore()!=null && pasa.getDatiConnettore().getNome()!=null) {
						nomeConnettore = pasa.getDatiConnettore().getNome();
					}
					lId.add(id+"");
					m.put(id+"",nomeConnettore);
					break;
				}
			}
		}
		
		List<String> sorted = new ArrayList<>();
		Collections.sort(lId);
		for (String sId : lId) {
			sorted.add(m.get(sId));
		}
		
		return sorted;
	}
}
