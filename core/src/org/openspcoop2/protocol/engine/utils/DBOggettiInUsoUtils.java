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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openspcoop2.core.commons.ErrorsHandlerCostant;
import org.openspcoop2.core.config.CanaleConfigurazione;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.controllo_traffico.IdPolicy;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDAccordoCooperazione;
import org.openspcoop2.core.id.IDGenericProperties;
import org.openspcoop2.core.id.IDGruppo;
import org.openspcoop2.core.id.IDPortType;
import org.openspcoop2.core.id.IDPortTypeAzione;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDResource;
import org.openspcoop2.core.id.IDRuolo;
import org.openspcoop2.core.id.IDScope;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.mapping.DBMappingUtils;
import org.openspcoop2.core.mapping.MappingErogazionePortaApplicativa;
import org.openspcoop2.core.mapping.MappingFruizionePortaDelegata;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.engine.constants.Costanti;
import org.openspcoop2.protocol.engine.constants.ModalitaIdentificazioneAzione;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.SQLObjectFactory;

/**
 * DBOggettiInUsoUtils
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class DBOggettiInUsoUtils  {

	
	protected static String getProtocolPrefix(String protocollo) throws UtilsException {
		try {
			return "["+NamingUtils.getLabelProtocollo(protocollo)+"] ";
		} catch (Exception se) {
			throw new UtilsException(se.getMessage(),se);
		}
	}
	protected static String getSubjectSuffix(String protocollo, IDSoggetto idSoggetto)throws UtilsException {
		try {
			return " ("+NamingUtils.getLabelSoggetto(protocollo, idSoggetto)+")";
		} catch (Exception se) {
			throw new UtilsException(se.getMessage(),se);
		}
	}
	public static String formatList(List<String> whereIsInUso, String separator) {
		StringBuilder sb = new StringBuilder();
		for (String v : whereIsInUso) {
			sb.append(separator);
			sb.append("- ");
			sb.append(v);
		}
		return sb.toString();
	}
	protected static ResultPorta formatPortaDelegata(String nomePorta, String tipoDB, Connection con, boolean normalizeObjectIds) throws Exception {
		
		PreparedStatement stmt = null;
		ResultSet risultato = null;
		String queryString;

		try {
		
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE);
			sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
			sqlQueryObject.addSelectField("tipo_soggetto");
			sqlQueryObject.addSelectField("nome_soggetto");
			sqlQueryObject.addSelectField(CostantiDB.PORTE_DELEGATE+".nome_porta");
			sqlQueryObject.addSelectField(CostantiDB.PORTE_DELEGATE+".tipo_soggetto_erogatore");
			sqlQueryObject.addSelectField(CostantiDB.PORTE_DELEGATE+".nome_soggetto_erogatore");
			sqlQueryObject.addSelectField(CostantiDB.PORTE_DELEGATE+".tipo_servizio");
			sqlQueryObject.addSelectField(CostantiDB.PORTE_DELEGATE+".nome_servizio");
			sqlQueryObject.addSelectField(CostantiDB.PORTE_DELEGATE+".versione_servizio");
			sqlQueryObject.setANDLogicOperator(true);
			sqlQueryObject.setSelectDistinct(true);
			sqlQueryObject.addWhereCondition(CostantiDB.PORTE_DELEGATE+".nome_porta = ?");
			sqlQueryObject.addWhereCondition(CostantiDB.PORTE_DELEGATE+".id_soggetto = "+CostantiDB.SOGGETTI+".id");
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setString(1, nomePorta);
			risultato = stmt.executeQuery();
			if (risultato.next()) {
				String tipo_soggetto = risultato.getString("tipo_soggetto");
				String nome_soggetto = risultato.getString("nome_soggetto");
				String nome = risultato.getString("nome_porta");
				IDSoggetto idSoggetto = new IDSoggetto(tipo_soggetto, nome_soggetto);
				if(normalizeObjectIds) {
					
					String tipoSoggettoErogatore = risultato.getString("tipo_soggetto_erogatore");
					String nomeSoggettoErogatore = risultato.getString("nome_soggetto_erogatore");
					IDSoggetto idSoggettoErogatore = null;
					if(tipoSoggettoErogatore!=null && !"".equals(tipoSoggettoErogatore) &&
							nomeSoggettoErogatore!=null && !"".equals(nomeSoggettoErogatore)) {
						idSoggettoErogatore = new IDSoggetto(tipoSoggettoErogatore, nomeSoggettoErogatore);
					}
					
					String tipoServizio = risultato.getString("tipo_servizio");
					String nomeServizio = risultato.getString("nome_servizio");
					Integer versioneServizio = risultato.getInt("versione_servizio");
					
					String protocollo = ProtocolFactoryManager.getInstance().getProtocolByOrganizationType(tipo_soggetto);
					
					MappingFruizionePortaDelegata mappingPD = null;
					if(tipoServizio!=null && !"".equals(tipoServizio) && 
							nomeServizio!=null && !"".equals(nomeServizio) &&
									versioneServizio!=null && versioneServizio>0) {
						IDPortaDelegata idPD = new IDPortaDelegata();
						idPD.setNome(nome);
						IDServizio idServizio = IDServizioFactory.getInstance().getIDServizioFromValues(tipoServizio, nomeServizio, idSoggettoErogatore, versioneServizio);
						boolean existsMapping = DBMappingUtils.existsMappingFruizione(idServizio, idSoggetto, idPD, con, tipoDB);
						if(existsMapping) {
							mappingPD = DBMappingUtils.getMappingFruizione(idServizio, idSoggetto, idPD, con, tipoDB);
						}
					}
					if(mappingPD!=null) {
						String suffixGruppo = "";
						if(!mappingPD.isDefault()) {
							suffixGruppo = " (Gruppo: "+mappingPD.getDescrizione()+")";
						}
						ResultPorta result = new ResultPorta();
						result.label=getProtocolPrefix(protocollo)+
								NamingUtils.getLabelAccordoServizioParteSpecifica(protocollo, mappingPD.getIdServizio())+
								" (Fruitore:"+NamingUtils.getLabelSoggetto(protocollo, idSoggetto)+")"+
								suffixGruppo;
						result.mapping=true;
						return result;
					}
					else {
						ResultPorta result = new ResultPorta();
						result.label=getProtocolPrefix(protocollo)+nome+getSubjectSuffix(protocollo, idSoggetto);
						return result;
					}
				}
				else {
					ResultPorta result = new ResultPorta();
					result.label=tipo_soggetto + "/" + nome_soggetto+"_"+nome;
					return result;
				}
				
			}
			
			throw new Exception("Porta Delegata '"+nomePorta+"' not found");
			
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
	protected static ResultPorta formatPortaApplicativa(String nomePorta, String tipoDB, Connection con, boolean normalizeObjectIds) throws Exception {
		PreparedStatement stmt = null;
		ResultSet risultato = null;
		String queryString;

		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE);
			sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
			sqlQueryObject.addSelectField("tipo_soggetto");
			sqlQueryObject.addSelectField("nome_soggetto");
			sqlQueryObject.addSelectField(CostantiDB.PORTE_APPLICATIVE+".nome_porta");
			sqlQueryObject.addSelectField(CostantiDB.PORTE_APPLICATIVE+".tipo_servizio");
			sqlQueryObject.addSelectField(CostantiDB.PORTE_APPLICATIVE+".servizio");
			sqlQueryObject.addSelectField(CostantiDB.PORTE_APPLICATIVE+".versione_servizio");
			sqlQueryObject.setANDLogicOperator(true);
			sqlQueryObject.setSelectDistinct(true);
			sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE+".nome_porta = ?");
			sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE+".id_soggetto = "+CostantiDB.SOGGETTI+".id");
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setString(1, nomePorta);
			risultato = stmt.executeQuery();
			if (risultato.next()) {
				String tipo_soggetto = risultato.getString("tipo_soggetto");
				String nome_soggetto = risultato.getString("nome_soggetto");
				String nome = risultato.getString("nome_porta");
				IDSoggetto idSoggetto = new IDSoggetto(tipo_soggetto, nome_soggetto);
				if(normalizeObjectIds) {
					
					String tipoServizio = risultato.getString("tipo_servizio");
					String nomeServizio = risultato.getString("servizio");
					Integer versioneServizio = risultato.getInt("versione_servizio");
					
					String protocollo = ProtocolFactoryManager.getInstance().getProtocolByOrganizationType(tipo_soggetto);
					
					MappingErogazionePortaApplicativa mappingPA = null;
					if(tipoServizio!=null && !"".equals(tipoServizio) && 
							nomeServizio!=null && !"".equals(nomeServizio) &&
									versioneServizio!=null && versioneServizio>0) {
						IDPortaApplicativa idPA = new IDPortaApplicativa();
						idPA.setNome(nome);
						IDServizio idServizio = IDServizioFactory.getInstance().getIDServizioFromValues(tipoServizio, nomeServizio, idSoggetto, versioneServizio);
						boolean existsMapping = DBMappingUtils.existsMappingErogazione(idServizio, idPA, con, tipoDB);
						if(existsMapping) {
							mappingPA = DBMappingUtils.getMappingErogazione(idServizio, idPA, con, tipoDB);
						}
					}
					if(mappingPA!=null) {
						String suffixGruppo = "";
						if(!mappingPA.isDefault()) {
							suffixGruppo = " (Gruppo: "+mappingPA.getDescrizione()+")";
						}
						ResultPorta result = new ResultPorta();
						result.label=getProtocolPrefix(protocollo)+
								NamingUtils.getLabelAccordoServizioParteSpecifica(protocollo, mappingPA.getIdServizio())+
								suffixGruppo;
						result.mapping = true;
						result.erogazioneModi = Costanti.MODIPA_PROTOCOL_NAME.equals(protocollo);
						return result;
					}
					else {
						ResultPorta result = new ResultPorta();
						result.label=getProtocolPrefix(protocollo)+nome+getSubjectSuffix(protocollo, idSoggetto);
						result.erogazioneModi = Costanti.MODIPA_PROTOCOL_NAME.equals(protocollo);
						return result;
					}
					
				}
				else {
					ResultPorta result = new ResultPorta();
					result.label=tipo_soggetto + "/" + nome_soggetto+"_"+nome;
					
					if(tipo_soggetto!=null) {
						String protocollo = ProtocolFactoryManager.getInstance().getProtocolByOrganizationType(tipo_soggetto);
						result.erogazioneModi = Costanti.MODIPA_PROTOCOL_NAME.equals(protocollo);
					}
					
					return result;
				}
				
			}
			
			throw new Exception("Porta Delegata '"+nomePorta+"' not found");
			
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

	public static void formatConnettori(List<Long> idConnettori,Map<ErrorsHandlerCostant, List<String>> whereIsInUso, 
			Connection con, boolean normalizeObjectIds, String tipoDB) throws Exception {
		if(idConnettori!=null && !idConnettori.isEmpty()) {
			
			List<String> connettorePA_list = whereIsInUso.get(ErrorsHandlerCostant.CONNETTORE_PA);
			List<String> connettorePD_list = whereIsInUso.get(ErrorsHandlerCostant.CONNETTORE_PD);
			List<String> mapping_connettorePA_list = whereIsInUso.get(ErrorsHandlerCostant.CONNETTORE_MAPPING_PA);
			List<String> mapping_connettorePD_list = whereIsInUso.get(ErrorsHandlerCostant.CONNETTORE_MAPPING_PD);
			
			if (connettorePA_list == null) {
				connettorePA_list = new ArrayList<String>();
				whereIsInUso.put(ErrorsHandlerCostant.CONNETTORE_PA, connettorePA_list);
			}
			if (connettorePD_list == null) {
				connettorePD_list = new ArrayList<String>();
				whereIsInUso.put(ErrorsHandlerCostant.CONNETTORE_PD, connettorePD_list);
			}
			
			if (mapping_connettorePA_list == null) {
				mapping_connettorePA_list = new ArrayList<String>();
				whereIsInUso.put(ErrorsHandlerCostant.CONNETTORE_MAPPING_PA, mapping_connettorePA_list);
			}
			if (mapping_connettorePD_list == null) {
				mapping_connettorePD_list = new ArrayList<String>();
				whereIsInUso.put(ErrorsHandlerCostant.CONNETTORE_MAPPING_PD, mapping_connettorePD_list);
			}
			
			List<Long> idFruizioniDefault = new ArrayList<>();
			
			List<Long> idFruizioniGruppi_idFruizione = new ArrayList<>();
			List<Long> idFruizioniGruppi_id = new ArrayList<>();
			Map<Long,List<String>> idFruizioniGruppi_azioni = new HashMap<Long, List<String>>();
			
			List<Long> idServiziApplicativi = new ArrayList<>();
			List<String> idServiziApplicativi_server = new ArrayList<>();
			List<String> idServiziApplicativi_nome = new ArrayList<>();
			
			PreparedStatement stmt = null;
			ResultSet risultato = null;
			String queryString;

			try {
								
				for (Long idConnettore : idConnettori) {
					
					
					// Controllo che il connettore non sia in uso nelle porte applicative sia di default che dei gruppi
					
					ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
					sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
					sqlQueryObject.addSelectField("id");
					sqlQueryObject.addSelectField("tipo");
					sqlQueryObject.addSelectField("nome");
					sqlQueryObject.setANDLogicOperator(true);
					sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".id_connettore_inv=?");
					queryString = sqlQueryObject.createSQLQuery();
					stmt = con.prepareStatement(queryString);
					stmt.setLong(1, idConnettore);
					risultato = stmt.executeQuery();
					while (risultato.next()) {
						Long id = risultato.getLong("id");
						String tipo = risultato.getString("tipo");
						String nome = risultato.getString("nome");
						idServiziApplicativi.add(id);
						idServiziApplicativi_server.add(tipo);
						idServiziApplicativi_nome.add(nome);
					}
					risultato.close();
					stmt.close();
					
					
					// Controllo che il connettore non sia in uso nelle porte delegate di default
					
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
					sqlQueryObject.addFromTable(CostantiDB.SERVIZI_FRUITORI);
					sqlQueryObject.addSelectField("id");
					sqlQueryObject.setANDLogicOperator(true);
					sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_FRUITORI+".id_connettore=?");
					queryString = sqlQueryObject.createSQLQuery();
					stmt = con.prepareStatement(queryString);
					stmt.setLong(1, idConnettore);
					risultato = stmt.executeQuery();
					while (risultato.next()) {
						Long id = risultato.getLong("id");
						idFruizioniDefault.add(id);
					}
					risultato.close();
					stmt.close();
					
					
					// Controllo che il connettore non sia in uso nelle porte delegate associate ai gruppi
					
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
					sqlQueryObject.addFromTable(CostantiDB.SERVIZI_FRUITORI_AZIONI);
					sqlQueryObject.addSelectField("id_fruizione");
					sqlQueryObject.addSelectField("id");
					sqlQueryObject.setANDLogicOperator(true);
					sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_FRUITORI_AZIONI+".id_connettore=?");
					queryString = sqlQueryObject.createSQLQuery();
					stmt = con.prepareStatement(queryString);
					stmt.setLong(1, idConnettore);
					risultato = stmt.executeQuery();
					while (risultato.next()) {
						Long idF = risultato.getLong("id_fruizione");
						idFruizioniGruppi_idFruizione.add(idF);
						
						Long id = risultato.getLong("id");
						idFruizioniGruppi_id.add(id);
					}
					risultato.close();
					stmt.close();
					
					if(!idFruizioniGruppi_id.isEmpty()) {
						for (int i = 0; i < idFruizioniGruppi_id.size(); i++) {
							
							Long id = idFruizioniGruppi_id.get(0);
							Long idF = idFruizioniGruppi_idFruizione.get(0);
							
							sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
							sqlQueryObject.addFromTable(CostantiDB.SERVIZI_FRUITORI_AZIONE);
							sqlQueryObject.addFromTable(CostantiDB.SERVIZI_FRUITORI_AZIONI);
							sqlQueryObject.addSelectField("nome_azione");
							sqlQueryObject.setANDLogicOperator(true);
							sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_FRUITORI_AZIONI+".id=?");
							sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_FRUITORI_AZIONE+".id_fruizione_azioni="+CostantiDB.SERVIZI_FRUITORI_AZIONI+".id");
							queryString = sqlQueryObject.createSQLQuery();
							stmt = con.prepareStatement(queryString);
							stmt.setLong(1, id);
							risultato = stmt.executeQuery();
							List<String> azioni = new ArrayList<String>();
							while (risultato.next()) {
								String nomeAzione = risultato.getString("nome_azione");
								azioni.add(nomeAzione);
							}
							risultato.close();
							stmt.close();
							
							idFruizioniGruppi_azioni.put(idF, azioni);
						}
					}
					
				}
				
				
				
				// Se ho rilevato che il connettore è in uso nelle porte applicative (sia di default che dei gruppi) traduco questa informazione
				
				if(idServiziApplicativi!=null && !idServiziApplicativi.isEmpty()) {
					for (int i = 0; i < idServiziApplicativi.size(); i++) {
						Long idServiziApplicativo = idServiziApplicativi.get(i);
						String suffix = "";
						String tipoApplicativo = idServiziApplicativi_server.get(i);
						if(CostantiConfigurazione.SERVER.equals(tipoApplicativo)) {
							suffix = " (Applicativo Server: "+idServiziApplicativi_nome.get(i)+")";
						}
						
						ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
						sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_SA);
						sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE);
						sqlQueryObject.setSelectDistinct(true);
						sqlQueryObject.addSelectField("nome_porta");
						sqlQueryObject.addSelectField("connettore_nome");
						sqlQueryObject.setANDLogicOperator(true);
						sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE_SA+".id_servizio_applicativo=?");
						sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE+".id="+CostantiDB.PORTE_APPLICATIVE_SA+".id_porta");
						queryString = sqlQueryObject.createSQLQuery();
						stmt = con.prepareStatement(queryString);
						stmt.setLong(1, idServiziApplicativo);
						risultato = stmt.executeQuery();
						while (risultato.next()) {
							String nomePorta = risultato.getString("nome_porta");
							
							String nomeConnettore = risultato.getString("connettore_nome");
							String labelNomeConnettore = "";
							if(nomeConnettore!=null && !"".equals(nomeConnettore)) {
								labelNomeConnettore = " (Connettore: "+nomeConnettore+")";
							}
							
							ResultPorta resultPorta = formatPortaApplicativa(nomePorta, tipoDB, con, normalizeObjectIds);
							if(resultPorta.mapping) {
								mapping_connettorePA_list.add(resultPorta.label+labelNomeConnettore+suffix);
							}
							else {
								connettorePA_list.add(resultPorta.label+labelNomeConnettore+suffix);
							}
						}
						risultato.close();
						stmt.close();
						
					}
				}
				
				
				
				// Se ho rilevato che il connettore è in uso nelle porte delegate di default traduco questa informazione
				
				if(idFruizioniDefault!=null && !idFruizioniDefault.isEmpty()) {
					
					for (Long idFruizione : idFruizioniDefault) {
						
						ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
						sqlQueryObject.addFromTable(CostantiDB.MAPPING_FRUIZIONE_PD);
						sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE);
						sqlQueryObject.setSelectDistinct(true);
						sqlQueryObject.addSelectField("nome_porta");
						sqlQueryObject.setANDLogicOperator(true);
						sqlQueryObject.addWhereCondition(CostantiDB.MAPPING_FRUIZIONE_PD+".id_fruizione=?");
						sqlQueryObject.addWhereCondition(CostantiDB.MAPPING_FRUIZIONE_PD+".id_porta="+CostantiDB.PORTE_DELEGATE+".id");
						sqlQueryObject.addWhereCondition(CostantiDB.MAPPING_FRUIZIONE_PD+".is_default=?");
						queryString = sqlQueryObject.createSQLQuery();
						stmt = con.prepareStatement(queryString);
						stmt.setLong(1, idFruizione);
						stmt.setInt(2, CostantiDB.TRUE);
						risultato = stmt.executeQuery();
						while (risultato.next()) {
							String nomePorta = risultato.getString("nome_porta");
							ResultPorta resultPorta = formatPortaDelegata(nomePorta, tipoDB, con, normalizeObjectIds);
							if(resultPorta.mapping) {
								mapping_connettorePD_list.add(resultPorta.label);
							}
							else {
								connettorePD_list.add(resultPorta.label);
							}
						}
						risultato.close();
						stmt.close();
						
					}
					
				}
			
				
				
				// Se ho rilevato che il token è in uso nelle porte delegate di qualche gruppo traduco questa informazione
				
				if(idFruizioniGruppi_idFruizione!=null && !idFruizioniGruppi_idFruizione.isEmpty()) {
					
					for (Long idFruizione : idFruizioniGruppi_idFruizione) {
						
						ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
						sqlQueryObject.addFromTable(CostantiDB.MAPPING_FRUIZIONE_PD);
						sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE);
						sqlQueryObject.setSelectDistinct(true);
						sqlQueryObject.addSelectField("nome_porta");
						sqlQueryObject.setANDLogicOperator(true);
						sqlQueryObject.addWhereCondition(CostantiDB.MAPPING_FRUIZIONE_PD+".id_fruizione=?");
						sqlQueryObject.addWhereCondition(CostantiDB.MAPPING_FRUIZIONE_PD+".id_porta="+CostantiDB.PORTE_DELEGATE+".id");
						sqlQueryObject.addWhereCondition(CostantiDB.MAPPING_FRUIZIONE_PD+".is_default=?");
						sqlQueryObject.addWhereCondition(CostantiDB.PORTE_DELEGATE+".mode_azione=?");
						
						List<String> azioni = idFruizioniGruppi_azioni.get(idFruizione);
						if(azioni!=null && !azioni.isEmpty()) {
							for (@SuppressWarnings("unused") String azione : azioni) {
								ISQLQueryObject sqlQueryObjectAzione = SQLObjectFactory.createSQLQueryObject(tipoDB);
								sqlQueryObjectAzione.addFromTable(CostantiDB.PORTE_DELEGATE_AZIONI);
								sqlQueryObjectAzione.addSelectField(CostantiDB.PORTE_DELEGATE_AZIONI+".azione");
								sqlQueryObjectAzione.setANDLogicOperator(true);
								sqlQueryObjectAzione.addWhereCondition(CostantiDB.PORTE_DELEGATE_AZIONI+".id_porta="+CostantiDB.PORTE_DELEGATE+".id");
								sqlQueryObjectAzione.addWhereCondition(CostantiDB.PORTE_DELEGATE_AZIONI+".azione=?");
								sqlQueryObject.addWhereExistsCondition(false, sqlQueryObjectAzione);
							}
						}
						
						queryString = sqlQueryObject.createSQLQuery();
						stmt = con.prepareStatement(queryString);
						int index = 1;
						stmt.setLong(index++, idFruizione);
						stmt.setInt(index++, CostantiDB.FALSE);
						stmt.setString(index++, ModalitaIdentificazioneAzione.DELEGATED_BY.getValue());
						if(azioni!=null && !azioni.isEmpty()) {
							for (String azione : azioni) {
								stmt.setString(index++, azione);
							}
						}
						risultato = stmt.executeQuery();
						while (risultato.next()) {
							String nomePorta = risultato.getString("nome_porta");
							ResultPorta resultPorta = formatPortaDelegata(nomePorta, tipoDB, con, normalizeObjectIds);
							if(resultPorta.mapping) {
								mapping_connettorePD_list.add(resultPorta.label);
							}
							else {
								connettorePD_list.add(resultPorta.label);
							}
						}
						risultato.close();
						stmt.close();
						
					}
					
				}
			}
			finally {
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
	}

	public static String formatGenericProperties(String tipologia, String nome) {
		
		/*
		String template = null;
		if(CostantiConfigurazione.GENERIC_PROPERTIES_TOKEN_TIPOLOGIA_VALIDATION.equals(tipologia)) {
			template = "Token Policy Validazione [{0}]";
		}
		else if(CostantiConfigurazione.GENERIC_PROPERTIES_TOKEN_TIPOLOGIA_RETRIEVE.equals(tipologia)) {
			template = "Token Policy Negoziazione [{0}]";
		}
		else {
			template = "Attribute Authority [{0}]";
		}
		return MessageFormat.format(template, nome);
		*/
		return nome;
		
	}
	
	
	
	
	

	// ***** PDD ******

	public static boolean isPddInUso(Connection con, String tipoDB, String nomePdd, List<String> whereIsInUso, boolean normalizeObjectIds) throws UtilsException {
		return DBOggettiInUsoUtils_pdd.isPddInUso(con, tipoDB, nomePdd, whereIsInUso, normalizeObjectIds);
	}

	public static String toString(String nomePdd, List<String> whereIsInUso, boolean prefix, String separator){
		return DBOggettiInUsoUtils_pdd.toString(nomePdd, whereIsInUso, prefix, separator);
	}
	
	
	
	
	// ***** GRUPPI ******

	// Lascio i metodi se servissero in futuro
	public static boolean isGruppoConfigInUso(Connection con, String tipoDB, IDGruppo idGruppo, Map<ErrorsHandlerCostant, 
			List<String>> whereIsInUso, boolean normalizeObjectIds) throws UtilsException {
		return DBOggettiInUsoUtils_gruppi.isGruppoConfigInUso(con, tipoDB, idGruppo, whereIsInUso, normalizeObjectIds);
	}
	public static boolean isGruppoRegistryInUso(Connection con, String tipoDB, IDGruppo idGruppo, Map<ErrorsHandlerCostant, 
			List<String>> whereIsInUso, boolean normalizeObjectIds) throws UtilsException {
		return DBOggettiInUsoUtils_gruppi.isGruppoRegistryInUso(con, tipoDB, idGruppo, whereIsInUso, normalizeObjectIds);
	}
	public static boolean isGruppoInUso(Connection con, String tipoDB, IDGruppo idGruppo, Map<ErrorsHandlerCostant, 
			List<String>> whereIsInUso, boolean normalizeObjectIds) throws UtilsException {
		return DBOggettiInUsoUtils_gruppi.isGruppoInUso(con, tipoDB, idGruppo, whereIsInUso, normalizeObjectIds);
	}


	public static String toString(IDGruppo idGruppo, Map<ErrorsHandlerCostant, List<String>> whereIsInUso, boolean prefix, String separator){
		return DBOggettiInUsoUtils_gruppi.toString(idGruppo, whereIsInUso, prefix, separator);
	}
	public static String toString(IDGruppo idGruppo, Map<ErrorsHandlerCostant, List<String>> whereIsInUso, boolean prefix, String separator, String intestazione){
		return DBOggettiInUsoUtils_gruppi.toString(idGruppo, whereIsInUso, prefix, separator, intestazione);
	}
	
	
	
	
	// ***** RUOLI ******

	public static boolean isRuoloConfigInUso(Connection con, String tipoDB, IDRuolo idRuolo, Map<ErrorsHandlerCostant, 
			List<String>> whereIsInUso, boolean normalizeObjectIds) throws UtilsException {
		return DBOggettiInUsoUtils_ruoli.isRuoloConfigInUso(con, tipoDB, idRuolo, whereIsInUso, normalizeObjectIds);
	}
	public static boolean isRuoloRegistryInUso(Connection con, String tipoDB, IDRuolo idRuolo, Map<ErrorsHandlerCostant, 
			List<String>> whereIsInUso, boolean normalizeObjectIds) throws UtilsException {
		return DBOggettiInUsoUtils_ruoli.isRuoloRegistryInUso(con, tipoDB, idRuolo, whereIsInUso, normalizeObjectIds);
	}
	public static boolean isRuoloInUso(Connection con, String tipoDB, IDRuolo idRuolo, Map<ErrorsHandlerCostant, 
			List<String>> whereIsInUso, boolean normalizeObjectIds) throws UtilsException {
		return DBOggettiInUsoUtils_ruoli.isRuoloInUso(con, tipoDB, idRuolo, whereIsInUso, normalizeObjectIds);
	}


	public static String toString(IDRuolo idRuolo, Map<ErrorsHandlerCostant, List<String>> whereIsInUso, boolean prefix, String separator){
		return DBOggettiInUsoUtils_ruoli.toString(idRuolo, whereIsInUso, prefix, separator);
	}
	public static String toString(IDRuolo idRuolo, Map<ErrorsHandlerCostant, List<String>> whereIsInUso, boolean prefix, String separator, String intestazione){
		return DBOggettiInUsoUtils_ruoli.toString(idRuolo, whereIsInUso, prefix, separator, intestazione);
	}
	
	
	
	
	// ***** SCOPE ******

	// Lascio i metodi se servissero in futuro
	public static boolean isScopeConfigInUso(Connection con, String tipoDB, IDScope idScope, Map<ErrorsHandlerCostant, 
			List<String>> whereIsInUso, boolean normalizeObjectIds) throws UtilsException {
		return DBOggettiInUsoUtils_scope.isScopeConfigInUso(con, tipoDB, idScope, whereIsInUso, normalizeObjectIds);
	}
