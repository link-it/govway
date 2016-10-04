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

package org.openspcoop2.protocol.engine.archive;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.openspcoop2.core.commons.DBOggettiInUsoUtils;
import org.openspcoop2.core.commons.ErrorsHandlerCostant;
import org.openspcoop2.core.config.Configurazione;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDAccordoCooperazione;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDPortaApplicativaByNome;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.AccordoCooperazione;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Fruitore;
import org.openspcoop2.core.registry.PortaDominio;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziNotFound;
import org.openspcoop2.protocol.sdk.archive.Archive;
import org.openspcoop2.protocol.sdk.archive.ArchiveAccordoCooperazione;
import org.openspcoop2.protocol.sdk.archive.ArchiveAccordoServizioComposto;
import org.openspcoop2.protocol.sdk.archive.ArchiveAccordoServizioParteComune;
import org.openspcoop2.protocol.sdk.archive.ArchiveAccordoServizioParteSpecifica;
import org.openspcoop2.protocol.sdk.archive.ArchiveEsitoDelete;
import org.openspcoop2.protocol.sdk.archive.ArchiveEsitoImportDetail;
import org.openspcoop2.protocol.sdk.archive.ArchiveEsitoImportDetailConfigurazione;
import org.openspcoop2.protocol.sdk.archive.ArchiveFruitore;
import org.openspcoop2.protocol.sdk.archive.ArchivePdd;
import org.openspcoop2.protocol.sdk.archive.ArchivePortaApplicativa;
import org.openspcoop2.protocol.sdk.archive.ArchivePortaDelegata;
import org.openspcoop2.protocol.sdk.archive.ArchiveServizioApplicativo;
import org.openspcoop2.protocol.sdk.archive.ArchiveSoggetto;
import org.openspcoop2.protocol.sdk.constants.ArchiveStatoImport;

