/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
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


package org.openspcoop2.web.ctrlstat.driver;

import static org.openspcoop2.core.constants.CostantiDB.CREATE;
import static org.openspcoop2.core.constants.CostantiDB.DELETE;
import static org.openspcoop2.core.constants.CostantiDB.UPDATE;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.MessageFormat;

import org.slf4j.Logger;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.registry.constants.StatoFunzionalita;
import org.openspcoop2.core.registry.driver.db.DriverRegistroServiziDB_LIB;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.SQLObjectFactory;
import org.openspcoop2.web.ctrlstat.dao.PdDControlStation;

/**
 * DriverControlStationDB_LIB
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class DriverControlStationDB_LIB {

	private static Logger log = null;
	public static void initialize(Logger logParam){
		log = logParam;
	}

	// Tipo database ereditato da DriverControlStationDB
	private static String tipoDB = null;

	// Setto il tipoDB
	public static void setTipoDB(String tipoDatabase) {
		DriverControlStationDB_LIB.tipoDB = tipoDatabase;
	}

	public static void CRUDPdd(int type, PdDControlStation pdd, Connection con) throws DriverControlStationException {
		if (pdd == null) {
			throw new DriverControlStationException("[DriverControlStationDB_LIB::CRUDPdd] Parametro non valido.");
		}

		if ((type != CostantiDB.CREATE) && (pdd.getId() <= 0)) {
			throw new DriverControlStationException("[DriverControlStationDB_LIB::CRUDPdd] ID Pdd non valido.");
		}

		String nome = pdd.getNome();
		String descrizione = pdd.getDescrizione();

		String ip = pdd.getIp();
		int porta = pdd.getPorta();
		String ipGestione = pdd.getIpGestione();
		int portaGestione = pdd.getPortaGestione();
		String protocollo = pdd.getProtocollo();
		String protocolloGestione = pdd.getProtocolloGestione();

		String tipo = pdd.getTipo();
		String implementazione = pdd.getImplementazione();

		String subject = pdd.getSubject();
		String password = pdd.getPassword();
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
					ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverControlStationDB_LIB.tipoDB);
					sqlQueryObject.addInsertTable(CostantiDB.PDD);
					sqlQueryObject.addInsertField("nome", "?");
					sqlQueryObject.addInsertField("descrizione", "?");
					sqlQueryObject.addInsertField("ip", "?");
					sqlQueryObject.addInsertField("porta", "?");
					sqlQueryObject.addInsertField("ip_gestione", "?");
					sqlQueryObject.addInsertField("porta_gestione", "?");
					sqlQueryObject.addInsertField("protocollo", "?");
					sqlQueryObject.addInsertField("protocollo_gestione", "?");
					sqlQueryObject.addInsertField("tipo", "?");
					sqlQueryObject.addInsertField("implementazione", "?");
					sqlQueryObject.addInsertField("subject", "?");
					sqlQueryObject.addInsertField("password", "?");
					sqlQueryObject.addInsertField("client_auth", "?");
					sqlQueryObject.addInsertField("superuser", "?");
					if (pdd.getOraRegistrazione() != null)
						sqlQueryObject.addInsertField("ora_registrazione", "?");

					updateQuery = sqlQueryObject.createSQLInsert();
					updateStmt = con.prepareStatement(updateQuery);

					int index = 1;
					updateStmt.setString(index++, nome);
					updateStmt.setString(index++, descrizione);
					updateStmt.setString(index++, ip);
					updateStmt.setInt(index++, porta);
					updateStmt.setString(index++, ipGestione);
					updateStmt.setInt(index++, portaGestione);
					updateStmt.setString(index++, protocollo);
					updateStmt.setString(index++, protocolloGestione);
					updateStmt.setString(index++, tipo);
					updateStmt.setString(index++, implementazione);
					updateStmt.setString(index++, (subject != null ? Utilities.formatSubject(subject) : null));
					updateStmt.setString(index++, password);
					updateStmt.setString(index++, DriverRegistroServiziDB_LIB.getValue(client_auth));
					updateStmt.setString(index++, superuser);
					if (pdd.getOraRegistrazione() != null)
						updateStmt.setTimestamp(index++, new Timestamp(pdd.getOraRegistrazione().getTime()));

					// eseguo lo statement
					n = updateStmt.executeUpdate();

					updateStmt.close();

					DriverControlStationDB_LIB.log.debug("CRUDPdd type = " + type + " row affected =" + n);

					DriverControlStationDB_LIB.log.debug("CRUDPdd CREATE : \n" + DriverControlStationDB_LIB.formatSQLString(updateQuery, nome, descrizione, ip, porta, ipGestione, portaGestione, protocollo, protocolloGestione, tipo, implementazione, subject, password, client_auth));

					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverControlStationDB_LIB.tipoDB);
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

					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverControlStationDB_LIB.tipoDB);
					sqlQueryObject.addUpdateTable(CostantiDB.PDD);
					sqlQueryObject.addUpdateField("descrizione", "?");
					sqlQueryObject.addUpdateField("ip", "?");
					sqlQueryObject.addUpdateField("porta", "?");
					sqlQueryObject.addUpdateField("ip_gestione", "?");
					sqlQueryObject.addUpdateField("porta_gestione", "?");
					sqlQueryObject.addUpdateField("protocollo", "?");
					sqlQueryObject.addUpdateField("protocollo_gestione", "?");
					sqlQueryObject.addUpdateField("tipo", "?");
					sqlQueryObject.addUpdateField("implementazione", "?");
					sqlQueryObject.addUpdateField("subject", "?");
					sqlQueryObject.addUpdateField("password", "?");
					sqlQueryObject.addUpdateField("client_auth", "?");
					sqlQueryObject.addUpdateField("superuser", "?");
					sqlQueryObject.addUpdateField("nome", "?");
					if (pdd.getOraRegistrazione() != null)
						sqlQueryObject.addUpdateField("ora_registrazione", "?");
					sqlQueryObject.addWhereCondition("id=?");
					updateQuery = sqlQueryObject.createSQLUpdate();
					updateStmt = con.prepareStatement(updateQuery);

					index = 1;
					updateStmt.setString(index++, descrizione);
					updateStmt.setString(index++, ip);
					updateStmt.setInt(index++, porta);
					updateStmt.setString(index++, ipGestione);
					updateStmt.setInt(index++, portaGestione);
					updateStmt.setString(index++, protocollo);
					updateStmt.setString(index++, protocolloGestione);
					updateStmt.setString(index++, tipo);
					updateStmt.setString(index++, implementazione);
					updateStmt.setString(index++, (subject != null ? Utilities.formatSubject(subject) : null));
					updateStmt.setString(index++, password);
					updateStmt.setString(index++, DriverRegistroServiziDB_LIB.getValue(client_auth));
					updateStmt.setString(index++, superuser);
					updateStmt.setString(index++, nome);
					if (pdd.getOraRegistrazione() != null)
						updateStmt.setTimestamp(index++, new Timestamp(pdd.getOraRegistrazione().getTime()));

					updateStmt.setLong(index++, pdd.getId());

					// eseguo lo statement
					n = updateStmt.executeUpdate();
					updateStmt.close();
					DriverControlStationDB_LIB.log.debug("CRUDPdd type = " + type + " row affected =" + n);

					DriverControlStationDB_LIB.log.debug("CRUDPdd UPDATE : \n" + DriverControlStationDB_LIB.formatSQLString(updateQuery, descrizione, ip, porta, ipGestione, portaGestione, protocollo, protocolloGestione, tipo, implementazione, subject, password, client_auth, pdd.getId()));

					break;

				case DELETE:
					// DELETE

					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverControlStationDB_LIB.tipoDB);
					sqlQueryObject.addDeleteTable(CostantiDB.PDD);
					sqlQueryObject.addWhereCondition("id=?");
					updateQuery = sqlQueryObject.createSQLDelete();
					updateStmt = con.prepareStatement(updateQuery);

					updateStmt.setLong(1, pdd.getId());

					// eseguo lo statement
					n = updateStmt.executeUpdate();
					updateStmt.close();
					DriverControlStationDB_LIB.log.debug("CRUDPdd type = " + type + " row affected =" + n);

					DriverControlStationDB_LIB.log.debug("CRUDPdd DELETE : \n" + DriverControlStationDB_LIB.formatSQLString(updateQuery, pdd.getId()));

					break;
			}

		} catch (SQLException se) {
			throw new DriverControlStationException("[DriverControlStationDB_LIB::CRUDPdd] SQLException [" + se.getMessage() + "].");
		} catch (Exception se) {
			throw new DriverControlStationException("[DriverControlStationDB_LIB::CRUDPdd] Exception [" + se.getMessage() + "].");
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

}
