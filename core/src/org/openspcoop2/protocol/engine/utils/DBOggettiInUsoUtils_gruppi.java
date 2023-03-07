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
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDGruppo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.jdbc.JDBCUtilities;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.SQLObjectFactory;

/**
 * DBOggettiInUsoUtils_gruppi
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class DBOggettiInUsoUtils_gruppi {

	// Lascio i metodi se servissero in futuro
	protected static boolean isGruppoConfigInUso(Connection con, String tipoDB, IDGruppo idGruppo, Map<ErrorsHandlerCostant, 
			List<String>> whereIsInUso, boolean normalizeObjectIds) throws UtilsException {
		return _isGruppoInUso(con,tipoDB,idGruppo,false,true,whereIsInUso,normalizeObjectIds);
	}
	protected static boolean isGruppoRegistryInUso(Connection con, String tipoDB, IDGruppo idGruppo, Map<ErrorsHandlerCostant, 
			List<String>> whereIsInUso, boolean normalizeObjectIds) throws UtilsException {
		return _isGruppoInUso(con,tipoDB,idGruppo,true,false,whereIsInUso,normalizeObjectIds);
	}
	protected static boolean isGruppoInUso(Connection con, String tipoDB, IDGruppo idGruppo, Map<ErrorsHandlerCostant, 
			List<String>> whereIsInUso, boolean normalizeObjectIds) throws UtilsException {
		return _isGruppoInUso(con,tipoDB,idGruppo,true,true,whereIsInUso,normalizeObjectIds);
	}
	private static boolean _isGruppoInUso(Connection con, String tipoDB, IDGruppo idGruppo, boolean registry, boolean config, Map<ErrorsHandlerCostant, 
			List<String>> whereIsInUso, boolean normalizeObjectIds) throws UtilsException {
		String nomeMetodo = "_isGruppoInUso";

		PreparedStatement stmt = null;
		ResultSet risultato = null;
		PreparedStatement stmt2 = null;
		ResultSet risultato2 = null;
		String queryString;

		try {

			long idG = DBUtils.getIdGruppo(idGruppo, con, tipoDB);

			boolean isInUso = false;

			List<String> accordi_list = whereIsInUso.get(ErrorsHandlerCostant.IN_USO_IN_ACCORDI);
			List<String> ct_list = whereIsInUso.get(ErrorsHandlerCostant.CONTROLLO_TRAFFICO);
			List<String> allarme_list = whereIsInUso.get(ErrorsHandlerCostant.ALLARMI);	

			if (accordi_list == null) {
				accordi_list = new ArrayList<String>();
				whereIsInUso.put(ErrorsHandlerCostant.IN_USO_IN_ACCORDI, accordi_list);
			}
			if (ct_list == null) {
				ct_list = new ArrayList<String>();
				whereIsInUso.put(ErrorsHandlerCostant.CONTROLLO_TRAFFICO, ct_list);
			}
			if (allarme_list == null) {
				allarme_list = new ArrayList<String>();
				whereIsInUso.put(ErrorsHandlerCostant.ALLARMI, allarme_list);
			}


			// Controllo che il gruppo non sia in uso negli accordi
			if(registry){

				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.ACCORDI_GRUPPI);
				sqlQueryObject.addFromTable(CostantiDB.ACCORDI);
				sqlQueryObject.addSelectField("*");
				sqlQueryObject.addWhereCondition(CostantiDB.ACCORDI_GRUPPI+".id_gruppo = ?");
				sqlQueryObject.addWhereCondition(CostantiDB.ACCORDI_GRUPPI+".id_accordo = "+CostantiDB.ACCORDI+".id");
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
				stmt = con.prepareStatement(queryString);
				stmt.setLong(1, idG);
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

			// Controllo che il soggetto non sia associato a policy di controllo del traffico o allarmi
			if(config){

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

					ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
					sqlQueryObject.addFromTable(tabella);
					sqlQueryObject.addSelectField(identificativo_column);
					sqlQueryObject.addSelectField(alias_column);
					sqlQueryObject.addSelectField("filtro_ruolo");
					sqlQueryObject.addSelectField("filtro_porta");
					sqlQueryObject.setANDLogicOperator(false); // OR
					sqlQueryObject.addWhereCondition(true, 
							tabella+".filtro_tag = ?");
					queryString = sqlQueryObject.createSQLQuery();
					sqlQueryObject.addOrderBy("filtro_ruolo");
					sqlQueryObject.addOrderBy("filtro_porta");
					stmt = con.prepareStatement(queryString);
					int index = 1;
					stmt.setString(index++, idGruppo.getNome());
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
			JDBCUtilities.closeResources(risultato2, stmt2);
			JDBCUtilities.closeResources(risultato, stmt);
		}
	}


	protected static String toString(IDGruppo idGruppo, Map<ErrorsHandlerCostant, List<String>> whereIsInUso, boolean prefix, String separator){
		return toString(idGruppo, whereIsInUso, prefix, separator," non eliminabile perch&egrave; :");
	}
	protected static String toString(IDGruppo idGruppo, Map<ErrorsHandlerCostant, List<String>> whereIsInUso, boolean prefix, String separator, String intestazione){
		Set<ErrorsHandlerCostant> keys = whereIsInUso.keySet();
		String msg = "Gruppo '"+idGruppo.getNome()+"'" + intestazione+separator;
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
