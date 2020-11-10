/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it). 
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openspcoop2.core.commons.DBUtils;
import org.openspcoop2.core.commons.ErrorsHandlerCostant;
import org.openspcoop2.core.config.CanaleConfigurazione;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.constants.CostantiDB;
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
import org.openspcoop2.core.mapping.ProprietariProtocolProperty;
import org.openspcoop2.core.registry.constants.ServiceBinding;
import org.openspcoop2.core.registry.driver.IDAccordoCooperazioneFactory;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.engine.constants.Costanti;
import org.openspcoop2.protocol.engine.mapping.ModalitaIdentificazioneAzione;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.LikeConfig;
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


	private static String getProtocolPrefix(String protocollo) throws UtilsException {
		try {
			return "["+NamingUtils.getLabelProtocollo(protocollo)+"] ";
		} catch (Exception se) {
			throw new UtilsException(se.getMessage(),se);
		}
	}
	private static String getSubjectSuffix(String protocollo, IDSoggetto idSoggetto)throws UtilsException {
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
	private static ResultPorta formatPortaDelegata(String nomePorta, String tipoDB, Connection con, boolean normalizeObjectIds) throws Exception {
		
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
				if (stmt != null) {
					stmt.close();
				}
			} catch (Exception e) {
				// ignore
			}
		}
	}
	private static ResultPorta formatPortaApplicativa(String nomePorta, String tipoDB, Connection con, boolean normalizeObjectIds) throws Exception {
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
				if (stmt != null) {
					stmt.close();
				}
			} catch (Exception e) {
				// ignore
			}
		}
	}

	


	// ***** PDD ******

	public static boolean isPddInUso(Connection con, String tipoDB, String nomePdd, List<String> whereIsInUso, boolean normalizeObjectIds) throws UtilsException {
		String nomeMetodo = "pddInUso";

		PreparedStatement stmt = null;
		ResultSet risultato = null;
		String queryString;

		try {

			// Controllo che il pdd non sia in uso
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("server = ?");
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setString(1, nomePdd);
			risultato = stmt.executeQuery();
			boolean isInUso = false;
			while (risultato.next()) {
				String tipo_soggetto = risultato.getString("tipo_soggetto");
				String nome_soggetto = risultato.getString("nome_soggetto");
				IDSoggetto idSoggetto = new IDSoggetto(tipo_soggetto, nome_soggetto);
				if(normalizeObjectIds) {
					String protocollo = ProtocolFactoryManager.getInstance().getProtocolByOrganizationType(tipo_soggetto);
					whereIsInUso.add(getProtocolPrefix(protocollo)+NamingUtils.getLabelSoggetto(protocollo, idSoggetto));
				}
				else {
					whereIsInUso.add(tipo_soggetto + "/" + nome_soggetto);
				}
				isInUso = true;
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
				if (stmt != null) {
					stmt.close();
				}
			} catch (Exception e) {
				// ignore
			}
		}
	}

	public static String toString(String nomePdd, List<String> whereIsInUso, boolean prefix, String separator){
		String prefixString = "";
		if(prefix){
			prefixString = "La Porta di Dominio ["+nomePdd+"] non è eliminabile poichè: "+separator;
		}
		return prefixString+
				"risulta associata ad uno o pi&ugrave; Soggetti: " + formatList(whereIsInUso,separator)+separator;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	// ***** GRUPPI ******

	// Lascio i metodi se servissero in futuro
	public static boolean isGruppoConfigInUso(Connection con, String tipoDB, IDGruppo idGruppo, Map<ErrorsHandlerCostant, 
			List<String>> whereIsInUso, boolean normalizeObjectIds) throws UtilsException {
		return _isGruppoInUso(con,tipoDB,idGruppo,false,true,whereIsInUso,normalizeObjectIds);
	}
	public static boolean isGruppoRegistryInUso(Connection con, String tipoDB, IDGruppo idGruppo, Map<ErrorsHandlerCostant, 
			List<String>> whereIsInUso, boolean normalizeObjectIds) throws UtilsException {
		return _isGruppoInUso(con,tipoDB,idGruppo,true,false,whereIsInUso,normalizeObjectIds);
	}
	public static boolean isGruppoInUso(Connection con, String tipoDB, IDGruppo idGruppo, Map<ErrorsHandlerCostant, 
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
				
			if (accordi_list == null) {
				accordi_list = new ArrayList<String>();
				whereIsInUso.put(ErrorsHandlerCostant.IN_USO_IN_ACCORDI, accordi_list);
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
						accordi_list.add(getProtocolPrefix(protocollo)+NamingUtils.getLabelAccordoServizioParteComune(protocollo, idAccordo));
					}
					else {

						StringBuilder bf = new StringBuilder();

						bf.append(idReferenteObject.getTipo());
						bf.append("/");
						bf.append(idReferenteObject.getNome());
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

			return isInUso;

		} catch (Exception se) {
			throw new UtilsException("[DBOggettiInUsoUtils::" + nomeMetodo + "] Exception: " + se.getMessage(),se);
		} finally {
			// Chiudo statement and resultset
			try {
				try{
					if(risultato2!=null) risultato2.close();
					if(stmt2!=null) stmt2.close();
				}catch (Exception e) {
					//ignore
				}
				if (risultato != null) {
					risultato.close();
				}
				if (stmt != null) {
					stmt.close();
				}
			} catch (Exception e) {
				// ignore
			}
		}
	}


	public static String toString(IDGruppo idGruppo, Map<ErrorsHandlerCostant, List<String>> whereIsInUso, boolean prefix, String separator){
		return toString(idGruppo, whereIsInUso, prefix, separator," non eliminabile perch&egrave; :");
	}
	public static String toString(IDGruppo idGruppo, Map<ErrorsHandlerCostant, List<String>> whereIsInUso, boolean prefix, String separator, String intestazione){
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
					msg += "associato all'API: " + formatList(messages,separator) + separator;
				}
				break;
			default:
				msg += "utilizzato in oggetto non codificato ("+key+")"+separator;
				break;
			}

		}// chiudo for

		return msg;
	}






	
	
	
	
	
	
	
	
	
	
	// ***** RUOLI ******

	public static boolean isRuoloConfigInUso(Connection con, String tipoDB, IDRuolo idRuolo, Map<ErrorsHandlerCostant, 
			List<String>> whereIsInUso, boolean normalizeObjectIds) throws UtilsException {
		return _isRuoloInUso(con,tipoDB,idRuolo,false,true,whereIsInUso,normalizeObjectIds);
	}
	public static boolean isRuoloRegistryInUso(Connection con, String tipoDB, IDRuolo idRuolo, Map<ErrorsHandlerCostant, 
			List<String>> whereIsInUso, boolean normalizeObjectIds) throws UtilsException {
		return _isRuoloInUso(con,tipoDB,idRuolo,true,false,whereIsInUso,normalizeObjectIds);
	}
	public static boolean isRuoloInUso(Connection con, String tipoDB, IDRuolo idRuolo, Map<ErrorsHandlerCostant, 
			List<String>> whereIsInUso, boolean normalizeObjectIds) throws UtilsException {
		return _isRuoloInUso(con,tipoDB,idRuolo,true,true,whereIsInUso,normalizeObjectIds);
	}
	private static boolean _isRuoloInUso(Connection con, String tipoDB, IDRuolo idRuolo, boolean registry, boolean config, Map<ErrorsHandlerCostant, 
			List<String>> whereIsInUso, boolean normalizeObjectIds) throws UtilsException {
		String nomeMetodo = "_isRuoloInUso";

		PreparedStatement stmt = null;
		ResultSet risultato = null;
		String queryString;

		try {

			long idR = DBUtils.getIdRuolo(idRuolo, con, tipoDB);
			
			boolean isInUso = false;
			
			List<String> soggetti_list = whereIsInUso.get(ErrorsHandlerCostant.IN_USO_IN_SOGGETTI);
			List<String> servizi_applicativi_list = whereIsInUso.get(ErrorsHandlerCostant.IN_USO_IN_SERVIZI_APPLICATIVI);
			List<String> porte_applicative_list = whereIsInUso.get(ErrorsHandlerCostant.IN_USO_IN_PORTE_APPLICATIVE);
			List<String> porte_delegate_list = whereIsInUso.get(ErrorsHandlerCostant.IN_USO_IN_PORTE_DELEGATE);
			List<String> mappingErogazionePA_list = whereIsInUso.get(ErrorsHandlerCostant.IN_USO_IN_MAPPING_EROGAZIONE_PA);
			List<String> mappingFruizionePD_list = whereIsInUso.get(ErrorsHandlerCostant.IN_USO_IN_MAPPING_FRUIZIONE_PD);
			List<String> ct_list = whereIsInUso.get(ErrorsHandlerCostant.CONTROLLO_TRAFFICO);
			
			if (soggetti_list == null) {
				soggetti_list = new ArrayList<String>();
				whereIsInUso.put(ErrorsHandlerCostant.IN_USO_IN_SOGGETTI, soggetti_list);
			}
			if (servizi_applicativi_list == null) {
				servizi_applicativi_list = new ArrayList<String>();
				whereIsInUso.put(ErrorsHandlerCostant.IN_USO_IN_SERVIZI_APPLICATIVI, servizi_applicativi_list);
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
			if (ct_list == null) {
				ct_list = new ArrayList<String>();
				whereIsInUso.put(ErrorsHandlerCostant.CONTROLLO_TRAFFICO, ct_list);
			}
			
			
			// Controllo che il ruolo non sia in uso nei soggetti
			if(registry){
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SOGGETTI_RUOLI);
				sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
				sqlQueryObject.addSelectField("tipo_soggetto");
				sqlQueryObject.addSelectField("nome_soggetto");
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.setSelectDistinct(true);
				sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI_RUOLI+".id_ruolo = ?");
				sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI_RUOLI+".id_soggetto = "+CostantiDB.SOGGETTI+".id");
				queryString = sqlQueryObject.createSQLQuery();
				stmt = con.prepareStatement(queryString);
				stmt.setLong(1, idR);
				risultato = stmt.executeQuery();
				while (risultato.next()) {
					String tipo_soggetto = risultato.getString("tipo_soggetto");
					String nome_soggetto = risultato.getString("nome_soggetto");
					IDSoggetto idSoggetto = new IDSoggetto(tipo_soggetto, nome_soggetto);
					if(normalizeObjectIds) {
						String protocollo = ProtocolFactoryManager.getInstance().getProtocolByOrganizationType(tipo_soggetto);
						soggetti_list.add(getProtocolPrefix(protocollo)+NamingUtils.getLabelSoggetto(protocollo, idSoggetto));
					}
					else {
						soggetti_list.add(tipo_soggetto + "/" + nome_soggetto);	
					}
					isInUso = true;
				}
				risultato.close();
				stmt.close();

			}
			
			// Controllo che il ruolo non sia in uso nei serviziApplicativi
			if(config){
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI_RUOLI);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
				sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
				sqlQueryObject.addSelectField("tipo_soggetto");
				sqlQueryObject.addSelectField("nome_soggetto");
				sqlQueryObject.addSelectField(CostantiDB.SERVIZI_APPLICATIVI+".nome");
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.setSelectDistinct(true);
				sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI_RUOLI+".ruolo = ?");
				sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI_RUOLI+".id_servizio_applicativo = "+CostantiDB.SERVIZI_APPLICATIVI+".id");
				sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".id_soggetto = "+CostantiDB.SOGGETTI+".id");
				queryString = sqlQueryObject.createSQLQuery();
				stmt = con.prepareStatement(queryString);
				stmt.setString(1, idRuolo.getNome());
				risultato = stmt.executeQuery();
				while (risultato.next()) {
					String tipo_soggetto = risultato.getString("tipo_soggetto");
					String nome_soggetto = risultato.getString("nome_soggetto");
					String nome = risultato.getString("nome");
					IDSoggetto idSoggetto = new IDSoggetto(tipo_soggetto, nome_soggetto);
					if(normalizeObjectIds) {
						String protocollo = ProtocolFactoryManager.getInstance().getProtocolByOrganizationType(tipo_soggetto);
						servizi_applicativi_list.add(getProtocolPrefix(protocollo)+nome+getSubjectSuffix(protocollo, idSoggetto));
					}
					else {
						servizi_applicativi_list.add(tipo_soggetto + "/" + nome_soggetto+"_"+nome);
					}
					isInUso = true;
				}
				risultato.close();
				stmt.close();
			}
			
			// Controllo che il ruolo non sia in uso nelle porte applicative
			if(config){
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_RUOLI);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE);
				sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
				sqlQueryObject.addSelectField(CostantiDB.PORTE_APPLICATIVE+".nome_porta");
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.setSelectDistinct(true);
				sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE_RUOLI+".ruolo = ?");
				sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE_RUOLI+".id_porta = "+CostantiDB.PORTE_APPLICATIVE+".id");
				sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE+".id_soggetto = "+CostantiDB.SOGGETTI+".id");
				queryString = sqlQueryObject.createSQLQuery();
				stmt = con.prepareStatement(queryString);
				stmt.setString(1, idRuolo.getNome());
				risultato = stmt.executeQuery();
				while (risultato.next()) {
					String nome = risultato.getString("nome_porta");
					ResultPorta resultPorta = formatPortaApplicativa(nome, tipoDB, con, normalizeObjectIds);
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
			
			// Controllo che il ruolo non sia in uso nelle porte applicative
			if(config){
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE_RUOLI);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE);
				sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
				sqlQueryObject.addSelectField(CostantiDB.PORTE_DELEGATE+".nome_porta");
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.setSelectDistinct(true);
				sqlQueryObject.addWhereCondition(CostantiDB.PORTE_DELEGATE_RUOLI+".ruolo = ?");
				sqlQueryObject.addWhereCondition(CostantiDB.PORTE_DELEGATE_RUOLI+".id_porta = "+CostantiDB.PORTE_DELEGATE+".id");
				sqlQueryObject.addWhereCondition(CostantiDB.PORTE_DELEGATE+".id_soggetto = "+CostantiDB.SOGGETTI+".id");
				queryString = sqlQueryObject.createSQLQuery();
				stmt = con.prepareStatement(queryString);
				stmt.setString(1, idRuolo.getNome());
				risultato = stmt.executeQuery();
				while (risultato.next()) {
					String nome = risultato.getString("nome_porta");
					ResultPorta resultPorta = formatPortaDelegata(nome, tipoDB, con, normalizeObjectIds);
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
			
			// Controllo che il soggetto non sia associato a policy di controllo del traffico
			if(config){
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.CONTROLLO_TRAFFICO_ACTIVE_POLICY);
				sqlQueryObject.addSelectField("active_policy_id");
				sqlQueryObject.addSelectField("policy_alias");
				sqlQueryObject.addSelectField("filtro_ruolo");
				sqlQueryObject.addSelectField("filtro_porta");
				sqlQueryObject.setANDLogicOperator(false); // OR
				sqlQueryObject.addWhereCondition(true, 
						CostantiDB.CONTROLLO_TRAFFICO_ACTIVE_POLICY+".filtro_ruolo_fruitore = ?");
				sqlQueryObject.addWhereCondition(true, 
						CostantiDB.CONTROLLO_TRAFFICO_ACTIVE_POLICY+".filtro_ruolo_erogatore = ?");
				queryString = sqlQueryObject.createSQLQuery();
				stmt = con.prepareStatement(queryString);
				int index = 1;
				stmt.setString(index++, idRuolo.getNome());
				stmt.setString(index++, idRuolo.getNome());
				risultato = stmt.executeQuery();
				while (risultato.next()) {
					
					String alias = risultato.getString("policy_alias");
					if(alias== null || "".equals(alias)) {
						alias = risultato.getString("active_policy_id");
					}
					
					String nomePorta = risultato.getString("filtro_porta");
					String filtro_ruolo = risultato.getString("filtro_ruolo");
					if(nomePorta!=null) {
						String tipo = null;
						String label = null;
						if("delegata".equals(filtro_ruolo)) {
							try {
								ResultPorta resultPorta = formatPortaDelegata(nomePorta, tipoDB, con, normalizeObjectIds);
								if(resultPorta.mapping) {
									label = "Fruizione di Servizio "+ resultPorta.label;
								}
							}catch(Exception e) {
								tipo = "Outbound";
							}
						}
						else if("applicativa".equals(filtro_ruolo)) {
							try {
								ResultPorta resultPorta = formatPortaApplicativa(nomePorta, tipoDB, con, normalizeObjectIds);
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
							ct_list.add("Policy '"+alias+"' attiva nella porta '"+tipo+"' '"+nomePorta+"' ");
						}
						else {
							ct_list.add("Policy '"+alias+"' attiva nella "+label);
						}
					}
					else {
						ct_list.add("Policy '"+alias+"'");
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
			try {
				if (risultato != null) {
					risultato.close();
				}
				if (stmt != null) {
					stmt.close();
				}
			} catch (Exception e) {
				// ignore
			}
		}
	}


	public static String toString(IDRuolo idRuolo, Map<ErrorsHandlerCostant, List<String>> whereIsInUso, boolean prefix, String separator){
		return toString(idRuolo, whereIsInUso, prefix, separator," non eliminabile perch&egrave; :");
	}
	public static String toString(IDRuolo idRuolo, Map<ErrorsHandlerCostant, List<String>> whereIsInUso, boolean prefix, String separator, String intestazione){
		Set<ErrorsHandlerCostant> keys = whereIsInUso.keySet();
		String msg = "Ruolo '"+idRuolo.getNome()+"'" + intestazione+separator;
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
			case IN_USO_IN_SOGGETTI:
				if ( messages!=null && messages.size() > 0) {
					msg += "utilizzato nei Soggetti: " + formatList(messages,separator) + separator;
				}
				break;
			case IN_USO_IN_SERVIZI_APPLICATIVI:
				if ( messages!=null && messages.size() > 0) {
					msg += "utilizzato negli Applicativi: " + formatList(messages,separator) + separator;
				}
				break;
			case IN_USO_IN_PORTE_APPLICATIVE:
				if ( messages!=null && messages.size() > 0) {
					msg += "utilizzato nelle Porte Inbound: " + formatList(messages,separator) + separator;
				}
				break;
			case IN_USO_IN_PORTE_DELEGATE:
				if ( messages!=null && messages.size() > 0) {
					msg += "utilizzato nelle Porte Outbound: " + formatList(messages,separator) + separator;
				}
				break;
			case IN_USO_IN_MAPPING_EROGAZIONE_PA:
				if ( messages!=null && messages.size() > 0) {
					msg += "utilizzato nelle Erogazioni: " + formatList(messages,separator) + separator;
				}
				break;
			case IN_USO_IN_MAPPING_FRUIZIONE_PD:
				if ( messages!=null && messages.size() > 0) {
					msg += "utilizzato nelle Fruizioni: " + formatList(messages,separator) + separator;
				}
				break;
			case CONTROLLO_TRAFFICO:
				if ( messages!=null && messages.size() > 0 ) {
					msg += "utilizzato in Policy di Rate Limiting: " + formatList(messages,separator) + separator;
				}
				break;
			default:
				msg += "utilizzato in oggetto non codificato ("+key+")"+separator;
				break;
			}

		}// chiudo for

		return msg;
	}

	
	
	
	
	
	
	
	
	
	
	
	// ***** SCOPE ******

	// Lascio i metodi se servissero in futuro
	public static boolean isScopeConfigInUso(Connection con, String tipoDB, IDScope idScope, Map<ErrorsHandlerCostant, 
			List<String>> whereIsInUso, boolean normalizeObjectIds) throws UtilsException {
		return _isScopeInUso(con,tipoDB,idScope,false,true,whereIsInUso,normalizeObjectIds);
	}
