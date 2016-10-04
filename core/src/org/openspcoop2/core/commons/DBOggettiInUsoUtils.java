/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it). 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDAccordoCooperazione;
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





	// ***** SOGGETTI ******

	public static boolean isSoggettoConfigInUso(Connection con, String tipoDB, IDSoggetto idSoggettoConfig, Map<ErrorsHandlerCostant, List<String>> whereIsInUso) throws UtilsException {
		return isSoggettoInUso(con,tipoDB,idSoggettoConfig,null,whereIsInUso);
	}
	public static boolean isSoggettoRegistryInUso(Connection con, String tipoDB, IDSoggetto idSoggettoRegistro, Map<ErrorsHandlerCostant, List<String>> whereIsInUso) throws UtilsException {
		return isSoggettoInUso(con,tipoDB,null,idSoggettoRegistro,whereIsInUso);
	}
	private static boolean isSoggettoInUso(Connection con, String tipoDB, IDSoggetto idSoggettoConfig, IDSoggetto idSoggettoRegistro, Map<ErrorsHandlerCostant, List<String>> whereIsInUso) throws UtilsException {
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
		bf.append(idAccordo.getNome());
		if(idAccordo.getVersione()!=null){
			bf.append(":");
			bf.append(idAccordo.getVersione());
		}

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

	public static boolean isAccordoServizioParteComuneInUso_soloOggettiRegistro(Connection con, String tipoDB, IDAccordo idAccordo, 
			Map<ErrorsHandlerCostant,List<String>> whereIsInUso) throws UtilsException {
		String nomeMetodo = "isAccordoServizioParteComuneInUso_soloOggettiRegistro";

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

			//Vector<String> nomiServiziApplicativi = new Vector<String>();
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

	public static boolean isAccordoServizioParteComuneInUso(Connection con, String tipoDB, IDAccordo idAccordo, 
			Map<ErrorsHandlerCostant, List<String>> whereIsInUso) throws UtilsException {
		String nomeMetodo = "isAccordoServizioParteComuneInUso";

		PreparedStatement stmt = null;
		ResultSet risultato = null;
		PreparedStatement stmt2 = null;
		ResultSet risultato2 = null;
		String queryString;
		try {
			boolean isInUso = false;

			// controllo utilizzando il driver registro
			isInUso = isAccordoServizioParteComuneInUso_soloOggettiRegistro(con, tipoDB, idAccordo, whereIsInUso);

			long idAccordoServizioParteComune = DBUtils.getIdAccordoServizioParteComune(idAccordo, con, tipoDB);
			if(idAccordoServizioParteComune<=0){
				throw new UtilsException("Accordi di Servizio Parte Comune con id ["+idAccordo.toString()+"] non trovato");
			}

			List<String> saRuoli_list = whereIsInUso.get(ErrorsHandlerCostant.POSSIEDE_RUOLI);

			if (saRuoli_list == null) {
				saRuoli_list = new ArrayList<String>();
				whereIsInUso.put(ErrorsHandlerCostant.POSSIEDE_RUOLI, saRuoli_list);
			}

			// controllo servizi applicativi
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.RUOLI_SA);
			sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition(CostantiDB.RUOLI_SA + ".id_accordo = ?");
			sqlQueryObject.addWhereCondition(CostantiDB.RUOLI_SA + ".id_servizio_applicativo = " + CostantiDB.SERVIZI_APPLICATIVI + ".id");
			sqlQueryObject.setANDLogicOperator(true);
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idAccordoServizioParteComune);
			risultato = stmt.executeQuery();
			while (risultato.next()) {
				isInUso = true;
				String nomeSA = risultato.getString("nome");

				long idSoggettoProprietario = risultato.getLong("id_soggetto");
				String tipoSoggettoProprietario = null;
				String nomeSoggettoProprietario = null;
				if(idSoggettoProprietario>0){
					ISQLQueryObject sqlQueryObjectErogatore = SQLObjectFactory.createSQLQueryObject(tipoDB);
					sqlQueryObjectErogatore.addFromTable(CostantiDB.SOGGETTI);
					sqlQueryObjectErogatore.addSelectField("*");
					sqlQueryObjectErogatore.addWhereCondition("id=?");
					sqlQueryObjectErogatore.setANDLogicOperator(true);
					String queryStringProprietario = sqlQueryObjectErogatore.createSQLQuery();
					stmt2 = con.prepareStatement(queryStringProprietario);
					stmt2.setLong(1, idSoggettoProprietario);
					risultato2 = stmt2.executeQuery();
					if(risultato2.next()){
						tipoSoggettoProprietario = risultato2.getString("tipo_soggetto");
						nomeSoggettoProprietario = risultato2.getString("nome_soggetto");
					}
					risultato2.close(); risultato2=null;
					stmt2.close(); stmt2=null;
				}

				saRuoli_list.add(tipoSoggettoProprietario+"/"+nomeSoggettoProprietario+"_"+nomeSA);
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

	public static String toString(IDAccordo idAccordo, Map<ErrorsHandlerCostant, List<String>> whereIsInUso, boolean prefix, String separator){

		StringBuffer bf = new StringBuffer();
		if(idAccordo.getSoggettoReferente()!=null){
			bf.append(idAccordo.getSoggettoReferente().toString());
			bf.append(":");
		}
		bf.append(idAccordo.getNome());
		if(idAccordo.getVersione()!=null){
			bf.append(":");
			bf.append(idAccordo.getVersione());
		}

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
			case POSSIEDE_RUOLI:
				if ( messages!=null && messages.size() > 0 ) {
					msg += "- usato come ruolo dai Servizi Applicativi: " + messages.toString() + separator;
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
			String nomePAGenerataAutomaticamente) throws UtilsException {
		
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

			
			
			// Raccolgo Dati Servizio.
			String tipoServizio = null;
			String nomeServizio = null;
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
			stmt.setLong(1, idAccordoServizioParteSpecifica);
			risultato = stmt.executeQuery();
			if (risultato.next()) {
				tipoServizio = risultato.getString("tipo_servizio");
				nomeServizio = risultato.getString("nome_servizio");
				tipoSoggetto = risultato.getString("tipo_soggetto");
				nomeSoggetto = risultato.getString("nome_soggetto");
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
			sqlQueryObject.addWhereCondition(false, "id_servizio = ?", "tipo_servizio = ? AND servizio = ?");
			sqlQueryObject.addWhereCondition(false, "id_soggetto = ?", "id_soggetto_virtuale = ?", "tipo_soggetto_virtuale = ? AND nome_soggetto_virtuale = ?");
			sqlQueryObject.setANDLogicOperator(true);
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idAccordoServizioParteSpecifica);
			stmt.setString(2, tipoServizio);
			stmt.setString(3, nomeServizio);
			stmt.setLong(4, idSoggetto);
			stmt.setLong(5, idSoggetto);
			stmt.setString(6, tipoSoggetto);
			stmt.setString(7, nomeSoggetto);
			risultato = stmt.executeQuery();
			while (risultato.next()) {
				
				String nomePorta = risultato.getString("nome_porta");
				if(!nomePorta.equals(nomePAGenerataAutomaticamente)){
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
			sqlQueryObject.addWhereCondition(false, "id_servizio = ?", "tipo_servizio = ? AND nome_servizio = ?");
			sqlQueryObject.addWhereCondition(false, "id_soggetto_erogatore = ?", "tipo_soggetto_erogatore = ? AND nome_soggetto_erogatore = ?");
			sqlQueryObject.setANDLogicOperator(true);
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idAccordoServizioParteSpecifica);
			stmt.setString(2, tipoServizio);
			stmt.setString(3, nomeServizio);
			stmt.setLong(4, idSoggetto);
			stmt.setString(5, tipoSoggetto);
			stmt.setString(6, nomeSoggetto);
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
			//Vector<String> nomiServiziApplicativi = new Vector<String>();
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
	
	public static boolean isAccordoServizioParteSpecificaInUso(Connection con, String tipoDB, IDServizio idServizio, 
			Map<ErrorsHandlerCostant,List<String>> whereIsInUso,
			String nomePAGenerataAutomaticamente) throws UtilsException {
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
		return isAccordoServizioParteSpecificaInUso(con, tipoDB, idAccordoServizioParteSpecifica, whereIsInUso,nomeMetodo,nomePAGenerataAutomaticamente);
	}
	
	public static boolean isAccordoServizioParteSpecificaInUso(Connection con, String tipoDB, IDAccordo idAccordo, 
			Map<ErrorsHandlerCostant,List<String>> whereIsInUso,
			String nomePAGenerataAutomaticamente) throws UtilsException {
		String nomeMetodo = "isAccordoServizioParteSpecificaInUso(IDAccordo)";
		long idAccordoServizioParteSpecifica = -1;
		try {
			idAccordoServizioParteSpecifica = DBUtils.getIdAccordoServizioParteSpecifica(idAccordo, con, tipoDB);
			if(idAccordoServizioParteSpecifica<=0){
				throw new UtilsException("Accordi di Servizio Parte Specifica con id ["+idAccordo.toString()+"] non trovato");
			}
		}catch (Exception se) {
			throw new UtilsException("[DBOggettiInUsoUtils::" + nomeMetodo + "] Exception: " + se.getMessage(),se);
		}
		return isAccordoServizioParteSpecificaInUso(con, tipoDB, idAccordoServizioParteSpecifica, whereIsInUso,nomeMetodo,nomePAGenerataAutomaticamente);
	}

	public static String toString(IDServizio idServizio, Map<ErrorsHandlerCostant, List<String>> whereIsInUso, boolean prefix, String separator){
		StringBuffer bf = new StringBuffer();
		bf.append(idServizio.getSoggettoErogatore().toString());
		bf.append("_");
		bf.append(idServizio.getTipoServizio());
		bf.append("/");
		bf.append(idServizio.getServizio());
		return _toStringServizio(bf.toString(), whereIsInUso,prefix,separator);
	}
	public static String toStringAccordoServizioParteSpecifica(IDAccordo idAccordo, Map<ErrorsHandlerCostant, List<String>> whereIsInUso, boolean prefix, String separator){
		StringBuffer bf = new StringBuffer();
		if(idAccordo.getSoggettoReferente()!=null){
			bf.append(idAccordo.getSoggettoReferente().toString());
			bf.append(":");
		}
		bf.append(idAccordo.getNome());
		if(idAccordo.getVersione()!=null){
			bf.append(":");
			bf.append(idAccordo.getVersione());
		}
		return _toStringServizio(bf.toString(), whereIsInUso,prefix,separator);
	}
	private static String _toStringServizio(String nome, Map<ErrorsHandlerCostant, List<String>> whereIsInUso, boolean prefix, String separator){

		Set<ErrorsHandlerCostant> keys = whereIsInUso.keySet();
		String msg = nome + " non eliminabile perch&egrave; :"+separator;
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
			List<String> ruoli_list = whereIsInUso.get(ErrorsHandlerCostant.POSSIEDE_RUOLI);
			List<String> politiche_sicurezza_list = whereIsInUso.get(ErrorsHandlerCostant.UTILIZZATO_IN_POLITICHE_SICUREZZA);

			if (porte_delegate_list == null) {
				porte_delegate_list = new ArrayList<String>();
				whereIsInUso.put(ErrorsHandlerCostant.IN_USO_IN_PORTE_DELEGATE, porte_delegate_list);
			}
			if (porte_applicative_list == null) {
				porte_applicative_list = new ArrayList<String>();
				whereIsInUso.put(ErrorsHandlerCostant.IN_USO_IN_PORTE_APPLICATIVE, porte_applicative_list);
			}
			if (ruoli_list == null) {
				ruoli_list = new ArrayList<String>();
				whereIsInUso.put(ErrorsHandlerCostant.POSSIEDE_RUOLI, ruoli_list);
			}
			if (politiche_sicurezza_list == null) {
				politiche_sicurezza_list = new ArrayList<String>();
				whereIsInUso.put(ErrorsHandlerCostant.UTILIZZATO_IN_POLITICHE_SICUREZZA, politiche_sicurezza_list);
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



			if(isRegistroServiziLocale){

				// ruolo
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.RUOLI_SA);
				sqlQueryObject.addFromTable(CostantiDB.ACCORDI);
				sqlQueryObject.addSelectField("*");
				sqlQueryObject.addWhereCondition(CostantiDB.RUOLI_SA + ".id_servizio_applicativo=?");
				sqlQueryObject.addWhereCondition(CostantiDB.RUOLI_SA + ".id_accordo = " + CostantiDB.ACCORDI + ".id");
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
				stmt = con.prepareStatement(queryString);
				stmt.setLong(1, idServizioApplicativoLong);
				risultato = stmt.executeQuery();
				while (risultato.next()){
					isInUso = true;

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

					ruoli_list.add(bf.toString());
				}
				risultato.close();
				stmt.close();

			}


			// politiche di sicurezza
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.POLITICHE_SICUREZZA);
			sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
			sqlQueryObject.addFromTable(CostantiDB.SERVIZI);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition(CostantiDB.POLITICHE_SICUREZZA + ".id_servizio_applicativo=?");
			sqlQueryObject.addWhereCondition(CostantiDB.POLITICHE_SICUREZZA + ".id_fruitore = " + CostantiDB.SOGGETTI + ".id");
			sqlQueryObject.addWhereCondition(CostantiDB.POLITICHE_SICUREZZA + ".id_servizio = " + CostantiDB.SERVIZI + ".id");
			sqlQueryObject.setANDLogicOperator(true);
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idServizioApplicativoLong);
			risultato = stmt.executeQuery();
			while (risultato.next()){
				isInUso = true;

				String tipoFruitore = risultato.getString("tipo_soggetto");
				String nomeFruitore = risultato.getString("nome_soggetto");

				String tipoServizio = risultato.getString("tipo_servizio");
				String nomeServizio = risultato.getString("nome_servizio");

				long idSoggettoErogatore = risultato.getLong("id_soggetto");
				String tipoSoggettoErogatore = null;
				String nomeSoggettoErogatore = null;
				if(idSoggettoErogatore>0){
					ISQLQueryObject sqlQueryObjectErogatore = SQLObjectFactory.createSQLQueryObject(tipoDB);
					sqlQueryObjectErogatore.addFromTable(CostantiDB.SOGGETTI);
					sqlQueryObjectErogatore.addSelectField("*");
					sqlQueryObjectErogatore.addWhereCondition("id=?");
					sqlQueryObjectErogatore.setANDLogicOperator(true);
					String queryStringErogatore = sqlQueryObjectErogatore.createSQLQuery();
					stmt2 = con.prepareStatement(queryStringErogatore);
					stmt2.setLong(1, idSoggettoErogatore);
					risultato2 = stmt2.executeQuery();
					if(risultato2.next()){
						tipoSoggettoErogatore = risultato2.getString("tipo_soggetto");
						nomeSoggettoErogatore = risultato2.getString("nome_soggetto");
					}
					risultato2.close(); risultato2=null;
					stmt2.close(); stmt2=null;
				}

				StringBuffer bf = new StringBuffer();
				bf.append(tipoFruitore);
				bf.append("/");
				bf.append(nomeFruitore);
				bf.append("-&gt;");
				bf.append(tipoSoggettoErogatore);
				bf.append("/");
				bf.append(nomeSoggettoErogatore);
				bf.append("_");
				bf.append(tipoServizio);
				bf.append("/");
				bf.append(nomeServizio);
				politiche_sicurezza_list.add(bf.toString());

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
			case POSSIEDE_RUOLI:
				if ( messages!=null && messages.size() > 0 ) {
					msg += "- possiede i seguenti ruoli: " + messages.toString() + separator;
				}
				break;
			case UTILIZZATO_IN_POLITICHE_SICUREZZA:
				if ( messages!=null && messages.size() > 0 ) {
					msg += "- in uso in Politiche di Sicurezza: " + messages.toString() + separator;
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
