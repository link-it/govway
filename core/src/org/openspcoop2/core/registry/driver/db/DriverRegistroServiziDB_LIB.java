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



package org.openspcoop2.core.registry.driver.db;

import static org.openspcoop2.core.constants.CostantiDB.CREATE;
import static org.openspcoop2.core.constants.CostantiDB.DELETE;
import static org.openspcoop2.core.constants.CostantiDB.UPDATE;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.List;

import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.core.commons.DBUtils;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.id.IDGruppo;
import org.openspcoop2.core.id.IDRuolo;
import org.openspcoop2.core.id.IDScope;
import org.openspcoop2.core.mapping.DBProtocolPropertiesUtils;
import org.openspcoop2.core.registry.Gruppo;
import org.openspcoop2.core.registry.ProtocolProperty;
import org.openspcoop2.core.registry.Ruolo;
import org.openspcoop2.core.registry.Scope;
import org.openspcoop2.core.registry.constants.BindingStyle;
import org.openspcoop2.core.registry.constants.BindingUse;
import org.openspcoop2.core.registry.constants.CostantiRegistroServizi;
import org.openspcoop2.core.registry.constants.CredenzialeTipo;
import org.openspcoop2.core.registry.constants.FormatoSpecifica;
import org.openspcoop2.core.registry.constants.HttpMethod;
import org.openspcoop2.core.registry.constants.MessageType;
import org.openspcoop2.core.registry.constants.ParameterType;
import org.openspcoop2.core.registry.constants.ProfiloCollaborazione;
import org.openspcoop2.core.registry.constants.RepresentationType;
import org.openspcoop2.core.registry.constants.RepresentationXmlType;
import org.openspcoop2.core.registry.constants.RuoloContesto;
import org.openspcoop2.core.registry.constants.RuoloTipologia;
import org.openspcoop2.core.registry.constants.ScopeContesto;
import org.openspcoop2.core.registry.constants.ServiceBinding;
import org.openspcoop2.core.registry.constants.StatoFunzionalita;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziNotFound;
import org.openspcoop2.generic_project.exception.NotFoundException;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.jdbc.JDBCUtilities;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.SQLObjectFactory;
import org.slf4j.Logger;

