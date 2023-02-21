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

import org.openspcoop2.core.commons.ErrorsHandlerCostant;
import org.openspcoop2.core.config.CanaleConfigurazione;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.LikeConfig;
import org.openspcoop2.utils.sql.SQLObjectFactory;

/**
 * DBOggettiInUsoUtils_canali
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class DBOggettiInUsoUtils_canali {

	protected static boolean isCanaleInUsoRegistro(Connection con, String tipoDB, CanaleConfigurazione canale, Map<ErrorsHandlerCostant, List<String>> whereIsInUso, boolean normalizeObjectIds) throws UtilsException {
		return isCanaleInUso(con, tipoDB, canale, whereIsInUso, normalizeObjectIds, true, true, false);
	}
	protected static boolean isCanaleInUso(Connection con, String tipoDB, CanaleConfigurazione canale, Map<ErrorsHandlerCostant, List<String>> whereIsInUso, boolean normalizeObjectIds) throws UtilsException {
		return isCanaleInUso(con, tipoDB, canale, whereIsInUso, normalizeObjectIds, true, true, true);
	}
	protected static boolean isCanaleInUso(Connection con, String tipoDB, CanaleConfigurazione canale, Map<ErrorsHandlerCostant, List<String>> whereIsInUso, boolean normalizeObjectIds, boolean registry, boolean config, boolean nodi) throws UtilsException {
		String nomeMetodo = "_isCanaleInUso";

		PreparedStatement stmt = null;
		ResultSet risultato = null;
		PreparedStatement stmt2 = null;
		ResultSet risultato2 = null;
		String queryString;

		try {
			String nomeCanale = canale.getNome();
			
			boolean isInUso = false;
			
			List<String> accordi_list = whereIsInUso.get(ErrorsHandlerCostant.IN_USO_IN_ACCORDI);
			List<String> porte_applicative_list = whereIsInUso.get(ErrorsHandlerCostant.IN_USO_IN_PORTE_APPLICATIVE);
			List<String> porte_delegate_list = whereIsInUso.get(ErrorsHandlerCostant.IN_USO_IN_PORTE_DELEGATE);
			List<String> mappingErogazionePA_list = whereIsInUso.get(ErrorsHandlerCostant.IN_USO_IN_MAPPING_EROGAZIONE_PA);
			List<String> mappingFruizionePD_list = whereIsInUso.get(ErrorsHandlerCostant.IN_USO_IN_MAPPING_FRUIZIONE_PD);
			List<String> nodi_list = whereIsInUso.get(ErrorsHandlerCostant.IN_USO_IN_CANALE_NODO);
			
			if (accordi_list == null) {
				accordi_list = new ArrayList<String>();
				whereIsInUso.put(ErrorsHandlerCostant.IN_USO_IN_ACCORDI, accordi_list);
			}
			if (porte_applicative_list == null) {
				porte_applicative_list = new ArrayList<String>();
				whereIsInUso.put(ErrorsHandlerCostant.IN_USO_IN_PORTE_APPLICATIVE, porte_applicative_list);
			}
			if (porte_delegate_list == null) {
				porte_delegate_list = new ArrayList<String>();
				whereIsInUso.put(ErrorsHandlerCostant.IN_USO_IN_PORTE_DELEGATE, porte_delegate_list);
			}
			if (mappingErogazionePA_list == null) {
				mappingErogazionePA_list = new ArrayList<String>();
				whereIsInUso.put(ErrorsHandlerCostant.IN_USO_IN_MAPPING_EROGAZIONE_PA, mappingErogazionePA_list);
			}
			if (mappingFruizionePD_list == null) {
				mappingFruizionePD_list = new ArrayList<String>();
				whereIsInUso.put(ErrorsHandlerCostant.IN_USO_IN_MAPPING_FRUIZIONE_PD, mappingFruizionePD_list);
			}
			if (nodi_list == null) {
				nodi_list = new ArrayList<String>();
				whereIsInUso.put(ErrorsHandlerCostant.IN_USO_IN_CANALE_NODO, nodi_list);
			}
			
			// Controllo che il gruppo non sia in uso negli accordi
			if(registry) {
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.ACCORDI);
				sqlQueryObject.addSelectField("*");
				sqlQueryObject.addWhereCondition(CostantiDB.ACCORDI+".canale = ?");
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
				stmt = con.prepareStatement(queryString);
				stmt.setString(1, nomeCanale);
				risultato = stmt.executeQuery();
				while (risultato.next()){
					isInUso=true;
					
					String nomeAccordo = risultato.getString("nome");
					int versione = risultato.getInt("versione");
					long idReferente = risultato.getLong("id_referente");
					IDSoggetto idReferenteObject = null;
					
					if(idReferente>0){
	
						ISQLQueryObject sqlQueryObjectReferente = SQLObjectFactory.createSQLQueryObject(tipoDB);
						sqlQueryObjectReferente.addFromTable(CostantiDB.SOGGETTI);
						sqlQueryObjectReferente.addSelectField("*");
						sqlQueryObjectReferente.addWhereCondition("id=?");
						sqlQueryObjectReferente.setANDLogicOperator(true);
						String queryStringReferente = sqlQueryObjectReferente.createSQLQuery();
						stmt2 = con.prepareStatement(queryStringReferente);
						stmt2.setLong(1, idReferente);
						risultato2 = stmt2.executeQuery();
						if(risultato2.next()){
							idReferenteObject = new IDSoggetto();
							idReferenteObject.setTipo(risultato2.getString("tipo_soggetto"));
							idReferenteObject.setNome(risultato2.getString("nome_soggetto"));
						}
						risultato2.close(); risultato2=null;
						stmt2.close(); stmt2=null;
	
					}
					
					if(normalizeObjectIds && idReferenteObject!=null) {
						String protocollo = ProtocolFactoryManager.getInstance().getProtocolByOrganizationType(idReferenteObject.getTipo());
						IDAccordo idAccordo = IDAccordoFactory.getInstance().getIDAccordoFromValues(nomeAccordo, idReferenteObject, versione); 
						accordi_list.add(DBOggettiInUsoUtils.getProtocolPrefix(protocollo)+NamingUtils.getLabelAccordoServizioParteComune(protocollo, idAccordo));
					}
					else {
	
						StringBuilder bf = new StringBuilder();
	
						bf.append(idReferenteObject!=null ? idReferenteObject.getTipo() : "?");
						bf.append("/");
						bf.append(idReferenteObject!=null ? idReferenteObject.getNome() : "?");
						bf.append(":");
						
						bf.append(nomeAccordo);
		
						if(idReferente>0){
							bf.append(":");
							bf.append(versione);
						}
		
						accordi_list.add(bf.toString());
					}
					
				}
				risultato.close();
				stmt.close();
			}
			
			// Controllo che il canale non sia in uso nelle porte applicative
			if(config){
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE);
				sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
				sqlQueryObject.addSelectField(CostantiDB.PORTE_APPLICATIVE+".nome_porta");
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.setSelectDistinct(true);
				sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE+".canale = ?");
				sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE+".id_soggetto = "+CostantiDB.SOGGETTI+".id");
				queryString = sqlQueryObject.createSQLQuery();
				stmt = con.prepareStatement(queryString);
				stmt.setString(1, nomeCanale);
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
			}
			
			// Controllo che il canale non sia in uso nelle porte applicative
			if(config){
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE);
				sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
				sqlQueryObject.addSelectField(CostantiDB.PORTE_DELEGATE+".nome_porta");
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.setSelectDistinct(true);
				sqlQueryObject.addWhereCondition(CostantiDB.PORTE_DELEGATE+".canale = ?");
				sqlQueryObject.addWhereCondition(CostantiDB.PORTE_DELEGATE+".id_soggetto = "+CostantiDB.SOGGETTI+".id");
				queryString = sqlQueryObject.createSQLQuery();
				stmt = con.prepareStatement(queryString);
				stmt.setString(1, nomeCanale);
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
			
			// Controllo che il canale non sia in uso in un nodo
			if(nodi){
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.CONFIGURAZIONE_CANALI_NODI);
				sqlQueryObject.addSelectField(CostantiDB.CONFIGURAZIONE_CANALI_NODI+".nome");
				sqlQueryObject.setANDLogicOperator(false);
				sqlQueryObject.setSelectDistinct(true);
				// condizione di controllo
				// (canali == 'NOME') OR (canali like 'CANALE,%') OR (canali like '%,CANALE') OR (canali like '%,CANALE,%')
				sqlQueryObject.addWhereCondition(CostantiDB.CONFIGURAZIONE_CANALI_NODI+".canali = ?");
				// fix: devo generare io il % e non farlo fare a sql query object poiche' mi serve lo start e l'end
				sqlQueryObject.addWhereLikeCondition(CostantiDB.CONFIGURAZIONE_CANALI_NODI+".canali", nomeCanale+",", LikeConfig.startsWith(false));
				sqlQueryObject.addWhereLikeCondition(CostantiDB.CONFIGURAZIONE_CANALI_NODI+".canali", ","+nomeCanale, LikeConfig.endsWith(false));
				sqlQueryObject.addWhereLikeCondition(CostantiDB.CONFIGURAZIONE_CANALI_NODI+".canali", ","+nomeCanale+",", true , false);
				queryString = sqlQueryObject.createSQLQuery(); 
				stmt = con.prepareStatement(queryString);
				stmt.setString(1, nomeCanale);
				risultato = stmt.executeQuery();
				while (risultato.next()) {
					String nome = risultato.getString("nome");
					nodi_list.add(nome);
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
			try {
				if (risultato2 != null) {
					risultato2.close();
				}
			} catch (Exception e) {
				// ignore
			}
			try {
				if (stmt2 != null) {
					stmt2.close();
				}
			} catch (Exception e) {
				// ignore
			}
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
	}

	protected static String toString(CanaleConfigurazione canale, Map<ErrorsHandlerCostant, List<String>> whereIsInUso, boolean prefix, String separator){
		return toString(canale, whereIsInUso, prefix, separator," non eliminabile perch&egrave; :");
	}
	protected static String toString(CanaleConfigurazione canale, Map<ErrorsHandlerCostant, List<String>> whereIsInUso, boolean prefix, String separator, String intestazione){
		Set<ErrorsHandlerCostant> keys = whereIsInUso.keySet();
		String msg = "Canale '"+canale.getNome()+"'" + intestazione+separator;
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
			case IN_USO_IN_ACCORDI:
				if ( messages!=null && messages.size() > 0 ) {
					msg += "associato all'API: " + DBOggettiInUsoUtils.formatList(messages,separator) + separator;
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
			case IN_USO_IN_PORTE_DELEGATE:
				if ( messages!=null && messages.size() > 0 ) {
					msg += "in uso in Porte Delegate: " + DBOggettiInUsoUtils.formatList(messages,separator) + separator;
				}
				break;
			case IN_USO_IN_PORTE_APPLICATIVE:
				if ( messages!=null && messages.size() > 0 ) {
					msg += "in uso in Porte Applicative: " + DBOggettiInUsoUtils.formatList(messages,separator) + separator;
				}
				break;
			case IN_USO_IN_CANALE_NODO:
				if ( messages!=null && messages.size() > 0) {
					msg += "utilizzato nei Nodi: " + DBOggettiInUsoUtils.formatList(messages,separator) + separator;
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
