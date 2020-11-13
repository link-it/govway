/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it). 
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

package org.openspcoop2.protocol.engine.archive;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.openspcoop2.protocol.engine.utils.DBOggettiInUsoUtils;
import org.openspcoop2.core.commons.ErrorsHandlerCostant;
import org.openspcoop2.core.config.Configurazione;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDAccordoCooperazione;
import org.openspcoop2.core.id.IDGenericProperties;
import org.openspcoop2.core.id.IDGruppo;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDRuolo;
import org.openspcoop2.core.id.IDScope;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.mapping.MappingErogazionePortaApplicativa;
import org.openspcoop2.core.mapping.MappingFruizionePortaDelegata;
import org.openspcoop2.core.registry.AccordoCooperazione;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Fruitore;
import org.openspcoop2.core.registry.Gruppo;
import org.openspcoop2.core.registry.PortaDominio;
import org.openspcoop2.core.registry.Ruolo;
import org.openspcoop2.core.registry.Scope;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.core.registry.constants.PddTipologia;
import org.openspcoop2.protocol.sdk.archive.Archive;
import org.openspcoop2.protocol.sdk.archive.ArchiveAccordoCooperazione;
import org.openspcoop2.protocol.sdk.archive.ArchiveAccordoServizioComposto;
import org.openspcoop2.protocol.sdk.archive.ArchiveAccordoServizioParteComune;
import org.openspcoop2.protocol.sdk.archive.ArchiveAccordoServizioParteSpecifica;
import org.openspcoop2.protocol.sdk.archive.ArchiveActivePolicy;
import org.openspcoop2.protocol.sdk.archive.ArchiveConfigurationPolicy;
import org.openspcoop2.protocol.sdk.archive.ArchiveEsitoDelete;
import org.openspcoop2.protocol.sdk.archive.ArchiveEsitoImportDetail;
import org.openspcoop2.protocol.sdk.archive.ArchiveEsitoImportDetailConfigurazione;
import org.openspcoop2.protocol.sdk.archive.ArchiveFruitore;
import org.openspcoop2.protocol.sdk.archive.ArchiveGruppo;
import org.openspcoop2.protocol.sdk.archive.ArchiveMappingErogazione;
import org.openspcoop2.protocol.sdk.archive.ArchiveMappingFruizione;
import org.openspcoop2.protocol.sdk.archive.ArchivePdd;
import org.openspcoop2.protocol.sdk.archive.ArchivePortaApplicativa;
import org.openspcoop2.protocol.sdk.archive.ArchivePortaDelegata;
import org.openspcoop2.protocol.sdk.archive.ArchiveRuolo;
import org.openspcoop2.protocol.sdk.archive.ArchiveScope;
import org.openspcoop2.protocol.sdk.archive.ArchiveServizioApplicativo;
import org.openspcoop2.protocol.sdk.archive.ArchiveSoggetto;
import org.openspcoop2.protocol.sdk.archive.ArchiveTokenPolicy;
import org.openspcoop2.protocol.sdk.constants.ArchiveStatoImport;
import org.slf4j.Logger;

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
	private boolean deletePolicyConfigurazione;
	
	public DeleterArchiveUtils(AbstractArchiveEngine importerEngine,Logger log,
			String userLogin,
			boolean deletePolicyConfigurazione) throws Exception{
		this.importerEngine = importerEngine;
		this.log = log;
		this.userLogin = userLogin;
		this.deletePolicyConfigurazione = deletePolicyConfigurazione;
	}
	
	private static String NEW_LINE = "\n\t\t";
	private static boolean NORMALIZE_OBJECT_ID_MESSAGGIO_IN_USO = false;
	
	
	public ArchiveEsitoDelete deleteArchive(Archive archive, String userLogin) throws Exception,ImportInformationMissingException{
		try{
			
			ArchiveEsitoDelete esito = new ArchiveEsitoDelete();
			
			
			
			// Preparo Liste di Mapping da creare una volta registrati sia gli accordi (servizi e fruitori) che le porte (delegate e applicative)
			List<MappingErogazionePortaApplicativa> listMappingErogazionePA = new ArrayList<MappingErogazionePortaApplicativa>();
			List<MappingFruizionePortaDelegata> listMappingFruizionePD = new ArrayList<MappingFruizionePortaDelegata>();
			
			// Servizi
			for (int i = 0; i < archive.getAccordiServizioParteSpecifica().size(); i++) {
				ArchiveAccordoServizioParteSpecifica archiveAccordoServizioParteSpecifica = archive.getAccordiServizioParteSpecifica().get(i);
				archiveAccordoServizioParteSpecifica.update();
				
				// gestione portaApplicativaAssociata
				if(archiveAccordoServizioParteSpecifica.getMappingPorteApplicativeAssociate()!=null &&
						archiveAccordoServizioParteSpecifica.getMappingPorteApplicativeAssociate().size()>0){
					List<IDPortaApplicativa> idPACheck = null;
					if(this.importerEngine.existsAccordoServizioParteSpecifica(archiveAccordoServizioParteSpecifica.getIdAccordoServizioParteSpecifica()) && 
							this.importerEngine.existsIDPorteApplicativeAssociateErogazione(archiveAccordoServizioParteSpecifica.getIdAccordoServizioParteSpecifica())) {
						idPACheck = this.importerEngine.getIDPorteApplicativeAssociateErogazione(archiveAccordoServizioParteSpecifica.getIdAccordoServizioParteSpecifica());
					}
					for (MappingErogazionePortaApplicativa mappingPorteApplicativeAssociata : archiveAccordoServizioParteSpecifica.getMappingPorteApplicativeAssociate()) {
						if(idPACheck!=null && idPACheck.size()>0 && idPACheck.contains(mappingPorteApplicativeAssociata.getIdPortaApplicativa())){
							listMappingErogazionePA.add(mappingPorteApplicativeAssociata);	
						}	
					}
				}
			}
			
			// Fruitori
			for (int i = 0; i < archive.getAccordiFruitori().size(); i++) {
				ArchiveFruitore archiveFruitore = archive.getAccordiFruitori().get(i);
				archiveFruitore.update();
			
				// gestione portaDelegataAssociata
				if(archiveFruitore.getMappingPorteDelegateAssociate()!=null &&
						archiveFruitore.getMappingPorteDelegateAssociate().size()>0){
					List<IDPortaDelegata> idPDCheck = null;
					if(this.importerEngine.existsAccordoServizioParteSpecifica(archiveFruitore.getIdAccordoServizioParteSpecifica()) &&
							this.importerEngine.existsIDPorteDelegateAssociateFruizione(archiveFruitore.getIdAccordoServizioParteSpecifica(), archiveFruitore.getIdSoggettoFruitore())) {
						idPDCheck = this.importerEngine.getIDPorteDelegateAssociateFruizione(archiveFruitore.getIdAccordoServizioParteSpecifica(), archiveFruitore.getIdSoggettoFruitore());
					}
					for (MappingFruizionePortaDelegata mappingPorteDelegateAssociata : archiveFruitore.getMappingPorteDelegateAssociate()) {
						if(idPDCheck!=null && idPDCheck.size()>0 && idPDCheck.contains(mappingPorteDelegateAssociata.getIdPortaDelegata())){
							listMappingFruizionePD.add(mappingPorteDelegateAssociata);
						}
					}
				}
			}
			
			// Mapping Erogazione - PA  (eventuali errori non provocano il fallimento del loader, 
			// cmq sia il mapping viene eliminato dopo quando si procede all'eliminazione puntuale degli oggetti)
			if(listMappingErogazionePA.size()>0){
				for (int i = 0; i < listMappingErogazionePA.size(); i++) {
					MappingErogazionePortaApplicativa mapping = listMappingErogazionePA.get(i);
					ArchiveMappingErogazione archiveMappingErogazione = new ArchiveMappingErogazione(mapping);
					ArchiveEsitoImportDetail detail = new ArchiveEsitoImportDetail(archiveMappingErogazione);
					try{
						if(this.importerEngine.existsMappingErogazione(mapping.getIdServizio(), mapping.getIdPortaApplicativa())) {
							this.importerEngine.deleteMappingErogazione(mapping.getIdServizio(), mapping.getIdPortaApplicativa());
							detail.setState(ArchiveStatoImport.DELETED);
						}
						else {
							detail.setState(ArchiveStatoImport.DELETED_NOT_EXISTS);
						}
					}catch(Exception e){
						this.log.error("Errore durante l'eliminazione del mapping di erogazione del servizio ["+mapping.getIdServizio()+
								"] verso la porta applicativa ["+mapping.getIdPortaApplicativa().getNome()+"]: "+e.getMessage(),e);
						detail.setState(ArchiveStatoImport.ERROR);
						detail.setException(e);
					}
					esito.getMappingErogazioni().add(detail);
				}
			}
			
			// Mapping Fruizione - PD  (eventuali errori non provocano il fallimento del loader, 
			// cmq sia il mapping viene eliminato dopo quando si procede all'eliminazione puntuale degli oggetti)
			if(listMappingFruizionePD.size()>0){
				for (int i = 0; i < listMappingFruizionePD.size(); i++) {
					MappingFruizionePortaDelegata mapping = listMappingFruizionePD.get(i);
					ArchiveMappingFruizione archiveMappingFruizione = new ArchiveMappingFruizione(mapping);
					ArchiveEsitoImportDetail detail = new ArchiveEsitoImportDetail(archiveMappingFruizione);
					try{
						if(this.importerEngine.existsMappingFruizione(mapping.getIdServizio(), mapping.getIdFruitore(), mapping.getIdPortaDelegata())) {
							this.importerEngine.deleteMappingFruizione(mapping.getIdServizio(), mapping.getIdFruitore(), mapping.getIdPortaDelegata());	
							detail.setState(ArchiveStatoImport.DELETED);
						}
						else {
							detail.setState(ArchiveStatoImport.DELETED_NOT_EXISTS);
						}
					}catch(Exception e){
						this.log.error("Errore durante l'eliminazione del mapping di fruizione del servizio ["+mapping.getIdServizio()+
								"] verso la porta delegata ["+mapping.getIdPortaDelegata().getNome()+"] da parte del soggetto ["+mapping.getIdFruitore()+"]: "+e.getMessage(),e);
						detail.setState(ArchiveStatoImport.ERROR);
						detail.setException(e);
					}
					esito.getMappingFruizioni().add(detail);
				}
			}
			

			
			
			// PorteApplicative
			for (int i = 0; i < archive.getPorteApplicative().size(); i++) {
				ArchivePortaApplicativa archivePortaApplicativa = archive.getPorteApplicative().get(i);
				ArchiveEsitoImportDetail detail = new ArchiveEsitoImportDetail(archivePortaApplicativa);
				try{
					archivePortaApplicativa.update();
					if(archivePortaApplicativa.getIdServizio()!=null){
						try{
							if(this.importerEngine.existsMappingErogazione(archivePortaApplicativa.getIdServizio(), archivePortaApplicativa.getIdPortaApplicativa())){
								this.importerEngine.deleteMappingErogazione(archivePortaApplicativa.getIdServizio(), archivePortaApplicativa.getIdPortaApplicativa());
							}
						}catch(Exception e){
							this.log.debug("Errore durante l'eliminazione del mapping di erogazione del servizio ["+archivePortaApplicativa.getIdServizio()+
									"] verso la porta applicativa ["+archivePortaApplicativa.getIdPortaApplicativa()+"]: "+e.getMessage(),e);
						}
					}
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
					if(archivePortaDelegata.getIdSoggettoProprietario()!=null){
							
						PortaDelegata pd = archivePortaDelegata.getPortaDelegata();
						if(pd.getSoggettoErogatore()!=null && pd.getSoggettoErogatore().getNome()!=null &&
								pd.getServizio()!=null && pd.getServizio().getNome()!=null){
							IDServizio idServizio = IDServizioFactory.getInstance().getIDServizioFromValues(pd.getServizio().getTipo(), pd.getServizio().getNome(), 
									new IDSoggetto(pd.getSoggettoErogatore().getTipo(), pd.getSoggettoErogatore().getNome()), 
									pd.getServizio().getVersione()); 
							try{
								if(this.importerEngine.existsMappingFruizione(idServizio, archivePortaDelegata.getIdSoggettoProprietario(), archivePortaDelegata.getIdPortaDelegata())){
									this.importerEngine.deleteMappingFruizione(idServizio, archivePortaDelegata.getIdSoggettoProprietario(), archivePortaDelegata.getIdPortaDelegata());
								}
							}catch(Exception e){
								this.log.debug("Errore durante l'eliminazione del mapping di fruizione del servizio ["+idServizio+
										"] verso la porta delegata ["+archivePortaDelegata.getIdPortaDelegata()+"] da parte del soggetto ["+archivePortaDelegata.getIdSoggettoProprietario()+"]: "+e.getMessage(),e);
							}
						}
					}
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
					//archiveFruitore.update(); effettuato durante la preparazione del mapping Fruizione - PD
					if(archiveFruitore.getIdAccordoServizioParteSpecifica()!=null){
						if(this.importerEngine.existsIDPorteDelegateAssociateFruizione(archiveFruitore.getIdAccordoServizioParteSpecifica(), archiveFruitore.getIdSoggettoFruitore())){
							this.importerEngine.deleteMappingFruizione(archiveFruitore.getIdAccordoServizioParteSpecifica(), archiveFruitore.getIdSoggettoFruitore());
						}
					}
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
				//archiveAccordoServizioParteSpecifica.update();  effettuato durante la preparazione del mapping Erogazione - PA
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
					if(archiveAccordoServizioParteSpecifica.getIdAccordoServizioParteSpecifica()!=null){
						if(this.importerEngine.existsIDPorteApplicativeAssociateErogazione(archiveAccordoServizioParteSpecifica.getIdAccordoServizioParteSpecifica())){
							this.importerEngine.deleteMappingErogazione(archiveAccordoServizioParteSpecifica.getIdAccordoServizioParteSpecifica());
						}
					}
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
					if(archiveAccordoServizioParteSpecifica.getIdAccordoServizioParteSpecifica()!=null){
						if(this.importerEngine.existsIDPorteApplicativeAssociateErogazione(archiveAccordoServizioParteSpecifica.getIdAccordoServizioParteSpecifica())){
							this.importerEngine.deleteMappingErogazione(archiveAccordoServizioParteSpecifica.getIdAccordoServizioParteSpecifica());
						}
					}
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
			
			// Gruppi
			for (int i = 0; i < archive.getGruppi().size(); i++) {
				ArchiveGruppo archiveGruppo = archive.getGruppi().get(i);
				ArchiveEsitoImportDetail detail = new ArchiveEsitoImportDetail(archiveGruppo);
				try{
					this.deleteGruppo(archiveGruppo, detail);
				}catch(Exception e){
					detail.setState(ArchiveStatoImport.ERROR);
					detail.setException(e);
				}
				esito.getGruppi().add(detail);
			}
			
			// Ruoli
			for (int i = 0; i < archive.getRuoli().size(); i++) {
				ArchiveRuolo archiveRuolo = archive.getRuoli().get(i);
				ArchiveEsitoImportDetail detail = new ArchiveEsitoImportDetail(archiveRuolo);
				try{
					this.deleteRuolo(archiveRuolo, detail);
				}catch(Exception e){
					detail.setState(ArchiveStatoImport.ERROR);
					detail.setException(e);
				}
				esito.getRuoli().add(detail);
			}
			
			// Scope
			for (int i = 0; i < archive.getScope().size(); i++) {
				ArchiveScope archiveScope = archive.getScope().get(i);
				ArchiveEsitoImportDetail detail = new ArchiveEsitoImportDetail(archiveScope);
				try{
					this.deleteScope(archiveScope, detail);
				}catch(Exception e){
					detail.setState(ArchiveStatoImport.ERROR);
					detail.setException(e);
				}
				esito.getScope().add(detail);
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
			
			
			// ControlloTraffico (AttivazionePolicy)
			for (int i = 0; i < archive.getControlloTraffico_activePolicies().size(); i++) {
				ArchiveActivePolicy archivePolicy = archive.getControlloTraffico_activePolicies().get(i);
				ArchiveEsitoImportDetail detail = new ArchiveEsitoImportDetail(archivePolicy);
				try{
					this.deleteActivePolicy(archivePolicy, detail);
				}catch(Exception e){
					detail.setState(ArchiveStatoImport.ERROR);
					detail.setException(e);
				}
				esito.getControlloTraffico_activePolicies().add(detail);
			}
			
			// ControlloTraffico (ConfigurazionePolicy)
			for (int i = 0; i < archive.getControlloTraffico_configurationPolicies().size(); i++) {
				ArchiveConfigurationPolicy archivePolicy = archive.getControlloTraffico_configurationPolicies().get(i);
				ArchiveEsitoImportDetail detail = new ArchiveEsitoImportDetail(archivePolicy);
				try{
					this.deleteConfigPolicy(archivePolicy, detail);
				}catch(Exception e){
					detail.setState(ArchiveStatoImport.ERROR);
					detail.setException(e);
				}
				esito.getControlloTraffico_configurationPolicies().add(detail);
			}
			
			
			// TokenPolicy (Validation)
			for (int i = 0; i < archive.getToken_validation_policies().size(); i++) {
				ArchiveTokenPolicy archivePolicy = archive.getToken_validation_policies().get(i);
				ArchiveEsitoImportDetail detail = new ArchiveEsitoImportDetail(archivePolicy);
				try{
					this.deleteTokenPolicy(archivePolicy, detail);
				}catch(Exception e){
					detail.setState(ArchiveStatoImport.ERROR);
					detail.setException(e);
				}
				esito.getToken_validation_policies().add(detail);
			}
			
			// TokenPolicy (Retrieve)
			for (int i = 0; i < archive.getToken_retrieve_policies().size(); i++) {
				ArchiveTokenPolicy archivePolicy = archive.getToken_retrieve_policies().get(i);
				ArchiveEsitoImportDetail detail = new ArchiveEsitoImportDetail(archivePolicy);
				try{
					this.deleteTokenPolicy(archivePolicy, detail);
				}catch(Exception e){
					detail.setState(ArchiveStatoImport.ERROR);
					detail.setException(e);
				}
				esito.getToken_retrieve_policies().add(detail);
			}
			
			
			// Configurazione (Gestisco solamente informazioni extended, in modo da chiamare il driver con la DELETE)
			if(archive.getConfigurazionePdD()!=null && archive.getConfigurazionePdD().sizeExtendedInfoList()>0) {
				Configurazione newConfig = new Configurazione();
				newConfig.getExtendedInfoList().addAll(archive.getConfigurazionePdD().getExtendedInfoList());
				ArchiveEsitoImportDetailConfigurazione<Configurazione> detail = new ArchiveEsitoImportDetailConfigurazione<Configurazione>(newConfig);
				try{
					this.deleteConfigurazione(newConfig, detail);
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
	
	
	public void deleteConfigurazione(Configurazione config, ArchiveEsitoImportDetailConfigurazione<Configurazione> detail){
		
		try{
						
			// --- delete ---
			this.importerEngine.deleteConfigurazione(config);
			detail.setState(ArchiveStatoImport.DELETED);				

						
		}			
		catch(Exception e){
			this.log.error("Errore durante l'eliminazione della configurazione: "+e.getMessage(),e);
			detail.setState(ArchiveStatoImport.ERROR);
			detail.setException(e);
		}
	}
	
	
	public void deleteConfigPolicy(ArchiveConfigurationPolicy archivePolicy,ArchiveEsitoImportDetail detail){
		
		String nomePolicy = archivePolicy.getNomePolicy();
		try{
			
			// --- check abilitazione ---
			if(this.deletePolicyConfigurazione==false){
				detail.setState(ArchiveStatoImport.DELETED_POLICY_CONFIG_NOT_ENABLED);
				return;
			}
			
			
			// --- check esistenza ---
			if(this.importerEngine.existsControlloTraffico_configurationPolicy(nomePolicy)==false){
				detail.setState(ArchiveStatoImport.DELETED_NOT_EXISTS);
				return;
			}
			
			
			// ---- visibilita' oggetto che si vuole eliminare ---
			
			// non esistenti
			
			
			// ---- controllo di utilizzo dell'oggetto tramite altri oggetti ---
			
			List<String> whereIsInUso = new ArrayList<String>();
			if (this.importerEngine.isControlloTraffico_configurationPolicyInUso(nomePolicy, whereIsInUso)) {
				throw new Exception(NEW_LINE+DBOggettiInUsoUtils.toString(nomePolicy, whereIsInUso,false,NEW_LINE));
			}
			
			
			// --- delete ---
			this.importerEngine.deleteControlloTraffico_configurationPolicy(archivePolicy.getPolicy());
			detail.setState(ArchiveStatoImport.DELETED);				

						
		}			
		catch(Exception e){
			this.log.error("Errore durante l'eliminazione della configurazione della policy ["+nomePolicy+"]: "+e.getMessage(),e);
			detail.setState(ArchiveStatoImport.ERROR);
			detail.setException(e);
		}
	}
	
	
	public void deleteActivePolicy(ArchiveActivePolicy archivePolicy,ArchiveEsitoImportDetail detail){
		
		String nomePolicy = archivePolicy.getNomePolicy();
		try{
			
			boolean policyGlobale = archivePolicy.getPolicy().getFiltro()==null || archivePolicy.getPolicy().getFiltro().getNomePorta()==null || "".equals(archivePolicy.getPolicy().getFiltro().getNomePorta());
			
			// --- check abilitazione ---
			if(policyGlobale && this.deletePolicyConfigurazione==false){ // se non e' globale la policy di attivazione va eliminata sempre poiche' associata all'erogazione o alla fruizione
				detail.setState(ArchiveStatoImport.DELETED_POLICY_CONFIG_NOT_ENABLED);
				return;
			}
			
			
			// --- check esistenza ---
			if(this.importerEngine.existsControlloTraffico_activePolicy(nomePolicy)==false){
				detail.setState(ArchiveStatoImport.DELETED_NOT_EXISTS);
				return;
			}
			
			
			// ---- visibilita' oggetto che si vuole eliminare ---
			
			// non esistenti
			
			
			// ---- controllo di utilizzo dell'oggetto tramite altri oggetti ---
			
			List<String> whereIsInUso = new ArrayList<String>();
			if (this.importerEngine.isControlloTraffico_activePolicyInUso(nomePolicy, whereIsInUso)) {
				throw new Exception(NEW_LINE+DBOggettiInUsoUtils.toString(nomePolicy, whereIsInUso,false,NEW_LINE));
			}
			
			
			// --- delete ---
			this.importerEngine.deleteControlloTraffico_activePolicy(archivePolicy.getPolicy());
			detail.setState(ArchiveStatoImport.DELETED);				

						
		}			
		catch(Exception e){
			this.log.error("Errore durante l'eliminazione dell'attivazione della policy ["+nomePolicy+"]: "+e.getMessage(),e);
			detail.setState(ArchiveStatoImport.ERROR);
			detail.setException(e);
		}
	}
	
	
	public void deleteTokenPolicy(ArchiveTokenPolicy archivePolicy,ArchiveEsitoImportDetail detail){
		
		String nomePolicy = archivePolicy.getNomePolicy();
		String tipologiaPolicy = archivePolicy.getTipologiaPolicy();
		try{
			
			// --- check abilitazione ---
			if(this.deletePolicyConfigurazione==false){
				detail.setState(ArchiveStatoImport.DELETED_POLICY_CONFIG_NOT_ENABLED);
				return;
			}
			
			
			// --- check esistenza ---
			if(this.importerEngine.existsGenericProperties(tipologiaPolicy, nomePolicy)==false){
				detail.setState(ArchiveStatoImport.DELETED_NOT_EXISTS);
				return;
			}
			
			
			// ---- visibilita' oggetto che si vuole eliminare ---
			
			// non esistenti
			
			
			// ---- controllo di utilizzo dell'oggetto tramite altri oggetti ---
			
			HashMap<ErrorsHandlerCostant, List<String>> whereIsInUso = new HashMap<ErrorsHandlerCostant, List<String>>();
			if (this.importerEngine.isGenericPropertiesInUso(tipologiaPolicy,  nomePolicy, whereIsInUso, NORMALIZE_OBJECT_ID_MESSAGGIO_IN_USO)) {
				IDGenericProperties idGP = new IDGenericProperties();
				idGP.setNome(nomePolicy);
				idGP.setTipologia(tipologiaPolicy);
				throw new Exception(NEW_LINE+DBOggettiInUsoUtils.toString(idGP, whereIsInUso,false,NEW_LINE));
			}
			
			
			// --- delete ---
			this.importerEngine.deleteGenericProperties(archivePolicy.getPolicy());
			detail.setState(ArchiveStatoImport.DELETED);				

						
		}			
		catch(Exception e){
			this.log.error("Errore durante l'eliminazione della token policy ["+nomePolicy+"] (tipo: '"+tipologiaPolicy+"'): "+e.getMessage(),e);
			detail.setState(ArchiveStatoImport.ERROR);
			detail.setException(e);
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
			
			
			// ---- controllo non sia una pdd 'operativa' ---
			
			String tipoPdd = this.importerEngine.getTipoPortaDominio(nomePdd);
			if(!PddTipologia.ESTERNO.equals(tipoPdd)) {
				throw new Exception("Porta di Dominio ["+nomePdd+"] non è eliminabile poichè di dominio interno");
			}
						
			
			// ---- tipo di Pdd ---
			if(PddTipologia.OPERATIVO.toString().equals(this.importerEngine.getTipoPortaDominio(nomePdd))){
				throw new Exception("La Porta di Dominio ["+nomePdd+"] non è eliminabile essendo di tipo '"+PddTipologia.OPERATIVO.toString()+"'");
			}
			
			
			// ---- controllo di utilizzo dell'oggetto tramite altri oggetti ---
			
			List<String> whereIsInUso = new ArrayList<String>();
			if (this.importerEngine.isPddInUso(nomePdd, whereIsInUso, NORMALIZE_OBJECT_ID_MESSAGGIO_IN_USO)) {
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
	
	
	
	public void deleteGruppo(ArchiveGruppo archiveGruppo,ArchiveEsitoImportDetail detail){
		
		IDGruppo idGruppo = archiveGruppo.getIdGruppo();
		try{
			
			// --- check esistenza ---
			if(this.importerEngine.existsGruppo(idGruppo)==false){
				detail.setState(ArchiveStatoImport.DELETED_NOT_EXISTS);
				return;
			}
			
			
			// ---- visibilita' oggetto che si vuole eliminare ---
			
			// gruppo
			Gruppo gruppoReadFromDb = this.importerEngine.getGruppo(idGruppo);
			if(this.importerEngine.isVisioneOggettiGlobale(this.userLogin)==false){
				if(this.userLogin.equals(gruppoReadFromDb.getSuperUser())==false){
					throw new Exception("Il Gruppo ["+idGruppo+"] non è visibile/eliminabile dall'utente collegato ("+this.userLogin+")");
				}
			}
			
			
			// ---- controllo di utilizzo dell'oggetto tramite altri oggetti ---
			
			HashMap<ErrorsHandlerCostant, List<String>> whereIsInUso = new HashMap<ErrorsHandlerCostant, List<String>>();
			if (this.importerEngine.isGruppoInUso(idGruppo, whereIsInUso, NORMALIZE_OBJECT_ID_MESSAGGIO_IN_USO)) {
				throw new Exception(NEW_LINE+DBOggettiInUsoUtils.toString(idGruppo, whereIsInUso,false,NEW_LINE));
			}
			
			
			// --- delete ---
			this.importerEngine.deleteGruppo(archiveGruppo.getGruppo());
			detail.setState(ArchiveStatoImport.DELETED);				

						
		}			
		catch(Exception e){
			this.log.error("Errore durante l'eliminazione del gruppo ["+idGruppo+"]: "+e.getMessage(),e);
			detail.setState(ArchiveStatoImport.ERROR);
			detail.setException(e);
		}
	}
	
	
	
	public void deleteRuolo(ArchiveRuolo archiveRuolo,ArchiveEsitoImportDetail detail){
		
		IDRuolo idRuolo = archiveRuolo.getIdRuolo();
		try{
			
			// --- check esistenza ---
			if(this.importerEngine.existsRuolo(idRuolo)==false){
				detail.setState(ArchiveStatoImport.DELETED_NOT_EXISTS);
				return;
			}
			
			
			// ---- visibilita' oggetto che si vuole eliminare ---
			
			// ruolo
			Ruolo ruoloReadFromDb = this.importerEngine.getRuolo(idRuolo);
			if(this.importerEngine.isVisioneOggettiGlobale(this.userLogin)==false){
				if(this.userLogin.equals(ruoloReadFromDb.getSuperUser())==false){
					throw new Exception("Il Ruolo ["+idRuolo+"] non è visibile/eliminabile dall'utente collegato ("+this.userLogin+")");
				}
			}
			
			
			// ---- controllo di utilizzo dell'oggetto tramite altri oggetti ---
			
			HashMap<ErrorsHandlerCostant, List<String>> whereIsInUso = new HashMap<ErrorsHandlerCostant, List<String>>();
			if (this.importerEngine.isRuoloInUso(idRuolo, whereIsInUso, NORMALIZE_OBJECT_ID_MESSAGGIO_IN_USO)) {
				throw new Exception(NEW_LINE+DBOggettiInUsoUtils.toString(idRuolo, whereIsInUso,false,NEW_LINE));
			}
			
			
			// --- delete ---
			this.importerEngine.deleteRuolo(archiveRuolo.getRuolo());
			detail.setState(ArchiveStatoImport.DELETED);				

						
		}			
		catch(Exception e){
			this.log.error("Errore durante l'eliminazione del ruolo ["+idRuolo+"]: "+e.getMessage(),e);
			detail.setState(ArchiveStatoImport.ERROR);
			detail.setException(e);
		}
	}
	
	
	
	public void deleteScope(ArchiveScope archiveScope,ArchiveEsitoImportDetail detail){
		
		IDScope idScope = archiveScope.getIdScope();
		try{
			
			// --- check esistenza ---
			if(this.importerEngine.existsScope(idScope)==false){
				detail.setState(ArchiveStatoImport.DELETED_NOT_EXISTS);
				return;
			}
			
			
			// ---- visibilita' oggetto che si vuole eliminare ---
			
			// scope
			Scope scopeReadFromDb = this.importerEngine.getScope(idScope);
			if(this.importerEngine.isVisioneOggettiGlobale(this.userLogin)==false){
				if(this.userLogin.equals(scopeReadFromDb.getSuperUser())==false){
					throw new Exception("Lo Scope ["+idScope+"] non è visibile/eliminabile dall'utente collegato ("+this.userLogin+")");
				}
			}
			
			
			// ---- controllo di utilizzo dell'oggetto tramite altri oggetti ---
			
			HashMap<ErrorsHandlerCostant, List<String>> whereIsInUso = new HashMap<ErrorsHandlerCostant, List<String>>();
			if (this.importerEngine.isScopeInUso(idScope, whereIsInUso, NORMALIZE_OBJECT_ID_MESSAGGIO_IN_USO)) {
				throw new Exception(NEW_LINE+DBOggettiInUsoUtils.toString(idScope, whereIsInUso,false,NEW_LINE));
			}
			
			
			// --- delete ---
			this.importerEngine.deleteScope(archiveScope.getScope());
			detail.setState(ArchiveStatoImport.DELETED);				

						
		}			
		catch(Exception e){
			this.log.error("Errore durante l'eliminazione dello scope ["+idScope+"]: "+e.getMessage(),e);
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
					if (this.importerEngine.isSoggettoRegistroInUso(idSoggetto, whereIsInUso, NORMALIZE_OBJECT_ID_MESSAGGIO_IN_USO)){
						throw new Exception(NEW_LINE+DBOggettiInUsoUtils.toString(idSoggetto, whereIsInUso,false,NEW_LINE, NORMALIZE_OBJECT_ID_MESSAGGIO_IN_USO));
					}
					
					// ---- controllo che il soggetto non sia un soggetto 'operativo' ---
					if(old.getPortaDominio()!=null){
						String tipoPdd = this.importerEngine.getTipoPortaDominio(old.getPortaDominio());
						if(!PddTipologia.ESTERNO.equals(tipoPdd)) {
							throw new Exception("Il soggetto non è eliminabile poichè di dominio interno");
						}
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
					if (this.importerEngine.isSoggettoConfigurazioneInUso(idSoggetto, whereIsInUso, NORMALIZE_OBJECT_ID_MESSAGGIO_IN_USO)){
						throw new Exception(NEW_LINE+DBOggettiInUsoUtils.toString(idSoggetto, whereIsInUso,false,NEW_LINE, NORMALIZE_OBJECT_ID_MESSAGGIO_IN_USO));
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
			if (this.importerEngine.isServizioApplicativoInUso(idServizioApplicativo, whereIsInUso, NORMALIZE_OBJECT_ID_MESSAGGIO_IN_USO)){
				throw new Exception(NEW_LINE+DBOggettiInUsoUtils.toString(idServizioApplicativo, whereIsInUso,false,NEW_LINE,NORMALIZE_OBJECT_ID_MESSAGGIO_IN_USO));
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
			if (this.importerEngine.isAccordoCooperazioneInUso(idAccordoCooperazione, whereIsInUso, NORMALIZE_OBJECT_ID_MESSAGGIO_IN_USO)){
				throw new Exception(NEW_LINE+DBOggettiInUsoUtils.toString(idAccordoCooperazione, whereIsInUso,false,NEW_LINE,NORMALIZE_OBJECT_ID_MESSAGGIO_IN_USO));
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
			if (this.importerEngine.isAccordoServizioParteComuneInUso(idAccordoServizioParteComune, whereIsInUso, NORMALIZE_OBJECT_ID_MESSAGGIO_IN_USO)){
				throw new Exception(NEW_LINE+DBOggettiInUsoUtils.toString(idAccordoServizioParteComune, whereIsInUso,false,NEW_LINE,NORMALIZE_OBJECT_ID_MESSAGGIO_IN_USO));
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
			if (this.importerEngine.isAccordoServizioParteComuneInUso(idAccordoServizioComposto, whereIsInUso, NORMALIZE_OBJECT_ID_MESSAGGIO_IN_USO)){
				throw new Exception(NEW_LINE+DBOggettiInUsoUtils.toString(idAccordoServizioComposto, whereIsInUso,false,NEW_LINE,NORMALIZE_OBJECT_ID_MESSAGGIO_IN_USO));
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
		
		IDServizio idAccordoServizioParteSpecifica = archiveAccordoServizioParteSpecifica.getIdAccordoServizioParteSpecifica();
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
			if (this.importerEngine.isAccordoServizioParteSpecificaInUso(archiveAccordoServizioParteSpecifica.getIdAccordoServizioParteSpecifica(), whereIsInUso, NORMALIZE_OBJECT_ID_MESSAGGIO_IN_USO)){
				throw new Exception(NEW_LINE+DBOggettiInUsoUtils.toString(archiveAccordoServizioParteSpecifica.getIdAccordoServizioParteSpecifica(), whereIsInUso,false,NEW_LINE,NORMALIZE_OBJECT_ID_MESSAGGIO_IN_USO,"Servizio"));
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
		
		IDServizio idAccordoServizioParteSpecifica = archiveFruitore.getIdAccordoServizioParteSpecifica();
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
						
			// prima ho rimosso il fruitore se gia' esisteva.
			// update
			IDServizio oldIDServizioForUpdate = IDServizioFactory.getInstance().getIDServizioFromValues(oldAccordo.getTipo(), oldAccordo.getNome(), 
					oldAccordo.getTipoSoggettoErogatore(),oldAccordo.getNomeSoggettoErogatore(),oldAccordo.getVersione());
			oldAccordo.setOldIDServizioForUpdate(oldIDServizioForUpdate);
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
		IDSoggetto idSoggettoProprietario = archivePortaApplicativa.getIdSoggettoProprietario();
		try{
			// --- check esistenza ---
			if(this.importerEngine.existsPortaApplicativa(idPortaApplicativa)==false){
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
	
	

}
