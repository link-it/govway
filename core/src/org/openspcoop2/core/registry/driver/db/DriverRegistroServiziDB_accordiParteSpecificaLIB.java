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
import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.core.byok.IDriverBYOK;
import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.core.commons.DBUtils;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.constants.ProprietariProtocolProperty;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.ConfigurazioneServizioAzione;
import org.openspcoop2.core.registry.Connettore;
import org.openspcoop2.core.registry.Documento;
import org.openspcoop2.core.registry.Fruitore;
import org.openspcoop2.core.registry.constants.CostantiRegistroServizi;
import org.openspcoop2.core.registry.constants.ProprietariDocumento;
import org.openspcoop2.core.registry.constants.RuoliDocumento;
import org.openspcoop2.core.registry.constants.StatoFunzionalita;
import org.openspcoop2.core.registry.constants.TipologiaServizio;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.utils.date.DateManager;
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
public class DriverRegistroServiziDB_accordiParteSpecificaLIB {
	
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
			String tipoDatabase, IDriverBYOK driverBYOK) throws DriverRegistroServiziException {
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

		Connettore connettore = null;
		if(asps.getConfigurazioneServizio()!=null){
			connettore = asps.getConfigurazioneServizio().getConnettore();
		}

		String wsdlImplementativoErogatore = (asps.getByteWsdlImplementativoErogatore()!=null ? new String(asps.getByteWsdlImplementativoErogatore()) : null );
		String wsdlImplementativoFruitore =  (asps.getByteWsdlImplementativoFruitore()!=null ? new String(asps.getByteWsdlImplementativoFruitore()) : null );
		
		wsdlImplementativoErogatore = wsdlImplementativoErogatore!=null && !"".equals(wsdlImplementativoErogatore.trim().replace("\n", "")) ? wsdlImplementativoErogatore : null;
		wsdlImplementativoFruitore = wsdlImplementativoFruitore!=null && !"".equals(wsdlImplementativoFruitore.trim().replace("\n", "")) ? wsdlImplementativoFruitore : null;
				
		String superUser = asps.getSuperUser();
		StatoFunzionalita servizioCorrelato = (TipologiaServizio.CORRELATO.equals(asps.getTipologiaServizio()) ? CostantiRegistroServizi.ABILITATO : CostantiRegistroServizi.DISABILITATO);
		String portType = (asps.getPortType()!=null ? asps.getPortType() : null );
				
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
			DriverRegistroServiziDB_LIB.logError("Driver Error for get IDAccordo nome:["+asps.getAccordoServizioParteComune()+"].", e);
			throw new DriverRegistroServiziException(e);
		}

		try {
			String nomeS = nomeProprietario;
			String tipoS = tipoProprietario;
			idSoggetto = DBUtils.getIdSoggetto(nomeS, tipoS, con, DriverRegistroServiziDB_LIB.tipoDB);
		} catch (CoreException e) {
			DriverRegistroServiziDB_LIB.logError("[DriverRegistroServiziDB_LIB::CRUDAccordoServizioParteSpecifica] getIdSoggetto failed: "+e.getMessage(), e);
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
				idConnettore = DriverRegistroServiziDB_connettoriLIB.CRUDConnettore(1, connettore, con, driverBYOK);

				String utenteRichiedente = null;
				if(asps.getProprietaOggetto()!=null && asps.getProprietaOggetto().getUtenteRichiedente()!=null) {
					utenteRichiedente = asps.getProprietaOggetto().getUtenteRichiedente();
				}
				else {
					utenteRichiedente = superUser;
				}
				
				Timestamp dataCreazione = null;
				if(asps.getProprietaOggetto()!=null && asps.getProprietaOggetto().getDataCreazione()!=null) {
					dataCreazione = new Timestamp(asps.getProprietaOggetto().getDataCreazione().getTime());
				}
				else if(asps.getOraRegistrazione()!=null){
					dataCreazione = new Timestamp(asps.getOraRegistrazione().getTime());
				}
				else {
					dataCreazione = DateManager.getTimestamp();
				}
				
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
				if(utenteRichiedente!=null) {
					sqlQueryObject.addInsertField(CostantiDB.PROPRIETA_OGGETTO_UTENTE_RICHIEDENTE, "?");
				}
				if(dataCreazione!=null) {
					sqlQueryObject.addInsertField(CostantiDB.PROPRIETA_OGGETTO_DATA_CREAZIONE, "?");
				}
				
				updateQuery = sqlQueryObject.createSQLInsert();
				updateStmt = con.prepareStatement(updateQuery);

				int index = 1;
				updateStmt.setString(index++, nomeServizio);
				updateStmt.setString(index++, tipoServizio);
				updateStmt.setInt(index++, versioneServizio);
				updateStmt.setLong(index++, idSoggetto);
				updateStmt.setLong(index++, idAccordoLong);
				updateStmt.setString(index++, DriverRegistroServiziDB_LIB.getValue(servizioCorrelato));
				updateStmt.setLong(index++, idConnettore);
				updateStmt.setString(index++, wsdlImplementativoErogatore);
				updateStmt.setString(index++, wsdlImplementativoFruitore);
				updateStmt.setString(index++, superUser);
				if(asps.getPrivato()!=null && asps.getPrivato())
					updateStmt.setInt(index++, 1);
				else
					updateStmt.setInt(index++, 0);
				updateStmt.setString(index++, portType);
				updateStmt.setString(index++, asps.getVersioneProtocollo());
				updateStmt.setString(index++, descrizione);
								
				if(stato!=null){
					updateStmt.setString(index++, stato);
				}
				
				if(asps.getOraRegistrazione()!=null){
					updateStmt.setTimestamp(index++, new Timestamp(asps.getOraRegistrazione().getTime()));
				}
				
				updateStmt.setString(index++, DriverRegistroServiziDB_LIB.getValue(asps.getMessageType()));
				
				if(utenteRichiedente!=null) {
					updateStmt.setString(index++, utenteRichiedente);
				}
				
				if(dataCreazione!=null) {
					updateStmt.setTimestamp(index++, dataCreazione);
				}
				
				// eseguo lo statement
				n = updateStmt.executeUpdate();
				updateStmt.close();
				DriverRegistroServiziDB_LIB.logDebug("CRUDAccordoServizioParteSpecifica CREATE : \n" + 
						DriverRegistroServiziDB_LIB.formatSQLString(updateQuery, nomeServizio, tipoServizio, versioneServizio,
								idSoggetto, idAccordoLong, servizioCorrelato, idConnettore, wsdlImplementativoErogatore, wsdlImplementativoFruitore, superUser));
				DriverRegistroServiziDB_LIB.logDebug("CRUDAccordoServizioParteSpecifica type = " + type + " row affected =" + n);

				
				// recupero l'id del servizio inserito
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI);
				sqlQueryObject.addSelectField("*");
				sqlQueryObject.addWhereCondition("nome_servizio = ?");
				sqlQueryObject.addWhereCondition("tipo_servizio = ?");
				sqlQueryObject.addWhereCondition("versione_servizio = ?");
				sqlQueryObject.addWhereCondition("id_soggetto = ?");
				sqlQueryObject.setANDLogicOperator(true);
				updateQuery = sqlQueryObject.createSQLQuery();
				updateStmt = con.prepareStatement(updateQuery);

				updateStmt.setString(1, nomeServizio);
				updateStmt.setString(2, tipoServizio);
				updateStmt.setInt(3, versioneServizio);
				updateStmt.setLong(4, idSoggetto);

				DriverRegistroServiziDB_LIB.logDebug("CRUDAccordoServizioParteSpecifica recupero l'id del servizio appena creato : \n" + DriverRegistroServiziDB_LIB.formatSQLString(updateQuery, nomeServizio, tipoServizio, idSoggetto));
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
					DriverRegistroServiziDB_accordiParteSpecificaLIB.CRUDAccordoServizioParteSpecificaFruitore(1, fruitore, con, asps, driverBYOK);
				}
				
				// aggiungo azioni
				if(asps.getConfigurazioneServizio()!=null){
					sizeAzioni = asps.getConfigurazioneServizio().sizeConfigurazioneAzioneList();
					for (int i = 0; i < sizeAzioni; i++) {
						ConfigurazioneServizioAzione conf = asps.getConfigurazioneServizio().getConfigurazioneAzione(i);
						DriverRegistroServiziDB_accordiParteSpecificaLIB.CRUDAccordoServizioParteSpecificaAzioni(1, conf, con, asps, driverBYOK);
					}
				}
				
				// Documenti generici servizio
				List<Documento> documenti = new ArrayList<>();
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
				DriverRegistroServiziDB_documentiLIB.CRUDDocumento(CostantiDB.CREATE, documenti, idServizio, ProprietariDocumento.servizio, con, tipoDatabase);
				

				// ProtocolProperties
				DriverRegistroServiziDB_LIB.CRUDProtocolProperty(CostantiDB.CREATE, asps.getProtocolPropertyList(), 
						idServizio, ProprietariProtocolProperty.ACCORDO_SERVIZIO_PARTE_SPECIFICA, con, DriverRegistroServiziDB_LIB.tipoDB);
				
				
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
					oldTipoServizio = asps.getOldIDServizioForUpdate().getTipo();
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

				String utenteUltimaModifica = null;
				if(asps.getProprietaOggetto()!=null && asps.getProprietaOggetto().getUtenteUltimaModifica()!=null) {
					utenteUltimaModifica = asps.getProprietaOggetto().getUtenteUltimaModifica();
				}
				else {
					utenteUltimaModifica = superUser;
				}
				
				Timestamp dataUltimaModifica = null;
				if(asps.getProprietaOggetto()!=null && asps.getProprietaOggetto().getDataUltimaModifica()!=null) {
					dataUltimaModifica = new Timestamp(asps.getProprietaOggetto().getDataUltimaModifica().getTime());
				}
				/**else {
					dataUltimaModifica = DateManager.getTimestamp();
				} Se presente si aggiorna, altrimenti no, e si mantiene il precedente valore per gestire le modifiche delle fruizioni */ 
				
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
				if(utenteUltimaModifica!=null) {
					sqlQueryObject.addUpdateField(CostantiDB.PROPRIETA_OGGETTO_UTENTE_ULTIMA_MODIFICA, "?");
				}
				if(dataUltimaModifica!=null) {
					sqlQueryObject.addUpdateField(CostantiDB.PROPRIETA_OGGETTO_DATA_ULTIMA_MODIFICA, "?");
				}
				sqlQueryObject.addWhereCondition("id=?");
				updateQuery = sqlQueryObject.createSQLUpdate();
				updateStmt = con.prepareStatement(updateQuery);

				index = 1;
				
				updateStmt.setString(index++, nomeServizio);
				updateStmt.setString(index++, tipoServizio);
				updateStmt.setInt(index++, versioneServizio);
				updateStmt.setLong(index++, idSoggetto);
				updateStmt.setLong(index++, idAccordoLong);
				updateStmt.setString(index++, DriverRegistroServiziDB_LIB.getValue(servizioCorrelato));
				updateStmt.setLong(index++, idConnettore);
				updateStmt.setString(index++, wsdlImplementativoErogatore);
				updateStmt.setString(index++, wsdlImplementativoFruitore);
				updateStmt.setString(index++, superUser);
				if(asps.getPrivato()!=null && asps.getPrivato())
					updateStmt.setInt(index++, 1);
				else
					updateStmt.setInt(index++, 0);
				updateStmt.setString(index++, portType);
				updateStmt.setString(index++, asps.getVersioneProtocollo());
				updateStmt.setString(index++, descrizione);
			
				if(stato!=null){
					updateStmt.setString(index++, stato);
				}
				
				if(asps.getOraRegistrazione()!=null){
					updateStmt.setTimestamp(index++, new Timestamp(asps.getOraRegistrazione().getTime()));
				}
				
				updateStmt.setString(index++, DriverRegistroServiziDB_LIB.getValue(asps.getMessageType()));
	
				if(utenteUltimaModifica!=null) {
					updateStmt.setString(index++, utenteUltimaModifica);
				}
				
				if(dataUltimaModifica!=null) {
					updateStmt.setTimestamp(index++, dataUltimaModifica);
				}
				
				updateStmt.setLong(index++, idServizio);


				// eseguo lo statement
				n = updateStmt.executeUpdate();
				updateStmt.close();
				DriverRegistroServiziDB_LIB.logDebug("CRUDAccordoServizioParteSpecifica type = " + type + " row affected =" + n);
				DriverRegistroServiziDB_LIB.logDebug("CRUDAccordoServizioParteSpecifica UPDATE : \n" + DriverRegistroServiziDB_LIB.formatSQLString(updateQuery, nomeServizio, tipoServizio, idSoggetto, idAccordoLong, servizioCorrelato, idConnettore, wsdlImplementativoErogatore, wsdlImplementativoFruitore, superUser, idServizio));

				
				// aggiorno nome connettore
				String newNomeConnettore = "CNT_" + tipoProprietario+"/"+nomeProprietario +"_"+ tipoServizio + "/" +nomeServizio+ "/"+versioneServizio;
				connettore.setNome(newNomeConnettore);
				DriverRegistroServiziDB_connettoriLIB.CRUDConnettore(2, connettore, con, driverBYOK);

				
				//aggiorno fruitori
				//La lista dei fruitori del servizio contiene tutti e soli i fruitori di questo servizio
				//prima vengono cancellati i fruitori esistenti e poi vengono riaggiunti
				sizeFruitori = asps.sizeFruitoreList();
				fruitore = null;
				// NON POSSO: esistono i mapping
