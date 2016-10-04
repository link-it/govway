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
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import org.slf4j.Logger;
import org.openspcoop2.core.commons.AccordiUtils;
import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.core.commons.DBUtils;
import org.openspcoop2.core.constants.CostantiConnettori;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.constants.TipiConnettore;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDAccordoCooperazione;
import org.openspcoop2.core.registry.AccordoCooperazionePartecipanti;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteComuneServizioComposto;
import org.openspcoop2.core.registry.AccordoServizioParteComuneServizioCompostoServizioComponente;
import org.openspcoop2.core.registry.Azione;
import org.openspcoop2.core.registry.Connettore;
import org.openspcoop2.core.registry.Documento;
import org.openspcoop2.core.registry.Fruitore;
import org.openspcoop2.core.registry.IdSoggetto;
import org.openspcoop2.core.registry.MessagePart;
import org.openspcoop2.core.registry.Operation;
import org.openspcoop2.core.registry.PortType;
import org.openspcoop2.core.registry.PortaDominio;
import org.openspcoop2.core.registry.Servizio;
import org.openspcoop2.core.registry.ServizioAzione;
import org.openspcoop2.core.registry.constants.BindingStyle;
import org.openspcoop2.core.registry.constants.BindingUse;
import org.openspcoop2.core.registry.constants.CostantiRegistroServizi;
import org.openspcoop2.core.registry.constants.ProfiloCollaborazione;
import org.openspcoop2.core.registry.constants.ProprietariDocumento;
import org.openspcoop2.core.registry.constants.RuoliDocumento;
import org.openspcoop2.core.registry.constants.StatoFunzionalita;
import org.openspcoop2.core.registry.constants.TipologiaServizio;
import org.openspcoop2.core.registry.driver.BeanUtilities;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziNotFound;
import org.openspcoop2.core.registry.driver.IDAccordoCooperazioneFactory;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.jdbc.IJDBCAdapter;
import org.openspcoop2.utils.jdbc.JDBCAdapterFactory;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.SQLObjectFactory;

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
					+ se.getMessage() + "].");
		} catch (Exception se) {
			throw new DriverRegistroServiziException(
					"[DriverControlStationDB_LIB::CRUDPdd] Exception ["
					+ se.getMessage() + "].");
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

		// setto i dati, se le property non sono presenti il loro valore rimarra
		// a null e verra settato come tale nel DB
		String nomeProperty = null;
		String valoreProperty = null;

		nomeConnettore = connettore.getNome();
		endpointtype = connettore.getTipo();

		if (endpointtype == null || endpointtype.trim().equals(""))
			endpointtype = TipiConnettore.DISABILITATO.getNome();

		Hashtable<String, String> extendedProperties = new Hashtable<String, String>();
		
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
				sqlQueryObject.addInsertField("custom", "?");
				sqlQuery = sqlQueryObject.createSQLInsert();
				stm = connection.prepareStatement(sqlQuery);

				stm.setString(1, endpointtype);
				stm.setString(2, url);
				stm.setString(3, nome);
				stm.setString(4, tipo);
				stm.setString(5, utente);
				stm.setString(6, password);
				stm.setString(7, initcont);
				stm.setString(8, urlpkg);
				stm.setString(9, provurl);
				stm.setString(10, connectionfactory);
				stm.setString(11, sendas);
				stm.setString(12, nomeConnettore);
				if(debug){
					stm.setInt(13, 1);
				}else{
					stm.setInt(13, 0);
				}
				if(connettore.getCustom()!=null && connettore.getCustom()){
					stm.setInt(14, 1);
				}else{
					stm.setInt(14, 0);
				}

				DriverRegistroServiziDB_LIB.log.debug("CRUDConnettore CREATE : \n" + DriverRegistroServiziDB_LIB.formatSQLString(sqlQuery, endpointtype, url, nome, tipo, utente, password, initcont, urlpkg, provurl, connectionfactory, sendas, nomeConnettore, debug, (connettore.getCustom()!=null && connettore.getCustom())));
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
				sqlQueryObject.addUpdateField("custom", "?");
				sqlQueryObject.addWhereCondition("id=?");
				sqlQuery = sqlQueryObject.createSQLUpdate();
				stm = connection.prepareStatement(sqlQuery);

				stm.setString(1, endpointtype);
				stm.setString(2, url);
				stm.setString(3, nome);
				stm.setString(4, tipo);
				stm.setString(5, utente);
				stm.setString(6, password);
				stm.setString(7, initcont);
				stm.setString(8, urlpkg);
				stm.setString(9, provurl);
				stm.setString(10, connectionfactory);
				stm.setString(11, sendas);
				stm.setString(12, nomeConnettore);
				if(debug){
					stm.setInt(13, 1);
				}else{
					stm.setInt(13, 0);
				}
				if(connettore.getCustom()!=null && connettore.getCustom()){
					stm.setInt(14, 1);
				}else{
					stm.setInt(14, 0);
				}
				stm.setLong(15, idConnettore);

				DriverRegistroServiziDB_LIB.log.debug("CRUDConnettore UPDATE : \n" + DriverRegistroServiziDB_LIB.formatSQLString(sqlQuery, endpointtype, url, nome, tipo, utente, password, initcont, urlpkg, provurl, connectionfactory, sendas, nomeConnettore, debug,(connettore.getCustom()!=null && connettore.getCustom()),idConnettore));
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
	public static long CRUDSoggetto(int type, org.openspcoop2.core.registry.Soggetto soggetto, Connection con) throws DriverRegistroServiziException {
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

				updateStmt.setString(1, nome);
				updateStmt.setString(2, descizione);
				updateStmt.setString(3, identificativoPorta);
				updateStmt.setString(4, tipo);
				updateStmt.setLong(5, idConnettore);
				updateStmt.setString(6, server);
				updateStmt.setString(7, soggetto.getSuperUser());
				if(soggetto.getPrivato()!=null && soggetto.getPrivato())
					updateStmt.setInt(8, 1);
				else
					updateStmt.setInt(8, 0);
				updateStmt.setString(9, soggetto.getVersioneProtocollo());
				updateStmt.setString(10, codiceIPA);
				if(soggetto.getOraRegistrazione()!=null){
					updateStmt.setTimestamp(11, new Timestamp(soggetto.getOraRegistrazione().getTime()));
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

				break;

			case UPDATE:
				// UPDATE
				String oldNomeSoggetto = soggetto.getOldNomeForUpdate();
				String oldTipoSoggetto = soggetto.getOldTipoForUpdate();

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

				updateStmt.setString(1, nome);
				updateStmt.setString(2, descizione);
				updateStmt.setString(3, identificativoPorta);
				updateStmt.setString(4, tipo);
				updateStmt.setString(5, server);
				updateStmt.setString(6, soggetto.getSuperUser());
				if(soggetto.getPrivato()!=null && soggetto.getPrivato())
					updateStmt.setInt(7, 1);
				else
					updateStmt.setInt(7, 0);
				updateStmt.setString(8, soggetto.getVersioneProtocollo());
				updateStmt.setString(9, codiceIPA);
				if(soggetto.getOraRegistrazione()!=null){
					updateStmt.setTimestamp(10, new Timestamp(soggetto.getOraRegistrazione().getTime()));
					updateStmt.setLong(11, idSoggetto);
				}else{
					updateStmt.setLong(10, idSoggetto);
				}
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

				break;

			case DELETE:
				// DELETE
				idSoggetto = DBUtils.getIdSoggetto(nome, tipo, con, DriverRegistroServiziDB_LIB.tipoDB);
				idConnettore = DriverRegistroServiziDB_LIB.getIdConnettoreSoggetto(nome, tipo, con);
				if (idSoggetto <= 0)
					throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDSoggetto(DELETE)] Id Soggetto non valido.");
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

		Servizio servizio = asps.getServizio();
		String nomeProprietario = servizio.getNomeSoggettoErogatore();
		String tipoProprietario = servizio.getTipoSoggettoErogatore();
		String nomeServizio = servizio.getNome();
		String tipoServizio = servizio.getTipo();
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

		// String accordoServizio=servizio.getAccordoServizio();

		// String confermaRicezione=servizio.getConfermaRicezione();
		Connettore connettore = servizio.getConnettore();
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
		StatoFunzionalita servizioCorrelato = (TipologiaServizio.CORRELATO.equals(servizio.getTipologiaServizio()) ? CostantiRegistroServizi.ABILITATO : CostantiRegistroServizi.DISABILITATO);
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
			ServizioAzione azione = null;
			asps.setIdAccordo(idAccordoLong);
			switch (type) {
			case CREATE:
				// CREATE

				if (connettore == null) {
					connettore = new Connettore();
					connettore.setNome("CNT_" + tipoProprietario+"/"+nomeProprietario +"_"+ tipoServizio + "/" +nomeServizio);
				}

				if (connettore.getNome() == null || connettore.getNome().equals("")) {
					// setto il nome del connettore
					connettore.setNome("CNT_" + tipoProprietario+"/"+nomeProprietario +"_"+ tipoServizio + "/" +nomeServizio );
				}

				// creo il connettore del servizio
				idConnettore = DriverRegistroServiziDB_LIB.CRUDConnettore(1, connettore, con);

				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
				sqlQueryObject.addInsertTable(CostantiDB.SERVIZI);
				sqlQueryObject.addInsertField("nome_servizio", "?");
				sqlQueryObject.addInsertField("tipo_servizio", "?");
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
				sqlQueryObject.addInsertField("aps_nome", "?");
				sqlQueryObject.addInsertField("aps_versione", "?");
				
				updateQuery = sqlQueryObject.createSQLInsert();
				updateStmt = con.prepareStatement(updateQuery);

				updateStmt.setString(1, nomeServizio);
				updateStmt.setString(2, tipoServizio);
				updateStmt.setLong(3, idSoggetto);
				updateStmt.setLong(4, idAccordoLong);
				updateStmt.setString(5, getValue(servizioCorrelato));
				updateStmt.setLong(6, idConnettore);
				updateStmt.setString(7, wsdlImplementativoErogatore);
				updateStmt.setString(8, wsdlImplementativoFruitore);
				updateStmt.setString(9, superUser);
				if(asps.getPrivato()!=null && asps.getPrivato())
					updateStmt.setInt(10, 1);
				else
					updateStmt.setInt(10, 0);
				updateStmt.setString(11, port_type);
				updateStmt.setString(12, asps.getVersioneProtocollo());
				updateStmt.setString(13, descrizione);
								
				int index = 14;
				
				if(stato!=null){
					updateStmt.setString(index, stato);
					index++;
				}
				
				if(asps.getOraRegistrazione()!=null){
					updateStmt.setTimestamp(index, new Timestamp(asps.getOraRegistrazione().getTime()));
					index++;
				}
		
				
				updateStmt.setString(index, asps.getNome());
				index++;
				updateStmt.setString(index, asps.getVersione());
				index++;
				
				// eseguo lo statement
				n = updateStmt.executeUpdate();
				updateStmt.close();
				DriverRegistroServiziDB_LIB.log.debug("CRUDAccordoServizioParteSpecifica CREATE : \n" + DriverRegistroServiziDB_LIB.formatSQLString(updateQuery, nomeServizio, tipoServizio, idSoggetto, idAccordoLong, servizioCorrelato, idConnettore, wsdlImplementativoErogatore, wsdlImplementativoFruitore, superUser));
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
					DriverRegistroServiziDB_LIB.CRUDAccordoServizioParteSpecificaFruitore(1, fruitore, con, servizio);
				}
				
				// aggiungo azioni
				sizeAzioni = servizio.sizeParametriAzioneList();
				azione = null;
				for (int i = 0; i < sizeAzioni; i++) {
					azione = servizio.getParametriAzione(i);
					DriverRegistroServiziDB_LIB.log.debug("CRUD AZIONE -----------["+azione.getNome()+"]");
					DriverRegistroServiziDB_LIB.CRUDAccordoServizioParteSpecificaAzioni(1, azione, con, servizio);
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
				

				break;

			case UPDATE:
				// UPDATE
				String oldNomeSoggetto = servizio.getOldNomeSoggettoErogatoreForUpdate();
				String oldTipoSoggetto = servizio.getOldTipoSoggettoErogatoreForUpdate();
				String oldNomeServizio = servizio.getOldNomeForUpdate();
				String oldTipoServizio = servizio.getOldTipoForUpdate();

				if (oldNomeServizio == null || oldNomeServizio.equals(""))
					oldNomeServizio = nomeServizio;
				if (oldTipoServizio == null || oldTipoServizio.equals(""))
					oldTipoServizio = tipoServizio;
				if (oldNomeSoggetto == null || oldNomeSoggetto.equals(""))
					oldNomeSoggetto = nomeProprietario;
				if (oldTipoSoggetto == null || oldTipoSoggetto.equals(""))
					oldTipoSoggetto = tipoProprietario;

				//recupero id servizio
				idServizio = DBUtils.getIdServizio(oldNomeServizio, oldTipoServizio, oldNomeSoggetto, oldTipoSoggetto, con, DriverRegistroServiziDB_LIB.tipoDB);
				if (idServizio <= 0){
					// Puo' darsi che l'old soggetto e il nuovo soggetto siano la stesso soggetto della tabella. E' stato cambiato il nome.
					idServizio = DBUtils.getIdServizio(oldNomeServizio, oldTipoServizio, nomeProprietario, tipoProprietario, con, DriverRegistroServiziDB_LIB.tipoDB);
				}
				if (idServizio <= 0)
					throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDAccordoServizioParteSpecifica(UPDATE)] Id Servizio non valido.");

				//recupero l'id del connettore
				idConnettore = DriverRegistroServiziDB_LIB.getIdConnettoreServizio(oldNomeServizio, oldTipoServizio, oldNomeSoggetto, oldTipoSoggetto, con);
				if (idConnettore <= 0){
					// Puo' darsi che l'old soggetto e il nuovo soggetto siano la stesso soggetto della tabella. E' stato cambiato il nome.
					idConnettore = DriverRegistroServiziDB_LIB.getIdConnettoreServizio(oldNomeServizio, oldTipoServizio, nomeProprietario, tipoProprietario, con);
				}
				if (idConnettore <= 0)
					throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDAccordoServizioParteSpecifica] id connettore nullo.");
				connettore.setId(idConnettore);

				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
				sqlQueryObject.addUpdateTable(CostantiDB.SERVIZI);
				sqlQueryObject.addUpdateField("nome_servizio", "?");
				sqlQueryObject.addUpdateField("tipo_servizio", "?");
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
				sqlQueryObject.addUpdateField("aps_nome", "?");
				sqlQueryObject.addUpdateField("aps_versione", "?");
				sqlQueryObject.addWhereCondition("id=?");
				updateQuery = sqlQueryObject.createSQLUpdate();
				updateStmt = con.prepareStatement(updateQuery);

				updateStmt.setString(1, nomeServizio);
				updateStmt.setString(2, tipoServizio);
				updateStmt.setLong(3, idSoggetto);
				updateStmt.setLong(4, idAccordoLong);
				updateStmt.setString(5, getValue(servizioCorrelato));
				updateStmt.setLong(6, idConnettore);
				updateStmt.setString(7, wsdlImplementativoErogatore);
				updateStmt.setString(8, wsdlImplementativoFruitore);
				updateStmt.setString(9, superUser);
				if(asps.getPrivato()!=null && asps.getPrivato())
					updateStmt.setInt(10, 1);
				else
					updateStmt.setInt(10, 0);
				updateStmt.setString(11, port_type);
				updateStmt.setString(12, asps.getVersioneProtocollo());
				updateStmt.setString(13, descrizione);
				
				index = 14;
								
				if(stato!=null){
					updateStmt.setString(index, stato);
					index++;
				}
				
				if(asps.getOraRegistrazione()!=null){
					updateStmt.setTimestamp(index, new Timestamp(asps.getOraRegistrazione().getTime()));
					index++;
				}
				
				updateStmt.setString(index, asps.getNome());
				index++;
				updateStmt.setString(index, asps.getVersione());
				index++;
								
				updateStmt.setLong(index, idServizio);


				// eseguo lo statement
				n = updateStmt.executeUpdate();
				updateStmt.close();
				DriverRegistroServiziDB_LIB.log.debug("CRUDAccordoServizioParteSpecifica type = " + type + " row affected =" + n);

				// aggiorno nome connettore
				String newNomeConnettore = "CNT_" + tipoProprietario+"/"+nomeProprietario +"_"+ tipoServizio + "/" +nomeServizio;
				connettore.setNome(newNomeConnettore);
				DriverRegistroServiziDB_LIB.CRUDConnettore(2, connettore, con);

				//aggiorno fruitori
				//La lista dei fruitori del servizio contiene tutti e soli i fruitori di questo servizio
				//prima vengono cancellati i fruitori esistenti e poi vengono riaggiunti
				sizeFruitori = asps.sizeFruitoreList();
				fruitore = null;
				//cancellazione
				DriverRegistroServiziDB_LIB.deleteAllFruitoriServizio(idServizio, con);
				//creazione
				for (int i = 0; i < sizeFruitori; i++) {
					fruitore = asps.getFruitore(i);
					DriverRegistroServiziDB_LIB.CRUDAccordoServizioParteSpecificaFruitore(1, fruitore, con, servizio);
				}

				DriverRegistroServiziDB_LIB.log.debug("CRUDAccordoServizioParteSpecifica UPDATE : \n" + DriverRegistroServiziDB_LIB.formatSQLString(updateQuery, nomeServizio, tipoServizio, idSoggetto, idAccordoLong, servizioCorrelato, idConnettore, wsdlImplementativoErogatore, wsdlImplementativoFruitore, superUser, idServizio));


				//aggiorno azioni
				//La lista delle azioni del servizio contiene tutti e soli le azioni di questo servizio
				//prima vengono cancellati le azioni esistenti e poi vengono riaggiunte
				sizeAzioni = servizio.sizeParametriAzioneList();
				azione = null;
				//cancellazione
				DriverRegistroServiziDB_LIB.deleteAllAzioniServizio(idServizio, con);
				//creazione
				for (int i = 0; i < sizeAzioni; i++) {
					azione = servizio.getParametriAzione(i);
					DriverRegistroServiziDB_LIB.log.debug("CRUD AZIONE -----------["+azione.getNome()+"]");
					DriverRegistroServiziDB_LIB.CRUDAccordoServizioParteSpecificaAzioni(1, azione, con, servizio);
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
				
				break;

			case DELETE:
				// DELETE
				// if(servizio.getId()<=0) throw new
				// DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDAccordoServizioParteSpecifica(DELETE)]
				// ID Servizio non valido.");
				// idServizio=servizio.getId();
				idServizio = DBUtils.getIdServizio(nomeServizio, tipoServizio, nomeProprietario, tipoProprietario, con, DriverRegistroServiziDB_LIB.tipoDB);
				if (idServizio <= 0)
					throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDAccordoServizioParteSpecifica(DELETE)] Id Servizio non valido.");
				idConnettore = DriverRegistroServiziDB_LIB.getIdConnettoreServizio(nomeServizio, tipoServizio, nomeProprietario, tipoProprietario, con);
				if (idConnettore <= 0)
					throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDAccordoServizioParteSpecifica(DELETE)] Id Connettore non valido.");

				//	elimino fruitori
				sizeFruitori = asps.sizeFruitoreList();
				fruitore = null;
				for (int i = 0; i < sizeFruitori; i++) {
					fruitore = asps.getFruitore(i);

					DriverRegistroServiziDB_LIB.CRUDAccordoServizioParteSpecificaFruitore(3, fruitore, con, servizio);
				}
				
				// elimino azioni
				sizeAzioni = servizio.sizeParametriAzioneList();
				azione = null;
				for (int i = 0; i < sizeAzioni; i++) {
					azione = servizio.getParametriAzione(i);
					DriverRegistroServiziDB_LIB.CRUDAccordoServizioParteSpecificaAzioni(3, azione, con, servizio);
				}

				
				// Documenti generici accordo di servizio
				// Allegati
				// Specifiche Semiformali
				// Specifiche Livelli di Servizio
				// Specifiche Sicurezza
				DriverRegistroServiziDB_LIB.CRUDDocumento(CostantiDB.DELETE, null, idServizio, ProprietariDocumento.servizio, con, tipoDatabase);
				
				
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
	public static long CRUDAccordoServizioParteSpecificaFruitore(int type, Fruitore fruitore, Connection con, Servizio servizio) throws DriverRegistroServiziException {
		PreparedStatement updateStmt = null;
		String updateQuery;
		PreparedStatement selectStmt = null;
		String selectQuery = "";
		ResultSet selectRS = null;

		long idServizio = -1;
		try{
			String tipoServ = servizio.getTipo();
			String nomeServ = servizio.getNome();
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
			
			idServizio = DBUtils.getIdServizio(nomeServ, tipoServ, nomeSogg, tipoSogg, con, DriverRegistroServiziDB_LIB.tipoDB);
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

				break;

			case 3:
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
				selectRS = selectStmt.executeQuery();
				if (selectRS.next())
					return selectRS.getLong("id");
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
	public static long CRUDAccordoServizioParteSpecificaAzioni(int type, ServizioAzione azione, Connection con, Servizio servizio) throws DriverRegistroServiziException {
		PreparedStatement updateStmt = null;
		String updateQuery;
		PreparedStatement selectStmt = null;
		String selectQuery = "";
		ResultSet selectRS = null;

		long idServizio = -1;
		try{
			String tipoServ = servizio.getTipo();
			String nomeServ = servizio.getNome();
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
			
			idServizio = DBUtils.getIdServizio(nomeServ, tipoServ, nomeSogg, tipoSogg, con, DriverRegistroServiziDB_LIB.tipoDB);
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
				updateStmt.setLong(1, idAccordo);
				updateStmt.setString(2, pt.getNome());
				updateStmt.setString(3, pt.getDescrizione());
				updateStmt.setString(4, pt.getProfiloPT());

				DriverRegistroServiziDB_LIB.log.debug("Aggiungo port-type ["+pt.getNome()+"] con profilo ["+pt.getProfiloPT()+"]");

				if(CostantiRegistroServizi.PROFILO_AZIONE_RIDEFINITO.equals(pt.getProfiloPT())){
					DriverRegistroServiziDB_LIB.log.debug("ridefinizione...");
					updateStmt.setString(5, getValue(pt.getFiltroDuplicati()));
					updateStmt.setString(6, getValue(pt.getConfermaRicezione()));
					updateStmt.setString(7, getValue(pt.getIdCollaborazione()));
					updateStmt.setString(8, getValue(pt.getConsegnaInOrdine()));
					updateStmt.setString(9, pt.getScadenza());
					updateStmt.setString(10, getValue(pt.getProfiloCollaborazione()));
				}else{
					if(pt.getFiltroDuplicati()!=null)
						updateStmt.setString(5, getValue(pt.getFiltroDuplicati()));
					else
						updateStmt.setString(5, getValue(as.getFiltroDuplicati()));
					if(pt.getConfermaRicezione()!=null)
						updateStmt.setString(6, getValue(pt.getConfermaRicezione()));
					else
						updateStmt.setString(6, getValue(as.getConfermaRicezione()));
					if(pt.getIdCollaborazione()!=null)
						updateStmt.setString(7, getValue(pt.getIdCollaborazione()));
					else
						updateStmt.setString(7, getValue(as.getIdCollaborazione()));
					if(pt.getConsegnaInOrdine()!=null)
						updateStmt.setString(8, getValue(pt.getConsegnaInOrdine()));
					else
						updateStmt.setString(8, getValue(as.getConsegnaInOrdine()));
					if(pt.getScadenza()!=null)
						updateStmt.setString(9, pt.getScadenza());
					else
						updateStmt.setString(9, as.getScadenza());
					if(pt.getProfiloCollaborazione()!=null)
						updateStmt.setString(10, getValue(pt.getProfiloCollaborazione()));
					else
						updateStmt.setString(10, getValue(as.getProfiloCollaborazione()));
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
				updateStmt.setString(1, pt.getDescrizione());
				updateStmt.setString(2, pt.getProfiloPT());


				if(CostantiRegistroServizi.PROFILO_AZIONE_RIDEFINITO.equals(pt.getProfiloPT())){
					updateStmt.setString(3, getValue(pt.getFiltroDuplicati()));
					updateStmt.setString(4, getValue(pt.getConfermaRicezione()));
					updateStmt.setString(5, getValue(pt.getIdCollaborazione()));
					updateStmt.setString(6, getValue(pt.getConsegnaInOrdine()));
					updateStmt.setString(7, pt.getScadenza());
					updateStmt.setString(8, getValue(pt.getProfiloCollaborazione()));
				}else{
					if(pt.getFiltroDuplicati()!=null)
						updateStmt.setString(3, getValue(pt.getFiltroDuplicati()));
					else
						updateStmt.setString(3, getValue(as.getFiltroDuplicati()));
					if(pt.getConfermaRicezione()!=null)
						updateStmt.setString(4, getValue(pt.getConfermaRicezione()));
					else
						updateStmt.setString(4, getValue(as.getConfermaRicezione()));
					if(pt.getIdCollaborazione()!=null)
						updateStmt.setString(5, getValue(pt.getIdCollaborazione()));
					else
						updateStmt.setString(5, getValue(as.getIdCollaborazione()));
					if(pt.getConsegnaInOrdine()!=null)
						updateStmt.setString(6, getValue(pt.getConsegnaInOrdine()));
					else
						updateStmt.setString(6, getValue(as.getConsegnaInOrdine()));
					if(pt.getScadenza()!=null)
						updateStmt.setString(7, pt.getScadenza());
					else
						updateStmt.setString(7, as.getScadenza());
					if(pt.getProfiloCollaborazione()!=null)
						updateStmt.setString(8, getValue(pt.getProfiloCollaborazione()));
					else
						updateStmt.setString(8, getValue(as.getProfiloCollaborazione()));
				}
				updateStmt.setString(9,getValue(pt.getStyle()));
				updateStmt.setLong(10, idAccordo);
				updateStmt.setString(11, pt.getNome());
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

				for(int i=0;i<pt.sizeAzioneList();i++){
					DriverRegistroServiziDB_LIB.CRUDAzionePortType(CostantiDB.DELETE, as, pt, pt.getAzione(i), con, idPT);
				}

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
	private static long getIdConnettoreServizio(String nomeServizio, String tipoServizio, String nomeSoggetto, String tipoSoggetto, Connection con) throws DriverRegistroServiziException {
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
			sqlQueryObject.addWhereCondition("id_soggetto = ?");
			sqlQueryObject.setANDLogicOperator(true);
			String query = sqlQueryObject.createSQLQuery();
			stm = con.prepareStatement(query);
			stm.setString(1, tipoServizio);
			stm.setString(2, nomeServizio);
			stm.setLong(3, idSoggetto);

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
	private static void deleteAllFruitoriServizio(long idServizio, Connection con) throws DriverRegistroServiziException {
		PreparedStatement stm = null;
		ResultSet rs = null;
		try {

			ArrayList<Long> listaConnettori = new ArrayList<Long>();

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
				listaConnettori.add(rs.getLong("id_connettore"));
			}
			rs.close();
			stm.close();

			//elimino prima le entry nella tab servizi_fruitori per rispettare le dipendenze
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.SERVIZI_FRUITORI);
			sqlQueryObject.addWhereCondition("id_servizio=?");
			String sqlQuery = sqlQueryObject.createSQLDelete();
			stm = con.prepareStatement(sqlQuery);
			stm.setLong(1, idServizio);
			int n=stm.executeUpdate();
			stm.close();
			DriverRegistroServiziDB_LIB.log.debug("Cancellati "+n+" Fruitori del servizio "+idServizio);

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
			DriverRegistroServiziDB_LIB.log.debug("Cancellati connettori "+listaConnettori.toString()+" associati ai Fruitori del servizio "+idServizio);
			
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
										throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDDocumento] OraRegistrazione non definita per documento, precedentemente gia inserito, ["+doc.getFile()+"]");
											
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
							throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDDocumento] Contenuto non definito per documento ["+doc.getFile()+"]");

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
								throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDDocumento] ID non definito per documento da aggiorare ["+doc.getFile()+"]");
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
							idServizioComponente = DBUtils.getIdServizio(tmp.getNome(), tmp.getTipo(), tmp.getNomeSoggetto(), tmp.getTipoSoggetto(), con, DriverRegistroServiziDB_LIB.tipoDB);
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
							idServizioComponente = DBUtils.getIdServizio(tmp.getNome(), tmp.getTipo(), tmp.getNomeSoggetto(), tmp.getTipoSoggetto(), con, DriverRegistroServiziDB_LIB.tipoDB);
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
				
				if(accordoCooperazione.getVersione()!=null && !"".equals(accordoCooperazione.getVersione())){
					updateStmt.setString(3, accordoCooperazione.getVersione());
				}else{
					updateStmt.setString(3, AccordiUtils.VERSIONE_DEFAULT);
				}
				
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
				IDAccordoCooperazione idAccordoObject = IDAccordoCooperazioneFactory.getInstance().getIDAccordoFromValues(accordoCooperazione.getNome(),accordoCooperazione.getVersione());
				long idAccordoCooperazione = DBUtils.getIdAccordoCooperazione(idAccordoObject, con, DriverRegistroServiziDB_LIB.tipoDB);
				if (idAccordoCooperazione<=0) {
					throw new DriverRegistroServiziException("[DriverRegistroServiziDB::CRUDAccordoCooperazione] non riesco a trovare l'id del'Accordo inserito");
				}
				accordoCooperazione.setId(idAccordoCooperazione);
								
				
				// aggiungo servizi componenti
				/*for(int i=0; i<accordoCooperazione.sizeServizioCompostoList(); i++){
					
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

					updateStmt.setLong(1, idAccordoCooperazione);
					updateStmt.setLong(2, idAccordoServizioComposto);
					
					n = updateStmt.executeUpdate();
					updateStmt.close();
					DriverRegistroServiziDB_LIB.log.debug("CRUDAccordoCooperazione (Partecipante) type = " + type + " row affected =" + n);
				}*/
				
				
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
				
				if(accordoCooperazione.getVersione()!=null && !"".equals(accordoCooperazione.getVersione())){
					updateStmt.setString(3, accordoCooperazione.getVersione());
				}else{
					updateStmt.setString(3, AccordiUtils.VERSIONE_DEFAULT);
				}
				
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
					updateStmt.setLong(index, AccordiUtils.SOGGETTO_REFERENTE_DEFAULT);
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
				
				
				break;

			case DELETE:
				// DELETE
				
				IDAccordoCooperazione idAccordo = IDAccordoCooperazioneFactory.getInstance().getIDAccordoFromValues(nome,accordoCooperazione.getVersione());
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
	
	
	
}
