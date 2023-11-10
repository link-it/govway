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

import org.openspcoop2.core.commons.DBUtils;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.constants.ProprietariProtocolProperty;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDGruppo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.Azione;
import org.openspcoop2.core.registry.Documento;
import org.openspcoop2.core.registry.GruppoAccordo;
import org.openspcoop2.core.registry.PortType;
import org.openspcoop2.core.registry.Resource;
import org.openspcoop2.core.registry.constants.CostantiRegistroServizi;
import org.openspcoop2.core.registry.constants.FormatoSpecifica;
import org.openspcoop2.core.registry.constants.MessageType;
import org.openspcoop2.core.registry.constants.ProfiloCollaborazione;
import org.openspcoop2.core.registry.constants.ProprietariDocumento;
import org.openspcoop2.core.registry.constants.RuoliDocumento;
import org.openspcoop2.core.registry.constants.ServiceBinding;
import org.openspcoop2.core.registry.constants.StatoFunzionalita;
import org.openspcoop2.core.registry.driver.BeanUtilities;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
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
public class DriverRegistroServiziDB_accordiLIB {
	
	
	public static void createAccordoServizioParteComune(org.openspcoop2.core.registry.AccordoServizioParteComune accordoServizio, 
			Connection con, String tabellaSoggetti, Logger log, IDAccordoFactory idAccordoFactory) throws DriverRegistroServiziException {

		if (accordoServizio == null)
			throw new DriverRegistroServiziException("L'AccordoServizio non puo' essere null.");

		String nome = accordoServizio.getNome();
		if (nome == null || nome.equals(""))
			throw new DriverRegistroServiziException("Il nome dell'AccordoServizio non e' valido.");

		ServiceBinding serviceBinding = accordoServizio.getServiceBinding();
		MessageType messageType = accordoServizio.getMessageType();
		
		StatoFunzionalita confermaRicezione = accordoServizio.getConfermaRicezione();
		StatoFunzionalita conegnaInOrdine = accordoServizio.getConsegnaInOrdine();
		String descrizione = accordoServizio.getDescrizione();
		StatoFunzionalita filtroDuplicati = accordoServizio.getFiltroDuplicati();
		StatoFunzionalita identificativoCollaborazione = accordoServizio.getIdCollaborazione();
		StatoFunzionalita identificativoRiferimentoRichiesta = accordoServizio.getIdRiferimentoRichiesta();

		ProfiloCollaborazione profiloCollaborazione = accordoServizio.getProfiloCollaborazione();
		String scadenza = accordoServizio.getScadenza();
		boolean utilizzioSenzaAzione = accordoServizio.getUtilizzoSenzaAzione();
		String wsdlConcettuale = (accordoServizio.getByteWsdlConcettuale()!=null ? new String(accordoServizio.getByteWsdlConcettuale()) : null);
		String wsdlDefinitorio = (accordoServizio.getByteWsdlDefinitorio()!=null ? new String(accordoServizio.getByteWsdlDefinitorio()) : null);
		String wsdlLogicoErogatore = (accordoServizio.getByteWsdlLogicoErogatore()!=null ? new String(accordoServizio.getByteWsdlLogicoErogatore()) : null);
		String wsdlLogicoFruitore = (accordoServizio.getByteWsdlLogicoFruitore()!=null ? new String(accordoServizio.getByteWsdlLogicoFruitore()) : null);
		String conversazioneConcettuale = (accordoServizio.getByteSpecificaConversazioneConcettuale()!=null ? new String(accordoServizio.getByteSpecificaConversazioneConcettuale()) : null);
		String conversazioneErogatore = (accordoServizio.getByteSpecificaConversazioneErogatore()!=null ? new String(accordoServizio.getByteSpecificaConversazioneErogatore()) : null);
		String conversazioneFruitore = (accordoServizio.getByteSpecificaConversazioneFruitore()!=null ? new String(accordoServizio.getByteSpecificaConversazioneFruitore()) : null);
		String superUser = accordoServizio.getSuperUser();

		FormatoSpecifica formatoSpecifica = accordoServizio.getFormatoSpecifica();
		wsdlConcettuale = wsdlConcettuale!=null && !"".equals(wsdlConcettuale.trim().replaceAll("\n", "")) ? wsdlConcettuale : null;
		wsdlDefinitorio = wsdlDefinitorio!=null && !"".equals(wsdlDefinitorio.trim().replaceAll("\n", "")) ? wsdlDefinitorio : null;
		wsdlLogicoErogatore = wsdlLogicoErogatore!=null && !"".equals(wsdlLogicoErogatore.trim().replaceAll("\n", "")) ? wsdlLogicoErogatore : null;
		wsdlLogicoFruitore = wsdlLogicoFruitore!=null && !"".equals(wsdlLogicoFruitore.trim().replaceAll("\n", "")) ? wsdlLogicoFruitore : null;
		conversazioneConcettuale = conversazioneConcettuale!=null && !"".equals(conversazioneConcettuale.trim().replaceAll("\n", "")) ? conversazioneConcettuale : null;
		conversazioneErogatore = conversazioneErogatore!=null && !"".equals(conversazioneErogatore.trim().replaceAll("\n", "")) ? conversazioneErogatore : null;
		conversazioneFruitore = conversazioneFruitore!=null && !"".equals(conversazioneFruitore.trim().replaceAll("\n", "")) ? conversazioneFruitore : null;

		String utenteRichiedente = null;
		if(accordoServizio.getProprietaOggetto()!=null && accordoServizio.getProprietaOggetto().getUtenteRichiedente()!=null) {
			utenteRichiedente = accordoServizio.getProprietaOggetto().getUtenteRichiedente();
		}
		else {
			utenteRichiedente = superUser;
		}
		
		Timestamp dataCreazione = null;
		if(accordoServizio.getProprietaOggetto()!=null && accordoServizio.getProprietaOggetto().getDataCreazione()!=null) {
			dataCreazione = new Timestamp(accordoServizio.getProprietaOggetto().getDataCreazione().getTime());
		}
		else if(accordoServizio.getOraRegistrazione()!=null){
			dataCreazione = new Timestamp(accordoServizio.getOraRegistrazione().getTime());
		}
		else {
			dataCreazione = DateManager.getTimestamp();
		}
		
		String sqlQuery = "";

		PreparedStatement stm = null;
		
		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
			sqlQueryObject.addInsertTable(CostantiDB.ACCORDI);
			sqlQueryObject.addInsertField("service_binding", "?");
			sqlQueryObject.addInsertField("message_type", "?");
			sqlQueryObject.addInsertField("conferma_ricezione", "?");
			sqlQueryObject.addInsertField("consegna_in_ordine", "?");
			sqlQueryObject.addInsertField("descrizione", "?");
			sqlQueryObject.addInsertField("filtro_duplicati", "?");
			sqlQueryObject.addInsertField("identificativo_collaborazione", "?");
			sqlQueryObject.addInsertField("id_riferimento_richiesta", "?");
			sqlQueryObject.addInsertField("nome", "?");
			sqlQueryObject.addInsertField("profilo_collaborazione", "?");
			sqlQueryObject.addInsertField("scadenza", "?");
			sqlQueryObject.addInsertField("formato_specifica", "?");
			sqlQueryObject.addInsertField("wsdl_concettuale", "?");
			sqlQueryObject.addInsertField("wsdl_definitorio", "?");
			sqlQueryObject.addInsertField("wsdl_logico_erogatore", "?");
			sqlQueryObject.addInsertField("wsdl_logico_fruitore", "?");
			sqlQueryObject.addInsertField("spec_conv_concettuale", "?");
			sqlQueryObject.addInsertField("spec_conv_erogatore", "?");
			sqlQueryObject.addInsertField("spec_conv_fruitore", "?");
			sqlQueryObject.addInsertField("superuser", "?");
			sqlQueryObject.addInsertField("utilizzo_senza_azione", "?");
			sqlQueryObject.addInsertField("privato", "?");
			if(accordoServizio.getStatoPackage()!=null)
				sqlQueryObject.addInsertField("stato", "?");
			sqlQueryObject.addInsertField("canale", "?");
			if(accordoServizio.getSoggettoReferente()!=null)
				sqlQueryObject.addInsertField("id_referente", "?");
			sqlQueryObject.addInsertField("versione", "?");
			if(accordoServizio.getOraRegistrazione()!=null)
				sqlQueryObject.addInsertField("ora_registrazione", "?");
			if(utenteRichiedente!=null) {
				sqlQueryObject.addInsertField("utente_richiedente", "?");
			}
			if(dataCreazione!=null) {
				sqlQueryObject.addInsertField("data_creazione", "?");
			}
			sqlQuery = sqlQueryObject.createSQLInsert();
			stm = con.prepareStatement(sqlQuery);
			int index = 1;
			stm.setString(index++, DriverRegistroServiziDB_LIB.getValue(serviceBinding));
			stm.setString(index++, DriverRegistroServiziDB_LIB.getValue(messageType));
			stm.setString(index++, DriverRegistroServiziDB_LIB.getValue(confermaRicezione));
			stm.setString(index++, DriverRegistroServiziDB_LIB.getValue(conegnaInOrdine));
			stm.setString(index++, descrizione);
			stm.setString(index++, DriverRegistroServiziDB_LIB.getValue(filtroDuplicati));
			stm.setString(index++, DriverRegistroServiziDB_LIB.getValue(identificativoCollaborazione));
			stm.setString(index++, DriverRegistroServiziDB_LIB.getValue(identificativoRiferimentoRichiesta));
			stm.setString(index++, nome);
			stm.setString(index++, DriverRegistroServiziDB_LIB.getValue(profiloCollaborazione));
			stm.setString(index++, scadenza);
			stm.setString(index++, DriverRegistroServiziDB_LIB.getValue(formatoSpecifica));
			stm.setString(index++, wsdlConcettuale!=null && !wsdlConcettuale.trim().equals("") ? wsdlConcettuale : null);
			stm.setString(index++, wsdlDefinitorio!=null && !wsdlDefinitorio.trim().equals("") ? wsdlDefinitorio : null );
			stm.setString(index++ ,wsdlLogicoErogatore!=null && !wsdlLogicoErogatore.trim().equals("") ? wsdlLogicoErogatore : null );
			stm.setString(index++, wsdlLogicoFruitore!=null && !wsdlLogicoFruitore.trim().equals("") ? wsdlLogicoFruitore : null );
			stm.setString(index++, conversazioneConcettuale!=null && !conversazioneConcettuale.trim().equals("") ? conversazioneConcettuale : null);
			stm.setString(index++ ,conversazioneErogatore!=null && !conversazioneErogatore.trim().equals("") ? conversazioneErogatore : null );
			stm.setString(index++, conversazioneFruitore!=null && !conversazioneFruitore.trim().equals("") ? conversazioneFruitore : null );
			stm.setString(index++, superUser);
			stm.setInt(index++, utilizzioSenzaAzione ? CostantiDB.TRUE : CostantiDB.FALSE);
			if (accordoServizio.getPrivato()!=null && accordoServizio.getPrivato())
				stm.setInt(index++, 1);
			else
				stm.setInt(index++, 0);
			if(accordoServizio.getStatoPackage()!=null){
				stm.setString(index, accordoServizio.getStatoPackage());
				index++;
			}
			stm.setString(index, accordoServizio.getCanale());
			index++;
			
			long idReferente = -1;
			if(accordoServizio.getSoggettoReferente()!=null){
				idReferente = DBUtils.getIdSoggetto(accordoServizio.getSoggettoReferente().getNome(), accordoServizio.getSoggettoReferente().getTipo(), con, DriverRegistroServiziDB_LIB.tipoDB, tabellaSoggetti);
				if(idReferente<=0){
					throw new DriverRegistroServiziException("Soggetto Referente ["+accordoServizio.getSoggettoReferente().getTipo()+"/"+accordoServizio.getSoggettoReferente().getNome()+"] non trovato");
				}
				stm.setLong(index, idReferente);
				index++;
			}

			stm.setInt(index, accordoServizio.getVersione());
			index++;

			if(accordoServizio.getOraRegistrazione()!=null){
				stm.setTimestamp(index, new Timestamp(accordoServizio.getOraRegistrazione().getTime()));
				index++;
			}
			
			if(utenteRichiedente!=null) {
				stm.setString(index, utenteRichiedente);
				index++;
			}
			
			if(dataCreazione!=null) {
				stm.setTimestamp(index, dataCreazione);
				index++;
			}

			String msgDebug = "inserisco accordoServizio : " + DriverRegistroServiziDB_LIB.formatSQLString(sqlQuery, 
					serviceBinding, messageType,
					confermaRicezione, conegnaInOrdine, descrizione, 
					filtroDuplicati, identificativoCollaborazione, identificativoRiferimentoRichiesta, nome, profiloCollaborazione, scadenza, 
					wsdlConcettuale, wsdlDefinitorio, wsdlLogicoErogatore, wsdlLogicoFruitore, 
					conversazioneConcettuale, conversazioneErogatore, conversazioneFruitore,
					superUser, accordoServizio.getUtilizzoSenzaAzione(), 
					(accordoServizio.getPrivato()!=null && accordoServizio.getPrivato()));
			log.debug(msgDebug);

			// eseguo la query
			stm.executeUpdate();
			stm.close();
			// recupero l-id dell'accordo appena inserito
			IDSoggetto soggettoReferente = null;
			if(accordoServizio.getSoggettoReferente()!=null){
				soggettoReferente = new IDSoggetto(accordoServizio.getSoggettoReferente().getTipo(),accordoServizio.getSoggettoReferente().getNome());
			}
			IDAccordo idAccordoObject = idAccordoFactory.getIDAccordoFromValues(accordoServizio.getNome(),soggettoReferente,accordoServizio.getVersione());
			long idAccordo = DBUtils.getIdAccordoServizioParteComune(idAccordoObject, con, DriverRegistroServiziDB_LIB.tipoDB);
			if (idAccordo<=0) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::createAccordoServizio] non riesco a trovare l'id del'Accordo inserito");
			}
			accordoServizio.setId(idAccordo);
			// aggiungo le eventuali azioni che c-erano
			
			Azione azione = null;
			for (int i = 0; i < accordoServizio.sizeAzioneList(); i++) {
				azione = accordoServizio.getAzione(i);
				DriverRegistroServiziDB_accordiSoapLIB.CRUDAzione(CostantiDB.CREATE,accordoServizio, azione, con, idAccordo);

			}
			log.debug("inserite " + accordoServizio.sizeAzioneList() + " azioni relative all'accordo :" + nome + " id :" + idAccordo);

			PortType pt = null;
			for (int i = 0; i < accordoServizio.sizePortTypeList(); i++) {
				pt = accordoServizio.getPortType(i);
				DriverRegistroServiziDB_accordiSoapLIB.CRUDPortType(CostantiDB.CREATE,accordoServizio,pt, con, idAccordo);
			}
			log.debug("inserite " + accordoServizio.sizePortTypeList() + " porttype relative all'accordo :" + nome + " id :" + idAccordo);

			Resource resource = null;
			for (int i = 0; i < accordoServizio.sizeResourceList(); i++) {
				resource = accordoServizio.getResource(i);
				DriverRegistroServiziDB_accordiRestLIB.CRUDResource(CostantiDB.CREATE,accordoServizio,resource, con, idAccordo);
			}
			log.debug("inserite " + accordoServizio.sizeResourceList() + " resources relative all'accordo :" + nome + " id :" + idAccordo);
			
			// Gruppi
			if(accordoServizio.getGruppi()!=null && accordoServizio.getGruppi().sizeGruppoList()>0) {
				for (int i = 0; i < accordoServizio.getGruppi().sizeGruppoList(); i++) {
					GruppoAccordo gruppo = accordoServizio.getGruppi().getGruppo(i);
					DriverRegistroServiziDB_accordiLIB.CRUDAccordoGruppo(CostantiDB.CREATE,accordoServizio, gruppo, con, idAccordo);
				}
				log.debug("inserite " + accordoServizio.sizeAzioneList() + " gruppi relative all'accordo :" + nome + " id :" + idAccordo);
			}
			
			// Accordo servizio composto
			if(accordoServizio.getServizioComposto()!=null){
				DriverRegistroServiziDB_accordiCooperazioneLIB.CRUDAccordoServizioParteComuneServizioComposto(CostantiDB.CREATE, 
						accordoServizio.getServizioComposto(), con, idAccordo);
			}


			// Documenti generici accordo di servizio
			List<Documento> documenti = new ArrayList<Documento>();
			// Allegati
			for(int i=0; i<accordoServizio.sizeAllegatoList(); i++){
				Documento doc = accordoServizio.getAllegato(i);
				doc.setRuolo(RuoliDocumento.allegato.toString());
				documenti.add(doc);
			}
			// Specifiche Semiformali
			for(int i=0; i<accordoServizio.sizeSpecificaSemiformaleList(); i++){
				Documento doc = accordoServizio.getSpecificaSemiformale(i);
				doc.setRuolo(RuoliDocumento.specificaSemiformale.toString());
				documenti.add(doc);
			}
			// Specifiche Coordinamento
			if(accordoServizio.getServizioComposto()!=null){
				for(int i=0; i<accordoServizio.getServizioComposto().sizeSpecificaCoordinamentoList(); i++){
					Documento doc = accordoServizio.getServizioComposto().getSpecificaCoordinamento(i);
					doc.setRuolo(RuoliDocumento.specificaCoordinamento.toString());
					documenti.add(doc);
				}
			}
			// CRUD
			DriverRegistroServiziDB_documentiLIB.CRUDDocumento(CostantiDB.CREATE, documenti, idAccordo, ProprietariDocumento.accordoServizio, con, DriverRegistroServiziDB_LIB.tipoDB);
			
			// ProtocolProperties
			DriverRegistroServiziDB_LIB.CRUDProtocolProperty(CostantiDB.CREATE, accordoServizio.getProtocolPropertyList(), 
					idAccordo, ProprietariProtocolProperty.ACCORDO_SERVIZIO_PARTE_COMUNE, con, DriverRegistroServiziDB_LIB.tipoDB);
			

		} catch (SQLException se) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::createAccordoServizioParteComune] SQLException [" + se.getMessage() + "].",se);
		} catch (DriverRegistroServiziException e) {
			throw new DriverRegistroServiziException(e.getMessage(),e);
		}catch (Exception e) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::createAccordoServizioParteComune] Exception [" + e.getMessage() + "].",e);
		}finally {
			JDBCUtilities.closeResources(stm);
		}

	}
	
	
	
	public static void updateAccordoServizioParteComune(org.openspcoop2.core.registry.AccordoServizioParteComune accordoServizio, 
			Connection con, String tabellaSoggetti, Logger log, IDAccordoFactory idAccordoFactory) throws DriverRegistroServiziException {

		if (accordoServizio == null)
			throw new DriverRegistroServiziException("L'AccordoServizio non puo' essere null.");

		String nome = accordoServizio.getNome();
		if (nome == null || nome.equals(""))
			throw new DriverRegistroServiziException("Il nome dell'AccordoServizio non e' valido.");

		PreparedStatement stm = null;
		ResultSet rs =null;
		String sqlQuery = "";

		ServiceBinding serviceBinding = accordoServizio.getServiceBinding();
		MessageType messageType = accordoServizio.getMessageType();
		
		StatoFunzionalita confermaRicezione = accordoServizio.getConfermaRicezione();
		StatoFunzionalita conegnaInOrdine = accordoServizio.getConsegnaInOrdine();
		String descrizione = accordoServizio.getDescrizione();
		StatoFunzionalita filtroDuplicati = accordoServizio.getFiltroDuplicati();
		StatoFunzionalita identificativoCollaborazione = accordoServizio.getIdCollaborazione();
		StatoFunzionalita identificativoRiferimentoRichiesta = accordoServizio.getIdRiferimentoRichiesta();

		ProfiloCollaborazione profiloCollaborazione = accordoServizio.getProfiloCollaborazione();
		String scadenza = accordoServizio.getScadenza();
		boolean utilizzioSenzaAzione = accordoServizio.getUtilizzoSenzaAzione();

		FormatoSpecifica formatoSpecifica = accordoServizio.getFormatoSpecifica();
		
		String wsdlConcettuale = (accordoServizio.getByteWsdlConcettuale()!=null ? new String(accordoServizio.getByteWsdlConcettuale()) : null);
		String wsdlDefinitorio = (accordoServizio.getByteWsdlDefinitorio()!=null ? new String(accordoServizio.getByteWsdlDefinitorio()) : null);
		String wsdlLogicoErogatore = (accordoServizio.getByteWsdlLogicoErogatore()!=null ? new String(accordoServizio.getByteWsdlLogicoErogatore()) : null);
		String wsdlLogicoFruitore = (accordoServizio.getByteWsdlLogicoFruitore()!=null ? new String(accordoServizio.getByteWsdlLogicoFruitore()) : null);
		String conversazioneConcettuale = (accordoServizio.getByteSpecificaConversazioneConcettuale()!=null ? new String(accordoServizio.getByteSpecificaConversazioneConcettuale()) : null);
		String conversazioneErogatore = (accordoServizio.getByteSpecificaConversazioneErogatore()!=null ? new String(accordoServizio.getByteSpecificaConversazioneErogatore()) : null);
		String conversazioneFruitore = (accordoServizio.getByteSpecificaConversazioneFruitore()!=null ? new String(accordoServizio.getByteSpecificaConversazioneFruitore()) : null);
		String superUser = accordoServizio.getSuperUser();

		wsdlConcettuale = wsdlConcettuale!=null && !"".equals(wsdlConcettuale.trim().replaceAll("\n", "")) ? wsdlConcettuale : null;
		wsdlDefinitorio = wsdlDefinitorio!=null && !"".equals(wsdlDefinitorio.trim().replaceAll("\n", "")) ? wsdlDefinitorio : null;
		wsdlLogicoErogatore = wsdlLogicoErogatore!=null && !"".equals(wsdlLogicoErogatore.trim().replaceAll("\n", "")) ? wsdlLogicoErogatore : null;
		wsdlLogicoFruitore = wsdlLogicoFruitore!=null && !"".equals(wsdlLogicoFruitore.trim().replaceAll("\n", "")) ? wsdlLogicoFruitore : null;
		conversazioneConcettuale = conversazioneConcettuale!=null && !"".equals(conversazioneConcettuale.trim().replaceAll("\n", "")) ? conversazioneConcettuale : null;
		conversazioneErogatore = conversazioneErogatore!=null && !"".equals(conversazioneErogatore.trim().replaceAll("\n", "")) ? conversazioneErogatore : null;
		conversazioneFruitore = conversazioneFruitore!=null && !"".equals(conversazioneFruitore.trim().replaceAll("\n", "")) ? conversazioneFruitore : null;

		try {

			IDAccordo idAccordoAttualeInseritoDB = null;
			if(accordoServizio.getOldIDAccordoForUpdate()!=null){
				idAccordoAttualeInseritoDB = accordoServizio.getOldIDAccordoForUpdate();
			}else{
				idAccordoAttualeInseritoDB = idAccordoFactory.getIDAccordoFromAccordo(accordoServizio);
			}

			long idAccordoLong = -1;
			try{
				idAccordoLong = DBUtils.getIdAccordoServizioParteComune(idAccordoAttualeInseritoDB, con,DriverRegistroServiziDB_LIB.tipoDB);
			}catch(Exception e){
				if(accordoServizio.getOldIDAccordoForUpdate()!=null){
					// Provo con soggetto attuale
					if(accordoServizio.getSoggettoReferente()!=null){
						idAccordoAttualeInseritoDB = idAccordoFactory.getIDAccordoFromValues(idAccordoAttualeInseritoDB.getNome(),
								new IDSoggetto(accordoServizio.getSoggettoReferente().getTipo(),
										accordoServizio.getSoggettoReferente().getNome()),
										idAccordoAttualeInseritoDB.getVersione());
					}else{
						idAccordoAttualeInseritoDB = idAccordoFactory.getIDAccordoFromValues(idAccordoAttualeInseritoDB.getNome(),null,
								idAccordoAttualeInseritoDB.getVersione());
					}
					idAccordoLong = DBUtils.getIdAccordoServizioParteComune(idAccordoAttualeInseritoDB, con,DriverRegistroServiziDB_LIB.tipoDB);
				}else{
					throw e;
				}
			}
			if (idAccordoLong <= 0)
				throw new DriverRegistroServiziException("Impossibile recuperare l'id dell'Accordo di Servizio : " + nome);

			String utenteUltimaModifica = null;
			if(accordoServizio.getProprietaOggetto()!=null && accordoServizio.getProprietaOggetto().getUtenteUltimaModifica()!=null) {
				utenteUltimaModifica = accordoServizio.getProprietaOggetto().getUtenteUltimaModifica();
			}
			else {
				utenteUltimaModifica = superUser;
			}
			
			Timestamp dataUltimaModifica = null;
			if(accordoServizio.getProprietaOggetto()!=null && accordoServizio.getProprietaOggetto().getDataUltimaModifica()!=null) {
				dataUltimaModifica = new Timestamp(accordoServizio.getProprietaOggetto().getDataUltimaModifica().getTime());
			}
			else {
				dataUltimaModifica = DateManager.getTimestamp();
			}
			
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
			sqlQueryObject.addUpdateTable(CostantiDB.ACCORDI);
			sqlQueryObject.addUpdateField("service_binding", "?");
			sqlQueryObject.addUpdateField("message_type", "?");
			sqlQueryObject.addUpdateField("conferma_ricezione", "?");
			sqlQueryObject.addUpdateField("consegna_in_ordine", "?");
			sqlQueryObject.addUpdateField("descrizione", "?");
			sqlQueryObject.addUpdateField("filtro_duplicati", "?");
			sqlQueryObject.addUpdateField("identificativo_collaborazione", "?");
			sqlQueryObject.addUpdateField("id_riferimento_richiesta", "?");
			sqlQueryObject.addUpdateField("nome", "?");
			sqlQueryObject.addUpdateField("profilo_collaborazione", "?");
			sqlQueryObject.addUpdateField("scadenza", "?");
			sqlQueryObject.addUpdateField("formato_specifica", "?");
			sqlQueryObject.addUpdateField("wsdl_concettuale", "?");
			sqlQueryObject.addUpdateField("wsdl_definitorio", "?");
			sqlQueryObject.addUpdateField("wsdl_logico_erogatore", "?");
			sqlQueryObject.addUpdateField("wsdl_logico_fruitore", "?");
			sqlQueryObject.addUpdateField("spec_conv_concettuale", "?");
			sqlQueryObject.addUpdateField("spec_conv_erogatore", "?");
			sqlQueryObject.addUpdateField("spec_conv_fruitore", "?");
			sqlQueryObject.addUpdateField("superuser", "?");
			sqlQueryObject.addUpdateField("utilizzo_senza_azione", "?");
			sqlQueryObject.addUpdateField("privato", "?");

			if(accordoServizio.getStatoPackage()!=null){
				sqlQueryObject.addUpdateField("stato", "?");
			}

			sqlQueryObject.addUpdateField("canale", "?");
			
			if(accordoServizio.getOraRegistrazione()!=null)
				sqlQueryObject.addUpdateField("ora_registrazione", "?");

			sqlQueryObject.addUpdateField("id_referente", "?");
			sqlQueryObject.addUpdateField("versione", "?");
			
			if(utenteUltimaModifica!=null) {
				sqlQueryObject.addUpdateField("utente_ultima_modifica", "?");
			}
			if(dataUltimaModifica!=null) {
				sqlQueryObject.addUpdateField("data_ultima_modifica", "?");
			}

			sqlQueryObject.addWhereCondition("id=?");
			sqlQuery = sqlQueryObject.createSQLUpdate();

			stm = con.prepareStatement(sqlQuery);
			int index = 1;
			stm.setString(index++, DriverRegistroServiziDB_LIB.getValue(serviceBinding));
			stm.setString(index++, DriverRegistroServiziDB_LIB.getValue(messageType));
			stm.setString(index++, DriverRegistroServiziDB_LIB.getValue(confermaRicezione));
			stm.setString(index++, DriverRegistroServiziDB_LIB.getValue(conegnaInOrdine));
			stm.setString(index++, descrizione);
			stm.setString(index++, DriverRegistroServiziDB_LIB.getValue(filtroDuplicati));
			stm.setString(index++, DriverRegistroServiziDB_LIB.getValue(identificativoCollaborazione));
			stm.setString(index++, DriverRegistroServiziDB_LIB.getValue(identificativoRiferimentoRichiesta));
			stm.setString(index++, nome);
			stm.setString(index++, DriverRegistroServiziDB_LIB.getValue(profiloCollaborazione));
			stm.setString(index++, scadenza);
			stm.setString(index++, DriverRegistroServiziDB_LIB.getValue(formatoSpecifica));
			stm.setString(index++, wsdlConcettuale);
			stm.setString(index++, wsdlDefinitorio);
			stm.setString(index++, wsdlLogicoErogatore);
			stm.setString(index++, wsdlLogicoFruitore);
			stm.setString(index++, conversazioneConcettuale);
			stm.setString(index++, conversazioneErogatore);
			stm.setString(index++, conversazioneFruitore);
			stm.setString(index++, superUser);
			stm.setInt(index++, utilizzioSenzaAzione ? CostantiDB.TRUE : CostantiDB.FALSE);
			if(accordoServizio.getPrivato()!=null && accordoServizio.getPrivato())
				stm.setInt(index++, 1);
			else
				stm.setInt(index++, 0);

			if(accordoServizio.getStatoPackage()!=null){
				stm.setString(index++, accordoServizio.getStatoPackage());
			}

			stm.setString(index++, accordoServizio.getCanale());
			
			if(accordoServizio.getOraRegistrazione()!=null){
				stm.setTimestamp(index++, new Timestamp(accordoServizio.getOraRegistrazione().getTime()));
			}

			if(accordoServizio.getSoggettoReferente()!=null) {
				long idSRef = DBUtils.getIdSoggetto(accordoServizio.getSoggettoReferente().getNome(), 
						accordoServizio.getSoggettoReferente().getTipo(), con, DriverRegistroServiziDB_LIB.tipoDB,tabellaSoggetti);
				stm.setLong(index++, idSRef);
			}else{
				stm.setLong(index++, CostantiRegistroServizi.SOGGETTO_REFERENTE_DEFAULT);
			}
			stm.setInt(index++, accordoServizio.getVersione());

			if(utenteUltimaModifica!=null) {
				stm.setString(index++, utenteUltimaModifica);
			}
			
			if(dataUltimaModifica!=null) {
				stm.setTimestamp(index++, dataUltimaModifica);
			}
			
			stm.setLong(index++, idAccordoLong);

			String msgDebug = "update accordoServizio : " + 
					DriverRegistroServiziDB_LIB.formatSQLString(sqlQuery, 
							serviceBinding, messageType,
							confermaRicezione, conegnaInOrdine, descrizione, 
							filtroDuplicati, identificativoCollaborazione, identificativoRiferimentoRichiesta, nome, profiloCollaborazione, scadenza, 
							wsdlConcettuale, wsdlDefinitorio, wsdlLogicoErogatore, wsdlLogicoFruitore, 
							conversazioneConcettuale, conversazioneErogatore, conversazioneFruitore,
							superUser,utilizzioSenzaAzione, idAccordoLong);
			log.debug(msgDebug);

			stm.executeUpdate();
			stm.close();

			//aggiorno le azioni
			//possibile ottimizzazione
			//la lista contiene tutte e sole le azioni necessarie
			//prima cancello le azioni e poi reinserisco quelle nuove
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.ACCORDI_AZIONI);
			sqlQueryObject.addWhereCondition("id_accordo=?");
			String updateString = sqlQueryObject.createSQLDelete();
			stm = con.prepareStatement(updateString);
			stm.setLong(1, idAccordoLong);
			int n=stm.executeUpdate();
			stm.close();
			log.debug("Cancellate "+n+" azioni associate all'accordo "+idAccordoLong);

			for (int i = 0; i < accordoServizio.sizeAzioneList(); i++) {
				Azione azione = accordoServizio.getAzione(i);
				String profiloAzione = azione.getProfAzione();
				//se profilo azione = default allora utilizzo il profilo collaborazione dell'accordo
				if(profiloAzione!=null && profiloAzione.equals(CostantiRegistroServizi.PROFILO_AZIONE_DEFAULT))
				{
					azione.setProfiloCollaborazione(profiloCollaborazione);
				}
				DriverRegistroServiziDB_accordiSoapLIB.CRUDAzione(CostantiDB.CREATE, accordoServizio,azione, con, idAccordoLong);
			}
			log.debug("Inserite "+accordoServizio.sizeAzioneList()+" azioni associate all'accordo "+idAccordoLong);

			//aggiorno i port type
			//TODO possibile ottimizzazione
			//la lista contiene tutte e soli i port type necessari
			//prima cancello i port type e poi reinserisco quelle nuove

			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PORT_TYPE);
			sqlQueryObject.addSelectField("id");
			sqlQueryObject.addWhereCondition("id_accordo = ?");
			sqlQuery = sqlQueryObject.createSQLQuery();
			stm=con.prepareStatement(sqlQuery);
			stm.setLong(1, idAccordoLong);
			rs=stm.executeQuery();
			List<Long> idPT = new ArrayList<Long>();
			while(rs.next()){
				idPT.add(rs.getLong("id"));
			}
			rs.close();
			stm.close();
			log.debug("Trovati "+idPT.size()+" port type...");

			while(idPT.size()>0){
				Long idPortType = idPT.remove(0);

				// Seleziono id port type azione
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORT_TYPE_AZIONI);
				sqlQueryObject.addSelectField("id");
				sqlQueryObject.addWhereCondition("id_port_type=?");
				sqlQueryObject.setANDLogicOperator(true);
				sqlQuery = sqlQueryObject.createSQLQuery();
				stm=con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortType);
				rs=stm.executeQuery();
				List<Long> idPTAzione = new ArrayList<Long>();
				while(rs.next()){
					idPTAzione.add(rs.getLong("id"));
				}
				rs.close();
				stm.close();

				log.debug("Trovati "+idPTAzione.size()+" port type azioni...");

				// Elimino i messages
				while(idPTAzione.size()>0){
					Long idPortTypeAzione = idPTAzione.remove(0);
					log.debug("Eliminazione message con id["+idPortTypeAzione+"]...");
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
					sqlQueryObject.addDeleteTable(CostantiDB.PORT_TYPE_AZIONI_OPERATION_MESSAGES);
					sqlQueryObject.addWhereCondition("id_port_type_azione=?");
					sqlQueryObject.setANDLogicOperator(true);
					sqlQuery = sqlQueryObject.createSQLDelete();
					stm=con.prepareStatement(sqlQuery);
					stm.setLong(1, idPortTypeAzione);
					n=stm.executeUpdate();
					stm.close();
					log.debug("Cancellate "+n+" messages di un'azione con id["+idPortTypeAzione+"] del port type ["+idPortType+"] associate all'accordo "+idAccordoLong);
				}

				log.debug("Elimino port type azione del port types ["+idPortType+"]...");

				// Elimino port types azioni
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORT_TYPE_AZIONI);
				sqlQueryObject.addWhereCondition("id_port_type=?");
				sqlQueryObject.setANDLogicOperator(true);
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm=con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortType);
				n=stm.executeUpdate();
				stm.close();
				log.debug("Cancellate "+n+" azioni del port type ["+idPortType+"] associate all'accordo "+idAccordoLong);
			}

			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.PORT_TYPE);
			sqlQueryObject.addWhereCondition("id_accordo=?");
			sqlQueryObject.setANDLogicOperator(true);
			sqlQuery = sqlQueryObject.createSQLDelete();
			stm=con.prepareStatement(sqlQuery);
			stm.setLong(1, idAccordoLong);
			n=stm.executeUpdate();
			stm.close();
			log.debug("Cancellate "+n+" port type associate all'accordo "+idAccordoLong);

			PortType pt = null;
			for (int i = 0; i < accordoServizio.sizePortTypeList(); i++) {
				pt = accordoServizio.getPortType(i);
				DriverRegistroServiziDB_accordiSoapLIB.CRUDPortType(CostantiDB.CREATE,accordoServizio,pt, con, idAccordoLong);
			}
			log.debug("inserite " + accordoServizio.sizePortTypeList() + " porttype relative all'accordo :" + nome + " id :" + idAccordoLong);

			// risorse
			//TODO possibile ottimizzazione
			//la lista contiene tutte e sole le risorse necessarie
			//prima cancello le risorse e poi reinserisco quelle nuove
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.API_RESOURCES);
			sqlQueryObject.addSelectField("id");
			sqlQueryObject.addWhereCondition("id_accordo = ?");
			sqlQuery = sqlQueryObject.createSQLQuery();
			stm=con.prepareStatement(sqlQuery);
			stm.setLong(1, idAccordoLong);
			rs=stm.executeQuery();
			List<Long> idResources = new ArrayList<Long>();
			while(rs.next()){
				idResources.add(rs.getLong("id"));
			}
			rs.close();
			stm.close();
	
			n=0;
			while(idResources.size()>0){
				Long idR = idResources.remove(0);
				Resource resource = new Resource();
				resource.setId(idR);
				n = n + DriverRegistroServiziDB_accordiRestLIB.CRUDResource(CostantiDB.DELETE, accordoServizio, resource, con, idAccordoLong);
			}
			log.debug("Cancellate "+n+" resources associate all'accordo :" + nome + " id :" + idAccordoLong);
			
			Resource resource = null;
			for (int i = 0; i < accordoServizio.sizeResourceList(); i++) {
				resource = accordoServizio.getResource(i);
				DriverRegistroServiziDB_accordiRestLIB.CRUDResource(CostantiDB.CREATE,accordoServizio,resource, con, idAccordoLong);
			}
			log.debug("inserite " + accordoServizio.sizeResourceList() + " resources relative all'accordo :" + nome + " id :" + idAccordoLong);
			
			
			
			// Gruppi
			//TODO possibile ottimizzazione
			//la lista contiene tutte e sole le risorse necessarie
			//prima cancello le risorse e poi reinserisco quelle nuove
			
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.ACCORDI_GRUPPI);
			sqlQueryObject.addFromTable(CostantiDB.GRUPPI);
			sqlQueryObject.addSelectAliasField(CostantiDB.GRUPPI, "id", "identificativoGruppo");
			sqlQueryObject.addSelectAliasField(CostantiDB.GRUPPI, "nome", "nomeGruppo");
			sqlQueryObject.addWhereCondition(CostantiDB.ACCORDI_GRUPPI+".id_gruppo = "+CostantiDB.GRUPPI+".id");
			sqlQueryObject.addWhereCondition(CostantiDB.ACCORDI_GRUPPI+".id_accordo = ?");
			sqlQueryObject.setANDLogicOperator(true);
			sqlQuery = sqlQueryObject.createSQLQuery();
			stm=con.prepareStatement(sqlQuery);
			stm.setLong(1, idAccordoLong);
			rs=stm.executeQuery();
			List<GruppoAccordo> gruppi = new ArrayList<GruppoAccordo>();
			while(rs.next()){
				GruppoAccordo gruppo = new GruppoAccordo();
				gruppo.setNome(rs.getString("nomeGruppo"));
				gruppo.setId(rs.getLong("identificativoGruppo"));
				gruppi.add(gruppo);
			}
			rs.close();
			stm.close();
	
			while(gruppi.size()>0){
				GruppoAccordo gruppo = gruppi.remove(0);
				DriverRegistroServiziDB_accordiLIB.CRUDAccordoGruppo(CostantiDB.DELETE, accordoServizio, gruppo, con, idAccordoLong);
			}
			log.debug("Cancellate "+n+" resources associate all'accordo :" + nome + " id :" + idAccordoLong);
			
			if(accordoServizio.getGruppi()!=null && accordoServizio.getGruppi().sizeGruppoList()>0) {
				for (int i = 0; i < accordoServizio.getGruppi().sizeGruppoList(); i++) {
					GruppoAccordo gruppo = accordoServizio.getGruppi().getGruppo(i);
					DriverRegistroServiziDB_accordiLIB.CRUDAccordoGruppo(CostantiDB.CREATE,accordoServizio, gruppo, con, idAccordoLong);
				}
				log.debug("inserite " + accordoServizio.sizeAzioneList() + " gruppi relative all'accordo :" + nome + " id :" + idAccordoLong);
			}
			
			
			
			// Accordo servizio composto
			if(accordoServizio.getServizioComposto()!=null){
				// Elimino eventualmente se prima era presente
				DriverRegistroServiziDB_accordiCooperazioneLIB.CRUDAccordoServizioParteComuneServizioComposto(CostantiDB.DELETE, 
						null, con, idAccordoLong);

				DriverRegistroServiziDB_accordiCooperazioneLIB.CRUDAccordoServizioParteComuneServizioComposto(CostantiDB.CREATE, 
						accordoServizio.getServizioComposto(), con, idAccordoLong);
			}else{
				// Elimino eventualmente se prima era presente
				DriverRegistroServiziDB_accordiCooperazioneLIB.CRUDAccordoServizioParteComuneServizioComposto(CostantiDB.DELETE, 
						null, con, idAccordoLong);
			}


			// Documenti generici accordo di servizio
			List<Documento> documenti = new ArrayList<Documento>();
			// Allegati
			for(int i=0; i<accordoServizio.sizeAllegatoList(); i++){
				Documento doc = accordoServizio.getAllegato(i);
				doc.setRuolo(RuoliDocumento.allegato.toString());
				documenti.add(doc);
			}
			// Specifiche Semiformali
			for(int i=0; i<accordoServizio.sizeSpecificaSemiformaleList(); i++){
				Documento doc = accordoServizio.getSpecificaSemiformale(i);
				doc.setRuolo(RuoliDocumento.specificaSemiformale.toString());
				documenti.add(doc);
			}
			// Specifiche Coordinamento
			if(accordoServizio.getServizioComposto()!=null){
				for(int i=0; i<accordoServizio.getServizioComposto().sizeSpecificaCoordinamentoList(); i++){
					Documento doc = accordoServizio.getServizioComposto().getSpecificaCoordinamento(i);
					doc.setRuolo(RuoliDocumento.specificaCoordinamento.toString());
					documenti.add(doc);
				}
			}

			// CRUD
			DriverRegistroServiziDB_documentiLIB.CRUDDocumento(CostantiDB.UPDATE, documenti, idAccordoLong, ProprietariDocumento.accordoServizio, con, DriverRegistroServiziDB_LIB.tipoDB);

			
			// ProtocolProperties
			DriverRegistroServiziDB_LIB.CRUDProtocolProperty(CostantiDB.UPDATE, accordoServizio.getProtocolPropertyList(), 
					idAccordoLong, ProprietariProtocolProperty.ACCORDO_SERVIZIO_PARTE_COMUNE, con, DriverRegistroServiziDB_LIB.tipoDB);
			
			
		}catch (SQLException se) {
			log.error(se.getMessage(),se);
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::updateAccordoServizio] SQLException [" + se.getMessage() + "].",se);
		} 
		catch (Exception se) {
			log.error(se.getMessage(),se);
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::updateAccordoServizio] Exception [" + se.getMessage() + "].",se);
		}finally {
			JDBCUtilities.closeResources(rs, stm);
		}

	}
	
	
	
	public static long CRUDAccordoGruppo(int type, AccordoServizioParteComune as,GruppoAccordo gruppo, Connection con, long idAccordo) throws DriverRegistroServiziException {
		PreparedStatement updateStmt = null;
		String updateQuery;
		long n = 0;
		if (idAccordo <= 0)
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDAccordoGruppo] ID Accordo non valido.");
		
		try {
			
			IDGruppo idGruppo = new IDGruppo(gruppo.getNome());
			long idGruppoLong = DBUtils.getIdGruppo(idGruppo, con, DriverRegistroServiziDB_LIB.tipoDB);
			if(idGruppoLong<=0) {
				throw new Exception("[DriverRegistroServiziDB_LIB::CRUDAccordoGruppo] Gruppo con nome '"+idGruppo.getNome()+"' non esistente.");
			}
			
			switch (type) {
			case CREATE:
								
				// create
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
				sqlQueryObject.addInsertTable(CostantiDB.ACCORDI_GRUPPI);
				sqlQueryObject.addInsertField("id_accordo", "?");
				sqlQueryObject.addInsertField("id_gruppo", "?");
				updateQuery = sqlQueryObject.createSQLInsert();
				updateStmt = con.prepareStatement(updateQuery);
				int index = 1;
				updateStmt.setLong(index++, idAccordo);
				updateStmt.setLong(index++, idGruppoLong);

				DriverRegistroServiziDB_LIB.log.debug("CRUDAccordoGruppo CREATE :\n"+
						DriverRegistroServiziDB_LIB.formatSQLString(updateQuery,idAccordo,idGruppoLong));
				n = updateStmt.executeUpdate();
				updateStmt.close();
				DriverRegistroServiziDB_LIB.log.debug("CRUDAccordoGruppo type = " + type + " row affected =" + n);

				break;

			case UPDATE:
				
				throw new DriverRegistroServiziException("Non supportato");

			case DELETE:
				// delete

				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.ACCORDI_GRUPPI);
				sqlQueryObject.addWhereCondition("id_accordo=?");
				sqlQueryObject.addWhereCondition("id_gruppo=?");
				sqlQueryObject.setANDLogicOperator(true);
				String sqlQuery = sqlQueryObject.createSQLDelete();
				updateStmt = con.prepareStatement(sqlQuery);
				index = 1;
				updateStmt.setLong(index++, idAccordo);
				updateStmt.setLong(index++, idGruppoLong);
				n=updateStmt.executeUpdate();
				updateStmt.close();
				DriverRegistroServiziDB_LIB.log.debug("CRUDAccordoGruppo type = " + type + " row affected =" + n);
				
				break;
			}

			return n;

		} catch (SQLException se) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDAccordoGruppo] SQLException : " + se.getMessage(),se);
		} catch (Exception se) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDAccordoGruppo] Exception : " + se.getMessage(),se);
		} finally {
			JDBCUtilities.closeResources(updateStmt);
		}
	}
	
	

	
	public static ISQLQueryObject getSQLRicercaAccordiValidi() throws Exception{
		ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
		
		sqlQueryObject.addFromTable(CostantiDB.ACCORDI);
		
		sqlQueryObject.addWhereExistsCondition(false, DriverRegistroServiziDB_accordiLIB.getSQLRicercaServiziValidi(CostantiDB.ACCORDI+".id",false));
		
		return sqlQueryObject;
	}
	
	/**
	 * 
	 * @return Ritorna l' ISQLQueryObject con ? settato come id accordo, in modo da poterlo usare con il setParameter dello statement
	 * @throws Exception
	 */
	
	public static ISQLQueryObject getSQLRicercaServiziValidiByIdAccordo(boolean isErogazione) throws Exception{
		return DriverRegistroServiziDB_accordiLIB.getSQLRicercaServiziValidi("?",isErogazione);
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
		ISQLQueryObject sqlQueryServiziValidi = DriverRegistroServiziDB_accordiLIB.getSQLIndividuazioneServiziValidi(DriverRegistroServiziDB_LIB.tipoDB,isErogazione);
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
	
	
	
	public static void deleteAccordoServizioParteComune(org.openspcoop2.core.registry.AccordoServizioParteComune accordoServizio, 
			Connection con, String tabellaSoggetti, Logger log, IDAccordoFactory idAccordoFactory) throws DriverRegistroServiziException {

		if (accordoServizio == null)
			throw new DriverRegistroServiziException("L'AccordoServizio non puo' essere null.");

		String nome = accordoServizio.getNome();
		if (nome == null || nome.equals(""))
			throw new DriverRegistroServiziException("Il nome dell'AccordoServizio non e' valido.");

		PreparedStatement stm = null;
		ResultSet rs = null;
		String sqlQuery = "";

		

		try {

			IDAccordo idAccordo = idAccordoFactory.getIDAccordoFromValues(nome,BeanUtilities.getSoggettoReferenteID(accordoServizio.getSoggettoReferente()),accordoServizio.getVersione());
			long idAccordoLong = DBUtils.getIdAccordoServizioParteComune(idAccordo, con, DriverRegistroServiziDB_LIB.tipoDB);
			if (idAccordoLong <= 0)
				throw new DriverRegistroServiziException("Impossibile recuperare l'id dell'Accordo di Servizio : " + nome);

			// elimino tutte le azioni correlate con questo accordo
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.ACCORDI_AZIONI);
			sqlQueryObject.addWhereCondition("id_accordo=?");
			String updateString = sqlQueryObject.createSQLDelete();
			stm = con.prepareStatement(updateString);
			stm.setLong(1, idAccordoLong);
			log.debug("delete azioni :" + DriverRegistroServiziDB_LIB.formatSQLString(sqlQuery, idAccordoLong));
			int n=stm.executeUpdate();
			stm.close();
			log.debug("cancellate " + n + " azioni.");

			
			// elimino tutte i port type e struttura interna
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PORT_TYPE);
			sqlQueryObject.addSelectField("id");
			sqlQueryObject.addWhereCondition("id_accordo = ?");
			sqlQuery = sqlQueryObject.createSQLQuery();
			stm=con.prepareStatement(sqlQuery);
			stm.setLong(1, idAccordoLong);
			rs=stm.executeQuery();
			List<Long> idPT = new ArrayList<Long>();
			while(rs.next()){
				idPT.add(rs.getLong("id"));
			}
			rs.close();
			stm.close();

			while(idPT.size()>0){
				Long idPortType = idPT.remove(0);

				// gestione operation_messages
				List<Long> idPortTypeAzioni = new ArrayList<Long>();
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORT_TYPE_AZIONI);
				sqlQueryObject.addSelectField("id");
				sqlQueryObject.addWhereCondition("id_port_type=?");
				sqlQuery = sqlQueryObject.createSQLQuery();
				stm=con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortType);
				rs=stm.executeQuery();
				while(rs.next()){
					idPortTypeAzioni.add(rs.getLong("id"));
				}
				rs.close();
				stm.close();

				while(idPortTypeAzioni.size()>0){
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
					sqlQueryObject.addDeleteTable(CostantiDB.PORT_TYPE_AZIONI_OPERATION_MESSAGES);
					sqlQueryObject.addWhereCondition("id_port_type_azione=?");
					sqlQueryObject.setANDLogicOperator(true);
					sqlQuery = sqlQueryObject.createSQLDelete();
					stm=con.prepareStatement(sqlQuery);
					stm.setLong(1, idPortTypeAzioni.remove(0));
					n=stm.executeUpdate();
					stm.close();
				}

				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORT_TYPE_AZIONI);
				sqlQueryObject.addWhereCondition("id_port_type=?");
				sqlQueryObject.setANDLogicOperator(true);
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm=con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortType);
				n=stm.executeUpdate();
				stm.close();
				log.debug("Cancellate "+n+" azioni del port type ["+idPortType+"] associate all'accordo "+idAccordoLong);
			}

			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.PORT_TYPE);
			sqlQueryObject.addWhereCondition("id_accordo=?");
			sqlQueryObject.setANDLogicOperator(true);
			sqlQuery = sqlQueryObject.createSQLDelete();
			stm=con.prepareStatement(sqlQuery);
			stm.setLong(1, idAccordoLong);
			n=stm.executeUpdate();
			stm.close();
			log.debug("Cancellate "+n+" port type associate all'accordo "+idAccordoLong);

			
			
			// Risorse
			// elimino tutte le risorse api comprese di struttura interna
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.API_RESOURCES);
			sqlQueryObject.addSelectField("id");
			sqlQueryObject.addWhereCondition("id_accordo = ?");
			sqlQuery = sqlQueryObject.createSQLQuery();
			stm=con.prepareStatement(sqlQuery);
			stm.setLong(1, idAccordoLong);
			rs=stm.executeQuery();
			List<Long> idResources = new ArrayList<Long>();
			while(rs.next()){
				idResources.add(rs.getLong("id"));
			}
			rs.close();
			stm.close();
	
			while(idResources.size()>0){
				Long idR = idResources.remove(0);
				Resource resource = new Resource();
				resource.setId(idR);
				DriverRegistroServiziDB_accordiRestLIB.CRUDResource(CostantiDB.DELETE, accordoServizio, resource, con, idAccordoLong);
			}
			
			
			// Gruppi
			//TODO possibile ottimizzazione
			//la lista contiene tutte e sole le risorse necessarie
			//prima cancello le risorse e poi reinserisco quelle nuove
			
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.ACCORDI_GRUPPI);
			sqlQueryObject.addFromTable(CostantiDB.GRUPPI);
			sqlQueryObject.addSelectAliasField(CostantiDB.GRUPPI, "id", "identificativoGruppo");
			sqlQueryObject.addSelectAliasField(CostantiDB.GRUPPI, "nome", "nomeGruppo");
			sqlQueryObject.addWhereCondition(CostantiDB.ACCORDI_GRUPPI+".id_gruppo = "+CostantiDB.GRUPPI+".id");
			sqlQueryObject.addWhereCondition(CostantiDB.ACCORDI_GRUPPI+".id_accordo = ?");
			sqlQueryObject.setANDLogicOperator(true);
			sqlQuery = sqlQueryObject.createSQLQuery();
			stm=con.prepareStatement(sqlQuery);
			stm.setLong(1, idAccordoLong);
			rs=stm.executeQuery();
			List<GruppoAccordo> gruppi = new ArrayList<GruppoAccordo>();
			while(rs.next()){
				GruppoAccordo gruppo = new GruppoAccordo();
				gruppo.setNome(rs.getString("nomeGruppo"));
				gruppo.setId(rs.getLong("identificativoGruppo"));
				gruppi.add(gruppo);
			}
			rs.close();
			stm.close();
	
			while(gruppi.size()>0){
				GruppoAccordo gruppo = gruppi.remove(0);
				DriverRegistroServiziDB_accordiLIB.CRUDAccordoGruppo(CostantiDB.DELETE, accordoServizio, gruppo, con, idAccordoLong);
			}
			log.debug("Cancellate "+n+" resources associate all'accordo :" + nome + " id :" + idAccordoLong);
			
			
			
			// Documenti generici accordo di servizio
			// Allegati
			// Specifiche Semiformali
			// Speficiche Coordinamento
			DriverRegistroServiziDB_documentiLIB.CRUDDocumento(CostantiDB.DELETE, null, idAccordoLong, ProprietariDocumento.accordoServizio, con, DriverRegistroServiziDB_LIB.tipoDB);


			// Accordo servizio composto
			if(accordoServizio.getServizioComposto()!=null){
				DriverRegistroServiziDB_accordiCooperazioneLIB.CRUDAccordoServizioParteComuneServizioComposto(CostantiDB.DELETE, 
						accordoServizio.getServizioComposto(), con, idAccordoLong);
			}else{
				DriverRegistroServiziDB_accordiCooperazioneLIB.CRUDAccordoServizioParteComuneServizioComposto(CostantiDB.DELETE, 
						null, con, idAccordoLong);
			}

			
			// ProtocolProperties
			DriverRegistroServiziDB_LIB.CRUDProtocolProperty(CostantiDB.DELETE, null, 
					idAccordoLong, ProprietariProtocolProperty.ACCORDO_SERVIZIO_PARTE_COMUNE, con, DriverRegistroServiziDB_LIB.tipoDB);
			

			// elimino accordoservizio
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.ACCORDI);
			sqlQueryObject.addWhereCondition("id=?");
			updateString = sqlQueryObject.createSQLDelete();
			stm = con.prepareStatement(updateString);
			stm.setLong(1, idAccordoLong);
			log.debug("delete accordoServizio :" + DriverRegistroServiziDB_LIB.formatSQLString(sqlQuery, idAccordoLong));
			stm.executeUpdate();
			stm.close();


		} catch (SQLException se) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::deleteAccordoServizio] SQLException [" + se.getMessage() + "].",se);
		} catch (Exception se) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::deleteAccordoServizio] Exception [" + se.getMessage() + "].",se);
		}finally {
			JDBCUtilities.closeResources(rs, stm);
		}
	}
	
}
