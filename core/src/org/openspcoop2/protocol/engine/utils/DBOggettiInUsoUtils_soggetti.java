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

package org.openspcoop2.protocol.engine.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openspcoop2.core.commons.DBUtils;
import org.openspcoop2.core.commons.ErrorsHandlerCostant;
import org.openspcoop2.core.config.constants.RuoloTipoMatch;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDAccordoCooperazione;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.driver.IDAccordoCooperazioneFactory;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.jdbc.JDBCUtilities;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.SQLObjectFactory;

/**
 * DBOggettiInUsoUtils_soggetti
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class DBOggettiInUsoUtils_soggetti {

	protected static boolean isSoggettoConfigInUso(Connection con, String tipoDB, IDSoggetto idSoggettoConfig, boolean checkControlloTraffico, 
			Map<ErrorsHandlerCostant, List<String>> whereIsInUso, boolean normalizeObjectIds,
			boolean verificaRuoli) throws UtilsException {
		return isSoggettoInUso(con,tipoDB,idSoggettoConfig,null,checkControlloTraffico, whereIsInUso, normalizeObjectIds, verificaRuoli);
	}
	protected static boolean isSoggettoRegistryInUso(Connection con, String tipoDB, IDSoggetto idSoggettoRegistro, boolean checkControlloTraffico, 
			Map<ErrorsHandlerCostant, List<String>> whereIsInUso, boolean normalizeObjectIds,
			boolean verificaRuoli) throws UtilsException {
		return isSoggettoInUso(con,tipoDB,null,idSoggettoRegistro,checkControlloTraffico, whereIsInUso, normalizeObjectIds, verificaRuoli);
	}
	private static boolean isSoggettoInUso(Connection con, String tipoDB, IDSoggetto idSoggettoConfig, IDSoggetto idSoggettoRegistro, boolean checkControlloTraffico, 
			Map<ErrorsHandlerCostant, List<String>> whereIsInUso, boolean normalizeObjectIds,
			boolean verificaRuoli) throws UtilsException {
		String nomeMetodo = "isSoggettoInUso";

		PreparedStatement stmt = null;
		ResultSet risultato = null;
		String queryString;
		try {
			boolean isInUso = false;

			long idSoggetto = -1;
			String tipoSoggetto = null;
			String nomeSoggetto = null;
			if(idSoggettoRegistro!=null){
				tipoSoggetto = idSoggettoRegistro.getTipo();
				nomeSoggetto = idSoggettoRegistro.getNome();

			}else{
				tipoSoggetto = idSoggettoConfig.getTipo();
				nomeSoggetto = idSoggettoConfig.getNome();
			}
			idSoggetto = DBUtils.getIdSoggetto(nomeSoggetto, tipoSoggetto, con, tipoDB);
			if(idSoggetto<=0){
				throw new UtilsException("Soggetto con tipo["+tipoSoggetto+"] e nome["+nomeSoggetto+"] non trovato");
			}

			List<String> servizi_fruitori_list = whereIsInUso.get(ErrorsHandlerCostant.IS_FRUITORE);
			List<String> servizi_applicativi_list = whereIsInUso.get(ErrorsHandlerCostant.IN_USO_IN_SERVIZI_APPLICATIVI);
			List<String> servizi_list = whereIsInUso.get(ErrorsHandlerCostant.IN_USO_IN_SERVIZI);
			List<String> porte_delegate_list = whereIsInUso.get(ErrorsHandlerCostant.IN_USO_IN_PORTE_DELEGATE);
			List<String> porte_applicative_list = whereIsInUso.get(ErrorsHandlerCostant.IN_USO_IN_PORTE_APPLICATIVE);
			List<String> mappingErogazionePA_list = whereIsInUso.get(ErrorsHandlerCostant.IN_USO_IN_MAPPING_EROGAZIONE_PA);
			List<String> mappingFruizionePD_list = whereIsInUso.get(ErrorsHandlerCostant.IN_USO_IN_MAPPING_FRUIZIONE_PD);
			List<String> accordi_list = whereIsInUso.get(ErrorsHandlerCostant.IS_REFERENTE);
			List<String> accordi_coop_list = whereIsInUso.get(ErrorsHandlerCostant.IS_REFERENTE_COOPERAZIONE);
			List<String> partecipanti_list = whereIsInUso.get(ErrorsHandlerCostant.IS_PARTECIPANTE_COOPERAZIONE);
			List<String> utenti_list = whereIsInUso.get(ErrorsHandlerCostant.UTENTE);
			List<String> ct_list = whereIsInUso.get(ErrorsHandlerCostant.CONTROLLO_TRAFFICO);
			List<String> allarme_list = whereIsInUso.get(ErrorsHandlerCostant.ALLARMI);
			List<String> autorizzazionePA_mapping_list = whereIsInUso.get(ErrorsHandlerCostant.AUTORIZZAZIONE_MAPPING);
			List<String> autorizzazionePA_list = whereIsInUso.get(ErrorsHandlerCostant.AUTORIZZAZIONE);
			List<String> ruoliPA_mapping_list = whereIsInUso.get(ErrorsHandlerCostant.RUOLI_MAPPING);
			List<String> ruoliPA_list = whereIsInUso.get(ErrorsHandlerCostant.RUOLI);
			List<String> ruoliTokenPA_mapping_list = whereIsInUso.get(ErrorsHandlerCostant.RUOLI_TOKEN_MAPPING_PA);
			List<String> ruoliTokenPA_list = whereIsInUso.get(ErrorsHandlerCostant.RUOLI_TOKEN_PA);
			List<String> ruoliTokenPA_mapping_modi_list = whereIsInUso.get(ErrorsHandlerCostant.RUOLI_TOKEN_MAPPING_PA_MODI);
			List<String> ruoliTokenPA_modi_list = whereIsInUso.get(ErrorsHandlerCostant.RUOLI_TOKEN_PA_MODI);
			List<String> trasformazionePA_mapping_list = whereIsInUso.get(ErrorsHandlerCostant.TRASFORMAZIONE_MAPPING_PA);
			List<String> trasformazionePA_list = whereIsInUso.get(ErrorsHandlerCostant.TRASFORMAZIONE_PA);
			List<String> configurazioniProxyPass_list = whereIsInUso.get(ErrorsHandlerCostant.CONFIGURAZIONE_REGOLE_PROXY_PASS);

			if (servizi_fruitori_list == null) {
				servizi_fruitori_list = new ArrayList<>();
				whereIsInUso.put(ErrorsHandlerCostant.IS_FRUITORE, servizi_fruitori_list);
			}
			if (servizi_applicativi_list == null) {
				servizi_applicativi_list = new ArrayList<>();
				whereIsInUso.put(ErrorsHandlerCostant.IN_USO_IN_SERVIZI_APPLICATIVI, servizi_applicativi_list);
			}
			if (servizi_list == null) {
				servizi_list = new ArrayList<>();
				whereIsInUso.put(ErrorsHandlerCostant.IN_USO_IN_SERVIZI, servizi_list);
			}
			if (porte_delegate_list == null) {
				porte_delegate_list = new ArrayList<>();
				whereIsInUso.put(ErrorsHandlerCostant.IN_USO_IN_PORTE_DELEGATE, porte_delegate_list);
			}
			if (porte_applicative_list == null) {
				porte_applicative_list = new ArrayList<>();
				whereIsInUso.put(ErrorsHandlerCostant.IN_USO_IN_PORTE_APPLICATIVE, porte_applicative_list);
			}
			if (mappingErogazionePA_list == null) {
				mappingErogazionePA_list = new ArrayList<>();
				whereIsInUso.put(ErrorsHandlerCostant.IN_USO_IN_MAPPING_EROGAZIONE_PA, mappingErogazionePA_list);
			}
			if (mappingFruizionePD_list == null) {
				mappingFruizionePD_list = new ArrayList<>();
				whereIsInUso.put(ErrorsHandlerCostant.IN_USO_IN_MAPPING_FRUIZIONE_PD, mappingFruizionePD_list);
			}
			if (accordi_list == null) {
				accordi_list = new ArrayList<>();
				whereIsInUso.put(ErrorsHandlerCostant.IS_REFERENTE, accordi_list);
			}
			if (accordi_coop_list == null) {
				accordi_coop_list = new ArrayList<>();
				whereIsInUso.put(ErrorsHandlerCostant.IS_REFERENTE_COOPERAZIONE, accordi_coop_list);
			}
			if (partecipanti_list == null) {
				partecipanti_list = new ArrayList<>();
				whereIsInUso.put(ErrorsHandlerCostant.IS_PARTECIPANTE_COOPERAZIONE, partecipanti_list);
			}
			if (utenti_list == null) {
				utenti_list = new ArrayList<>();
				whereIsInUso.put(ErrorsHandlerCostant.UTENTE, utenti_list);
			}
			if (ct_list == null) {
				ct_list = new ArrayList<>();
				whereIsInUso.put(ErrorsHandlerCostant.CONTROLLO_TRAFFICO, ct_list);
			}
			if (allarme_list == null) {
				allarme_list = new ArrayList<>();
				whereIsInUso.put(ErrorsHandlerCostant.ALLARMI, allarme_list);
			}
			if (autorizzazionePA_mapping_list == null) {
				autorizzazionePA_mapping_list = new ArrayList<>();
				whereIsInUso.put(ErrorsHandlerCostant.AUTORIZZAZIONE_MAPPING, autorizzazionePA_mapping_list);
			}
			if (autorizzazionePA_list == null) {
				autorizzazionePA_list = new ArrayList<>();
				whereIsInUso.put(ErrorsHandlerCostant.AUTORIZZAZIONE, autorizzazionePA_list);
			}
			if (ruoliPA_mapping_list == null) {
				ruoliPA_mapping_list = new ArrayList<>();
				whereIsInUso.put(ErrorsHandlerCostant.RUOLI_MAPPING, ruoliPA_mapping_list);
			}
			if (ruoliPA_list == null) {
				ruoliPA_list = new ArrayList<>();
				whereIsInUso.put(ErrorsHandlerCostant.RUOLI, ruoliPA_list);
			}
			if (ruoliTokenPA_mapping_list == null) {
				ruoliTokenPA_mapping_list = new ArrayList<>();
				whereIsInUso.put(ErrorsHandlerCostant.RUOLI_TOKEN_MAPPING_PA, ruoliTokenPA_mapping_list);
			}
			if (ruoliTokenPA_list == null) {
				ruoliTokenPA_list = new ArrayList<>();
				whereIsInUso.put(ErrorsHandlerCostant.RUOLI_TOKEN_PA, ruoliTokenPA_list);
			}
			if (ruoliTokenPA_mapping_modi_list == null) {
				ruoliTokenPA_mapping_modi_list = new ArrayList<>();
				whereIsInUso.put(ErrorsHandlerCostant.RUOLI_TOKEN_MAPPING_PA_MODI, ruoliTokenPA_mapping_modi_list);
			}
			if (ruoliTokenPA_modi_list == null) {
				ruoliTokenPA_modi_list = new ArrayList<>();
				whereIsInUso.put(ErrorsHandlerCostant.RUOLI_TOKEN_PA_MODI, ruoliTokenPA_modi_list);
			}
			if (trasformazionePA_mapping_list == null) {
				trasformazionePA_mapping_list = new ArrayList<>();
				whereIsInUso.put(ErrorsHandlerCostant.TRASFORMAZIONE_MAPPING_PA, trasformazionePA_mapping_list);
			}
			if (trasformazionePA_list == null) {
				trasformazionePA_list = new ArrayList<>();
				whereIsInUso.put(ErrorsHandlerCostant.TRASFORMAZIONE_PA, trasformazionePA_list);
			}
			if (configurazioniProxyPass_list == null) {
				configurazioniProxyPass_list = new ArrayList<>();
				whereIsInUso.put(ErrorsHandlerCostant.CONFIGURAZIONE_REGOLE_PROXY_PASS, configurazioniProxyPass_list);
			}


			// Controllo che il soggetto non sia in uso nei servizi applicativi
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
			sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
			sqlQueryObject.addSelectField("tipo_soggetto");
			sqlQueryObject.addSelectField("nome_soggetto");
			sqlQueryObject.addSelectField(CostantiDB.SERVIZI_APPLICATIVI+".nome");
			sqlQueryObject.setANDLogicOperator(true);
			sqlQueryObject.setSelectDistinct(true);
			sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".id_soggetto = "+CostantiDB.SOGGETTI+".id");
			sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".id_soggetto = ?");
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idSoggetto);
			risultato = stmt.executeQuery();
			while (risultato.next()) {
				String tipo_soggetto = risultato.getString("tipo_soggetto");
				//String nome_soggetto = risultato.getString("nome_soggetto");
				String nome = risultato.getString("nome");
				// non serve, essendo di un soggetto specificoIDSoggetto idSoggettoProprietario = new IDSoggetto(tipo_soggetto, nome_soggetto);
				if(normalizeObjectIds) {
					String protocollo = ProtocolFactoryManager.getInstance().getProtocolByOrganizationType(tipo_soggetto);
					servizi_applicativi_list.add(DBOggettiInUsoUtils.getProtocolPrefix(protocollo)+nome);
							// non serve, essendo di un soggetto specifico +getSubjectSuffix(protocollo, idSoggettoProprietario));
				}
				else {
					servizi_applicativi_list.add(nome);
				}
				isInUso = true;
			}
			risultato.close();
			stmt.close();


			// controllo porte delegate sia per id che per tipo e nome
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE);
			sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
			sqlQueryObject.addSelectField(CostantiDB.PORTE_DELEGATE+".nome_porta");
			sqlQueryObject.setANDLogicOperator(true);
			sqlQueryObject.addWhereCondition(CostantiDB.PORTE_DELEGATE+".id_soggetto = "+CostantiDB.SOGGETTI+".id");
			sqlQueryObject.addWhereCondition(false, 
					"id_soggetto_erogatore = ?", 
					"(tipo_soggetto_erogatore = ? AND nome_soggetto_erogatore = ?)",
					"id_soggetto = ?");
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			// controllo se soggetto erogatore di porte delegate
			stmt.setLong(1, idSoggetto);
			stmt.setString(2, tipoSoggetto);
			stmt.setString(3, nomeSoggetto);
			// controllo se soggetto proprietario di porte delegate
			stmt.setLong(4, idSoggetto);
			risultato = stmt.executeQuery();
			while (risultato.next()) {
				String nome = risultato.getString("nome_porta");
				ResultPorta resultPorta = DBOggettiInUsoUtils.formatPortaDelegata(nome, tipoDB, con, normalizeObjectIds);
				if(resultPorta.mapping) {
					mappingFruizionePD_list.add(resultPorta.label);
				}
				else {
					porte_delegate_list.add(resultPorta.label);
				}
				isInUso = true;
			}
			risultato.close();
			stmt.close();



			// controllo se in uso in porte applicative come proprietario della porta o come soggetto virtuale
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE);
			sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
			sqlQueryObject.addSelectField(CostantiDB.PORTE_APPLICATIVE+".nome_porta");
			sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE+".id_soggetto = "+CostantiDB.SOGGETTI+".id");
			sqlQueryObject.setANDLogicOperator(true);
			sqlQueryObject.addWhereCondition(false, 
					"id_soggetto = ?", 
					"id_soggetto_virtuale = ?", 
					"(tipo_soggetto_virtuale = ? AND nome_soggetto_virtuale = ?)");		
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idSoggetto);
			stmt.setLong(2, idSoggetto);
			stmt.setString(3, tipoSoggetto);
			stmt.setString(4, nomeSoggetto);
			risultato = stmt.executeQuery();
			while (risultato.next()) {
				String nome = risultato.getString("nome_porta");
				ResultPorta resultPorta = DBOggettiInUsoUtils.formatPortaApplicativa(nome, tipoDB, con, normalizeObjectIds);
				if(resultPorta.mapping) {
					mappingErogazionePA_list.add(resultPorta.label);
				}
				else {
					porte_applicative_list.add(resultPorta.label);
				}
				isInUso = true;
			}
			risultato.close();
			stmt.close();



			if(idSoggettoRegistro!=null){
				//controllo se referente
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.ACCORDI);
				sqlQueryObject.addSelectField("*");
				sqlQueryObject.addWhereCondition("id_referente = ?");
				queryString = sqlQueryObject.createSQLQuery();
				stmt = con.prepareStatement(queryString);
				stmt.setLong(1, idSoggetto);
				risultato = stmt.executeQuery();
				while (risultato.next()){
					String nomeAccordo = risultato.getString("nome");
					Integer versione = risultato.getInt("versione");
					if(normalizeObjectIds) {
						String protocollo = ProtocolFactoryManager.getInstance().getProtocolByOrganizationType(idSoggettoRegistro.getTipo());
						IDAccordo idAccordo = IDAccordoFactory.getInstance().getIDAccordoFromValues(nomeAccordo, idSoggettoRegistro, versione); 
						accordi_list.add(DBOggettiInUsoUtils.getProtocolPrefix(protocollo)+NamingUtils.getLabelAccordoServizioParteComune(protocollo, idAccordo));
					}
					else {
						StringBuilder bf = new StringBuilder();
						bf.append(idSoggettoRegistro.toString());
						bf.append(":");
						bf.append(nomeAccordo);
						if(versione!=null){
							bf.append(":");
							bf.append(versione);
						}
						accordi_list.add(bf.toString());
					}				
					isInUso=true;
				}
				risultato.close();
				stmt.close();

			}

			if(idSoggettoRegistro!=null){
				//controllo se referente di un accordo cooperazione
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.ACCORDI_COOPERAZIONE);
				sqlQueryObject.addSelectField("*");
				sqlQueryObject.addWhereCondition("id_referente = ?");
				queryString = sqlQueryObject.createSQLQuery();
				stmt = con.prepareStatement(queryString);
				stmt.setLong(1, idSoggetto);
				risultato = stmt.executeQuery();
				while (risultato.next()){
					String nomeAccordo = risultato.getString("nome");
					Integer versione = risultato.getInt("versione");
					if(normalizeObjectIds) {
						String protocollo = ProtocolFactoryManager.getInstance().getProtocolByOrganizationType(idSoggettoRegistro.getTipo());
						IDAccordoCooperazione idAccordo = IDAccordoCooperazioneFactory.getInstance().getIDAccordoFromValues(nomeAccordo, idSoggettoRegistro, versione); 
						accordi_coop_list.add(DBOggettiInUsoUtils.getProtocolPrefix(protocollo)+NamingUtils.getLabelAccordoCooperazione(protocollo, idAccordo));
					}
					else {
						StringBuilder bf = new StringBuilder();
						bf.append(idSoggettoRegistro.toString());
						bf.append(":");
						bf.append(nomeAccordo);
						if(versione!=null){
							bf.append(":");
							bf.append(versione);
						}
						accordi_coop_list.add(bf.toString());
					}
					isInUso=true;
				}
				risultato.close();
				stmt.close();

			}

			if(idSoggettoRegistro!=null){

				//controllo se partecipante
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.ACCORDI_COOPERAZIONE_PARTECIPANTI);
				sqlQueryObject.addFromTable(CostantiDB.ACCORDI_COOPERAZIONE);
				sqlQueryObject.addSelectField("*");
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addWhereCondition(CostantiDB.ACCORDI_COOPERAZIONE_PARTECIPANTI+".id_accordo_cooperazione = "+CostantiDB.ACCORDI_COOPERAZIONE+".id");
				sqlQueryObject.addWhereCondition("id_soggetto = ?");
				queryString = sqlQueryObject.createSQLQuery();
				stmt = con.prepareStatement(queryString);
				stmt.setLong(1, idSoggetto);
				risultato = stmt.executeQuery();
				while (risultato.next()){
					String nomeAccordo = risultato.getString("nome");
					Integer versione = risultato.getInt("versione");
					if(normalizeObjectIds) {
						String protocollo = ProtocolFactoryManager.getInstance().getProtocolByOrganizationType(idSoggettoRegistro.getTipo());
						IDAccordo idAccordo = IDAccordoFactory.getInstance().getIDAccordoFromValues(nomeAccordo, idSoggettoRegistro, versione); 
						partecipanti_list.add(DBOggettiInUsoUtils.getProtocolPrefix(protocollo)+NamingUtils.getLabelAccordoServizioParteComune(protocollo, idAccordo));
					}
					else {
						StringBuilder bf = new StringBuilder();
						bf.append(idSoggettoRegistro.toString());
						bf.append(":");
						bf.append(nomeAccordo);
						if(versione!=null){
							bf.append(":");
							bf.append(versione);
						}
						partecipanti_list.add(bf.toString());
					}
					isInUso=true;
				}
				risultato.close();
				stmt.close();

			}
			
			if(idSoggettoRegistro!=null && mappingFruizionePD_list.isEmpty() && mappingErogazionePA_list.isEmpty()){
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI);
				sqlQueryObject.addSelectField("*");
				sqlQueryObject.addWhereCondition("id_soggetto = ?");
				queryString = sqlQueryObject.createSQLQuery();
				stmt = con.prepareStatement(queryString);
				stmt.setLong(1, idSoggetto);
				risultato = stmt.executeQuery();
				while (risultato.next()) {
					String nome_servizio = risultato.getString("nome_servizio");
					String tipo_servizio = risultato.getString("tipo_servizio");
					Integer versione_servizio = risultato.getInt("versione_servizio");
					if(normalizeObjectIds) {
						String protocollo = ProtocolFactoryManager.getInstance().getProtocolByOrganizationType(idSoggettoRegistro.getTipo());
						IDServizio idServizio = IDServizioFactory.getInstance().getIDServizioFromValues(tipo_servizio, nome_servizio, idSoggettoRegistro, versione_servizio);
						servizi_list.add(DBOggettiInUsoUtils.getProtocolPrefix(protocollo)+NamingUtils.getLabelAccordoServizioParteSpecifica(protocollo, idServizio));
					}
					else {
						servizi_list.add(idSoggettoRegistro.toString()+
								"_"+tipo_servizio + "/" + nome_servizio+"/"+versione_servizio);
					}
					isInUso = true;
				}
				risultato.close();
				stmt.close();
			}
			
			if(idSoggettoRegistro!=null && mappingFruizionePD_list.isEmpty() && mappingErogazionePA_list.isEmpty()){

				// controllo che non sia fruitore di un servizio
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI_FRUITORI);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI);
				sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
				sqlQueryObject.addSelectField("tipo_servizio");
				sqlQueryObject.addSelectField("nome_servizio");
				sqlQueryObject.addSelectField("versione_servizio");
				sqlQueryObject.addSelectField("tipo_soggetto");
				sqlQueryObject.addSelectField("nome_soggetto");
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_FRUITORI+".id_servizio = "+CostantiDB.SERVIZI+".id");
				sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI+".id_soggetto = "+CostantiDB.SOGGETTI+".id");
				sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_FRUITORI+".id_soggetto = ?");
				queryString = sqlQueryObject.createSQLQuery();
				stmt = con.prepareStatement(queryString);
				stmt.setLong(1, idSoggetto);
				risultato = stmt.executeQuery();
				while (risultato.next()) {
					String tipo_soggetto = risultato.getString("tipo_soggetto");
					String nome_soggetto = risultato.getString("nome_soggetto");

					String tipoServizio = risultato.getString("tipo_servizio");
					String nomeServizio = risultato.getString("nome_servizio");
					Integer versioneServizio = risultato.getInt("versione_servizio");
					
					if(normalizeObjectIds) {
						String protocollo = ProtocolFactoryManager.getInstance().getProtocolByOrganizationType(idSoggettoRegistro.getTipo());
						IDServizio idServizio = IDServizioFactory.getInstance().getIDServizioFromValues(tipoServizio, nomeServizio, new IDSoggetto(tipo_soggetto, nome_soggetto), versioneServizio);
						servizi_fruitori_list.add(DBOggettiInUsoUtils.getProtocolPrefix(protocollo)+NamingUtils.getLabelAccordoServizioParteSpecifica(protocollo, idServizio));
					}
					else {
						servizi_fruitori_list.add(tipo_soggetto+"/"+nome_soggetto+
								"_"+
								tipoServizio+"/"+nomeServizio+"/"+versioneServizio);
					}
					isInUso = true;
				}
				risultato.close();
				stmt.close();

			}
			
			
			// Controllo che il soggetto non sia associato ad utenti
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.USERS);
			sqlQueryObject.addFromTable(CostantiDB.USERS_SOGGETTI);
			sqlQueryObject.addSelectField("login");
			sqlQueryObject.setANDLogicOperator(true);
			sqlQueryObject.addWhereCondition(CostantiDB.USERS_SOGGETTI+".id_soggetto = ?");
			sqlQueryObject.addWhereCondition(CostantiDB.USERS+".id = "+CostantiDB.USERS_SOGGETTI+".id_utente");
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idSoggetto);
			risultato = stmt.executeQuery();
			while (risultato.next()) {
				utenti_list.add(risultato.getString("login"));
				isInUso = true;
			}
			risultato.close();
			stmt.close();
			
			
			
			
			// Controllo che il soggetto non sia associato a policy di controllo del traffico o allarmi
			
			int max = 2;
			if(!CostantiDB.ALLARMI_ENABLED) {
				max=1;
			}
			for (int i = 0; i < max; i++) {
				
				String tabella = CostantiDB.CONTROLLO_TRAFFICO_ACTIVE_POLICY;
				String identificativo_column = "active_policy_id";
				String alias_column = "policy_alias";
				List<String> list = ct_list;
				String oggetto = "Policy";
				if(i==1) {
					tabella = CostantiDB.ALLARMI;
					identificativo_column = "nome";
					alias_column = "alias";
					list = allarme_list;
					oggetto = "Allarme";
				}
			
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
				sqlQueryObject.addFromTable(tabella);
				sqlQueryObject.addSelectField(identificativo_column);
				sqlQueryObject.addSelectField(alias_column);
				sqlQueryObject.addSelectField("filtro_ruolo");
				sqlQueryObject.addSelectField("filtro_porta");
				sqlQueryObject.setANDLogicOperator(false); // OR
				sqlQueryObject.addWhereCondition(true, 
						tabella+".filtro_tipo_fruitore = ?", 
						tabella+".filtro_nome_fruitore = ?");
				sqlQueryObject.addWhereCondition(true, 
						tabella+".filtro_tipo_erogatore = ?", 
						tabella+".filtro_nome_erogatore = ?");
				sqlQueryObject.addOrderBy("filtro_ruolo");
				sqlQueryObject.addOrderBy("filtro_porta");
				queryString = sqlQueryObject.createSQLQuery();
				stmt = con.prepareStatement(queryString);
				int index = 1;
				stmt.setString(index++, tipoSoggetto);
				stmt.setString(index++, nomeSoggetto);
				stmt.setString(index++, tipoSoggetto);
				stmt.setString(index++, nomeSoggetto);
				risultato = stmt.executeQuery();
				while (risultato.next()) {
					
					String alias = risultato.getString(alias_column);
					if(alias== null || "".equals(alias)) {
						alias = risultato.getString(identificativo_column);
					}
					
					String nomePorta = risultato.getString("filtro_porta");
					String filtro_ruolo = risultato.getString("filtro_ruolo");
					if(nomePorta!=null) {
						String tipo = null;
						String label = null;
						if("delegata".equals(filtro_ruolo)) {
							try {
								ResultPorta resultPorta = DBOggettiInUsoUtils.formatPortaDelegata(nomePorta, tipoDB, con, normalizeObjectIds);
								if(resultPorta.mapping) {
									label = "Fruizione di Servizio "+ resultPorta.label;
								}
							}catch(Exception e) {
								tipo = "Outbound";
							}
						}
						else if("applicativa".equals(filtro_ruolo)) {
							try {
								ResultPorta resultPorta = DBOggettiInUsoUtils.formatPortaApplicativa(nomePorta, tipoDB, con, normalizeObjectIds);
								if(resultPorta.mapping) {
									label = "Erogazione di Servizio "+ resultPorta.label;
								}
							}catch(Exception e) {
								tipo = "Inbound";
							}
						}
						else {
							tipo = filtro_ruolo;
						}
						if(label==null) {
							list.add(oggetto+" '"+alias+"' attiva nella porta '"+tipo+"' '"+nomePorta+"' ");
						}
						else {
							list.add(oggetto+" '"+alias+"' attiva nella "+label);
						}
					}
					else {
						list.add(oggetto+" '"+alias+"'");
					}
	
					isInUso = true;
				}
				risultato.close();
				stmt.close();
			}

			
			// controllo se in uso in porte applicative nell'autorizzazione
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_SOGGETTI);
			sqlQueryObject.addSelectField(CostantiDB.PORTE_APPLICATIVE+".nome_porta");
			sqlQueryObject.setANDLogicOperator(true);
			sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE_SOGGETTI+".id_porta = "+CostantiDB.PORTE_APPLICATIVE+".id");
			sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE_SOGGETTI+".tipo_soggetto=?");
			sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE_SOGGETTI+".nome_soggetto=?");	
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setString(1, tipoSoggetto);
			stmt.setString(2, nomeSoggetto);
			risultato = stmt.executeQuery();
			while (risultato.next()) {
				String nome = risultato.getString("nome_porta");
				ResultPorta resultPorta = DBOggettiInUsoUtils.formatPortaApplicativa(nome, tipoDB, con, normalizeObjectIds);
				if(resultPorta.mapping) {
					autorizzazionePA_mapping_list.add(resultPorta.label);
				}
				else {
					autorizzazionePA_list.add(resultPorta.label);
				}
				isInUso = true;
			}
			risultato.close();
			stmt.close();
			
			
			if(verificaRuoli) {
				// Raccolgo prima i ruoli
				List<String> listRuoliSoggetti = new ArrayList<>();
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
				sqlQueryObject.addFromTable(CostantiDB.SOGGETTI_RUOLI);
				sqlQueryObject.addFromTable(CostantiDB.RUOLI);
				sqlQueryObject.addSelectAliasField(CostantiDB.RUOLI,"nome","nomeRuolo");
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".id=?");
				sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI_RUOLI+".id_soggetto="+CostantiDB.SOGGETTI+".id");
				sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI_RUOLI+".id_ruolo="+CostantiDB.RUOLI+".id");
				queryString = sqlQueryObject.createSQLQuery();
				stmt = con.prepareStatement(queryString);
				stmt.setLong(1, idSoggetto);
				risultato = stmt.executeQuery();
				while (risultato.next()){
					String ruolo = risultato.getString("nomeRuolo");
					listRuoliSoggetti.add(ruolo);
				}
				risultato.close();
				stmt.close();
				
				if(!listRuoliSoggetti.isEmpty()) {
				
					boolean isInUsoRuoli = _checkSoggetto_ruoloInUsoInPorteApplicative(con, tipoDB, normalizeObjectIds,
							CostantiDB.PORTE_APPLICATIVE_RUOLI,
							listRuoliSoggetti,
							ruoliPA_mapping_list, ruoliPA_list,
							ruoliPA_mapping_list, ruoliPA_list);
					if(isInUsoRuoli) {
						isInUso = true;
					}

					boolean isInUsoRuoliToken = _checkSoggetto_ruoloInUsoInPorteApplicative(con, tipoDB, normalizeObjectIds,
							CostantiDB.PORTE_APPLICATIVE_TOKEN_RUOLI,
							listRuoliSoggetti,
							ruoliTokenPA_mapping_list, ruoliTokenPA_list,
							ruoliTokenPA_mapping_modi_list, ruoliTokenPA_modi_list);
					if(isInUsoRuoliToken) {
						isInUso = true;
					}
					
				}
			}

			
			// controllo se in uso in porte applicative nella trasformazione
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI_SOGGETTI);
			sqlQueryObject.addSelectField(CostantiDB.PORTE_APPLICATIVE+".nome_porta");
			sqlQueryObject.setANDLogicOperator(true);
			sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI+".id_porta = "+CostantiDB.PORTE_APPLICATIVE+".id");
			sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI+".id = "+CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI_SOGGETTI+".id_trasformazione");
			sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI_SOGGETTI+".tipo_soggetto=?");
			sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI_SOGGETTI+".nome_soggetto=?");	
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setString(1, tipoSoggetto);
			stmt.setString(2, nomeSoggetto);
			risultato = stmt.executeQuery();
			while (risultato.next()) {
				String nome = risultato.getString("nome_porta");
				ResultPorta resultPorta = DBOggettiInUsoUtils.formatPortaApplicativa(nome, tipoDB, con, normalizeObjectIds);
				if(resultPorta.mapping) {
					trasformazionePA_mapping_list.add(resultPorta.label);
				}
				else {
					trasformazionePA_list.add(resultPorta.label);
				}
				isInUso = true;
			}
			risultato.close();
			stmt.close();
			
			// controllo se in uso nelle regole di cnonfigurazione del proxy pass
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.CONFIG_URL_REGOLE);
			sqlQueryObject.addSelectField(CostantiDB.CONFIG_URL_REGOLE+".nome");
			sqlQueryObject.setANDLogicOperator(true);
			sqlQueryObject.addWhereCondition(CostantiDB.CONFIG_URL_REGOLE+".tipo_soggetto=?");
			sqlQueryObject.addWhereCondition(CostantiDB.CONFIG_URL_REGOLE+".nome_soggetto=?");	
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setString(1, tipoSoggetto);
			stmt.setString(2, nomeSoggetto);
			risultato = stmt.executeQuery();
			while (risultato.next()) {
				String nome = risultato.getString("nome");
				configurazioniProxyPass_list.add(nome);
				isInUso = true;
			}
			risultato.close();
			stmt.close();
			
			
			return isInUso;

		} catch (Exception se) {
			throw new UtilsException("[DBOggettiInUsoUtils::" + nomeMetodo + "] Exception: " + se.getMessage(),se);
		} finally {
			// Chiudo statement and resultset
			JDBCUtilities.closeResources(risultato, stmt);
		}
	}

	private static boolean _checkSoggetto_ruoloInUsoInPorteApplicative(Connection con, String tipoDB, boolean normalizeObjectIds,
			String nomeTabella,
			List<String> listRuoliSoggetti,
			List<String> ruoliPA_mapping_list, List<String> ruoliPA_list,
			List<String> ruoliPA_mapping_modi_list, List<String> ruoliPA_modi_list) throws Exception {
		boolean isInUso = false;
		PreparedStatement stmt = null;
		ResultSet risultato = null;
		try {

			List<String> distinctPorteApplicative = new ArrayList<>();
			List<String> verificaPorteApplicativeAll = new ArrayList<>();
			
			for (String ruolo : listRuoliSoggetti) {
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
				sqlQueryObject.addFromTable(nomeTabella);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE);
				sqlQueryObject.addSelectField("nome_porta");
				sqlQueryObject.addSelectField("ruoli_match");
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addWhereCondition(nomeTabella+".id_porta="+CostantiDB.PORTE_APPLICATIVE+".id");
				sqlQueryObject.addWhereCondition(nomeTabella+".ruolo=?");
				String queryString = sqlQueryObject.createSQLQuery();
				stmt = con.prepareStatement(queryString);
				stmt.setString(1, ruolo);
				risultato = stmt.executeQuery();
				while (risultato.next()){
					String nome = risultato.getString("nome_porta");
					String ruolo_match = risultato.getString("ruoli_match");
					if(RuoloTipoMatch.ANY.getValue().equals(ruolo_match)) {
						if(distinctPorteApplicative.contains(nome)) {
							continue;
						}
						else {
							distinctPorteApplicative.add(nome);
						}
						ResultPorta resultPorta = DBOggettiInUsoUtils.formatPortaApplicativa(nome, tipoDB, con, normalizeObjectIds);
						if(resultPorta.mapping) {
							if(resultPorta.erogazioneModi)
								ruoliPA_mapping_modi_list.add(resultPorta.label);
							else
								ruoliPA_mapping_list.add(resultPorta.label);
						}
						else {
							if(resultPorta.erogazioneModi)
								ruoliPA_modi_list.add(resultPorta.label);
							else
								ruoliPA_list.add(resultPorta.label);
						}
						isInUso = true;
					}
					else {
						// devo verificare tutti i ruoli richiesti
						if(verificaPorteApplicativeAll.contains(nome)) {
							continue;
						}
						else {
							verificaPorteApplicativeAll.add(nome);
						}
					}
				}
				risultato.close();
				stmt.close();	
			}
			
			// autorizzazione 'all'
			if(!verificaPorteApplicativeAll.isEmpty()) {
				for (String nome : verificaPorteApplicativeAll) {
					
					// Raccolgo prima i ruoli della porta
					List<String> listRuoliPorta = new ArrayList<>();
					ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
					sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE);
					sqlQueryObject.addFromTable(nomeTabella);
					sqlQueryObject.addSelectField("ruolo");
					sqlQueryObject.setANDLogicOperator(true);
					sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE+".nome_porta=?");
					sqlQueryObject.addWhereCondition(nomeTabella+".id_porta="+CostantiDB.PORTE_APPLICATIVE+".id");
					String queryString = sqlQueryObject.createSQLQuery();
					stmt = con.prepareStatement(queryString);
					stmt.setString(1, nome);
					risultato = stmt.executeQuery();
					while (risultato.next()){
						String ruolo = risultato.getString("ruolo");
						listRuoliPorta.add(ruolo);
					}
					risultato.close();
					stmt.close();
					
					if(!listRuoliPorta.isEmpty()) {
						boolean match = true;
						for (String ruoloPorta : listRuoliPorta) {
							if(!listRuoliSoggetti.contains(ruoloPorta)) {
								match = false;
								break;
							}
						}
						if(match) {
							if(distinctPorteApplicative.contains(nome)) {
								continue;
							}
							else {
								distinctPorteApplicative.add(nome);
							}
							ResultPorta resultPorta = DBOggettiInUsoUtils.formatPortaApplicativa(nome, tipoDB, con, normalizeObjectIds);
							if(resultPorta.mapping) {
								if(resultPorta.erogazioneModi)
									ruoliPA_mapping_modi_list.add(resultPorta.label);
								else
									ruoliPA_mapping_list.add(resultPorta.label);
							}
							else {
								if(resultPorta.erogazioneModi)
									ruoliPA_modi_list.add(resultPorta.label);
								else
									ruoliPA_list.add(resultPorta.label);
							}
							isInUso = true;
						}
					}
					
				}
			}

		} finally {
			// Chiudo statement and resultset
			try {
				if (risultato != null) {
					risultato.close();
				}
			} catch (Exception e) {
				// ignore
			}
			try {
				if (stmt != null) {
					stmt.close();
				}
			} catch (Exception e) {
				// ignore
			}
		}
		return isInUso;
	}
	
	protected static String toString(IDSoggetto idSoggetto, Map<ErrorsHandlerCostant, List<String>> whereIsInUso, boolean prefix, String separator, boolean normalizeObjectIds){
		Set<ErrorsHandlerCostant> keys = whereIsInUso.keySet();
		String labelSoggetto = idSoggetto.toString();
		try {
			if(normalizeObjectIds) {
				String protocollo = ProtocolFactoryManager.getInstance().getProtocolByOrganizationType(idSoggetto.getTipo());
				labelSoggetto = DBOggettiInUsoUtils.getProtocolPrefix(protocollo)+NamingUtils.getLabelSoggetto(protocollo, idSoggetto);
			}
		}catch(Exception e) {
			// ignore
		}
		String msg = "Soggetto '"+labelSoggetto+ "' non eliminabile perch&egrave; :"+separator;
		if(prefix==false){
			msg = "";
		}
		String separatorCategorie = "";
		if(whereIsInUso.size()>1) {
			separatorCategorie = separator;
		}
		for (ErrorsHandlerCostant key : keys) {
			List<String> messages = whereIsInUso.get(key);

			if ( messages!=null && messages.size() > 0) {
				msg += separatorCategorie;
			}
			
			switch (key) {
			case IS_FRUITORE:
				if ( messages!=null && messages.size() > 0) {
					msg += "fruitore dei Servizi: " + DBOggettiInUsoUtils.formatList(messages,separator) + separator;
				}
				break;
			case IN_USO_IN_SERVIZI_APPLICATIVI:
				if ( messages!=null && messages.size() > 0 ) {
					msg += "utilizzato negli Applicativi: " + DBOggettiInUsoUtils.formatList(messages,separator) + separator;
				}
				break;
			case IN_USO_IN_SERVIZI:
				if ( messages!=null && messages.size() > 0) {
					msg += "erogatore dei Servizi: " + DBOggettiInUsoUtils.formatList(messages,separator) + separator;
				}
				break;
			case IN_USO_IN_PORTE_APPLICATIVE:
				if ( messages!=null && messages.size() > 0) {
					msg += "utilizzato nelle Porte Inbound: " + DBOggettiInUsoUtils.formatList(messages,separator) + separator;
				}
				break;
			case IN_USO_IN_PORTE_DELEGATE:
				if ( messages!=null && messages.size() > 0) {
					msg += "utilizzato nelle Porte Outbound: " + DBOggettiInUsoUtils.formatList(messages,separator) + separator;
				}
				break;
			case IN_USO_IN_MAPPING_EROGAZIONE_PA:
				if ( messages!=null && messages.size() > 0) {
					msg += "utilizzato nelle Erogazioni: " + DBOggettiInUsoUtils.formatList(messages,separator) + separator;
				}
				break;
			case IN_USO_IN_MAPPING_FRUIZIONE_PD:
				if ( messages!=null && messages.size() > 0) {
					msg += "utilizzato nelle Fruizioni: " + DBOggettiInUsoUtils.formatList(messages,separator) + separator;
				}
				break;
			case IS_REFERENTE:
				if ( messages!=null && messages.size() > 0 ) {
					msg += "referente di API: " + DBOggettiInUsoUtils.formatList(messages,separator) + separator;
				}
				break;
			case IS_REFERENTE_COOPERAZIONE:
				if ( messages!=null && messages.size() > 0 ) {
					msg += "referente di Accordi di Cooperazione: " + DBOggettiInUsoUtils.formatList(messages,separator) + separator;
				}
				break;
			case IS_PARTECIPANTE_COOPERAZIONE:
				if ( messages!=null && messages.size() > 0 ) {
					msg += "partecipante in Accordi di Cooperazione: " + DBOggettiInUsoUtils.formatList(messages,separator) + separator;
				}
				break;
			case UTENTE:
				if ( messages!=null && messages.size() > 0 ) {
					msg += "associato ad Utenti: " + DBOggettiInUsoUtils.formatList(messages,separator) + separator;
				}
				break;
			case CONTROLLO_TRAFFICO:
				if ( messages!=null && messages.size() > 0 ) {
					msg += "utilizzato in policy di Rate Limiting: " + DBOggettiInUsoUtils.formatList(messages,separator) + separator;
				}
				break;
			case ALLARMI:
				if ( messages!=null && messages.size() > 0 ) {
					msg += "utilizzato in Allarmi: " + DBOggettiInUsoUtils.formatList(messages,separator) + separator;
				}
				break;
			case AUTORIZZAZIONE_MAPPING:
				if ( messages!=null && messages.size() > 0) {
					msg += "utilizzato nel Controllo degli Accessi (Soggetti Autenticati) delle Erogazioni: " + DBOggettiInUsoUtils.formatList(messages,separator) + separator;
				}
				break;
			case AUTORIZZAZIONE:
				if ( messages!=null && messages.size() > 0) {
					msg += "utilizzato nelle Porte Inbound (Controllo degli Accessi - Soggetti Autenticati): " + DBOggettiInUsoUtils.formatList(messages,separator) + separator;
				}
				break;
			case RUOLI_MAPPING:
				if ( messages!=null && messages.size() > 0) {
					msg += "compatibile con l'Autorizzazione Trasporto per Ruoli indicata nel Controllo degli Accessi delle Erogazioni: " + DBOggettiInUsoUtils.formatList(messages,separator) + separator;
				}
				break;
			case RUOLI:
				if ( messages!=null && messages.size() > 0) {
					msg += "compatibile con l'Autorizzazione Trasporto per Ruoli indicata nel Controllo degli Accessi delle Porte Inbound: " + DBOggettiInUsoUtils.formatList(messages,separator) + separator;
				}
				break;
			case RUOLI_TOKEN_MAPPING_PA:
				if ( messages!=null && messages.size() > 0) {
					msg += "compatibile con l'Autorizzazione Token per Ruoli indicata nel Controllo degli Accessi delle Erogazioni: " + DBOggettiInUsoUtils.formatList(messages,separator) + separator;
				}
				break;
			case RUOLI_TOKEN_PA:
				if ( messages!=null && messages.size() > 0) {
					msg += "compatibile con l'Autorizzazione Token per Ruoli indicata nel Controllo degli Accessi delle Porte Inbound: " + DBOggettiInUsoUtils.formatList(messages,separator) + separator;
				}
				break;
			case RUOLI_TOKEN_MAPPING_PA_MODI:
				if ( messages!=null && messages.size() > 0) {
					msg += "compatibile con l'Autorizzazione Messaggio per Ruoli indicata nel Controllo degli Accessi delle Erogazioni: " + DBOggettiInUsoUtils.formatList(messages,separator) + separator;
				}
				break;
			case RUOLI_TOKEN_PA_MODI:
				if ( messages!=null && messages.size() > 0) {
					msg += "compatibile con l'Autorizzazione Messaggio per Ruoli indicata nel Controllo degli Accessi delle Porte Inbound: " + DBOggettiInUsoUtils.formatList(messages,separator) + separator;
				}
				break;
			case TRASFORMAZIONE_MAPPING_PA:
				if ( messages!=null && messages.size() > 0) {
					msg += "utilizzato nel criterio di applicabilità della Trasformazione (Soggetti) per le Erogazioni: " + DBOggettiInUsoUtils.formatList(messages,separator) + separator;
				}
				break;
			case TRASFORMAZIONE_PA:
				if ( messages!=null && messages.size() > 0) {
					msg += "utilizzato nelle Porte Inbound (Criterio di applicabilità della Trasformazione - Soggetti): " + DBOggettiInUsoUtils.formatList(messages,separator) + separator;
				}
				break;
			case CONFIGURAZIONE_REGOLE_PROXY_PASS:
				if ( messages!=null && messages.size() > 0) {
					msg += "utilizzato nelle Regole di Proxy Pass: " + DBOggettiInUsoUtils.formatList(messages,separator) + separator;
				}
				break;
			default:
				msg += "utilizzato in oggetto non codificato ("+key+")"+separator;
				break;
			}

		}// chiudo for

		return msg;
	}
	
}
