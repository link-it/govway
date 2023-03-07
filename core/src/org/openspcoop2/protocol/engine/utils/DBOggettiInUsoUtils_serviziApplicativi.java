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
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.jdbc.JDBCUtilities;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.SQLObjectFactory;

/**
 * DBOggettiInUsoUtils_serviziApplicativi
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class DBOggettiInUsoUtils_serviziApplicativi {


	protected static boolean isServizioApplicativoInUso(Connection con, String tipoDB, IDServizioApplicativo idServizioApplicativo, 
			Map<ErrorsHandlerCostant, List<String>> whereIsInUso, boolean isRegistroServiziLocale, boolean normalizeObjectIds,
			boolean verificaRuoli) throws UtilsException {

		String nomeMetodo = "isServizioApplicativoInUso";

		PreparedStatement stmt = null;
		ResultSet risultato = null;
		PreparedStatement stmt2 = null;
		ResultSet risultato2 = null;
		String queryString;
		try {
			boolean isInUso = false;

			long idServizioApplicativoLong = DBUtils.getIdServizioApplicativo(idServizioApplicativo.getNome(), 
					idServizioApplicativo.getIdSoggettoProprietario().getTipo(), 
					idServizioApplicativo.getIdSoggettoProprietario().getNome(), 
					con, tipoDB);
			if(idServizioApplicativoLong<=0){
				throw new UtilsException("Servizio Applicativo con id ["+idServizioApplicativo+"] non trovato");
			}


			List<String> autorizzazionePD_mapping_list = whereIsInUso.get(ErrorsHandlerCostant.AUTORIZZAZIONE_MAPPING);
			List<String> autorizzazionePD_list = whereIsInUso.get(ErrorsHandlerCostant.AUTORIZZAZIONE);
			List<String> autorizzazionePA_mapping_list = whereIsInUso.get(ErrorsHandlerCostant.AUTORIZZAZIONE_MAPPING_PA);
			List<String> autorizzazionePA_list = whereIsInUso.get(ErrorsHandlerCostant.AUTORIZZAZIONE_PA);
			List<String> autorizzazionePA_mapping_modi_list = whereIsInUso.get(ErrorsHandlerCostant.AUTORIZZAZIONE_MAPPING_PA_MODI);
			List<String> autorizzazionePA_modi_list = whereIsInUso.get(ErrorsHandlerCostant.AUTORIZZAZIONE_PA_MODI);
			List<String> autorizzazioneTokenPD_mapping_list = whereIsInUso.get(ErrorsHandlerCostant.AUTORIZZAZIONE_TOKEN_MAPPING_PD);
			List<String> autorizzazioneTokenPD_list = whereIsInUso.get(ErrorsHandlerCostant.AUTORIZZAZIONE_TOKEN_PD);
			List<String> autorizzazioneTokenPA_mapping_list = whereIsInUso.get(ErrorsHandlerCostant.AUTORIZZAZIONE_TOKEN_MAPPING_PA);
			List<String> autorizzazioneTokenPA_list = whereIsInUso.get(ErrorsHandlerCostant.AUTORIZZAZIONE_TOKEN_PA);
			List<String> ruoliPD_mapping_list = whereIsInUso.get(ErrorsHandlerCostant.RUOLI_MAPPING);
			List<String> ruoliPD_list = whereIsInUso.get(ErrorsHandlerCostant.RUOLI);
			List<String> ruoliPA_mapping_list = whereIsInUso.get(ErrorsHandlerCostant.RUOLI_MAPPING_PA);
			List<String> ruoliPA_list = whereIsInUso.get(ErrorsHandlerCostant.RUOLI_PA);
			List<String> ruoliTokenPD_mapping_list = whereIsInUso.get(ErrorsHandlerCostant.RUOLI_TOKEN_MAPPING_PD);
			List<String> ruoliTokenPD_list = whereIsInUso.get(ErrorsHandlerCostant.RUOLI_TOKEN_PD);
			List<String> ruoliTokenPA_mapping_list = whereIsInUso.get(ErrorsHandlerCostant.RUOLI_TOKEN_MAPPING_PA);
			List<String> ruoliTokenPA_list = whereIsInUso.get(ErrorsHandlerCostant.RUOLI_TOKEN_PA);
			List<String> ruoliTokenPA_mapping_modi_list = whereIsInUso.get(ErrorsHandlerCostant.RUOLI_TOKEN_MAPPING_PA_MODI);
			List<String> ruoliTokenPA_modi_list = whereIsInUso.get(ErrorsHandlerCostant.RUOLI_TOKEN_PA_MODI);
			List<String> porte_applicative_list = whereIsInUso.get(ErrorsHandlerCostant.IN_USO_IN_PORTE_APPLICATIVE);
			List<String> porte_applicative_mapping_list = whereIsInUso.get(ErrorsHandlerCostant.IN_USO_IN_MAPPING_EROGAZIONE_PA);
			List<String> ct_list = whereIsInUso.get(ErrorsHandlerCostant.CONTROLLO_TRAFFICO);
			List<String> allarme_list = whereIsInUso.get(ErrorsHandlerCostant.ALLARMI);
			List<String> trasformazionePD_mapping_list = whereIsInUso.get(ErrorsHandlerCostant.TRASFORMAZIONE_MAPPING_PD);
			List<String> trasformazionePD_list = whereIsInUso.get(ErrorsHandlerCostant.TRASFORMAZIONE_PD);
			List<String> trasformazionePA_mapping_list = whereIsInUso.get(ErrorsHandlerCostant.TRASFORMAZIONE_MAPPING_PA);
			List<String> trasformazionePA_list = whereIsInUso.get(ErrorsHandlerCostant.TRASFORMAZIONE_PA);
			
			if (autorizzazionePD_mapping_list == null) {
				autorizzazionePD_mapping_list = new ArrayList<String>();
				whereIsInUso.put(ErrorsHandlerCostant.AUTORIZZAZIONE_MAPPING, autorizzazionePD_mapping_list);
			}
			if (autorizzazionePD_list == null) {
				autorizzazionePD_list = new ArrayList<String>();
				whereIsInUso.put(ErrorsHandlerCostant.AUTORIZZAZIONE, autorizzazionePD_list);
			}
			if (autorizzazionePA_mapping_list == null) {
				autorizzazionePA_mapping_list = new ArrayList<String>();
				whereIsInUso.put(ErrorsHandlerCostant.AUTORIZZAZIONE_MAPPING_PA, autorizzazionePA_mapping_list);
			}
			if (autorizzazionePA_list == null) {
				autorizzazionePA_list = new ArrayList<String>();
				whereIsInUso.put(ErrorsHandlerCostant.AUTORIZZAZIONE_PA, autorizzazionePA_list);
			}
			if (autorizzazionePA_mapping_modi_list == null) {
				autorizzazionePA_mapping_modi_list = new ArrayList<String>();
				whereIsInUso.put(ErrorsHandlerCostant.AUTORIZZAZIONE_MAPPING_PA_MODI, autorizzazionePA_mapping_modi_list);
			}
			if (autorizzazionePA_modi_list == null) {
				autorizzazionePA_modi_list = new ArrayList<String>();
				whereIsInUso.put(ErrorsHandlerCostant.AUTORIZZAZIONE_PA_MODI, autorizzazionePA_modi_list);
			}
			
			if (autorizzazioneTokenPD_mapping_list == null) {
				autorizzazioneTokenPD_mapping_list = new ArrayList<String>();
				whereIsInUso.put(ErrorsHandlerCostant.AUTORIZZAZIONE_TOKEN_MAPPING_PD, autorizzazioneTokenPD_mapping_list);
			}
			if (autorizzazioneTokenPD_list == null) {
				autorizzazioneTokenPD_list = new ArrayList<String>();
				whereIsInUso.put(ErrorsHandlerCostant.AUTORIZZAZIONE_TOKEN_PD, autorizzazioneTokenPD_list);
			}
			if (autorizzazioneTokenPA_mapping_list == null) {
				autorizzazioneTokenPA_mapping_list = new ArrayList<String>();
				whereIsInUso.put(ErrorsHandlerCostant.AUTORIZZAZIONE_TOKEN_MAPPING_PA, autorizzazioneTokenPA_mapping_list);
			}
			if (autorizzazioneTokenPA_list == null) {
				autorizzazioneTokenPA_list = new ArrayList<String>();
				whereIsInUso.put(ErrorsHandlerCostant.AUTORIZZAZIONE_TOKEN_PA, autorizzazioneTokenPA_list);
			}
			
			if (ruoliPD_mapping_list == null) {
				ruoliPD_mapping_list = new ArrayList<String>();
				whereIsInUso.put(ErrorsHandlerCostant.RUOLI_MAPPING, ruoliPD_mapping_list);
			}
			if (ruoliPD_list == null) {
				ruoliPD_list = new ArrayList<String>();
				whereIsInUso.put(ErrorsHandlerCostant.RUOLI, ruoliPD_list);
			}
			if (ruoliPA_mapping_list == null) {
				ruoliPA_mapping_list = new ArrayList<String>();
				whereIsInUso.put(ErrorsHandlerCostant.RUOLI_MAPPING_PA, ruoliPA_mapping_list);
			}
			if (ruoliPA_list == null) {
				ruoliPA_list = new ArrayList<String>();
				whereIsInUso.put(ErrorsHandlerCostant.RUOLI_PA, ruoliPA_list);
			}
			
			if (ruoliTokenPD_mapping_list == null) {
				ruoliTokenPD_mapping_list = new ArrayList<String>();
				whereIsInUso.put(ErrorsHandlerCostant.RUOLI_TOKEN_MAPPING_PD, ruoliPD_mapping_list);
			}
			if (ruoliTokenPD_list == null) {
				ruoliTokenPD_list = new ArrayList<String>();
				whereIsInUso.put(ErrorsHandlerCostant.RUOLI_TOKEN_PD, ruoliTokenPD_list);
			}
			if (ruoliTokenPA_mapping_list == null) {
				ruoliTokenPA_mapping_list = new ArrayList<String>();
				whereIsInUso.put(ErrorsHandlerCostant.RUOLI_TOKEN_MAPPING_PA, ruoliTokenPA_mapping_list);
			}
			if (ruoliTokenPA_list == null) {
				ruoliTokenPA_list = new ArrayList<String>();
				whereIsInUso.put(ErrorsHandlerCostant.RUOLI_TOKEN_PA, ruoliTokenPA_list);
			}
			if (ruoliTokenPA_mapping_modi_list == null) {
				ruoliTokenPA_mapping_modi_list = new ArrayList<String>();
				whereIsInUso.put(ErrorsHandlerCostant.RUOLI_TOKEN_MAPPING_PA_MODI, ruoliTokenPA_mapping_modi_list);
			}
			if (ruoliTokenPA_modi_list == null) {
				ruoliTokenPA_modi_list = new ArrayList<String>();
				whereIsInUso.put(ErrorsHandlerCostant.RUOLI_TOKEN_PA_MODI, ruoliTokenPA_modi_list);
			}
			
			if (porte_applicative_list == null) {
				porte_applicative_list = new ArrayList<String>();
				whereIsInUso.put(ErrorsHandlerCostant.IN_USO_IN_PORTE_APPLICATIVE, porte_applicative_list);
			}
			if (porte_applicative_mapping_list == null) {
				porte_applicative_mapping_list = new ArrayList<String>();
				whereIsInUso.put(ErrorsHandlerCostant.IN_USO_IN_MAPPING_EROGAZIONE_PA, porte_applicative_mapping_list);
			}
			if (ct_list == null) {
				ct_list = new ArrayList<String>();
				whereIsInUso.put(ErrorsHandlerCostant.CONTROLLO_TRAFFICO, ct_list);
			}
			if (allarme_list == null) {
				allarme_list = new ArrayList<String>();
				whereIsInUso.put(ErrorsHandlerCostant.ALLARMI, allarme_list);
			}
			if (trasformazionePD_mapping_list == null) {
				trasformazionePD_mapping_list = new ArrayList<String>();
				whereIsInUso.put(ErrorsHandlerCostant.TRASFORMAZIONE_MAPPING_PD, trasformazionePD_mapping_list);
			}
			if (trasformazionePD_list == null) {
				trasformazionePD_list = new ArrayList<String>();
				whereIsInUso.put(ErrorsHandlerCostant.TRASFORMAZIONE_PD, trasformazionePD_list);
			}
			if (trasformazionePA_mapping_list == null) {
				trasformazionePA_mapping_list = new ArrayList<String>();
				whereIsInUso.put(ErrorsHandlerCostant.TRASFORMAZIONE_MAPPING_PA, trasformazionePA_mapping_list);
			}
			if (trasformazionePA_list == null) {
				trasformazionePA_list = new ArrayList<String>();
				whereIsInUso.put(ErrorsHandlerCostant.TRASFORMAZIONE_PA, trasformazionePA_list);
			}

			// Porte delegate, autorizzazione
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE_SA);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE);
			sqlQueryObject.addSelectField("nome_porta");
			sqlQueryObject.setANDLogicOperator(true);
			sqlQueryObject.addWhereCondition(CostantiDB.PORTE_DELEGATE_SA+".id_porta="+CostantiDB.PORTE_DELEGATE+".id");
			sqlQueryObject.addWhereCondition("id_servizio_applicativo=?");
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idServizioApplicativoLong);
			risultato = stmt.executeQuery();
			while (risultato.next()){
				String nome = risultato.getString("nome_porta");
				ResultPorta resultPorta = DBOggettiInUsoUtils.formatPortaDelegata(nome, tipoDB, con, normalizeObjectIds);
				if(resultPorta.mapping) {
					autorizzazionePD_mapping_list.add(resultPorta.label);
				}
				else {
					autorizzazionePD_list.add(resultPorta.label);
				}
				isInUso = true;
			}
			risultato.close();
			stmt.close();
			
			
			// Porte delegate, autorizzazione (token)
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE_TOKEN_SA);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE);
			sqlQueryObject.addSelectField("nome_porta");
			sqlQueryObject.setANDLogicOperator(true);
			sqlQueryObject.addWhereCondition(CostantiDB.PORTE_DELEGATE_TOKEN_SA+".id_porta="+CostantiDB.PORTE_DELEGATE+".id");
			sqlQueryObject.addWhereCondition("id_servizio_applicativo=?");
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idServizioApplicativoLong);
			risultato = stmt.executeQuery();
			while (risultato.next()){
				String nome = risultato.getString("nome_porta");
				ResultPorta resultPorta = DBOggettiInUsoUtils.formatPortaDelegata(nome, tipoDB, con, normalizeObjectIds);
				if(resultPorta.mapping) {
					autorizzazioneTokenPD_mapping_list.add(resultPorta.label);
				}
				else {
					autorizzazioneTokenPD_list.add(resultPorta.label);
				}
				isInUso = true;
			}
			risultato.close();
			stmt.close();
			
			
			// Porte applicative, autorizzazione
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_SA_AUTORIZZATI);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE);
			sqlQueryObject.addSelectField("nome_porta");
			sqlQueryObject.setANDLogicOperator(true);
			sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE_SA_AUTORIZZATI+".id_porta="+CostantiDB.PORTE_APPLICATIVE+".id");
			sqlQueryObject.addWhereCondition("id_servizio_applicativo=?");
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idServizioApplicativoLong);
			risultato = stmt.executeQuery();
			while (risultato.next()){
				String nome = risultato.getString("nome_porta");
				ResultPorta resultPorta = DBOggettiInUsoUtils.formatPortaApplicativa(nome, tipoDB, con, normalizeObjectIds);
				if(resultPorta.mapping) {
					if(resultPorta.erogazioneModi)
						autorizzazionePA_mapping_modi_list.add(resultPorta.label);
					else
						autorizzazionePA_mapping_list.add(resultPorta.label);
				}
				else {
					if(resultPorta.erogazioneModi)
						autorizzazionePA_modi_list.add(resultPorta.label);
					else
						autorizzazionePA_list.add(resultPorta.label);
				}
				isInUso = true;
			}
			risultato.close();
			stmt.close();
			
			
			// Porte applicative, autorizzazione (token)
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_TOKEN_SA);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE);
			sqlQueryObject.addSelectField("nome_porta");
			sqlQueryObject.setANDLogicOperator(true);
			sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE_TOKEN_SA+".id_porta="+CostantiDB.PORTE_APPLICATIVE+".id");
			sqlQueryObject.addWhereCondition("id_servizio_applicativo=?");
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idServizioApplicativoLong);
			risultato = stmt.executeQuery();
			while (risultato.next()){
				String nome = risultato.getString("nome_porta");
				ResultPorta resultPorta = DBOggettiInUsoUtils.formatPortaApplicativa(nome, tipoDB, con, normalizeObjectIds);
				if(resultPorta.mapping) {
					autorizzazioneTokenPA_mapping_list.add(resultPorta.label);
				}
				else {
					autorizzazioneTokenPA_list.add(resultPorta.label);
				}
				isInUso = true;
			}
			risultato.close();
			stmt.close(); 

			
			
			if(verificaRuoli) {
				
				// Raccolgo prima i ruoli
				List<String> listRuoliSA = new ArrayList<>();
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI_RUOLI);
				sqlQueryObject.addSelectField("ruolo");
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".id=?");
				sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI_RUOLI+".id_servizio_applicativo="+CostantiDB.SERVIZI_APPLICATIVI+".id");
				queryString = sqlQueryObject.createSQLQuery();
				stmt = con.prepareStatement(queryString);
				stmt.setLong(1, idServizioApplicativoLong);
				risultato = stmt.executeQuery();
				while (risultato.next()){
					String ruolo = risultato.getString("ruolo");
					listRuoliSA.add(ruolo);
				}
				risultato.close();
				stmt.close();
				
				if(!listRuoliSA.isEmpty()) {
				
					_checkServizioApplicativo_ruoloInUsoInPorteDelegate(con, tipoDB, normalizeObjectIds,
							CostantiDB.PORTE_DELEGATE_RUOLI,
							listRuoliSA,
							ruoliPD_mapping_list, ruoliPD_list);
					
					_checkServizioApplicativo_ruoloInUsoInPorteDelegate(con, tipoDB, normalizeObjectIds,
							CostantiDB.PORTE_DELEGATE_TOKEN_RUOLI,
							listRuoliSA,
							ruoliTokenPD_mapping_list, ruoliTokenPD_list);
									
					boolean isInUsoRuoli = _checkServizioApplicativo_ruoloInUsoInPorteApplicative(con, tipoDB, normalizeObjectIds,
							CostantiDB.PORTE_APPLICATIVE_RUOLI,
							listRuoliSA,
							ruoliPA_mapping_list, ruoliPA_list,
							ruoliPA_mapping_list, ruoliPA_list);
					if(isInUsoRuoli) {
						isInUso = true;
					}
					
					boolean isInUsoRuoliToken = _checkServizioApplicativo_ruoloInUsoInPorteApplicative(con, tipoDB, normalizeObjectIds,
							CostantiDB.PORTE_APPLICATIVE_TOKEN_RUOLI,
							listRuoliSA,
							ruoliTokenPA_mapping_list, ruoliTokenPA_list,
							ruoliTokenPA_mapping_modi_list, ruoliTokenPA_modi_list);
					if(isInUsoRuoliToken) {
						isInUso = true;
					}
				}
			}
			


			// Porte applicative
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_SA);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.setANDLogicOperator(true);
			sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE_SA+".id_porta="+CostantiDB.PORTE_APPLICATIVE+".id");
			sqlQueryObject.addWhereCondition("id_servizio_applicativo=?");
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idServizioApplicativoLong);
			risultato = stmt.executeQuery();
			while (risultato.next()){
				String nome = risultato.getString("nome_porta");
				ResultPorta resultPorta = DBOggettiInUsoUtils.formatPortaApplicativa(nome, tipoDB, con, normalizeObjectIds);
				if(resultPorta.mapping) {
					porte_applicative_mapping_list.add(resultPorta.label);
				}
				else {
					porte_applicative_list.add(resultPorta.label);
				}
				isInUso = true;
			}
			risultato.close();
			stmt.close();

			
			// Controllo che il servizio aplicativo non sia associato a policy di controllo del traffico o allarmi
			
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
				boolean allarmi = false;
				if(i==1) {
					tabella = CostantiDB.ALLARMI;
					identificativo_column = "nome";
					alias_column = "alias";
					list = allarme_list;
					oggetto = "Allarme";
					allarmi=true;
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
						tabella+".filtro_nome_fruitore = ?",
						tabella+".filtro_sa_fruitore = ?");
				if(!allarmi) {
					sqlQueryObject.addWhereCondition(true, 
							tabella+".filtro_tipo_erogatore = ?", 
							tabella+".filtro_nome_erogatore = ?",
							tabella+".filtro_sa_erogatore = ?");
				}
				sqlQueryObject.addOrderBy("filtro_ruolo");
				sqlQueryObject.addOrderBy("filtro_porta");
				queryString = sqlQueryObject.createSQLQuery();
				stmt = con.prepareStatement(queryString);
				int index = 1;
				stmt.setString(index++, idServizioApplicativo.getIdSoggettoProprietario().getTipo());
				stmt.setString(index++, idServizioApplicativo.getIdSoggettoProprietario().getNome());
				stmt.setString(index++, idServizioApplicativo.getNome());
				if(!allarmi) {
					stmt.setString(index++, idServizioApplicativo.getIdSoggettoProprietario().getTipo());
					stmt.setString(index++, idServizioApplicativo.getIdSoggettoProprietario().getNome());
					stmt.setString(index++, idServizioApplicativo.getNome());
				}
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
			
			
			
			// Porte delegate, trasformazioni
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI_SA);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE);
			sqlQueryObject.addSelectField("nome_porta");
			sqlQueryObject.setANDLogicOperator(true);
			sqlQueryObject.addWhereCondition(CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI+".id_porta="+CostantiDB.PORTE_DELEGATE+".id");
			sqlQueryObject.addWhereCondition(CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI_SA+".id_trasformazione="+CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI+".id");
			sqlQueryObject.addWhereCondition("id_servizio_applicativo=?");
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idServizioApplicativoLong);
			risultato = stmt.executeQuery();
			while (risultato.next()){
				String nome = risultato.getString("nome_porta");
				ResultPorta resultPorta = DBOggettiInUsoUtils.formatPortaDelegata(nome, tipoDB, con, normalizeObjectIds);
				if(resultPorta.mapping) {
					trasformazionePD_mapping_list.add(resultPorta.label);
				}
				else {
					trasformazionePD_list.add(resultPorta.label);
				}
				isInUso = true;
			}
			risultato.close();
			stmt.close();
			
			
			
			// Porte applicative, trasformazioni
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI_SA);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE);
			sqlQueryObject.addSelectField("nome_porta");
			sqlQueryObject.setANDLogicOperator(true);
			sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI+".id_porta="+CostantiDB.PORTE_APPLICATIVE+".id");
			sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI_SA+".id_trasformazione="+CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI+".id");
			sqlQueryObject.addWhereCondition("id_servizio_applicativo=?");
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idServizioApplicativoLong);
			risultato = stmt.executeQuery();
			while (risultato.next()){
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
			
			
			
			return isInUso;

		} catch (Exception se) {
			throw new UtilsException("[DBOggettiInUsoUtils::" + nomeMetodo + "] Exception: " + se.getMessage(),se);
		} finally {
			// Chiudo statement and resultset
			JDBCUtilities.closeResources(risultato2, stmt2);
			JDBCUtilities.closeResources(risultato, stmt);
		}
	}
	
	private static boolean _checkServizioApplicativo_ruoloInUsoInPorteDelegate(Connection con, String tipoDB, boolean normalizeObjectIds,
			String nomeTabella,
			List<String> listRuoliSA,
			List<String> ruoliPD_mapping_list, List<String> ruoliPD_list) throws Exception {
		boolean isInUso = false;
		PreparedStatement stmt = null;
		ResultSet risultato = null;
		try {
			
			List<String> distinctPorteDelegate = new ArrayList<String>();
			List<String> verificaPorteDelegateAll = new ArrayList<String>();
			
			for (String ruolo : listRuoliSA) {
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
				sqlQueryObject.addFromTable(nomeTabella);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE);
				sqlQueryObject.addSelectField("nome_porta");
				sqlQueryObject.addSelectField("ruoli_match");
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addWhereCondition(nomeTabella+".id_porta="+CostantiDB.PORTE_DELEGATE+".id");
				sqlQueryObject.addWhereCondition(nomeTabella+".ruolo=?");
				String queryString = sqlQueryObject.createSQLQuery();
				stmt = con.prepareStatement(queryString);
				stmt.setString(1, ruolo);
				risultato = stmt.executeQuery();
				while (risultato.next()){
					String nome = risultato.getString("nome_porta");
					String ruolo_match = risultato.getString("ruoli_match");
					if(RuoloTipoMatch.ANY.getValue().equals(ruolo_match)) {
						if(distinctPorteDelegate.contains(nome)) {
							continue;
						}
						else {
							distinctPorteDelegate.add(nome);
						}
						ResultPorta resultPorta = DBOggettiInUsoUtils.formatPortaDelegata(nome, tipoDB, con, normalizeObjectIds);
						if(resultPorta.mapping) {
							ruoliPD_mapping_list.add(resultPorta.label);
						}
						else {
							ruoliPD_list.add(resultPorta.label);
						}
						isInUso = true;
					}
					else {
						// devo verificare tutti i ruoli richiesti
						if(verificaPorteDelegateAll.contains(nome)) {
							continue;
						}
						else {
							verificaPorteDelegateAll.add(nome);
						}
					}
				}
				risultato.close();
				stmt.close();	
			}
			
			// autorizzazione 'all'
			if(!verificaPorteDelegateAll.isEmpty()) {
				for (String nome : verificaPorteDelegateAll) {
					
					// Raccolgo prima i ruoli della porta
					List<String> listRuoliPorta = new ArrayList<>();
					ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
					sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE);
					sqlQueryObject.addFromTable(nomeTabella);
					sqlQueryObject.addSelectField("ruolo");
					sqlQueryObject.setANDLogicOperator(true);
					sqlQueryObject.addWhereCondition(CostantiDB.PORTE_DELEGATE+".nome_porta=?");
					sqlQueryObject.addWhereCondition(nomeTabella+".id_porta="+CostantiDB.PORTE_DELEGATE+".id");
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
							if(!listRuoliSA.contains(ruoloPorta)) {
								match = false;
								break;
							}
						}
						if(match) {
							if(distinctPorteDelegate.contains(nome)) {
								continue;
							}
							else {
								distinctPorteDelegate.add(nome);
							}
							ResultPorta resultPorta = DBOggettiInUsoUtils.formatPortaDelegata(nome, tipoDB, con, normalizeObjectIds);
							if(resultPorta.mapping) {
								ruoliPD_mapping_list.add(resultPorta.label);
							}
							else {
								ruoliPD_list.add(resultPorta.label);
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
	
	private static boolean _checkServizioApplicativo_ruoloInUsoInPorteApplicative(Connection con, String tipoDB, boolean normalizeObjectIds,
			String nomeTabella,
			List<String> listRuoliSA,
			List<String> ruoliPA_mapping_list, List<String> ruoliPA_list,
			List<String> ruoliPA_mapping_modi_list, List<String> ruoliPA_modi_list) throws Exception {
		boolean isInUso = false;
		PreparedStatement stmt = null;
		ResultSet risultato = null;
		try {
			
			List<String> distinctPorteApplicative = new ArrayList<String>();
			List<String> verificaPorteApplicativeAll = new ArrayList<String>();
			
			for (String ruolo : listRuoliSA) {
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
							if(!listRuoliSA.contains(ruoloPorta)) {
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

	protected static String toString(IDServizioApplicativo idServizioApplicativo, Map<ErrorsHandlerCostant, List<String>> whereIsInUso, boolean prefix, String separator, boolean normalizeObjectIds){
		
		StringBuilder bf = new StringBuilder();
		if(normalizeObjectIds) {
			try {
				String protocollo = ProtocolFactoryManager.getInstance().getProtocolByOrganizationType(idServizioApplicativo.getIdSoggettoProprietario().getTipo());
				String labelSA = DBOggettiInUsoUtils.getProtocolPrefix(protocollo)+idServizioApplicativo.getNome()+DBOggettiInUsoUtils.getSubjectSuffix(protocollo, idServizioApplicativo.getIdSoggettoProprietario());
				bf.append(labelSA);
			}catch(Exception e) {
				bf.append(idServizioApplicativo.getIdSoggettoProprietario().toString()+"_"+idServizioApplicativo.getNome());
			}
		}
		else {
			bf.append(idServizioApplicativo.getIdSoggettoProprietario().toString()+"_"+idServizioApplicativo.getNome());
		}
		
		Set<ErrorsHandlerCostant> keys = whereIsInUso.keySet();
		String msg = "Applicativo '" +bf.toString() +"' non eliminabile perch&egrave; :"+separator;
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
			case AUTORIZZAZIONE_MAPPING:
				if ( messages!=null && messages.size() > 0) {
					msg += "utilizzato nel Controllo degli Accessi (Autorizzazione Trasporto - Richiedenti Autorizzati) delle Fruizioni: " + DBOggettiInUsoUtils.formatList(messages,separator) + separator;
				}
				break;
			case AUTORIZZAZIONE:
				if ( messages!=null && messages.size() > 0) {
					msg += "utilizzato nelle Porte Outbound (Controllo degli Accessi - Autorizzazione Trasporto -  Richiedenti Autorizzati): " + DBOggettiInUsoUtils.formatList(messages,separator) + separator;
				}
				break;
			case AUTORIZZAZIONE_MAPPING_PA:
				if ( messages!=null && messages.size() > 0) {
					msg += "utilizzato nel Controllo degli Accessi (Autorizzazione Trasporto - Richiedenti Autorizzati) delle Erogazioni: " + DBOggettiInUsoUtils.formatList(messages,separator) + separator;
				}
				break;
			case AUTORIZZAZIONE_PA:
				if ( messages!=null && messages.size() > 0) {
					msg += "utilizzato nelle Porte Inbound (Controllo degli Accessi - Autorizzazione Trasporto - Richiedenti Autorizzati): " + DBOggettiInUsoUtils.formatList(messages,separator) + separator;
				}
				break;
			case AUTORIZZAZIONE_MAPPING_PA_MODI:
				if ( messages!=null && messages.size() > 0) {
					msg += "utilizzato nel Controllo degli Accessi (Autorizzazione Messaggio - Richiedenti Autorizzati) delle Erogazioni: " + DBOggettiInUsoUtils.formatList(messages,separator) + separator;
				}
				break;
			case AUTORIZZAZIONE_PA_MODI:
				if ( messages!=null && messages.size() > 0) {
					msg += "utilizzato nelle Porte Inbound (Controllo degli Accessi - Autorizzazione Messaggio - Richiedenti Autorizzati): " + DBOggettiInUsoUtils.formatList(messages,separator) + separator;
				}
				break;
				
			case AUTORIZZAZIONE_TOKEN_MAPPING_PD:
				if ( messages!=null && messages.size() > 0) {
					msg += "utilizzato nel Controllo degli Accessi (Autorizzazione Token - Richiedenti Autorizzati) delle Fruizioni: " + DBOggettiInUsoUtils.formatList(messages,separator) + separator;
				}
				break;
			case AUTORIZZAZIONE_TOKEN_PD:
				if ( messages!=null && messages.size() > 0) {
					msg += "utilizzato nelle Porte Outbound (Controllo degli Accessi - Autorizzazione Token - Richiedenti Autorizzati): " + DBOggettiInUsoUtils.formatList(messages,separator) + separator;
				}
				break;
			case AUTORIZZAZIONE_TOKEN_MAPPING_PA:
				if ( messages!=null && messages.size() > 0) {
					msg += "utilizzato nel Controllo degli Accessi (Autorizzazione Token - Richiedenti Autorizzati) delle Erogazioni: " + DBOggettiInUsoUtils.formatList(messages,separator) + separator;
				}
				break;
			case AUTORIZZAZIONE_TOKEN_PA:
				if ( messages!=null && messages.size() > 0) {
					msg += "utilizzato nelle Porte Inbound (Controllo degli Accessi - Autorizzazione Token - Richiedenti Autorizzati): " + DBOggettiInUsoUtils.formatList(messages,separator) + separator;
				}
				break;
				
			case RUOLI_MAPPING:
				if ( messages!=null && messages.size() > 0) {
					msg += "compatibile con l'Autorizzazione Trasporto per Ruoli indicata nel Controllo degli Accessi delle Fruizioni: " + DBOggettiInUsoUtils.formatList(messages,separator) + separator;
				}
				break;
			case RUOLI:
				if ( messages!=null && messages.size() > 0) {
					msg += "compatibile con l'Autorizzazione Trasporto per Ruoli indicata nel Controllo degli Accessi delle Porte Outbound: " + DBOggettiInUsoUtils.formatList(messages,separator) + separator;
				}
				break;
			case RUOLI_MAPPING_PA:
				if ( messages!=null && messages.size() > 0) {
					msg += "compatibile con l'Autorizzazione Trasporto per Ruoli indicata nel Controllo degli Accessi delle Erogazioni: " + DBOggettiInUsoUtils.formatList(messages,separator) + separator;
				}
				break;
			case RUOLI_PA:
				if ( messages!=null && messages.size() > 0) {
					msg += "compatibile con l'Autorizzazione Trasporto per Ruoli indicata nel Controllo degli Accessi delle Porte Inbound: " + DBOggettiInUsoUtils.formatList(messages,separator) + separator;
				}
				break;
				
			case RUOLI_TOKEN_MAPPING_PD:
				if ( messages!=null && messages.size() > 0) {
					msg += "compatibile con l'Autorizzazione Token per Ruoli indicata nel Controllo degli Accessi delle Fruizioni: " + DBOggettiInUsoUtils.formatList(messages,separator) + separator;
				}
				break;
			case RUOLI_TOKEN_PD:
				if ( messages!=null && messages.size() > 0) {
					msg += "compatibile con l'Autorizzazione Token per Ruoli indicata nel Controllo degli Accessi delle Porte Outbound: " + DBOggettiInUsoUtils.formatList(messages,separator) + separator;
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
				
			case IN_USO_IN_MAPPING_EROGAZIONE_PA:
				if ( messages!=null && messages.size() > 0 ) {
					msg += "utilizzato come applicativo server nei connettori delle Erogazioni: " + DBOggettiInUsoUtils.formatList(messages,separator) + separator;
				}
				break;
			case IN_USO_IN_PORTE_APPLICATIVE:
				if ( messages!=null && messages.size() > 0 ) {
					msg += "in uso in Porte Inbound: " + DBOggettiInUsoUtils.formatList(messages,separator) + separator;
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
			case TRASFORMAZIONE_MAPPING_PD:
				if ( messages!=null && messages.size() > 0) {
					msg += "utilizzato nel criterio di applicabilità della Trasformazione (Applicativi) per le Fruizioni: " + DBOggettiInUsoUtils.formatList(messages,separator) + separator;
				}
				break;
			case TRASFORMAZIONE_PD:
				if ( messages!=null && messages.size() > 0) {
					msg += "utilizzato nelle Porte Outbound (Criterio di applicabilità della Trasformazione - Applicativi): " + DBOggettiInUsoUtils.formatList(messages,separator) + separator;
				}
				break;
			case TRASFORMAZIONE_MAPPING_PA:
				if ( messages!=null && messages.size() > 0) {
					msg += "utilizzato nel criterio di applicabilità della Trasformazione (Applicativi) per le Erogazioni: " + DBOggettiInUsoUtils.formatList(messages,separator) + separator;
				}
				break;
			case TRASFORMAZIONE_PA:
				if ( messages!=null && messages.size() > 0) {
					msg += "utilizzato nelle Porte Inbound (Criterio di applicabilità della Trasformazione - Applicativi): " + DBOggettiInUsoUtils.formatList(messages,separator) + separator;
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
