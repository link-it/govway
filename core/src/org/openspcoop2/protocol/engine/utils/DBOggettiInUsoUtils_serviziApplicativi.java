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
import org.openspcoop2.core.constants.ProprietariProtocolProperty;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.mapping.DBMappingUtils;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.engine.constants.Costanti;
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

		if(isRegistroServiziLocale) {
			// nop
		}
		
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


			List<String> autorizzazionePDMappingList = whereIsInUso.get(ErrorsHandlerCostant.AUTORIZZAZIONE_MAPPING);
			List<String> autorizzazionePDList = whereIsInUso.get(ErrorsHandlerCostant.AUTORIZZAZIONE);
			List<String> autorizzazionePAMappingList = whereIsInUso.get(ErrorsHandlerCostant.AUTORIZZAZIONE_MAPPING_PA);
			List<String> autorizzazionePAList = whereIsInUso.get(ErrorsHandlerCostant.AUTORIZZAZIONE_PA);
			List<String> autorizzazionePAMappingModiList = whereIsInUso.get(ErrorsHandlerCostant.AUTORIZZAZIONE_MAPPING_PA_MODI);
			List<String> autorizzazionePAModiList = whereIsInUso.get(ErrorsHandlerCostant.AUTORIZZAZIONE_PA_MODI);
			List<String> autorizzazioneTokenPDMappingList = whereIsInUso.get(ErrorsHandlerCostant.AUTORIZZAZIONE_TOKEN_MAPPING_PD);
			List<String> autorizzazioneTokenPDList = whereIsInUso.get(ErrorsHandlerCostant.AUTORIZZAZIONE_TOKEN_PD);
			List<String> autorizzazioneTokenPAMappingList = whereIsInUso.get(ErrorsHandlerCostant.AUTORIZZAZIONE_TOKEN_MAPPING_PA);
			List<String> autorizzazioneTokenPAList = whereIsInUso.get(ErrorsHandlerCostant.AUTORIZZAZIONE_TOKEN_PA);
			List<String> ruoliPDMappingList = whereIsInUso.get(ErrorsHandlerCostant.RUOLI_MAPPING);
			List<String> ruoliPDList = whereIsInUso.get(ErrorsHandlerCostant.RUOLI);
			List<String> ruoliPAMappingList = whereIsInUso.get(ErrorsHandlerCostant.RUOLI_MAPPING_PA);
			List<String> ruoliPAList = whereIsInUso.get(ErrorsHandlerCostant.RUOLI_PA);
			List<String> ruoliTokenPDMappingList = whereIsInUso.get(ErrorsHandlerCostant.RUOLI_TOKEN_MAPPING_PD);
			List<String> ruoliTokenPDList = whereIsInUso.get(ErrorsHandlerCostant.RUOLI_TOKEN_PD);
			List<String> ruoliTokenPAMappingList = whereIsInUso.get(ErrorsHandlerCostant.RUOLI_TOKEN_MAPPING_PA);
			List<String> ruoliTokenPAList = whereIsInUso.get(ErrorsHandlerCostant.RUOLI_TOKEN_PA);
			List<String> ruoliTokenPAMappingModiList = whereIsInUso.get(ErrorsHandlerCostant.RUOLI_TOKEN_MAPPING_PA_MODI);
			List<String> ruoliTokenPAModiList = whereIsInUso.get(ErrorsHandlerCostant.RUOLI_TOKEN_PA_MODI);
			List<String> porteApplicativeList = whereIsInUso.get(ErrorsHandlerCostant.IN_USO_IN_PORTE_APPLICATIVE);
			List<String> porteApplicativeMappingList = whereIsInUso.get(ErrorsHandlerCostant.IN_USO_IN_MAPPING_EROGAZIONE_PA);
			List<String> ctList = whereIsInUso.get(ErrorsHandlerCostant.CONTROLLO_TRAFFICO);
			List<String> allarmeList = whereIsInUso.get(ErrorsHandlerCostant.ALLARMI);
			List<String> trasformazionePDMappingList = whereIsInUso.get(ErrorsHandlerCostant.TRASFORMAZIONE_MAPPING_PD);
			List<String> trasformazionePDList = whereIsInUso.get(ErrorsHandlerCostant.TRASFORMAZIONE_PD);
			List<String> trasformazionePAMappingList = whereIsInUso.get(ErrorsHandlerCostant.TRASFORMAZIONE_MAPPING_PA);
			List<String> trasformazionePAList = whereIsInUso.get(ErrorsHandlerCostant.TRASFORMAZIONE_PA);
			List<String> modiSignalHubList = whereIsInUso.get(ErrorsHandlerCostant.IS_RIFERITA_MODI_SIGNAL_HUB);
			
			if (autorizzazionePDMappingList == null) {
				autorizzazionePDMappingList = new ArrayList<>();
				whereIsInUso.put(ErrorsHandlerCostant.AUTORIZZAZIONE_MAPPING, autorizzazionePDMappingList);
			}
			if (autorizzazionePDList == null) {
				autorizzazionePDList = new ArrayList<>();
				whereIsInUso.put(ErrorsHandlerCostant.AUTORIZZAZIONE, autorizzazionePDList);
			}
			if (autorizzazionePAMappingList == null) {
				autorizzazionePAMappingList = new ArrayList<>();
				whereIsInUso.put(ErrorsHandlerCostant.AUTORIZZAZIONE_MAPPING_PA, autorizzazionePAMappingList);
			}
			if (autorizzazionePAList == null) {
				autorizzazionePAList = new ArrayList<>();
				whereIsInUso.put(ErrorsHandlerCostant.AUTORIZZAZIONE_PA, autorizzazionePAList);
			}
			if (autorizzazionePAMappingModiList == null) {
				autorizzazionePAMappingModiList = new ArrayList<>();
				whereIsInUso.put(ErrorsHandlerCostant.AUTORIZZAZIONE_MAPPING_PA_MODI, autorizzazionePAMappingModiList);
			}
			if (autorizzazionePAModiList == null) {
				autorizzazionePAModiList = new ArrayList<>();
				whereIsInUso.put(ErrorsHandlerCostant.AUTORIZZAZIONE_PA_MODI, autorizzazionePAModiList);
			}
			
			if (autorizzazioneTokenPDMappingList == null) {
				autorizzazioneTokenPDMappingList = new ArrayList<>();
				whereIsInUso.put(ErrorsHandlerCostant.AUTORIZZAZIONE_TOKEN_MAPPING_PD, autorizzazioneTokenPDMappingList);
			}
			if (autorizzazioneTokenPDList == null) {
				autorizzazioneTokenPDList = new ArrayList<>();
				whereIsInUso.put(ErrorsHandlerCostant.AUTORIZZAZIONE_TOKEN_PD, autorizzazioneTokenPDList);
			}
			if (autorizzazioneTokenPAMappingList == null) {
				autorizzazioneTokenPAMappingList = new ArrayList<>();
				whereIsInUso.put(ErrorsHandlerCostant.AUTORIZZAZIONE_TOKEN_MAPPING_PA, autorizzazioneTokenPAMappingList);
			}
			if (autorizzazioneTokenPAList == null) {
				autorizzazioneTokenPAList = new ArrayList<>();
				whereIsInUso.put(ErrorsHandlerCostant.AUTORIZZAZIONE_TOKEN_PA, autorizzazioneTokenPAList);
			}
			
			if (ruoliPDMappingList == null) {
				ruoliPDMappingList = new ArrayList<>();
				whereIsInUso.put(ErrorsHandlerCostant.RUOLI_MAPPING, ruoliPDMappingList);
			}
			if (ruoliPDList == null) {
				ruoliPDList = new ArrayList<>();
				whereIsInUso.put(ErrorsHandlerCostant.RUOLI, ruoliPDList);
			}
			if (ruoliPAMappingList == null) {
				ruoliPAMappingList = new ArrayList<>();
				whereIsInUso.put(ErrorsHandlerCostant.RUOLI_MAPPING_PA, ruoliPAMappingList);
			}
			if (ruoliPAList == null) {
				ruoliPAList = new ArrayList<>();
				whereIsInUso.put(ErrorsHandlerCostant.RUOLI_PA, ruoliPAList);
			}
			
			if (ruoliTokenPDMappingList == null) {
				ruoliTokenPDMappingList = new ArrayList<>();
				whereIsInUso.put(ErrorsHandlerCostant.RUOLI_TOKEN_MAPPING_PD, ruoliPDMappingList);
			}
			if (ruoliTokenPDList == null) {
				ruoliTokenPDList = new ArrayList<>();
				whereIsInUso.put(ErrorsHandlerCostant.RUOLI_TOKEN_PD, ruoliTokenPDList);
			}
			if (ruoliTokenPAMappingList == null) {
				ruoliTokenPAMappingList = new ArrayList<>();
				whereIsInUso.put(ErrorsHandlerCostant.RUOLI_TOKEN_MAPPING_PA, ruoliTokenPAMappingList);
			}
			if (ruoliTokenPAList == null) {
				ruoliTokenPAList = new ArrayList<>();
				whereIsInUso.put(ErrorsHandlerCostant.RUOLI_TOKEN_PA, ruoliTokenPAList);
			}
			if (ruoliTokenPAMappingModiList == null) {
				ruoliTokenPAMappingModiList = new ArrayList<>();
				whereIsInUso.put(ErrorsHandlerCostant.RUOLI_TOKEN_MAPPING_PA_MODI, ruoliTokenPAMappingModiList);
			}
			if (ruoliTokenPAModiList == null) {
				ruoliTokenPAModiList = new ArrayList<>();
				whereIsInUso.put(ErrorsHandlerCostant.RUOLI_TOKEN_PA_MODI, ruoliTokenPAModiList);
			}
			
			if (porteApplicativeList == null) {
				porteApplicativeList = new ArrayList<>();
				whereIsInUso.put(ErrorsHandlerCostant.IN_USO_IN_PORTE_APPLICATIVE, porteApplicativeList);
			}
			if (porteApplicativeMappingList == null) {
				porteApplicativeMappingList = new ArrayList<>();
				whereIsInUso.put(ErrorsHandlerCostant.IN_USO_IN_MAPPING_EROGAZIONE_PA, porteApplicativeMappingList);
			}
			if (ctList == null) {
				ctList = new ArrayList<>();
				whereIsInUso.put(ErrorsHandlerCostant.CONTROLLO_TRAFFICO, ctList);
			}
			if (allarmeList == null) {
				allarmeList = new ArrayList<>();
				whereIsInUso.put(ErrorsHandlerCostant.ALLARMI, allarmeList);
			}
			if (trasformazionePDMappingList == null) {
				trasformazionePDMappingList = new ArrayList<>();
				whereIsInUso.put(ErrorsHandlerCostant.TRASFORMAZIONE_MAPPING_PD, trasformazionePDMappingList);
			}
			if (trasformazionePDList == null) {
				trasformazionePDList = new ArrayList<>();
				whereIsInUso.put(ErrorsHandlerCostant.TRASFORMAZIONE_PD, trasformazionePDList);
			}
			if (trasformazionePAMappingList == null) {
				trasformazionePAMappingList = new ArrayList<>();
				whereIsInUso.put(ErrorsHandlerCostant.TRASFORMAZIONE_MAPPING_PA, trasformazionePAMappingList);
			}
			if (trasformazionePAList == null) {
				trasformazionePAList = new ArrayList<>();
				whereIsInUso.put(ErrorsHandlerCostant.TRASFORMAZIONE_PA, trasformazionePAList);
			}
			
			if (modiSignalHubList == null) {
				modiSignalHubList = new ArrayList<>();
				whereIsInUso.put(ErrorsHandlerCostant.IS_RIFERITA_MODI_SIGNAL_HUB, modiSignalHubList);
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
					autorizzazionePDMappingList.add(resultPorta.label);
				}
				else {
					autorizzazionePDList.add(resultPorta.label);
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
					autorizzazioneTokenPDMappingList.add(resultPorta.label);
				}
				else {
					autorizzazioneTokenPDList.add(resultPorta.label);
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
						autorizzazionePAMappingModiList.add(resultPorta.label);
					else
						autorizzazionePAMappingList.add(resultPorta.label);
				}
				else {
					if(resultPorta.erogazioneModi)
						autorizzazionePAModiList.add(resultPorta.label);
					else
						autorizzazionePAList.add(resultPorta.label);
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
					autorizzazioneTokenPAMappingList.add(resultPorta.label);
				}
				else {
					autorizzazioneTokenPAList.add(resultPorta.label);
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
				
					checkServizioApplicativoRuoloInUsoInPorteDelegate(con, tipoDB, normalizeObjectIds,
							CostantiDB.PORTE_DELEGATE_RUOLI,
							listRuoliSA,
							ruoliPDMappingList, ruoliPDList);
					
					checkServizioApplicativoRuoloInUsoInPorteDelegate(con, tipoDB, normalizeObjectIds,
							CostantiDB.PORTE_DELEGATE_TOKEN_RUOLI,
							listRuoliSA,
							ruoliTokenPDMappingList, ruoliTokenPDList);
									
					boolean isInUsoRuoli = checkServizioApplicativoRuoloInUsoInPorteApplicative(con, tipoDB, normalizeObjectIds,
							CostantiDB.PORTE_APPLICATIVE_RUOLI,
							listRuoliSA,
							ruoliPAMappingList, ruoliPAList,
							ruoliPAMappingList, ruoliPAList);
					if(isInUsoRuoli) {
						isInUso = true;
					}
					
					boolean isInUsoRuoliToken = checkServizioApplicativoRuoloInUsoInPorteApplicative(con, tipoDB, normalizeObjectIds,
							CostantiDB.PORTE_APPLICATIVE_TOKEN_RUOLI,
							listRuoliSA,
							ruoliTokenPAMappingList, ruoliTokenPAList,
							ruoliTokenPAMappingModiList, ruoliTokenPAModiList);
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
					porteApplicativeMappingList.add(resultPorta.label);
				}
				else {
					porteApplicativeList.add(resultPorta.label);
				}
				isInUso = true;
			}
			risultato.close();
			stmt.close();

			
			// Controllo che il servizio aplicativo non sia associato a policy di controllo del traffico o allarmi
			
			int max = 2;
			if(!CostantiDB.isAllarmiEnabled()) {
				max=1;
			}
			for (int i = 0; i < max; i++) {
				
				String tabella = CostantiDB.CONTROLLO_TRAFFICO_ACTIVE_POLICY;
				String identificativo_column = "active_policy_id";
				String alias_column = "policy_alias";
				List<String> list = ctList;
				String oggetto = "Policy";
				boolean allarmi = false;
				if(i==1) {
					tabella = CostantiDB.ALLARMI;
					identificativo_column = "nome";
					alias_column = "alias";
					list = allarmeList;
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
					trasformazionePDMappingList.add(resultPorta.label);
				}
				else {
					trasformazionePDList.add(resultPorta.label);
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
					trasformazionePAMappingList.add(resultPorta.label);
				}
				else {
					trasformazionePAList.add(resultPorta.label);
				}
				isInUso = true;
			}
			risultato.close();
			stmt.close();
			
			
			
			
			// ** Controllo che non sia riferito dalla configurazione SignalHub **		
			
			
			if(Costanti.MODIPA_PROTOCOL_NAME.equals(idServizioApplicativo.getIdSoggettoProprietario().getTipo())) {
			
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI);
				sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
				sqlQueryObject.addFromTable(CostantiDB.PROTOCOL_PROPERTIES);
				sqlQueryObject.addSelectAliasField(CostantiDB.SERVIZI,"tipo_servizio","tipoServizio");
				sqlQueryObject.addSelectAliasField(CostantiDB.SERVIZI,"nome_servizio","nomeServizio");
				sqlQueryObject.addSelectAliasField(CostantiDB.SERVIZI,"versione_servizio","versioneServizio");
				sqlQueryObject.addSelectAliasField(CostantiDB.SOGGETTI,"tipo_soggetto","tipoSoggetto");
				sqlQueryObject.addSelectAliasField(CostantiDB.SOGGETTI,"nome_soggetto","nomeSoggetto");
				sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI + ".id_soggetto = " + CostantiDB.SOGGETTI + ".id");
				sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI + ".id = " + CostantiDB.PROTOCOL_PROPERTIES + ".id_proprietario");
				sqlQueryObject.addWhereCondition(CostantiDB.PROTOCOL_PROPERTIES + ".tipo_proprietario=?");
				sqlQueryObject.addWhereCondition(CostantiDB.PROTOCOL_PROPERTIES + ".name=?");
				sqlQueryObject.addWhereCondition(CostantiDB.PROTOCOL_PROPERTIES + ".value_string=?");
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
				stmt = con.prepareStatement(queryString);
				int index = 1;
				stmt.setString(index++, ProprietariProtocolProperty.ACCORDO_SERVIZIO_PARTE_SPECIFICA.name());
				stmt.setString(index++, CostantiDB.MODIPA_API_IMPL_INFO_SIGNAL_HUB_PUBLISHER_SA_ID);
				stmt.setString(index++, idServizioApplicativo.getNome());
				risultato = stmt.executeQuery();
				while(risultato.next()) {
					
					IDServizio idS = IDServizioFactory.getInstance().getIDServizioFromValues(risultato.getString("tipoServizio"), risultato.getString("nomeServizio"), 
							risultato.getString("tipoSoggetto"), risultato.getString("nomeSoggetto"), 
							risultato.getInt("versioneServizio"));
					
					// trovata
					IDPortaApplicativa idPA = DBMappingUtils.getIDPortaApplicativaAssociataDefault(idS, con, tipoDB);
					if(idPA!=null) {
						ResultPorta resultPorta = DBOggettiInUsoUtils.formatPortaApplicativa(idPA.getNome(), tipoDB, con, normalizeObjectIds);
						modiSignalHubList.add(resultPorta.label);
					}
					else {
						modiSignalHubList.add(idS.toString());
					}
					
					isInUso = true;
					
				}
				risultato.close();
				stmt.close();
					
			}
			
			
			return isInUso;

		} catch (Exception se) {
			throw new UtilsException("[DBOggettiInUsoUtils::" + nomeMetodo + "] Exception: " + se.getMessage(),se);
		} finally {
			// Chiudo statement and resultset
			JDBCUtilities.closeResources(risultato2, stmt2);
			JDBCUtilities.closeResources(risultato, stmt);
		}
	}
	
	private static boolean checkServizioApplicativoRuoloInUsoInPorteDelegate(Connection con, String tipoDB, boolean normalizeObjectIds,
			String nomeTabella,
			List<String> listRuoliSA,
			List<String> ruoliPDmappingList, List<String> ruoliPDlist) throws Exception {
		boolean isInUso = false;
		PreparedStatement stmt = null;
		ResultSet risultato = null;
		try {
			
			List<String> distinctPorteDelegate = new ArrayList<>();
			List<String> verificaPorteDelegateAll = new ArrayList<>();
			
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
					String ruoloMatch = risultato.getString("ruoli_match");
					if(RuoloTipoMatch.ANY.getValue().equals(ruoloMatch)) {
						if(distinctPorteDelegate.contains(nome)) {
							continue;
						}
						else {
							distinctPorteDelegate.add(nome);
						}
						ResultPorta resultPorta = DBOggettiInUsoUtils.formatPortaDelegata(nome, tipoDB, con, normalizeObjectIds);
						if(resultPorta.mapping) {
							ruoliPDmappingList.add(resultPorta.label);
						}
						else {
							ruoliPDlist.add(resultPorta.label);
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
								ruoliPDmappingList.add(resultPorta.label);
							}
							else {
								ruoliPDlist.add(resultPorta.label);
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
	
	private static boolean checkServizioApplicativoRuoloInUsoInPorteApplicative(Connection con, String tipoDB, boolean normalizeObjectIds,
			String nomeTabella,
			List<String> listRuoliSA,
			List<String> ruoliPAmappingList, List<String> ruoliPAlist,
			List<String> ruoliPAmappingModiList, List<String> ruoliPAmodiList) throws Exception {
		boolean isInUso = false;
		PreparedStatement stmt = null;
		ResultSet risultato = null;
		try {
			
			List<String> distinctPorteApplicative = new ArrayList<>();
			List<String> verificaPorteApplicativeAll = new ArrayList<>();
			
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
					String ruoloMatch = risultato.getString("ruoli_match");
					if(RuoloTipoMatch.ANY.getValue().equals(ruoloMatch)) {
						if(distinctPorteApplicative.contains(nome)) {
							continue;
						}
						else {
							distinctPorteApplicative.add(nome);
						}
						ResultPorta resultPorta = DBOggettiInUsoUtils.formatPortaApplicativa(nome, tipoDB, con, normalizeObjectIds);
						if(resultPorta.mapping) {
							if(resultPorta.erogazioneModi)
								ruoliPAmappingModiList.add(resultPorta.label);
							else
								ruoliPAmappingList.add(resultPorta.label);
						}
						else {
							if(resultPorta.erogazioneModi)
								ruoliPAmodiList.add(resultPorta.label);
							else
								ruoliPAlist.add(resultPorta.label);
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
									ruoliPAmappingModiList.add(resultPorta.label);
								else
									ruoliPAmappingList.add(resultPorta.label);
							}
							else {
								if(resultPorta.erogazioneModi)
									ruoliPAmodiList.add(resultPorta.label);
								else
									ruoliPAlist.add(resultPorta.label);
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
		StringBuilder msgBuilder = new StringBuilder("Applicativo '" +bf.toString() +"' non eliminabile perch&egrave; :"+separator);
		if(!prefix){
			msgBuilder = new StringBuilder("");
		}
		String separatorCategorie = "";
		if(whereIsInUso.size()>1) {
			separatorCategorie = separator;
		}
		for (ErrorsHandlerCostant key : keys) {
			List<String> messages = whereIsInUso.get(key);

			if ( messages!=null && !messages.isEmpty()) {
				msgBuilder.append(separatorCategorie);
			}
			
			switch (key) {
			case AUTORIZZAZIONE_MAPPING:
				if ( messages!=null && !messages.isEmpty()) {
					msgBuilder.append("utilizzato nel Controllo degli Accessi (Autorizzazione Trasporto - Richiedenti Autorizzati) delle Fruizioni: " + DBOggettiInUsoUtils.formatList(messages,separator) + separator);
				}
				break;
			case AUTORIZZAZIONE:
				if ( messages!=null && !messages.isEmpty()) {
					msgBuilder.append("utilizzato nelle Porte Outbound (Controllo degli Accessi - Autorizzazione Trasporto -  Richiedenti Autorizzati): " + DBOggettiInUsoUtils.formatList(messages,separator) + separator);
				}
				break;
			case AUTORIZZAZIONE_MAPPING_PA:
				if ( messages!=null && !messages.isEmpty()) {
					msgBuilder.append("utilizzato nel Controllo degli Accessi (Autorizzazione Trasporto - Richiedenti Autorizzati) delle Erogazioni: " + DBOggettiInUsoUtils.formatList(messages,separator) + separator);
				}
				break;
			case AUTORIZZAZIONE_PA:
				if ( messages!=null && !messages.isEmpty()) {
					msgBuilder.append("utilizzato nelle Porte Inbound (Controllo degli Accessi - Autorizzazione Trasporto - Richiedenti Autorizzati): " + DBOggettiInUsoUtils.formatList(messages,separator) + separator);
				}
				break;
			case AUTORIZZAZIONE_MAPPING_PA_MODI:
				if ( messages!=null && !messages.isEmpty()) {
					msgBuilder.append("utilizzato nel Controllo degli Accessi (Autorizzazione Messaggio - Richiedenti Autorizzati) delle Erogazioni: " + DBOggettiInUsoUtils.formatList(messages,separator) + separator);
				}
				break;
			case AUTORIZZAZIONE_PA_MODI:
				if ( messages!=null && !messages.isEmpty()) {
					msgBuilder.append("utilizzato nelle Porte Inbound (Controllo degli Accessi - Autorizzazione Messaggio - Richiedenti Autorizzati): " + DBOggettiInUsoUtils.formatList(messages,separator) + separator);
				}
				break;
				
			case AUTORIZZAZIONE_TOKEN_MAPPING_PD:
				if ( messages!=null && !messages.isEmpty()) {
					msgBuilder.append("utilizzato nel Controllo degli Accessi (Autorizzazione Token - Richiedenti Autorizzati) delle Fruizioni: " + DBOggettiInUsoUtils.formatList(messages,separator) + separator);
				}
				break;
			case AUTORIZZAZIONE_TOKEN_PD:
				if ( messages!=null && !messages.isEmpty()) {
					msgBuilder.append("utilizzato nelle Porte Outbound (Controllo degli Accessi - Autorizzazione Token - Richiedenti Autorizzati): " + DBOggettiInUsoUtils.formatList(messages,separator) + separator);
				}
				break;
			case AUTORIZZAZIONE_TOKEN_MAPPING_PA:
				if ( messages!=null && !messages.isEmpty()) {
					msgBuilder.append("utilizzato nel Controllo degli Accessi (Autorizzazione Token - Richiedenti Autorizzati) delle Erogazioni: " + DBOggettiInUsoUtils.formatList(messages,separator) + separator);
				}
				break;
			case AUTORIZZAZIONE_TOKEN_PA:
				if ( messages!=null && !messages.isEmpty()) {
					msgBuilder.append("utilizzato nelle Porte Inbound (Controllo degli Accessi - Autorizzazione Token - Richiedenti Autorizzati): " + DBOggettiInUsoUtils.formatList(messages,separator) + separator);
				}
				break;
				
			case RUOLI_MAPPING:
				if ( messages!=null && !messages.isEmpty()) {
					msgBuilder.append("compatibile con l'Autorizzazione Trasporto per Ruoli indicata nel Controllo degli Accessi delle Fruizioni: " + DBOggettiInUsoUtils.formatList(messages,separator) + separator);
				}
				break;
			case RUOLI:
				if ( messages!=null && !messages.isEmpty()) {
					msgBuilder.append("compatibile con l'Autorizzazione Trasporto per Ruoli indicata nel Controllo degli Accessi delle Porte Outbound: " + DBOggettiInUsoUtils.formatList(messages,separator) + separator);
				}
				break;
			case RUOLI_MAPPING_PA:
				if ( messages!=null && !messages.isEmpty()) {
					msgBuilder.append("compatibile con l'Autorizzazione Trasporto per Ruoli indicata nel Controllo degli Accessi delle Erogazioni: " + DBOggettiInUsoUtils.formatList(messages,separator) + separator);
				}
				break;
			case RUOLI_PA:
				if ( messages!=null && !messages.isEmpty()) {
					msgBuilder.append("compatibile con l'Autorizzazione Trasporto per Ruoli indicata nel Controllo degli Accessi delle Porte Inbound: " + DBOggettiInUsoUtils.formatList(messages,separator) + separator);
				}
				break;
				
			case RUOLI_TOKEN_MAPPING_PD:
				if ( messages!=null && !messages.isEmpty()) {
					msgBuilder.append("compatibile con l'Autorizzazione Token per Ruoli indicata nel Controllo degli Accessi delle Fruizioni: " + DBOggettiInUsoUtils.formatList(messages,separator) + separator);
				}
				break;
			case RUOLI_TOKEN_PD:
				if ( messages!=null && !messages.isEmpty()) {
					msgBuilder.append("compatibile con l'Autorizzazione Token per Ruoli indicata nel Controllo degli Accessi delle Porte Outbound: " + DBOggettiInUsoUtils.formatList(messages,separator) + separator);
				}
				break;
			case RUOLI_TOKEN_MAPPING_PA:
				if ( messages!=null && !messages.isEmpty()) {
					msgBuilder.append("compatibile con l'Autorizzazione Token per Ruoli indicata nel Controllo degli Accessi delle Erogazioni: " + DBOggettiInUsoUtils.formatList(messages,separator) + separator);
				}
				break;
			case RUOLI_TOKEN_PA:
				if ( messages!=null && !messages.isEmpty()) {
					msgBuilder.append("compatibile con l'Autorizzazione Token per Ruoli indicata nel Controllo degli Accessi delle Porte Inbound: " + DBOggettiInUsoUtils.formatList(messages,separator) + separator);
				}
				break;
			case RUOLI_TOKEN_MAPPING_PA_MODI:
				if ( messages!=null && !messages.isEmpty()) {
					msgBuilder.append("compatibile con l'Autorizzazione Messaggio per Ruoli indicata nel Controllo degli Accessi delle Erogazioni: " + DBOggettiInUsoUtils.formatList(messages,separator) + separator);
				}
				break;
			case RUOLI_TOKEN_PA_MODI:
				if ( messages!=null && !messages.isEmpty()) {
					msgBuilder.append("compatibile con l'Autorizzazione Messaggio per Ruoli indicata nel Controllo degli Accessi delle Porte Inbound: " + DBOggettiInUsoUtils.formatList(messages,separator) + separator);
				}
				break;
				
			case IN_USO_IN_MAPPING_EROGAZIONE_PA:
				if ( messages!=null && !messages.isEmpty()) {
					msgBuilder.append("utilizzato come applicativo server nei connettori delle Erogazioni: " + DBOggettiInUsoUtils.formatList(messages,separator) + separator);
				}
				break;
			case IN_USO_IN_PORTE_APPLICATIVE:
				if ( messages!=null && !messages.isEmpty()) {
					msgBuilder.append("in uso in Porte Inbound: " + DBOggettiInUsoUtils.formatList(messages,separator) + separator);
				}
				break;
			case CONTROLLO_TRAFFICO:
				if ( messages!=null && !messages.isEmpty()) {
					msgBuilder.append("utilizzato in policy di Rate Limiting: " + DBOggettiInUsoUtils.formatList(messages,separator) + separator);
				}
				break;
			case ALLARMI:
				if ( messages!=null && !messages.isEmpty()) {
					msgBuilder.append("utilizzato in Allarmi: " + DBOggettiInUsoUtils.formatList(messages,separator) + separator);
				}
				break;
			case TRASFORMAZIONE_MAPPING_PD:
				if ( messages!=null && !messages.isEmpty()) {
					msgBuilder.append("utilizzato nel criterio di applicabilità della Trasformazione (Applicativi) per le Fruizioni: " + DBOggettiInUsoUtils.formatList(messages,separator) + separator);
				}
				break;
			case TRASFORMAZIONE_PD:
				if ( messages!=null && !messages.isEmpty()) {
					msgBuilder.append("utilizzato nelle Porte Outbound (Criterio di applicabilità della Trasformazione - Applicativi): " + DBOggettiInUsoUtils.formatList(messages,separator) + separator);
				}
				break;
			case TRASFORMAZIONE_MAPPING_PA:
				if ( messages!=null && !messages.isEmpty()) {
					msgBuilder.append("utilizzato nel criterio di applicabilità della Trasformazione (Applicativi) per le Erogazioni: " + DBOggettiInUsoUtils.formatList(messages,separator) + separator);
				}
				break;
			case TRASFORMAZIONE_PA:
				if ( messages!=null && !messages.isEmpty()) {
					msgBuilder.append("utilizzato nelle Porte Inbound (Criterio di applicabilità della Trasformazione - Applicativi): " + DBOggettiInUsoUtils.formatList(messages,separator) + separator);
				}
				break;
				
			case IS_RIFERITA_MODI_SIGNAL_HUB:
				if ( messages!=null && !messages.isEmpty() ) {
					msgBuilder.append("riferito nella configurazione 'SignalHub' dell'erogazione: " + DBOggettiInUsoUtils.formatList(messages,separator) + separator);
				}
				break;				
				
			default:
				msgBuilder.append("utilizzato in oggetto non codificato ("+key+")"+separator);
				break;
			}

		}// chiudo for

		return msgBuilder.toString();
	}
	
}
