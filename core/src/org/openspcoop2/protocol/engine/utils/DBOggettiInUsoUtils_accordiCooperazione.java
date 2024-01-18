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
import org.openspcoop2.core.id.IDAccordoCooperazione;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.driver.IDAccordoCooperazioneFactory;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.jdbc.JDBCUtilities;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.SQLObjectFactory;

/**
 * DBOggettiInUsoUtils_accordiCooperazione
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class DBOggettiInUsoUtils_accordiCooperazione {

	protected static boolean isAccordoCooperazioneInUso(Connection con, String tipoDB, IDAccordoCooperazione idAccordo, 
			Map<ErrorsHandlerCostant,List<String>> whereIsInUso, boolean normalizeObjectIds) throws UtilsException {
		String nomeMetodo = "isAccordoCooperazioneInUso";

		PreparedStatement stmt = null;
		ResultSet risultato = null;
		PreparedStatement stmt2 = null;
		ResultSet risultato2 = null;
		String queryString;
		try {
			boolean isInUso = false;


			long idAccordoServizioParteComune = DBUtils.getIdAccordoCooperazione(idAccordo, con, tipoDB);
			if(idAccordoServizioParteComune<=0){
				throw new UtilsException("Accordi di Cooperazione con id ["+idAccordo.toString()+"] non trovato");
			}

			List<String> accordi_list = whereIsInUso.get(ErrorsHandlerCostant.IN_USO_IN_ACCORDI);

			if (accordi_list == null) {
				accordi_list = new ArrayList<>();
				whereIsInUso.put(ErrorsHandlerCostant.IN_USO_IN_ACCORDI, accordi_list);
			}

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.ACCORDI_SERVIZI_COMPOSTO);
			sqlQueryObject.addFromTable(CostantiDB.ACCORDI);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition(CostantiDB.ACCORDI_SERVIZI_COMPOSTO+".id_accordo_cooperazione = ?");
			sqlQueryObject.addWhereCondition(CostantiDB.ACCORDI_SERVIZI_COMPOSTO+".id_accordo = "+CostantiDB.ACCORDI+".id");
			sqlQueryObject.setANDLogicOperator(true);
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idAccordoServizioParteComune);
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
					IDAccordoCooperazione idAccordoCooperazione = IDAccordoCooperazioneFactory.getInstance().getIDAccordoFromValues(nomeAccordo, idReferenteObject, versione); 
					accordi_list.add(DBOggettiInUsoUtils.getProtocolPrefix(protocollo)+NamingUtils.getLabelAccordoCooperazione(protocollo, idAccordoCooperazione));
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


			return isInUso;

		} catch (Exception se) {

			throw new UtilsException("[DBOggettiInUsoUtils::" + nomeMetodo + "] Exception: " + se.getMessage(),se);
		} finally {
			//Chiudo statement and resultset
			JDBCUtilities.closeResources(risultato, stmt);
			JDBCUtilities.closeResources(risultato2, stmt2);
		}
	}

	protected static String toString(IDAccordoCooperazione idAccordo, Map<ErrorsHandlerCostant, List<String>> whereIsInUso, boolean prefix, String separator, boolean normalizeObjectIds){

		StringBuilder bf = new StringBuilder();
		if(normalizeObjectIds && idAccordo.getSoggettoReferente()!=null) {
			try {
				String protocollo = ProtocolFactoryManager.getInstance().getProtocolByOrganizationType(idAccordo.getSoggettoReferente().getTipo());
				String labelAccordo = DBOggettiInUsoUtils.getProtocolPrefix(protocollo)+NamingUtils.getLabelAccordoCooperazione(protocollo, idAccordo);
				bf.append(labelAccordo);
			}catch(Exception e) {
				bf.append(idAccordo.toString());
			}
		}
		else {
			bf.append(idAccordo.toString());
		}

		Set<ErrorsHandlerCostant> keys = whereIsInUso.keySet();
		String msg = "Accordo Cooperazione '"+bf.toString() + "' non eliminabile perch&egrave; :"+separator;
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
				if ( messages!=null && messages.size() > 0) {
					msg += "riferito da API (Servizi Composti): " + DBOggettiInUsoUtils.formatList(messages,separator) + separator;
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
