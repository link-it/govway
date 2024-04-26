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
package org.openspcoop2.core.config.driver.db;

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

import org.openspcoop2.core.byok.BYOKWrappedValue;
import org.openspcoop2.core.byok.IDriverBYOK;
import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.core.commons.DBUtils;
import org.openspcoop2.core.config.Connettore;
import org.openspcoop2.core.config.Credenziali;
import org.openspcoop2.core.config.InvocazioneCredenziali;
import org.openspcoop2.core.config.InvocazionePorta;
import org.openspcoop2.core.config.InvocazionePortaGestioneErrore;
import org.openspcoop2.core.config.InvocazioneServizio;
import org.openspcoop2.core.config.Proprieta;
import org.openspcoop2.core.config.RispostaAsincrona;
import org.openspcoop2.core.config.Ruolo;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.config.constants.TipologiaErogazione;
import org.openspcoop2.core.config.constants.TipologiaFruizione;
import org.openspcoop2.core.config.driver.DriverConfigurazioneException;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.constants.ProprietariProtocolProperty;
import org.openspcoop2.core.constants.TipiConnettore;
import org.openspcoop2.utils.certificate.CertificateUtils;
import org.openspcoop2.utils.certificate.PrincipalType;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.jdbc.IJDBCAdapter;
import org.openspcoop2.utils.jdbc.JDBCAdapterFactory;
import org.openspcoop2.utils.jdbc.JDBCUtilities;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.SQLObjectFactory;

