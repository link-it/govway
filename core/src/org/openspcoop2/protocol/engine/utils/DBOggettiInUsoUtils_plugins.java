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

package org.openspcoop2.protocol.engine.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openspcoop2.core.allarmi.constants.RuoloPorta;
import org.openspcoop2.core.commons.DBUtils;
import org.openspcoop2.core.commons.ErrorsHandlerCostant;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.controllo_traffico.constants.TipoFiltroApplicativo;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.jdbc.JDBCParameterUtilities;
import org.openspcoop2.utils.jdbc.JDBCUtilities;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.LikeConfig;
import org.openspcoop2.utils.sql.SQLObjectFactory;

/**
 * DBOggettiInUsoUtils_plugins
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class DBOggettiInUsoUtils_plugins {

	protected static boolean isPluginInUso(Connection con, String tipoDB, String className, String label, String tipoPlugin, String tipo, 
			Map<ErrorsHandlerCostant, List<String>> whereIsInUso, boolean normalizeObjectIds) throws UtilsException {

		String nomeMetodo = "isPluginInUso";

		PreparedStatement stmt = null;
		ResultSet risultato = null;
		PreparedStatement stmt2 = null;
		ResultSet risultato2 = null;
		String queryString;
		try {
			boolean isInUso = false;

			long idPluginLong = DBUtils.getIdPlugin(className, label, tipoPlugin, tipo,	con, tipoDB);
			if(idPluginLong<=0){
				throw new UtilsException("Plugin con Label ["+label+"] non trovato");
			}

			if("CONNETTORE".equals(tipoPlugin)) {
				
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.CONNETTORI);
				sqlQueryObject.addSelectField("id");
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addWhereCondition("endpointtype=?");
				sqlQueryObject.addWhereCondition("custom=?");
				queryString = sqlQueryObject.createSQLQuery();
				stmt = con.prepareStatement(queryString);
				stmt.setString(1, tipo);
				stmt.setInt(2, CostantiDB.TRUE);
				risultato = stmt.executeQuery();
				List<Long> idConnettori = new ArrayList<Long>();
				while (risultato.next()){
					long idConnettore = risultato.getLong("id");
					idConnettori.add(idConnettore);
				}
				risultato.close();
				stmt.close();
				
				if(idConnettori!=null && !idConnettori.isEmpty()) {
					
					isInUso = true; // già è in uso... anche se fallisse sotto il mapping
				
				}
				
				/*
				 * Le seguenti liste vengono inizializzate dentro il metodo.
				 * 
				 * List<String> connettorePA_list = whereIsInUso.get(ErrorsHandlerCostant.CONNETTORE_PA);
				 * List<String> connettorePD_list = whereIsInUso.get(ErrorsHandlerCostant.CONNETTORE_PD);
				 * List<String> mapping_connettorePA_list = whereIsInUso.get(ErrorsHandlerCostant.CONNETTORE_MAPPING_PA);
				 * List<String> mapping_connettorePD_list = whereIsInUso.get(ErrorsHandlerCostant.CONNETTORE_MAPPING_PD);
				 **/
				DBOggettiInUsoUtils.formatConnettori(idConnettori,whereIsInUso, con, normalizeObjectIds, tipoDB);
			}
			
			else if("AUTENTICAZIONE".equals(tipoPlugin) || 
					"AUTORIZZAZIONE".equals(tipoPlugin) || 
					"AUTORIZZAZIONE_CONTENUTI".equals(tipoPlugin) ||
					"INTEGRAZIONE".equals(tipoPlugin) ||
					"BEHAVIOUR".equals(tipoPlugin)) {
				
				boolean autenticazione = "AUTENTICAZIONE".equals(tipoPlugin);
				boolean autorizzazione = "AUTORIZZAZIONE".equals(tipoPlugin);
				boolean autorizzazione_contenuti = "AUTORIZZAZIONE_CONTENUTI".equals(tipoPlugin);
				boolean integrazione = "INTEGRAZIONE".equals(tipoPlugin);
				boolean behaviour = "BEHAVIOUR".equals(tipoPlugin);
				
				
				ErrorsHandlerCostant PD_tipoControllo_mapping = null;
				ErrorsHandlerCostant PD_tipoControllo = null;
				ErrorsHandlerCostant PA_tipoControllo_mapping = null;
				ErrorsHandlerCostant PA_tipoControllo = null;
				String colonna = "";
				if(autenticazione){
					PD_tipoControllo_mapping = ErrorsHandlerCostant.AUTENTICAZIONE_MAPPING_PD;
					PD_tipoControllo = ErrorsHandlerCostant.AUTENTICAZIONE_PD;
					PA_tipoControllo_mapping = ErrorsHandlerCostant.AUTENTICAZIONE_MAPPING_PA;
					PA_tipoControllo = ErrorsHandlerCostant.AUTENTICAZIONE_PA;
					colonna = "autenticazione";
				}
				else if(autorizzazione){
					PD_tipoControllo_mapping = ErrorsHandlerCostant.AUTORIZZAZIONE_MAPPING_PD;
					PD_tipoControllo = ErrorsHandlerCostant.AUTORIZZAZIONE_PD;
					PA_tipoControllo_mapping = ErrorsHandlerCostant.AUTORIZZAZIONE_MAPPING_PA;
					PA_tipoControllo = ErrorsHandlerCostant.AUTORIZZAZIONE_PA;
					colonna = "autorizzazione";
				}
				else if(autorizzazione_contenuti){
					PD_tipoControllo_mapping = ErrorsHandlerCostant.AUTORIZZAZIONE_CONTENUTI_MAPPING_PD;
					PD_tipoControllo = ErrorsHandlerCostant.AUTORIZZAZIONE_CONTENUTI_PD;
					PA_tipoControllo_mapping = ErrorsHandlerCostant.AUTORIZZAZIONE_CONTENUTI_MAPPING_PA;
					PA_tipoControllo = ErrorsHandlerCostant.AUTORIZZAZIONE_CONTENUTI_PA;
					colonna = "autorizzazione_contenuto";
				}
				else if(integrazione) {
					PD_tipoControllo_mapping = ErrorsHandlerCostant.INTEGRAZIONE_MAPPING_PD;
					PD_tipoControllo = ErrorsHandlerCostant.INTEGRAZIONE_PD;
					PA_tipoControllo_mapping = ErrorsHandlerCostant.INTEGRAZIONE_MAPPING_PA;
					PA_tipoControllo = ErrorsHandlerCostant.INTEGRAZIONE_PA;
					colonna = "integrazione";
				}
				else if(behaviour) {
					PA_tipoControllo_mapping = ErrorsHandlerCostant.BEHAVIOUR_MAPPING_PA;
					PA_tipoControllo = ErrorsHandlerCostant.BEHAVIOUR_PA;
					colonna = "behaviour";
				}
				
				List<String> PD_mapping_list = null;
				List<String> PD_list = null;
				if(!behaviour) {
					PD_mapping_list = whereIsInUso.get(PD_tipoControllo_mapping);
					PD_list = whereIsInUso.get(PD_tipoControllo);
				}
				List<String> PA_mapping_list = whereIsInUso.get(PA_tipoControllo_mapping);
				List<String> PA_list = whereIsInUso.get(PA_tipoControllo);
				
				if(!behaviour) {
					if (PD_mapping_list == null) {
						PD_mapping_list = new ArrayList<>();
						whereIsInUso.put(PD_tipoControllo_mapping, PD_mapping_list);
					}
					if (PD_list == null) {
						PD_list = new ArrayList<>();
						whereIsInUso.put(PD_tipoControllo, PD_list);
					}
				}
				if (PA_mapping_list == null) {
					PA_mapping_list = new ArrayList<>();
					whereIsInUso.put(PA_tipoControllo_mapping, PA_mapping_list);
				}
				if (PA_list == null) {
					PA_list = new ArrayList<>();
					whereIsInUso.put(PA_tipoControllo, PA_list);
				}
				
				int i = 0;
				if(behaviour) {
					i=1;
				}
				for (; i < 2; i++) {
					
					String table = CostantiDB.PORTE_DELEGATE;
					if(i==1) {
						table = CostantiDB.PORTE_APPLICATIVE;
					}
					
					ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
					sqlQueryObject.addFromTable(table);
					sqlQueryObject.addSelectField("nome_porta");
					sqlQueryObject.setANDLogicOperator(true);
					if(integrazione) {
						// condizione di controllo
						ISQLQueryObject sqlQueryObjectOr = SQLObjectFactory.createSQLQueryObject(tipoDB);
						sqlQueryObjectOr.setANDLogicOperator(false);
						// (integrazione == 'NOME') OR (integrazione like 'NOME,%') OR (integrazione like '%,NOME') OR (integrazione like '%,applicabilita_azioni,%')
						// CLOB sqlQueryObjectOr.addWhereCondition(integrazione = ?");
						sqlQueryObjectOr.addWhereLikeCondition(colonna, tipo, false , false);
						sqlQueryObjectOr.addWhereLikeCondition(colonna, tipo+",", LikeConfig.startsWith(false));
						sqlQueryObjectOr.addWhereLikeCondition(colonna, ","+tipo, LikeConfig.endsWith(false));
						sqlQueryObjectOr.addWhereLikeCondition(colonna, ","+tipo+",", true , false);
						sqlQueryObject.addWhereCondition(sqlQueryObjectOr.createSQLConditions());
					}
					else {
						sqlQueryObject.addWhereCondition(colonna+"=?");
					}
					queryString = sqlQueryObject.createSQLQuery();
					stmt = con.prepareStatement(queryString);
					if(!integrazione) {
						stmt.setString(1, tipo);
					}
					risultato = stmt.executeQuery();
					while (risultato.next()){
						String nome = risultato.getString("nome_porta");
						if(CostantiDB.PORTE_DELEGATE.equals(table)) {
							ResultPorta resultPorta = DBOggettiInUsoUtils.formatPortaDelegata(nome, tipoDB, con, normalizeObjectIds);
							if(resultPorta.mapping) {
								PD_mapping_list.add(resultPorta.label);
							}
							else {
								PD_list.add(resultPorta.label);
							}
						}
						else {
							ResultPorta resultPorta = DBOggettiInUsoUtils.formatPortaApplicativa(nome, tipoDB, con, normalizeObjectIds);
							if(resultPorta.mapping) {
								PA_mapping_list.add(resultPorta.label);
							}
							else {
								PA_list.add(resultPorta.label);
							}
						}
						isInUso = true;
					}
					risultato.close();
					stmt.close();
					
				}
			}
			
			else if("RATE_LIMITING".equals(tipoPlugin)) {
				
				List<String> ct_list = whereIsInUso.get(ErrorsHandlerCostant.CONTROLLO_TRAFFICO);
				if (ct_list == null) {
					ct_list = new ArrayList<>();
					whereIsInUso.put(ErrorsHandlerCostant.CONTROLLO_TRAFFICO, ct_list);
				}	
				
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.CONTROLLO_TRAFFICO_ACTIVE_POLICY);
				sqlQueryObject.addSelectField("active_policy_id");
				sqlQueryObject.addSelectField("policy_alias");
				sqlQueryObject.addSelectField("filtro_ruolo");
				sqlQueryObject.addSelectField("filtro_porta");
				sqlQueryObject.setANDLogicOperator(false); // OR
				sqlQueryObject.addWhereCondition(true, 
						CostantiDB.CONTROLLO_TRAFFICO_ACTIVE_POLICY+".filtro_key_enabled = ?",
						CostantiDB.CONTROLLO_TRAFFICO_ACTIVE_POLICY+".filtro_key_type = ?",
						sqlQueryObject.getWhereLikeCondition(CostantiDB.CONTROLLO_TRAFFICO_ACTIVE_POLICY+".filtro_key_name", tipo, false, false));
				sqlQueryObject.addWhereCondition(true, 
						CostantiDB.CONTROLLO_TRAFFICO_ACTIVE_POLICY+".group_key_enabled = ?",
						CostantiDB.CONTROLLO_TRAFFICO_ACTIVE_POLICY+".group_key_type = ?",
						sqlQueryObject.getWhereLikeCondition(CostantiDB.CONTROLLO_TRAFFICO_ACTIVE_POLICY+".group_key_name", tipo, false, false));
				sqlQueryObject.addOrderBy("filtro_ruolo");
				sqlQueryObject.addOrderBy("filtro_porta");
				queryString = sqlQueryObject.createSQLQuery();
				stmt = con.prepareStatement(queryString);
				int index = 1;
				JDBCParameterUtilities utils = new JDBCParameterUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2());
				utils.setParameter(stmt, index++, true, boolean.class);
				stmt.setString(index++, TipoFiltroApplicativo.PLUGIN_BASED.getValue());
				utils.setParameter(stmt, index++, true, boolean.class);
				stmt.setString(index++, TipoFiltroApplicativo.PLUGIN_BASED.getValue());
				risultato = stmt.executeQuery();
				while (risultato.next()) {
					
					String alias = risultato.getString("policy_alias");
					if(alias== null || "".equals(alias)) {
						alias = risultato.getString("active_policy_id");
					}
					
					String nomePorta = risultato.getString("filtro_porta");
					String filtro_ruolo = risultato.getString("filtro_ruolo");
					if(nomePorta!=null) {
						String tipoPorta = null;
						String labelPorta = null;
						if("delegata".equals(filtro_ruolo)) {
							try {
								ResultPorta resultPorta = DBOggettiInUsoUtils.formatPortaDelegata(nomePorta, tipoDB, con, normalizeObjectIds);
								if(resultPorta.mapping) {
									labelPorta = "Fruizione di Servizio "+ resultPorta.label;
								}
							}catch(Exception e) {
								tipoPorta = "Outbound";
							}
						}
						else if("applicativa".equals(filtro_ruolo)) {
							try {
								ResultPorta resultPorta = DBOggettiInUsoUtils.formatPortaApplicativa(nomePorta, tipoDB, con, normalizeObjectIds);
								if(resultPorta.mapping) {
									labelPorta = "Erogazione di Servizio "+ resultPorta.label;
								}
							}catch(Exception e) {
								tipoPorta = "Inbound";
							}
						}
						else {
							tipoPorta = filtro_ruolo;
						}
						if(labelPorta==null) {
							ct_list.add("Policy '"+alias+"' attiva nella porta '"+tipoPorta+"' '"+nomePorta+"' ");
						}
						else {
							ct_list.add("Policy '"+alias+"' attiva nella "+labelPorta);
						}
					}
					else {
						ct_list.add("Policy '"+alias+"'");
					}
	
					isInUso = true;
				}
				risultato.close();
				stmt.close();
			}
						
			else if("ALLARME".equals(tipoPlugin)) {
				List<String> allarmiPD_mapping_list = whereIsInUso.get(ErrorsHandlerCostant.ALLARMI_MAPPING_PD);
				List<String> allarmiPD_list = whereIsInUso.get(ErrorsHandlerCostant.ALLARMI_PD);
				List<String> allarmiPA_mapping_list = whereIsInUso.get(ErrorsHandlerCostant.ALLARMI_MAPPING_PA);
				List<String> allarmiPA_list = whereIsInUso.get(ErrorsHandlerCostant.ALLARMI_PA);
				List<String> allarmi_list = whereIsInUso.get(ErrorsHandlerCostant.ALLARMI);
				
				if (allarmiPD_mapping_list == null) {
					allarmiPD_mapping_list = new ArrayList<>();
					whereIsInUso.put(ErrorsHandlerCostant.ALLARMI_MAPPING_PD, allarmiPD_mapping_list);
				}
				if (allarmiPD_list == null) {
					allarmiPD_list = new ArrayList<>();
					whereIsInUso.put(ErrorsHandlerCostant.ALLARMI_PD, allarmiPD_list);
				}
				if (allarmiPA_mapping_list == null) {
					allarmiPA_mapping_list = new ArrayList<>();
					whereIsInUso.put(ErrorsHandlerCostant.ALLARMI_MAPPING_PA, allarmiPA_mapping_list);
				}
				if (allarmiPA_list == null) {
					allarmiPA_list = new ArrayList<>();
					whereIsInUso.put(ErrorsHandlerCostant.ALLARMI_PA, allarmiPA_list);
				}
				if (allarmi_list == null) {
					allarmi_list = new ArrayList<>();
					whereIsInUso.put(ErrorsHandlerCostant.ALLARMI, allarmi_list);
				}
				
				// allarmi
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.ALLARMI);
				sqlQueryObject.addSelectField("nome");
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addWhereCondition("tipo=?");
				sqlQueryObject.addWhereIsNullCondition("filtro_porta");
				queryString = sqlQueryObject.createSQLQuery();
				stmt = con.prepareStatement(queryString);
				stmt.setString(1, tipo);
				risultato = stmt.executeQuery();
				while (risultato.next()){
					String nome = risultato.getString("nome");
					allarmi_list.add(nome);
					isInUso = true;
				}
				risultato.close();
				stmt.close();
				
				
				// Porte applicative, allarmi
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.ALLARMI);
				sqlQueryObject.addSelectField("nome");
				sqlQueryObject.addSelectField("filtro_porta");
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addWhereCondition("tipo=?");
				sqlQueryObject.addWhereCondition("filtro_ruolo=?");
				sqlQueryObject.addWhereIsNotNullCondition("filtro_porta");
				queryString = sqlQueryObject.createSQLQuery();
				stmt = con.prepareStatement(queryString);
				stmt.setString(1, tipo);
				stmt.setString(2, RuoloPorta.APPLICATIVA.getValue());
				risultato = stmt.executeQuery();
				while (risultato.next()){
					String nome = risultato.getString("filtro_porta");
					ResultPorta resultPorta = DBOggettiInUsoUtils.formatPortaApplicativa(nome, tipoDB, con, normalizeObjectIds);
					if(resultPorta.mapping) {
						allarmiPA_mapping_list.add(resultPorta.label);
					}
					else {
						allarmiPA_list.add(resultPorta.label);
					}
					isInUso = true;
				}
				risultato.close();
				stmt.close();

				// Porte delegate, allarmi
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.ALLARMI);
				sqlQueryObject.addSelectField("nome");
				sqlQueryObject.addSelectField("filtro_porta");
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addWhereCondition("tipo=?");
				sqlQueryObject.addWhereCondition("filtro_ruolo=?");
				sqlQueryObject.addWhereIsNotNullCondition("filtro_porta");
				queryString = sqlQueryObject.createSQLQuery();
				stmt = con.prepareStatement(queryString);
				stmt.setString(1, tipo);
				stmt.setString(2, RuoloPorta.DELEGATA.getValue());
				risultato = stmt.executeQuery();
				while (risultato.next()){
					String nome = risultato.getString("filtro_porta");
					ResultPorta resultPorta = DBOggettiInUsoUtils.formatPortaDelegata(nome, tipoDB, con, normalizeObjectIds);
					if(resultPorta.mapping) {
						allarmiPD_mapping_list.add(resultPorta.label);
					}
					else {
						allarmiPD_list.add(resultPorta.label);
					}
					isInUso = true;
				}
				risultato.close();
				stmt.close();
				
			}else if("MESSAGE_HANDLER".equals(tipoPlugin)) {
				List<String> messageHandlerPD_mapping_list = whereIsInUso.get(ErrorsHandlerCostant.MESSAGE_HANDLER_MAPPING_PD);
				List<String> messageHandlerPD_list = whereIsInUso.get(ErrorsHandlerCostant.MESSAGE_HANDLER_PD);
				List<String> messageHandlerPA_mapping_list = whereIsInUso.get(ErrorsHandlerCostant.MESSAGE_HANDLER_MAPPING_PA);
				List<String> messageHandlerPA_list = whereIsInUso.get(ErrorsHandlerCostant.MESSAGE_HANDLER_PA);
				List<String> messageHandler_list = whereIsInUso.get(ErrorsHandlerCostant.MESSAGE_HANDLER);
				
				if (messageHandlerPD_mapping_list == null) {
					messageHandlerPD_mapping_list = new ArrayList<>();
					whereIsInUso.put(ErrorsHandlerCostant.MESSAGE_HANDLER_MAPPING_PD, messageHandlerPD_mapping_list);
				}
				if (messageHandlerPD_list == null) {
					messageHandlerPD_list = new ArrayList<>();
					whereIsInUso.put(ErrorsHandlerCostant.MESSAGE_HANDLER_PD, messageHandlerPD_list);
				}
				if (messageHandlerPA_mapping_list == null) {
					messageHandlerPA_mapping_list = new ArrayList<>();
					whereIsInUso.put(ErrorsHandlerCostant.MESSAGE_HANDLER_MAPPING_PA, messageHandlerPA_mapping_list);
				}
				if (messageHandlerPA_list == null) {
					messageHandlerPA_list = new ArrayList<>();
					whereIsInUso.put(ErrorsHandlerCostant.MESSAGE_HANDLER_PA, messageHandlerPA_list);
				}
				if (messageHandler_list == null) {
					messageHandler_list = new ArrayList<>();
					whereIsInUso.put(ErrorsHandlerCostant.MESSAGE_HANDLER, messageHandler_list);
				}
				
				// config handlers
				String tabella = CostantiDB.CONFIGURAZIONE_HANDLERS;
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
				sqlQueryObject.addFromTable(tabella);
				sqlQueryObject.addSelectField(tabella +".tipologia");
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addWhereCondition(tabella +".tipo=?");
				queryString = sqlQueryObject.createSQLQuery();
				stmt = con.prepareStatement(queryString);
				stmt.setString(1, tipo);
				risultato = stmt.executeQuery();
				while (risultato.next()){
					String tipologia = risultato.getString("tipologia");
					String labelMessaggio = formatMessageHandlerFromTipologia(tipologia);
					messageHandler_list.add(labelMessaggio);
					isInUso = true;
				}
				risultato.close();
				stmt.close();
				
				
				// Porte applicative, message handlers
				tabella = CostantiDB.PORTE_APPLICATIVE_HANDLERS;
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
				sqlQueryObject.addFromTable(tabella);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE);
				sqlQueryObject.addSelectField(tabella +".tipologia");
				sqlQueryObject.addSelectField(CostantiDB.PORTE_APPLICATIVE +".nome_porta");
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addWhereCondition(tabella+".id_porta="+CostantiDB.PORTE_APPLICATIVE+".id");
				sqlQueryObject.addWhereCondition(tabella+".tipo=?");
				queryString = sqlQueryObject.createSQLQuery();
				stmt = con.prepareStatement(queryString);
				stmt.setString(1, tipo);
				risultato = stmt.executeQuery();
				while (risultato.next()){
					String tipologia = risultato.getString("tipologia");
					String nome = risultato.getString("nome_porta");
					String labelMessaggio = formatMessageHandlerFromTipologia(tipologia);
					
					ResultPorta resultPorta = DBOggettiInUsoUtils.formatPortaApplicativa(nome, tipoDB, con, normalizeObjectIds);
					if(resultPorta.mapping) {
						messageHandlerPA_mapping_list.add(resultPorta.label + ": " + labelMessaggio);
					}
					else {
						messageHandlerPA_list.add(resultPorta.label + ": " + labelMessaggio);
					}
					isInUso = true;
				}
				risultato.close();
				stmt.close();

				// Porte delegate, message handlers
				tabella = CostantiDB.PORTE_DELEGATE_HANDLERS;
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
				sqlQueryObject.addFromTable(tabella);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE);
				sqlQueryObject.addSelectField(tabella +".tipologia");
				sqlQueryObject.addSelectField(CostantiDB.PORTE_DELEGATE +".nome_porta");
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addWhereCondition(tabella+".id_porta="+CostantiDB.PORTE_DELEGATE+".id");
				sqlQueryObject.addWhereCondition(tabella+".tipo=?");
				queryString = sqlQueryObject.createSQLQuery();
				stmt = con.prepareStatement(queryString);
				stmt.setString(1, tipo);
				risultato = stmt.executeQuery();
				while (risultato.next()){
					String tipologia = risultato.getString("tipologia");
					String nome = risultato.getString("nome_porta");
					String labelMessaggio = formatMessageHandlerFromTipologia(tipologia);
					
					ResultPorta resultPorta = DBOggettiInUsoUtils.formatPortaDelegata(nome, tipoDB, con, normalizeObjectIds);
					if(resultPorta.mapping) {
						messageHandlerPD_mapping_list.add(resultPorta.label + ": " + labelMessaggio);
					}
					else {
						messageHandlerPD_list.add(resultPorta.label + ": " + labelMessaggio);
					}
					isInUso = true;
				}
				risultato.close();
				stmt.close();
				
			}else if("SERVICE_HANDLER".equals(tipoPlugin)) {
				List<String> serviceHandler_list = whereIsInUso.get(ErrorsHandlerCostant.SERVICE_HANDLER);
				
				if (serviceHandler_list == null) {
					serviceHandler_list = new ArrayList<>();
					whereIsInUso.put(ErrorsHandlerCostant.SERVICE_HANDLER, serviceHandler_list);
				}
				
				// config handlers
				String tabella = CostantiDB.CONFIGURAZIONE_HANDLERS;
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
				sqlQueryObject.addFromTable(tabella);
				sqlQueryObject.addSelectField(tabella +".tipologia");
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addWhereCondition(tabella +".tipo=?");
				queryString = sqlQueryObject.createSQLQuery();
				stmt = con.prepareStatement(queryString);
				stmt.setString(1, tipo);
				risultato = stmt.executeQuery();
				while (risultato.next()){
					String tipologia = risultato.getString("tipologia");
					String labelMessaggio = formatServiceHandlerFromTipologia(tipologia);
					serviceHandler_list.add(labelMessaggio);
					isInUso = true;
				}
				risultato.close();
				stmt.close();
			}
			else if("TOKEN_DYNAMIC_DISCOVERY".equals(tipoPlugin)
					||
				"TOKEN_VALIDAZIONE".equals(tipoPlugin)
				||
				"TOKEN_NEGOZIAZIONE".equals(tipoPlugin)
				||
				"ATTRIBUTE_AUTHORITY".equals(tipoPlugin) ) {
				
				boolean tokenDynamicDiscovery = "TOKEN_DYNAMIC_DISCOVERY".equals(tipoPlugin);
				boolean tokenValidazione = "TOKEN_VALIDAZIONE".equals(tipoPlugin);
				boolean tokenNegoziazione = "TOKEN_NEGOZIAZIONE".equals(tipoPlugin);
				boolean attributeAuthority = "ATTRIBUTE_AUTHORITY".equals(tipoPlugin);
				
				ErrorsHandlerCostant constants = null;
				String tipologia = null;
				if(tokenDynamicDiscovery) {
					constants = ErrorsHandlerCostant.TOKEN_DYNAMIC_DISCOVERY_PA;
					tipologia = CostantiConfigurazione.GENERIC_PROPERTIES_TOKEN_TIPOLOGIA_VALIDATION;
				}
				else if(tokenValidazione) {
					constants = ErrorsHandlerCostant.TOKEN_PA;
					tipologia = CostantiConfigurazione.GENERIC_PROPERTIES_TOKEN_TIPOLOGIA_VALIDATION;
				}
				else if(tokenNegoziazione) {
					constants = ErrorsHandlerCostant.TOKEN_NEGOZIAZIONE_PA;
					tipologia = CostantiConfigurazione.GENERIC_PROPERTIES_TOKEN_TIPOLOGIA_RETRIEVE;
				}
				else {
					constants = ErrorsHandlerCostant.ATTRIBUTE_AUTHORITY_PA;
					tipologia = CostantiConfigurazione.GENERIC_PROPERTIES_ATTRIBUTE_AUTHORITY;
				}
								
				List<String> gpList = whereIsInUso.get(constants);
				if (gpList == null) {
					gpList = new ArrayList<>();
					whereIsInUso.put(constants, gpList);
				}	
				
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.CONFIG_GENERIC_PROPERTIES, "gps");
				sqlQueryObject.addFromTable(CostantiDB.CONFIG_GENERIC_PROPERTY, "gp");
				sqlQueryObject.setSelectDistinct(true);
				sqlQueryObject.addSelectAliasField("gps", "nome", "nomeGP");
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addWhereCondition("gps.id=gp.id_props");
				sqlQueryObject.addWhereCondition("gps.tipologia=?");
				String campoGpNome = "gp.nome=?";
				String campoGpValore = "gp.valore";
				if(tokenValidazione) {
					sqlQueryObject.addWhereCondition(false, campoGpNome, campoGpNome, campoGpNome);
					sqlQueryObject.addWhereLikeCondition(campoGpValore, tipo, false, false, false);
				}
				else if(tokenDynamicDiscovery || tokenNegoziazione || attributeAuthority) {
					sqlQueryObject.addWhereCondition(campoGpNome);
					sqlQueryObject.addWhereLikeCondition(campoGpValore, tipo, false, false, false);
				}
				
				queryString = sqlQueryObject.createSQLQuery();
				stmt = con.prepareStatement(queryString);
				int index = 1;
				JDBCParameterUtilities utils = new JDBCParameterUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2());
				utils.setParameter(stmt, index++, tipologia, String.class);
				if(tokenValidazione) {
					utils.setParameter(stmt, index++, CostantiConfigurazione.POLICY_VALIDAZIONE_CLAIMS_PARSER_PLUGIN_TYPE, String.class);
					utils.setParameter(stmt, index++, CostantiConfigurazione.POLICY_INTROSPECTION_CLAIMS_PARSER_PLUGIN_TYPE, String.class);
					utils.setParameter(stmt, index++, CostantiConfigurazione.POLICY_USER_INFO_CLAIMS_PARSER_PLUGIN_TYPE, String.class);
				}
				else if(tokenDynamicDiscovery) {
					utils.setParameter(stmt, index++, CostantiConfigurazione.POLICY_DYNAMIC_DISCOVERY_CLAIMS_PARSER_PLUGIN_TYPE, String.class);
				}
				else if(tokenNegoziazione) {
					utils.setParameter(stmt, index++, CostantiConfigurazione.POLICY_RETRIEVE_TOKEN_PARSER_PLUGIN_TYPE, String.class);
				}
				else if(attributeAuthority) {
					utils.setParameter(stmt, index++, CostantiConfigurazione.AA_RESPONSE_PARSER_PLUGIN_TYPE, String.class);
				}
				risultato = stmt.executeQuery();
				while (risultato.next()) {
					
					String nome = risultato.getString("nomeGP");
					
					String labelMessaggio = DBOggettiInUsoUtils.formatGenericProperties(tipologia, nome);
					gpList.add(labelMessaggio);
	
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

	protected static String toString(String className, String label, String tipoPlugin, String tipo, Map<ErrorsHandlerCostant, List<String>> whereIsInUso, boolean prefix, String separator){
		
		if(className!=null && tipoPlugin!=null && tipo!=null) {
			// nop
		}
		
		StringBuilder bf = new StringBuilder();
	
		bf.append(label);
		
		Set<ErrorsHandlerCostant> keys = whereIsInUso.keySet();
		StringBuilder msg = new StringBuilder();
		msg.append("Plugin '" +bf.toString() +"' non eliminabile perch&egrave; :"+separator);
		if(!prefix){
			msg = new StringBuilder();
		}
		String separatorCategorie = "";
		if(whereIsInUso.size()>1) {
			separatorCategorie = separator;
		}
		for (ErrorsHandlerCostant key : keys) {
			List<String> messages = whereIsInUso.get(key);

			if ( messages!=null && !messages.isEmpty()) {
				msg.append(separatorCategorie);
			}
			
			switch (key) {
			
			case CONNETTORE_MAPPING_PD:
				if ( messages!=null && !messages.isEmpty()) {
					msg.append("utilizzata nel connettore per le Fruizioni: " + DBOggettiInUsoUtils.formatList(messages,separator) + separator);
				}
				break;
			case CONNETTORE_PD:
				if ( messages!=null && !messages.isEmpty()) {
					msg.append("utilizzato nelle Porte Outbound (Connettore): " + DBOggettiInUsoUtils.formatList(messages,separator) + separator);
				}
				break;
			case CONNETTORE_MAPPING_PA:
				if ( messages!=null && !messages.isEmpty()) {
					msg.append("utilizzato nel connettore per le Erogazioni: " + DBOggettiInUsoUtils.formatList(messages,separator) + separator);
				}
				break;
			case CONNETTORE_PA:
				if ( messages!=null && !messages.isEmpty()) {
					msg.append("utilizzato nelle Porte Inbound (Connettore): " + DBOggettiInUsoUtils.formatList(messages,separator) + separator);
				}
				break;
			
			case AUTENTICAZIONE_MAPPING_PD:
				if ( messages!=null && !messages.isEmpty()) {
					msg.append("utilizzato come processo di autenticazione nel Controllo degli Accessi delle Fruizioni: " + DBOggettiInUsoUtils.formatList(messages,separator) + separator);
				}
				break;
			case AUTENTICAZIONE_PD:
				if ( messages!=null && !messages.isEmpty()) {
					msg.append("utilizzato nelle Porte Outbound (Controllo degli Accessi - Autenticazione): " + DBOggettiInUsoUtils.formatList(messages,separator) + separator);
				}
				break;
			case AUTENTICAZIONE_MAPPING_PA:
				if ( messages!=null && !messages.isEmpty()) {
					msg.append("utilizzato come processo di autenticazione nel Controllo degli Accessi delle Erogazioni: " + DBOggettiInUsoUtils.formatList(messages,separator) + separator);
				}
				break;
			case AUTENTICAZIONE_PA:
				if ( messages!=null && !messages.isEmpty()) {
					msg.append("utilizzato nelle Porte Inbound (Controllo degli Accessi - Autenticazione): " + DBOggettiInUsoUtils.formatList(messages,separator) + separator);
				}
				break;
				
			case AUTORIZZAZIONE_MAPPING_PD:
				if ( messages!=null && !messages.isEmpty()) {
					msg.append("utilizzato come processo di autorizzazione nel Controllo degli Accessi delle Fruizioni: " + DBOggettiInUsoUtils.formatList(messages,separator) + separator);
				}
				break;
			case AUTORIZZAZIONE_PD:
				if ( messages!=null && !messages.isEmpty()) {
					msg.append("utilizzato nelle Porte Outbound (Controllo degli Accessi - Autorizzazione): " + DBOggettiInUsoUtils.formatList(messages,separator) + separator);
				}
				break;
			case AUTORIZZAZIONE_MAPPING_PA:
				if ( messages!=null && !messages.isEmpty()) {
					msg.append("utilizzato come processo di autorizzazione nel Controllo degli Accessi delle Erogazioni: " + DBOggettiInUsoUtils.formatList(messages,separator) + separator);
				}
				break;
			case AUTORIZZAZIONE_PA:
				if ( messages!=null && !messages.isEmpty()) {
					msg.append("utilizzato nelle Porte Inbound (Controllo degli Accessi - Autorizzazione): " + DBOggettiInUsoUtils.formatList(messages,separator) + separator);
				}
				break;
				
			case AUTORIZZAZIONE_CONTENUTI_MAPPING_PD:
				if ( messages!=null && !messages.isEmpty()) {
					msg.append("utilizzato come processo di autorizzazione dei contenuti nel Controllo degli Accessi delle Fruizioni: " + DBOggettiInUsoUtils.formatList(messages,separator) + separator);
				}
				break;
			case AUTORIZZAZIONE_CONTENUTI_PD:
				if ( messages!=null && !messages.isEmpty()) {
					msg.append("utilizzato nelle Porte Outbound (Controllo degli Accessi - Autorizzazione dei Contenuti): " + DBOggettiInUsoUtils.formatList(messages,separator) + separator);
				}
				break;
			case AUTORIZZAZIONE_CONTENUTI_MAPPING_PA:
				if ( messages!=null && !messages.isEmpty()) {
					msg.append("utilizzato come processo di autorizzazione dei contenuti nel Controllo degli Accessi delle Erogazioni: " + DBOggettiInUsoUtils.formatList(messages,separator) + separator);
				}
				break;
			case AUTORIZZAZIONE_CONTENUTI_PA:
				if ( messages!=null && !messages.isEmpty()) {
					msg.append("utilizzato nelle Porte Inbound (Controllo degli Accessi - Autorizzazione dei Contenuti): " + DBOggettiInUsoUtils.formatList(messages,separator) + separator);
				}
				break;
				
			case CONTROLLO_TRAFFICO:
				if ( messages!=null && !messages.isEmpty() ) {
					msg.append("utilizzato in Policy di Rate Limiting: " + DBOggettiInUsoUtils.formatList(messages,separator) + separator);
				}
				break;
				
			case INTEGRAZIONE_MAPPING_PD:
				if ( messages!=null && !messages.isEmpty()) {
					msg.append("utilizzato per generare i metadati di integrazione nelle Opzioni Avanzate delle Fruizioni: " + DBOggettiInUsoUtils.formatList(messages,separator) + separator);
				}
				break;
			case INTEGRAZIONE_PD:
				if ( messages!=null && !messages.isEmpty()) {
					msg.append("utilizzato nelle Porte Outbound (Opzioni Avanzate - Metadata di Integrazione): " + DBOggettiInUsoUtils.formatList(messages,separator) + separator);
				}
				break;
			case INTEGRAZIONE_MAPPING_PA:
				if ( messages!=null && !messages.isEmpty()) {
					msg.append("utilizzato per generare i metadati di integrazione nelle Opzioni Avanzate delle Erogazioni: " + DBOggettiInUsoUtils.formatList(messages,separator) + separator);
				}
				break;
			case INTEGRAZIONE_PA:
				if ( messages!=null && !messages.isEmpty()) {
					msg.append("utilizzato nelle Porte Inbound (Opzioni Avanzate - Metadata di Integrazione): " + DBOggettiInUsoUtils.formatList(messages,separator) + separator);
				}
				break;
				
			case BEHAVIOUR_MAPPING_PA:
				if ( messages!=null && !messages.isEmpty()) {
					msg.append("utilizzato nella configurazione dei connettori multipli come consegna personalizzata delle Erogazioni: " + DBOggettiInUsoUtils.formatList(messages,separator) + separator);
				}
				break;
			case BEHAVIOUR_PA:
				if ( messages!=null && !messages.isEmpty()) {
					msg.append("utilizzato nelle Porte Inbound (Connettori Multipli - Consegna Personalizzata): " + DBOggettiInUsoUtils.formatList(messages,separator) + separator);
				}
				break;
				
			case ALLARMI_MAPPING_PA:
				if ( messages!=null && !messages.isEmpty()) {
					msg.append("utilizzato negli Allarmi delle Erogazioni: " + DBOggettiInUsoUtils.formatList(messages,separator) + separator);
				}
				break;
			case ALLARMI_PA:
				if ( messages!=null && !messages.isEmpty()) {
					msg.append("utilizzato nelle Porte Inbound (Allarmi): " + DBOggettiInUsoUtils.formatList(messages,separator) + separator);
				}
				break;
			case ALLARMI_MAPPING_PD:
				if ( messages!=null && !messages.isEmpty()) {
					msg.append("utilizzato negli Allarmi delle Fruizioni: " + DBOggettiInUsoUtils.formatList(messages,separator) + separator);
				}
				break;
			case ALLARMI_PD:
				if ( messages!=null && !messages.isEmpty()) {
					msg.append("utilizzato nelle Porte Outbound (Allarmi): " + DBOggettiInUsoUtils.formatList(messages,separator) + separator);
				}
				break;
			case ALLARMI:
				if ( messages!=null && !messages.isEmpty() ) {
					msg.append("utilizzato negli Allarmi definiti nella configurazione generale: " + DBOggettiInUsoUtils.formatList(messages,separator) + separator);
				}
				break;
			case MESSAGE_HANDLER_MAPPING_PA:
				if ( messages!=null && !messages.isEmpty()) {
					msg.append("utilizzato nei Message Handlers delle Erogazioni: " + DBOggettiInUsoUtils.formatList(messages,separator) + separator);
				}
				break;
			case MESSAGE_HANDLER_PA:
				if ( messages!=null && !messages.isEmpty()) {
					msg.append("utilizzato nelle Porte Inbound (Message Handlers): " + DBOggettiInUsoUtils.formatList(messages,separator) + separator);
				}
				break;
			case MESSAGE_HANDLER:
				if ( messages!=null && !messages.isEmpty() ) {
					msg.append("utilizzato nei Message Handlers: " + DBOggettiInUsoUtils.formatList(messages,separator) + separator);
				}
				break;
			case MESSAGE_HANDLER_MAPPING_PD:
				if ( messages!=null && !messages.isEmpty()) {
					msg.append("utilizzato nei Message Handlers delle Fruizioni: " + DBOggettiInUsoUtils.formatList(messages,separator) + separator);
				}
				break;
			case MESSAGE_HANDLER_PD:
				if ( messages!=null && !messages.isEmpty()) {
					msg.append("utilizzato nelle Porte Outbound (Message Handlers): " + DBOggettiInUsoUtils.formatList(messages,separator) + separator);
				}
				break;
			case SERVICE_HANDLER:
				if ( messages!=null && !messages.isEmpty() ) {
					msg.append("utilizzato nei Service Handlers: " + DBOggettiInUsoUtils.formatList(messages,separator) + separator);
				}
				break;
				
			case TOKEN_DYNAMIC_DISCOVERY_PA:
				if ( messages!=null && !messages.isEmpty() ) {
					msg.append("utilizzato nelle Token Policy di Validazione: " + DBOggettiInUsoUtils.formatList(messages,separator) + separator);
				}
				break;	
			case TOKEN_PA:
				if ( messages!=null && !messages.isEmpty() ) {
					msg.append("utilizzato nelle Token Policy di Validazione: " + DBOggettiInUsoUtils.formatList(messages,separator) + separator);
				}
				break;	
			case TOKEN_NEGOZIAZIONE_PA:
				if ( messages!=null && !messages.isEmpty() ) {
					msg.append("utilizzato nelle Token Policy di Negoziazione: " + DBOggettiInUsoUtils.formatList(messages,separator) + separator);
				}
				break;	
			case ATTRIBUTE_AUTHORITY_PA:
				if ( messages!=null && !messages.isEmpty() ) {
					msg.append("utilizzato negli Attribute Authority: " + DBOggettiInUsoUtils.formatList(messages,separator) + separator);
				}
				break;	
				
			default:
				msg.append("utilizzato in oggetto non codificato ("+key+")"+separator);
				break;
			}

		}// chiudo for

		return msg.toString();
	}

	private static String formatMessageHandlerFromTipologia(String tipologia) {
		if(tipologia.endsWith(CostantiDB.HANDLER_REQUEST_SUFFIX)) {
			String tipologiaWS = tipologia.substring(0, tipologia.indexOf(CostantiDB.HANDLER_REQUEST_SUFFIX));
			String template = "Fase [{0}] degli Handler di Richiesta";
			return MessageFormat.format(template, tipologiaWS);
		} else {
			String tipologiaWS = tipologia.substring(0, tipologia.indexOf(CostantiDB.HANDLER_RESPONSE_SUFFIX));
			String template = "Fase [{0}] degli Handler di Risposta";
			return MessageFormat.format(template, tipologiaWS);
		}
	}
	
	private static String formatServiceHandlerFromTipologia(String tipologia) {
/**		String tipologiaWS = tipologia.substring(0, tipologia.indexOf(CostantiDB.HANDLER_REQUEST_SUFFIX));*/
		return MessageFormat.format("Fase [{0}]", tipologia);
		
	}
	
}