/**
 *  DeleterArchiveUtils
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DeleterArchiveUtils {

	private AbstractArchiveEngine importerEngine;
	private Logger log;
	private String userLogin;
	
	public DeleterArchiveUtils(AbstractArchiveEngine importerEngine,Logger log,
			String userLogin) throws Exception{
		this.importerEngine = importerEngine;
		this.log = log;
		this.userLogin = userLogin;
	}
	
	private static String NEW_LINE = "\n\t\t";

	
	
	public ArchiveEsitoDelete deleteArchive(Archive archive, String userLogin) throws Exception,ImportInformationMissingException{
		try{
			
			ArchiveEsitoDelete esito = new ArchiveEsitoDelete();
			
			
			// PorteApplicative
			for (int i = 0; i < archive.getPorteApplicative().size(); i++) {
				ArchivePortaApplicativa archivePortaApplicativa = archive.getPorteApplicative().get(i);
				ArchiveEsitoImportDetail detail = new ArchiveEsitoImportDetail(archivePortaApplicativa);
				try{
					archivePortaApplicativa.update();
					this.deletePortaApplicativa(archivePortaApplicativa, detail);
				}catch(Exception e){
					detail.setState(ArchiveStatoImport.ERROR);
					detail.setException(e);
				}
				esito.getPorteApplicative().add(detail);
			}
			
			
			// PorteDelegate
			for (int i = 0; i < archive.getPorteDelegate().size(); i++) {
				ArchivePortaDelegata archivePortaDelegata = archive.getPorteDelegate().get(i);
				ArchiveEsitoImportDetail detail = new ArchiveEsitoImportDetail(archivePortaDelegata);
				try{
					archivePortaDelegata.update();
					this.deletePortaDelegata(archivePortaDelegata, detail);
				}catch(Exception e){
					detail.setState(ArchiveStatoImport.ERROR);
					detail.setException(e);
				}
				esito.getPorteDelegate().add(detail);
			}
			
			
			// Fruitori
			for (int i = 0; i < archive.getAccordiFruitori().size(); i++) {
				ArchiveFruitore archiveFruitore = archive.getAccordiFruitori().get(i);
				ArchiveEsitoImportDetail detail = new ArchiveEsitoImportDetail(archiveFruitore);
				try{
					archiveFruitore.update();
					this.deleteFruitore(archiveFruitore, detail);
				}catch(Exception e){
					detail.setState(ArchiveStatoImport.ERROR);
					detail.setException(e);
				}
				esito.getAccordiFruitori().add(detail);
			}
			
			
			// Divisione asps che implementano accordi di servizio parte comune,
			// da asps che implementano servizi composti
			List<ArchiveAccordoServizioParteSpecifica> listAccordiServizioParteSpecifica = 
					new ArrayList<ArchiveAccordoServizioParteSpecifica>();
			List<ArchiveAccordoServizioParteSpecifica> listAccordiServizioParteSpecifica_serviziComposti = 
					new ArrayList<ArchiveAccordoServizioParteSpecifica>();
			for (int i = 0; i < archive.getAccordiServizioParteSpecifica().size(); i++) {
				ArchiveAccordoServizioParteSpecifica archiveAccordoServizioParteSpecifica =
						archive.getAccordiServizioParteSpecifica().get(i);
				archiveAccordoServizioParteSpecifica.update();
				IDAccordo idAccordo = archiveAccordoServizioParteSpecifica.getIdAccordoServizioParteComune();
				if(this.importerEngine.existsAccordoServizioParteComune(idAccordo)){
					// verifico la tipologia
					AccordoServizioParteComune aspc = this.importerEngine.getAccordoServizioParteComune(idAccordo);
					if(aspc.getServizioComposto()!=null){
						listAccordiServizioParteSpecifica_serviziComposti.add(archiveAccordoServizioParteSpecifica);	
					}else{
						listAccordiServizioParteSpecifica.add(archiveAccordoServizioParteSpecifica);	
					}
				}
				else{
					// aggiungo alla lista di servizi composti, visto che l'accordo riferito dovrebbe essere definito nell'archivio che si sta importando
					// se cosi' non fosse, la gestione dell'accordo di servizio parte specifica per servizi composti comunque
					// segnalera' che l'accordo riferito non esiste
					listAccordiServizioParteSpecifica_serviziComposti.add(archiveAccordoServizioParteSpecifica);	
				}
			}
			
			
			
			// Accordi di Servizio Parte Specifica (implementano accordi di servizio composto)
			for (int i = 0; i < listAccordiServizioParteSpecifica_serviziComposti.size(); i++) {
				ArchiveAccordoServizioParteSpecifica archiveAccordoServizioParteSpecifica = listAccordiServizioParteSpecifica_serviziComposti.get(i);
				ArchiveEsitoImportDetail detail = new ArchiveEsitoImportDetail(archiveAccordoServizioParteSpecifica);
				try{
					//archiveAccordoServizioParteSpecifica.update(); eseguito durante la preparazione della lista listAccordiServizioParteSpecifica_serviziComposti
					this.deleteAccordoServizioParteSpecifica(archiveAccordoServizioParteSpecifica, true, detail);
				}catch(Exception e){
					detail.setState(ArchiveStatoImport.ERROR);
					detail.setException(e);
				}
				esito.getAccordiServizioParteSpecificaServiziComposti().add(detail);
			}
			
			
			// Accordi di Servizio Composto
			for (int i = 0; i < archive.getAccordiServizioComposto().size(); i++) {
				ArchiveAccordoServizioComposto archiveAccordoServizioComposto = archive.getAccordiServizioComposto().get(i);
				ArchiveEsitoImportDetail detail = new ArchiveEsitoImportDetail(archiveAccordoServizioComposto);
				try{
					archiveAccordoServizioComposto.update();
					this.deleteAccordoServizioComposto(archiveAccordoServizioComposto, detail);
				}catch(Exception e){
					detail.setState(ArchiveStatoImport.ERROR);
					detail.setException(e);
				}
				esito.getAccordiServizioComposto().add(detail);
			}
			
			// Accordi di Servizio Parte Specifica (implementano accordi di servizio parte comune)
			for (int i = 0; i < listAccordiServizioParteSpecifica.size(); i++) {
				ArchiveAccordoServizioParteSpecifica archiveAccordoServizioParteSpecifica = listAccordiServizioParteSpecifica.get(i);
				ArchiveEsitoImportDetail detail = new ArchiveEsitoImportDetail(archiveAccordoServizioParteSpecifica);
				try{
					//archiveAccordoServizioParteSpecifica.update(); eseguito durante la preparazione della lista listAccordiServizioParteSpecifica
					this.deleteAccordoServizioParteSpecifica(archiveAccordoServizioParteSpecifica, false, detail);
				}catch(Exception e){
					detail.setState(ArchiveStatoImport.ERROR);
					detail.setException(e);
				}
				esito.getAccordiServizioParteSpecifica().add(detail);
			}
			
			// Accordi di Servizio Parte Comune
			for (int i = 0; i < archive.getAccordiServizioParteComune().size(); i++) {
				ArchiveAccordoServizioParteComune archiveAccordoServizioParteComune = archive.getAccordiServizioParteComune().get(i);
				ArchiveEsitoImportDetail detail = new ArchiveEsitoImportDetail(archiveAccordoServizioParteComune);
				try{
					archiveAccordoServizioParteComune.update();
					this.deleteAccordoServizioParteComune(archiveAccordoServizioParteComune, detail);
				}catch(Exception e){
					detail.setState(ArchiveStatoImport.ERROR);
					detail.setException(e);
				}
				esito.getAccordiServizioParteComune().add(detail);
			}
			
			// Accordi di Cooperazione
			for (int i = 0; i < archive.getAccordiCooperazione().size(); i++) {
				ArchiveAccordoCooperazione archiveAccordoCooperazione = archive.getAccordiCooperazione().get(i);
				ArchiveEsitoImportDetail detail = new ArchiveEsitoImportDetail(archiveAccordoCooperazione);
				try{
					archiveAccordoCooperazione.update();
					this.deleteAccordoCooperazione(archiveAccordoCooperazione, detail);
				}catch(Exception e){
					detail.setState(ArchiveStatoImport.ERROR);
					detail.setException(e);
				}
				esito.getAccordiCooperazione().add(detail);
			}
			
			// Servizi Applicativi
			for (int i = 0; i < archive.getServiziApplicativi().size(); i++) {
				ArchiveServizioApplicativo archiveServizioApplicativo = archive.getServiziApplicativi().get(i);
				ArchiveEsitoImportDetail detail = new ArchiveEsitoImportDetail(archiveServizioApplicativo);
				try{
					archiveServizioApplicativo.update();
					this.deleteServizioApplicativo(archiveServizioApplicativo, detail);
				}catch(Exception e){
					detail.setState(ArchiveStatoImport.ERROR);
					detail.setException(e);
				}
				esito.getServiziApplicativi().add(detail);
			}
			
			// Soggetti
			for (int i = 0; i < archive.getSoggetti().size(); i++) {
				ArchiveSoggetto archiveSoggetto = archive.getSoggetti().get(i);
				ArchiveEsitoImportDetail detail = new ArchiveEsitoImportDetail(archiveSoggetto);
				try{
					this.deleteSoggetto(archiveSoggetto, detail);
				}catch(Exception e){
					detail.setState(ArchiveStatoImport.ERROR);
					detail.setException(e);
				}
				esito.getSoggetti().add(detail);
			}
			
			// Pdd
			for (int i = 0; i < archive.getPdd().size(); i++) {
				ArchivePdd archivePdd = archive.getPdd().get(i);
				ArchiveEsitoImportDetail detail = new ArchiveEsitoImportDetail(archivePdd);
				try{
					this.deletePdd(archivePdd, detail);
				}catch(Exception e){
					detail.setState(ArchiveStatoImport.ERROR);
					detail.setException(e);
				}
				esito.getPdd().add(detail);
			}
			
			return esito;
			
		}catch(Exception e){
			throw e;
		}
	}
	
	
	
	
	public void deletePdd(ArchivePdd archivePdd,ArchiveEsitoImportDetail detail){
		
		String nomePdd = archivePdd.getNomePdd();
		try{
			
			// --- check esistenza ---
			if(this.importerEngine.existsPortaDominio(nomePdd)==false){
				detail.setState(ArchiveStatoImport.DELETED_NOT_EXISTS);
				return;
			}
			
			
			// ---- visibilita' oggetto che si vuole eliminare ---
			
			// porta di dominio
			PortaDominio pddReadFromDb = this.importerEngine.getPortaDominio(nomePdd);
			if(this.importerEngine.isVisioneOggettiGlobale(this.userLogin)==false){
				if(this.userLogin.equals(pddReadFromDb.getSuperUser())==false){
					throw new Exception("La Porta di Dominio ["+nomePdd+"] non è visibile/eliminabile dall'utente collegato ("+this.userLogin+")");
				}
			}
			
			
			// ---- controllo di utilizzo dell'oggetto tramite altri oggetti ---
			
			List<String> whereIsInUso = new ArrayList<String>();
			if (this.importerEngine.isPddInUso(nomePdd, whereIsInUso)) {
				throw new Exception(NEW_LINE+DBOggettiInUsoUtils.toString(nomePdd, whereIsInUso,false,NEW_LINE));
			}
			
			
			// --- delete ---
			this.importerEngine.deletePortaDominio(archivePdd.getPortaDominio());
			detail.setState(ArchiveStatoImport.DELETED);				

						
		}			
		catch(Exception e){
			this.log.error("Errore durante l'eliminazione della porta di dominio ["+nomePdd+"]: "+e.getMessage(),e);
			detail.setState(ArchiveStatoImport.ERROR);
			detail.setException(e);
		}
	}
	
	
	
	public void deleteSoggetto(ArchiveSoggetto archiveSoggetto,ArchiveEsitoImportDetail detail){
		
		IDSoggetto idSoggetto = archiveSoggetto.getIdSoggetto();
		try{
			
			boolean delete = false;
			
			if(archiveSoggetto.getSoggettoRegistro()!=null){
								
				
				// --- check esistenza ---
				if(this.importerEngine.existsSoggettoRegistro(idSoggetto)){
									
					// ---- visibilita' oggetto che si vuole eliminare ----
					org.openspcoop2.core.registry.Soggetto old = this.importerEngine.getSoggettoRegistro(idSoggetto);
					if(this.importerEngine.isVisioneOggettiGlobale(this.userLogin)==false){
						if(this.userLogin.equals(old.getSuperUser())==false){
							throw new Exception("Il soggetto non è visibile/eliminabile dall'utente collegato ("+this.userLogin+")");
						}
					}
	
					// ---- controllo di utilizzo dell'oggetto tramite altri oggetti ---
					
					HashMap<ErrorsHandlerCostant, List<String>> whereIsInUso = new HashMap<ErrorsHandlerCostant, List<String>>();
					if (this.importerEngine.isSoggettoRegistroInUso(idSoggetto, whereIsInUso)){
						throw new Exception(NEW_LINE+DBOggettiInUsoUtils.toString(idSoggetto, whereIsInUso,false,NEW_LINE));
					}
					
					// --- delete ---
					this.importerEngine.deleteSoggettoRegistro(archiveSoggetto.getSoggettoRegistro());
					delete = true;
								
				}
					
			}
			
			if(archiveSoggetto.getSoggettoConfigurazione()!=null){
			
				// --- check esistenza ---
				if(this.importerEngine.existsSoggettoConfigurazione(idSoggetto)){
					
					// ---- controllo di utilizzo dell'oggetto tramite altri oggetti ---
					
					HashMap<ErrorsHandlerCostant, List<String>> whereIsInUso = new HashMap<ErrorsHandlerCostant, List<String>>();
					if (this.importerEngine.isSoggettoConfigurazioneInUso(idSoggetto, whereIsInUso)){
						throw new Exception(NEW_LINE+DBOggettiInUsoUtils.toString(idSoggetto, whereIsInUso,false,NEW_LINE));
					}
					
					// --- delete ---
					this.importerEngine.deleteSoggettoConfigurazione(archiveSoggetto.getSoggettoConfigurazione());
					delete = true;
				}
				
			}
			
			
			// --- upload ---
			if(delete){
				detail.setState(ArchiveStatoImport.DELETED);
			}else{
				detail.setState(ArchiveStatoImport.DELETED_NOT_EXISTS);
			}
			
		}			
		catch(Exception e){
			this.log.error("Errore durante l'eliminazione del soggetto ["+idSoggetto+"]: "+e.getMessage(),e);
			detail.setState(ArchiveStatoImport.ERROR);
			detail.setException(e);
		}
	}
	
	
	
	
	public void deleteServizioApplicativo(ArchiveServizioApplicativo archiveServizioApplicativo,
			ArchiveEsitoImportDetail detail){
		
		IDServizioApplicativo idServizioApplicativo = archiveServizioApplicativo.getIdServizioApplicativo();
		IDSoggetto idSoggettoProprietario = archiveServizioApplicativo.getIdSoggettoProprietario();
		try{
			
			// --- check esistenza ---
			if(this.importerEngine.existsServizioApplicativo(idServizioApplicativo)==false){
				detail.setState(ArchiveStatoImport.DELETED_NOT_EXISTS);
				return;
			}
			
			
			// ---- visibilita' oggetto che si vuole eliminare ----
			
			// Devo vedere che l'oggetto che voglio eliminare sia visibile dall'utente.
			// Il SA è visibile se è visibile il soggetto proprietario
			
			org.openspcoop2.core.config.Soggetto soggetto = this.importerEngine.getSoggettoConfigurazione(idSoggettoProprietario);
			if(this.importerEngine.isVisioneOggettiGlobale(this.userLogin)==false){
				if(this.userLogin.equals(soggetto.getSuperUser())==false){
					throw new Exception("Il soggetto proprietario ["+idSoggettoProprietario+"] non è visibile/eliminabile dall'utente collegato ("+this.userLogin+")");
				}
			}
			
			
			// ---- controllo di utilizzo dell'oggetto tramite altri oggetti ---
			
			HashMap<ErrorsHandlerCostant, List<String>> whereIsInUso = new HashMap<ErrorsHandlerCostant, List<String>>();
			if (this.importerEngine.isServizioApplicativoInUso(idServizioApplicativo, whereIsInUso)){
				throw new Exception(NEW_LINE+DBOggettiInUsoUtils.toString(idServizioApplicativo, whereIsInUso,false,NEW_LINE));
			}
			
		
			// --- delete ---
			this.importerEngine.deleteServizioApplicativo(archiveServizioApplicativo.getServizioApplicativo());
			detail.setState(ArchiveStatoImport.DELETED);
						
		}			
		catch(Exception e){
			this.log.error("Errore durante l'eliminazione del servizio applicativo ["+idServizioApplicativo+"]: "+e.getMessage(),e);
			detail.setState(ArchiveStatoImport.ERROR);
			detail.setException(e);
		}
	}
	
	
	
	

	public void deleteAccordoCooperazione(ArchiveAccordoCooperazione archiveAccordoCooperazione,
			ArchiveEsitoImportDetail detail){
		
		IDAccordoCooperazione idAccordoCooperazione = archiveAccordoCooperazione.getIdAccordoCooperazione();
		try{
			// --- check esistenza ---
			if(this.importerEngine.existsAccordoCooperazione(idAccordoCooperazione)==false){
				detail.setState(ArchiveStatoImport.DELETED_NOT_EXISTS);
				return;
			}
			
			// ---- visibilita' oggetto che si vuole eliminare ---
			
			// accordo di cooperazione
			AccordoCooperazione acReadFromDb = this.importerEngine.getAccordoCooperazione(idAccordoCooperazione);
			if(this.importerEngine.isVisioneOggettiGlobale(this.userLogin)==false){
				if(this.userLogin.equals(acReadFromDb.getSuperUser())==false){
					throw new Exception("L'accordo di cooperazione ["+idAccordoCooperazione+"] non è visibile/eliminabile dall'utente collegato ("+this.userLogin+")");
				}
			}
			
			
			// ---- controllo di utilizzo dell'oggetto tramite altri oggetti ---
			
			HashMap<ErrorsHandlerCostant, List<String>> whereIsInUso = new HashMap<ErrorsHandlerCostant, List<String>>();
			if (this.importerEngine.isAccordoCooperazioneInUso(idAccordoCooperazione, whereIsInUso)){
				throw new Exception(NEW_LINE+DBOggettiInUsoUtils.toString(idAccordoCooperazione, whereIsInUso,false,NEW_LINE));
			}
			
			
			// --- delete ---
			this.importerEngine.deleteAccordoCooperazione(archiveAccordoCooperazione.getAccordoCooperazione());
			detail.setState(ArchiveStatoImport.DELETED);				

		}			
		catch(Exception e){
			this.log.error("Errore durante l'eliminazione dell'accordo di cooperazione ["+idAccordoCooperazione+"]: "+e.getMessage(),e);
			detail.setState(ArchiveStatoImport.ERROR);
			detail.setException(e);
		}
	}
	

	
	
	public void deleteAccordoServizioParteComune(ArchiveAccordoServizioParteComune archiveAccordoServizioParteComune,
			ArchiveEsitoImportDetail detail){
		
		IDAccordo idAccordoServizioParteComune = archiveAccordoServizioParteComune.getIdAccordoServizioParteComune();
		try{
			// --- check esistenza ---
			if(this.importerEngine.existsAccordoServizioParteComune(idAccordoServizioParteComune)==false){
				detail.setState(ArchiveStatoImport.DELETED_NOT_EXISTS);
				return;
			}
		
			
			// ---- visibilita' oggetto che si vuole eliminare ---
			
			// accordo di servizio parte comune
			AccordoServizioParteComune aspcReadFromDb = this.importerEngine.getAccordoServizioParteComune(idAccordoServizioParteComune);
			if(this.importerEngine.isVisioneOggettiGlobale(this.userLogin)==false){
				if(this.userLogin.equals(aspcReadFromDb.getSuperUser())==false){
					throw new Exception("L'accordo di servizio parte comune ["+idAccordoServizioParteComune+"] non è visibile/eliminabile dall'utente collegato ("+this.userLogin+")");
				}
			}
			
			
			// ---- controllo di utilizzo dell'oggetto tramite altri oggetti ---
			
			HashMap<ErrorsHandlerCostant, List<String>> whereIsInUso = new HashMap<ErrorsHandlerCostant, List<String>>();
			if (this.importerEngine.isAccordoServizioParteComuneInUso(idAccordoServizioParteComune, whereIsInUso)){
				throw new Exception(NEW_LINE+DBOggettiInUsoUtils.toString(idAccordoServizioParteComune, whereIsInUso,false,NEW_LINE));
			}
			
			
			// --- delete ---
			this.importerEngine.deleteAccordoServizioParteComune(archiveAccordoServizioParteComune.getAccordoServizioParteComune());
			detail.setState(ArchiveStatoImport.DELETED);
			
		}			
		catch(Exception e){
			this.log.error("Errore durante l'eliminazione dell'accordo di servizio parte comune ["+idAccordoServizioParteComune+"]: "+e.getMessage(),e);
			detail.setState(ArchiveStatoImport.ERROR);
			detail.setException(e);
		}
	}
	
	
	public void deleteAccordoServizioComposto(ArchiveAccordoServizioComposto archiveAccordoServizioComposto,
			ArchiveEsitoImportDetail detail){
		
		IDAccordo idAccordoServizioComposto = archiveAccordoServizioComposto.getIdAccordoServizioParteComune();
		try{
			// --- check esistenza ---
			if(this.importerEngine.existsAccordoServizioParteComune(idAccordoServizioComposto)==false){
				detail.setState(ArchiveStatoImport.DELETED_NOT_EXISTS);
				return;
			}
						
			// ---- visibilita' oggetto che si vuole eliminare ---
			
			// accordo di servizio composto
			AccordoServizioParteComune ascReadFromDb = this.importerEngine.getAccordoServizioParteComune(idAccordoServizioComposto);
			if(this.importerEngine.isVisioneOggettiGlobale(this.userLogin)==false){
				if(this.userLogin.equals(ascReadFromDb.getSuperUser())==false){
					throw new Exception("L'accordo di servizio composto ["+idAccordoServizioComposto+"] non è visibile/eliminabile dall'utente collegato ("+this.userLogin+")");
				}
			}
			
			
			// ---- controllo di utilizzo dell'oggetto tramite altri oggetti ---
			
			HashMap<ErrorsHandlerCostant, List<String>> whereIsInUso = new HashMap<ErrorsHandlerCostant, List<String>>();
			if (this.importerEngine.isAccordoServizioParteComuneInUso(idAccordoServizioComposto, whereIsInUso)){
				throw new Exception(NEW_LINE+DBOggettiInUsoUtils.toString(idAccordoServizioComposto, whereIsInUso,false,NEW_LINE));
			}
			
			
			// --- delete ---
			this.importerEngine.deleteAccordoServizioParteComune(archiveAccordoServizioComposto.getAccordoServizioParteComune());
			detail.setState(ArchiveStatoImport.DELETED);
			
		}			
		catch(Exception e){
			this.log.error("Errore durante l'eliminazione dell'accordo di servizio parte comune ["+idAccordoServizioComposto+"]: "+e.getMessage(),e);
			detail.setState(ArchiveStatoImport.ERROR);
			detail.setException(e);
		}
	}

	
	public void deleteAccordoServizioParteSpecifica(ArchiveAccordoServizioParteSpecifica archiveAccordoServizioParteSpecifica,
			boolean servizioComposto,
			ArchiveEsitoImportDetail detail){
		
		IDAccordo idAccordoServizioParteSpecifica = archiveAccordoServizioParteSpecifica.getIdAccordoServizioParteSpecifica();
		try{
			// --- check esistenza ---
			if(this.importerEngine.existsAccordoServizioParteSpecifica(idAccordoServizioParteSpecifica)==false){
				detail.setState(ArchiveStatoImport.DELETED_NOT_EXISTS);
				return;
			}
						
				
			// ---- visibilita' oggetto che si vuole eliminare ---
			
			// accordo di servizio parte specifica
			AccordoServizioParteSpecifica aspsReadFromDb = this.importerEngine.getAccordoServizioParteSpecifica(idAccordoServizioParteSpecifica);
			if(this.importerEngine.isVisioneOggettiGlobale(this.userLogin)==false){
				if(this.userLogin.equals(aspsReadFromDb.getSuperUser())==false){
					throw new Exception("L'accordo di servizio parte specifica ["+idAccordoServizioParteSpecifica+"] non è visibile/eliminabile dall'utente collegato ("+this.userLogin+")");
				}
			}
			
			
			// ---- controllo di utilizzo dell'oggetto tramite altri oggetti ---
			
			HashMap<ErrorsHandlerCostant, List<String>> whereIsInUso = new HashMap<ErrorsHandlerCostant, List<String>>();
			if (this.importerEngine.isAccordoServizioParteSpecificaInUso(archiveAccordoServizioParteSpecifica.getIdServizio(), whereIsInUso)){
				throw new Exception(NEW_LINE+DBOggettiInUsoUtils.toString(archiveAccordoServizioParteSpecifica.getIdServizio(), whereIsInUso,false,NEW_LINE));
			}
			
			
			// --- delete ---
			this.importerEngine.deleteAccordoServizioParteSpecifica(archiveAccordoServizioParteSpecifica.getAccordoServizioParteSpecifica());
			detail.setState(ArchiveStatoImport.DELETED);
						
		}			
		catch(Exception e){
			this.log.error("Errore durante l'eliminazione dell'accordo di servizio parte specifica ["+idAccordoServizioParteSpecifica+"]: "+e.getMessage(),e);
			detail.setState(ArchiveStatoImport.ERROR);
			detail.setException(e);
		}
	}
	
	
	
	
	public void deleteFruitore(ArchiveFruitore archiveFruitore,
			ArchiveEsitoImportDetail detail){
		
		IDAccordo idAccordoServizioParteSpecifica = archiveFruitore.getIdAccordoServizioParteSpecifica();
		IDSoggetto idSoggettoFruitore = archiveFruitore.getIdSoggettoFruitore();

		try{
			AccordoServizioParteSpecifica oldAccordo = null;
			Fruitore old = null;
			if(this.importerEngine.existsAccordoServizioParteSpecifica(idAccordoServizioParteSpecifica)){
				oldAccordo = this.importerEngine.getAccordoServizioParteSpecifica(idAccordoServizioParteSpecifica);
				for (int i = 0; i < oldAccordo.sizeFruitoreList(); i++) {
					Fruitore check = oldAccordo.getFruitore(i);
					if(check.getTipo().equals(idSoggettoFruitore.getTipo()) &&
							check.getNome().equals(idSoggettoFruitore.getNome())){
						old = oldAccordo.removeFruitore(i);
						break;
					}
				}
			}			
			
			// --- check esistenza ---
			if(old==null){
				detail.setState(ArchiveStatoImport.DELETED_NOT_EXISTS);
				return;
			}
			
						
			// ---- visibilita' oggetto che si vuole eliminare ----
			
			// Devo vedere che l'oggetto che voglio eliminare sia visibile dall'utente.
			// Il Fruitore è visibile se è visibile l'accordo di servizio parte specifica
			
			// soggetto 
			Soggetto soggetto = this.importerEngine.getSoggettoRegistro(idSoggettoFruitore);
			if(this.importerEngine.isVisioneOggettiGlobale(this.userLogin)==false){
				if(this.userLogin.equals(soggetto.getSuperUser())==false){
					throw new Exception("Il soggetto fruitore ["+idSoggettoFruitore+"] non è visibile/eliminabile dall'utente collegato ("+this.userLogin+")");
				}
			}
			// accordo di servizio parte specifica
			if(this.importerEngine.isVisioneOggettiGlobale(this.userLogin)==false){
				if(this.userLogin.equals(oldAccordo.getSuperUser())==false){
					throw new Exception("L'accordo di servizio parte specifica ["+idAccordoServizioParteSpecifica+"] non è visibile/eliminabile dall'utente collegato ("+this.userLogin+")");
				}
			}
			

			// --- delete ---
			
			// gestione serviziApplicativiAutorizzati
			if(archiveFruitore.getServiziApplicativiAutorizzati()!=null && archiveFruitore.getServiziApplicativiAutorizzati().size()>0){
				List<IDServizioApplicativo> listaAttuale = null;
				try{
					listaAttuale = this.importerEngine.getAllIdServiziApplicativiAutorizzati(archiveFruitore.getIdAccordoServizioParteSpecifica(), archiveFruitore.getIdSoggettoFruitore());
				}catch(DriverRegistroServiziNotFound notFound){}
				if(listaAttuale==null){
					listaAttuale = new ArrayList<IDServizioApplicativo>();
				}
				for (String nomeServizioApplicativo : archiveFruitore.getServiziApplicativiAutorizzati()) {
					boolean found = false;
					for (IDServizioApplicativo idServizioApplicativo : listaAttuale) {
						if(idServizioApplicativo.getNome().equals(nomeServizioApplicativo)){
							found = true;
							break;
						}
					}
					if(found){
						this.importerEngine.deleteServizioApplicativoAutorizzato(archiveFruitore.getIdAccordoServizioParteSpecifica(), archiveFruitore.getIdSoggettoFruitore(), nomeServizioApplicativo);
					}
				}
			}
			
			// prima ho rimosso il fruitore se gia' esisteva.
			// update
			oldAccordo.getServizio().setOldTipoForUpdate(oldAccordo.getServizio().getTipo());
			oldAccordo.getServizio().setOldNomeForUpdate(oldAccordo.getServizio().getNome());
			oldAccordo.getServizio().setOldTipoSoggettoErogatoreForUpdate(oldAccordo.getServizio().getTipoSoggettoErogatore());
			oldAccordo.getServizio().setOldNomeSoggettoErogatoreForUpdate(oldAccordo.getServizio().getNomeSoggettoErogatore());
			this.importerEngine.updateAccordoServizioParteSpecifica(oldAccordo);
			
			detail.setState(ArchiveStatoImport.DELETED);
					
		}			
		catch(Exception e){
			this.log.error("Errore durante l'eliminazione del fruitore["+idSoggettoFruitore+"] dell'accordo di servizio parte specifica ["+idAccordoServizioParteSpecifica+"]: "+e.getMessage(),e);
			detail.setState(ArchiveStatoImport.ERROR);
			detail.setException(e);
		}
	}
	
	
	
	
	public void deletePortaDelegata(ArchivePortaDelegata archivePortaDelegata,
			ArchiveEsitoImportDetail detail){
		
		IDPortaDelegata idPortaDelegata = archivePortaDelegata.getIdPortaDelegata();
		IDSoggetto idSoggettoProprietario = archivePortaDelegata.getIdSoggettoProprietario();
		try{
			// --- check esistenza ---
			if(this.importerEngine.existsPortaDelegata(idPortaDelegata)==false){
				detail.setState(ArchiveStatoImport.DELETED_NOT_EXISTS);
				return;
			}
			
			
			// ---- visibilita' oggetto che si vuole eliminare ----
			
			// Devo vedere che l'oggetto che voglio eliminare sia visibile dall'utente.
			// La PD è visibile se è visibile il soggetto proprietario
			
			org.openspcoop2.core.config.Soggetto soggetto = this.importerEngine.getSoggettoConfigurazione(idSoggettoProprietario);
			if(this.importerEngine.isVisioneOggettiGlobale(this.userLogin)==false){
				if(this.userLogin.equals(soggetto.getSuperUser())==false){
					throw new Exception("Il soggetto proprietario ["+idSoggettoProprietario+"] non è visibile/eliminabile dall'utente collegato ("+this.userLogin+")");
				}
			}
			
			
			// --- delete ---
			PortaDelegata portaDelegata = archivePortaDelegata.getPortaDelegata();
			this.importerEngine.deletePortaDelegata(portaDelegata);
			detail.setState(ArchiveStatoImport.DELETED);
						
		}			
		catch(Exception e){
			this.log.error("Errore durante l'eliminazione della porta delegata ["+idPortaDelegata+"]: "+e.getMessage(),e);
			detail.setState(ArchiveStatoImport.ERROR);
			detail.setException(e);
		}
	}
	
	
	
	
	
	
	public void deletePortaApplicativa(ArchivePortaApplicativa archivePortaApplicativa,
			ArchiveEsitoImportDetail detail){
		
		IDPortaApplicativa idPortaApplicativa = archivePortaApplicativa.getIdPortaApplicativa();
		IDPortaApplicativaByNome idPortaApplicativaByNome = archivePortaApplicativa.getIdPortaApplicativaByNome();
		IDSoggetto idSoggettoProprietario = archivePortaApplicativa.getIdSoggettoProprietario();
		try{
			// --- check esistenza ---
			if(this.importerEngine.existsPortaApplicativa(idPortaApplicativaByNome.getNome(),idSoggettoProprietario)==false){
				detail.setState(ArchiveStatoImport.DELETED_NOT_EXISTS);
				return;
			}
			
					
			// ---- visibilita' oggetto che si vuole eliminare ---
			
			// Devo vedere che l'oggetto che voglio eliminare sia visibile dall'utente.
			// La PA è visibile se è visibile il soggetto proprietario
			
			org.openspcoop2.core.config.Soggetto soggetto = this.importerEngine.getSoggettoConfigurazione(idSoggettoProprietario);
			if(this.importerEngine.isVisioneOggettiGlobale(this.userLogin)==false){
				if(this.userLogin.equals(soggetto.getSuperUser())==false){
					throw new Exception("Il soggetto proprietario ["+idSoggettoProprietario+"] non è visibile/eliminabile dall'utente collegato ("+this.userLogin+")");
				}
			}

			
			// --- delete ---
			PortaApplicativa pa = archivePortaApplicativa.getPortaApplicativa();
			this.importerEngine.deletePortaApplicativa(pa);
			detail.setState(ArchiveStatoImport.DELETED);
			
		}			
		catch(Exception e){
			this.log.error("Errore durante l'eliminazione della porta applicativa ["+idPortaApplicativa+"]: "+e.getMessage(),e);
			detail.setState(ArchiveStatoImport.ERROR);
			detail.setException(e);
		}
	}
	
	
	
	public void importConfigurazione(Configurazione configurazionePdD, ArchiveEsitoImportDetailConfigurazione detail){		
		try{
			// update
			this.importerEngine.updateConfigurazione(configurazionePdD);
			detail.setState(ArchiveStatoImport.UPDATED);
		}			
		catch(Exception e){
			this.log.error("Errore durante l'import della configurazione: "+e.getMessage(),e);
			detail.setState(ArchiveStatoImport.ERROR);
			detail.setException(e);
		}
	}
}
