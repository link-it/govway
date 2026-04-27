/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2026 Link.it srl (https://link.it). 
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

import org.openspcoop2.core.allarmi.Allarme;
import org.openspcoop2.core.allarmi.constants.RuoloPorta;
import org.openspcoop2.core.allarmi.constants.StatoAllarme;
import org.openspcoop2.core.allarmi.utils.AllarmiConverterUtils;
import org.openspcoop2.core.allarmi.utils.AllarmiDriverUtils;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.config.driver.DriverConfigurazioneException;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.utils.jdbc.JDBCUtilities;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.SQLObjectFactory;
import org.openspcoop2.utils.sql.SQLQueryObjectException;

/**
 * DriverConfigurazioneDB_allarmiDriver
 * 
 * 
 * @author Sandra Giangrandi (sandra@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DriverConfigurazioneDB_allarmiDriver {

	private DriverConfigurazioneDB driver = null;
	
	protected DriverConfigurazioneDB_allarmiDriver(DriverConfigurazioneDB driver) {
		this.driver = driver;
	}
	
	protected long countAllarmi(String tipologiaRicerca, Boolean enabled, StatoAllarme stato, Boolean acknowledged, String nomeAllarme,
			List<IDSoggetto> listSoggettiProprietariAbilitati, List<IDServizio> listIDServizioAbilitati,
			List<String> tipoSoggettiByProtocollo, List<String> tipoServiziByProtocollo, 
			IDSoggetto idSoggettoProprietario, List<IDServizio> listIDServizio) throws DriverConfigurazioneException {
		
		Connection con = null;
		ResultSet rs = null;
		PreparedStatement stm = null;
		
		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("countAllarmi");

			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::countAllarmi] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		try {
			
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.ALLARMI);
			sqlQueryObject.addSelectCountField(CostantiDB.ALLARMI + ".id", "numeroAllarmi");
			sqlQueryObject.setANDLogicOperator(true);
			
			setExpressionAllarmiEngine(sqlQueryObject, tipologiaRicerca, enabled, stato, acknowledged, nomeAllarme,
					listSoggettiProprietariAbilitati, listIDServizioAbilitati,
					tipoSoggettiByProtocollo, tipoServiziByProtocollo, 
					idSoggettoProprietario, listIDServizio);
			
			String sqlQuery = sqlQueryObject.createSQLQuery();

			stm = con.prepareStatement(sqlQuery);
			
			sqlQuery = setExpressionAllarmiValuesEngine(stm, sqlQuery,
					tipologiaRicerca, enabled, stato, acknowledged, nomeAllarme,
					listSoggettiProprietariAbilitati, listIDServizioAbilitati,
					tipoSoggettiByProtocollo, tipoServiziByProtocollo, 
					idSoggettoProprietario, listIDServizio);
			this.driver.logDebug("eseguo query: " + sqlQuery);
			
			rs = stm.executeQuery();
			long numeroAllarmi = 0;
			if(rs.next()) {
				
				 numeroAllarmi = rs.getLong("numeroAllarmi");
			}
			rs.close(); rs=null;
			stm.close(); stm = null;

			return numeroAllarmi;
			
		} catch (Exception se) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::countAllarmi] Exception: " + se.getMessage(),se);
		} finally {
			//Chiudo statement and resultset
			JDBCUtilities.closeResources(rs, stm);
			this.driver.closeConnection(con);
		}
		
	}
	
	protected List<Allarme> findAllAllarmi(String tipologiaRicerca, Boolean enabled, StatoAllarme stato, Boolean acknowledged, String nomeAllarme,
			List<IDSoggetto> listSoggettiProprietariAbilitati, List<IDServizio> listIDServizioAbilitati,
			List<String> tipoSoggettiByProtocollo, List<String> tipoServiziByProtocollo, 
			IDSoggetto idSoggettoProprietario, List<IDServizio> listIDServizio,
			Integer offset, Integer limit) throws DriverConfigurazioneException {
		
		Connection con = null;
		ResultSet rs = null;
		PreparedStatement stm = null;
		
		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("findAllAllarmi");

			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::findAllAllarmi] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		try {
			
			List<Allarme> list = new ArrayList<>();
			
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.ALLARMI);
			sqlQueryObject.addSelectField(CostantiDB.ALLARMI + ".id");
			sqlQueryObject.addSelectField(CostantiDB.ALLARMI + ".alias");
			sqlQueryObject.setANDLogicOperator(true);
			
			setExpressionAllarmiEngine(sqlQueryObject, tipologiaRicerca, enabled, stato, acknowledged, nomeAllarme,
					listSoggettiProprietariAbilitati, listIDServizioAbilitati,
					tipoSoggettiByProtocollo, tipoServiziByProtocollo, 
					idSoggettoProprietario, listIDServizio);
			
			sqlQueryObject.addOrderBy("alias");
			
			if(offset!=null) {
				sqlQueryObject.setOffset(offset);
			}
			if(limit!=null) {
				sqlQueryObject.setLimit(limit);
			}
			
			String sqlQuery = sqlQueryObject.createSQLQuery();

			stm = con.prepareStatement(sqlQuery);

			sqlQuery = setExpressionAllarmiValuesEngine(stm, sqlQuery,
					tipologiaRicerca, enabled, stato, acknowledged, nomeAllarme,
					listSoggettiProprietariAbilitati, listIDServizioAbilitati,
					tipoSoggettiByProtocollo, tipoServiziByProtocollo, 
					idSoggettoProprietario, listIDServizio);
			this.driver.logDebug("eseguo query: " + sqlQuery);
			
			rs = stm.executeQuery();
			while(rs.next()) {
				
				long idAllarme = rs.getLong("id");
				list.add(AllarmiDriverUtils.getAllarme(idAllarme, con, this.driver.log, this.driver.tipoDB));
				
			}
			rs.close(); rs=null;
			stm.close(); stm = null;

			return list;
			
		} catch (Exception se) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::findAllAllarmi] Exception: " + se.getMessage(),se);
		} finally {
			//Chiudo statement and resultset
			JDBCUtilities.closeResources(rs, stm);
			this.driver.closeConnection(con);
		}
		
	}
	
	private void setExpressionAllarmiEngine(ISQLQueryObject sqlQueryObject, String tipologiaRicerca, Boolean enabled, StatoAllarme stato, Boolean acknowledged, String nomeAllarme,
			List<IDSoggetto> listSoggettiProprietariAbilitati, List<IDServizio> listIDServizioAbilitati,
			List<String> tipoSoggettiByProtocollo, List<String> tipoServiziByProtocollo, 
			IDSoggetto idSoggettoProprietario, List<IDServizio> listIDServizio) throws SQLQueryObjectException {
		if(enabled!=null){
			sqlQueryObject.addWhereCondition("enabled=?");
		}
		if(stato!=null){
			sqlQueryObject.addWhereCondition("stato=?");
		}
		if(acknowledged!=null){
			sqlQueryObject.addWhereCondition("acknowledged=?");
		}
		
		if(nomeAllarme!=null) {
			sqlQueryObject.addWhereLikeCondition("alias", nomeAllarme, true, true);
		}
		
		ISQLQueryObject sqlQueryObjectPorteApplicative = null;
		ISQLQueryObject sqlQueryObjectPorteDelegate = null;
		
		if(CostantiConfigurazione.ALLARMI_TIPOLOGIA_CONFIGURAZIONE.equals(tipologiaRicerca)) {
			sqlQueryObject.addWhereIsNullCondition("filtro_porta");
		}
		
		if(CostantiConfigurazione.ALLARMI_TIPOLOGIA_APPLICATIVA.equals(tipologiaRicerca) || CostantiConfigurazione.ALLARMI_TIPOLOGIA_SOLO_ASSOCIATE.equals(tipologiaRicerca)) {
		
			String aliasPA = "pa";
			String aliasSOGGETTI = "sog";
			
			sqlQueryObjectPorteApplicative = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObjectPorteApplicative.addFromTable(CostantiDB.PORTE_APPLICATIVE,aliasPA);
			sqlQueryObjectPorteApplicative.setANDLogicOperator(true);
			sqlQueryObjectPorteApplicative.addWhereCondition(CostantiDB.ALLARMI+".filtro_ruolo=?");
			sqlQueryObjectPorteApplicative.addWhereCondition(CostantiDB.ALLARMI+".filtro_porta="+aliasPA+".nome_porta");
			sqlQueryObjectPorteApplicative.addWhereIsNotNullCondition(aliasPA+".tipo_servizio");
			sqlQueryObjectPorteApplicative.addWhereIsNotNullCondition(aliasPA+".servizio");
			sqlQueryObjectPorteApplicative.addWhereIsNotNullCondition(aliasPA+".versione_servizio");
			
			if( (listSoggettiProprietariAbilitati!=null && !listSoggettiProprietariAbilitati.isEmpty())
					||
				(listIDServizioAbilitati!=null && !listIDServizioAbilitati.isEmpty()) 
					||
				(tipoSoggettiByProtocollo!=null && !tipoSoggettiByProtocollo.isEmpty())
					||
				idSoggettoProprietario!=null
					||
				listIDServizio!=null && !listIDServizio.isEmpty()
					) {
				sqlQueryObjectPorteApplicative.addFromTable(CostantiDB.SOGGETTI,aliasSOGGETTI);
				sqlQueryObjectPorteApplicative.addWhereCondition(aliasPA+".id_soggetto = "+aliasSOGGETTI+".id");
			}
			
			
			
			// Utenza permessi
			
			String condizioneUtenzaSoggetti = null;
			if(listSoggettiProprietariAbilitati!=null && !listSoggettiProprietariAbilitati.isEmpty()) {
				ISQLQueryObject sqlQueryObjectUtenzaSoggetto = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObjectUtenzaSoggetto.setANDLogicOperator(true);
				List<String> condizioni = new ArrayList<>();
				for (@SuppressWarnings("unused") IDSoggetto idSoggetto : listSoggettiProprietariAbilitati) {
					ISQLQueryObject sqlQueryObjectSoggetto = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
					sqlQueryObjectSoggetto.setANDLogicOperator(true);
					sqlQueryObjectSoggetto.addWhereCondition(aliasSOGGETTI+".tipo_soggetto = ?");
					sqlQueryObjectSoggetto.addWhereCondition(aliasSOGGETTI+".nome_soggetto = ?");
					condizioni.add(sqlQueryObjectSoggetto.createSQLConditions());
				}
				sqlQueryObjectUtenzaSoggetto.addWhereCondition(false, condizioni.toArray(new String[condizioni.size()]));
				
				condizioneUtenzaSoggetti = sqlQueryObjectUtenzaSoggetto.createSQLConditions();
			}
			
			String condizioneUtenzaServizi = null;
			if(listIDServizioAbilitati!=null && !listIDServizioAbilitati.isEmpty()) {
				ISQLQueryObject sqlQueryObjectUtenzaServizio = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObjectUtenzaServizio.setANDLogicOperator(true);
				List<String> condizioni = new ArrayList<>();
				for (@SuppressWarnings("unused") IDServizio idServizio : listIDServizioAbilitati) {
					ISQLQueryObject sqlQueryObjectServizio = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
					sqlQueryObjectServizio.setANDLogicOperator(true);
					sqlQueryObjectServizio.addWhereCondition(aliasSOGGETTI+".tipo_soggetto = ?");
					sqlQueryObjectServizio.addWhereCondition(aliasSOGGETTI+".nome_soggetto = ?");
					sqlQueryObjectServizio.addWhereCondition(aliasPA+".tipo_servizio = ?");
					sqlQueryObjectServizio.addWhereCondition(aliasPA+".servizio = ?");
					sqlQueryObjectServizio.addWhereCondition(aliasPA+".versione_servizio = ?");
					condizioni.add(sqlQueryObjectServizio.createSQLConditions());
				}
				sqlQueryObjectUtenzaServizio.addWhereCondition(false, condizioni.toArray(new String[condizioni.size()]));
				
				condizioneUtenzaServizi = sqlQueryObjectUtenzaServizio.createSQLConditions();
			}
			
			if(condizioneUtenzaSoggetti!=null && condizioneUtenzaServizi!=null) {
				sqlQueryObjectPorteApplicative.addWhereCondition(true, condizioneUtenzaSoggetti, condizioneUtenzaServizi);
			}
			else if(condizioneUtenzaSoggetti!=null) {
				sqlQueryObjectPorteApplicative.addWhereCondition(true, condizioneUtenzaSoggetti);
			}
			else if(condizioneUtenzaServizi!=null) {
				sqlQueryObjectPorteApplicative.addWhereCondition(true, condizioneUtenzaServizi);
			}
		
			// protocollo
			if(tipoSoggettiByProtocollo!=null && !tipoSoggettiByProtocollo.isEmpty()) {
				sqlQueryObjectPorteApplicative.addWhereINCondition(aliasSOGGETTI+".tipo_soggetto", true, tipoSoggettiByProtocollo.toArray(new String[tipoSoggettiByProtocollo.size()]));
			}
			if(tipoServiziByProtocollo!=null && !tipoServiziByProtocollo.isEmpty()) {
				sqlQueryObjectPorteApplicative.addWhereINCondition(aliasPA+".tipo_servizio", true, tipoServiziByProtocollo.toArray(new String[tipoServiziByProtocollo.size()]));
			}
			
			// soggetto proprietario
			if(idSoggettoProprietario!=null) {
				sqlQueryObjectPorteApplicative.addWhereCondition(aliasSOGGETTI+".tipo_soggetto = ?");
				sqlQueryObjectPorteApplicative.addWhereCondition(aliasSOGGETTI+".nome_soggetto = ?");
			}
			
			// servizi
			if(listIDServizio!=null && !listIDServizio.isEmpty()) {
				
				if(listIDServizio.size()==1) {
					sqlQueryObjectPorteApplicative.addWhereCondition(aliasSOGGETTI+".tipo_soggetto = ?");
					sqlQueryObjectPorteApplicative.addWhereCondition(aliasSOGGETTI+".nome_soggetto = ?");
					sqlQueryObjectPorteApplicative.addWhereCondition(aliasPA+".tipo_servizio = ?");
					sqlQueryObjectPorteApplicative.addWhereCondition(aliasPA+".servizio = ?");
					sqlQueryObjectPorteApplicative.addWhereCondition(aliasPA+".versione_servizio = ?");
				}
				else {
					List<String> condizioni = new ArrayList<>();
					for (@SuppressWarnings("unused") IDServizio idServizio : listIDServizio) {
						ISQLQueryObject sqlQueryObjectServizio = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
						sqlQueryObjectServizio.setANDLogicOperator(true);
						sqlQueryObjectServizio.addWhereCondition(aliasSOGGETTI+".tipo_soggetto = ?");
						sqlQueryObjectServizio.addWhereCondition(aliasSOGGETTI+".nome_soggetto = ?");
						sqlQueryObjectServizio.addWhereCondition(aliasPA+".tipo_servizio = ?");
						sqlQueryObjectServizio.addWhereCondition(aliasPA+".servizio = ?");
						sqlQueryObjectServizio.addWhereCondition(aliasPA+".versione_servizio = ?");
						condizioni.add(sqlQueryObjectServizio.createSQLConditions());
					}
					sqlQueryObjectPorteApplicative.addWhereCondition(false, condizioni.toArray(new String[condizioni.size()]));
				}
				
			}
			
		}
		
		if(CostantiConfigurazione.ALLARMI_TIPOLOGIA_DELEGATA.equals(tipologiaRicerca) || CostantiConfigurazione.ALLARMI_TIPOLOGIA_SOLO_ASSOCIATE.equals(tipologiaRicerca)) {
			
			String aliasPD = "pd";
			String aliasSOGGETTI = "sog";
			
			sqlQueryObjectPorteDelegate = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObjectPorteDelegate.addFromTable(CostantiDB.PORTE_DELEGATE,aliasPD);
			sqlQueryObjectPorteDelegate.setANDLogicOperator(true);
			sqlQueryObjectPorteDelegate.addWhereCondition(CostantiDB.ALLARMI+".filtro_ruolo=?");
			sqlQueryObjectPorteDelegate.addWhereCondition(CostantiDB.ALLARMI+".filtro_porta="+aliasPD+".nome_porta");
			sqlQueryObjectPorteDelegate.addWhereIsNotNullCondition(aliasPD+".tipo_soggetto_erogatore");
			sqlQueryObjectPorteDelegate.addWhereIsNotNullCondition(aliasPD+".nome_soggetto_erogatore");
			sqlQueryObjectPorteDelegate.addWhereIsNotNullCondition(aliasPD+".tipo_servizio");
			sqlQueryObjectPorteDelegate.addWhereIsNotNullCondition(aliasPD+".nome_servizio");
			sqlQueryObjectPorteDelegate.addWhereIsNotNullCondition(aliasPD+".versione_servizio");
			
			if( (listSoggettiProprietariAbilitati!=null && !listSoggettiProprietariAbilitati.isEmpty())
					||
				(tipoSoggettiByProtocollo!=null && !tipoSoggettiByProtocollo.isEmpty())
					||
				(tipoServiziByProtocollo!=null && !tipoServiziByProtocollo.isEmpty())
					||
				idSoggettoProprietario!=null
					) {
				sqlQueryObjectPorteDelegate.addFromTable(CostantiDB.SOGGETTI,aliasSOGGETTI);
				sqlQueryObjectPorteDelegate.addWhereCondition(aliasPD+".id_soggetto = "+aliasSOGGETTI+".id");
			}
			
			
			
			// Utenza permessi
			
			String condizioneUtenzaSoggetti = null;
			if(listSoggettiProprietariAbilitati!=null && !listSoggettiProprietariAbilitati.isEmpty()) {
				ISQLQueryObject sqlQueryObjectUtenzaSoggetto = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObjectUtenzaSoggetto.setANDLogicOperator(true);
				List<String> condizioni = new ArrayList<>();
				for (@SuppressWarnings("unused") IDSoggetto idSoggetto : listSoggettiProprietariAbilitati) {
					ISQLQueryObject sqlQueryObjectSoggetto = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
					sqlQueryObjectSoggetto.setANDLogicOperator(true);
					sqlQueryObjectSoggetto.addWhereCondition(aliasSOGGETTI+".tipo_soggetto = ?");
					sqlQueryObjectSoggetto.addWhereCondition(aliasSOGGETTI+".nome_soggetto = ?");
					condizioni.add(sqlQueryObjectSoggetto.createSQLConditions());
				}
				sqlQueryObjectUtenzaSoggetto.addWhereCondition(false, condizioni.toArray(new String[condizioni.size()]));
				
				condizioneUtenzaSoggetti = sqlQueryObjectUtenzaSoggetto.createSQLConditions();
			}
			
			String condizioneUtenzaServizi = null;
			if(listIDServizioAbilitati!=null && !listIDServizioAbilitati.isEmpty()) {
				ISQLQueryObject sqlQueryObjectUtenzaServizio = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObjectUtenzaServizio.setANDLogicOperator(true);
				List<String> condizioni = new ArrayList<>();
				for (@SuppressWarnings("unused") IDServizio idServizio : listIDServizioAbilitati) {
					ISQLQueryObject sqlQueryObjectServizio = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
					sqlQueryObjectServizio.setANDLogicOperator(true);
					sqlQueryObjectServizio.addWhereCondition(aliasPD+".tipo_soggetto_erogatore = ?");
					sqlQueryObjectServizio.addWhereCondition(aliasPD+".nome_soggetto_erogatore = ?");
					sqlQueryObjectServizio.addWhereCondition(aliasPD+".tipo_servizio = ?");
					sqlQueryObjectServizio.addWhereCondition(aliasPD+".nome_servizio = ?");
					sqlQueryObjectServizio.addWhereCondition(aliasPD+".versione_servizio = ?");
					condizioni.add(sqlQueryObjectServizio.createSQLConditions());
				}
				sqlQueryObjectUtenzaServizio.addWhereCondition(false, condizioni.toArray(new String[condizioni.size()]));
				
				condizioneUtenzaServizi = sqlQueryObjectUtenzaServizio.createSQLConditions();
			}
			
			if(condizioneUtenzaSoggetti!=null && condizioneUtenzaServizi!=null) {
				sqlQueryObjectPorteDelegate.addWhereCondition(true, condizioneUtenzaSoggetti, condizioneUtenzaServizi);
			}
			else if(condizioneUtenzaSoggetti!=null) {
				sqlQueryObjectPorteDelegate.addWhereCondition(true, condizioneUtenzaSoggetti);
			}
			else if(condizioneUtenzaServizi!=null) {
				sqlQueryObjectPorteDelegate.addWhereCondition(true, condizioneUtenzaServizi);
			}
		
			// protocollo
			if(tipoSoggettiByProtocollo!=null && !tipoSoggettiByProtocollo.isEmpty()) {
				sqlQueryObjectPorteDelegate.addWhereINCondition(aliasSOGGETTI+".tipo_soggetto", true, tipoSoggettiByProtocollo.toArray(new String[tipoSoggettiByProtocollo.size()]));
				sqlQueryObjectPorteDelegate.addWhereINCondition(aliasPD+".tipo_soggetto_erogatore", true, tipoSoggettiByProtocollo.toArray(new String[tipoSoggettiByProtocollo.size()]));
			}
			if(tipoServiziByProtocollo!=null && !tipoServiziByProtocollo.isEmpty()) {
				sqlQueryObjectPorteDelegate.addWhereINCondition(aliasPD+".tipo_servizio", true, tipoServiziByProtocollo.toArray(new String[tipoServiziByProtocollo.size()]));
			}
			
			// soggetto proprietario
			if(idSoggettoProprietario!=null) {
				sqlQueryObjectPorteDelegate.addWhereCondition(aliasSOGGETTI+".tipo_soggetto = ?");
				sqlQueryObjectPorteDelegate.addWhereCondition(aliasSOGGETTI+".nome_soggetto = ?");
			}
			
			// servizi
			if(listIDServizio!=null && !listIDServizio.isEmpty()) {
				
				if(listIDServizio.size()==1) {
					sqlQueryObjectPorteDelegate.addWhereCondition(aliasPD+".tipo_soggetto_erogatore = ?");
					sqlQueryObjectPorteDelegate.addWhereCondition(aliasPD+".nome_soggetto_erogatore = ?");
					sqlQueryObjectPorteDelegate.addWhereCondition(aliasPD+".tipo_servizio = ?");
					sqlQueryObjectPorteDelegate.addWhereCondition(aliasPD+".nome_servizio = ?");
					sqlQueryObjectPorteDelegate.addWhereCondition(aliasPD+".versione_servizio = ?");
				}
				else {
					List<String> condizioni = new ArrayList<>();
					for (@SuppressWarnings("unused") IDServizio idServizio : listIDServizio) {
						ISQLQueryObject sqlQueryObjectServizio = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
						sqlQueryObjectServizio.setANDLogicOperator(true);
						sqlQueryObjectServizio.addWhereCondition(aliasPD+".tipo_soggetto_erogatore = ?");
						sqlQueryObjectServizio.addWhereCondition(aliasPD+".nome_soggetto_erogatore = ?");
						sqlQueryObjectServizio.addWhereCondition(aliasPD+".tipo_servizio = ?");
						sqlQueryObjectServizio.addWhereCondition(aliasPD+".nome_servizio = ?");
						sqlQueryObjectServizio.addWhereCondition(aliasPD+".versione_servizio = ?");
						condizioni.add(sqlQueryObjectServizio.createSQLConditions());
					}
					sqlQueryObjectPorteDelegate.addWhereCondition(false, condizioni.toArray(new String[condizioni.size()]));
				}
				
			}
			
		}
		
		if(sqlQueryObjectPorteApplicative!=null && sqlQueryObjectPorteDelegate!=null) {
			ISQLQueryObject sqlQueryObjectOR = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObjectOR.setANDLogicOperator(false);
			sqlQueryObjectOR.addWhereExistsCondition(false, sqlQueryObjectPorteApplicative);
			sqlQueryObjectOR.addWhereExistsCondition(false, sqlQueryObjectPorteDelegate);
			sqlQueryObject.addWhereCondition(sqlQueryObjectOR.createSQLConditions());
		}
		else if(sqlQueryObjectPorteApplicative!=null) {
			sqlQueryObject.addWhereExistsCondition(false, sqlQueryObjectPorteApplicative);
		}
		else if(sqlQueryObjectPorteDelegate!=null) {
			sqlQueryObject.addWhereExistsCondition(false, sqlQueryObjectPorteDelegate);
		}
		
	}
	private String setExpressionAllarmiValuesEngine(PreparedStatement stm, String query, 
			String tipologiaRicerca,Boolean enabled, StatoAllarme stato, Boolean acknowledged, String nomeAllarme,
			List<IDSoggetto> listSoggettiProprietariAbilitati, List<IDServizio> listIDServizioAbilitati,
			List<String> tipoSoggettiByProtocollo, List<String> tipoServiziByProtocollo, 
			IDSoggetto idSoggettoProprietario, List<IDServizio> listIDServizio) throws SQLException {
		
		if(tipoSoggettiByProtocollo!=null && tipoServiziByProtocollo!=null) {
			// nop
		}
		
		int index = 1;
		if(enabled!=null){
			int v = enabled? CostantiDB.TRUE : CostantiDB.FALSE;
			stm.setInt(index++, v);
			query = query.replaceFirst("\\?", v+"");
		}
		if(stato!=null){
			int v = AllarmiConverterUtils.toIntegerValue(stato);
			stm.setInt(index++,v);
			query = query.replaceFirst("\\?", v+"");
		}
		if(acknowledged!=null){
			int v = acknowledged? CostantiDB.TRUE : CostantiDB.FALSE;
			stm.setInt(index++, v);
			query = query.replaceFirst("\\?", v+"");
		}
				
		if(nomeAllarme!=null) {
			// nop
		}
		
/**		if(CostantiConfigurazione.ALLARMI_TIPOLOGIA_CONFIGURAZIONE.equals(tipologiaRicerca)) {
//			// nop
//		}*/
		
		if(CostantiConfigurazione.ALLARMI_TIPOLOGIA_APPLICATIVA.equals(tipologiaRicerca) || CostantiConfigurazione.ALLARMI_TIPOLOGIA_SOLO_ASSOCIATE.equals(tipologiaRicerca)) {
			
			stm.setString(index++, RuoloPorta.APPLICATIVA.getValue()); query = query.replaceFirst("\\?", "'"+RuoloPorta.APPLICATIVA.getValue()+"'");
						
			// Utenza permessi

			if(listSoggettiProprietariAbilitati!=null && !listSoggettiProprietariAbilitati.isEmpty()) {
				for (IDSoggetto idSoggetto : listSoggettiProprietariAbilitati) {
					stm.setString(index++, idSoggetto.getTipo()); query = query.replaceFirst("\\?", "'"+idSoggetto.getTipo()+"'");
					stm.setString(index++, idSoggetto.getNome()); query = query.replaceFirst("\\?", "'"+idSoggetto.getNome()+"'");
				}
			}
			if(listIDServizioAbilitati!=null && !listIDServizioAbilitati.isEmpty()) {
				for (IDServizio idServizio : listIDServizioAbilitati) {
					stm.setString(index++, idServizio.getSoggettoErogatore().getTipo()); query = query.replaceFirst("\\?", "'"+idServizio.getSoggettoErogatore().getTipo()+"'");
					stm.setString(index++, idServizio.getSoggettoErogatore().getNome()); query = query.replaceFirst("\\?", "'"+idServizio.getSoggettoErogatore().getNome()+"'");
					stm.setString(index++, idServizio.getTipo()); query = query.replaceFirst("\\?", "'"+idServizio.getTipo()+"'");
					stm.setString(index++, idServizio.getNome()); query = query.replaceFirst("\\?", "'"+idServizio.getNome()+"'");
					stm.setInt(index++, idServizio.getVersione()); query = query.replaceFirst("\\?", idServizio.getVersione()+"");
				}
			}

			// protocollo
			// nop
			
			// soggetto proprietario
			if(idSoggettoProprietario!=null) {
				stm.setString(index++, idSoggettoProprietario.getTipo()); query = query.replaceFirst("\\?", "'"+idSoggettoProprietario.getTipo()+"'");
				stm.setString(index++, idSoggettoProprietario.getNome()); query = query.replaceFirst("\\?", "'"+idSoggettoProprietario.getNome()+"'");
			}
			
			// servizi
			if(listIDServizio!=null && !listIDServizio.isEmpty()) {
				
				if(listIDServizio.size()==1) {
					IDServizio idServizio = listIDServizio.get(0);
					stm.setString(index++, idServizio.getSoggettoErogatore().getTipo()); query = query.replaceFirst("\\?", "'"+idServizio.getSoggettoErogatore().getTipo()+"'");
					stm.setString(index++, idServizio.getSoggettoErogatore().getNome()); query = query.replaceFirst("\\?", "'"+idServizio.getSoggettoErogatore().getNome()+"'");
					stm.setString(index++, idServizio.getTipo()); query = query.replaceFirst("\\?", "'"+idServizio.getTipo()+"'");
					stm.setString(index++, idServizio.getNome()); query = query.replaceFirst("\\?", "'"+idServizio.getNome()+"'");
					stm.setInt(index++, idServizio.getVersione()); query = query.replaceFirst("\\?", idServizio.getVersione()+"");
				}
				else {
					for (IDServizio idServizio : listIDServizio) {
						stm.setString(index++, idServizio.getSoggettoErogatore().getTipo()); query = query.replaceFirst("\\?", "'"+idServizio.getSoggettoErogatore().getTipo()+"'");
						stm.setString(index++, idServizio.getSoggettoErogatore().getNome()); query = query.replaceFirst("\\?", "'"+idServizio.getSoggettoErogatore().getNome()+"'");
						stm.setString(index++, idServizio.getTipo()); query = query.replaceFirst("\\?", "'"+idServizio.getTipo()+"'");
						stm.setString(index++, idServizio.getNome()); query = query.replaceFirst("\\?", "'"+idServizio.getNome()+"'");
						stm.setInt(index++, idServizio.getVersione()); query = query.replaceFirst("\\?", idServizio.getVersione()+"");
					}
				}
				
			}
			
		}
		
		if(CostantiConfigurazione.ALLARMI_TIPOLOGIA_DELEGATA.equals(tipologiaRicerca) || CostantiConfigurazione.ALLARMI_TIPOLOGIA_SOLO_ASSOCIATE.equals(tipologiaRicerca)) {
			
			stm.setString(index++, RuoloPorta.DELEGATA.getValue()); query = query.replaceFirst("\\?", "'"+RuoloPorta.DELEGATA.getValue()+"'");
			
			// Utenza permessi
			
			if(listSoggettiProprietariAbilitati!=null && !listSoggettiProprietariAbilitati.isEmpty()) {
				for (IDSoggetto idSoggetto : listSoggettiProprietariAbilitati) {
					stm.setString(index++, idSoggetto.getTipo()); query = query.replaceFirst("\\?", "'"+idSoggetto.getTipo()+"'");
					stm.setString(index++, idSoggetto.getNome()); query = query.replaceFirst("\\?", "'"+idSoggetto.getNome()+"'");
				}
			}
			if(listIDServizioAbilitati!=null && !listIDServizioAbilitati.isEmpty()) {
				for (IDServizio idServizio : listIDServizioAbilitati) {
					stm.setString(index++, idServizio.getSoggettoErogatore().getTipo()); query = query.replaceFirst("\\?", "'"+idServizio.getSoggettoErogatore().getTipo()+"'");
					stm.setString(index++, idServizio.getSoggettoErogatore().getNome()); query = query.replaceFirst("\\?", "'"+idServizio.getSoggettoErogatore().getNome()+"'");
					stm.setString(index++, idServizio.getTipo()); query = query.replaceFirst("\\?", "'"+idServizio.getTipo()+"'");
					stm.setString(index++, idServizio.getNome()); query = query.replaceFirst("\\?", "'"+idServizio.getNome()+"'");
					stm.setInt(index++, idServizio.getVersione()); query = query.replaceFirst("\\?", idServizio.getVersione()+"");
				}
			}
			
			// protocollo
			// nop
			
			// soggetto proprietario
			if(idSoggettoProprietario!=null) {
				stm.setString(index++, idSoggettoProprietario.getTipo()); query = query.replaceFirst("\\?", "'"+idSoggettoProprietario.getTipo()+"'");
				stm.setString(index++, idSoggettoProprietario.getNome()); query = query.replaceFirst("\\?", "'"+idSoggettoProprietario.getNome()+"'");
			}
			
			// servizi
			if(listIDServizio!=null && !listIDServizio.isEmpty()) {
				
				if(listIDServizio.size()==1) {
					IDServizio idServizio = listIDServizio.get(0);
					stm.setString(index++, idServizio.getSoggettoErogatore().getTipo()); query = query.replaceFirst("\\?", "'"+idServizio.getSoggettoErogatore().getTipo()+"'");
					stm.setString(index++, idServizio.getSoggettoErogatore().getNome()); query = query.replaceFirst("\\?", "'"+idServizio.getSoggettoErogatore().getNome()+"'");
					stm.setString(index++, idServizio.getTipo()); query = query.replaceFirst("\\?", "'"+idServizio.getTipo()+"'");
					stm.setString(index++, idServizio.getNome()); query = query.replaceFirst("\\?", "'"+idServizio.getNome()+"'");
					stm.setInt(index, idServizio.getVersione()); query = query.replaceFirst("\\?", idServizio.getVersione()+"");
				}
				else {
					for (IDServizio idServizio : listIDServizio) {
						stm.setString(index++, idServizio.getSoggettoErogatore().getTipo()); query = query.replaceFirst("\\?", "'"+idServizio.getSoggettoErogatore().getTipo()+"'");
						stm.setString(index++, idServizio.getSoggettoErogatore().getNome()); query = query.replaceFirst("\\?", "'"+idServizio.getSoggettoErogatore().getNome()+"'");
						stm.setString(index++, idServizio.getTipo()); query = query.replaceFirst("\\?", "'"+idServizio.getTipo()+"'");
						stm.setString(index++, idServizio.getNome()); query = query.replaceFirst("\\?", "'"+idServizio.getNome()+"'");
						stm.setInt(index++, idServizio.getVersione()); query = query.replaceFirst("\\?", idServizio.getVersione()+"");
					}
				}
				
			}
			
		}
		
		return query;
	}
}
