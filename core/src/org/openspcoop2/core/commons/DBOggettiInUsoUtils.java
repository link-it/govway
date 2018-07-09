/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it). 
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


package org.openspcoop2.core.commons;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDAccordoCooperazione;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDRuolo;
import org.openspcoop2.core.id.IDScope;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
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




	// ***** PDD ******

	public static boolean isPddInUso(Connection con, String tipoDB, String nomePdd, List<String> whereIsInUso) throws UtilsException {
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
				whereIsInUso.add(tipo_soggetto + "/" + nome_soggetto);
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
				"- risulta associata ad uno o pi&ugrave; Soggetti: " + whereIsInUso.toString()+separator;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	// ***** RUOLI ******

	public static boolean isRuoloConfigInUso(Connection con, String tipoDB, IDRuolo idRuolo, Map<ErrorsHandlerCostant, List<String>> whereIsInUso) throws UtilsException {
		return _isRuoloInUso(con,tipoDB,idRuolo,false,true,whereIsInUso);
	}
	public static boolean isRuoloRegistryInUso(Connection con, String tipoDB, IDRuolo idRuolo, Map<ErrorsHandlerCostant, List<String>> whereIsInUso) throws UtilsException {
		return _isRuoloInUso(con,tipoDB,idRuolo,true,false,whereIsInUso);
	}
	public static boolean isRuoloInUso(Connection con, String tipoDB, IDRuolo idRuolo, Map<ErrorsHandlerCostant, List<String>> whereIsInUso) throws UtilsException {
		return _isRuoloInUso(con,tipoDB,idRuolo,true,true,whereIsInUso);
	}
	private static boolean _isRuoloInUso(Connection con, String tipoDB, IDRuolo idRuolo, boolean registry, boolean config, Map<ErrorsHandlerCostant, List<String>> whereIsInUso) throws UtilsException {
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
					soggetti_list.add(tipo_soggetto + "/" + nome_soggetto);
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
					servizi_applicativi_list.add(tipo_soggetto + "/" + nome_soggetto+"_"+nome);
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
				sqlQueryObject.addSelectField("tipo_soggetto");
				sqlQueryObject.addSelectField("nome_soggetto");
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
					String tipo_soggetto = risultato.getString("tipo_soggetto");
					String nome_soggetto = risultato.getString("nome_soggetto");
					String nome = risultato.getString("nome_porta");
					porte_applicative_list.add(tipo_soggetto + "/" + nome_soggetto+"_"+nome);
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
				sqlQueryObject.addSelectField("tipo_soggetto");
				sqlQueryObject.addSelectField("nome_soggetto");
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
					String tipo_soggetto = risultato.getString("tipo_soggetto");
					String nome_soggetto = risultato.getString("nome_soggetto");
					String nome = risultato.getString("nome_porta");
					porte_delegate_list.add(tipo_soggetto + "/" + nome_soggetto+"_"+nome);
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
						ct_list.add("Policy '"+alias+"' attiva nella porta '"+filtro_ruolo+"' '"+nomePorta+"' ");
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
		String msg = idRuolo.getNome() + intestazione+separator;
		if(prefix==false){
			msg = "";
		}
		for (ErrorsHandlerCostant key : keys) {
			List<String> messages = whereIsInUso.get(key);

			switch (key) {
			case IN_USO_IN_SOGGETTI:
				if ( messages!=null && messages.size() > 0) {
					msg += "- utilizzato nei Soggetti: " + messages.toString() + separator;
				}
				break;
			case IN_USO_IN_SERVIZI_APPLICATIVI:
				if ( messages!=null && messages.size() > 0) {
					msg += "- utilizzato nei Servizi Applicativi: " + messages.toString() + separator;
				}
				break;
			case IN_USO_IN_PORTE_APPLICATIVE:
				if ( messages!=null && messages.size() > 0) {
					msg += "- utilizzato nelle Porte Applicative: " + messages.toString() + separator;
				}
				break;
			case IN_USO_IN_PORTE_DELEGATE:
				if ( messages!=null && messages.size() > 0) {
					msg += "- utilizzato nelle Porte Delegate: " + messages.toString() + separator;
				}
				break;
			case CONTROLLO_TRAFFICO:
				if ( messages!=null && messages.size() > 0 ) {
					msg += "- utilizzato in policy di Rate Limiting: " + messages.toString() + separator;
				}
				break;
			default:
				msg += "- utilizzato in oggetto non codificato ("+key+")"+separator;
				break;
			}

		}// chiudo for

		return msg;
	}

	
	
	
	
	
	
	
	
	
	
	
	// ***** SCOPE ******

	// Lascio i metodi se servissero in futuro
	public static boolean isScopeConfigInUso(Connection con, String tipoDB, IDScope idScope, Map<ErrorsHandlerCostant, List<String>> whereIsInUso) throws UtilsException {
		return _isScopeInUso(con,tipoDB,idScope,false,true,whereIsInUso);
	}
//	public static boolean isScopeRegistryInUso(Connection con, String tipoDB, IDScope idScope, Map<ErrorsHandlerCostant, List<String>> whereIsInUso) throws UtilsException {
//		return _isScopeInUso(con,tipoDB,idScope,true,false,whereIsInUso);
//	}
	public static boolean isScopeInUso(Connection con, String tipoDB, IDScope idScope, Map<ErrorsHandlerCostant, List<String>> whereIsInUso) throws UtilsException {
		return _isScopeInUso(con,tipoDB,idScope,true,true,whereIsInUso);
	}
	private static boolean _isScopeInUso(Connection con, String tipoDB, IDScope idScope, boolean registry, boolean config, Map<ErrorsHandlerCostant, List<String>> whereIsInUso) throws UtilsException {
		String nomeMetodo = "_isScopeInUso";

		PreparedStatement stmt = null;
		ResultSet risultato = null;
		String queryString;

		try {

			//long idS = DBUtils.getIdScope(idScope, con, tipoDB);
			
			boolean isInUso = false;
			
			List<String> porte_applicative_list = whereIsInUso.get(ErrorsHandlerCostant.IN_USO_IN_PORTE_APPLICATIVE);
			List<String> porte_delegate_list = whereIsInUso.get(ErrorsHandlerCostant.IN_USO_IN_PORTE_DELEGATE);
			
			if (porte_applicative_list == null) {
				porte_applicative_list = new ArrayList<String>();
				whereIsInUso.put(ErrorsHandlerCostant.IN_USO_IN_PORTE_APPLICATIVE, porte_applicative_list);
			}
			if (porte_delegate_list == null) {
				porte_delegate_list = new ArrayList<String>();
				whereIsInUso.put(ErrorsHandlerCostant.IN_USO_IN_PORTE_DELEGATE, porte_delegate_list);
			}
			

			// Controllo che il scope non sia in uso nelle porte applicative
			if(config){
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_SCOPE);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE);
				sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
				sqlQueryObject.addSelectField("tipo_soggetto");
				sqlQueryObject.addSelectField("nome_soggetto");
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
					String tipo_soggetto = risultato.getString("tipo_soggetto");
					String nome_soggetto = risultato.getString("nome_soggetto");
					String nome = risultato.getString("nome_porta");
					porte_applicative_list.add(tipo_soggetto + "/" + nome_soggetto+"_"+nome);
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
				sqlQueryObject.addSelectField("tipo_soggetto");
				sqlQueryObject.addSelectField("nome_soggetto");
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
					String tipo_soggetto = risultato.getString("tipo_soggetto");
					String nome_soggetto = risultato.getString("nome_soggetto");
					String nome = risultato.getString("nome_porta");
					porte_delegate_list.add(tipo_soggetto + "/" + nome_soggetto+"_"+nome);
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
		String msg = idScope.getNome() + intestazione+separator;
		if(prefix==false){
			msg = "";
		}
		for (ErrorsHandlerCostant key : keys) {
			List<String> messages = whereIsInUso.get(key);

			switch (key) {
			case IN_USO_IN_PORTE_APPLICATIVE:
				if ( messages!=null && messages.size() > 0) {
					msg += "- utilizzato nelle Porte Applicative: " + messages.toString() + separator;
				}
				break;
			case IN_USO_IN_PORTE_DELEGATE:
				if ( messages!=null && messages.size() > 0) {
					msg += "- utilizzato nelle Porte Delegate: " + messages.toString() + separator;
				}
				break;
			default:
				msg += "- utilizzato in oggetto non codificato ("+key+")"+separator;
				break;
			}

		}// chiudo for

		return msg;
	}
	
	
	
	
	
	
	





	// ***** SOGGETTI ******

	public static boolean isSoggettoConfigInUso(Connection con, String tipoDB, IDSoggetto idSoggettoConfig, boolean checkControlloTraffico, Map<ErrorsHandlerCostant, List<String>> whereIsInUso) throws UtilsException {
		return isSoggettoInUso(con,tipoDB,idSoggettoConfig,null,checkControlloTraffico, whereIsInUso);
	}
	public static boolean isSoggettoRegistryInUso(Connection con, String tipoDB, IDSoggetto idSoggettoRegistro, boolean checkControlloTraffico, Map<ErrorsHandlerCostant, List<String>> whereIsInUso) throws UtilsException {
		return isSoggettoInUso(con,tipoDB,null,idSoggettoRegistro,checkControlloTraffico, whereIsInUso);
	}
	private static boolean isSoggettoInUso(Connection con, String tipoDB, IDSoggetto idSoggettoConfig, IDSoggetto idSoggettoRegistro, boolean checkControlloTraffico, Map<ErrorsHandlerCostant, List<String>> whereIsInUso) throws UtilsException {
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
			List<String> accordi_list = whereIsInUso.get(ErrorsHandlerCostant.IS_REFERENTE);
			List<String> accordi_coop_list = whereIsInUso.get(ErrorsHandlerCostant.IS_REFERENTE_COOPERAZIONE);
			List<String> partecipanti_list = whereIsInUso.get(ErrorsHandlerCostant.IS_PARTECIPANTE_COOPERAZIONE);
			List<String> utenti_list = whereIsInUso.get(ErrorsHandlerCostant.UTENTE);
			List<String> ct_list = whereIsInUso.get(ErrorsHandlerCostant.CONTROLLO_TRAFFICO);

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

			if(idSoggettoRegistro!=null){

				// controllo che non sia fruitore di un servizio
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI_FRUITORI);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI);
				sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
				sqlQueryObject.addSelectField("tipo_servizio");
				sqlQueryObject.addSelectField("nome_servizio");
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

					servizi_fruitori_list.add(risultato.getString("tipo_soggetto")+"/"+risultato.getString("nome_soggetto")+
							"_"+
							risultato.getString("tipo_servizio")+"/"+risultato.getString("nome_servizio"));

					isInUso = true;

				}
				risultato.close();
				stmt.close();

			}

			// Controllo che il soggetto non sia in uso nei servizi applicativi
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("id_soggetto = ?");
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idSoggetto);
			risultato = stmt.executeQuery();
			while (risultato.next()) {
				servizi_applicativi_list.add(risultato.getString("nome"));// nome
				// del
				// servizio
				// applicativo

				isInUso = true;
			}
			risultato.close();
			stmt.close();


			if(idSoggettoRegistro!=null){
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
					servizi_list.add(idSoggettoRegistro.toString()+
							"_"+tipo_servizio + "/" + nome_servizio);
					isInUso = true;
				}
				risultato.close();
				stmt.close();
			}

			// controllo porte delegate sia per id che per tipo e
			// nome
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("id_soggetto_erogatore = ?");
			sqlQueryObject.addWhereCondition(true, "tipo_soggetto_erogatore = ?", "nome_soggetto_erogatore = ?");
			sqlQueryObject.addWhereCondition("id_soggetto = ?");
			sqlQueryObject.setANDLogicOperator(false);
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idSoggetto);// controllo se soggetto
			// erogatore di porte delegate
			stmt.setString(2, tipoSoggetto);// controllo se soggetto
			// erogatore di porte
			// delegate
			stmt.setString(3, nomeSoggetto);// controllo se soggetto
			// erogatore di porte
			// delegate

			stmt.setLong(4, idSoggetto);// controllo se soggetto
			// proprietario di porte
			// delegate

			risultato = stmt.executeQuery();
			while (risultato.next()) {
				porte_delegate_list.add(risultato.getString("nome_porta"));// nome
				// porta
				// delegata
				isInUso = true;
			}
			risultato.close();
			stmt.close();



			// controllo se in uso in porte applicative come proprietario della
			// porta
			// o come soggetto virtuale
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("id_soggetto = ?");
			sqlQueryObject.addWhereCondition("id_soggetto_virtuale = ?");
			sqlQueryObject.addWhereCondition(true, "tipo_soggetto_virtuale = ?", "nome_soggetto_virtuale = ?");
			sqlQueryObject.setANDLogicOperator(false);
			queryString = sqlQueryObject.createSQLQuery();

			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idSoggetto);
			stmt.setLong(2, idSoggetto);
			stmt.setString(3, tipoSoggetto);
			stmt.setString(4, nomeSoggetto);
			risultato = stmt.executeQuery();
			while (risultato.next()) {
				porte_applicative_list.add(risultato.getString("nome_porta"));
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
					isInUso=true;

					String nomeAccordo = risultato.getString("nome");
					String versione = risultato.getString("versione");

					StringBuffer bf = new StringBuffer();
					bf.append(idSoggettoRegistro.toString());
					bf.append(":");
					bf.append(nomeAccordo);
					if(versione!=null && !"".equals(versione)){
						bf.append(":");
						bf.append(versione);
					}
					accordi_list.add(bf.toString());
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
					isInUso=true;

					String nomeAccordo = risultato.getString("nome");
					String versione = risultato.getString("versione");

					StringBuffer bf = new StringBuffer();
					bf.append(idSoggettoRegistro.toString());
					bf.append(":");
					bf.append(nomeAccordo);
					if(versione!=null && !"".equals(versione)){
						bf.append(":");
						bf.append(versione);
					}

					accordi_coop_list.add(bf.toString());
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
					isInUso=true;

					String nomeAccordo = risultato.getString("nome");
					String versione = risultato.getString("versione");

					StringBuffer bf = new StringBuffer();
					bf.append(idSoggettoRegistro.toString());
					bf.append(":");
					bf.append(nomeAccordo);
					if(versione!=null && !"".equals(versione)){
						bf.append(":");
						bf.append(versione);
					}

					partecipanti_list.add(bf.toString());
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
			sqlQueryObject.addWhereCondition(CostantiDB.USERS+".id = "+CostantiDB.USERS_SOGGETTI+".id_soggetto");
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
					ct_list.add("Policy '"+alias+"' attiva nella porta '"+filtro_ruolo+"' '"+nomePorta+"' ");
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

	public static String toString(IDSoggetto idSoggetto, Map<ErrorsHandlerCostant, List<String>> whereIsInUso, boolean prefix, String separator){
		Set<ErrorsHandlerCostant> keys = whereIsInUso.keySet();
		String msg = idSoggetto.toString() + " non eliminabile perch&egrave; :"+separator;
		if(prefix==false){
			msg = "";
		}
		for (ErrorsHandlerCostant key : keys) {
			List<String> messages = whereIsInUso.get(key);

			switch (key) {
			case IS_FRUITORE:
				if ( messages!=null && messages.size() > 0) {
					msg += "- fruitore di Servizi: " + messages.toString() + separator;
				}
				break;
			case IN_USO_IN_SERVIZI_APPLICATIVI:
				if ( messages!=null && messages.size() > 0 ) {
					msg += "- in uso in Servizi Applicativi: " + messages.toString() + separator;
				}
				break;
			case IN_USO_IN_SERVIZI:
				if ( messages!=null && messages.size() > 0) {
					msg += "- erogatore di Servizi: " + messages.toString() + separator;
				}
				break;
			case IN_USO_IN_PORTE_DELEGATE:
				if ( messages!=null && messages.size() > 0 ) {
					msg += "- in uso in Porte Delegate: " + messages.toString() + separator;
				}
				break;
			case IN_USO_IN_PORTE_APPLICATIVE:
				if ( messages!=null && messages.size() > 0 ) {
					msg += "- in uso in Porte Applicative: " + messages.toString() + separator;
				}
				break;
			case IS_REFERENTE:
				if ( messages!=null && messages.size() > 0 ) {
					msg += "- referente in Accordi di Servizio Parte Comune: " + messages.toString() + separator;
				}
				break;
			case IS_REFERENTE_COOPERAZIONE:
				if ( messages!=null && messages.size() > 0 ) {
					msg += "- referente in Accordi di Cooperazione: " + messages.toString() + separator;
				}
				break;
			case IS_PARTECIPANTE_COOPERAZIONE:
				if ( messages!=null && messages.size() > 0 ) {
					msg += "- partecipante in Accordi di Cooperazione: " + messages.toString() + separator;
				}
				break;
			case UTENTE:
				if ( messages!=null && messages.size() > 0 ) {
					msg += "- associato ad Utenti: " + messages.toString() + separator;
				}
				break;
			case CONTROLLO_TRAFFICO:
				if ( messages!=null && messages.size() > 0 ) {
					msg += "- utilizzato in policy di Rate Limiting: " + messages.toString() + separator;
				}
				break;
			default:
				msg += "- utilizzato in oggetto non codificato ("+key+")"+separator;
				break;
			}

		}// chiudo for

		return msg;
	}














	// ***** ACCORDI DI COOPERAZIONE ******

	public static boolean isAccordoCooperazioneInUso(Connection con, String tipoDB, IDAccordoCooperazione idAccordo, 
			Map<ErrorsHandlerCostant,List<String>> whereIsInUso) throws UtilsException {
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
				String versione = risultato.getString("versione");
				long idReferente = risultato.getLong("id_referente");

				StringBuffer bf = new StringBuffer();
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
						bf.append(risultato2.getString("tipo_soggetto"));
						bf.append("/");
						bf.append(risultato2.getString("nome_soggetto"));
						bf.append(":");
					}
					risultato2.close(); risultato2=null;
					stmt2.close(); stmt2=null;

				}

				bf.append(nomeAccordo);

				if(versione!=null && !"".equals(versione)){
					bf.append(":");
					bf.append(versione);
				}

				accordi_list.add(bf.toString());
				
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

	public static String toString(IDAccordoCooperazione idAccordo, Map<ErrorsHandlerCostant, List<String>> whereIsInUso, boolean prefix, String separator){

		StringBuffer bf = new StringBuffer();
		bf.append(idAccordo.toString());

		Set<ErrorsHandlerCostant> keys = whereIsInUso.keySet();
		String msg = bf.toString() + " non eliminabile perch&egrave; :"+separator;
		if(prefix==false){
			msg = "";
		}
		for (ErrorsHandlerCostant key : keys) {
			List<String> messages = whereIsInUso.get(key);

			switch (key) {
			case IN_USO_IN_ACCORDI:
				if ( messages!=null && messages.size() > 0) {
					msg += "- riferito da Accordi di Servizio Composti: " + messages.toString() + separator;
				}
				break;
			default:
				msg += "- utilizzato in oggetto non codificato ("+key+")"+separator;
				break;
			}

		}// chiudo for

		return msg;
	}











	// ***** ACCORDI DI SERVIZIO PARTE COMUNE ******

	public static boolean isAccordoServizioParteComuneInUso(Connection con, String tipoDB, IDAccordo idAccordo, 
			Map<ErrorsHandlerCostant,List<String>> whereIsInUso) throws UtilsException {
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

			if (servizi_list == null) {
				servizi_list = new ArrayList<String>();
				whereIsInUso.put(ErrorsHandlerCostant.IN_USO_IN_SERVIZI, servizi_list);
			}

			//List<String> nomiServiziApplicativi = new ArrayList<String>();
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
			while (risultato.next()){
				isInUso=true;
				String nomeServizio = risultato.getString("tipo_soggetto")+"/"+risultato.getString("nome_soggetto")+
						"_"+
						risultato.getString("tipo_servizio")+"/"+risultato.getString("nome_servizio");
				servizi_list.add(nomeServizio);
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
		}
	}


	public static String toString(IDAccordo idAccordo, Map<ErrorsHandlerCostant, List<String>> whereIsInUso, boolean prefix, String separator){

		StringBuffer bf = new StringBuffer();
		bf.append(idAccordo.toString());

		Set<ErrorsHandlerCostant> keys = whereIsInUso.keySet();
		String msg = bf.toString() + " non eliminabile perch&egrave; :"+separator;
		if(prefix==false){
			msg = "";
		}
		for (ErrorsHandlerCostant key : keys) {
			List<String> messages = whereIsInUso.get(key);

			switch (key) {
			case IN_USO_IN_SERVIZI:
				if ( messages!=null && messages.size() > 0) {
					msg += "- implementato dai Servizi: " + messages.toString() + separator;
				}
				break;
			default:
				msg += "- utilizzato in oggetto non codificato ("+key+")"+separator;
				break;
			}

		}// chiudo for

		return msg;
	}



	
	
	
	
	
	
	
	// ***** ACCORDI DI SERVIZIO PARTE SPECIFICA ******

	private static boolean isAccordoServizioParteSpecificaInUso(Connection con, String tipoDB, long idAccordoServizioParteSpecifica, 
			Map<ErrorsHandlerCostant,List<String>> whereIsInUso, String nomeMetodo,
			List<IDPortaApplicativa> nomePAGenerateAutomaticamente) throws UtilsException {
		
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
					isInUso=true;
					porteApplicative_list.add(nomePorta);
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
				isInUso=true;
				
				String nomePorta = risultato.getString("nome_porta");
				porteDelegate_list.add(nomePorta);
			}
			risultato.close();
			stmt.close();
			
			
			
			
			
			
			// fruitori
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
				isInUso=true;
				
				String tipoSoggettoFruitore = risultato.getString("tipo_soggetto");
				String nomeSoggettoFruitore = risultato.getString("nome_soggetto");
				fruitori_list.add(tipoSoggettoFruitore+"/"+nomeSoggettoFruitore);
			}
			risultato.close();
			stmt.close();
			
			
			
			
			
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
				String versione = risultato.getString("versione");
				long idReferente = risultato.getLong("id_referente");

				StringBuffer bf = new StringBuffer();
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
						bf.append(risultato2.getString("tipo_soggetto"));
						bf.append("/");
						bf.append(risultato2.getString("nome_soggetto"));
						bf.append(":");
					}
					risultato2.close(); risultato2=null;
					stmt2.close(); stmt2=null;

				}
				
				bf.append(nomeAccordo);

				if(versione!=null && !"".equals(versione)){
					bf.append(":");
					bf.append(versione);
				}

				servizioComponente_list.add(bf.toString());
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
				String tipo_soggetto = risultato.getString("tipo_soggetto");
				String nome_soggetto = risultato.getString("nome_soggetto");
				String nome = risultato.getString("nome_porta");
				IDPortaApplicativa idPA = new IDPortaApplicativa();
				idPA.setNome(nome);
				if(porteApplicative_list.contains(nome)==false && 
						nomePAGenerateAutomaticamente!=null && 
						!nomePAGenerateAutomaticamente.contains(idPA)){
					mappingErogazionePA_list.add("("+tipo_soggetto + "/" + nome_soggetto+")_"+nome);
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
			sqlQueryObject.setANDLogicOperator(true);
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idAccordoServizioParteSpecifica);
			risultato = stmt.executeQuery();
			while (risultato.next()) {
				String tipo_soggetto = risultato.getString("tipo_soggetto");
				String nome_soggetto = risultato.getString("nome_soggetto");
				String nome = risultato.getString("nome_porta");
				if(porteDelegate_list.contains(nome)==false){
					mappingFruizionePD_list.add("("+tipo_soggetto + "/" + nome_soggetto+")_"+nome);
					isInUso=true;
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
			sqlQueryObject.addWhereCondition(CostantiDB.USERS+".id = "+CostantiDB.USERS_SERVIZI+".id_servizio");
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
						ct_list.add("Policy '"+alias+"' attiva nella porta '"+filtro_ruolo+"' '"+nomePorta+"' ");
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
			List<IDPortaApplicativa> nomePAGenerateAutomaticamente) throws UtilsException {
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
		return isAccordoServizioParteSpecificaInUso(con, tipoDB, idAccordoServizioParteSpecifica, whereIsInUso,nomeMetodo,nomePAGenerateAutomaticamente);
	}
	public static String toString(IDServizio idServizio, Map<ErrorsHandlerCostant, List<String>> whereIsInUso, boolean prefix, String separator){
		
		Set<ErrorsHandlerCostant> keys = whereIsInUso.keySet();
		String msg = idServizio.toString() + " non eliminabile perch&egrave; :"+separator;
		if(prefix==false){
			msg = "";
		}
		for (ErrorsHandlerCostant key : keys) {
			List<String> messages = whereIsInUso.get(key);

			switch (key) {
			case IN_USO_IN_PORTE_DELEGATE:
				if ( messages!=null && messages.size() > 0 ) {
					msg += "- in uso in Porte Delegate: " + messages.toString() + separator;
				}
				break;
			case IN_USO_IN_PORTE_APPLICATIVE:
				if ( messages!=null && messages.size() > 0 ) {
					msg += "- in uso in Porte Applicative: " + messages.toString() + separator;
				}
				break;
			case POSSIEDE_FRUITORI:
				if ( messages!=null && messages.size() > 0 ) {
					msg += "- fruito dai soggetti: " + messages.toString() + separator;
				}
				break;
			case IS_SERVIZIO_COMPONENTE_IN_ACCORDI:
				if ( messages!=null && messages.size() > 0 ) {
					msg += "- associato come servizio componente degli Accordi Servizio Composti: " + messages.toString() + separator;
				}
				break;
			case IN_USO_IN_MAPPING_EROGAZIONE_PA:
				if ( messages!=null && messages.size() > 0 ) {
					msg += "- associato alla Porta Applicativa: " + messages.toString() + separator;
				}
				break;
			case IN_USO_IN_MAPPING_FRUIZIONE_PD:
				if ( messages!=null && messages.size() > 0 ) {
					msg += "- associato alle Porte Delegate: " + messages.toString() + separator;
				}
				break;
			case UTENTE:
				if ( messages!=null && messages.size() > 0 ) {
					msg += "- associato ad Utenti: " + messages.toString() + separator;
				}
				break;
			case CONTROLLO_TRAFFICO:
				if ( messages!=null && messages.size() > 0 ) {
					msg += "- utilizzato in policy di Rate Limiting: " + messages.toString() + separator;
				}
				break;
			default:
				msg += "- utilizzato in oggetto non codificato ("+key+")"+separator;
				break;
			}

		}// chiudo for

		return msg;
	}
	
	
	
	
	
	
	



	// ***** SERVIZI APPLICATIVI ******

	public static boolean isServizioApplicativoInUso(Connection con, String tipoDB, IDServizioApplicativo idServizioApplicativo, 
			Map<ErrorsHandlerCostant, List<String>> whereIsInUso, boolean isRegistroServiziLocale) throws UtilsException {

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


			List<String> porte_delegate_list = whereIsInUso.get(ErrorsHandlerCostant.IN_USO_IN_PORTE_DELEGATE);
			List<String> porte_applicative_list = whereIsInUso.get(ErrorsHandlerCostant.IN_USO_IN_PORTE_APPLICATIVE);
			List<String> ct_list = whereIsInUso.get(ErrorsHandlerCostant.CONTROLLO_TRAFFICO);

			if (porte_delegate_list == null) {
				porte_delegate_list = new ArrayList<String>();
				whereIsInUso.put(ErrorsHandlerCostant.IN_USO_IN_PORTE_DELEGATE, porte_delegate_list);
			}
			if (porte_applicative_list == null) {
				porte_applicative_list = new ArrayList<String>();
				whereIsInUso.put(ErrorsHandlerCostant.IN_USO_IN_PORTE_APPLICATIVE, porte_applicative_list);
			}
			if (ct_list == null) {
				ct_list = new ArrayList<String>();
				whereIsInUso.put(ErrorsHandlerCostant.CONTROLLO_TRAFFICO, ct_list);
			}


			// Porte delegate
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE_SA);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.setANDLogicOperator(true);
			sqlQueryObject.addWhereCondition(CostantiDB.PORTE_DELEGATE_SA+".id_porta="+CostantiDB.PORTE_DELEGATE+".id");
			sqlQueryObject.addWhereCondition("id_servizio_applicativo=?");
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idServizioApplicativoLong);
			risultato = stmt.executeQuery();
			while (risultato.next()){
				isInUso = true;

				porte_delegate_list.add(risultato.getString("nome_porta"));
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
				isInUso = true;

				porte_applicative_list.add(risultato.getString("nome_porta"));
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
					ct_list.add("Policy '"+alias+"' attiva nella porta '"+filtro_ruolo+"' '"+nomePorta+"' ");
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

	public static String toString(IDServizioApplicativo idServizioApplicativo, Map<ErrorsHandlerCostant, List<String>> whereIsInUso, boolean prefix, String separator){
		Set<ErrorsHandlerCostant> keys = whereIsInUso.keySet();
		String msg = idServizioApplicativo.getIdSoggettoProprietario().toString()+"_"+idServizioApplicativo.getNome() + " non eliminabile perch&egrave; :"+separator;
		if(prefix==false){
			msg = "";
		}
		for (ErrorsHandlerCostant key : keys) {
			List<String> messages = whereIsInUso.get(key);

			switch (key) {
			case IN_USO_IN_PORTE_DELEGATE:
				if ( messages!=null && messages.size() > 0 ) {
					msg += "- in uso in Porte Delegate: " + messages.toString() + separator;
				}
				break;
			case IN_USO_IN_PORTE_APPLICATIVE:
				if ( messages!=null && messages.size() > 0 ) {
					msg += "- in uso in Porte Applicative: " + messages.toString() + separator;
				}
				break;
			case CONTROLLO_TRAFFICO:
				if ( messages!=null && messages.size() > 0 ) {
					msg += "- utilizzato in policy di Rate Limiting: " + messages.toString() + separator;
				}
				break;
			default:
				msg += "- utilizzato in oggetto non codificato ("+key+")"+separator;
				break;
			}

		}// chiudo for

		return msg;
	}
}
