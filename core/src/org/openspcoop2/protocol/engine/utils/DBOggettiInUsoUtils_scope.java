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
import org.openspcoop2.core.id.IDScope;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.jdbc.JDBCUtilities;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.SQLObjectFactory;

/**
 * DBOggettiInUsoUtils_scope
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class DBOggettiInUsoUtils_scope {

	protected static boolean isScopeConfigInUso(Connection con, String tipoDB, IDScope idScope, Map<ErrorsHandlerCostant, 
			List<String>> whereIsInUso, boolean normalizeObjectIds) throws UtilsException {
		return _isScopeInUso(con,tipoDB,idScope,false,true,whereIsInUso,normalizeObjectIds);
	}
//	protected static boolean isScopeRegistryInUso(Connection con, String tipoDB, IDScope idScope, Map<ErrorsHandlerCostant, 
//			List<String>> whereIsInUso, boolean normalizeObjectIds) throws UtilsException {
//		return _isScopeInUso(con,tipoDB,idScope,true,false,whereIsInUso,normalizeObjectIds);
//	}
	protected static boolean isScopeInUso(Connection con, String tipoDB, IDScope idScope, Map<ErrorsHandlerCostant, 
			List<String>> whereIsInUso, boolean normalizeObjectIds) throws UtilsException {
		return _isScopeInUso(con,tipoDB,idScope,true,true,whereIsInUso,normalizeObjectIds);
	}
	private static boolean _isScopeInUso(Connection con, String tipoDB, IDScope idScope, boolean registry, boolean config, 
			Map<ErrorsHandlerCostant, List<String>> whereIsInUso, boolean normalizeObjectIds) throws UtilsException {
		String nomeMetodo = "_isScopeInUso";

		PreparedStatement stmt = null;
		ResultSet risultato = null;
		String queryString;

		try {

			//long idS = DBUtils.getIdScope(idScope, con, tipoDB);
			
			boolean isInUso = false;
			
			List<String> porte_applicative_list = whereIsInUso.get(ErrorsHandlerCostant.IN_USO_IN_PORTE_APPLICATIVE);
			List<String> porte_delegate_list = whereIsInUso.get(ErrorsHandlerCostant.IN_USO_IN_PORTE_DELEGATE);
			List<String> mappingErogazionePA_list = whereIsInUso.get(ErrorsHandlerCostant.IN_USO_IN_MAPPING_EROGAZIONE_PA);
			List<String> mappingFruizionePD_list = whereIsInUso.get(ErrorsHandlerCostant.IN_USO_IN_MAPPING_FRUIZIONE_PD);
			
			if (porte_applicative_list == null) {
				porte_applicative_list = new ArrayList<>();
				whereIsInUso.put(ErrorsHandlerCostant.IN_USO_IN_PORTE_APPLICATIVE, porte_applicative_list);
			}
			if (porte_delegate_list == null) {
				porte_delegate_list = new ArrayList<>();
				whereIsInUso.put(ErrorsHandlerCostant.IN_USO_IN_PORTE_DELEGATE, porte_delegate_list);
			}
			if (mappingErogazionePA_list == null) {
				mappingErogazionePA_list = new ArrayList<>();
				whereIsInUso.put(ErrorsHandlerCostant.IN_USO_IN_MAPPING_EROGAZIONE_PA, mappingErogazionePA_list);
			}
			if (mappingFruizionePD_list == null) {
				mappingFruizionePD_list = new ArrayList<>();
				whereIsInUso.put(ErrorsHandlerCostant.IN_USO_IN_MAPPING_FRUIZIONE_PD, mappingFruizionePD_list);
			}
			

			// Controllo che il scope non sia in uso nelle porte applicative
			if(config){
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_SCOPE);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE);
				sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
				sqlQueryObject.addSelectField(CostantiDB.PORTE_APPLICATIVE+".nome_porta");
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.setSelectDistinct(true);
				sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE_SCOPE+".scope = ?");
				sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE_SCOPE+".id_porta = "+CostantiDB.PORTE_APPLICATIVE+".id");
				sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE+".id_soggetto = "+CostantiDB.SOGGETTI+".id");
				queryString = sqlQueryObject.createSQLQuery();
				stmt = con.prepareStatement(queryString);
				stmt.setString(1, idScope.getNome());
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
			
			// Controllo che il scope non sia in uso nelle porte applicative
			if(config){
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE_SCOPE);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE);
				sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
				sqlQueryObject.addSelectField(CostantiDB.PORTE_DELEGATE+".nome_porta");
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.setSelectDistinct(true);
				sqlQueryObject.addWhereCondition(CostantiDB.PORTE_DELEGATE_SCOPE+".scope = ?");
				sqlQueryObject.addWhereCondition(CostantiDB.PORTE_DELEGATE_SCOPE+".id_porta = "+CostantiDB.PORTE_DELEGATE+".id");
				sqlQueryObject.addWhereCondition(CostantiDB.PORTE_DELEGATE+".id_soggetto = "+CostantiDB.SOGGETTI+".id");
				queryString = sqlQueryObject.createSQLQuery();
				stmt = con.prepareStatement(queryString);
				stmt.setString(1, idScope.getNome());
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
			
			return isInUso;

		} catch (Exception se) {
			throw new UtilsException("[DBOggettiInUsoUtils::" + nomeMetodo + "] Exception: " + se.getMessage(),se);
		} finally {
			// Chiudo statement and resultset
			JDBCUtilities.closeResources(risultato, stmt);
		}
	}


	protected static String toString(IDScope idScope, Map<ErrorsHandlerCostant, List<String>> whereIsInUso, boolean prefix, String separator){
		return toString(idScope, whereIsInUso, prefix, separator," non eliminabile perch&egrave; :");
	}
	protected static String toString(IDScope idScope, Map<ErrorsHandlerCostant, List<String>> whereIsInUso, boolean prefix, String separator, String intestazione){
		Set<ErrorsHandlerCostant> keys = whereIsInUso.keySet();
		String msg = "Scope '"+ idScope.getNome() + "'" + intestazione+separator;
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

			case IN_USO_IN_PORTE_APPLICATIVE:
				if ( messages!=null && messages.size() > 0) {
					msg += "utilizzato nelle Porte Inbound (Controllo Accessi - Autorizzazione Token): " + DBOggettiInUsoUtils.formatList(messages,separator) + separator;
				}
				break;
			case IN_USO_IN_PORTE_DELEGATE:
				if ( messages!=null && messages.size() > 0) {
					msg += "utilizzato nelle Porte Outbound (Controllo Accessi - Autorizzazione Token): " + DBOggettiInUsoUtils.formatList(messages,separator) + separator;
				}
				break;
			case IN_USO_IN_MAPPING_EROGAZIONE_PA:
				if ( messages!=null && messages.size() > 0) {
					msg += "utilizzato nelle Erogazioni (Controllo Accessi - Autorizzazione Token): " + DBOggettiInUsoUtils.formatList(messages,separator) + separator;
				}
				break;
			case IN_USO_IN_MAPPING_FRUIZIONE_PD:
				if ( messages!=null && messages.size() > 0) {
					msg += "utilizzato nelle Fruizioni (Controllo Accessi - Autorizzazione Token): " + DBOggettiInUsoUtils.formatList(messages,separator) + separator;
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
