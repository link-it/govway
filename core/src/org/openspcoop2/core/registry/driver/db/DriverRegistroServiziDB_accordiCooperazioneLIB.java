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
import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.core.commons.DBUtils;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.constants.ProprietariProtocolProperty;
import org.openspcoop2.core.id.IDAccordoCooperazione;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.AccordoCooperazionePartecipanti;
import org.openspcoop2.core.registry.AccordoServizioParteComuneServizioComposto;
import org.openspcoop2.core.registry.AccordoServizioParteComuneServizioCompostoServizioComponente;
import org.openspcoop2.core.registry.Documento;
import org.openspcoop2.core.registry.IdSoggetto;
import org.openspcoop2.core.registry.constants.CostantiRegistroServizi;
import org.openspcoop2.core.registry.constants.ProprietariDocumento;
import org.openspcoop2.core.registry.constants.RuoliDocumento;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.core.registry.driver.IDAccordoCooperazioneFactory;
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
public class DriverRegistroServiziDB_accordiCooperazioneLIB {
	
		
	
	public static void CRUDAccordoServizioParteComuneServizioComposto(int type, AccordoServizioParteComuneServizioComposto asServComposto, Connection con, long idAccordo) throws DriverRegistroServiziException {
		PreparedStatement updateStmt = null;
		String updateQuery;
		PreparedStatement selectStmt = null;
		ResultSet selectRS = null;
		if (idAccordo <= 0)
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDAccordoServizioServizioComposto] ID Accordo non valido.");
		
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
				
				if (asServComposto == null)
					throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDAccordoServizioServizioComposto] Accordo Cooperazione non valido.");
				
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
				
				if (asServComposto == null)
					throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDAccordoServizioServizioComposto] Accordo Cooperazione non valido.");
				
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
				if(selectRS!=null)
					selectRS.close();
			} catch (Exception e) {
				// ignore
			}
			try {
				if(selectStmt!=null)
					selectStmt.close();
			} catch (Exception e) {
				// ignore
			}
			try {
				if(updateStmt!=null)
					updateStmt.close();
			} catch (Exception e) {
				// ignore
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
				DriverRegistroServiziDB_documentiLIB.CRUDDocumento(CostantiDB.CREATE, documenti, idAccordoCooperazione, ProprietariDocumento.accordoCooperazione, con, tipoDatabase);
				
				
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
				DriverRegistroServiziDB_documentiLIB.CRUDDocumento(CostantiDB.UPDATE, documenti, idAccordoLong, ProprietariDocumento.accordoCooperazione, con,tipoDatabase);
				
				
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
				DriverRegistroServiziDB_documentiLIB.CRUDDocumento(CostantiDB.DELETE, null, idAccordoLong, ProprietariDocumento.accordoCooperazione, con,tipoDatabase);
				
				
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
				if(updateRS!=null)
					updateRS.close();
			} catch (Exception e) {
				// ignore
			}
			try {
				if(updateStmt!=null)
					updateStmt.close();
			} catch (Exception e) {
				// ignore
			}
		}

	}
	
}