//	public static boolean isScopeRegistryInUso(Connection con, String tipoDB, IDScope idScope, Map<ErrorsHandlerCostant, 
//			List<String>> whereIsInUso, boolean normalizeObjectIds) throws UtilsException {
//		return DBOggettiInUsoUtils_scope.isScopeRegistryInUso(con, tipoDB, idScope, whereIsInUso, normalizeObjectIds);
//	}
	public static boolean isScopeInUso(Connection con, String tipoDB, IDScope idScope, Map<ErrorsHandlerCostant, 
			List<String>> whereIsInUso, boolean normalizeObjectIds) throws UtilsException {
		return DBOggettiInUsoUtils_scope.isScopeInUso(con, tipoDB, idScope, whereIsInUso, normalizeObjectIds);
	}


	public static String toString(IDScope idScope, Map<ErrorsHandlerCostant, List<String>> whereIsInUso, boolean prefix, String separator){
		return DBOggettiInUsoUtils_scope.toString(idScope, whereIsInUso, prefix, separator);
	}
	public static String toString(IDScope idScope, Map<ErrorsHandlerCostant, List<String>> whereIsInUso, boolean prefix, String separator, String intestazione){
		return DBOggettiInUsoUtils_scope.toString(idScope, whereIsInUso, prefix, separator, intestazione);
	}
	
	
	
	
	// ***** SOGGETTI ******

	public static boolean isSoggettoConfigInUso(Connection con, String tipoDB, IDSoggetto idSoggettoConfig, boolean checkControlloTraffico, 
			Map<ErrorsHandlerCostant, List<String>> whereIsInUso, boolean normalizeObjectIds,
			boolean verificaRuoli) throws UtilsException {
		return DBOggettiInUsoUtils_soggetti.isSoggettoConfigInUso(con, tipoDB, idSoggettoConfig, checkControlloTraffico, whereIsInUso, normalizeObjectIds, verificaRuoli);
	}
	public static boolean isSoggettoRegistryInUso(Connection con, String tipoDB, IDSoggetto idSoggettoRegistro, boolean checkControlloTraffico, 
			Map<ErrorsHandlerCostant, List<String>> whereIsInUso, boolean normalizeObjectIds,
			boolean verificaRuoli) throws UtilsException {
		return DBOggettiInUsoUtils_soggetti.isSoggettoRegistryInUso(con, tipoDB, idSoggettoRegistro, checkControlloTraffico, whereIsInUso, normalizeObjectIds, verificaRuoli);
	}
	
	public static String toString(IDSoggetto idSoggetto, Map<ErrorsHandlerCostant, List<String>> whereIsInUso, boolean prefix, String separator, boolean normalizeObjectIds){
		return DBOggettiInUsoUtils_soggetti.toString(idSoggetto, whereIsInUso, prefix, separator, normalizeObjectIds);
	}
	
	
	
	
	// ***** ACCORDI DI COOPERAZIONE ******

	public static boolean isAccordoCooperazioneInUso(Connection con, String tipoDB, IDAccordoCooperazione idAccordo, 
			Map<ErrorsHandlerCostant,List<String>> whereIsInUso, boolean normalizeObjectIds) throws UtilsException {
		return DBOggettiInUsoUtils_accordiCooperazione.isAccordoCooperazioneInUso(con, tipoDB, idAccordo, whereIsInUso, normalizeObjectIds);
	}

	public static String toString(IDAccordoCooperazione idAccordo, Map<ErrorsHandlerCostant, List<String>> whereIsInUso, boolean prefix, String separator, boolean normalizeObjectIds){
		return DBOggettiInUsoUtils_accordiCooperazione.toString(idAccordo, whereIsInUso, prefix, separator, normalizeObjectIds);
	}
	
	
	
	
	// ***** ACCORDI DI SERVIZIO PARTE COMUNE ******

	public static boolean isAccordoServizioParteComuneInUso(Connection con, String tipoDB, IDAccordo idAccordo, 
			Map<ErrorsHandlerCostant,List<String>> whereIsInUso, boolean normalizeObjectIds) throws UtilsException {
		return DBOggettiInUsoUtils_accordi.isAccordoServizioParteComuneInUso(con, tipoDB, idAccordo, whereIsInUso, normalizeObjectIds);
	}

	public static String toString(IDAccordo idAccordo, Map<ErrorsHandlerCostant, List<String>> whereIsInUso, boolean prefix, String separator, boolean normalizeObjectIds){
		return DBOggettiInUsoUtils_accordi.toString(idAccordo, whereIsInUso, prefix, separator, normalizeObjectIds);
	}
	
	
	
	
	// ***** RESOURCES ******

	public static boolean isRisorsaConfigInUso(Connection con, String tipoDB, IDResource idRisorsa, Map<ErrorsHandlerCostant, 
			List<String>> whereIsInUso, boolean normalizeObjectIds) throws UtilsException {
		return DBOggettiInUsoUtils_accordiRest.isRisorsaConfigInUso(con, tipoDB, idRisorsa, whereIsInUso, normalizeObjectIds);
	}
	public static boolean isRisorsaRegistryInUso(Connection con, String tipoDB, IDResource idRisorsa, Map<ErrorsHandlerCostant, 
			List<String>> whereIsInUso, boolean normalizeObjectIds) throws UtilsException {
		return DBOggettiInUsoUtils_accordiRest.isRisorsaRegistryInUso(con, tipoDB, idRisorsa, whereIsInUso, normalizeObjectIds);
	}
	public static boolean isRisorsaInUso(Connection con, String tipoDB, IDResource idRisorsa, Map<ErrorsHandlerCostant, 
			List<String>> whereIsInUso, boolean normalizeObjectIds) throws UtilsException {
		return DBOggettiInUsoUtils_accordiRest.isRisorsaInUso(con, tipoDB, idRisorsa, whereIsInUso, normalizeObjectIds);
	}

	public static String toString(IDResource idRisorsa, String methodPath, Map<ErrorsHandlerCostant, List<String>> whereIsInUso, boolean prefix, String separator){
		return DBOggettiInUsoUtils_accordiRest.toString(idRisorsa, methodPath, whereIsInUso, prefix, separator);
	}
	public static String toString(IDResource idRisorsa, String methodPath, Map<ErrorsHandlerCostant, List<String>> whereIsInUso, boolean prefix, String separator, String intestazione){
		return DBOggettiInUsoUtils_accordiRest.toString(idRisorsa, methodPath, whereIsInUso, prefix, separator, intestazione);
	}
	
	
	
	
	// ***** PORT TYPES ******

	public static boolean isPortTypeConfigInUso(Connection con, String tipoDB, IDPortType idPT, Map<ErrorsHandlerCostant, 
			List<String>> whereIsInUso, boolean normalizeObjectIds) throws UtilsException {
		return DBOggettiInUsoUtils_accordiSoap.isPortTypeConfigInUso(con, tipoDB, idPT, whereIsInUso, normalizeObjectIds);
	}
	public static boolean isPortTypeRegistryInUso(Connection con, String tipoDB, IDPortType idPT, Map<ErrorsHandlerCostant, 
			List<String>> whereIsInUso, boolean normalizeObjectIds) throws UtilsException {
		return DBOggettiInUsoUtils_accordiSoap.isPortTypeRegistryInUso(con, tipoDB, idPT, whereIsInUso, normalizeObjectIds);
	}
	public static boolean isPortTypeInUso(Connection con, String tipoDB, IDPortType idPT, Map<ErrorsHandlerCostant, 
			List<String>> whereIsInUso, boolean normalizeObjectIds) throws UtilsException {
		return DBOggettiInUsoUtils_accordiSoap.isPortTypeInUso(con, tipoDB, idPT, whereIsInUso, normalizeObjectIds);
	}

	public static String toString(IDPortType idPT, Map<ErrorsHandlerCostant, List<String>> whereIsInUso, boolean prefix, String separator){
		return DBOggettiInUsoUtils_accordiSoap.toString(idPT, whereIsInUso, prefix, separator);
	}
	public static String toString(IDPortType idPT, Map<ErrorsHandlerCostant, List<String>> whereIsInUso, boolean prefix, String separator, String intestazione){
		return DBOggettiInUsoUtils_accordiSoap.toString(idPT, whereIsInUso, prefix, separator, intestazione);
	}
	
	
	
	
	// ***** OPERATION ******

	public static boolean isOperazioneConfigInUso(Connection con, String tipoDB, IDPortTypeAzione idOperazione, Map<ErrorsHandlerCostant, 
			List<String>> whereIsInUso, boolean normalizeObjectIds) throws UtilsException {
		return DBOggettiInUsoUtils_accordiSoap.isOperazioneConfigInUso(con, tipoDB, idOperazione, whereIsInUso, normalizeObjectIds);
	}
	public static boolean isOperazioneRegistryInUso(Connection con, String tipoDB, IDPortTypeAzione idOperazione, Map<ErrorsHandlerCostant, 
			List<String>> whereIsInUso, boolean normalizeObjectIds) throws UtilsException {
		return DBOggettiInUsoUtils_accordiSoap.isOperazioneRegistryInUso(con, tipoDB, idOperazione, whereIsInUso, normalizeObjectIds);
	}
	public static boolean isOperazioneInUso(Connection con, String tipoDB, IDPortTypeAzione idOperazione, Map<ErrorsHandlerCostant, 
			List<String>> whereIsInUso, boolean normalizeObjectIds) throws UtilsException {
		return DBOggettiInUsoUtils_accordiSoap.isOperazioneInUso(con, tipoDB, idOperazione, whereIsInUso, normalizeObjectIds);
	}

	public static String toString(IDPortTypeAzione idOperazione, Map<ErrorsHandlerCostant, List<String>> whereIsInUso, boolean prefix, String separator){
		return DBOggettiInUsoUtils_accordiSoap.toString(idOperazione, whereIsInUso, prefix, separator);
	}
	public static String toString(IDPortTypeAzione idOperazione, Map<ErrorsHandlerCostant, List<String>> whereIsInUso, boolean prefix, String separator, String intestazione){
		return DBOggettiInUsoUtils_accordiSoap.toString(idOperazione, whereIsInUso, prefix, separator, intestazione);
	}
	
	
	
	
	// ***** ACCORDI DI SERVIZIO PARTE SPECIFICA ******
	
	public static boolean isAccordoServizioParteSpecificaInUso(Connection con, String tipoDB, IDServizio idServizio, 
			Map<ErrorsHandlerCostant,List<String>> whereIsInUso,
			List<IDPortaDelegata> nomePDGenerateAutomaticamente, List<IDPortaApplicativa> nomePAGenerateAutomaticamente, boolean normalizeObjectIds) throws UtilsException {
		return DBOggettiInUsoUtils_accordiParteSpecifica.isAccordoServizioParteSpecificaInUso(con, tipoDB, idServizio, whereIsInUso, 
				nomePDGenerateAutomaticamente, nomePAGenerateAutomaticamente, normalizeObjectIds);
	}
	public static String toString(IDServizio idServizio, Map<ErrorsHandlerCostant, List<String>> whereIsInUso, boolean prefix, String separator, 
			boolean normalizeObjectIds, String oggetto){
		return DBOggettiInUsoUtils_accordiParteSpecifica.toString(idServizio, whereIsInUso, prefix, separator, normalizeObjectIds, oggetto);
	}
	
	
	
	
	// ***** SERVIZI APPLICATIVI ******

	public static boolean isServizioApplicativoInUso(Connection con, String tipoDB, IDServizioApplicativo idServizioApplicativo, 
			Map<ErrorsHandlerCostant, List<String>> whereIsInUso, boolean isRegistroServiziLocale, boolean normalizeObjectIds,
			boolean verificaRuoli) throws UtilsException {
		return DBOggettiInUsoUtils_serviziApplicativi.isServizioApplicativoInUso(con, tipoDB, idServizioApplicativo, whereIsInUso, 
				isRegistroServiziLocale, normalizeObjectIds, verificaRuoli);
	}

	public static String toString(IDServizioApplicativo idServizioApplicativo, Map<ErrorsHandlerCostant, List<String>> whereIsInUso, boolean prefix, String separator, boolean normalizeObjectIds){
		return DBOggettiInUsoUtils_serviziApplicativi.toString(idServizioApplicativo, whereIsInUso, prefix, separator, normalizeObjectIds);
	}
	
	
	
	
	// ***** GENERIC PROPERTIES ******

	// Lascio i metodi se servissero in futuro
	public static boolean isGenericPropertiesInUso(Connection con, String tipoDB, IDGenericProperties idGP, Map<ErrorsHandlerCostant, 
			List<String>> whereIsInUso, boolean normalizeObjectIds) throws UtilsException {
		return DBOggettiInUsoUtils_genericProperties.isGenericPropertiesInUso(con, tipoDB, idGP, whereIsInUso, normalizeObjectIds);
	}

	public static String toString(IDGenericProperties idGP, Map<ErrorsHandlerCostant, List<String>> whereIsInUso, boolean prefix, String separator){
		return DBOggettiInUsoUtils_genericProperties.toString(idGP, whereIsInUso, prefix, separator);
	}
	public static String toString(IDGenericProperties idGP, Map<ErrorsHandlerCostant, List<String>> whereIsInUso, boolean prefix, String separator, String intestazione){
		return DBOggettiInUsoUtils_genericProperties.toString(idGP, whereIsInUso, prefix, separator, intestazione);
	}
	
	
	
	
	// ***** CANALE ******
	
	public static boolean isCanaleInUsoRegistro(Connection con, String tipoDB, CanaleConfigurazione canale, Map<ErrorsHandlerCostant, List<String>> whereIsInUso, boolean normalizeObjectIds) throws UtilsException {
		return DBOggettiInUsoUtils_canali.isCanaleInUsoRegistro(con, tipoDB, canale, whereIsInUso, normalizeObjectIds);
	}
	public static boolean isCanaleInUso(Connection con, String tipoDB, CanaleConfigurazione canale, Map<ErrorsHandlerCostant, List<String>> whereIsInUso, boolean normalizeObjectIds) throws UtilsException {
		return DBOggettiInUsoUtils_canali.isCanaleInUso(con, tipoDB, canale, whereIsInUso, normalizeObjectIds);
	}
	public static boolean isCanaleInUso(Connection con, String tipoDB, CanaleConfigurazione canale, Map<ErrorsHandlerCostant, List<String>> whereIsInUso, boolean normalizeObjectIds, 
			boolean registry, boolean config, boolean nodi) throws UtilsException {
		return DBOggettiInUsoUtils_canali.isCanaleInUso(con, tipoDB, canale, whereIsInUso, normalizeObjectIds,
			 registry, config, nodi);
	}

	public static String toString(CanaleConfigurazione canale, Map<ErrorsHandlerCostant, List<String>> whereIsInUso, boolean prefix, String separator){
		return DBOggettiInUsoUtils_canali.toString(canale, whereIsInUso, prefix, separator);
	}
	public static String toString(CanaleConfigurazione canale, Map<ErrorsHandlerCostant, List<String>> whereIsInUso, boolean prefix, String separator, String intestazione){
		return DBOggettiInUsoUtils_canali.toString(canale, whereIsInUso, prefix, separator, intestazione);
	}
	
	
	
	
	// ***** RATE LIMITNG POLICY ******

	public static boolean isRateLimitingPolicyInUso(Connection con, String tipoDB, IdPolicy idPolicy, 
			Map<ErrorsHandlerCostant, List<String>> whereIsInUso, boolean normalizeObjectIds) throws UtilsException {
		return DBOggettiInUsoUtils_rateLimiting.isRateLimitingPolicyInUso(con, tipoDB, idPolicy, whereIsInUso, normalizeObjectIds);
	}
	
	public static String toString(IdPolicy idPolicy, Map<ErrorsHandlerCostant, List<String>> whereIsInUso, boolean prefix, String separator){
		return DBOggettiInUsoUtils_rateLimiting.toString(idPolicy, whereIsInUso, prefix, separator);
	}
	
	
	
	
	// ***** PLUGINS ******

	public static boolean isPluginInUso(Connection con, String tipoDB, String className, String label, String tipoPlugin, String tipo, 
			Map<ErrorsHandlerCostant, List<String>> whereIsInUso, boolean normalizeObjectIds) throws UtilsException {
		return DBOggettiInUsoUtils_plugins.isPluginInUso(con, tipoDB, className, label, tipoPlugin, tipo, whereIsInUso, normalizeObjectIds);
	}

	public static String toString(String className, String label, String tipoPlugin, String tipo, Map<ErrorsHandlerCostant, List<String>> whereIsInUso, boolean prefix, String separator){
		return DBOggettiInUsoUtils_plugins.toString(className, label, tipoPlugin, tipo, whereIsInUso, prefix, separator);
	}

}

class ResultPorta {
	boolean mapping = false;
	String label;
	boolean erogazioneModi = false;
}
