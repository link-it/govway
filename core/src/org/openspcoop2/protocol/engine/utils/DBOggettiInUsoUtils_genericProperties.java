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

import org.openspcoop2.core.commons.ErrorsHandlerCostant;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.constants.CostantiLabel;
import org.openspcoop2.core.constants.ProprietariProtocolProperty;
import org.openspcoop2.core.id.IDGenericProperties;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.jdbc.JDBCUtilities;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.SQLObjectFactory;

/**
 * DBOggettiInUsoUtils_genericProperties
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class DBOggettiInUsoUtils_genericProperties {

	protected static boolean isGenericPropertiesInUso(Connection con, String tipoDB, IDGenericProperties idGP, Map<ErrorsHandlerCostant, 
			List<String>> whereIsInUso, boolean normalizeObjectIds) throws UtilsException {
		String nomeMetodo = "isGenericPropertiesInUso";

		PreparedStatement stmt = null;
		ResultSet risultato = null;
		PreparedStatement stmt2 = null;
		ResultSet risultato2 = null;
		String queryString;

		try {

			boolean isInUso = false;
			
			
			if(CostantiConfigurazione.GENERIC_PROPERTIES_TOKEN_TIPOLOGIA_VALIDATION.equals(idGP.getTipologia())) {
			
				List<String> tokenPA_list = whereIsInUso.get(ErrorsHandlerCostant.TOKEN_PA);
				List<String> tokenPD_list = whereIsInUso.get(ErrorsHandlerCostant.TOKEN_PD);
				
				List<String> mapping_tokenPA_list = whereIsInUso.get(ErrorsHandlerCostant.TOKEN_MAPPING_PA);
				List<String> mapping_tokenPD_list = whereIsInUso.get(ErrorsHandlerCostant.TOKEN_MAPPING_PD);
				
				List<String> tokenSA_list = whereIsInUso.get(ErrorsHandlerCostant.TOKEN_SA);
				List<String> tokenSA_MODI_list = whereIsInUso.get(ErrorsHandlerCostant.TOKEN_SA_MODI);
				
				if (tokenPA_list == null) {
					tokenPA_list = new ArrayList<>();
					whereIsInUso.put(ErrorsHandlerCostant.TOKEN_PA, tokenPA_list);
				}
				if (tokenPD_list == null) {
					tokenPD_list = new ArrayList<>();
					whereIsInUso.put(ErrorsHandlerCostant.TOKEN_PD, tokenPD_list);
				}
				
				if (mapping_tokenPA_list == null) {
					mapping_tokenPA_list = new ArrayList<>();
					whereIsInUso.put(ErrorsHandlerCostant.TOKEN_MAPPING_PA, mapping_tokenPA_list);
				}
				if (mapping_tokenPD_list == null) {
					mapping_tokenPD_list = new ArrayList<>();
					whereIsInUso.put(ErrorsHandlerCostant.TOKEN_MAPPING_PD, mapping_tokenPD_list);
				}
				
				if (tokenSA_list == null) {
					tokenSA_list = new ArrayList<>();
					whereIsInUso.put(ErrorsHandlerCostant.TOKEN_SA, tokenSA_list);
				}
				if (tokenSA_MODI_list == null) {
					tokenSA_MODI_list = new ArrayList<>();
					whereIsInUso.put(ErrorsHandlerCostant.TOKEN_SA_MODI, tokenSA_MODI_list);
				}
				
				
				// Controllo che non sia in uso nelle porte applicative
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE);
				sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
				sqlQueryObject.addSelectField(CostantiDB.PORTE_APPLICATIVE+".nome_porta");
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.setSelectDistinct(true);
				sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE+".token_policy = ?");
				sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE+".id_soggetto = "+CostantiDB.SOGGETTI+".id");
				queryString = sqlQueryObject.createSQLQuery();
				stmt = con.prepareStatement(queryString);
				stmt.setString(1, idGP.getNome());
				risultato = stmt.executeQuery();
				while (risultato.next()) {
					String nome = risultato.getString("nome_porta");
					ResultPorta resultPorta = DBOggettiInUsoUtils.formatPortaApplicativa(nome, tipoDB, con, normalizeObjectIds);
					if(resultPorta.mapping) {
						mapping_tokenPA_list.add(resultPorta.label);
					}
					else {
						tokenPA_list.add(resultPorta.label);
					}
					isInUso = true;
				}
				risultato.close();
				stmt.close();
				
				
				// Controllo che il ruolo non sia in uso nelle porte delegate
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE);
				sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
				sqlQueryObject.addSelectField(CostantiDB.PORTE_DELEGATE+".nome_porta");
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.setSelectDistinct(true);
				sqlQueryObject.addWhereCondition(CostantiDB.PORTE_DELEGATE+".token_policy = ?");
				sqlQueryObject.addWhereCondition(CostantiDB.PORTE_DELEGATE+".id_soggetto = "+CostantiDB.SOGGETTI+".id");
				queryString = sqlQueryObject.createSQLQuery();
				stmt = con.prepareStatement(queryString);
				stmt.setString(1, idGP.getNome());
				risultato = stmt.executeQuery();
				while (risultato.next()) {
					String nome = risultato.getString("nome_porta");
					ResultPorta resultPorta = DBOggettiInUsoUtils.formatPortaDelegata(nome, tipoDB, con, normalizeObjectIds);
					if(resultPorta.mapping) {
						mapping_tokenPD_list.add(resultPorta.label);
					}
					else {
						tokenPD_list.add(resultPorta.label);
					}
					isInUso = true;
				}
				risultato.close();
				stmt.close();				
				
				
				// Controllo che non sia in uso nelle credenziali di un servizio applicativo
				
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
				sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
				sqlQueryObject.addSelectField("tipo_soggetto");
				sqlQueryObject.addSelectField("nome_soggetto");
				sqlQueryObject.addSelectField(CostantiDB.SERVIZI_APPLICATIVI+".nome");
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.setSelectDistinct(true);
				sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".token_policy = ?");
				sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".id_soggetto = "+CostantiDB.SOGGETTI+".id");
				queryString = sqlQueryObject.createSQLQuery();
				stmt = con.prepareStatement(queryString);
				stmt.setString(1, idGP.getNome());
				risultato = stmt.executeQuery();
				while (risultato.next()) {
					String tipo_soggetto = risultato.getString("tipo_soggetto");
					String nome_soggetto = risultato.getString("nome_soggetto");
					String nome = risultato.getString("nome");
					IDSoggetto idSoggetto = new IDSoggetto(tipo_soggetto, nome_soggetto);
					if(normalizeObjectIds) {
						String protocollo = ProtocolFactoryManager.getInstance().getProtocolByOrganizationType(tipo_soggetto);
						tokenSA_list.add(DBOggettiInUsoUtils.getProtocolPrefix(protocollo)+nome+DBOggettiInUsoUtils.getSubjectSuffix(protocollo, idSoggetto));
					}
					else {
						tokenSA_list.add(tipo_soggetto + "/" + nome_soggetto+"_"+nome);
					}
					isInUso = true;
				}
				risultato.close();
				stmt.close();
				
				
				// Controllo che non sia in uso nelle credenziali di un servizio applicativo interno ModI
				
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PROTOCOL_PROPERTIES);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
				sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
				sqlQueryObject.addSelectField("tipo_soggetto");
				sqlQueryObject.addSelectField("nome_soggetto");
				sqlQueryObject.addSelectField(CostantiDB.SERVIZI_APPLICATIVI+".nome");
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.setSelectDistinct(true);
				sqlQueryObject.addWhereCondition(CostantiDB.PROTOCOL_PROPERTIES+".tipo_proprietario = ?");
				sqlQueryObject.addWhereCondition(CostantiDB.PROTOCOL_PROPERTIES+".id_proprietario = "+CostantiDB.SERVIZI_APPLICATIVI+".id");
				sqlQueryObject.addWhereCondition(CostantiDB.PROTOCOL_PROPERTIES+".name = ?");
				sqlQueryObject.addWhereCondition(CostantiDB.PROTOCOL_PROPERTIES+".value_string = ?");
				sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".id_soggetto = "+CostantiDB.SOGGETTI+".id");
				queryString = sqlQueryObject.createSQLQuery();
				stmt = con.prepareStatement(queryString);
				int index = 1;
				stmt.setString(index++, ProprietariProtocolProperty.SERVIZIO_APPLICATIVO.name());
				stmt.setString(index++, CostantiDB.MODIPA_SICUREZZA_TOKEN_POLICY);
				stmt.setString(index++, idGP.getNome());
				risultato = stmt.executeQuery();
				while (risultato.next()) {
					String tipo_soggetto = risultato.getString("tipo_soggetto");
					String nome_soggetto = risultato.getString("nome_soggetto");
					String nome = risultato.getString("nome");
					IDSoggetto idSoggetto = new IDSoggetto(tipo_soggetto, nome_soggetto);
					if(normalizeObjectIds) {
						String protocollo = ProtocolFactoryManager.getInstance().getProtocolByOrganizationType(tipo_soggetto);
						tokenSA_MODI_list.add(DBOggettiInUsoUtils.getProtocolPrefix(protocollo)+nome+DBOggettiInUsoUtils.getSubjectSuffix(protocollo, idSoggetto));
					}
					else {
						tokenSA_MODI_list.add(tipo_soggetto + "/" + nome_soggetto+"_"+nome);
					}
					isInUso = true;
				}
				risultato.close();
				stmt.close();
				
			}
			
			if(CostantiConfigurazione.GENERIC_PROPERTIES_TOKEN_TIPOLOGIA_RETRIEVE.equals(idGP.getTipologia())) {
				
				List<Long> idConnettori = new ArrayList<>();
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.CONNETTORI);
				sqlQueryObject.addSelectField(CostantiDB.CONNETTORI+".id");
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.setSelectDistinct(true);
				sqlQueryObject.addWhereCondition(CostantiDB.CONNETTORI+".token_policy = ?");
				queryString = sqlQueryObject.createSQLQuery();
				stmt = con.prepareStatement(queryString);
				stmt.setString(1, idGP.getNome());
				risultato = stmt.executeQuery();
				while (risultato.next()) {
					Long id = risultato.getLong("id");
					idConnettori.add(id);
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
			
			
			if(CostantiConfigurazione.GENERIC_PROPERTIES_ATTRIBUTE_AUTHORITY.equals(idGP.getTipologia())) {
				
				List<String> aaPA_list = whereIsInUso.get(ErrorsHandlerCostant.ATTRIBUTE_AUTHORITY_PA);
				List<String> aaPD_list = whereIsInUso.get(ErrorsHandlerCostant.ATTRIBUTE_AUTHORITY_PD);
				
				List<String> mapping_aaPA_list = whereIsInUso.get(ErrorsHandlerCostant.ATTRIBUTE_AUTHORITY_MAPPING_PA);
				List<String> mapping_aaPD_list = whereIsInUso.get(ErrorsHandlerCostant.ATTRIBUTE_AUTHORITY_MAPPING_PD);
				
				if (aaPA_list == null) {
					aaPA_list = new ArrayList<>();
					whereIsInUso.put(ErrorsHandlerCostant.TOKEN_PA, aaPA_list);
				}
				if (aaPD_list == null) {
					aaPD_list = new ArrayList<>();
					whereIsInUso.put(ErrorsHandlerCostant.TOKEN_PD, aaPD_list);
				}
				
				if (mapping_aaPA_list == null) {
					mapping_aaPA_list = new ArrayList<>();
					whereIsInUso.put(ErrorsHandlerCostant.TOKEN_MAPPING_PA, mapping_aaPA_list);
				}
				if (mapping_aaPD_list == null) {
					mapping_aaPD_list = new ArrayList<>();
					whereIsInUso.put(ErrorsHandlerCostant.TOKEN_MAPPING_PD, mapping_aaPD_list);
				}
				
				// Controllo che non sia in uso nelle porte applicative
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_ATTRIBUTE_AUTHORITY);
				sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
				sqlQueryObject.addSelectField(CostantiDB.PORTE_APPLICATIVE+".nome_porta");
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.setSelectDistinct(true);
				sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE_ATTRIBUTE_AUTHORITY+".nome = ?");
				sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE_ATTRIBUTE_AUTHORITY+".id_porta = "+CostantiDB.PORTE_APPLICATIVE+".id");
				sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE+".id_soggetto = "+CostantiDB.SOGGETTI+".id");
				queryString = sqlQueryObject.createSQLQuery();
				stmt = con.prepareStatement(queryString);
				stmt.setString(1, idGP.getNome());
				risultato = stmt.executeQuery();
				while (risultato.next()) {
					String nome = risultato.getString("nome_porta");
					ResultPorta resultPorta = DBOggettiInUsoUtils.formatPortaApplicativa(nome, tipoDB, con, normalizeObjectIds);
					if(resultPorta.mapping) {
						mapping_aaPA_list.add(resultPorta.label);
					}
					else {
						aaPA_list.add(resultPorta.label);
					}
					isInUso = true;
				}
				risultato.close();
				stmt.close();
				
				
				// Controllo che il ruolo non sia in uso nelle porte delegate
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE_ATTRIBUTE_AUTHORITY);
				sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
				sqlQueryObject.addSelectField(CostantiDB.PORTE_DELEGATE+".nome_porta");
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.setSelectDistinct(true);
				sqlQueryObject.addWhereCondition(CostantiDB.PORTE_DELEGATE_ATTRIBUTE_AUTHORITY+".nome = ?");
				sqlQueryObject.addWhereCondition(CostantiDB.PORTE_DELEGATE_ATTRIBUTE_AUTHORITY+".id_porta = "+CostantiDB.PORTE_DELEGATE+".id");
				sqlQueryObject.addWhereCondition(CostantiDB.PORTE_DELEGATE+".id_soggetto = "+CostantiDB.SOGGETTI+".id");
				queryString = sqlQueryObject.createSQLQuery();
				stmt = con.prepareStatement(queryString);
				stmt.setString(1, idGP.getNome());
				risultato = stmt.executeQuery();
				while (risultato.next()) {
					String nome = risultato.getString("nome_porta");
					ResultPorta resultPorta = DBOggettiInUsoUtils.formatPortaDelegata(nome, tipoDB, con, normalizeObjectIds);
					if(resultPorta.mapping) {
						mapping_aaPD_list.add(resultPorta.label);
					}
					else {
						aaPD_list.add(resultPorta.label);
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


	protected static String toString(IDGenericProperties idGP, Map<ErrorsHandlerCostant, List<String>> whereIsInUso, boolean prefix, String separator){
		return toString(idGP, whereIsInUso, prefix, separator," non eliminabile perch&egrave; :");
	}
	protected static String toString(IDGenericProperties idGP, Map<ErrorsHandlerCostant, List<String>> whereIsInUso, boolean prefix, String separator, String intestazione){
		Set<ErrorsHandlerCostant> keys = whereIsInUso.keySet();
		String object = "Token Policy";
		if(idGP!=null && CostantiConfigurazione.GENERIC_PROPERTIES_ATTRIBUTE_AUTHORITY.equals(idGP.getTipologia())) {
			object = "Attribute Authority";
		}
		String msg = object+" '"+(idGP!=null ? idGP.getNome() : "?")+"'" + intestazione+separator;
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
			
			case TOKEN_MAPPING_PD:
				if ( messages!=null && messages.size() > 0) {
					msg += "utilizzata nel controllo degli accessi per le Fruizioni: " + DBOggettiInUsoUtils.formatList(messages,separator) + separator;
				}
				break;
			case TOKEN_PD:
				if ( messages!=null && messages.size() > 0) {
					msg += "utilizzato nelle Porte Outbound (Controllo degli Accessi): " + DBOggettiInUsoUtils.formatList(messages,separator) + separator;
				}
				break;
			case TOKEN_MAPPING_PA:
				if ( messages!=null && messages.size() > 0) {
					msg += "utilizzato nel controllo degli accessi per le Erogazioni: " + DBOggettiInUsoUtils.formatList(messages,separator) + separator;
				}
				break;
			case TOKEN_PA:
				if ( messages!=null && messages.size() > 0) {
					msg += "utilizzato nelle Porte Inbound (Controllo degli Accessi): " + DBOggettiInUsoUtils.formatList(messages,separator) + separator;
				}
				break;
				
			case TOKEN_SA:
				if ( messages!=null && messages.size() > 0) {
					msg += "utilizzato negli Applicativi: " + DBOggettiInUsoUtils.formatList(messages,separator) + separator;
				}
				break;
			case TOKEN_SA_MODI:
				if ( messages!=null && messages.size() > 0) {
					msg += "utilizzato negli Applicativi (ModI "+CostantiLabel.MODIPA_SICUREZZA_TOKEN_SUBTITLE_LABEL+"): " + DBOggettiInUsoUtils.formatList(messages,separator) + separator;
				}
				break;
				
			case CONNETTORE_MAPPING_PD:
				if ( messages!=null && messages.size() > 0) {
					msg += "utilizzata nel connettore per le Fruizioni: " + DBOggettiInUsoUtils.formatList(messages,separator) + separator;
				}
				break;
			case CONNETTORE_PD:
				if ( messages!=null && messages.size() > 0) {
					msg += "utilizzato nelle Porte Outbound (Connettore): " + DBOggettiInUsoUtils.formatList(messages,separator) + separator;
				}
				break;
			case CONNETTORE_MAPPING_PA:
				if ( messages!=null && messages.size() > 0) {
					msg += "utilizzato nel connettore per le Erogazioni: " + DBOggettiInUsoUtils.formatList(messages,separator) + separator;
				}
				break;
			case CONNETTORE_PA:
				if ( messages!=null && messages.size() > 0) {
					msg += "utilizzato nelle Porte Inbound (Connettore): " + DBOggettiInUsoUtils.formatList(messages,separator) + separator;
				}
				break;
				
			case ATTRIBUTE_AUTHORITY_MAPPING_PD:
				if ( messages!=null && messages.size() > 0) {
					msg += "utilizzata nel controllo degli accessi per le Fruizioni: " + DBOggettiInUsoUtils.formatList(messages,separator) + separator;
				}
				break;
			case ATTRIBUTE_AUTHORITY_PD:
				if ( messages!=null && messages.size() > 0) {
					msg += "utilizzato nelle Porte Outbound (Controllo degli Accessi): " + DBOggettiInUsoUtils.formatList(messages,separator) + separator;
				}
				break;
			case ATTRIBUTE_AUTHORITY_MAPPING_PA:
				if ( messages!=null && messages.size() > 0) {
					msg += "utilizzato nel controllo degli accessi per le Erogazioni: " + DBOggettiInUsoUtils.formatList(messages,separator) + separator;
				}
				break;
			case ATTRIBUTE_AUTHORITY_PA:
				if ( messages!=null && messages.size() > 0) {
					msg += "utilizzato nelle Porte Inbound (Controllo degli Accessi): " + DBOggettiInUsoUtils.formatList(messages,separator) + separator;
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
