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
import org.openspcoop2.core.id.IDPortType;
import org.openspcoop2.core.id.IDPortTypeAzione;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.mapping.DBMappingUtils;
import org.openspcoop2.core.mapping.MappingErogazionePortaApplicativa;
import org.openspcoop2.core.mapping.MappingFruizionePortaDelegata;
import org.openspcoop2.core.registry.constants.ServiceBinding;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.engine.constants.Costanti;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.jdbc.JDBCUtilities;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.SQLObjectFactory;

/**
 * DBOggettiInUsoUtils_accordi
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class DBOggettiInUsoUtils_accordi {

	protected static boolean isAccordoServizioParteComuneInUso(Connection con, String tipoDB, IDAccordo idAccordo, 
			Map<ErrorsHandlerCostant,List<String>> whereIsInUso, boolean normalizeObjectIds) throws UtilsException {
		String nomeMetodo = "isAccordoServizioParteComuneInUso";

		PreparedStatement stmt = null;
		ResultSet risultato = null;
		String queryString;
		try {
			boolean isInUso = false;


			long idAccordoServizioParteComune = DBUtils.getIdAccordoServizioParteComune(idAccordo, con, tipoDB);
			if(idAccordoServizioParteComune<=0){
				throw new UtilsException("Accordi di Servizio Parte Comune con id ["+idAccordo.toString()+"] non trovato");
			}

			List<String> servizi_list = whereIsInUso.get(ErrorsHandlerCostant.IN_USO_IN_SERVIZI);
			List<String> mappingErogazionePA_list = whereIsInUso.get(ErrorsHandlerCostant.IN_USO_IN_MAPPING_EROGAZIONE_PA);
			List<String> mappingFruizionePD_list = whereIsInUso.get(ErrorsHandlerCostant.IN_USO_IN_MAPPING_FRUIZIONE_PD);
			
			List<String> correlazione_list = whereIsInUso.get(ErrorsHandlerCostant.IS_CORRELATA);
			
			if (servizi_list == null) {
				servizi_list = new ArrayList<>();
				whereIsInUso.put(ErrorsHandlerCostant.IN_USO_IN_SERVIZI, servizi_list);
			}
			if (mappingErogazionePA_list == null) {
				mappingErogazionePA_list = new ArrayList<>();
				whereIsInUso.put(ErrorsHandlerCostant.IN_USO_IN_MAPPING_EROGAZIONE_PA, mappingErogazionePA_list);
			}
			if (mappingFruizionePD_list == null) {
				mappingFruizionePD_list = new ArrayList<>();
				whereIsInUso.put(ErrorsHandlerCostant.IN_USO_IN_MAPPING_FRUIZIONE_PD, mappingFruizionePD_list);
			}
			
			if (correlazione_list == null) {
				correlazione_list = new ArrayList<>();
				whereIsInUso.put(ErrorsHandlerCostant.IS_CORRELATA, correlazione_list);
			}
			

			//controllo se in uso in servizi
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
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
				isInUso=true;
				
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
					
					String protocollo = ProtocolFactoryManager.getInstance().getProtocolByOrganizationType(idServizio.getSoggettoErogatore().getTipo());
					boolean found = false;
					
					// check PA
					List<MappingErogazionePortaApplicativa> listPA = null;
					try {
						listPA = DBMappingUtils.mappingErogazionePortaApplicativaList(con, tipoDB, idServizio, true);
					}catch(Exception e) {
						// ignore
					}
					if(listPA!=null && !listPA.isEmpty()) {
						found=true;
						for (MappingErogazionePortaApplicativa mappingPA : listPA) {
							String suffixGruppo = "";
							if(!mappingPA.isDefault()) {
								suffixGruppo = " (Gruppo: "+mappingPA.getDescrizione()+")";
							}
							String servizio = DBOggettiInUsoUtils.getProtocolPrefix(protocollo)+
									NamingUtils.getLabelAccordoServizioParteSpecifica(protocollo, mappingPA.getIdServizio())+
									suffixGruppo;
							mappingErogazionePA_list.add(servizio);
						}
					}
				
					// check PD
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
					sqlQueryObject.addFromTable(CostantiDB.SERVIZI);
					sqlQueryObject.addFromTable(CostantiDB.SERVIZI_FRUITORI);
					sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
					sqlQueryObject.addSelectField("tipo_soggetto");
					sqlQueryObject.addSelectField("nome_soggetto");
					sqlQueryObject.addWhereCondition("tipo_servizio = ?");
					sqlQueryObject.addWhereCondition("nome_servizio = ?");
					sqlQueryObject.addWhereCondition("versione_servizio = ?");
					sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI+".id_soggetto = ?");
					sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_FRUITORI+".id_servizio = "+CostantiDB.SERVIZI+".id");
					sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_FRUITORI+".id_soggetto = "+CostantiDB.SOGGETTI+".id");
					sqlQueryObject.setANDLogicOperator(true);
					queryString = sqlQueryObject.createSQLQuery();
					stmt = con.prepareStatement(queryString);
					stmt.setString(1, idServizio.getTipo());
					stmt.setString(2, idServizio.getNome());
					stmt.setInt(3, idServizio.getVersione());
					stmt.setLong(4, DBUtils.getIdSoggetto(idServizio.getSoggettoErogatore().getNome(), idServizio.getSoggettoErogatore().getTipo(), con, tipoDB));
					risultato = stmt.executeQuery();
					while (risultato.next()){					
						String tipoSoggettoFruitore = risultato.getString("tipo_soggetto");
						String nomeSoggettoFruitore = risultato.getString("nome_soggetto");
						IDSoggetto idSoggettoFruitore = new IDSoggetto(tipoSoggettoFruitore, nomeSoggettoFruitore);
						List<MappingFruizionePortaDelegata> listPD = null;
						try {
							listPD = DBMappingUtils.mappingFruizionePortaDelegataList(con, tipoDB, idSoggettoFruitore, idServizio, true);
						}catch(Exception e) {
							// ignore
						}
						if(listPD!=null && !listPD.isEmpty()) {
							found=true;
							for (MappingFruizionePortaDelegata mappingPD : listPD) {
								String suffixGruppo = "";
								if(!mappingPD.isDefault()) {
									suffixGruppo = " (Gruppo: "+mappingPD.getDescrizione()+")";
								}
								String servizio = DBOggettiInUsoUtils.getProtocolPrefix(protocollo)+
										NamingUtils.getLabelAccordoServizioParteSpecifica(protocollo, mappingPD.getIdServizio())+
										" (Fruitore:"+NamingUtils.getLabelSoggetto(protocollo, idSoggettoFruitore)+")"+
										suffixGruppo;
								mappingFruizionePD_list.add(servizio);
							}
						}
					}
					risultato.close();
					stmt.close();
					
					// servizio
					if(!found) {
						servizi_list.add(idServizio.toString());
					}
				}
				
			}

			
			
			
			// Controllo che qualche azione o risorsa non sia stata correlata da un'altra azione tramite ModI
			if(Costanti.MODIPA_PROTOCOL_NAME.equals(idAccordo.getSoggettoReferente().getTipo())) {
			
				// Recupero tipo REST/SOAP
				
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.ACCORDI);
				sqlQueryObject.addSelectField("service_binding");
				sqlQueryObject.addWhereCondition("id = ?");
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
				stmt = con.prepareStatement(queryString);
				stmt.setLong(1, idAccordoServizioParteComune);
				risultato = stmt.executeQuery();
				ServiceBinding tipoAccordo = null;
				if (risultato.next()){
					tipoAccordo = ServiceBinding.toEnumConstant(risultato.getString("service_binding"), false);
				}
				risultato.close();
				stmt.close();
				
				if(ServiceBinding.REST.equals(tipoAccordo)) {
					
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
					sqlQueryObject.addFromTable(CostantiDB.ACCORDI);
					sqlQueryObject.addFromTable(CostantiDB.API_RESOURCES);
					sqlQueryObject.addSelectAliasField(CostantiDB.API_RESOURCES,"nome","nomeRisorsa");
					sqlQueryObject.addSelectAliasField(CostantiDB.API_RESOURCES,"http_method","httpMethodRisorsa");
					sqlQueryObject.addSelectAliasField(CostantiDB.API_RESOURCES,"path","pathRisorsa");
					sqlQueryObject.addWhereCondition(CostantiDB.ACCORDI + ".id = ?");
					sqlQueryObject.addWhereCondition(CostantiDB.API_RESOURCES + ".id_accordo = " + CostantiDB.ACCORDI + ".id");
					sqlQueryObject.setANDLogicOperator(true);
					sqlQueryObject.addOrderBy("pathRisorsa");
					sqlQueryObject.addOrderBy("httpMethodRisorsa");
					queryString = sqlQueryObject.createSQLQuery();
					stmt = con.prepareStatement(queryString);
					int index = 1;
					stmt.setLong(index++, idAccordoServizioParteComune);
					risultato = stmt.executeQuery();
					List<String> risorse = new ArrayList<>();
					while(risultato.next()){
						String nomeRisorsa = risultato.getString("nomeRisorsa");
						risorse.add(nomeRisorsa);
					}
					risultato.close();
					stmt.close();
					
					if(!risorse.isEmpty()) {
						
						for (String nomeRisorsa : risorse) {
							
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
							
							
							// Verifico correlazione PUSH verso altri accordi
							
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
							stmt.setString(index++, IDAccordoFactory.getInstance().getUriFromIDAccordo(idAccordo));
							// sqlQueryObjectOperationExists
							stmt.setString(index++, ProprietariProtocolProperty.RESOURCE.name());
							stmt.setString(index++, Costanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_AZIONE_RICHIESTA_CORRELATA);
							stmt.setString(index++, nomeRisorsa);
							risultato = stmt.executeQuery();
							IDAccordoFactory idAccordoFactory = IDAccordoFactory.getInstance();
							while (risultato.next()){
								
								String nomeApi = risultato.getString("nomeApi");
								int versioneApi = risultato.getInt("versioneApi");
								String tipoReferenteApi = risultato.getString("tipoReferenteApi");
								String nomeReferenteApi = risultato.getString("nomeReferenteApi");
								IDAccordo idAPI = idAccordoFactory.getIDAccordoFromValues(nomeApi, tipoReferenteApi, nomeReferenteApi, versioneApi);
								
								if(idAPI.equals(idAccordo)) {
									continue; // non devo tenere traccia di correlazioni interne allo stesso accordo. Devo poter eliminare l'accordo nella sua interezza
								}
								
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
					}
				}
				else {
					
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
					sqlQueryObject.addFromTable(CostantiDB.ACCORDI);
					sqlQueryObject.addFromTable(CostantiDB.PORT_TYPE);
					sqlQueryObject.addFromTable(CostantiDB.PORT_TYPE_AZIONI);
					sqlQueryObject.addSelectAliasField(CostantiDB.PORT_TYPE_AZIONI,"nome","nomeAzione");
					sqlQueryObject.addSelectAliasField(CostantiDB.PORT_TYPE,"nome","nomePT");
					sqlQueryObject.addWhereCondition(CostantiDB.ACCORDI + ".id = ?");
					sqlQueryObject.addWhereCondition(CostantiDB.PORT_TYPE + ".id_accordo = " + CostantiDB.ACCORDI + ".id");
					sqlQueryObject.addWhereCondition(CostantiDB.PORT_TYPE_AZIONI + ".id_port_type = " + CostantiDB.PORT_TYPE + ".id");
					sqlQueryObject.setANDLogicOperator(true);
					sqlQueryObject.addOrderBy("nomePT");
					sqlQueryObject.addOrderBy("nomeAzione");
					queryString = sqlQueryObject.createSQLQuery();
					stmt = con.prepareStatement(queryString);
					int index = 1;
					stmt.setLong(index++, idAccordoServizioParteComune);
					risultato = stmt.executeQuery();
					List<IDPortTypeAzione> azioni = new ArrayList<IDPortTypeAzione>();
					while(risultato.next()){
						
						IDPortType idPT = new IDPortType();
						idPT.setIdAccordo(idAccordo);
						idPT.setNome(risultato.getString("nomePT"));
						
						IDPortTypeAzione id = new IDPortTypeAzione();
						id.setIdPortType(idPT);
						id.setNome(risultato.getString("nomeAzione"));
						
						azioni.add(id);
					}
					risultato.close();
					stmt.close();
					
					if(!azioni.isEmpty()) {
						
						for (IDPortTypeAzione idAzione : azioni) {
							
							ISQLQueryObject sqlQueryObjectApiExists = SQLObjectFactory.createSQLQueryObject(tipoDB);
							sqlQueryObjectApiExists.setANDLogicOperator(true);
							sqlQueryObjectApiExists.addFromTable(CostantiDB.PROTOCOL_PROPERTIES);
							sqlQueryObjectApiExists.addSelectField(CostantiDB.PROTOCOL_PROPERTIES, "name");
							sqlQueryObjectApiExists.addWhereCondition(CostantiDB.PORT_TYPE_AZIONI + ".id = " + CostantiDB.PROTOCOL_PROPERTIES + ".id_proprietario");
							sqlQueryObjectApiExists.addWhereCondition(CostantiDB.PROTOCOL_PROPERTIES + ".tipo_proprietario=?");
							sqlQueryObjectApiExists.addWhereCondition(CostantiDB.PROTOCOL_PROPERTIES + ".name=?");
							sqlQueryObjectApiExists.addWhereCondition(CostantiDB.PROTOCOL_PROPERTIES + ".value_string=?");
							
							ISQLQueryObject sqlQueryObjectPortTypeExists = SQLObjectFactory.createSQLQueryObject(tipoDB);
							sqlQueryObjectPortTypeExists.setANDLogicOperator(true);
							sqlQueryObjectPortTypeExists.addFromTable(CostantiDB.PROTOCOL_PROPERTIES);
							sqlQueryObjectPortTypeExists.addSelectField(CostantiDB.PROTOCOL_PROPERTIES, "name");
							sqlQueryObjectPortTypeExists.addWhereCondition(CostantiDB.PORT_TYPE_AZIONI + ".id = " + CostantiDB.PROTOCOL_PROPERTIES + ".id_proprietario");
							sqlQueryObjectPortTypeExists.addWhereCondition(CostantiDB.PROTOCOL_PROPERTIES + ".tipo_proprietario=?");
							sqlQueryObjectPortTypeExists.addWhereCondition(CostantiDB.PROTOCOL_PROPERTIES + ".name=?");
							sqlQueryObjectPortTypeExists.addWhereCondition(CostantiDB.PROTOCOL_PROPERTIES + ".value_string=?");
							
							ISQLQueryObject sqlQueryObjectOperationExists = SQLObjectFactory.createSQLQueryObject(tipoDB);
							sqlQueryObjectOperationExists.setANDLogicOperator(true);
							sqlQueryObjectOperationExists.addFromTable(CostantiDB.PROTOCOL_PROPERTIES);
							sqlQueryObjectOperationExists.addSelectField(CostantiDB.PROTOCOL_PROPERTIES, "name");
							sqlQueryObjectOperationExists.addWhereCondition(CostantiDB.PORT_TYPE_AZIONI + ".id = " + CostantiDB.PROTOCOL_PROPERTIES + ".id_proprietario");
							sqlQueryObjectOperationExists.addWhereCondition(CostantiDB.PROTOCOL_PROPERTIES + ".tipo_proprietario=?");
							sqlQueryObjectOperationExists.addWhereCondition(CostantiDB.PROTOCOL_PROPERTIES + ".name=?");
							sqlQueryObjectOperationExists.addWhereCondition(CostantiDB.PROTOCOL_PROPERTIES + ".value_string=?");
							
							
							// Verifico correlazione PUSH verso altri accordi
							
							sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
							sqlQueryObject.addFromTable(CostantiDB.ACCORDI);
							sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
							sqlQueryObject.addFromTable(CostantiDB.PORT_TYPE);
							sqlQueryObject.addFromTable(CostantiDB.PORT_TYPE_AZIONI);
							sqlQueryObject.addSelectAliasField(CostantiDB.PORT_TYPE_AZIONI,"nome","nomeAzioneCorrelata");
							sqlQueryObject.addSelectAliasField(CostantiDB.PORT_TYPE,"nome","nomePTCorrelato");
							sqlQueryObject.addSelectAliasField(CostantiDB.ACCORDI,"nome","nomeApi");
							sqlQueryObject.addSelectAliasField(CostantiDB.ACCORDI,"versione","versioneApi");
							sqlQueryObject.addSelectAliasField(CostantiDB.SOGGETTI,"tipo_soggetto","tipoReferenteApi");
							sqlQueryObject.addSelectAliasField(CostantiDB.SOGGETTI,"nome_soggetto","nomeReferenteApi");
							sqlQueryObject.addWhereCondition(CostantiDB.PORT_TYPE + ".id_accordo = " + CostantiDB.ACCORDI + ".id");
							sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI + ".id = " + CostantiDB.ACCORDI + ".id_referente");
							sqlQueryObject.addWhereCondition(CostantiDB.PORT_TYPE_AZIONI + ".id_port_type = " + CostantiDB.PORT_TYPE + ".id");
							sqlQueryObject.addWhereExistsCondition(false, sqlQueryObjectApiExists);
							sqlQueryObject.addWhereExistsCondition(false, sqlQueryObjectPortTypeExists);
							sqlQueryObject.addWhereExistsCondition(false, sqlQueryObjectOperationExists);
							sqlQueryObject.setANDLogicOperator(true);
							sqlQueryObject.addOrderBy("nomeApi");
							sqlQueryObject.addOrderBy("versioneApi");
							sqlQueryObject.addOrderBy("nomeReferenteApi");
							sqlQueryObject.addOrderBy("tipoReferenteApi");
							sqlQueryObject.addOrderBy("nomePTCorrelato");
							sqlQueryObject.addOrderBy("nomeAzioneCorrelata");
							sqlQueryObject.setSortType(true);
							queryString = sqlQueryObject.createSQLQuery();
							stmt = con.prepareStatement(queryString);
							index = 1;
							// sqlQueryObjectApiExists
							stmt.setString(index++, ProprietariProtocolProperty.OPERATION.name());
							stmt.setString(index++, Costanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_API_RICHIESTA_CORRELATA);
							stmt.setString(index++, IDAccordoFactory.getInstance().getUriFromIDAccordo(idAzione.getIdPortType().getIdAccordo()));
							// sqlQueryObjectPortTypeExists
							stmt.setString(index++, ProprietariProtocolProperty.OPERATION.name());
							stmt.setString(index++, Costanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_SERVIZIO_RICHIESTA_CORRELATA);
							stmt.setString(index++, idAzione.getIdPortType().getNome());
							// sqlQueryObjectOperationExists
							stmt.setString(index++, ProprietariProtocolProperty.OPERATION.name());
							stmt.setString(index++, Costanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_AZIONE_RICHIESTA_CORRELATA);
							stmt.setString(index++, idAzione.getNome());
							risultato = stmt.executeQuery();
							IDAccordoFactory idAccordoFactory = IDAccordoFactory.getInstance();
							while (risultato.next()){
								String nomeAzioneCorrelata = risultato.getString("nomeAzioneCorrelata");
								String nomePTCorrelato = risultato.getString("nomePTCorrelato");
								String nomeApi = risultato.getString("nomeApi");
								int versioneApi = risultato.getInt("versioneApi");
								String tipoReferenteApi = risultato.getString("tipoReferenteApi");
								String nomeReferenteApi = risultato.getString("nomeReferenteApi");
								IDAccordo idAPI = idAccordoFactory.getIDAccordoFromValues(nomeApi, tipoReferenteApi, nomeReferenteApi, versioneApi);
								
								if(idAPI.equals(idAccordo)) {
									continue; // non devo tenere traccia di correlazioni interne allo stesso accordo. Devo poter eliminare l'accordo nella sua interezza
								}
								
								correlazione_list.add("Azione "+nomeAzioneCorrelata+" del Servizio "+nomePTCorrelato+" dell'API '"+idAccordoFactory.getUriFromIDAccordo(idAPI)+"' (interazione: NonBloccante-Push)");
								isInUso = true;
							}
							risultato.close();
							stmt.close();
							
						}
						
					}
					
				}

			}
			
			
			return isInUso;

		} catch (Exception se) {

			throw new UtilsException("[DBOggettiInUsoUtils::" + nomeMetodo + "] Exception: " + se.getMessage(),se);
		} finally {
			//Chiudo statement and resultset
			JDBCUtilities.closeResources(risultato, stmt);
		}
	}


	protected static String toString(IDAccordo idAccordo, Map<ErrorsHandlerCostant, List<String>> whereIsInUso, boolean prefix, String separator, boolean normalizeObjectIds){

		StringBuilder bf = new StringBuilder();
		if(normalizeObjectIds && idAccordo.getSoggettoReferente()!=null) {
			try {
				String protocollo = ProtocolFactoryManager.getInstance().getProtocolByOrganizationType(idAccordo.getSoggettoReferente().getTipo());
				String labelAccordo = DBOggettiInUsoUtils.getProtocolPrefix(protocollo)+NamingUtils.getLabelAccordoServizioParteComune(protocollo, idAccordo);
				bf.append(labelAccordo);
			}catch(Exception e) {
				bf.append(idAccordo.toString());
			}
		}
		else {
			bf.append(idAccordo.toString());
		}

		Set<ErrorsHandlerCostant> keys = whereIsInUso.keySet();
		String msg = "API '"+bf.toString() + "' non eliminabile perch&egrave; :"+separator;
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
			case IN_USO_IN_SERVIZI:
				if ( messages!=null && messages.size() > 0) {
					msg += "implementata dai Servizi: " + DBOggettiInUsoUtils.formatList(messages,separator) + separator;
				}
				break;
			case IN_USO_IN_MAPPING_EROGAZIONE_PA:
				if ( messages!=null && messages.size() > 0) {
					msg += "implementata nelle Erogazioni: " + DBOggettiInUsoUtils.formatList(messages,separator) + separator;
				}
				break;
			case IN_USO_IN_MAPPING_FRUIZIONE_PD:
				if ( messages!=null && messages.size() > 0) {
					msg += "implementata nelle Fruizioni: " + DBOggettiInUsoUtils.formatList(messages,separator) + separator;
				}
				break;
				
			case IS_CORRELATA:
				if ( messages!=null && messages.size() > 0) {
					msg += "correlata ad operazioni di altre API: " + DBOggettiInUsoUtils.formatList(messages,separator) + separator;
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
