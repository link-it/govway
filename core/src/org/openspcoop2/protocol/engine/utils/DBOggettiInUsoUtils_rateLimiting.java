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
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.controllo_traffico.IdPolicy;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.jdbc.JDBCUtilities;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.SQLObjectFactory;

/**
 * DBOggettiInUsoUtils_rateLimiting
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class DBOggettiInUsoUtils_rateLimiting {

	protected static boolean isRateLimitingPolicyInUso(Connection con, String tipoDB, IdPolicy idPolicy, 
			Map<ErrorsHandlerCostant, List<String>> whereIsInUso, boolean normalizeObjectIds) throws UtilsException {
		
		String nomeMetodo = "isRateLimitingPolicyInUso";
		
		PreparedStatement stmt = null;
		ResultSet risultato = null;
		PreparedStatement stmt2 = null;
		ResultSet risultato2 = null;
		String queryString;
		try {
			boolean isInUso = false;
			
			
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
			sqlQueryObject.addWhereCondition(CostantiDB.CONTROLLO_TRAFFICO_ACTIVE_POLICY+".policy_id = ?");
			sqlQueryObject.addOrderBy("filtro_ruolo");
			sqlQueryObject.addOrderBy("filtro_porta");
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			int index = 1;
			stmt.setString(index++, idPolicy.getNome());
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
			
			
			return isInUso;

		} catch (Exception se) {
			throw new UtilsException("[DBOggettiInUsoUtils::" + nomeMetodo + "] Exception: " + se.getMessage(),se);
		} finally {
			// Chiudo statement and resultset
			JDBCUtilities.closeResources(risultato2, stmt2);
			JDBCUtilities.closeResources(risultato, stmt);
		}
	}
	

	protected static String toString(IdPolicy idPolicy, Map<ErrorsHandlerCostant, List<String>> whereIsInUso, boolean prefix, String separator){
		
		StringBuilder bf = new StringBuilder();
	
		bf.append(idPolicy.getNome());
		
		Set<ErrorsHandlerCostant> keys = whereIsInUso.keySet();
		String msg = "Policy '" +bf.toString() +"' non eliminabile perch&egrave; :"+separator;
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
							
			case CONTROLLO_TRAFFICO:
				if ( messages!=null && messages.size() > 0 ) {
					msg += "utilizzato in: " + DBOggettiInUsoUtils.formatList(messages,separator) + separator;
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
