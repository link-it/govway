/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it). 
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

import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.core.commons.DBUtils;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.constants.ProprietariProtocolProperty;
import org.openspcoop2.core.id.IDRuolo;
import org.openspcoop2.core.registry.Connettore;
import org.openspcoop2.core.registry.CredenzialiSoggetto;
import org.openspcoop2.core.registry.PortaDominio;
import org.openspcoop2.core.registry.Proprieta;
import org.openspcoop2.core.registry.RuoloSoggetto;
import org.openspcoop2.core.registry.constants.StatoFunzionalita;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.utils.certificate.CertificateUtils;
import org.openspcoop2.utils.certificate.PrincipalType;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.jdbc.IJDBCAdapter;
import org.openspcoop2.utils.jdbc.JDBCAdapterFactory;
import org.openspcoop2.utils.jdbc.JDBCUtilities;
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
public class DriverRegistroServiziDB_soggettiLIB {

	

	/** Metodi CRUD */

	public static void CRUDPortaDominio(int type, PortaDominio pdd, Connection con)
	throws DriverRegistroServiziException {
		if (pdd == null) {
			throw new DriverRegistroServiziException(
			"[DriverRegistroServiziDB_LIB::CRUDPdd] Parametro non valido.");
		}

		/**if ((type != CostantiDB.CREATE) && (pdd.getId() <= 0)) {
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
				updateStmt.setString(4, (subject != null ? CertificateUtils.formatPrincipal(subject, PrincipalType.SUBJECT) : null));
				updateStmt.setString(5, DriverRegistroServiziDB_LIB.getValue(client_auth));
				updateStmt.setString(6, superuser);
				if(pdd.getOraRegistrazione()!=null)
					updateStmt.setTimestamp(7, new Timestamp(pdd.getOraRegistrazione().getTime()));

				// eseguo lo statement
				n = updateStmt.executeUpdate();

				updateStmt.close();

				DriverRegistroServiziDB_LIB.logDebug("CRUDPdd type = " + type
						+ " row affected =" + n);

				DriverRegistroServiziDB_LIB.logDebug("CRUDPdd CREATE : \n"
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
				updateStmt.setString(4, (subject != null ? CertificateUtils.formatPrincipal(subject, PrincipalType.SUBJECT) : null));
				updateStmt.setString(5, DriverRegistroServiziDB_LIB.getValue(client_auth));
				updateStmt.setString(6, superuser);

				int paramIndex = 6;

				if(pdd.getOraRegistrazione()!=null)
					updateStmt.setTimestamp(++paramIndex, new Timestamp(pdd.getOraRegistrazione().getTime()));

				updateStmt.setLong(++paramIndex, idPdd);

				// eseguo lo statement
				n = updateStmt.executeUpdate();
				updateStmt.close();
				DriverRegistroServiziDB_LIB.logDebug("CRUDPdd type = " + type
						+ " row affected =" + n);

				DriverRegistroServiziDB_LIB.logDebug("CRUDPdd UPDATE : \n"
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
				DriverRegistroServiziDB_LIB.logDebug("CRUDPdd type = " + type
						+ " row affected =" + n);

				DriverRegistroServiziDB_LIB.logDebug("CRUDPdd DELETE : \n"
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
			JDBCUtilities.closeResources(updateStmt);
			JDBCUtilities.closeResources(selectRS, selectStmt);
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
		String superUser = soggetto.getSuperUser();

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

				String utenteRichiedente = null;
				if(soggetto.getProprietaOggetto()!=null && soggetto.getProprietaOggetto().getUtenteRichiedente()!=null) {
					utenteRichiedente = soggetto.getProprietaOggetto().getUtenteRichiedente();
				}
				else {
					utenteRichiedente = superUser;
				}
				
				Timestamp dataCreazione = null;
				if(soggetto.getProprietaOggetto()!=null && soggetto.getProprietaOggetto().getDataCreazione()!=null) {
					dataCreazione = new Timestamp(soggetto.getProprietaOggetto().getDataCreazione().getTime());
				}
				else if(soggetto.getOraRegistrazione()!=null){
					dataCreazione = new Timestamp(soggetto.getOraRegistrazione().getTime());
				}
				else {
					dataCreazione = DateManager.getTimestamp();
				}
				
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
				sqlQueryObject.addInsertField("cn_subject", "?");
				sqlQueryObject.addInsertField("issuer", "?");
				sqlQueryObject.addInsertField("cn_issuer", "?");
				sqlQueryObject.addInsertField("certificate", "?");
				sqlQueryObject.addInsertField("cert_strict_verification", "?");
				if(soggetto.getOraRegistrazione()!=null)
					sqlQueryObject.addInsertField("ora_registrazione", "?");
				if(utenteRichiedente!=null) {
					sqlQueryObject.addInsertField(CostantiDB.PROPRIETA_OGGETTO_UTENTE_RICHIEDENTE, "?");
				}
				if(dataCreazione!=null) {
					sqlQueryObject.addInsertField(CostantiDB.PROPRIETA_OGGETTO_DATA_CREAZIONE, "?");
				}
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

				idConnettore = DriverRegistroServiziDB_connettoriLIB.CRUDConnettore(CostantiDB.CREATE, connettore, con);

				int index = 1;
				
				updateStmt.setString(index++, nome);
				updateStmt.setString(index++, descizione);
				updateStmt.setString(index++, identificativoPorta);
				updateStmt.setString(index++, tipo);
				updateStmt.setLong(index++, idConnettore);
				updateStmt.setString(index++, server);
				updateStmt.setString(index++, superUser);
				if(soggetto.getPrivato()!=null && soggetto.getPrivato())
					updateStmt.setInt(index++, 1);
				else
					updateStmt.setInt(index++, 0);
				updateStmt.setString(index++, soggetto.getVersioneProtocollo());
				updateStmt.setString(index++, codiceIPA);
				
				CredenzialiSoggetto credenziali = (soggetto.sizeCredenzialiList() > 0 ? soggetto.getCredenziali(0) : null);
				updateStmt.setString(index++, (credenziali != null ? DriverRegistroServiziDB_LIB.getValue(credenziali.getTipo()) : null));
				updateStmt.setString(index++, (credenziali != null ? credenziali.getUser() : null));
				updateStmt.setString(index++, (credenziali != null ? credenziali.getPassword() : null));
				
				String subject = null;
				if(credenziali!=null && credenziali.getSubject()!=null && !"".equals(credenziali.getSubject()))
					subject = credenziali.getSubject();
				updateStmt.setString(index++, (subject != null ? CertificateUtils.formatPrincipal(subject, PrincipalType.SUBJECT) : null));
				String subjectCN = null;
				if(credenziali!=null && credenziali.getCnSubject()!=null && !"".equals(credenziali.getCnSubject()))
					subjectCN = credenziali.getCnSubject();
				updateStmt.setString(index++, subjectCN);
				
				String issuer = null;
				if(credenziali != null && org.openspcoop2.core.registry.constants.CredenzialeTipo.APIKEY.equals(credenziali.getTipo())) {
					updateStmt.setString(index++, CostantiDB.getIssuerApiKey(credenziali.isAppId()));
				}
				else {
					if(credenziali!=null && credenziali.getIssuer()!=null && !"".equals(credenziali.getIssuer()))
						issuer = credenziali.getIssuer();
					updateStmt.setString(index++, (issuer != null ? CertificateUtils.formatPrincipal(issuer, PrincipalType.ISSUER) : null));
				}
				String issuerCN = null;
				if(credenziali!=null && credenziali.getCnIssuer()!=null && !"".equals(credenziali.getCnIssuer()))
					issuerCN = credenziali.getCnIssuer();
				updateStmt.setString(index++, issuerCN);
				
				byte [] certificate = null;
				if(credenziali!=null && credenziali.getCertificate()!=null) {
					certificate = credenziali.getCertificate();
				}
				IJDBCAdapter jdbcAdapter = JDBCAdapterFactory.createJDBCAdapter(DriverRegistroServiziDB_LIB.tipoDB);
				jdbcAdapter.setBinaryData(updateStmt, index++, certificate);
				if(credenziali!=null && credenziali.isCertificateStrictVerification()) {
					updateStmt.setInt(index++, CostantiDB.TRUE);
				}				
				else {
					updateStmt.setInt(index++, CostantiDB.FALSE);
				}
								
				if(soggetto.getOraRegistrazione()!=null){
					updateStmt.setTimestamp(index++, new Timestamp(soggetto.getOraRegistrazione().getTime()));
				}
				
				if(utenteRichiedente!=null) {
					updateStmt.setString(index++, utenteRichiedente);
				}
				
				if(dataCreazione!=null) {
					updateStmt.setTimestamp(index++, dataCreazione);
				}

				// eseguo lo statement
				n = updateStmt.executeUpdate();
				updateStmt.close();
				DriverRegistroServiziDB_LIB.logDebug("CRUDSoggetto type = " + type + " row affected =" + n);
				DriverRegistroServiziDB_LIB.logDebug("CRUDSoggetto CREATE : \n" + DriverRegistroServiziDB_LIB.formatSQLString(updateQuery, nome, descizione, identificativoPorta, tipo, idConnettore, server));

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
				
				
				// ruoli
				
				n = 0;
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
						
						int r= updateStmt.executeUpdate();
						n++;
						updateStmt.close();
						DriverRegistroServiziDB_LIB.logDebug("CRUDSoggetto type = " + type + " row affected =" + r+" create role ["+ruoloSoggetto.getNome()+"]");
					}
				}
				
				DriverRegistroServiziDB_LIB.logDebug("Aggiunti " + n + " ruoli al soggetto "+idSoggetto);
				
				
				// credenziali (le credenziali in questa tabella partono dal numero maggiore di 1)
				
				n = 0;
				if(soggetto.sizeCredenzialiList()>1){
					for (int i = 1; i < soggetto.sizeCredenzialiList(); i++) {
						CredenzialiSoggetto credenzialiSoggetto = soggetto.getCredenziali(i);
						
						sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
						sqlQueryObject.addInsertTable(CostantiDB.SOGGETTI_CREDENZIALI);
						sqlQueryObject.addInsertField("id_soggetto", "?");
						sqlQueryObject.addInsertField("subject", "?");
						sqlQueryObject.addInsertField("cn_subject", "?");
						sqlQueryObject.addInsertField("issuer", "?");
						sqlQueryObject.addInsertField("cn_issuer", "?");
						sqlQueryObject.addInsertField("certificate", "?");
						sqlQueryObject.addInsertField("cert_strict_verification", "?");
						updateQuery = sqlQueryObject.createSQLInsert();
						updateStmt = con.prepareStatement(updateQuery);
						
						index = 1;
						updateStmt.setLong(index++, idSoggetto);
						
						String subjectCredenziali = null;
						if(credenzialiSoggetto!=null && credenzialiSoggetto.getSubject()!=null && !"".equals(credenzialiSoggetto.getSubject()))
							subjectCredenziali = credenzialiSoggetto.getSubject();
						updateStmt.setString(index++, (subjectCredenziali != null ? CertificateUtils.formatPrincipal(subjectCredenziali, PrincipalType.SUBJECT) : null));
						String subjectCredenzialiCN = null;
						if(credenzialiSoggetto!=null && credenzialiSoggetto.getCnSubject()!=null && !"".equals(credenzialiSoggetto.getCnSubject()))
							subjectCredenzialiCN = credenzialiSoggetto.getCnSubject();
						updateStmt.setString(index++, subjectCredenzialiCN);
						
						String issuerCredenziali = null;
						if(credenzialiSoggetto != null && org.openspcoop2.core.registry.constants.CredenzialeTipo.APIKEY.equals(credenzialiSoggetto.getTipo())) {
							updateStmt.setString(index++, CostantiDB.getIssuerApiKey(credenzialiSoggetto.isAppId()));
						}
						else {
							if(credenzialiSoggetto!=null && credenzialiSoggetto.getIssuer()!=null && !"".equals(credenzialiSoggetto.getIssuer()))
								issuerCredenziali = credenzialiSoggetto.getIssuer();
							updateStmt.setString(index++, (issuerCredenziali != null ? CertificateUtils.formatPrincipal(issuerCredenziali, PrincipalType.ISSUER) : null));
						}
						String issuerCredenzialiCN = null;
						if(credenzialiSoggetto!=null && credenzialiSoggetto.getCnIssuer()!=null && !"".equals(credenzialiSoggetto.getCnIssuer()))
							issuerCredenzialiCN = credenzialiSoggetto.getCnIssuer();
						updateStmt.setString(index++, issuerCredenzialiCN);
						
						byte [] certificateCredenziali = null;
						if(credenzialiSoggetto!=null && credenzialiSoggetto.getCertificate()!=null) {
							certificateCredenziali = credenzialiSoggetto.getCertificate();
						}
						jdbcAdapter = JDBCAdapterFactory.createJDBCAdapter(DriverRegistroServiziDB_LIB.tipoDB);
						jdbcAdapter.setBinaryData(updateStmt, index++, certificateCredenziali);
						if(credenzialiSoggetto!=null && credenzialiSoggetto.isCertificateStrictVerification()) {
							updateStmt.setInt(index++, CostantiDB.TRUE);
						}				
						else {
							updateStmt.setInt(index++, CostantiDB.FALSE);
						}
						
						int r = updateStmt.executeUpdate();
						n++;
						updateStmt.close();
						DriverRegistroServiziDB_LIB.logDebug("CRUDSoggetto type = " + type + " row affected =" + r+" create credenziale");
					}
					
				}
				
				DriverRegistroServiziDB_LIB.logDebug("Aggiunte " + n + " credenziali al soggetto "+idSoggetto);
				
				
				// properties
				
				n = 0;
				if(soggetto.sizeProprietaList()>0){
					for (int i = 0; i < soggetto.sizeProprietaList(); i++) {
						
						Proprieta proprieta = soggetto.getProprieta(i);
						
						sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
						sqlQueryObject.addInsertTable(CostantiDB.SOGGETTI_PROPS);
						sqlQueryObject.addInsertField("id_soggetto", "?");
						sqlQueryObject.addInsertField("nome", "?");
						sqlQueryObject.addInsertField("valore", "?");
						updateQuery = sqlQueryObject.createSQLInsert();
						updateStmt = con.prepareStatement(updateQuery);
						
						updateStmt.setLong(1, idSoggetto);
						updateStmt.setString(2, proprieta.getNome());
						updateStmt.setString(3, proprieta.getValore());
						
						int r= updateStmt.executeUpdate();
						n++;
						updateStmt.close();
						DriverRegistroServiziDB_LIB.logDebug("CRUDSoggetto type = " + type + " row affected =" + r+" create property ["+proprieta.getNome()+"]");
					}
				}
				
				DriverRegistroServiziDB_LIB.logDebug("Aggiunti " + n + " proprietà al soggetto "+idSoggetto);
				
				
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

				String utenteUltimaModifica = null;
				if(soggetto.getProprietaOggetto()!=null && soggetto.getProprietaOggetto().getUtenteUltimaModifica()!=null) {
					utenteUltimaModifica = soggetto.getProprietaOggetto().getUtenteUltimaModifica();
				}
				else {
					utenteUltimaModifica = superUser;
				}
				
				Timestamp dataUltimaModifica = null;
				if(soggetto.getProprietaOggetto()!=null && soggetto.getProprietaOggetto().getDataUltimaModifica()!=null) {
					dataUltimaModifica = new Timestamp(soggetto.getProprietaOggetto().getDataUltimaModifica().getTime());
				}
				else {
					dataUltimaModifica = DateManager.getTimestamp();
				}
				
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
				sqlQueryObject.addUpdateField("cn_subject", "?");
				sqlQueryObject.addUpdateField("issuer", "?");
				sqlQueryObject.addUpdateField("cn_issuer", "?");
				sqlQueryObject.addUpdateField("certificate", "?");
				sqlQueryObject.addUpdateField("cert_strict_verification", "?");
				if(soggetto.getOraRegistrazione()!=null)
					sqlQueryObject.addUpdateField("ora_registrazione", "?");
				if(utenteUltimaModifica!=null) {
					sqlQueryObject.addUpdateField(CostantiDB.PROPRIETA_OGGETTO_UTENTE_ULTIMA_MODIFICA, "?");
				}
				if(dataUltimaModifica!=null) {
					sqlQueryObject.addUpdateField(CostantiDB.PROPRIETA_OGGETTO_DATA_ULTIMA_MODIFICA, "?");
				}
				
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
				updateStmt.setString(index++, superUser);
				if(soggetto.getPrivato()!=null && soggetto.getPrivato())
					updateStmt.setInt(index++, 1);
				else
					updateStmt.setInt(index++, 0);
				updateStmt.setString(index++, soggetto.getVersioneProtocollo());
				updateStmt.setString(index++, codiceIPA);
				
				credenziali = (soggetto.sizeCredenzialiList() > 0 ? soggetto.getCredenziali(0) : null);
				updateStmt.setString(index++, (credenziali != null ? DriverRegistroServiziDB_LIB.getValue(credenziali.getTipo()) : null));
				updateStmt.setString(index++, (credenziali != null ? credenziali.getUser() : null));
				updateStmt.setString(index++, (credenziali != null ? credenziali.getPassword() : null));
				
				subject = null;
				if(credenziali!=null && credenziali.getSubject()!=null && !"".equals(credenziali.getSubject()))
					subject = credenziali.getSubject();
				updateStmt.setString(index++, (subject != null ? CertificateUtils.formatPrincipal(subject, PrincipalType.SUBJECT) : null));
				subjectCN = null;
				if(credenziali!=null && credenziali.getCnSubject()!=null && !"".equals(credenziali.getCnSubject()))
					subjectCN = credenziali.getCnSubject();
				updateStmt.setString(index++, subjectCN);
				
				issuer = null;
				if(credenziali != null && org.openspcoop2.core.registry.constants.CredenzialeTipo.APIKEY.equals(credenziali.getTipo())) {
					updateStmt.setString(index++, CostantiDB.getIssuerApiKey(credenziali.isAppId()));
				}
				else {
					if(credenziali!=null && credenziali.getIssuer()!=null && !"".equals(credenziali.getIssuer()))
						issuer = credenziali.getIssuer();
					updateStmt.setString(index++, (issuer != null ? CertificateUtils.formatPrincipal(issuer, PrincipalType.ISSUER) : null));
				}
				issuerCN = null;
				if(credenziali!=null && credenziali.getCnIssuer()!=null && !"".equals(credenziali.getCnIssuer()))
					issuerCN = credenziali.getCnIssuer();
				updateStmt.setString(index++, issuerCN);
				
				certificate = null;
				if(credenziali!=null && credenziali.getCertificate()!=null) {
					certificate = credenziali.getCertificate();
				}
				jdbcAdapter = JDBCAdapterFactory.createJDBCAdapter(DriverRegistroServiziDB_LIB.tipoDB);
				jdbcAdapter.setBinaryData(updateStmt, index++, certificate);
				if(credenziali!=null && credenziali.isCertificateStrictVerification()) {
					updateStmt.setInt(index++, CostantiDB.TRUE);
				}				
				else {
					updateStmt.setInt(index++, CostantiDB.FALSE);
				}
				
				if(soggetto.getOraRegistrazione()!=null){
					updateStmt.setTimestamp(index++, new Timestamp(soggetto.getOraRegistrazione().getTime()));
				}
				
				if(utenteUltimaModifica!=null) {
					updateStmt.setString(index++, utenteUltimaModifica);
				}
				
				if(dataUltimaModifica!=null) {
					updateStmt.setTimestamp(index++, dataUltimaModifica);
				}
				
				updateStmt.setLong(index++, idSoggetto);

				// eseguo lo statement
				n = updateStmt.executeUpdate();
				updateStmt.close();
				DriverRegistroServiziDB_LIB.logDebug("CRUDSoggetto type = " + type + " row affected =" + n);
				// modifico i dati del connettore
				//setto il nuovo nome
				String newNomeConnettore = "CNT_" + tipo + "_" + nome;
				connettore.setNome(newNomeConnettore);
				DriverRegistroServiziDB_connettoriLIB.CRUDConnettore(2, connettore, con);

				DriverRegistroServiziDB_LIB.logDebug("CRUDSoggetto UPDATE : \n" + DriverRegistroServiziDB_LIB.formatSQLString(updateQuery, nome, descizione, identificativoPorta, tipo, idSoggetto));

				
				// Ruoli
				
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.SOGGETTI_RUOLI);
				sqlQueryObject.addWhereCondition("id_soggetto=?");
				updateQuery = sqlQueryObject.createSQLDelete();
				updateStmt = con.prepareStatement(updateQuery);
				updateStmt.setLong(1, idSoggetto);
				n = updateStmt.executeUpdate();
				updateStmt.close();
				DriverRegistroServiziDB_LIB.logDebug("CRUDSoggetto type = " + type + " row affected =" + n+" delete roles");
				
				n = 0;
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
						
						int r = updateStmt.executeUpdate();
						n++;
						updateStmt.close();
						DriverRegistroServiziDB_LIB.logDebug("CRUDSoggetto type = " + type + " row affected =" + r+" create role ["+ruoloSoggetto.getNome()+"]");
					}
				}
				
				DriverRegistroServiziDB_LIB.logDebug("Aggiunti " + n + " ruoli al soggetto "+idSoggetto);
				
				
				// credenziali (le credenziali in questa tabella partono dal numero maggiore di 1)

				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.SOGGETTI_CREDENZIALI);
				sqlQueryObject.addWhereCondition("id_soggetto=?");
				updateQuery = sqlQueryObject.createSQLDelete();
				updateStmt = con.prepareStatement(updateQuery);
				updateStmt.setLong(1, idSoggetto);
				n = updateStmt.executeUpdate();
				updateStmt.close();
				DriverRegistroServiziDB_LIB.logDebug("CRUDSoggetto type = " + type + " row affected =" + n+" delete roles");

				n = 0;
				if(soggetto.sizeCredenzialiList()>1){
					for (int i = 1; i < soggetto.sizeCredenzialiList(); i++) {
						CredenzialiSoggetto credenzialiSoggetto = soggetto.getCredenziali(i);
						
						sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
						sqlQueryObject.addInsertTable(CostantiDB.SOGGETTI_CREDENZIALI);
						sqlQueryObject.addInsertField("id_soggetto", "?");
						sqlQueryObject.addInsertField("subject", "?");
						sqlQueryObject.addInsertField("cn_subject", "?");
						sqlQueryObject.addInsertField("issuer", "?");
						sqlQueryObject.addInsertField("cn_issuer", "?");
						sqlQueryObject.addInsertField("certificate", "?");
						sqlQueryObject.addInsertField("cert_strict_verification", "?");
						updateQuery = sqlQueryObject.createSQLInsert();
						updateStmt = con.prepareStatement(updateQuery);
						
						index = 1;
						updateStmt.setLong(index++, idSoggetto);
						
						String subjectCredenziali = null;
						if(credenzialiSoggetto!=null && credenzialiSoggetto.getSubject()!=null && !"".equals(credenzialiSoggetto.getSubject()))
							subjectCredenziali = credenzialiSoggetto.getSubject();
						updateStmt.setString(index++, (subjectCredenziali != null ? CertificateUtils.formatPrincipal(subjectCredenziali, PrincipalType.SUBJECT) : null));
						String subjectCredenzialiCN = null;
						if(credenzialiSoggetto!=null && credenzialiSoggetto.getCnSubject()!=null && !"".equals(credenzialiSoggetto.getCnSubject()))
							subjectCredenzialiCN = credenzialiSoggetto.getCnSubject();
						updateStmt.setString(index++, subjectCredenzialiCN);
						
						String issuerCredenziali = null;
						if(credenzialiSoggetto != null && org.openspcoop2.core.registry.constants.CredenzialeTipo.APIKEY.equals(credenzialiSoggetto.getTipo())) {
							updateStmt.setString(index++, CostantiDB.getIssuerApiKey(credenzialiSoggetto.isAppId()));
						}
						else {
							if(credenzialiSoggetto!=null && credenzialiSoggetto.getIssuer()!=null && !"".equals(credenzialiSoggetto.getIssuer()))
								issuerCredenziali = credenzialiSoggetto.getIssuer();
							updateStmt.setString(index++, (issuerCredenziali != null ? CertificateUtils.formatPrincipal(issuerCredenziali, PrincipalType.ISSUER) : null));
						}
						String issuerCredenzialiCN = null;
						if(credenzialiSoggetto!=null && credenzialiSoggetto.getCnIssuer()!=null && !"".equals(credenzialiSoggetto.getCnIssuer()))
							issuerCredenzialiCN = credenzialiSoggetto.getCnIssuer();
						updateStmt.setString(index++, issuerCredenzialiCN);
						
						byte [] certificateCredenziali = null;
						if(credenzialiSoggetto!=null && credenzialiSoggetto.getCertificate()!=null) {
							certificateCredenziali = credenzialiSoggetto.getCertificate();
						}
						jdbcAdapter = JDBCAdapterFactory.createJDBCAdapter(DriverRegistroServiziDB_LIB.tipoDB);
						jdbcAdapter.setBinaryData(updateStmt, index++, certificateCredenziali);
						if(credenzialiSoggetto!=null && credenzialiSoggetto.isCertificateStrictVerification()) {
							updateStmt.setInt(index++, CostantiDB.TRUE);
						}				
						else {
							updateStmt.setInt(index++, CostantiDB.FALSE);
						}
						
						int r = updateStmt.executeUpdate();
						n++;
						updateStmt.close();
						DriverRegistroServiziDB_LIB.logDebug("CRUDSoggetto type = " + type + " row affected =" + r+" create credenziale");
					}
					
				}
				
				DriverRegistroServiziDB_LIB.logDebug("Aggiunte " + n + " credenziali al soggetto "+idSoggetto);
				
				
				// properties
				
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.SOGGETTI_PROPS);
				sqlQueryObject.addWhereCondition("id_soggetto=?");
				updateQuery = sqlQueryObject.createSQLDelete();
				updateStmt = con.prepareStatement(updateQuery);
				updateStmt.setLong(1, idSoggetto);
				n = updateStmt.executeUpdate();
				updateStmt.close();
				DriverRegistroServiziDB_LIB.logDebug("CRUDSoggetto type = " + type + " row affected =" + n+" delete properties");
				
				n = 0;
				if(soggetto.sizeProprietaList()>0){
					for (int i = 0; i < soggetto.sizeProprietaList(); i++) {
						
						Proprieta proprieta = soggetto.getProprieta(i);
						
						sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
						sqlQueryObject.addInsertTable(CostantiDB.SOGGETTI_PROPS);
						sqlQueryObject.addInsertField("id_soggetto", "?");
						sqlQueryObject.addInsertField("nome", "?");
						sqlQueryObject.addInsertField("valore", "?");
						updateQuery = sqlQueryObject.createSQLInsert();
						updateStmt = con.prepareStatement(updateQuery);
						
						updateStmt.setLong(1, idSoggetto);
						updateStmt.setString(2, proprieta.getNome());
						updateStmt.setString(3, proprieta.getValore());
						
						int r= updateStmt.executeUpdate();
						n++;
						updateStmt.close();
						DriverRegistroServiziDB_LIB.logDebug("CRUDSoggetto type = " + type + " row affected =" + r+" create property ["+proprieta.getNome()+"]");
					}
				}
				
				DriverRegistroServiziDB_LIB.logDebug("Aggiunti " + n + " proprietà al soggetto "+idSoggetto);
				
				
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
				
				// elimino le proprieta' del soggetto
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.SOGGETTI_PROPS);
				sqlQueryObject.addWhereCondition("id_soggetto=?");
				updateQuery = sqlQueryObject.createSQLDelete();
				updateStmt = con.prepareStatement(updateQuery);
				updateStmt.setLong(1, idSoggetto);
				n = updateStmt.executeUpdate();
				updateStmt.close();
				DriverRegistroServiziDB_LIB.logDebug("CRUDSoggetto type = " + type + " row affected =" + n+" delete properties");
				
				// elimino le credenziali del soggetto
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.SOGGETTI_CREDENZIALI);
				sqlQueryObject.addWhereCondition("id_soggetto=?");
				updateQuery = sqlQueryObject.createSQLDelete();
				updateStmt = con.prepareStatement(updateQuery);
				updateStmt.setLong(1, idSoggetto);
				n = updateStmt.executeUpdate();
				updateStmt.close();
				DriverRegistroServiziDB_LIB.logDebug("CRUDSoggetto type = " + type + " row affected =" + n+" delete credentials");
				
				// elimino i ruoli del soggetto
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.SOGGETTI_RUOLI);
				sqlQueryObject.addWhereCondition("id_soggetto=?");
				updateQuery = sqlQueryObject.createSQLDelete();
				updateStmt = con.prepareStatement(updateQuery);
				updateStmt.setLong(1, idSoggetto);
				n = updateStmt.executeUpdate();
				updateStmt.close();
				DriverRegistroServiziDB_LIB.logDebug("CRUDSoggetto type = " + type + " row affected =" + n+" delete roles");
				
				// elimino il soggetto
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.SOGGETTI);
				sqlQueryObject.addWhereCondition("id=?");
				String sqlQuery = sqlQueryObject.createSQLDelete();
				updateStmt = con.prepareStatement(sqlQuery);
				updateStmt.setLong(1, idSoggetto);
				n=updateStmt.executeUpdate();
				updateStmt.close();
				DriverRegistroServiziDB_LIB.logDebug("CRUDSoggetto type = " + type + " row affected =" + n);

				// elimino il connettore
				connettore=new Connettore();
				connettore.setId(idConnettore);
				DriverRegistroServiziDB_connettoriLIB.CRUDConnettore(3, connettore, con);

				DriverRegistroServiziDB_LIB.logDebug("CRUDSoggetto DELETE : \n" + DriverRegistroServiziDB_LIB.formatSQLString(updateQuery, idSoggetto));

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
			JDBCUtilities.closeResources(updateStmt);
			JDBCUtilities.closeResources(selectRS, selectStmt);
		}

	}

	
}
