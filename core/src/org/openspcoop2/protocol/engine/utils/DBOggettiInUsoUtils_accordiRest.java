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
import org.openspcoop2.core.constants.ProprietariProtocolProperty;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDResource;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.mapping.DBMappingUtils;
import org.openspcoop2.core.mapping.MappingErogazionePortaApplicativa;
import org.openspcoop2.core.mapping.MappingFruizionePortaDelegata;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.protocol.engine.constants.Costanti;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.jdbc.JDBCUtilities;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.LikeConfig;
import org.openspcoop2.utils.sql.SQLObjectFactory;

/**
 * DBOggettiInUsoUtils_accordiRest
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class DBOggettiInUsoUtils_accordiRest {

	protected static boolean isRisorsaConfigInUso(Connection con, String tipoDB, IDResource idRisorsa, Map<ErrorsHandlerCostant, 
			List<String>> whereIsInUso, boolean normalizeObjectIds) throws UtilsException {
		return _isRisorsaInUso(con,tipoDB,idRisorsa,false,true,whereIsInUso,normalizeObjectIds);
	}
	protected static boolean isRisorsaRegistryInUso(Connection con, String tipoDB, IDResource idRisorsa, Map<ErrorsHandlerCostant, 
			List<String>> whereIsInUso, boolean normalizeObjectIds) throws UtilsException {
		return _isRisorsaInUso(con,tipoDB,idRisorsa,true,false,whereIsInUso,normalizeObjectIds);
	}
	protected static boolean isRisorsaInUso(Connection con, String tipoDB, IDResource idRisorsa, Map<ErrorsHandlerCostant, 
			List<String>> whereIsInUso, boolean normalizeObjectIds) throws UtilsException {
		return _isRisorsaInUso(con,tipoDB,idRisorsa,true,true,whereIsInUso,normalizeObjectIds);
	}
	private static boolean _isRisorsaInUso(Connection con, String tipoDB, IDResource idRisorsa,
			boolean registry, boolean config, Map<ErrorsHandlerCostant, 
			List<String>> whereIsInUso, boolean normalizeObjectIds) throws UtilsException {
		String nomeMetodo = "_isRisorsaInUso";

		PreparedStatement stmt = null;
		ResultSet risultato = null;
		String queryString;

		try {
		
			boolean isInUso = false;
			
			List<String> correlazione_list = whereIsInUso.get(ErrorsHandlerCostant.IS_CORRELATA);
			
			List<String> porte_applicative_list = whereIsInUso.get(ErrorsHandlerCostant.IN_USO_IN_PORTE_APPLICATIVE);
			List<String> porte_delegate_list = whereIsInUso.get(ErrorsHandlerCostant.IN_USO_IN_PORTE_DELEGATE);
			List<String> mappingErogazionePA_list = whereIsInUso.get(ErrorsHandlerCostant.IN_USO_IN_MAPPING_EROGAZIONE_PA);
			List<String> mappingFruizionePD_list = whereIsInUso.get(ErrorsHandlerCostant.IN_USO_IN_MAPPING_FRUIZIONE_PD);
			
			List<String> trasformazionePD_mapping_list = whereIsInUso.get(ErrorsHandlerCostant.TRASFORMAZIONE_MAPPING_PD);
			List<String> trasformazionePD_list = whereIsInUso.get(ErrorsHandlerCostant.TRASFORMAZIONE_PD);
			List<String> trasformazionePA_mapping_list = whereIsInUso.get(ErrorsHandlerCostant.TRASFORMAZIONE_MAPPING_PA);
			List<String> trasformazionePA_list = whereIsInUso.get(ErrorsHandlerCostant.TRASFORMAZIONE_PA);
			
			List<String> ct_list = whereIsInUso.get(ErrorsHandlerCostant.CONTROLLO_TRAFFICO);
			List<String> allarme_list = whereIsInUso.get(ErrorsHandlerCostant.ALLARMI);
			
			if (correlazione_list == null) {
				correlazione_list = new ArrayList<String>();
				whereIsInUso.put(ErrorsHandlerCostant.IS_CORRELATA, correlazione_list);
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
			
			if (trasformazionePD_mapping_list == null) {
				trasformazionePD_mapping_list = new ArrayList<String>();
				whereIsInUso.put(ErrorsHandlerCostant.TRASFORMAZIONE_MAPPING_PD, trasformazionePD_mapping_list);
			}
			if (trasformazionePD_list == null) {
				trasformazionePD_list = new ArrayList<String>();
				whereIsInUso.put(ErrorsHandlerCostant.TRASFORMAZIONE_PD, trasformazionePD_list);
			}
			if (trasformazionePA_mapping_list == null) {
				trasformazionePA_mapping_list = new ArrayList<String>();
				whereIsInUso.put(ErrorsHandlerCostant.TRASFORMAZIONE_MAPPING_PA, trasformazionePA_mapping_list);
			}
			if (trasformazionePA_list == null) {
				trasformazionePA_list = new ArrayList<String>();
				whereIsInUso.put(ErrorsHandlerCostant.TRASFORMAZIONE_PA, trasformazionePA_list);
			}
			
			if (ct_list == null) {
				ct_list = new ArrayList<String>();
				whereIsInUso.put(ErrorsHandlerCostant.CONTROLLO_TRAFFICO, ct_list);
			}
			if (allarme_list == null) {
				allarme_list = new ArrayList<String>();
				whereIsInUso.put(ErrorsHandlerCostant.ALLARMI, allarme_list);
			}
			
			
			
			// recupero id
			
			long idAccordo = DBUtils.getIdAccordoServizioParteComune(idRisorsa.getIdAccordo(), con, tipoDB);
			if(idAccordo<=0) {
				throw new UtilsException("Accordo non trovato");
			}
			
			long idR = DBUtils.getIdResource(idAccordo, idRisorsa.getNome(), con);
			if(idR<=0) {
				throw new UtilsException("Risorsa non trovata");
			}
			
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.SERVIZI);
			sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
			sqlQueryObject.addSelectField("nome_servizio");
			sqlQueryObject.addSelectField("tipo_servizio");
			sqlQueryObject.addSelectField("versione_servizio");
			sqlQueryObject.addSelectField("nome_soggetto");
			sqlQueryObject.addSelectField("tipo_soggetto");
			sqlQueryObject.setANDLogicOperator(true);
			sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI+".id_accordo=?");
			sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI+".id_soggetto="+CostantiDB.SOGGETTI+".id");
			sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI+".port_type is null"); // condizione per rest
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idAccordo);
			risultato = stmt.executeQuery();
			List<IDServizio> idServiziWithAccordo = new ArrayList<IDServizio>();
			while (risultato.next()) {
				IDSoggetto soggettoErogatore = new IDSoggetto(risultato.getString("tipo_soggetto"), risultato.getString("nome_soggetto"));
				IDServizio idServizio = 
						IDServizioFactory.getInstance().getIDServizioFromValues(risultato.getString("tipo_servizio"), 
								risultato.getString("nome_servizio"), soggettoErogatore, risultato.getInt("versione_servizio"));
				idServiziWithAccordo.add(idServizio);
			}
			risultato.close();
			stmt.close();
			
			
			
			
			
			
			// Controllo che l'azione non sia stata correlata da un'altra azione tramite ModI
			if(Costanti.MODIPA_PROTOCOL_NAME.equals(idRisorsa.getIdAccordo().getSoggettoReferente().getTipo())) {
			
				ISQLQueryObject sqlQueryObjectApiExists = SQLObjectFactory.createSQLQueryObject(tipoDB);
				sqlQueryObjectApiExists.setANDLogicOperator(true);
				sqlQueryObjectApiExists.addFromTable(CostantiDB.PROTOCOL_PROPERTIES);
				sqlQueryObjectApiExists.addSelectField(CostantiDB.PROTOCOL_PROPERTIES, "name");
				sqlQueryObjectApiExists.addWhereCondition(CostantiDB.API_RESOURCES + ".id = " + CostantiDB.PROTOCOL_PROPERTIES + ".id_proprietario");
				sqlQueryObjectApiExists.addWhereCondition(CostantiDB.PROTOCOL_PROPERTIES + ".tipo_proprietario=?");
				sqlQueryObjectApiExists.addWhereCondition(CostantiDB.PROTOCOL_PROPERTIES + ".name=?");
				sqlQueryObjectApiExists.addWhereCondition(CostantiDB.PROTOCOL_PROPERTIES + ".value_string=?");

				ISQLQueryObject sqlQueryObjectOperationExists = SQLObjectFactory.createSQLQueryObject(tipoDB);
				sqlQueryObjectOperationExists.setANDLogicOperator(true);
				sqlQueryObjectOperationExists.addFromTable(CostantiDB.PROTOCOL_PROPERTIES);
				sqlQueryObjectOperationExists.addSelectField(CostantiDB.PROTOCOL_PROPERTIES, "name");
				sqlQueryObjectOperationExists.addWhereCondition(CostantiDB.API_RESOURCES + ".id = " + CostantiDB.PROTOCOL_PROPERTIES + ".id_proprietario");
				sqlQueryObjectOperationExists.addWhereCondition(CostantiDB.PROTOCOL_PROPERTIES + ".tipo_proprietario=?");
				sqlQueryObjectOperationExists.addWhereCondition(CostantiDB.PROTOCOL_PROPERTIES + ".name=?");
				sqlQueryObjectOperationExists.addWhereCondition(CostantiDB.PROTOCOL_PROPERTIES + ".value_string=?");
				
				
				// Verifico correlazione PULL (all'interno del solito accordo)
				
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.API_RESOURCES);
				sqlQueryObject.addWhereCondition(CostantiDB.API_RESOURCES + ".id_accordo = ?");
				sqlQueryObject.addSelectAliasField(CostantiDB.API_RESOURCES,"nome","nomeRisorsaCorrelata");
				sqlQueryObject.addSelectAliasField(CostantiDB.API_RESOURCES,"http_method","httpMethodRisorsaCorrelata");
				sqlQueryObject.addSelectAliasField(CostantiDB.API_RESOURCES,"path","pathRisorsaCorrelata");
				sqlQueryObject.addWhereExistsCondition(false, sqlQueryObjectApiExists);
				sqlQueryObject.addWhereExistsCondition(false, sqlQueryObjectOperationExists);
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addOrderBy("pathRisorsaCorrelata");
				sqlQueryObject.addOrderBy("httpMethodRisorsaCorrelata");
				sqlQueryObject.setSortType(true);
				queryString = sqlQueryObject.createSQLQuery();
				stmt = con.prepareStatement(queryString);
				int index = 1;
				stmt.setLong(index++, idAccordo);
				// sqlQueryObjectApiExists
				stmt.setString(index++, ProprietariProtocolProperty.RESOURCE.name());
				stmt.setString(index++, Costanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_API_RICHIESTA_CORRELATA);
				stmt.setString(index++, Costanti.MODIPA_VALUE_UNDEFINED);
				// sqlQueryObjectOperationExists
				stmt.setString(index++, ProprietariProtocolProperty.RESOURCE.name());
				stmt.setString(index++, Costanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_AZIONE_RICHIESTA_CORRELATA);
				stmt.setString(index++, idRisorsa.getNome());
				risultato = stmt.executeQuery();
				while (risultato.next()){
					@SuppressWarnings("unused")
					String nomeRisorsaCorrelata = risultato.getString("nomeRisorsaCorrelata");
					String httpMethodRisorsaCorrelata = risultato.getString("httpMethodRisorsaCorrelata");
					String pathRisorsaCorrelata = risultato.getString("pathRisorsaCorrelata");
					
					String path = null;
					if(pathRisorsaCorrelata==null || "".equals(pathRisorsaCorrelata)) {
						path = "*";
					}
					else {
						path = pathRisorsaCorrelata;
					}
					
					String method = null;
					if(httpMethodRisorsaCorrelata==null || "".equals(httpMethodRisorsaCorrelata)) {
						method = "Qualsiasi";
					}
					else {
						method = httpMethodRisorsaCorrelata;
					}
					
					correlazione_list.add("Risorsa "+method+" "+path+" (interazione: NonBloccante-Pull)");
					isInUso = true;
				}
				risultato.close();
				stmt.close();
				
				// Verifico correlazione PUSH
				
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.ACCORDI);
				sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
				sqlQueryObject.addFromTable(CostantiDB.API_RESOURCES);
				sqlQueryObject.addSelectAliasField(CostantiDB.API_RESOURCES,"nome","nomeRisorsaCorrelata");
				sqlQueryObject.addSelectAliasField(CostantiDB.API_RESOURCES,"http_method","httpMethodRisorsaCorrelata");
				sqlQueryObject.addSelectAliasField(CostantiDB.API_RESOURCES,"path","pathRisorsaCorrelata");
				sqlQueryObject.addSelectAliasField(CostantiDB.ACCORDI,"nome","nomeApi");
				sqlQueryObject.addSelectAliasField(CostantiDB.ACCORDI,"versione","versioneApi");
				sqlQueryObject.addSelectAliasField(CostantiDB.SOGGETTI,"tipo_soggetto","tipoReferenteApi");
				sqlQueryObject.addSelectAliasField(CostantiDB.SOGGETTI,"nome_soggetto","nomeReferenteApi");
				sqlQueryObject.addWhereCondition(CostantiDB.API_RESOURCES + ".id_accordo = " + CostantiDB.ACCORDI + ".id");
				sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI + ".id = " + CostantiDB.ACCORDI + ".id_referente");
				sqlQueryObject.addWhereExistsCondition(false, sqlQueryObjectApiExists);
				sqlQueryObject.addWhereExistsCondition(false, sqlQueryObjectOperationExists);
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addOrderBy("nomeApi");
				sqlQueryObject.addOrderBy("versioneApi");
				sqlQueryObject.addOrderBy("nomeReferenteApi");
				sqlQueryObject.addOrderBy("tipoReferenteApi");
				sqlQueryObject.addOrderBy("pathRisorsaCorrelata");
				sqlQueryObject.addOrderBy("httpMethodRisorsaCorrelata");
				sqlQueryObject.setSortType(true);
				queryString = sqlQueryObject.createSQLQuery();
				stmt = con.prepareStatement(queryString);
				index = 1;
				// sqlQueryObjectApiExists
				stmt.setString(index++, ProprietariProtocolProperty.RESOURCE.name());
				stmt.setString(index++, Costanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_API_RICHIESTA_CORRELATA);
				stmt.setString(index++, IDAccordoFactory.getInstance().getUriFromIDAccordo(idRisorsa.getIdAccordo()));
				// sqlQueryObjectOperationExists
				stmt.setString(index++, ProprietariProtocolProperty.RESOURCE.name());
				stmt.setString(index++, Costanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_AZIONE_RICHIESTA_CORRELATA);
				stmt.setString(index++, idRisorsa.getNome());
				risultato = stmt.executeQuery();
				IDAccordoFactory idAccordoFactory = IDAccordoFactory.getInstance();
				while (risultato.next()){
					
					String nomeApi = risultato.getString("nomeApi");
					int versioneApi = risultato.getInt("versioneApi");
					String tipoReferenteApi = risultato.getString("tipoReferenteApi");
					String nomeReferenteApi = risultato.getString("nomeReferenteApi");
					IDAccordo idAPI = idAccordoFactory.getIDAccordoFromValues(nomeApi, tipoReferenteApi, nomeReferenteApi, versioneApi);
					
					@SuppressWarnings("unused")
					String nomeRisorsaCorrelata = risultato.getString("nomeRisorsaCorrelata");
					String httpMethodRisorsaCorrelata = risultato.getString("httpMethodRisorsaCorrelata");
					String pathRisorsaCorrelata = risultato.getString("pathRisorsaCorrelata");
					
					String path = null;
					if(pathRisorsaCorrelata==null || "".equals(pathRisorsaCorrelata)) {
						path = "*";
					}
					else {
						path = pathRisorsaCorrelata;
					}
					
					String method = null;
					if(httpMethodRisorsaCorrelata==null || "".equals(httpMethodRisorsaCorrelata)) {
						method = "Qualsiasi";
					}
					else {
						method = httpMethodRisorsaCorrelata;
					}
					
					correlazione_list.add("Risorsa "+method+" "+path+" dell'API '"+idAccordoFactory.getUriFromIDAccordo(idAPI)+"' (interazione: NonBloccante-Push)");
					isInUso = true;
				}
				risultato.close();
				stmt.close();
				
			}
			
			
			
			

			
			// Porte delegate, mapping
			if(config){
				if(idServiziWithAccordo!=null && !idServiziWithAccordo.isEmpty()) {
					for (IDServizio idServizio : idServiziWithAccordo) {
						
						long idS = DBUtils.getIdServizio(idServizio.getNome(), idServizio.getTipo(), idServizio.getVersione(),
								idServizio.getSoggettoErogatore().getNome(), idServizio.getSoggettoErogatore().getTipo(),
								con, tipoDB);
						if(idS<=0) {
							throw new Exception("Servizio '"+idServizio+"' non esistente");
						}
						
						sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
						sqlQueryObject.addFromTable(CostantiDB.SERVIZI_FRUITORI);
						sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
						sqlQueryObject.addSelectAliasField(CostantiDB.SERVIZI_FRUITORI,"id","idFruitore");
						sqlQueryObject.addSelectField(CostantiDB.SOGGETTI + ".tipo_soggetto");
						sqlQueryObject.addSelectField(CostantiDB.SOGGETTI + ".nome_soggetto");
						sqlQueryObject.addSelectField(CostantiDB.SERVIZI_FRUITORI + ".id_servizio");
						sqlQueryObject.addSelectField(CostantiDB.SERVIZI_FRUITORI + ".id_soggetto");
						sqlQueryObject.addSelectField(CostantiDB.SOGGETTI + ".id");
						sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_FRUITORI + ".id_servizio = ?");
						sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_FRUITORI + ".id_soggetto = " + CostantiDB.SOGGETTI + ".id");
						sqlQueryObject.setANDLogicOperator(true);
						sqlQueryObject.addOrderBy(CostantiDB.SOGGETTI + ".tipo_soggetto");
						sqlQueryObject.addOrderBy(CostantiDB.SOGGETTI + ".nome_soggetto");
						sqlQueryObject.setSortType(true);
						queryString = sqlQueryObject.createSQLQuery();
						stmt = con.prepareStatement(queryString);
						stmt.setLong(1, idS);
						risultato = stmt.executeQuery();
						List<IDSoggetto> listFruitori = new ArrayList<IDSoggetto>();
						while (risultato.next()){
							listFruitori.add(new IDSoggetto(risultato.getString("tipo_soggetto"),risultato.getString("nome_soggetto")));
						}
						risultato.close();
						stmt.close();
						
						
						if(listFruitori!=null && !listFruitori.isEmpty()) {
						
							for (IDSoggetto idSoggettoFruitore : listFruitori) {
								List<MappingFruizionePortaDelegata> lPD = DBMappingUtils.mappingFruizionePortaDelegataList(con, tipoDB, idSoggettoFruitore, idServizio, true);
								if(lPD!=null && lPD.size()>0) {
									for (MappingFruizionePortaDelegata mapping : lPD) {
										
										
										// ** mapping **
										
										sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
										sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE);
										sqlQueryObject.addFromTable(CostantiDB.MAPPING_FRUIZIONE_PD);
										sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE_AZIONI);
										sqlQueryObject.addSelectField("nome_porta");
										sqlQueryObject.setANDLogicOperator(true);
										sqlQueryObject.addWhereCondition(CostantiDB.PORTE_DELEGATE+".nome_porta=?");
										sqlQueryObject.addWhereCondition(CostantiDB.PORTE_DELEGATE+".id="+CostantiDB.MAPPING_FRUIZIONE_PD+".id_porta");
										sqlQueryObject.addWhereCondition(CostantiDB.PORTE_DELEGATE_AZIONI+".id_porta="+CostantiDB.PORTE_DELEGATE+".id");
										sqlQueryObject.addWhereCondition(CostantiDB.PORTE_DELEGATE_AZIONI+".azione=?");
										queryString = sqlQueryObject.createSQLQuery();
										stmt = con.prepareStatement(queryString);
										stmt.setString(1, mapping.getIdPortaDelegata().getNome());
										stmt.setString(2, idRisorsa.getNome());
										risultato = stmt.executeQuery();
										while (risultato.next()){
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
										
										
										// ** trasformazioni **
										
										sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
										sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI);
										sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE);
										sqlQueryObject.addSelectField("nome_porta");
										sqlQueryObject.setANDLogicOperator(true);
										sqlQueryObject.addWhereCondition(CostantiDB.PORTE_DELEGATE+".nome_porta=?");
										sqlQueryObject.addWhereCondition(CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI+".id_porta="+CostantiDB.PORTE_DELEGATE+".id");
										// condizione di controllo
										ISQLQueryObject sqlQueryObjectOr = SQLObjectFactory.createSQLQueryObject(tipoDB);
										sqlQueryObjectOr.setANDLogicOperator(false);
										// (applicabilita_azioni == 'NOME') OR (applicabilita_azioni like 'NOME,%') OR (applicabilita_azioni like '%,NOME') OR (applicabilita_azioni like '%,applicabilita_azioni,%')
										// CLOB sqlQueryObjectOr.addWhereCondition(CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI+".applicabilita_azioni = ?");
										sqlQueryObjectOr.addWhereLikeCondition(CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI+".applicabilita_azioni", idRisorsa.getNome(), false , false);
										sqlQueryObjectOr.addWhereLikeCondition(CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI+".applicabilita_azioni", idRisorsa.getNome()+",", LikeConfig.startsWith(false));
										sqlQueryObjectOr.addWhereLikeCondition(CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI+".applicabilita_azioni", ","+idRisorsa.getNome(), LikeConfig.endsWith(false));
										sqlQueryObjectOr.addWhereLikeCondition(CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI+".applicabilita_azioni", ","+idRisorsa.getNome()+",", true , false);
										sqlQueryObject.addWhereCondition(sqlQueryObjectOr.createSQLConditions());
										queryString = sqlQueryObject.createSQLQuery();
										stmt = con.prepareStatement(queryString);
										stmt.setString(1, mapping.getIdPortaDelegata().getNome());
										// CLOB stmt.setString(2, idRisorsa.getNome());
										risultato = stmt.executeQuery();
										while (risultato.next()){
											String nome = risultato.getString("nome_porta");
											ResultPorta resultPorta = DBOggettiInUsoUtils.formatPortaDelegata(nome, tipoDB, con, normalizeObjectIds);
											if(resultPorta.mapping) {
												trasformazionePD_mapping_list.add(resultPorta.label);
											}
											else {
												trasformazionePD_list.add(resultPorta.label);
											}
											isInUso = true;
										}
										risultato.close();
										stmt.close();
										
										
										// ** Controllo che non sia associato a policy di controllo del traffico o allarmi **
										
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
										
											sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
											sqlQueryObject.addFromTable(tabella);
											sqlQueryObject.addSelectField(identificativo_column);
											sqlQueryObject.addSelectField(alias_column);
											sqlQueryObject.addSelectField("filtro_ruolo");
											sqlQueryObject.addSelectField("filtro_porta");
											sqlQueryObject.setANDLogicOperator(true);
											sqlQueryObject.addWhereCondition(tabella+".filtro_ruolo=?");
											sqlQueryObject.addWhereCondition(tabella+".filtro_porta=?");
											
											// condizione di controllo
											sqlQueryObjectOr = SQLObjectFactory.createSQLQueryObject(tipoDB);
											sqlQueryObjectOr.setANDLogicOperator(false);
											// (filtro_azione == 'NOME') OR (filtro_azione like 'NOME,%') OR (filtro_azione like '%,NOME') OR (filtro_azione like '%,applicabilita_azioni,%')
											// CLOB sqlQueryObjectOr.addWhereCondition(CostantiDB.CONTROLLO_TRAFFICO_ACTIVE_POLICY+".filtro_azione = ?");
											sqlQueryObjectOr.addWhereLikeCondition(tabella+".filtro_azione", idRisorsa.getNome(), false , false);
											sqlQueryObjectOr.addWhereLikeCondition(tabella+".filtro_azione", idRisorsa.getNome()+",", LikeConfig.startsWith(false));
											sqlQueryObjectOr.addWhereLikeCondition(tabella+".filtro_azione", ","+idRisorsa.getNome(), LikeConfig.endsWith(false));
											sqlQueryObjectOr.addWhereLikeCondition(tabella+".filtro_azione", ","+idRisorsa.getNome()+",", true , false);
											sqlQueryObject.addWhereCondition(sqlQueryObjectOr.createSQLConditions());
											
											sqlQueryObject.addOrderBy("filtro_ruolo");
											sqlQueryObject.addOrderBy("filtro_porta");
											queryString = sqlQueryObject.createSQLQuery();
											stmt = con.prepareStatement(queryString);
											stmt.setString(1, "delegata");
											stmt.setString(2, mapping.getIdPortaDelegata().getNome());
											// CLOB stmt.setString(3, idRisorsa.getNome());
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
								}
							}
							
						}
					}
				}
				
			}
				
				
			// Porte applicative, mapping
			if(config){
				if(idServiziWithAccordo!=null && !idServiziWithAccordo.isEmpty()) {
					for (IDServizio idServizio : idServiziWithAccordo) {
						List<MappingErogazionePortaApplicativa> lPA = DBMappingUtils.mappingErogazionePortaApplicativaList(con, tipoDB, idServizio, true);
						if(lPA!=null && lPA.size()>0) {
							for (MappingErogazionePortaApplicativa mapping : lPA) {
								
								
								// ** mapping **
								
								sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
								sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE);
								sqlQueryObject.addFromTable(CostantiDB.MAPPING_EROGAZIONE_PA);
								sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_AZIONI);
								sqlQueryObject.addSelectField("nome_porta");
								sqlQueryObject.setANDLogicOperator(true);
								sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE+".nome_porta=?");
								sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE+".id="+CostantiDB.MAPPING_EROGAZIONE_PA+".id_porta");
								sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE_AZIONI+".id_porta="+CostantiDB.PORTE_APPLICATIVE+".id");
								sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE_AZIONI+".azione=?");
								queryString = sqlQueryObject.createSQLQuery();
								stmt = con.prepareStatement(queryString);
								stmt.setString(1, mapping.getIdPortaApplicativa().getNome());
								stmt.setString(2, idRisorsa.getNome());
								risultato = stmt.executeQuery();
								while (risultato.next()){
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
								
								
								// ** trasformazioni **
								
								sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
								sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI);
								sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE);
								sqlQueryObject.addSelectField("nome_porta");
								sqlQueryObject.setANDLogicOperator(true);
								sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE+".nome_porta=?");
								sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI+".id_porta="+CostantiDB.PORTE_APPLICATIVE+".id");
								// condizione di controllo
								ISQLQueryObject sqlQueryObjectOr = SQLObjectFactory.createSQLQueryObject(tipoDB);
								sqlQueryObjectOr.setANDLogicOperator(false);
								// (applicabilita_azioni == 'NOME') OR (applicabilita_azioni like 'NOME,%') OR (applicabilita_azioni like '%,NOME') OR (applicabilita_azioni like '%,applicabilita_azioni,%')
								// CLOB sqlQueryObjectOr.addWhereCondition(CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI+".applicabilita_azioni = ?");
								sqlQueryObjectOr.addWhereLikeCondition(CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI+".applicabilita_azioni", idRisorsa.getNome(), false , false);
								sqlQueryObjectOr.addWhereLikeCondition(CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI+".applicabilita_azioni", idRisorsa.getNome()+",", LikeConfig.startsWith(false));
								sqlQueryObjectOr.addWhereLikeCondition(CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI+".applicabilita_azioni", ","+idRisorsa.getNome(), LikeConfig.endsWith(false));
								sqlQueryObjectOr.addWhereLikeCondition(CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI+".applicabilita_azioni", ","+idRisorsa.getNome()+",", true , false);
								sqlQueryObject.addWhereCondition(sqlQueryObjectOr.createSQLConditions());
								queryString = sqlQueryObject.createSQLQuery();
								stmt = con.prepareStatement(queryString);
								stmt.setString(1, mapping.getIdPortaApplicativa().getNome());
								// CLOB stmt.setString(2, idRisorsa.getNome());
								risultato = stmt.executeQuery();
								while (risultato.next()){
									String nome = risultato.getString("nome_porta");
									ResultPorta resultPorta = DBOggettiInUsoUtils.formatPortaApplicativa(nome, tipoDB, con, normalizeObjectIds);
									if(resultPorta.mapping) {
										trasformazionePA_mapping_list.add(resultPorta.label);
									}
									else {
										trasformazionePA_list.add(resultPorta.label);
									}
									isInUso = true;
								}
								risultato.close();
								stmt.close();
								
								
								// ** Controllo che non sia associato a policy di controllo del traffico o allarmi **
								
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
								
									sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
									sqlQueryObject.addFromTable(tabella);
									sqlQueryObject.addSelectField(identificativo_column);
									sqlQueryObject.addSelectField(alias_column);
									sqlQueryObject.addSelectField("filtro_ruolo");
									sqlQueryObject.addSelectField("filtro_porta");
									sqlQueryObject.setANDLogicOperator(true);
									sqlQueryObject.addWhereCondition(tabella+".filtro_ruolo=?");
									sqlQueryObject.addWhereCondition(tabella+".filtro_porta=?");
									
									// condizione di controllo
									sqlQueryObjectOr = SQLObjectFactory.createSQLQueryObject(tipoDB);
									sqlQueryObjectOr.setANDLogicOperator(false);
									// (filtro_azione == 'NOME') OR (filtro_azione like 'NOME,%') OR (filtro_azione like '%,NOME') OR (filtro_azione like '%,applicabilita_azioni,%')
									// CLOB sqlQueryObjectOr.addWhereCondition(CostantiDB.CONTROLLO_TRAFFICO_ACTIVE_POLICY+".filtro_azione = ?");
									sqlQueryObjectOr.addWhereLikeCondition(tabella+".filtro_azione", idRisorsa.getNome(), false , false);
									sqlQueryObjectOr.addWhereLikeCondition(tabella+".filtro_azione", idRisorsa.getNome()+",", LikeConfig.startsWith(false));
									sqlQueryObjectOr.addWhereLikeCondition(tabella+".filtro_azione", ","+idRisorsa.getNome(), LikeConfig.endsWith(false));
									sqlQueryObjectOr.addWhereLikeCondition(tabella+".filtro_azione", ","+idRisorsa.getNome()+",", true , false);
									sqlQueryObject.addWhereCondition(sqlQueryObjectOr.createSQLConditions());
									
									sqlQueryObject.addOrderBy("filtro_ruolo");
									sqlQueryObject.addOrderBy("filtro_porta");
									queryString = sqlQueryObject.createSQLQuery();
									stmt = con.prepareStatement(queryString);
									stmt.setString(1, "applicativa");
									stmt.setString(2, mapping.getIdPortaApplicativa().getNome());
									// CLOB stmt.setString(3, idRisorsa.getNome());
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
											if("applicativa".equals(filtro_ruolo)) {
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
						}
					}
				}
			}
			
			
			
			
			
			
			// ** Controllo che non sia associato a policy di controllo del traffico o allarmi generali **
			
			long idAccordoServizioParteComune = DBUtils.getIdAccordoServizioParteComune(idRisorsa.getIdAccordo(), con, tipoDB);
			if(idAccordoServizioParteComune<=0){
				throw new UtilsException("Accordi di Servizio Parte Comune con id ["+idRisorsa.getIdAccordo().toString()+"] non trovato");
			}
			
			// Recupero servizi che implementano l'accordo
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.SERVIZI);
			sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("id_accordo = ?");
			sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI+".id_soggetto = "+CostantiDB.SOGGETTI+".id");
			sqlQueryObject.setANDLogicOperator(true);
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idAccordoServizioParteComune);
			risultato = stmt.executeQuery();
			List<IDServizio> listIDServizio = new ArrayList<>();
			while (risultato.next()){
				String tipoSoggettoErogatore = risultato.getString("tipo_soggetto");
				String nomeSoggettoErogatore = risultato.getString("nome_soggetto");
				String tipoServizio = risultato.getString("tipo_servizio");
				String nomeServizio = risultato.getString("nome_servizio");
				int versioneServizio = risultato.getInt("versione_servizio");
				IDServizio idServizio = IDServizioFactory.getInstance().getIDServizioFromValues(tipoServizio, nomeServizio, 
						tipoSoggettoErogatore, nomeSoggettoErogatore, versioneServizio);
				listIDServizio.add(idServizio);
			}
			risultato.close();
			stmt.close();
			
			if(!listIDServizio.isEmpty()) {
			
				for (IDServizio idServizio : listIDServizio) {
					
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
					
						sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
						sqlQueryObject.addFromTable(tabella);
						sqlQueryObject.setSelectDistinct(true);
						sqlQueryObject.addSelectField(identificativo_column);
						sqlQueryObject.addSelectField(alias_column);
						sqlQueryObject.setANDLogicOperator(true);
						sqlQueryObject.addWhereIsNullCondition(tabella+".filtro_porta");
						
						ISQLQueryObject sqlQueryObjectServizioCompleto = SQLObjectFactory.createSQLQueryObject(tipoDB);
						sqlQueryObjectServizioCompleto.addWhereCondition(tabella+".filtro_tipo_erogatore=?");
						sqlQueryObjectServizioCompleto.addWhereCondition(tabella+".filtro_nome_erogatore=?");
						sqlQueryObjectServizioCompleto.addWhereCondition(tabella+".filtro_tipo_servizio=?");
						sqlQueryObjectServizioCompleto.addWhereCondition(tabella+".filtro_nome_servizio=?");
						sqlQueryObjectServizioCompleto.addWhereCondition(tabella+".filtro_versione_servizio=?");
						
						ISQLQueryObject sqlQueryObjectServizioParziale = SQLObjectFactory.createSQLQueryObject(tipoDB);
						sqlQueryObjectServizioParziale.addWhereIsNullCondition(tabella+".filtro_tipo_erogatore");
						sqlQueryObjectServizioParziale.addWhereIsNullCondition(tabella+".filtro_nome_erogatore");
						sqlQueryObjectServizioParziale.addWhereCondition(tabella+".filtro_tipo_servizio=?");
						sqlQueryObjectServizioParziale.addWhereCondition(tabella+".filtro_nome_servizio=?");
						sqlQueryObjectServizioParziale.addWhereCondition(tabella+".filtro_versione_servizio=?");
						
						sqlQueryObject.addWhereCondition(false,false, sqlQueryObjectServizioCompleto.createSQLConditions(), sqlQueryObjectServizioParziale.createSQLConditions());
						
						// condizione di controllo
						ISQLQueryObject sqlQueryObjectOr = SQLObjectFactory.createSQLQueryObject(tipoDB);
						sqlQueryObjectOr.setANDLogicOperator(false);
						// (filtro_azione == 'NOME') OR (filtro_azione like 'NOME,%') OR (filtro_azione like '%,NOME') OR (filtro_azione like '%,applicabilita_azioni,%')
						// CLOB sqlQueryObjectOr.addWhereCondition(CostantiDB.CONTROLLO_TRAFFICO_ACTIVE_POLICY+".filtro_azione = ?");
						sqlQueryObjectOr.addWhereLikeCondition(tabella+".filtro_azione", idRisorsa.getNome(), false , false);
						sqlQueryObjectOr.addWhereLikeCondition(tabella+".filtro_azione", idRisorsa.getNome()+",", LikeConfig.startsWith(false));
						sqlQueryObjectOr.addWhereLikeCondition(tabella+".filtro_azione", ","+idRisorsa.getNome(), LikeConfig.endsWith(false));
						sqlQueryObjectOr.addWhereLikeCondition(tabella+".filtro_azione", ","+idRisorsa.getNome()+",", true , false);
						sqlQueryObject.addWhereCondition(sqlQueryObjectOr.createSQLConditions());
						
						sqlQueryObject.addOrderBy(alias_column);
						sqlQueryObject.addOrderBy(identificativo_column);
						queryString = sqlQueryObject.createSQLQuery();
						stmt = con.prepareStatement(queryString);
						int index = 1;
						stmt.setString(index++, idServizio.getSoggettoErogatore().getTipo());
						stmt.setString(index++, idServizio.getSoggettoErogatore().getNome());
						stmt.setString(index++, idServizio.getTipo());
						stmt.setString(index++, idServizio.getNome());
						stmt.setInt(index++, idServizio.getVersione());
						stmt.setString(index++, idServizio.getTipo());
						stmt.setString(index++, idServizio.getNome());
						stmt.setInt(index++, idServizio.getVersione());
						// CLOB stmt.setString(index++, idRisorsa.getNome());
						risultato = stmt.executeQuery();
						while (risultato.next()) {
							
							String alias = risultato.getString(alias_column);
							if(alias== null || "".equals(alias)) {
								alias = risultato.getString(identificativo_column);
							}
							
							String oggettoTrovato = oggetto+" '"+alias+"'";
							if(!list.contains(oggettoTrovato)) {
								list.add(oggettoTrovato);
							}
			
							isInUso = true;
						}
						risultato.close();
						stmt.close();
					}
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


	protected static String toString(IDResource idRisorsa, String methodPath, Map<ErrorsHandlerCostant, List<String>> whereIsInUso, boolean prefix, String separator){
		return toString(idRisorsa, methodPath, whereIsInUso, prefix, separator," non eliminabile perch&egrave; :");
	}
	protected static String toString(IDResource idRisorsa, String methodPath, Map<ErrorsHandlerCostant, List<String>> whereIsInUso, boolean prefix, String separator, String intestazione){
		Set<ErrorsHandlerCostant> keys = whereIsInUso.keySet();
		String msg = "Risorsa '"+(methodPath!=null ? methodPath : idRisorsa.getNome())+"'" + intestazione+separator;
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

			case IS_CORRELATA:
				if ( messages!=null && messages.size() > 0) {
					msg += "correlata ad altre risorse: " + DBOggettiInUsoUtils.formatList(messages,separator) + separator;
				}
				break;
			
			case IN_USO_IN_PORTE_APPLICATIVE:
				if ( messages!=null && messages.size() > 0) {
					msg += "appartenente ad un gruppo differente da quello predefinito nelle Porte Inbound: " + DBOggettiInUsoUtils.formatList(messages,separator) + separator;
				}
				break;
			case IN_USO_IN_PORTE_DELEGATE:
				if ( messages!=null && messages.size() > 0) {
					msg += "appartenente ad un gruppo differente da quello predefinito nelle Porte Outbound: " + DBOggettiInUsoUtils.formatList(messages,separator) + separator;
				}
				break;
			case IN_USO_IN_MAPPING_EROGAZIONE_PA:
				if ( messages!=null && messages.size() > 0) {
					msg += "appartenente ad un gruppo differente da quello predefinito nelle Erogazioni: " + DBOggettiInUsoUtils.formatList(messages,separator) + separator;
				}
				break;
			case IN_USO_IN_MAPPING_FRUIZIONE_PD:
				if ( messages!=null && messages.size() > 0) {
					msg += "appartenente ad un gruppo differente da quello predefinito nelle Fruizioni: " + DBOggettiInUsoUtils.formatList(messages,separator) + separator;
				}
				break;
				
			case TRASFORMAZIONE_MAPPING_PD:
				if ( messages!=null && messages.size() > 0) {
					msg += "utilizzata nel criterio di applicabilità della Trasformazione (Risorse) per le Fruizioni: " + DBOggettiInUsoUtils.formatList(messages,separator) + separator;
				}
				break;
			case TRASFORMAZIONE_PD:
				if ( messages!=null && messages.size() > 0) {
					msg += "utilizzata nelle Porte Outbound (Criterio di applicabilità della Trasformazione - Risorse): " + DBOggettiInUsoUtils.formatList(messages,separator) + separator;
				}
				break;
			case TRASFORMAZIONE_MAPPING_PA:
				if ( messages!=null && messages.size() > 0) {
					msg += "utilizzata nel criterio di applicabilità della Trasformazione (Risorse) per le Erogazioni: " + DBOggettiInUsoUtils.formatList(messages,separator) + separator;
				}
				break;
			case TRASFORMAZIONE_PA:
				if ( messages!=null && messages.size() > 0) {
					msg += "utilizzata nelle Porte Inbound (Criterio di applicabilità della Trasformazione - Risorse): " + DBOggettiInUsoUtils.formatList(messages,separator) + separator;
				}
				break;
								
			case CONTROLLO_TRAFFICO:
				if ( messages!=null && messages.size() > 0 ) {
					msg += "utilizzata in Policy di Rate Limiting: " + DBOggettiInUsoUtils.formatList(messages,separator) + separator;
				}
				break;
			case ALLARMI:
				if ( messages!=null && messages.size() > 0 ) {
					msg += "utilizzato in Allarmi: " + DBOggettiInUsoUtils.formatList(messages,separator) + separator;
				}
				break;
				
			default:
				msg += "utilizzata in oggetto non codificato ("+key+")"+separator;
				break;
			}
			
		}// chiudo for

		return msg;
	}
	
}
