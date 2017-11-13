/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2017 Link.it srl (http://link.it). 
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
import java.sql.Types;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.core.commons.DBUtils;
import org.openspcoop2.core.constants.CostantiConnettori;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.constants.TipiConnettore;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDAccordoCooperazione;
import org.openspcoop2.core.id.IDRuolo;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.AccordoCooperazionePartecipanti;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteComuneServizioComposto;
import org.openspcoop2.core.registry.AccordoServizioParteComuneServizioCompostoServizioComponente;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Azione;
import org.openspcoop2.core.registry.ConfigurazioneServizioAzione;
import org.openspcoop2.core.registry.Connettore;
import org.openspcoop2.core.registry.CredenzialiSoggetto;
import org.openspcoop2.core.registry.Documento;
import org.openspcoop2.core.registry.Fruitore;
import org.openspcoop2.core.registry.IdSoggetto;
import org.openspcoop2.core.registry.MessagePart;
import org.openspcoop2.core.registry.Operation;
import org.openspcoop2.core.registry.PortType;
import org.openspcoop2.core.registry.PortaDominio;
import org.openspcoop2.core.registry.Property;
import org.openspcoop2.core.registry.ProtocolProperty;
import org.openspcoop2.core.registry.Resource;
import org.openspcoop2.core.registry.ResourceParameter;
import org.openspcoop2.core.registry.ResourceRepresentation;
import org.openspcoop2.core.registry.ResourceRequest;
import org.openspcoop2.core.registry.ResourceResponse;
import org.openspcoop2.core.registry.Ruolo;
import org.openspcoop2.core.registry.RuoloSoggetto;
import org.openspcoop2.core.registry.constants.BindingStyle;
import org.openspcoop2.core.registry.constants.BindingUse;
import org.openspcoop2.core.registry.constants.CostantiRegistroServizi;
import org.openspcoop2.core.registry.constants.CredenzialeTipo;
import org.openspcoop2.core.registry.constants.FormatoSpecifica;
import org.openspcoop2.core.registry.constants.HttpMethod;
import org.openspcoop2.core.registry.constants.MessageType;
import org.openspcoop2.core.registry.constants.ParameterType;
import org.openspcoop2.core.registry.constants.ProfiloCollaborazione;
import org.openspcoop2.core.registry.constants.ProprietariDocumento;
import org.openspcoop2.core.registry.constants.ProprietariProtocolProperty;
import org.openspcoop2.core.registry.constants.RepresentationType;
import org.openspcoop2.core.registry.constants.RepresentationXmlType;
import org.openspcoop2.core.registry.constants.RuoliDocumento;
import org.openspcoop2.core.registry.constants.RuoloContesto;
import org.openspcoop2.core.registry.constants.RuoloTipologia;
import org.openspcoop2.core.registry.constants.ServiceBinding;
import org.openspcoop2.core.registry.constants.StatoFunzionalita;
import org.openspcoop2.core.registry.constants.TipologiaServizio;
import org.openspcoop2.core.registry.driver.BeanUtilities;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziNotFound;
import org.openspcoop2.core.registry.driver.IDAccordoCooperazioneFactory;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.jdbc.IJDBCAdapter;
import org.openspcoop2.utils.jdbc.JDBCAdapterFactory;
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

	// Tipo database ereditato da DriverRegistroServiziDB
	private static String tipoDB = null;

	/** Log ereditato da DriverRegistroServiziDB
	 */
	private static boolean initialize = false;
	public static void initStaticLogger(Logger aLog){
		if(DriverRegistroServiziDB_LIB.initialize==false){
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

	/** Metodi CRUD */

	public static void CRUDPortaDominio(int type, PortaDominio pdd, Connection con)
	throws DriverRegistroServiziException {
		if (pdd == null) {
			throw new DriverRegistroServiziException(
			"[DriverRegistroServiziDB_LIB::CRUDPdd] Parametro non valido.");
		}

		/*if ((type != CostantiDB.CREATE) && (pdd.getId() <= 0)) {
			throw new DriverRegistroServiziException(
			"[DriverRegistroServiziDB_LIB::CRUDPdd] ID Pdd non valido.");
		}*/

		String nome = pdd.getNome();
		String descrizione = pdd.getDescrizione();
		String implementazione = pdd.getImplementazione();

		String subject = pdd.getSubject();
		StatoFunzionalita client_auth = pdd.getClientAuth();
		
		String superuser = pdd.getSuperUser();

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
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
				sqlQueryObject.addInsertTable(CostantiDB.PDD);
				sqlQueryObject.addInsertField("nome", "?");
				sqlQueryObject.addInsertField("descrizione", "?");
				sqlQueryObject.addInsertField("implementazione", "?");
				sqlQueryObject.addInsertField("subject", "?");
				sqlQueryObject.addInsertField("client_auth", "?");
				sqlQueryObject.addInsertField("superuser", "?");
				if(pdd.getOraRegistrazione()!=null)
					sqlQueryObject.addInsertField("ora_registrazione", "?");

				updateQuery = sqlQueryObject.createSQLInsert();
				updateStmt = con.prepareStatement(updateQuery);

				updateStmt.setString(1, nome);
				updateStmt.setString(2, descrizione);
				updateStmt.setString(3, implementazione);
				updateStmt.setString(4, (subject != null ? Utilities.formatSubject(subject) : null));
				updateStmt.setString(5, getValue(client_auth));
				updateStmt.setString(6, superuser);
				if(pdd.getOraRegistrazione()!=null)
					updateStmt.setTimestamp(7, new Timestamp(pdd.getOraRegistrazione().getTime()));

				// eseguo lo statement
				n = updateStmt.executeUpdate();

				updateStmt.close();

				DriverRegistroServiziDB_LIB.log.debug("CRUDPdd type = " + type
						+ " row affected =" + n);

				DriverRegistroServiziDB_LIB.log.debug("CRUDPdd CREATE : \n"
						+ DriverRegistroServiziDB_LIB.formatSQLString(
								updateQuery, nome, descrizione,implementazione,subject,client_auth));

				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PDD);
				sqlQueryObject.addSelectField("id");
				sqlQueryObject.addWhereCondition("nome = ?");
				selectQuery = sqlQueryObject.createSQLQuery();
				selectStmt = con.prepareStatement(selectQuery);
				selectStmt.setString(1, nome);

				selectRS = selectStmt.executeQuery();
				if (selectRS.next()) {
					pdd.setId(selectRS.getLong("id"));
				}
				selectRS.close();
				selectStmt.close();
				break;

			case UPDATE:
				// UPDATE

				String nomePdd = pdd.getOldNomeForUpdate();
				if(nomePdd==null || "".equals(nomePdd))
					nomePdd = pdd.getNome();
				
				long idPdd = DBUtils.getIdPortaDominio(nomePdd, con, DriverRegistroServiziDB_LIB.tipoDB);
				if (idPdd <= 0)
					throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDPortaDominio(UPDATE)] Id Porta di Dominio non valido.");
				
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
				sqlQueryObject.addUpdateTable(CostantiDB.PDD);
				sqlQueryObject.addUpdateField("nome", "?");
				sqlQueryObject.addUpdateField("descrizione", "?");
				sqlQueryObject.addUpdateField("implementazione", "?");
				sqlQueryObject.addUpdateField("subject", "?");
				sqlQueryObject.addUpdateField("client_auth", "?");
				sqlQueryObject.addUpdateField("superuser", "?");
				if(pdd.getOraRegistrazione()!=null)
					sqlQueryObject.addUpdateField("ora_registrazione", "?");
				sqlQueryObject.addWhereCondition("id=?");
				updateQuery = sqlQueryObject.createSQLUpdate();
				updateStmt = con.prepareStatement(updateQuery);

				updateStmt.setString(1, nome);
				updateStmt.setString(2, descrizione);
				updateStmt.setString(3, implementazione);
				updateStmt.setString(4, (subject != null ? Utilities.formatSubject(subject) : null));
				updateStmt.setString(5, getValue(client_auth));
				updateStmt.setString(6, superuser);

				int param_index = 6;

				if(pdd.getOraRegistrazione()!=null)
					updateStmt.setTimestamp(++param_index, new Timestamp(pdd.getOraRegistrazione().getTime()));

				updateStmt.setLong(++param_index, idPdd);

				// eseguo lo statement
				n = updateStmt.executeUpdate();
				updateStmt.close();
				DriverRegistroServiziDB_LIB.log.debug("CRUDPdd type = " + type
						+ " row affected =" + n);

				DriverRegistroServiziDB_LIB.log.debug("CRUDPdd UPDATE : \n"
						+ DriverRegistroServiziDB_LIB.formatSQLString(
								updateQuery, descrizione, implementazione,subject,client_auth,idPdd));

				break;

			case DELETE:
				// DELETE

				idPdd = DBUtils.getIdPortaDominio(nome, con, DriverRegistroServiziDB_LIB.tipoDB);
				if (idPdd <= 0)
					throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDPortaDominio(DELETE)] Id Porta di Dominio non valido.");
				
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PDD);
				sqlQueryObject.addWhereCondition("id=?");
				updateQuery = sqlQueryObject.createSQLDelete();
				updateStmt = con.prepareStatement(updateQuery);

				updateStmt.setLong(1, idPdd);

				// eseguo lo statement
				n = updateStmt.executeUpdate();
				updateStmt.close();
				DriverRegistroServiziDB_LIB.log.debug("CRUDPdd type = " + type
						+ " row affected =" + n);

				DriverRegistroServiziDB_LIB.log.debug("CRUDPdd DELETE : \n"
						+ DriverRegistroServiziDB_LIB.formatSQLString(
								updateQuery, idPdd));

				break;
			}

		} catch (SQLException se) {
			throw new DriverRegistroServiziException(
					"[DriverControlStationDB_LIB::CRUDPdd] SQLException ["
					+ se.getMessage() + "].",se);
		} catch (Exception se) {
			throw new DriverRegistroServiziException(
					"[DriverControlStationDB_LIB::CRUDPdd] Exception ["
					+ se.getMessage() + "].",se);
		} finally {

			try {
				updateStmt.close();
				selectRS.close();
				selectStmt.close();

			} catch (Exception e) {
				// ignore exception
			}
		}
	}
	
	
	public static void CRUDRuolo(int type, Ruolo ruolo, Connection con)
			throws DriverRegistroServiziException {
		if (ruolo == null) {
			throw new DriverRegistroServiziException(
			"[DriverRegistroServiziDB_LIB::CRUDRuolo] Parametro non valido.");
		}

		/*if ((type != CostantiDB.CREATE) && (pdd.getId() <= 0)) {
			throw new DriverRegistroServiziException(
			"[DriverRegistroServiziDB_LIB::CRUDRuolo] ID Ruolo non valido.");
		}*/

		String nome = ruolo.getNome();
		String descrizione = ruolo.getDescrizione();
		RuoloTipologia ruoloTipologia = ruolo.getTipologia();
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
				// CREATE
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
				sqlQueryObject.addInsertTable(CostantiDB.RUOLI);
				sqlQueryObject.addInsertField("nome", "?");
				sqlQueryObject.addInsertField("descrizione", "?");
				sqlQueryObject.addInsertField("tipologia", "?");
				sqlQueryObject.addInsertField("contesto_utilizzo", "?");
				sqlQueryObject.addInsertField("superuser", "?");
				if(ruolo.getOraRegistrazione()!=null)
					sqlQueryObject.addInsertField("ora_registrazione", "?");

				updateQuery = sqlQueryObject.createSQLInsert();
				updateStmt = con.prepareStatement(updateQuery);

				int index = 1;
				updateStmt.setString(index++, nome);
				updateStmt.setString(index++, descrizione);
				updateStmt.setString(index++, ruoloTipologia!=null? ruoloTipologia.getValue() : null);
				updateStmt.setString(index++, ruoloContesto!=null? ruoloContesto.getValue() : null);
				updateStmt.setString(index++, superuser);
				if(ruolo.getOraRegistrazione()!=null)
					updateStmt.setTimestamp(index++, new Timestamp(ruolo.getOraRegistrazione().getTime()));

				// eseguo lo statement
				n = updateStmt.executeUpdate();

				updateStmt.close();

				DriverRegistroServiziDB_LIB.log.debug("CRUDRuolo type = " + type
						+ " row affected =" + n);

				DriverRegistroServiziDB_LIB.log.debug("CRUDRuolo CREATE : \n"
						+ DriverRegistroServiziDB_LIB.formatSQLString(
								updateQuery, nome, descrizione,(ruoloTipologia!=null? ruoloTipologia.getValue() : null),
								(ruoloContesto!=null? ruoloContesto.getValue() : null),superuser));

				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PDD);
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
				// UPDATE

				String nomeRuolo = ruolo.getOldIDRuoloForUpdate()!=null ? ruolo.getOldIDRuoloForUpdate().getNome() : null;
				if(nomeRuolo==null || "".equals(nomeRuolo))
					nomeRuolo = ruolo.getNome();
				
				IDRuolo idR = new IDRuolo(nomeRuolo);
				long idRuolo = DBUtils.getIdRuolo(idR, con, DriverRegistroServiziDB_LIB.tipoDB);
				if (idRuolo <= 0)
					throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDRuolo(UPDATE)] Id Ruolo non valido.");
				
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
				sqlQueryObject.addUpdateTable(CostantiDB.RUOLI);
				sqlQueryObject.addUpdateField("nome", "?");
				sqlQueryObject.addUpdateField("descrizione", "?");
				sqlQueryObject.addUpdateField("tipologia", "?");
				sqlQueryObject.addUpdateField("contesto_utilizzo", "?");
				sqlQueryObject.addUpdateField("superuser", "?");
				if(ruolo.getOraRegistrazione()!=null)
					sqlQueryObject.addUpdateField("ora_registrazione", "?");
				sqlQueryObject.addWhereCondition("id=?");
				updateQuery = sqlQueryObject.createSQLUpdate();
				updateStmt = con.prepareStatement(updateQuery);

				index = 1;
				updateStmt.setString(index++, nome);
				updateStmt.setString(index++, descrizione);
				updateStmt.setString(index++, ruoloTipologia!=null? ruoloTipologia.getValue() : null);
				updateStmt.setString(index++, ruoloContesto!=null? ruoloContesto.getValue() : null);
				updateStmt.setString(index++, superuser);
				if(ruolo.getOraRegistrazione()!=null)
					updateStmt.setTimestamp(index++, new Timestamp(ruolo.getOraRegistrazione().getTime()));
	
				updateStmt.setLong(index++, idRuolo);

				// eseguo lo statement
				n = updateStmt.executeUpdate();
				updateStmt.close();
				DriverRegistroServiziDB_LIB.log.debug("CRUDRuolo type = " + type
						+ " row affected =" + n);

				DriverRegistroServiziDB_LIB.log.debug("CRUDRuolo UPDATE : \n"
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
				DriverRegistroServiziDB_LIB.log.debug("CRUDRuolo type = " + type
						+ " row affected =" + n);

				DriverRegistroServiziDB_LIB.log.debug("CRUDRuolo DELETE : \n"
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

			try {
				updateStmt.close();
				selectRS.close();
				selectStmt.close();

			} catch (Exception e) {
				// ignore exception
			}
		}
	}
	

	/**
	 * CRUD oggetto Connettore. In caso di CREATE inserisce nel db il dati del
	 * connettore passato e ritorna l'id dell'oggetto creato Non si occupa di
	 * chiudere la connessione con il db in caso di errore in quanto verra'
	 * gestita dal metodo chiamante
	 * 
	 * @param type
	 *            Tipo operazione {1 (CREATE),2 (UPDATE),3 (DELETE)}
	 * @param connettore
	 * @return id del connettore in caso di type 1 (CREATE)
	 */
	public static long CRUDConnettore(int type, Connettore connettore, Connection connection) throws DriverRegistroServiziException {
		PreparedStatement stm = null;
		String sqlQuery;

		if(connettore == null) throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDConnettore] L'oggetto Connettore non puo essere NULL.");
		if (type!=CostantiDB.DELETE){
			if(connettore.getNome() == null || connettore.getNome().trim().equals(""))throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDConnettore] il nome connettore non puo essere NULL.");
		}

		String nomeConnettore = null;
		String endpointtype = null;
		boolean debug = false;
		String url = null;
		String nome = null;
		String tipo = null;
		String utente = null;
		String password = null;
		String initcont = null;
		String urlpkg = null;
		String provurl = null;
		String connectionfactory = null;
		String sendas = null;

		String transfer_mode = null; // in caso di tipo http e https
		Integer transfer_mode_chunk_size = null; // in caso di tipo http e https

		boolean proxy = false;
		String proxy_type = null;
		String proxy_hostname = null;
		String proxy_port = null;
		String proxy_username = null;
		String proxy_password = null;

		String redirect_mode = null; // in caso di tipo http e https
		Integer redirect_max_hop = null; // in caso di tipo http e https
		
		// setto i dati, se le property non sono presenti il loro valore rimarra
		// a null e verra settato come tale nel DB
		String nomeProperty = null;
		String valoreProperty = null;

		boolean isAbilitato = false;
		
		nomeConnettore = connettore.getNome();
		endpointtype = connettore.getTipo();

		if (endpointtype == null || endpointtype.trim().equals(""))
			endpointtype = TipiConnettore.DISABILITATO.getNome();

		Hashtable<String, String> extendedProperties = new Hashtable<String, String>();
		
		List<String> propertiesGestiteAttraversoColonneAdHoc = new ArrayList<String>();
		
		for (int i = 0; i < connettore.sizePropertyList(); i++) {
			// prop=connettore.getProperty(i);
			nomeProperty = connettore.getProperty(i).getNome();

			valoreProperty = connettore.getProperty(i).getValore();
			if (valoreProperty != null && valoreProperty.equals(""))
				valoreProperty = null;

			// Debug
			if (nomeProperty.equals(CostantiDB.CONNETTORE_DEBUG)){
				if("true".equals(valoreProperty)){
					debug=true;
				}
			}
			
			// Proxy
			if (nomeProperty.equals(CostantiDB.CONNETTORE_PROXY_TYPE)){
				proxy = true;
				proxy_type = valoreProperty;
				
				propertiesGestiteAttraversoColonneAdHoc.add(nomeProperty);
				
				// cerco altri valori del proxy
				for (Property propertyCheck: connettore.getPropertyList()) {
					if (propertyCheck.getNome().equals(CostantiDB.CONNETTORE_PROXY_HOSTNAME)){
						propertiesGestiteAttraversoColonneAdHoc.add(propertyCheck.getNome());
						proxy_hostname = propertyCheck.getValore();
					}
					if (propertyCheck.getNome().equals(CostantiDB.CONNETTORE_PROXY_PORT)){
						propertiesGestiteAttraversoColonneAdHoc.add(propertyCheck.getNome());
						proxy_port = propertyCheck.getValore();
					}
					if (propertyCheck.getNome().equals(CostantiDB.CONNETTORE_PROXY_USERNAME)){
						propertiesGestiteAttraversoColonneAdHoc.add(propertyCheck.getNome());
						proxy_username = propertyCheck.getValore();
					}
					if (propertyCheck.getNome().equals(CostantiDB.CONNETTORE_PROXY_PASSWORD)){
						propertiesGestiteAttraversoColonneAdHoc.add(propertyCheck.getNome());
						proxy_password = propertyCheck.getValore();
					}
				}
			}
			
			// TransferMode
			if (nomeProperty.equals(CostantiDB.CONNETTORE_HTTP_DATA_TRANSFER_MODE)){
				propertiesGestiteAttraversoColonneAdHoc.add(nomeProperty);
				transfer_mode = valoreProperty;
			}
			if (nomeProperty.equals(CostantiDB.CONNETTORE_HTTP_DATA_TRANSFER_MODE_CHUNK_SIZE)){
				propertiesGestiteAttraversoColonneAdHoc.add(nomeProperty);
				transfer_mode_chunk_size = Integer.parseInt(valoreProperty);
			}
			
			// RedirectMode
			if (nomeProperty.equals(CostantiDB.CONNETTORE_HTTP_REDIRECT_FOLLOW)){
				propertiesGestiteAttraversoColonneAdHoc.add(nomeProperty);
				redirect_mode = valoreProperty;
			}
			if (nomeProperty.equals(CostantiDB.CONNETTORE_HTTP_REDIRECT_MAX_HOP)){
				propertiesGestiteAttraversoColonneAdHoc.add(nomeProperty);
				redirect_max_hop = Integer.parseInt(valoreProperty);
			}

			if(TipiConnettore.HTTP.getNome().equals(endpointtype)){
				if (nomeProperty.equals(CostantiDB.CONNETTORE_HTTP_LOCATION))
					url = valoreProperty;
				else if (nomeProperty.equals(CostantiDB.CONNETTORE_USER))
					utente = valoreProperty;
				else if (nomeProperty.equals(CostantiDB.CONNETTORE_PWD))
					password = valoreProperty;
			}
			else if(TipiConnettore.JMS.getNome().equals(endpointtype)){
				if (nomeProperty.equals(CostantiDB.CONNETTORE_JMS_NOME))
					nome = valoreProperty;
				else if (nomeProperty.equals(CostantiDB.CONNETTORE_JMS_TIPO))
					tipo = valoreProperty;
				else if (nomeProperty.equals(CostantiDB.CONNETTORE_USER))
					utente = valoreProperty;
				else if (nomeProperty.equals(CostantiDB.CONNETTORE_PWD))
					password = valoreProperty;
				else if (nomeProperty.equals(CostantiDB.CONNETTORE_JMS_CONTEXT_JAVA_NAMING_FACTORY_INITIAL))
					initcont = valoreProperty;
				else if (nomeProperty.equals(CostantiDB.CONNETTORE_JMS_CONTEXT_JAVA_NAMING_FACTORY_URL_PKG))
					urlpkg = valoreProperty;
				else if (nomeProperty.equals(CostantiDB.CONNETTORE_JMS_CONTEXT_JAVA_NAMING_PROVIDER_URL))
					provurl = valoreProperty;
				else if (nomeProperty.equals(CostantiDB.CONNETTORE_JMS_CONNECTION_FACTORY))
					connectionfactory = valoreProperty;
				else if (nomeProperty.equals(CostantiDB.CONNETTORE_JMS_SEND_AS))
					sendas = valoreProperty;
			}

			// se endpointype != disabilitato allora lo setto abilitato
			if (!endpointtype.equalsIgnoreCase(TipiConnettore.DISABILITATO.getNome()))
				isAbilitato = true;
			
			// extendedProperties
			if(nomeProperty.startsWith(CostantiConnettori.CONNETTORE_EXTENDED_PREFIX)){
				extendedProperties.put(nomeProperty, valoreProperty);
			}
		}

		try {

			long idConnettore = 0;
			switch (type) {
			case 1:

				// create
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
				sqlQueryObject.addInsertTable(CostantiDB.CONNETTORI);
				sqlQueryObject.addInsertField("endpointtype", "?");
				sqlQueryObject.addInsertField("url", "?");
				sqlQueryObject.addInsertField("transfer_mode", "?");
				sqlQueryObject.addInsertField("transfer_mode_chunk_size", "?");
				sqlQueryObject.addInsertField("redirect_mode", "?");
				sqlQueryObject.addInsertField("redirect_max_hop", "?");
				sqlQueryObject.addInsertField("nome", "?");
				sqlQueryObject.addInsertField("tipo", "?");
				sqlQueryObject.addInsertField("utente", "?");
				sqlQueryObject.addInsertField("password", "?");
				sqlQueryObject.addInsertField("initcont", "?");
				sqlQueryObject.addInsertField("urlpkg", "?");
				sqlQueryObject.addInsertField("provurl", "?");
				sqlQueryObject.addInsertField("connection_factory", "?");
				sqlQueryObject.addInsertField("send_as", "?");
				sqlQueryObject.addInsertField("nome_connettore", "?");
				sqlQueryObject.addInsertField("debug", "?");
				sqlQueryObject.addInsertField("proxy", "?");		
				sqlQueryObject.addInsertField("proxy_type", "?");		
				sqlQueryObject.addInsertField("proxy_hostname", "?");		
				sqlQueryObject.addInsertField("proxy_port", "?");		
				sqlQueryObject.addInsertField("proxy_username", "?");		
				sqlQueryObject.addInsertField("proxy_password", "?");
				sqlQueryObject.addInsertField("custom", "?");
				sqlQuery = sqlQueryObject.createSQLInsert();
				stm = connection.prepareStatement(sqlQuery);

				int index = 1;
				stm.setString(index++, endpointtype);
				stm.setString(index++, url);
				stm.setString(index++, (isAbilitato ? transfer_mode : null));
				if(isAbilitato && transfer_mode_chunk_size!=null){
					stm.setInt(index++, transfer_mode_chunk_size);
				}
				else{
					stm.setNull(index++, Types.INTEGER);
				}
				stm.setString(index++, (isAbilitato ? redirect_mode : null));
				if(isAbilitato && redirect_max_hop!=null){
					stm.setInt(index++, redirect_max_hop);
				}
				else{
					stm.setNull(index++, Types.INTEGER);
				}
				stm.setString(index++, nome);
				stm.setString(index++, tipo);
				stm.setString(index++, utente);
				stm.setString(index++, password);
				stm.setString(index++, initcont);
				stm.setString(index++, urlpkg);
				stm.setString(index++, provurl);
				stm.setString(index++, connectionfactory);
				stm.setString(index++, sendas);
				stm.setString(index++, nomeConnettore);
				if(debug){
					stm.setInt(index++, 1);
				}else{
					stm.setInt(index++, 0);
				}
				if(proxy){
					stm.setInt(index++, 1);
				}else{
					stm.setInt(index++, 0);
				}
				stm.setString(index++, isAbilitato && proxy ? proxy_type : null);
				stm.setString(index++, isAbilitato && proxy ? proxy_hostname : null);
				stm.setString(index++, isAbilitato && proxy ? proxy_port : null);
				stm.setString(index++, isAbilitato && proxy ? proxy_username : null);
				stm.setString(index++, isAbilitato && proxy ? proxy_password : null);
				if(connettore.getCustom()!=null && connettore.getCustom()){
					stm.setInt(index++, 1);
				}else{
					stm.setInt(index++, 0);
				}

				DriverRegistroServiziDB_LIB.log.debug("CRUDConnettore CREATE : \n" + 
						DriverRegistroServiziDB_LIB.formatSQLString(sqlQuery, endpointtype, url, 
								transfer_mode, transfer_mode_chunk_size, redirect_mode, redirect_max_hop,
								nome, tipo, utente, password, initcont, urlpkg, provurl, connectionfactory, sendas, nomeConnettore, debug, 
								proxy, proxy_type, proxy_hostname, proxy_port, proxy_username, proxy_password,
								(connettore.getCustom()!=null && connettore.getCustom())));
				int n = stm.executeUpdate();
				DriverRegistroServiziDB_LIB.log.debug("CRUDConnettore type = " + type + " row affected =" + n);
				stm.close();
				
				ResultSet rs;
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.CONNETTORI);
				sqlQueryObject.addSelectField("*");
				sqlQueryObject.addWhereCondition("nome_connettore = ?");
				sqlQueryObject.addWhereCondition("endpointtype = ?");
				sqlQueryObject.setANDLogicOperator(true);
				sqlQuery = sqlQueryObject.createSQLQuery();
				stm = connection.prepareStatement(sqlQuery);

				stm.setString(1, nomeConnettore);
				stm.setString(2, endpointtype);

				DriverRegistroServiziDB_LIB.log.debug("Recupero idConnettore inserito : \n" + DriverRegistroServiziDB_LIB.formatSQLString(sqlQuery, nomeConnettore, endpointtype));

				rs = stm.executeQuery();

				if (rs.next()) {
					idConnettore = rs.getLong("id");
				} else {
					throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDConnettore] Errore tentanto di effettuare la select dopo una create, non riesco a recuperare l'id!");
				}

				rs.close();
				stm.close();
								
				
				// Custom properties
				if(connettore.getCustom()!=null && connettore.getCustom()){					
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
					sqlQueryObject.addInsertTable(CostantiDB.CONNETTORI_CUSTOM);
					sqlQueryObject.addInsertField("name", "?");
					sqlQueryObject.addInsertField("value", "?");
					sqlQueryObject.addInsertField("id_connettore", "?");
					sqlQuery = sqlQueryObject.createSQLInsert();
					
					for (int i = 0; i < connettore.sizePropertyList(); i++) {
						nomeProperty = connettore.getProperty(i).getNome();
						if(propertiesGestiteAttraversoColonneAdHoc.contains(nomeProperty)){
							continue;
						}
						valoreProperty = connettore.getProperty(i).getValore();
						if (valoreProperty != null && valoreProperty.equals(""))
							valoreProperty = null;
					
						if(valoreProperty==null){
							throw new Exception("Property ["+nomeProperty+"] without value");
						}
						
						stm = connection.prepareStatement(sqlQuery);
						stm.setString(1, nomeProperty);
						stm.setString(2, valoreProperty);
						stm.setLong(3, idConnettore);
						stm.executeUpdate();
						stm.close();
					}				
				}
				else if(extendedProperties.size()>0){
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
					sqlQueryObject.addInsertTable(CostantiDB.CONNETTORI_CUSTOM);
					sqlQueryObject.addInsertField("name", "?");
					sqlQueryObject.addInsertField("value", "?");
					sqlQueryObject.addInsertField("id_connettore", "?");
					sqlQuery = sqlQueryObject.createSQLInsert();
					
					Enumeration<String> keys = extendedProperties.keys();
					while (keys.hasMoreElements()) {
						nomeProperty = (String) keys.nextElement();
						valoreProperty = extendedProperties.get(nomeProperty);
						if (valoreProperty != null && valoreProperty.equals(""))
							valoreProperty = null;
					
						if(valoreProperty==null){
							throw new Exception("Property ["+nomeProperty+"] without value");
						}
						
						stm = connection.prepareStatement(sqlQuery);
						stm.setString(1, nomeProperty);
						stm.setString(2, valoreProperty);
						stm.setLong(3, idConnettore);
						stm.executeUpdate();
						stm.close();
					}				
				}
				
				break;

			case 2:
				// update

				idConnettore = connettore.getId();

				if (idConnettore <= 0)
					throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDConnettore] L'id del connettore non puo essere 0 tentando di fare una operazione di update.");

				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
				sqlQueryObject.addUpdateTable(CostantiDB.CONNETTORI);
				sqlQueryObject.addUpdateField("endpointtype", "?");
				sqlQueryObject.addUpdateField("url", "?");
				sqlQueryObject.addUpdateField("transfer_mode", "?");
				sqlQueryObject.addUpdateField("transfer_mode_chunk_size", "?");
				sqlQueryObject.addUpdateField("redirect_mode", "?");
				sqlQueryObject.addUpdateField("redirect_max_hop", "?");
				sqlQueryObject.addUpdateField("nome", "?");
				sqlQueryObject.addUpdateField("tipo", "?");
				sqlQueryObject.addUpdateField("utente", "?");
				sqlQueryObject.addUpdateField("password", "?");
				sqlQueryObject.addUpdateField("initcont", "?");
				sqlQueryObject.addUpdateField("urlpkg", "?");
				sqlQueryObject.addUpdateField("provurl", "?");
				sqlQueryObject.addUpdateField("connection_factory", "?");
				sqlQueryObject.addUpdateField("send_as", "?");
				sqlQueryObject.addUpdateField("nome_connettore", "?");
				sqlQueryObject.addUpdateField("debug", "?");
				sqlQueryObject.addUpdateField("proxy", "?");		
				sqlQueryObject.addUpdateField("proxy_type", "?");		
				sqlQueryObject.addUpdateField("proxy_hostname", "?");		
				sqlQueryObject.addUpdateField("proxy_port", "?");		
				sqlQueryObject.addUpdateField("proxy_username", "?");		
				sqlQueryObject.addUpdateField("proxy_password", "?");
				sqlQueryObject.addUpdateField("custom", "?");
				sqlQueryObject.addWhereCondition("id=?");
				sqlQuery = sqlQueryObject.createSQLUpdate();
				stm = connection.prepareStatement(sqlQuery);

				index = 1;
				stm.setString(index++, endpointtype);
				stm.setString(index++, url);
				stm.setString(index++, (isAbilitato ? transfer_mode : null));
				if(isAbilitato && transfer_mode_chunk_size!=null){
					stm.setInt(index++, transfer_mode_chunk_size);
				}
				else{
					stm.setNull(index++, Types.INTEGER);
				}
				stm.setString(index++, (isAbilitato ? redirect_mode : null));
				if(isAbilitato && redirect_max_hop!=null){
					stm.setInt(index++, redirect_max_hop);
				}
				else{
					stm.setNull(index++, Types.INTEGER);
				}
				stm.setString(index++, nome);
				stm.setString(index++, tipo);
				stm.setString(index++, utente);
				stm.setString(index++, password);
				stm.setString(index++, initcont);
				stm.setString(index++, urlpkg);
				stm.setString(index++, provurl);
				stm.setString(index++, connectionfactory);
				stm.setString(index++, sendas);
				stm.setString(index++, nomeConnettore);
				if(debug){
					stm.setInt(index++, 1);
				}else{
					stm.setInt(index++, 0);
				}
				if(proxy){
					stm.setInt(index++, 1);
				}else{
					stm.setInt(index++, 0);
				}
				stm.setString(index++, isAbilitato && proxy ? proxy_type : null);
				stm.setString(index++, isAbilitato && proxy ? proxy_hostname : null);
				stm.setString(index++, isAbilitato && proxy ? proxy_port : null);
				stm.setString(index++, isAbilitato && proxy ? proxy_username : null);
				stm.setString(index++, isAbilitato && proxy ? proxy_password : null);
				if(connettore.getCustom()!=null && connettore.getCustom()){
					stm.setInt(index++, 1);
				}else{
					stm.setInt(index++, 0);
				}
				stm.setLong(index++, idConnettore);

				DriverRegistroServiziDB_LIB.log.debug("CRUDConnettore UPDATE : \n" + 
						DriverRegistroServiziDB_LIB.formatSQLString(sqlQuery, endpointtype, url, 
								transfer_mode, transfer_mode_chunk_size, redirect_mode, redirect_max_hop,
								nome, tipo, utente, password, initcont, urlpkg, provurl, connectionfactory, sendas, nomeConnettore, debug,
								proxy, proxy_type, proxy_hostname, proxy_port, proxy_username, proxy_password,
								(connettore.getCustom()!=null && connettore.getCustom()),idConnettore));
				n = stm.executeUpdate();
				DriverRegistroServiziDB_LIB.log.debug("CRUDConnettore type = " + type + " row affected =" + n);
				stm.close();
				
				
				// Custom properties
				
				// Delete eventuali vecchie properties
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.CONNETTORI_CUSTOM);
				sqlQueryObject.addWhereCondition("id_connettore=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = connection.prepareStatement(sqlQuery);
				stm.setLong(1, idConnettore);
				stm.executeUpdate();
				stm.close();
				
				// Aggiungo attuali
				if(connettore.getCustom()!=null && connettore.getCustom()){					
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
					sqlQueryObject.addInsertTable(CostantiDB.CONNETTORI_CUSTOM);
					sqlQueryObject.addInsertField("name", "?");
					sqlQueryObject.addInsertField("value", "?");
					sqlQueryObject.addInsertField("id_connettore", "?");
					sqlQuery = sqlQueryObject.createSQLInsert();
					
					for (int i = 0; i < connettore.sizePropertyList(); i++) {
						nomeProperty = connettore.getProperty(i).getNome();
						if(propertiesGestiteAttraversoColonneAdHoc.contains(nomeProperty)){
							continue;
						}
						valoreProperty = connettore.getProperty(i).getValore();
						if (valoreProperty != null && valoreProperty.equals(""))
							valoreProperty = null;
					
						if(valoreProperty==null){
							throw new Exception("Property ["+nomeProperty+"] without value");
						}
						
						stm = connection.prepareStatement(sqlQuery);
						stm.setString(1, nomeProperty);
						stm.setString(2, valoreProperty);
						stm.setLong(3, idConnettore);
						stm.executeUpdate();
						stm.close();
					}				
				}
				else if(extendedProperties.size()>0){
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
					sqlQueryObject.addInsertTable(CostantiDB.CONNETTORI_CUSTOM);
					sqlQueryObject.addInsertField("name", "?");
					sqlQueryObject.addInsertField("value", "?");
					sqlQueryObject.addInsertField("id_connettore", "?");
					sqlQuery = sqlQueryObject.createSQLInsert();
					
					Enumeration<String> keys = extendedProperties.keys();
					while (keys.hasMoreElements()) {
						nomeProperty = (String) keys.nextElement();
						valoreProperty = extendedProperties.get(nomeProperty);
						if (valoreProperty != null && valoreProperty.equals(""))
							valoreProperty = null;
					
						if(valoreProperty==null){
							throw new Exception("Property ["+nomeProperty+"] without value");
						}
						
						stm = connection.prepareStatement(sqlQuery);
						stm.setString(1, nomeProperty);
						stm.setString(2, valoreProperty);
						stm.setLong(3, idConnettore);
						stm.executeUpdate();
						stm.close();
					}			
				}
				
				break;

			case 3:
				// delete
				idConnettore = connettore.getId();

				if (idConnettore <= 0)
					throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDConnettore] L'id del connettore non puo essere 0 tentando di fare una operazione di delete.");

				// Delete eventuali vecchie properties
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.CONNETTORI_CUSTOM);
				sqlQueryObject.addWhereCondition("id_connettore=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = connection.prepareStatement(sqlQuery);
				stm.setLong(1, idConnettore);
				stm.executeUpdate();
				stm.close();
				
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.CONNETTORI);
				sqlQueryObject.addWhereCondition("id=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = connection.prepareStatement(sqlQuery);
				stm.setLong(1, idConnettore);
				DriverRegistroServiziDB_LIB.log.debug("CRUDConnettore DELETE : \n" + DriverRegistroServiziDB_LIB.formatSQLString(sqlQuery, idConnettore));
				n = stm.executeUpdate();
				DriverRegistroServiziDB_LIB.log.debug("CRUDConnettore type = " + type + " row affected =" + n);
				stm.close();
				
				break;
			}

			// ritorno l id del connettore questo e' utile in caso di create
			connettore.setId(idConnettore);
			return idConnettore;

		} catch (SQLException se) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDConnettore] SQLException : " + se.getMessage(),se);
		} catch (Exception se) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDConnettore] Exception : " + se.getMessage(),se);
		}finally {
			try {
				stm.close();
			} catch (Exception e) {
			}
		}
	}
	
	/**
	 * CRUD oggetto Soggetto Non si occupa di chiudere la connessione con
	 * il db in caso di errore in quanto verra' gestita dal metodo chiamante
	 * 
	 * @param type
	 *            Tipo operazione {1 (CREATE),2 (UPDATE),3 (DELETE)}
	 * @param soggetto
	 * @throws DriverRegistroServiziException
	 */
	public static long CRUDSoggetto(int type, org.openspcoop2.core.registry.Soggetto soggetto, 
			Connection con, String tipoDatabase) throws DriverRegistroServiziException {
		if (soggetto == null)
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDSoggetto] Parametro non valido.");

		String nome = soggetto.getNome();
		String tipo = soggetto.getTipo();

		if (nome == null || nome.equals(""))
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDSoggetto] Parametro Nome non valido.");
		if (tipo == null || tipo.equals(""))
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDSoggetto] Parametro Tipo non valido.");

		String descizione = soggetto.getDescrizione();
		String identificativoPorta = soggetto.getIdentificativoPorta();
		String server = soggetto.getPortaDominio();
		Connettore connettore = soggetto.getConnettore();
		String codiceIPA = soggetto.getCodiceIpa();

		if (connettore == null && type != CostantiDB.CREATE && type!=CostantiDB.DELETE)
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDSoggetto] Il connettore del soggetto e' null.");

		PreparedStatement updateStmt = null;
		String updateQuery = "";
		PreparedStatement selectStmt = null;
		String selectQuery = "";
		ResultSet selectRS = null;
		long idSoggetto = 0;
		int n = 0;

		try {
			long idConnettore;

			// preparo lo statement in base al tipo di operazione
			switch (type) {
			case CREATE:
				// CREATE

				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
				sqlQueryObject.addInsertTable(CostantiDB.SOGGETTI);
				sqlQueryObject.addInsertField("nome_soggetto", "?");
				sqlQueryObject.addInsertField("descrizione", "?");
				sqlQueryObject.addInsertField("identificativo_porta", "?");
				sqlQueryObject.addInsertField("tipo_soggetto", "?");
				sqlQueryObject.addInsertField("id_connettore", "?");
				sqlQueryObject.addInsertField("server", "?");
				sqlQueryObject.addInsertField("superuser", "?");
				sqlQueryObject.addInsertField("privato", "?");
				sqlQueryObject.addInsertField("profilo", "?");
				sqlQueryObject.addInsertField("codice_ipa", "?");
				sqlQueryObject.addInsertField("tipoauth", "?");
				sqlQueryObject.addInsertField("utente", "?");
				sqlQueryObject.addInsertField("password", "?");
				sqlQueryObject.addInsertField("subject", "?");
				if(soggetto.getOraRegistrazione()!=null)
					sqlQueryObject.addInsertField("ora_registrazione", "?");
				updateQuery = sqlQueryObject.createSQLInsert();
				updateStmt = con.prepareStatement(updateQuery);

				// Controllo se il connettore non e' presente ne creo uno
				// disabilitato
				// in questo caso setto il nome del connettore
				if (connettore == null) {
					connettore = new Connettore();
					connettore.setNome("CNT_" + tipo + "_" + nome);
				}

				if (connettore.getNome() == null || connettore.getNome().equals(""))
					connettore.setNome("CNT_" + tipo + "_" + nome);

				idConnettore = DriverRegistroServiziDB_LIB.CRUDConnettore(CostantiDB.CREATE, connettore, con);

				int index = 1;
				
				updateStmt.setString(index++, nome);
				updateStmt.setString(index++, descizione);
				updateStmt.setString(index++, identificativoPorta);
				updateStmt.setString(index++, tipo);
				updateStmt.setLong(index++, idConnettore);
				updateStmt.setString(index++, server);
				updateStmt.setString(index++, soggetto.getSuperUser());
				if(soggetto.getPrivato()!=null && soggetto.getPrivato())
					updateStmt.setInt(index++, 1);
				else
					updateStmt.setInt(index++, 0);
				updateStmt.setString(index++, soggetto.getVersioneProtocollo());
				updateStmt.setString(index++, codiceIPA);
				
				CredenzialiSoggetto credenziali = soggetto.getCredenziali();
				updateStmt.setString(index++, (credenziali != null ? DriverRegistroServiziDB_LIB.getValue(credenziali.getTipo()) : null));
				updateStmt.setString(index++, (credenziali != null ? credenziali.getUser() : null));
				updateStmt.setString(index++, (credenziali != null ? credenziali.getPassword() : null));
				String subject = null;
				if(credenziali!=null && credenziali.getSubject()!=null && !"".equals(credenziali.getSubject()))
					subject = credenziali.getSubject();
				updateStmt.setString(index++, (subject != null ? Utilities.formatSubject(subject) : null));
				
				if(soggetto.getOraRegistrazione()!=null){
					updateStmt.setTimestamp(index++, new Timestamp(soggetto.getOraRegistrazione().getTime()));
				}

				// eseguo lo statement
				n = updateStmt.executeUpdate();
				updateStmt.close();
				DriverRegistroServiziDB_LIB.log.debug("CRUDSoggetto type = " + type + " row affected =" + n);
				DriverRegistroServiziDB_LIB.log.debug("CRUDSoggetto CREATE : \n" + DriverRegistroServiziDB_LIB.formatSQLString(updateQuery, nome, descizione, identificativoPorta, tipo, idConnettore, server));

				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
				sqlQueryObject.addSelectField("id");
				sqlQueryObject.addWhereCondition("tipo_soggetto = ?");
				sqlQueryObject.addWhereCondition("nome_soggetto = ?");
				sqlQueryObject.setANDLogicOperator(true);
				selectQuery = sqlQueryObject.createSQLQuery();
				selectStmt = con.prepareStatement(selectQuery);
				selectStmt.setString(1, tipo);
				selectStmt.setString(2, nome);
				selectRS = selectStmt.executeQuery();
				if (selectRS.next())
					idSoggetto = selectRS.getLong("id");

				soggetto.setId(idSoggetto);

				selectRS.close();
				selectStmt.close();
				
				
				// ProtocolProperties
				DriverRegistroServiziDB_LIB.CRUDProtocolProperty(CostantiDB.CREATE, soggetto.getProtocolPropertyList(), 
						idSoggetto, ProprietariProtocolProperty.SOGGETTO, con, tipoDatabase);
				
				

				
				if(soggetto.getRuoli()!=null && soggetto.getRuoli().sizeRuoloList()>0){
					for (int i = 0; i < soggetto.getRuoli().sizeRuoloList(); i++) {
						RuoloSoggetto ruoloSoggetto = soggetto.getRuoli().getRuolo(i);
						
						IDRuolo idRuoloObject= new IDRuolo(ruoloSoggetto.getNome());
						long idRuolo = DBUtils.getIdRuolo(idRuoloObject, con, DriverRegistroServiziDB_LIB.tipoDB);
						
						sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
						sqlQueryObject.addInsertTable(CostantiDB.SOGGETTI_RUOLI);
						sqlQueryObject.addInsertField("id_soggetto", "?");
						sqlQueryObject.addInsertField("id_ruolo", "?");
						updateQuery = sqlQueryObject.createSQLInsert();
						updateStmt = con.prepareStatement(updateQuery);
						
						updateStmt.setLong(1, idSoggetto);
						updateStmt.setLong(2, idRuolo);
						
						n = updateStmt.executeUpdate();
						updateStmt.close();
						DriverRegistroServiziDB_LIB.log.debug("CRUDSoggetto type = " + type + " row affected =" + n+" create role ["+ruoloSoggetto.getNome()+"]");
					}
				}
				
				break;

			case UPDATE:
				// UPDATE
				String oldNomeSoggetto = null;
				String oldTipoSoggetto = null;
				if(soggetto.getOldIDSoggettoForUpdate()!=null){
					oldNomeSoggetto = soggetto.getOldIDSoggettoForUpdate().getNome();
					oldTipoSoggetto = soggetto.getOldIDSoggettoForUpdate().getTipo();
				}

				// se i valori old... non sono settati allora uso quelli normali
				if (oldNomeSoggetto == null || oldNomeSoggetto.equals(""))
					oldNomeSoggetto = nome;
				if (oldTipoSoggetto == null || oldTipoSoggetto.equals(""))
					oldTipoSoggetto = tipo;

				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
				sqlQueryObject.addUpdateTable(CostantiDB.SOGGETTI);
				sqlQueryObject.addUpdateField("nome_soggetto", "?");
				sqlQueryObject.addUpdateField("descrizione", "?");
				sqlQueryObject.addUpdateField("identificativo_porta", "?");
				sqlQueryObject.addUpdateField("tipo_soggetto", "?");
				sqlQueryObject.addUpdateField("server", "?");
				sqlQueryObject.addUpdateField("superuser", "?");
				sqlQueryObject.addUpdateField("privato", "?");
				sqlQueryObject.addUpdateField("profilo", "?");
				sqlQueryObject.addUpdateField("codice_ipa", "?");
				sqlQueryObject.addUpdateField("tipoauth", "?");
				sqlQueryObject.addUpdateField("utente", "?");
				sqlQueryObject.addUpdateField("password", "?");
				sqlQueryObject.addUpdateField("subject", "?");
				if(soggetto.getOraRegistrazione()!=null)
					sqlQueryObject.addUpdateField("ora_registrazione", "?");
				sqlQueryObject.addWhereCondition("id=?");
				updateQuery = sqlQueryObject.createSQLUpdate();
				updateStmt = con.prepareStatement(updateQuery);

				idConnettore = DriverRegistroServiziDB_LIB.getIdConnettoreSoggetto(oldNomeSoggetto, oldTipoSoggetto, con);
				connettore.setId(idConnettore);

				idSoggetto = DBUtils.getIdSoggetto(oldNomeSoggetto, oldTipoSoggetto, con, DriverRegistroServiziDB_LIB.tipoDB);
				if (idSoggetto <= 0)
					throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDSoggetto(UPDATE)] Id Soggetto non valido.");

				index = 1;
				
				updateStmt.setString(index++, nome);
				updateStmt.setString(index++, descizione);
				updateStmt.setString(index++, identificativoPorta);
				updateStmt.setString(index++, tipo);
				updateStmt.setString(index++, server);
				updateStmt.setString(index++, soggetto.getSuperUser());
				if(soggetto.getPrivato()!=null && soggetto.getPrivato())
					updateStmt.setInt(index++, 1);
				else
					updateStmt.setInt(index++, 0);
				updateStmt.setString(index++, soggetto.getVersioneProtocollo());
				updateStmt.setString(index++, codiceIPA);
				
				credenziali = soggetto.getCredenziali();
				updateStmt.setString(index++, (credenziali != null ? DriverRegistroServiziDB_LIB.getValue(credenziali.getTipo()) : null));
				updateStmt.setString(index++, (credenziali != null ? credenziali.getUser() : null));
				updateStmt.setString(index++, (credenziali != null ? credenziali.getPassword() : null));
				subject = null;
				if(credenziali!=null && credenziali.getSubject()!=null && !"".equals(credenziali.getSubject()))
					subject = credenziali.getSubject();
				updateStmt.setString(index++, (subject != null ? Utilities.formatSubject(subject) : null));
				
				if(soggetto.getOraRegistrazione()!=null){
					updateStmt.setTimestamp(index++, new Timestamp(soggetto.getOraRegistrazione().getTime()));
				}
				updateStmt.setLong(index++, idSoggetto);

				// eseguo lo statement
				n = updateStmt.executeUpdate();
				updateStmt.close();
				DriverRegistroServiziDB_LIB.log.debug("CRUDSoggetto type = " + type + " row affected =" + n);
				// modifico i dati del connettore
				//setto il nuovo nome
				String newNomeConnettore = "CNT_" + tipo + "_" + nome;
				connettore.setNome(newNomeConnettore);
				DriverRegistroServiziDB_LIB.CRUDConnettore(2, connettore, con);

				DriverRegistroServiziDB_LIB.log.debug("CRUDSoggetto UPDATE : \n" + DriverRegistroServiziDB_LIB.formatSQLString(updateQuery, nome, descizione, identificativoPorta, tipo, idSoggetto));

				
				
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.SOGGETTI_RUOLI);
				sqlQueryObject.addWhereCondition("id_soggetto=?");
				updateQuery = sqlQueryObject.createSQLDelete();
				updateStmt = con.prepareStatement(updateQuery);
				updateStmt.setLong(1, idSoggetto);
				n = updateStmt.executeUpdate();
				updateStmt.close();
				DriverRegistroServiziDB_LIB.log.debug("CRUDSoggetto type = " + type + " row affected =" + n+" delete roles");
				
				if(soggetto.getRuoli()!=null && soggetto.getRuoli().sizeRuoloList()>0){
					for (int i = 0; i < soggetto.getRuoli().sizeRuoloList(); i++) {
						RuoloSoggetto ruoloSoggetto = soggetto.getRuoli().getRuolo(i);
						
						IDRuolo idRuoloObject= new IDRuolo(ruoloSoggetto.getNome());
						long idRuolo = DBUtils.getIdRuolo(idRuoloObject, con, DriverRegistroServiziDB_LIB.tipoDB);
						
						sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
						sqlQueryObject.addInsertTable(CostantiDB.SOGGETTI_RUOLI);
						sqlQueryObject.addInsertField("id_soggetto", "?");
						sqlQueryObject.addInsertField("id_ruolo", "?");
						updateQuery = sqlQueryObject.createSQLInsert();
						updateStmt = con.prepareStatement(updateQuery);
						
						updateStmt.setLong(1, idSoggetto);
						updateStmt.setLong(2, idRuolo);
						
						n = updateStmt.executeUpdate();
						updateStmt.close();
						DriverRegistroServiziDB_LIB.log.debug("CRUDSoggetto type = " + type + " row affected =" + n+" create role ["+ruoloSoggetto.getNome()+"]");
					}
				}
				
				
				// ProtocolProperties
				DriverRegistroServiziDB_LIB.CRUDProtocolProperty(CostantiDB.UPDATE, soggetto.getProtocolPropertyList(), 
						idSoggetto, ProprietariProtocolProperty.SOGGETTO, con, tipoDatabase);
				
				
				break;

			case DELETE:
								
				// DELETE

				idSoggetto = DBUtils.getIdSoggetto(nome, tipo, con, DriverRegistroServiziDB_LIB.tipoDB);
				idConnettore = DriverRegistroServiziDB_LIB.getIdConnettoreSoggetto(nome, tipo, con);
				if (idSoggetto <= 0)
					throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDSoggetto(DELETE)] Id Soggetto non valido.");
				
				// ProtocolProperties
				DriverRegistroServiziDB_LIB.CRUDProtocolProperty(CostantiDB.DELETE, null, 
						idSoggetto, ProprietariProtocolProperty.SOGGETTO, con, tipoDatabase);
				
				// elimino il soggetto
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.SOGGETTI_RUOLI);
				sqlQueryObject.addWhereCondition("id_soggetto=?");
				updateQuery = sqlQueryObject.createSQLDelete();
				updateStmt = con.prepareStatement(updateQuery);
				updateStmt.setLong(1, idSoggetto);
				n = updateStmt.executeUpdate();
				updateStmt.close();
				DriverRegistroServiziDB_LIB.log.debug("CRUDSoggetto type = " + type + " row affected =" + n+" delete roles");
				
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.SOGGETTI);
				sqlQueryObject.addWhereCondition("id=?");
				String sqlQuery = sqlQueryObject.createSQLDelete();
				updateStmt = con.prepareStatement(sqlQuery);
				updateStmt.setLong(1, idSoggetto);
				n=updateStmt.executeUpdate();
				updateStmt.close();
				DriverRegistroServiziDB_LIB.log.debug("CRUDSoggetto type = " + type + " row affected =" + n);

				// elimino il connettore
				connettore=new Connettore();
				connettore.setId(idConnettore);
				DriverRegistroServiziDB_LIB.CRUDConnettore(3, connettore, con);

				DriverRegistroServiziDB_LIB.log.debug("CRUDSoggetto DELETE : \n" + DriverRegistroServiziDB_LIB.formatSQLString(updateQuery, idSoggetto));

				break;
			}

			if (type == CostantiDB.CREATE) {
				return idSoggetto;
			} else {
				return n;
			}

		} catch (CoreException e) {
			throw new DriverRegistroServiziException(e);
		} catch (SQLException se) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDSoggetto] SQLException [" + se.getMessage() + "].",se);
		}catch (Exception se) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDSoggetto] Exception [" + se.getMessage() + "].",se);
		} finally {

			try {
				if(selectRS!=null)selectRS.close();
				if(selectStmt!=null)selectStmt.close();
				if(updateStmt!=null)updateStmt.close();
			} catch (Exception e) {
				// ignore exception
			}
		}

	}

	/**
	 * CRUD oggetto AccordoServizioParteSpecifica Non si occupa di chiudere la connessione con
	 * il db in caso di errore in quanto verra' gestita dal metodo chiamante
	 * 
	 * @param type
	 *            Tipo operazione {1 (CREATE),2 (UPDATE),3 (DELETE)}
	 * @param asps
	 * @param con
	 * @throws DriverRegistroServiziException
	 */
	public static long CRUDAccordoServizioParteSpecifica(int type, org.openspcoop2.core.registry.AccordoServizioParteSpecifica asps, Connection con,
			String tipoDatabase) throws DriverRegistroServiziException {
		if (asps == null)
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDAccordoServizioParteSpecifica] asps non valido.");

		String nomeProprietario = asps.getNomeSoggettoErogatore();
		String tipoProprietario = asps.getTipoSoggettoErogatore();
		String nomeServizio = asps.getNome();
		String tipoServizio = asps.getTipo();
		Integer versioneServizio = asps.getVersione();
		String descrizione = asps.getDescrizione();
		String stato = asps.getStatoPackage();

		if (nomeProprietario == null || nomeProprietario.equals(""))
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDAccordoServizioParteSpecifica] Parametro Nome Proprietario non valido.");
		if (tipoProprietario == null || tipoProprietario.equals(""))
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDAccordoServizioParteSpecifica] Parametro Tipo Proprietario non valido.");
		if (nomeServizio == null || nomeServizio.equals(""))
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDAccordoServizioParteSpecifica] Parametro Nome Servizio non valido.");
		if (tipoServizio == null || tipoServizio.equals(""))
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDAccordoServizioParteSpecifica] Parametro Tipo Servizio non valido.");
		if (versioneServizio == null)
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDAccordoServizioParteSpecifica] Parametro Versione Servizio non valido.");

		// String accordoServizio=servizio.getAccordoServizio();

		// String confermaRicezione=servizio.getConfermaRicezione();
		Connettore connettore = null;
		if(asps.getConfigurazioneServizio()!=null){
			connettore = asps.getConfigurazioneServizio().getConnettore();
		}
		// String consegnaInOrdine=servizio.getConsegnaInOrdine();
		// String filtroDuplicati=servizio.getFiltroDuplicati();

		// String identificativoCollab=servizio.getIdCollaborazione();
		// String nomeServizio=servizio.getNome();
		// String scadenza=servizio.getScadenza();
		// String tipo=servizio.getTipo();

		String wsdlImplementativoErogatore = (asps.getByteWsdlImplementativoErogatore()!=null ? new String(asps.getByteWsdlImplementativoErogatore()) : null );
		String wsdlImplementativoFruitore =  (asps.getByteWsdlImplementativoFruitore()!=null ? new String(asps.getByteWsdlImplementativoFruitore()) : null );
		
		wsdlImplementativoErogatore = wsdlImplementativoErogatore!=null && !"".equals(wsdlImplementativoErogatore.trim().replaceAll("\n", "")) ? wsdlImplementativoErogatore : null;
		wsdlImplementativoFruitore = wsdlImplementativoFruitore!=null && !"".equals(wsdlImplementativoFruitore.trim().replaceAll("\n", "")) ? wsdlImplementativoFruitore : null;
				
		String superUser = asps.getSuperUser();
		StatoFunzionalita servizioCorrelato = (TipologiaServizio.CORRELATO.equals(asps.getTipologiaServizio()) ? CostantiRegistroServizi.ABILITATO : CostantiRegistroServizi.DISABILITATO);
		String port_type = (asps.getPortType()!=null ? asps.getPortType() : null );
		
		
		long idSoggetto = -1;

		// Recupero IDAccordo
		long idAccordoLong = -1;
		try {
			//L accordo mi serve solo in caso di create/update
			if(type!=CostantiDB.DELETE){
				String uriAccordo = asps.getAccordoServizioParteComune();
				if(uriAccordo==null || uriAccordo.equals("")) throw new DriverRegistroServiziException("L'uri dell'Accordo di Servizio non puo essere null.");
				IDAccordo idAccordo = IDAccordoFactory.getInstance().getIDAccordoFromUri(uriAccordo);
				idAccordoLong = DBUtils.getIdAccordoServizioParteComune(idAccordo, con, DriverRegistroServiziDB_LIB.tipoDB);
			}
		} catch (Exception e) {
			DriverRegistroServiziDB_LIB.log.error("Driver Error for get IDAccordo nome:["+asps.getAccordoServizioParteComune()+"].", e);
			throw new DriverRegistroServiziException(e);
		}

		// long idAccordo=-1;
		try {
			String nomeS = nomeProprietario;
			String tipoS = tipoProprietario;
			//if(servizio.getOldTipoSoggettoErogatoreForUpdate()!=null){
			//	tipoS = servizio.getOldTipoSoggettoErogatoreForUpdate();
			//}
			//if(servizio.getOldNomeSoggettoErogatoreForUpdate()!=null){
			//	nomeS = servizio.getOldNomeSoggettoErogatoreForUpdate();
			//}
			idSoggetto = DBUtils.getIdSoggetto(nomeS, tipoS, con, DriverRegistroServiziDB_LIB.tipoDB);
			// idAccordo = DBUtils.getIdAccordo(nomeServizio, tipoServizio,
			// nomeProprietario, tipoProprietario, con, tipoDB);
		} catch (CoreException e) {
			DriverRegistroServiziDB_LIB.log.error("Driver Error.", e);
			throw new DriverRegistroServiziException(e);
		}
		if (idSoggetto <= 0)
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDAccordoServizioParteSpecifica] Parametri non validi. Impossibile risalire all'id del soggettoo");		
		//l'id accordo mi serve solo in caso di create/update
		if(idAccordoLong <= 0 && type!=CostantiDB.DELETE)
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDAccordoServizioParteSpecifica] Parametri non validi. Impossibile risalire all'id dell'accordo");

		long idConnettore;
		if (connettore == null && type != CostantiDB.CREATE && type!=CostantiDB.DELETE)
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDAccordoServizioParteSpecifica] Il Connettore non puo essere nullo.");

		PreparedStatement updateStmt = null;
		ResultSet updateRS = null;
		String updateQuery = "";

		try {
			long idServizio = 0;
			int n = 0;
			int sizeFruitori=0;
			int sizeAzioni=0;
			Fruitore fruitore;
			ConfigurazioneServizioAzione azione = null;
			asps.setIdAccordo(idAccordoLong);
			switch (type) {
			case CREATE:
				// CREATE

				if (connettore == null) {
					connettore = new Connettore();
					connettore.setNome("CNT_" + tipoProprietario+"/"+nomeProprietario +"_"+ tipoServizio + "/" +nomeServizio+"/"+versioneServizio);
				}

				if (connettore.getNome() == null || connettore.getNome().equals("")) {
					// setto il nome del connettore
					connettore.setNome("CNT_" + tipoProprietario+"/"+nomeProprietario +"_"+ tipoServizio + "/" +nomeServizio+"/"+versioneServizio );
				}

				// creo il connettore del servizio
				idConnettore = DriverRegistroServiziDB_LIB.CRUDConnettore(1, connettore, con);

				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
				sqlQueryObject.addInsertTable(CostantiDB.SERVIZI);
				sqlQueryObject.addInsertField("nome_servizio", "?");
				sqlQueryObject.addInsertField("tipo_servizio", "?");
				sqlQueryObject.addInsertField("versione_servizio", "?");
				sqlQueryObject.addInsertField("id_soggetto", "?");
				sqlQueryObject.addInsertField("id_accordo", "?");
				sqlQueryObject.addInsertField("servizio_correlato", "?");
				sqlQueryObject.addInsertField("id_connettore", "?");
				sqlQueryObject.addInsertField("wsdl_implementativo_erogatore", "?");
				sqlQueryObject.addInsertField("wsdl_implementativo_fruitore", "?");
				sqlQueryObject.addInsertField("superuser", "?");
				sqlQueryObject.addInsertField("privato", "?");
				sqlQueryObject.addInsertField("port_type", "?");
				sqlQueryObject.addInsertField("profilo", "?");
				sqlQueryObject.addInsertField("descrizione", "?");
				if(stato!=null)
					sqlQueryObject.addInsertField("stato", "?");
				if(asps.getOraRegistrazione()!=null)
					sqlQueryObject.addInsertField("ora_registrazione", "?");
				sqlQueryObject.addInsertField("message_type", "?");
				
				updateQuery = sqlQueryObject.createSQLInsert();
				updateStmt = con.prepareStatement(updateQuery);

				int index = 1;
				updateStmt.setString(index++, nomeServizio);
				updateStmt.setString(index++, tipoServizio);
				updateStmt.setInt(index++, versioneServizio);
				updateStmt.setLong(index++, idSoggetto);
				updateStmt.setLong(index++, idAccordoLong);
				updateStmt.setString(index++, getValue(servizioCorrelato));
				updateStmt.setLong(index++, idConnettore);
				updateStmt.setString(index++, wsdlImplementativoErogatore);
				updateStmt.setString(index++, wsdlImplementativoFruitore);
				updateStmt.setString(index++, superUser);
				if(asps.getPrivato()!=null && asps.getPrivato())
					updateStmt.setInt(index++, 1);
				else
					updateStmt.setInt(index++, 0);
				updateStmt.setString(index++, port_type);
				updateStmt.setString(index++, asps.getVersioneProtocollo());
				updateStmt.setString(index++, descrizione);
								
				if(stato!=null){
					updateStmt.setString(index++, stato);
				}
				
				if(asps.getOraRegistrazione()!=null){
					updateStmt.setTimestamp(index++, new Timestamp(asps.getOraRegistrazione().getTime()));
				}
				
				updateStmt.setString(index++, DriverRegistroServiziDB_LIB.getValue(asps.getMessageType()));
				
				// eseguo lo statement
				n = updateStmt.executeUpdate();
				updateStmt.close();
				DriverRegistroServiziDB_LIB.log.debug("CRUDAccordoServizioParteSpecifica CREATE : \n" + 
						DriverRegistroServiziDB_LIB.formatSQLString(updateQuery, nomeServizio, tipoServizio, versioneServizio,
								idSoggetto, idAccordoLong, servizioCorrelato, idConnettore, wsdlImplementativoErogatore, wsdlImplementativoFruitore, superUser));
				DriverRegistroServiziDB_LIB.log.debug("CRUDAccordoServizioParteSpecifica type = " + type + " row affected =" + n);

				
				// recupero l'id del servizio inserito
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI);
				sqlQueryObject.addSelectField("*");
				sqlQueryObject.addWhereCondition("nome_servizio = ?");
				sqlQueryObject.addWhereCondition("tipo_servizio = ?");
				sqlQueryObject.addWhereCondition("id_soggetto = ?");
				sqlQueryObject.setANDLogicOperator(true);
				updateQuery = sqlQueryObject.createSQLQuery();
				updateStmt = con.prepareStatement(updateQuery);

				updateStmt.setString(1, nomeServizio);
				updateStmt.setString(2, tipoServizio);
				updateStmt.setLong(3, idSoggetto);

				DriverRegistroServiziDB_LIB.log.debug("CRUDAccordoServizioParteSpecifica recupero l'id del servizio appena creato : \n" + DriverRegistroServiziDB_LIB.formatSQLString(updateQuery, nomeServizio, tipoServizio, idSoggetto));
				updateRS = updateStmt.executeQuery();

				if (updateRS.next()) {
					idServizio = updateRS.getLong("id");
				}
				updateRS.close();
				updateStmt.close();

				if(idServizio<=0)
					throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDAccordoServizioParteSpecifica(addFruitore)] id servizio non recuperato");
				asps.setId(idServizio);
								
				
				// aggiungo fruitori
				sizeFruitori = asps.sizeFruitoreList();
				fruitore = null;
				for (int i = 0; i < sizeFruitori; i++) {
					fruitore = asps.getFruitore(i);
					DriverRegistroServiziDB_LIB.CRUDAccordoServizioParteSpecificaFruitore(1, fruitore, con, asps);
				}
				
				// aggiungo azioni
				if(asps.getConfigurazioneServizio()!=null){
					sizeAzioni = asps.getConfigurazioneServizio().sizeConfigurazioneAzioneList();
					azione = null;
					for (int i = 0; i < sizeAzioni; i++) {
						azione = asps.getConfigurazioneServizio().getConfigurazioneAzione(i);
						DriverRegistroServiziDB_LIB.log.debug("CRUD AZIONE -----------["+azione.getNome()+"]");
						DriverRegistroServiziDB_LIB.CRUDAccordoServizioParteSpecificaAzioni(1, azione, con, asps);
					}
				}
				
				// Documenti generici servizio
				List<Documento> documenti = new ArrayList<Documento>();
				// Allegati
				for(int i=0; i<asps.sizeAllegatoList(); i++){
					Documento doc = asps.getAllegato(i);
					doc.setRuolo(RuoliDocumento.allegato.toString());
					documenti.add(doc);
				}
				// Specifiche Semiformali
				for(int i=0; i<asps.sizeSpecificaSemiformaleList(); i++){
					Documento doc = asps.getSpecificaSemiformale(i);
					doc.setRuolo(RuoliDocumento.specificaSemiformale.toString());
					documenti.add(doc);
				}
				// Specifiche Livelli di Servizio
				for(int i=0; i<asps.sizeSpecificaLivelloServizioList(); i++){
					Documento doc = asps.getSpecificaLivelloServizio(i);
					doc.setRuolo(RuoliDocumento.specificaLivelloServizio.toString());
					documenti.add(doc);
				}
				// Specifiche Sicurezza
				for(int i=0; i<asps.sizeSpecificaSicurezzaList(); i++){
					Documento doc = asps.getSpecificaSicurezza(i);
					doc.setRuolo(RuoliDocumento.specificaSicurezza.toString());
					documenti.add(doc);
				}
				// CRUD
				DriverRegistroServiziDB_LIB.CRUDDocumento(CostantiDB.CREATE, documenti, idServizio, ProprietariDocumento.servizio, con, tipoDatabase);
				

				// ProtocolProperties
				DriverRegistroServiziDB_LIB.CRUDProtocolProperty(CostantiDB.CREATE, asps.getProtocolPropertyList(), 
						idServizio, ProprietariProtocolProperty.ACCORDO_SERVIZIO_PARTE_SPECIFICA, con, tipoDB);
				
				
				break;

			case UPDATE:
				// UPDATE
				String oldNomeSoggetto = null;
				String oldTipoSoggetto = null;
				String oldNomeServizio = null;
				String oldTipoServizio = null;
				Integer oldVersioneServizio = null;
				if(asps.getOldIDServizioForUpdate()!=null){
					if(asps.getOldIDServizioForUpdate().getSoggettoErogatore()!=null){
						oldNomeSoggetto = asps.getOldIDServizioForUpdate().getSoggettoErogatore().getNome();
						oldTipoSoggetto = asps.getOldIDServizioForUpdate().getSoggettoErogatore().getTipo();
					}
					oldNomeServizio = asps.getOldIDServizioForUpdate().getNome();
					oldTipoSoggetto = asps.getOldIDServizioForUpdate().getTipo();
					oldVersioneServizio = asps.getOldIDServizioForUpdate().getVersione();
				}

				if (oldNomeServizio == null || oldNomeServizio.equals(""))
					oldNomeServizio = nomeServizio;
				if (oldTipoServizio == null || oldTipoServizio.equals(""))
					oldTipoServizio = tipoServizio;
				if (oldNomeSoggetto == null || oldNomeSoggetto.equals(""))
					oldNomeSoggetto = nomeProprietario;
				if (oldTipoSoggetto == null || oldTipoSoggetto.equals(""))
					oldTipoSoggetto = tipoProprietario;
				if (oldVersioneServizio == null)
					oldVersioneServizio = versioneServizio;

				//recupero id servizio
				idServizio = DBUtils.getIdServizio(oldNomeServizio, oldTipoServizio, oldVersioneServizio, oldNomeSoggetto, oldTipoSoggetto, con, DriverRegistroServiziDB_LIB.tipoDB);
				if (idServizio <= 0){
					// Puo' darsi che l'old soggetto e il nuovo soggetto siano la stesso soggetto della tabella. E' stato cambiato il nome.
					idServizio = DBUtils.getIdServizio(oldNomeServizio, oldTipoServizio, oldVersioneServizio, nomeProprietario, tipoProprietario, con, DriverRegistroServiziDB_LIB.tipoDB);
				}
				if (idServizio <= 0)
					throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDAccordoServizioParteSpecifica(UPDATE)] Id Servizio non valido.");

				//recupero l'id del connettore
				idConnettore = DriverRegistroServiziDB_LIB.getIdConnettoreServizio(oldNomeServizio, oldTipoServizio, oldVersioneServizio, oldNomeSoggetto, oldTipoSoggetto, con);
				if (idConnettore <= 0){
					// Puo' darsi che l'old soggetto e il nuovo soggetto siano la stesso soggetto della tabella. E' stato cambiato il nome.
					idConnettore = DriverRegistroServiziDB_LIB.getIdConnettoreServizio(oldNomeServizio, oldTipoServizio, oldVersioneServizio, nomeProprietario, tipoProprietario, con);
				}
				if (idConnettore <= 0)
					throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDAccordoServizioParteSpecifica] id connettore nullo.");
				connettore.setId(idConnettore);

				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
				sqlQueryObject.addUpdateTable(CostantiDB.SERVIZI);
				sqlQueryObject.addUpdateField("nome_servizio", "?");
				sqlQueryObject.addUpdateField("tipo_servizio", "?");
				sqlQueryObject.addUpdateField("versione_servizio", "?");
				sqlQueryObject.addUpdateField("id_soggetto", "?");
				sqlQueryObject.addUpdateField("id_accordo", "?");
				sqlQueryObject.addUpdateField("servizio_correlato", "?");
				sqlQueryObject.addUpdateField("id_connettore", "?");
				sqlQueryObject.addUpdateField("wsdl_implementativo_erogatore", "?");
				sqlQueryObject.addUpdateField("wsdl_implementativo_fruitore", "?");
				sqlQueryObject.addUpdateField("superuser", "?");
				sqlQueryObject.addUpdateField("privato", "?");
				sqlQueryObject.addUpdateField("port_type", "?");
				sqlQueryObject.addUpdateField("profilo", "?");
				sqlQueryObject.addUpdateField("descrizione", "?");
				if(stato!=null)
					sqlQueryObject.addUpdateField("stato", "?");
				if(asps.getOraRegistrazione()!=null)
					sqlQueryObject.addUpdateField("ora_registrazione", "?");
				sqlQueryObject.addUpdateField("message_type", "?");
				sqlQueryObject.addWhereCondition("id=?");
				updateQuery = sqlQueryObject.createSQLUpdate();
				updateStmt = con.prepareStatement(updateQuery);

				index = 1;
				
				updateStmt.setString(index++, nomeServizio);
				updateStmt.setString(index++, tipoServizio);
				updateStmt.setInt(index++, versioneServizio);
				updateStmt.setLong(index++, idSoggetto);
				updateStmt.setLong(index++, idAccordoLong);
				updateStmt.setString(index++, getValue(servizioCorrelato));
				updateStmt.setLong(index++, idConnettore);
				updateStmt.setString(index++, wsdlImplementativoErogatore);
				updateStmt.setString(index++, wsdlImplementativoFruitore);
				updateStmt.setString(index++, superUser);
				if(asps.getPrivato()!=null && asps.getPrivato())
					updateStmt.setInt(index++, 1);
				else
					updateStmt.setInt(index++, 0);
				updateStmt.setString(index++, port_type);
				updateStmt.setString(index++, asps.getVersioneProtocollo());
				updateStmt.setString(index++, descrizione);
			
				if(stato!=null){
					updateStmt.setString(index++, stato);
				}
				
				if(asps.getOraRegistrazione()!=null){
					updateStmt.setTimestamp(index++, new Timestamp(asps.getOraRegistrazione().getTime()));
				}
				
				updateStmt.setString(index++, DriverRegistroServiziDB_LIB.getValue(asps.getMessageType()));
								
				updateStmt.setLong(index++, idServizio);


				// eseguo lo statement
				n = updateStmt.executeUpdate();
				updateStmt.close();
				DriverRegistroServiziDB_LIB.log.debug("CRUDAccordoServizioParteSpecifica type = " + type + " row affected =" + n);
				DriverRegistroServiziDB_LIB.log.debug("CRUDAccordoServizioParteSpecifica UPDATE : \n" + DriverRegistroServiziDB_LIB.formatSQLString(updateQuery, nomeServizio, tipoServizio, idSoggetto, idAccordoLong, servizioCorrelato, idConnettore, wsdlImplementativoErogatore, wsdlImplementativoFruitore, superUser, idServizio));

				
				// aggiorno nome connettore
				String newNomeConnettore = "CNT_" + tipoProprietario+"/"+nomeProprietario +"_"+ tipoServizio + "/" +nomeServizio+ "/"+versioneServizio;
				connettore.setNome(newNomeConnettore);
				DriverRegistroServiziDB_LIB.CRUDConnettore(2, connettore, con);

				
				//aggiorno fruitori
				//La lista dei fruitori del servizio contiene tutti e soli i fruitori di questo servizio
				//prima vengono cancellati i fruitori esistenti e poi vengono riaggiunti
				sizeFruitori = asps.sizeFruitoreList();
				fruitore = null;
				// NON POSSO: esistono i mapping
