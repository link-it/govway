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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openspcoop2.core.commons.DBUtils;
import org.openspcoop2.core.commons.ErrorsHandlerCostant;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.jdbc.JDBCUtilities;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.SQLObjectFactory;

/**
 * DBOggettiInUsoUtils_accordiParteSpecifica
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class DBOggettiInUsoUtils_accordiParteSpecifica {

	private static boolean isAccordoServizioParteSpecificaInUso(Connection con, String tipoDB, long idAccordoServizioParteSpecifica, 
			Map<ErrorsHandlerCostant,List<String>> whereIsInUso, String nomeMetodo,
			List<IDPortaDelegata> nomePDGenerateAutomaticamente, List<IDPortaApplicativa> nomePAGenerateAutomaticamente, boolean normalizeObjectIds) throws UtilsException {
		
		PreparedStatement stmt = null;
		ResultSet risultato = null;
		PreparedStatement stmt2 = null;
		ResultSet risultato2 = null;
		String queryString;
		try {
			boolean isInUso = false;


			List<String> porteApplicative_list = whereIsInUso.get(ErrorsHandlerCostant.IN_USO_IN_PORTE_APPLICATIVE);
			List<String> porteDelegate_list = whereIsInUso.get(ErrorsHandlerCostant.IN_USO_IN_PORTE_DELEGATE);
			List<String> fruitori_list = whereIsInUso.get(ErrorsHandlerCostant.POSSIEDE_FRUITORI);
			List<String> servizioComponente_list = whereIsInUso.get(ErrorsHandlerCostant.IS_SERVIZIO_COMPONENTE_IN_ACCORDI);
			List<String> mappingErogazionePA_list = whereIsInUso.get(ErrorsHandlerCostant.IN_USO_IN_MAPPING_EROGAZIONE_PA);
			List<String> mappingFruizionePD_list = whereIsInUso.get(ErrorsHandlerCostant.IN_USO_IN_MAPPING_FRUIZIONE_PD);
			List<String> utenti_list = whereIsInUso.get(ErrorsHandlerCostant.UTENTE);
			List<String> ct_list = whereIsInUso.get(ErrorsHandlerCostant.CONTROLLO_TRAFFICO);
			List<String> allarme_list = whereIsInUso.get(ErrorsHandlerCostant.ALLARMI);

			if (porteApplicative_list == null) {
				porteApplicative_list = new ArrayList<>();
				whereIsInUso.put(ErrorsHandlerCostant.IN_USO_IN_PORTE_APPLICATIVE, porteApplicative_list);
			}
			if (porteDelegate_list == null) {
				porteDelegate_list = new ArrayList<>();
				whereIsInUso.put(ErrorsHandlerCostant.IN_USO_IN_PORTE_DELEGATE, porteDelegate_list);
			}
			if (fruitori_list == null) {
				fruitori_list = new ArrayList<>();
				whereIsInUso.put(ErrorsHandlerCostant.POSSIEDE_FRUITORI, fruitori_list);
			}
			if (servizioComponente_list == null) {
				servizioComponente_list = new ArrayList<>();
				whereIsInUso.put(ErrorsHandlerCostant.IS_SERVIZIO_COMPONENTE_IN_ACCORDI, servizioComponente_list);
			}
			if (mappingErogazionePA_list == null) {
				mappingErogazionePA_list = new ArrayList<>();
				whereIsInUso.put(ErrorsHandlerCostant.IN_USO_IN_MAPPING_EROGAZIONE_PA, mappingErogazionePA_list);
			}
			if (mappingFruizionePD_list == null) {
				mappingFruizionePD_list = new ArrayList<>();
				whereIsInUso.put(ErrorsHandlerCostant.IN_USO_IN_MAPPING_FRUIZIONE_PD, mappingFruizionePD_list);
			}
			if (utenti_list == null) {
				utenti_list = new ArrayList<>();
				whereIsInUso.put(ErrorsHandlerCostant.UTENTE, utenti_list);
			}
			if (ct_list == null) {
				ct_list = new ArrayList<>();
				whereIsInUso.put(ErrorsHandlerCostant.CONTROLLO_TRAFFICO, ct_list);
			}
			if (allarme_list == null) {
				allarme_list = new ArrayList<>();
				whereIsInUso.put(ErrorsHandlerCostant.ALLARMI, allarme_list);
			}

			
			
			// Raccolgo Dati Servizio.
			String tipoServizio = null;
			String nomeServizio = null;
			int versioneServizio;
			String tipoSoggetto = null;
			String nomeSoggetto = null;
			long idSoggetto;
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.SERVIZI);
			sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI+".id_soggetto="+CostantiDB.SOGGETTI+".id");
			sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI+".id=?");
			sqlQueryObject.setANDLogicOperator(true);
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			int index = 1;
			stmt.setLong(index++, idAccordoServizioParteSpecifica);
			risultato = stmt.executeQuery();
			if (risultato.next()) {
				tipoServizio = risultato.getString("tipo_servizio");
				nomeServizio = risultato.getString("nome_servizio");
				versioneServizio = risultato.getInt("versione_servizio");
				tipoSoggetto = risultato.getString("tipo_soggetto");
				nomeSoggetto = risultato.getString("nome_soggetto");
				versioneServizio = risultato.getInt("versione_servizio");
			}
			else{
				throw new UtilsException("Accordo con id ["+idAccordoServizioParteSpecifica+"] non trovato");
			}
			risultato.close();
			stmt.close();
			
			idSoggetto = DBUtils.getIdSoggetto(nomeSoggetto, tipoSoggetto, con, tipoDB);
			
			
			
			
			
			// porte applicative
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition(false, "id_servizio = ?", "tipo_servizio = ? AND servizio = ? AND versione_servizio = ?");
			sqlQueryObject.addWhereCondition(false, "id_soggetto = ?", "id_soggetto_virtuale = ?", "tipo_soggetto_virtuale = ? AND nome_soggetto_virtuale = ?");
			sqlQueryObject.setANDLogicOperator(true);
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			index = 1;
			stmt.setLong(index++, idAccordoServizioParteSpecifica);
			stmt.setString(index++, tipoServizio);
			stmt.setString(index++, nomeServizio);
			stmt.setInt(index++, versioneServizio);
			stmt.setLong(index++, idSoggetto);
			stmt.setLong(index++, idSoggetto);
			stmt.setString(index++, tipoSoggetto);
			stmt.setString(index++, nomeSoggetto);
			risultato = stmt.executeQuery();
			while (risultato.next()) {
				
				String nomePorta = risultato.getString("nome_porta");
				IDPortaApplicativa idPA = new IDPortaApplicativa();
				idPA.setNome(nomePorta);
				if(nomePAGenerateAutomaticamente!=null && !nomePAGenerateAutomaticamente.contains(idPA)) {
					ResultPorta resultPorta = DBOggettiInUsoUtils.formatPortaApplicativa(nomePorta, tipoDB, con, normalizeObjectIds);
					if(resultPorta.mapping) {
						mappingErogazionePA_list.add(resultPorta.label); // non dovrebbe mai entrare in questa caso essendo filtrato da nomePAGenerateAutomaticamente
					}
					else {
						porteApplicative_list.add(resultPorta.label);
					}
					isInUso=true;
				}
			}
			risultato.close();
			stmt.close();
			
			
			
			
			// porte delegate
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition(false, "id_servizio = ?", "tipo_servizio = ? AND nome_servizio = ? AND versione_servizio = ?");
			sqlQueryObject.addWhereCondition(false, "id_soggetto_erogatore = ?", "tipo_soggetto_erogatore = ? AND nome_soggetto_erogatore = ?");
			sqlQueryObject.setANDLogicOperator(true);
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			index = 1;
			stmt.setLong(index++, idAccordoServizioParteSpecifica);
			stmt.setString(index++, tipoServizio);
			stmt.setString(index++, nomeServizio);
			stmt.setInt(index++, versioneServizio);
			stmt.setLong(index++, idSoggetto);
			stmt.setString(index++, tipoSoggetto);
			stmt.setString(index++, nomeSoggetto);
			risultato = stmt.executeQuery();
			while (risultato.next()) {			
				String nomePorta = risultato.getString("nome_porta");
				IDPortaDelegata idPD = new IDPortaDelegata();
				idPD.setNome(nomePorta);
				if(nomePDGenerateAutomaticamente!=null && !nomePDGenerateAutomaticamente.contains(idPD)) {
					ResultPorta resultPorta = DBOggettiInUsoUtils.formatPortaDelegata(nomePorta, tipoDB, con, normalizeObjectIds);
					if(resultPorta.mapping) {
						mappingFruizionePD_list.add(resultPorta.label);
					}
					else {
						porteDelegate_list.add(resultPorta.label);
					}
					isInUso=true;
				}
			}
			risultato.close();
			stmt.close();
			
			
			
			
			
			// mapping erogazione pa 
			
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.MAPPING_EROGAZIONE_PA);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE);
			sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
			sqlQueryObject.addSelectField("tipo_soggetto");
			sqlQueryObject.addSelectField("nome_soggetto");
			sqlQueryObject.addSelectField(CostantiDB.PORTE_APPLICATIVE+".nome_porta");
			sqlQueryObject.setANDLogicOperator(true);
			sqlQueryObject.setSelectDistinct(true);
			sqlQueryObject.addWhereCondition(CostantiDB.MAPPING_EROGAZIONE_PA+".id_erogazione = ?");
			sqlQueryObject.addWhereCondition(CostantiDB.MAPPING_EROGAZIONE_PA+".id_porta = "+CostantiDB.PORTE_APPLICATIVE+".id");
			sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE+".id_soggetto = "+CostantiDB.SOGGETTI+".id");
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idAccordoServizioParteSpecifica);
			risultato = stmt.executeQuery();
			while (risultato.next()) {
				//String tipo_soggetto = risultato.getString("tipo_soggetto");
				//String nome_soggetto = risultato.getString("nome_soggetto");
				String nomePorta = risultato.getString("nome_porta");
				IDPortaApplicativa idPA = new IDPortaApplicativa();
				idPA.setNome(nomePorta);
				if(nomePAGenerateAutomaticamente!=null && 
						!nomePAGenerateAutomaticamente.contains(idPA)){
					ResultPorta resultPorta = DBOggettiInUsoUtils.formatPortaApplicativa(nomePorta, tipoDB, con, normalizeObjectIds);
					if(resultPorta.mapping) {
						String l = resultPorta.label;
						if(mappingErogazionePA_list.contains(l)==false) { 
							mappingErogazionePA_list.add(l);
						}
					}
					else {
						// ?? e' gia' un mapping
						String l = resultPorta.label;
						if(porteApplicative_list.contains(l)==false) { 
							porteApplicative_list.add(l);
						}
					}
					isInUso=true;
				}
			}
			risultato.close();
			stmt.close();
			
			
			
			
			
			
			
			
			
			
			// mapping fruizioni pd
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.MAPPING_FRUIZIONE_PD);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE);
			sqlQueryObject.addFromTable(CostantiDB.SERVIZI_FRUITORI);
			sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
			sqlQueryObject.setSelectDistinct(true);
			sqlQueryObject.addSelectField(CostantiDB.SOGGETTI + ".tipo_soggetto");
			sqlQueryObject.addSelectField(CostantiDB.SOGGETTI + ".nome_soggetto");
			sqlQueryObject.addSelectField(CostantiDB.PORTE_DELEGATE+".nome_porta");
			sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI + ".id = " + CostantiDB.SERVIZI_FRUITORI + ".id_soggetto");
			sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_FRUITORI + ".id_servizio = ?");
			sqlQueryObject.addWhereCondition(CostantiDB.MAPPING_FRUIZIONE_PD+".id_fruizione = "+CostantiDB.SERVIZI_FRUITORI + ".id");
			sqlQueryObject.addWhereCondition(CostantiDB.MAPPING_FRUIZIONE_PD+".id_porta = "+CostantiDB.PORTE_DELEGATE + ".id");
			sqlQueryObject.setANDLogicOperator(true);
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idAccordoServizioParteSpecifica);
			risultato = stmt.executeQuery();
			while (risultato.next()) {
				//String tipo_soggetto = risultato.getString("tipo_soggetto");
				//String nome_soggetto = risultato.getString("nome_soggetto");
				String nomePorta = risultato.getString("nome_porta");
				IDPortaDelegata idPD = new IDPortaDelegata();
				idPD.setNome(nomePorta);
				if(nomePDGenerateAutomaticamente!=null && !nomePDGenerateAutomaticamente.contains(idPD)) {
					ResultPorta resultPorta = DBOggettiInUsoUtils.formatPortaDelegata(nomePorta, tipoDB, con, normalizeObjectIds);
					if(resultPorta.mapping) {
						String l = resultPorta.label;
						if(mappingFruizionePD_list.contains(l)==false) { 
							mappingFruizionePD_list.add(l);
						}
					}
					else {
						// ?? e' gia' un mapping
						String l = resultPorta.label;
						if(porteDelegate_list.contains(l)==false) { 
							porteDelegate_list.add(l);
						}
					}
					isInUso=true;
				}
			}
			risultato.close();
			stmt.close();
			
			
			
			
			
			// fruitori
			if(mappingFruizionePD_list.isEmpty()) {
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI_FRUITORI);
				sqlQueryObject.setSelectDistinct(true);
				sqlQueryObject.addSelectField(CostantiDB.SOGGETTI + ".tipo_soggetto");
				sqlQueryObject.addSelectField(CostantiDB.SOGGETTI + ".nome_soggetto");
				sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI + ".id = " + CostantiDB.SERVIZI_FRUITORI + ".id_soggetto");
				sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_FRUITORI + ".id_servizio = ?");
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
				stmt = con.prepareStatement(queryString);
				stmt.setLong(1, idAccordoServizioParteSpecifica);
				risultato = stmt.executeQuery();
				while (risultato.next()) {
										
					String tipoSoggettoFruitore = risultato.getString("tipo_soggetto");
					String nomeSoggettoFruitore = risultato.getString("nome_soggetto");
					
					boolean usedForPD = false;
					if(nomePDGenerateAutomaticamente!=null) {
						for (IDPortaDelegata idPD : nomePDGenerateAutomaticamente) {
							if(idPD.getIdentificativiFruizione()!=null && idPD.getIdentificativiFruizione().getSoggettoFruitore()!=null) {
								IDSoggetto soggettoFruitore = new IDSoggetto(tipoSoggettoFruitore, nomeSoggettoFruitore);
								if(soggettoFruitore.equals(idPD.getIdentificativiFruizione().getSoggettoFruitore())) {
									usedForPD = true;
									break;
								}
							}
						}
					}
					
					if(!usedForPD) {
						if(normalizeObjectIds) {
							fruitori_list.add(NamingUtils.getLabelSoggetto(new IDSoggetto(tipoSoggettoFruitore, nomeSoggettoFruitore)));
						}
						else {
							fruitori_list.add(tipoSoggettoFruitore+"/"+nomeSoggettoFruitore);
						}
						isInUso=true;
					}
				}
				risultato.close();
				stmt.close();
			}
			
			
			
			
			
			// servizio Componente
			//List<String> nomiServiziApplicativi = new ArrayList<>();
			//controllo se in uso in servizi
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.ACCORDI_SERVIZI_COMPONENTI);
			sqlQueryObject.addFromTable(CostantiDB.ACCORDI_SERVIZI_COMPOSTO);
			sqlQueryObject.addFromTable(CostantiDB.ACCORDI);
			sqlQueryObject.setSelectDistinct(true);
			sqlQueryObject.addSelectField(CostantiDB.ACCORDI + ".nome");
			sqlQueryObject.addSelectField(CostantiDB.ACCORDI + ".versione");
			sqlQueryObject.addSelectField(CostantiDB.ACCORDI + ".id_referente");
			sqlQueryObject.addWhereCondition(CostantiDB.ACCORDI_SERVIZI_COMPONENTI+".id_servizio_composto = "+CostantiDB.ACCORDI_SERVIZI_COMPOSTO+".id");
			sqlQueryObject.addWhereCondition(CostantiDB.ACCORDI_SERVIZI_COMPOSTO+".id_accordo = "+CostantiDB.ACCORDI+".id");
			sqlQueryObject.addWhereCondition(CostantiDB.ACCORDI_SERVIZI_COMPONENTI+".id_servizio_componente = ?");
			sqlQueryObject.setANDLogicOperator(true);
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idAccordoServizioParteSpecifica);
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
					servizioComponente_list.add(DBOggettiInUsoUtils.getProtocolPrefix(protocollo)+NamingUtils.getLabelAccordoServizioParteComune(protocollo, idAccordo));
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
	
					servizioComponente_list.add(bf.toString());
				}
				
			}
			risultato.close();
			stmt.close();

			
			
			
			
			
			

						
			
			
			// Controllo che il soggetto non sia associato ad utenti
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.USERS);
			sqlQueryObject.addFromTable(CostantiDB.USERS_SERVIZI);
			sqlQueryObject.addSelectField("login");
			sqlQueryObject.setANDLogicOperator(true);
			sqlQueryObject.addWhereCondition(CostantiDB.USERS_SERVIZI+".id_servizio = ?");
			sqlQueryObject.addWhereCondition(CostantiDB.USERS+".id = "+CostantiDB.USERS_SERVIZI+".id_utente");
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idAccordoServizioParteSpecifica);
			risultato = stmt.executeQuery();
			while (risultato.next()) {
				utenti_list.add(risultato.getString("login"));

				isInUso = true;
			}
			risultato.close();
			stmt.close();
			
			
			
			
			
			
			
			// Controllo che il servizio non sia associato a policy di controllo del traffico o allarmi
							
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.SERVIZI);
			sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
			sqlQueryObject.addSelectField("tipo_soggetto");
			sqlQueryObject.addSelectField("nome_soggetto");
			sqlQueryObject.addSelectField("tipo_servizio");
			sqlQueryObject.addSelectField("nome_servizio");
			sqlQueryObject.addSelectField("versione_servizio");
			sqlQueryObject.setANDLogicOperator(true);
			sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI+".id=?");
			sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI+".id_soggetto="+CostantiDB.SOGGETTI+".id");
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idAccordoServizioParteSpecifica);
			risultato = stmt.executeQuery();
			IDServizio idServizio = null;
			if(risultato.next()) {
				idServizio = readIdServizio(risultato);
			}
			risultato.close();
			stmt.close();
			
			if(idServizio!=null) {
				
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
				
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
					sqlQueryObject.addFromTable(tabella);
					sqlQueryObject.addSelectField(identificativo_column);
					sqlQueryObject.addSelectField(alias_column);
					sqlQueryObject.addSelectField("filtro_ruolo");
					sqlQueryObject.addSelectField("filtro_porta");
					sqlQueryObject.setANDLogicOperator(true); // OR
					sqlQueryObject.addWhereCondition(tabella+".filtro_tipo_erogatore = ?");
					sqlQueryObject.addWhereCondition(tabella+".filtro_nome_erogatore = ?");
					sqlQueryObject.addWhereCondition(tabella+".filtro_tipo_servizio = ?");
					sqlQueryObject.addWhereCondition(tabella+".filtro_nome_servizio = ?");
					sqlQueryObject.addWhereCondition(tabella+".filtro_versione_servizio = ?");
					sqlQueryObject.addOrderBy("filtro_ruolo");
					sqlQueryObject.addOrderBy("filtro_porta");
					queryString = sqlQueryObject.createSQLQuery();
					stmt = con.prepareStatement(queryString);
					index = 1;
					stmt.setString(index++, idServizio.getSoggettoErogatore().getTipo());
					stmt.setString(index++, idServizio.getSoggettoErogatore().getNome());
					stmt.setString(index++, idServizio.getTipo());
					stmt.setString(index++, idServizio.getNome());
					stmt.setInt(index++, idServizio.getVersione());
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
			//Chiudo statement and resultset
			JDBCUtilities.closeResources(risultato2, stmt2);
			JDBCUtilities.closeResources(risultato, stmt);
		}
	}
	@SuppressWarnings("deprecation")
	private static IDServizio readIdServizio(ResultSet risultato) throws SQLException {
		IDServizio idServizio = new IDServizio();
		idServizio.setTipo(risultato.getString("tipo_servizio"));
		idServizio.setNome(risultato.getString("nome_servizio"));
		idServizio.setVersione(risultato.getInt("versione_servizio"));
		idServizio.setSoggettoErogatore(new IDSoggetto(risultato.getString("tipo_soggetto"), risultato.getString("nome_soggetto")));
		return idServizio;
	}
	
	protected static boolean isAccordoServizioParteSpecificaInUso(Connection con, String tipoDB, IDServizio idServizio, 
			Map<ErrorsHandlerCostant,List<String>> whereIsInUso,
			List<IDPortaDelegata> nomePDGenerateAutomaticamente, List<IDPortaApplicativa> nomePAGenerateAutomaticamente, boolean normalizeObjectIds) throws UtilsException {
		String nomeMetodo = "isAccordoServizioParteSpecificaInUso(IDServizio)";
		long idAccordoServizioParteSpecifica = -1;
		try {
			idAccordoServizioParteSpecifica = DBUtils.getIdAccordoServizioParteSpecifica(idServizio, con, tipoDB);
			if(idAccordoServizioParteSpecifica<=0){
				throw new UtilsException("Accordi di Servizio Parte Specifica con id ["+idServizio.toString()+"] non trovato");
			}
		}catch (Exception se) {
			throw new UtilsException("[DBOggettiInUsoUtils::" + nomeMetodo + "] Exception: " + se.getMessage(),se);
		}
		return isAccordoServizioParteSpecificaInUso(con, tipoDB, idAccordoServizioParteSpecifica, whereIsInUso,nomeMetodo,
				nomePDGenerateAutomaticamente, nomePAGenerateAutomaticamente, normalizeObjectIds);
	}
	protected static String toString(IDServizio idServizio, Map<ErrorsHandlerCostant, List<String>> whereIsInUso, boolean prefix, String separator, 
			boolean normalizeObjectIds, String oggetto){
		
		StringBuilder bf = new StringBuilder();
		if(normalizeObjectIds) {
			try {
				String protocollo = ProtocolFactoryManager.getInstance().getProtocolByOrganizationType(idServizio.getSoggettoErogatore().getTipo());
				String labelAccordo = DBOggettiInUsoUtils.getProtocolPrefix(protocollo)+NamingUtils.getLabelAccordoServizioParteSpecifica(protocollo, idServizio);
				bf.append(labelAccordo);
			}catch(Exception e) {
				bf.append(idServizio.toString());
			}
		}
		else {
			bf.append(idServizio.toString());
		}
		
		
		Set<ErrorsHandlerCostant> keys = whereIsInUso.keySet();
		String msg = oggetto+ " '"+bf.toString() + "' non eliminabile perch&egrave; :"+separator;
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
			case POSSIEDE_FRUITORI:
				if ( messages!=null && messages.size() > 0 ) {
					msg += "fruito dai soggetti: " + DBOggettiInUsoUtils.formatList(messages,separator) + separator;
				}
				break;
			case IS_SERVIZIO_COMPONENTE_IN_ACCORDI:
				if ( messages!=null && messages.size() > 0 ) {
					msg += "associato come servizio componente in API di Servizi Composti: " + DBOggettiInUsoUtils.formatList(messages,separator) + separator;
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
			case UTENTE:
				if ( messages!=null && messages.size() > 0 ) {
					msg += "associato ad Utenti: " + DBOggettiInUsoUtils.formatList(messages,separator) + separator;
				}
				break;
			case CONTROLLO_TRAFFICO:
				if ( messages!=null && messages.size() > 0 ) {
					msg += "utilizzato in policy di Rate Limiting: " + DBOggettiInUsoUtils.formatList(messages,separator) + separator;
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
