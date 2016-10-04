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
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import org.slf4j.Logger;
import org.openspcoop2.core.config.Configurazione;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDAccordoCooperazione;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDPortaApplicativaByNome;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.AccordoCooperazione;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteComuneServizioCompostoServizioComponente;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Fruitore;
import org.openspcoop2.core.registry.Operation;
import org.openspcoop2.core.registry.PortType;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.core.registry.constants.ProprietariDocumento;
import org.openspcoop2.core.registry.constants.RuoliDocumento;
import org.openspcoop2.core.registry.constants.StatiAccordo;
import org.openspcoop2.core.registry.constants.TipologiaServizio;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziNotFound;
import org.openspcoop2.core.registry.driver.FiltroRicerca;
import org.openspcoop2.core.registry.driver.IDAccordoCooperazioneFactory;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.core.registry.driver.ValidazioneStatoPackageException;
import org.openspcoop2.core.registry.wsdl.RegistroOpenSPCoopUtilities;
import org.openspcoop2.message.XMLUtils;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.archive.Archive;
import org.openspcoop2.protocol.sdk.archive.ArchiveAccordoCooperazione;
import org.openspcoop2.protocol.sdk.archive.ArchiveAccordoServizioComposto;
import org.openspcoop2.protocol.sdk.archive.ArchiveAccordoServizioParteComune;
import org.openspcoop2.protocol.sdk.archive.ArchiveAccordoServizioParteSpecifica;
import org.openspcoop2.protocol.sdk.archive.ArchiveEsitoImport;
import org.openspcoop2.protocol.sdk.archive.ArchiveEsitoImportDetail;
import org.openspcoop2.protocol.sdk.archive.ArchiveEsitoImportDetailConfigurazione;
import org.openspcoop2.protocol.sdk.archive.ArchiveFruitore;
import org.openspcoop2.protocol.sdk.archive.ArchivePdd;
import org.openspcoop2.protocol.sdk.archive.ArchivePortaApplicativa;
import org.openspcoop2.protocol.sdk.archive.ArchivePortaDelegata;
import org.openspcoop2.protocol.sdk.archive.ArchiveServizioApplicativo;
import org.openspcoop2.protocol.sdk.archive.ArchiveSoggetto;
import org.openspcoop2.protocol.sdk.config.ITraduttore;
import org.openspcoop2.protocol.sdk.constants.ArchiveStatoImport;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.wsdl.DefinitionWrapper;