//				//cancellazione
//				DriverRegistroServiziDB_LIB.deleteAllFruitoriServizio(idServizio, con);
//				//creazione
//				for (int i = 0; i < sizeFruitori; i++) {
//					fruitore = asps.getFruitore(i);
//					DriverRegistroServiziDB_LIB.CRUDAccordoServizioParteSpecificaFruitore(1, fruitore, con, servizio);
//				}
				List<Long> idFruitoriEsistenti = new ArrayList<>();
				for (int i = 0; i < sizeFruitori; i++) {
					fruitore = asps.getFruitore(i);
					// i dati del servizio sono gia' stati modificati, devo usare i dati nuovi
					IDServizio idS = IDServizioFactory.getInstance().getIDServizioFromValues(tipoServizio, nomeServizio, 
											new IDSoggetto(tipoProprietario, nomeProprietario), versioneServizio);
					long idFruizione = DBUtils.getIdFruizioneServizio(idS, 
							new IDSoggetto(fruitore.getTipo(), fruitore.getNome()), con, tipoDatabase);
					int typeFruitore = 1; // create
					if(idFruizione>0){
						typeFruitore = 2; // update
					}
					DriverRegistroServiziDB_LIB.CRUDAccordoServizioParteSpecificaFruitore(typeFruitore, fruitore, con, asps);
					idFruitoriEsistenti.add(DBUtils.getIdSoggetto(fruitore.getNome(), fruitore.getTipo(), con, tipoDatabase));
				}
				DriverRegistroServiziDB_LIB.deleteAllFruitoriServizio(idServizio, idFruitoriEsistenti, con);
	

				//aggiorno azioni
				//La lista delle azioni del servizio contiene tutti e soli le azioni di questo servizio
				//prima vengono cancellati le azioni esistenti e poi vengono riaggiunte
				if(asps.getConfigurazioneServizio()!=null){
					sizeAzioni = asps.getConfigurazioneServizio().sizeConfigurazioneAzioneList();
					azione = null;
					//cancellazione
					DriverRegistroServiziDB_LIB.deleteAllAzioniServizio(idServizio, con);
					//creazione
					for (int i = 0; i < sizeAzioni; i++) {
						azione = asps.getConfigurazioneServizio().getConfigurazioneAzione(i);
						DriverRegistroServiziDB_LIB.log.debug("CRUD AZIONE -----------["+azione.getNome()+"]");
						DriverRegistroServiziDB_LIB.CRUDAccordoServizioParteSpecificaAzioni(1, azione, con, asps);
					}
				}
			

				
				// Documenti generici servizio
				documenti = new ArrayList<Documento>();
				// Allegati
				for(int i=0; i<asps.sizeAllegatoList(); i++){
					Documento doc = asps.getAllegato(i);
					doc.setRuolo(RuoliDocumento.allegato.toString());
					documenti.add(doc);
				}
				// Specifiche Semiformali
				for(int i=0; i<asps.sizeSpecificaSemiformaleList(); i++){
					Documento doc = asps.getSpecificaSemiformale(i);
					doc.setRuolo(RuoliDocumento.specificaSemiformale.toString());
					documenti.add(doc);
				}
				// Specifiche Livelli di Servizio
				for(int i=0; i<asps.sizeSpecificaLivelloServizioList(); i++){
					Documento doc = asps.getSpecificaLivelloServizio(i);
					doc.setRuolo(RuoliDocumento.specificaLivelloServizio.toString());
					documenti.add(doc);
				}
				// Specifiche Sicurezza
				for(int i=0; i<asps.sizeSpecificaSicurezzaList(); i++){
					Documento doc = asps.getSpecificaSicurezza(i);
					doc.setRuolo(RuoliDocumento.specificaSicurezza.toString());
					documenti.add(doc);
				}
				// CRUD
				DriverRegistroServiziDB_LIB.CRUDDocumento(CostantiDB.UPDATE, documenti, idServizio, ProprietariDocumento.servizio, con, tipoDatabase);
				
				
				// ProtocolProperties
				DriverRegistroServiziDB_LIB.CRUDProtocolProperty(CostantiDB.UPDATE, asps.getProtocolPropertyList(), 
						idServizio, ProprietariProtocolProperty.ACCORDO_SERVIZIO_PARTE_SPECIFICA, con, tipoDB);
				
				
				break;

			case DELETE:
				// DELETE
				// if(servizio.getId()<=0) throw new
				// DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDAccordoServizioParteSpecifica(DELETE)]
				// ID Servizio non valido.");
				// idServizio=servizio.getId();
				idServizio = DBUtils.getIdServizio(nomeServizio, tipoServizio, versioneServizio, nomeProprietario, tipoProprietario, con, DriverRegistroServiziDB_LIB.tipoDB);
				if (idServizio <= 0)
					throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDAccordoServizioParteSpecifica(DELETE)] Id Servizio non valido.");
				idConnettore = DriverRegistroServiziDB_LIB.getIdConnettoreServizio(nomeServizio, tipoServizio, versioneServizio, nomeProprietario, tipoProprietario, con);
				if (idConnettore <= 0)
					throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDAccordoServizioParteSpecifica(DELETE)] Id Connettore non valido.");

				//	elimino fruitori
				sizeFruitori = asps.sizeFruitoreList();
				fruitore = null;
				for (int i = 0; i < sizeFruitori; i++) {
					fruitore = asps.getFruitore(i);

					DriverRegistroServiziDB_LIB.CRUDAccordoServizioParteSpecificaFruitore(3, fruitore, con, asps);
				}
				
				// elimino azioni
				if(asps.getConfigurazioneServizio()!=null){
					sizeAzioni = asps.getConfigurazioneServizio().sizeConfigurazioneAzioneList();
					azione = null;
					for (int i = 0; i < sizeAzioni; i++) {
						azione = asps.getConfigurazioneServizio().getConfigurazioneAzione(i);
						DriverRegistroServiziDB_LIB.CRUDAccordoServizioParteSpecificaAzioni(3, azione, con, asps);
					}
				}

				
				// Documenti generici accordo di servizio
				// Allegati
				// Specifiche Semiformali
				// Specifiche Livelli di Servizio
				// Specifiche Sicurezza
				DriverRegistroServiziDB_LIB.CRUDDocumento(CostantiDB.DELETE, null, idServizio, ProprietariDocumento.servizio, con, tipoDatabase);
				
				
				// ProtocolProperties
				DriverRegistroServiziDB_LIB.CRUDProtocolProperty(CostantiDB.DELETE, null, 
						idServizio, ProprietariProtocolProperty.ACCORDO_SERVIZIO_PARTE_SPECIFICA, con, tipoDB);
				
				
				
				// Delete servizio
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.SERVIZI);
				sqlQueryObject.addWhereCondition("id=?");
				String sqlQuery = sqlQueryObject.createSQLDelete();
				updateStmt = con.prepareStatement(sqlQuery);
				updateStmt.setLong(1, idServizio);
				n=updateStmt.executeUpdate();
				updateStmt.close();
				DriverRegistroServiziDB_LIB.log.debug("CRUDAccordoServizioParteSpecifica type = " + type + " row affected =" + n);
				// elimino connettore
				connettore=new Connettore();
				connettore.setId(idConnettore);
				DriverRegistroServiziDB_LIB.CRUDConnettore(CostantiDB.DELETE, connettore, con);
				DriverRegistroServiziDB_LIB.log.debug("CRUDAccordoServizioParteSpecifica CREATE : \n" + DriverRegistroServiziDB_LIB.formatSQLString(updateQuery, idServizio));

				// nn cancello azioni nn interessa per adesso

				break;
			}

			return n;

		} catch (CoreException e) {
			throw new DriverRegistroServiziException(e);
		} catch (SQLException se) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDAccordoServizioParteSpecifica] SQLException [" + se.getMessage() + "].", se);

		} catch (Exception se) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDAccordoServizioParteSpecifica] Exception [" + se.getMessage() + "].", se);

		} finally {
			try {
				if(updateRS!=null) updateRS.close();
				if(updateStmt!=null)updateStmt.close();
			} catch (Exception ex) {/* ignore */}
		}

	}

	/**
	 * Aggiunge un fruitore di un servizio alla lista dei fruitori dei servizi
	 * 
	 * @param type
	 * @param fruitore
	 * @param con
	 */
	public static long CRUDAccordoServizioParteSpecificaFruitore(int type, Fruitore fruitore, Connection con, AccordoServizioParteSpecifica servizio) throws DriverRegistroServiziException {
		PreparedStatement updateStmt = null;
		String updateQuery;
		PreparedStatement selectStmt = null;
		String selectQuery = "";
		ResultSet selectRS = null;

		long idServizio = -1;
		try{
			String tipoServ = servizio.getTipo();
			String nomeServ = servizio.getNome();
			Integer verServ = servizio.getVersione();
			String tipoSogg = servizio.getTipoSoggettoErogatore();
			String nomeSogg = servizio.getNomeSoggettoErogatore();
			// A questo punto sono gia modificati.
			//if(servizio.getOldTipoForUpdate()!=null)
			//	tipoServ = servizio.getOldTipoForUpdate();
			//if(servizio.getOldNomeForUpdate()!=null)
			//	nomeServ = servizio.getOldNomeForUpdate();
			//if(servizio.getOldTipoSoggettoErogatoreForUpdate()!=null)
			//	tipoSogg = servizio.getOldTipoSoggettoErogatoreForUpdate();
			//if(servizio.getOldNomeSoggettoErogatoreForUpdate()!=null)
			//	nomeSogg = servizio.getOldNomeSoggettoErogatoreForUpdate();
			
			idServizio = DBUtils.getIdServizio(nomeServ, tipoServ, verServ, nomeSogg, tipoSogg, con, DriverRegistroServiziDB_LIB.tipoDB);
			//long idServizio = servizio.getId();
		} catch (CoreException e1) {
			DriverRegistroServiziDB_LIB.log.error("Driver Error.", e1);
			throw new DriverRegistroServiziException(e1);
		}

		if (idServizio <= 0)
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDAccordoServizioParteSpecificaFruitore] ID Servizio non valido.");

		Connettore connettore = fruitore.getConnettore();
		if (connettore == null && type != CostantiDB.CREATE && type!=CostantiDB.DELETE)
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDAccordoServizioParteSpecificaFruitore] il connettore non puo essere nullo.");

		String nomeSoggetto = fruitore.getNome();
		String tipoSoggetto = fruitore.getTipo();
		if (nomeSoggetto == null || nomeSoggetto.equals(""))
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDAccordoServizioParteSpecificaFruitore] Nome Fruitore non valido.");
		if (tipoSoggetto == null || tipoSoggetto.equals(""))
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDAccordoServizioParteSpecificaFruitore] Tipo Fruitore non valido.");

		String stato = fruitore.getStatoPackage();
		
		long idSoggettoFruitore = -1;
		try {
			idSoggettoFruitore = DBUtils.getIdSoggetto(nomeSoggetto, tipoSoggetto, con, DriverRegistroServiziDB_LIB.tipoDB);
		} catch (CoreException e1) {
			DriverRegistroServiziDB_LIB.log.error("Driver Error.", e1);
			throw new DriverRegistroServiziException(e1);
		}
		if (idSoggettoFruitore <= 0)
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDAccordoServizioParteSpecificaFruitore] Id Soggetto Fruitore non valido.");
		String wsdlImplementativoErogatore = (fruitore.getByteWsdlImplementativoErogatore()!=null ? new String(fruitore.getByteWsdlImplementativoErogatore()) : null );
		wsdlImplementativoErogatore = wsdlImplementativoErogatore!=null && !"".equals(wsdlImplementativoErogatore.trim().replaceAll("\n", "")) ? wsdlImplementativoErogatore : null;
		String wsdlImplementativoFruitore =  (fruitore.getByteWsdlImplementativoFruitore()!=null ? new String(fruitore.getByteWsdlImplementativoFruitore()) : null );
		wsdlImplementativoFruitore = wsdlImplementativoFruitore!=null && !"".equals(wsdlImplementativoFruitore.trim().replaceAll("\n", "")) ? wsdlImplementativoFruitore : null;
		StatoFunzionalita clientAuth = fruitore.getClientAuth();

		long idFruizione = 0;
		if (CostantiDB.CREATE != type) {
			idFruizione = DriverRegistroServiziDB_LIB.getIdFruizione(idServizio, nomeSoggetto, tipoSoggetto, con);
		}
		
		long idConnettore = 0;
		long n = 0;
		try {
			switch (type) {
			case 1:
				if (connettore == null) {
					connettore = new Connettore();
					//connettore.setNome("CNT_SF_" + tipoSoggetto+"/"+nomeSoggetto + "_" + servizio.getTipoSoggettoErogatore()+"/"+servizio.getNomeSoggettoErogatore() + "_" + servizio.getTipo() +"/"+servizio.getNome());
				}

				//if (connettore.getNome() == null || connettore.getNome().equals("")) {
				connettore.setNome("CNT_SF_" + tipoSoggetto+"/"+nomeSoggetto + "_" + servizio.getTipoSoggettoErogatore()+"/"+servizio.getNomeSoggettoErogatore() + "_" + servizio.getTipo() +"/"+servizio.getNome());
				//}
				
				DriverRegistroServiziDB_LIB.CRUDConnettore(CostantiDB.CREATE, connettore, con);
				idConnettore = connettore.getId();

				// create
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
				sqlQueryObject.addInsertTable(CostantiDB.SERVIZI_FRUITORI);
				sqlQueryObject.addInsertField("id_servizio", "?");
				sqlQueryObject.addInsertField("id_soggetto", "?");
				sqlQueryObject.addInsertField("id_connettore", "?");
				sqlQueryObject.addInsertField("wsdl_implementativo_erogatore", "?");
				sqlQueryObject.addInsertField("wsdl_implementativo_fruitore", "?");
				sqlQueryObject.addInsertField("profilo", "?");
				sqlQueryObject.addInsertField("client_auth", "?");
				if(stato!=null)
					sqlQueryObject.addInsertField("stato", "?");
				if(fruitore.getOraRegistrazione()!=null)
					sqlQueryObject.addInsertField("ora_registrazione", "?");
				updateQuery = sqlQueryObject.createSQLInsert();
				updateStmt = con.prepareStatement(updateQuery);

				updateStmt.setLong(1, idServizio);
				updateStmt.setLong(2, idSoggettoFruitore);
				updateStmt.setLong(3, idConnettore);
				updateStmt.setString(4, wsdlImplementativoErogatore);
				updateStmt.setString(5, wsdlImplementativoFruitore);
				updateStmt.setString(6, fruitore.getVersioneProtocollo());
				updateStmt.setString(7, getValue(clientAuth));
				
				int index = 8;
				
				if(stato!=null){
					updateStmt.setString(index, stato);
					index++;
				}
				if(fruitore.getOraRegistrazione()!=null){
					updateStmt.setTimestamp(index, new Timestamp(fruitore.getOraRegistrazione().getTime()));
					index++;
				}

				n = updateStmt.executeUpdate();
				DriverRegistroServiziDB_LIB.log.debug("CRUDAccordoServizioParteSpecificaFruitore CREATE : \n" + DriverRegistroServiziDB_LIB.formatSQLString(updateQuery, idServizio, idSoggettoFruitore, idConnettore, wsdlImplementativoErogatore, wsdlImplementativoFruitore));

				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI_FRUITORI);
				sqlQueryObject.addSelectField("id");
				sqlQueryObject.addWhereCondition("id_servizio = ?");
				sqlQueryObject.addWhereCondition("id_soggetto = ?");
				sqlQueryObject.addWhereCondition("id_connettore = ?");
				sqlQueryObject.setANDLogicOperator(true);
				selectQuery = sqlQueryObject.createSQLQuery();
				selectStmt = con.prepareStatement(selectQuery);
				selectStmt.setLong(1, idServizio);
				selectStmt.setLong(2, idSoggettoFruitore);
				selectStmt.setLong(3, idConnettore);

				selectRS = selectStmt.executeQuery();
				if (selectRS.next())
					idFruizione = selectRS.getLong("id");
				
				
				// ProtocolProperties
				DriverRegistroServiziDB_LIB.CRUDProtocolProperty(CostantiDB.UPDATE, fruitore.getProtocolPropertyList(), 
						idFruizione, ProprietariProtocolProperty.FRUITORE, con, tipoDB);
				
				break;

			case 2:
				// update
				idConnettore = DriverRegistroServiziDB_LIB.getIdConnettoreServizioFruitore(idServizio, nomeSoggetto, tipoSoggetto, con);
				if(idConnettore<0) throw new DriverRegistroServiziException("Il connettore del Fruitore del Servizio e' invalido id<0");
				connettore.setId(idConnettore);

				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
				sqlQueryObject.addUpdateTable(CostantiDB.SERVIZI_FRUITORI);
				sqlQueryObject.addUpdateField("wsdl_implementativo_erogatore", "?");
				sqlQueryObject.addUpdateField("wsdl_implementativo_fruitore", "?");
				sqlQueryObject.addUpdateField("profilo", "?");
				sqlQueryObject.addUpdateField("client_auth", "?");
				if(stato!=null)
					sqlQueryObject.addUpdateField("stato", "?");
				if(fruitore.getOraRegistrazione()!=null)
					sqlQueryObject.addUpdateField("ora_registrazione", "?");
				sqlQueryObject.addWhereCondition("id_servizio=?");
				sqlQueryObject.addWhereCondition("id_soggetto=?");
				sqlQueryObject.addWhereCondition("id_connettore=?");
				sqlQueryObject.setANDLogicOperator(true);
				updateQuery = sqlQueryObject.createSQLUpdate();
				updateStmt = con.prepareStatement(updateQuery);

				updateStmt.setString(1, wsdlImplementativoErogatore);
				updateStmt.setString(2, wsdlImplementativoFruitore);

				updateStmt.setString(3, fruitore.getVersioneProtocollo());

				updateStmt.setString(4, getValue(clientAuth));

				index = 5;
				
				if(stato!=null){
					updateStmt.setString(index, stato);
					index++;
				}
				
				if(fruitore.getOraRegistrazione()!=null){
					updateStmt.setTimestamp(index, new Timestamp(fruitore.getOraRegistrazione().getTime()));
					index++;
				}
					
				updateStmt.setLong(index,idServizio);
				index++;
				updateStmt.setLong(index,idSoggettoFruitore);
				index++;
				updateStmt.setLong(index,idConnettore);
				index++;
				
				n= updateStmt.executeUpdate();
				DriverRegistroServiziDB_LIB.log.debug("CRUDAccordoServizioParteSpecificaFruitore UPDATE : \n" + DriverRegistroServiziDB_LIB.formatSQLString(updateQuery, wsdlImplementativoErogatore, wsdlImplementativoFruitore, idServizio, idSoggettoFruitore, idConnettore));

				// modifico i dati del connettore
				//aggiorno nome
				DriverRegistroServiziDB_LIB.log.debug("Tento aggiornamento connettore id: ["+idConnettore+"] oldNome: ["+connettore.getNome()+"]...");
				String newNomeConnettore = "CNT_SF_" + tipoSoggetto+"/"+nomeSoggetto + "_" + servizio.getTipoSoggettoErogatore()+"/"+servizio.getNomeSoggettoErogatore() + "_" + servizio.getTipo() +"/"+servizio.getNome();
				connettore.setNome(newNomeConnettore);
				DriverRegistroServiziDB_LIB.log.debug("nuovo nome connettore ["+newNomeConnettore+"]");
				DriverRegistroServiziDB_LIB.CRUDConnettore(2, connettore, con);
				
				
				// ProtocolProperties
				DriverRegistroServiziDB_LIB.CRUDProtocolProperty(CostantiDB.UPDATE, fruitore.getProtocolPropertyList(), 
						idFruizione, ProprietariProtocolProperty.FRUITORE, con, tipoDB);
				
				break;

			case 3:
				
				// ProtocolProperties
				DriverRegistroServiziDB_LIB.CRUDProtocolProperty(CostantiDB.DELETE, null, 
						idFruizione, ProprietariProtocolProperty.FRUITORE, con, tipoDB);
				
				// delete
				idConnettore = DriverRegistroServiziDB_LIB.getIdConnettoreServizioFruitore(idServizio, nomeSoggetto, tipoSoggetto, con);
				connettore.setId(idConnettore);
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.SERVIZI_FRUITORI);
				sqlQueryObject.addWhereCondition("id_servizio=?");
				sqlQueryObject.addWhereCondition("id_soggetto=?");
				sqlQueryObject.addWhereCondition("id_connettore=?");
				sqlQueryObject.setANDLogicOperator(true);
				String sqlQuery = sqlQueryObject.createSQLDelete();
				updateStmt = con.prepareStatement(sqlQuery);
				updateStmt.setLong(1, idServizio);
				updateStmt.setLong(2, idSoggettoFruitore);
				updateStmt.setLong(3, idConnettore);
				n=updateStmt.executeUpdate();
				updateStmt.close();
				DriverRegistroServiziDB_LIB.log.debug("CRUDAccordoServizioParteSpecificaFruitore DELETE : \n" + DriverRegistroServiziDB_LIB.formatSQLString(sqlQuery, idServizio, idSoggettoFruitore, idConnettore));

				// elimino il connettore
				connettore=new Connettore();
				connettore.setId(idConnettore);
				DriverRegistroServiziDB_LIB.CRUDConnettore(3, connettore, con);

				break;
			}

			DriverRegistroServiziDB_LIB.log.debug("CRUDAccordoServizioParteSpecificaFruitore type = " + type + " row affected =" + n);

			if (CostantiDB.CREATE == type) {
				return idFruizione;
			}

			return n;

		} catch (SQLException se) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDFruitore] SQLException : " + se.getMessage(),se);
		}  catch (Exception se) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDFruitore] Exception : " + se.getMessage(),se);
		}finally {
			try {

				if(selectRS!=null)selectRS.close();
				if(selectStmt!=null)selectStmt.close();
				if(updateStmt!=null)updateStmt.close();
			} catch (Exception e) {
			}
		}
	}
	
	
	/**
	 * Aggiunge un fruitore di un servizio alla lista dei fruitori dei servizi
	 * 
	 * @param type
	 * @param azione azione
	 * @param con
	 */
	public static long CRUDAccordoServizioParteSpecificaAzioni(int type, ConfigurazioneServizioAzione azione, Connection con, AccordoServizioParteSpecifica servizio) throws DriverRegistroServiziException {
		PreparedStatement updateStmt = null;
		String updateQuery;
		PreparedStatement selectStmt = null;
		String selectQuery = "";
		ResultSet selectRS = null;

		long idServizio = -1;
		try{
			String tipoServ = servizio.getTipo();
			String nomeServ = servizio.getNome();
			Integer verServ = servizio.getVersione();
			String tipoSogg = servizio.getTipoSoggettoErogatore();
			String nomeSogg = servizio.getNomeSoggettoErogatore();
			// A questo punto sono gia modificati.
			//if(servizio.getOldTipoForUpdate()!=null)
			//	tipoServ = servizio.getOldTipoForUpdate();
			//if(servizio.getOldNomeForUpdate()!=null)
			//	nomeServ = servizio.getOldNomeForUpdate();
			//if(servizio.getOldTipoSoggettoErogatoreForUpdate()!=null)
			//	tipoSogg = servizio.getOldTipoSoggettoErogatoreForUpdate();
			//if(servizio.getOldNomeSoggettoErogatoreForUpdate()!=null)
			//	nomeSogg = servizio.getOldNomeSoggettoErogatoreForUpdate();
			
			idServizio = DBUtils.getIdServizio(nomeServ, tipoServ, verServ, nomeSogg, tipoSogg, con, DriverRegistroServiziDB_LIB.tipoDB);
			//long idServizio = servizio.getId();
		} catch (CoreException e1) {
			DriverRegistroServiziDB_LIB.log.error("Driver Error.", e1);
			throw new DriverRegistroServiziException(e1);
		}

		if (idServizio <= 0)
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDAccordoServizioParteSpecificaAzioni] ID Servizio non valido.");

		Connettore connettore = azione.getConnettore();
		if (connettore == null && type != CostantiDB.CREATE && type!=CostantiDB.DELETE)
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDAccordoServizioParteSpecificaAzioni] il connettore non puo essere nullo.");

		String azioneValue = azione.getNome();
		if (azioneValue == null || azioneValue.equals(""))
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDAccordoServizioParteSpecificaAzioni] Azione non valida.");

		String nomeConnettore = "CNT_SAZIONE_" + servizio.getTipoSoggettoErogatore()+"/"+servizio.getNomeSoggettoErogatore() +"_"+ servizio.getTipo() +"/"+servizio.getNome()+"_"+azioneValue;
		if (connettore == null) {
			connettore = new Connettore();
		}
		connettore.setNome(nomeConnettore);
		
		long idConnettore = 0;
		long n = 0;
		try {
			switch (type) {
			case 1:
				
				DriverRegistroServiziDB_LIB.CRUDConnettore(CostantiDB.CREATE, connettore, con);
				idConnettore = connettore.getId();

				// create
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
				sqlQueryObject.addInsertTable(CostantiDB.SERVIZI_AZIONI);
				sqlQueryObject.addInsertField("id_servizio", "?");
				sqlQueryObject.addInsertField("nome_azione", "?");
				sqlQueryObject.addInsertField("id_connettore", "?");
				updateQuery = sqlQueryObject.createSQLInsert();
				updateStmt = con.prepareStatement(updateQuery);

				updateStmt.setLong(1, idServizio);
				updateStmt.setString(2, azioneValue);
				updateStmt.setLong(3, idConnettore);
				
				n = updateStmt.executeUpdate();
				DriverRegistroServiziDB_LIB.log.debug("CRUDAccordoServizioParteSpecificaAzioni CREATE : \n" + DriverRegistroServiziDB_LIB.formatSQLString(updateQuery, idServizio, azioneValue, idConnettore));

				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI_AZIONI);
				sqlQueryObject.addSelectField("id");
				sqlQueryObject.addWhereCondition("id_servizio = ?");
				sqlQueryObject.addWhereCondition("nome_azione = ?");
				sqlQueryObject.addWhereCondition("id_connettore = ?");
				sqlQueryObject.setANDLogicOperator(true);
				selectQuery = sqlQueryObject.createSQLQuery();
				selectStmt = con.prepareStatement(selectQuery);
				selectStmt.setLong(1, idServizio);
				selectStmt.setString(2, azioneValue);
				selectStmt.setLong(3, idConnettore);

				break;

			case 2:
				// update
				idConnettore = DriverRegistroServiziDB_LIB.getIdConnettoreServizioAzione(idServizio, azioneValue, con);
				if(idConnettore<0) throw new DriverRegistroServiziException("Il connettore dell'azione ["+azioneValue+"] del Servizio e' invalido id<0");
				connettore.setId(idConnettore);

				// modifico i dati del connettore
				//aggiorno nome
				DriverRegistroServiziDB_LIB.log.debug("Tento aggiornamento connettore id: ["+idConnettore+"] oldNome: ["+connettore.getNome()+"]...");
				DriverRegistroServiziDB_LIB.log.debug("nuovo nome connettore ["+connettore.getNome()+"]");
				DriverRegistroServiziDB_LIB.CRUDConnettore(2, connettore, con);

				break;

			case 3:
				// delete
				idConnettore = DriverRegistroServiziDB_LIB.getIdConnettoreServizioAzione(idServizio, azioneValue, con);
				connettore.setId(idConnettore);
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.SERVIZI_AZIONI);
				sqlQueryObject.addWhereCondition("id_servizio=?");
				sqlQueryObject.addWhereCondition("nome_azione=?");
				sqlQueryObject.addWhereCondition("id_connettore=?");
				sqlQueryObject.setANDLogicOperator(true);
				String sqlQuery = sqlQueryObject.createSQLDelete();
				updateStmt = con.prepareStatement(sqlQuery);
				updateStmt.setLong(1, idServizio);
				updateStmt.setString(2, azioneValue);
				updateStmt.setLong(3, idConnettore);
				n=updateStmt.executeUpdate();
				updateStmt.close();
				DriverRegistroServiziDB_LIB.log.debug("CRUDAccordoServizioParteSpecificaAzioni DELETE : \n" + DriverRegistroServiziDB_LIB.formatSQLString(sqlQuery, idServizio, azioneValue, idConnettore));

				// elimino il connettore
				connettore=new Connettore();
				connettore.setId(idConnettore);
				DriverRegistroServiziDB_LIB.CRUDConnettore(3, connettore, con);

				break;
			}

			DriverRegistroServiziDB_LIB.log.debug("CRUDAccordoServizioParteSpecificaAzioni type = " + type + " row affected =" + n);

			if (CostantiDB.CREATE == type) {
				selectRS = selectStmt.executeQuery();
				if (selectRS.next())
					return selectRS.getLong("id");
			}

			return n;

		} catch (SQLException se) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDAccordoServizioParteSpecificaAzioni] SQLException : " + se.getMessage(),se);
		}  catch (Exception se) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDAccordoServizioParteSpecificaAzioni] Exception : " + se.getMessage(),se);
		}finally {
			try {

				if(selectRS!=null)selectRS.close();
				if(selectStmt!=null)selectStmt.close();
				if(updateStmt!=null)updateStmt.close();
			} catch (Exception e) {
			}
		}
	}

	public static long CRUDAzione(int type, AccordoServizioParteComune as,Azione azione, Connection con, long idAccordo) throws DriverRegistroServiziException {
		PreparedStatement updateStmt = null;
		String updateQuery;
		PreparedStatement selectStmt = null;
		String selectQuery = "";
		ResultSet selectRS = null;
		long n = 0;
		if (idAccordo <= 0)
			new Exception("[DriverRegistroServiziDB_LIB::CRUDAzione] ID Accordo non valido.");

		try {
			switch (type) {
			case CREATE:
				// create
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
				sqlQueryObject.addInsertTable(CostantiDB.ACCORDI_AZIONI);
				sqlQueryObject.addInsertField("id_accordo", "?");
				sqlQueryObject.addInsertField("nome", "?");
				sqlQueryObject.addInsertField("profilo_azione", "?");
				sqlQueryObject.addInsertField("filtro_duplicati", "?");
				sqlQueryObject.addInsertField("conferma_ricezione", "?");
				sqlQueryObject.addInsertField("identificativo_collaborazione", "?");
				sqlQueryObject.addInsertField("consegna_in_ordine", "?");
				sqlQueryObject.addInsertField("scadenza", "?");
				sqlQueryObject.addInsertField("profilo_collaborazione", "?");
				sqlQueryObject.addInsertField("correlata", "?");
				updateQuery = sqlQueryObject.createSQLInsert();
				updateStmt = con.prepareStatement(updateQuery);
				updateStmt.setLong(1, idAccordo);
				updateStmt.setString(2, azione.getNome());
				updateStmt.setString(3, azione.getProfAzione());

				DriverRegistroServiziDB_LIB.log.debug("Aggiungo azione ["+azione.getNome()+"] con profilo ["+azione.getProfAzione()+"]");

				if(CostantiRegistroServizi.PROFILO_AZIONE_RIDEFINITO.equals(azione.getProfAzione())){
					DriverRegistroServiziDB_LIB.log.debug("ridefinizione...");
					updateStmt.setString(4, getValue(azione.getFiltroDuplicati()));
					updateStmt.setString(5, getValue(azione.getConfermaRicezione()));
					updateStmt.setString(6, getValue(azione.getIdCollaborazione()));
					updateStmt.setString(7, getValue(azione.getConsegnaInOrdine()));
					updateStmt.setString(8, azione.getScadenza());
					updateStmt.setString(9, getValue(azione.getProfiloCollaborazione()));
				}else{
					updateStmt.setString(4, getValue(as.getFiltroDuplicati()));
					updateStmt.setString(5, getValue(as.getConfermaRicezione()));
					updateStmt.setString(6, getValue(as.getIdCollaborazione()));
					updateStmt.setString(7, getValue(as.getConsegnaInOrdine()));
					updateStmt.setString(8, as.getScadenza());
					updateStmt.setString(9, getValue(as.getProfiloCollaborazione()));
				}
				updateStmt.setString(10, azione.getCorrelata());

				DriverRegistroServiziDB_LIB.log.debug("CRUDAzione CREATE :\n"+DriverRegistroServiziDB_LIB.formatSQLString(updateQuery,idAccordo,azione.getNome(),azione.getProfAzione()));
				n = updateStmt.executeUpdate();

				DriverRegistroServiziDB_LIB.log.debug("CRUDAzione type = " + type + " row affected =" + n);
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.ACCORDI_AZIONI);
				sqlQueryObject.addSelectField("id");
				sqlQueryObject.addWhereCondition("id_accordo = ?");
				sqlQueryObject.addWhereCondition("nome = ?");
				sqlQueryObject.setANDLogicOperator(true);
				selectQuery = sqlQueryObject.createSQLQuery();
				selectStmt = con.prepareStatement(selectQuery);
				selectStmt.setLong(1, idAccordo);
				selectStmt.setString(2, azione.getNome());

				break;

			case UPDATE:
				// update
				//
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
				sqlQueryObject.addUpdateTable(CostantiDB.ACCORDI_AZIONI);
				sqlQueryObject.addUpdateField("profilo_azione", "?");
				sqlQueryObject.addUpdateField("filtro_duplicati", "?");
				sqlQueryObject.addUpdateField("conferma_ricezione", "?");
				sqlQueryObject.addUpdateField("identificativo_collaborazione", "?");
				sqlQueryObject.addUpdateField("consegna_in_ordine", "?");
				sqlQueryObject.addUpdateField("scadenza", "?");
				sqlQueryObject.addUpdateField("profilo_collaborazione", "?");
				sqlQueryObject.addUpdateField("correlata", "?");
				sqlQueryObject.addWhereCondition("id_accordo=?");
				sqlQueryObject.addWhereCondition("nome=?");
				sqlQueryObject.setANDLogicOperator(true);
				updateQuery = sqlQueryObject.createSQLUpdate();
				updateStmt = con.prepareStatement(updateQuery);
				updateStmt.setString(1, azione.getProfAzione());


				if(CostantiRegistroServizi.PROFILO_AZIONE_RIDEFINITO.equals(azione.getProfAzione())){
					updateStmt.setString(2, getValue(azione.getFiltroDuplicati()));
					updateStmt.setString(3, getValue(azione.getConfermaRicezione()));
					updateStmt.setString(4, getValue(azione.getIdCollaborazione()));
					updateStmt.setString(5, getValue(azione.getConsegnaInOrdine()));
					updateStmt.setString(6, azione.getScadenza());
					updateStmt.setString(7, getValue(azione.getProfiloCollaborazione()));
				}else{
					updateStmt.setString(2, getValue(as.getFiltroDuplicati()));
					updateStmt.setString(3, getValue(as.getConfermaRicezione()));
					updateStmt.setString(4, getValue(as.getIdCollaborazione()));
					updateStmt.setString(5, getValue(as.getConsegnaInOrdine()));
					updateStmt.setString(6, as.getScadenza());
					updateStmt.setString(7, getValue(as.getProfiloCollaborazione()));
				}

				updateStmt.setString(8, azione.getCorrelata());

				updateStmt.setLong(9, idAccordo);
				updateStmt.setString(10, azione.getNome());
				n = updateStmt.executeUpdate();

				DriverRegistroServiziDB_LIB.log.debug("CRUDAzione type = " + type + " row affected =" + n);
				// log.debug("CRUDAzione UPDATE :
				// \n"+formatSQLString(updateQuery,wsdlImplementativoErogatore,wsdlImplementativoFruitore,
				// idServizio,idSoggettoFruitore,idConnettore));

				break;

			case DELETE:
				// delete

				// ProtocolProperties
				DriverRegistroServiziDB_LIB.CRUDProtocolProperty(CostantiDB.DELETE, null, 
						azione.getId(), ProprietariProtocolProperty.AZIONE_ACCORDO, con, tipoDB);
				
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.ACCORDI_AZIONI);
				sqlQueryObject.addWhereCondition("id_accordo=?");
				sqlQueryObject.addWhereCondition("nome=?");
				sqlQueryObject.setANDLogicOperator(true);
				String sqlQuery = sqlQueryObject.createSQLDelete();
				updateStmt = con.prepareStatement(sqlQuery);
				updateStmt.setLong(1, idAccordo);
				updateStmt.setString(2, azione.getNome());
				n=updateStmt.executeUpdate();
				updateStmt.close();
				DriverRegistroServiziDB_LIB.log.debug("CRUDAzione type = " + type + " row affected =" + n);
				// log.debug("CRUDAzione DELETE :
				// \n"+formatSQLString(updateQuery,idServizio,idSoggettoFruitore,idConnettore));

				break;
			}



			if (CostantiDB.CREATE == type) {
				selectRS = selectStmt.executeQuery();
				if (selectRS.next()) {

					azione.setId(selectRS.getLong("id"));
					
					// ProtocolProperties
					DriverRegistroServiziDB_LIB.CRUDProtocolProperty(type, azione.getProtocolPropertyList(), 
							azione.getId(), ProprietariProtocolProperty.AZIONE_ACCORDO, con, tipoDB);
					
					return azione.getId();

				}
			}

			return n;

		} catch (SQLException se) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDAzione] SQLException : " + se.getMessage(),se);
		} catch (Exception se) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDAzione] Exception : " + se.getMessage(),se);
		} finally {
			try {
				if(selectRS!=null)selectRS.close();
				if(selectStmt!=null)selectStmt.close();
				if(updateStmt!=null)updateStmt.close();
			} catch (Exception e) {
			}
		}
	}

	public static long CRUDPortType(int type, AccordoServizioParteComune as,PortType pt, Connection con, long idAccordo) throws DriverRegistroServiziException {
		PreparedStatement updateStmt = null;
		String updateQuery;
		PreparedStatement selectStmt = null;
		ResultSet selectRS = null;
		long n = 0;
		if (idAccordo <= 0)
			new Exception("[DriverRegistroServiziDB_LIB::CRUDPortType] ID Accordo non valido.");

		try {
			switch (type) {
			case CREATE:
				// create
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
				sqlQueryObject.addInsertTable(CostantiDB.PORT_TYPE);
				sqlQueryObject.addInsertField("id_accordo", "?");
				sqlQueryObject.addInsertField("nome", "?");
				sqlQueryObject.addInsertField("descrizione", "?");
				sqlQueryObject.addInsertField("message_type", "?");
				sqlQueryObject.addInsertField("profilo_pt", "?");
				sqlQueryObject.addInsertField("filtro_duplicati", "?");
				sqlQueryObject.addInsertField("conferma_ricezione", "?");
				sqlQueryObject.addInsertField("identificativo_collaborazione", "?");
				sqlQueryObject.addInsertField("consegna_in_ordine", "?");
				sqlQueryObject.addInsertField("scadenza", "?");
				sqlQueryObject.addInsertField("profilo_collaborazione", "?");
				sqlQueryObject.addInsertField("soap_style", "?");
				updateQuery = sqlQueryObject.createSQLInsert();
				updateStmt = con.prepareStatement(updateQuery);
				int index = 1;
				updateStmt.setLong(index++, idAccordo);
				updateStmt.setString(index++, pt.getNome());
				updateStmt.setString(index++, pt.getDescrizione());
				updateStmt.setString(index++, DriverRegistroServiziDB_LIB.getValue(pt.getMessageType()));
				updateStmt.setString(index++, pt.getProfiloPT());

				DriverRegistroServiziDB_LIB.log.debug("Aggiungo port-type ["+pt.getNome()+"] con profilo ["+pt.getProfiloPT()+"]");

				if(CostantiRegistroServizi.PROFILO_AZIONE_RIDEFINITO.equals(pt.getProfiloPT())){
					DriverRegistroServiziDB_LIB.log.debug("ridefinizione...");
					updateStmt.setString(index++, getValue(pt.getFiltroDuplicati()));
					updateStmt.setString(index++, getValue(pt.getConfermaRicezione()));
					updateStmt.setString(index++, getValue(pt.getIdCollaborazione()));
					updateStmt.setString(index++, getValue(pt.getConsegnaInOrdine()));
					updateStmt.setString(index++, pt.getScadenza());
					updateStmt.setString(index++, getValue(pt.getProfiloCollaborazione()));
				}else{
					if(pt.getFiltroDuplicati()!=null)
						updateStmt.setString(index++, getValue(pt.getFiltroDuplicati()));
					else
						updateStmt.setString(index++, getValue(as.getFiltroDuplicati()));
					if(pt.getConfermaRicezione()!=null)
						updateStmt.setString(index++, getValue(pt.getConfermaRicezione()));
					else
						updateStmt.setString(index++, getValue(as.getConfermaRicezione()));
					if(pt.getIdCollaborazione()!=null)
						updateStmt.setString(index++, getValue(pt.getIdCollaborazione()));
					else
						updateStmt.setString(index++, getValue(as.getIdCollaborazione()));
					if(pt.getConsegnaInOrdine()!=null)
						updateStmt.setString(index++, getValue(pt.getConsegnaInOrdine()));
					else
						updateStmt.setString(index++, getValue(as.getConsegnaInOrdine()));
					if(pt.getScadenza()!=null)
						updateStmt.setString(index++, pt.getScadenza());
					else
						updateStmt.setString(index++, as.getScadenza());
					if(pt.getProfiloCollaborazione()!=null)
						updateStmt.setString(index++, getValue(pt.getProfiloCollaborazione()));
					else
						updateStmt.setString(index++, getValue(as.getProfiloCollaborazione()));
				}
				updateStmt.setString(11,getValue(pt.getStyle()));
				// log.debug("CRUDAzione CREATE :
				// \n"+formatSQLString(updateQuery,idAccordo,idSoggettoFruitore,idConnettore,wsdlImplementativoErogatore,wsdlImplementativoFruitore));
				n = updateStmt.executeUpdate();

				DriverRegistroServiziDB_LIB.log.debug("CRUDPortType type = " + type + " row affected =" + n);

				break;

			case UPDATE:
				// update
				//
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
				sqlQueryObject.addUpdateTable(CostantiDB.PORT_TYPE);
				sqlQueryObject.addUpdateField("descrizione", "?");
				sqlQueryObject.addUpdateField("message_type", "?");
				sqlQueryObject.addUpdateField("profilo_pt", "?");
				sqlQueryObject.addUpdateField("filtro_duplicati", "?");
				sqlQueryObject.addUpdateField("conferma_ricezione", "?");
				sqlQueryObject.addUpdateField("identificativo_collaborazione", "?");
				sqlQueryObject.addUpdateField("consegna_in_ordine", "?");
				sqlQueryObject.addUpdateField("scadenza", "?");
				sqlQueryObject.addUpdateField("profilo_collaborazione", "?");
				sqlQueryObject.addUpdateField("soap_style", "?");
				sqlQueryObject.addWhereCondition("id_accordo=?");
				sqlQueryObject.addWhereCondition("nome=?");
				sqlQueryObject.setANDLogicOperator(true);
				updateQuery = sqlQueryObject.createSQLUpdate();
				updateStmt = con.prepareStatement(updateQuery);
				index = 1;
				updateStmt.setString(index++, pt.getDescrizione());
				updateStmt.setString(index++, DriverRegistroServiziDB_LIB.getValue(pt.getMessageType()));
				updateStmt.setString(index++, pt.getProfiloPT());


				if(CostantiRegistroServizi.PROFILO_AZIONE_RIDEFINITO.equals(pt.getProfiloPT())){
					updateStmt.setString(index++, getValue(pt.getFiltroDuplicati()));
					updateStmt.setString(index++, getValue(pt.getConfermaRicezione()));
					updateStmt.setString(index++, getValue(pt.getIdCollaborazione()));
					updateStmt.setString(index++, getValue(pt.getConsegnaInOrdine()));
					updateStmt.setString(index++, pt.getScadenza());
					updateStmt.setString(index++, getValue(pt.getProfiloCollaborazione()));
				}else{
					if(pt.getFiltroDuplicati()!=null)
						updateStmt.setString(index++, getValue(pt.getFiltroDuplicati()));
					else
						updateStmt.setString(index++, getValue(as.getFiltroDuplicati()));
					if(pt.getConfermaRicezione()!=null)
						updateStmt.setString(index++, getValue(pt.getConfermaRicezione()));
					else
						updateStmt.setString(index++, getValue(as.getConfermaRicezione()));
					if(pt.getIdCollaborazione()!=null)
						updateStmt.setString(index++, getValue(pt.getIdCollaborazione()));
					else
						updateStmt.setString(index++, getValue(as.getIdCollaborazione()));
					if(pt.getConsegnaInOrdine()!=null)
						updateStmt.setString(index++, getValue(pt.getConsegnaInOrdine()));
					else
						updateStmt.setString(index++, getValue(as.getConsegnaInOrdine()));
					if(pt.getScadenza()!=null)
						updateStmt.setString(index++, pt.getScadenza());
					else
						updateStmt.setString(index++, as.getScadenza());
					if(pt.getProfiloCollaborazione()!=null)
						updateStmt.setString(index++, getValue(pt.getProfiloCollaborazione()));
					else
						updateStmt.setString(index++, getValue(as.getProfiloCollaborazione()));
				}
				updateStmt.setString(index++,getValue(pt.getStyle()));
				updateStmt.setLong(index++, idAccordo);
				updateStmt.setString(index++, pt.getNome());
				n = updateStmt.executeUpdate();

				DriverRegistroServiziDB_LIB.log.debug("CRUDPortType type = " + type + " row affected =" + n);
				// log.debug("CRUDAzione UPDATE :
				// \n"+formatSQLString(updateQuery,wsdlImplementativoErogatore,wsdlImplementativoFruitore,
				// idServizio,idSoggettoFruitore,idConnettore));

				break;

			case DELETE:
				// delete

				Long idPT = 0l;
				if(pt.getId()==null || pt.getId()<=0){
					idPT = DBUtils.getIdPortType(idAccordo, pt.getNome(), con);
					if(idPT==null || idPT<=0)
						throw new Exception("ID del porttype["+pt.getNome()+"] idAccordo["+idAccordo+"] non trovato");
				}

				// Operations
				for(int i=0;i<pt.sizeAzioneList();i++){
					DriverRegistroServiziDB_LIB.CRUDAzionePortType(CostantiDB.DELETE, as, pt, pt.getAzione(i), con, idPT);
				}

				// ProtocolProperties
				DriverRegistroServiziDB_LIB.CRUDProtocolProperty(CostantiDB.DELETE, null, 
						idPT, ProprietariProtocolProperty.PORT_TYPE, con, tipoDB);
				
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORT_TYPE);
				sqlQueryObject.addWhereCondition("id_accordo=?");
				sqlQueryObject.addWhereCondition("nome=?");
				sqlQueryObject.setANDLogicOperator(true);
				updateQuery = sqlQueryObject.createSQLDelete();
				updateStmt = con.prepareStatement(updateQuery);

				updateStmt.setLong(1, idAccordo);
				updateStmt.setString(2, pt.getNome());
				n = updateStmt.executeUpdate();

				DriverRegistroServiziDB_LIB.log.debug("CRUDPortType type = " + type + " row affected =" + n);
				// log.debug("CRUDAzione DELETE :
				// \n"+formatSQLString(updateQuery,idServizio,idSoggettoFruitore,idConnettore));

				break;
			}



			if ( (CostantiDB.CREATE == type) || (CostantiDB.UPDATE == type)) {
				Long idPT = DBUtils.getIdPortType(idAccordo, pt.getNome(), con);
				if(idPT==null || idPT<=0)
					throw new Exception("ID del porttype["+pt.getNome()+"] idAccordo["+idAccordo+"] non trovato");

				DriverRegistroServiziDB_LIB.log.debug("ID port type: "+idPT);

				if ( CostantiDB.UPDATE == type ){
					n = 0;
					for(int i=0;i<pt.sizeAzioneList();i++){
						n += DriverRegistroServiziDB_LIB.CRUDAzionePortType(CostantiDB.DELETE, as, pt, pt.getAzione(i), con, idPT);
					}
					
//					ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
//					sqlQueryObject.addDeleteTable(CostantiDB.PORT_TYPE_AZIONI);
//					sqlQueryObject.addWhereCondition("id_port_type=?");
//					sqlQueryObject.setANDLogicOperator(true);
//					String sqlQuery = sqlQueryObject.createSQLDelete();
//					updateStmt=con.prepareStatement(sqlQuery);
//					updateStmt.setLong(1, idPT);
//					n=updateStmt.executeUpdate();
//					updateStmt.close();
					DriverRegistroServiziDB_LIB.log.info("Cancellate "+n+" azioni del port type ["+idPT+"] associate all'accordo "+idAccordo);
				}

				// Azioni PortType
				Operation azione = null;
				for (int i = 0; i < pt.sizeAzioneList(); i++) {
					azione = pt.getAzione(i);
					DriverRegistroServiziDB_LIB.CRUDAzionePortType(CostantiDB.CREATE,as,pt, azione, con, idPT);
				}
				DriverRegistroServiziDB_LIB.log.debug("inserite " + pt.sizeAzioneList() + " azioni relative al port type["+pt.getNome()+"] id-porttype["+pt.getId()+"] dell'accordo :" + IDAccordoFactory.getInstance().getUriFromAccordo(as) + " id-accordo :" + idAccordo);
				
				
				// ProtocolProperties
				DriverRegistroServiziDB_LIB.CRUDProtocolProperty(type, pt.getProtocolPropertyList(), 
						idPT, ProprietariProtocolProperty.PORT_TYPE, con, tipoDB);
			}


			return n;

		} catch (SQLException se) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDPortType] SQLException : " + se.getMessage(),se);
		} catch (Exception se) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDPortType] Exception : " + se.getMessage(),se);
		} finally {
			try {
				if(selectRS!=null)selectRS.close();
				if(selectStmt!=null)selectStmt.close();
				if(updateStmt!=null)updateStmt.close();
			} catch (Exception e) {
			}
		}
	}

	public static long CRUDAzionePortType(int type, AccordoServizioParteComune as,PortType pt,Operation azione, Connection con, long idPortType) throws DriverRegistroServiziException {
		PreparedStatement updateStmt = null;
		String updateQuery;
		PreparedStatement selectStmt = null;
		String selectQuery = "";
		ResultSet selectRS = null;
		long n = 0;
		if (idPortType <= 0)
			new Exception("[DriverRegistroServiziDB_LIB::CRUDAzionePortType] ID Port Type non valido.");

		try {
			switch (type) {
			case CREATE:
				// create
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
				sqlQueryObject.addInsertTable(CostantiDB.PORT_TYPE_AZIONI);
				sqlQueryObject.addInsertField("id_port_type", "?");
				sqlQueryObject.addInsertField("nome", "?");
				sqlQueryObject.addInsertField("profilo_pt_azione", "?");
				sqlQueryObject.addInsertField("filtro_duplicati", "?");
				sqlQueryObject.addInsertField("conferma_ricezione", "?");
				sqlQueryObject.addInsertField("identificativo_collaborazione", "?");
				sqlQueryObject.addInsertField("consegna_in_ordine", "?");
				sqlQueryObject.addInsertField("scadenza", "?");
				sqlQueryObject.addInsertField("profilo_collaborazione", "?");
				sqlQueryObject.addInsertField("soap_style", "?");
				sqlQueryObject.addInsertField("soap_action", "?");
				sqlQueryObject.addInsertField("soap_use_msg_input", "?");
				sqlQueryObject.addInsertField("soap_namespace_msg_input", "?");
				sqlQueryObject.addInsertField("soap_use_msg_output", "?");
				sqlQueryObject.addInsertField("soap_namespace_msg_output", "?");
				sqlQueryObject.addInsertField("correlata_servizio", "?");
				sqlQueryObject.addInsertField("correlata", "?");
				updateQuery = sqlQueryObject.createSQLInsert();
				updateStmt = con.prepareStatement(updateQuery);
				updateStmt.setLong(1, idPortType);
				updateStmt.setString(2, azione.getNome());
				updateStmt.setString(3, azione.getProfAzione());

				DriverRegistroServiziDB_LIB.log.debug("Aggiungo azione ["+azione.getNome()+"] pt ["+pt.getNome()+"] con profilo ["+azione.getProfAzione()+"]");

				if(CostantiRegistroServizi.PROFILO_AZIONE_RIDEFINITO.equals(azione.getProfAzione())){
					DriverRegistroServiziDB_LIB.log.debug("ridefinizione...");
					updateStmt.setString(4, getValue(azione.getFiltroDuplicati()));
					updateStmt.setString(5, getValue(azione.getConfermaRicezione()));
					updateStmt.setString(6, getValue(azione.getIdCollaborazione()));
					updateStmt.setString(7, getValue(azione.getConsegnaInOrdine()));
					updateStmt.setString(8, azione.getScadenza());
					updateStmt.setString(9, getValue(azione.getProfiloCollaborazione()));
				}else{
					if(azione.getFiltroDuplicati()!=null)
						updateStmt.setString(4, getValue(azione.getFiltroDuplicati()));
					else if(pt.getFiltroDuplicati()!=null)
						updateStmt.setString(4, getValue(pt.getFiltroDuplicati()));
					else
						updateStmt.setString(4, getValue(as.getFiltroDuplicati()));

					if(azione.getConfermaRicezione()!=null)
						updateStmt.setString(5, getValue(azione.getConfermaRicezione()));
					else if(pt.getConfermaRicezione()!=null)
						updateStmt.setString(5, getValue(pt.getConfermaRicezione()));
					else
						updateStmt.setString(5, getValue(as.getConfermaRicezione()));

					if(azione.getIdCollaborazione()!=null)
						updateStmt.setString(6, getValue(azione.getIdCollaborazione()));
					else if(pt.getIdCollaborazione()!=null)
						updateStmt.setString(6, getValue(pt.getIdCollaborazione()));
					else
						updateStmt.setString(6, getValue(as.getIdCollaborazione()));

					if(azione.getConsegnaInOrdine()!=null)
						updateStmt.setString(7, getValue(azione.getConsegnaInOrdine()));
					else if(pt.getConsegnaInOrdine()!=null)
						updateStmt.setString(7, getValue(pt.getConsegnaInOrdine()));
					else
						updateStmt.setString(7, getValue(as.getConsegnaInOrdine()));

					if(azione.getScadenza()!=null)
						updateStmt.setString(8, azione.getScadenza());
					else if(pt.getScadenza()!=null)
						updateStmt.setString(8, pt.getScadenza());
					else
						updateStmt.setString(8, as.getScadenza());

					if(azione.getProfiloCollaborazione()!=null)
						updateStmt.setString(9, getValue(azione.getProfiloCollaborazione()));
					else if(pt.getProfiloCollaborazione()!=null)
						updateStmt.setString(9, getValue(pt.getProfiloCollaborazione()));
					else
						updateStmt.setString(9, getValue(as.getProfiloCollaborazione()));
				}
				updateStmt.setString(10, getValue(azione.getStyle()));
				updateStmt.setString(11, azione.getSoapAction());
				if(azione.getMessageInput()!=null){
					updateStmt.setString(12, getValue(azione.getMessageInput().getUse()));
					updateStmt.setString(13, azione.getMessageInput().getSoapNamespace());
				}else{
					updateStmt.setString(12, null);
					updateStmt.setString(13, null);
				}
				if(azione.getMessageOutput()!=null){
					updateStmt.setString(14, getValue(azione.getMessageOutput().getUse()));
					updateStmt.setString(15, azione.getMessageOutput().getSoapNamespace());
				}else{
					updateStmt.setString(14, null);
					updateStmt.setString(15, null);
				}

				updateStmt.setString(16, azione.getCorrelataServizio());
				updateStmt.setString(17, azione.getCorrelata());

				DriverRegistroServiziDB_LIB.log.debug("CRUDPortTypeAzione CREATE :\n"+updateQuery);
				n = updateStmt.executeUpdate();

				DriverRegistroServiziDB_LIB.log.debug("CRUDAzionePortType type = " + type + " row affected =" + n);
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORT_TYPE_AZIONI);
				sqlQueryObject.addSelectField("id");
				sqlQueryObject.addWhereCondition("id_port_type = ?");
				sqlQueryObject.addWhereCondition("nome = ?");
				sqlQueryObject.setANDLogicOperator(true);
				selectQuery = sqlQueryObject.createSQLQuery();
				selectStmt = con.prepareStatement(selectQuery);
				selectStmt.setLong(1, idPortType);
				selectStmt.setString(2, azione.getNome());

				break;

			case UPDATE:
				// update
				//
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
				sqlQueryObject.addUpdateTable(CostantiDB.PORT_TYPE_AZIONI);
				sqlQueryObject.addUpdateField("profilo_azione", "?");
				sqlQueryObject.addUpdateField("filtro_duplicati", "?");
				sqlQueryObject.addUpdateField("conferma_ricezione", "?");
				sqlQueryObject.addUpdateField("identificativo_collaborazione", "?");
				sqlQueryObject.addUpdateField("consegna_in_ordine", "?");
				sqlQueryObject.addUpdateField("scadenza", "?");
				sqlQueryObject.addUpdateField("profilo_collaborazione", "?");
				sqlQueryObject.addUpdateField("soap_style", "?");
				sqlQueryObject.addUpdateField("soap_action", "?");
				sqlQueryObject.addUpdateField("soap_use_msg_input", "?");
				sqlQueryObject.addUpdateField("soap_namespace_msg_input", "?");
				sqlQueryObject.addUpdateField("soap_use_msg_output", "?");
				sqlQueryObject.addUpdateField("soap_namespace_msg_output", "?");
				sqlQueryObject.addUpdateField("correlata_servizio", "?");
				sqlQueryObject.addUpdateField("correlata", "?");
				sqlQueryObject.addWhereCondition("id_port_type=?");
				sqlQueryObject.addWhereCondition("nome=?");
				sqlQueryObject.setANDLogicOperator(true);
				updateQuery = sqlQueryObject.createSQLUpdate();
				updateStmt = con.prepareStatement(updateQuery);
				updateStmt.setString(1, azione.getProfAzione());


				if(CostantiRegistroServizi.PROFILO_AZIONE_RIDEFINITO.equals(azione.getProfAzione())){
					updateStmt.setString(2, getValue(azione.getFiltroDuplicati()));
					updateStmt.setString(3, getValue(azione.getConfermaRicezione()));
					updateStmt.setString(4, getValue(azione.getIdCollaborazione()));
					updateStmt.setString(5, getValue(azione.getConsegnaInOrdine()));
					updateStmt.setString(6, azione.getScadenza());
					updateStmt.setString(7, getValue(azione.getProfiloCollaborazione()));
				}else{
					if(azione.getFiltroDuplicati()!=null)
						updateStmt.setString(2, getValue(azione.getFiltroDuplicati()));
					else if(pt.getFiltroDuplicati()!=null)
						updateStmt.setString(2, getValue(pt.getFiltroDuplicati()));
					else
						updateStmt.setString(2, getValue(as.getFiltroDuplicati()));

					if(azione.getConfermaRicezione()!=null)
						updateStmt.setString(3, getValue(azione.getConfermaRicezione()));
					else if(pt.getConfermaRicezione()!=null)
						updateStmt.setString(3, getValue(pt.getConfermaRicezione()));
					else
						updateStmt.setString(3, getValue(as.getConfermaRicezione()));

					if(azione.getIdCollaborazione()!=null)
						updateStmt.setString(4, getValue(azione.getIdCollaborazione()));
					else if(pt.getIdCollaborazione()!=null)
						updateStmt.setString(4, getValue(pt.getIdCollaborazione()));
					else
						updateStmt.setString(4, getValue(as.getIdCollaborazione()));

					if(azione.getConsegnaInOrdine()!=null)
						updateStmt.setString(5, getValue(azione.getConsegnaInOrdine()));
					else if(pt.getConsegnaInOrdine()!=null)
						updateStmt.setString(5, getValue(pt.getConsegnaInOrdine()));
					else
						updateStmt.setString(5, getValue(as.getConsegnaInOrdine()));

					if(azione.getScadenza()!=null)
						updateStmt.setString(6, azione.getScadenza());
					else if(pt.getScadenza()!=null)
						updateStmt.setString(6, pt.getScadenza());
					else
						updateStmt.setString(6, as.getScadenza());

					if(azione.getProfiloCollaborazione()!=null)
						updateStmt.setString(7, getValue(azione.getProfiloCollaborazione()));
					else if(pt.getProfiloCollaborazione()!=null)
						updateStmt.setString(7, getValue(pt.getProfiloCollaborazione()));
					else
						updateStmt.setString(7, getValue(as.getProfiloCollaborazione()));
				}
				updateStmt.setString(8, getValue(azione.getStyle()));
				updateStmt.setString(9, azione.getSoapAction());
				if(azione.getMessageInput()!=null){
					updateStmt.setString(10, getValue(azione.getMessageInput().getUse()));
					updateStmt.setString(11, azione.getMessageInput().getSoapNamespace());
				}else{
					updateStmt.setString(10, null);
					updateStmt.setString(11, null);
				}
				if(azione.getMessageOutput()!=null){
					updateStmt.setString(12, getValue(azione.getMessageOutput().getUse()));
					updateStmt.setString(13, azione.getMessageOutput().getSoapNamespace());
				}else{
					updateStmt.setString(12, null);
					updateStmt.setString(13, null);
				}

				updateStmt.setString(14, azione.getCorrelataServizio());
				updateStmt.setString(15, azione.getCorrelata());

				updateStmt.setLong(16, idPortType);
				updateStmt.setString(17, azione.getNome());
				n = updateStmt.executeUpdate();

				DriverRegistroServiziDB_LIB.log.debug("CRUDAzionePortType type = " + type + " row affected =" + n);
				// log.debug("CRUDAzione UPDATE :
				// \n"+formatSQLString(updateQuery,wsdlImplementativoErogatore,wsdlImplementativoFruitore,
				// idServizio,idSoggettoFruitore,idConnettore));

				break;

			case DELETE:
				// delete

				// message-part
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORT_TYPE_AZIONI_OPERATION_MESSAGES);
				sqlQueryObject.addWhereCondition("id_port_type_azione=?");
				sqlQueryObject.setANDLogicOperator(true);
				updateQuery = sqlQueryObject.createSQLDelete();
				DriverRegistroServiziDB_LIB.log.debug("CRUDAzione DELETE MESSAGES:\n"+DriverRegistroServiziDB_LIB.formatSQLString(updateQuery,azione.getId()));
				updateStmt = con.prepareStatement(updateQuery);
				updateStmt.setLong(1, azione.getId());
				n = updateStmt.executeUpdate();
				updateStmt.close();

				// ProtocolProperties
				DriverRegistroServiziDB_LIB.CRUDProtocolProperty(CostantiDB.DELETE, null, 
						azione.getId(), ProprietariProtocolProperty.OPERATION, con, tipoDB);
				
				// azioni
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORT_TYPE_AZIONI);
				sqlQueryObject.addWhereCondition("id_port_type=?");
				sqlQueryObject.addWhereCondition("nome=?");
				sqlQueryObject.setANDLogicOperator(true);
				updateQuery = sqlQueryObject.createSQLDelete();
				DriverRegistroServiziDB_LIB.log.debug("CRUDAzione DELETE AZIONI:\n"+DriverRegistroServiziDB_LIB.formatSQLString(updateQuery,idPortType,azione.getNome()));
				updateStmt = con.prepareStatement(updateQuery);
				updateStmt.setLong(1, idPortType);
				updateStmt.setString(2, azione.getNome());
				n = updateStmt.executeUpdate();

				DriverRegistroServiziDB_LIB.log.debug("CRUDAzionePortType type = " + type + " row affected =" + n);

				break;
			}



			if (CostantiDB.CREATE == type) {
				selectRS = selectStmt.executeQuery();
				if (selectRS.next()) {

					azione.setId(selectRS.getLong("id"));
					azione.setIdPortType(idPortType);

					// creo message-part
					DriverRegistroServiziDB_LIB.CRUDMessageAzionePortType(CostantiDB.CREATE, azione, con);

					// ProtocolProperties
					DriverRegistroServiziDB_LIB.CRUDProtocolProperty(type, azione.getProtocolPropertyList(), 
							azione.getId(), ProprietariProtocolProperty.OPERATION, con, tipoDB);
					
					return azione.getId();

				}
			}

			return n;

		} catch (SQLException se) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDAzionePortType] SQLException : " + se.getMessage(),se);
		} catch (Exception se) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDAzionePortType] Exception : " + se.getMessage(),se);
		} finally {
			try {
				if(selectRS!=null)selectRS.close();
				if(selectStmt!=null)selectStmt.close();
				if(updateStmt!=null)updateStmt.close();
			} catch (Exception e) {
			}
		}
	}


	public static void CRUDMessageAzionePortType(int type, Operation azione, Connection con) throws DriverRegistroServiziException {
		PreparedStatement updateStmt = null;
		String updateQuery;
		PreparedStatement selectStmt = null;
		ResultSet selectRS = null;
		if (azione.getId() <= 0)
			new Exception("[DriverRegistroServiziDB_LIB::CRUDMessageAzionePortType] ID Operation Port Type non valida.");

		try {
			switch (type) {
			case CREATE:
				// create
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
				sqlQueryObject.addInsertTable(CostantiDB.PORT_TYPE_AZIONI_OPERATION_MESSAGES);
				sqlQueryObject.addInsertField("id_port_type_azione", "?");
				sqlQueryObject.addInsertField("input_message", "?");
				sqlQueryObject.addInsertField("name", "?");
				sqlQueryObject.addInsertField("element_name", "?");
				sqlQueryObject.addInsertField("element_namespace", "?");
				sqlQueryObject.addInsertField("type_name", "?");
				sqlQueryObject.addInsertField("type_namespace", "?");
				updateQuery = sqlQueryObject.createSQLInsert();


				// message input
				if(azione.getMessageInput()!=null){
					for(int i=0; i<azione.getMessageInput().sizePartList(); i++){
						updateStmt = con.prepareStatement(updateQuery);
						MessagePart part = azione.getMessageInput().getPart(i);

						updateStmt.setLong(1, azione.getId());
						updateStmt.setInt(2, 1);
						updateStmt.setString(3, part.getName());
						updateStmt.setString(4, part.getElementName());
						updateStmt.setString(5, part.getElementNamespace());
						updateStmt.setString(6, part.getTypeName());
						updateStmt.setString(7, part.getTypeNamespace());

						DriverRegistroServiziDB_LIB.log.debug("Aggiungo part element input  ["+part.getName()+"] per azione ["+azione.getNome()+"]");

						DriverRegistroServiziDB_LIB.log.debug("CRUDMessageAzionePortType CREATE :\n"+updateQuery);
						updateStmt.executeUpdate();
						updateStmt.close();
					}
				}

				// message output
				if(azione.getMessageOutput()!=null){
					for(int i=0; i<azione.getMessageOutput().sizePartList(); i++){
						updateStmt = con.prepareStatement(updateQuery);
						MessagePart part = azione.getMessageOutput().getPart(i);

						updateStmt.setLong(1, azione.getId());
						updateStmt.setInt(2, 0);
						updateStmt.setString(3, part.getName());
						updateStmt.setString(4, part.getElementName());
						updateStmt.setString(5, part.getElementNamespace());
						updateStmt.setString(6, part.getTypeName());
						updateStmt.setString(7, part.getTypeNamespace());

						DriverRegistroServiziDB_LIB.log.debug("Aggiungo part element output ["+part.getName()+"] per azione ["+azione.getNome()+"]");

						DriverRegistroServiziDB_LIB.log.debug("CRUDMessageAzionePortType CREATE :\n"+updateQuery);
						updateStmt.executeUpdate();
						updateStmt.close();
					}
				}

				break;

			case UPDATE:
				// update
				//
				throw new Exception("Non implementato");

			case DELETE:
				// delete

				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORT_TYPE_AZIONI_OPERATION_MESSAGES);
				sqlQueryObject.addWhereCondition("id_port_type_azione=?");
				sqlQueryObject.setANDLogicOperator(true);
				updateQuery = sqlQueryObject.createSQLDelete();
				updateStmt = con.prepareStatement(updateQuery);

				updateStmt.setLong(1, azione.getId());
				long n = updateStmt.executeUpdate();
				updateStmt.close();

				DriverRegistroServiziDB_LIB.log.debug("CRUDMessageAzionePortType type = " + type + " row affected =" + n);
				// log.debug("CRUDAzione DELETE :
				// \n"+formatSQLString(updateQuery,idServizio,idSoggettoFruitore,idConnettore));

				break;
			}


		} catch (SQLException se) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDMessageAzionePortType] SQLException : " + se.getMessage(),se);
		} catch (Exception se) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDMessageAzionePortType] Exception : " + se.getMessage(),se);
		} finally {
			try {
				if(selectRS!=null)selectRS.close();
				if(selectStmt!=null)selectStmt.close();
				if(updateStmt!=null)updateStmt.close();
			} catch (Exception e) {
			}
		}
	}

	
	public static long CRUDResource(int type, AccordoServizioParteComune as,Resource resource, Connection con, long idAccordo) throws DriverRegistroServiziException {
		PreparedStatement updateStmt = null;
		String updateQuery;
		PreparedStatement selectStmt = null;
		ResultSet selectRS = null;
		long n = 0;
		if (idAccordo <= 0)
			new Exception("[DriverRegistroServiziDB_LIB::CRUDResource] ID Accordo non valido.");

		try {
			switch (type) {
			case CREATE:
				// create
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
				sqlQueryObject.addInsertTable(CostantiDB.API_RESOURCES);
				sqlQueryObject.addInsertField("id_accordo", "?");
				sqlQueryObject.addInsertField("nome", "?");
				sqlQueryObject.addInsertField("descrizione", "?");
				sqlQueryObject.addInsertField("http_method", "?");
				sqlQueryObject.addInsertField("path", "?");
				sqlQueryObject.addInsertField("message_type", "?");
				sqlQueryObject.addInsertField("message_type_request", "?");
				sqlQueryObject.addInsertField("message_type_response", "?");
				updateQuery = sqlQueryObject.createSQLInsert();
				updateStmt = con.prepareStatement(updateQuery);
				int index = 1;
				updateStmt.setLong(index++, idAccordo);
				updateStmt.setString(index++, resource.getNome());
				updateStmt.setString(index++, resource.getDescrizione());
				updateStmt.setString(index++, DriverRegistroServiziDB_LIB.getValue(resource.getMethod()));
				if(resource.getPath()==null) {
					updateStmt.setString(index++, CostantiDB.API_RESOURCE_PATH_ALL_VALUE);
				}
				else {
					updateStmt.setString(index++, resource.getPath());
				}
				updateStmt.setString(index++, DriverRegistroServiziDB_LIB.getValue(resource.getMessageType()));
				updateStmt.setString(index++, DriverRegistroServiziDB_LIB.getValue(resource.getRequestMessageType()));
				updateStmt.setString(index++, DriverRegistroServiziDB_LIB.getValue(resource.getResponseMessageType()));
				
				DriverRegistroServiziDB_LIB.log.debug("Aggiungo resource ["+resource.getNome()+"]");

				// log.debug("CRUDAzione CREATE :
				// \n"+formatSQLString(updateQuery,idAccordo,idSoggettoFruitore,idConnettore,wsdlImplementativoErogatore,wsdlImplementativoFruitore));
				n = updateStmt.executeUpdate();

				DriverRegistroServiziDB_LIB.log.debug("CRUDResource type = " + type + " row affected =" + n);

				break;

			case UPDATE:
				// update
				//
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
				sqlQueryObject.addUpdateTable(CostantiDB.PORT_TYPE);
				sqlQueryObject.addUpdateField("descrizione", "?");
				sqlQueryObject.addUpdateField("http_method", "?");
				sqlQueryObject.addUpdateField("path", "?");
				sqlQueryObject.addUpdateField("message_type", "?");
				sqlQueryObject.addUpdateField("message_type_request", "?");
				sqlQueryObject.addUpdateField("message_type_response", "?");
				sqlQueryObject.addWhereCondition("id_accordo=?");
				sqlQueryObject.addWhereCondition("nome=?");
				sqlQueryObject.setANDLogicOperator(true);
				updateQuery = sqlQueryObject.createSQLUpdate();
				updateStmt = con.prepareStatement(updateQuery);
				index = 1;
				updateStmt.setString(index++, resource.getDescrizione());
				updateStmt.setString(index++, DriverRegistroServiziDB_LIB.getValue(resource.getMethod()));
				if(resource.getPath()==null) {
					updateStmt.setString(index++, CostantiDB.API_RESOURCE_PATH_ALL_VALUE);
				}
				else {
					updateStmt.setString(index++, resource.getPath());
				}
				updateStmt.setString(index++, DriverRegistroServiziDB_LIB.getValue(resource.getMessageType()));
				updateStmt.setString(index++, DriverRegistroServiziDB_LIB.getValue(resource.getRequestMessageType()));
				updateStmt.setString(index++, DriverRegistroServiziDB_LIB.getValue(resource.getResponseMessageType()));
				updateStmt.setLong(index++, idAccordo);
				updateStmt.setString(index++, resource.getNome());
				n = updateStmt.executeUpdate();

				DriverRegistroServiziDB_LIB.log.debug("CRUDResource type = " + type + " row affected =" + n);
				// log.debug("CRUDAzione UPDATE :
				// \n"+formatSQLString(updateQuery,wsdlImplementativoErogatore,wsdlImplementativoFruitore,
				// idServizio,idSoggettoFruitore,idConnettore));

				break;

			case DELETE:
				// delete

				Long idResource = 0l;
				if(resource.getId()==null || resource.getId()<=0){
					idResource = DBUtils.getIdResource(idAccordo, resource.getNome(), con);
					if(idResource==null || idResource<=0)
						throw new Exception("ID della risorsa ["+resource.getNome()+"] idAccordo["+idAccordo+"] non trovato");
				}
				
				// gestione request
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.API_RESOURCES_MEDIA);
				sqlQueryObject.addWhereCondition("id_resource_media=?");
				sqlQueryObject.setANDLogicOperator(true);
				updateQuery = sqlQueryObject.createSQLDelete();
				updateStmt=con.prepareStatement(updateQuery);
				updateStmt.setLong(1, idResource );
				updateStmt.executeUpdate();
				updateStmt.close();
				
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.API_RESOURCES_PARAMETER);
				sqlQueryObject.addWhereCondition("id_resource_parameter=?");
				sqlQueryObject.setANDLogicOperator(true);
				updateQuery = sqlQueryObject.createSQLDelete();
				updateStmt=con.prepareStatement(updateQuery);
				updateStmt.setLong(1, idResource);
				updateStmt.executeUpdate();
				updateStmt.close();
				
				// gestione response
				List<Long> idResourceResponse = new ArrayList<Long>();
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.API_RESOURCES_RESPONSE);
				sqlQueryObject.addSelectField("id");
				sqlQueryObject.addWhereCondition("id_resource=?");
				updateQuery = sqlQueryObject.createSQLQuery();
				updateStmt=con.prepareStatement(updateQuery);
				updateStmt.setLong(1, idResource);
				selectRS=updateStmt.executeQuery();
				while(selectRS.next()){
					idResourceResponse.add(selectRS.getLong("id"));
				}
				selectRS.close();
				updateStmt.close();
	
				while(idResourceResponse.size()>0){
					
					long idRR = idResourceResponse.remove(0);
					
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
					sqlQueryObject.addDeleteTable(CostantiDB.API_RESOURCES_MEDIA);
					sqlQueryObject.addWhereCondition("id_resource_response_media=?");
					sqlQueryObject.setANDLogicOperator(true);
					updateQuery = sqlQueryObject.createSQLDelete();
					updateStmt=con.prepareStatement(updateQuery);
					updateStmt.setLong(1, idRR );
					updateStmt.executeUpdate();
					updateStmt.close();
					
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
					sqlQueryObject.addDeleteTable(CostantiDB.API_RESOURCES_PARAMETER);
					sqlQueryObject.addWhereCondition("id_resource_response_par=?");
					sqlQueryObject.setANDLogicOperator(true);
					updateQuery = sqlQueryObject.createSQLDelete();
					updateStmt=con.prepareStatement(updateQuery);
					updateStmt.setLong(1, idRR);
					updateStmt.executeUpdate();
					updateStmt.close();
				}
				
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.API_RESOURCES_RESPONSE);
				sqlQueryObject.addWhereCondition("id_resource=?");
				sqlQueryObject.setANDLogicOperator(true);
				updateQuery = sqlQueryObject.createSQLDelete();
				updateStmt=con.prepareStatement(updateQuery);
				updateStmt.setLong(1, idResource);
				updateStmt.executeUpdate();
				updateStmt.close();
				
				// elimino risorsa
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.API_RESOURCES);
				sqlQueryObject.addWhereCondition("id=?");
				sqlQueryObject.setANDLogicOperator(true);
				updateQuery = sqlQueryObject.createSQLDelete();
				updateStmt=con.prepareStatement(updateQuery);
				updateStmt.setLong(1, idResource);
				n = updateStmt.executeUpdate();
				updateStmt.close();

				DriverRegistroServiziDB_LIB.log.debug("CRUDResource type = " + type + " row affected =" + n);
				// log.debug("CRUDAzione DELETE :
				// \n"+formatSQLString(updateQuery,idServizio,idSoggettoFruitore,idConnettore));

				break;
			}



			if ( (CostantiDB.CREATE == type) || (CostantiDB.UPDATE == type)) {
				Long idResource = DBUtils.getIdResource(idAccordo, resource.getNome(), con);
				if(idResource==null || idResource<=0)
					throw new Exception("ID della risorsa ["+resource.getNome()+"] idAccordo["+idAccordo+"] non trovato");

				DriverRegistroServiziDB_LIB.log.debug("ID risorsa: "+idResource);

				if ( CostantiDB.UPDATE == type ){
					
					if(resource.getRequest()!=null) {
						DriverRegistroServiziDB_LIB.CRUDResourceRequest(CostantiDB.DELETE, as, resource, resource.getRequest(), con, idResource);
						DriverRegistroServiziDB_LIB.log.info("Cancellato dettagli di richiesta della risorsa ["+idResource+"] associata all'accordo "+idAccordo);
					}
					
					n = 0;
					for(int i=0;i<resource.sizeResponseList();i++){
						DriverRegistroServiziDB_LIB.CRUDResourceResponse(CostantiDB.DELETE, as, resource, resource.getResponse(i), con, idResource);
					}
					DriverRegistroServiziDB_LIB.log.info("Cancellate "+n+" dettagli di risposta della risorsa ["+idResource+"] associata all'accordo "+idAccordo);
				}

				if(resource.getRequest()!=null) {
					DriverRegistroServiziDB_LIB.CRUDResourceRequest(CostantiDB.CREATE, as, resource, resource.getRequest(), con, idResource);
				}
				
				for(int i=0;i<resource.sizeResponseList();i++){
					DriverRegistroServiziDB_LIB.CRUDResourceResponse(CostantiDB.CREATE, as, resource, resource.getResponse(i), con, idResource);
				}					
				DriverRegistroServiziDB_LIB.log.debug("inserite " + resource.sizeResponseList() + " dettagli di risposta relative alla risorsa ["+resource.getNome()+"] id-risorsa["+resource.getId()+"] dell'accordo :" + IDAccordoFactory.getInstance().getUriFromAccordo(as) + " id-accordo :" + idAccordo);
				
				
				// ProtocolProperties
				DriverRegistroServiziDB_LIB.CRUDProtocolProperty(type, resource.getProtocolPropertyList(), 
						idResource, ProprietariProtocolProperty.RESOURCE, con, tipoDB);
			}


			return n;

		} catch (SQLException se) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDResource] SQLException : " + se.getMessage(),se);
		} catch (Exception se) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDResource] Exception : " + se.getMessage(),se);
		} finally {
			try {
				if(selectRS!=null)selectRS.close();
				if(selectStmt!=null)selectStmt.close();
				if(updateStmt!=null)updateStmt.close();
			} catch (Exception e) {
			}
		}
	}
	
	public static void CRUDResourceRequest(int type, AccordoServizioParteComune as,Resource resource,ResourceRequest resourceRequest, Connection con, long idResource) throws DriverRegistroServiziException {
		_CRUDResourceRequestResponse(type, as, resource, resourceRequest, null, con, idResource);
	}
	public static void CRUDResourceResponse(int type, AccordoServizioParteComune as,Resource resource,ResourceResponse resourceResponse, Connection con, long idResource) throws DriverRegistroServiziException {
		_CRUDResourceRequestResponse(type, as, resource, null, resourceResponse, con, idResource);
	}
	
	private static void _CRUDResourceRequestResponse(int type, AccordoServizioParteComune as,Resource resource,
			ResourceRequest resourceRequest,ResourceResponse resourceResponse, Connection con, long idResource) throws DriverRegistroServiziException {

		PreparedStatement updateStmt = null;
		String updateQuery;
		PreparedStatement selectStmt = null;
		String selectQuery = "";
		ResultSet selectRS = null;
		long n = 0;
		if (idResource <= 0)
			new Exception("[DriverRegistroServiziDB_LIB::_CRUDResourceRequestResponse] ID Risorda non valido.");

		try {
			switch (type) {
			case CREATE:
				
				long idFK = -1;
				if(resourceRequest!=null) {
					idFK = idResource;
				}
				else {
					
					ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
					sqlQueryObject.addInsertTable(CostantiDB.API_RESOURCES_RESPONSE);
					sqlQueryObject.addInsertField("id_resource", "?");
					sqlQueryObject.addInsertField("descrizione", "?");
					sqlQueryObject.addInsertField("status", "?");
					updateQuery = sqlQueryObject.createSQLInsert();
					updateStmt = con.prepareStatement(updateQuery);
					int index = 1;
					updateStmt.setLong(index++, idResource);
					updateStmt.setString(index++, resourceResponse.getDescrizione());
					if(resourceResponse.getStatus()>=0) {
						updateStmt.setInt(index++, resourceResponse.getStatus());
					}
					else {
						updateStmt.setInt(index++, CostantiDB.API_RESOURCE_DETAIL_STATUS_UNDEFINED);
					}
					DriverRegistroServiziDB_LIB.log.debug("_CRUDResourceRequestResponse (RESPONSE) CREATE :\n"+updateQuery);
					n = updateStmt.executeUpdate();
					updateStmt.close();
					DriverRegistroServiziDB_LIB.log.debug("_CRUDResourceRequestResponse (RESPONSE) type = " + type + " row affected =" + n);
					
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
					sqlQueryObject.addFromTable(CostantiDB.API_RESOURCES_RESPONSE);
					sqlQueryObject.addSelectField("id");
					sqlQueryObject.addWhereCondition("id_resource = ?");
					sqlQueryObject.addWhereCondition("status = ?");
					sqlQueryObject.setANDLogicOperator(true);
					selectQuery = sqlQueryObject.createSQLQuery();
					selectStmt = con.prepareStatement(selectQuery);
					selectStmt.setLong(index++, idResource);
					if(resourceResponse.getStatus()>=0) {
						selectStmt.setInt(index++, resourceResponse.getStatus());
					}
					else {
						selectStmt.setInt(index++, CostantiDB.API_RESOURCE_DETAIL_STATUS_UNDEFINED);
					}
					selectRS = selectStmt.executeQuery();
					if (selectRS.next()) {
						idFK = selectRS.getLong("id");
					}
					else {
						throw new Exception("Recupero dell'id della tabella '"+CostantiDB.API_RESOURCES_RESPONSE+"' con id_resource='"+idResource+"' e status='"+resourceResponse.getStatus()+"' non riuscito");
					}
				}
				if(idFK<=0) {
					throw new Exception("Recupero dell'id della tabella padre non riuscito");
				}
				if(resourceRequest!=null) {
					resourceRequest.setIdResource(idFK);
				}
				else {
					resourceResponse.setIdResource(idFK);
				}
				
				
				for (ResourceRepresentation rr : resourceRequest.getRepresentationList()) {
					
					ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
					sqlQueryObject.addInsertTable(CostantiDB.API_RESOURCES_MEDIA);
					if(resourceRequest!=null) {
						sqlQueryObject.addInsertField("id_resource_media", "?");
					}
					else {
						sqlQueryObject.addInsertField("id_resource_response_media", "?");
					}
					sqlQueryObject.addInsertField("media_type", "?");
					sqlQueryObject.addInsertField("message_type", "?");
					sqlQueryObject.addInsertField("nome", "?");
					sqlQueryObject.addInsertField("descrizione", "?");
					sqlQueryObject.addInsertField("tipo", "?");
					sqlQueryObject.addInsertField("xml_tipo", "?");
					sqlQueryObject.addInsertField("xml_name", "?");
					sqlQueryObject.addInsertField("xml_namespace", "?");
					sqlQueryObject.addInsertField("json_type", "?");
					updateQuery = sqlQueryObject.createSQLInsert();
					updateStmt = con.prepareStatement(updateQuery);
					int index = 1;
					updateStmt.setLong(index++, idFK);
					updateStmt.setString(index++, rr.getMediaType());
					updateStmt.setString(index++,  DriverRegistroServiziDB_LIB.getValue(rr.getMessageType()));
					updateStmt.setString(index++, rr.getNome());
					updateStmt.setString(index++, rr.getDescrizione());
					updateStmt.setString(index++, DriverRegistroServiziDB_LIB.getValue(rr.getRepresentationType()));
					updateStmt.setString(index++, rr.getXml()!=null ? DriverRegistroServiziDB_LIB.getValue(rr.getXml().getXmlType()) : null);
					updateStmt.setString(index++, rr.getXml()!=null ? rr.getXml().getNome() : null);
					updateStmt.setString(index++, rr.getXml()!=null ? rr.getXml().getNamespace() : null);
					updateStmt.setString(index++, rr.getXml()!=null ? rr.getJson().getTipo() : null);					
					DriverRegistroServiziDB_LIB.log.debug("_CRUDResourceRequestResponse (MEDIA) CREATE :\n"+updateQuery);
					n = updateStmt.executeUpdate();
					DriverRegistroServiziDB_LIB.log.debug("_CRUDResourceRequestResponse (MEDIA) type = " + type + " row affected =" + n);
					
				}
				
				for (ResourceParameter rr : resourceRequest.getParameterList()) {
					
					ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
					sqlQueryObject.addInsertTable(CostantiDB.API_RESOURCES_PARAMETER);
					if(resourceRequest!=null) {
						sqlQueryObject.addInsertField("id_resource_parameter", "?");
					}
					else {
						sqlQueryObject.addInsertField("id_resource_response_par", "?");
					}
					sqlQueryObject.addInsertField("nome", "?");
					sqlQueryObject.addInsertField("descrizione", "?");
					sqlQueryObject.addInsertField("tipo_parametro", "?");
					sqlQueryObject.addInsertField("required", "?");
					sqlQueryObject.addInsertField("tipo", "?");
					updateQuery = sqlQueryObject.createSQLInsert();
					updateStmt = con.prepareStatement(updateQuery);
					int index = 1;
					updateStmt.setLong(index++, idFK);
					updateStmt.setString(index++, rr.getNome());
					updateStmt.setString(index++, rr.getDescrizione());
					updateStmt.setString(index++, DriverRegistroServiziDB_LIB.getValue(rr.getParameterType()));
					updateStmt.setBoolean(index++, rr.isRequired());
					updateStmt.setString(index++, rr.getTipo());				
					DriverRegistroServiziDB_LIB.log.debug("_CRUDResourceRequestResponse (PARAMETER) CREATE :\n"+updateQuery);
					n = updateStmt.executeUpdate();
					DriverRegistroServiziDB_LIB.log.debug("_CRUDResourceRequestResponse (PARAMETER) type = " + type + " row affected =" + n);
					
				}
				
				break;

			case UPDATE:
				throw new Exception("Not Implemented");

				//break;

			case DELETE:
				// delete

				
				// gestione request
				if(resourceRequest!=null) {
					ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
					sqlQueryObject.addDeleteTable(CostantiDB.API_RESOURCES_MEDIA);
					sqlQueryObject.addWhereCondition("id_resource_media=?");
					sqlQueryObject.setANDLogicOperator(true);
					updateQuery = sqlQueryObject.createSQLDelete();
					updateStmt=con.prepareStatement(updateQuery);
					updateStmt.setLong(1, idResource );
					updateStmt.executeUpdate();
					updateStmt.close();
					
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
					sqlQueryObject.addDeleteTable(CostantiDB.API_RESOURCES_PARAMETER);
					sqlQueryObject.addWhereCondition("id_resource_parameter=?");
					sqlQueryObject.setANDLogicOperator(true);
					updateQuery = sqlQueryObject.createSQLDelete();
					updateStmt=con.prepareStatement(updateQuery);
					updateStmt.setLong(1, idResource);
					updateStmt.executeUpdate();
					updateStmt.close();
				}
				else {
				
					// gestione response
					List<Long> idResourceResponse = new ArrayList<Long>();
					ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
					sqlQueryObject.addFromTable(CostantiDB.API_RESOURCES_RESPONSE);
					sqlQueryObject.addSelectField("id");
					sqlQueryObject.addWhereCondition("id_resource=?");
					updateQuery = sqlQueryObject.createSQLQuery();
					updateStmt=con.prepareStatement(updateQuery);
					updateStmt.setLong(1, idResource);
					selectRS=updateStmt.executeQuery();
					while(selectRS.next()){
						idResourceResponse.add(selectRS.getLong("id"));
					}
					selectRS.close();
					updateStmt.close();
		
					while(idResourceResponse.size()>0){
						
						long idRR = idResourceResponse.remove(0);
						
						sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
						sqlQueryObject.addDeleteTable(CostantiDB.API_RESOURCES_MEDIA);
						sqlQueryObject.addWhereCondition("id_resource_response_media=?");
						sqlQueryObject.setANDLogicOperator(true);
						updateQuery = sqlQueryObject.createSQLDelete();
						updateStmt=con.prepareStatement(updateQuery);
						updateStmt.setLong(1, idRR );
						updateStmt.executeUpdate();
						updateStmt.close();
						
						sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
						sqlQueryObject.addDeleteTable(CostantiDB.API_RESOURCES_PARAMETER);
						sqlQueryObject.addWhereCondition("id_resource_response_par=?");
						sqlQueryObject.setANDLogicOperator(true);
						updateQuery = sqlQueryObject.createSQLDelete();
						updateStmt=con.prepareStatement(updateQuery);
						updateStmt.setLong(1, idRR);
						updateStmt.executeUpdate();
						updateStmt.close();
					}
					
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
					sqlQueryObject.addDeleteTable(CostantiDB.API_RESOURCES_RESPONSE);
					sqlQueryObject.addWhereCondition("id_resource=?");
					sqlQueryObject.setANDLogicOperator(true);
					updateQuery = sqlQueryObject.createSQLDelete();
					updateStmt=con.prepareStatement(updateQuery);
					updateStmt.setLong(1, idResource);
					updateStmt.executeUpdate();
					updateStmt.close();
				
				}
				
				break;
			}


		} catch (SQLException se) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::_CRUDResourceRequestResponse] SQLException : " + se.getMessage(),se);
		} catch (Exception se) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::_CRUDResourceRequestResponse] Exception : " + se.getMessage(),se);
		} finally {
			try {
				if(selectRS!=null)selectRS.close();
			} catch (Exception e) {
			}
			try {
				if(selectStmt!=null)selectStmt.close();
			} catch (Exception e) {
			}
			try {
				if(updateStmt!=null)updateStmt.close();
			} catch (Exception e) {
			}
		}
	}
	

	/**
	 * Ritorna l'id del connettore di un soggetto
	 * 
	 * @param nomeSoggetto
	 * @param tipoSoggetto
	 * @param con
	 * @return id del connettore del soggetto
	 * @throws DriverRegistroServiziException
	 */
	private static long getIdConnettoreSoggetto(String nomeSoggetto, String tipoSoggetto, Connection con) throws DriverRegistroServiziException {
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
			try {
				rs.close();
				stm.close();
			} catch (Exception e) {

			}

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
	private static long getIdConnettoreServizio(String nomeServizio, String tipoServizio, Integer versioneServizio, String nomeSoggetto, String tipoSoggetto, Connection con) throws DriverRegistroServiziException {
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
			try {
				rs.close();
				stm.close();
			} catch (Exception e) {

			}

		}
	}

	private static long getIdFruizione(long idServizio, String nomeSoggetto, String tipoSoggetto, Connection con) throws DriverRegistroServiziException {
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
			try {
				rs.close();
				stm.close();
			} catch (Exception e) {

			}

		}
	}
	
	private static long getIdConnettoreServizioFruitore(long idServizio, String nomeSoggetto, String tipoSoggetto, Connection con) throws DriverRegistroServiziException {
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
			try {
				rs.close();
				stm.close();
			} catch (Exception e) {

			}

		}
	}
	
	private static long getIdConnettoreServizioAzione(long idServizio, String azione, Connection con) throws DriverRegistroServiziException {
		PreparedStatement stm = null;
		ResultSet rs = null;
		long idConnettore = -1;
		try {

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.SERVIZI_AZIONI);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("id_servizio = ?");
			sqlQueryObject.addWhereCondition("nome_azione = ?");
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
			try {
				rs.close();
				stm.close();
			} catch (Exception e) {

			}

		}
	}

	/**
	 * Cancella tutti i fruitori di un servizio e i connettori associati ai fruitori
	 * @param idServizio
	 * @param con
	 * @throws DriverRegistroServiziException
	 */
	private static void deleteAllFruitoriServizio(long idServizio, List<Long> idFruitoriEsistenti, Connection con) throws DriverRegistroServiziException {
		PreparedStatement stm = null;
		ResultSet rs = null;
		try {

			ArrayList<Long> listaFruizioniDaEliminare = new ArrayList<Long>();
			ArrayList<Long> listaFruizioniDaEliminare_Connettori = new ArrayList<Long>();

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.SERVIZI_FRUITORI);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("id_servizio = ?");
			String query = sqlQueryObject.createSQLQuery();
			stm = con.prepareStatement(query);
			stm.setLong(1, idServizio);

			rs=stm.executeQuery();
			//recupero i connettori da cancellare
			while(rs.next()){
				long idSoggettoFruitore = rs.getLong("id_soggetto");
				boolean find = false;
				for (int i = 0; i < idFruitoriEsistenti.size(); i++) {
					if(idSoggettoFruitore == idFruitoriEsistenti.get(i)){
						find = true;
						break;
					}
				}
				if(!find){
					listaFruizioniDaEliminare.add(rs.getLong("id"));
					listaFruizioniDaEliminare_Connettori.add(rs.getLong("id_connettore"));
				}
			}
			rs.close();
			stm.close();

			for (int i = 0; i < listaFruizioniDaEliminare.size(); i++) {
			
				long idFruizione = listaFruizioniDaEliminare.get(i);
				
				//elimino prima le entry nella tab servizi_fruitori per rispettare le dipendenze
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.SERVIZI_FRUITORI);
				sqlQueryObject.addWhereCondition("id=?");
				String sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idFruizione);
				int n=stm.executeUpdate();
				stm.close();
				DriverRegistroServiziDB_LIB.log.debug("Cancellata (row:"+n+") fruizione con id:"+idFruizione);
	
			}
			
			for (int i = 0; i < listaFruizioniDaEliminare_Connettori.size(); i++) {
				
				long idFruizione = listaFruizioniDaEliminare.get(i);
				long idConnettore = listaFruizioniDaEliminare_Connettori.get(i);
				
				//cancello adesso i connettori custom
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.CONNETTORI_CUSTOM);
				sqlQueryObject.addWhereCondition("id_connettore=?");
				String sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idConnettore);
				stm.executeUpdate();
				stm.close();
				DriverRegistroServiziDB_LIB.log.debug("Cancellato connettore custom associato al connettore con id:"+idConnettore+" associato alla fruizione con id:"+idFruizione);
				
				//cancello adesso i connettori
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.CONNETTORI);
				sqlQueryObject.addWhereCondition("id=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idConnettore);
				stm.executeUpdate();
				stm.close();
				DriverRegistroServiziDB_LIB.log.debug("Cancellati connettoro con id:"+idConnettore+" associato alla fruizione con id:"+idFruizione);
				
			}



		} catch (SQLException e) {
			DriverRegistroServiziDB_LIB.log.error("Errore SQL", e);
			throw new DriverRegistroServiziException(e);
		}catch (Exception e) {
			DriverRegistroServiziDB_LIB.log.error("Errore", e);
			throw new DriverRegistroServiziException(e);
		} finally {
			try {
				stm.close();
			} catch (Exception e) {

			}

		}
	}
	
	
	/**
	 * Cancella tutti le azioni di un servizio e i connettori associati alle azioni
	 * @param idServizio
	 * @param con
	 * @throws DriverRegistroServiziException
	 */
	private static void deleteAllAzioniServizio(long idServizio, Connection con) throws DriverRegistroServiziException {
		PreparedStatement stm = null;
		ResultSet rs = null;
		try {

			ArrayList<Long> listaConnettori = new ArrayList<Long>();

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.SERVIZI_AZIONI);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("id_servizio = ?");
			String query = sqlQueryObject.createSQLQuery();
			stm = con.prepareStatement(query);
			stm.setLong(1, idServizio);

			rs=stm.executeQuery();
			//recupero i connettori da cancellare
			while(rs.next()){
				listaConnettori.add(rs.getLong("id_connettore"));
			}
			rs.close();
			stm.close();

			//elimino prima le entry nella tab servizi_azioni per rispettare le dipendenze
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.SERVIZI_AZIONI);
			sqlQueryObject.addWhereCondition("id_servizio=?");
			String sqlQuery = sqlQueryObject.createSQLDelete();
			stm = con.prepareStatement(sqlQuery);
			stm.setLong(1, idServizio);
			int n=stm.executeUpdate();
			stm.close();
			DriverRegistroServiziDB_LIB.log.debug("Cancellati "+n+" Azioni del servizio "+idServizio);

			//cancello adesso i connettori custom
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.CONNETTORI_CUSTOM);
			sqlQueryObject.addWhereCondition("id_connettore=?");
			sqlQuery = sqlQueryObject.createSQLDelete();
			stm = con.prepareStatement(sqlQuery);
			for (Long idConnettore : listaConnettori) {
				stm.setLong(1, idConnettore);
				stm.executeUpdate();
			}
			stm.close();
			DriverRegistroServiziDB_LIB.log.debug("Cancellati connettori "+listaConnettori.toString()+" associati alle azioni del servizio "+idServizio);
			
			//cancello adesso i connettori
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.CONNETTORI);
			sqlQueryObject.addWhereCondition("id=?");
			sqlQuery = sqlQueryObject.createSQLDelete();
			stm = con.prepareStatement(sqlQuery);
			for (Long idConnettore : listaConnettori) {
				stm.setLong(1, idConnettore);
				stm.executeUpdate();
			}
			stm.close();
			DriverRegistroServiziDB_LIB.log.debug("Cancellati connettori "+listaConnettori.toString()+" associati ai Fruitori del servizio "+idServizio);



		} catch (SQLException e) {
			DriverRegistroServiziDB_LIB.log.error("Errore SQL", e);
			throw new DriverRegistroServiziException(e);
		}catch (Exception e) {
			DriverRegistroServiziDB_LIB.log.error("Errore", e);
			throw new DriverRegistroServiziException(e);
		} finally {
			try {
				stm.close();
			} catch (Exception e) {

			}

		}
	}

	
	public static void CRUDDocumento(int type, List<Documento> documenti, long idProprietario,
			ProprietariDocumento tipologiaProprietarioDocumento, Connection connection,
			String tipoDatabase) throws DriverRegistroServiziException {
		
		// NOTA: l'update dei documenti, essendo mega di documenti non puo' essere implementata come delete + create
		
		PreparedStatement stm = null;
		ResultSet rs=null;
		String sqlQuery;

		if((documenti == null) && (type!= CostantiDB.DELETE)) 
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDDocumento] L'oggetto documenti non puo essere null");
		if(idProprietario <= 0 ) 
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDDocumento] id proprietario non definito");
		
		IJDBCAdapter jdbcAdapter = null;
		
		try {

			jdbcAdapter = JDBCAdapterFactory.createJDBCAdapter(tipoDatabase);
			
			switch (type) {
			case CREATE:

				for(int i=0; i<documenti.size(); i++){
				
					Documento doc = documenti.get(i);
					if(doc.getFile()==null || "".equals(doc.getFile()))
						throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDDocumento] Nome non definito per documento ["+i+"]");
					if(doc.getRuolo()==null || "".equals(doc.getRuolo()))
						throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDDocumento] Ruolo non definito per documento ["+doc.getFile()+"]");
					if(doc.getTipo()==null || "".equals(doc.getTipo()))
						throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDDocumento] Tipo non definito per documento ["+doc.getFile()+"]");
					if(doc.getByteContenuto()==null || doc.getByteContenuto().length<=0)
						throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDDocumento] Contenuto non definito per documento ["+doc.getFile()+"]");
					BeanUtilities.validateTipoRuolo(doc.getTipo(),doc.getRuolo());
					
					byte[] contenuto = doc.getByteContenuto();
					if(contenuto.length<3){
						String test = new String(contenuto);
						if("".equals(test.trim().replaceAll("\n", ""))){
							// eliminare \n\n
							contenuto = null;	
						}
					}
					
					
					// create
					ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
					sqlQueryObject.addInsertTable(CostantiDB.DOCUMENTI);
					sqlQueryObject.addInsertField("ruolo", "?");
					sqlQueryObject.addInsertField("tipo", "?");
					sqlQueryObject.addInsertField("nome", "?");
					sqlQueryObject.addInsertField("id_proprietario", "?");
					sqlQueryObject.addInsertField("tipo_proprietario", "?");
					if(contenuto!=null)
						sqlQueryObject.addInsertField("contenuto", "?");
					if(doc.getOraRegistrazione()!=null)
						sqlQueryObject.addInsertField("ora_registrazione", "?");
					sqlQuery = sqlQueryObject.createSQLInsert();
					stm = connection.prepareStatement(sqlQuery);
					stm.setString(1, doc.getRuolo());
					stm.setString(2, doc.getTipo());
					stm.setString(3, doc.getFile());
					stm.setLong(4, idProprietario);
					stm.setString(5, tipologiaProprietarioDocumento.toString());
					int index = 6;
					if(contenuto!=null){
						jdbcAdapter.setBinaryData(stm,index,contenuto);
						index++;
					}
					if(doc.getOraRegistrazione()!=null){
						stm.setTimestamp(index, new Timestamp(doc.getOraRegistrazione().getTime()));
						index++;
					}
					
					DriverRegistroServiziDB_LIB.log.debug("CRUDConnettore CREATE : \n" + DBUtils.formatSQLString(sqlQuery, doc.getRuolo(), doc.getTipo(), doc.getFile(), idProprietario, tipologiaProprietarioDocumento.toString()));
	
					int n = stm.executeUpdate();
					stm.close();
					DriverRegistroServiziDB_LIB.log.debug("Inserted " + n + " row(s)");
		
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
					sqlQueryObject.addFromTable(CostantiDB.DOCUMENTI);
					sqlQueryObject.addSelectField("id");
					sqlQueryObject.addSelectField("ora_registrazione");
					sqlQueryObject.addWhereCondition("ruolo = ?");
					sqlQueryObject.addWhereCondition("tipo = ?");
					sqlQueryObject.addWhereCondition("nome = ?");
					sqlQueryObject.addWhereCondition("id_proprietario = ?");
					sqlQueryObject.addWhereCondition("tipo_proprietario = ?");
					sqlQueryObject.setANDLogicOperator(true);
					sqlQuery = sqlQueryObject.createSQLQuery();
					stm = connection.prepareStatement(sqlQuery);
					stm.setString(1, doc.getRuolo());
					stm.setString(2, doc.getTipo());
					stm.setString(3, doc.getFile());
					stm.setLong(4, idProprietario);
					stm.setString(5, tipologiaProprietarioDocumento.toString());
	
					DriverRegistroServiziDB_LIB.log.debug("Recupero idConnettore e oraRegistrazione inserito : \n" + DBUtils.formatSQLString(sqlQuery,doc.getRuolo(), doc.getTipo(), doc.getFile(), idProprietario, tipologiaProprietarioDocumento.toString()));
	
					rs = stm.executeQuery();
	
					if (rs.next()) {
						documenti.get(i).setId(rs.getLong("id"));
						documenti.get(i).setOraRegistrazione(rs.getTimestamp("ora_registrazione"));
					} else {
						throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDConnettore] Errore tentanto di effettuare la select dopo una create, non riesco a recuperare l'id!");
					}
	
					rs.close();
					stm.close();
					
				}
				break;

			case UPDATE:
				
				// Prelevo vecchia lista
				List<Documento> oldLista = null;
				try{
					oldLista = DriverRegistroServiziDB_LIB.getListaDocumenti(idProprietario,tipologiaProprietarioDocumento, false, connection, tipoDatabase);
				}catch(DriverRegistroServiziNotFound dNotFound){
					oldLista = new ArrayList<Documento>();
				}
				
				// Gestico la nuova immagine
				for(int i=0; i<documenti.size(); i++){
					
					Documento doc = documenti.get(i);
					if(doc.getFile()==null || "".equals(doc.getFile()))
						throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDDocumento] Nome non definito per documento ["+i+"]");
					if(doc.getRuolo()==null || "".equals(doc.getRuolo()))
						throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDDocumento] Ruolo non definito per documento ["+doc.getFile()+"]");
					if(doc.getTipo()==null || "".equals(doc.getTipo()))
						throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDDocumento] Tipo non definito per documento ["+doc.getFile()+"]");
					BeanUtilities.validateTipoRuolo(doc.getTipo(),doc.getRuolo());
					
					//if(doc.getId()<=0){
					// Rileggo sempre id, puo' essere diverso (es. importato tramite sincronizzazioni)
					String tipologiaProprietarioDocumentoString = null;
					if(tipologiaProprietarioDocumento!=null){
						tipologiaProprietarioDocumentoString = tipologiaProprietarioDocumento.toString();
					}
					doc.setId(DBUtils.getIdDocumento(doc.getFile(), doc.getTipo(), doc.getRuolo(), idProprietario, connection, 
							DriverRegistroServiziDB_LIB.tipoDB, tipologiaProprietarioDocumentoString));
					//}
					// L'ora di registrazione deve essere impostata solo se non presente, in tale caso siamo in un caso di creazione del nuovo documento
					if(doc.getOraRegistrazione()==null){
						doc.setOraRegistrazione(new Date());
					}
					
					// Assegno corretto idProprietario se id e' diverso (es. importato tramite sincronizzazioni)
					doc.setIdProprietarioDocumento(idProprietario);
					
					boolean documentoGiaPresente = false;
					boolean documentoDaAggiornare = false;
					if(doc.getId()>0){
						for(int j=0; j<oldLista.size(); j++){
							Documento old = oldLista.get(j);
							/*System.out.println("OLD["+old.getRuolo()+"]==ATTUALE["+doc.getRuolo()+"] ("+doc.getRuolo().equals(old.getRuolo())+")");
							System.out.println("OLD["+old.getTipo()+"]==ATTUALE["+doc.getTipo()+"] ("+doc.getTipo().equals(old.getTipo())+")");
							System.out.println("OLD["+old.getFile()+"]==ATTUALE["+doc.getFile()+"] ("+doc.getFile().equals(old.getFile())+")");
							System.out.println("OLD["+old.getIdProprietarioDocumento().longValue()+"]==ATTUALE["+doc.getIdProprietarioDocumento().longValue()+"] ("+(doc.getIdProprietarioDocumento().longValue() == old.getIdProprietarioDocumento().longValue())+")");
							System.out.println("OLD["+old.getTipoProprietarioDocumento()+"]==ATTUALE["+tipologiaProprietarioDocumento+"] ("+tipologiaProprietarioDocumento.equals(old.getTipoProprietarioDocumento())+")");
							if(doc.getRuolo().equals(old.getRuolo()) &&
									doc.getTipo().equals(old.getTipo()) &&
									doc.getFile().equals(old.getFile()) &&
									( (doc.getIdProprietarioDocumento().longValue()) == (old.getIdProprietarioDocumento().longValue()) ) &&
									tipologiaProprietarioDocumento.equals(old.getTipoProprietarioDocumento()) ){*/
							
							//System.out.println("OLD["+old.getId().longValue()+"]==ATTUALE["+doc.getId().longValue()+"] ("+((doc.getId().longValue() == old.getId().longValue()))+")");
							if(doc.getId().longValue() == old.getId().longValue()){		
									documentoGiaPresente = true; // non devo fare una insert, ma una update...
									
									if(doc.getOraRegistrazione()==null)
										throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDDocumento] OraRegistrazione non definita per documento, precedentemente gia inserito, ["+doc.getFile()+"] ["+doc.getTipo()+"] ["+tipologiaProprietarioDocumento+"] ["+idProprietario+"]");
											
									// rimuovo la vecchia immagine del documento dalla lista dei doc vecchi
									oldLista.remove(j);
									
									// Il documento e' lo stesso, rimane da verificare che sia un documento successivo di modifica.
									if(doc.getOraRegistrazione().after(old.getOraRegistrazione())){
										documentoDaAggiornare = true;
									}
							}
						}
					}

					byte[] contenuto = null;
					if( (documentoGiaPresente == false) || (documentoDaAggiornare) ){
						// Il contenuto ci deve essere
						if(doc.getByteContenuto()==null || doc.getByteContenuto().length<=0)
							throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDDocumento] Contenuto non definito per documento ["+doc.getFile()+"] ["+doc.getTipo()+"] ["+tipologiaProprietarioDocumento+"] ["+idProprietario+"]");

						contenuto = doc.getByteContenuto();
						if(contenuto.length<3){
							String test = new String(contenuto);
							if("".equals(test.trim().replaceAll("\n", ""))){
								// eliminare \n\n
								contenuto = null;	
							}
						}
					}
					
					if(documentoGiaPresente){
						if(documentoDaAggiornare){
							// update
							long idDocumento = doc.getId();
							if(idDocumento<=0){
								throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDDocumento] ID non definito per documento da aggiorare ["+doc.getFile()+"] ["+doc.getTipo()+"] ["+tipologiaProprietarioDocumento+"] ["+idProprietario+"]");
							}
							ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
							sqlQueryObject.addUpdateTable(CostantiDB.DOCUMENTI);
							sqlQueryObject.addUpdateField("tipo", "?");
							sqlQueryObject.addUpdateField("nome", "?");
							sqlQueryObject.addUpdateField("contenuto", "?");
							sqlQueryObject.addUpdateField("ora_registrazione", "?");
							sqlQueryObject.addWhereCondition("id=?");
							sqlQuery = sqlQueryObject.createSQLUpdate();
							stm = connection.prepareStatement(sqlQuery);
							stm.setString(1, doc.getTipo());
							stm.setString(2, doc.getFile());
							jdbcAdapter.setBinaryData(stm,3,contenuto);
							stm.setTimestamp(4, new Timestamp(doc.getOraRegistrazione().getTime()));
							stm.setLong(5, idDocumento);
							stm.executeUpdate();
							stm.close();
						}
					}else{
						// insert
						ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
						sqlQueryObject.addInsertTable(CostantiDB.DOCUMENTI);
						sqlQueryObject.addInsertField("ruolo", "?");
						sqlQueryObject.addInsertField("tipo", "?");
						sqlQueryObject.addInsertField("nome", "?");
						sqlQueryObject.addInsertField("id_proprietario", "?");
						sqlQueryObject.addInsertField("tipo_proprietario", "?");
						if(contenuto!=null)
							sqlQueryObject.addInsertField("contenuto", "?");
						if(doc.getOraRegistrazione()!=null)
							sqlQueryObject.addInsertField("ora_registrazione", "?");
						sqlQuery = sqlQueryObject.createSQLInsert();
						stm = connection.prepareStatement(sqlQuery);
						stm.setString(1, doc.getRuolo());
						stm.setString(2, doc.getTipo());
						stm.setString(3, doc.getFile());
						stm.setLong(4, idProprietario);
						stm.setString(5, tipologiaProprietarioDocumento.toString());
						int index = 6;
						if(contenuto!=null){
							jdbcAdapter.setBinaryData(stm,index,contenuto);
							index++;
						}
						if(doc.getOraRegistrazione()!=null){
							stm.setTimestamp(index, new Timestamp(doc.getOraRegistrazione().getTime()));
							index++;
						}
						
						DriverRegistroServiziDB_LIB.log.debug("CRUDDocumento UPDATE : \n" + DBUtils.formatSQLString(sqlQuery, doc.getRuolo(), doc.getTipo(), doc.getFile(), idProprietario, tipologiaProprietarioDocumento.toString()));
		
						int n = stm.executeUpdate();
						stm.close();
						DriverRegistroServiziDB_LIB.log.debug("Inserted " + n + " row(s)");
			
						sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
						sqlQueryObject.addFromTable(CostantiDB.DOCUMENTI);
						sqlQueryObject.addSelectField("id");
						sqlQueryObject.addWhereCondition("ruolo = ?");
						sqlQueryObject.addWhereCondition("tipo = ?");
						sqlQueryObject.addWhereCondition("nome = ?");
						sqlQueryObject.addWhereCondition("id_proprietario = ?");
						sqlQueryObject.addWhereCondition("tipo_proprietario = ?");
						sqlQueryObject.setANDLogicOperator(true);
						sqlQuery = sqlQueryObject.createSQLQuery();
						stm = connection.prepareStatement(sqlQuery);
						stm.setString(1, doc.getRuolo());
						stm.setString(2, doc.getTipo());
						stm.setString(3, doc.getFile());
						stm.setLong(4, idProprietario);
						stm.setString(5, tipologiaProprietarioDocumento.toString());
		
						DriverRegistroServiziDB_LIB.log.debug("Recupero idConnettore inserito : \n" + DBUtils.formatSQLString(sqlQuery,doc.getRuolo(), doc.getTipo(), doc.getFile(), idProprietario,tipologiaProprietarioDocumento.toString()));
		
						rs = stm.executeQuery();
		
						if (rs.next()) {
							documenti.get(i).setId(rs.getLong("id"));
						} else {
							throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDConnettore] Errore tentanto di effettuare la select dopo una create, non riesco a recuperare l'id!");
						}
		
						rs.close();
						stm.close();
					}
					
				}
				
				if(oldLista.size()>0){
					// Qualche documento e' stato cancellato.
					// Non e' piu' presente.
					for(int j=0; j<oldLista.size(); j++){
						Documento old = oldLista.get(j);
						
						ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
						sqlQueryObject.addDeleteTable(CostantiDB.DOCUMENTI);
						sqlQueryObject.addWhereCondition("id=?");
						sqlQuery = sqlQueryObject.createSQLDelete();
						stm = connection.prepareStatement(sqlQuery);
						stm.setLong(1, old.getId());
						stm.executeUpdate();
						stm.close();
					}
				}
				
				break;

			case DELETE:
				// delete
				
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.DOCUMENTI);
				sqlQueryObject.addWhereCondition("id_proprietario=?");
				sqlQueryObject.addWhereCondition("tipo_proprietario = ?");
				sqlQueryObject.setANDLogicOperator(true);
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = connection.prepareStatement(sqlQuery);
				stm.setLong(1, idProprietario);
				stm.setString(2, tipologiaProprietarioDocumento.toString());
				stm.executeUpdate();
				stm.close();

				DriverRegistroServiziDB_LIB.log.debug("CRUDDocumento DELETE : \n" + DBUtils.formatSQLString(sqlQuery, idProprietario));

				break;
			}

		} catch (SQLException se) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDDocumento] SQLException : " + se.getMessage(),se);
		}catch (Exception se) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDDocumento] Exception : " + se.getMessage(),se);
		} finally {
			try {
				if(rs!=null) rs.close();
				if(stm!=null) stm.close();
			} catch (Exception e) {
			}
		}
	}
	
	
	public static List<Documento> getListaDocumenti(long idProprietario, ProprietariDocumento tipologiaProprietarioDocumento, boolean readBytes, 
			Connection connection,
			String tipoDatabase) throws DriverRegistroServiziException,DriverRegistroServiziNotFound {
		return DriverRegistroServiziDB_LIB.getListaDocumenti(null,idProprietario,tipologiaProprietarioDocumento,readBytes,connection,tipoDatabase);
	}
		
	public static List<Documento> getListaDocumenti(String ruoloDocumenti, long idProprietario, 
			ProprietariDocumento tipologiaProprietarioDocumento, boolean readBytes, Connection connection,
			String tipoDatabase) throws DriverRegistroServiziException,DriverRegistroServiziNotFound {
		
		PreparedStatement stm = null;
		ResultSet rs=null;
		String sqlQuery;

		if(idProprietario <= 0 ) 
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::getListaDocumenti] id proprietario non definito");
		
		try {
		
			List<Documento> documenti = new ArrayList<Documento>();
			
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.DOCUMENTI);
			sqlQueryObject.addSelectField("id");
			sqlQueryObject.addWhereCondition("id_proprietario = ?");
			sqlQueryObject.addWhereCondition("tipo_proprietario = ?");
			sqlQueryObject.setANDLogicOperator(true);
			if(ruoloDocumenti!=null)
				sqlQueryObject.addWhereCondition("ruolo = ?");
			sqlQueryObject.setANDLogicOperator(true);
			sqlQuery = sqlQueryObject.createSQLQuery();
			stm = connection.prepareStatement(sqlQuery);
			stm.setLong(1, idProprietario);
			stm.setString(2, tipologiaProprietarioDocumento.toString());
			if(ruoloDocumenti!=null)
				stm.setString(3, ruoloDocumenti);
			rs = stm.executeQuery();
			
			while(rs.next()){
				Documento doc = DriverRegistroServiziDB_LIB.getDocumento(rs.getLong("id"),readBytes, connection,tipoDatabase); 
				documenti.add(doc);
			}
			
			if(documenti.size()<=0)
				throw new DriverRegistroServiziNotFound("Documenti con ruolo["+ruoloDocumenti+"] e idProprietario["+idProprietario+"] non trovati");
			
			return documenti;

		} catch (SQLException se) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::getListaDocumenti] SQLException : " + se.getMessage(),se);
		}catch (DriverRegistroServiziNotFound dnf) {
			throw dnf;
		}catch (Exception se) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::getListaDocumenti] Exception : " + se.getMessage(),se);
		} finally {
			try {
				if(rs!=null) rs.close();
				if(stm!=null) stm.close();
			} catch (Exception e) {
			}
		}
	}
	
	public static Documento getDocumento(long id, boolean readBytes, Connection connection, String tipoDatabase) throws DriverRegistroServiziException,DriverRegistroServiziNotFound {
		
		PreparedStatement stm = null;
		ResultSet rs=null;
		String sqlQuery;

		if(id <= 0 ) 
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::getDocumento] id non definito");
		
		try {
		
			IJDBCAdapter jdbcAdapter = null;
			if(readBytes){
				// jdbcAdapter = org.openspcoop.utils.jdbc.JDBCAdapterFactory.createJDBCAdapter(DriverRegistroServiziDB_LIB.tipoDB);
				jdbcAdapter = JDBCAdapterFactory.createJDBCAdapter(tipoDatabase);
			}
				
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.DOCUMENTI);
			sqlQueryObject.addSelectField("ruolo");
			sqlQueryObject.addSelectField("tipo");
			sqlQueryObject.addSelectField("nome");
			sqlQueryObject.addSelectField("id_proprietario");
			sqlQueryObject.addSelectField("tipo_proprietario");
			sqlQueryObject.addSelectField("ora_registrazione");
			sqlQueryObject.addSelectField("id");
			if(readBytes)
				sqlQueryObject.addSelectField("contenuto");
			sqlQueryObject.addWhereCondition("id = ?");
			sqlQueryObject.setANDLogicOperator(true);
			sqlQuery = sqlQueryObject.createSQLQuery();
			stm = connection.prepareStatement(sqlQuery);
			stm.setLong(1, id);
			rs = stm.executeQuery();
			
			Documento doc = null;
			if(rs.next()){
				doc = new Documento();
				doc.setRuolo(rs.getString("ruolo"));
				doc.setTipo(rs.getString("tipo"));
				doc.setFile(rs.getString("nome"));
				doc.setIdProprietarioDocumento(rs.getLong("id_proprietario"));
				doc.setTipoProprietarioDocumento(rs.getString("tipo_proprietario"));
				doc.setOraRegistrazione(rs.getTimestamp("ora_registrazione"));
				doc.setId(rs.getLong("id"));
				if(readBytes){
					doc.setByteContenuto(jdbcAdapter.getBinaryData(rs,8));
				}
			}
			
			if(doc==null)
				throw new DriverRegistroServiziNotFound("Documento con id["+id+"] non trovato");
			
			return doc;

		} catch (SQLException se) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::getDocumento] SQLException : " + se.getMessage(),se);
		}catch (DriverRegistroServiziNotFound dnf) {
			throw dnf;
		}catch (Exception se) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::getDocumento] Exception : " + se.getMessage(),se);
		} finally {
			try {
				if(rs!=null) rs.close();
				if(stm!=null) stm.close();
			} catch (Exception e) {
			}
		}
	}
	
	
	public static void CRUDAccordoServizioParteComuneServizioComposto(int type, AccordoServizioParteComuneServizioComposto asServComposto, Connection con, long idAccordo) throws DriverRegistroServiziException {
		PreparedStatement updateStmt = null;
		String updateQuery;
		PreparedStatement selectStmt = null;
		ResultSet selectRS = null;
		if (idAccordo <= 0)
			new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDAccordoServizioServizioComposto] ID Accordo non valido.");
		
		if (asServComposto == null && (type==CostantiDB.CREATE || type==CostantiDB.UPDATE))
			new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDAccordoServizioServizioComposto] Accordo Cooperazione non valido.");
		
