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

package org.openspcoop2.protocol.engine.archive;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.allarmi.Allarme;
import org.openspcoop2.core.allarmi.AllarmeParametro;
import org.openspcoop2.core.allarmi.constants.RuoloPorta;
import org.openspcoop2.core.allarmi.utils.AllarmiDriverUtils;
import org.openspcoop2.core.config.Configurazione;
import org.openspcoop2.core.config.ConfigurazioneUrlInvocazione;
import org.openspcoop2.core.config.ConfigurazioneUrlInvocazioneRegola;
import org.openspcoop2.core.config.Credenziali;
import org.openspcoop2.core.config.GenericProperties;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.Property;
import org.openspcoop2.core.config.RegistroPlugin;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.config.constants.TipoAutenticazione;
import org.openspcoop2.core.config.constants.TipoAutorizzazione;
import org.openspcoop2.core.config.constants.TipoBehaviour;
import org.openspcoop2.core.constants.CostantiConnettori;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.constants.CostantiLabel;
import org.openspcoop2.core.constants.TipiConnettore;
import org.openspcoop2.core.controllo_traffico.AttivazionePolicy;
import org.openspcoop2.core.controllo_traffico.ConfigurazionePolicy;
import org.openspcoop2.core.controllo_traffico.IdActivePolicy;
import org.openspcoop2.core.controllo_traffico.constants.RuoloPolicy;
import org.openspcoop2.core.controllo_traffico.constants.TipoFiltroApplicativo;
import org.openspcoop2.core.controllo_traffico.utils.ControlloTrafficoDriverUtils;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDAccordoCooperazione;
import org.openspcoop2.core.id.IDGruppo;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDRuolo;
import org.openspcoop2.core.id.IDScope;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.mapping.MappingErogazionePortaApplicativa;
import org.openspcoop2.core.mapping.MappingFruizionePortaDelegata;
import org.openspcoop2.core.plugins.IdPlugin;
import org.openspcoop2.core.plugins.Plugin;
import org.openspcoop2.core.plugins.constants.TipoPlugin;
import org.openspcoop2.core.registry.AccordoCooperazione;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteComuneServizioCompostoServizioComponente;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.ConfigurazioneServizioAzione;
import org.openspcoop2.core.registry.CredenzialiSoggetto;
import org.openspcoop2.core.registry.Fruitore;
import org.openspcoop2.core.registry.Operation;
import org.openspcoop2.core.registry.PortType;
import org.openspcoop2.core.registry.ProtocolProperty;
import org.openspcoop2.core.registry.Resource;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.core.registry.constants.PddTipologia;
import org.openspcoop2.core.registry.constants.ProprietariDocumento;
import org.openspcoop2.core.registry.constants.RuoliDocumento;
import org.openspcoop2.core.registry.constants.StatiAccordo;
import org.openspcoop2.core.registry.constants.TipologiaServizio;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziNotFound;
import org.openspcoop2.core.registry.driver.FiltroRicerca;
import org.openspcoop2.core.registry.driver.IDAccordoCooperazioneFactory;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.core.registry.driver.ValidazioneStatoPackageException;
import org.openspcoop2.core.registry.wsdl.RegistroOpenSPCoopUtilities;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.message.xml.MessageXMLUtils;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.manifest.constants.InterfaceType;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.archive.AbstractArchiveGenericProperties;
import org.openspcoop2.protocol.sdk.archive.Archive;
import org.openspcoop2.protocol.sdk.archive.ArchiveAccordoCooperazione;
import org.openspcoop2.protocol.sdk.archive.ArchiveAccordoServizioComposto;
import org.openspcoop2.protocol.sdk.archive.ArchiveAccordoServizioParteComune;
import org.openspcoop2.protocol.sdk.archive.ArchiveAccordoServizioParteSpecifica;
import org.openspcoop2.protocol.sdk.archive.ArchiveActivePolicy;
import org.openspcoop2.protocol.sdk.archive.ArchiveAllarme;
import org.openspcoop2.protocol.sdk.archive.ArchiveAttributeAuthority;
import org.openspcoop2.protocol.sdk.archive.ArchiveConfigurationPolicy;
import org.openspcoop2.protocol.sdk.archive.ArchiveEsitoImport;
import org.openspcoop2.protocol.sdk.archive.ArchiveEsitoImportDetail;
import org.openspcoop2.protocol.sdk.archive.ArchiveEsitoImportDetailConfigurazione;
import org.openspcoop2.protocol.sdk.archive.ArchiveFruitore;
import org.openspcoop2.protocol.sdk.archive.ArchiveGruppo;
import org.openspcoop2.protocol.sdk.archive.ArchiveMappingErogazione;
import org.openspcoop2.protocol.sdk.archive.ArchiveMappingFruizione;
import org.openspcoop2.protocol.sdk.archive.ArchivePdd;
import org.openspcoop2.protocol.sdk.archive.ArchivePluginArchivio;
import org.openspcoop2.protocol.sdk.archive.ArchivePluginClasse;
import org.openspcoop2.protocol.sdk.archive.ArchivePortaApplicativa;
import org.openspcoop2.protocol.sdk.archive.ArchivePortaDelegata;
import org.openspcoop2.protocol.sdk.archive.ArchiveRuolo;
import org.openspcoop2.protocol.sdk.archive.ArchiveScope;
import org.openspcoop2.protocol.sdk.archive.ArchiveServizioApplicativo;
import org.openspcoop2.protocol.sdk.archive.ArchiveSoggetto;
import org.openspcoop2.protocol.sdk.archive.ArchiveSortedMap;
import org.openspcoop2.protocol.sdk.archive.ArchiveTokenPolicy;
import org.openspcoop2.protocol.sdk.archive.ArchiveUrlInvocazioneRegola;
import org.openspcoop2.protocol.sdk.archive.IPositionArchiveObject;
import org.openspcoop2.protocol.sdk.config.ITraduttore;
import org.openspcoop2.protocol.sdk.constants.ArchiveStatoImport;
import org.openspcoop2.protocol.sdk.registry.RegistryException;
import org.openspcoop2.security.keystore.MerlinProvider;
import org.openspcoop2.utils.SortedMap;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.wsdl.DefinitionWrapper;
import org.slf4j.Logger;

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
	private boolean importPolicyConfigurazione;
	private boolean importPluginConfigurazione;
	private boolean checkExistsPluginConfigurazione;
	private boolean importConfigurazione;
	private String nomePddOperativa;
	private String tipoPddDefault;
	private ProtocolFactoryManager protocolFactoryManager;
	private IDAccordoCooperazioneFactory idAccordoCooperazioneFactory;
	private IDAccordoFactory idAccordoFactory;
	
	public ImporterArchiveUtils(AbstractArchiveEngine importerEngine,Logger log,
			String userLogin,String nomePddOperativa, String tipoPddDefault,
			boolean gestioneWorkflowStatiAccordi,
			boolean updateAbilitato,
			boolean importPolicyConfigurazione, 
			boolean importPluginConfigurazione, boolean checkExistsPluginConfigurazione,
			boolean importConfigurazione) throws Exception{
		this.importerEngine = importerEngine;
		this.log = log;
		this.userLogin = userLogin;
		this.gestioneWorkflowStatiAccordi = gestioneWorkflowStatiAccordi;
		this.updateAbilitato = updateAbilitato;
		this.importPolicyConfigurazione = importPolicyConfigurazione;
		this.importPluginConfigurazione = importPluginConfigurazione;
		this.checkExistsPluginConfigurazione = checkExistsPluginConfigurazione;
		this.importConfigurazione = importConfigurazione;
		if(nomePddOperativa!=null){
			this.nomePddOperativa = nomePddOperativa;
		}
		else{
			FiltroRicerca filtroRicerca = new FiltroRicerca();
			filtroRicerca.setTipo(PddTipologia.OPERATIVO.toString());
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
			this.tipoPddDefault = PddTipologia.ESTERNO.toString();
		}
		this.protocolFactoryManager = ProtocolFactoryManager.getInstance();
		this.idAccordoCooperazioneFactory = IDAccordoCooperazioneFactory.getInstance();
		this.idAccordoFactory = IDAccordoFactory.getInstance();
	}
	
	

	
	
	public ArchiveEsitoImport importArchive(Archive archive, String userLogin,
			boolean utilizzoAzioniDiretteInAccordoAbilitato,
			boolean isAbilitatoControlloUnicitaImplementazioneAccordoPerSoggetto,
			boolean isAbilitatoControlloUnicitaImplementazionePortTypePerSoggetto,
			boolean isSoggettiApplicativiCredenzialiBasicPermitSameCredentials,
			boolean isSoggettiApplicativiCredenzialiSslPermitSameCredentials,
			boolean isSoggettiApplicativiCredenzialiPrincipalPermitSameCredentials) throws Exception,ImportInformationMissingException{
		try{
			
			ArchiveEsitoImport esito = new ArchiveEsitoImport();
			
			// Soggetto Default
			Map<String, IDSoggetto> mapIdSoggettoDefault = new HashMap<>();
			Map<String, org.openspcoop2.core.registry.Soggetto> mapSoggettoDefault = new HashMap<>();
			Map<String, Boolean> mapAPIconReferente = new HashMap<>();
			if(this.protocolFactoryManager==null) {
				throw new Exception("ProtocolFactoryManager not initialized");
			}
			Enumeration<String> pEnum = this.protocolFactoryManager.getProtocolFactories().keys();
			if(pEnum!=null) {
				while (pEnum.hasMoreElements()) {
					String protocollo = (String) pEnum.nextElement();
					IDSoggetto soggettoDefaultProtocollo = this.importerEngine.getSoggettoDefault( protocollo );
					if(soggettoDefaultProtocollo==null) {
						throw new Exception("IDSoggetto di default non trovato per il protocollo '"+protocollo+"'");
					}
					mapIdSoggettoDefault.put(protocollo, soggettoDefaultProtocollo);
					
					org.openspcoop2.core.registry.Soggetto soggettoDefault = null;
					try {
						soggettoDefault = this.importerEngine.getSoggettoRegistro(soggettoDefaultProtocollo);
						if(soggettoDefault!=null) {
							mapSoggettoDefault.put(protocollo, soggettoDefault);
						}
					}catch( DriverRegistroServiziNotFound notFound) {
						// ignore
					}
					if(soggettoDefault==null) {
						throw new Exception("Soggetto di default non trovato per il protocollo '"+protocollo+"'");
					}
					
					IProtocolFactory<?> pf = this.protocolFactoryManager.getProtocolFactoryByName(protocollo);
					boolean supportoReferente = false;
					if(pf!=null) {
						supportoReferente = pf.createProtocolConfiguration().isSupportoSoggettoReferenteAccordiParteComune();
					}
					mapAPIconReferente.put(protocollo, supportoReferente);
				}
			}
			
			
			
			
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
			
			
			// Plugin (Archivi)
			// NOTA: devo riordinare per posizione, poi anche se la posizione verrà ricalcolata, verrà mantenuto l'ordine
			SortedMap<List<ArchivePluginArchivio>> map_plugin_archivi = reorderByPosition(archive.getPlugin_archivi());
			if(!map_plugin_archivi.isEmpty()) {
				for (String position : map_plugin_archivi.keys()) {
					List<ArchivePluginArchivio> archivePluginList = map_plugin_archivi.get(position);
					if(!archivePluginList.isEmpty()) {
						for (ArchivePluginArchivio archivePluginArchivio : archivePluginList) {
							ArchiveEsitoImportDetail detail = new ArchiveEsitoImportDetail(archivePluginArchivio);
							try{
								this.importPluginArchivio(archivePluginArchivio, detail);
							}catch(Exception e){
								detail.setState(ArchiveStatoImport.ERROR);
								detail.setException(e);
							}
							esito.getPlugin_archivi().add(detail);
						}
					}
				}
			}
			
			// Plugin (Classi)
			for (int i = 0; i < archive.getPlugin_classi().size(); i++) {
				ArchivePluginClasse archivePluginClasse = archive.getPlugin_classi().get(i);
				ArchiveEsitoImportDetail detail = new ArchiveEsitoImportDetail(archivePluginClasse);
				try{
					this.importPluginClasse(archivePluginClasse, detail);
				}catch(Exception e){
					detail.setState(ArchiveStatoImport.ERROR);
					detail.setException(e);
				}
				esito.getPlugin_classi().add(detail);
			}
			
			
			// Token Policy (Validation)
			for (int i = 0; i < archive.getToken_validation_policies().size(); i++) {
				ArchiveTokenPolicy archiveTokenPolicy = archive.getToken_validation_policies().get(i);
				ArchiveEsitoImportDetail detail = new ArchiveEsitoImportDetail(archiveTokenPolicy);
				try{
					this.importTokenPolicy(archiveTokenPolicy, detail);
				}catch(Exception e){
					detail.setState(ArchiveStatoImport.ERROR);
					detail.setException(e);
				}
				esito.getToken_validation_policies().add(detail);
			}
			
			// Token Policy (Retrieve)
			for (int i = 0; i < archive.getToken_retrieve_policies().size(); i++) {
				ArchiveTokenPolicy archiveTokenPolicy = archive.getToken_retrieve_policies().get(i);
				ArchiveEsitoImportDetail detail = new ArchiveEsitoImportDetail(archiveTokenPolicy);
				try{
					this.importTokenPolicy(archiveTokenPolicy, detail);
				}catch(Exception e){
					detail.setState(ArchiveStatoImport.ERROR);
					detail.setException(e);
				}
				esito.getToken_retrieve_policies().add(detail);
			}
			
			// Attribute Authority (Retrieve)
			for (int i = 0; i < archive.getAttributeAuthorities().size(); i++) {
				ArchiveAttributeAuthority archiveAttributeAuthority = archive.getAttributeAuthorities().get(i);
				ArchiveEsitoImportDetail detail = new ArchiveEsitoImportDetail(archiveAttributeAuthority);
				try{
					this.importAttributeAuthority(archiveAttributeAuthority, detail);
				}catch(Exception e){
					detail.setState(ArchiveStatoImport.ERROR);
					detail.setException(e);
				}
				esito.getAttributeAuthorities().add(detail);
			}
			
			
			// Gruppi
			for (int i = 0; i < archive.getGruppi().size(); i++) {
				ArchiveGruppo archiveGruppo = archive.getGruppi().get(i);
				ArchiveEsitoImportDetail detail = new ArchiveEsitoImportDetail(archiveGruppo);
				try{
					this.importGruppo(archiveGruppo, detail);
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
					this.importRuolo(archiveRuolo, detail);
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
					this.importScope(archiveScope, detail);
				}catch(Exception e){
					detail.setState(ArchiveStatoImport.ERROR);
					detail.setException(e);
				}
				esito.getScope().add(detail);
			}
			
			
			// Soggetti
			for (int i = 0; i < archive.getSoggetti().size(); i++) {
				ArchiveSoggetto archiveSoggetto = archive.getSoggetti().get(i);
				ArchiveEsitoImportDetail detail = new ArchiveEsitoImportDetail(archiveSoggetto);
				try{
					this.importSoggetto(archiveSoggetto, detail,
							isSoggettiApplicativiCredenzialiBasicPermitSameCredentials,
							isSoggettiApplicativiCredenzialiSslPermitSameCredentials,
							isSoggettiApplicativiCredenzialiPrincipalPermitSameCredentials,
							mapIdSoggettoDefault, mapSoggettoDefault);
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
					this.importServizioApplicativo(archiveServizioApplicativo, detail,
							isSoggettiApplicativiCredenzialiBasicPermitSameCredentials,
							isSoggettiApplicativiCredenzialiSslPermitSameCredentials,
							isSoggettiApplicativiCredenzialiPrincipalPermitSameCredentials);
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
					this.importAccordoServizioParteComune(archiveAccordoServizioParteComune, utilizzoAzioniDiretteInAccordoAbilitato, detail,
							mapIdSoggettoDefault, mapAPIconReferente);
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
				IDAccordo idAccordoServizioParteComune = archiveAccordoServizioParteSpecifica.getIdAccordoServizioParteComune();
				
				IProtocolFactory<?> protocolFactory = this.protocolFactoryManager.getProtocolFactoryByOrganizationType(idAccordoServizioParteComune.getSoggettoReferente().getTipo());
				boolean APIconReferente = mapAPIconReferente.get(protocolFactory.getProtocol());
				if(!APIconReferente) {
					IDSoggetto soggettoDefaultProtocollo = mapIdSoggettoDefault.get(protocolFactory.getProtocol()); 
					if(!idAccordoServizioParteComune.getSoggettoReferente().equals(soggettoDefaultProtocollo)) {
						idAccordoServizioParteComune.getSoggettoReferente().setTipo(soggettoDefaultProtocollo.getTipo());
						idAccordoServizioParteComune.getSoggettoReferente().setNome(soggettoDefaultProtocollo.getNome());
						archiveAccordoServizioParteSpecifica.getAccordoServizioParteSpecifica().setAccordoServizioParteComune(this.idAccordoFactory.getUriFromIDAccordo(idAccordoServizioParteComune));
					}
				}
				
				if(this.importerEngine.existsAccordoServizioParteComune(idAccordoServizioParteComune)){
					// verifico la tipologia
					AccordoServizioParteComune aspc = this.importerEngine.getAccordoServizioParteComune(idAccordoServizioParteComune);
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

			
			
			// Preparo Liste di Mapping da creare una volta registrati sia gli accordi (servizi e fruitori) che le porte (delegate e applicative)
			List<MappingErogazionePortaApplicativa> listMappingErogazionePA = new ArrayList<MappingErogazionePortaApplicativa>();
			List<MappingFruizionePortaDelegata> listMappingFruizionePD = new ArrayList<MappingFruizionePortaDelegata>();
			
			
			// Accordi di Servizio Parte Specifica (implementano accordi di servizio parte comune)
			for (int i = 0; i < listAccordiServizioParteSpecifica.size(); i++) {
				ArchiveAccordoServizioParteSpecifica archiveAccordoServizioParteSpecifica = listAccordiServizioParteSpecifica.get(i);
				ArchiveEsitoImportDetail detail = new ArchiveEsitoImportDetail(archiveAccordoServizioParteSpecifica);
				try{
					//archiveAccordoServizioParteSpecifica.update(); eseguito durante la preparazione della lista listAccordiServizioParteSpecifica
					this.importAccordoServizioParteSpecifica(archiveAccordoServizioParteSpecifica, false, 
							isAbilitatoControlloUnicitaImplementazioneAccordoPerSoggetto,
							isAbilitatoControlloUnicitaImplementazionePortTypePerSoggetto,
							detail,
							listMappingErogazionePA,
							mapIdSoggettoDefault, mapAPIconReferente);
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
					this.importAccordoServizioComposto(archiveAccordoServizioComposto, utilizzoAzioniDiretteInAccordoAbilitato, detail,
							mapIdSoggettoDefault, mapAPIconReferente);
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
							detail,
							listMappingErogazionePA,
							mapIdSoggettoDefault, mapAPIconReferente);
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
					this.importFruitore(archiveFruitore, detail, listMappingFruizionePD);
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
			
			
			// Mapping Erogazione - PA  (eventuali errori non provocano il fallimento del loader)
			if(listMappingErogazionePA.size()>0){
				for (int i = 0; i < listMappingErogazionePA.size(); i++) {
					MappingErogazionePortaApplicativa mapping = listMappingErogazionePA.get(i);
					ArchiveMappingErogazione archiveMappingErogazione = new ArchiveMappingErogazione(mapping);
					ArchiveEsitoImportDetail detail = new ArchiveEsitoImportDetail(archiveMappingErogazione);
					try{
						if(this.importerEngine.existsMappingErogazione(mapping.getIdServizio(), mapping.getIdPortaApplicativa())) {
							detail.setState(ArchiveStatoImport.UPDATE_NOT_ENABLED);
						}
						else {
							this.importerEngine.createMappingErogazione(mapping.getNome(), mapping.getDescrizione(), mapping.isDefault(), mapping.getIdServizio(), mapping.getIdPortaApplicativa());
							detail.setState(ArchiveStatoImport.CREATED);
						}
					}catch(Exception e){
						this.log.error("Errore durante la creazione del mapping di erogazione nome ["+mapping.getNome()+"] deafult ["+mapping.isDefault()+"] del servizio ["+mapping.getIdServizio()+
								"] verso la porta applicativa ["+mapping.getIdPortaApplicativa().getNome()+"]: "+e.getMessage(),e);
						detail.setState(ArchiveStatoImport.ERROR);
						detail.setException(e);
					}
					esito.getMappingErogazioni().add(detail);
				}
			}
			
			// Mapping Fruizione - PD  (eventuali errori non provocano il fallimento del loader)
			if(listMappingFruizionePD.size()>0){
				for (int i = 0; i < listMappingFruizionePD.size(); i++) {
					MappingFruizionePortaDelegata mapping = listMappingFruizionePD.get(i);
					ArchiveMappingFruizione archiveMappingFruizione = new ArchiveMappingFruizione(mapping);
					ArchiveEsitoImportDetail detail = new ArchiveEsitoImportDetail(archiveMappingFruizione);
					try{
						if(this.importerEngine.existsMappingFruizione(mapping.getIdServizio(), mapping.getIdFruitore(), mapping.getIdPortaDelegata())) {
							detail.setState(ArchiveStatoImport.UPDATE_NOT_ENABLED);
						}
						else {
							this.importerEngine.createMappingFruizione(mapping.getNome(), mapping.getDescrizione(), mapping.isDefault(), mapping.getIdServizio(), mapping.getIdFruitore(), mapping.getIdPortaDelegata());
							detail.setState(ArchiveStatoImport.CREATED);
						}
					}catch(Exception e){
						this.log.error("Errore durante la creazione del mapping di fruizione nome ["+mapping.getNome()+"] deafult ["+mapping.isDefault()+"] del servizio ["+mapping.getIdServizio()+
								"] verso la porta delegata ["+mapping.getIdPortaDelegata().getNome()+"] da parte del soggetto ["+mapping.getIdFruitore()+"]: "+e.getMessage(),e);
						detail.setState(ArchiveStatoImport.ERROR);
						detail.setException(e);
					}
					esito.getMappingFruizioni().add(detail);
				}
			}
			
						
			// Controllo Traffico (Configurazione)
			if(archive.getControlloTraffico_configurazione()!=null){
				org.openspcoop2.core.controllo_traffico.ConfigurazioneGenerale configurazione = archive.getControlloTraffico_configurazione();
				ArchiveEsitoImportDetailConfigurazione<org.openspcoop2.core.controllo_traffico.ConfigurazioneGenerale> detail = 
						new ArchiveEsitoImportDetailConfigurazione<org.openspcoop2.core.controllo_traffico.ConfigurazioneGenerale>(configurazione);
				try{
					this.importControlloTraffico_configurazione(configurazione, detail);
				}catch(Exception e){
					detail.setState(ArchiveStatoImport.ERROR);
					detail.setException(e);
				}
				esito.setControlloTraffico_configurazione(detail);
			}
					
			// Controllo Traffico (ConfigurazionePolicy)
			for (int i = 0; i < archive.getControlloTraffico_configurationPolicies().size(); i++) {
				ArchiveConfigurationPolicy archiveCCPolicy = archive.getControlloTraffico_configurationPolicies().get(i);
				ArchiveEsitoImportDetail detail = new ArchiveEsitoImportDetail(archiveCCPolicy);
				try{
					this.importControlloTraffico_configurationPolicy(archiveCCPolicy, detail);
				}catch(Exception e){
					detail.setState(ArchiveStatoImport.ERROR);
					detail.setException(e);
				}
				esito.getControlloTraffico_configurationPolicies().add(detail);
			}
			
			// Controllo Traffico (AttivazionePolicy)
			HashMap<String, String> instanceSerialIdsForPolicy = new HashMap<>();
			// NOTA: devo riordinare per posizione, poi anche se la posizione verrà ricalcolata, verrà mantenuto l'ordine
			SortedMap<List<ArchiveActivePolicy>> map_controllo_traffico_activePolicy = reorderByPosition(archive.getControlloTraffico_activePolicies());
			if(!map_controllo_traffico_activePolicy.isEmpty()) {
				for (String position : map_controllo_traffico_activePolicy.keys()) {
					List<ArchiveActivePolicy> archiveActivePolicyList = map_controllo_traffico_activePolicy.get(position);
					if(!archiveActivePolicyList.isEmpty()) {
						for (ArchiveActivePolicy archiveCCPolicy : archiveActivePolicyList) {
							ArchiveEsitoImportDetail detail = new ArchiveEsitoImportDetail(archiveCCPolicy);
							try{
								this.importControlloTraffico_activePolicy(archiveCCPolicy, detail, instanceSerialIdsForPolicy);
							}catch(Exception e){
								detail.setState(ArchiveStatoImport.ERROR);
								detail.setException(e);
							}
							esito.getControlloTraffico_activePolicies().add(detail);
						}
					}
				}
			}

			
			// Allarmi
			HashMap<String, String> instanceSerialIdsForAlarm = new HashMap<>();
			for (int i = 0; i < archive.getAllarmi().size(); i++) {
				ArchiveAllarme archiveAllarme = archive.getAllarmi().get(i);
				ArchiveEsitoImportDetail detail = new ArchiveEsitoImportDetail(archiveAllarme);
				try{
					this.importAllarme(archiveAllarme, detail, instanceSerialIdsForAlarm);
				}catch(Exception e){
					detail.setState(ArchiveStatoImport.ERROR);
					detail.setException(e);
				}
				esito.getAllarmi().add(detail);
			}
			
			
			// Configurazione (UrlInvocazioneRegole)
			// NOTA: devo riordinare per posizione, poi anche se la posizione verrà ricalcolata, verrà mantenuto l'ordine
			SortedMap<List<ArchiveUrlInvocazioneRegola>> map_urlInvocazione_regole = reorderByPosition(archive.getConfigurazionePdD_urlInvocazione_regole());
			if(!map_urlInvocazione_regole.isEmpty()) {
				for (String position : map_urlInvocazione_regole.keys()) {
					List<ArchiveUrlInvocazioneRegola> archiveRegolaList = map_urlInvocazione_regole.get(position);
					if(!archiveRegolaList.isEmpty()) {
						for (ArchiveUrlInvocazioneRegola archiveRegola : archiveRegolaList) {
							ArchiveEsitoImportDetail detail = new ArchiveEsitoImportDetail(archiveRegola);
							try{
								this.importConfigurazione_UrlInvocazioneRegola(archiveRegola, detail);
							}catch(Exception e){
								detail.setState(ArchiveStatoImport.ERROR);
								detail.setException(e);
							}
							esito.getConfigurazionePdD_urlInvocazione_regole().add(detail);
						}
					}
				}
			}
			
			
			// Configurazione (UrlInvocazione)
			if(archive.getConfigurazionePdD_urlInvocazione()!=null){
				org.openspcoop2.core.config.ConfigurazioneUrlInvocazione configurazione = archive.getConfigurazionePdD_urlInvocazione();
				ArchiveEsitoImportDetailConfigurazione<org.openspcoop2.core.config.ConfigurazioneUrlInvocazione> detail = 
						new ArchiveEsitoImportDetailConfigurazione<org.openspcoop2.core.config.ConfigurazioneUrlInvocazione>(configurazione);
				try{
					this.importConfigurazione_UrlInvocazione(configurazione, detail);
				}catch(Exception e){
					detail.setState(ArchiveStatoImport.ERROR);
					detail.setException(e);
				}
				esito.setConfigurazionePdD_urlInvocazione(detail);
			}
			
						
			// Configurazione
			if(archive.getConfigurazionePdD()!=null){
				Configurazione configurazione = archive.getConfigurazionePdD();
				ArchiveEsitoImportDetailConfigurazione<Configurazione> detail = new ArchiveEsitoImportDetailConfigurazione<Configurazione>(configurazione);
				try{
					this.importConfigurazione(configurazione, detail);
				}catch(Exception e){
					detail.setState(ArchiveStatoImport.ERROR);
					detail.setException(e);
				}
				esito.setConfigurazionePdD(detail);
			}

			
			// Creazione mapping in caso di importazione package creati prima dell'introduzione dei mapping fruizione ed erogazione
			if(archive.getPorteApplicative().size()>0){
				for (int i = 0; i < archive.getPorteApplicative().size(); i++) {
					
					ArchiveEsitoImportDetail detailEsitoPortaApplicativaPrecedente = null;
					if(esito.getPorteApplicative().size()>i) {
						detailEsitoPortaApplicativaPrecedente = esito.getPorteApplicative().get(i);
					}
					boolean init = true;
					if(detailEsitoPortaApplicativaPrecedente!=null) {
						if(ArchiveStatoImport.ERROR.equals(detailEsitoPortaApplicativaPrecedente.getState())){
							init = false;
						}
					}
					
					ArchivePortaApplicativa archivePortaApplicativa = archive.getPorteApplicative().get(i);
					ArchiveEsitoImportDetail detail = new ArchiveEsitoImportDetail(archivePortaApplicativa);
					try{
						if(init) {
							List<PortaApplicativa> listPA = new ArrayList<>();
							listPA.add(archive.getPorteApplicative().get(i).getPortaApplicativa());
							this.importerEngine.initMappingErogazione(this.log,listPA);
						}
						detail.setState(ArchiveStatoImport.UPDATED); // esito non gestito ma ci vuole sempre per corrispondere alla lista degli esiti della PA
					}catch(Exception e){
						detail.setState(ArchiveStatoImport.ERROR);
						detail.setException(e);
					}
					esito.getPorteApplicative_initMapping().add(detail);
				}
			}
			if(archive.getPorteDelegate().size()>0){
				for (int i = 0; i < archive.getPorteDelegate().size(); i++) {
					
					ArchiveEsitoImportDetail detailEsitoPortaDelegataPrecedente = null;
					if(esito.getPorteApplicative().size()>i) {
						detailEsitoPortaDelegataPrecedente = esito.getPorteDelegate().get(i);
					}
					boolean init = true;
					if(detailEsitoPortaDelegataPrecedente!=null) {
						if(ArchiveStatoImport.ERROR.equals(detailEsitoPortaDelegataPrecedente.getState())){
							init = false;
						}
					}
					
					ArchivePortaDelegata archivePortaDelegata = archive.getPorteDelegate().get(i);
					ArchiveEsitoImportDetail detail = new ArchiveEsitoImportDetail(archivePortaDelegata);
					try{
						if(init) {
							List<PortaDelegata> listPD = new ArrayList<>();
							listPD.add(archive.getPorteDelegate().get(i).getPortaDelegata());
							this.importerEngine.initMappingFruizione(this.log,listPD);
						}
						detail.setState(ArchiveStatoImport.UPDATED); // esito non gestito ma ci vuole sempre per corrispondere alla lista degli esiti della PA
					}catch(Exception e){
						detail.setState(ArchiveStatoImport.ERROR);
						detail.setException(e);
					}
					esito.getPorteDelegate_initMapping().add(detail);
				}
			}
			
			this.importerEngine.finalizeImport(archive);
			
			return esito;
			
		}catch(Exception e){
			throw e;
		}
	}
	
	
	private <T extends IPositionArchiveObject> SortedMap<List<T>> reorderByPosition(ArchiveSortedMap<T> map) throws ProtocolException, UtilsException {
		SortedMap<List<T>> map_reordered = new SortedMap<List<T>>();
		if(map.size()>0) {
			HashMap<String, List<T>> map_for_reorder = new HashMap<String, List<T>>();
			for (int i = 0; i < map.size(); i++) {
				T archive = map.get(i);
				int posizione = archive.position();
				String posizionePadding = StringUtils.leftPad(posizione+"", 20, "0");
				List<T> l = null;
				if(map_for_reorder.containsKey(posizionePadding)) {
					l = map_for_reorder.get(posizionePadding);
				}
				else {
					l = new ArrayList<T>();
					map_for_reorder.put(posizionePadding, l);
				}
				l.add(archive);
			}
			
			List<String> keys = new ArrayList<>();
			keys.addAll(map_for_reorder.keySet());
			Collections.sort(keys);
			for (String position : keys) {
				map_reordered.add(position, map_for_reorder.remove(position));
			}
		}
		return map_reordered;
	}
	
	
	
	public void importPdd(ArchivePdd archivePdd,ArchiveEsitoImportDetail detail){
		
		String nomePdd = archivePdd.getNomePdd();
		try{
			
			// --- check esistenza ---
			if(this.updateAbilitato==false){
				if(this.importerEngine.existsPortaDominio(nomePdd)){
					detail.setState(ArchiveStatoImport.UPDATE_NOT_ENABLED);
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
	
	
	
	
	
	
	
	public void importGruppo(ArchiveGruppo archiveGruppo,ArchiveEsitoImportDetail detail){
		
		IDGruppo idGruppo = archiveGruppo.getIdGruppo();
		try{
			
			// --- check esistenza ---
			if(this.updateAbilitato==false){
				if(this.importerEngine.existsGruppo(idGruppo)){
					detail.setState(ArchiveStatoImport.UPDATE_NOT_ENABLED);
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
			
			archiveGruppo.getGruppo().setSuperUser(this.userLogin);
			
			org.openspcoop2.core.registry.driver.utils.XMLDataConverter.
				impostaInformazioniRegistroDB_Gruppo(archiveGruppo.getGruppo());
			
			
			// --- ora registrazione
			archiveGruppo.getGruppo().setOraRegistrazione(DateManager.getDate());
			
			
			// --- upload ---
			boolean create = false;
			if(this.importerEngine.existsGruppo(idGruppo)){
				
				org.openspcoop2.core.registry.Gruppo old = this.importerEngine.getGruppo(idGruppo);
				archiveGruppo.getGruppo().setId(old.getId());
				
				// visibilita' oggetto stesso per update
				if(this.importerEngine.isVisioneOggettiGlobale(this.userLogin)==false){
					if(this.userLogin.equals(old.getSuperUser())==false){
						throw new Exception("Il gruppo non è visibile/aggiornabile dall'utente collegato ("+this.userLogin+")");
					}
				}

				// update
				this.importerEngine.updateGruppo(archiveGruppo.getGruppo());
				create = false;
			}
			// --- create ---
			else{
				this.importerEngine.createGruppo(archiveGruppo.getGruppo());
				create = true;
			}
				

			// --- info ---
			if(create){
				detail.setState(ArchiveStatoImport.CREATED);
			}else{
				detail.setState(ArchiveStatoImport.UPDATED);
			}
		}			
		catch(Exception e){
			this.log.error("Errore durante l'import del gruppo ["+idGruppo+"]: "+e.getMessage(),e);
			detail.setState(ArchiveStatoImport.ERROR);
			detail.setException(e);
		}
	}
	
	
	
	
	
	
	
	public void importRuolo(ArchiveRuolo archiveRuolo,ArchiveEsitoImportDetail detail){
		
		IDRuolo idRuolo = archiveRuolo.getIdRuolo();
		try{
			
			// --- check esistenza ---
			if(this.updateAbilitato==false){
				if(this.importerEngine.existsRuolo(idRuolo)){
					detail.setState(ArchiveStatoImport.UPDATE_NOT_ENABLED);
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
			
			archiveRuolo.getRuolo().setSuperUser(this.userLogin);
			
			org.openspcoop2.core.registry.driver.utils.XMLDataConverter.
				impostaInformazioniRegistroDB_Ruolo(archiveRuolo.getRuolo());
			
			
			// --- ora registrazione
			archiveRuolo.getRuolo().setOraRegistrazione(DateManager.getDate());
			
			
			// --- upload ---
			boolean create = false;
			if(this.importerEngine.existsRuolo(idRuolo)){
				
				org.openspcoop2.core.registry.Ruolo old = this.importerEngine.getRuolo(idRuolo);
				archiveRuolo.getRuolo().setId(old.getId());
				
				// visibilita' oggetto stesso per update
				if(this.importerEngine.isVisioneOggettiGlobale(this.userLogin)==false){
					if(this.userLogin.equals(old.getSuperUser())==false){
						throw new Exception("Il ruolo non è visibile/aggiornabile dall'utente collegato ("+this.userLogin+")");
					}
				}

				// update
				this.importerEngine.updateRuolo(archiveRuolo.getRuolo());
				create = false;
			}
			// --- create ---
			else{
				this.importerEngine.createRuolo(archiveRuolo.getRuolo());
				create = true;
			}
				

			// --- info ---
			if(create){
				detail.setState(ArchiveStatoImport.CREATED);
			}else{
				detail.setState(ArchiveStatoImport.UPDATED);
			}
		}			
		catch(Exception e){
			this.log.error("Errore durante l'import del ruolo ["+idRuolo+"]: "+e.getMessage(),e);
			detail.setState(ArchiveStatoImport.ERROR);
			detail.setException(e);
		}
	}
	
	
	
	
	
	public void importScope(ArchiveScope archiveScope,ArchiveEsitoImportDetail detail){
		
		IDScope idScope = archiveScope.getIdScope();
		try{
			
			// --- check esistenza ---
			if(this.updateAbilitato==false){
				if(this.importerEngine.existsScope(idScope)){
					detail.setState(ArchiveStatoImport.UPDATE_NOT_ENABLED);
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
			
			archiveScope.getScope().setSuperUser(this.userLogin);
			
			org.openspcoop2.core.registry.driver.utils.XMLDataConverter.
				impostaInformazioniRegistroDB_Scope(archiveScope.getScope());
			
			
			// --- ora registrazione
			archiveScope.getScope().setOraRegistrazione(DateManager.getDate());
			
			
			// --- upload ---
			boolean create = false;
			if(this.importerEngine.existsScope(idScope)){
				
				org.openspcoop2.core.registry.Scope old = this.importerEngine.getScope(idScope);
				archiveScope.getScope().setId(old.getId());
				
				// visibilita' oggetto stesso per update
				if(this.importerEngine.isVisioneOggettiGlobale(this.userLogin)==false){
					if(this.userLogin.equals(old.getSuperUser())==false){
						throw new Exception("Lo scope non è visibile/aggiornabile dall'utente collegato ("+this.userLogin+")");
					}
				}

				// update
				this.importerEngine.updateScope(archiveScope.getScope());
				create = false;
			}
			// --- create ---
			else{
				this.importerEngine.createScope(archiveScope.getScope());
				create = true;
			}
				

			// --- info ---
			if(create){
				detail.setState(ArchiveStatoImport.CREATED);
			}else{
				detail.setState(ArchiveStatoImport.UPDATED);
			}
		}			
		catch(Exception e){
			this.log.error("Errore durante l'import dello scope ["+idScope+"]: "+e.getMessage(),e);
			detail.setState(ArchiveStatoImport.ERROR);
			detail.setException(e);
		}
	}
	
	
	
	
	public void importSoggetto(ArchiveSoggetto archiveSoggetto,ArchiveEsitoImportDetail detail,
			boolean isSoggettiApplicativiCredenzialiBasicPermitSameCredentials,
			boolean isSoggettiApplicativiCredenzialiSslPermitSameCredentials,
			boolean isSoggettiApplicativiCredenzialiPrincipalPermitSameCredentials,
			Map<String, IDSoggetto> mapIdSoggettoDefault, Map<String, org.openspcoop2.core.registry.Soggetto> mapSoggettoDefault){
		
		IDSoggetto idSoggetto = archiveSoggetto.getIdSoggetto();
		IProtocolFactory<?> protocolFactory = null;
		try{
			
			boolean create = false;
			
			if(archiveSoggetto.getSoggettoRegistro()!=null){
								
				
				// --- check esistenza ---
				if(this.updateAbilitato==false){
					if(this.importerEngine.existsSoggettoRegistro(idSoggetto)){
						detail.setState(ArchiveStatoImport.UPDATE_NOT_ENABLED);
						return;
					}
				}
				
				
				
				// --- check soggetto default ---
				
				protocolFactory = this.protocolFactoryManager.getProtocolFactoryByOrganizationType(idSoggetto.getTipo());
				//IDSoggetto soggettoDefaultProtocollo = this.importerEngine.getSoggettoDefault( protocolFactory.getProtocol());
				IDSoggetto soggettoDefaultProtocollo = mapIdSoggettoDefault.get(protocolFactory.getProtocol());
								
				// non devono essere importati soggetti di default diversi da quelli presenti
				if(archiveSoggetto.getSoggettoConfigurazione().isDominioDefault()) {
					if(soggettoDefaultProtocollo!=null) {
						if(!soggettoDefaultProtocollo.equals(idSoggetto)) {
							// imposto a false il dominio di default
							archiveSoggetto.getSoggettoConfigurazione().setDominioDefault(false);
						}
					}
				}
				
				// se il soggetto che sto importando è il soggetto di default, deve avere la stessa pdd e deve rimanere soggetto di default
				if(soggettoDefaultProtocollo!=null) {
					if(soggettoDefaultProtocollo.equals(idSoggetto)) {
						archiveSoggetto.getSoggettoConfigurazione().setDominioDefault(true);
						//try {
							//org.openspcoop2.core.registry.Soggetto soggettoDefault = this.importerEngine.getSoggettoRegistro(soggettoDefaultProtocollo);
							org.openspcoop2.core.registry.Soggetto soggettoDefault = mapSoggettoDefault.get(protocolFactory.getProtocol());
							if(soggettoDefault!=null && soggettoDefault.getPortaDominio()!=null) {
								if(!soggettoDefault.getPortaDominio().equals(archiveSoggetto.getSoggettoRegistro().getPortaDominio())) {
									archiveSoggetto.getSoggettoRegistro().setPortaDominio(soggettoDefault.getPortaDominio());
								}
							}
						//}catch( DriverRegistroServiziNotFound notFound) {
							// ignore
						//}
					}
				}
				
				
				// --- check elementi riferiti ---
				
				// porta dominio
				if(archiveSoggetto.getSoggettoRegistro().getPortaDominio()!=null){
					if(this.importerEngine.existsPortaDominio(archiveSoggetto.getSoggettoRegistro().getPortaDominio()) == false ){
						throw new Exception("Porta di dominio ["+archiveSoggetto.getSoggettoRegistro().getPortaDominio()+"] associata non esiste");
					}
				}		
				
				// ruoli
				if(archiveSoggetto.getSoggettoRegistro().getRuoli()!=null && archiveSoggetto.getSoggettoRegistro().getRuoli().sizeRuoloList()>0){
					for (int i = 0; i < archiveSoggetto.getSoggettoRegistro().getRuoli().sizeRuoloList(); i++) {
						IDRuolo idRuolo = new IDRuolo(archiveSoggetto.getSoggettoRegistro().getRuoli().getRuolo(i).getNome());
						if(this.importerEngine.existsRuolo(idRuolo) == false ){
							throw new Exception("Ruolo ["+idRuolo.getNome()+"] associato non esiste");
						}	
					}
				}
				
				// univocita' credenziali
				if(	archiveSoggetto.getSoggettoRegistro().getCredenzialiList()!=null &&
						!archiveSoggetto.getSoggettoRegistro().getCredenzialiList().isEmpty()) {
					for (CredenzialiSoggetto credenziali : archiveSoggetto.getSoggettoRegistro().getCredenzialiList()) {
						org.openspcoop2.core.registry.constants.CredenzialeTipo tipo = credenziali.getTipo();
						if(tipo!=null) {
							Soggetto soggettoFound = null;
							ServizioApplicativo saFound = null;
							String c = null;
							switch (tipo) {
							case BASIC:
								soggettoFound = this.importerEngine.getSoggettoRegistroCredenzialiBasic(credenziali.getUser());
								c = credenziali.getUser();
								if(!isSoggettiApplicativiCredenzialiBasicPermitSameCredentials) {
									saFound = this.importerEngine.getServizioApplicativoCredenzialiBasic(credenziali.getUser());
								}
								break;
							case APIKEY:
								soggettoFound = this.importerEngine.getSoggettoRegistroCredenzialiApiKey(credenziali.getUser(), credenziali.isAppId());
								c = credenziali.getUser();
								break;
							case SSL:
								if(credenziali.getCertificate()!=null && credenziali.getCertificate().length>0) {
									soggettoFound = this.importerEngine.getSoggettoRegistroCredenzialiSsl(credenziali.getCertificate(), credenziali.isCertificateStrictVerification());
									c = "X.509";
									if(!isSoggettiApplicativiCredenzialiSslPermitSameCredentials) {
										saFound = this.importerEngine.getServizioApplicativoCredenzialiSsl(credenziali.getCertificate(), credenziali.isCertificateStrictVerification());
									}
								}
								else {
									soggettoFound = this.importerEngine.getSoggettoRegistroCredenzialiSsl(credenziali.getSubject(), credenziali.getIssuer());
									c = credenziali.getSubject();
									if(!isSoggettiApplicativiCredenzialiSslPermitSameCredentials) {
										saFound = this.importerEngine.getServizioApplicativoCredenzialiSsl(credenziali.getSubject(), credenziali.getIssuer());
									}
								}
								break;
							case PRINCIPAL:
								soggettoFound = this.importerEngine.getSoggettoRegistroCredenzialiPrincipal(credenziali.getUser());
								c = credenziali.getUser();
								if(!isSoggettiApplicativiCredenzialiPrincipalPermitSameCredentials) {
									saFound = this.importerEngine.getServizioApplicativoCredenzialiPrincipal(credenziali.getUser());
								}
								break;
							}
							if(soggettoFound!=null) {
								IDSoggetto idSoggettoFound =  new IDSoggetto(soggettoFound.getTipo(), soggettoFound.getNome());
								boolean equalsForUpdate = idSoggettoFound.equals(idSoggetto);
								if(!equalsForUpdate) {
									throw new Exception("Le credenziali '"+tipo+"' ("+c+") risultano già associate al soggetto '"+idSoggettoFound+"'");
								}
							}
							if(saFound!=null) {
								IDSoggetto idSoggettoSaFound =  new IDSoggetto(saFound.getTipoSoggettoProprietario(), saFound.getNomeSoggettoProprietario());
								throw new Exception("Le credenziali '"+tipo+"' ("+c+") risultano già associate all'applicativo '"+saFound.getNome()+"' appartenente al soggetto '"+idSoggettoSaFound+"'");
							}
						}
					}
				}
				
				
				// --- compatibilita' elementi riferiti ---
				// Non ce ne sono da controllare
				
				
				// ---- visibilita' oggetto riferiti ---
				// Non ce ne sono da controllare
				
				
				// --- set dati obbligatori nel db ----
				String server = this.nomePddOperativa;
				if(PddTipologia.ESTERNO.equals(this.tipoPddDefault)) {
					server = null; // non gli assegno alcuna pdd se non definita
				}
				org.openspcoop2.core.registry.driver.utils.XMLDataConverter.
					impostaInformazioniRegistroDB_Soggetto(archiveSoggetto.getSoggettoRegistro(),server);
				
				archiveSoggetto.getSoggettoRegistro().setSuperUser(this.userLogin);			
				
				if(archiveSoggetto.getSoggettoRegistro().getCodiceIpa()==null ||
						archiveSoggetto.getSoggettoRegistro().getIdentificativoPorta()==null){
					
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
					IDSoggetto oldIDSoggettoForUpdate = new IDSoggetto(old.getTipo(), old.getNome());
					archiveSoggetto.getSoggettoRegistro().setOldIDSoggettoForUpdate(oldIDSoggettoForUpdate);
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
						detail.setState(ArchiveStatoImport.UPDATE_NOT_ENABLED);
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
					
					if(protocolFactory==null) {
						protocolFactory = this.protocolFactoryManager.getProtocolFactoryByOrganizationType(idSoggetto.getTipo());
					}
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
					IDSoggetto oldIDSoggettoForUpdate = new IDSoggetto(old.getTipo(), old.getNome());
					archiveSoggetto.getSoggettoRegistro().setOldIDSoggettoForUpdate(oldIDSoggettoForUpdate);
					
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
			ArchiveEsitoImportDetail detail,
			boolean isSoggettiApplicativiCredenzialiBasicPermitSameCredentials,
			boolean isSoggettiApplicativiCredenzialiSslPermitSameCredentials,
			boolean isSoggettiApplicativiCredenzialiPrincipalPermitSameCredentials){
		
		IDServizioApplicativo idServizioApplicativo = archiveServizioApplicativo.getIdServizioApplicativo();
		IDSoggetto idSoggettoProprietario = archiveServizioApplicativo.getIdSoggettoProprietario();
		try{
			
			// --- check esistenza ---
			if(this.updateAbilitato==false){
				if(this.importerEngine.existsServizioApplicativo(idServizioApplicativo)){
					detail.setState(ArchiveStatoImport.UPDATE_NOT_ENABLED);
					return;
				}
			}
			
				
			// --- check elementi riferiti ---
			
			// soggetto proprietario
			if(this.importerEngine.existsSoggettoConfigurazione(idSoggettoProprietario) == false ){
				throw new Exception("Soggetto proprietario ["+idSoggettoProprietario+"] non esistente");
			}
			
			// ruoli
			if(archiveServizioApplicativo.getServizioApplicativo().getInvocazionePorta()!=null &&
					archiveServizioApplicativo.getServizioApplicativo().getInvocazionePorta().getRuoli()!=null && 
					archiveServizioApplicativo.getServizioApplicativo().getInvocazionePorta().getRuoli().sizeRuoloList()>0){
				for (int i = 0; i < archiveServizioApplicativo.getServizioApplicativo().getInvocazionePorta().getRuoli().sizeRuoloList(); i++) {
					IDRuolo idRuolo = new IDRuolo(archiveServizioApplicativo.getServizioApplicativo().getInvocazionePorta().getRuoli().getRuolo(i).getNome());
					if(this.importerEngine.existsRuolo(idRuolo) == false ){
						throw new Exception("Ruolo ["+idRuolo.getNome()+"] associato non esiste");
					}	
				}
			}
			
			// univocita' credenziali
			if(archiveServizioApplicativo.getServizioApplicativo().getInvocazionePorta()!=null &&
					archiveServizioApplicativo.getServizioApplicativo().getInvocazionePorta().getCredenzialiList()!=null &&
					!archiveServizioApplicativo.getServizioApplicativo().getInvocazionePorta().getCredenzialiList().isEmpty()) {
				for (Credenziali credenziali : archiveServizioApplicativo.getServizioApplicativo().getInvocazionePorta().getCredenzialiList()) {
					org.openspcoop2.core.config.constants.CredenzialeTipo tipo = credenziali.getTipo();
					if(tipo!=null) {
						ServizioApplicativo saFound = null;
						Soggetto soggettoFound = null;
						String c = null;
						switch (tipo) {
						case BASIC:
							saFound = this.importerEngine.getServizioApplicativoCredenzialiBasic(credenziali.getUser());
							c = credenziali.getUser();
							if(!isSoggettiApplicativiCredenzialiBasicPermitSameCredentials) {
								soggettoFound = this.importerEngine.getSoggettoRegistroCredenzialiBasic(credenziali.getUser());
							}
							break;
						case APIKEY:
							saFound = this.importerEngine.getServizioApplicativoCredenzialiApiKey(credenziali.getUser(), credenziali.isAppId());
							c = credenziali.getUser();
							break;
						case SSL:
							
							boolean tokenWithHttpsEnabled = false;
							ProtocolFactoryManager protocolFactoryManager = ProtocolFactoryManager.getInstance();
							String protocollo = protocolFactoryManager.getProtocolByOrganizationType(idSoggettoProprietario.getTipo());
							tokenWithHttpsEnabled = protocolFactoryManager.getProtocolFactoryByName(protocollo).createProtocolConfiguration().isSupportatoAutenticazioneApplicativiHttpsConToken();
							if(tokenWithHttpsEnabled && credenziali.getTokenPolicy()!=null && StringUtils.isNotEmpty(credenziali.getTokenPolicy())) {
								saFound = this.importerEngine.getServizioApplicativoCredenzialiToken(credenziali.getTokenPolicy(), credenziali.getUser());
								c = credenziali.getUser();
								tipo = org.openspcoop2.core.config.constants.CredenzialeTipo.TOKEN;
							}
							else {
								if(credenziali.getCertificate()!=null && credenziali.getCertificate().length>0) {
									saFound = this.importerEngine.getServizioApplicativoCredenzialiSsl(credenziali.getCertificate(), credenziali.isCertificateStrictVerification());
									c = "X.509";
									if(!isSoggettiApplicativiCredenzialiSslPermitSameCredentials) {
										soggettoFound = this.importerEngine.getSoggettoRegistroCredenzialiSsl(credenziali.getCertificate(), credenziali.isCertificateStrictVerification());
									}
								}
								else {
									saFound = this.importerEngine.getServizioApplicativoCredenzialiSsl(credenziali.getSubject(), credenziali.getIssuer());
									c = credenziali.getSubject();
									if(!isSoggettiApplicativiCredenzialiSslPermitSameCredentials) {
										soggettoFound = this.importerEngine.getSoggettoRegistroCredenzialiSsl(credenziali.getSubject(), credenziali.getIssuer());
									}
								}
							}
							break;
						case PRINCIPAL:
							saFound = this.importerEngine.getServizioApplicativoCredenzialiPrincipal(credenziali.getUser());
							c = credenziali.getUser();
							if(!isSoggettiApplicativiCredenzialiPrincipalPermitSameCredentials) {
								soggettoFound = this.importerEngine.getSoggettoRegistroCredenzialiPrincipal(credenziali.getUser());
							}
							break;
						case TOKEN:
							saFound = this.importerEngine.getServizioApplicativoCredenzialiToken(credenziali.getTokenPolicy(), credenziali.getUser());
							c = credenziali.getUser();
							break;
						}
						if(saFound!=null) {
							IDSoggetto idSoggettoProprietarioFound =  new IDSoggetto(saFound.getTipoSoggettoProprietario(), saFound.getNomeSoggettoProprietario());
							boolean equalsForUpdate = idSoggettoProprietario.equals(idSoggettoProprietarioFound) && idServizioApplicativo.getNome().contentEquals(saFound.getNome());
							if(!equalsForUpdate) {
								throw new Exception("Le credenziali '"+tipo+"' ("+c+") risultano già associate all'applicativo '"+saFound.getNome()+"' appartenente al soggetto '"+idSoggettoProprietarioFound+"'");
							}
						}
						if(soggettoFound!=null) {
							IDSoggetto idSoggettoFound =  new IDSoggetto(soggettoFound.getTipo(), soggettoFound.getNome());
							throw new Exception("Le credenziali '"+tipo+"' ("+c+") risultano già associate al soggetto '"+idSoggettoFound+"'");
						}
					}
				}
			}
			
			// token negoziazione
			if(archiveServizioApplicativo.getServizioApplicativo()!=null && archiveServizioApplicativo.getServizioApplicativo().getInvocazioneServizio()!=null) {
				if(archiveServizioApplicativo.getServizioApplicativo().getInvocazioneServizio().getConnettore()!=null &&
						archiveServizioApplicativo.getServizioApplicativo().getInvocazioneServizio().getConnettore().getProperties()!=null && archiveServizioApplicativo.getServizioApplicativo().getInvocazioneServizio().getConnettore().getProperties().containsKey(CostantiConnettori.CONNETTORE_TOKEN_POLICY)) {
					String policy = archiveServizioApplicativo.getServizioApplicativo().getInvocazioneServizio().getConnettore().getProperties().get(CostantiConnettori.CONNETTORE_TOKEN_POLICY);
					if(this.importerEngine.existsGenericProperties_retrieve(policy) == false) {
						throw new Exception("Token Policy Negoziazione ["+policy+"] indicato nel connettore non esistente");
					}
				}
			}
			if(archiveServizioApplicativo.getServizioApplicativo()!=null && archiveServizioApplicativo.getServizioApplicativo().getRispostaAsincrona()!=null) {
				if(archiveServizioApplicativo.getServizioApplicativo().getRispostaAsincrona().getConnettore()!=null &&
						archiveServizioApplicativo.getServizioApplicativo().getRispostaAsincrona().getConnettore().getProperties()!=null && archiveServizioApplicativo.getServizioApplicativo().getRispostaAsincrona().getConnettore().getProperties().containsKey(CostantiConnettori.CONNETTORE_TOKEN_POLICY)) {
					String policy = archiveServizioApplicativo.getServizioApplicativo().getRispostaAsincrona().getConnettore().getProperties().get(CostantiConnettori.CONNETTORE_TOKEN_POLICY);
					if(this.importerEngine.existsGenericProperties_retrieve(policy) == false) {
						throw new Exception("Token Policy Negoziazione ["+policy+"] indicato nel connettore non esistente");
					}
				}
			}
			
			// plugin
			if(this.checkExistsPluginConfigurazione) {
				if(archiveServizioApplicativo.getServizioApplicativo()!=null && archiveServizioApplicativo.getServizioApplicativo().getInvocazioneServizio()!=null) {
					if(archiveServizioApplicativo.getServizioApplicativo().getInvocazioneServizio().getConnettore()!=null &&
							archiveServizioApplicativo.getServizioApplicativo().getInvocazioneServizio().getConnettore().getCustom()!=null &&
							archiveServizioApplicativo.getServizioApplicativo().getInvocazioneServizio().getConnettore().getCustom() &&
							archiveServizioApplicativo.getServizioApplicativo().getInvocazioneServizio().getConnettore().getTipo()!=null) {
						TipiConnettore tipo = TipiConnettore.toEnumFromName(archiveServizioApplicativo.getServizioApplicativo().getInvocazioneServizio().getConnettore().getTipo());
						if(tipo==null) {
							if(this.importerEngine.existsPluginClasse(TipoPlugin.CONNETTORE.getValue(), archiveServizioApplicativo.getServizioApplicativo().getInvocazioneServizio().getConnettore().getTipo()) == false ){
								throw new Exception("Plugin '"+TipoPlugin.CONNETTORE.getValue()+"' ["+archiveServizioApplicativo.getServizioApplicativo().getInvocazioneServizio().getConnettore().getTipo()+"] non esistente nel registro");
							}
						}
					}
				}
				if(archiveServizioApplicativo.getServizioApplicativo()!=null && archiveServizioApplicativo.getServizioApplicativo().getRispostaAsincrona()!=null) {
					if(archiveServizioApplicativo.getServizioApplicativo().getRispostaAsincrona().getConnettore()!=null &&
							archiveServizioApplicativo.getServizioApplicativo().getRispostaAsincrona().getConnettore().getCustom()!=null &&
							archiveServizioApplicativo.getServizioApplicativo().getRispostaAsincrona().getConnettore().getCustom() &&
							archiveServizioApplicativo.getServizioApplicativo().getRispostaAsincrona().getConnettore().getTipo()!=null) {
						TipiConnettore tipo = TipiConnettore.toEnumFromName(archiveServizioApplicativo.getServizioApplicativo().getRispostaAsincrona().getConnettore().getTipo());
						if(tipo==null) {
							if(this.importerEngine.existsPluginClasse(TipoPlugin.CONNETTORE.getValue(), archiveServizioApplicativo.getServizioApplicativo().getRispostaAsincrona().getConnettore().getTipo()) == false ){
								throw new Exception("Plugin '"+TipoPlugin.CONNETTORE.getValue()+"' ["+archiveServizioApplicativo.getServizioApplicativo().getRispostaAsincrona().getConnettore().getTipo()+"] non esistente nel registro");
							}
						}
					}
				}
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
				IDServizioApplicativo oldIDServizioApplicativoForUpdate = new IDServizioApplicativo();
				oldIDServizioApplicativoForUpdate.setNome(old.getNome());
				oldIDServizioApplicativoForUpdate.setIdSoggettoProprietario(new IDSoggetto(old.getTipoSoggettoProprietario(), old.getNomeSoggettoProprietario()));
				archiveServizioApplicativo.getServizioApplicativo().setOldIDServizioApplicativoForUpdate(oldIDServizioApplicativoForUpdate);
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
					detail.setState(ArchiveStatoImport.UPDATE_NOT_ENABLED);
					return;
				}
			}
			
				
			// --- check elementi riferiti ---
			
			// soggetto referente
			if(this.importerEngine.existsSoggettoRegistro(idSoggettoReferente) == false ){
				throw new Exception("Soggetto proprietario ["+idSoggettoReferente+"] non esistente");
			}
			
			// soggetti partecipanti
			if(idSoggettiPartecipanti!=null){
				for (IDSoggetto idSoggettoPartecipante : idSoggettiPartecipanti) {
					if(this.importerEngine.existsSoggettoRegistro(idSoggettoPartecipante) == false ){
						throw new Exception("Soggetto partecipante ["+idSoggettoPartecipante+"] non esistente");
					}
				}
			}
			
			
			// --- compatibilita' elementi riferiti ---
			String protocolloAssociatoAccordo = this.protocolFactoryManager.getProtocolByOrganizationType(idSoggettoReferente.getTipo());
			// soggetti partecipanti
			if(idSoggettiPartecipanti!=null){
				for (IDSoggetto idSoggettoPartecipante : idSoggettiPartecipanti) {
					String protocolloAssociatoSoggettoPartecipante = this.protocolFactoryManager.getProtocolByOrganizationType(idSoggettoPartecipante.getTipo());
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
			StringBuilder warningInfoStatoFinale = new StringBuilder("");
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
	
	
	
	
	
	
	private void modifyUriAccordoCorrelato(AccordoServizioParteComune aspc, IDSoggetto soggettoDefaultProtocollo) {
		if(aspc!=null) {
			if(aspc.sizeProtocolPropertyList()>0) {
				for (ProtocolProperty pp : aspc.getProtocolPropertyList()) {
					if(CostantiDB.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_API_RICHIESTA_CORRELATA.equals(pp.getName()) && pp.getValue()!=null && StringUtils.isNotEmpty(pp.getValue())) {
						modifyUriAccordo(pp, soggettoDefaultProtocollo);
					}
				}
			}
			if(aspc.sizeResourceList()>0) {
				for (Resource r : aspc.getResourceList()) {
					if(r.sizeProtocolPropertyList()>0) {
						for (ProtocolProperty pp : r.getProtocolPropertyList()) {
							if(CostantiDB.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_API_RICHIESTA_CORRELATA.equals(pp.getName()) && pp.getValue()!=null && StringUtils.isNotEmpty(pp.getValue())) {
								modifyUriAccordo(pp, soggettoDefaultProtocollo);
							}
						}
					}
				}
			}
			if(aspc.sizePortTypeList()>0) {
				for (PortType pt : aspc.getPortTypeList()) {
					if(pt.sizeProtocolPropertyList()>0) {
						for (ProtocolProperty pp : pt.getProtocolPropertyList()) {
							if(CostantiDB.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_API_RICHIESTA_CORRELATA.equals(pp.getName()) && pp.getValue()!=null && StringUtils.isNotEmpty(pp.getValue())) {
								modifyUriAccordo(pp, soggettoDefaultProtocollo);
							}
						}
					}
					if(pt.sizeAzioneList()>0) {
						for (Operation op : pt.getAzioneList()) {
							if(op.sizeProtocolPropertyList()>0) {
								for (ProtocolProperty pp : op.getProtocolPropertyList()) {
									if(CostantiDB.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_API_RICHIESTA_CORRELATA.equals(pp.getName()) && pp.getValue()!=null && StringUtils.isNotEmpty(pp.getValue())) {
										modifyUriAccordo(pp, soggettoDefaultProtocollo);
									}
								}
							}
						}
					}
				}
			}
		}
	}
	private void modifyUriAccordo(ProtocolProperty pp, IDSoggetto soggettoDefaultProtocollo) {
		if(pp!=null && CostantiDB.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_API_RICHIESTA_CORRELATA.equals(pp.getName()) && pp.getValue()!=null && StringUtils.isNotEmpty(pp.getValue())) {
			String uri = pp.getValue();
			try {
				IDAccordo idUri = this.idAccordoFactory.getIDAccordoFromUri(uri);
				if(idUri!=null && idUri.getSoggettoReferente()!=null) {
					if(!idUri.getSoggettoReferente().equals(soggettoDefaultProtocollo)) {
						idUri.getSoggettoReferente().setTipo(soggettoDefaultProtocollo.getTipo());
						idUri.getSoggettoReferente().setNome(soggettoDefaultProtocollo.getNome());
						String newUri = this.idAccordoFactory.getUriFromIDAccordo(idUri);
						pp.setValue(newUri);
						//this.log.info("MODIFICATO old["+uri+"] nuovo["+newUri+"]");
					}
				}
			}catch(Throwable t) {
				// ignore
			}
		}
	}
	
	public void importAccordoServizioParteComune(ArchiveAccordoServizioParteComune archiveAccordoServizioParteComune,
			boolean utilizzoAzioniDiretteInAccordoAbilitato,
			ArchiveEsitoImportDetail detail,
			Map<String, IDSoggetto> mapIdSoggettoDefault, Map<String, Boolean> mapAPIconReferente){
		
		IDAccordo idAccordoServizioParteComune = archiveAccordoServizioParteComune.getIdAccordoServizioParteComune();
		IDSoggetto idSoggettoReferente = archiveAccordoServizioParteComune.getIdSoggettoReferente();
		try{
			// --- fix soggetto referente --
			IProtocolFactory<?> protocolFactory = this.protocolFactoryManager.getProtocolFactoryByOrganizationType(idSoggettoReferente.getTipo());
			boolean APIconReferente = mapAPIconReferente.get(protocolFactory.getProtocol());
			if(!APIconReferente) {
				IDSoggetto soggettoDefaultProtocollo = mapIdSoggettoDefault.get(protocolFactory.getProtocol()); 
				if(!idSoggettoReferente.equals(soggettoDefaultProtocollo)) {
					archiveAccordoServizioParteComune.getAccordoServizioParteComune().getSoggettoReferente().setTipo(soggettoDefaultProtocollo.getTipo());
					archiveAccordoServizioParteComune.getAccordoServizioParteComune().getSoggettoReferente().setNome(soggettoDefaultProtocollo.getNome());
					idSoggettoReferente.setTipo(soggettoDefaultProtocollo.getTipo());
					idSoggettoReferente.setNome(soggettoDefaultProtocollo.getNome());
					idAccordoServizioParteComune.getSoggettoReferente().setTipo(soggettoDefaultProtocollo.getTipo());
					idAccordoServizioParteComune.getSoggettoReferente().setNome(soggettoDefaultProtocollo.getNome());
				}
				if(CostantiLabel.MODIPA_PROTOCOL_NAME.equals(protocolFactory.getProtocol())) {
					modifyUriAccordoCorrelato(archiveAccordoServizioParteComune.getAccordoServizioParteComune(), soggettoDefaultProtocollo);
				}
			}
			
			
			// --- check esistenza ---
			if(this.updateAbilitato==false){
				if(this.importerEngine.existsAccordoServizioParteComune(idAccordoServizioParteComune)){
					detail.setState(ArchiveStatoImport.UPDATE_NOT_ENABLED);
					return;
				}
			}
			
				
			
			// --- check elementi riferiti ---
			
			// soggetto referente
			if(APIconReferente) {
				if(this.importerEngine.existsSoggettoRegistro(idSoggettoReferente) == false ){
					throw new Exception("Soggetto proprietario ["+idSoggettoReferente+"] non esistente");
				}
			}
			
			// gruppi
			if(archiveAccordoServizioParteComune.getAccordoServizioParteComune().getGruppi()!=null && 
					archiveAccordoServizioParteComune.getAccordoServizioParteComune().getGruppi().sizeGruppoList()>0){
				for (int i = 0; i < archiveAccordoServizioParteComune.getAccordoServizioParteComune().getGruppi().sizeGruppoList(); i++) {
					IDGruppo idGruppo = new IDGruppo(archiveAccordoServizioParteComune.getAccordoServizioParteComune().getGruppi().getGruppo(i).getNome());
					if(this.importerEngine.existsGruppo(idGruppo) == false ){
						throw new Exception("Tag ["+idGruppo.getNome()+"] associato non esiste");
					}	
				}
			}
			
			
			// --- compatibilita' elementi riferiti ---
			// non ce ne sono da controllare
			//String protocolloAssociatoAccordo = this.protocolFactoryManager.getProtocolByOrganizationType(idSoggettoReferente.getTipo());
						

			// ---- visibilita' oggetto riferiti ---
			if(APIconReferente) {
				org.openspcoop2.core.registry.Soggetto soggetto = this.importerEngine.getSoggettoRegistro(idSoggettoReferente);
				if(this.importerEngine.isVisioneOggettiGlobale(this.userLogin)==false){
					if(this.userLogin.equals(soggetto.getSuperUser())==false){
						throw new Exception("Il soggetto referente ["+idSoggettoReferente+"] non è visibile/utilizzabile dall'utente collegato ("+this.userLogin+")");
					}
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
			StringBuilder warningInfoStatoFinale = new StringBuilder("");
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
			StringBuilder warningAderenzaWSDL = new StringBuilder("");
			if(org.openspcoop2.core.registry.constants.ServiceBinding.SOAP.equals(archiveAccordoServizioParteComune.getAccordoServizioParteComune().getServiceBinding())) {
				this.informazioniServizioAderentiWSDL(archiveAccordoServizioParteComune.getAccordoServizioParteComune(), warningAderenzaWSDL);
			}
			
			
			
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
			ArchiveEsitoImportDetail detail,
			Map<String, IDSoggetto> mapIdSoggettoDefault, Map<String, Boolean> mapAPIconReferente){
		
		IDAccordo idAccordoServizioComposto = archiveAccordoServizioComposto.getIdAccordoServizioParteComune();
		IDSoggetto idSoggettoReferente = archiveAccordoServizioComposto.getIdSoggettoReferente();
		IDAccordoCooperazione idAccordoCooperazione = archiveAccordoServizioComposto.getIdAccordoCooperazione();
		List<IDServizio> idServiziComponenti = archiveAccordoServizioComposto.getIdServiziComponenti();
		try{
			// --- fix soggetto referente --
			IProtocolFactory<?> protocolFactory = this.protocolFactoryManager.getProtocolFactoryByOrganizationType(idSoggettoReferente.getTipo());
			boolean APIconReferente = mapAPIconReferente.get(protocolFactory.getProtocol());
			if(!APIconReferente) {
				IDSoggetto soggettoDefaultProtocollo = mapIdSoggettoDefault.get(protocolFactory.getProtocol()); 
				if(!idSoggettoReferente.equals(soggettoDefaultProtocollo)) {
					archiveAccordoServizioComposto.getAccordoServizioParteComune().getSoggettoReferente().setTipo(soggettoDefaultProtocollo.getTipo());
					archiveAccordoServizioComposto.getAccordoServizioParteComune().getSoggettoReferente().setNome(soggettoDefaultProtocollo.getNome());
					idSoggettoReferente.setTipo(soggettoDefaultProtocollo.getTipo());
					idSoggettoReferente.setNome(soggettoDefaultProtocollo.getNome());
					idAccordoServizioComposto.getSoggettoReferente().setTipo(soggettoDefaultProtocollo.getTipo());
					idAccordoServizioComposto.getSoggettoReferente().setNome(soggettoDefaultProtocollo.getNome());
				}
				if(CostantiLabel.MODIPA_PROTOCOL_NAME.equals(protocolFactory.getProtocol())) {
					modifyUriAccordoCorrelato(archiveAccordoServizioComposto.getAccordoServizioParteComune(), soggettoDefaultProtocollo);
				}
			}
			
			// --- check esistenza ---
			if(this.updateAbilitato==false){
				if(this.importerEngine.existsAccordoServizioParteComune(idAccordoServizioComposto)){
					detail.setState(ArchiveStatoImport.UPDATE_NOT_ENABLED);
					return;
				}
			}
			
				
			
			// --- check elementi riferiti ---
			
			// soggetto referente
			if(APIconReferente) {
				if(this.importerEngine.existsSoggettoRegistro(idSoggettoReferente) == false ){
					throw new Exception("Soggetto proprietario ["+idSoggettoReferente+"] non esistente");
				}
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
			String protocolloAssociatoAccordo = this.protocolFactoryManager.getProtocolByOrganizationType(idSoggettoReferente.getTipo());
			// accordo cooperazione
			AccordoCooperazione ac = this.importerEngine.getAccordoCooperazione(idAccordoCooperazione);
			String protocolloAssociatoAccordoCooperazione = 
					this.protocolFactoryManager.getProtocolByOrganizationType(ac.getSoggettoReferente().getTipo());
			if(protocolloAssociatoAccordo.equals(protocolloAssociatoAccordoCooperazione)==false){
				throw new Exception("Soggetto referente ("+ac.getSoggettoReferente().getTipo()+"/"+ac.getSoggettoReferente().getNome()+
						") dell'accordo di cooperazione ["+idAccordoCooperazione+"] (protocollo:"+protocolloAssociatoAccordoCooperazione+
						") non utilizzabile in un accordo con soggetto referente ["+idSoggettoReferente+"] (protocollo:"+protocolloAssociatoAccordo+")");
			}
			// servizi componenti
			for (IDServizio idServizioComponente : idServiziComponenti) {
				String protocolloAssociatoServizioComponente = this.protocolFactoryManager.getProtocolByServiceType(idServizioComponente.getTipo());
				if(protocolloAssociatoAccordo.equals(protocolloAssociatoServizioComponente)==false){
					throw new Exception("ServizioComponente ["+idServizioComponente+"] (protocollo:"+protocolloAssociatoServizioComponente+
							") non utilizzabile in un accordo con soggetto referente ["+idSoggettoReferente+"] (protocollo:"+protocolloAssociatoAccordo+")");
				}
				String protocolloAssociatoSoggettoServizioComponente = this.protocolFactoryManager.getProtocolByOrganizationType(idServizioComponente.getSoggettoErogatore().getTipo());
				if(protocolloAssociatoAccordo.equals(protocolloAssociatoSoggettoServizioComponente)==false){
					throw new Exception("ServizioComponente ["+idServizioComponente+"] (protocollo:"+protocolloAssociatoSoggettoServizioComponente+
							") non utilizzabile in un accordo con soggetto referente ["+idSoggettoReferente+"] (protocollo:"+protocolloAssociatoAccordo+")");
				}
			}
			

			// ---- visibilita' oggetto riferiti ---
			// soggetto referente
			if(APIconReferente) {
				org.openspcoop2.core.registry.Soggetto soggetto = this.importerEngine.getSoggettoRegistro(idSoggettoReferente);
				if(this.importerEngine.isVisioneOggettiGlobale(this.userLogin)==false){
					if(this.userLogin.equals(soggetto.getSuperUser())==false){
						throw new Exception("Il soggetto referente ["+idSoggettoReferente+"] non è visibile/utilizzabile dall'utente collegato ("+this.userLogin+")");
					}
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
			Map<String, AccordoServizioParteSpecifica> serviziComponenti = new HashMap<String, AccordoServizioParteSpecifica>();
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
				IDServizio idServizioComponente = IDServizioFactory.getInstance().getIDServizioFromValues(componente.getTipo(), componente.getNome(), 
						componente.getTipoSoggetto(), componente.getNomeSoggetto(), 
						componente.getVersione()); 
				idServizioComponente.setAzione(componente.getAzione());
				AccordoServizioParteSpecifica asps = serviziComponenti.get(idServizioComponente.toString());
				if(asps==null){
					StringBuilder bfException = new StringBuilder();
					for (String idS : serviziComponenti.keySet()) {
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
			StringBuilder warningInfoStatoFinale = new StringBuilder("");
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
			StringBuilder warningAderenzaWSDL = new StringBuilder("");
			if(org.openspcoop2.core.registry.constants.ServiceBinding.SOAP.equals(archiveAccordoServizioComposto.getAccordoServizioParteComune().getServiceBinding())) {
				this.informazioniServizioAderentiWSDL(archiveAccordoServizioComposto.getAccordoServizioParteComune(), warningAderenzaWSDL);
			}
			
			
			
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
	
	
	private void informazioniServizioAderentiWSDL(AccordoServizioParteComune accordoServizioParteComune,StringBuilder anomalieRiscontrate) throws Exception{
		
		try{
		
			String indent = "\t\t";
			
			// lettura WSDL Concettuale
			StringBuilder strutturaWSDL = new StringBuilder("Struttura WSDL:\n");
			//strutturaWSDL.append(indent+"=========================================\n");
			byte[]wsdl = accordoServizioParteComune.getByteWsdlConcettuale();
			if(wsdl==null){
				throw new Exception("WSDLConcettuale non fornito");
			}
			OpenSPCoop2MessageFactory defaultFactory = OpenSPCoop2MessageFactory.getDefaultMessageFactory();
			RegistroOpenSPCoopUtilities wsdlUtility = new RegistroOpenSPCoopUtilities(defaultFactory, this.log);
			wsdl=wsdlUtility.eliminaImportASParteComune(wsdl);
			DefinitionWrapper wsdlObject = new DefinitionWrapper(wsdl,MessageXMLUtils.getInstance(defaultFactory));
			Map<String, List<String>> mapPortTypeOperations = new HashMap<>();
			java.util.Map<?,?> pts = wsdlObject.getAllPortTypes();
			if(pts==null || pts.size()<=0){
				this.log.warn("Non sono stati definiti port types nel wsdl concettuale");
			}else{
				java.util.Iterator<?> ptsIterator = pts.values().iterator();
				java.util.ArrayList<String> listPortTypesName = new ArrayList<>();
				while(ptsIterator.hasNext()) {
					javax.wsdl.PortType pt = (javax.wsdl.PortType) ptsIterator.next();
					String ptName = pt.getQName().getLocalPart();
					listPortTypesName.add(ptName);
					java.util.List<?> ops = pt.getOperations();
					java.util.Iterator<?> opIt = ops.iterator();
					java.util.ArrayList<String> listOperationName = new ArrayList<>();
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
			StringBuilder strutturaAccordoOpenSPCoop = new StringBuilder("Servizi/Azioni associati all'accordo:\n");
			//strutturaAccordoOpenSPCoop.append(indent+"=========================================\n");
			java.util.ArrayList<String> listPortTypesName = new ArrayList<>();
			Map<String, PortType> mapPtNameToObject = new HashMap<String, PortType>();
			for(int i=0;i<accordoServizioParteComune.sizePortTypeList();i++){
				PortType ptOpenSPCoop = accordoServizioParteComune.getPortType(i);
				listPortTypesName.add(ptOpenSPCoop.getNome());
				mapPtNameToObject.put(ptOpenSPCoop.getNome(), ptOpenSPCoop);
			}
			java.util.Collections.sort(listPortTypesName);
			for (String ptName : listPortTypesName) {
				PortType ptOpenSPCoop = mapPtNameToObject.get(ptName);
				strutturaAccordoOpenSPCoop.append(indent+". "+ptOpenSPCoop.getNome()+"\n");
				java.util.ArrayList<String> listOperationName = new ArrayList<>();
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
			ArchiveEsitoImportDetail detail,
			List<MappingErogazionePortaApplicativa> listMappingErogazionePA,
			Map<String, IDSoggetto> mapIdSoggettoDefault, Map<String, Boolean> mapAPIconReferente){
		
		IDAccordo idAccordoServizioParteComune = archiveAccordoServizioParteSpecifica.getIdAccordoServizioParteComune();
		IDServizio idAccordoServizioParteSpecifica = archiveAccordoServizioParteSpecifica.getIdAccordoServizioParteSpecifica();
		IDSoggetto idSoggettoErogatore = archiveAccordoServizioParteSpecifica.getIdSoggettoErogatore();
		String labelAccordoParteComune = "Accordo di Servizio Parte Comune";
		if(servizioComposto){
			labelAccordoParteComune = "Accordo di Servizio Composto";
		}
		try{
			// --- check esistenza ---
			if(this.updateAbilitato==false){
				if(this.importerEngine.existsAccordoServizioParteSpecifica(idAccordoServizioParteSpecifica)){
					
					List<IDPortaApplicativa> idPACheck = this.importerEngine.getIDPorteApplicativeAssociateErogazione(idAccordoServizioParteSpecifica);
					// gestione portaApplicativaAssociata
					if(archiveAccordoServizioParteSpecifica.getMappingPorteApplicativeAssociate()!=null &&
							archiveAccordoServizioParteSpecifica.getMappingPorteApplicativeAssociate().size()>0){
						for (MappingErogazionePortaApplicativa mappingPortaApplicativaAssociata : archiveAccordoServizioParteSpecifica.getMappingPorteApplicativeAssociate()) {
							if(idPACheck==null || idPACheck.size()<=0 || (idPACheck.contains(mappingPortaApplicativaAssociata.getIdPortaApplicativa())==false)){
								listMappingErogazionePA.add(mappingPortaApplicativaAssociata);
							}	
						}
					}
					
					detail.setState(ArchiveStatoImport.UPDATE_NOT_ENABLED);
					return;
				}
			}
			
				
			// --- check elementi riferiti ---
			
			// soggetto erogatore
			if(this.importerEngine.existsSoggettoRegistro(idSoggettoErogatore) == false ){
				throw new Exception("Soggetto erogatore ["+idSoggettoErogatore+"] non esistente");
			}
			
			// accordo di servizio parte comune
			IProtocolFactory<?> protocolFactory = this.protocolFactoryManager.getProtocolFactoryByOrganizationType(idSoggettoErogatore.getTipo());
			boolean APIconReferente = mapAPIconReferente.get(protocolFactory.getProtocol());
			if(!APIconReferente) {
				// Lascio il codice, ma lo switch viene effettuato prima durante la divisione asps che implementano accordi di servizio parte comune e composti
				IDSoggetto soggettoDefaultProtocollo = mapIdSoggettoDefault.get(protocolFactory.getProtocol()); 
				if(!idAccordoServizioParteComune.getSoggettoReferente().equals(soggettoDefaultProtocollo)) {
					idAccordoServizioParteComune.getSoggettoReferente().setTipo(soggettoDefaultProtocollo.getTipo());
					idAccordoServizioParteComune.getSoggettoReferente().setNome(soggettoDefaultProtocollo.getNome());
					archiveAccordoServizioParteSpecifica.getAccordoServizioParteSpecifica().setAccordoServizioParteComune(this.idAccordoFactory.getUriFromIDAccordo(idAccordoServizioParteComune));
				}
			}
			if(this.importerEngine.existsAccordoServizioParteComune(idAccordoServizioParteComune) == false ){
				throw new Exception(labelAccordoParteComune+" ["+idAccordoServizioParteComune+"] non esistente");
			}
			
			// controlli interni accordo
			org.openspcoop2.core.registry.Soggetto soggetto = this.importerEngine.getSoggettoRegistro(idSoggettoErogatore);
			AccordoServizioParteComune accordoServizioParteComune = this.importerEngine.getAccordoServizioParteComune(idAccordoServizioParteComune);
			AccordoServizioParteSpecifica old = null;
			long idAccordoServizioParteSpecificaLong = -1;
			boolean isUpdate = false;
			boolean servizioCorrelato = 
					TipologiaServizio.CORRELATO.
						equals(archiveAccordoServizioParteSpecifica.getAccordoServizioParteSpecifica().getTipologiaServizio());
			if(this.importerEngine.existsAccordoServizioParteSpecifica(idAccordoServizioParteSpecifica)){
				old = this.importerEngine.getAccordoServizioParteSpecifica(idAccordoServizioParteSpecifica);
				idAccordoServizioParteSpecificaLong = old.getId();
				isUpdate = true;
			}
			// uri
			String uriAccordo = null;
			try{
				uriAccordo = IDServizioFactory.getInstance().getUriFromIDServizio(idAccordoServizioParteSpecifica);
			}catch(Exception e){
				uriAccordo = idAccordoServizioParteSpecifica.toString(false);
			}
			// exists other service che implementa lo stesso accordo di servizio parte comune
			this.importerEngine.controlloUnicitaImplementazioneAccordoPerSoggetto(archiveAccordoServizioParteSpecifica.getAccordoServizioParteSpecifica().getPortType(), 
					idSoggettoErogatore, soggetto.getId(), 
					idAccordoServizioParteComune, accordoServizioParteComune.getId(), 
					idAccordoServizioParteSpecifica, idAccordoServizioParteSpecificaLong, 
					isUpdate, servizioCorrelato,
					isAbilitatoControlloUnicitaImplementazioneAccordoPerSoggetto,
					isAbilitatoControlloUnicitaImplementazionePortTypePerSoggetto);
			// exists other service with same tipo/nome e tipo/nome soggetto erogatore 
			if(this.importerEngine.existsAccordoServizioParteSpecifica(idAccordoServizioParteSpecifica)){
				if(!isUpdate){
					AccordoServizioParteSpecifica aspsCheck = this.importerEngine.getAccordoServizioParteSpecifica(idAccordoServizioParteSpecifica);
					throw new Exception("Servizio ["+uriAccordo+"] già esistente definito all'interno dell'accordo di servizio parte specifica ["+IDServizioFactory.getInstance().getUriFromAccordo(aspsCheck)+"]");
				}
				else{
					// change
					AccordoServizioParteSpecifica aspsCheck = this.importerEngine.getAccordoServizioParteSpecifica(idAccordoServizioParteSpecifica);
					IDServizio idAccordoASPSCheck = IDServizioFactory.getInstance().getIDServizioFromAccordo(aspsCheck);
					if(idAccordoASPSCheck.equals(idAccordoServizioParteSpecifica)==false){
						throw new Exception("Servizio ["+uriAccordo+"] già esistente definito all'interno dell'accordo di servizio parte specifica ["+IDServizioFactory.getInstance().getUriFromAccordo(aspsCheck)+"]");
					}
				}
			}
			
			// token negoziazione
			if(archiveAccordoServizioParteSpecifica.getAccordoServizioParteSpecifica()!=null && archiveAccordoServizioParteSpecifica.getAccordoServizioParteSpecifica().getConfigurazioneServizio()!=null) {
				if(archiveAccordoServizioParteSpecifica.getAccordoServizioParteSpecifica().getConfigurazioneServizio().getConnettore()!=null &&
						archiveAccordoServizioParteSpecifica.getAccordoServizioParteSpecifica().getConfigurazioneServizio().getConnettore().getProperties()!=null && 
						archiveAccordoServizioParteSpecifica.getAccordoServizioParteSpecifica().getConfigurazioneServizio().getConnettore().getProperties().containsKey(CostantiConnettori.CONNETTORE_TOKEN_POLICY)) {
					String policy = archiveAccordoServizioParteSpecifica.getAccordoServizioParteSpecifica().getConfigurazioneServizio().getConnettore().getProperties().get(CostantiConnettori.CONNETTORE_TOKEN_POLICY);
					if(this.importerEngine.existsGenericProperties_retrieve(policy) == false) {
						throw new Exception("Token Policy Negoziazione ["+policy+"] indicato nel connettore non esistente");
					}
				}
				if(archiveAccordoServizioParteSpecifica.getAccordoServizioParteSpecifica().getConfigurazioneServizio().sizeConfigurazioneAzioneList()>0) {
					for (ConfigurazioneServizioAzione confAzione : archiveAccordoServizioParteSpecifica.getAccordoServizioParteSpecifica().getConfigurazioneServizio().getConfigurazioneAzioneList()) {
						if(confAzione.getConnettore()!=null &&
								confAzione.getConnettore().getProperties()!=null && confAzione.getConnettore().getProperties().containsKey(CostantiConnettori.CONNETTORE_TOKEN_POLICY)) {
							String policy = confAzione.getConnettore().getProperties().get(CostantiConnettori.CONNETTORE_TOKEN_POLICY);
							if(this.importerEngine.existsGenericProperties_retrieve(policy) == false) {
								throw new Exception("Token Policy Negoziazione ["+policy+"] indicato nel connettore non esistente");
							}
						}
					}
				}
			}
			
			// plugin
			if(this.checkExistsPluginConfigurazione) {
				if(archiveAccordoServizioParteSpecifica.getAccordoServizioParteSpecifica().getConfigurazioneServizio().getConnettore()!=null&&
						archiveAccordoServizioParteSpecifica.getAccordoServizioParteSpecifica().getConfigurazioneServizio().getConnettore().getCustom()!=null &&
						archiveAccordoServizioParteSpecifica.getAccordoServizioParteSpecifica().getConfigurazioneServizio().getConnettore().getCustom() &&
						archiveAccordoServizioParteSpecifica.getAccordoServizioParteSpecifica().getConfigurazioneServizio().getConnettore().getTipo()!=null) {
					TipiConnettore tipo = TipiConnettore.toEnumFromName(archiveAccordoServizioParteSpecifica.getAccordoServizioParteSpecifica().getConfigurazioneServizio().getConnettore().getTipo());
					if(tipo==null) {
						if(this.importerEngine.existsPluginClasse(TipoPlugin.CONNETTORE.getValue(), archiveAccordoServizioParteSpecifica.getAccordoServizioParteSpecifica().getConfigurazioneServizio().getConnettore().getTipo()) == false ){
							throw new Exception("Plugin '"+TipoPlugin.CONNETTORE.getValue()+"' ["+archiveAccordoServizioParteSpecifica.getAccordoServizioParteSpecifica().getConfigurazioneServizio().getConnettore().getTipo()+"] non esistente nel registro");
						}
					}
				}
				if(archiveAccordoServizioParteSpecifica.getAccordoServizioParteSpecifica().getConfigurazioneServizio().sizeConfigurazioneAzioneList()>0) {
					for (ConfigurazioneServizioAzione confAzione : archiveAccordoServizioParteSpecifica.getAccordoServizioParteSpecifica().getConfigurazioneServizio().getConfigurazioneAzioneList()) {
						if(confAzione.getConnettore()!=null &&
								confAzione.getConnettore().getCustom()!=null &&
								confAzione.getConnettore().getCustom() &&
								confAzione.getConnettore().getTipo()!=null) {
							TipiConnettore tipo = TipiConnettore.toEnumFromName(confAzione.getConnettore().getTipo());
							if(tipo==null) {
								if(this.importerEngine.existsPluginClasse(TipoPlugin.CONNETTORE.getValue(), confAzione.getConnettore().getTipo()) == false ){
									throw new Exception("Plugin '"+TipoPlugin.CONNETTORE.getValue()+"' ["+confAzione.getConnettore().getTipo()+"] non esistente nel registro");
								}
							}
						}
					}
				}
			}
			
			
			
			// --- compatibilita' elementi riferiti ---
			String protocolloAssociatoAccordo = this.protocolFactoryManager.getProtocolByOrganizationType(idSoggettoErogatore.getTipo());
			// accordo di servizio parte comune
			String protocolloAssociatoAccordoParteComune = this.protocolFactoryManager.getProtocolByOrganizationType(idAccordoServizioParteComune.getSoggettoReferente().getTipo());
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
			StringBuilder warningInfoStatoFinale = new StringBuilder("");
			if(archiveAccordoServizioParteSpecifica.getAccordoServizioParteSpecifica().getStatoPackage()==null){
				if(this.gestioneWorkflowStatiAccordi){
					boolean gestioneWsdlImplementativo = false;
					try{
						IProtocolFactory<?> protocol = ProtocolFactoryManager.getInstance().getProtocolFactoryByServiceType(archiveAccordoServizioParteSpecifica.getAccordoServizioParteSpecifica().getTipo());
						gestioneWsdlImplementativo = protocol.createProtocolConfiguration().
								isSupportoPortiAccessoAccordiParteSpecifica(toMessageServiceBinding(accordoServizioParteComune.getServiceBinding()),
										formatoSpecifica2InterfaceType(accordoServizioParteComune.getFormatoSpecifica()));
						archiveAccordoServizioParteSpecifica.getAccordoServizioParteSpecifica().setStatoPackage(StatiAccordo.finale.toString());
						this.importerEngine.validaStatoAccordoServizioParteSpecifica(archiveAccordoServizioParteSpecifica.getAccordoServizioParteSpecifica(),
								gestioneWsdlImplementativo, false);
					}catch(ValidazioneStatoPackageException validazioneException){
						try{
							archiveAccordoServizioParteSpecifica.getAccordoServizioParteSpecifica().setStatoPackage(StatiAccordo.operativo.toString());
							this.importerEngine.validaStatoAccordoServizioParteSpecifica(archiveAccordoServizioParteSpecifica.getAccordoServizioParteSpecifica(),
									gestioneWsdlImplementativo, false);
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
			
			List<IDPortaApplicativa> idPACheck = null;
			ArchiveStatoImport statoImport = null;
			
			if(this.importerEngine.existsAccordoServizioParteSpecifica(idAccordoServizioParteSpecifica)){
				
				archiveAccordoServizioParteSpecifica.getAccordoServizioParteSpecifica().setId(old.getId());
				IDServizio oldIDServizioForUpdate = IDServizioFactory.getInstance().getIDServizioFromValues(old.getTipo(), old.getNome(), 
						old.getTipoSoggettoErogatore(),	old.getNomeSoggettoErogatore(),
						old.getVersione());
				archiveAccordoServizioParteSpecifica.getAccordoServizioParteSpecifica().setOldIDServizioForUpdate(oldIDServizioForUpdate);
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
				statoImport = ArchiveStatoImport.UPDATED;
				
				idPACheck = this.importerEngine.getIDPorteApplicativeAssociateErogazione(idAccordoServizioParteSpecifica);
					
			}
			// --- create ---
			else{
				this.importerEngine.createAccordoServizioParteSpecifica(archiveAccordoServizioParteSpecifica.getAccordoServizioParteSpecifica());
				statoImport = ArchiveStatoImport.CREATED;
			}
			
			// gestione portaApplicativaAssociata
			if(archiveAccordoServizioParteSpecifica.getMappingPorteApplicativeAssociate()!=null &&
					archiveAccordoServizioParteSpecifica.getMappingPorteApplicativeAssociate().size()>0){
				for (MappingErogazionePortaApplicativa mappingPortaApplicativaAssociata : archiveAccordoServizioParteSpecifica.getMappingPorteApplicativeAssociate()) {
					if(idPACheck==null || idPACheck.size()<=0 || (idPACheck.contains(mappingPortaApplicativaAssociata.getIdPortaApplicativa())==false)){
						listMappingErogazionePA.add(mappingPortaApplicativaAssociata);
					}	
				}
			}
			
			// update info importazione
			detail.setState(statoImport);
			detail.setStateDetail(warningInfoStatoFinale.toString());
				

			
		}			
		catch(Exception e){
			this.log.error("Errore durante l'import dell'accordo di servizio parte specifica ["+idAccordoServizioParteSpecifica+"]: "+e.getMessage(),e);
			detail.setState(ArchiveStatoImport.ERROR);
			detail.setException(e);
		}
	}
	private ServiceBinding toMessageServiceBinding(org.openspcoop2.core.registry.constants.ServiceBinding serviceBinding) {
		if(serviceBinding == null)
			return null;
		
		switch (serviceBinding) {
		case REST:
			return ServiceBinding.REST;
		case SOAP:
		default:
			return ServiceBinding.SOAP;
		}			
	}
	private InterfaceType formatoSpecifica2InterfaceType(org.openspcoop2.core.registry.constants.FormatoSpecifica formatoSpecifica) {
		if(formatoSpecifica == null)
			return null;
		
		switch (formatoSpecifica) {
		case SWAGGER_2:
			return InterfaceType.SWAGGER_2;
		case OPEN_API_3:
			return InterfaceType.OPEN_API_3;
		case WADL:
			return InterfaceType.WADL;

			
		case WSDL_11:
		default:
			return InterfaceType.WSDL_11;
		}			
	}
	
	
	
	public void importFruitore(ArchiveFruitore archiveFruitore,
			ArchiveEsitoImportDetail detail, 
			List<MappingFruizionePortaDelegata> listMappingFruizionePD){
		
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
			if(this.updateAbilitato==false){
				if(old!=null){
					
					// gestione portaDelegataAssociata
					if(archiveFruitore.getMappingPorteDelegateAssociate()!=null &&
							archiveFruitore.getMappingPorteDelegateAssociate().size()>0){
						List<IDPortaDelegata> idPDCheck = this.importerEngine.getIDPorteDelegateAssociateFruizione(archiveFruitore.getIdAccordoServizioParteSpecifica(), archiveFruitore.getIdSoggettoFruitore());
						for (MappingFruizionePortaDelegata mappingPortaDelegataAssociata : archiveFruitore.getMappingPorteDelegateAssociate()) {
							if(idPDCheck==null || idPDCheck.size()<=0 || idPDCheck.contains(mappingPortaDelegataAssociata.getIdPortaDelegata())==false){
								listMappingFruizionePD.add(mappingPortaDelegataAssociata);
							}	
						}
					}
					
					detail.setState(ArchiveStatoImport.UPDATE_NOT_ENABLED);
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
			
			// token negoziazione
			if(archiveFruitore.getFruitore().getConnettore()!=null &&
					archiveFruitore.getFruitore().getConnettore().getProperties()!=null && 
					archiveFruitore.getFruitore().getConnettore().getProperties().containsKey(CostantiConnettori.CONNETTORE_TOKEN_POLICY)) {
				String policy = archiveFruitore.getFruitore().getConnettore().getProperties().get(CostantiConnettori.CONNETTORE_TOKEN_POLICY);
				if(this.importerEngine.existsGenericProperties_retrieve(policy) == false) {
					throw new Exception("Token Policy Negoziazione ["+policy+"] indicato nel connettore non esistente");
				}
			}
			if(archiveFruitore.getFruitore().sizeConfigurazioneAzioneList()>0) {
				for (ConfigurazioneServizioAzione confAzione : archiveFruitore.getFruitore().getConfigurazioneAzioneList()) {
					if(confAzione.getConnettore()!=null &&
							confAzione.getConnettore().getProperties()!=null && 
							confAzione.getConnettore().getProperties().containsKey(CostantiConnettori.CONNETTORE_TOKEN_POLICY)) {
						String policy = confAzione.getConnettore().getProperties().get(CostantiConnettori.CONNETTORE_TOKEN_POLICY);
						if(this.importerEngine.existsGenericProperties_retrieve(policy) == false) {
							throw new Exception("Token Policy Negoziazione ["+policy+"] indicato nel connettore non esistente");
						}
					}
				}
			}
			
			// plugin
			if(this.checkExistsPluginConfigurazione) {
				if(archiveFruitore.getFruitore().getConnettore()!=null &&
						archiveFruitore.getFruitore().getConnettore().getCustom()!=null &&
						archiveFruitore.getFruitore().getConnettore().getCustom() &&
						archiveFruitore.getFruitore().getConnettore().getTipo()!=null) {
					TipiConnettore tipo = TipiConnettore.toEnumFromName(archiveFruitore.getFruitore().getConnettore().getTipo());
					if(tipo==null) {
						if(this.importerEngine.existsPluginClasse(TipoPlugin.CONNETTORE.getValue(), archiveFruitore.getFruitore().getConnettore().getTipo()) == false ){
							throw new Exception("Plugin '"+TipoPlugin.CONNETTORE.getValue()+"' ["+archiveFruitore.getFruitore().getConnettore().getTipo()+"] non esistente nel registro");
						}
					}
				}
				if(archiveFruitore.getFruitore().sizeConfigurazioneAzioneList()>0) {
					for (ConfigurazioneServizioAzione confAzione : archiveFruitore.getFruitore().getConfigurazioneAzioneList()) {
						if(confAzione.getConnettore()!=null &&
								confAzione.getConnettore().getCustom()!=null &&
								confAzione.getConnettore().getCustom() &&
								confAzione.getConnettore().getTipo()!=null) {
							TipiConnettore tipo = TipiConnettore.toEnumFromName(confAzione.getConnettore().getTipo());
							if(tipo==null) {
								if(this.importerEngine.existsPluginClasse(TipoPlugin.CONNETTORE.getValue(), confAzione.getConnettore().getTipo()) == false ){
									throw new Exception("Plugin '"+TipoPlugin.CONNETTORE.getValue()+"' ["+confAzione.getConnettore().getTipo()+"] non esistente nel registro");
								}
							}
						}
					}
				}
			}
		
			
			// --- compatibilita' elementi riferiti ---
			String protocolloAssociatoFruitore = this.protocolFactoryManager.getProtocolByOrganizationType(idSoggettoFruitore.getTipo());
			// accordo di servizio parte specifica
			String protocolloAssociatoAccordoParteSpecifica = this.protocolFactoryManager.getProtocolByOrganizationType(idAccordoServizioParteSpecifica.getSoggettoErogatore().getTipo());
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
			StringBuilder warningInfoStatoFinale = new StringBuilder("");
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
			IDServizio oldIDServizioForUpdate = IDServizioFactory.getInstance().getIDServizioFromValues(oldAccordo.getTipo(), oldAccordo.getNome(), 
					oldAccordo.getTipoSoggettoErogatore(),oldAccordo.getNomeSoggettoErogatore(),
					oldAccordo.getVersione());
			oldAccordo.setOldIDServizioForUpdate(oldIDServizioForUpdate);
			this.importerEngine.updateAccordoServizioParteSpecifica(oldAccordo);
			
			// gestione portaDelegataAssociata
			if(archiveFruitore.getMappingPorteDelegateAssociate()!=null &&
					archiveFruitore.getMappingPorteDelegateAssociate().size()>0){
				List<IDPortaDelegata> idPDCheck = this.importerEngine.getIDPorteDelegateAssociateFruizione(archiveFruitore.getIdAccordoServizioParteSpecifica(), archiveFruitore.getIdSoggettoFruitore());
				for (MappingFruizionePortaDelegata mappingPortaDelegataAssociata : archiveFruitore.getMappingPorteDelegateAssociate()) {
					if(idPDCheck==null || idPDCheck.size()<=0 || idPDCheck.contains(mappingPortaDelegataAssociata.getIdPortaDelegata())==false){
						listMappingFruizionePD.add(mappingPortaDelegataAssociata);
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
					detail.setState(ArchiveStatoImport.UPDATE_NOT_ENABLED);
					return;
				}
			}
			
			
			// --- lettura elementi riferiti all'interno della porta delegata ---
			PortaDelegata pd = archivePortaDelegata.getPortaDelegata();
			IDSoggetto idSoggettoErogatore = null;
			IDServizio idServizio = null;
			if(pd.getSoggettoErogatore()!=null &&
					pd.getSoggettoErogatore().getTipo()!=null &&
					pd.getSoggettoErogatore().getNome()!=null ){
				idSoggettoErogatore = new IDSoggetto(pd.getSoggettoErogatore().getTipo(), pd.getSoggettoErogatore().getNome());
			}
			if(idSoggettoErogatore!=null){
				if(pd.getServizio()!=null &&
						pd.getServizio().getTipo()!=null &&
						pd.getServizio().getNome()!=null &&
						pd.getServizio().getVersione()!=null){
					idServizio = IDServizioFactory.getInstance().getIDServizioFromValues(pd.getServizio().getTipo(), pd.getServizio().getNome(), 
							idSoggettoErogatore, pd.getServizio().getVersione());
				}
			}
			
			
			// --- check elementi riferiti ---
			
			// soggetto proprietario
			if(this.importerEngine.existsSoggettoConfigurazione(idSoggettoProprietario) == false ){
				throw new Exception("Soggetto proprietario ["+idSoggettoProprietario+"] non esistente");
			}
			
			// soggetto erogatore
			if(idSoggettoErogatore!=null){
				if(this.importerEngine.existsSoggettoRegistro(idSoggettoErogatore) == false ){
					throw new Exception("Soggetto erogatore riferito nella PD ["+idSoggettoErogatore+"] non esistente");
				}
			}
			
			// accordo servizio parte specifica
			if(idServizio!=null){
				if(this.importerEngine.existsAccordoServizioParteSpecifica(idServizio) == false ){
					throw new Exception("Servizio riferito nella PD ["+idServizio+"] non esistente");
				}
			}
			
			// ruoli
			if(archivePortaDelegata.getPortaDelegata().getRuoli()!=null && archivePortaDelegata.getPortaDelegata().getRuoli().sizeRuoloList()>0){
				for (int i = 0; i < archivePortaDelegata.getPortaDelegata().getRuoli().sizeRuoloList(); i++) {
					IDRuolo idRuolo = new IDRuolo(archivePortaDelegata.getPortaDelegata().getRuoli().getRuolo(i).getNome());
					if(this.importerEngine.existsRuolo(idRuolo) == false ){
						throw new Exception("Ruolo ["+idRuolo.getNome()+"] associato non esiste");
					}	
				}
			}
			
			// scope
			if(archivePortaDelegata.getPortaDelegata().getScope()!=null && archivePortaDelegata.getPortaDelegata().getScope().sizeScopeList()>0){
				for (int i = 0; i < archivePortaDelegata.getPortaDelegata().getScope().sizeScopeList(); i++) {
					IDScope idScope = new IDScope(archivePortaDelegata.getPortaDelegata().getScope().getScope(i).getNome());
					if(this.importerEngine.existsScope(idScope) == false ){
						throw new Exception("Scope ["+idScope.getNome()+"] associato non esiste");
					}	
				}
			}
			
			// servizi applicativi autorizzati
			if(archivePortaDelegata.getPortaDelegata().getServizioApplicativoList()!=null && archivePortaDelegata.getPortaDelegata().sizeServizioApplicativoList()>0){
				for (int i = 0; i < archivePortaDelegata.getPortaDelegata().sizeServizioApplicativoList(); i++) {
					IDServizioApplicativo idSaAuth = new IDServizioApplicativo();
					idSaAuth.setIdSoggettoProprietario(idSoggettoProprietario);
					idSaAuth.setNome(archivePortaDelegata.getPortaDelegata().getServizioApplicativo(i).getNome());
					if(this.importerEngine.existsServizioApplicativo(idSaAuth) == false ){
						throw new Exception("Applicativo ["+idSaAuth+"] indicato nel controllo degli accessi non esistente");
					}	
				}
			}
			
			// token validazione
			if(archivePortaDelegata.getPortaDelegata().getGestioneToken()!=null && 
					archivePortaDelegata.getPortaDelegata().getGestioneToken().getPolicy()!=null && 
					!"".equals(archivePortaDelegata.getPortaDelegata().getGestioneToken().getPolicy())) {
				if(this.importerEngine.existsGenericProperties_validation(archivePortaDelegata.getPortaDelegata().getGestioneToken().getPolicy()) == false) {
					throw new Exception("Token Policy Validazione ["+archivePortaDelegata.getPortaDelegata().getGestioneToken().getPolicy()+"] indicato nel controllo degli accessi non esistente");
				}
			}
			
			// plugin
			if(this.checkExistsPluginConfigurazione) {
				
				if(archivePortaDelegata.getPortaDelegata()!=null && StringUtils.isNotEmpty(archivePortaDelegata.getPortaDelegata().getAutenticazione())) {
					TipoAutenticazione tipo = TipoAutenticazione.toEnumConstant(archivePortaDelegata.getPortaDelegata().getAutenticazione(), false);
					if(tipo==null) {
						if(this.importerEngine.existsPluginClasse(TipoPlugin.AUTENTICAZIONE.getValue(), archivePortaDelegata.getPortaDelegata().getAutenticazione()) == false ){
							throw new Exception("Plugin '"+TipoPlugin.AUTENTICAZIONE.getValue()+"' ["+archivePortaDelegata.getPortaDelegata().getAutenticazione()+"] non esistente nel registro");
						}
					}
				}
				if(archivePortaDelegata.getPortaDelegata()!=null && StringUtils.isNotEmpty(archivePortaDelegata.getPortaDelegata().getAutorizzazione())) {
					TipoAutorizzazione tipo = TipoAutorizzazione.toEnumConstant(archivePortaDelegata.getPortaDelegata().getAutorizzazione(), false);
					if(tipo==null) {
						if(this.importerEngine.existsPluginClasse(TipoPlugin.AUTORIZZAZIONE.getValue(), archivePortaDelegata.getPortaDelegata().getAutorizzazione()) == false ){
							throw new Exception("Plugin '"+TipoPlugin.AUTORIZZAZIONE.getValue()+"' ["+archivePortaDelegata.getPortaDelegata().getAutorizzazione()+"] non esistente nel registro");
						}
					}
				}
				if(archivePortaDelegata.getPortaDelegata()!=null && StringUtils.isNotEmpty(archivePortaDelegata.getPortaDelegata().getAutorizzazioneContenuto())) {
					if(!CostantiConfigurazione.AUTORIZZAZIONE_CONTENUTO_BUILT_IN.equals(archivePortaDelegata.getPortaDelegata().getAutorizzazioneContenuto())) {
						if(this.importerEngine.existsPluginClasse(TipoPlugin.AUTORIZZAZIONE_CONTENUTI.getValue(), archivePortaDelegata.getPortaDelegata().getAutorizzazioneContenuto()) == false ){
							throw new Exception("Plugin '"+TipoPlugin.AUTORIZZAZIONE_CONTENUTI.getValue()+"' ["+archivePortaDelegata.getPortaDelegata().getAutorizzazioneContenuto()+"] non esistente nel registro");
						}
					}
				}
//				// TODO:
//				TipoPlugin.INTEGRAZIONE;
//				TipoPlugin.MESSAGE_HANDLER;
				
			}
			
			
			
			// --- compatibilita' elementi riferiti ---
			String protocolloAssociatoSoggettoProprietario = this.protocolFactoryManager.getProtocolByOrganizationType(idSoggettoProprietario.getTipo());
			// erogatore
			if(idSoggettoErogatore!=null){
				String protocolloAssociatoSoggettoErogatore = this.protocolFactoryManager.getProtocolByOrganizationType(idSoggettoErogatore.getTipo());
				if(protocolloAssociatoSoggettoProprietario.equals(protocolloAssociatoSoggettoErogatore)==false){
					throw new Exception("SoggettoErogatore ["+idSoggettoErogatore+"] (protocollo:"+protocolloAssociatoSoggettoErogatore+
							") non utilizzabile in una porta delegata appartenete al soggetto ["+idSoggettoProprietario+"] (protocollo:"+protocolloAssociatoSoggettoProprietario+")");
				}
			}
			// accordo di servizio parte specifica
			if(idServizio!=null){
				String protocolloAssociatoAccordoParteSpecifica = this.protocolFactoryManager.getProtocolByServiceType(idServizio.getTipo());
				if(protocolloAssociatoSoggettoProprietario.equals(protocolloAssociatoAccordoParteSpecifica)==false){
					throw new Exception("AccordoServizioParteSpecifica ["+idServizio+"] (protocollo:"+protocolloAssociatoAccordoParteSpecifica+
							") con servizio non utilizzabile in una porta delegata appartenete al soggetto ["+idSoggettoProprietario+"] (protocollo:"+protocolloAssociatoSoggettoProprietario+")");
				}
				String protocolloAssociatoSoggettoAccordoParteSpecifica = this.protocolFactoryManager.getProtocolByOrganizationType(idServizio.getSoggettoErogatore().getTipo());
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
			
			
			// --- set dati obbligatori nel db ----
			org.openspcoop2.core.config.driver.utils.XMLDataConverter.
				impostaInformazioniConfigurazione_PortaDelegata(pd);
			
			// --- correzione nomi provider per backward compatibility ----
			MerlinProvider.correctProviderName(pd.getMessageSecurity());
			
			
			// --- upload ---
			if(this.importerEngine.existsPortaDelegata(idPortaDelegata)){
				
				org.openspcoop2.core.config.PortaDelegata old = this.importerEngine.getPortaDelegata(idPortaDelegata);
				pd.setId(old.getId());
				IDPortaDelegata oldIDPortaDelegataForUpdate = new IDPortaDelegata();
				oldIDPortaDelegataForUpdate.setNome(old.getNome());
				pd.setOldIDPortaDelegataForUpdate(oldIDPortaDelegataForUpdate);
				
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
		IDSoggetto idSoggettoProprietario = archivePortaApplicativa.getIdSoggettoProprietario();
		try{
			// --- check esistenza ---
			if(this.updateAbilitato==false){
				if(this.importerEngine.existsPortaApplicativa(idPortaApplicativa)){
					detail.setState(ArchiveStatoImport.UPDATE_NOT_ENABLED);
					return;
				}
			}
			
			
			// --- lettura elementi riferiti all'interno della porta delegata ---
			
			if(idSoggettoProprietario==null) {
				throw new Exception("Soggetto proprietario non definito");
			}
			
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
						pa.getServizio().getNome()!=null &&
						pa.getServizio().getVersione()!=null){
					idServizio = IDServizioFactory.getInstance().getIDServizioFromValues(pa.getServizio().getTipo(), pa.getServizio().getNome(), 
							idSoggettoErogatore, pa.getServizio().getVersione());
				}
			}
			
			
			// --- check elementi riferiti ---
			
			// soggetto proprietario
			if(this.importerEngine.existsSoggettoConfigurazione(idSoggettoProprietario) == false ){
				throw new Exception("Soggetto proprietario ["+idSoggettoProprietario+"] non esistente");
			}
			
			// soggetto erogatore
			if(idSoggettoErogatore!=null){
				if(this.importerEngine.existsSoggettoRegistro(idSoggettoErogatore) == false ){
					throw new Exception("Soggetto erogatore riferito nella PA ["+idSoggettoErogatore+"] non esistente");
				}
			}
			
			// accordo servizio parte specifica
			if(idServizio!=null){
				if(this.importerEngine.existsAccordoServizioParteSpecifica(idServizio) == false ){
					throw new Exception("Servizio riferito nella PA ["+idServizio+"] non esistente");
				}
			}
			
			// ruoli
			if(archivePortaApplicativa.getPortaApplicativa().getRuoli()!=null && archivePortaApplicativa.getPortaApplicativa().getRuoli().sizeRuoloList()>0){
				for (int i = 0; i < archivePortaApplicativa.getPortaApplicativa().getRuoli().sizeRuoloList(); i++) {
					IDRuolo idRuolo = new IDRuolo(archivePortaApplicativa.getPortaApplicativa().getRuoli().getRuolo(i).getNome());
					if(this.importerEngine.existsRuolo(idRuolo) == false ){
						throw new Exception("Ruolo ["+idRuolo.getNome()+"] associato non esiste");
					}	
				}
			}
			
			// scope
			if(archivePortaApplicativa.getPortaApplicativa().getScope()!=null && archivePortaApplicativa.getPortaApplicativa().getScope().sizeScopeList()>0){
				for (int i = 0; i < archivePortaApplicativa.getPortaApplicativa().getScope().sizeScopeList(); i++) {
					IDScope idScope = new IDScope(archivePortaApplicativa.getPortaApplicativa().getScope().getScope(i).getNome());
					if(this.importerEngine.existsScope(idScope) == false ){
						throw new Exception("Scope ["+idScope.getNome()+"] associato non esiste");
					}	
				}
			}
			
			// soggetti autorizzati
			if(archivePortaApplicativa.getPortaApplicativa().getSoggetti()!=null && archivePortaApplicativa.getPortaApplicativa().getSoggetti().sizeSoggettoList()>0){
				for (int i = 0; i < archivePortaApplicativa.getPortaApplicativa().getSoggetti().sizeSoggettoList(); i++) {
					IDSoggetto idSoggettoAuth = new IDSoggetto(archivePortaApplicativa.getPortaApplicativa().getSoggetti().getSoggetto(i).getTipo(),
							archivePortaApplicativa.getPortaApplicativa().getSoggetti().getSoggetto(i).getNome());
					if(this.importerEngine.existsSoggettoRegistro(idSoggettoAuth) == false ){
						throw new Exception("Soggetto ["+idSoggettoAuth+"] indicato nel controllo degli accessi non esistente");
					}	
				}
			}
			
			// servizi applicativi autorizzati
			if(archivePortaApplicativa.getPortaApplicativa().getServiziApplicativiAutorizzati()!=null && archivePortaApplicativa.getPortaApplicativa().getServiziApplicativiAutorizzati().sizeServizioApplicativoList()>0){
				for (int i = 0; i < archivePortaApplicativa.getPortaApplicativa().getServiziApplicativiAutorizzati().sizeServizioApplicativoList(); i++) {
					IDSoggetto idSoggettoAuth = new IDSoggetto(archivePortaApplicativa.getPortaApplicativa().getServiziApplicativiAutorizzati().getServizioApplicativo(i).getTipoSoggettoProprietario(),
							archivePortaApplicativa.getPortaApplicativa().getServiziApplicativiAutorizzati().getServizioApplicativo(i).getNomeSoggettoProprietario());
					IDServizioApplicativo idSaAuth = new IDServizioApplicativo();
					idSaAuth.setIdSoggettoProprietario(idSoggettoAuth);
					idSaAuth.setNome(archivePortaApplicativa.getPortaApplicativa().getServiziApplicativiAutorizzati().getServizioApplicativo(i).getNome());
					if(this.importerEngine.existsServizioApplicativo(idSaAuth) == false ){
						throw new Exception("Applicativo ["+idSaAuth+"] indicato nel controllo degli accessi non esistente");
					}	
				}
			}
			
			// servizi applicativi erogatori
			if(archivePortaApplicativa.getPortaApplicativa().getServizioApplicativoList()!=null && archivePortaApplicativa.getPortaApplicativa().sizeServizioApplicativoList()>0){
				for (int i = 0; i < archivePortaApplicativa.getPortaApplicativa().sizeServizioApplicativoList(); i++) {
					IDServizioApplicativo idSaErogatore = new IDServizioApplicativo();
					idSaErogatore.setIdSoggettoProprietario(idSoggettoErogatore);
					idSaErogatore.setNome(archivePortaApplicativa.getPortaApplicativa().getServizioApplicativo(i).getNome());
					if(this.importerEngine.existsServizioApplicativo(idSaErogatore) == false ){
						throw new Exception("Servizio Applicativo ["+idSaErogatore+"] (erogatore) non esistente");
					}	
				}
			}
			
			// token validazione
			if(archivePortaApplicativa.getPortaApplicativa().getGestioneToken()!=null && 
					archivePortaApplicativa.getPortaApplicativa().getGestioneToken().getPolicy()!=null && 
					!"".equals(archivePortaApplicativa.getPortaApplicativa().getGestioneToken().getPolicy())) {
				if(this.importerEngine.existsGenericProperties_validation(archivePortaApplicativa.getPortaApplicativa().getGestioneToken().getPolicy()) == false) {
					throw new Exception("Token Policy Validazione ["+archivePortaApplicativa.getPortaApplicativa().getGestioneToken().getPolicy()+"] indicato nel controllo degli accessi non esistente");
				}
			}
			
			// plugin
			if(this.checkExistsPluginConfigurazione) {
				
				if(archivePortaApplicativa.getPortaApplicativa()!=null && StringUtils.isNotEmpty(archivePortaApplicativa.getPortaApplicativa().getAutenticazione())) {
					TipoAutenticazione tipo = TipoAutenticazione.toEnumConstant(archivePortaApplicativa.getPortaApplicativa().getAutenticazione(), false);
					if(tipo==null) {
						if(this.importerEngine.existsPluginClasse(TipoPlugin.AUTENTICAZIONE.getValue(), archivePortaApplicativa.getPortaApplicativa().getAutenticazione()) == false ){
							throw new Exception("Plugin '"+TipoPlugin.AUTENTICAZIONE.getValue()+"' ["+archivePortaApplicativa.getPortaApplicativa().getAutenticazione()+"] non esistente nel registro");
						}
					}
				}
				if(archivePortaApplicativa.getPortaApplicativa()!=null && StringUtils.isNotEmpty(archivePortaApplicativa.getPortaApplicativa().getAutorizzazione())) {
					TipoAutorizzazione tipo = TipoAutorizzazione.toEnumConstant(archivePortaApplicativa.getPortaApplicativa().getAutorizzazione(), false);
					if(tipo==null) {
						if(this.importerEngine.existsPluginClasse(TipoPlugin.AUTORIZZAZIONE.getValue(), archivePortaApplicativa.getPortaApplicativa().getAutorizzazione()) == false ){
							throw new Exception("Plugin '"+TipoPlugin.AUTORIZZAZIONE.getValue()+"' ["+archivePortaApplicativa.getPortaApplicativa().getAutorizzazione()+"] non esistente nel registro");
						}
					}
				}
				if(archivePortaApplicativa.getPortaApplicativa()!=null && StringUtils.isNotEmpty(archivePortaApplicativa.getPortaApplicativa().getAutorizzazioneContenuto())) {
					if(!CostantiConfigurazione.AUTORIZZAZIONE_CONTENUTO_BUILT_IN.equals(archivePortaApplicativa.getPortaApplicativa().getAutorizzazioneContenuto())) {
						if(this.importerEngine.existsPluginClasse(TipoPlugin.AUTORIZZAZIONE_CONTENUTI.getValue(), archivePortaApplicativa.getPortaApplicativa().getAutorizzazioneContenuto()) == false ){
							throw new Exception("Plugin '"+TipoPlugin.AUTORIZZAZIONE_CONTENUTI.getValue()+"' ["+archivePortaApplicativa.getPortaApplicativa().getAutorizzazioneContenuto()+"] non esistente nel registro");
						}
					}
				}
				if(archivePortaApplicativa.getPortaApplicativa()!=null && archivePortaApplicativa.getPortaApplicativa().getBehaviour()!=null && archivePortaApplicativa.getPortaApplicativa().getBehaviour().getNome()!=null) {
					TipoBehaviour behaviourType = TipoBehaviour.toEnumConstant(archivePortaApplicativa.getPortaApplicativa().getBehaviour().getNome());
					if(TipoBehaviour.CUSTOM.equals(behaviourType)){
						if(this.importerEngine.existsPluginClasse(TipoPlugin.BEHAVIOUR.getValue(), archivePortaApplicativa.getPortaApplicativa().getBehaviour().getNome()) == false ){
							throw new Exception("Plugin '"+TipoPlugin.BEHAVIOUR.getValue()+"' ["+archivePortaApplicativa.getPortaApplicativa().getBehaviour().getNome()+"] non esistente nel registro");
						}
					}
				}
//				// TODO:
//				TipoPlugin.INTEGRAZIONE;
//				TipoPlugin.MESSAGE_HANDLER;
				
			}
			
			
			// --- compatibilita' elementi riferiti ---
			String protocolloAssociatoSoggettoProprietario = this.protocolFactoryManager.getProtocolByOrganizationType(idSoggettoProprietario.getTipo());
			// erogatore
			String protocolloAssociatoSoggettoErogatore = this.protocolFactoryManager.getProtocolByOrganizationType(idSoggettoErogatore.getTipo());
			if(protocolloAssociatoSoggettoProprietario.equals(protocolloAssociatoSoggettoErogatore)==false){
				throw new Exception("SoggettoErogatore ["+idSoggettoErogatore+"] (protocollo:"+protocolloAssociatoSoggettoErogatore+
						") non utilizzabile in una porta applicativa appartenete al soggetto ["+idSoggettoProprietario+"] (protocollo:"+protocolloAssociatoSoggettoProprietario+")");
			}
			// accordo di servizio parte specifica
			if(idServizio!=null) {
				String protocolloAssociatoAccordoParteSpecifica = this.protocolFactoryManager.getProtocolByServiceType(idServizio.getTipo());
				if(protocolloAssociatoSoggettoProprietario.equals(protocolloAssociatoAccordoParteSpecifica)==false){
					throw new Exception("AccordoServizioParteSpecifica ["+idServizio+"] (protocollo:"+protocolloAssociatoAccordoParteSpecifica+
							") con servizio non utilizzabile in una porta applicativa appartenete al soggetto ["+idSoggettoProprietario+"] (protocollo:"+protocolloAssociatoSoggettoProprietario+")");
				}
				String protocolloAssociatoSoggettoAccordoParteSpecifica = this.protocolFactoryManager.getProtocolByOrganizationType(idServizio.getSoggettoErogatore().getTipo());
				if(protocolloAssociatoSoggettoProprietario.equals(protocolloAssociatoSoggettoAccordoParteSpecifica)==false){
					throw new Exception("AccordoServizioParteSpecifica ["+idServizio+"] (protocollo:"+protocolloAssociatoSoggettoAccordoParteSpecifica+
							") con soggetto non utilizzabile in una porta applicativa appartenete al soggetto ["+idSoggettoProprietario+"] (protocollo:"+protocolloAssociatoSoggettoProprietario+")");
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
			
			
			// --- set dati obbligatori nel db ----
			org.openspcoop2.core.config.driver.utils.XMLDataConverter.
				impostaInformazioniConfigurazione_PortaApplicativa(pa);
			
			// --- correzione nomi provider per backward compatibility ----
			MerlinProvider.correctProviderName(pa.getMessageSecurity());
			
			
			// --- upload ---
			if(this.importerEngine.existsPortaApplicativa(idPortaApplicativa)){
				
				org.openspcoop2.core.config.PortaApplicativa old = this.importerEngine.getPortaApplicativa(idPortaApplicativa);
				pa.setId(old.getId());
				IDPortaApplicativa oldIDPortaApplicativaForUpdate = new IDPortaApplicativa();
				oldIDPortaApplicativaForUpdate.setNome(old.getNome());
				pa.setOldIDPortaApplicativaForUpdate(oldIDPortaApplicativaForUpdate);
				
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
	
	
	
	
	public void importControlloTraffico_configurazione(org.openspcoop2.core.controllo_traffico.ConfigurazioneGenerale configurazione, 
			ArchiveEsitoImportDetailConfigurazione<org.openspcoop2.core.controllo_traffico.ConfigurazioneGenerale> detail){		
		try{
			// --- check abilitazione ---
			if(this.importConfigurazione==false){
				detail.setState(ArchiveStatoImport.IMPORT_CONFIG_NOT_ENABLED);
				return;
			}
			
			// update
			this.importerEngine.updateControlloTraffico_configurazione(configurazione);
			detail.setState(ArchiveStatoImport.UPDATED);
		}			
		catch(Exception e){
			this.log.error("Errore durante l'import della configurazione di controllo del traffico: "+e.getMessage(),e);
			detail.setState(ArchiveStatoImport.ERROR);
			detail.setException(e);
		}
	}
	
	
	public void importControlloTraffico_configurationPolicy(ArchiveConfigurationPolicy archivePolicy,ArchiveEsitoImportDetail detail){
		
		String nomePolicy = archivePolicy.getNomePolicy();
		try{
			
			// --- check abilitazione ---
			if(this.importPolicyConfigurazione==false){
				detail.setState(ArchiveStatoImport.IMPORT_POLICY_CONFIG_NOT_ENABLED);
				return;
			}
			
			// --- check esistenza ---
			if(this.updateAbilitato==false){
				if(this.importerEngine.existsControlloTraffico_configurationPolicy(nomePolicy)){
					detail.setState(ArchiveStatoImport.UPDATE_NOT_ENABLED);
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
			// non esistenti
			
			
			// --- ora registrazione
			// non esistenti
			
			
			// --- upload ---
			boolean create = false;
			if(this.importerEngine.existsControlloTraffico_configurationPolicy(nomePolicy)){
				
				ConfigurazionePolicy old = this.importerEngine.getControlloTraffico_configurationPolicy(nomePolicy);
				archivePolicy.getPolicy().setId(old.getId());
				
				// visibilita' oggetto stesso per update
				// non esistenti

				// update
				this.importerEngine.updateControlloTraffico_configurationPolicy(archivePolicy.getPolicy());
				create = false;
			}
			// --- create ---
			else{
				this.importerEngine.createControlloTraffico_configurationPolicy(archivePolicy.getPolicy());
				create = true;
			}
				
			
			// --- info ---
			if(create){
				detail.setState(ArchiveStatoImport.CREATED);
			}else{
				detail.setState(ArchiveStatoImport.UPDATED);
			}
		}			
		catch(Exception e){
			this.log.error("Errore durante l'import della configurazione della policy ["+nomePolicy+"]: "+e.getMessage(),e);
			detail.setState(ArchiveStatoImport.ERROR);
			detail.setException(e);
		}
	}
	
	
	public void importControlloTraffico_activePolicy(ArchiveActivePolicy archivePolicy,ArchiveEsitoImportDetail detail,HashMap<String, String> instanceSerialIdsForPolicy){
		
		AttivazionePolicy attivazionePolicy = archivePolicy.getPolicy();
		String aliasPolicy = archivePolicy.getAliasPolicy();
		RuoloPolicy ruoloPorta = archivePolicy.getRuoloPorta();
		String nomePorta = archivePolicy.getNomePorta();
		
		String tipoPolicy = "configurazione";
		if(ruoloPorta!=null && nomePorta!=null) {
			tipoPolicy = ruoloPorta.toString()+" "+nomePorta;
		}

		try{
			boolean policyGlobale = archivePolicy.isPolicyGlobale();
						
			// --- check abilitazione ---
			if(policyGlobale && this.importPolicyConfigurazione==false){ // se non e' globale la policy di attivazione va aggiunta sempre poiche' associata all'erogazione o alla fruizione
				detail.setState(ArchiveStatoImport.IMPORT_POLICY_CONFIG_NOT_ENABLED);
				return;
			}
			
			// --- check esistenza ---
			if(this.updateAbilitato==false){
				if(this.importerEngine.existsControlloTraffico_activePolicy(ruoloPorta, nomePorta, aliasPolicy)){
					detail.setState(ArchiveStatoImport.UPDATE_NOT_ENABLED);
					return;
				}
			}
			
				
			// --- check elementi riferiti ---
			
			if(attivazionePolicy==null) {
				throw new Exception("Configurazione della policy non definita");
			}
			
			// policy
			if(this.importerEngine.existsControlloTraffico_configurationPolicy(attivazionePolicy.getIdPolicy()) == false ){
				throw new Exception("Configurazione della policy ["+attivazionePolicy.getIdPolicy()+"] non esistente nel registro");
			}
			
			
			// --- compatibilita' elementi riferiti ---
			
			// plugin
			if(this.checkExistsPluginConfigurazione) {
				
				if(attivazionePolicy!=null && attivazionePolicy.getFiltro()!=null && attivazionePolicy.getFiltro().isEnabled() && 
						attivazionePolicy.getFiltro().isInformazioneApplicativaEnabled() &&
						StringUtils.isNotEmpty(attivazionePolicy.getFiltro().getInformazioneApplicativaTipo())) {
					TipoFiltroApplicativo tipoFiltro = TipoFiltroApplicativo.toEnumConstant(attivazionePolicy.getFiltro().getInformazioneApplicativaTipo(), false);
					if(TipoFiltroApplicativo.PLUGIN_BASED.equals(tipoFiltro)) {
						if(this.importerEngine.existsPluginClasse(TipoPlugin.RATE_LIMITING.getValue(), attivazionePolicy.getFiltro().getInformazioneApplicativaNome()) == false ){
							throw new Exception("Plugin '"+TipoPlugin.RATE_LIMITING.getValue()+"' ["+attivazionePolicy.getFiltro().getInformazioneApplicativaNome()+"] non esistente nel registro");
						}
					}
				}
				if(attivazionePolicy!=null && attivazionePolicy.getGroupBy()!=null && attivazionePolicy.getGroupBy().isEnabled() && 
						attivazionePolicy.getGroupBy().isInformazioneApplicativaEnabled() &&
						StringUtils.isNotEmpty(attivazionePolicy.getGroupBy().getInformazioneApplicativaTipo())) {
					TipoFiltroApplicativo tipoFiltro = TipoFiltroApplicativo.toEnumConstant(attivazionePolicy.getGroupBy().getInformazioneApplicativaTipo(), false);
					if(TipoFiltroApplicativo.PLUGIN_BASED.equals(tipoFiltro)) {
						if(this.importerEngine.existsPluginClasse(TipoPlugin.RATE_LIMITING.getValue(), attivazionePolicy.getGroupBy().getInformazioneApplicativaNome()) == false ){
							throw new Exception("Plugin '"+TipoPlugin.RATE_LIMITING.getValue()+"' ["+attivazionePolicy.getGroupBy().getInformazioneApplicativaNome()+"] non esistente nel registro");
						}
					}
				}
				
			}
			
			
			// ---- visibilita' oggetto riferiti ---
			// non esistenti
			
			
			// --- set dati obbligatori nel db ----
			// non esistenti
			
			
			// --- ora registrazione
			// non esistenti
			
			
			// --- upload ---
			boolean create = false;
			if(this.importerEngine.existsControlloTraffico_activePolicy(ruoloPorta, nomePorta, aliasPolicy)){
				
				AttivazionePolicy old = this.importerEngine.getControlloTraffico_activePolicy(ruoloPorta, nomePorta, aliasPolicy);
				attivazionePolicy.setId(old.getId());
				attivazionePolicy.setOldIdActivePolicy(new IdActivePolicy());
				attivazionePolicy.getOldIdActivePolicy().setNome(old.getIdActivePolicy());
				if(old.getIdPolicy().equals(attivazionePolicy.getIdPolicy())) {
					attivazionePolicy.setIdActivePolicy(old.getIdActivePolicy()); // uso lo stesso id dinamico già calcolato sul sistema se non è cambiata la policy
				}
				else {
					// ricalcolo
					attivazionePolicy.setIdActivePolicy(this.buildIdActivePolicy(instanceSerialIdsForPolicy, attivazionePolicy.getIdPolicy()));
				}
				attivazionePolicy.setPosizione(old.getPosizione()); // in caso di aggiornamento non devo influenzare la posizione
				
				// visibilita' oggetto stesso per update
				// non esistenti

				// update
				this.importerEngine.updateControlloTraffico_activePolicy(attivazionePolicy);
				create = false;
			}
			// --- create ---
			else{
				// calcolo in maniera dinamica l'id
				attivazionePolicy.setIdActivePolicy(this.buildIdActivePolicy(instanceSerialIdsForPolicy, attivazionePolicy.getIdPolicy()));
				
				this.importerEngine.createControlloTraffico_activePolicy(attivazionePolicy,this.log);
				create = true;
			}
				
			
			// --- info ---
			if(create){
				detail.setState(ArchiveStatoImport.CREATED);
			}else{
				detail.setState(ArchiveStatoImport.UPDATED);
			}
		}			
		catch(Exception e){
			this.log.error("Errore durante l'import dell'attivazione della policy ["+aliasPolicy+"] ("+tipoPolicy+"): "+e.getMessage(),e);
			detail.setState(ArchiveStatoImport.ERROR);
			detail.setException(e);
		}
	}
	private String buildIdActivePolicy(HashMap<String, String> instanceSerialIdsForPolicy, String idPolicy) throws RegistryException {
		String serialId = null;
		if(instanceSerialIdsForPolicy.containsKey(idPolicy)) {
			serialId = instanceSerialIdsForPolicy.remove(idPolicy);
			serialId = ControlloTrafficoDriverUtils.incrementPolicyInstanceSerialId(serialId);
			instanceSerialIdsForPolicy.put(idPolicy, serialId);
		}
		else {
			serialId = this.importerEngine.getNextPolicyInstanceSerialId(idPolicy, this.log);
			instanceSerialIdsForPolicy.put(idPolicy, serialId);
		}
		String idActive = ControlloTrafficoDriverUtils.buildIdActivePolicy(idPolicy, serialId);
		return idActive;
	}
	
	
	public void importAllarme(ArchiveAllarme archiveAllarme,ArchiveEsitoImportDetail detail,HashMap<String, String> instanceSerialIdsForAlarm){
		
		Allarme allarme = archiveAllarme.getAllarme();
		String aliasAllarme = archiveAllarme.getAlias();
		RuoloPorta ruoloPorta = archiveAllarme.getRuoloPorta();
		String nomePorta = archiveAllarme.getNomePorta();
		
		String tipoAllarme = "configurazione";
		if(ruoloPorta!=null && nomePorta!=null) {
			tipoAllarme = ruoloPorta.toString()+" "+nomePorta;
		}

		try{
			boolean allarmeGlobale = archiveAllarme.isAllarmeGlobale();
						
			// --- check abilitazione ---
			if(allarmeGlobale && this.importPolicyConfigurazione==false){ // se non e' globale l'allarme va aggiunto sempre poiche' associata all'erogazione o alla fruizione
				detail.setState(ArchiveStatoImport.IMPORT_POLICY_CONFIG_NOT_ENABLED);
				return;
			}
			
			// --- check esistenza ---
			if(this.updateAbilitato==false){
				if(this.importerEngine.existsAllarme(ruoloPorta, nomePorta, aliasAllarme)){
					detail.setState(ArchiveStatoImport.UPDATE_NOT_ENABLED);
					return;
				}
			}
			
				
			// --- check elementi riferiti ---
			
			// plugin
			if(this.importerEngine.existsPluginClasse(TipoPlugin.ALLARME.getValue(), allarme.getTipo()) == false ){
				throw new Exception("Plugin '"+TipoPlugin.ALLARME.getValue()+"' ["+allarme.getTipo()+"] non esistente nel registro");
			}
			
			
			// --- compatibilita' elementi riferiti ---
			// non esistenti
			
			
			// ---- visibilita' oggetto riferiti ---
			// non esistenti
			
			
			// --- set dati obbligatori nel db ----
			// non esistenti
			
			
			// --- ora registrazione
			// non esistenti
			
			
			// --- upload ---
			boolean create = false;
			if(this.importerEngine.existsAllarme(ruoloPorta, nomePorta, aliasAllarme)){
				
				Allarme old = this.importerEngine.getAllarme(ruoloPorta, nomePorta, aliasAllarme);
				allarme.setId(old.getId());
//				allarme.setOldIdActivePolicy(new IdActivePolicy());
//				allarme.getOldIdActivePolicy().setNome(old.getIdActivePolicy());
//				if(old.getIdPolicy().equals(allarme.getIdPolicy())) {
				allarme.setNome(old.getNome()); // uso lo stesso id dinamico già calcolato sul sistema se non è cambiato il tipo di plugin
//				}
//				else {
//					// ricalcolo
//					allarme.setIdActivePolicy(this.buildIdActivePolicy(instanceSerialIdsForAlarm, allarme.getIdPolicy()));
//				}
				
				if(allarme.sizeAllarmeParametroList()>0) {
					if(old.sizeAllarmeParametroList()>0) {
						for (AllarmeParametro param : allarme.getAllarmeParametroList()) {
							for (AllarmeParametro oldParam : old.getAllarmeParametroList()) {
								if(oldParam.getIdParametro().equals(param.getIdParametro())) {
									param.setId(oldParam.getId());
									break;
								}
							}
						}
					}
				}
				
				// visibilita' oggetto stesso per update
				// non esistenti

				// update
				this.importerEngine.updateAllarme(allarme, this.log);
				create = false;
			}
			// --- create ---
			else{
				// calcolo in maniera dinamica l'id
				allarme.setNome(this.buildIdAllarme(instanceSerialIdsForAlarm, allarme.getTipo()));
				
				this.importerEngine.createAllarme(allarme, this.log);
				create = true;
			}
				
			
			// --- info ---
			if(create){
				detail.setState(ArchiveStatoImport.CREATED);
			}else{
				detail.setState(ArchiveStatoImport.UPDATED);
			}
		}			
		catch(Exception e){
			this.log.error("Errore durante l'import dell'allarme ["+aliasAllarme+"] ("+tipoAllarme+"): "+e.getMessage(),e);
			detail.setState(ArchiveStatoImport.ERROR);
			detail.setException(e);
		}
	}
	private String buildIdAllarme(HashMap<String, String> instanceSerialIdsForAlarm, String tipoPlugin) throws RegistryException {
		String serialId = null;
		if(instanceSerialIdsForAlarm.containsKey(tipoPlugin)) {
			serialId = instanceSerialIdsForAlarm.remove(tipoPlugin);
			serialId = AllarmiDriverUtils.incrementAlarmInstanceSerialId(serialId);
			instanceSerialIdsForAlarm.put(tipoPlugin, serialId);
		}
		else {
			serialId = this.importerEngine.getNextAlarmInstanceSerialId(tipoPlugin, this.log);
			instanceSerialIdsForAlarm.put(tipoPlugin, serialId);
		}
		String idActive = AllarmiDriverUtils.buildIdAlarm(tipoPlugin, serialId);
		return idActive;
	}
	
	
	public void importTokenPolicy(ArchiveTokenPolicy archivePolicy,ArchiveEsitoImportDetail detail){
		_importGenericProperties("Token Policy", archivePolicy, detail);
	}
	public void importAttributeAuthority(ArchiveAttributeAuthority archiveAA,ArchiveEsitoImportDetail detail){
		_importGenericProperties("Attribute Authority", archiveAA, detail);
	}
	public void _importGenericProperties(String oggetto, AbstractArchiveGenericProperties archiveGenericProperties,ArchiveEsitoImportDetail detail){
		
		if(archiveGenericProperties==null) {
			detail.setState(ArchiveStatoImport.ERROR);
			detail.setException(new Exception("archiveGenericProperties is null"));
			return;
		}
		
		String nomePolicy = archiveGenericProperties.getNomePolicy();
		String tipologiaPolicy = archiveGenericProperties.getTipologiaPolicy();
		try{
			
			// --- check abilitazione ---
			if(this.importPolicyConfigurazione==false){
				detail.setState(ArchiveStatoImport.IMPORT_POLICY_CONFIG_NOT_ENABLED);
				return;
			}
			
			// --- check esistenza ---
			if(this.updateAbilitato==false){
				if(this.importerEngine.existsGenericProperties(tipologiaPolicy, nomePolicy)){
					detail.setState(ArchiveStatoImport.UPDATE_NOT_ENABLED);
					return;
				}
			}
			
				
			// --- check elementi riferiti ---
			// non esistenti
			
			
			// --- compatibilita' elementi riferiti ---

			// plugin
			if(this.checkExistsPluginConfigurazione) {
				
				boolean tokenValidazione = CostantiConfigurazione.GENERIC_PROPERTIES_TOKEN_TIPOLOGIA_VALIDATION.equals(archiveGenericProperties.getTipologiaPolicy());
				boolean tokenNegoziazione = CostantiConfigurazione.GENERIC_PROPERTIES_TOKEN_TIPOLOGIA_RETRIEVE.equals(archiveGenericProperties.getTipologiaPolicy());
				boolean attributeAuthority = CostantiConfigurazione.GENERIC_PROPERTIES_ATTRIBUTE_AUTHORITY.equals(archiveGenericProperties.getTipologiaPolicy());
				List<String> tokenName = new ArrayList<>();
				TipoPlugin tipoPlugin = null;
				if(tokenValidazione) {
					tokenName.add(CostantiConfigurazione.POLICY_VALIDAZIONE_CLAIMS_PARSER_PLUGIN_TYPE);
					tokenName.add(CostantiConfigurazione.POLICY_INTROSPECTION_CLAIMS_PARSER_PLUGIN_TYPE);
					tokenName.add(CostantiConfigurazione.POLICY_USER_INFO_CLAIMS_PARSER_PLUGIN_TYPE);
					tipoPlugin = TipoPlugin.TOKEN_VALIDAZIONE;
				}
				else if(tokenNegoziazione) {
					tokenName.add(CostantiConfigurazione.POLICY_RETRIEVE_TOKEN_PARSER_PLUGIN_TYPE);
					tipoPlugin = TipoPlugin.TOKEN_NEGOZIAZIONE;
				}
				else if(attributeAuthority) {
					tokenName.add(CostantiConfigurazione.AA_RESPONSE_PARSER_PLUGIN_TYPE);
					tipoPlugin = TipoPlugin.ATTRIBUTE_AUTHORITY;
				}
				if(!tokenName.isEmpty()) {
					if(archiveGenericProperties!=null && archiveGenericProperties.getPolicy()!=null && archiveGenericProperties.getPolicy().sizePropertyList()>0) {
						for (int i = 0; i < archiveGenericProperties.getPolicy().sizePropertyList(); i++) {
							Property p = archiveGenericProperties.getPolicy().getProperty(i);
							if(tokenName.contains(p.getNome())) {
								String gpName = p.getValore();
								if(gpName!=null && StringUtils.isNotEmpty(gpName) && !CostantiConfigurazione.POLICY_ID_NON_DEFINITA.equals(gpName)) {
									if(this.importerEngine.existsPluginClasse(tipoPlugin.getValue(), gpName) == false ){
										throw new Exception("Plugin '"+tipoPlugin.getValue()+"' ["+p.getValore()+"] non esistente nel registro");
									}	
								}
							}
						}
					}
				}
				
				// ulteriore per token dynamic discovery
				tokenName.clear();
				if(tokenValidazione) {
					tokenName.add(CostantiConfigurazione.POLICY_DYNAMIC_DISCOVERY_CLAIMS_PARSER_PLUGIN_TYPE);
					tipoPlugin = TipoPlugin.TOKEN_DYNAMIC_DISCOVERY;
				}
				if(!tokenName.isEmpty()) {
					if(archiveGenericProperties!=null && archiveGenericProperties.getPolicy()!=null && archiveGenericProperties.getPolicy().sizePropertyList()>0) {
						for (int i = 0; i < archiveGenericProperties.getPolicy().sizePropertyList(); i++) {
							Property p = archiveGenericProperties.getPolicy().getProperty(i);
							if(tokenName.contains(p.getNome())) {
								String gpName = p.getValore();
								if(gpName!=null && StringUtils.isNotEmpty(gpName) && !CostantiConfigurazione.POLICY_ID_NON_DEFINITA.equals(gpName)) {
									if(this.importerEngine.existsPluginClasse(tipoPlugin.getValue(), gpName) == false ){
										throw new Exception("Plugin '"+tipoPlugin.getValue()+"' ["+p.getValore()+"] non esistente nel registro");
									}	
								}
							}
						}
					}
				}
			}
			
			
			// ---- visibilita' oggetto riferiti ---
			// non esistenti
			
			
			// --- set dati obbligatori nel db ----
			// non esistenti
			
			
			// --- ora registrazione
			// non esistenti
			
			
			// --- upload ---
			boolean create = false;
			if(this.importerEngine.existsGenericProperties(tipologiaPolicy, nomePolicy)){
				
				GenericProperties old = this.importerEngine.getGenericProperties(tipologiaPolicy, nomePolicy);
				archiveGenericProperties.getPolicy().setId(old.getId());
				
				// visibilita' oggetto stesso per update
				// non esistenti

				// update
				this.importerEngine.updateGenericProperties(archiveGenericProperties.getPolicy());
				create = false;
			}
			// --- create ---
			else{
				this.importerEngine.createGenericProperties(archiveGenericProperties.getPolicy());
				create = true;
			}
				
			
			// --- info ---
			if(create){
				detail.setState(ArchiveStatoImport.CREATED);
			}else{
				detail.setState(ArchiveStatoImport.UPDATED);
			}
		}			
		catch(Exception e){
			this.log.error("Errore durante l'import della configurazione '"+oggetto+"' ["+nomePolicy+"] (tipo: '"+tipologiaPolicy+"'): "+e.getMessage(),e);
			detail.setState(ArchiveStatoImport.ERROR);
			detail.setException(e);
		}
	}
	
	public void importPluginArchivio(ArchivePluginArchivio archivePlugin,ArchiveEsitoImportDetail detail){
		
		String nome = archivePlugin.getNome();
		try{
			
			// --- check abilitazione ---
			if(this.importPluginConfigurazione==false){
				detail.setState(ArchiveStatoImport.IMPORT_PLUGIN_CONFIG_NOT_ENABLED);
				return;
			}
			
			// --- check esistenza ---
			if(this.updateAbilitato==false){
				if(this.importerEngine.existsPluginArchivio(nome)){
					detail.setState(ArchiveStatoImport.UPDATE_NOT_ENABLED);
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
			// non esistenti
			
			
			// --- ora registrazione
			// non esistenti
			
			
			// --- upload ---
			boolean create = false;
			if(this.importerEngine.existsPluginArchivio(nome)){
				
				RegistroPlugin old = this.importerEngine.getPluginArchivio(nome);
				archivePlugin.getPlugin().setId(old.getId());
				archivePlugin.getPlugin().setOldNome(old.getNome());
				
				archivePlugin.getPlugin().setPosizione(old.getPosizione()); // in caso di aggiornamento non devo influenzare la posizione
				
				// visibilita' oggetto stesso per update
				// non esistenti

				// update
				this.importerEngine.updatePluginArchivio(archivePlugin.getPlugin());
				create = false;
			}
			// --- create ---
			else{
				this.importerEngine.createPluginArchivio(archivePlugin.getPlugin());
				create = true;
			}
				
			
			// --- info ---
			if(create){
				detail.setState(ArchiveStatoImport.CREATED);
			}else{
				detail.setState(ArchiveStatoImport.UPDATED);
			}
		}			
		catch(Exception e){
			this.log.error("Errore durante l'import dell'archivio dei plugin ["+nome+"]: "+e.getMessage(),e);
			detail.setState(ArchiveStatoImport.ERROR);
			detail.setException(e);
		}
	}
	
	public void importPluginClasse(ArchivePluginClasse archivePluginClasse,ArchiveEsitoImportDetail detail){
		
		String tipoPlugin = archivePluginClasse.getTipoPlugin();
		String tipo = archivePluginClasse.getTipo();
		try{
			
			// --- check abilitazione ---
			if(this.importPluginConfigurazione==false){
				detail.setState(ArchiveStatoImport.IMPORT_PLUGIN_CONFIG_NOT_ENABLED);
				return;
			}
			
			// --- check esistenza ---
			if(this.updateAbilitato==false){
				if(this.importerEngine.existsPluginClasse(tipoPlugin, tipo)){
					detail.setState(ArchiveStatoImport.UPDATE_NOT_ENABLED);
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
			// non esistenti
			
			
			// --- ora registrazione
			// non esistenti
			
			
			// --- upload ---
			boolean create = false;
			if(this.importerEngine.existsPluginClasse(tipoPlugin, tipo)){
				
				Plugin old = this.importerEngine.getPluginClasse(tipoPlugin, tipo);
				archivePluginClasse.getPlugin().setId(old.getId());
				IdPlugin oldIdPlugin = new IdPlugin();
				oldIdPlugin.setClassName(old.getClassName());
				oldIdPlugin.setLabel(old.getLabel());
				oldIdPlugin.setTipo(old.getTipo());
				oldIdPlugin.setTipoPlugin(old.getTipoPlugin());
				archivePluginClasse.getPlugin().setOldIdPlugin(oldIdPlugin);
				
				// visibilita' oggetto stesso per update
				// non esistenti

				// update
				this.importerEngine.updatePluginClasse(archivePluginClasse.getPlugin());
				create = false;
			}
			// --- create ---
			else{
				this.importerEngine.createPluginClasse(archivePluginClasse.getPlugin());
				create = true;
			}
				
			
			// --- info ---
			if(create){
				detail.setState(ArchiveStatoImport.CREATED);
			}else{
				detail.setState(ArchiveStatoImport.UPDATED);
			}
		}			
		catch(Exception e){
			this.log.error("Errore durante l'import del plugin ["+tipo+"] (tipo-plugin: '"+tipoPlugin+"'): "+e.getMessage(),e);
			detail.setState(ArchiveStatoImport.ERROR);
			detail.setException(e);
		}
	}

	public void importConfigurazione_UrlInvocazioneRegola(ArchiveUrlInvocazioneRegola archiveRegola,ArchiveEsitoImportDetail detail){
		
		String nome = archiveRegola.getNome();
		try{
			
			// --- check abilitazione ---
			if(this.importConfigurazione==false){
				detail.setState(ArchiveStatoImport.IMPORT_CONFIG_NOT_ENABLED);
				return;
			}
			
			// --- check esistenza ---
			if(this.updateAbilitato==false){
				if(this.importerEngine.existsUrlInvocazioneRegola(nome)){
					detail.setState(ArchiveStatoImport.UPDATE_NOT_ENABLED);
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
			// non esistenti
			
			
			// --- ora registrazione
			// non esistenti
			
			
			// --- upload ---
			boolean create = false;
			if(this.importerEngine.existsUrlInvocazioneRegola(nome)){
				
				ConfigurazioneUrlInvocazioneRegola old = this.importerEngine.getUrlInvocazioneRegola(nome);
				archiveRegola.getRegola().setId(old.getId());
				archiveRegola.getRegola().setOldNome(old.getNome());
				
				archiveRegola.getRegola().setPosizione(old.getPosizione()); // in caso di aggiornamento non devo influenzare la posizione
				
				// visibilita' oggetto stesso per update
				// non esistenti

				// update
				this.importerEngine.updateUrlInvocazioneRegola(archiveRegola.getRegola());
				create = false;
			}
			// --- create ---
			else{
				this.importerEngine.createUrlInvocazioneRegola(archiveRegola.getRegola());
				create = true;
			}
				
			
			// --- info ---
			if(create){
				detail.setState(ArchiveStatoImport.CREATED);
			}else{
				detail.setState(ArchiveStatoImport.UPDATED);
			}
		}			
		catch(Exception e){
			this.log.error("Errore durante l'import della regola di proxy pass ["+nome+"]: "+e.getMessage(),e);
			detail.setState(ArchiveStatoImport.ERROR);
			detail.setException(e);
		}
	}
	
	public void importConfigurazione_UrlInvocazione(ConfigurazioneUrlInvocazione configurazionePdD, ArchiveEsitoImportDetailConfigurazione<ConfigurazioneUrlInvocazione> detail){		
		try{
			// --- check abilitazione ---
			if(this.importConfigurazione==false){
				detail.setState(ArchiveStatoImport.IMPORT_CONFIG_NOT_ENABLED);
				return;
			}
			
			// update
			this.importerEngine.updateConfigurazione_UrlInvocazione(configurazionePdD);
			detail.setState(ArchiveStatoImport.UPDATED);
		}			
		catch(Exception e){
			this.log.error("Errore durante l'import della configurazione dell'url di invocazione: "+e.getMessage(),e);
			detail.setState(ArchiveStatoImport.ERROR);
			detail.setException(e);
		}
	}
	
	public void importConfigurazione(Configurazione configurazionePdD, ArchiveEsitoImportDetailConfigurazione<Configurazione> detail){		
		try{
			// --- check abilitazione ---
			if(this.importConfigurazione==false){
				detail.setState(ArchiveStatoImport.IMPORT_CONFIG_NOT_ENABLED);
				return;
			}
			
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
