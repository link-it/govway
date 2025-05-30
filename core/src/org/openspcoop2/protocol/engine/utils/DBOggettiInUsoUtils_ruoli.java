/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it). 
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
import org.openspcoop2.core.constants.ProprietariProtocolProperty;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDRuolo;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.mapping.DBMappingUtils;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
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
		return isRuoloInUsoEngine(con,tipoDB,idRuolo,false,true,whereIsInUso,normalizeObjectIds);
	}
	protected static boolean isRuoloRegistryInUso(Connection con, String tipoDB, IDRuolo idRuolo, Map<ErrorsHandlerCostant, 
			List<String>> whereIsInUso, boolean normalizeObjectIds) throws UtilsException {
		return isRuoloInUsoEngine(con,tipoDB,idRuolo,true,false,whereIsInUso,normalizeObjectIds);
	}
	protected static boolean isRuoloInUso(Connection con, String tipoDB, IDRuolo idRuolo, Map<ErrorsHandlerCostant, 
			List<String>> whereIsInUso, boolean normalizeObjectIds) throws UtilsException {
		return isRuoloInUsoEngine(con,tipoDB,idRuolo,true,true,whereIsInUso,normalizeObjectIds);
	}
	private static boolean isRuoloInUsoEngine(Connection con, String tipoDB, IDRuolo idRuolo, boolean registry, boolean config, Map<ErrorsHandlerCostant, 
			List<String>> whereIsInUso, boolean normalizeObjectIds) throws UtilsException {
		String nomeMetodo = "_isRuoloInUso";

		PreparedStatement stmt = null;
		ResultSet risultato = null;
		String queryString;

		try {

			long idR = DBUtils.getIdRuolo(idRuolo, con, tipoDB);
			
			boolean isInUso = false;
			
			List<String> soggettiList = whereIsInUso.get(ErrorsHandlerCostant.IN_USO_IN_SOGGETTI);
			List<String> serviziApplicativiList = whereIsInUso.get(ErrorsHandlerCostant.IN_USO_IN_SERVIZI_APPLICATIVI);
			List<String> porteApplicativeList = whereIsInUso.get(ErrorsHandlerCostant.IN_USO_IN_PORTE_APPLICATIVE);
			List<String> porteApplicativeModiList = whereIsInUso.get(ErrorsHandlerCostant.IN_USO_IN_PORTE_APPLICATIVE_MODI);
			List<String> porteDelegateList = whereIsInUso.get(ErrorsHandlerCostant.IN_USO_IN_PORTE_DELEGATE);
			List<String> mappingErogazionePAList = whereIsInUso.get(ErrorsHandlerCostant.IN_USO_IN_MAPPING_EROGAZIONE_PA);
			List<String> mappingErogazionePAModiList = whereIsInUso.get(ErrorsHandlerCostant.IN_USO_IN_MAPPING_EROGAZIONE_PA_MODI);
			List<String> mappingFruizionePDList = whereIsInUso.get(ErrorsHandlerCostant.IN_USO_IN_MAPPING_FRUIZIONE_PD);
			List<String> porteApplicativeTokenList = whereIsInUso.get(ErrorsHandlerCostant.RUOLI_TOKEN_PA);
			List<String> porteApplicativeTokenModiList = whereIsInUso.get(ErrorsHandlerCostant.RUOLI_TOKEN_PA_MODI);
			List<String> porteDelegateTokenList = whereIsInUso.get(ErrorsHandlerCostant.RUOLI_TOKEN_PD);
			List<String> mappingErogazioneTokenPAList = whereIsInUso.get(ErrorsHandlerCostant.RUOLI_TOKEN_MAPPING_PA);
			List<String> mappingErogazioneTokenPAModiList = whereIsInUso.get(ErrorsHandlerCostant.RUOLI_TOKEN_MAPPING_PA_MODI);
			List<String> mappingFruizioneTokenPDList = whereIsInUso.get(ErrorsHandlerCostant.RUOLI_TOKEN_MAPPING_PD);
			List<String> ctList = whereIsInUso.get(ErrorsHandlerCostant.CONTROLLO_TRAFFICO);
			List<String> allarmeList = whereIsInUso.get(ErrorsHandlerCostant.ALLARMI);
			List<String> modiSignalHubList = whereIsInUso.get(ErrorsHandlerCostant.IS_RIFERITA_MODI_SIGNAL_HUB);
			
			if (soggettiList == null) {
				soggettiList = new ArrayList<>();
				whereIsInUso.put(ErrorsHandlerCostant.IN_USO_IN_SOGGETTI, soggettiList);
			}
			if (serviziApplicativiList == null) {
				serviziApplicativiList = new ArrayList<>();
				whereIsInUso.put(ErrorsHandlerCostant.IN_USO_IN_SERVIZI_APPLICATIVI, serviziApplicativiList);
			}
			
			if (porteApplicativeList == null) {
				porteApplicativeList = new ArrayList<>();
				whereIsInUso.put(ErrorsHandlerCostant.IN_USO_IN_PORTE_APPLICATIVE, porteApplicativeList);
			}
			if (porteApplicativeModiList == null) {
				porteApplicativeModiList = new ArrayList<>();
				whereIsInUso.put(ErrorsHandlerCostant.IN_USO_IN_PORTE_APPLICATIVE_MODI, porteApplicativeModiList);
			}
			if (porteDelegateList == null) {
				porteDelegateList = new ArrayList<>();
				whereIsInUso.put(ErrorsHandlerCostant.IN_USO_IN_PORTE_DELEGATE, porteDelegateList);
			}
			if (mappingErogazionePAList == null) {
				mappingErogazionePAList = new ArrayList<>();
				whereIsInUso.put(ErrorsHandlerCostant.IN_USO_IN_MAPPING_EROGAZIONE_PA, mappingErogazionePAList);
			}
			if (mappingErogazionePAModiList == null) {
				mappingErogazionePAModiList = new ArrayList<>();
				whereIsInUso.put(ErrorsHandlerCostant.IN_USO_IN_MAPPING_EROGAZIONE_PA_MODI, mappingErogazionePAModiList);
			}
			if (mappingFruizionePDList == null) {
				mappingFruizionePDList = new ArrayList<>();
				whereIsInUso.put(ErrorsHandlerCostant.IN_USO_IN_MAPPING_FRUIZIONE_PD, mappingFruizionePDList);
			}
			
			if (porteApplicativeTokenList == null) {
				porteApplicativeTokenList = new ArrayList<>();
				whereIsInUso.put(ErrorsHandlerCostant.RUOLI_TOKEN_PA, porteApplicativeTokenList);
			}
			if (porteApplicativeTokenModiList == null) {
				porteApplicativeTokenModiList = new ArrayList<>();
				whereIsInUso.put(ErrorsHandlerCostant.RUOLI_TOKEN_PA_MODI, porteApplicativeTokenModiList);
			}
			if (porteDelegateTokenList == null) {
				porteDelegateTokenList = new ArrayList<>();
				whereIsInUso.put(ErrorsHandlerCostant.RUOLI_TOKEN_PD, porteDelegateTokenList);
			}
			if (mappingErogazioneTokenPAList == null) {
				mappingErogazioneTokenPAList = new ArrayList<>();
				whereIsInUso.put(ErrorsHandlerCostant.RUOLI_TOKEN_MAPPING_PA, mappingErogazioneTokenPAList);
			}
			if (mappingErogazioneTokenPAModiList == null) {
				mappingErogazioneTokenPAModiList = new ArrayList<>();
				whereIsInUso.put(ErrorsHandlerCostant.RUOLI_TOKEN_MAPPING_PA_MODI, mappingErogazioneTokenPAModiList);
			}
			if (mappingFruizioneTokenPDList == null) {
				mappingFruizioneTokenPDList = new ArrayList<>();
				whereIsInUso.put(ErrorsHandlerCostant.RUOLI_TOKEN_MAPPING_PD, mappingFruizioneTokenPDList);
			}
			
			if (ctList == null) {
				ctList = new ArrayList<>();
				whereIsInUso.put(ErrorsHandlerCostant.CONTROLLO_TRAFFICO, ctList);
			}
			if (allarmeList == null) {
				allarmeList = new ArrayList<>();
				whereIsInUso.put(ErrorsHandlerCostant.ALLARMI, allarmeList);
			}
			
			if (modiSignalHubList == null) {
				modiSignalHubList = new ArrayList<>();
				whereIsInUso.put(ErrorsHandlerCostant.IS_RIFERITA_MODI_SIGNAL_HUB, modiSignalHubList);
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
					String tipoSoggetto = risultato.getString("tipo_soggetto");
					String nomeSoggetto = risultato.getString("nome_soggetto");
					IDSoggetto idSoggetto = new IDSoggetto(tipoSoggetto, nomeSoggetto);
					if(normalizeObjectIds) {
						String protocollo = ProtocolFactoryManager.getInstance().getProtocolByOrganizationType(tipoSoggetto);
						soggettiList.add(DBOggettiInUsoUtils.getProtocolPrefix(protocollo)+NamingUtils.getLabelSoggetto(protocollo, idSoggetto));
					}
					else {
						soggettiList.add(tipoSoggetto + "/" + nomeSoggetto);	
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
					String tipoSoggetto = risultato.getString("tipo_soggetto");
					String nomeSoggetto = risultato.getString("nome_soggetto");
					String nome = risultato.getString("nome");
					IDSoggetto idSoggetto = new IDSoggetto(tipoSoggetto, nomeSoggetto);
					if(normalizeObjectIds) {
						String protocollo = ProtocolFactoryManager.getInstance().getProtocolByOrganizationType(tipoSoggetto);
						serviziApplicativiList.add(DBOggettiInUsoUtils.getProtocolPrefix(protocollo)+nome+DBOggettiInUsoUtils.getSubjectSuffix(protocollo, idSoggetto));
					}
					else {
						serviziApplicativiList.add(tipoSoggetto + "/" + nomeSoggetto+"_"+nome);
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
							mappingErogazionePAModiList.add(resultPorta.label);
						else
							mappingErogazionePAList.add(resultPorta.label);
					}
					else {
						if(resultPorta.erogazioneModi)
							porteApplicativeModiList.add(resultPorta.label);
						else
							porteApplicativeList.add(resultPorta.label);
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
							mappingErogazioneTokenPAModiList.add(resultPorta.label);
						else
							mappingErogazioneTokenPAList.add(resultPorta.label);
					}
					else {
						if(resultPorta.erogazioneModi)
							porteApplicativeTokenModiList.add(resultPorta.label);
						else
							porteApplicativeTokenList.add(resultPorta.label);
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
						mappingFruizionePDList.add(resultPorta.label);
					}
					else {
						porteDelegateList.add(resultPorta.label);
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
						mappingFruizioneTokenPDList.add(resultPorta.label);
					}
					else {
						porteDelegateTokenList.add(resultPorta.label);
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
					String identificativoColumn = "active_policy_id";
					String aliasColumn = "policy_alias";
					List<String> list = ctList;
					String oggetto = "Policy";
					if(i==1) {
						tabella = CostantiDB.ALLARMI;
						identificativoColumn = "nome";
						aliasColumn = "alias";
						list = allarmeList;
						oggetto = "Allarme";
					}
				
					ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
					sqlQueryObject.addFromTable(tabella);
					sqlQueryObject.addSelectField(identificativoColumn);
					sqlQueryObject.addSelectField(aliasColumn);
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
						
						String alias = risultato.getString(aliasColumn);
						if(alias== null || "".equals(alias)) {
							alias = risultato.getString(identificativoColumn);
						}
						
						String nomePorta = risultato.getString("filtro_porta");
						String filtroRuolo = risultato.getString("filtro_ruolo");
						if(nomePorta!=null) {
							String tipo = null;
							String label = null;
							if("delegata".equals(filtroRuolo)) {
								try {
									ResultPorta resultPorta = DBOggettiInUsoUtils.formatPortaDelegata(nomePorta, tipoDB, con, normalizeObjectIds);
									if(resultPorta.mapping) {
										label = "Fruizione di Servizio "+ resultPorta.label;
									}
								}catch(Exception e) {
									tipo = "Outbound";
								}
							}
							else if("applicativa".equals(filtroRuolo)) {
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
								tipo = filtroRuolo;
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
			
			
			// ** Controllo che non sia riferito dalla configurazione SignalHub **		
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
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
			stmt.setString(index++, CostantiDB.MODIPA_API_IMPL_INFO_SIGNAL_HUB_PUBLISHER_ROLE_ID);
			stmt.setString(index++, idRuolo.getNome());
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
		StringBuilder msgBuilder = new StringBuilder("Ruolo '"+idRuolo.getNome()+"'" + intestazione+separator);
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
			case IN_USO_IN_SOGGETTI:
				if ( messages!=null && !messages.isEmpty()) {
					msgBuilder.append("utilizzato nei Soggetti: " + DBOggettiInUsoUtils.formatList(messages,separator) + separator);
				}
				break;
			case IN_USO_IN_SERVIZI_APPLICATIVI:
				if ( messages!=null && !messages.isEmpty()) {
					msgBuilder.append("utilizzato negli Applicativi: " + DBOggettiInUsoUtils.formatList(messages,separator) + separator);
				}
				break;
				
			case IN_USO_IN_PORTE_APPLICATIVE:
				if ( messages!=null && !messages.isEmpty()) {
					msgBuilder.append("utilizzato nelle Porte Inbound (Controllo Accessi - Autorizzazione Trasporto): " + DBOggettiInUsoUtils.formatList(messages,separator) + separator);
				}
				break;
			case IN_USO_IN_PORTE_APPLICATIVE_MODI:
				if ( messages!=null && !messages.isEmpty()) {
					msgBuilder.append("utilizzato nelle Porte Inbound (Controllo Accessi - Autorizzazione Canale): " + DBOggettiInUsoUtils.formatList(messages,separator) + separator);
				}
				break;
			case IN_USO_IN_PORTE_DELEGATE:
				if ( messages!=null && !messages.isEmpty()) {
					msgBuilder.append("utilizzato nelle Porte Outbound (Controllo Accessi - Autorizzazione Trasporto): " + DBOggettiInUsoUtils.formatList(messages,separator) + separator);
				}
				break;
			case IN_USO_IN_MAPPING_EROGAZIONE_PA:
				if ( messages!=null && !messages.isEmpty()) {
					msgBuilder.append("utilizzato nelle Erogazioni (Controllo Accessi - Autorizzazione Trasporto): " + DBOggettiInUsoUtils.formatList(messages,separator) + separator);
				}
				break;
			case IN_USO_IN_MAPPING_EROGAZIONE_PA_MODI:
				if ( messages!=null && !messages.isEmpty()) {
					msgBuilder.append("utilizzato nelle Erogazioni (Controllo Accessi - Autorizzazione Canale): " + DBOggettiInUsoUtils.formatList(messages,separator) + separator);
				}
				break;
			case IN_USO_IN_MAPPING_FRUIZIONE_PD:
				if ( messages!=null && !messages.isEmpty()) {
					msgBuilder.append("utilizzato nelle Fruizioni (Controllo Accessi - Autorizzazione Trasporto): " + DBOggettiInUsoUtils.formatList(messages,separator) + separator);
				}
				break;
				
			case RUOLI_TOKEN_PA:
				if ( messages!=null && !messages.isEmpty()) {
					msgBuilder.append("utilizzato nelle Porte Inbound (Controllo Accessi - Autorizzazione Token): " + DBOggettiInUsoUtils.formatList(messages,separator) + separator);
				}
				break;
			case RUOLI_TOKEN_PA_MODI:
				if ( messages!=null && !messages.isEmpty()) {
					msgBuilder.append("utilizzato nelle Porte Inbound (Controllo Accessi - Autorizzazione Messaggio): " + DBOggettiInUsoUtils.formatList(messages,separator) + separator);
				}
				break;
			case RUOLI_TOKEN_PD:
				if ( messages!=null && !messages.isEmpty()) {
					msgBuilder.append("utilizzato nelle Porte Outbound (Controllo Accessi - Autorizzazione Token): " + DBOggettiInUsoUtils.formatList(messages,separator) + separator);
				}
				break;
			case RUOLI_TOKEN_MAPPING_PA:
				if ( messages!=null && !messages.isEmpty()) {
					msgBuilder.append("utilizzato nelle Erogazioni (Controllo Accessi - Autorizzazione Token): " + DBOggettiInUsoUtils.formatList(messages,separator) + separator);
				}
				break;
			case RUOLI_TOKEN_MAPPING_PA_MODI:
				if ( messages!=null && !messages.isEmpty()) {
					msgBuilder.append("utilizzato nelle Erogazioni (Controllo Accessi - Autorizzazione Messaggio): " + DBOggettiInUsoUtils.formatList(messages,separator) + separator);
				}
				break;
			case RUOLI_TOKEN_MAPPING_PD:
				if ( messages!=null && !messages.isEmpty()) {
					msgBuilder.append("utilizzato nelle Fruizioni (Controllo Accessi - Autorizzazione Token): " + DBOggettiInUsoUtils.formatList(messages,separator) + separator);
				}
				break;
				
			case CONTROLLO_TRAFFICO:
				if ( messages!=null && !messages.isEmpty()) {
					msgBuilder.append("utilizzato in Policy di Rate Limiting: " + DBOggettiInUsoUtils.formatList(messages,separator) + separator);
				}
				break;
			case ALLARMI:
				if ( messages!=null && !messages.isEmpty()) {
					msgBuilder.append("utilizzato in Allarmi: " + DBOggettiInUsoUtils.formatList(messages,separator) + separator);
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