//		if(asServComposto!=null){
//			if (asServComposto.getIdAccordoCooperazione() <= 0 && (type==CostantiDB.CREATE || type==CostantiDB.UPDATE)){
//				
//			}
//				new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDAccordoServizioServizioComposto] ID Accordo Cooperazione non valido.");
//		}
		
		try {
			switch (type) {
			case CREATE:
				// create
				
				long idAccordoCooperazione = -1;
				if(asServComposto.getIdAccordoCooperazione()!=null){
					idAccordoCooperazione = asServComposto.getIdAccordoCooperazione();
				}
				//if(idAccordoCooperazione<=0){
				// Necessario sempre per la sincronizzazione
				if(asServComposto.getAccordoCooperazione()!=null){
					idAccordoCooperazione = 
							DBUtils.getIdAccordoCooperazione(IDAccordoCooperazioneFactory.getInstance().getIDAccordoFromUri(asServComposto.getAccordoCooperazione()),
								con, DriverRegistroServiziDB_LIB.tipoDB);
				}
				//}
				if(idAccordoCooperazione<=0){
					throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDAccordoServizioServizioComposto] idAccordoCooperazione non fornito");
				}
				
				// Servizio composto
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
				sqlQueryObject.addInsertTable(CostantiDB.ACCORDI_SERVIZI_COMPOSTO);
				sqlQueryObject.addInsertField("id_accordo", "?");
				sqlQueryObject.addInsertField("id_accordo_cooperazione", "?");
				updateQuery = sqlQueryObject.createSQLInsert();
				updateStmt = con.prepareStatement(updateQuery);
				updateStmt.setLong(1, idAccordo);
				updateStmt.setLong(2, idAccordoCooperazione);
				updateStmt.executeUpdate();
				updateStmt.close();
				DriverRegistroServiziDB_LIB.log.debug("Aggiungo acc servizio composto");

				// Recupero id
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.ACCORDI_SERVIZI_COMPOSTO);
				sqlQueryObject.addSelectField("id");
				sqlQueryObject.addWhereCondition("id_accordo=?");
				sqlQueryObject.setANDLogicOperator(true);
				updateQuery = sqlQueryObject.createSQLQuery();
				updateStmt = con.prepareStatement(updateQuery);
				updateStmt.setLong(1, idAccordo);
				selectRS = updateStmt.executeQuery();
				long idAccServComposto = -1;
				if(selectRS.next()){
					idAccServComposto = selectRS.getLong("id");
					asServComposto.setId(idAccServComposto);
				}else{
					throw new  DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDAccordoServizioServizioComposto] ID AccordoServizioComposto inserito non recuperato.");
				}
				selectRS.close();
				updateStmt.close();
				
				// Servizio componenti
				for(int i=0; i< asServComposto.sizeServizioComponenteList(); i++){
				
					AccordoServizioParteComuneServizioCompostoServizioComponente tmp = asServComposto.getServizioComponente(i);
					long idServizioComponente = -1;
					//if(idServizioComponente<=0){
					// Necessario sempre per la sincronizzazione
					// Provo a prenderlo attraverso la uri dell'accordo
					if(tmp.getTipoSoggetto()!=null && tmp.getNomeSoggetto()!=null && tmp.getTipo()!=null && tmp.getNome()!=null){
						// Provo a prenderlo attraverso la uri dell'accordo
						DriverRegistroServiziDB_LIB.log.debug("Provo a recuperare l'id del servizio con tipo/nome soggetto erogatore ["+tmp.getTipoSoggetto()+"/"+tmp.getNomeSoggetto()
								+"] e tipo/nome servizio ["+tmp.getTipo()+"/"+tmp.getNome()+"]");
						if(tmp.getTipoSoggetto()!=null && tmp.getNomeSoggetto()!=null && tmp.getTipo()!=null  &&  tmp.getNome()!=null)
							idServizioComponente = DBUtils.getIdServizio(tmp.getNome(), tmp.getTipo(), tmp.getVersione(), tmp.getNomeSoggetto(), tmp.getTipoSoggetto(), con, DriverRegistroServiziDB_LIB.tipoDB);
					}
					if(idServizioComponente<=0){
						idServizioComponente = tmp.getIdServizioComponente();
					}
					//}
					if(idServizioComponente <=0 )
						throw new  DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDAccordoServizioServizioComposto] ID ServizioComponente non definito.");
							
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
					sqlQueryObject.addInsertTable(CostantiDB.ACCORDI_SERVIZI_COMPONENTI);
					sqlQueryObject.addInsertField("id_servizio_composto", "?");
					sqlQueryObject.addInsertField("id_servizio_componente", "?");
					sqlQueryObject.addInsertField("azione", "?");
					updateQuery = sqlQueryObject.createSQLInsert();
					updateStmt = con.prepareStatement(updateQuery);
					updateStmt.setLong(1, idAccServComposto);
					updateStmt.setLong(2, idServizioComponente);
					updateStmt.setString(3, tmp.getAzione());
					updateStmt.executeUpdate();
					updateStmt.close();
					DriverRegistroServiziDB_LIB.log.debug("Aggiunto acc servizio componente");
					
				}

								
				break;

			case UPDATE:
				// update
				
				idAccServComposto = asServComposto.getId();
				if(idAccServComposto <=0 )
					throw new  DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDAccordoServizioServizioComposto] ID AccordoServizioComposto non definito.");
				
				
				idAccordoCooperazione = -1;
				if(asServComposto.getIdAccordoCooperazione()!=null){
					idAccordoCooperazione = asServComposto.getIdAccordoCooperazione();
				}
				//if(idAccordoCooperazione<=0){
				// Necessario sempre per la sincronizzazione
				if(asServComposto.getAccordoCooperazione()!=null){
					idAccordoCooperazione = 
							DBUtils.getIdAccordoCooperazione(IDAccordoCooperazioneFactory.getInstance().getIDAccordoFromUri(asServComposto.getAccordoCooperazione()),
								con, DriverRegistroServiziDB_LIB.tipoDB);
				}
				//}
				if(idAccordoCooperazione<=0){
					throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDAccordoServizioServizioComposto] idAccordoCooperazione non fornito");
				}
				
				
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
				sqlQueryObject.addUpdateTable(CostantiDB.ACCORDI_SERVIZI_COMPOSTO);
				sqlQueryObject.addUpdateField("id_accordo_cooperazione", "?");
				sqlQueryObject.addWhereCondition("id=?");
				sqlQueryObject.setANDLogicOperator(true);
				updateQuery = sqlQueryObject.createSQLUpdate();
				updateStmt = con.prepareStatement(updateQuery);
				updateStmt.setLong(1, idAccordoCooperazione);
				updateStmt.executeUpdate();
				updateStmt.close();
				DriverRegistroServiziDB_LIB.log.debug("Aggiornato acc servizio composto");
				
				// Elimino vecchi servizi componenti
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.ACCORDI_SERVIZI_COMPONENTI);
				sqlQueryObject.addWhereCondition("id_servizio_composto=?");
				sqlQueryObject.setANDLogicOperator(true);
				updateQuery = sqlQueryObject.createSQLDelete();
				updateStmt = con.prepareStatement(updateQuery);
				updateStmt.setLong(1, idAccServComposto);
				updateStmt.executeUpdate();
				updateStmt.close();

				// Inserisco nuovi servizi componenti
				for(int i=0; i< asServComposto.sizeServizioComponenteList(); i++){
					
					AccordoServizioParteComuneServizioCompostoServizioComponente tmp = asServComposto.getServizioComponente(i);
					long idServizioComponente = -1;
					//if(idServizioComponente<=0){
					// Necessario sempre per la sincronizzazione
					// Provo a prenderlo attraverso la uri dell'accordo
					if(tmp.getTipoSoggetto()!=null && tmp.getNomeSoggetto()!=null && tmp.getTipo()!=null && tmp.getNome()!=null){
						// Provo a prenderlo attraverso la uri dell'accordo
						DriverRegistroServiziDB_LIB.log.debug("Provo a recuperare l'id del servizio con tipo/nome soggetto erogatore ["+tmp.getTipoSoggetto()+"/"+tmp.getNomeSoggetto()
								+"] e tipo/nome servizio ["+tmp.getTipo()+"/"+tmp.getNome()+"]");
						if(tmp.getTipoSoggetto()!=null && tmp.getNomeSoggetto()!=null && tmp.getTipo()!=null  &&  tmp.getNome()!=null)
							idServizioComponente = DBUtils.getIdServizio(tmp.getNome(), tmp.getTipo(), tmp.getVersione(), tmp.getNomeSoggetto(), tmp.getTipoSoggetto(), con, DriverRegistroServiziDB_LIB.tipoDB);
					}
					if(idServizioComponente<=0){
						idServizioComponente = tmp.getIdServizioComponente();
					}
					if(idServizioComponente <=0 )
						throw new  DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDAccordoServizioServizioComposto] ID ServizioComponente non definito.");
						
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
					sqlQueryObject.addInsertTable(CostantiDB.ACCORDI_SERVIZI_COMPONENTI);
					sqlQueryObject.addInsertField("id_servizio_composto", "?");
					sqlQueryObject.addInsertField("id_servizio_componente", "?");
					sqlQueryObject.addInsertField("azione", "?");
					updateQuery = sqlQueryObject.createSQLInsert();
					updateStmt = con.prepareStatement(updateQuery);
					updateStmt.setLong(1, idAccServComposto);
					updateStmt.setLong(2, idServizioComponente);
					updateStmt.setString(3, tmp.getAzione());
					updateStmt.executeUpdate();
					updateStmt.close();
					DriverRegistroServiziDB_LIB.log.debug("Aggiungo acc servizio componente");
					
				}
				
				break;

			case DELETE:
				// delete

				idAccServComposto = -1;
				
				if(asServComposto!=null){
					idAccServComposto = asServComposto.getId();
					if(idAccServComposto <=0 ){
						//throw new  DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDAccordoServizioServizioComposto] ID AccordoServizioComposto non definito.");
						
						// lo cerco
						sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
						sqlQueryObject.addFromTable(CostantiDB.ACCORDI_SERVIZI_COMPOSTO);
						sqlQueryObject.addSelectField("id");
						sqlQueryObject.addWhereCondition("id_accordo=?");
						updateQuery = sqlQueryObject.createSQLQuery();
						updateStmt = con.prepareStatement(updateQuery);
						updateStmt.setLong(1, idAccordo);
						selectRS = updateStmt.executeQuery();
						if(selectRS.next()){
							idAccServComposto = selectRS.getLong("id");
						}
						selectRS.close();
						updateStmt.close();
						
						if(idAccServComposto <=0 ){
							throw new  DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDAccordoServizioServizioComposto] ID AccordoServizioComposto non definito e non recuperabile.");
						}
					}
				}else{
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
					sqlQueryObject.addFromTable(CostantiDB.ACCORDI_SERVIZI_COMPOSTO);
					sqlQueryObject.addSelectField("id");
					sqlQueryObject.addWhereCondition("id_accordo=?");
					updateQuery = sqlQueryObject.createSQLQuery();
					updateStmt = con.prepareStatement(updateQuery);
					updateStmt.setLong(1, idAccordo);
					selectRS = updateStmt.executeQuery();
					if(selectRS.next()){
						idAccServComposto = selectRS.getLong("id");
					}
					selectRS.close();
					updateStmt.close();
				}
				
				if(idAccServComposto>0){
				
					// Elimino vecchi servizi componenti
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
					sqlQueryObject.addDeleteTable(CostantiDB.ACCORDI_SERVIZI_COMPONENTI);
					sqlQueryObject.addWhereCondition("id_servizio_composto=?");
					sqlQueryObject.setANDLogicOperator(true);
					updateQuery = sqlQueryObject.createSQLDelete();
					updateStmt = con.prepareStatement(updateQuery);
					updateStmt.setLong(1, idAccServComposto);
					updateStmt.executeUpdate();
					updateStmt.close();
					
					// Elimino servizio composto
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
					sqlQueryObject.addDeleteTable(CostantiDB.ACCORDI_SERVIZI_COMPOSTO);
					sqlQueryObject.addWhereCondition("id=?");
					sqlQueryObject.setANDLogicOperator(true);
					updateQuery = sqlQueryObject.createSQLDelete();
					updateStmt = con.prepareStatement(updateQuery);
					updateStmt.setLong(1, idAccServComposto);
					updateStmt.executeUpdate();
					updateStmt.close();

				}
				
				break;
			}

		} catch (SQLException se) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDAccordoServizioServizioComposto] SQLException : " + se.getMessage(),se);
		} catch (Exception se) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDAccordoServizioServizioComposto] Exception : " + se.getMessage(),se);
		} finally {
			try {
				if(selectRS!=null)selectRS.close();
				if(selectStmt!=null)selectStmt.close();
				if(updateStmt!=null)updateStmt.close();
			} catch (Exception e) {
			}
		}
	}
	
	
	
	
	
	
	/**
	 * Accordo di Cooperazione CRUD
	 */
	public static void CRUDAccordoCooperazione(int type, org.openspcoop2.core.registry.AccordoCooperazione accordoCooperazione, 
			Connection con, String tipoDatabase) throws DriverRegistroServiziException {
		if (accordoCooperazione == null)
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDAccordoCooperazione] Accordo di Cooperazione non valido.");

		String nome = accordoCooperazione.getNome();
		String descrizione = accordoCooperazione.getDescrizione();
		
		String stato = accordoCooperazione.getStatoPackage();
		boolean privato = accordoCooperazione.getPrivato()!=null && accordoCooperazione.getPrivato();
		String superUser = accordoCooperazione.getSuperUser();	
		
		if (nome == null || nome.equals(""))
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDAccordoCooperazione] Parametro Nome non valido.");
		
		IDSoggetto soggettoReferente = null;
		if(accordoCooperazione.getSoggettoReferente()!=null){
			soggettoReferente = new IDSoggetto(accordoCooperazione.getSoggettoReferente().getTipo(), accordoCooperazione.getSoggettoReferente().getNome());
		}
		
		PreparedStatement updateStmt = null;
		ResultSet updateRS = null;
		try {
			
			switch (type) {
			case CREATE:
				// CREATE

				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
				sqlQueryObject.addInsertTable(CostantiDB.ACCORDI_COOPERAZIONE);
				sqlQueryObject.addInsertField("nome", "?");
				sqlQueryObject.addInsertField("descrizione", "?");
				sqlQueryObject.addInsertField("versione", "?");
				sqlQueryObject.addInsertField("privato", "?");
				sqlQueryObject.addInsertField("superuser", "?");
				if(stato!=null)
					sqlQueryObject.addInsertField("stato", "?");
				if(accordoCooperazione.getSoggettoReferente()!=null)
					sqlQueryObject.addInsertField("id_referente", "?");
				if(accordoCooperazione.getOraRegistrazione()!=null)
					sqlQueryObject.addInsertField("ora_registrazione", "?");
				
				String updateQuery = sqlQueryObject.createSQLInsert();
				updateStmt = con.prepareStatement(updateQuery);

				updateStmt.setString(1, nome);
				updateStmt.setString(2, descrizione);
				
				updateStmt.setInt(3, accordoCooperazione.getVersione());
				
				if(privato)
					updateStmt.setInt(4, 1);
				else
					updateStmt.setInt(4, 0);
				updateStmt.setString(5, superUser);
				
				int index = 6;
				
				if(stato!=null){
					updateStmt.setString(index, stato);
					index++;
				}
				
				if(accordoCooperazione.getSoggettoReferente()!=null){
					long idReferente = DBUtils.getIdSoggetto(accordoCooperazione.getSoggettoReferente().getNome(), accordoCooperazione.getSoggettoReferente().getTipo(), con, DriverRegistroServiziDB_LIB.tipoDB);
					if(idReferente<=0){
						throw new DriverRegistroServiziException("Soggetto Referente ["+accordoCooperazione.getSoggettoReferente().getTipo()+"/"+accordoCooperazione.getSoggettoReferente().getNome()+"] non trovato");
					}
					updateStmt.setLong(index, idReferente);
					index++;
				}
				
				if(accordoCooperazione.getOraRegistrazione()!=null){
					updateStmt.setTimestamp(index, new Timestamp(accordoCooperazione.getOraRegistrazione().getTime()));
					index++;
				}
				
				// eseguo lo statement
				int n = updateStmt.executeUpdate();
				updateStmt.close();
				DriverRegistroServiziDB_LIB.log.debug("CRUDAccordoServizioParteSpecifica CREATE : \n" + DriverRegistroServiziDB_LIB.formatSQLString(updateQuery, nome, descrizione));
				DriverRegistroServiziDB_LIB.log.debug("CRUDAccordoServizioParteSpecifica type = " + type + " row affected =" + n);

				
				// recupero l-id dell'accordo appena inserito
				IDAccordoCooperazione idAccordoObject = IDAccordoCooperazioneFactory.getInstance().getIDAccordoFromValues(accordoCooperazione.getNome(),
						soggettoReferente, accordoCooperazione.getVersione());
				long idAccordoCooperazione = DBUtils.getIdAccordoCooperazione(idAccordoObject, con, DriverRegistroServiziDB_LIB.tipoDB);
				if (idAccordoCooperazione<=0) {
					throw new DriverRegistroServiziException("[DriverRegistroServiziDB::CRUDAccordoCooperazione] non riesco a trovare l'id del'Accordo inserito");
				}
				accordoCooperazione.setId(idAccordoCooperazione);
								
				
				// aggiungo partecipanti
				if(accordoCooperazione.getElencoPartecipanti()!=null){
					AccordoCooperazionePartecipanti partecipanti = accordoCooperazione.getElencoPartecipanti();
					for(int i=0; i<partecipanti.sizeSoggettoPartecipanteList(); i++){
						
						IdSoggetto soggettoPartecipante = partecipanti.getSoggettoPartecipante(i);
						long idSoggettoPartecipante = -1;
						if(idSoggettoPartecipante <=0 ){
							// Provo a prenderlo attraverso il tipo/nome
							DriverRegistroServiziDB_LIB.log.debug("Provo a recuperare l'id del soggetto con tipo/nome ["+soggettoPartecipante.getTipo()+"]/["+soggettoPartecipante.getNome()+"]");
							if(soggettoPartecipante.getTipo()!=null && soggettoPartecipante.getNome()!=null)
								idSoggettoPartecipante = DBUtils.getIdSoggetto(soggettoPartecipante.getNome(),soggettoPartecipante.getTipo(), con, DriverRegistroServiziDB_LIB.tipoDB);
						}
						if(idSoggettoPartecipante<=0){
							idSoggettoPartecipante = soggettoPartecipante.getIdSoggetto();
						}
						if(idSoggettoPartecipante<=0)
							throw new DriverRegistroServiziException("[DriverRegistroServiziDB::CRUDAccordoCooperazione] idSoggettoPartecipante non presente");
						
						sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
						sqlQueryObject.addInsertTable(CostantiDB.ACCORDI_COOPERAZIONE_PARTECIPANTI);
						sqlQueryObject.addInsertField("id_accordo_cooperazione", "?");
						sqlQueryObject.addInsertField("id_soggetto", "?");
						updateQuery = sqlQueryObject.createSQLInsert();
						updateStmt = con.prepareStatement(updateQuery);
	
						updateStmt.setLong(1, idAccordoCooperazione);
						updateStmt.setLong(2, idSoggettoPartecipante);
						
						n = updateStmt.executeUpdate();
						updateStmt.close();
						DriverRegistroServiziDB_LIB.log.debug("CRUDAccordoCooperazione (servizioComposto) type = " + type + " row affected =" + n);
					}
				}
				
				
				// Documenti generici servizio
				List<Documento> documenti = new ArrayList<Documento>();
				// Allegati
				for(int i=0; i<accordoCooperazione.sizeAllegatoList(); i++){
					Documento doc = accordoCooperazione.getAllegato(i);
					doc.setRuolo(RuoliDocumento.allegato.toString());
					documenti.add(doc);
				}
				// Specifiche Semiformali
				for(int i=0; i<accordoCooperazione.sizeSpecificaSemiformaleList(); i++){
					Documento doc = accordoCooperazione.getSpecificaSemiformale(i);
					doc.setRuolo(RuoliDocumento.specificaSemiformale.toString());
					documenti.add(doc);
				}
				// CRUD
				DriverRegistroServiziDB_LIB.CRUDDocumento(CostantiDB.CREATE, documenti, idAccordoCooperazione, ProprietariDocumento.accordoCooperazione, con, tipoDatabase);
				
				
				// ProtocolProperties
				DriverRegistroServiziDB_LIB.CRUDProtocolProperty(CostantiDB.CREATE, accordoCooperazione.getProtocolPropertyList(), 
						idAccordoCooperazione, ProprietariProtocolProperty.ACCORDO_COOPERAZIONE, con, tipoDatabase);
				

				break;

			case UPDATE:
				// UPDATE
				
				IDAccordoCooperazione idAccordoAttualeInseritoDB = null;
				if(accordoCooperazione.getOldIDAccordoForUpdate()!=null){
					idAccordoAttualeInseritoDB = accordoCooperazione.getOldIDAccordoForUpdate();
				}else{
					idAccordoAttualeInseritoDB = IDAccordoCooperazioneFactory.getInstance().getIDAccordoFromAccordo(accordoCooperazione);
				}
				
				long idAccordoLong = DBUtils.getIdAccordoCooperazione(idAccordoAttualeInseritoDB, con,DriverRegistroServiziDB_LIB.tipoDB);

				if (idAccordoLong <= 0)
					throw new DriverRegistroServiziException("Impossibile recuperare l'id dell'Accordo di Cooperazione : " + nome);
				
				
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
				sqlQueryObject.addUpdateTable(CostantiDB.ACCORDI_COOPERAZIONE);
				sqlQueryObject.addUpdateField("nome", "?");
				sqlQueryObject.addUpdateField("descrizione", "?");
				sqlQueryObject.addUpdateField("versione", "?");
				sqlQueryObject.addUpdateField("privato", "?");
				sqlQueryObject.addUpdateField("superuser", "?");
				sqlQueryObject.addUpdateField("id_referente", "?");
				if(stato!=null)
					sqlQueryObject.addUpdateField("stato", "?");
				if(accordoCooperazione.getOraRegistrazione()!=null)
					sqlQueryObject.addUpdateField("ora_registrazione", "?");
				sqlQueryObject.addWhereCondition("id=?");
				
				updateQuery = sqlQueryObject.createSQLUpdate();
				updateStmt = con.prepareStatement(updateQuery);
				
				updateStmt.setString(1, nome);
				updateStmt.setString(2, descrizione);
				
				updateStmt.setInt(3, accordoCooperazione.getVersione());
				
				if(privato)
					updateStmt.setInt(4, 1);
				else
					updateStmt.setInt(4, 0);
				updateStmt.setString(5, superUser);
				
				index = 6;
				
				if(accordoCooperazione.getSoggettoReferente()!=null) {
					long idSRef = DBUtils.getIdSoggetto(accordoCooperazione.getSoggettoReferente().getNome(), 
							accordoCooperazione.getSoggettoReferente().getTipo(), con, DriverRegistroServiziDB_LIB.tipoDB);
					updateStmt.setLong(index, idSRef);
				}else{
					updateStmt.setLong(index, CostantiRegistroServizi.SOGGETTO_REFERENTE_DEFAULT);
				}
				index++;
				
				if(stato!=null){
					updateStmt.setString(index, stato);
					index++;
				}
				
				if(accordoCooperazione.getOraRegistrazione()!=null){
					updateStmt.setTimestamp(index, new Timestamp(accordoCooperazione.getOraRegistrazione().getTime()));
					index++;
				}
				
				updateStmt.setLong(index, idAccordoLong);
				
				n = updateStmt.executeUpdate();
				updateStmt.close();
				DriverRegistroServiziDB_LIB.log.debug("CRUDAccordoCooperazione type = " + type + " row affected =" + n);
				
				
				/*
				// update servizi componenti, elimino i vecchi e riaggiungo i nuovi
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.ACCORDI_COOPERAZIONE_SERVIZI_COMPOSTI);
				sqlQueryObject.addWhereCondition("id_accordo_cooperazione=?");
				sqlQueryObject.setANDLogicOperator(true);
				updateQuery = sqlQueryObject.createSQLDelete();
				updateStmt = con.prepareStatement(updateQuery);
				updateStmt.setLong(1, idAccordoLong);
				updateStmt.executeUpdate();
				updateStmt.close();
				
				// aggiungo servizi componenti
				for(int i=0; i<accordoCooperazione.sizeServizioCompostoList(); i++){
					
					AccordoCooperazioneServizioComposto sComposto = accordoCooperazione.getServizioComposto(i);
					long idAccordoServizioComposto = sComposto.getIdAccordo();
					if(idAccordoServizioComposto <=0 ){
						// Provo a prenderlo attraverso la uri dell'accordo
						DriverRegistroServiziDB_LIB.log.debug("Provo a recuperare l'id dell'accordo di servizio con uri ["+sComposto.getNomeAccordoServizio()+"]");
						if(sComposto.getNomeAccordoServizio()!=null)
							idAccordoServizioComposto = DBUtils.getIdAccordoServizio(IDAccordo.getIDAccordoFromUri(sComposto.getNomeAccordoServizio()), con, DriverRegistroServiziDB_LIB.tipoDB);
					}
					if(idAccordoServizioComposto<=0)
						throw new DriverRegistroServiziException("[DriverRegistroServiziDB::CRUDAccordoCooperazione] idAccordoServizio composto non presente");
					
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
					sqlQueryObject.addInsertTable(CostantiDB.ACCORDI_COOPERAZIONE_SERVIZI_COMPOSTI);
					sqlQueryObject.addInsertField("id_accordo_cooperazione", "?");
					sqlQueryObject.addInsertField("id_accordo_servizio", "?");
					updateQuery = sqlQueryObject.createSQLInsert();
					updateStmt = con.prepareStatement(updateQuery);

					updateStmt.setLong(1, idAccordoLong);
					updateStmt.setLong(2, idAccordoServizioComposto);
					
					n = updateStmt.executeUpdate();
					updateStmt.close();
					DriverRegistroServiziDB_LIB.log.debug("CRUDAccordoCooperazione (Partecipante) type = " + type + " row affected =" + n);
				}*/
				
				
				// update partecipanti, elimino i vecchi e riaggiungo i nuovi
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.ACCORDI_COOPERAZIONE_PARTECIPANTI);
				sqlQueryObject.addWhereCondition("id_accordo_cooperazione=?");
				sqlQueryObject.setANDLogicOperator(true);
				updateQuery = sqlQueryObject.createSQLDelete();
				updateStmt = con.prepareStatement(updateQuery);
				updateStmt.setLong(1, idAccordoLong);
				updateStmt.executeUpdate();
				updateStmt.close();
				
				// aggiungo partecipanti
				if(accordoCooperazione.getElencoPartecipanti()!=null){
					AccordoCooperazionePartecipanti partecipanti = accordoCooperazione.getElencoPartecipanti();
					for(int i=0; i<partecipanti.sizeSoggettoPartecipanteList(); i++){
						
						IdSoggetto soggettoPartecipante = partecipanti.getSoggettoPartecipante(i);
					
						Long idSoggettoPartecipante = -1L;
						if(idSoggettoPartecipante==null || idSoggettoPartecipante <=0 ){
							// Provo a prenderlo attraverso il tipo/nome
							DriverRegistroServiziDB_LIB.log.debug("Provo a recuperare l'id del soggetto con tipo/nome ["+soggettoPartecipante.getTipo()+"]/["+soggettoPartecipante.getNome()+"]");
							if(soggettoPartecipante.getTipo()!=null && soggettoPartecipante.getNome()!=null)
								idSoggettoPartecipante = DBUtils.getIdSoggetto(soggettoPartecipante.getNome(),soggettoPartecipante.getTipo(), con, DriverRegistroServiziDB_LIB.tipoDB);
						}
						if(idSoggettoPartecipante<=0){
							idSoggettoPartecipante = soggettoPartecipante.getIdSoggetto();
						}
						if(idSoggettoPartecipante<=0)
							throw new DriverRegistroServiziException("[DriverRegistroServiziDB::CRUDAccordoCooperazione] idSoggettoPartecipante non presente");
						
						sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
						sqlQueryObject.addInsertTable(CostantiDB.ACCORDI_COOPERAZIONE_PARTECIPANTI);
						sqlQueryObject.addInsertField("id_accordo_cooperazione", "?");
						sqlQueryObject.addInsertField("id_soggetto", "?");
						updateQuery = sqlQueryObject.createSQLInsert();
						updateStmt = con.prepareStatement(updateQuery);
	
						updateStmt.setLong(1, idAccordoLong);
						updateStmt.setLong(2, idSoggettoPartecipante);
						
						n = updateStmt.executeUpdate();
						updateStmt.close();
						DriverRegistroServiziDB_LIB.log.debug("CRUDAccordoCooperazione (servizioComposto) type = " + type + " row affected =" + n);
					}
				}
				
				
				// Documenti generici servizio
				documenti = new ArrayList<Documento>();
				// Allegati
				for(int i=0; i<accordoCooperazione.sizeAllegatoList(); i++){
					Documento doc = accordoCooperazione.getAllegato(i);
					doc.setRuolo(RuoliDocumento.allegato.toString());
					documenti.add(doc);
				}
				// Specifiche Semiformali
				for(int i=0; i<accordoCooperazione.sizeSpecificaSemiformaleList(); i++){
					Documento doc = accordoCooperazione.getSpecificaSemiformale(i);
					doc.setRuolo(RuoliDocumento.specificaSemiformale.toString());
					documenti.add(doc);
				}
				// CRUD
				DriverRegistroServiziDB_LIB.CRUDDocumento(CostantiDB.UPDATE, documenti, idAccordoLong, ProprietariDocumento.accordoCooperazione, con,tipoDatabase);
				
				
				// ProtocolProperties
				DriverRegistroServiziDB_LIB.CRUDProtocolProperty(CostantiDB.UPDATE, accordoCooperazione.getProtocolPropertyList(), 
						idAccordoLong, ProprietariProtocolProperty.ACCORDO_COOPERAZIONE, con, tipoDatabase);
				
				
				
				break;

			case DELETE:
				// DELETE
				
				IDAccordoCooperazione idAccordo = IDAccordoCooperazioneFactory.getInstance().getIDAccordoFromValues(nome,soggettoReferente,accordoCooperazione.getVersione());
				idAccordoLong = DBUtils.getIdAccordoCooperazione(idAccordo, con, DriverRegistroServiziDB_LIB.tipoDB);
				if (idAccordoLong <= 0)
					throw new DriverRegistroServiziException("Impossibile recuperare l'id dell'Accordo di Cooperazione : " + nome);
				
				/*
				// delete servizi componenti
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.ACCORDI_COOPERAZIONE_SERVIZI_COMPOSTI);
				sqlQueryObject.addWhereCondition("id_accordo_cooperazione=?");
				sqlQueryObject.setANDLogicOperator(true);
				updateQuery = sqlQueryObject.createSQLDelete();
				updateStmt = con.prepareStatement(updateQuery);
				updateStmt.setLong(1, idAccordoLong);
				updateStmt.executeUpdate();
				updateStmt.close();
				*/
				
				// delete partecipanti
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.ACCORDI_COOPERAZIONE_PARTECIPANTI);
				sqlQueryObject.addWhereCondition("id_accordo_cooperazione=?");
				sqlQueryObject.setANDLogicOperator(true);
				updateQuery = sqlQueryObject.createSQLDelete();
				updateStmt = con.prepareStatement(updateQuery);
				updateStmt.setLong(1, idAccordoLong);
				updateStmt.executeUpdate();
				updateStmt.close();
				
				// Documenti generici accordo di servizio
				// Allegati
				// Specifiche Semiformali
				DriverRegistroServiziDB_LIB.CRUDDocumento(CostantiDB.DELETE, null, idAccordoLong, ProprietariDocumento.accordoCooperazione, con,tipoDatabase);
				
				
				// ProtocolProperties
				DriverRegistroServiziDB_LIB.CRUDProtocolProperty(CostantiDB.DELETE, null, 
						idAccordoLong, ProprietariProtocolProperty.ACCORDO_COOPERAZIONE, con, tipoDatabase);
				
				
				// delete Accordo di Cooperazione
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.ACCORDI_COOPERAZIONE);
				sqlQueryObject.addWhereCondition("id=?");
				sqlQueryObject.setANDLogicOperator(true);
				updateQuery = sqlQueryObject.createSQLDelete();
				updateStmt = con.prepareStatement(updateQuery);
				updateStmt.setLong(1, idAccordoLong);
				updateStmt.executeUpdate();
				updateStmt.close();

				break;
			}

		} catch (CoreException e) {
			throw new DriverRegistroServiziException(e);
		} catch (SQLException se) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDAccordoCooperazione] SQLException [" + se.getMessage() + "].", se);

		} catch (Exception se) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDAccordoCooperazione] Exception [" + se.getMessage() + "].", se);

		} finally {
			try {
				if(updateRS!=null) updateRS.close();
				if(updateStmt!=null)updateStmt.close();
			} catch (Exception ex) {/* ignore */}
		}

	}
	
	
	public static ISQLQueryObject getSQLRicercaAccordiValidi() throws Exception{
		ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
		
		sqlQueryObject.addFromTable(CostantiDB.ACCORDI);
		
		sqlQueryObject.addWhereExistsCondition(false, DriverRegistroServiziDB_LIB.getSQLRicercaServiziValidi(CostantiDB.ACCORDI+".id",false));
		
		return sqlQueryObject;
	}
	
	/**
	 * 
	 * @return Ritorna l' ISQLQueryObject con ? settato come id accordo, in modo da poterlo usare con il setParameter dello statement
	 * @throws Exception
	 */
	
	public static ISQLQueryObject getSQLRicercaServiziValidiByIdAccordo(boolean isErogazione) throws Exception{
		return DriverRegistroServiziDB_LIB.getSQLRicercaServiziValidi("?",isErogazione);
	}
	
	private static ISQLQueryObject getSQLRicercaServiziValidi(String idAccordo,boolean isErogazione) throws Exception{
		ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
		
		// select * from port_type where
		sqlQueryObject.addFromTable(CostantiDB.PORT_TYPE);
		// port_type.id_accordo=8 
		sqlQueryObject.addWhereCondition(CostantiDB.PORT_TYPE+".id_accordo="+idAccordo);
	    //  (EXISTS (select * from port_type_azioni where port_type_azioni.id_port_type=port_type.id))  
		ISQLQueryObject sqlQueryObjectExistsAlmenoUnAzione = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
		sqlQueryObjectExistsAlmenoUnAzione.addFromTable(CostantiDB.PORT_TYPE_AZIONI);
		sqlQueryObjectExistsAlmenoUnAzione.addWhereCondition(CostantiDB.PORT_TYPE_AZIONI+".id_port_type="+CostantiDB.PORT_TYPE+".id");
		sqlQueryObject.addWhereExistsCondition(false, sqlQueryObjectExistsAlmenoUnAzione);
		// Gestione profili di collaborazione
		ISQLQueryObject sqlQueryServiziValidi = DriverRegistroServiziDB_LIB.getSQLIndividuazioneServiziValidi(DriverRegistroServiziDB_LIB.tipoDB,isErogazione);
		sqlQueryObject.addWhereCondition("( "+sqlQueryServiziValidi.createSQLConditions()+" )");
		// And tra le condizioni
		sqlQueryObject.setANDLogicOperator(true);
		return sqlQueryObject;
	}
	
	public static ISQLQueryObject getSQLIndividuazioneServiziValidi(String tipoDatabase,boolean isErogazione) throws Exception{
		ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDatabase);
		
		
		// (port_type.profilo_collaborazione='oneway') 
		sqlQueryObject.addWhereCondition(CostantiDB.PORT_TYPE+".profilo_collaborazione='oneway'");
		
		
		// (port_type.profilo_collaborazione='sincrono') 
		sqlQueryObject.addWhereCondition(CostantiDB.PORT_TYPE+".profilo_collaborazione='sincrono'");
		
		
		// (      
		//    port_type.profilo_collaborazione='asincronoAsimmetrico' AND 
		//        
		//    EXISTS (select * from port_type_azioni where port_type_azioni.id_port_type=port_type.id AND 
		//                                                 port_type_azioni.correlata_servizio is null AND
		//                                                 port_type_azioni.correlata is null
		//            ) AND
		//
		//    EXISTS (select * from port_type_azioni where port_type_azioni.id_port_type=port_type.id AND 
		//                                                 port_type_azioni.correlata_servizio is not null AND
		//                                                 port_type_azioni.correlata_servizio=port_type.nome AND
		//                                                 port_type_azioni.correlata is not null AND
		//                                                 port_type_azioni.correlata IN (
		//                                                      select nome from port_type_azioni where port_type_azioni.id_port_type=port_type.id)
		//                                                 )
		//           )
		// )
		ISQLQueryObject sqlQueryObjectExistsAsinAsimRichiesta = SQLObjectFactory.createSQLQueryObject(tipoDatabase);
		sqlQueryObjectExistsAsinAsimRichiesta.addFromTable(CostantiDB.PORT_TYPE_AZIONI);
		sqlQueryObjectExistsAsinAsimRichiesta.addWhereCondition(CostantiDB.PORT_TYPE_AZIONI+".id_port_type="+CostantiDB.PORT_TYPE+".id");
		sqlQueryObjectExistsAsinAsimRichiesta.addWhereCondition(CostantiDB.PORT_TYPE_AZIONI+".correlata_servizio is null");
		sqlQueryObjectExistsAsinAsimRichiesta.addWhereCondition(CostantiDB.PORT_TYPE_AZIONI+".correlata is null");
		sqlQueryObjectExistsAsinAsimRichiesta.setANDLogicOperator(true);
		
		ISQLQueryObject sqlQueryObjectExistsAsinAsimRisposta = SQLObjectFactory.createSQLQueryObject(tipoDatabase);
		sqlQueryObjectExistsAsinAsimRisposta.addFromTable(CostantiDB.PORT_TYPE_AZIONI);
		sqlQueryObjectExistsAsinAsimRisposta.addWhereCondition(CostantiDB.PORT_TYPE_AZIONI+".id_port_type="+CostantiDB.PORT_TYPE+".id");
		sqlQueryObjectExistsAsinAsimRisposta.addWhereCondition(CostantiDB.PORT_TYPE_AZIONI+".correlata_servizio is not null");
		sqlQueryObjectExistsAsinAsimRisposta.addWhereCondition(CostantiDB.PORT_TYPE_AZIONI+".correlata_servizio="+CostantiDB.PORT_TYPE+".nome");
		sqlQueryObjectExistsAsinAsimRisposta.addWhereCondition(CostantiDB.PORT_TYPE_AZIONI+".correlata is not null");
		ISQLQueryObject sqlQueryObjectExistsAsinAsimRisposta_checkCorrelata = SQLObjectFactory.createSQLQueryObject(tipoDatabase);
		sqlQueryObjectExistsAsinAsimRisposta_checkCorrelata.addFromTable(CostantiDB.PORT_TYPE_AZIONI);
		sqlQueryObjectExistsAsinAsimRisposta_checkCorrelata.addSelectField(CostantiDB.PORT_TYPE_AZIONI, "nome");
		sqlQueryObjectExistsAsinAsimRisposta_checkCorrelata.addWhereCondition(CostantiDB.PORT_TYPE_AZIONI+".id_port_type="+CostantiDB.PORT_TYPE+".id");
		sqlQueryObjectExistsAsinAsimRisposta.addWhereINSelectSQLCondition(false, CostantiDB.PORT_TYPE_AZIONI+".correlata", sqlQueryObjectExistsAsinAsimRisposta_checkCorrelata);
		sqlQueryObjectExistsAsinAsimRisposta.setANDLogicOperator(true);
	
		sqlQueryObject.addWhereCondition(true, 
				CostantiDB.PORT_TYPE+".profilo_collaborazione='asincronoAsimmetrico'",
				sqlQueryObject.getWhereExistsCondition(false, sqlQueryObjectExistsAsinAsimRichiesta),
				sqlQueryObject.getWhereExistsCondition(false,sqlQueryObjectExistsAsinAsimRisposta));	
		
		
		
		// (
		//   port_type.profilo_collaborazione='asincronoSimmetrico' AND
		//
        //   EXISTS (select * from port_type_azioni where port_type_azioni.id_port_type=port_type.id AND 
   	  	//	   	                                         port_type_azioni.correlata is null AND 
		//                                               port_type_azioni.correlata_servizio is null
		// 			) AND
		//
        //   EXISTS (select * from port_type as ptRicerca2,port_type_azioni where 
        //                                    ptRicerca2.id_accordo=port_type.id_accordo AND 
        //                                    ptRicerca2.profilo_collaborazione='asincronoSimmetrico' AND
		//									  ptRicerca2.nome <> port_type.nome AND
        //                                    port_type_azioni.id_port_type=ptRicerca2.id AND
		//									  port_type_azioni.correlata_servizio is not null AND
        //                                    port_type_azioni.correlata_servizio=port_type.nome AND
		//	                                  port_type_azioni.correlata is not null AND
		//									  port_type_azioni.correlata IN (
		//										  select nome from port_type_azioni where port_type_azioni.id_port_type=port_type.id
		//									  )
		//           )
		// )
		
		ISQLQueryObject sqlQueryObjectExistsAsinSimRichiesta = SQLObjectFactory.createSQLQueryObject(tipoDatabase);
		sqlQueryObjectExistsAsinSimRichiesta.addFromTable(CostantiDB.PORT_TYPE_AZIONI);
		sqlQueryObjectExistsAsinSimRichiesta.addWhereCondition(CostantiDB.PORT_TYPE_AZIONI+".id_port_type="+CostantiDB.PORT_TYPE+".id");
		sqlQueryObjectExistsAsinSimRichiesta.addWhereCondition(CostantiDB.PORT_TYPE_AZIONI+".correlata_servizio is null");
		sqlQueryObjectExistsAsinSimRichiesta.addWhereCondition(CostantiDB.PORT_TYPE_AZIONI+".correlata is null");
		sqlQueryObjectExistsAsinSimRichiesta.setANDLogicOperator(true);
		
		ISQLQueryObject sqlQueryObjectExistsAsinSimRichiesta_checkCorrelazione = SQLObjectFactory.createSQLQueryObject(tipoDatabase);
		String PORT_TYPE_ALIAS_RICERCA = "ptRicerca2";
		sqlQueryObjectExistsAsinSimRichiesta_checkCorrelazione.addFromTable(CostantiDB.PORT_TYPE+" as "+PORT_TYPE_ALIAS_RICERCA);
		sqlQueryObjectExistsAsinSimRichiesta_checkCorrelazione.addFromTable(CostantiDB.PORT_TYPE_AZIONI);
		sqlQueryObjectExistsAsinSimRichiesta_checkCorrelazione.addWhereCondition(PORT_TYPE_ALIAS_RICERCA+".id_accordo="+CostantiDB.PORT_TYPE+".id_accordo");
		sqlQueryObjectExistsAsinSimRichiesta_checkCorrelazione.addWhereCondition(PORT_TYPE_ALIAS_RICERCA+".profilo_collaborazione='asincronoSimmetrico'");
		sqlQueryObjectExistsAsinSimRichiesta_checkCorrelazione.addWhereCondition(PORT_TYPE_ALIAS_RICERCA+".nome <> "+CostantiDB.PORT_TYPE+".nome");
		sqlQueryObjectExistsAsinSimRichiesta_checkCorrelazione.addWhereCondition(CostantiDB.PORT_TYPE_AZIONI+".id_port_type="+PORT_TYPE_ALIAS_RICERCA+".id");
		sqlQueryObjectExistsAsinSimRichiesta_checkCorrelazione.addWhereCondition(CostantiDB.PORT_TYPE_AZIONI+".correlata_servizio is not null");
		sqlQueryObjectExistsAsinSimRichiesta_checkCorrelazione.addWhereCondition(CostantiDB.PORT_TYPE_AZIONI+".correlata_servizio="+CostantiDB.PORT_TYPE+".nome");
		sqlQueryObjectExistsAsinSimRichiesta_checkCorrelazione.addWhereCondition(CostantiDB.PORT_TYPE_AZIONI+".correlata is not null");
		ISQLQueryObject sqlQueryObjectExistsAsinSimRichiesta_checkCorrelazione_checkAzione = SQLObjectFactory.createSQLQueryObject(tipoDatabase);
		sqlQueryObjectExistsAsinSimRichiesta_checkCorrelazione_checkAzione.addFromTable(CostantiDB.PORT_TYPE_AZIONI);
		sqlQueryObjectExistsAsinSimRichiesta_checkCorrelazione_checkAzione.addSelectField(CostantiDB.PORT_TYPE_AZIONI, "nome");
		sqlQueryObjectExistsAsinSimRichiesta_checkCorrelazione_checkAzione.addWhereCondition(CostantiDB.PORT_TYPE_AZIONI+".id_port_type="+CostantiDB.PORT_TYPE+".id");
		sqlQueryObjectExistsAsinSimRichiesta_checkCorrelazione.addWhereINSelectSQLCondition(false, CostantiDB.PORT_TYPE_AZIONI+".correlata", sqlQueryObjectExistsAsinSimRichiesta_checkCorrelazione_checkAzione);
		sqlQueryObjectExistsAsinSimRichiesta_checkCorrelazione.setANDLogicOperator(true);
			
		sqlQueryObject.addWhereCondition(true, 
				CostantiDB.PORT_TYPE+".profilo_collaborazione='asincronoSimmetrico'",
				sqlQueryObject.getWhereExistsCondition(false, sqlQueryObjectExistsAsinSimRichiesta),
				sqlQueryObject.getWhereExistsCondition(false,sqlQueryObjectExistsAsinSimRichiesta_checkCorrelazione));
		
		
		
		// (
		//    port_type.profilo_collaborazione='asincronoSimmetrico' AND
		//
        //    EXISTS (select * from port_type_azioni where port_type_azioni.id_port_type=port_type.id AND 
	    //	   	     	  		   	                       port_type_azioni.correlata is not null AND 
		//					 							   port_type_azioni.correlata_servizio is not null AND
		//					                               EXISTS (select * from port_type as ptRicerca2,port_type_azioni ptAzioniRicerca2 where 
		//					 										ptRicerca2.id_accordo=port_type.id_accordo AND 
        //                                                          ptRicerca2.profilo_collaborazione='asincronoSimmetrico' AND
		//									  						ptRicerca2.nome <> port_type.nome AND
        //                                                          ptAzioniRicerca2.id_port_type=ptRicerca2.id AND
		//															ptAzioniRicerca2.correlata_servizio is null AND
		//															ptRicerca2.nome=port_type_azioni.correlata_servizio AND
		//															ptAzioniRicerca2.correlata is null AND
		//															ptAzioniRicerca2.nome=port_type_azioni.correlata
		//   					 							)
        //           ) 
        // )
		if(isErogazione==false){
			ISQLQueryObject sqlQueryObjectExistsAsinSimRisposta_checkCorrelazione = SQLObjectFactory.createSQLQueryObject(tipoDatabase);
			String PORT_TYPE_ALIAS_RICERCA_AS = "ptRicerca2";
			String PORT_TYPE_AZIONI_ALIAS_RICERCA_AS = "ptAzioniRicerca2";
			sqlQueryObjectExistsAsinSimRisposta_checkCorrelazione.addFromTable(CostantiDB.PORT_TYPE+" as "+PORT_TYPE_ALIAS_RICERCA_AS);
			sqlQueryObjectExistsAsinSimRisposta_checkCorrelazione.addFromTable(CostantiDB.PORT_TYPE_AZIONI+" as "+PORT_TYPE_AZIONI_ALIAS_RICERCA_AS);
			sqlQueryObjectExistsAsinSimRisposta_checkCorrelazione.addWhereCondition(PORT_TYPE_ALIAS_RICERCA_AS+".id_accordo="+CostantiDB.PORT_TYPE+".id_accordo");
			sqlQueryObjectExistsAsinSimRisposta_checkCorrelazione.addWhereCondition(PORT_TYPE_ALIAS_RICERCA_AS+".profilo_collaborazione='asincronoSimmetrico'");
			sqlQueryObjectExistsAsinSimRisposta_checkCorrelazione.addWhereCondition(PORT_TYPE_ALIAS_RICERCA_AS+".nome <> "+CostantiDB.PORT_TYPE+".nome");
			sqlQueryObjectExistsAsinSimRisposta_checkCorrelazione.addWhereCondition(PORT_TYPE_AZIONI_ALIAS_RICERCA_AS+".id_port_type="+PORT_TYPE_ALIAS_RICERCA_AS+".id");
			sqlQueryObjectExistsAsinSimRisposta_checkCorrelazione.addWhereCondition(PORT_TYPE_AZIONI_ALIAS_RICERCA_AS+".correlata_servizio is null");
			sqlQueryObjectExistsAsinSimRisposta_checkCorrelazione.addWhereCondition(PORT_TYPE_ALIAS_RICERCA_AS+".nome="+CostantiDB.PORT_TYPE_AZIONI+".correlata_servizio");
			sqlQueryObjectExistsAsinSimRisposta_checkCorrelazione.addWhereCondition(PORT_TYPE_AZIONI_ALIAS_RICERCA_AS+".correlata is null");
			sqlQueryObjectExistsAsinSimRisposta_checkCorrelazione.addWhereCondition(PORT_TYPE_AZIONI_ALIAS_RICERCA_AS+".nome="+CostantiDB.PORT_TYPE_AZIONI+".correlata");
			
			sqlQueryObjectExistsAsinSimRisposta_checkCorrelazione.setANDLogicOperator(true);
			
			ISQLQueryObject sqlQueryObjectExistsAsinSimRisposta = SQLObjectFactory.createSQLQueryObject(tipoDatabase);
			sqlQueryObjectExistsAsinSimRisposta.addFromTable(CostantiDB.PORT_TYPE_AZIONI);
			sqlQueryObjectExistsAsinSimRisposta.addWhereCondition(CostantiDB.PORT_TYPE_AZIONI+".id_port_type="+CostantiDB.PORT_TYPE+".id");
			sqlQueryObjectExistsAsinSimRisposta.addWhereCondition(CostantiDB.PORT_TYPE_AZIONI+".correlata is not null");
			sqlQueryObjectExistsAsinSimRisposta.addWhereCondition(CostantiDB.PORT_TYPE_AZIONI+".correlata_servizio is not null");
			sqlQueryObjectExistsAsinSimRisposta.addWhereExistsCondition(false, sqlQueryObjectExistsAsinSimRisposta_checkCorrelazione);
			sqlQueryObjectExistsAsinSimRisposta.setANDLogicOperator(true);
			
			sqlQueryObject.addWhereCondition(true, 
					CostantiDB.PORT_TYPE+".profilo_collaborazione='asincronoSimmetrico'",
					sqlQueryObject.getWhereExistsCondition(false, sqlQueryObjectExistsAsinSimRisposta));
		}
				
		
		// OR tra le condizioni
		sqlQueryObject.setANDLogicOperator(false);
		
		return sqlQueryObject;   
		     
	}
	
	

	
	
	
	

	public static void CRUDProtocolProperty(int type, List<ProtocolProperty> listPP, long idProprietario,
			ProprietariProtocolProperty tipologiaProprietarioProtocolProperty, Connection connection,
			String tipoDatabase) throws DriverRegistroServiziException {
		
		// NOTA: l'update dei documenti, essendo mega di documenti non puo' essere implementata come delete + create
		
		PreparedStatement stm = null;
		ResultSet rs=null;
		String sqlQuery;

		if((listPP == null) && (type!= CostantiDB.DELETE)) 
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDProtocolProperty] L'oggetto listPP non puo essere null");
		if(idProprietario <= 0 ) 
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDProtocolProperty] id proprietario non definito");
		
		IJDBCAdapter jdbcAdapter = null;
		
		try {

			jdbcAdapter = JDBCAdapterFactory.createJDBCAdapter(tipoDatabase);
			
			switch (type) {
			case CREATE:

				for(int i=0; i<listPP.size(); i++){
				
					ProtocolProperty protocolProperty = listPP.get(i);
					if(protocolProperty.getName()==null || "".equals(protocolProperty.getName()))
						throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDProtocolProperty] Nome non definito per protocolProperty ["+i+"]");
					
					// Aggiorno proprietari
					protocolProperty.setIdProprietario(idProprietario);
					protocolProperty.setTipoProprietario(tipologiaProprietarioProtocolProperty.name());
					
					int contenutiDefiniti = 0;
					
					boolean stringValue = protocolProperty.getValue()!=null && !"".equals(protocolProperty.getValue());
					String contenutoString = null;
					if(stringValue){
						contenutiDefiniti++;
						contenutoString = protocolProperty.getValue();
					}
					
					boolean numberValue = protocolProperty.getNumberValue()!=null;
					Long contenutoNumber = null;
					if(numberValue){
						contenutiDefiniti++;
						contenutoNumber = protocolProperty.getNumberValue();
					}
					
					boolean booleanValue = protocolProperty.getBooleanValue()!=null;
					Boolean contenutoBoolean = null;
					if(booleanValue){
						contenutiDefiniti++;
						contenutoBoolean = protocolProperty.getBooleanValue();
					}
					
					boolean binaryValue = protocolProperty.getByteFile()!=null && protocolProperty.getByteFile().length>0;
					byte[] contenutoBinario = null; 
					String contenutoBinarioFileName = null;
					if(binaryValue){
						contenutiDefiniti++;
						contenutoBinario = protocolProperty.getByteFile();
						if(contenutoBinario.length<3){
							String test = new String(contenutoBinario);
							if("".equals(test.trim().replaceAll("\n", ""))){
								// eliminare \n\n
								contenutoBinario = null;	
								binaryValue = false;
								contenutiDefiniti--;
							}
						}
						if(binaryValue){
							contenutoBinarioFileName = protocolProperty.getFile();
						}
					}
					
					if(!stringValue && !numberValue && !binaryValue && !booleanValue){
						throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDProtocolProperty] Contenuto non definito per protocolProperty ["+protocolProperty.getName()+"]");
					}
					if(contenutiDefiniti>1){
						throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDProtocolProperty] Contenuto definito con più tipologie per protocolProperty ["+protocolProperty.getName()+
								"] (string:"+stringValue+" number:"+numberValue+" binary:"+binaryValue+")");
					}
					
					
					// create
					ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
					sqlQueryObject.addInsertTable(CostantiDB.PROTOCOL_PROPERTIES);
					sqlQueryObject.addInsertField("tipo_proprietario", "?");
					sqlQueryObject.addInsertField("id_proprietario", "?");
					sqlQueryObject.addInsertField("name", "?");
					if(stringValue){
						sqlQueryObject.addInsertField("value_string", "?");
					}
					if(numberValue){
						sqlQueryObject.addInsertField("value_number", "?");
					}
					if(booleanValue){
						sqlQueryObject.addInsertField("value_boolean", "?");
					}
					if(binaryValue){
						sqlQueryObject.addInsertField("value_binary", "?");
						sqlQueryObject.addInsertField("file_name", "?");
					}
					sqlQuery = sqlQueryObject.createSQLInsert();
					stm = connection.prepareStatement(sqlQuery);
					int index = 1;
					stm.setString(index++, tipologiaProprietarioProtocolProperty.name());
					stm.setLong(index++, idProprietario);
					stm.setString(index++, protocolProperty.getName());
					String debug = null;
					if(stringValue){
						stm.setString(index++, contenutoString);
						debug = contenutoString;
					}
					if(numberValue){
						stm.setLong(index++, contenutoNumber);
						debug = contenutoNumber+"";
					}
					if(booleanValue){
						if(contenutoBoolean){
							stm.setInt(index++,CostantiDB.TRUE);
							debug = CostantiDB.TRUE+"";
						}
						else{
							stm.setInt(index++,CostantiDB.FALSE);
							debug = CostantiDB.FALSE+"";
						}
					}
					if(binaryValue){
						jdbcAdapter.setBinaryData(stm,index++,contenutoBinario);
						debug = "BinaryData";
						stm.setString(index++, contenutoBinarioFileName);
						debug = debug + "," + contenutoBinarioFileName;
					}
					
					DriverRegistroServiziDB_LIB.log.debug("CRUDProtocolProperty CREATE : \n" + DBUtils.
							formatSQLString(sqlQuery, tipologiaProprietarioProtocolProperty.name(), idProprietario, protocolProperty.getName(), debug));
	
					int n = stm.executeUpdate();
					stm.close();
					DriverRegistroServiziDB_LIB.log.debug("Inserted " + n + " row(s)");
		
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
					sqlQueryObject.addFromTable(CostantiDB.PROTOCOL_PROPERTIES);
					sqlQueryObject.addSelectField("id");
					sqlQueryObject.addWhereCondition("tipo_proprietario = ?");
					sqlQueryObject.addWhereCondition("id_proprietario = ?");
					sqlQueryObject.addWhereCondition("name = ?");
					sqlQueryObject.setANDLogicOperator(true);
					sqlQuery = sqlQueryObject.createSQLQuery();
					stm = connection.prepareStatement(sqlQuery);
					index = 1;
					stm.setString(index++, tipologiaProprietarioProtocolProperty.name());
					stm.setLong(index++, idProprietario);
					stm.setString(index++, protocolProperty.getName());
	
					DriverRegistroServiziDB_LIB.log.debug("Recupero id inserito : \n" + DBUtils.
							formatSQLString(sqlQuery, tipologiaProprietarioProtocolProperty.name(), idProprietario, protocolProperty.getName()));
	
					rs = stm.executeQuery();
	
					if (rs.next()) {
						listPP.get(i).setId(rs.getLong("id"));
					} else {
						throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDProtocolProperty] Errore avvenuto durante il recupero dell'id dopo una create");
					}
	
					rs.close();
					stm.close();
					
				}
				break;

			case UPDATE:
				
				// Prelevo vecchia lista
				List<ProtocolProperty> oldLista = null;
				try{
					oldLista = DriverRegistroServiziDB_LIB.getListaProtocolProperty(idProprietario,tipologiaProprietarioProtocolProperty, connection, tipoDatabase);
				}catch(DriverRegistroServiziNotFound dNotFound){
					oldLista = new ArrayList<ProtocolProperty>();
				}
				
				// Gestico la nuova immagine
				for(int i=0; i<listPP.size(); i++){
					
					ProtocolProperty protocolProperty = listPP.get(i);
					if(protocolProperty.getName()==null || "".equals(protocolProperty.getName()))
						throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDProtocolProperty] Nome non definito per protocolProperty ["+i+"]");
					
					// Aggiorno proprietari
					protocolProperty.setIdProprietario(idProprietario);
					protocolProperty.setTipoProprietario(tipologiaProprietarioProtocolProperty.name());
					
					int contenutiDefiniti = 0;
					
					boolean stringValue = protocolProperty.getValue()!=null && !"".equals(protocolProperty.getValue());
					String contenutoString = null;
					if(stringValue){
						contenutiDefiniti++;
						contenutoString = protocolProperty.getValue();
					}
					
					boolean numberValue = protocolProperty.getNumberValue()!=null;
					Long contenutoNumber = null;
					if(numberValue){
						contenutiDefiniti++;
						contenutoNumber = protocolProperty.getNumberValue();
					}
					
					boolean booleanValue = protocolProperty.getBooleanValue()!=null;
					Boolean contenutoBoolean = null;
					if(booleanValue){
						contenutiDefiniti++;
						contenutoBoolean = protocolProperty.getBooleanValue();
					}
					
					boolean binaryValue = protocolProperty.getByteFile()!=null && protocolProperty.getByteFile().length>0;
					byte[] contenutoBinario = null; 
					String contenutoBinarioFileName = null;
					if(binaryValue){
						contenutiDefiniti++;
						contenutoBinario = protocolProperty.getByteFile();
						if(contenutoBinario.length<3){
							String test = new String(contenutoBinario);
							if("".equals(test.trim().replaceAll("\n", ""))){
								// eliminare \n\n
								contenutoBinario = null;	
								binaryValue = false;
								contenutiDefiniti--;
							}
						}
						if(binaryValue){
							contenutoBinarioFileName = protocolProperty.getFile();
						}
					}
					
					if(!stringValue && !numberValue && !binaryValue && !booleanValue){
						throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDProtocolProperty] Contenuto non definito per protocolProperty ["+protocolProperty.getName()+"]");
					}
					if(contenutiDefiniti>1){
						throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDProtocolProperty] Contenuto definito con più tipologie per protocolProperty ["+protocolProperty.getName()+
								"] (string:"+stringValue+" number:"+numberValue+" binary:"+binaryValue+")");
					}
					
									
					//if(doc.getId()<=0){
					// Rileggo sempre id, puo' essere diverso (es. importato tramite sincronizzazioni)
					protocolProperty.setId(DBUtils.getIdProtocolProperty(protocolProperty.getTipoProprietario(), idProprietario,protocolProperty.getName(), 
							connection, 
							DriverRegistroServiziDB_LIB.tipoDB));
										
					boolean ppGiaPresente = false;
					boolean ppDaAggiornare = false;
					if(protocolProperty.getId()>0){
						for(int j=0; j<oldLista.size(); j++){
							ProtocolProperty old = oldLista.get(j);
		
							//System.out.println("OLD["+old.getId().longValue()+"]==ATTUALE["+doc.getId().longValue()+"] ("+((doc.getId().longValue() == old.getId().longValue()))+")");
							if(protocolProperty.getId().longValue() == old.getId().longValue()){		
									ppGiaPresente = true; // non devo fare una insert, ma una update...
										
									// rimuovo la vecchia immagine del documento dalla lista dei doc vecchi
									oldLista.remove(j);
									
									ppDaAggiornare = true;
							}
						}
					}

					if(ppGiaPresente){
						if(ppDaAggiornare){
							// update
							long idPP = protocolProperty.getId();
							if(idPP<=0){
								throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDProtocolProperty] ID non definito per documento da aggiorare ["+protocolProperty.getName()+"]");
							}
							ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
							sqlQueryObject.addUpdateTable(CostantiDB.PROTOCOL_PROPERTIES);
							sqlQueryObject.addUpdateField("value_string", "?");
							sqlQueryObject.addUpdateField("value_number", "?");
							sqlQueryObject.addUpdateField("value_boolean", "?");
							sqlQueryObject.addUpdateField("value_binary", "?");
							sqlQueryObject.addUpdateField("file_name", "?");
							sqlQueryObject.addWhereCondition("id=?");
							sqlQuery = sqlQueryObject.createSQLUpdate();
							stm = connection.prepareStatement(sqlQuery);
							int index = 1;
							
							stm.setString(index++, contenutoString);
							
							if(numberValue){
								stm.setLong(index++, contenutoNumber);
							}
							else{
								stm.setNull(index++, java.sql.Types.BIGINT);
							}
							
							if(booleanValue){
								if(contenutoBoolean){
									stm.setInt(index++,CostantiDB.TRUE);
								}
								else{
									stm.setInt(index++,CostantiDB.FALSE);
								}
							}
							else{
								stm.setNull(index++, java.sql.Types.INTEGER);
							}
							
							jdbcAdapter.setBinaryData(stm,index++,contenutoBinario);
							stm.setString(index++, contenutoBinarioFileName);
							
							stm.setLong(index++, idPP);
							stm.executeUpdate();
							stm.close();
						}
					}else{
						// create
						ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
						sqlQueryObject.addInsertTable(CostantiDB.PROTOCOL_PROPERTIES);
						sqlQueryObject.addInsertField("tipo_proprietario", "?");
						sqlQueryObject.addInsertField("id_proprietario", "?");
						sqlQueryObject.addInsertField("name", "?");
						if(stringValue){
							sqlQueryObject.addInsertField("value_string", "?");
						}
						if(numberValue){
							sqlQueryObject.addInsertField("value_number", "?");
						}
						if(booleanValue){
							sqlQueryObject.addInsertField("value_boolean", "?");
						}
						if(binaryValue){
							sqlQueryObject.addInsertField("value_binary", "?");
							sqlQueryObject.addInsertField("file_name", "?");
						}
						sqlQuery = sqlQueryObject.createSQLInsert();
						stm = connection.prepareStatement(sqlQuery);
						int index = 1;
						stm.setString(index++, tipologiaProprietarioProtocolProperty.name());
						stm.setLong(index++, idProprietario);
						stm.setString(index++, protocolProperty.getName());
						String debug = null;
						if(stringValue){
							stm.setString(index++, contenutoString);
							debug = contenutoString;
						}
						if(numberValue){
							stm.setLong(index++, contenutoNumber);
							debug = contenutoNumber+"";
						}
						if(booleanValue){
							if(contenutoBoolean){
								stm.setInt(index++,CostantiDB.TRUE);
								debug = CostantiDB.TRUE+"";
							}
							else{
								stm.setInt(index++,CostantiDB.FALSE);
								debug = CostantiDB.FALSE+"";
							}
						}
						if(binaryValue){
							jdbcAdapter.setBinaryData(stm,index++,contenutoBinario);
							debug = "BinaryData";
							stm.setString(index++, contenutoBinarioFileName);
							debug = debug + "," + contenutoBinarioFileName;
						}
						
						DriverRegistroServiziDB_LIB.log.debug("CRUDProtocolProperty CREATE : \n" + DBUtils.
								formatSQLString(sqlQuery, tipologiaProprietarioProtocolProperty.name(), idProprietario, protocolProperty.getName(), debug));
		
						int n = stm.executeUpdate();
						stm.close();
						DriverRegistroServiziDB_LIB.log.debug("Inserted " + n + " row(s)");
			
						sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
						sqlQueryObject.addFromTable(CostantiDB.PROTOCOL_PROPERTIES);
						sqlQueryObject.addSelectField("id");
						sqlQueryObject.addWhereCondition("tipo_proprietario = ?");
						sqlQueryObject.addWhereCondition("id_proprietario = ?");
						sqlQueryObject.addWhereCondition("name = ?");
						sqlQueryObject.setANDLogicOperator(true);
						sqlQuery = sqlQueryObject.createSQLQuery();
						stm = connection.prepareStatement(sqlQuery);
						index = 1;
						stm.setString(index++, tipologiaProprietarioProtocolProperty.name());
						stm.setLong(index++, idProprietario);
						stm.setString(index++, protocolProperty.getName());
		
						DriverRegistroServiziDB_LIB.log.debug("Recupero id inserito : \n" + DBUtils.
								formatSQLString(sqlQuery, tipologiaProprietarioProtocolProperty.name(), idProprietario, protocolProperty.getName()));
		
						rs = stm.executeQuery();
		
						if (rs.next()) {
							listPP.get(i).setId(rs.getLong("id"));
						} else {
							throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDProtocolProperty] Errore avvenuto durante il recupero dell'id dopo una create");
						}
		
						rs.close();
						stm.close();
						
					}
					
				}
				
				if(oldLista.size()>0){
					// Qualche documento e' stato cancellato.
					// Non e' piu' presente.
					for(int j=0; j<oldLista.size(); j++){
						ProtocolProperty old = oldLista.get(j);
						
						ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
						sqlQueryObject.addDeleteTable(CostantiDB.PROTOCOL_PROPERTIES);
						sqlQueryObject.addWhereCondition("id=?");
						sqlQuery = sqlQueryObject.createSQLDelete();
						stm = connection.prepareStatement(sqlQuery);
						stm.setLong(1, old.getId());
						stm.executeUpdate();
						stm.close();
					}
				}
				
				break;

			case DELETE:
				// delete
				
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PROTOCOL_PROPERTIES);
				sqlQueryObject.addWhereCondition("tipo_proprietario = ?");
				sqlQueryObject.addWhereCondition("id_proprietario=?");
				sqlQueryObject.setANDLogicOperator(true);
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = connection.prepareStatement(sqlQuery);
				stm.setString(1, tipologiaProprietarioProtocolProperty.name());
				stm.setLong(2, idProprietario);
				stm.executeUpdate();
				stm.close();

				DriverRegistroServiziDB_LIB.log.debug("CRUDDocumento DELETE : \n" + DBUtils.formatSQLString(sqlQuery, tipologiaProprietarioProtocolProperty.name(), idProprietario));

				break;
			}

		} catch (SQLException se) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDDocumento] SQLException : " + se.getMessage(),se);
		}catch (Exception se) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDDocumento] Exception : " + se.getMessage(),se);
		} finally {
			try {
				if(rs!=null) rs.close();
				if(stm!=null) stm.close();
			} catch (Exception e) {
			}
		}
	}
	
	
	public static List<ProtocolProperty> getListaProtocolProperty(long idProprietario, ProprietariProtocolProperty tipologiaProprietario, 
			Connection connection,
			String tipoDatabase) throws DriverRegistroServiziException,DriverRegistroServiziNotFound {
		
		PreparedStatement stm = null;
		ResultSet rs=null;
		String sqlQuery;

		if(idProprietario <= 0 ) 
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::getListaProtocolProperty] id proprietario non definito");
		
		try {
		
			List<ProtocolProperty> listPP = new ArrayList<ProtocolProperty>();
			
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PROTOCOL_PROPERTIES);
			sqlQueryObject.addSelectField("id");
			sqlQueryObject.addWhereCondition("tipo_proprietario = ?");
			sqlQueryObject.addWhereCondition("id_proprietario = ?");
			sqlQueryObject.setANDLogicOperator(true);
			sqlQueryObject.setANDLogicOperator(true);
			sqlQuery = sqlQueryObject.createSQLQuery();
			stm = connection.prepareStatement(sqlQuery);
			stm.setString(1, tipologiaProprietario.name());
			stm.setLong(2, idProprietario);
			rs = stm.executeQuery();
			
			while(rs.next()){
				ProtocolProperty pp = DriverRegistroServiziDB_LIB.getProtocolProperty(rs.getLong("id"), connection,tipoDatabase); 
				listPP.add(pp);
			}
			
			if(listPP.size()<=0)
				throw new DriverRegistroServiziNotFound("ProtocolProperty con tipologiaProprietario["+tipologiaProprietario.name()+
						"] e idProprietario["+idProprietario+"] non trovati");
			
			return listPP;

		} catch (SQLException se) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::getListaProtocolProperty] SQLException : " + se.getMessage(),se);
		}catch (DriverRegistroServiziNotFound dnf) {
			throw dnf;
		}catch (Exception se) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::getListaProtocolProperty] Exception : " + se.getMessage(),se);
		} finally {
			try {
				if(rs!=null) rs.close();
				if(stm!=null) stm.close();
			} catch (Exception e) {
			}
		}
	}
	
	public static ProtocolProperty getProtocolProperty(long id, Connection connection, String tipoDatabase) throws DriverRegistroServiziException,DriverRegistroServiziNotFound {
		
		PreparedStatement stm = null;
		ResultSet rs=null;
		String sqlQuery;

		if(id <= 0 ) 
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::getProtocolProperty] id non definito");
		
		try {
		
			IJDBCAdapter jdbcAdapter = JDBCAdapterFactory.createJDBCAdapter(tipoDatabase);
				
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PROTOCOL_PROPERTIES);
			sqlQueryObject.addSelectField("tipo_proprietario");
			sqlQueryObject.addSelectField("id_proprietario");
			sqlQueryObject.addSelectField("name");
			sqlQueryObject.addSelectField("value_string");
			sqlQueryObject.addSelectField("value_number");
			sqlQueryObject.addSelectField("value_boolean");
			sqlQueryObject.addSelectField("value_binary");
			sqlQueryObject.addSelectField("file_name");
			sqlQueryObject.addSelectField("id");
			sqlQueryObject.addWhereCondition("id = ?");
			sqlQueryObject.setANDLogicOperator(true);
			sqlQuery = sqlQueryObject.createSQLQuery();
			stm = connection.prepareStatement(sqlQuery);
			stm.setLong(1, id);
			rs = stm.executeQuery();
			
			ProtocolProperty pp = null;
			if(rs.next()){
				pp = new ProtocolProperty();
				pp.setTipoProprietario(rs.getString("tipo_proprietario"));
				pp.setIdProprietario(rs.getLong("id_proprietario"));
				pp.setName(rs.getString("name"));
				pp.setValue(rs.getString("value_string"));
				pp.setNumberValue(rs.getLong("value_number"));
				if(rs.wasNull()){
					pp.setNumberValue(null);
				}
				int value = rs.getInt("value_boolean");
				pp.setBooleanValue(value == CostantiDB.TRUE);
				if(rs.wasNull()){
					pp.setBooleanValue(null);
				}
				pp.setByteFile(jdbcAdapter.getBinaryData(rs,"value_binary"));
				pp.setFile(rs.getString("file_name"));
				pp.setId(rs.getLong("id"));
			}
			
			if(pp==null)
				throw new DriverRegistroServiziNotFound("ProtocolProperty con id["+id+"] non trovato");
			
			return pp;

		} catch (SQLException se) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::getProtocolProperty] SQLException : " + se.getMessage(),se);
		}catch (DriverRegistroServiziNotFound dnf) {
			throw dnf;
		}catch (Exception se) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::getProtocolProperty] Exception : " + se.getMessage(),se);
		} finally {
			try {
				if(rs!=null) rs.close();
				if(stm!=null) stm.close();
			} catch (Exception e) {
			}
		}
	}
	
}
