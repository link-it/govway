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

import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.core.commons.DBUtils;
import org.openspcoop2.core.commons.ErrorsHandlerCostant;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.constants.ProprietariProtocolProperty;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDPortType;
import org.openspcoop2.core.id.IDPortTypeAzione;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.mapping.DBMappingUtils;
import org.openspcoop2.core.mapping.MappingErogazionePortaApplicativa;
import org.openspcoop2.core.mapping.MappingFruizionePortaDelegata;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.protocol.engine.constants.Costanti;
import org.openspcoop2.protocol.engine.constants.ModalitaIdentificazioneAzione;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.jdbc.JDBCUtilities;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.LikeConfig;
import org.openspcoop2.utils.sql.SQLObjectFactory;

/**
 * DBOggettiInUsoUtils_accordiSoap
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class DBOggettiInUsoUtils_accordiSoap {
	
	private DBOggettiInUsoUtils_accordiSoap() {}

	protected static boolean isPortTypeConfigInUso(Connection con, String tipoDB, IDPortType idPT, Map<ErrorsHandlerCostant, 
			List<String>> whereIsInUso, boolean normalizeObjectIds) throws UtilsException {
		return isPortTypeInUsoInternal(con,tipoDB,idPT,false,true,whereIsInUso,normalizeObjectIds);
	}
	protected static boolean isPortTypeRegistryInUso(Connection con, String tipoDB, IDPortType idPT, Map<ErrorsHandlerCostant, 
			List<String>> whereIsInUso, boolean normalizeObjectIds) throws UtilsException {
		return isPortTypeInUsoInternal(con,tipoDB,idPT,true,false,whereIsInUso,normalizeObjectIds);
	}
	protected static boolean isPortTypeInUso(Connection con, String tipoDB, IDPortType idPT, Map<ErrorsHandlerCostant, 
			List<String>> whereIsInUso, boolean normalizeObjectIds) throws UtilsException {
		return isPortTypeInUsoInternal(con,tipoDB,idPT,true,true,whereIsInUso,normalizeObjectIds);
	}
	private static boolean isPortTypeInUsoInternal(Connection con, String tipoDB, IDPortType idPT,
			boolean registry, boolean config, Map<ErrorsHandlerCostant, 
			List<String>> whereIsInUso, boolean normalizeObjectIds) throws UtilsException {
		String nomeMetodo = "_isPortTypeInUso";

		PreparedStatement stmt = null;
		ResultSet risultato = null;
		String queryString;

		try {
		
			if(registry) {
				// nop
			}
			
			boolean isInUso = false;
			
			List<String> correlazioneList = whereIsInUso.get(ErrorsHandlerCostant.IS_CORRELATA);
			
			List<String> porteApplicativeList = whereIsInUso.get(ErrorsHandlerCostant.IN_USO_IN_PORTE_APPLICATIVE);
			List<String> porteDelegateList = whereIsInUso.get(ErrorsHandlerCostant.IN_USO_IN_PORTE_DELEGATE);
			List<String> mappingErogazionePAList = whereIsInUso.get(ErrorsHandlerCostant.IN_USO_IN_MAPPING_EROGAZIONE_PA);
			List<String> mappingFruizionePDList = whereIsInUso.get(ErrorsHandlerCostant.IN_USO_IN_MAPPING_FRUIZIONE_PD);
						
			if (correlazioneList == null) {
				correlazioneList = new ArrayList<>();
				whereIsInUso.put(ErrorsHandlerCostant.IS_CORRELATA, correlazioneList);
			}
			
			if (porteApplicativeList == null) {
				porteApplicativeList = new ArrayList<>();
				whereIsInUso.put(ErrorsHandlerCostant.IN_USO_IN_PORTE_APPLICATIVE, porteApplicativeList);
			}
			if (porteDelegateList == null) {
				porteDelegateList = new ArrayList<>();
				whereIsInUso.put(ErrorsHandlerCostant.IN_USO_IN_PORTE_DELEGATE, porteDelegateList);
			}
			if (mappingErogazionePAList == null) {
				mappingErogazionePAList = new ArrayList<>();
				whereIsInUso.put(ErrorsHandlerCostant.IN_USO_IN_MAPPING_EROGAZIONE_PA, mappingErogazionePAList);
			}
			if (mappingFruizionePDList == null) {
				mappingFruizionePDList = new ArrayList<>();
				whereIsInUso.put(ErrorsHandlerCostant.IN_USO_IN_MAPPING_FRUIZIONE_PD, mappingFruizionePDList);
			}
			
			
			
			// recupero id
			
			long idAccordo = DBUtils.getIdAccordoServizioParteComune(idPT.getIdAccordo(), con, tipoDB);
			if(idAccordo<=0) {
				throw new UtilsException("Accordo non trovato");
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
			sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI+".port_type=?"); // condizione per soap
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idAccordo);
			stmt.setString(2, idPT.getNome());
			risultato = stmt.executeQuery();
			List<IDServizio> idServiziWithAccordo = new ArrayList<>();
			while (risultato.next()) {
				IDSoggetto soggettoErogatore = new IDSoggetto(risultato.getString("tipo_soggetto"), risultato.getString("nome_soggetto"));
				IDServizio idServizio = 
						IDServizioFactory.getInstance().getIDServizioFromValues(risultato.getString("tipo_servizio"), 
								risultato.getString("nome_servizio"), soggettoErogatore, risultato.getInt("versione_servizio"));
				idServiziWithAccordo.add(idServizio);
			}
			risultato.close();
			stmt.close();
			

			
			
			
			
			
			// Controllo il servizio non sia stata correlato da un'altra azione
			
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PORT_TYPE);
			sqlQueryObject.addFromTable(CostantiDB.PORT_TYPE_AZIONI);
			sqlQueryObject.addSelectAliasField(CostantiDB.PORT_TYPE_AZIONI,"nome","nomeAzioneCorrelata");
			sqlQueryObject.addSelectAliasField(CostantiDB.PORT_TYPE,"nome","nomePTCorrelato");
			sqlQueryObject.addWhereCondition(CostantiDB.PORT_TYPE + ".id_accordo = ?");
			sqlQueryObject.addWhereCondition(CostantiDB.PORT_TYPE_AZIONI + ".id_port_type = " + CostantiDB.PORT_TYPE + ".id");
			sqlQueryObject.addWhereCondition(CostantiDB.PORT_TYPE_AZIONI + ".correlata_servizio = ?");
			sqlQueryObject.setANDLogicOperator(true);
			sqlQueryObject.addOrderBy("nomePTCorrelato");
			sqlQueryObject.addOrderBy("nomeAzioneCorrelata");
			sqlQueryObject.setSortType(true);
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idAccordo);
			stmt.setString(2, idPT.getNome());
			risultato = stmt.executeQuery();
			while (risultato.next()){
				String nomeAzioneCorrelata = risultato.getString("nomeAzioneCorrelata");
				String nomePTCorrelato = risultato.getString("nomePTCorrelato");
				correlazioneList.add("Azione "+nomeAzioneCorrelata+" del Servizio "+nomePTCorrelato);
				isInUso = true;
			}
			risultato.close();
			stmt.close();
			
			
			
			
			
			// Controllo che l'azione non sia stata correlata da un'altra azione tramite ModI
			if(Costanti.MODIPA_PROTOCOL_NAME.equals(idPT.getIdAccordo().getSoggettoReferente().getTipo())) {
			
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
				
								
				// Verifico correlazione PUSH
				
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
				int index = 1;
				// sqlQueryObjectApiExists
				stmt.setString(index++, ProprietariProtocolProperty.OPERATION.name());
				stmt.setString(index++, Costanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_API_RICHIESTA_CORRELATA);
				stmt.setString(index++, IDAccordoFactory.getInstance().getUriFromIDAccordo(idPT.getIdAccordo()));
				// sqlQueryObjectPortTypeExists
				stmt.setString(index++, ProprietariProtocolProperty.OPERATION.name());
				stmt.setString(index++, Costanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_SERVIZIO_RICHIESTA_CORRELATA);
				stmt.setString(index++, idPT.getNome());
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
					correlazioneList.add("Azione "+nomeAzioneCorrelata+" del Servizio "+nomePTCorrelato+" dell'API '"+idAccordoFactory.getUriFromIDAccordo(idAPI)+"' (interazione: NonBloccante-Push)");
					isInUso = true;
				}
				risultato.close();
				stmt.close();
				
			}
			
			
			
			
			
			
			
			// Porte delegate, mapping
			if(config &&
				idServiziWithAccordo!=null && !idServiziWithAccordo.isEmpty()) {
				for (IDServizio idServizio : idServiziWithAccordo) {
					
					long idS = DBUtils.getIdServizio(idServizio.getNome(), idServizio.getTipo(), idServizio.getVersione(),
							idServizio.getSoggettoErogatore().getNome(), idServizio.getSoggettoErogatore().getTipo(),
							con, tipoDB);
					if(idS<=0) {
						throw new CoreException("Servizio '"+idServizio+"' non esistente");
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
					List<IDSoggetto> listFruitori = new ArrayList<>();
					while (risultato.next()){
						listFruitori.add(new IDSoggetto(risultato.getString("tipo_soggetto"),risultato.getString("nome_soggetto")));
					}
					risultato.close();
					stmt.close();
					
					
					if(listFruitori!=null && !listFruitori.isEmpty()) {
					
						for (IDSoggetto idSoggettoFruitore : listFruitori) {
							List<MappingFruizionePortaDelegata> lPD = DBMappingUtils.mappingFruizionePortaDelegataList(con, tipoDB, idSoggettoFruitore, idServizio, true);
							if(lPD!=null && !lPD.isEmpty()) {
								for (MappingFruizionePortaDelegata mapping : lPD) {
									if(!mapping.isDefault()) {
										// segnalo solo quelle di default, non ha senso segnalarte tutte
										continue;
									}
									String nome = mapping.getIdPortaDelegata().getNome();
									ResultPorta resultPorta = DBOggettiInUsoUtils.formatPortaDelegata(nome, tipoDB, con, normalizeObjectIds);
									if(resultPorta.mapping) {
										mappingFruizionePDList.add(resultPorta.label);
									}
									else {
										porteDelegateList.add(resultPorta.label);
									}
									isInUso = true;
								}
							}
						}
						
					}
				}
			}
				
			
				
				
			// Porte applicative, mapping
			if(config &&
				idServiziWithAccordo!=null && !idServiziWithAccordo.isEmpty()) {
				for (IDServizio idServizio : idServiziWithAccordo) {
					List<MappingErogazionePortaApplicativa> lPA = DBMappingUtils.mappingErogazionePortaApplicativaList(con, tipoDB, idServizio, true);
					if(lPA!=null && !lPA.isEmpty()) {
						for (MappingErogazionePortaApplicativa mapping : lPA) {
							if(!mapping.isDefault()) {
								// segnalo solo quelle di default, non ha senso segnalarte tutte
								continue;
							}
							String nome = mapping.getIdPortaApplicativa().getNome();
							ResultPorta resultPorta = DBOggettiInUsoUtils.formatPortaApplicativa(nome, tipoDB, con, normalizeObjectIds);
							if(resultPorta.mapping) {
								mappingErogazionePAList.add(resultPorta.label);
							}
							else {
								porteApplicativeList.add(resultPorta.label);
							}
							isInUso = true;
						}
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


	protected static String toString(IDPortType idPT, Map<ErrorsHandlerCostant, List<String>> whereIsInUso, boolean prefix, String separator){
		return toString(idPT, whereIsInUso, prefix, separator," non eliminabile perch&egrave; :");
	}
	protected static String toString(IDPortType idPT, Map<ErrorsHandlerCostant, List<String>> whereIsInUso, boolean prefix, String separator, String intestazione){
		Set<ErrorsHandlerCostant> keys = whereIsInUso.keySet();
		StringBuilder msgBuilder = new StringBuilder("Servizio '"+(idPT.getNome())+"'" + intestazione+separator);
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

			case IS_CORRELATA:
				if ( messages!=null && !messages.isEmpty()) {
					msgBuilder.append("correlato verso altre azioni: " + DBOggettiInUsoUtils.formatList(messages,separator) + separator);
				}
				break;
			
			case IN_USO_IN_PORTE_APPLICATIVE:
				if ( messages!=null && !messages.isEmpty()) {
					msgBuilder.append("utilizzato nelle Porte Inbound: " + DBOggettiInUsoUtils.formatList(messages,separator) + separator);
				}
				break;
			case IN_USO_IN_PORTE_DELEGATE:
				if ( messages!=null && !messages.isEmpty()) {
					msgBuilder.append("utilizzato nelle Porte Outbound: " + DBOggettiInUsoUtils.formatList(messages,separator) + separator);
				}
				break;			
			case IN_USO_IN_MAPPING_EROGAZIONE_PA:
				if ( messages!=null && !messages.isEmpty()) {
					msgBuilder.append("utilizzato nelle Erogazioni: " + DBOggettiInUsoUtils.formatList(messages,separator) + separator);
				}
				break;
			case IN_USO_IN_MAPPING_FRUIZIONE_PD:
				if ( messages!=null && !messages.isEmpty()) {
					msgBuilder.append("utilizzato nelle Fruizioni: " + DBOggettiInUsoUtils.formatList(messages,separator) + separator);
				}
				break;
				
			default:
				msgBuilder.append("utilizzata in oggetto non codificato ("+key+")"+separator);
				break;
			}
			
		}// chiudo for

		return msgBuilder.toString();
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	// ***** OPERATION ******

	protected static boolean isOperazioneConfigInUso(Connection con, String tipoDB, IDPortTypeAzione idOperazione, Map<ErrorsHandlerCostant, 
			List<String>> whereIsInUso, boolean normalizeObjectIds) throws UtilsException {
		return isOperazioneInUsoInternal(con,tipoDB,idOperazione,false,true,whereIsInUso,normalizeObjectIds);
	}
	protected static boolean isOperazioneRegistryInUso(Connection con, String tipoDB, IDPortTypeAzione idOperazione, Map<ErrorsHandlerCostant, 
			List<String>> whereIsInUso, boolean normalizeObjectIds) throws UtilsException {
		return isOperazioneInUsoInternal(con,tipoDB,idOperazione,true,false,whereIsInUso,normalizeObjectIds);
	}
	protected static boolean isOperazioneInUso(Connection con, String tipoDB, IDPortTypeAzione idOperazione, Map<ErrorsHandlerCostant, 
			List<String>> whereIsInUso, boolean normalizeObjectIds) throws UtilsException {
		return isOperazioneInUsoInternal(con,tipoDB,idOperazione,true,true,whereIsInUso,normalizeObjectIds);
	}
	private static boolean isOperazioneInUsoInternal(Connection con, String tipoDB, IDPortTypeAzione idOperazione,
			boolean registry, boolean config, Map<ErrorsHandlerCostant, 
			List<String>> whereIsInUso, boolean normalizeObjectIds) throws UtilsException {
		String nomeMetodo = "_isOperazioneInUso";

		PreparedStatement stmt = null;
		ResultSet risultato = null;
		String queryString;

		try {
		
			boolean isInUso = false;
			
			List<String> correlazioneList = whereIsInUso.get(ErrorsHandlerCostant.IS_CORRELATA);
			
			List<String> porteApplicativeList = whereIsInUso.get(ErrorsHandlerCostant.IN_USO_IN_PORTE_APPLICATIVE);
			List<String> porteDelegateList = whereIsInUso.get(ErrorsHandlerCostant.IN_USO_IN_PORTE_DELEGATE);
			List<String> mappingErogazionePAList = whereIsInUso.get(ErrorsHandlerCostant.IN_USO_IN_MAPPING_EROGAZIONE_PA);
			List<String> mappingFruizionePDList = whereIsInUso.get(ErrorsHandlerCostant.IN_USO_IN_MAPPING_FRUIZIONE_PD);
			
			List<String> trasformazionePDMappingList = whereIsInUso.get(ErrorsHandlerCostant.TRASFORMAZIONE_MAPPING_PD);
			List<String> trasformazionePDList = whereIsInUso.get(ErrorsHandlerCostant.TRASFORMAZIONE_PD);
			List<String> trasformazionePAMappingLis = whereIsInUso.get(ErrorsHandlerCostant.TRASFORMAZIONE_MAPPING_PA);
			List<String> trasformazionePAList = whereIsInUso.get(ErrorsHandlerCostant.TRASFORMAZIONE_PA);
			
			List<String> urlInvocazionePDMappingList = whereIsInUso.get(ErrorsHandlerCostant.URLINVOCAZIONE_MAPPING_PD);
			List<String> urlInvocazionePDList = whereIsInUso.get(ErrorsHandlerCostant.URLINVOCAZIONE_PD);
			List<String> urlInvocazionePAMappingList = whereIsInUso.get(ErrorsHandlerCostant.URLINVOCAZIONE_MAPPING_PA);
			List<String> urlInvocazionePAList = whereIsInUso.get(ErrorsHandlerCostant.URLINVOCAZIONE_PA);
			
			List<String> ctList = whereIsInUso.get(ErrorsHandlerCostant.CONTROLLO_TRAFFICO);
			List<String> allarmeList = whereIsInUso.get(ErrorsHandlerCostant.ALLARMI);
			
			List<String> modiSignalHubList = whereIsInUso.get(ErrorsHandlerCostant.IS_RIFERITA_MODI_SIGNAL_HUB);
			
			if (correlazioneList == null) {
				correlazioneList = new ArrayList<>();
				whereIsInUso.put(ErrorsHandlerCostant.IS_CORRELATA, correlazioneList);
			}
			
			if (porteApplicativeList == null) {
				porteApplicativeList = new ArrayList<>();
				whereIsInUso.put(ErrorsHandlerCostant.IN_USO_IN_PORTE_APPLICATIVE, porteApplicativeList);
			}
			if (porteDelegateList == null) {
				porteDelegateList = new ArrayList<>();
				whereIsInUso.put(ErrorsHandlerCostant.IN_USO_IN_PORTE_DELEGATE, porteDelegateList);
			}
			if (mappingErogazionePAList == null) {
				mappingErogazionePAList = new ArrayList<>();
				whereIsInUso.put(ErrorsHandlerCostant.IN_USO_IN_MAPPING_EROGAZIONE_PA, mappingErogazionePAList);
			}
			if (mappingFruizionePDList == null) {
				mappingFruizionePDList = new ArrayList<>();
				whereIsInUso.put(ErrorsHandlerCostant.IN_USO_IN_MAPPING_FRUIZIONE_PD, mappingFruizionePDList);
			}
			
			if (trasformazionePDMappingList == null) {
				trasformazionePDMappingList = new ArrayList<>();
				whereIsInUso.put(ErrorsHandlerCostant.TRASFORMAZIONE_MAPPING_PD, trasformazionePDMappingList);
			}
			if (trasformazionePDList == null) {
				trasformazionePDList = new ArrayList<>();
				whereIsInUso.put(ErrorsHandlerCostant.TRASFORMAZIONE_PD, trasformazionePDList);
			}
			if (trasformazionePAMappingLis == null) {
				trasformazionePAMappingLis = new ArrayList<>();
				whereIsInUso.put(ErrorsHandlerCostant.TRASFORMAZIONE_MAPPING_PA, trasformazionePAMappingLis);
			}
			if (trasformazionePAList == null) {
				trasformazionePAList = new ArrayList<>();
				whereIsInUso.put(ErrorsHandlerCostant.TRASFORMAZIONE_PA, trasformazionePAList);
			}
			
			if (urlInvocazionePDMappingList == null) {
				urlInvocazionePDMappingList = new ArrayList<>();
				whereIsInUso.put(ErrorsHandlerCostant.URLINVOCAZIONE_MAPPING_PD, urlInvocazionePDMappingList);
			}
			if (urlInvocazionePDList == null) {
				urlInvocazionePDList = new ArrayList<>();
				whereIsInUso.put(ErrorsHandlerCostant.URLINVOCAZIONE_PD, urlInvocazionePDList);
			}
			if (urlInvocazionePAMappingList == null) {
				urlInvocazionePAMappingList = new ArrayList<>();
				whereIsInUso.put(ErrorsHandlerCostant.URLINVOCAZIONE_MAPPING_PA, urlInvocazionePAMappingList);
			}
			if (urlInvocazionePAList == null) {
				urlInvocazionePAList = new ArrayList<>();
				whereIsInUso.put(ErrorsHandlerCostant.URLINVOCAZIONE_PA, urlInvocazionePAList);
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
			
			
			
			// recupero id
			
			long idAccordo = DBUtils.getIdAccordoServizioParteComune(idOperazione.getIdPortType().getIdAccordo(), con, tipoDB);
			if(idAccordo<=0) {
				throw new UtilsException("Accordo non trovato");
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
			sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI+".port_type=?"); // condizione per soap
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idAccordo);
			stmt.setString(2, idOperazione.getIdPortType().getNome());
			risultato = stmt.executeQuery();
			List<IDServizio> idServiziWithAccordo = new ArrayList<>();
			while (risultato.next()) {
				IDSoggetto soggettoErogatore = new IDSoggetto(risultato.getString("tipo_soggetto"), risultato.getString("nome_soggetto"));
				IDServizio idServizio = 
						IDServizioFactory.getInstance().getIDServizioFromValues(risultato.getString("tipo_servizio"), 
								risultato.getString("nome_servizio"), soggettoErogatore, risultato.getInt("versione_servizio"));
				idServiziWithAccordo.add(idServizio);
			}
			risultato.close();
			stmt.close();
			
			
			
			
			
			// Controllo che l'azione non sia stata correlata da un'altra azione
			
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PORT_TYPE);
			sqlQueryObject.addFromTable(CostantiDB.PORT_TYPE_AZIONI);
			sqlQueryObject.addSelectAliasField(CostantiDB.PORT_TYPE_AZIONI,"nome","nomeAzioneCorrelata");
			sqlQueryObject.addSelectAliasField(CostantiDB.PORT_TYPE,"nome","nomePTCorrelato");
			sqlQueryObject.addSelectAliasField(CostantiDB.PORT_TYPE_AZIONI,"correlata_servizio","correlataServizio");
			sqlQueryObject.addWhereCondition(CostantiDB.PORT_TYPE + ".id_accordo = ?");
			sqlQueryObject.addWhereCondition(CostantiDB.PORT_TYPE_AZIONI + ".id_port_type = " + CostantiDB.PORT_TYPE + ".id");
			
			ISQLQueryObject sqlQueryObjectOr = SQLObjectFactory.createSQLQueryObject(tipoDB);
			sqlQueryObjectOr.setANDLogicOperator(false);
			// asincrono asimmetrico
			sqlQueryObjectOr.addWhereCondition(true, 
					CostantiDB.PORT_TYPE_AZIONI + ".correlata = ?",
					(CostantiDB.PORT_TYPE_AZIONI + ".correlata_servizio is null OR "+CostantiDB.PORT_TYPE_AZIONI + ".correlata_servizio = ?"),
					CostantiDB.PORT_TYPE + ".nome = ?");
			// asincrono simmetrico
			sqlQueryObjectOr.addWhereCondition(true,
					CostantiDB.PORT_TYPE_AZIONI + ".correlata = ?",
					CostantiDB.PORT_TYPE_AZIONI + ".correlata_servizio = ?",
					CostantiDB.PORT_TYPE + ".nome <> ?");
			sqlQueryObject.addWhereCondition(sqlQueryObjectOr.createSQLConditions());
			
			sqlQueryObject.setANDLogicOperator(true);
			sqlQueryObject.addOrderBy("nomePTCorrelato");
			sqlQueryObject.addOrderBy("nomeAzioneCorrelata");
			sqlQueryObject.setSortType(true);
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			int index = 1;
			stmt.setLong(index++, idAccordo);
			// asincrono asimmetrico
			stmt.setString(index++, idOperazione.getNome());
			stmt.setString(index++, "");
			stmt.setString(index++, idOperazione.getIdPortType().getNome());
			// asincrono simmetrico
			stmt.setString(index++, idOperazione.getNome());
			stmt.setString(index++, idOperazione.getIdPortType().getNome());
			stmt.setString(index++, idOperazione.getIdPortType().getNome());
			risultato = stmt.executeQuery();
			while (risultato.next()){
				String nomeAzioneCorrelata = risultato.getString("nomeAzioneCorrelata");
				String nomePTCorrelato = risultato.getString("nomePTCorrelato");
				String correlataServizio = risultato.getString("correlataServizio");
				if(correlataServizio!=null && !"".equals(correlataServizio)) {
					correlazioneList.add("Azione "+nomeAzioneCorrelata+" del Servizio "+nomePTCorrelato);
				}
				else {
					correlazioneList.add("Azione "+nomeAzioneCorrelata);
				}
				isInUso = true;
			}
			risultato.close();
			stmt.close();
			
			
			
			
			
			// Controllo che l'azione non sia stata correlata da un'altra azione tramite ModI
			if(Costanti.MODIPA_PROTOCOL_NAME.equals(idOperazione.getIdPortType().getIdAccordo().getSoggettoReferente().getTipo())) {
			
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
				
				
				// Verifico correlazione PULL (all'interno del solito accordo)
				
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORT_TYPE);
				sqlQueryObject.addFromTable(CostantiDB.PORT_TYPE_AZIONI);
				sqlQueryObject.addSelectAliasField(CostantiDB.PORT_TYPE_AZIONI,"nome","nomeAzioneCorrelata");
				sqlQueryObject.addWhereCondition(CostantiDB.PORT_TYPE + ".id_accordo = ?");
				sqlQueryObject.addWhereCondition(CostantiDB.PORT_TYPE + ".nome = ?");
				sqlQueryObject.addWhereCondition(CostantiDB.PORT_TYPE_AZIONI + ".id_port_type = " + CostantiDB.PORT_TYPE + ".id");
				sqlQueryObject.addWhereExistsCondition(false, sqlQueryObjectApiExists);
				sqlQueryObject.addWhereExistsCondition(false, sqlQueryObjectPortTypeExists);
				sqlQueryObject.addWhereExistsCondition(false, sqlQueryObjectOperationExists);
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addOrderBy("nomeAzioneCorrelata");
				sqlQueryObject.setSortType(true);
				queryString = sqlQueryObject.createSQLQuery();
				stmt = con.prepareStatement(queryString);
				index = 1;
				stmt.setLong(index++, idAccordo);
				stmt.setString(index++, idOperazione.getIdPortType().getNome());
				// sqlQueryObjectApiExists
				stmt.setString(index++, ProprietariProtocolProperty.OPERATION.name());
				stmt.setString(index++, Costanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_API_RICHIESTA_CORRELATA);
				stmt.setString(index++, Costanti.MODIPA_VALUE_UNDEFINED);
				// sqlQueryObjectPortTypeExists
				stmt.setString(index++, ProprietariProtocolProperty.OPERATION.name());
				stmt.setString(index++, Costanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_SERVIZIO_RICHIESTA_CORRELATA);
				stmt.setString(index++, Costanti.MODIPA_VALUE_UNDEFINED);
				// sqlQueryObjectOperationExists
				stmt.setString(index++, ProprietariProtocolProperty.OPERATION.name());
				stmt.setString(index++, Costanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_AZIONE_RICHIESTA_CORRELATA);
				stmt.setString(index++, idOperazione.getNome());
				risultato = stmt.executeQuery();
				while (risultato.next()){
					String nomeAzioneCorrelata = risultato.getString("nomeAzioneCorrelata");
					correlazioneList.add("Azione "+nomeAzioneCorrelata+" (interazione: NonBloccante-Pull)");
					isInUso = true;
				}
				risultato.close();
				stmt.close();
				
				// Verifico correlazione PUSH
				
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
				stmt.setString(index++, IDAccordoFactory.getInstance().getUriFromIDAccordo(idOperazione.getIdPortType().getIdAccordo()));
				// sqlQueryObjectPortTypeExists
				stmt.setString(index++, ProprietariProtocolProperty.OPERATION.name());
				stmt.setString(index++, Costanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_SERVIZIO_RICHIESTA_CORRELATA);
				stmt.setString(index++, idOperazione.getIdPortType().getNome());
				// sqlQueryObjectOperationExists
				stmt.setString(index++, ProprietariProtocolProperty.OPERATION.name());
				stmt.setString(index++, Costanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_AZIONE_RICHIESTA_CORRELATA);
				stmt.setString(index++, idOperazione.getNome());
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
					correlazioneList.add("Azione "+nomeAzioneCorrelata+" del Servizio "+nomePTCorrelato+" dell'API '"+idAccordoFactory.getUriFromIDAccordo(idAPI)+"' (interazione: NonBloccante-Push)");
					isInUso = true;
				}
				risultato.close();
				stmt.close();
				
			}
			
			
			

			
			// Porte delegate
			if(config &&
				idServiziWithAccordo!=null && !idServiziWithAccordo.isEmpty()) {
				for (IDServizio idServizio : idServiziWithAccordo) {
					
					long idS = DBUtils.getIdServizio(idServizio.getNome(), idServizio.getTipo(), idServizio.getVersione(),
							idServizio.getSoggettoErogatore().getNome(), idServizio.getSoggettoErogatore().getTipo(),
							con, tipoDB);
					if(idS<=0) {
						throw new CoreException("Servizio '"+idServizio+"' non esistente");
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
					List<IDSoggetto> listFruitori = new ArrayList<>();
					while (risultato.next()){
						listFruitori.add(new IDSoggetto(risultato.getString("tipo_soggetto"),risultato.getString("nome_soggetto")));
					}
					risultato.close();
					stmt.close();
					
					
					if(listFruitori!=null && !listFruitori.isEmpty()) {
					
						for (IDSoggetto idSoggettoFruitore : listFruitori) {
							List<MappingFruizionePortaDelegata> lPD = DBMappingUtils.mappingFruizionePortaDelegataList(con, tipoDB, idSoggettoFruitore, idServizio, true);
							if(lPD!=null && !lPD.isEmpty()) {
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
									stmt.setString(2, idOperazione.getNome());
									risultato = stmt.executeQuery();
									while (risultato.next()){
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
									
									
									// ** trasformazioni **
									
									sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
									sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI);
									sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE);
									sqlQueryObject.addSelectField("nome_porta");
									sqlQueryObject.setANDLogicOperator(true);
									sqlQueryObject.addWhereCondition(CostantiDB.PORTE_DELEGATE+".nome_porta=?");
									sqlQueryObject.addWhereCondition(CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI+".id_porta="+CostantiDB.PORTE_DELEGATE+".id");
									// condizione di controllo
									sqlQueryObjectOr = SQLObjectFactory.createSQLQueryObject(tipoDB);
									sqlQueryObjectOr.setANDLogicOperator(false);
									// (applicabilita_azioni == 'NOME') OR (applicabilita_azioni like 'NOME,%') OR (applicabilita_azioni like '%,NOME') OR (applicabilita_azioni like '%,applicabilita_azioni,%')
									/** CLOB sqlQueryObjectOr.addWhereCondition(CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI+".applicabilita_azioni = ?");*/
									sqlQueryObjectOr.addWhereLikeCondition(CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI+".applicabilita_azioni", idOperazione.getNome(), false , false);
									sqlQueryObjectOr.addWhereLikeCondition(CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI+".applicabilita_azioni", idOperazione.getNome()+",", LikeConfig.startsWith(false));
									sqlQueryObjectOr.addWhereLikeCondition(CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI+".applicabilita_azioni", ","+idOperazione.getNome(), LikeConfig.endsWith(false));
									sqlQueryObjectOr.addWhereLikeCondition(CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI+".applicabilita_azioni", ","+idOperazione.getNome()+",", true , false);
									sqlQueryObject.addWhereCondition(sqlQueryObjectOr.createSQLConditions());
									queryString = sqlQueryObject.createSQLQuery();
									stmt = con.prepareStatement(queryString);
									stmt.setString(1, mapping.getIdPortaDelegata().getNome());
									/** CLOB stmt.setString(2, idRisorsa.getNome());*/
									risultato = stmt.executeQuery();
									while (risultato.next()){
										String nome = risultato.getString("nome_porta");
										ResultPorta resultPorta = DBOggettiInUsoUtils.formatPortaDelegata(nome, tipoDB, con, normalizeObjectIds);
										if(resultPorta.mapping) {
											trasformazionePDMappingList.add(resultPorta.label);
										}
										else {
											trasformazionePDList.add(resultPorta.label);
										}
										isInUso = true;
									}
									risultato.close();
									stmt.close();
									
									
									// ** url di invocazione **
									
									sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
									sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE);
									sqlQueryObject.addSelectField("nome_porta");
									sqlQueryObject.setANDLogicOperator(true);
									sqlQueryObject.addWhereCondition(CostantiDB.PORTE_DELEGATE+".nome_porta=?");
									sqlQueryObject.addWhereCondition(CostantiDB.PORTE_DELEGATE+".tipo_soggetto_erogatore=?");
									sqlQueryObject.addWhereCondition(CostantiDB.PORTE_DELEGATE+".nome_soggetto_erogatore=?");
									sqlQueryObject.addWhereCondition(CostantiDB.PORTE_DELEGATE+".tipo_servizio=?");
									sqlQueryObject.addWhereCondition(CostantiDB.PORTE_DELEGATE+".nome_servizio=?");
									sqlQueryObject.addWhereCondition(CostantiDB.PORTE_DELEGATE+".versione_servizio=?");
									sqlQueryObject.addWhereCondition(CostantiDB.PORTE_DELEGATE+".nome_azione=?");
									sqlQueryObject.addWhereCondition(CostantiDB.PORTE_DELEGATE+".mode_azione=?");
									queryString = sqlQueryObject.createSQLQuery();
									stmt = con.prepareStatement(queryString);
									index = 1;
									stmt.setString(index++, mapping.getIdPortaDelegata().getNome());
									stmt.setString(index++, idServizio.getSoggettoErogatore().getTipo());
									stmt.setString(index++, idServizio.getSoggettoErogatore().getNome());
									stmt.setString(index++, idServizio.getTipo());
									stmt.setString(index++, idServizio.getNome());
									stmt.setInt(index++, idServizio.getVersione());
									stmt.setString(index++, idOperazione.getNome());
									stmt.setString(index++, ModalitaIdentificazioneAzione.STATIC.getValue());
									risultato = stmt.executeQuery();
									while (risultato.next()){
										String nome = risultato.getString("nome_porta");
										ResultPorta resultPorta = DBOggettiInUsoUtils.formatPortaDelegata(nome, tipoDB, con, normalizeObjectIds);
										if(resultPorta.mapping) {
											urlInvocazionePDMappingList.add(resultPorta.label);
										}
										else {
											urlInvocazionePDList.add(resultPorta.label);
										}
										isInUso = true;
									}
									risultato.close();
									stmt.close();
									
									
									// ** Controllo che non sia associato a policy di controllo del traffico o allarmi **
									
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
									
										sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
										sqlQueryObject.addFromTable(tabella);
										sqlQueryObject.addSelectField(identificativoColumn);
										sqlQueryObject.addSelectField(aliasColumn);
										sqlQueryObject.addSelectField("filtro_ruolo");
										sqlQueryObject.addSelectField("filtro_porta");
										sqlQueryObject.setANDLogicOperator(true);
										sqlQueryObject.addWhereCondition(tabella+".filtro_ruolo=?");
										sqlQueryObject.addWhereCondition(tabella+".filtro_porta=?");
										
										// condizione di controllo
										sqlQueryObjectOr = SQLObjectFactory.createSQLQueryObject(tipoDB);
										sqlQueryObjectOr.setANDLogicOperator(false);
										// (filtro_azione == 'NOME') OR (filtro_azione like 'NOME,%') OR (filtro_azione like '%,NOME') OR (filtro_azione like '%,applicabilita_azioni,%')
										/** CLOB sqlQueryObjectOr.addWhereCondition(tabella+".filtro_azione = ?");*/
										sqlQueryObjectOr.addWhereLikeCondition(tabella+".filtro_azione", idOperazione.getNome(), false , false);
										sqlQueryObjectOr.addWhereLikeCondition(tabella+".filtro_azione", idOperazione.getNome()+",", LikeConfig.startsWith(false));
										sqlQueryObjectOr.addWhereLikeCondition(tabella+".filtro_azione", ","+idOperazione.getNome(), LikeConfig.endsWith(false));
										sqlQueryObjectOr.addWhereLikeCondition(tabella+".filtro_azione", ","+idOperazione.getNome()+",", true , false);
										sqlQueryObject.addWhereCondition(sqlQueryObjectOr.createSQLConditions());
										
										sqlQueryObject.addOrderBy("filtro_ruolo");
										sqlQueryObject.addOrderBy("filtro_porta");
										queryString = sqlQueryObject.createSQLQuery();
										stmt = con.prepareStatement(queryString);
										stmt.setString(1, "delegata");
										stmt.setString(2, mapping.getIdPortaDelegata().getNome());
										/** CLOB stmt.setString(3, idRisorsa.getNome());*/
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
							}
						}
						
					}
				}
			}
				
			
				
				
			// Porte applicative
			if(config &&
				idServiziWithAccordo!=null && !idServiziWithAccordo.isEmpty()) {
				for (IDServizio idServizio : idServiziWithAccordo) {
					List<MappingErogazionePortaApplicativa> lPA = DBMappingUtils.mappingErogazionePortaApplicativaList(con, tipoDB, idServizio, true);
					if(lPA!=null && !lPA.isEmpty()) {
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
							stmt.setString(2, idOperazione.getNome());
							risultato = stmt.executeQuery();
							while (risultato.next()){
								String nome = risultato.getString("nome_porta");
								ResultPorta resultPorta = DBOggettiInUsoUtils.formatPortaApplicativa(nome, tipoDB, con, normalizeObjectIds);
								if(resultPorta.mapping) {
									mappingErogazionePAList.add(resultPorta.label);
								}
								else {
									porteApplicativeList.add(resultPorta.label);
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
							sqlQueryObjectOr = SQLObjectFactory.createSQLQueryObject(tipoDB);
							sqlQueryObjectOr.setANDLogicOperator(false);
							// (applicabilita_azioni == 'NOME') OR (applicabilita_azioni like 'NOME,%') OR (applicabilita_azioni like '%,NOME') OR (applicabilita_azioni like '%,applicabilita_azioni,%')
							/** CLOB sqlQueryObjectOr.addWhereCondition(CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI+".applicabilita_azioni = ?");*/
							sqlQueryObjectOr.addWhereLikeCondition(CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI+".applicabilita_azioni", idOperazione.getNome(), false , false);
							sqlQueryObjectOr.addWhereLikeCondition(CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI+".applicabilita_azioni", idOperazione.getNome()+",", LikeConfig.startsWith(false));
							sqlQueryObjectOr.addWhereLikeCondition(CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI+".applicabilita_azioni", ","+idOperazione.getNome(), LikeConfig.endsWith(false));
							sqlQueryObjectOr.addWhereLikeCondition(CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI+".applicabilita_azioni", ","+idOperazione.getNome()+",", true , false);
							sqlQueryObject.addWhereCondition(sqlQueryObjectOr.createSQLConditions());
							queryString = sqlQueryObject.createSQLQuery();
							stmt = con.prepareStatement(queryString);
							stmt.setString(1, mapping.getIdPortaApplicativa().getNome());
							/** CLOB stmt.setString(2, idRisorsa.getNome());*/
							risultato = stmt.executeQuery();
							while (risultato.next()){
								String nome = risultato.getString("nome_porta");
								ResultPorta resultPorta = DBOggettiInUsoUtils.formatPortaApplicativa(nome, tipoDB, con, normalizeObjectIds);
								if(resultPorta.mapping) {
									trasformazionePAMappingLis.add(resultPorta.label);
								}
								else {
									trasformazionePAList.add(resultPorta.label);
								}
								isInUso = true;
							}
							risultato.close();
							stmt.close();
							
							
							// ** url di invocazione **
							
							sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
							sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE);
							sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
							sqlQueryObject.addSelectField("nome_porta");
							sqlQueryObject.setANDLogicOperator(true);
							sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE+".nome_porta=?");
							sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE+".id_soggetto="+CostantiDB.SOGGETTI+".id");
							sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".tipo_soggetto=?");
							sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".nome_soggetto=?");
							sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE+".tipo_servizio=?");
							sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE+".servizio=?");
							sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE+".versione_servizio=?");
							sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE+".azione=?");
							sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE+".mode_azione=?");
							queryString = sqlQueryObject.createSQLQuery();
							stmt = con.prepareStatement(queryString);
							index = 1;
							stmt.setString(index++, mapping.getIdPortaApplicativa().getNome());
							stmt.setString(index++, idServizio.getSoggettoErogatore().getTipo());
							stmt.setString(index++, idServizio.getSoggettoErogatore().getNome());
							stmt.setString(index++, idServizio.getTipo());
							stmt.setString(index++, idServizio.getNome());
							stmt.setInt(index++, idServizio.getVersione());
							stmt.setString(index++, idOperazione.getNome());
							stmt.setString(index++, ModalitaIdentificazioneAzione.STATIC.getValue());
							risultato = stmt.executeQuery();
							while (risultato.next()){
								String nome = risultato.getString("nome_porta");
								ResultPorta resultPorta = DBOggettiInUsoUtils.formatPortaApplicativa(nome, tipoDB, con, normalizeObjectIds);
								if(resultPorta.mapping) {
									urlInvocazionePAMappingList.add(resultPorta.label);
								}
								else {
									urlInvocazionePAList.add(resultPorta.label);
								}
								isInUso = true;
							}
							risultato.close();
							stmt.close();
							
							
							// ** Controllo che non sia associato a policy di controllo del traffico o allarmi **
							
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
							
								sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
								sqlQueryObject.addFromTable(tabella);
								sqlQueryObject.addSelectField(identificativoColumn);
								sqlQueryObject.addSelectField(aliasColumn);
								sqlQueryObject.addSelectField("filtro_ruolo");
								sqlQueryObject.addSelectField("filtro_porta");
								sqlQueryObject.setANDLogicOperator(true);
								sqlQueryObject.addWhereCondition(tabella+".filtro_ruolo=?");
								sqlQueryObject.addWhereCondition(tabella+".filtro_porta=?");
								
								// condizione di controllo
								sqlQueryObjectOr = SQLObjectFactory.createSQLQueryObject(tipoDB);
								sqlQueryObjectOr.setANDLogicOperator(false);
								// (filtro_azione == 'NOME') OR (filtro_azione like 'NOME,%') OR (filtro_azione like '%,NOME') OR (filtro_azione like '%,applicabilita_azioni,%')
								/** CLOB sqlQueryObjectOr.addWhereCondition(tabella+".filtro_azione = ?");*/
								sqlQueryObjectOr.addWhereLikeCondition(tabella+".filtro_azione", idOperazione.getNome(), false , false);
								sqlQueryObjectOr.addWhereLikeCondition(tabella+".filtro_azione", idOperazione.getNome()+",", LikeConfig.startsWith(false));
								sqlQueryObjectOr.addWhereLikeCondition(tabella+".filtro_azione", ","+idOperazione.getNome(), LikeConfig.endsWith(false));
								sqlQueryObjectOr.addWhereLikeCondition(tabella+".filtro_azione", ","+idOperazione.getNome()+",", true , false);
								sqlQueryObject.addWhereCondition(sqlQueryObjectOr.createSQLConditions());
								
								sqlQueryObject.addOrderBy("filtro_ruolo");
								sqlQueryObject.addOrderBy("filtro_porta");
								queryString = sqlQueryObject.createSQLQuery();
								stmt = con.prepareStatement(queryString);
								stmt.setString(1, "applicativa");
								stmt.setString(2, mapping.getIdPortaApplicativa().getNome());
								/** CLOB stmt.setString(3, idRisorsa.getNome());*/
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
										if("applicativa".equals(filtroRuolo)) {
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
					}
				}
			}
			
						
			
			
			
			// ** Controllo che non sia associato a policy di controllo del traffico o allarmi generali **
			
			long idAccordoServizioParteComune = DBUtils.getIdAccordoServizioParteComune(idOperazione.getIdPortType().getIdAccordo(), con, tipoDB);
			if(idAccordoServizioParteComune<=0){
				throw new UtilsException("Accordi di Servizio Parte Comune con id ["+idOperazione.getIdPortType().getIdAccordo().toString()+"] non trovato");
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
					
						sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
						sqlQueryObject.addFromTable(tabella);
						sqlQueryObject.setSelectDistinct(true);
						sqlQueryObject.addSelectField(identificativoColumn);
						sqlQueryObject.addSelectField(aliasColumn);
						sqlQueryObject.addSelectField("filtro_ruolo");
						sqlQueryObject.addSelectField("filtro_porta");
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
						sqlQueryObjectOr = SQLObjectFactory.createSQLQueryObject(tipoDB);
						sqlQueryObjectOr.setANDLogicOperator(false);
						// (filtro_azione == 'NOME') OR (filtro_azione like 'NOME,%') OR (filtro_azione like '%,NOME') OR (filtro_azione like '%,applicabilita_azioni,%')
						/** CLOB sqlQueryObjectOr.addWhereCondition(tabella+".filtro_azione = ?");*/
						sqlQueryObjectOr.addWhereLikeCondition(tabella+".filtro_azione", idOperazione.getNome(), false , false);
						sqlQueryObjectOr.addWhereLikeCondition(tabella+".filtro_azione", idOperazione.getNome()+",", LikeConfig.startsWith(false));
						sqlQueryObjectOr.addWhereLikeCondition(tabella+".filtro_azione", ","+idOperazione.getNome(), LikeConfig.endsWith(false));
						sqlQueryObjectOr.addWhereLikeCondition(tabella+".filtro_azione", ","+idOperazione.getNome()+",", true , false);
						sqlQueryObject.addWhereCondition(sqlQueryObjectOr.createSQLConditions());
						
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
						stmt.setString(index++, idServizio.getTipo());
						stmt.setString(index++, idServizio.getNome());
						stmt.setInt(index++, idServizio.getVersione());
						/** CLOB stmt.setString(index++, idRisorsa.getNome());*/
						risultato = stmt.executeQuery();
						while (risultato.next()) {
							
							String alias = risultato.getString(aliasColumn);
							if(alias== null || "".equals(alias)) {
								alias = risultato.getString(identificativoColumn);
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
			
			
			
			
			
			
			
			// ** Controllo che non sia riferito dalla configurazione SignalHub **
			if(registry && Costanti.MODIPA_PROTOCOL_NAME.equals(idOperazione.getIdPortType().getIdAccordo().getSoggettoReferente().getTipo()) &&
				idServiziWithAccordo!=null && !idServiziWithAccordo.isEmpty()) {
				for (IDServizio idServizio : idServiziWithAccordo) {
									
					long idS = DBUtils.getIdServizio(idServizio.getNome(), idServizio.getTipo(), idServizio.getVersione(),
							idServizio.getSoggettoErogatore().getNome(), idServizio.getSoggettoErogatore().getTipo(),
							con, tipoDB);
					if(idS<=0) {
						throw new CoreException("Servizio '"+idServizio+"' non esistente");
					}
						
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
					sqlQueryObject.addFromTable(CostantiDB.SERVIZI);
					sqlQueryObject.addFromTable(CostantiDB.PROTOCOL_PROPERTIES);
					sqlQueryObject.addSelectAliasField(CostantiDB.SERVIZI,"id","idServizio");
					sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI + ".id = ?");
					sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI + ".id = " + CostantiDB.PROTOCOL_PROPERTIES + ".id_proprietario");
					sqlQueryObject.addWhereCondition(CostantiDB.PROTOCOL_PROPERTIES + ".tipo_proprietario=?");
					sqlQueryObject.addWhereCondition(CostantiDB.PROTOCOL_PROPERTIES + ".name=?");
					sqlQueryObject.addWhereCondition(CostantiDB.PROTOCOL_PROPERTIES + ".value_string=?");
					sqlQueryObject.setANDLogicOperator(true);
					queryString = sqlQueryObject.createSQLQuery();
					stmt = con.prepareStatement(queryString);
					index = 1;
					stmt.setLong(index++, idS);
					stmt.setString(index++, ProprietariProtocolProperty.ACCORDO_SERVIZIO_PARTE_SPECIFICA.name());
					stmt.setString(index++, Costanti.MODIPA_API_IMPL_INFO_SIGNAL_HUB_OPERATION_ID);
					stmt.setString(index++, idOperazione.getNome());
					risultato = stmt.executeQuery();
					if(risultato.next()) {
						
						// trovata
						IDPortaApplicativa idPA = DBMappingUtils.getIDPortaApplicativaAssociataDefault(idServizio, con, tipoDB);
						if(idPA!=null) {
							ResultPorta resultPorta = DBOggettiInUsoUtils.formatPortaApplicativa(idPA.getNome(), tipoDB, con, normalizeObjectIds);
							modiSignalHubList.add(resultPorta.label);
						}
						else {
							modiSignalHubList.add(idServizio.toString());
						}
						
						isInUso = true;
						
					}
				}
			}
			
			
			
			
			
			return isInUso;

		} catch (Exception se) {
			throw new UtilsException("[DBOggettiInUsoUtils::" + nomeMetodo + "] Exception: " + se.getMessage(),se);
		} finally {
			// Chiudo statement and resultset
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


	protected static String toString(IDPortTypeAzione idOperazione, Map<ErrorsHandlerCostant, List<String>> whereIsInUso, boolean prefix, String separator){
		return toString(idOperazione, whereIsInUso, prefix, separator," non eliminabile perch&egrave; :");
	}
	protected static String toString(IDPortTypeAzione idOperazione, Map<ErrorsHandlerCostant, List<String>> whereIsInUso, boolean prefix, String separator, String intestazione){
		Set<ErrorsHandlerCostant> keys = whereIsInUso.keySet();
		StringBuilder msgBuilder = new StringBuilder("Azione '"+idOperazione.getNome()+"'" + intestazione+separator);
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

			case IS_CORRELATA:
				if ( messages!=null && !messages.isEmpty()) {
					msgBuilder.append("correlata ad altre azioni: " + DBOggettiInUsoUtils.formatList(messages,separator) + separator);
				}
				break;
			
			case IN_USO_IN_PORTE_APPLICATIVE:
				if ( messages!=null && !messages.isEmpty()) {
					msgBuilder.append("appartenente ad un gruppo differente da quello predefinito nelle Porte Inbound: " + DBOggettiInUsoUtils.formatList(messages,separator) + separator);
				}
				break;
			case IN_USO_IN_PORTE_DELEGATE:
				if ( messages!=null && !messages.isEmpty()) {
					msgBuilder.append("appartenente ad un gruppo differente da quello predefinito nelle Porte Outbound: " + DBOggettiInUsoUtils.formatList(messages,separator) + separator);
				}
				break;
			case IN_USO_IN_MAPPING_EROGAZIONE_PA:
				if ( messages!=null && !messages.isEmpty()) {
					msgBuilder.append("appartenente ad un gruppo differente da quello predefinito nelle Erogazioni: " + DBOggettiInUsoUtils.formatList(messages,separator) + separator);
				}
				break;
			case IN_USO_IN_MAPPING_FRUIZIONE_PD:
				if ( messages!=null && !messages.isEmpty()) {
					msgBuilder.append("appartenente ad un gruppo differente da quello predefinito nelle Fruizioni: " + DBOggettiInUsoUtils.formatList(messages,separator) + separator);
				}
				break;
				
			case TRASFORMAZIONE_MAPPING_PD:
				if ( messages!=null && !messages.isEmpty()) {
					msgBuilder.append("utilizzata nel criterio di applicabilità della Trasformazione (Risorse) per le Fruizioni: " + DBOggettiInUsoUtils.formatList(messages,separator) + separator);
				}
				break;
			case TRASFORMAZIONE_PD:
				if ( messages!=null && !messages.isEmpty()) {
					msgBuilder.append("utilizzata nelle Porte Outbound (Criterio di applicabilità della Trasformazione - Risorse): " + DBOggettiInUsoUtils.formatList(messages,separator) + separator);
				}
				break;
			case TRASFORMAZIONE_MAPPING_PA:
				if ( messages!=null && !messages.isEmpty()) {
					msgBuilder.append("utilizzata nel criterio di applicabilità della Trasformazione (Risorse) per le Erogazioni: " + DBOggettiInUsoUtils.formatList(messages,separator) + separator);
				}
				break;
			case TRASFORMAZIONE_PA:
				if ( messages!=null && !messages.isEmpty()) {
					msgBuilder.append("utilizzata nelle Porte Inbound (Criterio di applicabilità della Trasformazione - Risorse): " + DBOggettiInUsoUtils.formatList(messages,separator) + separator);
				}
				break;
				
			case URLINVOCAZIONE_MAPPING_PD:
				if ( messages!=null && !messages.isEmpty()) {
					msgBuilder.append("utilizzata nella configurazione dell'Url di Invocazione per le Fruizioni: " + DBOggettiInUsoUtils.formatList(messages,separator) + separator);
				}
				break;
			case URLINVOCAZIONE_PD:
				if ( messages!=null && !messages.isEmpty()) {
					msgBuilder.append("utilizzata nelle Porte Outbound (Azione statica): " + DBOggettiInUsoUtils.formatList(messages,separator) + separator);
				}
				break;
			case URLINVOCAZIONE_MAPPING_PA:
				if ( messages!=null && !messages.isEmpty()) {
					msgBuilder.append("utilizzata nella configurazione dell'Url di Invocazione per le Erogazioni: " + DBOggettiInUsoUtils.formatList(messages,separator) + separator);
				}
				break;
			case URLINVOCAZIONE_PA:
				if ( messages!=null && !messages.isEmpty()) {
					msgBuilder.append("utilizzata nelle Porte Inbound (Azione statica): " + DBOggettiInUsoUtils.formatList(messages,separator) + separator);
				}
				break;
				
			case CONTROLLO_TRAFFICO:
				if ( messages!=null && !messages.isEmpty() ) {
					msgBuilder.append("utilizzata in Policy di Rate Limiting: " + DBOggettiInUsoUtils.formatList(messages,separator) + separator);
				}
				break;
			case ALLARMI:
				if ( messages!=null && !messages.isEmpty() ) {
					msgBuilder.append("utilizzato in Allarmi: " + DBOggettiInUsoUtils.formatList(messages,separator) + separator);
				}
				break;
				
			case IS_RIFERITA_MODI_SIGNAL_HUB:
				if ( messages!=null && !messages.isEmpty() ) {
					msgBuilder.append("riferito nella configurazione 'SignalHub' dell'erogazione: " + DBOggettiInUsoUtils.formatList(messages,separator) + separator);
				}
				break;				
				
			default:
				msgBuilder.append("utilizzata in oggetto non codificato ("+key+")"+separator);
				break;
			}
			
		}// chiudo for

		return msgBuilder.toString();
	}
	
}