/**
 * Classe utilizzata per effettuare query ad un registro dei servizi openspcoop
 * formato db.
 * 
 * 
 * @author Sandra Giangrandi (sandra@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DriverRegistroServiziDB_LIB {

	/** Logger utilizzato per debug. */
	public static org.slf4j.Logger log = LoggerWrapperFactory.getLogger(CostantiRegistroServizi.REGISTRO_DRIVER_DB_LOGGER);
	
	static void logDebug(String msg) {
		if(log!=null) {
			log.debug(msg);
		}
	}
	static void logDebug(String msg, Exception e) {
		if(log!=null) {
			log.debug(msg,e);
		}
	}
	
	static void logError(String msg) {
		if(log!=null) {
			log.error(msg);
		}
	}
	static void logError(String msg, Exception e) {
		if(log!=null) {
			log.error(msg,e);
		}
	}

	// Tipo database ereditato da DriverRegistroServiziDB
	static String tipoDB = null;

	/** Log ereditato da DriverRegistroServiziDB
	 */
	private static boolean initialize = false;
	public static void initStaticLogger(Logger aLog){
		if(!DriverRegistroServiziDB_LIB.initialize){
			if(aLog!=null){
				DriverRegistroServiziDB_LIB.log = aLog;
			}
			DriverRegistroServiziDB_LIB.initialize = true;
		}
	}
	public static boolean isStaticLoggerInitialized(){
		return DriverRegistroServiziDB_LIB.initialize;
	}

	// Setto il tipoDB
	public static void setTipoDB(String tipoDatabase) {
		DriverRegistroServiziDB_LIB.tipoDB = tipoDatabase;
	}
	
	public static String getValue(StatoFunzionalita funzionalita){
		if(funzionalita==null){
			return null;
		}
		else{
			return funzionalita.getValue();
		}
	}
	public static String getValue(ProfiloCollaborazione profilo){
		if(profilo==null){
			return null;
		}
		else{
			return profilo.getValue();
		}
	}
	public static String getValue(BindingStyle style){
		if(style==null){
			return null;
		}
		else{
			return style.getValue();
		}
	}
	public static String getValue(BindingUse use){
		if(use==null){
			return null;
		}
		else{
			return use.getValue();
		}
	}
	public static String getValue(CredenzialeTipo tipo){
		if(tipo==null){
			return null;
		}
		else{
			return tipo.getValue();
		}
	}
	public static String getValue(ServiceBinding tipo){
		if(tipo==null){
			return null;
		}
		else{
			return tipo.getValue();
		}
	}
	public static String getValue(MessageType tipo){
		if(tipo==null){
			return null;
		}
		else{
			return tipo.getValue();
		}
	}
	public static String getValue(HttpMethod tipo){
		if(tipo==null){
			return CostantiDB.API_RESOURCE_HTTP_METHOD_ALL_VALUE;
		}
		else{
			return tipo.getValue();
		}
	}
	public static String getValue(FormatoSpecifica tipo){
		if(tipo==null){
			return null;
		}
		else{
			return tipo.getValue();
		}
	}
	public static String getValue(RepresentationType tipo){
		if(tipo==null){
			return null;
		}
		else{
			return tipo.getValue();
		}
	}
	public static String getValue(RepresentationXmlType tipo){
		if(tipo==null){
			return null;
		}
		else{
			return tipo.getValue();
		}
	}
	public static String getValue(ParameterType tipo){
		if(tipo==null){
			return null;
		}
		else{
			return tipo.getValue();
		}
	}
	
	public static StatoFunzionalita getEnumStatoFunzionalita(String value){
		if(value==null){
			return null;
		}
		else{
			return StatoFunzionalita.toEnumConstant(value);
		}
	}
	public static ProfiloCollaborazione getEnumProfiloCollaborazione(String value){
		if(value==null){
			return null;
		}
		else{
			return ProfiloCollaborazione.toEnumConstant(value);
		}
	}
	public static BindingStyle getEnumBindingStyle(String value){
		if(value==null){
			return null;
		}
		else{
			return BindingStyle.toEnumConstant(value);
		}
	}
	public static BindingUse getEnumBindingUse(String value){
		if(value==null){
			return null;
		}
		else{
			return BindingUse.toEnumConstant(value);
		}
	}
	public static CredenzialeTipo getEnumCredenzialeTipo(String value){
		if(value==null){
			return null;
		}
		else{
			return CredenzialeTipo.toEnumConstant(value);
		}
	}
	public static ServiceBinding getEnumServiceBinding(String value){
		if(value==null){
			return null;
		}
		else{
			return ServiceBinding.toEnumConstant(value);
		}
	}
	public static MessageType getEnumMessageType(String value){
		if(value==null){
			return null;
		}
		else{
			return MessageType.toEnumConstant(value);
		}
	}
	public static HttpMethod getEnumHttpMethod(String value){
		if(value==null || CostantiDB.API_RESOURCE_HTTP_METHOD_ALL_VALUE.equalsIgnoreCase(value)){
			return null;
		}
		else{
			return HttpMethod.toEnumConstant(value);
		}
	}
	public static FormatoSpecifica getEnumFormatoSpecifica(String value){
		if(value==null){
			return null;
		}
		else{
			return FormatoSpecifica.toEnumConstant(value);
		}
	}
	public static RepresentationType getEnumRepresentationType(String value){
		if(value==null){
			return null;
		}
		else{
			return RepresentationType.toEnumConstant(value);
		}
	}
	public static RepresentationXmlType getEnumRepresentationXmlType(String value){
		if(value==null){
			return null;
		}
		else{
			return RepresentationXmlType.toEnumConstant(value);
		}
	}
	public static ParameterType getEnumParameterType(String value){
		if(value==null){
			return null;
		}
		else{
			return ParameterType.toEnumConstant(value);
		}
	}


	/**
	 * Utility per formattare la string sql con i parametri passati, e stamparla
	 * per debug
	 * 
	 * @param sql
	 *            la string sql utilizzata nel prepared statement
	 * @param params
	 *            i parametri da inserire nella stringa che sostituiranno i '?'
	 * @return La stringa sql con al posto dei '?' ha i parametri passati
	 */
	public static String formatSQLString(String sql, Object... params) {
		String res = sql;

		for (int i = 0; i < params.length; i++) {
			res = res.replaceFirst("\\?", "{" + i + "}");
		}

		return MessageFormat.format(res, params);

	}

	
	
	public static void CRUDGruppo(int type, Gruppo gruppo, Connection con)
			throws DriverRegistroServiziException {
		if (gruppo == null) {
			throw new DriverRegistroServiziException(
			"[DriverRegistroServiziDB_LIB::CRUDGruppo] Parametro non valido.");
		}

		/**if ((type != CostantiDB.CREATE) && (pdd.getId() <= 0)) {
			throw new DriverRegistroServiziException(
			"[DriverRegistroServiziDB_LIB::CRUDGruppo] ID Gruppo non valido.");
		}*/

		String nome = gruppo.getNome();
		String descrizione = gruppo.getDescrizione();
		ServiceBinding serviceBinding = gruppo.getServiceBinding();

		String superuser = gruppo.getSuperUser();

		PreparedStatement updateStmt = null;
		String updateQuery = "";
		PreparedStatement selectStmt = null;
		String selectQuery = "";
		ResultSet selectRS = null;
		int n = 0;

		try {

			// preparo lo statement in base al tipo di operazione
			switch (type) {
			case CREATE:
				// CREATE
				
				String utenteRichiedente = null;
				if(gruppo.getProprietaOggetto()!=null && gruppo.getProprietaOggetto().getUtenteRichiedente()!=null) {
					utenteRichiedente = gruppo.getProprietaOggetto().getUtenteRichiedente();
				}
				else {
					utenteRichiedente = superuser;
				}
				
				Timestamp dataCreazione = null;
				if(gruppo.getProprietaOggetto()!=null && gruppo.getProprietaOggetto().getDataCreazione()!=null) {
					dataCreazione = new Timestamp(gruppo.getProprietaOggetto().getDataCreazione().getTime());
				}
				else if(gruppo.getOraRegistrazione()!=null){
					dataCreazione = new Timestamp(gruppo.getOraRegistrazione().getTime());
				}
				else {
					dataCreazione = DateManager.getTimestamp();
				}
				
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
				sqlQueryObject.addInsertTable(CostantiDB.GRUPPI);
				sqlQueryObject.addInsertField("nome", "?");
				sqlQueryObject.addInsertField("descrizione", "?");
				sqlQueryObject.addInsertField("service_binding", "?");
				sqlQueryObject.addInsertField("superuser", "?");
				if(gruppo.getOraRegistrazione()!=null)
					sqlQueryObject.addInsertField("ora_registrazione", "?");
				if(utenteRichiedente!=null) {
					sqlQueryObject.addInsertField("utente_richiedente", "?");
				}
				if(dataCreazione!=null) {
					sqlQueryObject.addInsertField("data_creazione", "?");
				}

				updateQuery = sqlQueryObject.createSQLInsert();
				updateStmt = con.prepareStatement(updateQuery);

				int index = 1;
				updateStmt.setString(index++, nome);
				updateStmt.setString(index++, descrizione);
				updateStmt.setString(index++, serviceBinding!=null? serviceBinding.getValue() : null);
				updateStmt.setString(index++, superuser);
				
				if(gruppo.getOraRegistrazione()!=null)
					updateStmt.setTimestamp(index++, new Timestamp(gruppo.getOraRegistrazione().getTime()));
				
				if(utenteRichiedente!=null) {
					updateStmt.setString(index++, utenteRichiedente);
				}
				
				if(dataCreazione!=null) {
					updateStmt.setTimestamp(index++, dataCreazione);
				}

				// eseguo lo statement
				n = updateStmt.executeUpdate();

				updateStmt.close();

				DriverRegistroServiziDB_LIB.logDebug("CRUDGruppo type = " + type
						+ " row affected =" + n);

				DriverRegistroServiziDB_LIB.logDebug("CRUDGruppo CREATE : \n"
						+ DriverRegistroServiziDB_LIB.formatSQLString(
								updateQuery, nome, descrizione,(serviceBinding!=null? serviceBinding.getValue() : null),
								superuser));

				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.GRUPPI);
				sqlQueryObject.addSelectField("id");
				sqlQueryObject.addWhereCondition("nome = ?");
				selectQuery = sqlQueryObject.createSQLQuery();
				selectStmt = con.prepareStatement(selectQuery);
				selectStmt.setString(1, nome);

				selectRS = selectStmt.executeQuery();
				if (selectRS.next()) {
					gruppo.setId(selectRS.getLong("id"));
				}
				selectRS.close();
				selectStmt.close();
				break;

			case UPDATE:
				// UPDATE1

				String nomeGruppo = gruppo.getOldIDGruppoForUpdate()!=null ? gruppo.getOldIDGruppoForUpdate().getNome() : null;
				if(nomeGruppo==null || "".equals(nomeGruppo))
					nomeGruppo = gruppo.getNome();
				
				IDGruppo idG = new IDGruppo(nomeGruppo);
				long idGruppo = DBUtils.getIdGruppo(idG, con, DriverRegistroServiziDB_LIB.tipoDB);
				if (idGruppo <= 0)
					throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDGruppo(UPDATE)] Id Gruppo non valido.");
				
				String utenteUltimaModifica = null;
				if(gruppo.getProprietaOggetto()!=null && gruppo.getProprietaOggetto().getUtenteUltimaModifica()!=null) {
					utenteUltimaModifica = gruppo.getProprietaOggetto().getUtenteUltimaModifica();
				}
				else {
					utenteUltimaModifica = superuser;
				}
				
				Timestamp dataUltimaModifica = null;
				if(gruppo.getProprietaOggetto()!=null && gruppo.getProprietaOggetto().getDataUltimaModifica()!=null) {
					dataUltimaModifica = new Timestamp(gruppo.getProprietaOggetto().getDataUltimaModifica().getTime());
				}
				else {
					dataUltimaModifica = DateManager.getTimestamp();
				}
				
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
				sqlQueryObject.addUpdateTable(CostantiDB.GRUPPI);
				sqlQueryObject.addUpdateField("nome", "?");
				sqlQueryObject.addUpdateField("descrizione", "?");
				sqlQueryObject.addUpdateField("service_binding", "?");
				sqlQueryObject.addUpdateField("superuser", "?");
				if(gruppo.getOraRegistrazione()!=null)
					sqlQueryObject.addUpdateField("ora_registrazione", "?");
				if(utenteUltimaModifica!=null) {
					sqlQueryObject.addUpdateField("utente_ultima_modifica", "?");
				}
				if(dataUltimaModifica!=null) {
					sqlQueryObject.addUpdateField("data_ultima_modifica", "?");
				}
				
				sqlQueryObject.addWhereCondition("id=?");
				updateQuery = sqlQueryObject.createSQLUpdate();
				updateStmt = con.prepareStatement(updateQuery);

				index = 1;
				updateStmt.setString(index++, nome);
				updateStmt.setString(index++, descrizione);
				updateStmt.setString(index++, serviceBinding!=null? serviceBinding.getValue() : null);
				updateStmt.setString(index++, superuser);
				if(gruppo.getOraRegistrazione()!=null)
					updateStmt.setTimestamp(index++, new Timestamp(gruppo.getOraRegistrazione().getTime()));
				if(utenteUltimaModifica!=null) {
					updateStmt.setString(index++, utenteUltimaModifica);
				}
				if(dataUltimaModifica!=null) {
					updateStmt.setTimestamp(index++, dataUltimaModifica);
				}
	
				updateStmt.setLong(index++, idGruppo);

				// eseguo lo statement
				n = updateStmt.executeUpdate();
				updateStmt.close();
				DriverRegistroServiziDB_LIB.logDebug("CRUDGruppo type = " + type
						+ " row affected =" + n);

				DriverRegistroServiziDB_LIB.logDebug("CRUDGruppo UPDATE : \n"
						+ DriverRegistroServiziDB_LIB.formatSQLString(
								updateQuery, nome, descrizione,(serviceBinding!=null? serviceBinding.getValue() : null),
								superuser,idGruppo));

				break;

			case DELETE:
				// DELETE

				idG = new IDGruppo(nome);
				idGruppo = DBUtils.getIdGruppo(idG, con, DriverRegistroServiziDB_LIB.tipoDB);
				if (idGruppo <= 0)
					throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDGruppo(DELETE)] Id Gruppo non valido.");
				
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.GRUPPI);
				sqlQueryObject.addWhereCondition("id=?");
				updateQuery = sqlQueryObject.createSQLDelete();
				updateStmt = con.prepareStatement(updateQuery);

				updateStmt.setLong(1, idGruppo);

				// eseguo lo statement
				n = updateStmt.executeUpdate();
				updateStmt.close();
				DriverRegistroServiziDB_LIB.logDebug("CRUDGruppo type = " + type
						+ " row affected =" + n);

				DriverRegistroServiziDB_LIB.logDebug("CRUDGruppo DELETE : \n"
						+ DriverRegistroServiziDB_LIB.formatSQLString(
								updateQuery, idGruppo));

				break;
			}

		} catch (SQLException se) {
			throw new DriverRegistroServiziException(
					"[DriverControlStationDB_LIB::CRUDGruppo] SQLException ["
					+ se.getMessage() + "].",se);
		} catch (Exception se) {
			throw new DriverRegistroServiziException(
					"[DriverControlStationDB_LIB::CRUDGruppo] Exception ["
					+ se.getMessage() + "].",se);
		} finally {

			JDBCUtilities.closeResources(updateStmt);
			JDBCUtilities.closeResources(selectRS, selectStmt);

		}
	}
	
	public static void CRUDRuolo(int type, Ruolo ruolo, Connection con)
			throws DriverRegistroServiziException {
		if (ruolo == null) {
			throw new DriverRegistroServiziException(
			"[DriverRegistroServiziDB_LIB::CRUDRuolo] Parametro non valido.");
		}

		/**if ((type != CostantiDB.CREATE) && (pdd.getId() <= 0)) {
			throw new DriverRegistroServiziException(
			"[DriverRegistroServiziDB_LIB::CRUDRuolo] ID Ruolo non valido.");
		}*/

		String nome = ruolo.getNome();
		String descrizione = ruolo.getDescrizione();
		RuoloTipologia ruoloTipologia = ruolo.getTipologia();
		String nomeEsterno = ruolo.getNomeEsterno();
		RuoloContesto ruoloContesto = ruolo.getContestoUtilizzo();

		String superuser = ruolo.getSuperUser();

		PreparedStatement updateStmt = null;
		String updateQuery = "";
		PreparedStatement selectStmt = null;
		String selectQuery = "";
		ResultSet selectRS = null;
		int n = 0;

		try {

			// preparo lo statement in base al tipo di operazione
			switch (type) {
			case CREATE:
				
				String utenteRichiedente = null;
				if(ruolo.getProprietaOggetto()!=null && ruolo.getProprietaOggetto().getUtenteRichiedente()!=null) {
					utenteRichiedente = ruolo.getProprietaOggetto().getUtenteRichiedente();
				}
				else {
					utenteRichiedente = superuser;
				}
				
				Timestamp dataCreazione = null;
				if(ruolo.getProprietaOggetto()!=null && ruolo.getProprietaOggetto().getDataCreazione()!=null) {
					dataCreazione = new Timestamp(ruolo.getProprietaOggetto().getDataCreazione().getTime());
				}
				else if(ruolo.getOraRegistrazione()!=null){
					dataCreazione = new Timestamp(ruolo.getOraRegistrazione().getTime());
				}
				else {
					dataCreazione = DateManager.getTimestamp();
				}
				
				// CREATE
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
				sqlQueryObject.addInsertTable(CostantiDB.RUOLI);
				sqlQueryObject.addInsertField("nome", "?");
				sqlQueryObject.addInsertField("descrizione", "?");
				sqlQueryObject.addInsertField("tipologia", "?");
				sqlQueryObject.addInsertField("nome_esterno", "?");
				sqlQueryObject.addInsertField("contesto_utilizzo", "?");
				sqlQueryObject.addInsertField("superuser", "?");
				if(ruolo.getOraRegistrazione()!=null)
					sqlQueryObject.addInsertField("ora_registrazione", "?");
				if(utenteRichiedente!=null) {
					sqlQueryObject.addInsertField("utente_richiedente", "?");
				}
				if(dataCreazione!=null) {
					sqlQueryObject.addInsertField("data_creazione", "?");
				}

				updateQuery = sqlQueryObject.createSQLInsert();
				updateStmt = con.prepareStatement(updateQuery);

				int index = 1;
				updateStmt.setString(index++, nome);
				updateStmt.setString(index++, descrizione);
				updateStmt.setString(index++, ruoloTipologia!=null? ruoloTipologia.getValue() : null);
				updateStmt.setString(index++, nomeEsterno);
				updateStmt.setString(index++, ruoloContesto!=null? ruoloContesto.getValue() : null);
				updateStmt.setString(index++, superuser);
				
				if(ruolo.getOraRegistrazione()!=null)
					updateStmt.setTimestamp(index++, new Timestamp(ruolo.getOraRegistrazione().getTime()));
				
				if(utenteRichiedente!=null) {
					updateStmt.setString(index++, utenteRichiedente);
				}
				
				if(dataCreazione!=null) {
					updateStmt.setTimestamp(index++, dataCreazione);
				}

				// eseguo lo statement
				n = updateStmt.executeUpdate();

				updateStmt.close();

				DriverRegistroServiziDB_LIB.logDebug("CRUDRuolo type = " + type
						+ " row affected =" + n);

				DriverRegistroServiziDB_LIB.logDebug("CRUDRuolo CREATE : \n"
						+ DriverRegistroServiziDB_LIB.formatSQLString(
								updateQuery, nome, descrizione,(ruoloTipologia!=null? ruoloTipologia.getValue() : null),
								(ruoloContesto!=null? ruoloContesto.getValue() : null),superuser));

				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.RUOLI);
				sqlQueryObject.addSelectField("id");
				sqlQueryObject.addWhereCondition("nome = ?");
				selectQuery = sqlQueryObject.createSQLQuery();
				selectStmt = con.prepareStatement(selectQuery);
				selectStmt.setString(1, nome);

				selectRS = selectStmt.executeQuery();
				if (selectRS.next()) {
					ruolo.setId(selectRS.getLong("id"));
				}
				selectRS.close();
				selectStmt.close();
				break;

			case UPDATE:
				// UPDATE1

				String nomeRuolo = ruolo.getOldIDRuoloForUpdate()!=null ? ruolo.getOldIDRuoloForUpdate().getNome() : null;
				if(nomeRuolo==null || "".equals(nomeRuolo))
					nomeRuolo = ruolo.getNome();
				
				IDRuolo idR = new IDRuolo(nomeRuolo);
				long idRuolo = DBUtils.getIdRuolo(idR, con, DriverRegistroServiziDB_LIB.tipoDB);
				if (idRuolo <= 0)
					throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDRuolo(UPDATE)] Id Ruolo non valido.");
				
				String utenteUltimaModifica = null;
				if(ruolo.getProprietaOggetto()!=null && ruolo.getProprietaOggetto().getUtenteUltimaModifica()!=null) {
					utenteUltimaModifica = ruolo.getProprietaOggetto().getUtenteUltimaModifica();
				}
				else {
					utenteUltimaModifica = superuser;
				}
				
				Timestamp dataUltimaModifica = null;
				if(ruolo.getProprietaOggetto()!=null && ruolo.getProprietaOggetto().getDataUltimaModifica()!=null) {
					dataUltimaModifica = new Timestamp(ruolo.getProprietaOggetto().getDataUltimaModifica().getTime());
				}
				else {
					dataUltimaModifica = DateManager.getTimestamp();
				}
				
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
				sqlQueryObject.addUpdateTable(CostantiDB.RUOLI);
				sqlQueryObject.addUpdateField("nome", "?");
				sqlQueryObject.addUpdateField("descrizione", "?");
				sqlQueryObject.addUpdateField("tipologia", "?");
				sqlQueryObject.addUpdateField("nome_esterno", "?");
				sqlQueryObject.addUpdateField("contesto_utilizzo", "?");
				sqlQueryObject.addUpdateField("superuser", "?");
				if(ruolo.getOraRegistrazione()!=null)
					sqlQueryObject.addUpdateField("ora_registrazione", "?");
				if(utenteUltimaModifica!=null) {
					sqlQueryObject.addUpdateField("utente_ultima_modifica", "?");
				}
				if(dataUltimaModifica!=null) {
					sqlQueryObject.addUpdateField("data_ultima_modifica", "?");
				}
				
				sqlQueryObject.addWhereCondition("id=?");
				updateQuery = sqlQueryObject.createSQLUpdate();
				updateStmt = con.prepareStatement(updateQuery);

				index = 1;
				updateStmt.setString(index++, nome);
				updateStmt.setString(index++, descrizione);
				updateStmt.setString(index++, ruoloTipologia!=null? ruoloTipologia.getValue() : null);
				updateStmt.setString(index++, nomeEsterno);
				updateStmt.setString(index++, ruoloContesto!=null? ruoloContesto.getValue() : null);
				updateStmt.setString(index++, superuser);
				if(ruolo.getOraRegistrazione()!=null)
					updateStmt.setTimestamp(index++, new Timestamp(ruolo.getOraRegistrazione().getTime()));
				if(utenteUltimaModifica!=null) {
					updateStmt.setString(index++, utenteUltimaModifica);
				}
				if(dataUltimaModifica!=null) {
					updateStmt.setTimestamp(index++, dataUltimaModifica);
				}
	
				updateStmt.setLong(index++, idRuolo);

				// eseguo lo statement
				n = updateStmt.executeUpdate();
				updateStmt.close();
				DriverRegistroServiziDB_LIB.logDebug("CRUDRuolo type = " + type
						+ " row affected =" + n);

				DriverRegistroServiziDB_LIB.logDebug("CRUDRuolo UPDATE : \n"
						+ DriverRegistroServiziDB_LIB.formatSQLString(
								updateQuery, nome, descrizione,(ruoloTipologia!=null? ruoloTipologia.getValue() : null),
								(ruoloContesto!=null? ruoloContesto.getValue() : null),superuser,idRuolo));

				break;

			case DELETE:
				// DELETE

				idR = new IDRuolo(nome);
				idRuolo = DBUtils.getIdRuolo(idR, con, DriverRegistroServiziDB_LIB.tipoDB);
				if (idRuolo <= 0)
					throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDRuolo(DELETE)] Id Ruolo non valido.");
				
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.RUOLI);
				sqlQueryObject.addWhereCondition("id=?");
				updateQuery = sqlQueryObject.createSQLDelete();
				updateStmt = con.prepareStatement(updateQuery);

				updateStmt.setLong(1, idRuolo);

				// eseguo lo statement
				n = updateStmt.executeUpdate();
				updateStmt.close();
				DriverRegistroServiziDB_LIB.logDebug("CRUDRuolo type = " + type
						+ " row affected =" + n);

				DriverRegistroServiziDB_LIB.logDebug("CRUDRuolo DELETE : \n"
						+ DriverRegistroServiziDB_LIB.formatSQLString(
								updateQuery, idRuolo));

				break;
			}

		} catch (SQLException se) {
			throw new DriverRegistroServiziException(
					"[DriverControlStationDB_LIB::CRUDRuolo] SQLException ["
					+ se.getMessage() + "].",se);
		} catch (Exception se) {
			throw new DriverRegistroServiziException(
					"[DriverControlStationDB_LIB::CRUDRuolo] Exception ["
					+ se.getMessage() + "].",se);
		} finally {

			JDBCUtilities.closeResources(updateStmt);
			JDBCUtilities.closeResources(selectRS, selectStmt);
		}
	}
	
	public static void CRUDScope(int type, Scope scope, Connection con)
			throws DriverRegistroServiziException {
		if (scope == null) {
			throw new DriverRegistroServiziException(
			"[DriverRegistroServiziDB_LIB::CRUDScope] Parametro non valido.");
		}

		/**if ((type != CostantiDB.CREATE) && (pdd.getId() <= 0)) {
			throw new DriverRegistroServiziException(
			"[DriverRegistroServiziDB_LIB::CRUDScope] ID Scope non valido.");
		}*/

		String nome = scope.getNome();
		String descrizione = scope.getDescrizione();
		String scopeTipologia = scope.getTipologia();
		String nomeEsterno = scope.getNomeEsterno();
		ScopeContesto scopeContesto = scope.getContestoUtilizzo();

		String superuser = scope.getSuperUser();

		PreparedStatement updateStmt = null;
		String updateQuery = "";
		PreparedStatement selectStmt = null;
		String selectQuery = "";
		ResultSet selectRS = null;
		int n = 0;

		try {

			// preparo lo statement in base al tipo di operazione
			switch (type) {
			case CREATE:
				
				String utenteRichiedente = null;
				if(scope.getProprietaOggetto()!=null && scope.getProprietaOggetto().getUtenteRichiedente()!=null) {
					utenteRichiedente = scope.getProprietaOggetto().getUtenteRichiedente();
				}
				else {
					utenteRichiedente = superuser;
				}
				
				Timestamp dataCreazione = null;
				if(scope.getProprietaOggetto()!=null && scope.getProprietaOggetto().getDataCreazione()!=null) {
					dataCreazione = new Timestamp(scope.getProprietaOggetto().getDataCreazione().getTime());
				}
				else if(scope.getOraRegistrazione()!=null){
					dataCreazione = new Timestamp(scope.getOraRegistrazione().getTime());
				}
				else {
					dataCreazione = DateManager.getTimestamp();
				}				
				
				// CREATE
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
				sqlQueryObject.addInsertTable(CostantiDB.SCOPE);
				sqlQueryObject.addInsertField("nome", "?");
				sqlQueryObject.addInsertField("descrizione", "?");
				sqlQueryObject.addInsertField("tipologia", "?");
				sqlQueryObject.addInsertField("nome_esterno", "?");
				sqlQueryObject.addInsertField("contesto_utilizzo", "?");
				sqlQueryObject.addInsertField("superuser", "?");
				if(scope.getOraRegistrazione()!=null)
					sqlQueryObject.addInsertField("ora_registrazione", "?");
				if(utenteRichiedente!=null) {
					sqlQueryObject.addInsertField("utente_richiedente", "?");
				}
				if(dataCreazione!=null) {
					sqlQueryObject.addInsertField("data_creazione", "?");
				}

				updateQuery = sqlQueryObject.createSQLInsert();
				updateStmt = con.prepareStatement(updateQuery);

				int index = 1;
				updateStmt.setString(index++, nome);
				updateStmt.setString(index++, descrizione);
				updateStmt.setString(index++, scopeTipologia);
				updateStmt.setString(index++, nomeEsterno);
				updateStmt.setString(index++, scopeContesto!=null? scopeContesto.getValue() : null);
				updateStmt.setString(index++, superuser);
				
				if(scope.getOraRegistrazione()!=null)
					updateStmt.setTimestamp(index++, new Timestamp(scope.getOraRegistrazione().getTime()));
				
				if(utenteRichiedente!=null) {
					updateStmt.setString(index++, utenteRichiedente);
				}
				
				if(dataCreazione!=null) {
					updateStmt.setTimestamp(index++, dataCreazione);
				}

				// eseguo lo statement
				n = updateStmt.executeUpdate();

				updateStmt.close();

				DriverRegistroServiziDB_LIB.logDebug("CRUDScope type = " + type
						+ " row affected =" + n);

				DriverRegistroServiziDB_LIB.logDebug("CRUDScope CREATE : \n"
						+ DriverRegistroServiziDB_LIB.formatSQLString(
								updateQuery, nome, descrizione,scopeTipologia,
								(scopeContesto!=null? scopeContesto.getValue() : null),superuser));

				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SCOPE);
				sqlQueryObject.addSelectField("id");
				sqlQueryObject.addWhereCondition("nome = ?");
				selectQuery = sqlQueryObject.createSQLQuery();
				selectStmt = con.prepareStatement(selectQuery);
				selectStmt.setString(1, nome);

				selectRS = selectStmt.executeQuery();
				if (selectRS.next()) {
					scope.setId(selectRS.getLong("id"));
				}
				selectRS.close();
				selectStmt.close();
				break;

			case UPDATE:
				// UPDATE1

				String nomeScope = scope.getOldIDScopeForUpdate()!=null ? scope.getOldIDScopeForUpdate().getNome() : null;
				if(nomeScope==null || "".equals(nomeScope))
					nomeScope = scope.getNome();
				
				IDScope idS = new IDScope(nomeScope);
				long idScope = DBUtils.getIdScope(idS, con, DriverRegistroServiziDB_LIB.tipoDB);
				if (idScope <= 0)
					throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDScope(UPDATE)] Id Scope non valido.");
				
				String utenteUltimaModifica = null;
				if(scope.getProprietaOggetto()!=null && scope.getProprietaOggetto().getUtenteUltimaModifica()!=null) {
					utenteUltimaModifica = scope.getProprietaOggetto().getUtenteUltimaModifica();
				}
				else {
					utenteUltimaModifica = superuser;
				}
				
				Timestamp dataUltimaModifica = null;
				if(scope.getProprietaOggetto()!=null && scope.getProprietaOggetto().getDataUltimaModifica()!=null) {
					dataUltimaModifica = new Timestamp(scope.getProprietaOggetto().getDataUltimaModifica().getTime());
				}
				else {
					dataUltimaModifica = DateManager.getTimestamp();
				}
				
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
				sqlQueryObject.addUpdateTable(CostantiDB.SCOPE);
				sqlQueryObject.addUpdateField("nome", "?");
				sqlQueryObject.addUpdateField("descrizione", "?");
				sqlQueryObject.addUpdateField("tipologia", "?");
				sqlQueryObject.addUpdateField("nome_esterno", "?");
				sqlQueryObject.addUpdateField("contesto_utilizzo", "?");
				sqlQueryObject.addUpdateField("superuser", "?");
				if(scope.getOraRegistrazione()!=null)
					sqlQueryObject.addUpdateField("ora_registrazione", "?");
				if(utenteUltimaModifica!=null) {
					sqlQueryObject.addUpdateField("utente_ultima_modifica", "?");
				}
				if(dataUltimaModifica!=null) {
					sqlQueryObject.addUpdateField("data_ultima_modifica", "?");
				}
				
				sqlQueryObject.addWhereCondition("id=?");
				updateQuery = sqlQueryObject.createSQLUpdate();
				updateStmt = con.prepareStatement(updateQuery);

				index = 1;
				updateStmt.setString(index++, nome);
				updateStmt.setString(index++, descrizione);
				updateStmt.setString(index++, scopeTipologia);
				updateStmt.setString(index++, nomeEsterno);
				updateStmt.setString(index++, scopeContesto!=null? scopeContesto.getValue() : null);
				updateStmt.setString(index++, superuser);
				if(scope.getOraRegistrazione()!=null)
					updateStmt.setTimestamp(index++, new Timestamp(scope.getOraRegistrazione().getTime()));
				if(utenteUltimaModifica!=null) {
					updateStmt.setString(index++, utenteUltimaModifica);
				}
				if(dataUltimaModifica!=null) {
					updateStmt.setTimestamp(index++, dataUltimaModifica);
				}
				
				updateStmt.setLong(index++, idScope);

				// eseguo lo statement
				n = updateStmt.executeUpdate();
				updateStmt.close();
				DriverRegistroServiziDB_LIB.logDebug("CRUDScope type = " + type
						+ " row affected =" + n);

				DriverRegistroServiziDB_LIB.logDebug("CRUDScope UPDATE : \n"
						+ DriverRegistroServiziDB_LIB.formatSQLString(
								updateQuery, nome, descrizione,scopeTipologia,
								(scopeContesto!=null? scopeContesto.getValue() : null),superuser,idScope));

				break;

			case DELETE:
				// DELETE

				idS = new IDScope(nome);
				idScope = DBUtils.getIdScope(idS, con, DriverRegistroServiziDB_LIB.tipoDB);
				if (idScope <= 0)
					throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDScope(DELETE)] Id Scope non valido.");
				
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.SCOPE);
				sqlQueryObject.addWhereCondition("id=?");
				updateQuery = sqlQueryObject.createSQLDelete();
				updateStmt = con.prepareStatement(updateQuery);

				updateStmt.setLong(1, idScope);

				// eseguo lo statement
				n = updateStmt.executeUpdate();
				updateStmt.close();
				DriverRegistroServiziDB_LIB.logDebug("CRUDScope type = " + type
						+ " row affected =" + n);

				DriverRegistroServiziDB_LIB.logDebug("CRUDScope DELETE : \n"
						+ DriverRegistroServiziDB_LIB.formatSQLString(
								updateQuery, idScope));

				break;
			}

		} catch (SQLException se) {
			throw new DriverRegistroServiziException(
					"[DriverControlStationDB_LIB::CRUDScope] SQLException ["
					+ se.getMessage() + "].",se);
		} catch (Exception se) {
			throw new DriverRegistroServiziException(
					"[DriverControlStationDB_LIB::CRUDScope] Exception ["
					+ se.getMessage() + "].",se);
		} finally {

			JDBCUtilities.closeResources(updateStmt);
			JDBCUtilities.closeResources(selectRS, selectStmt);
		}
	}
	

	
	
	// UTILS ID
	
	/**
	 * Ritorna l'id del connettore di un soggetto
	 * 
	 * @param nomeSoggetto
	 * @param tipoSoggetto
	 * @param con
	 * @return id del connettore del soggetto
	 * @throws DriverRegistroServiziException
	 */
	static long getIdConnettoreSoggetto(String nomeSoggetto, String tipoSoggetto, Connection con) throws DriverRegistroServiziException {
		PreparedStatement stm = null;
		ResultSet rs = null;
		long idConnettore = -1;
		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("tipo_soggetto = ?");
			sqlQueryObject.addWhereCondition("nome_soggetto = ?");
			sqlQueryObject.setANDLogicOperator(true);
			String query = sqlQueryObject.createSQLQuery();
			stm = con.prepareStatement(query);
			stm.setString(1, tipoSoggetto);
			stm.setString(2, nomeSoggetto);

			rs = stm.executeQuery();

			if (rs.next()) {
				idConnettore = rs.getLong("id_connettore");
			}

			return idConnettore;

		} catch (SQLException e) {
			DriverRegistroServiziDB_LIB.log.error("Errore SQL", e);
			throw new DriverRegistroServiziException(e);
		}catch (Exception e) {
			DriverRegistroServiziDB_LIB.log.error("Errore", e);
			throw new DriverRegistroServiziException(e);
		} finally {
			JDBCUtilities.closeResources(rs, stm);
		}
	}

	/**
	 * Ritorna l'id del connettore del servizio fornito da un soggetto
	 * 
	 * @param nomeServizio
	 * @param tipoServizio
	 * @param nomeSoggetto
	 * @param tipoSoggetto
	 * @param con 
	 * @return id del connettore del servizio
	 * @throws DriverRegistroServiziException
	 */
	static long getIdConnettoreServizio(String nomeServizio, String tipoServizio, Integer versioneServizio, String nomeSoggetto, String tipoSoggetto, Connection con) throws DriverRegistroServiziException {
		PreparedStatement stm = null;
		ResultSet rs = null;
		long idConnettore = -1;
		long idSoggetto;
		try {

			idSoggetto = DBUtils.getIdSoggetto(nomeSoggetto, tipoSoggetto, con, DriverRegistroServiziDB_LIB.tipoDB);

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.SERVIZI);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("tipo_servizio = ?");
			sqlQueryObject.addWhereCondition("nome_servizio = ?");
			sqlQueryObject.addWhereCondition("versione_servizio = ?");
			sqlQueryObject.addWhereCondition("id_soggetto = ?");
			sqlQueryObject.setANDLogicOperator(true);
			String query = sqlQueryObject.createSQLQuery();
			stm = con.prepareStatement(query);
			int index = 1;
			stm.setString(index++, tipoServizio);
			stm.setString(index++, nomeServizio);
			stm.setInt(index++, versioneServizio);
			stm.setLong(index++, idSoggetto);

			rs = stm.executeQuery();

			if (rs.next()) {
				idConnettore = rs.getLong("id_connettore");
			}

			return idConnettore;

		} catch (CoreException e) {
			DriverRegistroServiziDB_LIB.log.error("Errore Driver.", e);
			throw new DriverRegistroServiziException(e);
		} catch (SQLException e) {
			DriverRegistroServiziDB_LIB.log.error("Errore SQL", e);
			throw new DriverRegistroServiziException(e);
		} catch (Exception e) {
			DriverRegistroServiziDB_LIB.log.error("Errore", e);
			throw new DriverRegistroServiziException(e);
		}finally {
			JDBCUtilities.closeResources(rs, stm);
		}
	}

	static long getIdFruizione(long idServizio, String nomeSoggetto, String tipoSoggetto, Connection con) throws DriverRegistroServiziException {
		PreparedStatement stm = null;
		ResultSet rs = null;
		long idFruizione = -1;
		long idSoggetto;
		try {

			idSoggetto = DBUtils.getIdSoggetto(nomeSoggetto, tipoSoggetto, con, DriverRegistroServiziDB_LIB.tipoDB);

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.SERVIZI_FRUITORI);
			sqlQueryObject.addSelectField("id");
			sqlQueryObject.addWhereCondition("id_servizio = ?");
			sqlQueryObject.addWhereCondition("id_soggetto = ?");
			sqlQueryObject.setANDLogicOperator(true);
			String query = sqlQueryObject.createSQLQuery();
			stm = con.prepareStatement(query);
			stm.setLong(1, idServizio);
			stm.setLong(2, idSoggetto);

			rs = stm.executeQuery();

			if (rs.next()) {
				idFruizione = rs.getLong("id");
			}

			return idFruizione;

		} catch (CoreException e) {
			DriverRegistroServiziDB_LIB.log.error("Errore Core: "+e.getMessage(),e);
			throw new DriverRegistroServiziException(e);
		} catch (SQLException e) {
			DriverRegistroServiziDB_LIB.log.error("Errore SQL: "+e.getMessage(), e);
			throw new DriverRegistroServiziException(e);
		}catch (Exception e) {
			DriverRegistroServiziDB_LIB.log.error("Errore Generico: "+e.getMessage(), e);
			throw new DriverRegistroServiziException(e);
		} finally {
			JDBCUtilities.closeResources(rs, stm);
		}
	}
	
	static long getIdConnettoreServizioFruitore(long idServizio, String nomeSoggetto, String tipoSoggetto, Connection con) throws DriverRegistroServiziException {
		PreparedStatement stm = null;
		ResultSet rs = null;
		long idConnettore = -1;
		long idSoggetto;
		try {

			idSoggetto = DBUtils.getIdSoggetto(nomeSoggetto, tipoSoggetto, con, DriverRegistroServiziDB_LIB.tipoDB);

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.SERVIZI_FRUITORI);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("id_servizio = ?");
			sqlQueryObject.addWhereCondition("id_soggetto = ?");
			sqlQueryObject.setANDLogicOperator(true);
			String query = sqlQueryObject.createSQLQuery();
			stm = con.prepareStatement(query);
			stm.setLong(1, idServizio);
			stm.setLong(2, idSoggetto);

			rs = stm.executeQuery();

			if (rs.next()) {
				idConnettore = rs.getLong("id_connettore");
			}

			return idConnettore;

		} catch (CoreException e) {
			DriverRegistroServiziDB_LIB.log.error("Errore Core: "+e.getMessage(),e);
			throw new DriverRegistroServiziException(e);
		} catch (SQLException e) {
			DriverRegistroServiziDB_LIB.log.error("Errore SQL: "+e.getMessage(), e);
			throw new DriverRegistroServiziException(e);
		}catch (Exception e) {
			DriverRegistroServiziDB_LIB.log.error("Errore Generico: "+e.getMessage(), e);
			throw new DriverRegistroServiziException(e);
		} finally {
			JDBCUtilities.closeResources(rs, stm);
		}
	}
	
	static long getIdConnettoreServizioAzione(long idServizio, String azione, Connection con) throws DriverRegistroServiziException {
		PreparedStatement stm = null;
		ResultSet rs = null;
		long idConnettore = -1;
		try {

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.SERVIZI_AZIONI);
			sqlQueryObject.addFromTable(CostantiDB.SERVIZI_AZIONE);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_AZIONI+".id="+CostantiDB.SERVIZI_AZIONE+".id_servizio_azioni");
			sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_AZIONI+".id_servizio = ?");
			sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_AZIONE+".nome_azione = ?");
			sqlQueryObject.setANDLogicOperator(true);
			String query = sqlQueryObject.createSQLQuery();
			stm = con.prepareStatement(query);
			stm.setLong(1, idServizio);
			stm.setString(2, azione);

			rs = stm.executeQuery();

			if (rs.next()) {
				idConnettore = rs.getLong("id_connettore");
			}

			return idConnettore;

		} catch (SQLException e) {
			DriverRegistroServiziDB_LIB.log.error("Errore SQL", e);
			throw new DriverRegistroServiziException(e);
		}catch (Exception e) {
			DriverRegistroServiziDB_LIB.log.error("Errore", e);
			throw new DriverRegistroServiziException(e);
		} finally {
			JDBCUtilities.closeResources(rs, stm);
		}
	}
	
	static long getIdConnettoreFruizioneServizioAzione(long idFruizione, String azione, Connection con) throws DriverRegistroServiziException {
		PreparedStatement stm = null;
		ResultSet rs = null;
		long idConnettore = -1;
		try {

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.SERVIZI_FRUITORI_AZIONI);
			sqlQueryObject.addFromTable(CostantiDB.SERVIZI_FRUITORI_AZIONE);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_FRUITORI_AZIONI+".id="+CostantiDB.SERVIZI_FRUITORI_AZIONE+".id_fruizione_azioni");
			sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_FRUITORI_AZIONI+".id_fruizione = ?");
			sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_FRUITORI_AZIONE+".nome_azione = ?");
			sqlQueryObject.setANDLogicOperator(true);
			String query = sqlQueryObject.createSQLQuery();
			stm = con.prepareStatement(query);
			stm.setLong(1, idFruizione);
			stm.setString(2, azione);

			rs = stm.executeQuery();

			if (rs.next()) {
				idConnettore = rs.getLong("id_connettore");
			}

			return idConnettore;

		} catch (SQLException e) {
			DriverRegistroServiziDB_LIB.log.error("Errore SQL", e);
			throw new DriverRegistroServiziException(e);
		}catch (Exception e) {
			DriverRegistroServiziDB_LIB.log.error("Errore", e);
			throw new DriverRegistroServiziException(e);
		} finally {
			JDBCUtilities.closeResources(rs, stm);
		}
	}

	
	
	
	
	

	public static void CRUDProtocolProperty(int type, List<ProtocolProperty> listPP, long idProprietario,
			org.openspcoop2.core.constants.ProprietariProtocolProperty tipologiaProprietarioProtocolProperty, Connection connection,
			String tipoDatabase) throws DriverRegistroServiziException {
		try {
			DBProtocolPropertiesUtils.CRUDRegistryProtocolProperty(log, type, listPP, idProprietario, tipologiaProprietarioProtocolProperty, connection, tipoDatabase);
		}catch(Exception e) {
			throw new DriverRegistroServiziException(e.getMessage(),e);
		}
	}
	
	
	public static List<ProtocolProperty> getListaProtocolProperty(long idProprietario, org.openspcoop2.core.constants.ProprietariProtocolProperty tipologiaProprietario, 
			Connection connection,
			String tipoDatabase) throws DriverRegistroServiziException,DriverRegistroServiziNotFound {
		try {
			return DBProtocolPropertiesUtils.getListaProtocolPropertyRegistry(idProprietario,tipologiaProprietario,connection,tipoDatabase);
		}
		catch(NotFoundException e) {
			throw new DriverRegistroServiziNotFound(e.getMessage(),e);
		}
		catch(Exception e) {
			throw new DriverRegistroServiziException(e.getMessage(),e);
		}
	}
	
	public static ProtocolProperty getProtocolProperty(long id, Connection connection, String tipoDatabase) throws DriverRegistroServiziException,DriverRegistroServiziNotFound {
		try {
			return DBProtocolPropertiesUtils.getProtocolPropertyRegistry(id, connection, tipoDatabase);
		}
		catch(NotFoundException e) {
			throw new DriverRegistroServiziNotFound(e.getMessage(),e);
		}
		catch(Exception e) {
			throw new DriverRegistroServiziException(e.getMessage(),e);
		}
	}
	
}
