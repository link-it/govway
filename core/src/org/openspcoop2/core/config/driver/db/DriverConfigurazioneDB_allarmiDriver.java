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
import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.core.allarmi.Allarme;
import org.openspcoop2.core.allarmi.constants.RuoloPorta;
import org.openspcoop2.core.allarmi.constants.StatoAllarme;
import org.openspcoop2.core.allarmi.utils.AllarmiConverterUtils;
import org.openspcoop2.core.allarmi.utils.AllarmiDriverUtils;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.config.driver.DriverConfigurazioneException;
import org.openspcoop2.core.config.driver.DriverConfigurazioneNotFound;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.utils.jdbc.JDBCUtilities;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.SQLObjectFactory;

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
			IDSoggetto idSoggettoProprietario, List<IDServizio> listIDServizio) throws DriverConfigurazioneException,DriverConfigurazioneNotFound {
		
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
			
			_setExpressionAllarmi(sqlQueryObject, tipologiaRicerca, enabled, stato, acknowledged, nomeAllarme,
					listSoggettiProprietariAbilitati, listIDServizioAbilitati,
					tipoSoggettiByProtocollo, tipoServiziByProtocollo, 
					idSoggettoProprietario, listIDServizio);
			
			String sqlQuery = sqlQueryObject.createSQLQuery();

			stm = con.prepareStatement(sqlQuery);
			
			sqlQuery = _setExpressionAllarmiValues(stm, sqlQuery,
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
			Integer offset, Integer limit) throws DriverConfigurazioneException,DriverConfigurazioneNotFound {
		
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
			
			List<Allarme> list = new ArrayList<Allarme>();
			
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.ALLARMI);
			sqlQueryObject.addSelectField(CostantiDB.ALLARMI + ".id");
			sqlQueryObject.addSelectField(CostantiDB.ALLARMI + ".alias");
			sqlQueryObject.setANDLogicOperator(true);
			
			_setExpressionAllarmi(sqlQueryObject, tipologiaRicerca, enabled, stato, acknowledged, nomeAllarme,
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

			sqlQuery = _setExpressionAllarmiValues(stm, sqlQuery,
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
	
	private void _setExpressionAllarmi(ISQLQueryObject sqlQueryObject, String tipologiaRicerca, Boolean enabled, StatoAllarme stato, Boolean acknowledged, String nomeAllarme,
			List<IDSoggetto> listSoggettiProprietariAbilitati, List<IDServizio> listIDServizioAbilitati,
			List<String> tipoSoggettiByProtocollo, List<String> tipoServiziByProtocollo, 
			IDSoggetto idSoggettoProprietario, List<IDServizio> listIDServizio) throws Exception {
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
		
			String alias_PA = "pa";
			String alias_SOGGETTI = "sog";
			
			sqlQueryObjectPorteApplicative = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObjectPorteApplicative.addFromTable(CostantiDB.PORTE_APPLICATIVE,alias_PA);
			sqlQueryObjectPorteApplicative.setANDLogicOperator(true);
			sqlQueryObjectPorteApplicative.addWhereCondition(CostantiDB.ALLARMI+".filtro_ruolo=?");
			sqlQueryObjectPorteApplicative.addWhereCondition(CostantiDB.ALLARMI+".filtro_porta="+alias_PA+".nome_porta");
			sqlQueryObjectPorteApplicative.addWhereIsNotNullCondition(alias_PA+".tipo_servizio");
			sqlQueryObjectPorteApplicative.addWhereIsNotNullCondition(alias_PA+".servizio");
			sqlQueryObjectPorteApplicative.addWhereIsNotNullCondition(alias_PA+".versione_servizio");
			
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
				sqlQueryObjectPorteApplicative.addFromTable(CostantiDB.SOGGETTI,alias_SOGGETTI);
				sqlQueryObjectPorteApplicative.addWhereCondition(alias_PA+".id_soggetto = "+alias_SOGGETTI+".id");
			}
			
			
			
			// Utenza permessi
			
			List<String> condizioniUtenza = new ArrayList<>();
			
			if(listSoggettiProprietariAbilitati!=null && !listSoggettiProprietariAbilitati.isEmpty()) {
				ISQLQueryObject sqlQueryObjectUtenzaSoggetto = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObjectUtenzaSoggetto.setANDLogicOperator(true);
				List<String> condizioni = new ArrayList<>();
				for (@SuppressWarnings("unused") IDSoggetto idSoggetto : listSoggettiProprietariAbilitati) {
					ISQLQueryObject sqlQueryObjectSoggetto = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
					sqlQueryObjectSoggetto.setANDLogicOperator(true);
					sqlQueryObjectSoggetto.addWhereCondition(alias_SOGGETTI+".tipo_soggetto = ?");
					sqlQueryObjectSoggetto.addWhereCondition(alias_SOGGETTI+".nome_soggetto = ?");
					condizioni.add(sqlQueryObjectSoggetto.createSQLConditions());
				}
				sqlQueryObjectUtenzaSoggetto.addWhereCondition(false, condizioni.toArray(new String[condizioni.size()]));
				
				condizioniUtenza.add(sqlQueryObjectUtenzaSoggetto.createSQLConditions());
			}
			if(listIDServizioAbilitati!=null && !listIDServizioAbilitati.isEmpty()) {
				ISQLQueryObject sqlQueryObjectUtenzaServizio = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObjectUtenzaServizio.setANDLogicOperator(true);
				List<String> condizioni = new ArrayList<>();
				for (@SuppressWarnings("unused") IDServizio idServizio : listIDServizioAbilitati) {
					ISQLQueryObject sqlQueryObjectServizio = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
					sqlQueryObjectServizio.setANDLogicOperator(true);
					sqlQueryObjectServizio.addWhereCondition(alias_SOGGETTI+".tipo_soggetto = ?");
					sqlQueryObjectServizio.addWhereCondition(alias_SOGGETTI+".nome_soggetto = ?");
					sqlQueryObjectServizio.addWhereCondition(alias_PA+".tipo_servizio = ?");
					sqlQueryObjectServizio.addWhereCondition(alias_PA+".servizio = ?");
					sqlQueryObjectServizio.addWhereCondition(alias_PA+".versione_servizio = ?");
					condizioni.add(sqlQueryObjectServizio.createSQLConditions());
				}
				sqlQueryObjectUtenzaServizio.addWhereCondition(false, condizioni.toArray(new String[condizioni.size()]));
				
				condizioniUtenza.add(sqlQueryObjectUtenzaServizio.createSQLConditions());
			}
			
			if(!condizioniUtenza.isEmpty()) {
				sqlQueryObjectPorteApplicative.addWhereCondition(false, condizioniUtenza.toArray(new String[condizioniUtenza.size()]));
			}
		
			// protocollo
			if(tipoSoggettiByProtocollo!=null && !tipoSoggettiByProtocollo.isEmpty()) {
				sqlQueryObjectPorteApplicative.addWhereINCondition(alias_SOGGETTI+".tipo_soggetto", true, tipoSoggettiByProtocollo.toArray(new String[tipoSoggettiByProtocollo.size()]));
			}
			if(tipoServiziByProtocollo!=null && !tipoServiziByProtocollo.isEmpty()) {
				sqlQueryObjectPorteApplicative.addWhereINCondition(alias_PA+".tipo_servizio", true, tipoServiziByProtocollo.toArray(new String[tipoServiziByProtocollo.size()]));
			}
			
			// soggetto proprietario
			if(idSoggettoProprietario!=null) {
				sqlQueryObjectPorteApplicative.addWhereCondition(alias_SOGGETTI+".tipo_soggetto = ?");
				sqlQueryObjectPorteApplicative.addWhereCondition(alias_SOGGETTI+".nome_soggetto = ?");
			}
			
			// servizi
			if(listIDServizio!=null && !listIDServizio.isEmpty()) {
				
				if(listIDServizio.size()==1) {
					sqlQueryObjectPorteApplicative.addWhereCondition(alias_SOGGETTI+".tipo_soggetto = ?");
					sqlQueryObjectPorteApplicative.addWhereCondition(alias_SOGGETTI+".nome_soggetto = ?");
					sqlQueryObjectPorteApplicative.addWhereCondition(alias_PA+".tipo_servizio = ?");
					sqlQueryObjectPorteApplicative.addWhereCondition(alias_PA+".servizio = ?");
					sqlQueryObjectPorteApplicative.addWhereCondition(alias_PA+".versione_servizio = ?");
				}
				else {
					List<String> condizioni = new ArrayList<>();
					for (@SuppressWarnings("unused") IDServizio idServizio : listIDServizio) {
						ISQLQueryObject sqlQueryObjectServizio = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
						sqlQueryObjectServizio.setANDLogicOperator(true);
						sqlQueryObjectServizio.addWhereCondition(alias_SOGGETTI+".tipo_soggetto = ?");
						sqlQueryObjectServizio.addWhereCondition(alias_SOGGETTI+".nome_soggetto = ?");
						sqlQueryObjectServizio.addWhereCondition(alias_PA+".tipo_servizio = ?");
						sqlQueryObjectServizio.addWhereCondition(alias_PA+".servizio = ?");
						sqlQueryObjectServizio.addWhereCondition(alias_PA+".versione_servizio = ?");
						condizioni.add(sqlQueryObjectServizio.createSQLConditions());
					}
					sqlQueryObjectPorteApplicative.addWhereCondition(false, condizioni.toArray(new String[condizioni.size()]));
				}
				
			}
			
		}
		
		if(CostantiConfigurazione.ALLARMI_TIPOLOGIA_DELEGATA.equals(tipologiaRicerca) || CostantiConfigurazione.ALLARMI_TIPOLOGIA_SOLO_ASSOCIATE.equals(tipologiaRicerca)) {
			
			String alias_PD = "pd";
			String alias_SOGGETTI = "sog";
			
			sqlQueryObjectPorteDelegate = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObjectPorteDelegate.addFromTable(CostantiDB.PORTE_DELEGATE,alias_PD);
			sqlQueryObjectPorteDelegate.setANDLogicOperator(true);
			sqlQueryObjectPorteDelegate.addWhereCondition(CostantiDB.ALLARMI+".filtro_ruolo=?");
			sqlQueryObjectPorteDelegate.addWhereCondition(CostantiDB.ALLARMI+".filtro_porta="+alias_PD+".nome_porta");
			sqlQueryObjectPorteDelegate.addWhereIsNotNullCondition(alias_PD+".tipo_soggetto_erogatore");
			sqlQueryObjectPorteDelegate.addWhereIsNotNullCondition(alias_PD+".nome_soggetto_erogatore");
			sqlQueryObjectPorteDelegate.addWhereIsNotNullCondition(alias_PD+".tipo_servizio");
			sqlQueryObjectPorteDelegate.addWhereIsNotNullCondition(alias_PD+".nome_servizio");
			sqlQueryObjectPorteDelegate.addWhereIsNotNullCondition(alias_PD+".versione_servizio");
			
			if( (listSoggettiProprietariAbilitati!=null && !listSoggettiProprietariAbilitati.isEmpty())
					||
				(tipoSoggettiByProtocollo!=null && !tipoSoggettiByProtocollo.isEmpty())
					||
				(tipoServiziByProtocollo!=null && !tipoServiziByProtocollo.isEmpty())
					||
				idSoggettoProprietario!=null
					) {
				sqlQueryObjectPorteDelegate.addFromTable(CostantiDB.SOGGETTI,alias_SOGGETTI);
				sqlQueryObjectPorteDelegate.addWhereCondition(alias_PD+".id_soggetto = "+alias_SOGGETTI+".id");
			}
			
			
			
			// Utenza permessi
			
			List<String> condizioniUtenza = new ArrayList<>();
			
			if(listSoggettiProprietariAbilitati!=null && !listSoggettiProprietariAbilitati.isEmpty()) {
				ISQLQueryObject sqlQueryObjectUtenzaSoggetto = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObjectUtenzaSoggetto.setANDLogicOperator(true);
				List<String> condizioni = new ArrayList<>();
				for (@SuppressWarnings("unused") IDSoggetto idSoggetto : listSoggettiProprietariAbilitati) {
					ISQLQueryObject sqlQueryObjectSoggetto = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
					sqlQueryObjectSoggetto.setANDLogicOperator(true);
					sqlQueryObjectSoggetto.addWhereCondition(alias_SOGGETTI+".tipo_soggetto = ?");
					sqlQueryObjectSoggetto.addWhereCondition(alias_SOGGETTI+".nome_soggetto = ?");
					condizioni.add(sqlQueryObjectSoggetto.createSQLConditions());
				}
				sqlQueryObjectUtenzaSoggetto.addWhereCondition(false, condizioni.toArray(new String[condizioni.size()]));
				
				condizioniUtenza.add(sqlQueryObjectUtenzaSoggetto.createSQLConditions());
			}
			if(listIDServizioAbilitati!=null && !listIDServizioAbilitati.isEmpty()) {
				ISQLQueryObject sqlQueryObjectUtenzaServizio = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObjectUtenzaServizio.setANDLogicOperator(true);
				List<String> condizioni = new ArrayList<>();
				for (@SuppressWarnings("unused") IDServizio idServizio : listIDServizioAbilitati) {
					ISQLQueryObject sqlQueryObjectServizio = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
					sqlQueryObjectServizio.setANDLogicOperator(true);
					sqlQueryObjectServizio.addWhereCondition(alias_PD+".tipo_soggetto_erogatore = ?");
					sqlQueryObjectServizio.addWhereCondition(alias_PD+".nome_soggetto_erogatore = ?");
					sqlQueryObjectServizio.addWhereCondition(alias_PD+".tipo_servizio = ?");
					sqlQueryObjectServizio.addWhereCondition(alias_PD+".nome_servizio = ?");
					sqlQueryObjectServizio.addWhereCondition(alias_PD+".versione_servizio = ?");
					condizioni.add(sqlQueryObjectServizio.createSQLConditions());
				}
				sqlQueryObjectUtenzaServizio.addWhereCondition(false, condizioni.toArray(new String[condizioni.size()]));
				
				condizioniUtenza.add(sqlQueryObjectUtenzaServizio.createSQLConditions());
			}
			
			if(!condizioniUtenza.isEmpty()) {
				sqlQueryObjectPorteDelegate.addWhereCondition(false, condizioniUtenza.toArray(new String[condizioniUtenza.size()]));
			}
		
			// protocollo
			if(tipoSoggettiByProtocollo!=null && !tipoSoggettiByProtocollo.isEmpty()) {
				sqlQueryObjectPorteDelegate.addWhereINCondition(alias_SOGGETTI+".tipo_soggetto", true, tipoSoggettiByProtocollo.toArray(new String[tipoSoggettiByProtocollo.size()]));
				sqlQueryObjectPorteDelegate.addWhereINCondition(alias_PD+".tipo_soggetto_erogatore", true, tipoSoggettiByProtocollo.toArray(new String[tipoSoggettiByProtocollo.size()]));
			}
			if(tipoServiziByProtocollo!=null && !tipoServiziByProtocollo.isEmpty()) {
				sqlQueryObjectPorteDelegate.addWhereINCondition(alias_PD+".tipo_servizio", true, tipoServiziByProtocollo.toArray(new String[tipoServiziByProtocollo.size()]));
			}
			
			// soggetto proprietario
			if(idSoggettoProprietario!=null) {
				sqlQueryObjectPorteDelegate.addWhereCondition(alias_SOGGETTI+".tipo_soggetto = ?");
				sqlQueryObjectPorteDelegate.addWhereCondition(alias_SOGGETTI+".nome_soggetto = ?");
			}
			
			// servizi
			if(listIDServizio!=null && !listIDServizio.isEmpty()) {
				
				if(listIDServizio.size()==1) {
					sqlQueryObjectPorteDelegate.addWhereCondition(alias_PD+".tipo_soggetto_erogatore = ?");
					sqlQueryObjectPorteDelegate.addWhereCondition(alias_PD+".nome_soggetto_erogatore = ?");
					sqlQueryObjectPorteDelegate.addWhereCondition(alias_PD+".tipo_servizio = ?");
					sqlQueryObjectPorteDelegate.addWhereCondition(alias_PD+".nome_servizio = ?");
					sqlQueryObjectPorteDelegate.addWhereCondition(alias_PD+".versione_servizio = ?");
				}
				else {
					List<String> condizioni = new ArrayList<>();
					for (@SuppressWarnings("unused") IDServizio idServizio : listIDServizio) {
						ISQLQueryObject sqlQueryObjectServizio = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
						sqlQueryObjectServizio.setANDLogicOperator(true);
						sqlQueryObjectServizio.addWhereCondition(alias_PD+".tipo_soggetto_erogatore = ?");
						sqlQueryObjectServizio.addWhereCondition(alias_PD+".nome_soggetto_erogatore = ?");
						sqlQueryObjectServizio.addWhereCondition(alias_PD+".tipo_servizio = ?");
						sqlQueryObjectServizio.addWhereCondition(alias_PD+".nome_servizio = ?");
						sqlQueryObjectServizio.addWhereCondition(alias_PD+".versione_servizio = ?");
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
	private String _setExpressionAllarmiValues(PreparedStatement stm, String query, 
			String tipologiaRicerca,Boolean enabled, StatoAllarme stato, Boolean acknowledged, String nomeAllarme,
			List<IDSoggetto> listSoggettiProprietariAbilitati, List<IDServizio> listIDServizioAbilitati,
			List<String> tipoSoggettiByProtocollo, List<String> tipoServiziByProtocollo, 
			IDSoggetto idSoggettoProprietario, List<IDServizio> listIDServizio) throws Exception {
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
		
//		if(CostantiConfigurazione.ALLARMI_TIPOLOGIA_CONFIGURAZIONE.equals(tipologiaRicerca)) {
//			// nop
//		}
		
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
		
		return query;
	}
}
