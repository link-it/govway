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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openspcoop2.core.commons.DBUtils;
import org.openspcoop2.core.commons.ErrorsHandlerCostant;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.id.IDRuolo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.jdbc.JDBCUtilities;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.SQLObjectFactory;

/**
 * DBOggettiInUsoUtils_ruoli
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class DBOggettiInUsoUtils_ruoli {

	protected static boolean isRuoloConfigInUso(Connection con, String tipoDB, IDRuolo idRuolo, Map<ErrorsHandlerCostant, 
			List<String>> whereIsInUso, boolean normalizeObjectIds) throws UtilsException {
		return _isRuoloInUso(con,tipoDB,idRuolo,false,true,whereIsInUso,normalizeObjectIds);
	}
	protected static boolean isRuoloRegistryInUso(Connection con, String tipoDB, IDRuolo idRuolo, Map<ErrorsHandlerCostant, 
			List<String>> whereIsInUso, boolean normalizeObjectIds) throws UtilsException {
		return _isRuoloInUso(con,tipoDB,idRuolo,true,false,whereIsInUso,normalizeObjectIds);
	}
	protected static boolean isRuoloInUso(Connection con, String tipoDB, IDRuolo idRuolo, Map<ErrorsHandlerCostant, 
			List<String>> whereIsInUso, boolean normalizeObjectIds) throws UtilsException {
		return _isRuoloInUso(con,tipoDB,idRuolo,true,true,whereIsInUso,normalizeObjectIds);
	}
	private static boolean _isRuoloInUso(Connection con, String tipoDB, IDRuolo idRuolo, boolean registry, boolean config, Map<ErrorsHandlerCostant, 
			List<String>> whereIsInUso, boolean normalizeObjectIds) throws UtilsException {
		String nomeMetodo = "_isRuoloInUso";

		PreparedStatement stmt = null;
		ResultSet risultato = null;
		String queryString;

		try {

			long idR = DBUtils.getIdRuolo(idRuolo, con, tipoDB);
			
			boolean isInUso = false;
			
			List<String> soggetti_list = whereIsInUso.get(ErrorsHandlerCostant.IN_USO_IN_SOGGETTI);
			List<String> servizi_applicativi_list = whereIsInUso.get(ErrorsHandlerCostant.IN_USO_IN_SERVIZI_APPLICATIVI);
			List<String> porte_applicative_list = whereIsInUso.get(ErrorsHandlerCostant.IN_USO_IN_PORTE_APPLICATIVE);
			List<String> porte_applicative_modi_list = whereIsInUso.get(ErrorsHandlerCostant.IN_USO_IN_PORTE_APPLICATIVE_MODI);
			List<String> porte_delegate_list = whereIsInUso.get(ErrorsHandlerCostant.IN_USO_IN_PORTE_DELEGATE);
			List<String> mappingErogazionePA_list = whereIsInUso.get(ErrorsHandlerCostant.IN_USO_IN_MAPPING_EROGAZIONE_PA);
			List<String> mappingErogazionePA_modi_list = whereIsInUso.get(ErrorsHandlerCostant.IN_USO_IN_MAPPING_EROGAZIONE_PA_MODI);
			List<String> mappingFruizionePD_list = whereIsInUso.get(ErrorsHandlerCostant.IN_USO_IN_MAPPING_FRUIZIONE_PD);
			List<String> porte_applicative_token_list = whereIsInUso.get(ErrorsHandlerCostant.RUOLI_TOKEN_PA);
			List<String> porte_applicative_token_modi_list = whereIsInUso.get(ErrorsHandlerCostant.RUOLI_TOKEN_PA_MODI);
			List<String> porte_delegate_token_list = whereIsInUso.get(ErrorsHandlerCostant.RUOLI_TOKEN_PD);
			List<String> mappingErogazioneTokenPA_list = whereIsInUso.get(ErrorsHandlerCostant.RUOLI_TOKEN_MAPPING_PA);
			List<String> mappingErogazioneTokenPA_modi_list = whereIsInUso.get(ErrorsHandlerCostant.RUOLI_TOKEN_MAPPING_PA_MODI);
			List<String> mappingFruizioneTokenPD_list = whereIsInUso.get(ErrorsHandlerCostant.RUOLI_TOKEN_MAPPING_PD);
			List<String> ct_list = whereIsInUso.get(ErrorsHandlerCostant.CONTROLLO_TRAFFICO);
			List<String> allarme_list = whereIsInUso.get(ErrorsHandlerCostant.ALLARMI);
			
			if (soggetti_list == null) {
				soggetti_list = new ArrayList<>();
				whereIsInUso.put(ErrorsHandlerCostant.IN_USO_IN_SOGGETTI, soggetti_list);
			}
			if (servizi_applicativi_list == null) {
				servizi_applicativi_list = new ArrayList<>();
				whereIsInUso.put(ErrorsHandlerCostant.IN_USO_IN_SERVIZI_APPLICATIVI, servizi_applicativi_list);
			}
			
			if (porte_applicative_list == null) {
				porte_applicative_list = new ArrayList<>();
				whereIsInUso.put(ErrorsHandlerCostant.IN_USO_IN_PORTE_APPLICATIVE, porte_applicative_list);
			}
			if (porte_applicative_modi_list == null) {
				porte_applicative_modi_list = new ArrayList<>();
				whereIsInUso.put(ErrorsHandlerCostant.IN_USO_IN_PORTE_APPLICATIVE_MODI, porte_applicative_modi_list);
			}
			if (porte_delegate_list == null) {
				porte_delegate_list = new ArrayList<>();
				whereIsInUso.put(ErrorsHandlerCostant.IN_USO_IN_PORTE_DELEGATE, porte_delegate_list);
			}
			if (mappingErogazionePA_list == null) {
				mappingErogazionePA_list = new ArrayList<>();
				whereIsInUso.put(ErrorsHandlerCostant.IN_USO_IN_MAPPING_EROGAZIONE_PA, mappingErogazionePA_list);
			}
			if (mappingErogazionePA_modi_list == null) {
				mappingErogazionePA_modi_list = new ArrayList<>();
				whereIsInUso.put(ErrorsHandlerCostant.IN_USO_IN_MAPPING_EROGAZIONE_PA_MODI, mappingErogazionePA_modi_list);
			}
			if (mappingFruizionePD_list == null) {
				mappingFruizionePD_list = new ArrayList<>();
				whereIsInUso.put(ErrorsHandlerCostant.IN_USO_IN_MAPPING_FRUIZIONE_PD, mappingFruizionePD_list);
			}
			
			if (porte_applicative_token_list == null) {
				porte_applicative_token_list = new ArrayList<>();
				whereIsInUso.put(ErrorsHandlerCostant.RUOLI_TOKEN_PA, porte_applicative_token_list);
			}
			if (porte_applicative_token_modi_list == null) {
				porte_applicative_token_modi_list = new ArrayList<>();
				whereIsInUso.put(ErrorsHandlerCostant.RUOLI_TOKEN_PA_MODI, porte_applicative_token_modi_list);
			}
			if (porte_delegate_token_list == null) {
				porte_delegate_token_list = new ArrayList<>();
				whereIsInUso.put(ErrorsHandlerCostant.RUOLI_TOKEN_PD, porte_delegate_token_list);
			}
			if (mappingErogazioneTokenPA_list == null) {
				mappingErogazioneTokenPA_list = new ArrayList<>();
				whereIsInUso.put(ErrorsHandlerCostant.RUOLI_TOKEN_MAPPING_PA, mappingErogazioneTokenPA_list);
			}
			if (mappingErogazioneTokenPA_modi_list == null) {
				mappingErogazioneTokenPA_modi_list = new ArrayList<>();
				whereIsInUso.put(ErrorsHandlerCostant.RUOLI_TOKEN_MAPPING_PA_MODI, mappingErogazioneTokenPA_modi_list);
			}
			if (mappingFruizioneTokenPD_list == null) {
				mappingFruizioneTokenPD_list = new ArrayList<>();
				whereIsInUso.put(ErrorsHandlerCostant.RUOLI_TOKEN_MAPPING_PD, mappingFruizioneTokenPD_list);
			}
			
			if (ct_list == null) {
				ct_list = new ArrayList<>();
				whereIsInUso.put(ErrorsHandlerCostant.CONTROLLO_TRAFFICO, ct_list);
			}
			if (allarme_list == null) {
				allarme_list = new ArrayList<>();
				whereIsInUso.put(ErrorsHandlerCostant.ALLARMI, allarme_list);
			}
			
			
			// Controllo che il ruolo non sia in uso nei soggetti
			if(registry){
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SOGGETTI_RUOLI);
				sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
				sqlQueryObject.addSelectField("tipo_soggetto");
				sqlQueryObject.addSelectField("nome_soggetto");
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.setSelectDistinct(true);
				sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI_RUOLI+".id_ruolo = ?");
				sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI_RUOLI+".id_soggetto = "+CostantiDB.SOGGETTI+".id");
				queryString = sqlQueryObject.createSQLQuery();
				stmt = con.prepareStatement(queryString);
				stmt.setLong(1, idR);
				risultato = stmt.executeQuery();
				while (risultato.next()) {
					String tipo_soggetto = risultato.getString("tipo_soggetto");
					String nome_soggetto = risultato.getString("nome_soggetto");
					IDSoggetto idSoggetto = new IDSoggetto(tipo_soggetto, nome_soggetto);
					if(normalizeObjectIds) {
						String protocollo = ProtocolFactoryManager.getInstance().getProtocolByOrganizationType(tipo_soggetto);
						soggetti_list.add(DBOggettiInUsoUtils.getProtocolPrefix(protocollo)+NamingUtils.getLabelSoggetto(protocollo, idSoggetto));
					}
					else {
						soggetti_list.add(tipo_soggetto + "/" + nome_soggetto);	
					}
					isInUso = true;
				}
				risultato.close();
				stmt.close();

			}
			
			// Controllo che il ruolo non sia in uso nei serviziApplicativi
			if(config){
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI_RUOLI);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
				sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
				sqlQueryObject.addSelectField("tipo_soggetto");
				sqlQueryObject.addSelectField("nome_soggetto");
				sqlQueryObject.addSelectField(CostantiDB.SERVIZI_APPLICATIVI+".nome");
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.setSelectDistinct(true);
				sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI_RUOLI+".ruolo = ?");
				sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI_RUOLI+".id_servizio_applicativo = "+CostantiDB.SERVIZI_APPLICATIVI+".id");
				sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".id_soggetto = "+CostantiDB.SOGGETTI+".id");
				queryString = sqlQueryObject.createSQLQuery();
				stmt = con.prepareStatement(queryString);
				stmt.setString(1, idRuolo.getNome());
				risultato = stmt.executeQuery();
				while (risultato.next()) {
					String tipo_soggetto = risultato.getString("tipo_soggetto");
					String nome_soggetto = risultato.getString("nome_soggetto");
					String nome = risultato.getString("nome");
					IDSoggetto idSoggetto = new IDSoggetto(tipo_soggetto, nome_soggetto);
					if(normalizeObjectIds) {
						String protocollo = ProtocolFactoryManager.getInstance().getProtocolByOrganizationType(tipo_soggetto);
						servizi_applicativi_list.add(DBOggettiInUsoUtils.getProtocolPrefix(protocollo)+nome+DBOggettiInUsoUtils.getSubjectSuffix(protocollo, idSoggetto));
					}
					else {
						servizi_applicativi_list.add(tipo_soggetto + "/" + nome_soggetto+"_"+nome);
					}
					isInUso = true;
				}
				risultato.close();
				stmt.close();
			}
			
			// Controllo che il ruolo non sia in uso nelle porte applicative
			if(config){
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_RUOLI);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE);
				sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
				sqlQueryObject.addSelectField(CostantiDB.PORTE_APPLICATIVE+".nome_porta");
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.setSelectDistinct(true);
				sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE_RUOLI+".ruolo = ?");
				sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE_RUOLI+".id_porta = "+CostantiDB.PORTE_APPLICATIVE+".id");
				sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE+".id_soggetto = "+CostantiDB.SOGGETTI+".id");
				queryString = sqlQueryObject.createSQLQuery();
				stmt = con.prepareStatement(queryString);
				stmt.setString(1, idRuolo.getNome());
				risultato = stmt.executeQuery();
				while (risultato.next()) {
					String nome = risultato.getString("nome_porta");
					ResultPorta resultPorta = DBOggettiInUsoUtils.formatPortaApplicativa(nome, tipoDB, con, normalizeObjectIds);
					if(resultPorta.mapping) {
						if(resultPorta.erogazioneModi)
							mappingErogazionePA_modi_list.add(resultPorta.label);
						else
							mappingErogazionePA_list.add(resultPorta.label);
					}
					else {
						if(resultPorta.erogazioneModi)
							porte_applicative_modi_list.add(resultPorta.label);
						else
							porte_applicative_list.add(resultPorta.label);
					}
					isInUso = true;
				}
				risultato.close();
				stmt.close();
			}
			
			// Controllo che il ruolo non sia in uso nelle porte applicative (token)
			if(config){
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_TOKEN_RUOLI);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE);
				sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
				sqlQueryObject.addSelectField(CostantiDB.PORTE_APPLICATIVE+".nome_porta");
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.setSelectDistinct(true);
				sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE_TOKEN_RUOLI+".ruolo = ?");
				sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE_TOKEN_RUOLI+".id_porta = "+CostantiDB.PORTE_APPLICATIVE+".id");
				sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE+".id_soggetto = "+CostantiDB.SOGGETTI+".id");
				queryString = sqlQueryObject.createSQLQuery();
				stmt = con.prepareStatement(queryString);
				stmt.setString(1, idRuolo.getNome());
				risultato = stmt.executeQuery();
				while (risultato.next()) {
					String nome = risultato.getString("nome_porta");
					ResultPorta resultPorta = DBOggettiInUsoUtils.formatPortaApplicativa(nome, tipoDB, con, normalizeObjectIds);
					if(resultPorta.mapping) {
						if(resultPorta.erogazioneModi)
							mappingErogazioneTokenPA_modi_list.add(resultPorta.label);
						else
							mappingErogazioneTokenPA_list.add(resultPorta.label);
					}
					else {
						if(resultPorta.erogazioneModi)
							porte_applicative_token_modi_list.add(resultPorta.label);
						else
							porte_applicative_token_list.add(resultPorta.label);
					}
					isInUso = true;
				}
				risultato.close();
				stmt.close();
			}
			
			// Controllo che il ruolo non sia in uso nelle porte delegate
			if(config){
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE_RUOLI);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE);
				sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
				sqlQueryObject.addSelectField(CostantiDB.PORTE_DELEGATE+".nome_porta");
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.setSelectDistinct(true);
				sqlQueryObject.addWhereCondition(CostantiDB.PORTE_DELEGATE_RUOLI+".ruolo = ?");
				sqlQueryObject.addWhereCondition(CostantiDB.PORTE_DELEGATE_RUOLI+".id_porta = "+CostantiDB.PORTE_DELEGATE+".id");
				sqlQueryObject.addWhereCondition(CostantiDB.PORTE_DELEGATE+".id_soggetto = "+CostantiDB.SOGGETTI+".id");
				queryString = sqlQueryObject.createSQLQuery();
				stmt = con.prepareStatement(queryString);
				stmt.setString(1, idRuolo.getNome());
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
			}
			
			// Controllo che il ruolo non sia in uso nelle porte delegate (token)
			if(config){
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE_TOKEN_RUOLI);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE);
				sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
				sqlQueryObject.addSelectField(CostantiDB.PORTE_DELEGATE+".nome_porta");
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.setSelectDistinct(true);
				sqlQueryObject.addWhereCondition(CostantiDB.PORTE_DELEGATE_TOKEN_RUOLI+".ruolo = ?");
				sqlQueryObject.addWhereCondition(CostantiDB.PORTE_DELEGATE_TOKEN_RUOLI+".id_porta = "+CostantiDB.PORTE_DELEGATE+".id");
				sqlQueryObject.addWhereCondition(CostantiDB.PORTE_DELEGATE+".id_soggetto = "+CostantiDB.SOGGETTI+".id");
				queryString = sqlQueryObject.createSQLQuery();
				stmt = con.prepareStatement(queryString);
				stmt.setString(1, idRuolo.getNome());
				risultato = stmt.executeQuery();
				while (risultato.next()) {
					String nome = risultato.getString("nome_porta");
					ResultPorta resultPorta = DBOggettiInUsoUtils.formatPortaDelegata(nome, tipoDB, con, normalizeObjectIds);
					if(resultPorta.mapping) {
						mappingFruizioneTokenPD_list.add(resultPorta.label);
					}
					else {
						porte_delegate_token_list.add(resultPorta.label);
					}
					isInUso = true;
				}
				risultato.close();
				stmt.close();
			}
			
			// Controllo che il soggetto non sia associato a policy di controllo del traffico o allarmi
			if(config){
				
				int max = 2;
				if(!CostantiDB.isAllarmiEnabled()) {
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
				
					ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
					sqlQueryObject.addFromTable(tabella);
					sqlQueryObject.addSelectField(identificativo_column);
					sqlQueryObject.addSelectField(alias_column);
					sqlQueryObject.addSelectField("filtro_ruolo");
					sqlQueryObject.addSelectField("filtro_porta");
					sqlQueryObject.setANDLogicOperator(false); // OR
					sqlQueryObject.addWhereCondition(true, 
							tabella+".filtro_ruolo_fruitore = ?");
					sqlQueryObject.addWhereCondition(true, 
							tabella+".filtro_ruolo_erogatore = ?");
					queryString = sqlQueryObject.createSQLQuery();
					sqlQueryObject.addOrderBy("filtro_ruolo");
					sqlQueryObject.addOrderBy("filtro_porta");
					stmt = con.prepareStatement(queryString);
					int index = 1;
					stmt.setString(index++, idRuolo.getNome());
					stmt.setString(index++, idRuolo.getNome());
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
				
			}
			
			return isInUso;

		} catch (Exception se) {
			throw new UtilsException("[DBOggettiInUsoUtils::" + nomeMetodo + "] Exception: " + se.getMessage(),se);
		} finally {
			// Chiudo statement and resultset
			JDBCUtilities.closeResources(risultato, stmt);
		}
	}


	protected static String toString(IDRuolo idRuolo, Map<ErrorsHandlerCostant, List<String>> whereIsInUso, boolean prefix, String separator){
		return toString(idRuolo, whereIsInUso, prefix, separator," non eliminabile perch&egrave; :");
	}
	protected static String toString(IDRuolo idRuolo, Map<ErrorsHandlerCostant, List<String>> whereIsInUso, boolean prefix, String separator, String intestazione){
		Set<ErrorsHandlerCostant> keys = whereIsInUso.keySet();
		String msg = "Ruolo '"+idRuolo.getNome()+"'" + intestazione+separator;
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
			case IN_USO_IN_SOGGETTI:
				if ( messages!=null && messages.size() > 0) {
					msg += "utilizzato nei Soggetti: " + DBOggettiInUsoUtils.formatList(messages,separator) + separator;
				}
				break;
			case IN_USO_IN_SERVIZI_APPLICATIVI:
				if ( messages!=null && messages.size() > 0) {
					msg += "utilizzato negli Applicativi: " + DBOggettiInUsoUtils.formatList(messages,separator) + separator;
				}
				break;
				
			case IN_USO_IN_PORTE_APPLICATIVE:
				if ( messages!=null && messages.size() > 0) {
					msg += "utilizzato nelle Porte Inbound (Controllo Accessi - Autorizzazione Trasporto): " + DBOggettiInUsoUtils.formatList(messages,separator) + separator;
				}
				break;
			case IN_USO_IN_PORTE_APPLICATIVE_MODI:
				if ( messages!=null && messages.size() > 0) {
					msg += "utilizzato nelle Porte Inbound (Controllo Accessi - Autorizzazione Canale): " + DBOggettiInUsoUtils.formatList(messages,separator) + separator;
				}
				break;
			case IN_USO_IN_PORTE_DELEGATE:
				if ( messages!=null && messages.size() > 0) {
					msg += "utilizzato nelle Porte Outbound (Controllo Accessi - Autorizzazione Trasporto): " + DBOggettiInUsoUtils.formatList(messages,separator) + separator;
				}
				break;
			case IN_USO_IN_MAPPING_EROGAZIONE_PA:
				if ( messages!=null && messages.size() > 0) {
					msg += "utilizzato nelle Erogazioni (Controllo Accessi - Autorizzazione Trasporto): " + DBOggettiInUsoUtils.formatList(messages,separator) + separator;
				}
				break;
			case IN_USO_IN_MAPPING_EROGAZIONE_PA_MODI:
				if ( messages!=null && messages.size() > 0) {
					msg += "utilizzato nelle Erogazioni (Controllo Accessi - Autorizzazione Canale): " + DBOggettiInUsoUtils.formatList(messages,separator) + separator;
				}
				break;
			case IN_USO_IN_MAPPING_FRUIZIONE_PD:
				if ( messages!=null && messages.size() > 0) {
					msg += "utilizzato nelle Fruizioni (Controllo Accessi - Autorizzazione Trasporto): " + DBOggettiInUsoUtils.formatList(messages,separator) + separator;
				}
				break;
				
			case RUOLI_TOKEN_PA:
				if ( messages!=null && messages.size() > 0) {
					msg += "utilizzato nelle Porte Inbound (Controllo Accessi - Autorizzazione Token): " + DBOggettiInUsoUtils.formatList(messages,separator) + separator;
				}
				break;
			case RUOLI_TOKEN_PA_MODI:
				if ( messages!=null && messages.size() > 0) {
					msg += "utilizzato nelle Porte Inbound (Controllo Accessi - Autorizzazione Messaggio): " + DBOggettiInUsoUtils.formatList(messages,separator) + separator;
				}
				break;
			case RUOLI_TOKEN_PD:
				if ( messages!=null && messages.size() > 0) {
					msg += "utilizzato nelle Porte Outbound (Controllo Accessi - Autorizzazione Token): " + DBOggettiInUsoUtils.formatList(messages,separator) + separator;
				}
				break;
			case RUOLI_TOKEN_MAPPING_PA:
				if ( messages!=null && messages.size() > 0) {
					msg += "utilizzato nelle Erogazioni (Controllo Accessi - Autorizzazione Token): " + DBOggettiInUsoUtils.formatList(messages,separator) + separator;
				}
				break;
			case RUOLI_TOKEN_MAPPING_PA_MODI:
				if ( messages!=null && messages.size() > 0) {
					msg += "utilizzato nelle Erogazioni (Controllo Accessi - Autorizzazione Messaggio): " + DBOggettiInUsoUtils.formatList(messages,separator) + separator;
				}
				break;
			case RUOLI_TOKEN_MAPPING_PD:
				if ( messages!=null && messages.size() > 0) {
					msg += "utilizzato nelle Fruizioni (Controllo Accessi - Autorizzazione Token): " + DBOggettiInUsoUtils.formatList(messages,separator) + separator;
				}
				break;
				
			case CONTROLLO_TRAFFICO:
				if ( messages!=null && messages.size() > 0 ) {
					msg += "utilizzato in Policy di Rate Limiting: " + DBOggettiInUsoUtils.formatList(messages,separator) + separator;
				}
				break;
			case ALLARMI:
				if ( messages!=null && messages.size() > 0 ) {
					msg += "utilizzato in Allarmi: " + DBOggettiInUsoUtils.formatList(messages,separator) + separator;
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