/**
 * DriverConfigurazioneDB_serviziApplicativiLIB
 * 
 * @author Stefano Corallo - corallo@link.it
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DriverConfigurazioneDB_serviziApplicativiLIB {


	
	/**
	 * 
	 * @param type
	 * @param aSA
	 * @param con
	 * @return id
	 * @throws DriverConfigurazioneException
	 */
	public static long CRUDServizioApplicativo(int type, ServizioApplicativo aSA, Connection con, IDriverBYOK driverBYOK) throws DriverConfigurazioneException {
		if (aSA == null)
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDServizioApplicativo] Servizio Applicativo non valido.");

		String nomeSA = aSA.getNome();
		String tipoProprietario = aSA.getTipoSoggettoProprietario();
		String nomeProprietario = aSA.getNomeSoggettoProprietario();

		if (nomeSA == null || nomeSA.equals(""))
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDServizioApplicativo] Nome Servizio Applicativo non valido.");
		if (tipoProprietario == null || tipoProprietario.equals(""))
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDServizioApplicativo] Tipo Proprietario Servizio Applicativo non valido.");
		if (nomeProprietario == null || nomeProprietario.equals(""))
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDServizioApplicativo] Nome Proprietario Servizio Applicativo non valido.");

		PreparedStatement stm = null;
		String sqlQuery;
		PreparedStatement stmQuery = null;
		ResultSet rs = null;
		PreparedStatement stm2 = null;
		int n = 0;

		try {
			String tipoSA = aSA.getTipo();
			int useAsClient = aSA.getUseAsClient() ? CostantiDB.TRUE : CostantiDB.FALSE;
			String descrizione = aSA.getDescrizione();
			DriverConfigurazioneDBLib.logDebug("get ID Soggetto con tipo["+tipoProprietario+"] e nome["+nomeProprietario+"]");
			long idProprietario = DBUtils.getIdSoggetto(nomeProprietario, tipoProprietario, con, DriverConfigurazioneDBLib.tipoDB,DriverConfigurazioneDBLib.tabellaSoggetti);
			DriverConfigurazioneDBLib.logDebug("get ID Soggetto con tipo["+tipoProprietario+"] e nome["+nomeProprietario+"] : "+idProprietario);
			InvocazionePorta invPorta = aSA.getInvocazionePorta();
			InvocazioneServizio invServizio = aSA.getInvocazioneServizio();
			RispostaAsincrona ricezione = aSA.getRispostaAsincrona();
			List<Proprieta> proprieta = aSA.getProprietaList();

			Connettore connettoreRisp = null;
			Connettore connettoreInv = null;
			long idConnettoreRisp = 0;
			long idConnettoreInv = 0;
			long idServizioApplicativo = 0;
			Credenziali credenzialiInvocazionePorta = null;
			InvocazioneCredenziali credenzialiInvocazione = null;
			InvocazionePortaGestioneErrore gestErr = null;
			String fault = null;

			switch (type) {
			case CREATE:
				
				String utenteRichiedente = null;
				if(aSA.getProprietaOggetto()!=null && aSA.getProprietaOggetto().getUtenteRichiedente()!=null) {
					utenteRichiedente = aSA.getProprietaOggetto().getUtenteRichiedente();
				}
				else {
					utenteRichiedente = DBUtils.getSuperUserSoggettoSafe(DriverConfigurazioneDBLib.log, "CRUDPortaApplicativa",
							idProprietario, con, DriverConfigurazioneDBLib.tipoDB,DriverConfigurazioneDBLib.tabellaSoggetti);
				}
				
				Timestamp dataCreazione = null;
				if(aSA.getProprietaOggetto()!=null && aSA.getProprietaOggetto().getDataCreazione()!=null) {
					dataCreazione = new Timestamp(aSA.getProprietaOggetto().getDataCreazione().getTime());
				}
				else if(aSA.getOraRegistrazione()!=null){
					dataCreazione = new Timestamp(aSA.getOraRegistrazione().getTime());
				}
				else {
					dataCreazione = DateManager.getTimestamp();
				}
				
				// create
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
				sqlQueryObject.addInsertTable(CostantiDB.SERVIZI_APPLICATIVI);
				sqlQueryObject.addInsertField("nome", "?");
				sqlQueryObject.addInsertField("tipo", "?");
				sqlQueryObject.addInsertField("as_client", "?");
				sqlQueryObject.addInsertField("descrizione", "?");
				sqlQueryObject.addInsertField("sbustamentorisp", "?");
				sqlQueryObject.addInsertField("sbustamento_protocol_info_risp", "?");
				sqlQueryObject.addInsertField("getmsgrisp", "?");
				sqlQueryObject.addInsertField("tipoauthrisp", "?");
				sqlQueryObject.addInsertField("utenterisp", "?");
				sqlQueryObject.addInsertField("passwordrisp", "?");
				sqlQueryObject.addInsertField("enc_passwordrisp", "?");
				sqlQueryObject.addInsertField("id_connettore_risp", "?");
				sqlQueryObject.addInsertField("sbustamentoinv", "?");
				sqlQueryObject.addInsertField("sbustamento_protocol_info_inv", "?");
				sqlQueryObject.addInsertField("getmsginv", "?");
				sqlQueryObject.addInsertField("tipoauthinv", "?");
				sqlQueryObject.addInsertField("utenteinv", "?");
				sqlQueryObject.addInsertField("passwordinv", "?");
				sqlQueryObject.addInsertField("enc_passwordinv", "?");
				sqlQueryObject.addInsertField("id_connettore_inv", "?");
				sqlQueryObject.addInsertField("id_soggetto", "?");
				sqlQueryObject.addInsertField("fault", "?");
				sqlQueryObject.addInsertField("tipoauth", "?");
				sqlQueryObject.addInsertField("utente", "?");
				sqlQueryObject.addInsertField("password", "?");
				sqlQueryObject.addInsertField("subject", "?");
				sqlQueryObject.addInsertField("cn_subject", "?");
				sqlQueryObject.addInsertField("issuer", "?");
				sqlQueryObject.addInsertField("cn_issuer", "?");
				sqlQueryObject.addInsertField("certificate", "?");
				sqlQueryObject.addInsertField("cert_strict_verification", "?");
				sqlQueryObject.addInsertField("token_policy", "?");
				sqlQueryObject.addInsertField("invio_x_rif_inv", "?");
				sqlQueryObject.addInsertField("risposta_x_rif_inv", "?");
				sqlQueryObject.addInsertField("invio_x_rif", "?");
				sqlQueryObject.addInsertField("invio_x_rif_risp", "?");
				sqlQueryObject.addInsertField("risposta_x_rif_risp", "?");
				sqlQueryObject.addInsertField("sbustamento_protocol_info", "?");
				sqlQueryObject.addInsertField("fault_actor", "?");
				sqlQueryObject.addInsertField("generic_fault_code", "?");
				sqlQueryObject.addInsertField("prefix_fault_code", "?");
				sqlQueryObject.addInsertField("tipologia_fruizione", "?");
				sqlQueryObject.addInsertField("tipologia_erogazione", "?");
				if(utenteRichiedente!=null) {
					sqlQueryObject.addInsertField(CostantiDB.PROPRIETA_OGGETTO_UTENTE_RICHIEDENTE, "?");
				}
				if(dataCreazione!=null) {
					sqlQueryObject.addInsertField(CostantiDB.PROPRIETA_OGGETTO_DATA_CREAZIONE, "?");
				}
				sqlQuery = sqlQueryObject.createSQLInsert();
				stm = con.prepareStatement(sqlQuery);

				// creo i connettori, ma disabilitati

				// connettore risp
				//il nome del connettore deve essere univoco Connettore[RISP o INV]_nomeSA+tipoSoggetto+nomeSoggetto
				connettoreRisp = new Connettore();
				connettoreRisp.setNome("ConnettoreRISP_" + aSA.getNome()+"_"+aSA.getTipoSoggettoProprietario()+aSA.getNomeSoggettoProprietario());
				connettoreRisp.setTipo(TipiConnettore.DISABILITATO.getNome());
				//Creo il connettore disabilitato
				idConnettoreRisp = DriverConfigurazioneDB_connettoriLIB.CRUDConnettore(1, connettoreRisp, con, driverBYOK);
				//Se il connettore mi era stato passato allora devo aggiornare il connettore con i dati giusti
				if(ricezione!=null && ricezione.getConnettore()!=null){
					Connettore connettore= ricezione.getConnettore();
					//setto l'id del connettore e il nome che aveva prima
					connettore.setId(idConnettoreRisp);
					connettore.setNome(connettoreRisp.getNome());//il nome DEVE essere quello creato in precedenza per assicurarsi che sia univoco
					DriverConfigurazioneDB_connettoriLIB.CRUDConnettore(CostantiDB.UPDATE, connettore, con, driverBYOK);
				}

				// connettore inv
				connettoreInv = new Connettore();
				connettoreInv.setNome("ConnettoreINV_" + aSA.getNome()+"_"+aSA.getTipoSoggettoProprietario()+aSA.getNomeSoggettoProprietario());
				connettoreInv.setTipo(TipiConnettore.DISABILITATO.getNome());
				idConnettoreInv = DriverConfigurazioneDB_connettoriLIB.CRUDConnettore(1, connettoreInv, con, driverBYOK);

				//setto i valori corretti del connettore se mi era stato passato
				if(invServizio!=null && invServizio.getConnettore()!=null){
					Connettore connettore = invServizio.getConnettore();
					connettore.setId(idConnettoreInv);
					connettore.setNome(connettoreInv.getNome());//il nome DEVE essere quello creato in precedenza per assicurarsi che sia univoco
					DriverConfigurazioneDB_connettoriLIB.CRUDConnettore(CostantiDB.UPDATE, connettore, con, driverBYOK);
				}

				int index = 1;
				
				stm.setString(index++, nomeSA);
				stm.setString(index++, tipoSA);
				stm.setInt(index++, useAsClient);
				stm.setString(index++, descrizione);

				// RicezioneRisposta
				stm.setInt(index++, (ricezione != null && (CostantiConfigurazione.ABILITATO.equals(ricezione.getSbustamentoSoap())) ? CostantiDB.TRUE : CostantiDB.FALSE));
				stm.setInt(index++, (ricezione != null && (!CostantiConfigurazione.DISABILITATO.equals(ricezione.getSbustamentoInformazioniProtocollo())) ? CostantiDB.TRUE : CostantiDB.FALSE));
				stm.setString(index++, ricezione != null ? DriverConfigurazioneDBLib.getValue(ricezione.getGetMessage()) : null);
				// setto credenziali risp
				credenzialiInvocazione = ricezione != null ? ricezione.getCredenziali() : null;
				stm.setString(index++, (ricezione != null ? DriverConfigurazioneDBLib.getValue(ricezione.getAutenticazione()) : null)); //l'autenticazione e' quella della risposta asincrona
				stm.setString(index++, (credenzialiInvocazione != null ? credenzialiInvocazione.getUser() : null));
				
				String plainValue = (credenzialiInvocazione != null ? credenzialiInvocazione.getPassword() : null);
				String encValue = null;
				if(driverBYOK!=null && plainValue!=null) {
					BYOKWrappedValue byokValue = driverBYOK.wrap(plainValue);
					if(byokValue!=null) {
						encValue = byokValue.getWrappedValue();
						plainValue = byokValue.getWrappedPlainValue();
					}
				}
				stm.setString(index++, plainValue);
				stm.setString(index++, encValue);
				
				// setto idconnettore risp
				stm.setLong(index++, idConnettoreRisp);

				// InvocazioneServizio
				stm.setInt(index++, (invServizio != null && (CostantiConfigurazione.ABILITATO.equals(invServizio.getSbustamentoSoap())) ? CostantiDB.TRUE : CostantiDB.FALSE));
				stm.setInt(index++, (invServizio != null && (!CostantiConfigurazione.DISABILITATO.equals(invServizio.getSbustamentoInformazioniProtocollo())) ? CostantiDB.TRUE : CostantiDB.FALSE));
				stm.setString(index++, invServizio != null ? DriverConfigurazioneDBLib.getValue(invServizio.getGetMessage()) : null);
				// setto credenziali inv
				credenzialiInvocazione = invServizio != null ? invServizio.getCredenziali() : null;
				stm.setString(index++, (invServizio != null ? DriverConfigurazioneDBLib.getValue(invServizio.getAutenticazione()) : null));//l'autenticazione e' quella dell invocazione servizio
				stm.setString(index++, (credenzialiInvocazione != null ? credenzialiInvocazione.getUser() : null));
				
				plainValue = (credenzialiInvocazione != null ? credenzialiInvocazione.getPassword() : null);
				encValue = null;
				if(driverBYOK!=null && plainValue!=null) {
					BYOKWrappedValue byokValue = driverBYOK.wrap(plainValue);
					if(byokValue!=null) {
						encValue = byokValue.getWrappedValue();
						plainValue = byokValue.getWrappedPlainValue();
					}
				}
				stm.setString(index++, plainValue);
				stm.setString(index++, encValue);
				
				// setto idconnettore inv
				stm.setLong(index++, idConnettoreInv);

				// idsoggetto proprietario
				stm.setLong(index++, idProprietario);

				// InvocazionePorta
				gestErr = invPorta != null ? invPorta.getGestioneErrore() : null;
				fault = (gestErr != null ? DriverConfigurazioneDBLib.getValue(gestErr.getFault()) : null);
				stm.setString(index++, fault);
				// setto credenziali invocaizone porta
				// per il momento c'e' soltato una credenziale,quindi un solo
				// oggetto nella lista
				credenzialiInvocazionePorta = (invPorta != null && invPorta.sizeCredenzialiList() > 0 ? invPorta.getCredenziali(0) : null);
				stm.setString(index++, (credenzialiInvocazionePorta != null ? DriverConfigurazioneDBLib.getValue(credenzialiInvocazionePorta.getTipo()) : null));
				stm.setString(index++, (credenzialiInvocazionePorta != null ? credenzialiInvocazionePorta.getUser() : null));
				stm.setString(index++, (credenzialiInvocazionePorta != null ? credenzialiInvocazionePorta.getPassword() : null));
				
				String subject = null;
				if(credenzialiInvocazionePorta!=null && credenzialiInvocazionePorta.getSubject()!=null && !"".equals(credenzialiInvocazionePorta.getSubject()))
					subject = credenzialiInvocazionePorta.getSubject();
				stm.setString(index++, (subject != null ? CertificateUtils.formatPrincipal(subject, PrincipalType.SUBJECT) : null));
				String subjectCN = null;
				if(credenzialiInvocazionePorta!=null && credenzialiInvocazionePorta.getCnSubject()!=null && !"".equals(credenzialiInvocazionePorta.getCnSubject()))
					subjectCN = credenzialiInvocazionePorta.getCnSubject();
				stm.setString(index++, subjectCN);
				
				String issuer = null;
				if(credenzialiInvocazionePorta != null && org.openspcoop2.core.config.constants.CredenzialeTipo.APIKEY.equals(credenzialiInvocazionePorta.getTipo())) {
					stm.setString(index++, CostantiDB.getIssuerApiKey(credenzialiInvocazionePorta.isAppId()));
				}
				else {
					if(credenzialiInvocazionePorta!=null && credenzialiInvocazionePorta.getIssuer()!=null && !"".equals(credenzialiInvocazionePorta.getIssuer()))
						issuer = credenzialiInvocazionePorta.getIssuer();
					stm.setString(index++, (issuer != null ? CertificateUtils.formatPrincipal(issuer, PrincipalType.ISSUER) : null));
				}
				String issuerCN = null;
				if(credenzialiInvocazionePorta!=null && credenzialiInvocazionePorta.getCnIssuer()!=null && !"".equals(credenzialiInvocazionePorta.getCnIssuer()))
					issuerCN = credenzialiInvocazionePorta.getCnIssuer();
				stm.setString(index++, issuerCN);
				
				byte [] certificate = null;
				if(credenzialiInvocazionePorta!=null && credenzialiInvocazionePorta.getCertificate()!=null) {
					certificate = credenzialiInvocazionePorta.getCertificate();
				}
				IJDBCAdapter jdbcAdapter = JDBCAdapterFactory.createJDBCAdapter(DriverConfigurazioneDBLib.tipoDB);
				jdbcAdapter.setBinaryData(stm, index++, certificate);
				if(credenzialiInvocazionePorta!=null && credenzialiInvocazionePorta.isCertificateStrictVerification()) {
					stm.setInt(index++, CostantiDB.TRUE);
				}				
				else {
					stm.setInt(index++, CostantiDB.FALSE);
				}
				
				stm.setString(index++, credenzialiInvocazionePorta!=null ? credenzialiInvocazionePorta.getTokenPolicy() : null);

				// aggiungo gestione invio/risposta per riferimento
				// invocazione servizio
				stm.setString(index++, invServizio != null ? DriverConfigurazioneDBLib.getValue(invServizio.getInvioPerRiferimento()) : null);
				stm.setString(index++, invServizio != null ? DriverConfigurazioneDBLib.getValue(invServizio.getRispostaPerRiferimento()) : null);
				// invocazione porta
				stm.setString(index++, invPorta != null ? DriverConfigurazioneDBLib.getValue(invPorta.getInvioPerRiferimento()) : null);
				// ricezione risposta
				stm.setString(index++, ricezione != null ? DriverConfigurazioneDBLib.getValue(ricezione.getInvioPerRiferimento()) : null);
				stm.setString(index++, ricezione != null ? DriverConfigurazioneDBLib.getValue(ricezione.getRispostaPerRiferimento()) : null);
				// sbustamento info protocolo
				stm.setInt(index++, (invPorta != null && (!CostantiConfigurazione.DISABILITATO.equals(invPorta.getSbustamentoInformazioniProtocollo())) ? CostantiDB.TRUE : CostantiDB.FALSE));
				//Invocazione Porta : fault_actor, generic_fault_code, prefix_fault_code
				stm.setString(index++, gestErr!=null ? gestErr.getFaultActor() : null);
				stm.setString(index++, gestErr!=null ? DriverConfigurazioneDBLib.getValue(gestErr.getGenericFaultCode()) : null);
				stm.setString(index++, gestErr!=null ? gestErr.getPrefixFaultCode() : null);
								
				//tipologia erogazione/fruizione
				stm.setString(index++, aSA.getTipologiaFruizione()!=null ? TipologiaFruizione.valueOf(aSA.getTipologiaFruizione().toUpperCase()).toString() : TipologiaFruizione.DISABILITATO.toString());
				stm.setString(index++, aSA.getTipologiaErogazione()!=null ? TipologiaErogazione.valueOf(aSA.getTipologiaErogazione().toUpperCase()).toString() : TipologiaErogazione.DISABILITATO.toString());
				
				if(utenteRichiedente!=null) {
					stm.setString(index++, utenteRichiedente);
				}
				
				if(dataCreazione!=null) {
					stm.setTimestamp(index++, dataCreazione);
				}
				
				n = stm.executeUpdate();
				stm.close();
				DriverConfigurazioneDBLib.logDebug("Inserted " + n + " row(s)");

				try {
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
					sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
					sqlQueryObject.addSelectField("id");
					sqlQueryObject.addWhereCondition("id_soggetto = ?");
					sqlQueryObject.addWhereCondition("nome = ?");
					sqlQueryObject.setANDLogicOperator(true);
					sqlQuery = sqlQueryObject.createSQLQuery();
					stmQuery = con.prepareStatement(sqlQuery);
					stmQuery.setLong(1, idProprietario);
					stmQuery.setString(2, nomeSA);
	
					rs = stmQuery.executeQuery();
	
					if (rs.next()) {
						idServizioApplicativo = rs.getLong("id");
						aSA.setId(idServizioApplicativo);
					} else {
						throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDServizioApplicativo(CREATE)] Impossibile trovare il servizio appena creato.");
					}
				}finally {
					if(rs!=null) {
						rs.close();
						rs = null;
					}
					if(stmQuery!=null) {
						stmQuery.close();
						stmQuery = null;
					}
				}
				
				// GestioneErrore
				if(aSA.getRispostaAsincrona()!=null && aSA.getRispostaAsincrona().getGestioneErrore()!=null){
					
					DriverConfigurazioneDB_gestioneErroreLIB.CRUDGestioneErroreServizioApplicativo(CostantiDB.CREATE, 
							aSA.getRispostaAsincrona().getGestioneErrore(), idProprietario, idServizioApplicativo, false, con);
					
					try {
						ISQLQueryObject sqlQueryObjectUpdate = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
						sqlQueryObjectUpdate.addUpdateTable(CostantiDB.SERVIZI_APPLICATIVI);
						sqlQueryObjectUpdate.addUpdateField("id_gestione_errore_risp", "?");
						sqlQueryObjectUpdate.addWhereCondition("id = ?");
						stm2 = con.prepareStatement(sqlQueryObjectUpdate.createSQLUpdate());
						stm2.setLong(1, aSA.getRispostaAsincrona().getGestioneErrore().getId());
						stm2.setLong(2, idServizioApplicativo);
						stm2.executeUpdate();
					}finally {
						if(stm2!=null) {
							stm2.close();
							stm2 = null;
						}
					}
					
				}
				
				// GestioneErrore
				if(aSA.getInvocazioneServizio()!=null && aSA.getInvocazioneServizio().getGestioneErrore()!=null){
					
					DriverConfigurazioneDB_gestioneErroreLIB.CRUDGestioneErroreServizioApplicativo(CostantiDB.CREATE, 
							aSA.getInvocazioneServizio().getGestioneErrore(), idProprietario, idServizioApplicativo, true, con);
					
					try {
						ISQLQueryObject sqlQueryObjectUpdate = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
						sqlQueryObjectUpdate.addUpdateTable(CostantiDB.SERVIZI_APPLICATIVI);
						sqlQueryObjectUpdate.addUpdateField("id_gestione_errore_inv", "?");
						sqlQueryObjectUpdate.addWhereCondition("id = ?");
						stm2 = con.prepareStatement(sqlQueryObjectUpdate.createSQLUpdate());
						stm2.setLong(1, aSA.getInvocazioneServizio().getGestioneErrore().getId());
						stm2.setLong(2, idServizioApplicativo);
						stm2.executeUpdate();
					}finally {
						if(stm2!=null) {
							stm2.close();
							stm2 = null;
						}
					}
				}
				
				// Ruoli
				n=0;
				if(invPorta!=null && invPorta.getRuoli()!=null && invPorta.getRuoli().sizeRuoloList()>0){
					for (int i = 0; i < invPorta.getRuoli().sizeRuoloList(); i++) {
						Ruolo ruolo = invPorta.getRuoli().getRuolo(i);
						sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
						sqlQueryObject.addInsertTable(CostantiDB.SERVIZI_APPLICATIVI_RUOLI);
						sqlQueryObject.addInsertField("id_servizio_applicativo", "?");
						sqlQueryObject.addInsertField("ruolo", "?");
						sqlQuery = sqlQueryObject.createSQLInsert();
						stm = con.prepareStatement(sqlQuery);
						stm.setLong(1, aSA.getId());
						stm.setString(2, ruolo.getNome());
						stm.executeUpdate();
						stm.close();
						n++;
						DriverConfigurazioneDBLib.logDebug("Aggiunto ruolo[" + ruolo.getNome() + "] al servizioApplicativo "+idServizioApplicativo);
					}
				}
				
				DriverConfigurazioneDBLib.logDebug("Aggiunti " + n + " ruoli al servizioApplicativo "+idServizioApplicativo);
				
				
				// Credenziali (le credenziali in questa tabella partono dal numero maggiore di 1)
				n=0;
				if(invPorta!=null && invPorta.sizeCredenzialiList()>1){
					for (int i = 1; i < invPorta.sizeCredenzialiList(); i++) {
						Credenziali credenziale = invPorta.getCredenziali(i);
						sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
						sqlQueryObject.addInsertTable(CostantiDB.SERVIZI_APPLICATIVI_CREDENZIALI);
						sqlQueryObject.addInsertField("id_servizio_applicativo", "?");
						sqlQueryObject.addInsertField("subject", "?");
						sqlQueryObject.addInsertField("cn_subject", "?");
						sqlQueryObject.addInsertField("issuer", "?");
						sqlQueryObject.addInsertField("cn_issuer", "?");
						sqlQueryObject.addInsertField("certificate", "?");
						sqlQueryObject.addInsertField("cert_strict_verification", "?");
						sqlQuery = sqlQueryObject.createSQLInsert();
						stm = con.prepareStatement(sqlQuery);
						
						index = 1;
						stm.setLong(index++, aSA.getId());
						
						String subjectCredenziale = null;
						if(credenziale!=null && credenziale.getSubject()!=null && !"".equals(credenziale.getSubject()))
							subjectCredenziale = credenziale.getSubject();
						stm.setString(index++, (subjectCredenziale != null ? CertificateUtils.formatPrincipal(subjectCredenziale, PrincipalType.SUBJECT) : null));
						String subjectCredenzialeCN = null;
						if(credenziale!=null && credenziale.getCnSubject()!=null && !"".equals(credenziale.getCnSubject()))
							subjectCredenzialeCN = credenziale.getCnSubject();
						stm.setString(index++, subjectCredenzialeCN);
						
						String issuerCredenziale = null;
						if(credenziale != null && org.openspcoop2.core.config.constants.CredenzialeTipo.APIKEY.equals(credenziale.getTipo())) {
							stm.setString(index++, CostantiDB.getIssuerApiKey(credenziale.isAppId()));
						}
						else {
							if(credenziale!=null && credenziale.getIssuer()!=null && !"".equals(credenziale.getIssuer()))
								issuerCredenziale = credenziale.getIssuer();
							stm.setString(index++, (issuerCredenziale != null ? CertificateUtils.formatPrincipal(issuerCredenziale, PrincipalType.ISSUER) : null));
						}
						String issuerCredenzialeCN = null;
						if(credenziale!=null && credenziale.getCnIssuer()!=null && !"".equals(credenziale.getCnIssuer()))
							issuerCredenzialeCN = credenziale.getCnIssuer();
						stm.setString(index++, issuerCredenzialeCN);
						
						byte [] certificateCredenziale = null;
						if(credenziale!=null && credenziale.getCertificate()!=null) {
							certificateCredenziale = credenziale.getCertificate();
						}
						jdbcAdapter = JDBCAdapterFactory.createJDBCAdapter(DriverConfigurazioneDBLib.tipoDB);
						jdbcAdapter.setBinaryData(stm, index++, certificateCredenziale);
						if(credenziale!=null && credenziale.isCertificateStrictVerification()) {
							stm.setInt(index++, CostantiDB.TRUE);
						}				
						else {
							stm.setInt(index++, CostantiDB.FALSE);
						}
						
						stm.executeUpdate();
						stm.close();
						n++;
						DriverConfigurazioneDBLib.logDebug("Aggiunta credenziale al servizioApplicativo "+idServizioApplicativo);
					}
				}
				
				DriverConfigurazioneDBLib.logDebug("Aggiunte " + n + " credenziali al servizioApplicativo "+idServizioApplicativo);
				
				
				// Proprieta
				n=0;
				if(proprieta!=null && !proprieta.isEmpty()){
					for (int i = 0; i < proprieta.size(); i++) {
						Proprieta prop = proprieta.get(i);
						
						sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
						sqlQueryObject.addInsertTable(CostantiDB.SERVIZI_APPLICATIVI_PROPS);
						sqlQueryObject.addInsertField("id_servizio_applicativo", "?");
						sqlQueryObject.addInsertField("nome", "?");
						sqlQueryObject.addInsertField("valore", "?");
						sqlQuery = sqlQueryObject.createSQLInsert();
						stm = con.prepareStatement(sqlQuery);
						stm.setLong(1, aSA.getId());
						stm.setString(2, prop.getNome());
						stm.setString(3, prop.getValore());
						stm.executeUpdate();
						stm.close();
						n++;
						DriverConfigurazioneDBLib.logDebug("Aggiunta proprieta' [" + prop.getNome() + "] al servizioApplicativo "+idServizioApplicativo);
					}
				}
				
				DriverConfigurazioneDBLib.logDebug("Aggiunte " + n + " proprieta' al servizioApplicativo "+idServizioApplicativo);
				
				
				// ProtocolProperties
				DriverConfigurazioneDBLib.crudProtocolProperty(CostantiDB.CREATE, aSA.getProtocolPropertyList(), 
						idServizioApplicativo, ProprietariProtocolProperty.SERVIZIO_APPLICATIVO, con, DriverConfigurazioneDBLib.tipoDB, driverBYOK);
				
				
				break;

			case UPDATE:
				String oldNomeSA = null;
				String oldNomeProprietario = null;
				String oldTipoProprietario = null;
				if(aSA.getOldIDServizioApplicativoForUpdate()!=null){
					oldNomeSA = aSA.getOldIDServizioApplicativoForUpdate().getNome();
					if(aSA.getOldIDServizioApplicativoForUpdate().getIdSoggettoProprietario()!=null){
						oldNomeProprietario = aSA.getOldIDServizioApplicativoForUpdate().getIdSoggettoProprietario().getNome();
						oldTipoProprietario = aSA.getOldIDServizioApplicativoForUpdate().getIdSoggettoProprietario().getTipo();
					}
				}

				if (oldNomeSA == null || oldNomeSA.equals(""))
					oldNomeSA = nomeSA;
				if (oldNomeProprietario == null || oldNomeProprietario.equals(""))
					oldNomeProprietario = nomeProprietario;
				if (oldTipoProprietario == null || oldTipoProprietario.equals(""))
					oldTipoProprietario = tipoProprietario;

				long oldIdProprietario = DBUtils.getIdSoggetto(oldNomeProprietario, oldTipoProprietario, con, DriverConfigurazioneDBLib.tipoDB,DriverConfigurazioneDBLib.tabellaSoggetti);
				// Puo' darsi che l'old soggetto e il nuovo soggetto siano la stesso soggetto della tabella. E' stato cambiato il nome.
				if(oldIdProprietario <=0) {
					oldIdProprietario = DBUtils.getIdSoggetto(nomeProprietario, tipoProprietario, con, DriverConfigurazioneDBLib.tipoDB,DriverConfigurazioneDBLib.tabellaSoggetti);
				}
				if(oldIdProprietario <=0) 
					throw new DriverConfigurazioneException("Impossibile recuperare l'id del Soggetto Proprietario del Servizio Applicativo");
				
				String utenteUltimaModifica = null;
				if(aSA.getProprietaOggetto()!=null && aSA.getProprietaOggetto().getUtenteUltimaModifica()!=null) {
					utenteUltimaModifica = aSA.getProprietaOggetto().getUtenteUltimaModifica();
				}
				
				Timestamp dataUltimaModifica = null;
				if(aSA.getProprietaOggetto()!=null && aSA.getProprietaOggetto().getDataUltimaModifica()!=null) {
					dataUltimaModifica = new Timestamp(aSA.getProprietaOggetto().getDataUltimaModifica().getTime());
				}
				else {
					dataUltimaModifica = DateManager.getTimestamp();
				}
				
				
				// update
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
				sqlQueryObject.addUpdateTable(CostantiDB.SERVIZI_APPLICATIVI);
				sqlQueryObject.addUpdateField("tipo", "?");
				sqlQueryObject.addUpdateField("as_client", "?");
				sqlQueryObject.addUpdateField("descrizione", "?");
				sqlQueryObject.addUpdateField("sbustamentorisp", "?");
				sqlQueryObject.addUpdateField("sbustamento_protocol_info_risp", "?");
				sqlQueryObject.addUpdateField("getmsgrisp", "?");
				sqlQueryObject.addUpdateField("tipoauthrisp", "?");
				sqlQueryObject.addUpdateField("utenterisp", "?");
				sqlQueryObject.addUpdateField("passwordrisp", "?");
				sqlQueryObject.addUpdateField("enc_passwordrisp", "?");
				sqlQueryObject.addUpdateField("id_connettore_risp", "?");
				sqlQueryObject.addUpdateField("sbustamentoinv", "?");
				sqlQueryObject.addUpdateField("sbustamento_protocol_info_inv", "?");
				sqlQueryObject.addUpdateField("getmsginv", "?");
				sqlQueryObject.addUpdateField("tipoauthinv", "?");
				sqlQueryObject.addUpdateField("utenteinv", "?");
				sqlQueryObject.addUpdateField("passwordinv", "?");
				sqlQueryObject.addUpdateField("enc_passwordinv", "?");
				sqlQueryObject.addUpdateField("id_connettore_inv", "?");
				sqlQueryObject.addUpdateField("fault", "?");
				sqlQueryObject.addUpdateField("tipoauth", "?");
				sqlQueryObject.addUpdateField("utente", "?");
				sqlQueryObject.addUpdateField("password", "?");
				sqlQueryObject.addUpdateField("subject", "?");
				sqlQueryObject.addUpdateField("cn_subject", "?");
				sqlQueryObject.addUpdateField("issuer", "?");
				sqlQueryObject.addUpdateField("cn_issuer", "?");
				sqlQueryObject.addUpdateField("certificate", "?");
				sqlQueryObject.addUpdateField("cert_strict_verification", "?");
				sqlQueryObject.addUpdateField("token_policy", "?");
				sqlQueryObject.addUpdateField("invio_x_rif_inv", "?");
				sqlQueryObject.addUpdateField("risposta_x_rif_inv", "?");
				sqlQueryObject.addUpdateField("invio_x_rif", "?");
				sqlQueryObject.addUpdateField("invio_x_rif_risp", "?");
				sqlQueryObject.addUpdateField("risposta_x_rif_risp", "?");
				sqlQueryObject.addUpdateField("sbustamento_protocol_info", "?");
				sqlQueryObject.addUpdateField("fault_actor", "?");
				sqlQueryObject.addUpdateField("generic_fault_code", "?");
				sqlQueryObject.addUpdateField("prefix_fault_code", "?");
				sqlQueryObject.addUpdateField("nome", "?");
				sqlQueryObject.addUpdateField("id_soggetto", "?");
				if(aSA.getRispostaAsincrona()!=null && aSA.getRispostaAsincrona().getGestioneErrore()!=null){
					sqlQueryObject.addUpdateField("id_gestione_errore_risp", "?");
				}
				if(aSA.getInvocazioneServizio()!=null && aSA.getInvocazioneServizio().getGestioneErrore()!=null){
					sqlQueryObject.addUpdateField("id_gestione_errore_inv", "?");
				}
				sqlQueryObject.addUpdateField("tipologia_fruizione", "?");
				sqlQueryObject.addUpdateField("tipologia_erogazione", "?");
				if(utenteUltimaModifica!=null) {
					sqlQueryObject.addUpdateField(CostantiDB.PROPRIETA_OGGETTO_UTENTE_ULTIMA_MODIFICA, "?");
				}
				if(dataUltimaModifica!=null) {
					sqlQueryObject.addUpdateField(CostantiDB.PROPRIETA_OGGETTO_DATA_ULTIMA_MODIFICA, "?");
				}
				sqlQueryObject.addWhereCondition("id=?");
				sqlQueryObject.addWhereCondition("nome=?");
				sqlQueryObject.addWhereCondition("id_soggetto=?");
				sqlQueryObject.setANDLogicOperator(true);
				sqlQuery = sqlQueryObject.createSQLUpdate();
				stm = con.prepareStatement(sqlQuery);

				idServizioApplicativo = getIdServizioApplicativo(oldNomeSA, oldTipoProprietario, oldNomeProprietario, con, DriverConfigurazioneDBLib.tipoDB,DriverConfigurazioneDBLib.tabellaSoggetti);
				// Puo' darsi che l'old soggetto e il nuovo soggetto siano la stesso soggetto della tabella. E' stato cambiato il nome.
				if(idServizioApplicativo<=0) {
					idServizioApplicativo = getIdServizioApplicativo(oldNomeSA, tipoProprietario, nomeProprietario, con, DriverConfigurazioneDBLib.tipoDB,DriverConfigurazioneDBLib.tabellaSoggetti);
				}
				if (idServizioApplicativo <= 0)
					throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDServizioApplicativo(UPDATE)] ID del ServizioApplicativo necessario per l'aggiornamento.");
				// recupero i connettori

				// connettore risp asinc				
				RispostaAsincrona rispAsin = aSA.getRispostaAsincrona();
				connettoreRisp = rispAsin != null ? rispAsin.getConnettore() : new Connettore();
				String newNomeConnettoreRisp = "ConnettoreRISP_"+nomeSA+"_"+tipoProprietario+nomeProprietario;
				idConnettoreRisp = DriverConfigurazioneDB_connettoriLIB.getIdConnettoreSARISP(idServizioApplicativo, con);
				
				// connettore inv servizio
				InvocazioneServizio invServ = aSA.getInvocazioneServizio();
				connettoreInv = invServ != null ? invServizio.getConnettore() : new Connettore();
				String newNomeConnettoreInv = "ConnettoreINV_"+nomeSA+"_"+tipoProprietario+nomeProprietario;
				idConnettoreInv = DriverConfigurazioneDB_connettoriLIB.getIdConnettoreSAINV(idServizioApplicativo, con);
				
				//Controllo consistenza degli id dei connettori in quanto devono essere specificati
				//e quindi maggiori di 0
				if(idConnettoreInv <=0 || idConnettoreRisp<=0) throw new DriverConfigurazioneException("I connettori del servizio applicativo sono invalidi");
				
				/*
				 * Problema:
				 * 	Se il nuovo connettore e' disabilitato (e il nome del connettore non cambia)
				 * 	il valore presente sul db non cambia anche se questo valore e' != da DISABILITATO
				 * Fix:
				 * 	I valori del nuovo connettore devono essere sempre aggiornati
				 *   
				 */

				String nomeConnettoreRisp = DriverConfigurazioneDB_connettoriLIB.getConnettore(idConnettoreRisp, con, null).getNome();
				String nomeConnettoreInv = DriverConfigurazioneDB_connettoriLIB.getConnettore(idConnettoreInv, con, null).getNome();

				String pattern = "Aggiorno Connettore [{0}] : id [{1}] oldNome [{2}] newNome [{2}]";

				DriverConfigurazioneDBLib.logDebug(MessageFormat.format(pattern, "Risposta Asincrona",idConnettoreRisp, nomeConnettoreRisp, newNomeConnettoreRisp));
				//aggiorno connettore risp
				connettoreRisp.setNome(newNomeConnettoreRisp);
				connettoreRisp.setId(idConnettoreRisp);
				DriverConfigurazioneDB_connettoriLIB.CRUDConnettore(CostantiDB.UPDATE, connettoreRisp, con, driverBYOK);

				//aggiorno connettore inv
				DriverConfigurazioneDBLib.logDebug(MessageFormat.format(pattern, "Invocazione Servizio",idConnettoreInv, nomeConnettoreInv, newNomeConnettoreInv));
				connettoreInv.setNome(newNomeConnettoreInv);
				connettoreInv.setId(idConnettoreInv);
				DriverConfigurazioneDB_connettoriLIB.CRUDConnettore(CostantiDB.UPDATE, connettoreInv, con, driverBYOK);


				// Setto i dati del ServizioApplicativo

				index = 1;
				
				stm.setString(index++, tipoSA);
				stm.setInt(index++, useAsClient);
				stm.setString(index++, descrizione);

				// RicezioneRisposta
				stm.setInt(index++, (ricezione != null && (CostantiConfigurazione.ABILITATO.equals(ricezione.getSbustamentoSoap())) ? CostantiDB.TRUE : CostantiDB.FALSE));
				stm.setInt(index++, (ricezione != null && (!CostantiConfigurazione.DISABILITATO.equals(ricezione.getSbustamentoInformazioniProtocollo())) ? CostantiDB.TRUE : CostantiDB.FALSE));
				stm.setString(index++, ricezione != null ? DriverConfigurazioneDBLib.getValue(ricezione.getGetMessage()) : null);
				// setto credenziali risp
				credenzialiInvocazione = ricezione != null ? ricezione.getCredenziali() : null;
				stm.setString(index++, (ricezione != null ? DriverConfigurazioneDBLib.getValue(ricezione.getAutenticazione()) : null));
				stm.setString(index++, (credenzialiInvocazione != null ? credenzialiInvocazione.getUser() : null));
				
				plainValue = (credenzialiInvocazione != null ? credenzialiInvocazione.getPassword() : null);
				encValue = null;
				if(driverBYOK!=null && plainValue!=null) {
					BYOKWrappedValue byokValue = driverBYOK.wrap(plainValue);
					if(byokValue!=null) {
						encValue = byokValue.getWrappedValue();
						plainValue = byokValue.getWrappedPlainValue();
					}
				}
				stm.setString(index++, plainValue);
				stm.setString(index++, encValue);
				
				// setto idconnettore risp
				stm.setLong(index++, idConnettoreRisp);

				// InvocazioneServizio
				stm.setInt(index++, (invServizio != null && (CostantiConfigurazione.ABILITATO.equals(invServizio.getSbustamentoSoap())) ? CostantiDB.TRUE : CostantiDB.FALSE));
				stm.setInt(index++, (invServizio != null && (!CostantiConfigurazione.DISABILITATO.equals(invServizio.getSbustamentoInformazioniProtocollo())) ? CostantiDB.TRUE : CostantiDB.FALSE));
				stm.setString(index++, invServizio != null ? DriverConfigurazioneDBLib.getValue(invServizio.getGetMessage()) : null);
				// setto credenziali inv
				credenzialiInvocazione = invServizio != null ? invServizio.getCredenziali() : null;
				stm.setString(index++, (invServizio != null ? DriverConfigurazioneDBLib.getValue(invServizio.getAutenticazione()) : null));
				stm.setString(index++, (credenzialiInvocazione != null ? credenzialiInvocazione.getUser() : null));
				
				plainValue = (credenzialiInvocazione != null ? credenzialiInvocazione.getPassword() : null);
				encValue = null;
				if(driverBYOK!=null && plainValue!=null) {
					BYOKWrappedValue byokValue = driverBYOK.wrap(plainValue);
					if(byokValue!=null) {
						encValue = byokValue.getWrappedValue();
						plainValue = byokValue.getWrappedPlainValue();
					}
				}
				stm.setString(index++, plainValue);
				stm.setString(index++, encValue);
				
				// setto idconnettore inv
				stm.setLong(index++, idConnettoreInv);

				// InvocazionePorta
				gestErr = invPorta != null ? invPorta.getGestioneErrore() : null;
				fault = (gestErr != null ? DriverConfigurazioneDBLib.getValue(gestErr.getFault()) : null);
				stm.setString(index++, fault);
				// setto credenziali invocaizone porta
				// per il momento c'e' soltato una credenziale,quindi un solo
				// oggetto nella lista
				credenzialiInvocazionePorta = (invPorta != null && invPorta.sizeCredenzialiList() > 0 ? invPorta.getCredenziali(0) : null);
				stm.setString(index++, (credenzialiInvocazionePorta != null ? DriverConfigurazioneDBLib.getValue(credenzialiInvocazionePorta.getTipo()) : null));
				stm.setString(index++, (credenzialiInvocazionePorta != null ? credenzialiInvocazionePorta.getUser() : null));
				stm.setString(index++, (credenzialiInvocazionePorta != null ? credenzialiInvocazionePorta.getPassword() : null));
				
				subject = null;
				if(credenzialiInvocazionePorta!=null && credenzialiInvocazionePorta.getSubject()!=null && !"".equals(credenzialiInvocazionePorta.getSubject()))
					subject = credenzialiInvocazionePorta.getSubject();
				stm.setString(index++, (subject != null ? CertificateUtils.formatPrincipal(subject, PrincipalType.SUBJECT) : null));
				subjectCN = null;
				if(credenzialiInvocazionePorta!=null && credenzialiInvocazionePorta.getCnSubject()!=null && !"".equals(credenzialiInvocazionePorta.getCnSubject()))
					subjectCN = credenzialiInvocazionePorta.getCnSubject();
				stm.setString(index++, subjectCN);
				
				issuer = null;
				if(credenzialiInvocazionePorta != null && org.openspcoop2.core.config.constants.CredenzialeTipo.APIKEY.equals(credenzialiInvocazionePorta.getTipo())) {
					stm.setString(index++, CostantiDB.getIssuerApiKey(credenzialiInvocazionePorta.isAppId()));
				}
				else {
					if(credenzialiInvocazionePorta!=null && credenzialiInvocazionePorta.getIssuer()!=null && !"".equals(credenzialiInvocazionePorta.getIssuer()))
						issuer = credenzialiInvocazionePorta.getIssuer();
					stm.setString(index++, (issuer != null ? CertificateUtils.formatPrincipal(issuer, PrincipalType.ISSUER) : null));
				}
				issuerCN = null;
				if(credenzialiInvocazionePorta!=null && credenzialiInvocazionePorta.getCnIssuer()!=null && !"".equals(credenzialiInvocazionePorta.getCnIssuer()))
					issuerCN = credenzialiInvocazionePorta.getCnIssuer();
				stm.setString(index++, issuerCN);
				
				certificate = null;
				if(credenzialiInvocazionePorta!=null && credenzialiInvocazionePorta.getCertificate()!=null) {
					certificate = credenzialiInvocazionePorta.getCertificate();
				}
				jdbcAdapter = JDBCAdapterFactory.createJDBCAdapter(DriverConfigurazioneDBLib.tipoDB);
				jdbcAdapter.setBinaryData(stm, index++, certificate);
				if(credenzialiInvocazionePorta!=null && credenzialiInvocazionePorta.isCertificateStrictVerification()) {
					stm.setInt(index++, CostantiDB.TRUE);
				}				
				else {
					stm.setInt(index++, CostantiDB.FALSE);
				}
				
				stm.setString(index++, credenzialiInvocazionePorta!=null ? credenzialiInvocazionePorta.getTokenPolicy() : null);

				// aggiungo gestione invio/risposta per riferimento
				// invocazione servizio
				stm.setString(index++, invServizio != null ? DriverConfigurazioneDBLib.getValue(invServizio.getInvioPerRiferimento()) : null);
				stm.setString(index++, invServizio != null ? DriverConfigurazioneDBLib.getValue(invServizio.getRispostaPerRiferimento()) : null);
				// invocazione porta
				stm.setString(index++, invPorta != null ? DriverConfigurazioneDBLib.getValue(invPorta.getInvioPerRiferimento()) : null);
				// ricezione risposta
				stm.setString(index++, ricezione != null ? DriverConfigurazioneDBLib.getValue(ricezione.getInvioPerRiferimento()) : null);
				stm.setString(index++, ricezione != null ? DriverConfigurazioneDBLib.getValue(ricezione.getRispostaPerRiferimento()) : null);
				// protocol info
				stm.setInt(index++, (invPorta != null && (!CostantiConfigurazione.DISABILITATO.equals(invPorta.getSbustamentoInformazioniProtocollo())) ? CostantiDB.TRUE : CostantiDB.FALSE));
				//Invocazione Porta : fault_actor, generic_fault_code, prefix_fault_code
				stm.setString(index++, gestErr!=null ? gestErr.getFaultActor() : null);
				stm.setString(index++, gestErr!=null ? DriverConfigurazioneDBLib.getValue(gestErr.getGenericFaultCode()) : null);
				stm.setString(index++, gestErr!=null ? gestErr.getPrefixFaultCode() : null);
				//Aggiorno nome servizio applicativo
				stm.setString(index++, nomeSA);
				//Aggiorno il proprietario
				stm.setLong(index++, idProprietario<0 ? oldIdProprietario : idProprietario);

				// GestioneErrore risposta asincrona
				if(aSA.getRispostaAsincrona() !=null && aSA.getRispostaAsincrona().getGestioneErrore()!=null){
					DriverConfigurazioneDB_gestioneErroreLIB.CRUDGestioneErroreServizioApplicativo(CostantiDB.UPDATE, 
							aSA.getRispostaAsincrona().getGestioneErrore(), idProprietario, idServizioApplicativo, false, con);
					stm.setLong(index++, aSA.getRispostaAsincrona().getGestioneErrore().getId());
				}
				//	GestioneErrore invocazione servizio
				if(aSA.getInvocazioneServizio() !=null && aSA.getInvocazioneServizio().getGestioneErrore()!=null){
					DriverConfigurazioneDB_gestioneErroreLIB.CRUDGestioneErroreServizioApplicativo(CostantiDB.UPDATE, 
							aSA.getInvocazioneServizio().getGestioneErrore(), idProprietario, idServizioApplicativo, true, con);
					stm.setLong(index++, aSA.getInvocazioneServizio().getGestioneErrore().getId());
				}
				
				//tipologia erogazione/fruizione
				stm.setString(index++, aSA.getTipologiaFruizione()!=null ? TipologiaFruizione.valueOf(aSA.getTipologiaFruizione().toUpperCase()).toString() : TipologiaFruizione.DISABILITATO.toString());
				stm.setString(index++, aSA.getTipologiaErogazione()!=null ? TipologiaErogazione.valueOf(aSA.getTipologiaErogazione().toUpperCase()).toString() : TipologiaErogazione.DISABILITATO.toString());
				
				if(utenteUltimaModifica!=null) {
					stm.setString(index++, utenteUltimaModifica);
				}
				
				if(dataUltimaModifica!=null) {
					stm.setTimestamp(index++, dataUltimaModifica);
				}
				
				// where
				stm.setLong(index++, idServizioApplicativo); 
				stm.setString(index++, oldNomeSA); 
				stm.setLong(index++, oldIdProprietario); 

				n = stm.executeUpdate();
				stm.close();
				DriverConfigurazioneDBLib.logDebug("Updated " + n + " row(s)");

				
				
				// Ruoli
				
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.SERVIZI_APPLICATIVI_RUOLI);
				sqlQueryObject.addWhereCondition("id_servizio_applicativo=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, aSA.getId());
				n=stm.executeUpdate();
				stm.close();
				DriverConfigurazioneDBLib.logDebug("Cancellati "+n+" ruoli associati al servizioApplicativo "+idServizioApplicativo);
				
				n=0;
				if(invPorta!=null && invPorta.getRuoli()!=null && invPorta.getRuoli().sizeRuoloList()>0){
					for (int i = 0; i < invPorta.getRuoli().sizeRuoloList(); i++) {
						Ruolo ruolo = invPorta.getRuoli().getRuolo(i);
						sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
						sqlQueryObject.addInsertTable(CostantiDB.SERVIZI_APPLICATIVI_RUOLI);
						sqlQueryObject.addInsertField("id_servizio_applicativo", "?");
						sqlQueryObject.addInsertField("ruolo", "?");
						sqlQuery = sqlQueryObject.createSQLInsert();
						stm = con.prepareStatement(sqlQuery);
						stm.setLong(1, aSA.getId());
						stm.setString(2, ruolo.getNome());
						stm.executeUpdate();
						stm.close();
						n++;
						DriverConfigurazioneDBLib.logDebug("Aggiunto ruolo[" + ruolo.getNome() + "] al servizioApplicativo "+idServizioApplicativo);
					}
				}
				
				DriverConfigurazioneDBLib.logDebug("Aggiunti " + n + " ruoli al servizioApplicativo "+idServizioApplicativo);
				
				
				
				
				
				
				
				// Credenziali  (le credenziali in questa tabella partono dal numero maggiore di 1)
				
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.SERVIZI_APPLICATIVI_CREDENZIALI);
				sqlQueryObject.addWhereCondition("id_servizio_applicativo=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, aSA.getId());
				n=stm.executeUpdate();
				stm.close();
				DriverConfigurazioneDBLib.logDebug("Cancellate "+n+" credenziali associate al servizioApplicativo "+idServizioApplicativo);
				
				n=0;
				if(invPorta!=null && invPorta.sizeCredenzialiList()>1){
					for (int i = 1; i < invPorta.sizeCredenzialiList(); i++) {
						Credenziali credenziale = invPorta.getCredenziali(i);
						sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
						sqlQueryObject.addInsertTable(CostantiDB.SERVIZI_APPLICATIVI_CREDENZIALI);
						sqlQueryObject.addInsertField("id_servizio_applicativo", "?");
						sqlQueryObject.addInsertField("subject", "?");
						sqlQueryObject.addInsertField("cn_subject", "?");
						sqlQueryObject.addInsertField("issuer", "?");
						sqlQueryObject.addInsertField("cn_issuer", "?");
						sqlQueryObject.addInsertField("certificate", "?");
						sqlQueryObject.addInsertField("cert_strict_verification", "?");
						sqlQuery = sqlQueryObject.createSQLInsert();
						stm = con.prepareStatement(sqlQuery);
						
						index = 1;
						stm.setLong(index++, aSA.getId());
						
						String subjectCredenziale = null;
						if(credenziale!=null && credenziale.getSubject()!=null && !"".equals(credenziale.getSubject()))
							subjectCredenziale = credenziale.getSubject();
						stm.setString(index++, (subjectCredenziale != null ? CertificateUtils.formatPrincipal(subjectCredenziale, PrincipalType.SUBJECT) : null));
						String subjectCredenzialeCN = null;
						if(credenziale!=null && credenziale.getCnSubject()!=null && !"".equals(credenziale.getCnSubject()))
							subjectCredenzialeCN = credenziale.getCnSubject();
						stm.setString(index++, subjectCredenzialeCN);
						
						String issuerCredenziale = null;
						if(credenziale != null && org.openspcoop2.core.config.constants.CredenzialeTipo.APIKEY.equals(credenziale.getTipo())) {
							stm.setString(index++, CostantiDB.getIssuerApiKey(credenziale.isAppId()));
						}
						else {
							if(credenziale!=null && credenziale.getIssuer()!=null && !"".equals(credenziale.getIssuer()))
								issuerCredenziale = credenziale.getIssuer();
							stm.setString(index++, (issuerCredenziale != null ? CertificateUtils.formatPrincipal(issuerCredenziale, PrincipalType.ISSUER) : null));
						}
						String issuerCredenzialeCN = null;
						if(credenziale!=null && credenziale.getCnIssuer()!=null && !"".equals(credenziale.getCnIssuer()))
							issuerCredenzialeCN = credenziale.getCnIssuer();
						stm.setString(index++, issuerCredenzialeCN);
						
						byte [] certificateCredenziale = null;
						if(credenziale!=null && credenziale.getCertificate()!=null) {
							certificateCredenziale = credenziale.getCertificate();
						}
						jdbcAdapter = JDBCAdapterFactory.createJDBCAdapter(DriverConfigurazioneDBLib.tipoDB);
						jdbcAdapter.setBinaryData(stm, index++, certificateCredenziale);
						if(credenziale!=null && credenziale.isCertificateStrictVerification()) {
							stm.setInt(index++, CostantiDB.TRUE);
						}				
						else {
							stm.setInt(index++, CostantiDB.FALSE);
						}
						
						stm.executeUpdate();
						stm.close();
						n++;
						DriverConfigurazioneDBLib.logDebug("Aggiunta credenziale al servizioApplicativo "+idServizioApplicativo);
					}
				}
				
				DriverConfigurazioneDBLib.logDebug("Aggiunte " + n + " credenziali al servizioApplicativo "+idServizioApplicativo);
				
				
				
				// Proprieta
				
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.SERVIZI_APPLICATIVI_PROPS);
				sqlQueryObject.addWhereCondition("id_servizio_applicativo=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, aSA.getId());
				n=stm.executeUpdate();
				stm.close();
				DriverConfigurazioneDBLib.logDebug("Cancellate "+n+" proprieta' associate al servizioApplicativo "+idServizioApplicativo);
				
				n=0;
				if(proprieta!=null && !proprieta.isEmpty()){
					for (int i = 0; i < proprieta.size(); i++) {
						Proprieta prop = proprieta.get(i);
						
						sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
						sqlQueryObject.addInsertTable(CostantiDB.SERVIZI_APPLICATIVI_PROPS);
						sqlQueryObject.addInsertField("id_servizio_applicativo", "?");
						sqlQueryObject.addInsertField("nome", "?");
						sqlQueryObject.addInsertField("valore", "?");
						sqlQuery = sqlQueryObject.createSQLInsert();
						stm = con.prepareStatement(sqlQuery);
						stm.setLong(1, aSA.getId());
						stm.setString(2, prop.getNome());
						stm.setString(3, prop.getValore());
						stm.executeUpdate();
						stm.close();
						n++;
						DriverConfigurazioneDBLib.logDebug("Aggiunta proprieta' [" + prop.getNome() + "] al servizioApplicativo "+idServizioApplicativo);
					}
				}
				
				DriverConfigurazioneDBLib.logDebug("Aggiunte " + n + " proprieta' al servizioApplicativo "+idServizioApplicativo);
				
				
				
				
				// ProtocolProperties
				DriverConfigurazioneDBLib.crudProtocolProperty(CostantiDB.UPDATE, aSA.getProtocolPropertyList(), 
						idServizioApplicativo, ProprietariProtocolProperty.SERVIZIO_APPLICATIVO, con, DriverConfigurazioneDBLib.tipoDB, driverBYOK);
				
				break;

			case DELETE:
				// delete
				/** if(aSA.getId()==null || aSA.getId()<=0) throw new
				// DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDServizioApplicativo(DELETE)]
				// id del ServizioApplicativo non valida.");*/

				DriverConfigurazioneDBLib.logDebug("get ID Servizio Applicativo con nome["+nomeSA+"] tipoProprietario["+tipoProprietario+"] nomeProprietario["+nomeProprietario+"]");
				idServizioApplicativo = getIdServizioApplicativo(nomeSA, tipoProprietario, nomeProprietario, con, DriverConfigurazioneDBLib.tipoDB,DriverConfigurazioneDBLib.tabellaSoggetti);
				DriverConfigurazioneDBLib.logDebug("get ID Servizio Applicativo: "+idServizioApplicativo); 

				// ProtocolProperties
				DriverConfigurazioneDBLib.crudProtocolProperty(CostantiDB.DELETE, null, 
						idServizioApplicativo, ProprietariProtocolProperty.SERVIZIO_APPLICATIVO, con, DriverConfigurazioneDBLib.tipoDB, driverBYOK);
				
				// proprieta'
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.SERVIZI_APPLICATIVI_PROPS);
				sqlQueryObject.addWhereCondition("id_servizio_applicativo=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idServizioApplicativo);
				n=stm.executeUpdate();
				stm.close();
				if (n > 0)
					DriverConfigurazioneDBLib.logDebug("Deleted " + n + " proprieta' associate al ServizioApplicativo[" + idServizioApplicativo + "]");
				
				// credenziali
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.SERVIZI_APPLICATIVI_CREDENZIALI);
				sqlQueryObject.addWhereCondition("id_servizio_applicativo=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idServizioApplicativo);
				n=stm.executeUpdate();
				stm.close();
				if (n > 0)
					DriverConfigurazioneDBLib.logDebug("Deleted " + n + " credenziali associate al ServizioApplicativo[" + idServizioApplicativo + "]");
				
				// ruoli
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.SERVIZI_APPLICATIVI_RUOLI);
				sqlQueryObject.addWhereCondition("id_servizio_applicativo=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idServizioApplicativo);
				n=stm.executeUpdate();
				stm.close();
				if (n > 0)
					DriverConfigurazioneDBLib.logDebug("Deleted " + n + " ruoli associati al ServizioApplicativo[" + idServizioApplicativo + "]");
				
				/*
				 * BUG Devo prima eliminare l'associazione
				 */
				/**
				// cancello anche le associazioni delle porteapplicative
				// associate a questo servizio
				// serviziapplicativi
				
				List<Long> idsPA_SA = new ArrayList<>(); 
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_SA);
				sqlQueryObject.addSelectField("id");
				sqlQueryObject.addWhereCondition("id_servizio_applicativo=?");
				sqlQueryObject.setANDLogicOperator(true);
				sqlQuery = sqlQueryObject.createSQLQuery();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idServizioApplicativo);
				rs = stm.executeQuery();
				while(rs.next()) {
					idsPA_SA.add(rs.getLong("id"));
				}
				rs.close();
				stm.close();
				
				if(!idsPA_SA.isEmpty()) {
					for (Long idsapa : idsPA_SA) {
						sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
						sqlQueryObject.addDeleteTable(CostantiDB.PORTE_APPLICATIVE_SA_PROPS);
						sqlQueryObject.addWhereCondition("id_porta=?");
						sqlQuery = sqlQueryObject.createSQLDelete();
						stm = con.prepareStatement(sqlQuery);
						stm.setLong(1, idsapa);
						n=stm.executeUpdate();
						stm.close();
						DriverConfigurazioneDB_LIB.logDebug("Eliminate "+n+" proprieta relative all'associazione '"+idsapa+"' (SA "+idServizioApplicativo+")");
					}
				}
				
				DriverConfigurazioneDB_LIB.logDebug("Deleted PA associazioni...");
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORTE_APPLICATIVE_SA);
				sqlQueryObject.addWhereCondition("id_servizio_applicativo=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idServizioApplicativo);
				n=stm.executeUpdate();
				stm.close();
				if (n > 0)
					DriverConfigurazioneDB_LIB.logDebug("Deleted " + n + " associazioni di PortaApplicativa<->ServizioApplicativo associate al ServizioApplicativo[" + idServizioApplicativo + "]");

				// faccio lo stesso per le portedelegate
				DriverConfigurazioneDB_LIB.logDebug("Deleted PD associazioni...");
				DriverConfigurazioneDB_LIB.logDebug("Deleted PA associazioni...");
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORTE_DELEGATE_SA);
				sqlQueryObject.addWhereCondition("id_servizio_applicativo=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idServizioApplicativo);
				n=stm.executeUpdate();
				stm.close();
				if (n > 0)
					DriverConfigurazioneDB_LIB.logDebug("Deleted " + n + " associazioni di PortaDelegata<->ServizioApplicativo associate al ServizioApplicativo[" + idServizioApplicativo + "]");
				 */


				DriverConfigurazioneDBLib.logDebug("Deleted ...");
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.SERVIZI_APPLICATIVI);
				sqlQueryObject.addWhereCondition("id=?");
				sqlQueryObject.addWhereCondition("nome=?");
				sqlQueryObject.addWhereCondition("id_soggetto=?");
				sqlQueryObject.setANDLogicOperator(true);
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idServizioApplicativo);
				stm.setString(2, nomeSA);
				stm.setLong(3, idProprietario);
				DriverConfigurazioneDBLib.logDebug("eseguo query : " + DBUtils.formatSQLString(sqlQuery, idServizioApplicativo,nomeSA,idProprietario));
				n=stm.executeUpdate();
				stm.close();
				DriverConfigurazioneDBLib.logDebug("Deleted " + n + " row(s)");


				//cancello i connettori

				// Connettore asincrono
				DriverConfigurazioneDBLib.logDebug("Recupero connettore asincrono ...");
				if(aSA.getRispostaAsincrona()!=null && aSA.getRispostaAsincrona().getConnettore()!=null){
					connettoreRisp=aSA.getRispostaAsincrona().getConnettore();
				}else{
					connettoreRisp = new Connettore();
					connettoreRisp.setTipo(TipiConnettore.DISABILITATO.getNome());
				}
				nomeConnettoreRisp = "ConnettoreRISP_" + aSA.getNome()+"_"+aSA.getTipoSoggettoProprietario()+aSA.getNomeSoggettoProprietario();
				connettoreRisp.setNome(nomeConnettoreRisp);
				idConnettoreRisp = DBUtils.getIdConnettore(nomeConnettoreRisp, con, DriverConfigurazioneDBLib.tipoDB);
				DriverConfigurazioneDBLib.logDebug("Recupero connettore asincrono id["+idConnettoreRisp+"]");
				connettoreRisp.setId(idConnettoreRisp);


				// Connettore inv servizio
				DriverConfigurazioneDBLib.logDebug("Recupero connettore invocazione servizio ...");
				if(aSA.getInvocazioneServizio()!=null && aSA.getInvocazioneServizio().getConnettore()!=null){
					connettoreInv=aSA.getInvocazioneServizio().getConnettore();
				}else{
					connettoreInv = new Connettore();
					connettoreInv.setTipo(TipiConnettore.DISABILITATO.getNome());
				}
				nomeConnettoreInv = "ConnettoreINV_" + aSA.getNome()+"_"+aSA.getTipoSoggettoProprietario()+aSA.getNomeSoggettoProprietario();
				connettoreInv.setNome(nomeConnettoreInv);
				idConnettoreInv = DBUtils.getIdConnettore(nomeConnettoreInv, con, DriverConfigurazioneDBLib.tipoDB);
				DriverConfigurazioneDBLib.logDebug("Recupero connettore invocazione servizio id["+idConnettoreInv+"]");
				connettoreInv.setId(idConnettoreInv);


				//Controllo consistenza degli id dei connettori in quanto devono essere specificati
				//e quindi maggiori di 0
				if(idConnettoreInv <=0 || idConnettoreRisp<=0) throw new DriverConfigurazioneException("I connettori del servizio applicativo sono invalidi");

				// se il connettore e' abilitato allora propago le modifiche al
				// connettore
				DriverConfigurazioneDBLib.logDebug("Delete connettore asincrono ...");
				DriverConfigurazioneDB_connettoriLIB.CRUDConnettore(CostantiDB.DELETE, connettoreRisp, con, driverBYOK);
				DriverConfigurazioneDBLib.logDebug("Delete connettore invocazione servizio ...");
				DriverConfigurazioneDB_connettoriLIB.CRUDConnettore(CostantiDB.DELETE, connettoreInv, con, driverBYOK);

				
				// Delete gestione errore risposta asincrona
				if(aSA.getRispostaAsincrona() !=null && aSA.getRispostaAsincrona().getGestioneErrore()!=null){
					DriverConfigurazioneDB_gestioneErroreLIB.CRUDGestioneErroreServizioApplicativo(CostantiDB.DELETE, 
							aSA.getRispostaAsincrona().getGestioneErrore(), idProprietario, idServizioApplicativo, false, con);
				}
				
				
				// Delete gestione errore invocazione servizio
				if(aSA.getInvocazioneServizio() !=null && aSA.getInvocazioneServizio().getGestioneErrore()!=null){
					DriverConfigurazioneDB_gestioneErroreLIB.CRUDGestioneErroreServizioApplicativo(CostantiDB.DELETE, 
							aSA.getInvocazioneServizio().getGestioneErrore(), idProprietario, idServizioApplicativo, true, con);
				}
				

				break;

			}

			return idServizioApplicativo;
		} catch (DriverConfigurazioneException e) {
			throw e;
		} catch (SQLException se) {
			throw new DriverConfigurazioneException("SQLException : " + se.getMessage(),se);
		} catch (Exception se) {
			throw new DriverConfigurazioneException("Exception : " + se.getMessage(),se);
		} finally {
			//Chiudo statement and resultset
			JDBCUtilities.closeResources(rs, stmQuery);
			JDBCUtilities.closeResources(stm);
			JDBCUtilities.closeResources(stm2);
		}
	}

	
	
	
	public static long getIdServizioApplicativo(String nomeServizioApplicativo, String tipoProprietario,String nomeProprietario,
			Connection con, String tipoDB) throws CoreException
	{
		return getIdServizioApplicativo(nomeServizioApplicativo, tipoProprietario, nomeProprietario, con, tipoDB, CostantiDB.SOGGETTI);
	}
	public static long getIdServizioApplicativo(String nomeServizioApplicativo, String tipoProprietario,String nomeProprietario,
			Connection con, String tipoDB,String tabellaSoggetti) throws CoreException
	{
		PreparedStatement stm = null;
		ResultSet rs = null;
		long idSoggetto;
		long idServizioApplicativo=-1;

		try
		{
			idSoggetto = DBUtils.getIdSoggetto(nomeProprietario, tipoProprietario, con, tipoDB,tabellaSoggetti);

			//recupero l'id della porta applicativa appena inserita
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
			sqlQueryObject.addSelectField("id");
			sqlQueryObject.addWhereCondition("id_soggetto = ?");
			sqlQueryObject.addWhereCondition("nome = ?");
			sqlQueryObject.setANDLogicOperator(true);
			String sqlQuery = sqlQueryObject.createSQLQuery();
			stm=con.prepareStatement(sqlQuery);
			stm.setLong(1, idSoggetto);
			stm.setString(2, nomeServizioApplicativo);

			rs=stm.executeQuery();

			if(rs.next())
			{
				idServizioApplicativo=rs.getLong("id");
			}
			return idServizioApplicativo;
		}catch (Exception e) {
			throw new CoreException(e);
		}finally
		{
			//Chiudo statement and resultset
			JDBCUtilities.closeResources(rs, stm);

		}
	}
	
}