//				//cancellazione
/**				DriverRegistroServiziDB_LIB.deleteAllFruitoriServizio(idServizio, con);
//				//creazione
//				for (int i = 0; i < sizeFruitori; i++) {
//					fruitore = asps.getFruitore(i);
//					DriverRegistroServiziDB_LIB.CRUDAccordoServizioParteSpecificaFruitore(1, fruitore, con, servizio);
//				}*/
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
					DriverRegistroServiziDB_accordiParteSpecificaLIB.CRUDAccordoServizioParteSpecificaFruitore(typeFruitore, fruitore, con, asps, driverBYOK);
					idFruitoriEsistenti.add(DBUtils.getIdSoggetto(fruitore.getNome(), fruitore.getTipo(), con, tipoDatabase));
				}
				DriverRegistroServiziDB_accordiParteSpecificaLIB.deleteAllFruitoriServizio(idServizio, idFruitoriEsistenti, con);
	

				//aggiorno azioni
				//La lista delle azioni del servizio contiene tutti e soli le azioni di questo servizio
				//prima vengono cancellati le azioni esistenti e poi vengono riaggiunte
				if(asps.getConfigurazioneServizio()!=null){
					sizeAzioni = asps.getConfigurazioneServizio().sizeConfigurazioneAzioneList();
					//cancellazione
					DriverRegistroServiziDB_accordiParteSpecificaLIB.deleteAllAzioniServizio(idServizio, con);
					//creazione
					for (int i = 0; i < sizeAzioni; i++) {
						ConfigurazioneServizioAzione conf = asps.getConfigurazioneServizio().getConfigurazioneAzione(i);
						DriverRegistroServiziDB_accordiParteSpecificaLIB.CRUDAccordoServizioParteSpecificaAzioni(1, conf, con, asps, driverBYOK);
					}
				}
				

				
				// Documenti generici servizio
				documenti = new ArrayList<>();
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
				DriverRegistroServiziDB_documentiLIB.CRUDDocumento(CostantiDB.UPDATE, documenti, idServizio, ProprietariDocumento.servizio, con, tipoDatabase);
				
				
				// ProtocolProperties
				DriverRegistroServiziDB_LIB.CRUDProtocolProperty(CostantiDB.UPDATE, asps.getProtocolPropertyList(), 
						idServizio, ProprietariProtocolProperty.ACCORDO_SERVIZIO_PARTE_SPECIFICA, con, DriverRegistroServiziDB_LIB.tipoDB);
				
				
				break;

			case DELETE:
				// DELETE
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

					DriverRegistroServiziDB_accordiParteSpecificaLIB.CRUDAccordoServizioParteSpecificaFruitore(3, fruitore, con, asps, driverBYOK);
				}
				
				// elimino azioni
				if(asps.getConfigurazioneServizio()!=null){
					sizeAzioni = asps.getConfigurazioneServizio().sizeConfigurazioneAzioneList();
					for (int i = 0; i < sizeAzioni; i++) {
						ConfigurazioneServizioAzione conf = asps.getConfigurazioneServizio().getConfigurazioneAzione(i);
						DriverRegistroServiziDB_accordiParteSpecificaLIB.CRUDAccordoServizioParteSpecificaAzioni(3, conf, con, asps, driverBYOK);
					}
				}

				
				// Documenti generici accordo di servizio
				// Allegati
				// Specifiche Semiformali
				// Specifiche Livelli di Servizio
				// Specifiche Sicurezza
				DriverRegistroServiziDB_documentiLIB.CRUDDocumento(CostantiDB.DELETE, null, idServizio, ProprietariDocumento.servizio, con, tipoDatabase);
				
				
				// ProtocolProperties
				DriverRegistroServiziDB_LIB.CRUDProtocolProperty(CostantiDB.DELETE, null, 
						idServizio, ProprietariProtocolProperty.ACCORDO_SERVIZIO_PARTE_SPECIFICA, con, DriverRegistroServiziDB_LIB.tipoDB);
				
				
				
				// Delete servizio
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.SERVIZI);
				sqlQueryObject.addWhereCondition("id=?");
				String sqlQuery = sqlQueryObject.createSQLDelete();
				updateStmt = con.prepareStatement(sqlQuery);
				updateStmt.setLong(1, idServizio);
				n=updateStmt.executeUpdate();
				updateStmt.close();
				DriverRegistroServiziDB_LIB.logDebug("CRUDAccordoServizioParteSpecifica type = " + type + " row affected =" + n);
				// elimino connettore
				connettore=new Connettore();
				connettore.setId(idConnettore);
				DriverRegistroServiziDB_connettoriLIB.CRUDConnettore(CostantiDB.DELETE, connettore, con, driverBYOK);
				DriverRegistroServiziDB_LIB.logDebug("CRUDAccordoServizioParteSpecifica CREATE : \n" + DriverRegistroServiziDB_LIB.formatSQLString(updateQuery, idServizio));

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
			JDBCUtilities.closeResources(updateRS, updateStmt);
		}

	}

	/**
	 * Aggiunge un fruitore di un servizio alla lista dei fruitori dei servizi
	 * 
	 * @param type
	 * @param fruitore
	 * @param con
	 */
	public static long CRUDAccordoServizioParteSpecificaFruitore(int type, Fruitore fruitore, Connection con, AccordoServizioParteSpecifica servizio, IDriverBYOK driverBYOK) throws DriverRegistroServiziException {
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
			
			idServizio = DBUtils.getIdServizio(nomeServ, tipoServ, verServ, nomeSogg, tipoSogg, con, DriverRegistroServiziDB_LIB.tipoDB);
		} catch (CoreException e1) {
			DriverRegistroServiziDB_LIB.logError("[DriverRegistroServiziDB_LIB::CRUDAccordoServizioParteSpecificaFruitore] getIdServizio failed: "+e1.getMessage(), e1);
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
		
		String descrizione = fruitore.getDescrizione();
		
		long idSoggettoFruitore = -1;
		try {
			idSoggettoFruitore = DBUtils.getIdSoggetto(nomeSoggetto, tipoSoggetto, con, DriverRegistroServiziDB_LIB.tipoDB);
		} catch (CoreException e1) {
			DriverRegistroServiziDB_LIB.logError("[DriverRegistroServiziDB_LIB::CRUDAccordoServizioParteSpecificaFruitore] getIdSoggetto failed: "+e1.getMessage(), e1);
			throw new DriverRegistroServiziException(e1);
		}
		if (idSoggettoFruitore <= 0)
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDAccordoServizioParteSpecificaFruitore] Id Soggetto Fruitore non valido.");
		String wsdlImplementativoErogatore = (fruitore.getByteWsdlImplementativoErogatore()!=null ? new String(fruitore.getByteWsdlImplementativoErogatore()) : null );
		wsdlImplementativoErogatore = wsdlImplementativoErogatore!=null && !"".equals(wsdlImplementativoErogatore.trim().replaceAll("\n", "")) ? wsdlImplementativoErogatore : null;
		String wsdlImplementativoFruitore =  (fruitore.getByteWsdlImplementativoFruitore()!=null ? new String(fruitore.getByteWsdlImplementativoFruitore()) : null );
		wsdlImplementativoFruitore = wsdlImplementativoFruitore!=null && !"".equals(wsdlImplementativoFruitore.trim().replaceAll("\n", "")) ? wsdlImplementativoFruitore : null;
		
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
				}

				connettore.setNome("CNT_SF_" + tipoSoggetto+"/"+nomeSoggetto + "_" + servizio.getTipoSoggettoErogatore()+"/"+servizio.getNomeSoggettoErogatore() + "_" + 
						servizio.getTipo() +"/"+servizio.getNome()+"/"+servizio.getVersione());
				
				DriverRegistroServiziDB_connettoriLIB.CRUDConnettore(CostantiDB.CREATE, connettore, con, driverBYOK);
				idConnettore = connettore.getId();

				String utenteRichiedente = null;
				if(fruitore.getProprietaOggetto()!=null && fruitore.getProprietaOggetto().getUtenteRichiedente()!=null) {
					utenteRichiedente = fruitore.getProprietaOggetto().getUtenteRichiedente();
				}
				else {
					utenteRichiedente = DBUtils.getSuperUserServizioSafe(DriverRegistroServiziDB_LIB.log, "DriverRegistroServiziDB_LIB::CRUDAccordoServizioParteSpecificaFruitore",
							idServizio, con, DriverRegistroServiziDB_LIB.tipoDB);
				}
				
				Timestamp dataCreazione = null;
				if(fruitore.getProprietaOggetto()!=null && fruitore.getProprietaOggetto().getDataCreazione()!=null) {
					dataCreazione = new Timestamp(fruitore.getProprietaOggetto().getDataCreazione().getTime());
				}
				else if(fruitore.getOraRegistrazione()!=null){
					dataCreazione = new Timestamp(fruitore.getOraRegistrazione().getTime());
				}
				else {
					dataCreazione = DateManager.getTimestamp();
				}
				
				// create
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
				sqlQueryObject.addInsertTable(CostantiDB.SERVIZI_FRUITORI);
				sqlQueryObject.addInsertField("id_servizio", "?");
				sqlQueryObject.addInsertField("id_soggetto", "?");
				sqlQueryObject.addInsertField("id_connettore", "?");
				sqlQueryObject.addInsertField("wsdl_implementativo_erogatore", "?");
				sqlQueryObject.addInsertField("wsdl_implementativo_fruitore", "?");
				if(stato!=null)
					sqlQueryObject.addInsertField("stato", "?");
				if(fruitore.getOraRegistrazione()!=null)
					sqlQueryObject.addInsertField("ora_registrazione", "?");
				sqlQueryObject.addInsertField("descrizione", "?");
				if(utenteRichiedente!=null) {
					sqlQueryObject.addInsertField(CostantiDB.PROPRIETA_OGGETTO_UTENTE_RICHIEDENTE, "?");
				}
				if(dataCreazione!=null) {
					sqlQueryObject.addInsertField(CostantiDB.PROPRIETA_OGGETTO_DATA_CREAZIONE, "?");
				}
				updateQuery = sqlQueryObject.createSQLInsert();
				updateStmt = con.prepareStatement(updateQuery);

				updateStmt.setLong(1, idServizio);
				updateStmt.setLong(2, idSoggettoFruitore);
				updateStmt.setLong(3, idConnettore);
				updateStmt.setString(4, wsdlImplementativoErogatore);
				updateStmt.setString(5, wsdlImplementativoFruitore);
				
				int index = 6;
				
				if(stato!=null){
					updateStmt.setString(index, stato);
					index++;
				}
				if(fruitore.getOraRegistrazione()!=null){
					updateStmt.setTimestamp(index, new Timestamp(fruitore.getOraRegistrazione().getTime()));
					index++;
				}
				
				updateStmt.setString(index, descrizione);
				index++;
				
				if(utenteRichiedente!=null) {
					updateStmt.setString(index, utenteRichiedente);
					index++;
				}
				
				if(dataCreazione!=null) {
					updateStmt.setTimestamp(index, dataCreazione);
					index++;
				}

				n = updateStmt.executeUpdate();
				DriverRegistroServiziDB_LIB.logDebug("CRUDAccordoServizioParteSpecificaFruitore CREATE : \n" + DriverRegistroServiziDB_LIB.formatSQLString(updateQuery, idServizio, idSoggettoFruitore, idConnettore, wsdlImplementativoErogatore, wsdlImplementativoFruitore));

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
				
				
				// aggiungo azioni
				int	sizeAzioni = fruitore.sizeConfigurazioneAzioneList();
				for (int i = 0; i < sizeAzioni; i++) {
					ConfigurazioneServizioAzione conf = fruitore.getConfigurazioneAzione(i);
					DriverRegistroServiziDB_accordiParteSpecificaLIB.CRUDAccordoServizioParteSpecificaFruitoreAzioni(1, conf, con, servizio, fruitore, driverBYOK);
				}
				
				// ProtocolProperties
				DriverRegistroServiziDB_LIB.CRUDProtocolProperty(CostantiDB.UPDATE, fruitore.getProtocolPropertyList(), 
						idFruizione, ProprietariProtocolProperty.FRUITORE, con, DriverRegistroServiziDB_LIB.tipoDB);
				
				break;

			case 2:
				// update
				idConnettore = DriverRegistroServiziDB_LIB.getIdConnettoreServizioFruitore(idServizio, nomeSoggetto, tipoSoggetto, con);
				if(idConnettore<0) throw new DriverRegistroServiziException("Il connettore del Fruitore del Servizio e' invalido id<0");
				connettore.setId(idConnettore);

				String utenteUltimaModifica = null;
				if(fruitore.getProprietaOggetto()!=null && fruitore.getProprietaOggetto().getUtenteUltimaModifica()!=null) {
					utenteUltimaModifica = fruitore.getProprietaOggetto().getUtenteUltimaModifica();
				}
				
				Timestamp dataUltimaModifica = null;
				if(fruitore.getProprietaOggetto()!=null && fruitore.getProprietaOggetto().getDataUltimaModifica()!=null) {
					dataUltimaModifica = new Timestamp(fruitore.getProprietaOggetto().getDataUltimaModifica().getTime());
				}
				/**else {
					dataUltimaModifica = DateManager.getTimestamp();
				}Se presente si aggiorna, altrimenti no, e si mantiene il precedente valore per gestire le modifiche delle fruizioni */ 
				
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
				sqlQueryObject.addUpdateTable(CostantiDB.SERVIZI_FRUITORI);
				sqlQueryObject.addUpdateField("wsdl_implementativo_erogatore", "?");
				sqlQueryObject.addUpdateField("wsdl_implementativo_fruitore", "?");
				if(stato!=null)
					sqlQueryObject.addUpdateField("stato", "?");
				if(fruitore.getOraRegistrazione()!=null)
					sqlQueryObject.addUpdateField("ora_registrazione", "?");
				sqlQueryObject.addUpdateField("descrizione", "?");
				if(utenteUltimaModifica!=null) {
					sqlQueryObject.addUpdateField(CostantiDB.PROPRIETA_OGGETTO_UTENTE_ULTIMA_MODIFICA, "?");
				}
				if(dataUltimaModifica!=null) {
					sqlQueryObject.addUpdateField(CostantiDB.PROPRIETA_OGGETTO_DATA_ULTIMA_MODIFICA, "?");
				}
				sqlQueryObject.addWhereCondition("id_servizio=?");
				sqlQueryObject.addWhereCondition("id_soggetto=?");
				sqlQueryObject.addWhereCondition("id_connettore=?");
				sqlQueryObject.setANDLogicOperator(true);
				updateQuery = sqlQueryObject.createSQLUpdate();
				updateStmt = con.prepareStatement(updateQuery);

				updateStmt.setString(1, wsdlImplementativoErogatore);
				updateStmt.setString(2, wsdlImplementativoFruitore);

				index = 3;
				
				if(stato!=null){
					updateStmt.setString(index, stato);
					index++;
				}
				
				if(fruitore.getOraRegistrazione()!=null){
					updateStmt.setTimestamp(index, new Timestamp(fruitore.getOraRegistrazione().getTime()));
					index++;
				}
				
				updateStmt.setString(index, descrizione);
				index++;
				
				if(utenteUltimaModifica!=null) {
					updateStmt.setString(index, utenteUltimaModifica);
					index++;
				}
				
				if(dataUltimaModifica!=null) {
					updateStmt.setTimestamp(index, dataUltimaModifica);
					index++;
				}
					
				updateStmt.setLong(index,idServizio);
				index++;
				updateStmt.setLong(index,idSoggettoFruitore);
				index++;
				updateStmt.setLong(index,idConnettore);
				index++;
				
				n= updateStmt.executeUpdate();
				DriverRegistroServiziDB_LIB.logDebug("CRUDAccordoServizioParteSpecificaFruitore UPDATE : \n" + DriverRegistroServiziDB_LIB.formatSQLString(updateQuery, wsdlImplementativoErogatore, wsdlImplementativoFruitore, idServizio, idSoggettoFruitore, idConnettore));

				// modifico i dati del connettore
				//aggiorno nome
				DriverRegistroServiziDB_LIB.logDebug("Tento aggiornamento connettore id: ["+idConnettore+"] oldNome: ["+connettore.getNome()+"]...");
				String newNomeConnettore = "CNT_SF_" + tipoSoggetto+"/"+nomeSoggetto + "_" + servizio.getTipoSoggettoErogatore()+"/"+servizio.getNomeSoggettoErogatore() + "_" + 
						servizio.getTipo() +"/"+servizio.getNome()+"/"+servizio.getVersione();
				connettore.setNome(newNomeConnettore);
				DriverRegistroServiziDB_LIB.logDebug("nuovo nome connettore ["+newNomeConnettore+"]");
				DriverRegistroServiziDB_connettoriLIB.CRUDConnettore(2, connettore, con, driverBYOK);
				
				//aggiorno azioni
				//La lista delle azioni del servizio contiene tutti e soli le azioni di questo servizio
				//prima vengono cancellati le azioni esistenti e poi vengono riaggiunte
				sizeAzioni = fruitore.sizeConfigurazioneAzioneList();
				//cancellazione
				DriverRegistroServiziDB_accordiParteSpecificaLIB.deleteAllAzioniFruizioneServizio(idFruizione, con);
				//creazione
				for (int i = 0; i < sizeAzioni; i++) {
					ConfigurazioneServizioAzione conf = fruitore.getConfigurazioneAzione(i);
					DriverRegistroServiziDB_accordiParteSpecificaLIB.CRUDAccordoServizioParteSpecificaFruitoreAzioni(1, conf, con, servizio, fruitore, driverBYOK);
				}
								
				// ProtocolProperties
				DriverRegistroServiziDB_LIB.CRUDProtocolProperty(CostantiDB.UPDATE, fruitore.getProtocolPropertyList(), 
						idFruizione, ProprietariProtocolProperty.FRUITORE, con, DriverRegistroServiziDB_LIB.tipoDB);
				
				break;

			case 3:
				
				// ProtocolProperties
				DriverRegistroServiziDB_LIB.CRUDProtocolProperty(CostantiDB.DELETE, null, 
						idFruizione, ProprietariProtocolProperty.FRUITORE, con, DriverRegistroServiziDB_LIB.tipoDB);
				
				
				// elimino azioni
				sizeAzioni = fruitore.sizeConfigurazioneAzioneList();
				for (int i = 0; i < sizeAzioni; i++) {
					ConfigurazioneServizioAzione conf = fruitore.getConfigurazioneAzione(i);
					DriverRegistroServiziDB_accordiParteSpecificaLIB.CRUDAccordoServizioParteSpecificaFruitoreAzioni(3, conf, con, servizio, fruitore, driverBYOK);
				}
				
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
				DriverRegistroServiziDB_LIB.logDebug("CRUDAccordoServizioParteSpecificaFruitore DELETE : \n" + DriverRegistroServiziDB_LIB.formatSQLString(sqlQuery, idServizio, idSoggettoFruitore, idConnettore));

				// elimino il connettore
				connettore=new Connettore();
				connettore.setId(idConnettore);
				DriverRegistroServiziDB_connettoriLIB.CRUDConnettore(3, connettore, con, driverBYOK);

				break;
			}

			DriverRegistroServiziDB_LIB.logDebug("CRUDAccordoServizioParteSpecificaFruitore type = " + type + " row affected =" + n);

			if (CostantiDB.CREATE == type) {
				return idFruizione;
			}

			return n;

		} catch (SQLException se) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDFruitore] SQLException : " + se.getMessage(),se);
		}  catch (Exception se) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDFruitore] Exception : " + se.getMessage(),se);
		}finally {
			JDBCUtilities.closeResources(selectRS, selectStmt);
			JDBCUtilities.closeResources(updateStmt);
		}
	}
	
	
	public static long CRUDAccordoServizioParteSpecificaAzioni(int type, ConfigurazioneServizioAzione conf, Connection con, AccordoServizioParteSpecifica servizio, IDriverBYOK driverBYOK) throws DriverRegistroServiziException {
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
			
			idServizio = DBUtils.getIdServizio(nomeServ, tipoServ, verServ, nomeSogg, tipoSogg, con, DriverRegistroServiziDB_LIB.tipoDB);
		} catch (CoreException e1) {
			DriverRegistroServiziDB_LIB.logError("Driver Error.", e1);
			throw new DriverRegistroServiziException(e1);
		}

		if (idServizio <= 0)
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDAccordoServizioParteSpecificaAzioni] ID Servizio non valido.");

		Connettore connettore = conf.getConnettore();
		if (connettore == null && type != CostantiDB.CREATE && type!=CostantiDB.DELETE)
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDAccordoServizioParteSpecificaAzioni] il connettore non puo essere nullo.");

		if(conf.sizeAzioneList()<=0) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDAccordoServizioParteSpecificaAzioni] la configurazione non contiene azioni??");
		}
		
		String azioneValue = conf.getAzione(0); // prendo la prima, tanto devono essere tutte diverse tra le varie configurazioni
		if (azioneValue == null || azioneValue.equals(""))
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDAccordoServizioParteSpecificaAzioni] Azione non valida.");

		String nomeConnettore = "CNT_SAZIONE_" + servizio.getTipoSoggettoErogatore()+"/"+servizio.getNomeSoggettoErogatore() +"_"+ servizio.getTipo() +"/"+servizio.getNome()+"/"+servizio.getVersione()+"_"+azioneValue;
		if (connettore == null) {
			connettore = new Connettore();
		}
		connettore.setNome(nomeConnettore);
		
		long idConnettore = 0;
		long n = 0;
		try {
			switch (type) {
			case 1:
				
				DriverRegistroServiziDB_connettoriLIB.CRUDConnettore(CostantiDB.CREATE, connettore, con, driverBYOK);
				idConnettore = connettore.getId();

				// create
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
				sqlQueryObject.addInsertTable(CostantiDB.SERVIZI_AZIONI);
				sqlQueryObject.addInsertField("id_servizio", "?");
				sqlQueryObject.addInsertField("id_connettore", "?");
				updateQuery = sqlQueryObject.createSQLInsert();
				updateStmt = con.prepareStatement(updateQuery);
				updateStmt.setLong(1, idServizio);
				updateStmt.setLong(2, idConnettore);
				n = updateStmt.executeUpdate();
				DriverRegistroServiziDB_LIB.logDebug("CRUDAccordoServizioParteSpecificaAzioni CREATE : \n" + DriverRegistroServiziDB_LIB.formatSQLString(updateQuery, idServizio, idConnettore));
				updateStmt.close();
				
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI_AZIONI);
				sqlQueryObject.addSelectField("id");
				sqlQueryObject.addWhereCondition("id_servizio = ?");
				sqlQueryObject.addWhereCondition("id_connettore = ?"); // idconnettore sara' univoco!
				sqlQueryObject.setANDLogicOperator(true);
				selectQuery = sqlQueryObject.createSQLQuery();
				selectStmt = con.prepareStatement(selectQuery);
				selectStmt.setLong(1, idServizio);
				selectStmt.setLong(2, idConnettore);
				selectRS = selectStmt.executeQuery();
				long idServizioAzione = -1;
				if (selectRS.next()) {
					idServizioAzione = selectRS.getLong("id");
				}
				selectRS.close();
				selectStmt.close();
				if(idServizioAzione<=0) {
					throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDAccordoServizioParteSpecificaAzioni] Recuro id per idServizio["+idServizio+"] idConnettore["+idConnettore+"] non riuscito");
				}
				
				for (String azione : conf.getAzioneList()) {
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
					sqlQueryObject.addInsertTable(CostantiDB.SERVIZI_AZIONE);
					sqlQueryObject.addInsertField("nome_azione", "?");
					sqlQueryObject.addInsertField("id_servizio_azioni", "?");
					updateQuery = sqlQueryObject.createSQLInsert();
					updateStmt = con.prepareStatement(updateQuery);
					updateStmt.setString(1, azione);
					updateStmt.setLong(2, idServizioAzione);
					n = updateStmt.executeUpdate();
					DriverRegistroServiziDB_LIB.logDebug("CRUDAccordoServizioParteSpecificaAzione CREATE : \n" + DriverRegistroServiziDB_LIB.formatSQLString(updateQuery, azione, idServizioAzione));
					updateStmt.close();	
				}
				
				break;

			case 2:
				// update
				idConnettore = DriverRegistroServiziDB_LIB.getIdConnettoreServizioAzione(idServizio, azioneValue, con);
				if(idConnettore<0) throw new DriverRegistroServiziException("Il connettore dell'azione ["+azioneValue+"] del Servizio e' invalido id<0");
				connettore.setId(idConnettore);

				// modifico i dati del connettore
				//aggiorno nome
				DriverRegistroServiziDB_LIB.logDebug("Tento aggiornamento connettore id: ["+idConnettore+"] oldNome: ["+connettore.getNome()+"]...");
				DriverRegistroServiziDB_LIB.logDebug("nuovo nome connettore ["+connettore.getNome()+"]");
				DriverRegistroServiziDB_connettoriLIB.CRUDConnettore(2, connettore, con, driverBYOK);

				break;

			case 3:
				// delete
				idConnettore = DriverRegistroServiziDB_LIB.getIdConnettoreServizioAzione(idServizio, azioneValue, con);
				connettore.setId(idConnettore);
				
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI_AZIONI);
				sqlQueryObject.addSelectField("id");
				sqlQueryObject.addWhereCondition("id_servizio = ?");
				sqlQueryObject.addWhereCondition("id_connettore = ?"); // idconnettore sara' univoco!
				sqlQueryObject.setANDLogicOperator(true);
				selectQuery = sqlQueryObject.createSQLQuery();
				selectStmt = con.prepareStatement(selectQuery);
				selectStmt.setLong(1, idServizio);
				selectStmt.setLong(2, idConnettore);
				selectRS = selectStmt.executeQuery();
				idServizioAzione = -1;
				if (selectRS.next()) {
					idServizioAzione = selectRS.getLong("id");
				}
				selectRS.close();
				selectStmt.close();
				if(idServizioAzione<=0) {
					throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDAccordoServizioParteSpecificaAzioni] Recuro id per idServizio["+idServizio+"] idConnettore["+idConnettore+"] non riuscito");
				}
					
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.SERVIZI_AZIONE);
				sqlQueryObject.addWhereCondition("id_servizio_azioni=?");
				sqlQueryObject.setANDLogicOperator(true);
				String sqlQuery = sqlQueryObject.createSQLDelete();
				updateStmt = con.prepareStatement(sqlQuery);
				updateStmt.setLong(1, idServizioAzione);
				n=updateStmt.executeUpdate();
				updateStmt.close();
				DriverRegistroServiziDB_LIB.logDebug("CRUDAccordoServizioParteSpecificaAzioni DELETE : \n" + DriverRegistroServiziDB_LIB.formatSQLString(sqlQuery, idServizioAzione));

				
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.SERVIZI_AZIONI);
				sqlQueryObject.addWhereCondition("id=?");
				sqlQueryObject.setANDLogicOperator(true);
				sqlQuery = sqlQueryObject.createSQLDelete();
				updateStmt = con.prepareStatement(sqlQuery);
				updateStmt.setLong(1, idServizioAzione);
				n=updateStmt.executeUpdate();
				updateStmt.close();
				DriverRegistroServiziDB_LIB.logDebug("CRUDAccordoServizioParteSpecificaAzioni DELETE : \n" + DriverRegistroServiziDB_LIB.formatSQLString(sqlQuery,idServizioAzione));

				// elimino il connettore
				connettore=new Connettore();
				connettore.setId(idConnettore);
				DriverRegistroServiziDB_connettoriLIB.CRUDConnettore(3, connettore, con, driverBYOK);

				break;
			}

			DriverRegistroServiziDB_LIB.logDebug("CRUDAccordoServizioParteSpecificaAzioni type = " + type + " row affected =" + n);

			return n;

		} catch (SQLException se) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDAccordoServizioParteSpecificaAzioni] SQLException : " + se.getMessage(),se);
		}  catch (Exception se) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDAccordoServizioParteSpecificaAzioni] Exception : " + se.getMessage(),se);
		}finally {
			JDBCUtilities.closeResources(selectRS, selectStmt);
			JDBCUtilities.closeResources(updateStmt);
		}
	}
	
	public static long CRUDAccordoServizioParteSpecificaFruitoreAzioni(int type, ConfigurazioneServizioAzione conf, Connection con, 
			AccordoServizioParteSpecifica servizio, Fruitore fruitore, IDriverBYOK driverBYOK) throws DriverRegistroServiziException {
		PreparedStatement updateStmt = null;
		String updateQuery;
		PreparedStatement selectStmt = null;
		String selectQuery = "";
		ResultSet selectRS = null;
		
		long idFruizione = -1;
		try{
			IDServizio idServizio = IDServizioFactory.getInstance().getIDServizioFromAccordo(servizio); 
			IDSoggetto idFruitore = new IDSoggetto(fruitore.getTipo(), fruitore.getNome());
			idFruizione = DBUtils.getIdFruizioneServizio(idServizio, idFruitore, con, DriverRegistroServiziDB_LIB.tipoDB);
		} catch (CoreException e1) {
			DriverRegistroServiziDB_LIB.logError("Driver Error.", e1);
			throw new DriverRegistroServiziException(e1);
		}
		if (idFruizione <= 0)
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDAccordoServizioParteSpecificaFruitoreAzioni] ID Servizio non valido.");
		
		Connettore connettore = conf.getConnettore();
		if (connettore == null && type != CostantiDB.CREATE && type!=CostantiDB.DELETE)
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDAccordoServizioParteSpecificaFruitoreAzioni] il connettore non puo essere nullo.");

		if(conf.sizeAzioneList()<=0) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDAccordoServizioParteSpecificaFruitoreAzioni] la configurazione non contiene azioni??");
		}
		
		String azioneValue = conf.getAzione(0); // prendo la prima, tanto devono essere tutte diverse tra le varie configurazioni
		if (azioneValue == null || azioneValue.equals(""))
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDAccordoServizioParteSpecificaFruitoreAzioni] Azione non valida.");

		String nomeConnettore = "CNT_SF_AZIONE_" + fruitore.getTipo()+"/"+fruitore.getNome()+"_"+  servizio.getTipoSoggettoErogatore()+"/"+servizio.getNomeSoggettoErogatore() +"_"+ servizio.getTipo() +"/"+servizio.getNome()+"/"+servizio.getVersione()+"_"+azioneValue;
		if (connettore == null) {
			connettore = new Connettore();
		}
		connettore.setNome(nomeConnettore);
		
		long idConnettore = 0;
		long n = 0;
		try {
			switch (type) {
			case 1:
				
				DriverRegistroServiziDB_connettoriLIB.CRUDConnettore(CostantiDB.CREATE, connettore, con, driverBYOK);
				idConnettore = connettore.getId();

				// create
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
				sqlQueryObject.addInsertTable(CostantiDB.SERVIZI_FRUITORI_AZIONI);
				sqlQueryObject.addInsertField("id_fruizione", "?");
				sqlQueryObject.addInsertField("id_connettore", "?");
				updateQuery = sqlQueryObject.createSQLInsert();
				updateStmt = con.prepareStatement(updateQuery);
				updateStmt.setLong(1, idFruizione);
				updateStmt.setLong(2, idConnettore);
				n = updateStmt.executeUpdate();
				DriverRegistroServiziDB_LIB.logDebug("CRUDAccordoServizioParteSpecificaFruitoreAzioni CREATE : \n" + DriverRegistroServiziDB_LIB.formatSQLString(updateQuery, idFruizione, idConnettore));
				updateStmt.close();
				
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI_FRUITORI_AZIONI);
				sqlQueryObject.addSelectField("id");
				sqlQueryObject.addWhereCondition("id_fruizione = ?");
				sqlQueryObject.addWhereCondition("id_connettore = ?"); // idconnettore sara' univoco!
				sqlQueryObject.setANDLogicOperator(true);
				selectQuery = sqlQueryObject.createSQLQuery();
				selectStmt = con.prepareStatement(selectQuery);
				selectStmt.setLong(1, idFruizione);
				selectStmt.setLong(2, idConnettore);
				selectRS = selectStmt.executeQuery();
				long idFruizioneAzione = -1;
				if (selectRS.next()) {
					idFruizioneAzione = selectRS.getLong("id");
				}
				selectRS.close();
				selectStmt.close();
				if(idFruizioneAzione<=0) {
					throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDAccordoServizioParteSpecificaFruitoreAzioni] Recuro id per idFruizione["+idFruizione+"] idConnettore["+idConnettore+"] non riuscito");
				}
				
				for (String azione : conf.getAzioneList()) {
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
					sqlQueryObject.addInsertTable(CostantiDB.SERVIZI_FRUITORI_AZIONE);
					sqlQueryObject.addInsertField("nome_azione", "?");
					sqlQueryObject.addInsertField("id_fruizione_azioni", "?");
					updateQuery = sqlQueryObject.createSQLInsert();
					updateStmt = con.prepareStatement(updateQuery);
					updateStmt.setString(1, azione);
					updateStmt.setLong(2, idFruizioneAzione);
					n = updateStmt.executeUpdate();
					DriverRegistroServiziDB_LIB.logDebug("CRUDAccordoServizioParteSpecificaFruitoreAzioni CREATE : \n" + DriverRegistroServiziDB_LIB.formatSQLString(updateQuery, azione, idFruizioneAzione));
					updateStmt.close();	
				}
				
				break;

			case 2:
				// update
				idConnettore = DriverRegistroServiziDB_LIB.getIdConnettoreFruizioneServizioAzione(idFruizione, azioneValue, con);
				if(idConnettore<0) throw new DriverRegistroServiziException("Il connettore dell'azione ["+azioneValue+"] della fruizione di Servizio e' invalido id<0");
				connettore.setId(idConnettore);

				// modifico i dati del connettore
				//aggiorno nome
				DriverRegistroServiziDB_LIB.logDebug("Tento aggiornamento connettore id: ["+idConnettore+"] oldNome: ["+connettore.getNome()+"]...");
				DriverRegistroServiziDB_LIB.logDebug("nuovo nome connettore ["+connettore.getNome()+"]");
				DriverRegistroServiziDB_connettoriLIB.CRUDConnettore(2, connettore, con, driverBYOK);

				break;

			case 3:
				// delete
				idConnettore = DriverRegistroServiziDB_LIB.getIdConnettoreFruizioneServizioAzione(idFruizione, azioneValue, con);
				connettore.setId(idConnettore);
				
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI_FRUITORI_AZIONI);
				sqlQueryObject.addSelectField("id");
				sqlQueryObject.addWhereCondition("id_fruizione = ?");
				sqlQueryObject.addWhereCondition("id_connettore = ?"); // idconnettore sara' univoco!
				sqlQueryObject.setANDLogicOperator(true);
				selectQuery = sqlQueryObject.createSQLQuery();
				selectStmt = con.prepareStatement(selectQuery);
				selectStmt.setLong(1, idFruizione);
				selectStmt.setLong(2, idConnettore);
				selectRS = selectStmt.executeQuery();
				idFruizioneAzione = -1;
				if (selectRS.next()) {
					idFruizioneAzione = selectRS.getLong("id");
				}
				selectRS.close();
				selectStmt.close();
				if(idFruizioneAzione<=0) {
					throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDAccordoServizioParteSpecificaFruitoreAzioni] Recuro id per idFruizione["+idFruizione+"] idConnettore["+idConnettore+"] non riuscito");
				}
					
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.SERVIZI_FRUITORI_AZIONE);
				sqlQueryObject.addWhereCondition("id_fruizione_azioni=?");
				sqlQueryObject.setANDLogicOperator(true);
				String sqlQuery = sqlQueryObject.createSQLDelete();
				updateStmt = con.prepareStatement(sqlQuery);
				updateStmt.setLong(1, idFruizioneAzione);
				n=updateStmt.executeUpdate();
				updateStmt.close();
				DriverRegistroServiziDB_LIB.logDebug("CRUDAccordoServizioParteSpecificaFruitoreAzioni DELETE : \n" + DriverRegistroServiziDB_LIB.formatSQLString(sqlQuery, idFruizioneAzione));

				
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.SERVIZI_FRUITORI_AZIONI);
				sqlQueryObject.addWhereCondition("id=?");
				sqlQueryObject.setANDLogicOperator(true);
				sqlQuery = sqlQueryObject.createSQLDelete();
				updateStmt = con.prepareStatement(sqlQuery);
				updateStmt.setLong(1, idFruizioneAzione);
				n=updateStmt.executeUpdate();
				updateStmt.close();
				DriverRegistroServiziDB_LIB.logDebug("CRUDAccordoServizioParteSpecificaFruitoreAzioni DELETE : \n" + DriverRegistroServiziDB_LIB.formatSQLString(sqlQuery,idFruizioneAzione));

				// elimino il connettore
				connettore=new Connettore();
				connettore.setId(idConnettore);
				DriverRegistroServiziDB_connettoriLIB.CRUDConnettore(3, connettore, con, driverBYOK);

				break;
			}

			DriverRegistroServiziDB_LIB.logDebug("CRUDAccordoServizioParteSpecificaFruitoreAzioni type = " + type + " row affected =" + n);

			return n;

		} catch (SQLException se) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDAccordoServizioParteSpecificaFruitoreAzioni] SQLException : " + se.getMessage(),se);
		}  catch (Exception se) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDAccordoServizioParteSpecificaFruitoreAzioni] Exception : " + se.getMessage(),se);
		}finally {
			JDBCUtilities.closeResources(selectRS, selectStmt);
			JDBCUtilities.closeResources(updateStmt);
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

			ArrayList<Long> listaFruizioniDaEliminare = new ArrayList<>();
			ArrayList<Long> listaFruizioniDaEliminareConnettori = new ArrayList<>();

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
					listaFruizioniDaEliminareConnettori.add(rs.getLong("id_connettore"));
				}
			}
			rs.close();
			stm.close();

			for (int i = 0; i < listaFruizioniDaEliminare.size(); i++) {
			
				long idFruizione = listaFruizioniDaEliminare.get(i);
				
				//cancellazione azioni
				DriverRegistroServiziDB_accordiParteSpecificaLIB.deleteAllAzioniFruizioneServizio(idFruizione, con);
				
				//elimino prima le entry nella tab servizi_fruitori per rispettare le dipendenze
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.SERVIZI_FRUITORI);
				sqlQueryObject.addWhereCondition("id=?");
				String sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idFruizione);
				int n=stm.executeUpdate();
				stm.close();
				DriverRegistroServiziDB_LIB.logDebug("Cancellata (row:"+n+") fruizione con id:"+idFruizione);
	
			}
			
			for (int i = 0; i < listaFruizioniDaEliminareConnettori.size(); i++) {
				
				long idFruizione = listaFruizioniDaEliminare.get(i);
				long idConnettore = listaFruizioniDaEliminareConnettori.get(i);
				
				//cancello adesso i connettori custom
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.CONNETTORI_CUSTOM);
				sqlQueryObject.addWhereCondition("id_connettore=?");
				String sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idConnettore);
				stm.executeUpdate();
				stm.close();
				DriverRegistroServiziDB_LIB.logDebug("Cancellato connettore custom associato al connettore con id:"+idConnettore+" associato alla fruizione con id:"+idFruizione);
				
				//cancello adesso i connettori
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.CONNETTORI);
				sqlQueryObject.addWhereCondition("id=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idConnettore);
				stm.executeUpdate();
				stm.close();
				DriverRegistroServiziDB_LIB.logDebug("Cancellati connettoro con id:"+idConnettore+" associato alla fruizione con id:"+idFruizione);
				
			}



		} catch (SQLException e) {
			DriverRegistroServiziDB_LIB.logError("Errore SQL", e);
			throw new DriverRegistroServiziException(e);
		}catch (Exception e) {
			DriverRegistroServiziDB_LIB.logError("Errore", e);
			throw new DriverRegistroServiziException(e);
		} finally {
			JDBCUtilities.closeResources(stm);
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

			ArrayList<Long> listaConnettori = new ArrayList<>();
			ArrayList<Long> listaConfigurazioni = new ArrayList<>();

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
				listaConfigurazioni.add(rs.getLong("id"));
			}
			rs.close();
			stm.close();

			//elimino prima le entry nella tab servizi_azione per rispettare le dipendenze
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.SERVIZI_AZIONE);
			sqlQueryObject.addWhereCondition("id_servizio_azioni=?");
			String sqlQuery = sqlQueryObject.createSQLDelete();
			stm = con.prepareStatement(sqlQuery);
			for (Long idConfigurazione : listaConfigurazioni) {
				stm.setLong(1, idConfigurazione);
				int n=stm.executeUpdate();
				DriverRegistroServiziDB_LIB.logDebug("Cancellati "+n+" Azioni della configurazione del servizio "+idConfigurazione);
			}
			stm.close();
						
			//elimino prima le entry nella tab servizi_azioni per rispettare le dipendenze
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.SERVIZI_AZIONI);
			sqlQueryObject.addWhereCondition("id_servizio=?");
			sqlQuery = sqlQueryObject.createSQLDelete();
			stm = con.prepareStatement(sqlQuery);
			stm.setLong(1, idServizio);
			int n=stm.executeUpdate();
			stm.close();
			DriverRegistroServiziDB_LIB.logDebug("Cancellati "+n+" Azioni del servizio "+idServizio);

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
			DriverRegistroServiziDB_LIB.logDebug("Cancellati connettori "+listaConnettori.toString()+" associati alle azioni del servizio "+idServizio);
			
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
			DriverRegistroServiziDB_LIB.logDebug("Cancellati connettori "+listaConnettori.toString()+" associati ai Fruitori del servizio "+idServizio);



		} catch (SQLException e) {
			DriverRegistroServiziDB_LIB.logError("Errore SQL", e);
			throw new DriverRegistroServiziException(e);
		}catch (Exception e) {
			DriverRegistroServiziDB_LIB.logError("Errore", e);
			throw new DriverRegistroServiziException(e);
		} finally {
			JDBCUtilities.closeResources(stm);
		}
	}

	
	private static void deleteAllAzioniFruizioneServizio(long idFruizione, Connection con) throws DriverRegistroServiziException {
		PreparedStatement stm = null;
		ResultSet rs = null;
		try {

			ArrayList<Long> listaConnettori = new ArrayList<>();
			ArrayList<Long> listaConfigurazioni = new ArrayList<>();

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.SERVIZI_FRUITORI_AZIONI);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("id_fruizione = ?");
			String query = sqlQueryObject.createSQLQuery();
			stm = con.prepareStatement(query);
			stm.setLong(1, idFruizione);

			rs=stm.executeQuery();
			//recupero i connettori da cancellare
			while(rs.next()){
				listaConnettori.add(rs.getLong("id_connettore"));
				listaConfigurazioni.add(rs.getLong("id"));
			}
			rs.close();
			stm.close();

			//elimino prima le entry nella tab servizi_azione per rispettare le dipendenze
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.SERVIZI_FRUITORI_AZIONE);
			sqlQueryObject.addWhereCondition("id_fruizione_azioni=?");
			String sqlQuery = sqlQueryObject.createSQLDelete();
			stm = con.prepareStatement(sqlQuery);
			for (Long idConfigurazione : listaConfigurazioni) {
				stm.setLong(1, idConfigurazione);
				int n=stm.executeUpdate();
				DriverRegistroServiziDB_LIB.logDebug("Cancellati "+n+" Azioni della configurazione della fruizione del servizio "+idConfigurazione);
			}
			stm.close();
						
			//elimino prima le entry nella tab servizi_azioni per rispettare le dipendenze
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.SERVIZI_FRUITORI_AZIONI);
			sqlQueryObject.addWhereCondition("id_fruizione=?");
			sqlQuery = sqlQueryObject.createSQLDelete();
			stm = con.prepareStatement(sqlQuery);
			stm.setLong(1, idFruizione);
			int n=stm.executeUpdate();
			stm.close();
			DriverRegistroServiziDB_LIB.logDebug("Cancellati "+n+" Azioni della fruizione del servizio "+idFruizione);

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
			DriverRegistroServiziDB_LIB.logDebug("Cancellati connettori "+listaConnettori.toString()+" associati alle azioni della fruizione del servizio "+idFruizione);
			
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
			DriverRegistroServiziDB_LIB.logDebug("Cancellati connettori "+listaConnettori.toString()+" associati ai Fruitori della fruizione del servizio "+idFruizione);



		} catch (SQLException e) {
			DriverRegistroServiziDB_LIB.logError("Errore SQL", e);
			throw new DriverRegistroServiziException(e);
		}catch (Exception e) {
			DriverRegistroServiziDB_LIB.logError("Errore", e);
			throw new DriverRegistroServiziException(e);
		} finally {
			JDBCUtilities.closeResources(stm);
		}
	}
	
	
}