/**
 *  ImporterArchiveUtils
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ImporterArchiveUtils {

	private AbstractArchiveEngine importerEngine;
	private Logger log;
	private String userLogin;
	private boolean gestioneWorkflowStatiAccordi;
	private boolean updateAbilitato;
	private String nomePddOperativa;
	private String tipoPddDefault;
	private ProtocolFactoryManager protocolFactoryManager;
	private IDAccordoCooperazioneFactory idAccordoCooperazioneFactory;
	private IDAccordoFactory idAccordoFactory;
	
	public ImporterArchiveUtils(AbstractArchiveEngine importerEngine,Logger log,
			String userLogin,String nomePddOperativa, String tipoPddDefault,
			boolean gestioneWorkflowStatiAccordi,
			boolean updateAbilitato) throws Exception{
		this.importerEngine = importerEngine;
		this.log = log;
		this.userLogin = userLogin;
		this.gestioneWorkflowStatiAccordi = gestioneWorkflowStatiAccordi;
		this.updateAbilitato = updateAbilitato;
		if(nomePddOperativa!=null){
			this.nomePddOperativa = nomePddOperativa;
		}
		else{
			FiltroRicerca filtroRicerca = new FiltroRicerca();
			filtroRicerca.setTipo("operativo");
			List<String> pdd = this.importerEngine.getAllIdPorteDominio(filtroRicerca);
			if(pdd.size()<=0){
				throw new Exception("Pdd operative non trovate nel registro");
			}
			if(pdd.size()>1){
				throw new Exception("Riscontrate piu' di una porta di dominio operativa");
			}
			this.nomePddOperativa = pdd.get(0);
		}
		this.tipoPddDefault = tipoPddDefault;
		if(this.tipoPddDefault==null){
			this.tipoPddDefault = "esterno";
		}
		this.protocolFactoryManager = ProtocolFactoryManager.getInstance();
		this.idAccordoCooperazioneFactory = IDAccordoCooperazioneFactory.getInstance();
		this.idAccordoFactory = IDAccordoFactory.getInstance();
	}
	
	

	
	
	public ArchiveEsitoImport importArchive(Archive archive, String userLogin,
			boolean utilizzoAzioniDiretteInAccordoAbilitato,
			boolean isAbilitatoControlloUnicitaImplementazioneAccordoPerSoggetto,
			boolean isAbilitatoControlloUnicitaImplementazionePortTypePerSoggetto) throws Exception,ImportInformationMissingException{
		try{
			
			ArchiveEsitoImport esito = new ArchiveEsitoImport();
			
			
			// Pdd
			for (int i = 0; i < archive.getPdd().size(); i++) {
				ArchivePdd archivePdd = archive.getPdd().get(i);
				ArchiveEsitoImportDetail detail = new ArchiveEsitoImportDetail(archivePdd);
				try{
					this.importPdd(archivePdd, detail);
				}catch(Exception e){
					detail.setState(ArchiveStatoImport.ERROR);
					detail.setException(e);
				}
				esito.getPdd().add(detail);
			}
			
			
			
			// Soggetti
			for (int i = 0; i < archive.getSoggetti().size(); i++) {
				ArchiveSoggetto archiveSoggetto = archive.getSoggetti().get(i);
				ArchiveEsitoImportDetail detail = new ArchiveEsitoImportDetail(archiveSoggetto);
				try{
					this.importSoggetto(archiveSoggetto, detail);
				}catch(Exception e){
					detail.setState(ArchiveStatoImport.ERROR);
					detail.setException(e);
				}
				esito.getSoggetti().add(detail);
			}
			

			// Servizi Applicativi
			for (int i = 0; i < archive.getServiziApplicativi().size(); i++) {
				ArchiveServizioApplicativo archiveServizioApplicativo = archive.getServiziApplicativi().get(i);
				ArchiveEsitoImportDetail detail = new ArchiveEsitoImportDetail(archiveServizioApplicativo);
				try{
					archiveServizioApplicativo.update();
					this.importServizioApplicativo(archiveServizioApplicativo, detail);
				}catch(Exception e){
					detail.setState(ArchiveStatoImport.ERROR);
					detail.setException(e);
				}
				esito.getServiziApplicativi().add(detail);
			}
			
			
			// Accordi di Cooperazione
			for (int i = 0; i < archive.getAccordiCooperazione().size(); i++) {
				ArchiveAccordoCooperazione archiveAccordoCooperazione = archive.getAccordiCooperazione().get(i);
				ArchiveEsitoImportDetail detail = new ArchiveEsitoImportDetail(archiveAccordoCooperazione);
				try{
					archiveAccordoCooperazione.update();
					this.importAccordoCooperazione(archiveAccordoCooperazione, detail);
				}catch(Exception e){
					detail.setState(ArchiveStatoImport.ERROR);
					detail.setException(e);
				}
				esito.getAccordiCooperazione().add(detail);
			}
			
			
			// Accordi di Servizio Parte Comune
			for (int i = 0; i < archive.getAccordiServizioParteComune().size(); i++) {
				ArchiveAccordoServizioParteComune archiveAccordoServizioParteComune = archive.getAccordiServizioParteComune().get(i);
				ArchiveEsitoImportDetail detail = new ArchiveEsitoImportDetail(archiveAccordoServizioParteComune);
				try{
					archiveAccordoServizioParteComune.update();
					this.importAccordoServizioParteComune(archiveAccordoServizioParteComune, utilizzoAzioniDiretteInAccordoAbilitato, detail);
				}catch(Exception e){
					detail.setState(ArchiveStatoImport.ERROR);
					detail.setException(e);
				}
				esito.getAccordiServizioParteComune().add(detail);
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
			
			
			// Accordi di Servizio Parte Specifica (implementano accordi di servizio parte comune)
			for (int i = 0; i < listAccordiServizioParteSpecifica.size(); i++) {
				ArchiveAccordoServizioParteSpecifica archiveAccordoServizioParteSpecifica = listAccordiServizioParteSpecifica.get(i);
				ArchiveEsitoImportDetail detail = new ArchiveEsitoImportDetail(archiveAccordoServizioParteSpecifica);
				try{
					//archiveAccordoServizioParteSpecifica.update(); eseguito durante la preparazione della lista listAccordiServizioParteSpecifica
					this.importAccordoServizioParteSpecifica(archiveAccordoServizioParteSpecifica, false, 
							isAbilitatoControlloUnicitaImplementazioneAccordoPerSoggetto,
							isAbilitatoControlloUnicitaImplementazionePortTypePerSoggetto,
							detail);
				}catch(Exception e){
					detail.setState(ArchiveStatoImport.ERROR);
					detail.setException(e);
				}
				esito.getAccordiServizioParteSpecifica().add(detail);
			}
			
			
			// Accordi di Servizio Composto
			for (int i = 0; i < archive.getAccordiServizioComposto().size(); i++) {
				ArchiveAccordoServizioComposto archiveAccordoServizioComposto = archive.getAccordiServizioComposto().get(i);
				ArchiveEsitoImportDetail detail = new ArchiveEsitoImportDetail(archiveAccordoServizioComposto);
				try{
					archiveAccordoServizioComposto.update();
					this.importAccordoServizioComposto(archiveAccordoServizioComposto, utilizzoAzioniDiretteInAccordoAbilitato, detail);
				}catch(Exception e){
					detail.setState(ArchiveStatoImport.ERROR);
					detail.setException(e);
				}
				esito.getAccordiServizioComposto().add(detail);
			}

			
			// Accordi di Servizio Parte Specifica (implementano accordi di servizio composto)
			for (int i = 0; i < listAccordiServizioParteSpecifica_serviziComposti.size(); i++) {
				ArchiveAccordoServizioParteSpecifica archiveAccordoServizioParteSpecifica = listAccordiServizioParteSpecifica_serviziComposti.get(i);
				ArchiveEsitoImportDetail detail = new ArchiveEsitoImportDetail(archiveAccordoServizioParteSpecifica);
				try{
					//archiveAccordoServizioParteSpecifica.update(); eseguito durante la preparazione della lista listAccordiServizioParteSpecifica_serviziComposti
					this.importAccordoServizioParteSpecifica(archiveAccordoServizioParteSpecifica, true, 
							isAbilitatoControlloUnicitaImplementazioneAccordoPerSoggetto,
							isAbilitatoControlloUnicitaImplementazionePortTypePerSoggetto,
							detail);
				}catch(Exception e){
					detail.setState(ArchiveStatoImport.ERROR);
					detail.setException(e);
				}
				esito.getAccordiServizioParteSpecificaServiziComposti().add(detail);
			}
			
			
			// Fruitori
			for (int i = 0; i < archive.getAccordiFruitori().size(); i++) {
				ArchiveFruitore archiveFruitore = archive.getAccordiFruitori().get(i);
				ArchiveEsitoImportDetail detail = new ArchiveEsitoImportDetail(archiveFruitore);
				try{
					archiveFruitore.update();
					this.importFruitore(archiveFruitore, detail);
				}catch(Exception e){
					detail.setState(ArchiveStatoImport.ERROR);
					detail.setException(e);
				}
				esito.getAccordiFruitori().add(detail);
			}
			
			
			// PorteDelegate
			for (int i = 0; i < archive.getPorteDelegate().size(); i++) {
				ArchivePortaDelegata archivePortaDelegata = archive.getPorteDelegate().get(i);
				ArchiveEsitoImportDetail detail = new ArchiveEsitoImportDetail(archivePortaDelegata);
				try{
					archivePortaDelegata.update();
					this.importPortaDelegata(archivePortaDelegata, detail);
				}catch(Exception e){
					detail.setState(ArchiveStatoImport.ERROR);
					detail.setException(e);
				}
				esito.getPorteDelegate().add(detail);
			}
			
			
			// PorteApplicative
			for (int i = 0; i < archive.getPorteApplicative().size(); i++) {
				ArchivePortaApplicativa archivePortaApplicativa = archive.getPorteApplicative().get(i);
				ArchiveEsitoImportDetail detail = new ArchiveEsitoImportDetail(archivePortaApplicativa);
				try{
					archivePortaApplicativa.update();
					this.importPortaApplicativa(archivePortaApplicativa, detail);
				}catch(Exception e){
					detail.setState(ArchiveStatoImport.ERROR);
					detail.setException(e);
				}
				esito.getPorteApplicative().add(detail);
			}
			
			
			
			// Configurazione
			if(archive.getConfigurazionePdD()!=null){
				Configurazione configurazione = archive.getConfigurazionePdD();
				ArchiveEsitoImportDetailConfigurazione detail = new ArchiveEsitoImportDetailConfigurazione(configurazione);
				try{
					this.importConfigurazione(configurazione, detail);
				}catch(Exception e){
					detail.setState(ArchiveStatoImport.ERROR);
					detail.setException(e);
				}
				esito.setConfigurazionePdD(detail);
			}
			
			
			return esito;
			
		}catch(Exception e){
			throw e;
		}
	}
	
	
	
	
	public void importPdd(ArchivePdd archivePdd,ArchiveEsitoImportDetail detail){
		
		String nomePdd = archivePdd.getNomePdd();
		try{
			
			// --- check esistenza ---
			if(this.updateAbilitato==false){
				if(this.importerEngine.existsPortaDominio(nomePdd)){
					detail.setState(ArchiveStatoImport.UPDATE_NOT_PERMISSED);
					return;
				}
			}
			
				
			// --- check elementi riferiti ---
			// non esistenti
			
			
			// --- compatibilita' elementi riferiti ---
			// non esistenti
			
			
			// ---- visibilita' oggetto riferiti ---
			// non esistenti
			
			
			// --- set dati obbligatori nel db ----
			
			archivePdd.getPortaDominio().setSuperUser(this.userLogin);
			
			org.openspcoop2.core.registry.driver.utils.XMLDataConverter.
				impostaInformazioniRegistroDB_PortaDominio(archivePdd.getPortaDominio());
			
			
			// --- ora registrazione
			archivePdd.getPortaDominio().setOraRegistrazione(DateManager.getDate());
			
			
			// --- upload ---
			boolean create = false;
			if(this.importerEngine.existsPortaDominio(nomePdd)){
				
				org.openspcoop2.core.registry.PortaDominio old = this.importerEngine.getPortaDominio(nomePdd);
				archivePdd.getPortaDominio().setId(old.getId());
				
				// visibilita' oggetto stesso per update
				if(this.importerEngine.isVisioneOggettiGlobale(this.userLogin)==false){
					if(this.userLogin.equals(old.getSuperUser())==false){
						throw new Exception("La porta di dominio non è visibile/aggiornabile dall'utente collegato ("+this.userLogin+")");
					}
				}

				// update
				this.importerEngine.updatePortaDominio(archivePdd.getPortaDominio());
				create = false;
			}
			// --- create ---
			else{
				this.importerEngine.createPortaDominio(archivePdd.getPortaDominio());
				create = true;
			}
				

			// --- tipoPdd ---
			org.openspcoop2.core.registry.driver.utils.XMLDataConverter.
				impostaInformazioniRegistroDB_PortaDominio_update(archivePdd.getPortaDominio(), this.nomePddOperativa, 
					this.log, this.importerEngine.getDriverRegistroServizi(), this.tipoPddDefault);
			
			
			// --- info ---
			if(create){
				detail.setState(ArchiveStatoImport.CREATED);
			}else{
				detail.setState(ArchiveStatoImport.UPDATED);
			}
		}			
		catch(Exception e){
			this.log.error("Errore durante l'import della porta di dominio ["+nomePdd+"]: "+e.getMessage(),e);
			detail.setState(ArchiveStatoImport.ERROR);
			detail.setException(e);
		}
	}
	
	
	
	public void importSoggetto(ArchiveSoggetto archiveSoggetto,ArchiveEsitoImportDetail detail){
		
		IDSoggetto idSoggetto = archiveSoggetto.getIdSoggetto();
		try{
			
			boolean create = false;
			
			if(archiveSoggetto.getSoggettoRegistro()!=null){
								
				
				// --- check esistenza ---
				if(this.updateAbilitato==false){
					if(this.importerEngine.existsSoggettoRegistro(idSoggetto)){
						detail.setState(ArchiveStatoImport.UPDATE_NOT_PERMISSED);
						return;
					}
				}
				
				
				// --- check elementi riferiti ---
				if(archiveSoggetto.getSoggettoRegistro().getPortaDominio()!=null){
					if(this.importerEngine.existsPortaDominio(archiveSoggetto.getSoggettoRegistro().getPortaDominio()) == false ){
						throw new Exception("Porta di dominio ["+archiveSoggetto.getSoggettoRegistro().getPortaDominio()+"] associata non esiste");
					}
				}
				
				
				// --- compatibilita' elementi riferiti ---
				// Non ce ne sono da controllare
				
				
				// ---- visibilita' oggetto riferiti ---
				// Non ce ne sono da controllare
				
				
				// --- set dati obbligatori nel db ----
				org.openspcoop2.core.registry.driver.utils.XMLDataConverter.
					impostaInformazioniRegistroDB_Soggetto(archiveSoggetto.getSoggettoRegistro(),this.nomePddOperativa);
				
				archiveSoggetto.getSoggettoRegistro().setSuperUser(this.userLogin);			
				
				if(archiveSoggetto.getSoggettoRegistro().getCodiceIpa()==null ||
						archiveSoggetto.getSoggettoRegistro().getIdentificativoPorta()==null){
					
					IProtocolFactory protocolFactory = this.protocolFactoryManager.getProtocolFactoryBySubjectType(idSoggetto.getTipo());
					ITraduttore traduttore = protocolFactory.createTraduttore();
					
					if(archiveSoggetto.getSoggettoRegistro().getCodiceIpa()==null){
						archiveSoggetto.getSoggettoRegistro().setCodiceIpa(traduttore.getIdentificativoCodiceIPADefault(idSoggetto, false));
					}
					
					if(archiveSoggetto.getSoggettoRegistro().getIdentificativoPorta()==null){
						archiveSoggetto.getSoggettoRegistro().setIdentificativoPorta(traduttore.getIdentificativoPortaDefault(idSoggetto));
					}
				}
				
				
				
				// --- ora registrazione
				archiveSoggetto.getSoggettoRegistro().setOraRegistrazione(DateManager.getDate());
				
				
				
				// --- upload ---
				if(this.importerEngine.existsSoggettoRegistro(idSoggetto)){
					
					org.openspcoop2.core.registry.Soggetto old = this.importerEngine.getSoggettoRegistro(idSoggetto);
					archiveSoggetto.getSoggettoRegistro().setId(old.getId());
					archiveSoggetto.getSoggettoRegistro().setOldTipoForUpdate(old.getTipo());
					archiveSoggetto.getSoggettoRegistro().setOldNomeForUpdate(old.getNome());
					org.openspcoop2.core.registry.driver.utils.XMLDataConverter.
						impostaInformazioniRegistroDB_Soggetto_update(archiveSoggetto.getSoggettoRegistro(), old);
					
					// visibilita' oggetto stesso per update
					if(this.importerEngine.isVisioneOggettiGlobale(this.userLogin)==false){
						if(this.userLogin.equals(old.getSuperUser())==false){
							throw new Exception("Il soggetto non è visibile/aggiornabile dall'utente collegato ("+this.userLogin+")");
						}
					}

					// update
					this.importerEngine.updateSoggettoRegistro(archiveSoggetto.getSoggettoRegistro());
					
				}
				// --- create ---
				else{
					this.importerEngine.createSoggettoRegistro(archiveSoggetto.getSoggettoRegistro());
					create = true;
				}
				
			}
			
			if(archiveSoggetto.getSoggettoConfigurazione()!=null){
			
				// --- check esistenza ---
				if(this.updateAbilitato==false){
					if(this.importerEngine.existsSoggettoConfigurazione(idSoggetto) && !create){
						detail.setState(ArchiveStatoImport.UPDATE_NOT_PERMISSED);
						return;
					}
				}
				
				
				// --- check elementi riferiti ---
				// Non ce ne sono da controllare
				
				
				// --- compatibilita' elementi riferiti ---
				// Non ce ne sono da controllare
				
				
				// --- visibilita' oggetto riferiti ---
				// Non ce ne sono da controllare
				
				
				// --- set dati obbligatori nel db ---
				
				archiveSoggetto.getSoggettoConfigurazione().setSuperUser(this.userLogin);
				
				if(archiveSoggetto.getSoggettoConfigurazione().getIdentificativoPorta()==null){
					
					IProtocolFactory protocolFactory = this.protocolFactoryManager.getProtocolFactoryBySubjectType(idSoggetto.getTipo());
					ITraduttore traduttore = protocolFactory.createTraduttore();
					
					if(archiveSoggetto.getSoggettoConfigurazione().getIdentificativoPorta()==null){
						archiveSoggetto.getSoggettoConfigurazione().setIdentificativoPorta(traduttore.getIdentificativoPortaDefault(idSoggetto));
					}
				}
				
				
				
				// --- ora registrazione
				archiveSoggetto.getSoggettoConfigurazione().setOraRegistrazione(DateManager.getDate());
				
				
				
				// --- upload ---
				if(this.importerEngine.existsSoggettoConfigurazione(idSoggetto)){
					
					org.openspcoop2.core.config.Soggetto old = this.importerEngine.getSoggettoConfigurazione(idSoggetto);
					archiveSoggetto.getSoggettoConfigurazione().setId(old.getId());
					archiveSoggetto.getSoggettoRegistro().setOldTipoForUpdate(old.getTipo());
					archiveSoggetto.getSoggettoRegistro().setOldNomeForUpdate(old.getNome());
					
					// visibilita' oggetto stesso per update
					if(this.importerEngine.isVisioneOggettiGlobale(this.userLogin)==false){
						if(this.userLogin.equals(old.getSuperUser())==false){
							throw new Exception("Il soggetto non è visibile/aggiornabile dall'utente collegato ("+this.userLogin+")");
						}
					}

					// update
					this.importerEngine.updateSoggettoConfigurazione(archiveSoggetto.getSoggettoConfigurazione());
					
				}
				// --- create ---
				else{
					this.importerEngine.createSoggettoConfigurazione(archiveSoggetto.getSoggettoConfigurazione());
					create = true;
				}
				
			}
			
			
			// --- upload ---
			if(create){
				detail.setState(ArchiveStatoImport.CREATED);
			}else{
				detail.setState(ArchiveStatoImport.UPDATED);
			}
			
		}			
		catch(Exception e){
			this.log.error("Errore durante l'import del soggetto ["+idSoggetto+"]: "+e.getMessage(),e);
			detail.setState(ArchiveStatoImport.ERROR);
			detail.setException(e);
		}
	}
	
	
	
	
	public void importServizioApplicativo(ArchiveServizioApplicativo archiveServizioApplicativo,
			ArchiveEsitoImportDetail detail){
		
		IDServizioApplicativo idServizioApplicativo = archiveServizioApplicativo.getIdServizioApplicativo();
		IDSoggetto idSoggettoProprietario = archiveServizioApplicativo.getIdSoggettoProprietario();
		try{
			
			// --- check esistenza ---
			if(this.updateAbilitato==false){
				if(this.importerEngine.existsServizioApplicativo(idServizioApplicativo)){
					detail.setState(ArchiveStatoImport.UPDATE_NOT_PERMISSED);
					return;
				}
			}
			
				
			// --- check elementi riferiti ---
			if(this.importerEngine.existsSoggettoConfigurazione(idSoggettoProprietario) == false ){
				throw new Exception("Soggetto proprietario ["+idSoggettoProprietario+"] non esistente");
			}
			
			
			// --- compatibilita' elementi riferiti ---
			// non ce ne sono da controllare
			
			
			// ---- visibilita' oggetto riferiti ---
			org.openspcoop2.core.config.Soggetto soggetto = this.importerEngine.getSoggettoConfigurazione(idSoggettoProprietario);
			if(this.importerEngine.isVisioneOggettiGlobale(this.userLogin)==false){
				if(this.userLogin.equals(soggetto.getSuperUser())==false){
					throw new Exception("Il soggetto proprietario ["+idSoggettoProprietario+"] non è visibile/utilizzabile dall'utente collegato ("+this.userLogin+")");
				}
			}
			
			
			// --- set dati obbligatori nel db ----
			
			// archiveServizioApplicativo.getServizioApplicativo().setSuperUser(this.userLogin);
			// L'oggetto non contiene informazione di visibilita, la eredita dal soggetto
			
			org.openspcoop2.core.config.driver.utils.XMLDataConverter.
				impostaInformazioniConfigurazione_ServizioApplicativo(archiveServizioApplicativo.getServizioApplicativo(), soggetto, "", 
						this.log, CostantiConfigurazione.CONFIGURAZIONE_DB);
			
			
			
			// --- ora registrazione
			archiveServizioApplicativo.getServizioApplicativo().setOraRegistrazione(DateManager.getDate());
			
			
			
			// --- upload ---
			if(this.importerEngine.existsServizioApplicativo(idServizioApplicativo)){
				
				org.openspcoop2.core.config.ServizioApplicativo old = this.importerEngine.getServizioApplicativo(idServizioApplicativo);
				archiveServizioApplicativo.getServizioApplicativo().setId(old.getId());
				archiveServizioApplicativo.getServizioApplicativo().setOldNomeForUpdate(old.getNome());
				archiveServizioApplicativo.getServizioApplicativo().setOldTipoSoggettoProprietarioForUpdate(old.getTipoSoggettoProprietario());
				archiveServizioApplicativo.getServizioApplicativo().setOldNomeSoggettoProprietarioForUpdate(old.getNomeSoggettoProprietario());
				org.openspcoop2.core.config.driver.utils.XMLDataConverter.
					impostaInformazioniConfigurazione_ServizioApplicativo_update(archiveServizioApplicativo.getServizioApplicativo(), old);
				
				// visibilita' oggetto stesso per update
				// L'oggetto non contiene informazione di visibilita, la eredita dal soggetto

				// update
				this.importerEngine.updateServizioApplicativo(archiveServizioApplicativo.getServizioApplicativo());
				detail.setState(ArchiveStatoImport.UPDATED);
			}
			// --- create ---
			else{
				this.importerEngine.createServizioApplicativo(archiveServizioApplicativo.getServizioApplicativo());
				detail.setState(ArchiveStatoImport.CREATED);
			}
				

			
		}			
		catch(Exception e){
			this.log.error("Errore durante l'import del servizio applicativo ["+idServizioApplicativo+"]: "+e.getMessage(),e);
			detail.setState(ArchiveStatoImport.ERROR);
			detail.setException(e);
		}
	}
	
	
	
	

	public void importAccordoCooperazione(ArchiveAccordoCooperazione archiveAccordoCooperazione,
			ArchiveEsitoImportDetail detail){
		
		IDAccordoCooperazione idAccordoCooperazione = archiveAccordoCooperazione.getIdAccordoCooperazione();
		IDSoggetto idSoggettoReferente = archiveAccordoCooperazione.getIdSoggettoReferente();
		List<IDSoggetto> idSoggettiPartecipanti = archiveAccordoCooperazione.getIdSoggettiPartecipanti();
		try{
			// --- check esistenza ---
			if(this.updateAbilitato==false){
				if(this.importerEngine.existsAccordoCooperazione(idAccordoCooperazione)){
					detail.setState(ArchiveStatoImport.UPDATE_NOT_PERMISSED);
					return;
				}
			}
			
				
			// --- check elementi riferiti ---
			if(this.importerEngine.existsSoggettoRegistro(idSoggettoReferente) == false ){
				throw new Exception("Soggetto proprietario ["+idSoggettoReferente+"] non esistente");
			}
			if(idSoggettiPartecipanti!=null){
				for (IDSoggetto idSoggettoPartecipante : idSoggettiPartecipanti) {
					if(this.importerEngine.existsSoggettoRegistro(idSoggettoPartecipante) == false ){
						throw new Exception("Soggetto partecipante ["+idSoggettoPartecipante+"] non esistente");
					}
				}
			}
			
			
			// --- compatibilita' elementi riferiti ---
			String protocolloAssociatoAccordo = this.protocolFactoryManager.getProtocolBySubjectType(idSoggettoReferente.getTipo());
			// soggetti partecipanti
			if(idSoggettiPartecipanti!=null){
				for (IDSoggetto idSoggettoPartecipante : idSoggettiPartecipanti) {
					String protocolloAssociatoSoggettoPartecipante = this.protocolFactoryManager.getProtocolBySubjectType(idSoggettoPartecipante.getTipo());
					if(protocolloAssociatoAccordo.equals(protocolloAssociatoSoggettoPartecipante)==false){
						throw new Exception("Soggetto partecipante ["+idSoggettoPartecipante+"] (protocollo:"+protocolloAssociatoSoggettoPartecipante+
								") non utilizzabile in un accordo con soggetto referente ["+idSoggettoReferente+"] (protocollo:"+protocolloAssociatoAccordo+")");
					}
				}
			}
			

			// ---- visibilita' oggetto riferiti ---
			org.openspcoop2.core.registry.Soggetto soggetto = this.importerEngine.getSoggettoRegistro(idSoggettoReferente);
			if(this.importerEngine.isVisioneOggettiGlobale(this.userLogin)==false){
				if(this.userLogin.equals(soggetto.getSuperUser())==false){
					throw new Exception("Il soggetto referente ["+idSoggettoReferente+"] non è visibile/utilizzabile dall'utente collegato ("+this.userLogin+")");
				}
			}
			if(idSoggettiPartecipanti!=null){
				for (IDSoggetto idSoggettoPartecipante : idSoggettiPartecipanti) {
					org.openspcoop2.core.registry.Soggetto soggettoPartecipante = this.importerEngine.getSoggettoRegistro(idSoggettoPartecipante);
					if(this.importerEngine.isVisioneOggettiGlobale(this.userLogin)==false){
						if(this.userLogin.equals(soggettoPartecipante.getSuperUser())==false){
							throw new Exception("Il soggetto partecipante ["+idSoggettoPartecipante+"] non è visibile/utilizzabile dall'utente collegato ("+this.userLogin+")");
						}
					}
				}
			}
			
			
			// --- set dati obbligatori nel db ----
			
			archiveAccordoCooperazione.getAccordoCooperazione().setSuperUser(this.userLogin);
			
			for(int i=0; i<archiveAccordoCooperazione.getAccordoCooperazione().sizeAllegatoList();i++){
				archiveAccordoCooperazione.getAccordoCooperazione().getAllegato(i).setRuolo(RuoliDocumento.allegato.toString());
				archiveAccordoCooperazione.getAccordoCooperazione().getAllegato(i).setTipoProprietarioDocumento(ProprietariDocumento.accordoCooperazione.toString());
			}
			for(int i=0; i<archiveAccordoCooperazione.getAccordoCooperazione().sizeSpecificaSemiformaleList();i++){
				archiveAccordoCooperazione.getAccordoCooperazione().getSpecificaSemiformale(i).setRuolo(RuoliDocumento.specificaSemiformale.toString());
				archiveAccordoCooperazione.getAccordoCooperazione().getSpecificaSemiformale(i).setTipoProprietarioDocumento(ProprietariDocumento.accordoCooperazione.toString());
			}
						
			
			// --- workflowStatoDocumenti ---
			StringBuffer warningInfoStatoFinale = new StringBuffer("");
			if(archiveAccordoCooperazione.getAccordoCooperazione().getStatoPackage()==null){
				if(this.gestioneWorkflowStatiAccordi){
					try{
						archiveAccordoCooperazione.getAccordoCooperazione().setStatoPackage(StatiAccordo.finale.toString());
						this.importerEngine.validaStatoAccordoCooperazione(archiveAccordoCooperazione.getAccordoCooperazione());
					}catch(ValidazioneStatoPackageException validazioneException){
						try{
							archiveAccordoCooperazione.getAccordoCooperazione().setStatoPackage(StatiAccordo.operativo.toString());
							this.importerEngine.validaStatoAccordoCooperazione(archiveAccordoCooperazione.getAccordoCooperazione());
						}catch(ValidazioneStatoPackageException validazioneExceptionLevelOperativo){
							warningInfoStatoFinale.append("\n\t\t(WARNING) Accordo salvato con stato '").append(StatiAccordo.bozza.toString()).append("'\n\t\t\t");
							warningInfoStatoFinale.append(validazioneException.toString("\n\t\t\t - ","\n\t\t\t - "));
							warningInfoStatoFinale.append("\n\t\t\t"+validazioneExceptionLevelOperativo.toString("\n\t\t\t - ","\n\t\t\t - "));
							archiveAccordoCooperazione.getAccordoCooperazione().setStatoPackage(StatiAccordo.bozza.toString());
						}
					}
				}
				else{
					archiveAccordoCooperazione.getAccordoCooperazione().setStatoPackage(StatiAccordo.finale.toString());
				}
			}
			
			
			
			// --- ora registrazione
			archiveAccordoCooperazione.getAccordoCooperazione().setOraRegistrazione(DateManager.getDate());
			
			
			
			// --- upload ---
			if(this.importerEngine.existsAccordoCooperazione(idAccordoCooperazione)){
				
				AccordoCooperazione old = this.importerEngine.getAccordoCooperazione(idAccordoCooperazione);
				archiveAccordoCooperazione.getAccordoCooperazione().setId(old.getId());
				archiveAccordoCooperazione.getAccordoCooperazione().setOldIDAccordoForUpdate(this.idAccordoCooperazioneFactory.getIDAccordoFromAccordo(old));
				
				// visibilita' oggetto stesso per update
				if(this.importerEngine.isVisioneOggettiGlobale(this.userLogin)==false){
					if(this.userLogin.equals(old.getSuperUser())==false){
						throw new Exception("L'accordo non è visibile/aggiornabile dall'utente collegato ("+this.userLogin+")");
					}
				}

				// update
				this.importerEngine.updateAccordoCooperazione(archiveAccordoCooperazione.getAccordoCooperazione());
				detail.setState(ArchiveStatoImport.UPDATED);
				detail.setStateDetail(warningInfoStatoFinale.toString());
			}
			// --- create ---
			else{
				this.importerEngine.createAccordoCooperazione(archiveAccordoCooperazione.getAccordoCooperazione());
				detail.setState(ArchiveStatoImport.CREATED);
				detail.setStateDetail(warningInfoStatoFinale.toString());
			}
				

			
		}			
		catch(Exception e){
			this.log.error("Errore durante l'import dell'accordo di cooperazione ["+idAccordoCooperazione+"]: "+e.getMessage(),e);
			detail.setState(ArchiveStatoImport.ERROR);
			detail.setException(e);
		}
	}
	
	
	
	
	
	
	
	
	
	public void importAccordoServizioParteComune(ArchiveAccordoServizioParteComune archiveAccordoServizioParteComune,
			boolean utilizzoAzioniDiretteInAccordoAbilitato,
			ArchiveEsitoImportDetail detail){
		
		IDAccordo idAccordoServizioParteComune = archiveAccordoServizioParteComune.getIdAccordoServizioParteComune();
		IDSoggetto idSoggettoReferente = archiveAccordoServizioParteComune.getIdSoggettoReferente();
		try{
			// --- check esistenza ---
			if(this.updateAbilitato==false){
				if(this.importerEngine.existsAccordoServizioParteComune(idAccordoServizioParteComune)){
					detail.setState(ArchiveStatoImport.UPDATE_NOT_PERMISSED);
					return;
				}
			}
			
				
			// --- check elementi riferiti ---
			if(this.importerEngine.existsSoggettoRegistro(idSoggettoReferente) == false ){
				throw new Exception("Soggetto proprietario ["+idSoggettoReferente+"] non esistente");
			}
			
			
			// --- compatibilita' elementi riferiti ---
			// non ce ne sono da controllare
			//String protocolloAssociatoAccordo = this.protocolFactoryManager.getProtocolBySubjectType(idSoggettoReferente.getTipo());
						

			// ---- visibilita' oggetto riferiti ---
			org.openspcoop2.core.registry.Soggetto soggetto = this.importerEngine.getSoggettoRegistro(idSoggettoReferente);
			if(this.importerEngine.isVisioneOggettiGlobale(this.userLogin)==false){
				if(this.userLogin.equals(soggetto.getSuperUser())==false){
					throw new Exception("Il soggetto referente ["+idSoggettoReferente+"] non è visibile/utilizzabile dall'utente collegato ("+this.userLogin+")");
				}
			}
			
			
			// --- set dati obbligatori nel db ----

			org.openspcoop2.core.registry.driver.utils.XMLDataConverter.
				impostaInformazioniRegistroDB_AccordoServizioParteComune(archiveAccordoServizioParteComune.getAccordoServizioParteComune());
			
			archiveAccordoServizioParteComune.getAccordoServizioParteComune().setSuperUser(this.userLogin);
			
			for(int i=0; i<archiveAccordoServizioParteComune.getAccordoServizioParteComune().sizeAllegatoList();i++){
				archiveAccordoServizioParteComune.getAccordoServizioParteComune().getAllegato(i).setRuolo(RuoliDocumento.allegato.toString());
				archiveAccordoServizioParteComune.getAccordoServizioParteComune().getAllegato(i).setTipoProprietarioDocumento(ProprietariDocumento.accordoServizio.toString());
			}
			for(int i=0; i<archiveAccordoServizioParteComune.getAccordoServizioParteComune().sizeSpecificaSemiformaleList();i++){
				archiveAccordoServizioParteComune.getAccordoServizioParteComune().getSpecificaSemiformale(i).setRuolo(RuoliDocumento.specificaSemiformale.toString());
				archiveAccordoServizioParteComune.getAccordoServizioParteComune().getSpecificaSemiformale(i).setTipoProprietarioDocumento(ProprietariDocumento.accordoServizio.toString());
			}
					
			
			// --- workflowStatoDocumenti ---
			StringBuffer warningInfoStatoFinale = new StringBuffer("");
			if(archiveAccordoServizioParteComune.getAccordoServizioParteComune().getStatoPackage()==null){
				if(this.gestioneWorkflowStatiAccordi){
					try{
						archiveAccordoServizioParteComune.getAccordoServizioParteComune().setStatoPackage(StatiAccordo.finale.toString());
						this.importerEngine.validaStatoAccordoServizioParteComune(archiveAccordoServizioParteComune.getAccordoServizioParteComune(),utilizzoAzioniDiretteInAccordoAbilitato);
					}catch(ValidazioneStatoPackageException validazioneException){
						try{
							archiveAccordoServizioParteComune.getAccordoServizioParteComune().setStatoPackage(StatiAccordo.operativo.toString());
							this.importerEngine.validaStatoAccordoServizioParteComune(archiveAccordoServizioParteComune.getAccordoServizioParteComune(),utilizzoAzioniDiretteInAccordoAbilitato);
						}catch(ValidazioneStatoPackageException validazioneExceptionLevelOperativo){
							warningInfoStatoFinale.append("\n\t\t(WARNING) Accordo salvato con stato '").append(StatiAccordo.bozza.toString()).append("':\n\t\t\t");
							warningInfoStatoFinale.append(validazioneException.toString("\n\t\t\t - ","\n\t\t\t - "));
							warningInfoStatoFinale.append("\n\t\t\t"+validazioneExceptionLevelOperativo.toString("\n\t\t\t - ","\n\t\t\t - "));
							archiveAccordoServizioParteComune.getAccordoServizioParteComune().setStatoPackage(StatiAccordo.bozza.toString());
						}
					}
				}
				else{
					archiveAccordoServizioParteComune.getAccordoServizioParteComune().setStatoPackage(StatiAccordo.finale.toString());
				}
			}
			
			
			
			// --- Aderenza WSDL ---
			StringBuffer warningAderenzaWSDL = new StringBuffer("");
			this.informazioniServizioAderentiWSDL(archiveAccordoServizioParteComune.getAccordoServizioParteComune(), warningAderenzaWSDL);
			
			
			
			// --- ora registrazione
			archiveAccordoServizioParteComune.getAccordoServizioParteComune().setOraRegistrazione(DateManager.getDate());
			
			
			
			// --- upload ---
			if(this.importerEngine.existsAccordoServizioParteComune(idAccordoServizioParteComune)){
				
				AccordoServizioParteComune old = this.importerEngine.getAccordoServizioParteComune(idAccordoServizioParteComune);
				archiveAccordoServizioParteComune.getAccordoServizioParteComune().setId(old.getId());
				archiveAccordoServizioParteComune.getAccordoServizioParteComune().setOldIDAccordoForUpdate(this.idAccordoFactory.getIDAccordoFromAccordo(old));
				
				// visibilita' oggetto stesso per update
				if(this.importerEngine.isVisioneOggettiGlobale(this.userLogin)==false){
					if(this.userLogin.equals(old.getSuperUser())==false){
						throw new Exception("L'accordo non è visibile/aggiornabile dall'utente collegato ("+this.userLogin+")");
					}
				}

				// update
				this.importerEngine.updateAccordoServizioParteComune(archiveAccordoServizioParteComune.getAccordoServizioParteComune());
				detail.setState(ArchiveStatoImport.UPDATED);
				detail.setStateDetail(warningInfoStatoFinale.toString()+warningAderenzaWSDL.toString());
					
			}
			// --- create ---
			else{
				this.importerEngine.createAccordoServizioParteComune(archiveAccordoServizioParteComune.getAccordoServizioParteComune());
				detail.setState(ArchiveStatoImport.CREATED);
				detail.setStateDetail(warningInfoStatoFinale.toString()+warningAderenzaWSDL.toString());
			}
				

			
		}			
		catch(Exception e){
			this.log.error("Errore durante l'import dell'accordo di servizio parte comune ["+idAccordoServizioParteComune+"]: "+e.getMessage(),e);
			detail.setState(ArchiveStatoImport.ERROR);
			detail.setException(e);
		}
	}
	
	
	public void importAccordoServizioComposto(ArchiveAccordoServizioComposto archiveAccordoServizioComposto,
			boolean utilizzoAzioniDiretteInAccordoAbilitato,
			ArchiveEsitoImportDetail detail){
		
		IDAccordo idAccordoServizioComposto = archiveAccordoServizioComposto.getIdAccordoServizioParteComune();
		IDSoggetto idSoggettoReferente = archiveAccordoServizioComposto.getIdSoggettoReferente();
		IDAccordoCooperazione idAccordoCooperazione = archiveAccordoServizioComposto.getIdAccordoCooperazione();
		List<IDServizio> idServiziComponenti = archiveAccordoServizioComposto.getIdServiziComponenti();
		try{
			// --- check esistenza ---
			if(this.updateAbilitato==false){
				if(this.importerEngine.existsAccordoServizioParteComune(idAccordoServizioComposto)){
					detail.setState(ArchiveStatoImport.UPDATE_NOT_PERMISSED);
					return;
				}
			}
			
				
			// --- check elementi riferiti ---
			// soggetto
			if(this.importerEngine.existsSoggettoRegistro(idSoggettoReferente) == false ){
				throw new Exception("Soggetto proprietario ["+idSoggettoReferente+"] non esistente");
			}
			// accordo cooperazione
			if(this.importerEngine.existsAccordoCooperazione(idAccordoCooperazione) == false){
				throw new Exception("Accordo di Cooperazione ["+idAccordoCooperazione+"] riferito non esistente");
			}
			// servizi componenti
			for (IDServizio idServizioComponente : idServiziComponenti) {
				if(this.importerEngine.existsAccordoServizioParteSpecifica(idServizioComponente)==false){
					throw new Exception("ServizioComponente ["+idServizioComponente+"] riferito non esistente");
				}
			}
			
			
			// --- compatibilita' elementi riferiti ---
			String protocolloAssociatoAccordo = this.protocolFactoryManager.getProtocolBySubjectType(idSoggettoReferente.getTipo());
			// accordo cooperazione
			AccordoCooperazione ac = this.importerEngine.getAccordoCooperazione(idAccordoCooperazione);
			String protocolloAssociatoAccordoCooperazione = 
					this.protocolFactoryManager.getProtocolBySubjectType(ac.getSoggettoReferente().getTipo());
			if(protocolloAssociatoAccordo.equals(protocolloAssociatoAccordoCooperazione)==false){
				throw new Exception("Soggetto referente ("+ac.getSoggettoReferente().getTipo()+"/"+ac.getSoggettoReferente().getNome()+
						") dell'accordo di cooperazione ["+idAccordoCooperazione+"] (protocollo:"+protocolloAssociatoAccordoCooperazione+
						") non utilizzabile in un accordo con soggetto referente ["+idSoggettoReferente+"] (protocollo:"+protocolloAssociatoAccordo+")");
			}
			// servizi componenti
			for (IDServizio idServizioComponente : idServiziComponenti) {
				String protocolloAssociatoServizioComponente = this.protocolFactoryManager.getProtocolByServiceType(idServizioComponente.getTipoServizio());
				if(protocolloAssociatoAccordo.equals(protocolloAssociatoServizioComponente)==false){
					throw new Exception("ServizioComponente ["+idServizioComponente+"] (protocollo:"+protocolloAssociatoServizioComponente+
							") non utilizzabile in un accordo con soggetto referente ["+idSoggettoReferente+"] (protocollo:"+protocolloAssociatoAccordo+")");
				}
				String protocolloAssociatoSoggettoServizioComponente = this.protocolFactoryManager.getProtocolBySubjectType(idServizioComponente.getSoggettoErogatore().getTipo());
				if(protocolloAssociatoAccordo.equals(protocolloAssociatoSoggettoServizioComponente)==false){
					throw new Exception("ServizioComponente ["+idServizioComponente+"] (protocollo:"+protocolloAssociatoSoggettoServizioComponente+
							") non utilizzabile in un accordo con soggetto referente ["+idSoggettoReferente+"] (protocollo:"+protocolloAssociatoAccordo+")");
				}
			}
			

			// ---- visibilita' oggetto riferiti ---
			// soggetto
			org.openspcoop2.core.registry.Soggetto soggetto = this.importerEngine.getSoggettoRegistro(idSoggettoReferente);
			if(this.importerEngine.isVisioneOggettiGlobale(this.userLogin)==false){
				if(this.userLogin.equals(soggetto.getSuperUser())==false){
					throw new Exception("Il soggetto referente ["+idSoggettoReferente+"] non è visibile/utilizzabile dall'utente collegato ("+this.userLogin+")");
				}
			}
			// accordo cooperazione
			org.openspcoop2.core.registry.AccordoCooperazione accordoCooperazione = this.importerEngine.getAccordoCooperazione(idAccordoCooperazione);
			if(this.importerEngine.isVisioneOggettiGlobale(this.userLogin)==false){
				if(this.userLogin.equals(accordoCooperazione.getSuperUser())==false){
					throw new Exception("L'accordo di cooperazione ["+idAccordoCooperazione+"] non è visibile/utilizzabile dall'utente collegato ("+this.userLogin+")");
				}
			}
			// servizi componenti
			Hashtable<String, AccordoServizioParteSpecifica> serviziComponenti = new Hashtable<String, AccordoServizioParteSpecifica>();
			for (IDServizio idServizioComponente : idServiziComponenti) {
				
				AccordoServizioParteSpecifica asps = this.importerEngine.getAccordoServizioParteSpecifica(idServizioComponente);
				serviziComponenti.put(idServizioComponente.toString(), asps);
				
				if(this.importerEngine.isVisioneOggettiGlobale(this.userLogin)==false){
					
					if(this.userLogin.equals(asps.getSuperUser())==false){
						throw new Exception("Il servizio componente ["+idServizioComponente+"] non è visibile/utilizzabile dall'utente collegato ("+this.userLogin+").");
					}
					
					// soggetto
					Soggetto soggettoErogatoreServizioComponente = 
						this.importerEngine.getSoggettoRegistro(new IDSoggetto(idServizioComponente.getSoggettoErogatore().getTipo(),
								idServizioComponente.getSoggettoErogatore().getNome()));
					if(this.userLogin.equals(soggettoErogatoreServizioComponente.getSuperUser())==false){
						throw new Exception("Il servizio componente ["+idServizioComponente+"] (il soggetto erogatore in particolar modo) non è visibile/utilizzabile dall'utente collegato ("+this.userLogin+").");
					}
				}
			}

					
			// --- set dati obbligatori nel db ----

			org.openspcoop2.core.registry.driver.utils.XMLDataConverter.
				impostaInformazioniRegistroDB_AccordoServizioParteComune(archiveAccordoServizioComposto.getAccordoServizioParteComune());
			
			archiveAccordoServizioComposto.getAccordoServizioParteComune().setSuperUser(this.userLogin);
			
			for(int i=0; i<archiveAccordoServizioComposto.getAccordoServizioParteComune().sizeAllegatoList();i++){
				archiveAccordoServizioComposto.getAccordoServizioParteComune().getAllegato(i).setRuolo(RuoliDocumento.allegato.toString());
				archiveAccordoServizioComposto.getAccordoServizioParteComune().getAllegato(i).setTipoProprietarioDocumento(ProprietariDocumento.accordoServizio.toString());
			}
			for(int i=0; i<archiveAccordoServizioComposto.getAccordoServizioParteComune().sizeSpecificaSemiformaleList();i++){
				archiveAccordoServizioComposto.getAccordoServizioParteComune().getSpecificaSemiformale(i).setRuolo(RuoliDocumento.specificaSemiformale.toString());
				archiveAccordoServizioComposto.getAccordoServizioParteComune().getSpecificaSemiformale(i).setTipoProprietarioDocumento(ProprietariDocumento.accordoServizio.toString());
			}
			for(int i=0; i<archiveAccordoServizioComposto.getAccordoServizioParteComune().getServizioComposto().sizeSpecificaCoordinamentoList();i++){
				archiveAccordoServizioComposto.getAccordoServizioParteComune().getSpecificaSemiformale(i).setRuolo(RuoliDocumento.specificaCoordinamento.toString());
				archiveAccordoServizioComposto.getAccordoServizioParteComune().getSpecificaSemiformale(i).setTipoProprietarioDocumento(ProprietariDocumento.accordoServizio.toString());
			}

			archiveAccordoServizioComposto.getAccordoServizioParteComune().getServizioComposto().
				setIdAccordoCooperazione(accordoCooperazione.getId());
			
			for(int i=0; i<archiveAccordoServizioComposto.getAccordoServizioParteComune().getServizioComposto().sizeServizioComponenteList();i++){
				AccordoServizioParteComuneServizioCompostoServizioComponente componente =
						archiveAccordoServizioComposto.getAccordoServizioParteComune().getServizioComposto().getServizioComponente(i);
				IDServizio idServizioComponente = 
						new IDServizio(componente.getTipoSoggetto(), componente.getNomeSoggetto(), 
								componente.getTipo(), componente.getNome(), componente.getAzione());
				AccordoServizioParteSpecifica asps = serviziComponenti.get(idServizioComponente.toString());
				if(asps==null){
					Enumeration<String> idServizi = serviziComponenti.keys();
					StringBuffer bfException = new StringBuffer();
					while (idServizi.hasMoreElements()) {
						String idS = (String) idServizi.nextElement();
						if(bfException.length()>0){
							bfException.append(",");
						}
						bfException.append(idS);
					}
					throw new Exception("ServizioComponente ["+idServizioComponente+"] riferito non esistente?? Dovrebbe essere stato recuperato? ListaKeys: "+
							bfException.toString());
				}
				componente.setIdServizioComponente(asps.getId());
			}
				
			
			// --- workflowStatoDocumenti ---
			StringBuffer warningInfoStatoFinale = new StringBuffer("");
			if(archiveAccordoServizioComposto.getAccordoServizioParteComune().getStatoPackage()==null){
				if(this.gestioneWorkflowStatiAccordi){
					try{
						archiveAccordoServizioComposto.getAccordoServizioParteComune().setStatoPackage(StatiAccordo.finale.toString());
						this.importerEngine.validaStatoAccordoServizioParteComune(archiveAccordoServizioComposto.getAccordoServizioParteComune(),utilizzoAzioniDiretteInAccordoAbilitato);
					}catch(ValidazioneStatoPackageException validazioneException){
						try{
							archiveAccordoServizioComposto.getAccordoServizioParteComune().setStatoPackage(StatiAccordo.operativo.toString());
							this.importerEngine.validaStatoAccordoServizioParteComune(archiveAccordoServizioComposto.getAccordoServizioParteComune(),utilizzoAzioniDiretteInAccordoAbilitato);
						}catch(ValidazioneStatoPackageException validazioneExceptionLevelOperativo){
							warningInfoStatoFinale.append("\n\t\t(WARNING) Accordo salvato con stato '").append(StatiAccordo.bozza.toString()).append("':\n\t\t\t");
							warningInfoStatoFinale.append(validazioneException.toString("\n\t\t\t - ","\n\t\t\t - "));
							warningInfoStatoFinale.append("\n\t\t\t"+validazioneExceptionLevelOperativo.toString("\n\t\t\t - ","\n\t\t\t - "));
							archiveAccordoServizioComposto.getAccordoServizioParteComune().setStatoPackage(StatiAccordo.bozza.toString());
						}
					}
				}
				else{
					archiveAccordoServizioComposto.getAccordoServizioParteComune().setStatoPackage(StatiAccordo.finale.toString());
				}
			}
			
			
			
			// --- Aderenza WSDL ---
			StringBuffer warningAderenzaWSDL = new StringBuffer("");
			this.informazioniServizioAderentiWSDL(archiveAccordoServizioComposto.getAccordoServizioParteComune(), warningAderenzaWSDL);
			
			
			
			// --- ora registrazione
			archiveAccordoServizioComposto.getAccordoServizioParteComune().setOraRegistrazione(DateManager.getDate());
			
			
			
			// --- upload ---
			if(this.importerEngine.existsAccordoServizioParteComune(idAccordoServizioComposto)){
				
				AccordoServizioParteComune old = this.importerEngine.getAccordoServizioParteComune(idAccordoServizioComposto);
				archiveAccordoServizioComposto.getAccordoServizioParteComune().setId(old.getId());
				archiveAccordoServizioComposto.getAccordoServizioParteComune().setOldIDAccordoForUpdate(this.idAccordoFactory.getIDAccordoFromAccordo(old));
				
				// visibilita' oggetto stesso per update
				if(this.importerEngine.isVisioneOggettiGlobale(this.userLogin)==false){
					if(this.userLogin.equals(old.getSuperUser())==false){
						throw new Exception("L'accordo non è visibile/aggiornabile dall'utente collegato ("+this.userLogin+")");
					}
				}

				// update
				this.importerEngine.updateAccordoServizioParteComune(archiveAccordoServizioComposto.getAccordoServizioParteComune());
				detail.setState(ArchiveStatoImport.UPDATED);
				detail.setStateDetail(warningInfoStatoFinale.toString()+warningAderenzaWSDL.toString());
					
			}
			// --- create ---
			else{
				this.importerEngine.createAccordoServizioParteComune(archiveAccordoServizioComposto.getAccordoServizioParteComune());
				detail.setState(ArchiveStatoImport.CREATED);
				detail.setStateDetail(warningInfoStatoFinale.toString()+warningAderenzaWSDL.toString());
			}
				

			
		}			
		catch(Exception e){
			this.log.error("Errore durante l'import dell'accordo di servizio parte comune ["+idAccordoServizioComposto+"]: "+e.getMessage(),e);
			detail.setState(ArchiveStatoImport.ERROR);
			detail.setException(e);
		}
	}
	
	
	private void informazioniServizioAderentiWSDL(AccordoServizioParteComune accordoServizioParteComune,StringBuffer anomalieRiscontrate) throws Exception{
		
		try{
		
			String indent = "\t\t";
			
			// lettura WSDL Concettuale
			StringBuffer strutturaWSDL = new StringBuffer("Struttura WSDL:\n");
			//strutturaWSDL.append(indent+"=========================================\n");
			byte[]wsdl = accordoServizioParteComune.getByteWsdlConcettuale();
			if(wsdl==null){
				throw new Exception("WSDLConcettuale non fornito");
			}
			RegistroOpenSPCoopUtilities wsdlUtility = new RegistroOpenSPCoopUtilities(this.log);
			wsdl=wsdlUtility.eliminaImportASParteComune(wsdl);
			DefinitionWrapper wsdlObject = new DefinitionWrapper(wsdl,XMLUtils.getInstance());
			Hashtable<String, List<String>> mapPortTypeOperations = new Hashtable<String, List<String>>();
			java.util.Map<?,?> pts = wsdlObject.getAllPortTypes();
			if(pts==null || pts.size()<=0){
				this.log.warn("Non sono stati definiti port types nel wsdl concettuale");
			}else{
				java.util.Iterator<?> ptsIterator = pts.values().iterator();
				java.util.ArrayList<String> listPortTypesName = new ArrayList<String>();
				while(ptsIterator.hasNext()) {
					javax.wsdl.PortType pt = (javax.wsdl.PortType) ptsIterator.next();
					String ptName = pt.getQName().getLocalPart();
					listPortTypesName.add(ptName);
					java.util.List<?> ops = pt.getOperations();
					java.util.Iterator<?> opIt = ops.iterator();
					java.util.ArrayList<String> listOperationName = new ArrayList<String>();
					while(opIt.hasNext()){
						javax.wsdl.Operation op = (javax.wsdl.Operation) opIt.next();
						String nomeOperazione = op.getName();
						listOperationName.add(nomeOperazione);
					}
					java.util.Collections.sort(listOperationName);
					mapPortTypeOperations.put(ptName, listOperationName);
				}
				
				java.util.Collections.sort(listPortTypesName);
				for (String ptName : listPortTypesName) {
					strutturaWSDL.append(indent+". "+ptName+"\n");
					List<String> opList = mapPortTypeOperations.get(ptName);
					for (String opName : opList) {
						strutturaWSDL.append(indent+"\t- "+opName+"\n");
					}
				}
			}
			
			// Accordo di Servizio registrato
			StringBuffer strutturaAccordoOpenSPCoop = new StringBuffer("Servizi/Azioni associati all'accordo:\n");
			//strutturaAccordoOpenSPCoop.append(indent+"=========================================\n");
			java.util.ArrayList<String> listPortTypesName = new ArrayList<String>();
			Hashtable<String, PortType> mapPtNameToObject = new Hashtable<String, PortType>();
			for(int i=0;i<accordoServizioParteComune.sizePortTypeList();i++){
				PortType ptOpenSPCoop = accordoServizioParteComune.getPortType(i);
				listPortTypesName.add(ptOpenSPCoop.getNome());
				mapPtNameToObject.put(ptOpenSPCoop.getNome(), ptOpenSPCoop);
			}
			java.util.Collections.sort(listPortTypesName);
			for (String ptName : listPortTypesName) {
				PortType ptOpenSPCoop = mapPtNameToObject.get(ptName);
				strutturaAccordoOpenSPCoop.append(indent+". "+ptOpenSPCoop.getNome()+"\n");
				java.util.ArrayList<String> listOperationName = new ArrayList<String>();
				for(int j=0;j<ptOpenSPCoop.sizeAzioneList();j++){
					Operation opOpenSPCoop = ptOpenSPCoop.getAzione(j);
					listOperationName.add(opOpenSPCoop.getNome());
				}
				java.util.Collections.sort(listOperationName);
				for (String opName : listOperationName) {
					strutturaAccordoOpenSPCoop.append(indent+"\t- "+opName+"\n");
				}
			}
		
			
			
			
			// Verifica su PT dell'accordo
			boolean aderenzaWSDL = true;
			for(int i=0;i<accordoServizioParteComune.sizePortTypeList();i++){
				PortType ptOpenSPCoop = accordoServizioParteComune.getPortType(i);
				if(mapPortTypeOperations.containsKey(ptOpenSPCoop.getNome())){
					List<String> opWSDL = mapPortTypeOperations.remove(ptOpenSPCoop.getNome());
					for(int j=0;j<ptOpenSPCoop.sizeAzioneList();j++){
						Operation opOpenSPCoop = ptOpenSPCoop.getAzione(j);
						if(opWSDL.contains(opOpenSPCoop.getNome())){
							opWSDL.remove(opOpenSPCoop.getNome());
						}else{
							aderenzaWSDL = false;
							break;
						}
					}
					if(opWSDL.size()>0){
						aderenzaWSDL = false;
						break;
					}
				}else{
					aderenzaWSDL = false;
					break;
				}
			}
			if(aderenzaWSDL){
				if(mapPortTypeOperations.size()>0){
					aderenzaWSDL = false;
				}
			}
			
			if(aderenzaWSDL==false){
				anomalieRiscontrate.append("\n"+indent+"(WARNING) accordo importato contiene una struttura (servizi e azioni) non aderente all'interfaccia WSDL");
				anomalieRiscontrate.append("\n"+indent+"Alcuni servizi o alcune azioni associate all'accordo importato");
				anomalieRiscontrate.append("\n"+indent+"non corrispondono ai port types e alle operations definite");
				anomalieRiscontrate.append("\n"+indent);
				anomalieRiscontrate.append(strutturaWSDL);
				anomalieRiscontrate.append(indent);
				anomalieRiscontrate.append(strutturaAccordoOpenSPCoop);
			}
			//return aderenzaWSDL;
			
		}catch(Exception e){
			// Non devo segnalare nulla. Comunque se c'e' abilitata la validazione otterro' un errore senza importare l'archivio.
			// se invece e' stato indicato di non effettuare la validazione e' corretto che venga importato comunque l'archivio.
			// senza che vi sia per forza un wsdl corretto.
			this.log.debug("Check informazioniServizioAderentiWSDL non riuscito: "+e.getMessage());
		}
		
		return;
	}
	
	
	
	
	
	
	
	
	public void importAccordoServizioParteSpecifica(ArchiveAccordoServizioParteSpecifica archiveAccordoServizioParteSpecifica,
			boolean servizioComposto,
			boolean isAbilitatoControlloUnicitaImplementazioneAccordoPerSoggetto,
			boolean isAbilitatoControlloUnicitaImplementazionePortTypePerSoggetto,
			ArchiveEsitoImportDetail detail){
		
		IDAccordo idAccordoServizioParteComune = archiveAccordoServizioParteSpecifica.getIdAccordoServizioParteComune();
		IDAccordo idAccordoServizioParteSpecifica = archiveAccordoServizioParteSpecifica.getIdAccordoServizioParteSpecifica();
		IDServizio idServizio = archiveAccordoServizioParteSpecifica.getIdServizio();
		IDSoggetto idSoggettoErogatore = archiveAccordoServizioParteSpecifica.getIdSoggettoErogatore();
		String labelAccordoParteComune = "Accordo di Servizio Parte Comune";
		if(servizioComposto){
			labelAccordoParteComune = "Accordo di Servizio Composto";
		}
		try{
			// --- check esistenza ---
			if(this.updateAbilitato==false){
				if(this.importerEngine.existsAccordoServizioParteSpecifica(idAccordoServizioParteSpecifica)){
					detail.setState(ArchiveStatoImport.UPDATE_NOT_PERMISSED);
					return;
				}
			}
			
				
			// --- check elementi riferiti ---
			// soggetto erogatore
			if(this.importerEngine.existsSoggettoRegistro(idSoggettoErogatore) == false ){
				throw new Exception("Soggetto erogatore ["+idSoggettoErogatore+"] non esistente");
			}
			// accordo di servizio parte comune
			if(this.importerEngine.existsAccordoServizioParteComune(idAccordoServizioParteComune) == false ){
				throw new Exception(labelAccordoParteComune+" ["+idAccordoServizioParteComune+"] non esistente");
			}
			// dati
			org.openspcoop2.core.registry.Soggetto soggetto = this.importerEngine.getSoggettoRegistro(idSoggettoErogatore);
			AccordoServizioParteComune accordoServizioParteComune = this.importerEngine.getAccordoServizioParteComune(idAccordoServizioParteComune);
			AccordoServizioParteSpecifica old = null;
			long idAccordoServizioParteSpecificaLong = -1;
			boolean isUpdate = false;
			boolean servizioCorrelato = 
					TipologiaServizio.CORRELATO.
						equals(archiveAccordoServizioParteSpecifica.getAccordoServizioParteSpecifica().getServizio().getTipologiaServizio());
			if(this.importerEngine.existsAccordoServizioParteSpecifica(idAccordoServizioParteSpecifica)){
				old = this.importerEngine.getAccordoServizioParteSpecifica(idAccordoServizioParteSpecifica);
				idAccordoServizioParteSpecificaLong = old.getId();
				isUpdate = true;
			}
			// exists other service che implementa lo stesso accordo di servizio parte comune
			this.importerEngine.controlloUnicitaImplementazioneAccordoPerSoggetto(archiveAccordoServizioParteSpecifica.getAccordoServizioParteSpecifica().getPortType(), 
					idSoggettoErogatore, soggetto.getId(), 
					idAccordoServizioParteComune, accordoServizioParteComune.getId(), 
					idServizio, idAccordoServizioParteSpecificaLong, 
					isUpdate, servizioCorrelato,
					isAbilitatoControlloUnicitaImplementazioneAccordoPerSoggetto,
					isAbilitatoControlloUnicitaImplementazionePortTypePerSoggetto);
			// exists other service with same tipo/nome e tipo/nome soggetto erogatore 
			if(this.importerEngine.existsAccordoServizioParteSpecifica(idServizio)){
				if(!isUpdate){
					AccordoServizioParteSpecifica aspsCheck = this.importerEngine.getAccordoServizioParteSpecifica(idServizio);
					throw new Exception("Servizio ["+idServizio.getTipoServizio()+"/"+idServizio.getServizio()+"] erogato dal soggetto ["+idSoggettoErogatore
							+"] già esistente definito all'interno dell'accordo di servizio parte specifica ["+IDAccordoFactory.getInstance().getUriFromAccordo(aspsCheck)+"]");
				}
				else{
					// change
					AccordoServizioParteSpecifica aspsCheck = this.importerEngine.getAccordoServizioParteSpecifica(idServizio);
					IDAccordo idAccordoASPSCheck = IDAccordoFactory.getInstance().getIDAccordoFromAccordo(aspsCheck);
					if(idAccordoASPSCheck.equals(idAccordoServizioParteSpecifica)==false){
						throw new Exception("Servizio ["+idServizio.getTipoServizio()+"/"+idServizio.getServizio()+"] erogato dal soggetto ["+idSoggettoErogatore
								+"] già esistente definito all'interno dell'accordo di servizio parte specifica ["+IDAccordoFactory.getInstance().getUriFromAccordo(aspsCheck)+"]");
					}
				}
			}
			
			
			// --- compatibilita' elementi riferiti ---
			String protocolloAssociatoAccordo = this.protocolFactoryManager.getProtocolBySubjectType(idSoggettoErogatore.getTipo());
			// accordo di servizio parte comune
			String protocolloAssociatoAccordoParteComune = this.protocolFactoryManager.getProtocolBySubjectType(idAccordoServizioParteComune.getSoggettoReferente().getTipo());
			if(protocolloAssociatoAccordo.equals(protocolloAssociatoAccordoParteComune)==false){
				throw new Exception("AccordoServizioParteComune ["+idAccordoServizioParteComune+"] (protocollo:"+protocolloAssociatoAccordoParteComune+
						") non utilizzabile in un accordo con soggetto erogatore ["+idSoggettoErogatore+"] (protocollo:"+protocolloAssociatoAccordo+")");
			}
				
			
			// ---- visibilita' oggetto riferiti ---
			// soggetto erogatore
			if(this.importerEngine.isVisioneOggettiGlobale(this.userLogin)==false){
				if(this.userLogin.equals(soggetto.getSuperUser())==false){
					throw new Exception("Il soggetto erogatore ["+idSoggettoErogatore+"] non è visibile/utilizzabile dall'utente collegato ("+this.userLogin+")");
				}
			}
			// accordo di servizio parte comune
			if(this.importerEngine.isVisioneOggettiGlobale(this.userLogin)==false){
				if(this.userLogin.equals(accordoServizioParteComune.getSuperUser())==false){
					throw new Exception("L'accordo di servizio parte comune ["+idAccordoServizioParteComune+"] non è visibile/utilizzabile dall'utente collegato ("+this.userLogin+")");
				}
			}
			
			
			// --- set dati obbligatori nel db ----

			org.openspcoop2.core.registry.driver.utils.XMLDataConverter.
				impostaInformazioniRegistroDB_AccordoServizioParteSpecifica(archiveAccordoServizioParteSpecifica.getAccordoServizioParteSpecifica());
			
			archiveAccordoServizioParteSpecifica.getAccordoServizioParteSpecifica().setSuperUser(this.userLogin);
			
			for(int i=0; i<archiveAccordoServizioParteSpecifica.getAccordoServizioParteSpecifica().sizeAllegatoList();i++){
				archiveAccordoServizioParteSpecifica.getAccordoServizioParteSpecifica().getAllegato(i).setRuolo(RuoliDocumento.allegato.toString());
				archiveAccordoServizioParteSpecifica.getAccordoServizioParteSpecifica().getAllegato(i).setTipoProprietarioDocumento(ProprietariDocumento.servizio.toString());
			}
			for(int i=0; i<archiveAccordoServizioParteSpecifica.getAccordoServizioParteSpecifica().sizeSpecificaSemiformaleList();i++){
				archiveAccordoServizioParteSpecifica.getAccordoServizioParteSpecifica().getSpecificaSemiformale(i).setRuolo(RuoliDocumento.specificaSemiformale.toString());
				archiveAccordoServizioParteSpecifica.getAccordoServizioParteSpecifica().getSpecificaSemiformale(i).setTipoProprietarioDocumento(ProprietariDocumento.servizio.toString());
			}
			for(int i=0; i<archiveAccordoServizioParteSpecifica.getAccordoServizioParteSpecifica().sizeSpecificaLivelloServizioList();i++){
				archiveAccordoServizioParteSpecifica.getAccordoServizioParteSpecifica().getSpecificaLivelloServizio(i).setRuolo(RuoliDocumento.specificaLivelloServizio.toString());
				archiveAccordoServizioParteSpecifica.getAccordoServizioParteSpecifica().getSpecificaLivelloServizio(i).setTipoProprietarioDocumento(ProprietariDocumento.servizio.toString());
			}
			for(int i=0; i<archiveAccordoServizioParteSpecifica.getAccordoServizioParteSpecifica().sizeSpecificaSicurezzaList();i++){
				archiveAccordoServizioParteSpecifica.getAccordoServizioParteSpecifica().getSpecificaSicurezza(i).setRuolo(RuoliDocumento.specificaSicurezza.toString());
				archiveAccordoServizioParteSpecifica.getAccordoServizioParteSpecifica().getSpecificaSicurezza(i).setTipoProprietarioDocumento(ProprietariDocumento.servizio.toString());
			}
					
			
			// --- workflowStatoDocumenti ---
			StringBuffer warningInfoStatoFinale = new StringBuffer("");
			if(archiveAccordoServizioParteSpecifica.getAccordoServizioParteSpecifica().getStatoPackage()==null){
				if(this.gestioneWorkflowStatiAccordi){
					try{
						archiveAccordoServizioParteSpecifica.getAccordoServizioParteSpecifica().setStatoPackage(StatiAccordo.finale.toString());
						this.importerEngine.validaStatoAccordoServizioParteSpecifica(archiveAccordoServizioParteSpecifica.getAccordoServizioParteSpecifica());
					}catch(ValidazioneStatoPackageException validazioneException){
						try{
							archiveAccordoServizioParteSpecifica.getAccordoServizioParteSpecifica().setStatoPackage(StatiAccordo.operativo.toString());
							this.importerEngine.validaStatoAccordoServizioParteSpecifica(archiveAccordoServizioParteSpecifica.getAccordoServizioParteSpecifica());
						}catch(ValidazioneStatoPackageException validazioneExceptionLevelOperativo){
							warningInfoStatoFinale.append("\n\t\t(WARNING) Accordo salvato con stato '").append(StatiAccordo.bozza.toString()).append("':\n\t\t\t");
							warningInfoStatoFinale.append(validazioneException.toString("\n\t\t\t - ","\n\t\t\t - "));
							warningInfoStatoFinale.append("\n\t\t\t"+validazioneExceptionLevelOperativo.toString("\n\t\t\t - ","\n\t\t\t - "));
							archiveAccordoServizioParteSpecifica.getAccordoServizioParteSpecifica().setStatoPackage(StatiAccordo.bozza.toString());
						}
					}
				}
				else{
					archiveAccordoServizioParteSpecifica.getAccordoServizioParteSpecifica().setStatoPackage(StatiAccordo.finale.toString());
				}
			}
			
					
			// --- ora registrazione
			archiveAccordoServizioParteSpecifica.getAccordoServizioParteSpecifica().setOraRegistrazione(DateManager.getDate());
			
			
			// --- upload ---
			org.openspcoop2.core.registry.driver.utils.XMLDataConverter.
				aggiornatoStatoFruitori(archiveAccordoServizioParteSpecifica.getAccordoServizioParteSpecifica(), 
						StatiAccordo.valueOf(archiveAccordoServizioParteSpecifica.getAccordoServizioParteSpecifica().getStatoPackage()));
			
			if(this.importerEngine.existsAccordoServizioParteSpecifica(idAccordoServizioParteSpecifica)){
				
				archiveAccordoServizioParteSpecifica.getAccordoServizioParteSpecifica().setId(old.getId());
				archiveAccordoServizioParteSpecifica.getAccordoServizioParteSpecifica().getServizio().setOldTipoForUpdate(old.getServizio().getTipo());
				archiveAccordoServizioParteSpecifica.getAccordoServizioParteSpecifica().getServizio().setOldNomeForUpdate(old.getServizio().getNome());
				archiveAccordoServizioParteSpecifica.getAccordoServizioParteSpecifica().getServizio().setOldTipoSoggettoErogatoreForUpdate(old.getServizio().getTipoSoggettoErogatore());
				archiveAccordoServizioParteSpecifica.getAccordoServizioParteSpecifica().getServizio().setOldNomeSoggettoErogatoreForUpdate(old.getServizio().getNomeSoggettoErogatore());
				boolean mantieniFruitoriEsistenti = true; 
				org.openspcoop2.core.registry.driver.utils.XMLDataConverter.
					impostaInformazioniRegistro_AccordoServizioParteSpecifica_update(archiveAccordoServizioParteSpecifica.getAccordoServizioParteSpecifica(),
							old,mantieniFruitoriEsistenti);
				
				// visibilita' oggetto stesso per update
				if(this.importerEngine.isVisioneOggettiGlobale(this.userLogin)==false){
					if(this.userLogin.equals(old.getSuperUser())==false){
						throw new Exception("L'accordo non è visibile/aggiornabile dall'utente collegato ("+this.userLogin+")");
					}
				}

				// update
				this.importerEngine.updateAccordoServizioParteSpecifica(archiveAccordoServizioParteSpecifica.getAccordoServizioParteSpecifica());
				detail.setState(ArchiveStatoImport.UPDATED);
				detail.setStateDetail(warningInfoStatoFinale.toString());
					
			}
			// --- create ---
			else{
				this.importerEngine.createAccordoServizioParteSpecifica(archiveAccordoServizioParteSpecifica.getAccordoServizioParteSpecifica());
				detail.setState(ArchiveStatoImport.CREATED);
				detail.setStateDetail(warningInfoStatoFinale.toString());
			}
				

			
		}			
		catch(Exception e){
			this.log.error("Errore durante l'import dell'accordo di servizio parte specifica ["+idAccordoServizioParteSpecifica+"]: "+e.getMessage(),e);
			detail.setState(ArchiveStatoImport.ERROR);
			detail.setException(e);
		}
	}
	
	
	
	
	public void importFruitore(ArchiveFruitore archiveFruitore,
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
			if(this.updateAbilitato==false){
				if(old!=null){
					detail.setState(ArchiveStatoImport.UPDATE_NOT_PERMISSED);
					return;
				}
			}
			
						
			// --- check elementi riferiti ---
			// soggetto fruitore
			if(this.importerEngine.existsSoggettoRegistro(idSoggettoFruitore) == false ){
				throw new Exception("Soggetto fruitore ["+idSoggettoFruitore+"] non esistente");
			}
			// accordo di servizio parte specifica
			if(this.importerEngine.existsAccordoServizioParteSpecifica(idAccordoServizioParteSpecifica) == false){
				throw new Exception("Accordo di Servizio Parte Specifica non esistente");
			}
		
			
			// --- compatibilita' elementi riferiti ---
			String protocolloAssociatoFruitore = this.protocolFactoryManager.getProtocolBySubjectType(idSoggettoFruitore.getTipo());
			// accordo di servizio parte specifica
			String protocolloAssociatoAccordoParteSpecifica = this.protocolFactoryManager.getProtocolBySubjectType(idAccordoServizioParteSpecifica.getSoggettoReferente().getTipo());
			if(protocolloAssociatoFruitore.equals(protocolloAssociatoAccordoParteSpecifica)==false){
				throw new Exception("AccordoServizioParteSpecifica ["+idAccordoServizioParteSpecifica+"] (protocollo:"+protocolloAssociatoAccordoParteSpecifica+
						") non utilizzabile in un fruitore ["+idSoggettoFruitore+"] (protocollo:"+protocolloAssociatoFruitore+")");
			}
				
			
			// ---- visibilita' oggetto riferiti ---
			// soggetto 
			Soggetto soggetto = this.importerEngine.getSoggettoRegistro(idSoggettoFruitore);
			if(this.importerEngine.isVisioneOggettiGlobale(this.userLogin)==false){
				if(this.userLogin.equals(soggetto.getSuperUser())==false){
					throw new Exception("Il soggetto fruitore ["+idSoggettoFruitore+"] non è visibile/utilizzabile dall'utente collegato ("+this.userLogin+")");
				}
			}
			// accordo di servizio parte specifica
			if(this.importerEngine.isVisioneOggettiGlobale(this.userLogin)==false){
				if(this.userLogin.equals(oldAccordo.getSuperUser())==false){
					throw new Exception("L'accordo di servizio parte specifica ["+idAccordoServizioParteSpecifica+"] non è visibile/utilizzabile dall'utente collegato ("+this.userLogin+")");
				}
			}
			
			
			// --- set dati obbligatori nel db ----

			org.openspcoop2.core.registry.driver.utils.XMLDataConverter.
				impostaInformazioniRegistroDB_AccordoServizioParteSpecifica_Fruitore(archiveFruitore.getFruitore());
					
			
			// --- workflowStatoDocumenti ---
			StringBuffer warningInfoStatoFinale = new StringBuffer("");
			if(archiveFruitore.getFruitore()==null){
				if(this.gestioneWorkflowStatiAccordi){
					try{
						archiveFruitore.getFruitore().setStatoPackage(StatiAccordo.finale.toString());
						this.importerEngine.validaStatoFruitoreServizio(archiveFruitore.getFruitore(),oldAccordo);
					}catch(ValidazioneStatoPackageException validazioneException){
						try{
							archiveFruitore.getFruitore().setStatoPackage(StatiAccordo.operativo.toString());
							this.importerEngine.validaStatoFruitoreServizio(archiveFruitore.getFruitore(),oldAccordo);
						}catch(ValidazioneStatoPackageException validazioneExceptionLevelOperativo){
							warningInfoStatoFinale.append("\n\t\t(WARNING) Fruitore salvato con stato '").append(StatiAccordo.bozza.toString()).append("':\n\t\t\t");
							warningInfoStatoFinale.append(validazioneException.toString("\n\t\t\t - ","\n\t\t\t - "));
							warningInfoStatoFinale.append("\n\t\t\t"+validazioneExceptionLevelOperativo.toString("\n\t\t\t - ","\n\t\t\t - "));
							archiveFruitore.getFruitore().setStatoPackage(StatiAccordo.bozza.toString());
						}
					}
				}
				else{
					archiveFruitore.getFruitore().setStatoPackage(StatiAccordo.finale.toString());
				}
			}
			
			
			
			// --- ora registrazione
			archiveFruitore.getFruitore().setOraRegistrazione(DateManager.getDate());
			
			
						
			// --- upload ---
			// prima ho rimosso il fruitore se gia' esisteva.
			if(old!=null){
				archiveFruitore.getFruitore().setId(old.getId());
			}
			oldAccordo.addFruitore(archiveFruitore.getFruitore());
			
			// update
			oldAccordo.getServizio().setOldTipoForUpdate(oldAccordo.getServizio().getTipo());
			oldAccordo.getServizio().setOldNomeForUpdate(oldAccordo.getServizio().getNome());
			oldAccordo.getServizio().setOldTipoSoggettoErogatoreForUpdate(oldAccordo.getServizio().getTipoSoggettoErogatore());
			oldAccordo.getServizio().setOldNomeSoggettoErogatoreForUpdate(oldAccordo.getServizio().getNomeSoggettoErogatore());
			this.importerEngine.updateAccordoServizioParteSpecifica(oldAccordo);
			
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
					if(!found){
						this.importerEngine.createServizioApplicativoAutorizzato(archiveFruitore.getIdAccordoServizioParteSpecifica(), archiveFruitore.getIdSoggettoFruitore(), nomeServizioApplicativo);
					}
				}
			}
			
			if(old!=null){
				detail.setState(ArchiveStatoImport.UPDATED);
				detail.setStateDetail(warningInfoStatoFinale.toString());
			}else{
				detail.setState(ArchiveStatoImport.CREATED);
				detail.setStateDetail(warningInfoStatoFinale.toString());
			}
					
		}			
		catch(Exception e){
			this.log.error("Errore durante l'import del fruitore["+idSoggettoFruitore+"] dell'accordo di servizio parte specifica ["+idAccordoServizioParteSpecifica+"]: "+e.getMessage(),e);
			detail.setState(ArchiveStatoImport.ERROR);
			detail.setException(e);
		}
	}
	
	
	
	
	public void importPortaDelegata(ArchivePortaDelegata archivePortaDelegata,
			ArchiveEsitoImportDetail detail){
		
		IDPortaDelegata idPortaDelegata = archivePortaDelegata.getIdPortaDelegata();
		IDSoggetto idSoggettoProprietario = archivePortaDelegata.getIdSoggettoProprietario();
		try{
			// --- check esistenza ---
			if(this.updateAbilitato==false){
				if(this.importerEngine.existsPortaDelegata(idPortaDelegata)){
					detail.setState(ArchiveStatoImport.UPDATE_NOT_PERMISSED);
					return;
				}
			}
			
			
			// --- lettura elementi riferiti all'interno della porta delegata ---
			PortaDelegata pd = archivePortaDelegata.getPortaDelegata();
			IDSoggetto idSoggettoErogatore = null;
			IDServizio idServizio = null;
			if(pd.getSoggettoErogatore()!=null &&
					pd.getSoggettoErogatore().getTipo()!=null &&
					pd.getSoggettoErogatore().getNome()!=null &&
					((pd.getSoggettoErogatore().getIdentificazione()==null)) || (CostantiConfigurazione.PORTA_DELEGATA_SOGGETTO_EROGATORE_STATIC.equals(pd.getSoggettoErogatore().getIdentificazione())) ){
				idSoggettoErogatore = new IDSoggetto(pd.getSoggettoErogatore().getTipo(), pd.getSoggettoErogatore().getNome());
			}
			if(idSoggettoErogatore!=null){
				if(pd.getServizio()!=null &&
						pd.getServizio().getTipo()!=null &&
						pd.getServizio().getNome()!=null &&
						((pd.getServizio().getIdentificazione()==null)) || (CostantiConfigurazione.PORTA_DELEGATA_SERVIZIO_STATIC.equals(pd.getServizio().getIdentificazione())) ){
					idServizio = new IDServizio(idSoggettoErogatore, 
							pd.getServizio().getTipo(), pd.getServizio().getNome());
				}
			}
			
			
			// --- check elementi riferiti ---
			if(this.importerEngine.existsSoggettoConfigurazione(idSoggettoProprietario) == false ){
				throw new Exception("Soggetto proprietario ["+idSoggettoProprietario+"] non esistente");
			}
			if(idSoggettoErogatore!=null){
				if(this.importerEngine.existsSoggettoRegistro(idSoggettoErogatore) == false ){
					throw new Exception("Soggetto erogatore riferito nella PD ["+idSoggettoErogatore+"] non esistente");
				}
			}
			if(idServizio!=null){
				if(this.importerEngine.existsAccordoServizioParteSpecifica(idServizio) == false ){
					throw new Exception("Servizio riferito nella PD ["+idServizio+"] non esistente");
				}
			}
			
			
			// --- compatibilita' elementi riferiti ---
			String protocolloAssociatoSoggettoProprietario = this.protocolFactoryManager.getProtocolBySubjectType(idSoggettoProprietario.getTipo());
			// erogatore
			if(idSoggettoErogatore!=null){
				String protocolloAssociatoSoggettoErogatore = this.protocolFactoryManager.getProtocolBySubjectType(idSoggettoErogatore.getTipo());
				if(protocolloAssociatoSoggettoProprietario.equals(protocolloAssociatoSoggettoErogatore)==false){
					throw new Exception("SoggettoErogatore ["+idSoggettoErogatore+"] (protocollo:"+protocolloAssociatoSoggettoErogatore+
							") non utilizzabile in una porta delegata appartenete al soggetto ["+idSoggettoProprietario+"] (protocollo:"+protocolloAssociatoSoggettoProprietario+")");
				}
			}
			// accordo di servizio parte specifica
			if(idServizio!=null){
				String protocolloAssociatoAccordoParteSpecifica = this.protocolFactoryManager.getProtocolByServiceType(idServizio.getTipoServizio());
				if(protocolloAssociatoSoggettoProprietario.equals(protocolloAssociatoAccordoParteSpecifica)==false){
					throw new Exception("AccordoServizioParteSpecifica ["+idServizio+"] (protocollo:"+protocolloAssociatoAccordoParteSpecifica+
							") con servizio non utilizzabile in una porta delegata appartenete al soggetto ["+idSoggettoProprietario+"] (protocollo:"+protocolloAssociatoSoggettoProprietario+")");
				}
				String protocolloAssociatoSoggettoAccordoParteSpecifica = this.protocolFactoryManager.getProtocolBySubjectType(idServizio.getSoggettoErogatore().getTipo());
				if(protocolloAssociatoSoggettoProprietario.equals(protocolloAssociatoSoggettoAccordoParteSpecifica)==false){
					throw new Exception("AccordoServizioParteSpecifica ["+idServizio+"] (protocollo:"+protocolloAssociatoSoggettoAccordoParteSpecifica+
							") con soggetto non utilizzabile in una porta delegata appartenete al soggetto ["+idSoggettoProprietario+"] (protocollo:"+protocolloAssociatoSoggettoProprietario+")");
				}
			}
			
			
			// ---- visibilita' oggetto riferiti ---
			org.openspcoop2.core.config.Soggetto soggetto = this.importerEngine.getSoggettoConfigurazione(idSoggettoProprietario);
			if(this.importerEngine.isVisioneOggettiGlobale(this.userLogin)==false){
				if(this.userLogin.equals(soggetto.getSuperUser())==false){
					throw new Exception("Il soggetto proprietario ["+idSoggettoProprietario+"] non è visibile/utilizzabile dall'utente collegato ("+this.userLogin+")");
				}
			}
			Soggetto soggettoErogatore = null;
			AccordoServizioParteSpecifica accordoServizioParteSpecifica = null;
			if(idSoggettoErogatore!=null){
				soggettoErogatore = this.importerEngine.getSoggettoRegistro(idSoggettoErogatore);
				if(this.importerEngine.isVisioneOggettiGlobale(this.userLogin)==false){
					if(this.userLogin.equals(soggettoErogatore.getSuperUser())==false){
						throw new Exception("Il soggetto erogatore riferito nella PD ["+idSoggettoErogatore+"] non è visibile/utilizzabile dall'utente collegato ("+this.userLogin+")");
					}
				}
			}
			if(idServizio!=null){
				accordoServizioParteSpecifica = this.importerEngine.getAccordoServizioParteSpecifica(idServizio);
				if(this.importerEngine.isVisioneOggettiGlobale(this.userLogin)==false){
					if(this.userLogin.equals(accordoServizioParteSpecifica.getSuperUser())==false){
						throw new Exception("Il servizio riferito nella PD ["+idServizio+"] non è visibile/utilizzabile dall'utente collegato ("+this.userLogin+")");
					}
				}
			}
			
			
			// --- set dati obbligatori nel db ----
			
			//pd.setSuperUser(this.userLogin);
			// L'oggetto non contiene informazione di visibilita, la eredita dal soggetto
			
			if(idSoggettoErogatore!=null){
				pd.getSoggettoErogatore().setId(soggettoErogatore.getId());
			}
			if(idServizio!=null){
				pd.getServizio().setId(accordoServizioParteSpecifica.getId());
			}
			
			
			// --- ora registrazione
			pd.setOraRegistrazione(DateManager.getDate());
			
			
			// --- upload ---
			if(this.importerEngine.existsPortaDelegata(idPortaDelegata)){
				
				org.openspcoop2.core.config.PortaDelegata old = this.importerEngine.getPortaDelegata(idPortaDelegata);
				pd.setId(old.getId());
				pd.setOldNomeForUpdate(old.getNome());
				pd.setOldTipoSoggettoProprietarioForUpdate(old.getTipoSoggettoProprietario());
				pd.setOldNomeSoggettoProprietarioForUpdate(old.getNomeSoggettoProprietario());
				org.openspcoop2.core.config.driver.utils.XMLDataConverter.
					impostaInformazioniConfigurazione_PortaDelegata(pd);
				
				// visibilita' oggetto stesso per update
				// L'oggetto non contiene informazione di visibilita, la eredita dal soggetto

				// update
				this.importerEngine.updatePortaDelegata(pd);
				detail.setState(ArchiveStatoImport.UPDATED);
			}
			// --- create ---
			else{
				this.importerEngine.createPortaDelegata(pd);
				detail.setState(ArchiveStatoImport.CREATED);
			}
				

			
		}			
		catch(Exception e){
			this.log.error("Errore durante l'import della porta delegata ["+idPortaDelegata+"]: "+e.getMessage(),e);
			detail.setState(ArchiveStatoImport.ERROR);
			detail.setException(e);
		}
	}
	
	
	
	
	
	
	public void importPortaApplicativa(ArchivePortaApplicativa archivePortaApplicativa,
			ArchiveEsitoImportDetail detail){
		
		IDPortaApplicativa idPortaApplicativa = archivePortaApplicativa.getIdPortaApplicativa();
		IDPortaApplicativaByNome idPortaApplicativaByNome = archivePortaApplicativa.getIdPortaApplicativaByNome();
		IDSoggetto idSoggettoProprietario = archivePortaApplicativa.getIdSoggettoProprietario();
		try{
			// --- check esistenza ---
			if(this.updateAbilitato==false){
				if(this.importerEngine.existsPortaApplicativa(idPortaApplicativaByNome.getNome(),idSoggettoProprietario)){
					detail.setState(ArchiveStatoImport.UPDATE_NOT_PERMISSED);
					return;
				}
			}
			
			
			// --- lettura elementi riferiti all'interno della porta delegata ---
			PortaApplicativa pa = archivePortaApplicativa.getPortaApplicativa();
			IDServizio idServizio = null;
			IDSoggetto idSoggettoErogatore = idSoggettoProprietario;
			if(pa.getSoggettoVirtuale()!=null &&
					pa.getSoggettoVirtuale().getTipo()!=null &&
					pa.getSoggettoVirtuale().getNome()!=null ){
				idSoggettoErogatore = new IDSoggetto(pa.getSoggettoVirtuale().getTipo(), pa.getSoggettoVirtuale().getNome());
			}
			if(idSoggettoErogatore!=null){
				if(pa.getServizio()!=null &&
						pa.getServizio().getTipo()!=null &&
						pa.getServizio().getNome()!=null  ){
					idServizio = new IDServizio(idSoggettoErogatore, 
							pa.getServizio().getTipo(), pa.getServizio().getNome());
				}
			}
			
			
			// --- check elementi riferiti ---
			if(this.importerEngine.existsSoggettoConfigurazione(idSoggettoProprietario) == false ){
				throw new Exception("Soggetto proprietario ["+idSoggettoProprietario+"] non esistente");
			}
			if(idSoggettoErogatore!=null){
				if(this.importerEngine.existsSoggettoRegistro(idSoggettoErogatore) == false ){
					throw new Exception("Soggetto erogatore riferito nella PA ["+idSoggettoErogatore+"] non esistente");
				}
			}
			if(idServizio!=null){
				if(this.importerEngine.existsAccordoServizioParteSpecifica(idServizio) == false ){
					throw new Exception("Servizio riferito nella PA ["+idServizio+"] non esistente");
				}
			}
			
			
			// --- compatibilita' elementi riferiti ---
			String protocolloAssociatoSoggettoProprietario = this.protocolFactoryManager.getProtocolBySubjectType(idSoggettoProprietario.getTipo());
			// erogatore
			String protocolloAssociatoSoggettoErogatore = this.protocolFactoryManager.getProtocolBySubjectType(idSoggettoErogatore.getTipo());
			if(protocolloAssociatoSoggettoProprietario.equals(protocolloAssociatoSoggettoErogatore)==false){
				throw new Exception("SoggettoErogatore ["+idSoggettoErogatore+"] (protocollo:"+protocolloAssociatoSoggettoErogatore+
						") non utilizzabile in una porta applicativa appartenete al soggetto ["+idSoggettoProprietario+"] (protocollo:"+protocolloAssociatoSoggettoProprietario+")");
			}
			// accordo di servizio parte specifica
			String protocolloAssociatoAccordoParteSpecifica = this.protocolFactoryManager.getProtocolByServiceType(idServizio.getTipoServizio());
			if(protocolloAssociatoSoggettoProprietario.equals(protocolloAssociatoAccordoParteSpecifica)==false){
				throw new Exception("AccordoServizioParteSpecifica ["+idServizio+"] (protocollo:"+protocolloAssociatoAccordoParteSpecifica+
						") con servizio non utilizzabile in una porta applicativa appartenete al soggetto ["+idSoggettoProprietario+"] (protocollo:"+protocolloAssociatoSoggettoProprietario+")");
			}
			String protocolloAssociatoSoggettoAccordoParteSpecifica = this.protocolFactoryManager.getProtocolBySubjectType(idServizio.getSoggettoErogatore().getTipo());
			if(protocolloAssociatoSoggettoProprietario.equals(protocolloAssociatoSoggettoAccordoParteSpecifica)==false){
				throw new Exception("AccordoServizioParteSpecifica ["+idServizio+"] (protocollo:"+protocolloAssociatoSoggettoAccordoParteSpecifica+
						") con soggetto non utilizzabile in una porta applicativa appartenete al soggetto ["+idSoggettoProprietario+"] (protocollo:"+protocolloAssociatoSoggettoProprietario+")");
			}
			
			
			// ---- visibilita' oggetto riferiti ---
			org.openspcoop2.core.config.Soggetto soggetto = this.importerEngine.getSoggettoConfigurazione(idSoggettoProprietario);
			if(this.importerEngine.isVisioneOggettiGlobale(this.userLogin)==false){
				if(this.userLogin.equals(soggetto.getSuperUser())==false){
					throw new Exception("Il soggetto proprietario ["+idSoggettoProprietario+"] non è visibile/utilizzabile dall'utente collegato ("+this.userLogin+")");
				}
			}
			Soggetto soggettoErogatore = null;
			AccordoServizioParteSpecifica accordoServizioParteSpecifica = null;
			if(idSoggettoErogatore!=null){
				soggettoErogatore = this.importerEngine.getSoggettoRegistro(idSoggettoErogatore);
				if(this.importerEngine.isVisioneOggettiGlobale(this.userLogin)==false){
					if(this.userLogin.equals(soggettoErogatore.getSuperUser())==false){
						throw new Exception("Il soggetto erogatore riferito nella PA ["+idSoggettoErogatore+"] non è visibile/utilizzabile dall'utente collegato ("+this.userLogin+")");
					}
				}
			}
			if(idServizio!=null){
				accordoServizioParteSpecifica = this.importerEngine.getAccordoServizioParteSpecifica(idServizio);
				if(this.importerEngine.isVisioneOggettiGlobale(this.userLogin)==false){
					if(this.userLogin.equals(accordoServizioParteSpecifica.getSuperUser())==false){
						throw new Exception("Il servizio riferito nella PA ["+idServizio+"] non è visibile/utilizzabile dall'utente collegato ("+this.userLogin+")");
					}
				}
			}
			
			
			// --- set dati obbligatori nel db ----
			
			//pa.setSuperUser(this.userLogin);
			// L'oggetto non contiene informazione di visibilita, la eredita dal soggetto
			
			if(pa.getSoggettoVirtuale()!=null){
				pa.getSoggettoVirtuale().setId(soggettoErogatore.getId());
			}
			if(idServizio!=null){
				pa.getServizio().setId(accordoServizioParteSpecifica.getId());
			}
			
			
			
			// --- ora registrazione
			pa.setOraRegistrazione(DateManager.getDate());
			
			
			// --- upload ---
			if(this.importerEngine.existsPortaApplicativa(idPortaApplicativaByNome.getNome(),idSoggettoProprietario)){
				
				org.openspcoop2.core.config.PortaApplicativa old = this.importerEngine.getPortaApplicativa(idPortaApplicativaByNome.getNome(),idSoggettoProprietario);
				pa.setId(old.getId());
				pa.setOldNomeForUpdate(old.getNome());
				pa.setOldTipoSoggettoProprietarioForUpdate(old.getTipoSoggettoProprietario());
				pa.setOldNomeSoggettoProprietarioForUpdate(old.getNomeSoggettoProprietario());
				org.openspcoop2.core.config.driver.utils.XMLDataConverter.
					impostaInformazioniConfigurazione_PortaApplicativa(pa);
				
				// visibilita' oggetto stesso per update
				// L'oggetto non contiene informazione di visibilita, la eredita dal soggetto

				// update
				this.importerEngine.updatePortaApplicativa(pa);
				detail.setState(ArchiveStatoImport.UPDATED);
			}
			// --- create ---
			else{
				this.importerEngine.createPortaApplicativa(pa);
				detail.setState(ArchiveStatoImport.CREATED);
			}
				

			
		}			
		catch(Exception e){
			this.log.error("Errore durante l'import della porta applicativa ["+idPortaApplicativa+"]: "+e.getMessage(),e);
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