//	public static boolean isScopeRegistryInUso(Connection con, String tipoDB, IDScope idScope, Map<ErrorsHandlerCostant, 
//			List<String>> whereIsInUso, boolean normalizeObjectIds) throws UtilsException {
//		return _isScopeInUso(con,tipoDB,idScope,true,false,whereIsInUso,normalizeObjectIds);
//	}
	public static boolean isScopeInUso(Connection con, String tipoDB, IDScope idScope, Map<ErrorsHandlerCostant, 
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
					ResultPorta resultPorta = formatPortaApplicativa(nome, tipoDB, con, normalizeObjectIds);
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
					ResultPorta resultPorta = formatPortaDelegata(nome, tipoDB, con, normalizeObjectIds);
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
			try {
				if (risultato != null) {
					risultato.close();
				}
				if (stmt != null) {
					stmt.close();
				}
			} catch (Exception e) {
				// ignore
			}
		}
	}


	public static String toString(IDScope idScope, Map<ErrorsHandlerCostant, List<String>> whereIsInUso, boolean prefix, String separator){
		return toString(idScope, whereIsInUso, prefix, separator," non eliminabile perch&egrave; :");
	}
	public static String toString(IDScope idScope, Map<ErrorsHandlerCostant, List<String>> whereIsInUso, boolean prefix, String separator, String intestazione){
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
					msg += "utilizzato nelle Porte Inbound: " + formatList(messages,separator) + separator;
				}
				break;
			case IN_USO_IN_PORTE_DELEGATE:
				if ( messages!=null && messages.size() > 0) {
					msg += "utilizzato nelle Porte Outbound: " + formatList(messages,separator) + separator;
				}
				break;
			case IN_USO_IN_MAPPING_EROGAZIONE_PA:
				if ( messages!=null && messages.size() > 0) {
					msg += "utilizzato nelle Erogazioni: " + formatList(messages,separator) + separator;
				}
				break;
			case IN_USO_IN_MAPPING_FRUIZIONE_PD:
				if ( messages!=null && messages.size() > 0) {
					msg += "utilizzato nelle Fruizioni: " + formatList(messages,separator) + separator;
				}
				break;
				
			default:
				msg += "utilizzato in oggetto non codificato ("+key+")"+separator;
				break;
			}

		}// chiudo for

		return msg;
	}
	
	
	
	
	
	
	





	// ***** SOGGETTI ******

	public static boolean isSoggettoConfigInUso(Connection con, String tipoDB, IDSoggetto idSoggettoConfig, boolean checkControlloTraffico, 
			Map<ErrorsHandlerCostant, List<String>> whereIsInUso, boolean normalizeObjectIds) throws UtilsException {
		return isSoggettoInUso(con,tipoDB,idSoggettoConfig,null,checkControlloTraffico, whereIsInUso, normalizeObjectIds);
	}
	public static boolean isSoggettoRegistryInUso(Connection con, String tipoDB, IDSoggetto idSoggettoRegistro, boolean checkControlloTraffico, 
			Map<ErrorsHandlerCostant, List<String>> whereIsInUso, boolean normalizeObjectIds) throws UtilsException {
		return isSoggettoInUso(con,tipoDB,null,idSoggettoRegistro,checkControlloTraffico, whereIsInUso, normalizeObjectIds);
	}
	private static boolean isSoggettoInUso(Connection con, String tipoDB, IDSoggetto idSoggettoConfig, IDSoggetto idSoggettoRegistro, boolean checkControlloTraffico, 
			Map<ErrorsHandlerCostant, List<String>> whereIsInUso, boolean normalizeObjectIds) throws UtilsException {
		String nomeMetodo = "isSoggettoInUso";

		PreparedStatement stmt = null;
		ResultSet risultato = null;
		String queryString;
		try {
			boolean isInUso = false;

			long idSoggetto = -1;
			String tipoSoggetto = null;
			String nomeSoggetto = null;
			if(idSoggettoRegistro!=null){
				tipoSoggetto = idSoggettoRegistro.getTipo();
				nomeSoggetto = idSoggettoRegistro.getNome();

			}else{
				tipoSoggetto = idSoggettoConfig.getTipo();
				nomeSoggetto = idSoggettoConfig.getNome();
			}
			idSoggetto = DBUtils.getIdSoggetto(nomeSoggetto, tipoSoggetto, con, tipoDB);
			if(idSoggetto<=0){
				throw new UtilsException("Soggetto con tipo["+tipoSoggetto+"] e nome["+nomeSoggetto+"] non trovato");
			}

			List<String> servizi_fruitori_list = whereIsInUso.get(ErrorsHandlerCostant.IS_FRUITORE);
			List<String> servizi_applicativi_list = whereIsInUso.get(ErrorsHandlerCostant.IN_USO_IN_SERVIZI_APPLICATIVI);
			List<String> servizi_list = whereIsInUso.get(ErrorsHandlerCostant.IN_USO_IN_SERVIZI);
			List<String> porte_delegate_list = whereIsInUso.get(ErrorsHandlerCostant.IN_USO_IN_PORTE_DELEGATE);
			List<String> porte_applicative_list = whereIsInUso.get(ErrorsHandlerCostant.IN_USO_IN_PORTE_APPLICATIVE);
			List<String> mappingErogazionePA_list = whereIsInUso.get(ErrorsHandlerCostant.IN_USO_IN_MAPPING_EROGAZIONE_PA);
			List<String> mappingFruizionePD_list = whereIsInUso.get(ErrorsHandlerCostant.IN_USO_IN_MAPPING_FRUIZIONE_PD);
			List<String> accordi_list = whereIsInUso.get(ErrorsHandlerCostant.IS_REFERENTE);
			List<String> accordi_coop_list = whereIsInUso.get(ErrorsHandlerCostant.IS_REFERENTE_COOPERAZIONE);
			List<String> partecipanti_list = whereIsInUso.get(ErrorsHandlerCostant.IS_PARTECIPANTE_COOPERAZIONE);
			List<String> utenti_list = whereIsInUso.get(ErrorsHandlerCostant.UTENTE);
			List<String> ct_list = whereIsInUso.get(ErrorsHandlerCostant.CONTROLLO_TRAFFICO);
			List<String> autorizzazionePA_mapping_list = whereIsInUso.get(ErrorsHandlerCostant.AUTORIZZAZIONE_MAPPING);
			List<String> autorizzazionePA_list = whereIsInUso.get(ErrorsHandlerCostant.AUTORIZZAZIONE);
			List<String> trasformazionePA_mapping_list = whereIsInUso.get(ErrorsHandlerCostant.TRASFORMAZIONE_MAPPING_PA);
			List<String> trasformazionePA_list = whereIsInUso.get(ErrorsHandlerCostant.TRASFORMAZIONE_PA);
			List<String> configurazioniProxyPass_list = whereIsInUso.get(ErrorsHandlerCostant.CONFIGURAZIONE_REGOLE_PROXY_PASS);

			if (servizi_fruitori_list == null) {
				servizi_fruitori_list = new ArrayList<String>();
				whereIsInUso.put(ErrorsHandlerCostant.IS_FRUITORE, servizi_fruitori_list);
			}
			if (servizi_applicativi_list == null) {
				servizi_applicativi_list = new ArrayList<String>();
				whereIsInUso.put(ErrorsHandlerCostant.IN_USO_IN_SERVIZI_APPLICATIVI, servizi_applicativi_list);
			}
			if (servizi_list == null) {
				servizi_list = new ArrayList<String>();
				whereIsInUso.put(ErrorsHandlerCostant.IN_USO_IN_SERVIZI, servizi_list);
			}
			if (porte_delegate_list == null) {
				porte_delegate_list = new ArrayList<String>();
				whereIsInUso.put(ErrorsHandlerCostant.IN_USO_IN_PORTE_DELEGATE, porte_delegate_list);
			}
			if (porte_applicative_list == null) {
				porte_applicative_list = new ArrayList<String>();
				whereIsInUso.put(ErrorsHandlerCostant.IN_USO_IN_PORTE_APPLICATIVE, porte_applicative_list);
			}
			if (mappingErogazionePA_list == null) {
				mappingErogazionePA_list = new ArrayList<String>();
				whereIsInUso.put(ErrorsHandlerCostant.IN_USO_IN_MAPPING_EROGAZIONE_PA, mappingErogazionePA_list);
			}
			if (mappingFruizionePD_list == null) {
				mappingFruizionePD_list = new ArrayList<String>();
				whereIsInUso.put(ErrorsHandlerCostant.IN_USO_IN_MAPPING_FRUIZIONE_PD, mappingFruizionePD_list);
			}
			if (accordi_list == null) {
				accordi_list = new ArrayList<String>();
				whereIsInUso.put(ErrorsHandlerCostant.IS_REFERENTE, accordi_list);
			}
			if (accordi_coop_list == null) {
				accordi_coop_list = new ArrayList<String>();
				whereIsInUso.put(ErrorsHandlerCostant.IS_REFERENTE_COOPERAZIONE, accordi_coop_list);
			}
			if (partecipanti_list == null) {
				partecipanti_list = new ArrayList<String>();
				whereIsInUso.put(ErrorsHandlerCostant.IS_PARTECIPANTE_COOPERAZIONE, partecipanti_list);
			}
			if (utenti_list == null) {
				utenti_list = new ArrayList<String>();
				whereIsInUso.put(ErrorsHandlerCostant.UTENTE, utenti_list);
			}
			if (ct_list == null) {
				ct_list = new ArrayList<String>();
				whereIsInUso.put(ErrorsHandlerCostant.CONTROLLO_TRAFFICO, ct_list);
			}
			if (autorizzazionePA_mapping_list == null) {
				autorizzazionePA_mapping_list = new ArrayList<String>();
				whereIsInUso.put(ErrorsHandlerCostant.AUTORIZZAZIONE_MAPPING, autorizzazionePA_mapping_list);
			}
			if (autorizzazionePA_list == null) {
				autorizzazionePA_list = new ArrayList<String>();
				whereIsInUso.put(ErrorsHandlerCostant.AUTORIZZAZIONE, autorizzazionePA_list);
			}
			if (trasformazionePA_mapping_list == null) {
				trasformazionePA_mapping_list = new ArrayList<String>();
				whereIsInUso.put(ErrorsHandlerCostant.TRASFORMAZIONE_MAPPING_PA, trasformazionePA_mapping_list);
			}
			if (trasformazionePA_list == null) {
				trasformazionePA_list = new ArrayList<String>();
				whereIsInUso.put(ErrorsHandlerCostant.TRASFORMAZIONE_PA, trasformazionePA_list);
			}
			if (configurazioniProxyPass_list == null) {
				configurazioniProxyPass_list = new ArrayList<String>();
				whereIsInUso.put(ErrorsHandlerCostant.CONFIGURAZIONE_REGOLE_PROXY_PASS, configurazioniProxyPass_list);
			}


			// Controllo che il soggetto non sia in uso nei servizi applicativi
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
			sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
			sqlQueryObject.addSelectField("tipo_soggetto");
			sqlQueryObject.addSelectField("nome_soggetto");
			sqlQueryObject.addSelectField(CostantiDB.SERVIZI_APPLICATIVI+".nome");
			sqlQueryObject.setANDLogicOperator(true);
			sqlQueryObject.setSelectDistinct(true);
			sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".id_soggetto = "+CostantiDB.SOGGETTI+".id");
			sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".id_soggetto = ?");
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idSoggetto);
			risultato = stmt.executeQuery();
			while (risultato.next()) {
				String tipo_soggetto = risultato.getString("tipo_soggetto");
				//String nome_soggetto = risultato.getString("nome_soggetto");
				String nome = risultato.getString("nome");
				// non serve, essendo di un soggetto specificoIDSoggetto idSoggettoProprietario = new IDSoggetto(tipo_soggetto, nome_soggetto);
				if(normalizeObjectIds) {
					String protocollo = ProtocolFactoryManager.getInstance().getProtocolByOrganizationType(tipo_soggetto);
					servizi_applicativi_list.add(getProtocolPrefix(protocollo)+nome);
							// non serve, essendo di un soggetto specifico +getSubjectSuffix(protocollo, idSoggettoProprietario));
				}
				else {
					servizi_applicativi_list.add(nome);
				}
				isInUso = true;
			}
			risultato.close();
			stmt.close();


			// controllo porte delegate sia per id che per tipo e nome
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE);
			sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
			sqlQueryObject.addSelectField(CostantiDB.PORTE_DELEGATE+".nome_porta");
			sqlQueryObject.setANDLogicOperator(true);
			sqlQueryObject.addWhereCondition(CostantiDB.PORTE_DELEGATE+".id_soggetto = "+CostantiDB.SOGGETTI+".id");
			sqlQueryObject.addWhereCondition(false, 
					"id_soggetto_erogatore = ?", 
					"(tipo_soggetto_erogatore = ? AND nome_soggetto_erogatore = ?)",
					"id_soggetto = ?");
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			// controllo se soggetto erogatore di porte delegate
			stmt.setLong(1, idSoggetto);
			stmt.setString(2, tipoSoggetto);
			stmt.setString(3, nomeSoggetto);
			// controllo se soggetto proprietario di porte delegate
			stmt.setLong(4, idSoggetto);
			risultato = stmt.executeQuery();
			while (risultato.next()) {
				String nome = risultato.getString("nome_porta");
				ResultPorta resultPorta = formatPortaDelegata(nome, tipoDB, con, normalizeObjectIds);
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



			// controllo se in uso in porte applicative come proprietario della porta o come soggetto virtuale
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE);
			sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
			sqlQueryObject.addSelectField(CostantiDB.PORTE_APPLICATIVE+".nome_porta");
			sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE+".id_soggetto = "+CostantiDB.SOGGETTI+".id");
			sqlQueryObject.setANDLogicOperator(true);
			sqlQueryObject.addWhereCondition(false, 
					"id_soggetto = ?", 
					"id_soggetto_virtuale = ?", 
					"(tipo_soggetto_virtuale = ? AND nome_soggetto_virtuale = ?)");		
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idSoggetto);
			stmt.setLong(2, idSoggetto);
			stmt.setString(3, tipoSoggetto);
			stmt.setString(4, nomeSoggetto);
			risultato = stmt.executeQuery();
			while (risultato.next()) {
				String nome = risultato.getString("nome_porta");
				ResultPorta resultPorta = formatPortaApplicativa(nome, tipoDB, con, normalizeObjectIds);
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



			if(idSoggettoRegistro!=null){
				//controllo se referente
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.ACCORDI);
				sqlQueryObject.addSelectField("*");
				sqlQueryObject.addWhereCondition("id_referente = ?");
				queryString = sqlQueryObject.createSQLQuery();
				stmt = con.prepareStatement(queryString);
				stmt.setLong(1, idSoggetto);
				risultato = stmt.executeQuery();
				while (risultato.next()){
					String nomeAccordo = risultato.getString("nome");
					Integer versione = risultato.getInt("versione");
					if(normalizeObjectIds) {
						String protocollo = ProtocolFactoryManager.getInstance().getProtocolByOrganizationType(idSoggettoRegistro.getTipo());
						IDAccordo idAccordo = IDAccordoFactory.getInstance().getIDAccordoFromValues(nomeAccordo, idSoggettoRegistro, versione); 
						accordi_list.add(getProtocolPrefix(protocollo)+NamingUtils.getLabelAccordoServizioParteComune(protocollo, idAccordo));
					}
					else {
						StringBuilder bf = new StringBuilder();
						bf.append(idSoggettoRegistro.toString());
						bf.append(":");
						bf.append(nomeAccordo);
						if(versione!=null && !"".equals(versione)){
							bf.append(":");
							bf.append(versione);
						}
						accordi_list.add(bf.toString());
					}				
					isInUso=true;
				}
				risultato.close();
				stmt.close();

			}

			if(idSoggettoRegistro!=null){
				//controllo se referente di un accordo cooperazione
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.ACCORDI_COOPERAZIONE);
				sqlQueryObject.addSelectField("*");
				sqlQueryObject.addWhereCondition("id_referente = ?");
				queryString = sqlQueryObject.createSQLQuery();
				stmt = con.prepareStatement(queryString);
				stmt.setLong(1, idSoggetto);
				risultato = stmt.executeQuery();
				while (risultato.next()){
					String nomeAccordo = risultato.getString("nome");
					Integer versione = risultato.getInt("versione");
					if(normalizeObjectIds) {
						String protocollo = ProtocolFactoryManager.getInstance().getProtocolByOrganizationType(idSoggettoRegistro.getTipo());
						IDAccordoCooperazione idAccordo = IDAccordoCooperazioneFactory.getInstance().getIDAccordoFromValues(nomeAccordo, idSoggettoRegistro, versione); 
						accordi_coop_list.add(getProtocolPrefix(protocollo)+NamingUtils.getLabelAccordoCooperazione(protocollo, idAccordo));
					}
					else {
						StringBuilder bf = new StringBuilder();
						bf.append(idSoggettoRegistro.toString());
						bf.append(":");
						bf.append(nomeAccordo);
						if(versione!=null && !"".equals(versione)){
							bf.append(":");
							bf.append(versione);
						}
						accordi_coop_list.add(bf.toString());
					}
					isInUso=true;
				}
				risultato.close();
				stmt.close();

			}

			if(idSoggettoRegistro!=null){

				//controllo se partecipante
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.ACCORDI_COOPERAZIONE_PARTECIPANTI);
				sqlQueryObject.addFromTable(CostantiDB.ACCORDI_COOPERAZIONE);
				sqlQueryObject.addSelectField("*");
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addWhereCondition(CostantiDB.ACCORDI_COOPERAZIONE_PARTECIPANTI+".id_accordo_cooperazione = "+CostantiDB.ACCORDI_COOPERAZIONE+".id");
				sqlQueryObject.addWhereCondition("id_soggetto = ?");
				queryString = sqlQueryObject.createSQLQuery();
				stmt = con.prepareStatement(queryString);
				stmt.setLong(1, idSoggetto);
				risultato = stmt.executeQuery();
				while (risultato.next()){
					String nomeAccordo = risultato.getString("nome");
					Integer versione = risultato.getInt("versione");
					if(normalizeObjectIds) {
						String protocollo = ProtocolFactoryManager.getInstance().getProtocolByOrganizationType(idSoggettoRegistro.getTipo());
						IDAccordo idAccordo = IDAccordoFactory.getInstance().getIDAccordoFromValues(nomeAccordo, idSoggettoRegistro, versione); 
						partecipanti_list.add(getProtocolPrefix(protocollo)+NamingUtils.getLabelAccordoServizioParteComune(protocollo, idAccordo));
					}
					else {
						StringBuilder bf = new StringBuilder();
						bf.append(idSoggettoRegistro.toString());
						bf.append(":");
						bf.append(nomeAccordo);
						if(versione!=null && !"".equals(versione)){
							bf.append(":");
							bf.append(versione);
						}
						partecipanti_list.add(bf.toString());
					}
					isInUso=true;
				}
				risultato.close();
				stmt.close();

			}
			
			if(idSoggettoRegistro!=null && mappingFruizionePD_list.isEmpty() && mappingErogazionePA_list.isEmpty()){
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI);
				sqlQueryObject.addSelectField("*");
				sqlQueryObject.addWhereCondition("id_soggetto = ?");
				queryString = sqlQueryObject.createSQLQuery();
				stmt = con.prepareStatement(queryString);
				stmt.setLong(1, idSoggetto);
				risultato = stmt.executeQuery();
				while (risultato.next()) {
					String nome_servizio = risultato.getString("nome_servizio");
					String tipo_servizio = risultato.getString("tipo_servizio");
					Integer versione_servizio = risultato.getInt("versione_servizio");
					if(normalizeObjectIds) {
						String protocollo = ProtocolFactoryManager.getInstance().getProtocolByOrganizationType(idSoggettoRegistro.getTipo());
						IDServizio idServizio = IDServizioFactory.getInstance().getIDServizioFromValues(tipo_servizio, nome_servizio, idSoggettoRegistro, versione_servizio);
						servizi_list.add(getProtocolPrefix(protocollo)+NamingUtils.getLabelAccordoServizioParteSpecifica(protocollo, idServizio));
					}
					else {
						servizi_list.add(idSoggettoRegistro.toString()+
								"_"+tipo_servizio + "/" + nome_servizio+"/"+versione_servizio);
					}
					isInUso = true;
				}
				risultato.close();
				stmt.close();
			}
			
			if(idSoggettoRegistro!=null && mappingFruizionePD_list.isEmpty() && mappingErogazionePA_list.isEmpty()){

				// controllo che non sia fruitore di un servizio
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI_FRUITORI);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI);
				sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
				sqlQueryObject.addSelectField("tipo_servizio");
				sqlQueryObject.addSelectField("nome_servizio");
				sqlQueryObject.addSelectField("versione_servizio");
				sqlQueryObject.addSelectField("tipo_soggetto");
				sqlQueryObject.addSelectField("nome_soggetto");
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_FRUITORI+".id_servizio = "+CostantiDB.SERVIZI+".id");
				sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI+".id_soggetto = "+CostantiDB.SOGGETTI+".id");
				sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_FRUITORI+".id_soggetto = ?");
				queryString = sqlQueryObject.createSQLQuery();
				stmt = con.prepareStatement(queryString);
				stmt.setLong(1, idSoggetto);
				risultato = stmt.executeQuery();
				while (risultato.next()) {
					String tipo_soggetto = risultato.getString("tipo_soggetto");
					String nome_soggetto = risultato.getString("nome_soggetto");

					String tipoServizio = risultato.getString("tipo_servizio");
					String nomeServizio = risultato.getString("nome_servizio");
					Integer versioneServizio = risultato.getInt("versione_servizio");
					
					if(normalizeObjectIds) {
						String protocollo = ProtocolFactoryManager.getInstance().getProtocolByOrganizationType(idSoggettoRegistro.getTipo());
						IDServizio idServizio = IDServizioFactory.getInstance().getIDServizioFromValues(tipoServizio, nomeServizio, new IDSoggetto(tipo_soggetto, nome_soggetto), versioneServizio);
						servizi_fruitori_list.add(getProtocolPrefix(protocollo)+NamingUtils.getLabelAccordoServizioParteSpecifica(protocollo, idServizio));
					}
					else {
						servizi_fruitori_list.add(tipo_soggetto+"/"+nome_soggetto+
								"_"+
								tipoServizio+"/"+nomeServizio+"/"+versioneServizio);
					}
					isInUso = true;
				}
				risultato.close();
				stmt.close();

			}
			
			
			// Controllo che il soggetto non sia associato ad utenti
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.USERS);
			sqlQueryObject.addFromTable(CostantiDB.USERS_SOGGETTI);
			sqlQueryObject.addSelectField("login");
			sqlQueryObject.setANDLogicOperator(true);
			sqlQueryObject.addWhereCondition(CostantiDB.USERS_SOGGETTI+".id_soggetto = ?");
			sqlQueryObject.addWhereCondition(CostantiDB.USERS+".id = "+CostantiDB.USERS_SOGGETTI+".id_utente");
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idSoggetto);
			risultato = stmt.executeQuery();
			while (risultato.next()) {
				utenti_list.add(risultato.getString("login"));
				isInUso = true;
			}
			risultato.close();
			stmt.close();
			
			// Controllo che il soggetto non sia associato a policy di controllo del traffico
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.CONTROLLO_TRAFFICO_ACTIVE_POLICY);
			sqlQueryObject.addSelectField("active_policy_id");
			sqlQueryObject.addSelectField("policy_alias");
			sqlQueryObject.addSelectField("filtro_ruolo");
			sqlQueryObject.addSelectField("filtro_porta");
			sqlQueryObject.setANDLogicOperator(false); // OR
			sqlQueryObject.addWhereCondition(true, 
					CostantiDB.CONTROLLO_TRAFFICO_ACTIVE_POLICY+".filtro_tipo_fruitore = ?", 
					CostantiDB.CONTROLLO_TRAFFICO_ACTIVE_POLICY+".filtro_nome_fruitore = ?");
			sqlQueryObject.addWhereCondition(true, 
					CostantiDB.CONTROLLO_TRAFFICO_ACTIVE_POLICY+".filtro_tipo_erogatore = ?", 
					CostantiDB.CONTROLLO_TRAFFICO_ACTIVE_POLICY+".filtro_nome_erogatore = ?");
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			int index = 1;
			stmt.setString(index++, tipoSoggetto);
			stmt.setString(index++, nomeSoggetto);
			stmt.setString(index++, tipoSoggetto);
			stmt.setString(index++, nomeSoggetto);
			risultato = stmt.executeQuery();
			while (risultato.next()) {
				
				String alias = risultato.getString("policy_alias");
				if(alias== null || "".equals(alias)) {
					alias = risultato.getString("active_policy_id");
				}
				
				String nomePorta = risultato.getString("filtro_porta");
				String filtro_ruolo = risultato.getString("filtro_ruolo");
				if(nomePorta!=null) {
					String tipo = null;
					String label = null;
					if("delegata".equals(filtro_ruolo)) {
						try {
							ResultPorta resultPorta = formatPortaDelegata(nomePorta, tipoDB, con, normalizeObjectIds);
							if(resultPorta.mapping) {
								label = "Fruizione di Servizio "+ resultPorta.label;
							}
						}catch(Exception e) {
							tipo = "Outbound";
						}
					}
					else if("applicativa".equals(filtro_ruolo)) {
						try {
							ResultPorta resultPorta = formatPortaApplicativa(nomePorta, tipoDB, con, normalizeObjectIds);
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
						ct_list.add("Policy '"+alias+"' attiva nella porta '"+tipo+"' '"+nomePorta+"' ");
					}
					else {
						ct_list.add("Policy '"+alias+"' attiva nella "+label);
					}
				}
				else {
					ct_list.add("Policy '"+alias+"'");
				}

				isInUso = true;
			}
			risultato.close();
			stmt.close();

			
			// controllo se in uso in porte applicative nell'autorizzazione
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_SOGGETTI);
			sqlQueryObject.addSelectField(CostantiDB.PORTE_APPLICATIVE+".nome_porta");
			sqlQueryObject.setANDLogicOperator(true);
			sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE_SOGGETTI+".id_porta = "+CostantiDB.PORTE_APPLICATIVE+".id");
			sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE_SOGGETTI+".tipo_soggetto=?");
			sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE_SOGGETTI+".nome_soggetto=?");	
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setString(1, tipoSoggetto);
			stmt.setString(2, nomeSoggetto);
			risultato = stmt.executeQuery();
			while (risultato.next()) {
				String nome = risultato.getString("nome_porta");
				ResultPorta resultPorta = formatPortaApplicativa(nome, tipoDB, con, normalizeObjectIds);
				if(resultPorta.mapping) {
					autorizzazionePA_mapping_list.add(resultPorta.label);
				}
				else {
					autorizzazionePA_list.add(resultPorta.label);
				}
				isInUso = true;
			}
			risultato.close();
			stmt.close();

			
			// controllo se in uso in porte applicative nella trasformazione
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI_SOGGETTI);
			sqlQueryObject.addSelectField(CostantiDB.PORTE_APPLICATIVE+".nome_porta");
			sqlQueryObject.setANDLogicOperator(true);
			sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI+".id_porta = "+CostantiDB.PORTE_APPLICATIVE+".id");
			sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI+".id = "+CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI_SOGGETTI+".id_trasformazione");
			sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI_SOGGETTI+".tipo_soggetto=?");
			sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI_SOGGETTI+".nome_soggetto=?");	
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setString(1, tipoSoggetto);
			stmt.setString(2, nomeSoggetto);
			risultato = stmt.executeQuery();
			while (risultato.next()) {
				String nome = risultato.getString("nome_porta");
				ResultPorta resultPorta = formatPortaApplicativa(nome, tipoDB, con, normalizeObjectIds);
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
			
			// controllo se in uso nelle regole di cnonfigurazione del proxy pass
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.CONFIG_URL_REGOLE);
			sqlQueryObject.addSelectField(CostantiDB.CONFIG_URL_REGOLE+".nome");
			sqlQueryObject.setANDLogicOperator(true);
			sqlQueryObject.addWhereCondition(CostantiDB.CONFIG_URL_REGOLE+".tipo_soggetto=?");
			sqlQueryObject.addWhereCondition(CostantiDB.CONFIG_URL_REGOLE+".nome_soggetto=?");	
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setString(1, tipoSoggetto);
			stmt.setString(2, nomeSoggetto);
			risultato = stmt.executeQuery();
			while (risultato.next()) {
				String nome = risultato.getString("nome");
				configurazioniProxyPass_list.add(nome);
				isInUso = true;
			}
			risultato.close();
			stmt.close();
			
			
			return isInUso;

		} catch (Exception se) {
			throw new UtilsException("[DBOggettiInUsoUtils::" + nomeMetodo + "] Exception: " + se.getMessage(),se);
		} finally {
			// Chiudo statement and resultset
			try {
				if (risultato != null) {
					risultato.close();
				}
				if (stmt != null) {
					stmt.close();
				}
			} catch (Exception e) {
				// ignore
			}
		}
	}

	public static String toString(IDSoggetto idSoggetto, Map<ErrorsHandlerCostant, List<String>> whereIsInUso, boolean prefix, String separator, boolean normalizeObjectIds){
		Set<ErrorsHandlerCostant> keys = whereIsInUso.keySet();
		String labelSoggetto = idSoggetto.toString();
		try {
			if(normalizeObjectIds) {
				String protocollo = ProtocolFactoryManager.getInstance().getProtocolByOrganizationType(idSoggetto.getTipo());
				labelSoggetto = getProtocolPrefix(protocollo)+NamingUtils.getLabelSoggetto(protocollo, idSoggetto);
			}
		}catch(Exception e) {}
		String msg = "Soggetto '"+labelSoggetto+ "' non eliminabile perch&egrave; :"+separator;
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
			case IS_FRUITORE:
				if ( messages!=null && messages.size() > 0) {
					msg += "fruitore dei Servizi: " + formatList(messages,separator) + separator;
				}
				break;
			case IN_USO_IN_SERVIZI_APPLICATIVI:
				if ( messages!=null && messages.size() > 0 ) {
					msg += "utilizzato negli Applicativi: " + formatList(messages,separator) + separator;
				}
				break;
			case IN_USO_IN_SERVIZI:
				if ( messages!=null && messages.size() > 0) {
					msg += "erogatore dei Servizi: " + formatList(messages,separator) + separator;
				}
				break;
			case IN_USO_IN_PORTE_APPLICATIVE:
				if ( messages!=null && messages.size() > 0) {
					msg += "utilizzato nelle Porte d: " + formatList(messages,separator) + separator;
				}
				break;
			case IN_USO_IN_PORTE_DELEGATE:
				if ( messages!=null && messages.size() > 0) {
					msg += "utilizzato nelle Porte Outbound: " + formatList(messages,separator) + separator;
				}
				break;
			case IN_USO_IN_MAPPING_EROGAZIONE_PA:
				if ( messages!=null && messages.size() > 0) {
					msg += "utilizzato nelle Erogazioni: " + formatList(messages,separator) + separator;
				}
				break;
			case IN_USO_IN_MAPPING_FRUIZIONE_PD:
				if ( messages!=null && messages.size() > 0) {
					msg += "utilizzato nelle Fruizioni: " + formatList(messages,separator) + separator;
				}
				break;
			case IS_REFERENTE:
				if ( messages!=null && messages.size() > 0 ) {
					msg += "referente di API: " + formatList(messages,separator) + separator;
				}
				break;
			case IS_REFERENTE_COOPERAZIONE:
				if ( messages!=null && messages.size() > 0 ) {
					msg += "referente di Accordi di Cooperazione: " + formatList(messages,separator) + separator;
				}
				break;
			case IS_PARTECIPANTE_COOPERAZIONE:
				if ( messages!=null && messages.size() > 0 ) {
					msg += "partecipante in Accordi di Cooperazione: " + formatList(messages,separator) + separator;
				}
				break;
			case UTENTE:
				if ( messages!=null && messages.size() > 0 ) {
					msg += "associato ad Utenti: " + formatList(messages,separator) + separator;
				}
				break;
			case CONTROLLO_TRAFFICO:
				if ( messages!=null && messages.size() > 0 ) {
					msg += "utilizzato in policy di Rate Limiting: " + formatList(messages,separator) + separator;
				}
				break;
			case AUTORIZZAZIONE_MAPPING:
				if ( messages!=null && messages.size() > 0) {
					msg += "utilizzato nel Controllo degli Accessi (Soggetti Autenticati) delle Erogazioni: " + formatList(messages,separator) + separator;
				}
				break;
			case AUTORIZZAZIONE:
				if ( messages!=null && messages.size() > 0) {
					msg += "utilizzato nelle Porte Inbound (Controllo degli Accessi - Soggetti Autenticati): " + formatList(messages,separator) + separator;
				}
				break;
			case TRASFORMAZIONE_MAPPING_PA:
				if ( messages!=null && messages.size() > 0) {
					msg += "utilizzato nel criterio di applicabilità della Trasformazione (Soggetti) per le Erogazioni: " + formatList(messages,separator) + separator;
				}
				break;
			case TRASFORMAZIONE_PA:
				if ( messages!=null && messages.size() > 0) {
					msg += "utilizzato nelle Porte Inbound (Criterio di applicabilità della Trasformazione - Soggetti): " + formatList(messages,separator) + separator;
				}
				break;
			case CONFIGURAZIONE_REGOLE_PROXY_PASS:
				if ( messages!=null && messages.size() > 0) {
					msg += "utilizzato nelle Regole di Proxy Pass: " + formatList(messages,separator) + separator;
				}
				break;
			default:
				msg += "utilizzato in oggetto non codificato ("+key+")"+separator;
				break;
			}

		}// chiudo for

		return msg;
	}














	// ***** ACCORDI DI COOPERAZIONE ******

	public static boolean isAccordoCooperazioneInUso(Connection con, String tipoDB, IDAccordoCooperazione idAccordo, 
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
				accordi_list = new ArrayList<String>();
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
					accordi_list.add(getProtocolPrefix(protocollo)+NamingUtils.getLabelAccordoCooperazione(protocollo, idAccordoCooperazione));
				}
				else {

					StringBuilder bf = new StringBuilder();

					bf.append(idReferenteObject.getTipo());
					bf.append("/");
					bf.append(idReferenteObject.getNome());
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
			try{
				if(risultato!=null) risultato.close();
				if(stmt!=null) stmt.close();
			}catch (Exception e) {
				//ignore
			}
			try{
				if(risultato2!=null) risultato2.close();
				if(stmt2!=null) stmt2.close();
			}catch (Exception e) {
				//ignore
			}
		}
	}

	public static String toString(IDAccordoCooperazione idAccordo, Map<ErrorsHandlerCostant, List<String>> whereIsInUso, boolean prefix, String separator, boolean normalizeObjectIds){

		StringBuilder bf = new StringBuilder();
		if(normalizeObjectIds && idAccordo.getSoggettoReferente()!=null) {
			try {
				String protocollo = ProtocolFactoryManager.getInstance().getProtocolByOrganizationType(idAccordo.getSoggettoReferente().getTipo());
				String labelAccordo = getProtocolPrefix(protocollo)+NamingUtils.getLabelAccordoCooperazione(protocollo, idAccordo);
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
					msg += "riferito da API (Servizi Composti): " + formatList(messages,separator) + separator;
				}
				break;
			default:
				msg += "utilizzato in oggetto non codificato ("+key+")"+separator;
				break;
			}

		}// chiudo for

		return msg;
	}











	// ***** ACCORDI DI SERVIZIO PARTE COMUNE ******

	public static boolean isAccordoServizioParteComuneInUso(Connection con, String tipoDB, IDAccordo idAccordo, 
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
				servizi_list = new ArrayList<String>();
				whereIsInUso.put(ErrorsHandlerCostant.IN_USO_IN_SERVIZI, servizi_list);
			}
			if (mappingErogazionePA_list == null) {
				mappingErogazionePA_list = new ArrayList<String>();
				whereIsInUso.put(ErrorsHandlerCostant.IN_USO_IN_MAPPING_EROGAZIONE_PA, mappingErogazionePA_list);
			}
			if (mappingFruizionePD_list == null) {
				mappingFruizionePD_list = new ArrayList<String>();
				whereIsInUso.put(ErrorsHandlerCostant.IN_USO_IN_MAPPING_FRUIZIONE_PD, mappingFruizionePD_list);
			}
			
			if (correlazione_list == null) {
				correlazione_list = new ArrayList<String>();
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
						listPA = DBMappingUtils.mappingErogazionePortaApplicativaList(con, tipoDB, idServizio);
					}catch(Exception e) {}
					if(listPA!=null && !listPA.isEmpty()) {
						found=true;
						for (MappingErogazionePortaApplicativa mappingPA : listPA) {
							String suffixGruppo = "";
							if(!mappingPA.isDefault()) {
								suffixGruppo = " (Gruppo: "+mappingPA.getDescrizione()+")";
							}
							String servizio = getProtocolPrefix(protocollo)+
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
							listPD = DBMappingUtils.mappingFruizionePortaDelegataList(con, tipoDB, idSoggettoFruitore, idServizio);
						}catch(Exception e) {}
						if(listPD!=null && !listPD.isEmpty()) {
							found=true;
							for (MappingFruizionePortaDelegata mappingPD : listPD) {
								String suffixGruppo = "";
								if(!mappingPD.isDefault()) {
									suffixGruppo = " (Gruppo: "+mappingPD.getDescrizione()+")";
								}
								String servizio = getProtocolPrefix(protocollo)+
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

			
			
			
			// Controllo che qualche azione o risorsa non sia stata correlata da un'altra azione tramite Modi PA
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
					List<String> risorse = new ArrayList<String>();
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
			try{
				if(risultato!=null) risultato.close();
				if(stmt!=null) stmt.close();
			}catch (Exception e) {
				//ignore
			}
		}
	}


	public static String toString(IDAccordo idAccordo, Map<ErrorsHandlerCostant, List<String>> whereIsInUso, boolean prefix, String separator, boolean normalizeObjectIds){

		StringBuilder bf = new StringBuilder();
		if(normalizeObjectIds && idAccordo.getSoggettoReferente()!=null) {
			try {
				String protocollo = ProtocolFactoryManager.getInstance().getProtocolByOrganizationType(idAccordo.getSoggettoReferente().getTipo());
				String labelAccordo = getProtocolPrefix(protocollo)+NamingUtils.getLabelAccordoServizioParteComune(protocollo, idAccordo);
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
					msg += "implementata dai Servizi: " + formatList(messages,separator) + separator;
				}
				break;
			case IN_USO_IN_MAPPING_EROGAZIONE_PA:
				if ( messages!=null && messages.size() > 0) {
					msg += "implementata nelle Erogazioni: " + formatList(messages,separator) + separator;
				}
				break;
			case IN_USO_IN_MAPPING_FRUIZIONE_PD:
				if ( messages!=null && messages.size() > 0) {
					msg += "implementata nelle Fruizioni: " + formatList(messages,separator) + separator;
				}
				break;
				
			case IS_CORRELATA:
				if ( messages!=null && messages.size() > 0) {
					msg += "correlata ad operazioni di altre API: " + formatList(messages,separator) + separator;
				}
				break;
				
			default:
				msg += "utilizzata in oggetto non codificato ("+key+")"+separator;
				break;
			}

		}// chiudo for

		return msg;
	}



	
	
	
	
	
	
	
	// ***** RESOURCES ******

	public static boolean isRisorsaConfigInUso(Connection con, String tipoDB, IDResource idRisorsa, Map<ErrorsHandlerCostant, 
			List<String>> whereIsInUso, boolean normalizeObjectIds) throws UtilsException {
		return _isRisorsaInUso(con,tipoDB,idRisorsa,false,true,whereIsInUso,normalizeObjectIds);
	}
	public static boolean isRisorsaRegistryInUso(Connection con, String tipoDB, IDResource idRisorsa, Map<ErrorsHandlerCostant, 
			List<String>> whereIsInUso, boolean normalizeObjectIds) throws UtilsException {
		return _isRisorsaInUso(con,tipoDB,idRisorsa,true,false,whereIsInUso,normalizeObjectIds);
	}
	public static boolean isRisorsaInUso(Connection con, String tipoDB, IDResource idRisorsa, Map<ErrorsHandlerCostant, 
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
			
			
			
			
			
			
			// Controllo che l'azione non sia stata correlata da un'altra azione tramite Modi PA
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
								List<MappingFruizionePortaDelegata> lPD = DBMappingUtils.mappingFruizionePortaDelegataList(con, tipoDB, idSoggettoFruitore, idServizio);
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
											ResultPorta resultPorta = formatPortaDelegata(nome, tipoDB, con, normalizeObjectIds);
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
											ResultPorta resultPorta = formatPortaDelegata(nome, tipoDB, con, normalizeObjectIds);
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
										
										
										// ** Controllo che non sia associato a policy di controllo del traffico **
										
										sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
										sqlQueryObject.addFromTable(CostantiDB.CONTROLLO_TRAFFICO_ACTIVE_POLICY);
										sqlQueryObject.addSelectField("active_policy_id");
										sqlQueryObject.addSelectField("policy_alias");
										sqlQueryObject.addSelectField("filtro_ruolo");
										sqlQueryObject.addSelectField("filtro_porta");
										sqlQueryObject.setANDLogicOperator(true);
										sqlQueryObject.addWhereCondition(CostantiDB.CONTROLLO_TRAFFICO_ACTIVE_POLICY+".filtro_ruolo=?");
										sqlQueryObject.addWhereCondition(CostantiDB.CONTROLLO_TRAFFICO_ACTIVE_POLICY+".filtro_porta=?");
										
										// condizione di controllo
										sqlQueryObjectOr = SQLObjectFactory.createSQLQueryObject(tipoDB);
										sqlQueryObjectOr.setANDLogicOperator(false);
										// (filtro_azione == 'NOME') OR (filtro_azione like 'NOME,%') OR (filtro_azione like '%,NOME') OR (filtro_azione like '%,applicabilita_azioni,%')
										// CLOB sqlQueryObjectOr.addWhereCondition(CostantiDB.CONTROLLO_TRAFFICO_ACTIVE_POLICY+".filtro_azione = ?");
										sqlQueryObjectOr.addWhereLikeCondition(CostantiDB.CONTROLLO_TRAFFICO_ACTIVE_POLICY+".filtro_azione", idRisorsa.getNome(), false , false);
										sqlQueryObjectOr.addWhereLikeCondition(CostantiDB.CONTROLLO_TRAFFICO_ACTIVE_POLICY+".filtro_azione", idRisorsa.getNome()+",", LikeConfig.startsWith(false));
										sqlQueryObjectOr.addWhereLikeCondition(CostantiDB.CONTROLLO_TRAFFICO_ACTIVE_POLICY+".filtro_azione", ","+idRisorsa.getNome(), LikeConfig.endsWith(false));
										sqlQueryObjectOr.addWhereLikeCondition(CostantiDB.CONTROLLO_TRAFFICO_ACTIVE_POLICY+".filtro_azione", ","+idRisorsa.getNome()+",", true , false);
										sqlQueryObject.addWhereCondition(sqlQueryObjectOr.createSQLConditions());
										
										queryString = sqlQueryObject.createSQLQuery();
										stmt = con.prepareStatement(queryString);
										stmt.setString(1, "delegata");
										stmt.setString(2, mapping.getIdPortaDelegata().getNome());
										// CLOB stmt.setString(3, idRisorsa.getNome());
										risultato = stmt.executeQuery();
										while (risultato.next()) {
											
											String alias = risultato.getString("policy_alias");
											if(alias== null || "".equals(alias)) {
												alias = risultato.getString("active_policy_id");
											}
											
											String nomePorta = risultato.getString("filtro_porta");
											String filtro_ruolo = risultato.getString("filtro_ruolo");
											if(nomePorta!=null) {
												String tipo = null;
												String label = null;
												if("delegata".equals(filtro_ruolo)) {
													try {
														ResultPorta resultPorta = formatPortaDelegata(nomePorta, tipoDB, con, normalizeObjectIds);
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
													ct_list.add("Policy '"+alias+"' attiva nella porta '"+tipo+"' '"+nomePorta+"' ");
												}
												else {
													ct_list.add("Policy '"+alias+"' attiva nella "+label);
												}
											}
											else {
												ct_list.add("Policy '"+alias+"'");
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
				
				
			// Porte applicative, mapping
			if(config){
				if(idServiziWithAccordo!=null && !idServiziWithAccordo.isEmpty()) {
					for (IDServizio idServizio : idServiziWithAccordo) {
						List<MappingErogazionePortaApplicativa> lPA = DBMappingUtils.mappingErogazionePortaApplicativaList(con, tipoDB, idServizio);
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
									ResultPorta resultPorta = formatPortaApplicativa(nome, tipoDB, con, normalizeObjectIds);
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
									ResultPorta resultPorta = formatPortaApplicativa(nome, tipoDB, con, normalizeObjectIds);
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
								
								
								// ** Controllo che non sia associato a policy di controllo del traffico **
								
								sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
								sqlQueryObject.addFromTable(CostantiDB.CONTROLLO_TRAFFICO_ACTIVE_POLICY);
								sqlQueryObject.addSelectField("active_policy_id");
								sqlQueryObject.addSelectField("policy_alias");
								sqlQueryObject.addSelectField("filtro_ruolo");
								sqlQueryObject.addSelectField("filtro_porta");
								sqlQueryObject.setANDLogicOperator(true);
								sqlQueryObject.addWhereCondition(CostantiDB.CONTROLLO_TRAFFICO_ACTIVE_POLICY+".filtro_ruolo=?");
								sqlQueryObject.addWhereCondition(CostantiDB.CONTROLLO_TRAFFICO_ACTIVE_POLICY+".filtro_porta=?");
								
								// condizione di controllo
								sqlQueryObjectOr = SQLObjectFactory.createSQLQueryObject(tipoDB);
								sqlQueryObjectOr.setANDLogicOperator(false);
								// (filtro_azione == 'NOME') OR (filtro_azione like 'NOME,%') OR (filtro_azione like '%,NOME') OR (filtro_azione like '%,applicabilita_azioni,%')
								// CLOB sqlQueryObjectOr.addWhereCondition(CostantiDB.CONTROLLO_TRAFFICO_ACTIVE_POLICY+".filtro_azione = ?");
								sqlQueryObjectOr.addWhereLikeCondition(CostantiDB.CONTROLLO_TRAFFICO_ACTIVE_POLICY+".filtro_azione", idRisorsa.getNome(), false , false);
								sqlQueryObjectOr.addWhereLikeCondition(CostantiDB.CONTROLLO_TRAFFICO_ACTIVE_POLICY+".filtro_azione", idRisorsa.getNome()+",", LikeConfig.startsWith(false));
								sqlQueryObjectOr.addWhereLikeCondition(CostantiDB.CONTROLLO_TRAFFICO_ACTIVE_POLICY+".filtro_azione", ","+idRisorsa.getNome(), LikeConfig.endsWith(false));
								sqlQueryObjectOr.addWhereLikeCondition(CostantiDB.CONTROLLO_TRAFFICO_ACTIVE_POLICY+".filtro_azione", ","+idRisorsa.getNome()+",", true , false);
								sqlQueryObject.addWhereCondition(sqlQueryObjectOr.createSQLConditions());
								
								queryString = sqlQueryObject.createSQLQuery();
								stmt = con.prepareStatement(queryString);
								stmt.setString(1, "applicativa");
								stmt.setString(2, mapping.getIdPortaApplicativa().getNome());
								// CLOB stmt.setString(3, idRisorsa.getNome());
								risultato = stmt.executeQuery();
								while (risultato.next()) {
									
									String alias = risultato.getString("policy_alias");
									if(alias== null || "".equals(alias)) {
										alias = risultato.getString("active_policy_id");
									}
									
									String nomePorta = risultato.getString("filtro_porta");
									String filtro_ruolo = risultato.getString("filtro_ruolo");
									if(nomePorta!=null) {
										String tipo = null;
										String label = null;
										if("applicativa".equals(filtro_ruolo)) {
											try {
												ResultPorta resultPorta = formatPortaApplicativa(nomePorta, tipoDB, con, normalizeObjectIds);
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
											ct_list.add("Policy '"+alias+"' attiva nella porta '"+tipo+"' '"+nomePorta+"' ");
										}
										else {
											ct_list.add("Policy '"+alias+"' attiva nella "+label);
										}
									}
									else {
										ct_list.add("Policy '"+alias+"'");
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
			
			
			

			return isInUso;

		} catch (Exception se) {
			throw new UtilsException("[DBOggettiInUsoUtils::" + nomeMetodo + "] Exception: " + se.getMessage(),se);
		} finally {
			// Chiudo statement and resultset
			try {
				if (risultato != null) {
					risultato.close();
				}
				if (stmt != null) {
					stmt.close();
				}
			} catch (Exception e) {
				// ignore
			}
		}
	}


	public static String toString(IDResource idRisorsa, String methodPath, Map<ErrorsHandlerCostant, List<String>> whereIsInUso, boolean prefix, String separator){
		return toString(idRisorsa, methodPath, whereIsInUso, prefix, separator," non eliminabile perch&egrave; :");
	}
	public static String toString(IDResource idRisorsa, String methodPath, Map<ErrorsHandlerCostant, List<String>> whereIsInUso, boolean prefix, String separator, String intestazione){
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
					msg += "correlata ad altre risorse: " + formatList(messages,separator) + separator;
				}
				break;
			
			case IN_USO_IN_PORTE_APPLICATIVE:
				if ( messages!=null && messages.size() > 0) {
					msg += "appartenente ad un gruppo differente da quello predefinito nelle Porte Inbound: " + formatList(messages,separator) + separator;
				}
				break;
			case IN_USO_IN_PORTE_DELEGATE:
				if ( messages!=null && messages.size() > 0) {
					msg += "appartenente ad un gruppo differente da quello predefinito nelle Porte Outbound: " + formatList(messages,separator) + separator;
				}
				break;
			case IN_USO_IN_MAPPING_EROGAZIONE_PA:
				if ( messages!=null && messages.size() > 0) {
					msg += "appartenente ad un gruppo differente da quello predefinito nelle Erogazioni: " + formatList(messages,separator) + separator;
				}
				break;
			case IN_USO_IN_MAPPING_FRUIZIONE_PD:
				if ( messages!=null && messages.size() > 0) {
					msg += "appartenente ad un gruppo differente da quello predefinito nelle Fruizioni: " + formatList(messages,separator) + separator;
				}
				break;
				
			case TRASFORMAZIONE_MAPPING_PD:
				if ( messages!=null && messages.size() > 0) {
					msg += "utilizzata nel criterio di applicabilità della Trasformazione (Risorse) per le Fruizioni: " + formatList(messages,separator) + separator;
				}
				break;
			case TRASFORMAZIONE_PD:
				if ( messages!=null && messages.size() > 0) {
					msg += "utilizzata nelle Porte Outbound (Criterio di applicabilità della Trasformazione - Risorse): " + formatList(messages,separator) + separator;
				}
				break;
			case TRASFORMAZIONE_MAPPING_PA:
				if ( messages!=null && messages.size() > 0) {
					msg += "utilizzata nel criterio di applicabilità della Trasformazione (Risorse) per le Erogazioni: " + formatList(messages,separator) + separator;
				}
				break;
			case TRASFORMAZIONE_PA:
				if ( messages!=null && messages.size() > 0) {
					msg += "utilizzata nelle Porte Inbound (Criterio di applicabilità della Trasformazione - Risorse): " + formatList(messages,separator) + separator;
				}
				break;
								
			case CONTROLLO_TRAFFICO:
				if ( messages!=null && messages.size() > 0 ) {
					msg += "utilizzata in Policy di Rate Limiting: " + formatList(messages,separator) + separator;
				}
				break;
			default:
				msg += "utilizzata in oggetto non codificato ("+key+")"+separator;
				break;
			}
			
		}// chiudo for

		return msg;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	// ***** PORT TYPES ******

	public static boolean isPortTypeConfigInUso(Connection con, String tipoDB, IDPortType idPT, Map<ErrorsHandlerCostant, 
			List<String>> whereIsInUso, boolean normalizeObjectIds) throws UtilsException {
		return _isPortTypeInUso(con,tipoDB,idPT,false,true,whereIsInUso,normalizeObjectIds);
	}
	public static boolean isPortTypeRegistryInUso(Connection con, String tipoDB, IDPortType idPT, Map<ErrorsHandlerCostant, 
			List<String>> whereIsInUso, boolean normalizeObjectIds) throws UtilsException {
		return _isPortTypeInUso(con,tipoDB,idPT,true,false,whereIsInUso,normalizeObjectIds);
	}
	public static boolean isPortTypeInUso(Connection con, String tipoDB, IDPortType idPT, Map<ErrorsHandlerCostant, 
			List<String>> whereIsInUso, boolean normalizeObjectIds) throws UtilsException {
		return _isPortTypeInUso(con,tipoDB,idPT,true,true,whereIsInUso,normalizeObjectIds);
	}
	private static boolean _isPortTypeInUso(Connection con, String tipoDB, IDPortType idPT,
			boolean registry, boolean config, Map<ErrorsHandlerCostant, 
			List<String>> whereIsInUso, boolean normalizeObjectIds) throws UtilsException {
		String nomeMetodo = "_isPortTypeInUso";

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
				correlazione_list.add("Azione "+nomeAzioneCorrelata+" del Servizio "+nomePTCorrelato);
				isInUso = true;
			}
			risultato.close();
			stmt.close();
			
			
			
			
			
			// Controllo che l'azione non sia stata correlata da un'altra azione tramite Modi PA
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
					correlazione_list.add("Azione "+nomeAzioneCorrelata+" del Servizio "+nomePTCorrelato+" dell'API '"+idAccordoFactory.getUriFromIDAccordo(idAPI)+"' (interazione: NonBloccante-Push)");
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
								List<MappingFruizionePortaDelegata> lPD = DBMappingUtils.mappingFruizionePortaDelegataList(con, tipoDB, idSoggettoFruitore, idServizio);
								if(lPD!=null && lPD.size()>0) {
									for (MappingFruizionePortaDelegata mapping : lPD) {
										if(!mapping.isDefault()) {
											// segnalo solo quelle di default, non ha senso segnalarte tutte
											continue;
										}
										String nome = mapping.getIdPortaDelegata().getNome();
										ResultPorta resultPorta = formatPortaDelegata(nome, tipoDB, con, normalizeObjectIds);
										if(resultPorta.mapping) {
											mappingFruizionePD_list.add(resultPorta.label);
										}
										else {
											porte_delegate_list.add(resultPorta.label);
										}
										isInUso = true;
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
						List<MappingErogazionePortaApplicativa> lPA = DBMappingUtils.mappingErogazionePortaApplicativaList(con, tipoDB, idServizio);
						if(lPA!=null && lPA.size()>0) {
							for (MappingErogazionePortaApplicativa mapping : lPA) {
								if(!mapping.isDefault()) {
									// segnalo solo quelle di default, non ha senso segnalarte tutte
									continue;
								}
								String nome = mapping.getIdPortaApplicativa().getNome();
								ResultPorta resultPorta = formatPortaApplicativa(nome, tipoDB, con, normalizeObjectIds);
								if(resultPorta.mapping) {
									mappingErogazionePA_list.add(resultPorta.label);
								}
								else {
									porte_applicative_list.add(resultPorta.label);
								}
								isInUso = true;
							}
						}
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
				if (stmt != null) {
					stmt.close();
				}
			} catch (Exception e) {
				// ignore
			}
		}
	}


	public static String toString(IDPortType idPT, Map<ErrorsHandlerCostant, List<String>> whereIsInUso, boolean prefix, String separator){
		return toString(idPT, whereIsInUso, prefix, separator," non eliminabile perch&egrave; :");
	}
	public static String toString(IDPortType idPT, Map<ErrorsHandlerCostant, List<String>> whereIsInUso, boolean prefix, String separator, String intestazione){
		Set<ErrorsHandlerCostant> keys = whereIsInUso.keySet();
		String msg = "Servizio '"+(idPT.getNome())+"'" + intestazione+separator;
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
					msg += "correlato verso altre azioni: " + formatList(messages,separator) + separator;
				}
				break;
			
			case IN_USO_IN_PORTE_APPLICATIVE:
				if ( messages!=null && messages.size() > 0) {
					msg += "utilizzato nelle Porte Inbound: " + formatList(messages,separator) + separator;
				}
				break;
			case IN_USO_IN_PORTE_DELEGATE:
				if ( messages!=null && messages.size() > 0) {
					msg += "utilizzato nelle Porte Outbound: " + formatList(messages,separator) + separator;
				}
				break;			
			case IN_USO_IN_MAPPING_EROGAZIONE_PA:
				if ( messages!=null && messages.size() > 0) {
					msg += "utilizzato nelle Erogazioni: " + formatList(messages,separator) + separator;
				}
				break;
			case IN_USO_IN_MAPPING_FRUIZIONE_PD:
				if ( messages!=null && messages.size() > 0) {
					msg += "utilizzato nelle Fruizioni: " + formatList(messages,separator) + separator;
				}
				break;
				
			default:
				msg += "utilizzata in oggetto non codificato ("+key+")"+separator;
				break;
			}
			
		}// chiudo for

		return msg;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	// ***** OPERATION ******

	public static boolean isOperazioneConfigInUso(Connection con, String tipoDB, IDPortTypeAzione idOperazione, Map<ErrorsHandlerCostant, 
			List<String>> whereIsInUso, boolean normalizeObjectIds) throws UtilsException {
		return _isOperazioneInUso(con,tipoDB,idOperazione,false,true,whereIsInUso,normalizeObjectIds);
	}
	public static boolean isOperazioneRegistryInUso(Connection con, String tipoDB, IDPortTypeAzione idOperazione, Map<ErrorsHandlerCostant, 
			List<String>> whereIsInUso, boolean normalizeObjectIds) throws UtilsException {
		return _isOperazioneInUso(con,tipoDB,idOperazione,true,false,whereIsInUso,normalizeObjectIds);
	}
	public static boolean isOperazioneInUso(Connection con, String tipoDB, IDPortTypeAzione idOperazione, Map<ErrorsHandlerCostant, 
			List<String>> whereIsInUso, boolean normalizeObjectIds) throws UtilsException {
		return _isOperazioneInUso(con,tipoDB,idOperazione,true,true,whereIsInUso,normalizeObjectIds);
	}
	private static boolean _isOperazioneInUso(Connection con, String tipoDB, IDPortTypeAzione idOperazione,
			boolean registry, boolean config, Map<ErrorsHandlerCostant, 
			List<String>> whereIsInUso, boolean normalizeObjectIds) throws UtilsException {
		String nomeMetodo = "_isOperazioneInUso";

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
			
			List<String> urlInvocazionePD_mapping_list = whereIsInUso.get(ErrorsHandlerCostant.URLINVOCAZIONE_MAPPING_PD);
			List<String> urlInvocazionePD_list = whereIsInUso.get(ErrorsHandlerCostant.URLINVOCAZIONE_PD);
			List<String> urlInvocazionePA_mapping_list = whereIsInUso.get(ErrorsHandlerCostant.URLINVOCAZIONE_MAPPING_PA);
			List<String> urlInvocazionePA_list = whereIsInUso.get(ErrorsHandlerCostant.URLINVOCAZIONE_PA);
			
			List<String> ct_list = whereIsInUso.get(ErrorsHandlerCostant.CONTROLLO_TRAFFICO);
			
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
			
			if (urlInvocazionePD_mapping_list == null) {
				urlInvocazionePD_mapping_list = new ArrayList<String>();
				whereIsInUso.put(ErrorsHandlerCostant.URLINVOCAZIONE_MAPPING_PD, urlInvocazionePD_mapping_list);
			}
			if (urlInvocazionePD_list == null) {
				urlInvocazionePD_list = new ArrayList<String>();
				whereIsInUso.put(ErrorsHandlerCostant.URLINVOCAZIONE_PD, urlInvocazionePD_list);
			}
			if (urlInvocazionePA_mapping_list == null) {
				urlInvocazionePA_mapping_list = new ArrayList<String>();
				whereIsInUso.put(ErrorsHandlerCostant.URLINVOCAZIONE_MAPPING_PA, urlInvocazionePA_mapping_list);
			}
			if (urlInvocazionePA_list == null) {
				urlInvocazionePA_list = new ArrayList<String>();
				whereIsInUso.put(ErrorsHandlerCostant.URLINVOCAZIONE_PA, urlInvocazionePA_list);
			}
			
			if (ct_list == null) {
				ct_list = new ArrayList<String>();
				whereIsInUso.put(ErrorsHandlerCostant.CONTROLLO_TRAFFICO, ct_list);
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
					correlazione_list.add("Azione "+nomeAzioneCorrelata+" del Servizio "+nomePTCorrelato);
				}
				else {
					correlazione_list.add("Azione "+nomeAzioneCorrelata);
				}
				isInUso = true;
			}
			risultato.close();
			stmt.close();
			
			
			
			
			
			// Controllo che l'azione non sia stata correlata da un'altra azione tramite Modi PA
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
					correlazione_list.add("Azione "+nomeAzioneCorrelata+" (interazione: NonBloccante-Pull)");
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
					correlazione_list.add("Azione "+nomeAzioneCorrelata+" del Servizio "+nomePTCorrelato+" dell'API '"+idAccordoFactory.getUriFromIDAccordo(idAPI)+"' (interazione: NonBloccante-Push)");
					isInUso = true;
				}
				risultato.close();
				stmt.close();
				
			}
			
			
			

			
			// Porte delegate
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
								List<MappingFruizionePortaDelegata> lPD = DBMappingUtils.mappingFruizionePortaDelegataList(con, tipoDB, idSoggettoFruitore, idServizio);
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
										stmt.setString(2, idOperazione.getNome());
										risultato = stmt.executeQuery();
										while (risultato.next()){
											String nome = risultato.getString("nome_porta");
											ResultPorta resultPorta = formatPortaDelegata(nome, tipoDB, con, normalizeObjectIds);
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
										sqlQueryObjectOr = SQLObjectFactory.createSQLQueryObject(tipoDB);
										sqlQueryObjectOr.setANDLogicOperator(false);
										// (applicabilita_azioni == 'NOME') OR (applicabilita_azioni like 'NOME,%') OR (applicabilita_azioni like '%,NOME') OR (applicabilita_azioni like '%,applicabilita_azioni,%')
										// CLOB sqlQueryObjectOr.addWhereCondition(CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI+".applicabilita_azioni = ?");
										sqlQueryObjectOr.addWhereLikeCondition(CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI+".applicabilita_azioni", idOperazione.getNome(), false , false);
										sqlQueryObjectOr.addWhereLikeCondition(CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI+".applicabilita_azioni", idOperazione.getNome()+",", LikeConfig.startsWith(false));
										sqlQueryObjectOr.addWhereLikeCondition(CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI+".applicabilita_azioni", ","+idOperazione.getNome(), LikeConfig.endsWith(false));
										sqlQueryObjectOr.addWhereLikeCondition(CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI+".applicabilita_azioni", ","+idOperazione.getNome()+",", true , false);
										sqlQueryObject.addWhereCondition(sqlQueryObjectOr.createSQLConditions());
										queryString = sqlQueryObject.createSQLQuery();
										stmt = con.prepareStatement(queryString);
										stmt.setString(1, mapping.getIdPortaDelegata().getNome());
										// CLOB stmt.setString(2, idRisorsa.getNome());
										risultato = stmt.executeQuery();
										while (risultato.next()){
											String nome = risultato.getString("nome_porta");
											ResultPorta resultPorta = formatPortaDelegata(nome, tipoDB, con, normalizeObjectIds);
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
											ResultPorta resultPorta = formatPortaDelegata(nome, tipoDB, con, normalizeObjectIds);
											if(resultPorta.mapping) {
												urlInvocazionePD_mapping_list.add(resultPorta.label);
											}
											else {
												urlInvocazionePD_list.add(resultPorta.label);
											}
											isInUso = true;
										}
										risultato.close();
										stmt.close();
										
										
										// ** Controllo che non sia associato a policy di controllo del traffico **
										
										sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
										sqlQueryObject.addFromTable(CostantiDB.CONTROLLO_TRAFFICO_ACTIVE_POLICY);
										sqlQueryObject.addSelectField("active_policy_id");
										sqlQueryObject.addSelectField("policy_alias");
										sqlQueryObject.addSelectField("filtro_ruolo");
										sqlQueryObject.addSelectField("filtro_porta");
										sqlQueryObject.setANDLogicOperator(true);
										sqlQueryObject.addWhereCondition(CostantiDB.CONTROLLO_TRAFFICO_ACTIVE_POLICY+".filtro_ruolo=?");
										sqlQueryObject.addWhereCondition(CostantiDB.CONTROLLO_TRAFFICO_ACTIVE_POLICY+".filtro_porta=?");
										
										// condizione di controllo
										sqlQueryObjectOr = SQLObjectFactory.createSQLQueryObject(tipoDB);
										sqlQueryObjectOr.setANDLogicOperator(false);
										// (filtro_azione == 'NOME') OR (filtro_azione like 'NOME,%') OR (filtro_azione like '%,NOME') OR (filtro_azione like '%,applicabilita_azioni,%')
										// CLOB sqlQueryObjectOr.addWhereCondition(CostantiDB.CONTROLLO_TRAFFICO_ACTIVE_POLICY+".filtro_azione = ?");
										sqlQueryObjectOr.addWhereLikeCondition(CostantiDB.CONTROLLO_TRAFFICO_ACTIVE_POLICY+".filtro_azione", idOperazione.getNome(), false , false);
										sqlQueryObjectOr.addWhereLikeCondition(CostantiDB.CONTROLLO_TRAFFICO_ACTIVE_POLICY+".filtro_azione", idOperazione.getNome()+",", LikeConfig.startsWith(false));
										sqlQueryObjectOr.addWhereLikeCondition(CostantiDB.CONTROLLO_TRAFFICO_ACTIVE_POLICY+".filtro_azione", ","+idOperazione.getNome(), LikeConfig.endsWith(false));
										sqlQueryObjectOr.addWhereLikeCondition(CostantiDB.CONTROLLO_TRAFFICO_ACTIVE_POLICY+".filtro_azione", ","+idOperazione.getNome()+",", true , false);
										sqlQueryObject.addWhereCondition(sqlQueryObjectOr.createSQLConditions());
										
										queryString = sqlQueryObject.createSQLQuery();
										stmt = con.prepareStatement(queryString);
										stmt.setString(1, "delegata");
										stmt.setString(2, mapping.getIdPortaDelegata().getNome());
										// CLOB stmt.setString(3, idRisorsa.getNome());
										risultato = stmt.executeQuery();
										while (risultato.next()) {
											
											String alias = risultato.getString("policy_alias");
											if(alias== null || "".equals(alias)) {
												alias = risultato.getString("active_policy_id");
											}
											
											String nomePorta = risultato.getString("filtro_porta");
											String filtro_ruolo = risultato.getString("filtro_ruolo");
											if(nomePorta!=null) {
												String tipo = null;
												String label = null;
												if("delegata".equals(filtro_ruolo)) {
													try {
														ResultPorta resultPorta = formatPortaDelegata(nomePorta, tipoDB, con, normalizeObjectIds);
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
													ct_list.add("Policy '"+alias+"' attiva nella porta '"+tipo+"' '"+nomePorta+"' ");
												}
												else {
													ct_list.add("Policy '"+alias+"' attiva nella "+label);
												}
											}
											else {
												ct_list.add("Policy '"+alias+"'");
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
			if(config){
				if(idServiziWithAccordo!=null && !idServiziWithAccordo.isEmpty()) {
					for (IDServizio idServizio : idServiziWithAccordo) {
						List<MappingErogazionePortaApplicativa> lPA = DBMappingUtils.mappingErogazionePortaApplicativaList(con, tipoDB, idServizio);
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
								stmt.setString(2, idOperazione.getNome());
								risultato = stmt.executeQuery();
								while (risultato.next()){
									String nome = risultato.getString("nome_porta");
									ResultPorta resultPorta = formatPortaApplicativa(nome, tipoDB, con, normalizeObjectIds);
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
								sqlQueryObjectOr = SQLObjectFactory.createSQLQueryObject(tipoDB);
								sqlQueryObjectOr.setANDLogicOperator(false);
								// (applicabilita_azioni == 'NOME') OR (applicabilita_azioni like 'NOME,%') OR (applicabilita_azioni like '%,NOME') OR (applicabilita_azioni like '%,applicabilita_azioni,%')
								// CLOB sqlQueryObjectOr.addWhereCondition(CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI+".applicabilita_azioni = ?");
								sqlQueryObjectOr.addWhereLikeCondition(CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI+".applicabilita_azioni", idOperazione.getNome(), false , false);
								sqlQueryObjectOr.addWhereLikeCondition(CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI+".applicabilita_azioni", idOperazione.getNome()+",", LikeConfig.startsWith(false));
								sqlQueryObjectOr.addWhereLikeCondition(CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI+".applicabilita_azioni", ","+idOperazione.getNome(), LikeConfig.endsWith(false));
								sqlQueryObjectOr.addWhereLikeCondition(CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI+".applicabilita_azioni", ","+idOperazione.getNome()+",", true , false);
								sqlQueryObject.addWhereCondition(sqlQueryObjectOr.createSQLConditions());
								queryString = sqlQueryObject.createSQLQuery();
								stmt = con.prepareStatement(queryString);
								stmt.setString(1, mapping.getIdPortaApplicativa().getNome());
								// CLOB stmt.setString(2, idRisorsa.getNome());
								risultato = stmt.executeQuery();
								while (risultato.next()){
									String nome = risultato.getString("nome_porta");
									ResultPorta resultPorta = formatPortaApplicativa(nome, tipoDB, con, normalizeObjectIds);
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
									ResultPorta resultPorta = formatPortaApplicativa(nome, tipoDB, con, normalizeObjectIds);
									if(resultPorta.mapping) {
										urlInvocazionePA_mapping_list.add(resultPorta.label);
									}
									else {
										urlInvocazionePA_list.add(resultPorta.label);
									}
									isInUso = true;
								}
								risultato.close();
								stmt.close();
								
								
								// ** Controllo che non sia associato a policy di controllo del traffico **
								
								sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
								sqlQueryObject.addFromTable(CostantiDB.CONTROLLO_TRAFFICO_ACTIVE_POLICY);
								sqlQueryObject.addSelectField("active_policy_id");
								sqlQueryObject.addSelectField("policy_alias");
								sqlQueryObject.addSelectField("filtro_ruolo");
								sqlQueryObject.addSelectField("filtro_porta");
								sqlQueryObject.setANDLogicOperator(true);
								sqlQueryObject.addWhereCondition(CostantiDB.CONTROLLO_TRAFFICO_ACTIVE_POLICY+".filtro_ruolo=?");
								sqlQueryObject.addWhereCondition(CostantiDB.CONTROLLO_TRAFFICO_ACTIVE_POLICY+".filtro_porta=?");
								
								// condizione di controllo
								sqlQueryObjectOr = SQLObjectFactory.createSQLQueryObject(tipoDB);
								sqlQueryObjectOr.setANDLogicOperator(false);
								// (filtro_azione == 'NOME') OR (filtro_azione like 'NOME,%') OR (filtro_azione like '%,NOME') OR (filtro_azione like '%,applicabilita_azioni,%')
								// CLOB sqlQueryObjectOr.addWhereCondition(CostantiDB.CONTROLLO_TRAFFICO_ACTIVE_POLICY+".filtro_azione = ?");
								sqlQueryObjectOr.addWhereLikeCondition(CostantiDB.CONTROLLO_TRAFFICO_ACTIVE_POLICY+".filtro_azione", idOperazione.getNome(), false , false);
								sqlQueryObjectOr.addWhereLikeCondition(CostantiDB.CONTROLLO_TRAFFICO_ACTIVE_POLICY+".filtro_azione", idOperazione.getNome()+",", LikeConfig.startsWith(false));
								sqlQueryObjectOr.addWhereLikeCondition(CostantiDB.CONTROLLO_TRAFFICO_ACTIVE_POLICY+".filtro_azione", ","+idOperazione.getNome(), LikeConfig.endsWith(false));
								sqlQueryObjectOr.addWhereLikeCondition(CostantiDB.CONTROLLO_TRAFFICO_ACTIVE_POLICY+".filtro_azione", ","+idOperazione.getNome()+",", true , false);
								sqlQueryObject.addWhereCondition(sqlQueryObjectOr.createSQLConditions());
								
								queryString = sqlQueryObject.createSQLQuery();
								stmt = con.prepareStatement(queryString);
								stmt.setString(1, "applicativa");
								stmt.setString(2, mapping.getIdPortaApplicativa().getNome());
								// CLOB stmt.setString(3, idRisorsa.getNome());
								risultato = stmt.executeQuery();
								while (risultato.next()) {
									
									String alias = risultato.getString("policy_alias");
									if(alias== null || "".equals(alias)) {
										alias = risultato.getString("active_policy_id");
									}
									
									String nomePorta = risultato.getString("filtro_porta");
									String filtro_ruolo = risultato.getString("filtro_ruolo");
									if(nomePorta!=null) {
										String tipo = null;
										String label = null;
										if("applicativa".equals(filtro_ruolo)) {
											try {
												ResultPorta resultPorta = formatPortaApplicativa(nomePorta, tipoDB, con, normalizeObjectIds);
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
											ct_list.add("Policy '"+alias+"' attiva nella porta '"+tipo+"' '"+nomePorta+"' ");
										}
										else {
											ct_list.add("Policy '"+alias+"' attiva nella "+label);
										}
									}
									else {
										ct_list.add("Policy '"+alias+"'");
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
						
			
			return isInUso;

		} catch (Exception se) {
			throw new UtilsException("[DBOggettiInUsoUtils::" + nomeMetodo + "] Exception: " + se.getMessage(),se);
		} finally {
			// Chiudo statement and resultset
			try {
				if (risultato != null) {
					risultato.close();
				}
				if (stmt != null) {
					stmt.close();
				}
			} catch (Exception e) {
				// ignore
			}
		}
	}


	public static String toString(IDPortTypeAzione idOperazione, Map<ErrorsHandlerCostant, List<String>> whereIsInUso, boolean prefix, String separator){
		return toString(idOperazione, whereIsInUso, prefix, separator," non eliminabile perch&egrave; :");
	}
	public static String toString(IDPortTypeAzione idOperazione, Map<ErrorsHandlerCostant, List<String>> whereIsInUso, boolean prefix, String separator, String intestazione){
		Set<ErrorsHandlerCostant> keys = whereIsInUso.keySet();
		String msg = "Azione '"+idOperazione.getNome()+"'" + intestazione+separator;
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
					msg += "correlata ad altre azioni: " + formatList(messages,separator) + separator;
				}
				break;
			
			case IN_USO_IN_PORTE_APPLICATIVE:
				if ( messages!=null && messages.size() > 0) {
					msg += "appartenente ad un gruppo differente da quello predefinito nelle Porte Inbound: " + formatList(messages,separator) + separator;
				}
				break;
			case IN_USO_IN_PORTE_DELEGATE:
				if ( messages!=null && messages.size() > 0) {
					msg += "appartenente ad un gruppo differente da quello predefinito nelle Porte Outbound: " + formatList(messages,separator) + separator;
				}
				break;
			case IN_USO_IN_MAPPING_EROGAZIONE_PA:
				if ( messages!=null && messages.size() > 0) {
					msg += "appartenente ad un gruppo differente da quello predefinito nelle Erogazioni: " + formatList(messages,separator) + separator;
				}
				break;
			case IN_USO_IN_MAPPING_FRUIZIONE_PD:
				if ( messages!=null && messages.size() > 0) {
					msg += "appartenente ad un gruppo differente da quello predefinito nelle Fruizioni: " + formatList(messages,separator) + separator;
				}
				break;
				
			case TRASFORMAZIONE_MAPPING_PD:
				if ( messages!=null && messages.size() > 0) {
					msg += "utilizzata nel criterio di applicabilità della Trasformazione (Risorse) per le Fruizioni: " + formatList(messages,separator) + separator;
				}
				break;
			case TRASFORMAZIONE_PD:
				if ( messages!=null && messages.size() > 0) {
					msg += "utilizzata nelle Porte Outbound (Criterio di applicabilità della Trasformazione - Risorse): " + formatList(messages,separator) + separator;
				}
				break;
			case TRASFORMAZIONE_MAPPING_PA:
				if ( messages!=null && messages.size() > 0) {
					msg += "utilizzata nel criterio di applicabilità della Trasformazione (Risorse) per le Erogazioni: " + formatList(messages,separator) + separator;
				}
				break;
			case TRASFORMAZIONE_PA:
				if ( messages!=null && messages.size() > 0) {
					msg += "utilizzata nelle Porte Inbound (Criterio di applicabilità della Trasformazione - Risorse): " + formatList(messages,separator) + separator;
				}
				break;
				
			case URLINVOCAZIONE_MAPPING_PD:
				if ( messages!=null && messages.size() > 0) {
					msg += "utilizzata nella configurazione dell'Url di Invocazione per le Fruizioni: " + formatList(messages,separator) + separator;
				}
				break;
			case URLINVOCAZIONE_PD:
				if ( messages!=null && messages.size() > 0) {
					msg += "utilizzata nelle Porte Outbound (Azione statica): " + formatList(messages,separator) + separator;
				}
				break;
			case URLINVOCAZIONE_MAPPING_PA:
				if ( messages!=null && messages.size() > 0) {
					msg += "utilizzata nella configurazione dell'Url di Invocazione per le Erogazioni: " + formatList(messages,separator) + separator;
				}
				break;
			case URLINVOCAZIONE_PA:
				if ( messages!=null && messages.size() > 0) {
					msg += "utilizzata nelle Porte Inbound (Azione statica): " + formatList(messages,separator) + separator;
				}
				break;
				
			case CONTROLLO_TRAFFICO:
				if ( messages!=null && messages.size() > 0 ) {
					msg += "utilizzata in Policy di Rate Limiting: " + formatList(messages,separator) + separator;
				}
				break;
			default:
				msg += "utilizzata in oggetto non codificato ("+key+")"+separator;
				break;
			}
			
		}// chiudo for

		return msg;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	// ***** ACCORDI DI SERVIZIO PARTE SPECIFICA ******

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

			if (porteApplicative_list == null) {
				porteApplicative_list = new ArrayList<String>();
				whereIsInUso.put(ErrorsHandlerCostant.IN_USO_IN_PORTE_APPLICATIVE, porteApplicative_list);
			}
			if (porteDelegate_list == null) {
				porteDelegate_list = new ArrayList<String>();
				whereIsInUso.put(ErrorsHandlerCostant.IN_USO_IN_PORTE_DELEGATE, porteDelegate_list);
			}
			if (fruitori_list == null) {
				fruitori_list = new ArrayList<String>();
				whereIsInUso.put(ErrorsHandlerCostant.POSSIEDE_FRUITORI, fruitori_list);
			}
			if (servizioComponente_list == null) {
				servizioComponente_list = new ArrayList<String>();
				whereIsInUso.put(ErrorsHandlerCostant.IS_SERVIZIO_COMPONENTE_IN_ACCORDI, servizioComponente_list);
			}
			if (mappingErogazionePA_list == null) {
				mappingErogazionePA_list = new ArrayList<String>();
				whereIsInUso.put(ErrorsHandlerCostant.IN_USO_IN_MAPPING_EROGAZIONE_PA, mappingErogazionePA_list);
			}
			if (mappingFruizionePD_list == null) {
				mappingFruizionePD_list = new ArrayList<String>();
				whereIsInUso.put(ErrorsHandlerCostant.IN_USO_IN_MAPPING_FRUIZIONE_PD, mappingFruizionePD_list);
			}
			if (utenti_list == null) {
				utenti_list = new ArrayList<String>();
				whereIsInUso.put(ErrorsHandlerCostant.UTENTE, utenti_list);
			}
			if (ct_list == null) {
				ct_list = new ArrayList<String>();
				whereIsInUso.put(ErrorsHandlerCostant.CONTROLLO_TRAFFICO, ct_list);
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
					ResultPorta resultPorta = formatPortaApplicativa(nomePorta, tipoDB, con, normalizeObjectIds);
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
					ResultPorta resultPorta = formatPortaDelegata(nomePorta, tipoDB, con, normalizeObjectIds);
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
					ResultPorta resultPorta = formatPortaApplicativa(nomePorta, tipoDB, con, normalizeObjectIds);
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
					ResultPorta resultPorta = formatPortaDelegata(nomePorta, tipoDB, con, normalizeObjectIds);
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
			//List<String> nomiServiziApplicativi = new ArrayList<String>();
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
					servizioComponente_list.add(getProtocolPrefix(protocollo)+NamingUtils.getLabelAccordoServizioParteComune(protocollo, idAccordo));
				}
				else {

					StringBuilder bf = new StringBuilder();

					bf.append(idReferenteObject.getTipo());
					bf.append("/");
					bf.append(idReferenteObject.getNome());
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
			
			
			
			
			
			
			
			// Controllo che il soggetto non sia associato a policy di controllo del traffico
			
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
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.CONTROLLO_TRAFFICO_ACTIVE_POLICY);
				sqlQueryObject.addSelectField("active_policy_id");
				sqlQueryObject.addSelectField("policy_alias");
				sqlQueryObject.addSelectField("filtro_ruolo");
				sqlQueryObject.addSelectField("filtro_porta");
				sqlQueryObject.setANDLogicOperator(true); // OR
				sqlQueryObject.addWhereCondition(CostantiDB.CONTROLLO_TRAFFICO_ACTIVE_POLICY+".filtro_tipo_erogatore = ?");
				sqlQueryObject.addWhereCondition(CostantiDB.CONTROLLO_TRAFFICO_ACTIVE_POLICY+".filtro_nome_erogatore = ?");
				sqlQueryObject.addWhereCondition(CostantiDB.CONTROLLO_TRAFFICO_ACTIVE_POLICY+".filtro_tipo_servizio = ?");
				sqlQueryObject.addWhereCondition(CostantiDB.CONTROLLO_TRAFFICO_ACTIVE_POLICY+".filtro_nome_servizio = ?");
				sqlQueryObject.addWhereCondition(CostantiDB.CONTROLLO_TRAFFICO_ACTIVE_POLICY+".filtro_versione_servizio = ?");
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
					
					String alias = risultato.getString("policy_alias");
					if(alias== null || "".equals(alias)) {
						alias = risultato.getString("active_policy_id");
					}
					
					String nomePorta = risultato.getString("filtro_porta");
					String filtro_ruolo = risultato.getString("filtro_ruolo");
					if(nomePorta!=null) {
						String tipo = null;
						String label = null;
						if("delegata".equals(filtro_ruolo)) {
							try {
								ResultPorta resultPorta = formatPortaDelegata(nomePorta, tipoDB, con, normalizeObjectIds);
								if(resultPorta.mapping) {
									label = "Fruizione di Servizio "+ resultPorta.label;
								}
							}catch(Exception e) {
								tipo = "Outbound";
							}
						}
						else if("applicativa".equals(filtro_ruolo)) {
							try {
								ResultPorta resultPorta = formatPortaApplicativa(nomePorta, tipoDB, con, normalizeObjectIds);
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
							ct_list.add("Policy '"+alias+"' attiva nella porta '"+tipo+"' '"+nomePorta+"' ");
						}
						else {
							ct_list.add("Policy '"+alias+"' attiva nella "+label);
						}
					}
					else {
						ct_list.add("Policy '"+alias+"'");
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
			//Chiudo statement and resultset
			try{
				if(risultato2!=null) risultato2.close();
				if(stmt2!=null) stmt2.close();
			}catch (Exception e) {
				//ignore
			}
			try{
				if(risultato!=null) risultato.close();
				if(stmt!=null) stmt.close();
			}catch (Exception e) {
				//ignore
			}
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
	
	public static boolean isAccordoServizioParteSpecificaInUso(Connection con, String tipoDB, IDServizio idServizio, 
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
	public static String toString(IDServizio idServizio, Map<ErrorsHandlerCostant, List<String>> whereIsInUso, boolean prefix, String separator, 
			boolean normalizeObjectIds, String oggetto){
		
		StringBuilder bf = new StringBuilder();
		if(normalizeObjectIds) {
			try {
				String protocollo = ProtocolFactoryManager.getInstance().getProtocolByOrganizationType(idServizio.getSoggettoErogatore().getTipo());
				String labelAccordo = getProtocolPrefix(protocollo)+NamingUtils.getLabelAccordoServizioParteSpecifica(protocollo, idServizio);
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
					msg += "in uso in Porte Delegate: " + formatList(messages,separator) + separator;
				}
				break;
			case IN_USO_IN_PORTE_APPLICATIVE:
				if ( messages!=null && messages.size() > 0 ) {
					msg += "in uso in Porte Applicative: " + formatList(messages,separator) + separator;
				}
				break;
			case POSSIEDE_FRUITORI:
				if ( messages!=null && messages.size() > 0 ) {
					msg += "fruito dai soggetti: " + formatList(messages,separator) + separator;
				}
				break;
			case IS_SERVIZIO_COMPONENTE_IN_ACCORDI:
				if ( messages!=null && messages.size() > 0 ) {
					msg += "associato come servizio componente in API di Servizi Composti: " + formatList(messages,separator) + separator;
				}
				break;
			case IN_USO_IN_MAPPING_EROGAZIONE_PA:
				if ( messages!=null && messages.size() > 0) {
					msg += "utilizzato nelle Erogazioni: " + formatList(messages,separator) + separator;
				}
				break;
			case IN_USO_IN_MAPPING_FRUIZIONE_PD:
				if ( messages!=null && messages.size() > 0) {
					msg += "utilizzato nelle Fruizioni: " + formatList(messages,separator) + separator;
				}
				break;
			case UTENTE:
				if ( messages!=null && messages.size() > 0 ) {
					msg += "associato ad Utenti: " + formatList(messages,separator) + separator;
				}
				break;
			case CONTROLLO_TRAFFICO:
				if ( messages!=null && messages.size() > 0 ) {
					msg += "utilizzato in policy di Rate Limiting: " + formatList(messages,separator) + separator;
				}
				break;
			default:
				msg += "utilizzato in oggetto non codificato ("+key+")"+separator;
				break;
			}

		}// chiudo for

		return msg;
	}
	
	
	
	
	
	
	



	// ***** SERVIZI APPLICATIVI ******

	public static boolean isServizioApplicativoInUso(Connection con, String tipoDB, IDServizioApplicativo idServizioApplicativo, 
			Map<ErrorsHandlerCostant, List<String>> whereIsInUso, boolean isRegistroServiziLocale, boolean normalizeObjectIds) throws UtilsException {

		String nomeMetodo = "isServizioApplicativoInUso";

		PreparedStatement stmt = null;
		ResultSet risultato = null;
		PreparedStatement stmt2 = null;
		ResultSet risultato2 = null;
		String queryString;
		try {
			boolean isInUso = false;

			long idServizioApplicativoLong = DBUtils.getIdServizioApplicativo(idServizioApplicativo.getNome(), 
					idServizioApplicativo.getIdSoggettoProprietario().getTipo(), 
					idServizioApplicativo.getIdSoggettoProprietario().getNome(), 
					con, tipoDB);
			if(idServizioApplicativoLong<=0){
				throw new UtilsException("Servizio Applicativo con id ["+idServizioApplicativo+"] non trovato");
			}


			List<String> autorizzazionePD_mapping_list = whereIsInUso.get(ErrorsHandlerCostant.AUTORIZZAZIONE_MAPPING);
			List<String> autorizzazionePD_list = whereIsInUso.get(ErrorsHandlerCostant.AUTORIZZAZIONE);
			List<String> autorizzazionePA_mapping_list = whereIsInUso.get(ErrorsHandlerCostant.AUTORIZZAZIONE_MAPPING_PA);
			List<String> autorizzazionePA_list = whereIsInUso.get(ErrorsHandlerCostant.AUTORIZZAZIONE_PA);
			List<String> porte_applicative_list = whereIsInUso.get(ErrorsHandlerCostant.IN_USO_IN_PORTE_APPLICATIVE);
			List<String> porte_applicative_mapping_list = whereIsInUso.get(ErrorsHandlerCostant.IN_USO_IN_MAPPING_EROGAZIONE_PA);
			List<String> ct_list = whereIsInUso.get(ErrorsHandlerCostant.CONTROLLO_TRAFFICO);
			List<String> trasformazionePD_mapping_list = whereIsInUso.get(ErrorsHandlerCostant.TRASFORMAZIONE_MAPPING_PD);
			List<String> trasformazionePD_list = whereIsInUso.get(ErrorsHandlerCostant.TRASFORMAZIONE_PD);
			List<String> trasformazionePA_mapping_list = whereIsInUso.get(ErrorsHandlerCostant.TRASFORMAZIONE_MAPPING_PA);
			List<String> trasformazionePA_list = whereIsInUso.get(ErrorsHandlerCostant.TRASFORMAZIONE_PA);
			
			if (autorizzazionePD_mapping_list == null) {
				autorizzazionePD_mapping_list = new ArrayList<String>();
				whereIsInUso.put(ErrorsHandlerCostant.AUTORIZZAZIONE_MAPPING, autorizzazionePD_mapping_list);
			}
			if (autorizzazionePD_list == null) {
				autorizzazionePD_list = new ArrayList<String>();
				whereIsInUso.put(ErrorsHandlerCostant.AUTORIZZAZIONE, autorizzazionePD_list);
			}
			if (autorizzazionePA_mapping_list == null) {
				autorizzazionePA_mapping_list = new ArrayList<String>();
				whereIsInUso.put(ErrorsHandlerCostant.AUTORIZZAZIONE_MAPPING_PA, autorizzazionePA_mapping_list);
			}
			if (autorizzazionePA_list == null) {
				autorizzazionePA_list = new ArrayList<String>();
				whereIsInUso.put(ErrorsHandlerCostant.AUTORIZZAZIONE_PA, autorizzazionePA_list);
			}
			if (porte_applicative_list == null) {
				porte_applicative_list = new ArrayList<String>();
				whereIsInUso.put(ErrorsHandlerCostant.IN_USO_IN_PORTE_APPLICATIVE, porte_applicative_list);
			}
			if (porte_applicative_mapping_list == null) {
				porte_applicative_mapping_list = new ArrayList<String>();
				whereIsInUso.put(ErrorsHandlerCostant.IN_USO_IN_MAPPING_EROGAZIONE_PA, porte_applicative_mapping_list);
			}
			if (ct_list == null) {
				ct_list = new ArrayList<String>();
				whereIsInUso.put(ErrorsHandlerCostant.CONTROLLO_TRAFFICO, ct_list);
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

			// Porte delegate, autorizzazione
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE_SA);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE);
			sqlQueryObject.addSelectField("nome_porta");
			sqlQueryObject.setANDLogicOperator(true);
			sqlQueryObject.addWhereCondition(CostantiDB.PORTE_DELEGATE_SA+".id_porta="+CostantiDB.PORTE_DELEGATE+".id");
			sqlQueryObject.addWhereCondition("id_servizio_applicativo=?");
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idServizioApplicativoLong);
			risultato = stmt.executeQuery();
			while (risultato.next()){
				String nome = risultato.getString("nome_porta");
				ResultPorta resultPorta = formatPortaDelegata(nome, tipoDB, con, normalizeObjectIds);
				if(resultPorta.mapping) {
					autorizzazionePD_mapping_list.add(resultPorta.label);
				}
				else {
					autorizzazionePD_list.add(resultPorta.label);
				}
				isInUso = true;
			}
			risultato.close();
			stmt.close();
			
			
			// Porte applicative, autorizzazione
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_SA_AUTORIZZATI);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE);
			sqlQueryObject.addSelectField("nome_porta");
			sqlQueryObject.setANDLogicOperator(true);
			sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE_SA_AUTORIZZATI+".id_porta="+CostantiDB.PORTE_APPLICATIVE+".id");
			sqlQueryObject.addWhereCondition("id_servizio_applicativo=?");
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idServizioApplicativoLong);
			risultato = stmt.executeQuery();
			while (risultato.next()){
				String nome = risultato.getString("nome_porta");
				ResultPorta resultPorta = formatPortaApplicativa(nome, tipoDB, con, normalizeObjectIds);
				if(resultPorta.mapping) {
					autorizzazionePA_mapping_list.add(resultPorta.label);
				}
				else {
					autorizzazionePA_list.add(resultPorta.label);
				}
				isInUso = true;
			}
			risultato.close();
			stmt.close();



			// Porte applicative
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_SA);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.setANDLogicOperator(true);
			sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE_SA+".id_porta="+CostantiDB.PORTE_APPLICATIVE+".id");
			sqlQueryObject.addWhereCondition("id_servizio_applicativo=?");
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idServizioApplicativoLong);
			risultato = stmt.executeQuery();
			while (risultato.next()){
				String nome = risultato.getString("nome_porta");
				ResultPorta resultPorta = formatPortaApplicativa(nome, tipoDB, con, normalizeObjectIds);
				if(resultPorta.mapping) {
					porte_applicative_mapping_list.add(resultPorta.label);
				}
				else {
					porte_applicative_list.add(resultPorta.label);
				}
				isInUso = true;
			}
			risultato.close();
			stmt.close();

			
			// Controllo che il soggetto non sia associato a policy di controllo del traffico
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.CONTROLLO_TRAFFICO_ACTIVE_POLICY);
			sqlQueryObject.addSelectField("active_policy_id");
			sqlQueryObject.addSelectField("policy_alias");
			sqlQueryObject.addSelectField("filtro_ruolo");
			sqlQueryObject.addSelectField("filtro_porta");
			sqlQueryObject.setANDLogicOperator(false); // OR
			sqlQueryObject.addWhereCondition(true, 
					CostantiDB.CONTROLLO_TRAFFICO_ACTIVE_POLICY+".filtro_tipo_fruitore = ?", 
					CostantiDB.CONTROLLO_TRAFFICO_ACTIVE_POLICY+".filtro_nome_fruitore = ?",
					CostantiDB.CONTROLLO_TRAFFICO_ACTIVE_POLICY+".filtro_sa_fruitore = ?");
			sqlQueryObject.addWhereCondition(true, 
					CostantiDB.CONTROLLO_TRAFFICO_ACTIVE_POLICY+".filtro_tipo_erogatore = ?", 
					CostantiDB.CONTROLLO_TRAFFICO_ACTIVE_POLICY+".filtro_nome_erogatore = ?",
					CostantiDB.CONTROLLO_TRAFFICO_ACTIVE_POLICY+".filtro_sa_erogatore = ?");
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			int index = 1;
			stmt.setString(index++, idServizioApplicativo.getIdSoggettoProprietario().getTipo());
			stmt.setString(index++, idServizioApplicativo.getIdSoggettoProprietario().getNome());
			stmt.setString(index++, idServizioApplicativo.getNome());
			stmt.setString(index++, idServizioApplicativo.getIdSoggettoProprietario().getTipo());
			stmt.setString(index++, idServizioApplicativo.getIdSoggettoProprietario().getNome());
			stmt.setString(index++, idServizioApplicativo.getNome());
			risultato = stmt.executeQuery();
			while (risultato.next()) {
				
				String alias = risultato.getString("policy_alias");
				if(alias== null || "".equals(alias)) {
					alias = risultato.getString("active_policy_id");
				}
				
				String nomePorta = risultato.getString("filtro_porta");
				String filtro_ruolo = risultato.getString("filtro_ruolo");
				if(nomePorta!=null) {
					String tipo = null;
					String label = null;
					if("delegata".equals(filtro_ruolo)) {
						try {
							ResultPorta resultPorta = formatPortaDelegata(nomePorta, tipoDB, con, normalizeObjectIds);
							if(resultPorta.mapping) {
								label = "Fruizione di Servizio "+ resultPorta.label;
							}
						}catch(Exception e) {
							tipo = "Outbound";
						}
					}
					else if("applicativa".equals(filtro_ruolo)) {
						try {
							ResultPorta resultPorta = formatPortaApplicativa(nomePorta, tipoDB, con, normalizeObjectIds);
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
						ct_list.add("Policy '"+alias+"' attiva nella porta '"+tipo+"' '"+nomePorta+"' ");
					}
					else {
						ct_list.add("Policy '"+alias+"' attiva nella "+label);
					}
				}
				else {
					ct_list.add("Policy '"+alias+"'");
				}

				isInUso = true;
			}
			risultato.close();
			stmt.close();
			
			
			
			// Porte delegate, trasformazioni
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI_SA);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE);
			sqlQueryObject.addSelectField("nome_porta");
			sqlQueryObject.setANDLogicOperator(true);
			sqlQueryObject.addWhereCondition(CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI+".id_porta="+CostantiDB.PORTE_DELEGATE+".id");
			sqlQueryObject.addWhereCondition(CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI_SA+".id_trasformazione="+CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI+".id");
			sqlQueryObject.addWhereCondition("id_servizio_applicativo=?");
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idServizioApplicativoLong);
			risultato = stmt.executeQuery();
			while (risultato.next()){
				String nome = risultato.getString("nome_porta");
				ResultPorta resultPorta = formatPortaDelegata(nome, tipoDB, con, normalizeObjectIds);
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
			
			
			
			// Porte applicative, trasformazioni
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI_SA);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE);
			sqlQueryObject.addSelectField("nome_porta");
			sqlQueryObject.setANDLogicOperator(true);
			sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI+".id_porta="+CostantiDB.PORTE_APPLICATIVE+".id");
			sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI_SA+".id_trasformazione="+CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI+".id");
			sqlQueryObject.addWhereCondition("id_servizio_applicativo=?");
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idServizioApplicativoLong);
			risultato = stmt.executeQuery();
			while (risultato.next()){
				String nome = risultato.getString("nome_porta");
				ResultPorta resultPorta = formatPortaApplicativa(nome, tipoDB, con, normalizeObjectIds);
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
			
			
			
			return isInUso;

		} catch (Exception se) {
			throw new UtilsException("[DBOggettiInUsoUtils::" + nomeMetodo + "] Exception: " + se.getMessage(),se);
		} finally {
			// Chiudo statement and resultset
			try {
				if (risultato2 != null) {
					risultato2.close();
				}
				if (stmt2 != null) {
					stmt2.close();
				}
			} catch (Exception e) {
				// ignore
			}
			// Chiudo statement and resultset
			try {
				if (risultato != null) {
					risultato.close();
				}
				if (stmt != null) {
					stmt.close();
				}
			} catch (Exception e) {
				// ignore
			}
		}
	}

	public static String toString(IDServizioApplicativo idServizioApplicativo, Map<ErrorsHandlerCostant, List<String>> whereIsInUso, boolean prefix, String separator, boolean normalizeObjectIds){
		
		StringBuilder bf = new StringBuilder();
		if(normalizeObjectIds) {
			try {
				String protocollo = ProtocolFactoryManager.getInstance().getProtocolByOrganizationType(idServizioApplicativo.getIdSoggettoProprietario().getTipo());
				String labelSA = getProtocolPrefix(protocollo)+idServizioApplicativo.getNome()+getSubjectSuffix(protocollo, idServizioApplicativo.getIdSoggettoProprietario());
				bf.append(labelSA);
			}catch(Exception e) {
				bf.append(idServizioApplicativo.getIdSoggettoProprietario().toString()+"_"+idServizioApplicativo.getNome());
			}
		}
		else {
			bf.append(idServizioApplicativo.getIdSoggettoProprietario().toString()+"_"+idServizioApplicativo.getNome());
		}
		
		Set<ErrorsHandlerCostant> keys = whereIsInUso.keySet();
		String msg = "Applicativo '" +bf.toString() +"' non eliminabile perch&egrave; :"+separator;
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
			case AUTORIZZAZIONE_MAPPING:
				if ( messages!=null && messages.size() > 0) {
					msg += "utilizzato nel Controllo degli Accessi (Richiedenti Autorizzati) delle Fruizioni: " + formatList(messages,separator) + separator;
				}
				break;
			case AUTORIZZAZIONE:
				if ( messages!=null && messages.size() > 0) {
					msg += "utilizzato nelle Porte Outbound (Controllo degli Accessi - Richiedenti Autorizzati): " + formatList(messages,separator) + separator;
				}
				break;
			case AUTORIZZAZIONE_MAPPING_PA:
				if ( messages!=null && messages.size() > 0) {
					msg += "utilizzato nel Controllo degli Accessi (Richiedenti Autorizzati) delle Erogazioni: " + formatList(messages,separator) + separator;
				}
				break;
			case AUTORIZZAZIONE_PA:
				if ( messages!=null && messages.size() > 0) {
					msg += "utilizzato nelle Porte Inbound (Controllo degli Accessi - Richiedenti Autorizzati): " + formatList(messages,separator) + separator;
				}
				break;
			case IN_USO_IN_MAPPING_EROGAZIONE_PA:
				if ( messages!=null && messages.size() > 0 ) {
					msg += "utilizzato come applicativo server nei connettori delle Erogazioni: " + formatList(messages,separator) + separator;
				}
				break;
			case IN_USO_IN_PORTE_APPLICATIVE:
				if ( messages!=null && messages.size() > 0 ) {
					msg += "in uso in Porte Inbound: " + formatList(messages,separator) + separator;
				}
				break;
			case CONTROLLO_TRAFFICO:
				if ( messages!=null && messages.size() > 0 ) {
					msg += "utilizzato in policy di Rate Limiting: " + formatList(messages,separator) + separator;
				}
				break;
			case TRASFORMAZIONE_MAPPING_PD:
				if ( messages!=null && messages.size() > 0) {
					msg += "utilizzato nel criterio di applicabilità della Trasformazione (Applicativi) per le Fruizioni: " + formatList(messages,separator) + separator;
				}
				break;
			case TRASFORMAZIONE_PD:
				if ( messages!=null && messages.size() > 0) {
					msg += "utilizzato nelle Porte Outbound (Criterio di applicabilità della Trasformazione - Applicativi): " + formatList(messages,separator) + separator;
				}
				break;
			case TRASFORMAZIONE_MAPPING_PA:
				if ( messages!=null && messages.size() > 0) {
					msg += "utilizzato nel criterio di applicabilità della Trasformazione (Applicativi) per le Erogazioni: " + formatList(messages,separator) + separator;
				}
				break;
			case TRASFORMAZIONE_PA:
				if ( messages!=null && messages.size() > 0) {
					msg += "utilizzato nelle Porte Inbound (Criterio di applicabilità della Trasformazione - Applicativi): " + formatList(messages,separator) + separator;
				}
				break;
			default:
				msg += "utilizzato in oggetto non codificato ("+key+")"+separator;
				break;
			}

		}// chiudo for

		return msg;
	}
	
	
	
	
	
	
	
	
	
	// ***** GENERIC PROPERTIES ******

	// Lascio i metodi se servissero in futuro
	public static boolean isGenericPropertiesInUso(Connection con, String tipoDB, IDGenericProperties idGP, Map<ErrorsHandlerCostant, 
			List<String>> whereIsInUso, boolean normalizeObjectIds) throws UtilsException {
		String nomeMetodo = "isGenericPropertiesInUso";

		PreparedStatement stmt = null;
		ResultSet risultato = null;
		PreparedStatement stmt2 = null;
		ResultSet risultato2 = null;
		String queryString;

		try {

			boolean isInUso = false;
			
			List<String> tokenPA_list = whereIsInUso.get(ErrorsHandlerCostant.TOKEN_PA);
			List<String> tokenPD_list = whereIsInUso.get(ErrorsHandlerCostant.TOKEN_PD);
			List<String> connettorePA_list = whereIsInUso.get(ErrorsHandlerCostant.CONNETTORE_PA);
			List<String> connettorePD_list = whereIsInUso.get(ErrorsHandlerCostant.CONNETTORE_PD);
			
			List<String> mapping_tokenPA_list = whereIsInUso.get(ErrorsHandlerCostant.TOKEN_MAPPING_PA);
			List<String> mapping_tokenPD_list = whereIsInUso.get(ErrorsHandlerCostant.TOKEN_MAPPING_PD);
			List<String> mapping_connettorePA_list = whereIsInUso.get(ErrorsHandlerCostant.CONNETTORE_MAPPING_PA);
			List<String> mapping_connettorePD_list = whereIsInUso.get(ErrorsHandlerCostant.CONNETTORE_MAPPING_PD);
			
			if (tokenPA_list == null) {
				tokenPA_list = new ArrayList<String>();
				whereIsInUso.put(ErrorsHandlerCostant.TOKEN_PA, tokenPA_list);
			}
			if (tokenPD_list == null) {
				tokenPD_list = new ArrayList<String>();
				whereIsInUso.put(ErrorsHandlerCostant.TOKEN_PD, tokenPD_list);
			}
			if (connettorePA_list == null) {
				connettorePA_list = new ArrayList<String>();
				whereIsInUso.put(ErrorsHandlerCostant.CONNETTORE_PA, connettorePA_list);
			}
			if (connettorePD_list == null) {
				connettorePD_list = new ArrayList<String>();
				whereIsInUso.put(ErrorsHandlerCostant.CONNETTORE_PD, connettorePD_list);
			}
			
			if (mapping_tokenPA_list == null) {
				mapping_tokenPA_list = new ArrayList<String>();
				whereIsInUso.put(ErrorsHandlerCostant.TOKEN_MAPPING_PA, mapping_tokenPA_list);
			}
			if (mapping_tokenPD_list == null) {
				mapping_tokenPD_list = new ArrayList<String>();
				whereIsInUso.put(ErrorsHandlerCostant.TOKEN_MAPPING_PD, mapping_tokenPD_list);
			}
			if (mapping_connettorePA_list == null) {
				mapping_connettorePA_list = new ArrayList<String>();
				whereIsInUso.put(ErrorsHandlerCostant.CONNETTORE_MAPPING_PA, mapping_connettorePA_list);
			}
			if (mapping_connettorePD_list == null) {
				mapping_connettorePD_list = new ArrayList<String>();
				whereIsInUso.put(ErrorsHandlerCostant.CONNETTORE_MAPPING_PD, mapping_connettorePD_list);
			}
			
			
			
			if(CostantiConfigurazione.GENERIC_PROPERTIES_TOKEN_TIPOLOGIA_VALIDATION.equals(idGP.getTipologia())) {
			
				// Controllo che non sia in uso nelle porte applicative
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE);
				sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
				sqlQueryObject.addSelectField(CostantiDB.PORTE_APPLICATIVE+".nome_porta");
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.setSelectDistinct(true);
				sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE+".token_policy = ?");
				sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE+".id_soggetto = "+CostantiDB.SOGGETTI+".id");
				queryString = sqlQueryObject.createSQLQuery();
				stmt = con.prepareStatement(queryString);
				stmt.setString(1, idGP.getNome());
				risultato = stmt.executeQuery();
				while (risultato.next()) {
					String nome = risultato.getString("nome_porta");
					ResultPorta resultPorta = formatPortaApplicativa(nome, tipoDB, con, normalizeObjectIds);
					if(resultPorta.mapping) {
						mapping_tokenPA_list.add(resultPorta.label);
					}
					else {
						tokenPA_list.add(resultPorta.label);
					}
					isInUso = true;
				}
				risultato.close();
				stmt.close();
				
				
				// Controllo che il ruolo non sia in uso nelle porte delegate
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE);
				sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
				sqlQueryObject.addSelectField(CostantiDB.PORTE_DELEGATE+".nome_porta");
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.setSelectDistinct(true);
				sqlQueryObject.addWhereCondition(CostantiDB.PORTE_DELEGATE+".token_policy = ?");
				sqlQueryObject.addWhereCondition(CostantiDB.PORTE_DELEGATE+".id_soggetto = "+CostantiDB.SOGGETTI+".id");
				queryString = sqlQueryObject.createSQLQuery();
				stmt = con.prepareStatement(queryString);
				stmt.setString(1, idGP.getNome());
				risultato = stmt.executeQuery();
				while (risultato.next()) {
					String nome = risultato.getString("nome_porta");
					ResultPorta resultPorta = formatPortaDelegata(nome, tipoDB, con, normalizeObjectIds);
					if(resultPorta.mapping) {
						mapping_tokenPD_list.add(resultPorta.label);
					}
					else {
						tokenPD_list.add(resultPorta.label);
					}
					isInUso = true;
				}
				risultato.close();
				stmt.close();				
				
			}
			
			if(CostantiConfigurazione.GENERIC_PROPERTIES_TOKEN_TIPOLOGIA_RETRIEVE.equals(idGP.getTipologia())) {
				
				List<Long> idConnettori = new ArrayList<>();
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.CONNETTORI);
				sqlQueryObject.addSelectField(CostantiDB.CONNETTORI+".id");
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.setSelectDistinct(true);
				sqlQueryObject.addWhereCondition(CostantiDB.CONNETTORI+".token_policy = ?");
				queryString = sqlQueryObject.createSQLQuery();
				stmt = con.prepareStatement(queryString);
				stmt.setString(1, idGP.getNome());
				risultato = stmt.executeQuery();
				while (risultato.next()) {
					Long id = risultato.getLong("id");
					idConnettori.add(id);
				}
				risultato.close();
				stmt.close();
				
				if(idConnettori!=null && !idConnettori.isEmpty()) {
				
					isInUso = true; // già è in uso... anche se fallisse sotto il mapping
					
					
					List<Long> idFruizioniDefault = new ArrayList<>();
					
					List<Long> idFruizioniGruppi_idFruizione = new ArrayList<>();
					List<Long> idFruizioniGruppi_id = new ArrayList<>();
					Map<Long,List<String>> idFruizioniGruppi_azioni = new HashMap<Long, List<String>>();
					
					List<Long> idServiziApplicativi = new ArrayList<>();
					
										
					for (Long idConnettore : idConnettori) {
						
						
						// Controllo che il token non sia in uso nelle porte applicative sia di default che dei gruppi
						
						sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
						sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
						sqlQueryObject.addSelectField("id");
						sqlQueryObject.setANDLogicOperator(true);
						sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".id_connettore_inv=?");
						queryString = sqlQueryObject.createSQLQuery();
						stmt = con.prepareStatement(queryString);
						stmt.setLong(1, idConnettore);
						risultato = stmt.executeQuery();
						while (risultato.next()) {
							Long id = risultato.getLong("id");
							idServiziApplicativi.add(id);
						}
						risultato.close();
						stmt.close();
						
						
						// Controllo che il token non sia in uso nelle porte delegate di default
						
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
						
						
						// Controllo che il token non sia in uso nelle porte delegate associate ai gruppi
						
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
					
					
					
					// Se ho rilevato che il token è in uso nelle porte applicative (sia di default che dei gruppi) traduco questa informazione
					
					if(idServiziApplicativi!=null && !idServiziApplicativi.isEmpty()) {
						for (Long idServiziApplicativo : idServiziApplicativi) {
							
							sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
							sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_SA);
							sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE);
							sqlQueryObject.setSelectDistinct(true);
							sqlQueryObject.addSelectField("nome_porta");
							sqlQueryObject.setANDLogicOperator(true);
							sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE_SA+".id_servizio_applicativo=?");
							sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE+".id="+CostantiDB.PORTE_APPLICATIVE_SA+".id_porta");
							queryString = sqlQueryObject.createSQLQuery();
							stmt = con.prepareStatement(queryString);
							stmt.setLong(1, idServiziApplicativo);
							risultato = stmt.executeQuery();
							while (risultato.next()) {
								String nomePorta = risultato.getString("nome_porta");
								ResultPorta resultPorta = formatPortaApplicativa(nomePorta, tipoDB, con, normalizeObjectIds);
								if(resultPorta.mapping) {
									mapping_connettorePA_list.add(resultPorta.label);
								}
								else {
									connettorePA_list.add(resultPorta.label);
								}
								isInUso = true;
							}
							risultato.close();
							stmt.close();
							
						}
					}
					
					
					
					// Se ho rilevato che il token è in uso nelle porte delegate di default traduco questa informazione
					
					if(idFruizioniDefault!=null && !idFruizioniDefault.isEmpty()) {
						
						for (Long idFruizione : idFruizioniDefault) {
							
							sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
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
								isInUso = true;
							}
							risultato.close();
							stmt.close();
							
						}
						
					}
				
					
					
					// Se ho rilevato che il token è in uso nelle porte delegate di qualche gruppo traduco questa informazione
					
					if(idFruizioniGruppi_idFruizione!=null && !idFruizioniGruppi_idFruizione.isEmpty()) {
						
						for (Long idFruizione : idFruizioniGruppi_idFruizione) {
							
							sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
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
			// Chiudo statement and resultset
			try {
				try{
					if(risultato2!=null) risultato2.close();
					if(stmt2!=null) stmt2.close();
				}catch (Exception e) {
					//ignore
				}
				if (risultato != null) {
					risultato.close();
				}
				if (stmt != null) {
					stmt.close();
				}
			} catch (Exception e) {
				// ignore
			}
		}
	}


	public static String toString(IDGenericProperties idGP, Map<ErrorsHandlerCostant, List<String>> whereIsInUso, boolean prefix, String separator){
		return toString(idGP, whereIsInUso, prefix, separator," non eliminabile perch&egrave; :");
	}
	public static String toString(IDGenericProperties idGP, Map<ErrorsHandlerCostant, List<String>> whereIsInUso, boolean prefix, String separator, String intestazione){
		Set<ErrorsHandlerCostant> keys = whereIsInUso.keySet();
		String msg = "Token Policy '"+idGP.getNome()+"'" + intestazione+separator;
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
			
			case TOKEN_MAPPING_PD:
				if ( messages!=null && messages.size() > 0) {
					msg += "utilizzata nel controllo degli accessi per le Fruizioni: " + formatList(messages,separator) + separator;
				}
				break;
			case TOKEN_PD:
				if ( messages!=null && messages.size() > 0) {
					msg += "utilizzato nelle Porte Outbound (Controllo degli Accessi): " + formatList(messages,separator) + separator;
				}
				break;
			case TOKEN_MAPPING_PA:
				if ( messages!=null && messages.size() > 0) {
					msg += "utilizzato nel controllo degli accessi per le Erogazioni: " + formatList(messages,separator) + separator;
				}
				break;
			case TOKEN_PA:
				if ( messages!=null && messages.size() > 0) {
					msg += "utilizzato nelle Porte Inbound (Controllo degli Accessi): " + formatList(messages,separator) + separator;
				}
				break;
				
			case CONNETTORE_MAPPING_PD:
				if ( messages!=null && messages.size() > 0) {
					msg += "utilizzata nel connettore per le Fruizioni: " + formatList(messages,separator) + separator;
				}
				break;
			case CONNETTORE_PD:
				if ( messages!=null && messages.size() > 0) {
					msg += "utilizzato nelle Porte Outbound (Connettore): " + formatList(messages,separator) + separator;
				}
				break;
			case CONNETTORE_MAPPING_PA:
				if ( messages!=null && messages.size() > 0) {
					msg += "utilizzato nel connettore per le Erogazioni: " + formatList(messages,separator) + separator;
				}
				break;
			case CONNETTORE_PA:
				if ( messages!=null && messages.size() > 0) {
					msg += "utilizzato nelle Porte Inbound (Connettore): " + formatList(messages,separator) + separator;
				}
				break;
				
			default:
				msg += "utilizzato in oggetto non codificato ("+key+")"+separator;
				break;
			}

		}// chiudo for

		return msg;
	}
	
	
	
	
	
	
	

	
	// ***** CANALE ******
	
	public static String toString(CanaleConfigurazione canale, Map<ErrorsHandlerCostant, List<String>> whereIsInUso, boolean prefix, String separator){
		return toString(canale, whereIsInUso, prefix, separator," non eliminabile perch&egrave; :");
	}
	public static String toString(CanaleConfigurazione canale, Map<ErrorsHandlerCostant, List<String>> whereIsInUso, boolean prefix, String separator, String intestazione){
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
					msg += "associato all'API: " + formatList(messages,separator) + separator;
				}
				break;
			case IN_USO_IN_MAPPING_EROGAZIONE_PA:
				if ( messages!=null && messages.size() > 0) {
					msg += "utilizzato nelle Erogazioni: " + formatList(messages,separator) + separator;
				}
				break;
			case IN_USO_IN_MAPPING_FRUIZIONE_PD:
				if ( messages!=null && messages.size() > 0) {
					msg += "utilizzato nelle Fruizioni: " + formatList(messages,separator) + separator;
				}
				break;
			case IN_USO_IN_PORTE_DELEGATE:
				if ( messages!=null && messages.size() > 0 ) {
					msg += "in uso in Porte Delegate: " + formatList(messages,separator) + separator;
				}
				break;
			case IN_USO_IN_PORTE_APPLICATIVE:
				if ( messages!=null && messages.size() > 0 ) {
					msg += "in uso in Porte Applicative: " + formatList(messages,separator) + separator;
				}
				break;
			case IN_USO_IN_CANALE_NODO:
				if ( messages!=null && messages.size() > 0) {
					msg += "utilizzato nei Nodi: " + formatList(messages,separator) + separator;
				}
				break;
			default:
				msg += "utilizzato in oggetto non codificato ("+key+")"+separator;
				break;
			}

		}// chiudo for

		return msg;
	}
	public static boolean isCanaleInUsoRegistro(Connection con, String tipoDB, CanaleConfigurazione canale, Map<ErrorsHandlerCostant, List<String>> whereIsInUso, boolean normalizeObjectIds) throws UtilsException {
		return isCanaleInUso(con, tipoDB, canale, whereIsInUso, normalizeObjectIds, true, true, false);
	}
	public static boolean isCanaleInUso(Connection con, String tipoDB, CanaleConfigurazione canale, Map<ErrorsHandlerCostant, List<String>> whereIsInUso, boolean normalizeObjectIds) throws UtilsException {
		return isCanaleInUso(con, tipoDB, canale, whereIsInUso, normalizeObjectIds, true, true, true);
	}
	public static boolean isCanaleInUso(Connection con, String tipoDB, CanaleConfigurazione canale, Map<ErrorsHandlerCostant, List<String>> whereIsInUso, boolean normalizeObjectIds, boolean registry, boolean config, boolean nodi) throws UtilsException {
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
						accordi_list.add(getProtocolPrefix(protocollo)+NamingUtils.getLabelAccordoServizioParteComune(protocollo, idAccordo));
					}
					else {
	
						StringBuilder bf = new StringBuilder();
	
						bf.append(idReferenteObject.getTipo());
						bf.append("/");
						bf.append(idReferenteObject.getNome());
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
					ResultPorta resultPorta = formatPortaApplicativa(nome, tipoDB, con, normalizeObjectIds);
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
					ResultPorta resultPorta = formatPortaDelegata(nome, tipoDB, con, normalizeObjectIds);
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
				try{
					if(risultato2!=null) risultato2.close();
					if(stmt2!=null) stmt2.close();
				}catch (Exception e) {
					//ignore
				}
				if (risultato != null) {
					risultato.close();
				}
				if (stmt != null) {
					stmt.close();
				}
			} catch (Exception e) {
				// ignore
			}
		}
	}
}

class ResultPorta {
	boolean mapping = false;
	String label;
}
