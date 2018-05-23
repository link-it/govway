/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it). 
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

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.openspcoop2.core.config.Ruolo;
import org.openspcoop2.core.config.PortaApplicativaServizioApplicativo;
import org.openspcoop2.core.config.PortaDelegataServizioApplicativo;
import org.openspcoop2.core.config.driver.DriverConfigurazioneNotFound;
import org.openspcoop2.core.config.driver.FiltroRicercaPorteApplicative;
import org.openspcoop2.core.config.driver.FiltroRicercaPorteDelegate;
import org.openspcoop2.core.config.driver.FiltroRicercaServiziApplicativi;
import org.openspcoop2.core.controllo_traffico.IdActivePolicy;
import org.openspcoop2.core.controllo_traffico.IdPolicy;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDAccordoCooperazione;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDRuolo;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteComuneServizioCompostoServizioComponente;
import org.openspcoop2.core.registry.Fruitore;
import org.openspcoop2.core.registry.IdSoggetto;
import org.openspcoop2.core.registry.RuoloSoggetto;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziNotFound;
import org.openspcoop2.core.registry.driver.FiltroRicerca;
import org.openspcoop2.core.registry.driver.FiltroRicercaAccordi;
import org.openspcoop2.core.registry.driver.FiltroRicercaRuoli;
import org.openspcoop2.core.registry.driver.FiltroRicercaServizi;
import org.openspcoop2.core.registry.driver.FiltroRicercaSoggetti;
import org.openspcoop2.core.registry.driver.IDAccordoCooperazioneFactory;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.protocol.basic.registry.ConfigIntegrationReader;
import org.openspcoop2.protocol.basic.registry.RegistryReader;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.archive.Archive;
import org.openspcoop2.protocol.sdk.archive.ArchiveAccordoCooperazione;
import org.openspcoop2.protocol.sdk.archive.ArchiveAccordoServizioComposto;
import org.openspcoop2.protocol.sdk.archive.ArchiveAccordoServizioParteComune;
import org.openspcoop2.protocol.sdk.archive.ArchiveAccordoServizioParteSpecifica;
import org.openspcoop2.protocol.sdk.archive.ArchiveActivePolicy;
import org.openspcoop2.protocol.sdk.archive.ArchiveCascadeConfiguration;
import org.openspcoop2.protocol.sdk.archive.ArchiveConfigurationPolicy;
import org.openspcoop2.protocol.sdk.archive.ArchiveFruitore;
import org.openspcoop2.protocol.sdk.archive.ArchiveIdCorrelazione;
import org.openspcoop2.protocol.sdk.archive.ArchiveMode;
import org.openspcoop2.protocol.sdk.archive.ArchivePdd;
import org.openspcoop2.protocol.sdk.archive.ArchivePortaApplicativa;
import org.openspcoop2.protocol.sdk.archive.ArchivePortaDelegata;
import org.openspcoop2.protocol.sdk.archive.ArchiveRuolo;
import org.openspcoop2.protocol.sdk.archive.ArchiveServizioApplicativo;
import org.openspcoop2.protocol.sdk.archive.ArchiveSoggetto;
import org.openspcoop2.protocol.sdk.archive.IArchive;
import org.openspcoop2.protocol.sdk.constants.ArchiveType;


/**
 * ExporterArchiveUtils 
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ExporterArchiveUtils {

	private AbstractArchiveEngine archiveEngine;
	private Logger log;
	private ProtocolFactoryManager protocolFactoryManager;
	private ArchiveIdCorrelazione idCorrelazione = null;
	
	public ExporterArchiveUtils(AbstractArchiveEngine archiveEngine,Logger log) throws Exception{
		this.archiveEngine = archiveEngine;
		this.log = log;
		this.protocolFactoryManager = ProtocolFactoryManager.getInstance();
		this.idCorrelazione = new ArchiveIdCorrelazione("export"); // non necessario in questa funzione. Comunque viene qua usata una variabile se servisse in futuro
	}
	
	public void export(String protocol,Archive archive,OutputStream out,ArchiveMode mode) throws Exception{
		IProtocolFactory<?> protocolFactory = this.protocolFactoryManager.getProtocolFactoryByName(protocol);
		IArchive archiveEngine = protocolFactory.createArchive();
		RegistryReader archiveRegistryReader = new RegistryReader(this.archiveEngine.getDriverRegistroServizi(),this.log);
		ConfigIntegrationReader archiveConfigIntegrationReader = new ConfigIntegrationReader(this.archiveEngine.getDriverConfigurazione(),this.log);
		archiveEngine.exportArchive(archive, out, mode, archiveRegistryReader, archiveConfigIntegrationReader);
	}
	
	public byte[] export(String protocol,Archive archive,ArchiveMode mode) throws Exception{
		IProtocolFactory<?> protocolFactory = this.protocolFactoryManager.getProtocolFactoryByName(protocol);
		IArchive archiveEngine = protocolFactory.createArchive();
		RegistryReader archiveRegistryReader = new RegistryReader(this.archiveEngine.getDriverRegistroServizi(),this.log);
		ConfigIntegrationReader archiveConfigIntegrationReader = new ConfigIntegrationReader(this.archiveEngine.getDriverConfigurazione(),this.log);
		return archiveEngine.exportArchive(archive, mode, archiveRegistryReader, archiveConfigIntegrationReader);
	}
	
	public void fillArchive(Archive archive, ArchiveType exportSourceArchiveType,List<?> listObject, ArchiveCascadeConfiguration cascadeConfig) throws Exception{
		
		// La navigazione dell'albero degli oggetti avviente con le chiamate effettuate di seguito.
		//
		// Per ogni oggetto e' possibile identificare:
		//	- gli oggetti che lo riferiscono (cascadeAvanti)
		//	- gli oggetti che lui riferisce (dipendenza)
		// 
		// Un oggetto (catalogato come servizioApplicativo,pa,pd,accordo....) viene aggiunto all'archivio SOLO SE e' richiesto dalla configurazione di cascade.
		//
		// Gli oggetti che vengono riferiti da un oggetto (dipendenza) vengono navigati SOLO SE l'oggetto e' stato aggiunto all'archivio (e quindi abilitato dalla configurazione di cascade). 
		// Saranno aggiunti pero' solo se poi essi stessi sono richiesti dalla configurazione di cascade.
		//
		// Gli oggetti che lo riferiscono (cascadeAvanti) sono sempre navigati.
		// Anche questi pero' saranno aggiunti solo se poi essi stessi sono richiesti dalla configurazione di cascade.
		//
		// Con questo approccio la navigazione e' sempre completa verso gli oggetti che lo riferiscono (cascadeAvanti)
		// mentre gli oggetti padre sono navigati solo se l'oggetto e' abilitato dalla configurazione di cascade.
		//
		// L'utente che configura il cascade puo' di fatto implementare una navigazione che tronca un ramo se disattiva il cascade di tutti i sotto elementi. 
		// Fanno eccezione i servizi applicativi per i quali esiste piu' di un padre. In questo caso si esamina il padre:
		// - nel caso di padre=soggetto/sa si vede esclusivamente il cascade dei servizi applicativi
		// - nel caso di padre=porta delegata si controlla sia il cascade dei servizi applicativi che il cascade della porta delegata
		// - nel caso di padre=porta applicativa si controlla sia il cascade dei servizi applicativi che il cascade della porta applicativa
		// - nel caso di padre=fruitore si controlla sia il cascade dei servizi applicativi che il cascade delle fruizioni
		
		switch (exportSourceArchiveType) {
		case PDD:
			for (Object object : listObject) {
				this.readPdd(archive, (String)object, cascadeConfig, exportSourceArchiveType);
			}
			break;
		case RUOLO:
			for (Object object : listObject) {
				this.readRuolo(archive, (IDRuolo)object, cascadeConfig, exportSourceArchiveType);
			}
			break;
		case SOGGETTO:
			for (Object object : listObject) {
				this.readSoggetto(archive, (IDSoggetto)object, cascadeConfig, exportSourceArchiveType);
			}
			break;
		case SERVIZIO_APPLICATIVO:
			for (Object object : listObject) {
				this.readServizioApplicativo(archive, (IDServizioApplicativo)object, cascadeConfig, exportSourceArchiveType);
			}
			break;
		case PORTA_DELEGATA:
			for (Object object : listObject) {
				this.readPortaDelegata(archive, (IDPortaDelegata)object, cascadeConfig, exportSourceArchiveType);
			}
			break;
		case PORTA_APPLICATIVA:
			for (Object object : listObject) {
				this.readPortaApplicativa(archive, (IDPortaApplicativa)object, cascadeConfig, exportSourceArchiveType);
			}
			break;
		case ACCORDO_COOPERAZIONE:
			for (Object object : listObject) {
				this.readAccordoCooperazione(archive, (IDAccordoCooperazione)object, cascadeConfig, exportSourceArchiveType);
			}
			break;
		case ACCORDO_SERVIZIO_PARTE_COMUNE:
			for (Object object : listObject) {
				this.readAccordoServizioParteComune(archive, (IDAccordo)object, cascadeConfig, exportSourceArchiveType);
			}
			break;
		case ACCORDO_SERVIZIO_COMPOSTO:
			for (Object object : listObject) {
				this.readAccordoServizioComposto(archive, (IDAccordo)object, cascadeConfig, exportSourceArchiveType);
			}
			break;
		case ACCORDO_SERVIZIO_PARTE_SPECIFICA:
			for (Object object : listObject) {
				this.readAccordoServizioParteSpecifica(archive, (IDServizio)object, cascadeConfig, exportSourceArchiveType);
			}
			break;
		case CONFIGURAZIONE:
			readControlloCongestioneConfiguration(archive);
			archive.setConfigurazionePdD(this.archiveEngine.getConfigurazione());
			break;
		case ALL:
		case ALL_WITHOUT_CONFIGURAZIONE:
			
			// soggetti e tutti gli oggetti interni
			FiltroRicercaSoggetti filtroSoggetti = new FiltroRicercaSoggetti();
			try{
				ArchiveCascadeConfiguration cascadeAll = new ArchiveCascadeConfiguration(true);
				List<IDSoggetto> idsSoggetti = this.archiveEngine.getAllIdSoggettiRegistro(filtroSoggetti);
				if(idsSoggetti!=null && idsSoggetti.size()>0){
					for (IDSoggetto idSoggetto : idsSoggetti) {
						this.readSoggetto(archive, idSoggetto, cascadeAll, exportSourceArchiveType); // vengono prese anche le pdd
					}
				}
			}catch(DriverConfigurazioneNotFound notFound){}
			
			// configurazione solo se richiesta
			if(exportSourceArchiveType.equals(ArchiveType.ALL)){
				readControlloCongestioneConfiguration(archive);
				archive.setConfigurazionePdD(this.archiveEngine.getConfigurazione());
			}
			
			// aggiungo porte di dominio 'zombie'
			FiltroRicerca filtroRicercaPdd = new FiltroRicerca();
			List<String> idPdds = null;
			try{
				idPdds = this.archiveEngine.getAllIdPorteDominio(filtroRicercaPdd);
				for (String idPdd : idPdds) {
					boolean found = false;
					if(archive.getPdd()!=null && archive.getPdd().size()>0){
						for (String idArchivePdd : archive.getPdd().keys()) {
							ArchivePdd archivePdd = archive.getPdd().get(idArchivePdd);
							if(archivePdd.getNomePdd().equals(idPdd)){
								found = true;
								break;
							}
						}
					}
					if(!found){
						this.readPdd(archive, idPdd, cascadeConfig, false, ArchiveType.PDD);
					}
				}
			}catch(DriverRegistroServiziNotFound notFound){}
			
			// aggiungo ruoli 'zombie'
			FiltroRicercaRuoli filtroRicercaRuoli = new FiltroRicercaRuoli();
			List<IDRuolo> idRuoli = null;
			try{
				idRuoli = this.archiveEngine.getAllIdRuoli(filtroRicercaRuoli);
				for (IDRuolo idRuolo : idRuoli) {
					boolean found = false;
					if(archive.getRuoli()!=null && archive.getRuoli().size()>0){
						for (String idArchiveRuolo : archive.getRuoli().keys()) {
							ArchiveRuolo archiveRuolo = archive.getRuoli().get(idArchiveRuolo);
							if(archiveRuolo.getIdRuolo().equals(idRuolo)){
								found = true;
								break;
							}
						}
					}
					if(!found){
						this.readRuolo(archive, idRuolo, cascadeConfig, false, ArchiveType.RUOLO);
					}
				}
			}catch(DriverRegistroServiziNotFound notFound){}
				
			break;
		default:
			break;
		}
		
	}
	
	private void readControlloCongestioneConfiguration(Archive archive) throws Exception {
		
		archive.setControlloCongestione_configurazione(this.archiveEngine.getControlloCongestione_Configurazione());
		
		List<IdPolicy> listControlloCongestione_configurationPolicies = null;
		try {
			listControlloCongestione_configurationPolicies = this.archiveEngine.getAllIdControlloCongestione_configurationPolicies();
		}catch(DriverConfigurazioneNotFound notFound) {}
		if(listControlloCongestione_configurationPolicies!=null && listControlloCongestione_configurationPolicies.size()>0) {
			for (IdPolicy idPolicy : listControlloCongestione_configurationPolicies) {
				archive.getControlloCongestione_configurationPolicies().add(new ArchiveConfigurationPolicy(
						this.archiveEngine.getControlloCongestione_configurationPolicy(idPolicy),
						this.idCorrelazione));
			}
		}
		
		List<IdActivePolicy> listControlloCongestione_activePolicies = null;
		try {
			listControlloCongestione_activePolicies = this.archiveEngine.getAllIdControlloCongestione_activePolicies();
		}catch(DriverConfigurazioneNotFound notFound) {}
		if(listControlloCongestione_activePolicies!=null && listControlloCongestione_activePolicies.size()>0) {
			for (IdActivePolicy idPolicy : listControlloCongestione_activePolicies) {
				archive.getControlloCongestione_activePolicies().add(new ArchiveActivePolicy(
						this.archiveEngine.getControlloCongestione_activePolicy(idPolicy),
						this.idCorrelazione));
			}
		}
	}
	

	private void readPdd(Archive archive, String nomePdd, ArchiveCascadeConfiguration cascadeConfig, ArchiveType provenienza) throws Exception{
		this.readPdd(archive, nomePdd, cascadeConfig, true, provenienza);
	}
	private void readPdd(Archive archive, String nomePdd, ArchiveCascadeConfiguration cascadeConfig, boolean cascadeAvanti, ArchiveType provenienza) throws Exception{
		
		String key = ArchivePdd.buildKey(nomePdd);
		if(archive.getPdd().containsKey(key)){
			// gia gestito
		}
		else{
			try{
				if(cascadeConfig.isCascadePdd() || ArchiveType.PDD.equals(provenienza)){
					
					// add
					org.openspcoop2.core.registry.PortaDominio pdd = this.archiveEngine.getPortaDominio(nomePdd);
					ArchivePdd archivePdd = new ArchivePdd(pdd, this.idCorrelazione);
					archive.getPdd().add(archivePdd);
					
					// *** dipendenze: oggetti necessari per la creazione dell'oggetto sopra aggiunto ***
					
					// non vi sono oggetti che possiedono come figlio la pdd
					
				}
			}catch(Exception e){
				throw new ProtocolException("(PdD "+nomePdd+") "+e.getMessage(),e);
			}
		}
		
		
		// *** cascade in avanti ***
		
		if(cascadeAvanti){
		
			// soggetti
				
			FiltroRicercaSoggetti filtroSoggetti = new FiltroRicercaSoggetti();
			filtroSoggetti.setNomePdd(nomePdd);
			try{
				List<IDSoggetto> idsSoggetti = this.archiveEngine.getAllIdSoggettiRegistro(filtroSoggetti);
				if(idsSoggetti!=null && idsSoggetti.size()>0){
					for (IDSoggetto idSoggetto : idsSoggetti) {
						this.readSoggetto(archive, idSoggetto, cascadeConfig, ArchiveType.PDD);
					}
				}
			}catch(DriverRegistroServiziNotFound notFound){}
				
		}
		
		
	}
	
	private void readRuolo(Archive archive, IDRuolo idRuolo, ArchiveCascadeConfiguration cascadeConfig, ArchiveType provenienza) throws Exception{
		this.readRuolo(archive, idRuolo, cascadeConfig, true, provenienza);
	}
	private void readRuolo(Archive archive, IDRuolo idRuolo, ArchiveCascadeConfiguration cascadeConfig, boolean cascadeAvanti, ArchiveType provenienza) throws Exception{
		
		String key = ArchiveRuolo.buildKey(idRuolo.getNome());
		if(archive.getRuoli().containsKey(key)){
			// gia gestito
		}
		else{
			try{
				if(cascadeConfig.isCascadeRuoli() || ArchiveType.RUOLO.equals(provenienza)){
					
					// add
					org.openspcoop2.core.registry.Ruolo ruolo = this.archiveEngine.getRuolo(idRuolo);
					ArchiveRuolo archiveRuolo = new ArchiveRuolo(ruolo, this.idCorrelazione);
					archive.getRuoli().add(archiveRuolo);
					
					// *** dipendenze: oggetti necessari per la creazione dell'oggetto sopra aggiunto ***
					
					// non vi sono oggetti che possiedono come figlio il ruolo
					
				}
			}catch(Exception e){
				throw new ProtocolException("(Ruolo "+idRuolo.getNome()+") "+e.getMessage(),e);
			}
		}
		
		
		// *** cascade in avanti ***
		
		if(cascadeAvanti){
		
			// soggetti
				
			FiltroRicercaSoggetti filtroSoggetti = new FiltroRicercaSoggetti();
			filtroSoggetti.setIdRuolo(idRuolo);
			try{
				List<IDSoggetto> idsSoggetti = this.archiveEngine.getAllIdSoggettiRegistro(filtroSoggetti);
				if(idsSoggetti!=null && idsSoggetti.size()>0){
					for (IDSoggetto idSoggetto : idsSoggetti) {
						this.readSoggetto(archive, idSoggetto, cascadeConfig, ArchiveType.RUOLO);
					}
				}
			}catch(DriverRegistroServiziNotFound notFound){}
			
			
			// porteDelegate
			
			FiltroRicercaPorteDelegate filtroPorteDelegate = new FiltroRicercaPorteDelegate();
			filtroPorteDelegate.setIdRuolo(idRuolo);
			try{
				List<IDPortaDelegata> idsPD = this.archiveEngine.getAllIdPorteDelegate(filtroPorteDelegate);
				if(idsPD!=null && idsPD.size()>0){
					for (IDPortaDelegata idPD : idsPD) {
						this.readPortaDelegata(archive, idPD, cascadeConfig, ArchiveType.RUOLO);
					}
				}
			}catch(DriverRegistroServiziNotFound notFound){}	
			
			
			// porteApplicative
			
			FiltroRicercaPorteApplicative filtroPorteApplicative = new FiltroRicercaPorteApplicative();
			filtroPorteApplicative.setIdRuolo(idRuolo);
			try{
				List<IDPortaApplicativa> idsPA = this.archiveEngine.getAllIdPorteApplicative(filtroPorteApplicative);
				if(idsPA!=null && idsPA.size()>0){
					for (IDPortaApplicativa idPA : idsPA) {
						this.readPortaApplicativa(archive, idPA, cascadeConfig, ArchiveType.RUOLO);
					}
				}
			}catch(DriverRegistroServiziNotFound notFound){}
			
			
			// serviziApplicativi
			
			FiltroRicercaServiziApplicativi filtroServiziApplicativi = new FiltroRicercaServiziApplicativi();
			filtroServiziApplicativi.setIdRuolo(idRuolo);
			try{
				List<IDServizioApplicativo> idsSA = this.archiveEngine.getAllIdServiziApplicativi(filtroServiziApplicativi);
				if(idsSA!=null && idsSA.size()>0){
					for (IDServizioApplicativo idSA : idsSA) {
						this.readServizioApplicativo(archive, idSA, cascadeConfig, ArchiveType.RUOLO);
					}
				}
			}catch(DriverRegistroServiziNotFound notFound){}
		}
		
		
	}
	
	
	private void readSoggetto(Archive archive, IDSoggetto idSoggetto, ArchiveCascadeConfiguration cascadeConfig, ArchiveType provenienza) throws Exception{
		this.readSoggetto(archive, idSoggetto, cascadeConfig, true, provenienza);
	}
	private void readSoggetto(Archive archive, IDSoggetto idSoggetto, ArchiveCascadeConfiguration cascadeConfig, boolean cascadeAvanti, ArchiveType provenienza) throws Exception{
		
		String key = ArchiveSoggetto.buildKey(idSoggetto.getTipo(), idSoggetto.getNome());
		if(archive.getSoggetti().containsKey(key)){
			// gia gestito
		}
		else{
			try{
				if(cascadeConfig.isCascadeSoggetti() || ArchiveType.SOGGETTO.equals(provenienza)){
					
					// add
					org.openspcoop2.core.registry.Soggetto soggettoRegistro = this.archiveEngine.getSoggettoRegistro(idSoggetto);
					org.openspcoop2.core.config.Soggetto soggettoConfigurazione = this.archiveEngine.getSoggettoConfigurazione(idSoggetto);
					ArchiveSoggetto archiveSoggetto = new ArchiveSoggetto(soggettoConfigurazione, soggettoRegistro, this.idCorrelazione);
					archive.getSoggetti().add(archiveSoggetto);
					
					// *** dipendenze: oggetti necessari per la creazione dell'oggetto sopra aggiunto ***
					
					// porteDominio
					FiltroRicerca filtroPdd = new FiltroRicerca();
					filtroPdd.setNome(soggettoRegistro.getPortaDominio());
					try{
						List<String> idsPdD = this.archiveEngine.getAllIdPorteDominio(filtroPdd);
						if(idsPdD!=null && idsPdD.size()>0){
							for (String idPdd : idsPdD) {
								this.readPdd(archive, idPdd, cascadeConfig, false, ArchiveType.SOGGETTO); // per evitare loop
							}
						}
					}catch(DriverRegistroServiziNotFound notFound){}
					
					// ruoli
					if(soggettoRegistro.getRuoli()!=null && soggettoRegistro.getRuoli().sizeRuoloList()>0){
						for (int i = 0; i < soggettoRegistro.getRuoli().sizeRuoloList(); i++) {
							RuoloSoggetto ruolo = soggettoRegistro.getRuoli().getRuolo(i);
							FiltroRicercaRuoli filtroRuolo = new FiltroRicercaRuoli();
							filtroRuolo.setNome(ruolo.getNome());
							try{
								List<IDRuolo> idsRuoli = this.archiveEngine.getAllIdRuoli(filtroRuolo);
								if(idsRuoli!=null && idsRuoli.size()>0){
									for (IDRuolo idRuolo : idsRuoli) {
										this.readRuolo(archive, idRuolo, cascadeConfig, false, ArchiveType.SOGGETTO); // per evitare loop
									}
								}
							}catch(DriverRegistroServiziNotFound notFound){}	
						}
					}
				}
			}catch(Exception e){
				throw new ProtocolException("(Soggetto "+idSoggetto+") "+e.getMessage(),e);
			}
		}
		
		
		// *** cascade in avanti ***
		
		if(cascadeAvanti){
		
			// serviziApplicativi		
			FiltroRicercaServiziApplicativi filtroServiziApplicativi = new FiltroRicercaServiziApplicativi();
			filtroServiziApplicativi.setTipoSoggetto(idSoggetto.getTipo());
			filtroServiziApplicativi.setNomeSoggetto(idSoggetto.getNome());
			try{
				List<IDServizioApplicativo> idsSA = this.archiveEngine.getAllIdServiziApplicativi(filtroServiziApplicativi);
				if(idsSA!=null && idsSA.size()>0){
					for (IDServizioApplicativo idServizioApplicativo : idsSA) {
						this.readServizioApplicativo(archive, idServizioApplicativo, cascadeConfig, ArchiveType.SOGGETTO);
					}
				}
			}catch(DriverConfigurazioneNotFound notFound){}
				
			// accordi cooperazione
			FiltroRicercaAccordi filtroAccordiCooperazione = new FiltroRicercaAccordi();
			filtroAccordiCooperazione.setTipoSoggettoReferente(idSoggetto.getTipo());
			filtroAccordiCooperazione.setNomeSoggettoReferente(idSoggetto.getNome());
			try{
				List<IDAccordoCooperazione> idsAC = this.archiveEngine.getAllIdAccordiCooperazione(filtroAccordiCooperazione);
				if(idsAC!=null && idsAC.size()>0){
					for (IDAccordoCooperazione idAccordoCooperazione : idsAC) {
						this.readAccordoCooperazione(archive, idAccordoCooperazione, cascadeConfig, ArchiveType.SOGGETTO);
					}
				}
			}catch(DriverRegistroServiziNotFound notFound){}
			
			// accordi servizio parte comune
			FiltroRicercaAccordi filtroAccordiServizioParteComune = new FiltroRicercaAccordi();
			filtroAccordiServizioParteComune.setTipoSoggettoReferente(idSoggetto.getTipo());
			filtroAccordiServizioParteComune.setNomeSoggettoReferente(idSoggetto.getNome());
			filtroAccordiServizioParteComune.setServizioComposto(false);
			try{
				List<IDAccordo> idsAccordi = this.archiveEngine.getAllIdAccordiServizioParteComune(filtroAccordiServizioParteComune);
				if(idsAccordi!=null && idsAccordi.size()>0){
					for (IDAccordo idAccordo : idsAccordi) {
						this.readAccordoServizioParteComune(archive, idAccordo, cascadeConfig, ArchiveType.SOGGETTO);
					}
				}
			}catch(DriverRegistroServiziNotFound notFound){}	
			
			// accordi servizio composto
			FiltroRicercaAccordi filtroAccordiServizioComposto = new FiltroRicercaAccordi();
			filtroAccordiServizioComposto.setTipoSoggettoReferente(idSoggetto.getTipo());
			filtroAccordiServizioComposto.setNomeSoggettoReferente(idSoggetto.getNome());
			filtroAccordiServizioComposto.setServizioComposto(true);
			try{
				List<IDAccordo> idsAccordi = this.archiveEngine.getAllIdAccordiServizioParteComune(filtroAccordiServizioComposto);
				if(idsAccordi!=null && idsAccordi.size()>0){
					for (IDAccordo idAccordo : idsAccordi) {
						this.readAccordoServizioComposto(archive, idAccordo, cascadeConfig, ArchiveType.SOGGETTO);
					}
				}
			}catch(DriverRegistroServiziNotFound notFound){}	
			
			// accordi servizio parte specifica
			FiltroRicercaServizi filtroAccordiServizioParteSpecifica = new FiltroRicercaServizi();
			filtroAccordiServizioParteSpecifica.setTipoSoggettoErogatore(idSoggetto.getTipo());
			filtroAccordiServizioParteSpecifica.setNomeSoggettoErogatore(idSoggetto.getNome());
			try{
				List<IDServizio> idsAccordi = this.archiveEngine.getAllIdAccordiServizioParteSpecifica(filtroAccordiServizioParteSpecifica);
				if(idsAccordi!=null && idsAccordi.size()>0){
					for (IDServizio idAccordo : idsAccordi) {
						this.readAccordoServizioParteSpecifica(archive, idAccordo, cascadeConfig, ArchiveType.SOGGETTO);
					}
				}
			}catch(DriverRegistroServiziNotFound notFound){}	
			
			// porteDelegate
			FiltroRicercaPorteDelegate filtroPorteDelegate = new FiltroRicercaPorteDelegate();
			filtroPorteDelegate.setTipoSoggetto(idSoggetto.getTipo());
			filtroPorteDelegate.setNomeSoggetto(idSoggetto.getNome());
			try{
				List<IDPortaDelegata> idsPD = this.archiveEngine.getAllIdPorteDelegate(filtroPorteDelegate);
				if(idsPD!=null && idsPD.size()>0){
					for (IDPortaDelegata idPortaDelegata : idsPD) {
						this.readPortaDelegata(archive, idPortaDelegata, cascadeConfig, ArchiveType.SOGGETTO);
					}
				}
			}catch(DriverConfigurazioneNotFound notFound){}
			
			// porteApplicative
			FiltroRicercaPorteApplicative filtroPorteApplicative = new FiltroRicercaPorteApplicative();
			filtroPorteApplicative.setTipoSoggetto(idSoggetto.getTipo());
			filtroPorteApplicative.setNomeSoggetto(idSoggetto.getNome());
			try{
				List<IDPortaApplicativa> idsPA = this.archiveEngine.getAllIdPorteApplicative(filtroPorteApplicative);
				if(idsPA!=null && idsPA.size()>0){
					for (IDPortaApplicativa idPortaApplicativa : idsPA) {
						this.readPortaApplicativa(archive, idPortaApplicativa, cascadeConfig, ArchiveType.SOGGETTO);
					}
				}
			}catch(DriverConfigurazioneNotFound notFound){}
			
		}
		
			
	}
	
	private void readServizioApplicativo(Archive archive, IDServizioApplicativo idServizioApplicativo, 
			ArchiveCascadeConfiguration cascadeConfig, ArchiveType provenienza) throws Exception{
		this.readServizioApplicativo(archive, idServizioApplicativo, cascadeConfig, true, provenienza);
	}
	private void readServizioApplicativo(Archive archive, IDServizioApplicativo idServizioApplicativo, 
			ArchiveCascadeConfiguration cascadeConfig, boolean cascadeAvanti, ArchiveType provenienza) throws Exception{
		
		String key =  ArchiveServizioApplicativo.buildKey(idServizioApplicativo.getIdSoggettoProprietario().getTipo(),
				idServizioApplicativo.getIdSoggettoProprietario().getNome(), 
				idServizioApplicativo.getNome());
		if(archive.getServiziApplicativi().containsKey(key)){
			// gia gestito
		}
		else{
			try{
				// Fanno eccezione i servizi applicativi per i quali esiste piu' di un padre. In questo caso si esamina il padre:
				// - nel caso di padre=soggetto/sa si vede esclusivamente il cascade dei servizi applicativi
				// - nel caso di padre=porta delegata si controlla sia il cascade dei servizi applicativi che il cascade della porta delegata
				// - nel caso di padre=porta applicativa si controlla sia il cascade dei servizi applicativi che il cascade della porta applicativa
				// - nel caso di padre=fruitore si controlla sia il cascade dei servizi applicativi che il cascade delle fruizioni
				boolean enabled = cascadeConfig.isCascadeServiziApplicativi();
				switch (provenienza) {
				case SOGGETTO:
					break;
				case SERVIZIO_APPLICATIVO:
					enabled = enabled || ArchiveType.SERVIZIO_APPLICATIVO.equals(provenienza);
					break;
				case PORTA_DELEGATA:
					enabled = enabled && cascadeConfig.isCascadePorteDelegate();
					break;
				case PORTA_APPLICATIVA:
					enabled = enabled && cascadeConfig.isCascadePorteApplicative();
					break;
				case FRUITORE:
					enabled = enabled && cascadeConfig.isCascadeFruizioni();
					break;
				default:
					break;
				}
				
				if(enabled){
					
					// add
					org.openspcoop2.core.config.ServizioApplicativo sa = this.archiveEngine.getServizioApplicativo(idServizioApplicativo);
					if(sa.getTipoSoggettoProprietario()==null){
						sa.setTipoSoggettoProprietario(idServizioApplicativo.getIdSoggettoProprietario().getTipo());
					}
					if(sa.getNomeSoggettoProprietario()==null){
						sa.setNomeSoggettoProprietario(idServizioApplicativo.getIdSoggettoProprietario().getNome());
					}
					ArchiveServizioApplicativo archiveSa = new ArchiveServizioApplicativo(sa, this.idCorrelazione);
					archive.getServiziApplicativi().add(archiveSa);
					
					// *** dipendenze: oggetti necessari per la creazione dell'oggetto sopra aggiunto ***
					
					// soggetto proprietario
					this.readSoggetto(archive, idServizioApplicativo.getIdSoggettoProprietario(), cascadeConfig, false, ArchiveType.SERVIZIO_APPLICATIVO); // per evitare loop
					
					// ruoli
					if(sa.getInvocazionePorta()!=null && sa.getInvocazionePorta().getRuoli()!=null && 
							sa.getInvocazionePorta().getRuoli().sizeRuoloList()>0){
						for (int i = 0; i < sa.getInvocazionePorta().getRuoli().sizeRuoloList(); i++) {
							Ruolo ruolo = sa.getInvocazionePorta().getRuoli().getRuolo(i);
							FiltroRicercaRuoli filtroRuolo = new FiltroRicercaRuoli();
							filtroRuolo.setNome(ruolo.getNome());
							try{
								List<IDRuolo> idsRuoli = this.archiveEngine.getAllIdRuoli(filtroRuolo);
								if(idsRuoli!=null && idsRuoli.size()>0){
									for (IDRuolo idRuolo : idsRuoli) {
										this.readRuolo(archive, idRuolo, cascadeConfig, false, ArchiveType.SERVIZIO_APPLICATIVO); // per evitare loop
									}
								}
							}catch(DriverRegistroServiziNotFound notFound){}	
						}
					}
				}
			}catch(Exception e){
				throw new ProtocolException("(ServizioApplicativo "+idServizioApplicativo+") "+e.getMessage(),e);
			}
		}
		
		
		// *** cascade in avanti ***
		
		if(cascadeAvanti){
		
			// non vi sono oggetti con padre un servizio applicativo
			
		}
		
	}
	
	private void readAccordoCooperazione(Archive archive, IDAccordoCooperazione idAccordoCooperazione, 
			ArchiveCascadeConfiguration cascadeConfig, ArchiveType provenienza) throws Exception{
		this.readAccordoCooperazione(archive, idAccordoCooperazione, cascadeConfig, true, provenienza);
	}
	private void readAccordoCooperazione(Archive archive, IDAccordoCooperazione idAccordoCooperazione, 
			ArchiveCascadeConfiguration cascadeConfig, boolean cascadeAvanti, ArchiveType provenienza) throws Exception{
		
		String tipoSoggetto = null;
		String nomeSoggetto = null;
		if(idAccordoCooperazione.getSoggettoReferente()!=null){
			tipoSoggetto = idAccordoCooperazione.getSoggettoReferente().getTipo();
			nomeSoggetto = idAccordoCooperazione.getSoggettoReferente().getNome();
		}
		
		String key =  ArchiveAccordoCooperazione.buildKey(tipoSoggetto,nomeSoggetto,
				idAccordoCooperazione.getNome(),idAccordoCooperazione.getVersione());
		if(archive.getAccordiCooperazione().containsKey(key)){
			// gia gestito
		}
		else{
			try{
				
				if(cascadeConfig.isCascadeAccordoCooperazione() || ArchiveType.ACCORDO_COOPERAZIONE.equals(provenienza)){
				
					// add
					org.openspcoop2.core.registry.AccordoCooperazione ac = this.archiveEngine.getAccordoCooperazione(idAccordoCooperazione,true);
					ArchiveAccordoCooperazione archiveAc = new ArchiveAccordoCooperazione(ac, this.idCorrelazione);
					archive.getAccordiCooperazione().add(archiveAc);
					
					// *** dipendenze: oggetti necessari per la creazione dell'oggetto sopra aggiunto ***
					
					// soggetto referente
					this.readSoggetto(archive, idAccordoCooperazione.getSoggettoReferente(), cascadeConfig, false, ArchiveType.ACCORDO_COOPERAZIONE); // per evitare loop
					
					// soggetti partecipanti
					if( ac.getElencoPartecipanti()!=null &&  ac.getElencoPartecipanti().sizeSoggettoPartecipanteList()>0){
						for (IdSoggetto partecipante : ac.getElencoPartecipanti().getSoggettoPartecipanteList()) {
							IDSoggetto idPartecipante = new IDSoggetto(partecipante.getTipo(), partecipante.getNome());
							this.readSoggetto(archive, idPartecipante, cascadeConfig, false, ArchiveType.ACCORDO_COOPERAZIONE); // per evitare loop
							// tutti gli accordi esati o fruiti dai soggetti partecipanti saranno gestiti dagli accordi di servizio composti tramite i servizi componenti
						}
					}
					
				}
					
			}catch(Exception e){
				throw new ProtocolException("(AccordoCooperazione "+IDAccordoCooperazioneFactory.getInstance().getUriFromIDAccordo(idAccordoCooperazione)+") "+e.getMessage(),e);
			}
		}
	
		
		// *** cascade in avanti ***
		
		if(cascadeAvanti){
		
			// accordi di servizio composti
			FiltroRicercaAccordi filtroRicercaAccordi = new FiltroRicercaAccordi();
			filtroRicercaAccordi.setIdAccordoCooperazione(idAccordoCooperazione);
			try{
				List<IDAccordo> idsAS = this.archiveEngine.getAllIdAccordiServizioParteComune(filtroRicercaAccordi);
				if(idsAS!=null && idsAS.size()>0){
					for (IDAccordo idAccordo : idsAS) {
						this.readAccordoServizioComposto(archive, idAccordo, cascadeConfig, ArchiveType.ACCORDO_COOPERAZIONE);
					}
				}
			}catch(DriverRegistroServiziNotFound notFound){}
			
		}
		
	}
	
	private void readAccordoServizioParteComune(Archive archive, IDAccordo idAccordoServizio, 
			ArchiveCascadeConfiguration cascadeConfig, ArchiveType provenienza) throws Exception{
		this.readAccordoServizioParteComune(archive, idAccordoServizio, cascadeConfig, true, provenienza);
	}
	private void readAccordoServizioParteComune(Archive archive, IDAccordo idAccordoServizio, 
			ArchiveCascadeConfiguration cascadeConfig, boolean cascadeAvanti, ArchiveType provenienza) throws Exception{
		
		String tipoSoggetto = null;
		String nomeSoggetto = null;
		if(idAccordoServizio.getSoggettoReferente()!=null){
			tipoSoggetto = idAccordoServizio.getSoggettoReferente().getTipo();
			nomeSoggetto = idAccordoServizio.getSoggettoReferente().getNome();
		}
		String key =  ArchiveAccordoServizioParteComune.buildKey(tipoSoggetto,nomeSoggetto,
				idAccordoServizio.getNome(),idAccordoServizio.getVersione());
		if(archive.getAccordiServizioParteComune().containsKey(key)){
			// gia gestito
		}
		else{
			try{
				
				if(cascadeConfig.isCascadeAccordoServizioParteComune() || ArchiveType.ACCORDO_SERVIZIO_PARTE_COMUNE.equals(provenienza)){
				
					// add
					org.openspcoop2.core.registry.AccordoServizioParteComune as = this.archiveEngine.getAccordoServizioParteComune(idAccordoServizio,true);
					ArchiveAccordoServizioParteComune archiveAs = new ArchiveAccordoServizioParteComune(as, this.idCorrelazione);
					archive.getAccordiServizioParteComune().add(archiveAs);
					
					// *** dipendenze: oggetti necessari per la creazione dell'oggetto sopra aggiunto ***
					
					this.readSoggetto(archive, idAccordoServizio.getSoggettoReferente(), cascadeConfig, false, ArchiveType.ACCORDO_SERVIZIO_PARTE_COMUNE); // per evitare loop
					
				}
				
			}catch(Exception e){
				throw new ProtocolException("(AccordoServizioParteComune "+IDAccordoFactory.getInstance().getUriFromIDAccordo(idAccordoServizio)+") "+e.getMessage(),e);
			}
		}
	
		
		// *** cascade in avanti ***
		
		if(cascadeAvanti){
		
			// accordi di servizio parte specifica
			FiltroRicercaServizi filtroRicercaAccordi = new FiltroRicercaServizi();
			filtroRicercaAccordi.setIdAccordoServizioParteComune(idAccordoServizio);
			try{
				List<IDServizio> idsAS = this.archiveEngine.getAllIdAccordiServizioParteSpecifica(filtroRicercaAccordi);
				if(idsAS!=null && idsAS.size()>0){
					for (IDServizio idAccordo : idsAS) {
						this.readAccordoServizioParteSpecifica(archive, idAccordo, cascadeConfig, ArchiveType.ACCORDO_SERVIZIO_PARTE_COMUNE);
					}
				}
			}catch(DriverRegistroServiziNotFound notFound){}
			
		}
		
	}
	
	private void readAccordoServizioComposto(Archive archive, IDAccordo idAccordoServizio, 
			ArchiveCascadeConfiguration cascadeConfig, ArchiveType provenienza) throws Exception{
		this.readAccordoServizioComposto(archive, idAccordoServizio, cascadeConfig, true, provenienza);
	}
	private void readAccordoServizioComposto(Archive archive, IDAccordo idAccordoServizio, 
			ArchiveCascadeConfiguration cascadeConfig, boolean cascadeAvanti, ArchiveType provenienza) throws Exception{
		
		String tipoSoggetto = null;
		String nomeSoggetto = null;
		if(idAccordoServizio.getSoggettoReferente()!=null){
			tipoSoggetto = idAccordoServizio.getSoggettoReferente().getTipo();
			nomeSoggetto = idAccordoServizio.getSoggettoReferente().getNome();
		}
		String key =  ArchiveAccordoServizioComposto.buildKey(tipoSoggetto,nomeSoggetto,
				idAccordoServizio.getNome(),idAccordoServizio.getVersione());
		org.openspcoop2.core.registry.AccordoServizioParteComune as = null;
		if(archive.getAccordiServizioComposto().containsKey(key)){
			// gia gestito
			as = archive.getAccordiServizioComposto().get(key).getAccordoServizioParteComune();
		}
		else{
			try{
				if(cascadeConfig.isCascadeAccordoServizioComposto() || ArchiveType.ACCORDO_SERVIZIO_COMPOSTO.equals(provenienza)){
				
					// add
					as = this.archiveEngine.getAccordoServizioParteComune(idAccordoServizio,true);
					ArchiveAccordoServizioComposto archiveAs = new ArchiveAccordoServizioComposto(as, this.idCorrelazione);
					archive.getAccordiServizioComposto().add(archiveAs);
				
					// *** dipendenze: oggetti necessari per la creazione dell'oggetto sopra aggiunto ***
					
					this.readSoggetto(archive, idAccordoServizio.getSoggettoReferente(), cascadeConfig, false, ArchiveType.ACCORDO_SERVIZIO_COMPOSTO); // per evitare loop
					
				}
				else{
					as = this.archiveEngine.getAccordoServizioParteComune(idAccordoServizio,false);
				}	
			}catch(Exception e){
				throw new ProtocolException("(AccordoServizioComposto "+IDAccordoFactory.getInstance().getUriFromIDAccordo(idAccordoServizio)+") "+e.getMessage(),e);
			}
		}
	
		
		// *** cascade in avanti ***
		
		if(cascadeAvanti){
		
			// accordi di servizio parte specifica
			FiltroRicercaServizi filtroRicercaAccordi = new FiltroRicercaServizi();
			filtroRicercaAccordi.setIdAccordoServizioParteComune(idAccordoServizio);
			try{
				List<IDServizio> idsAS = this.archiveEngine.getAllIdAccordiServizioParteSpecifica(filtroRicercaAccordi);
				if(idsAS!=null && idsAS.size()>0){
					for (IDServizio idAccordo : idsAS) {
						this.readAccordoServizioParteSpecifica(archive, idAccordo, cascadeConfig, ArchiveType.ACCORDO_SERVIZIO_COMPOSTO);
					}
				}
			}catch(DriverRegistroServiziNotFound notFound){}

			// servizi componenti
			for (AccordoServizioParteComuneServizioCompostoServizioComponente servizioComponente : as.getServizioComposto().getServizioComponenteList()) {
				IDServizio idServizio = IDServizioFactory.getInstance().getIDServizioFromValues(servizioComponente.getTipo(),servizioComponente.getNome(), 
						servizioComponente.getTipoSoggetto(),servizioComponente.getNomeSoggetto(), 
						servizioComponente.getVersione()); 
				this.readAccordoServizioParteSpecifica(archive, idServizio, cascadeConfig, ArchiveType.ACCORDO_SERVIZIO_COMPOSTO); 
			}
			
		}
		

	}

	private void readAccordoServizioParteSpecifica(Archive archive, IDServizio idAccordoServizio, 
			ArchiveCascadeConfiguration cascadeConfig, ArchiveType provenienza) throws Exception{
		this.readAccordoServizioParteSpecifica(archive, idAccordoServizio, cascadeConfig, true, provenienza);
	}
	private void readAccordoServizioParteSpecifica(Archive archive, IDServizio idAccordoServizio, 
			ArchiveCascadeConfiguration cascadeConfig, boolean cascadeAvanti, ArchiveType provenienza) throws Exception{
		
		String tipoSoggetto = null;
		String nomeSoggetto = null;
		if(idAccordoServizio.getSoggettoErogatore()!=null){
			tipoSoggetto = idAccordoServizio.getSoggettoErogatore().getTipo();
			nomeSoggetto = idAccordoServizio.getSoggettoErogatore().getNome();
		}
		String key =  ArchiveAccordoServizioParteSpecifica.buildKey(tipoSoggetto,nomeSoggetto,
				idAccordoServizio.getTipo(),idAccordoServizio.getNome(),idAccordoServizio.getVersione());
		List<Fruitore> fruitoriList = null;
		org.openspcoop2.core.registry.AccordoServizioParteSpecifica  as = null;
		if(archive.getAccordiServizioParteSpecifica().containsKey(key)){
			// gia gestito
			as = archive.getAccordiServizioParteSpecifica().get(key).getAccordoServizioParteSpecifica();
		}
		else{
			try{
				if(cascadeConfig.isCascadeAccordoServizioParteSpecifica() || ArchiveType.ACCORDO_SERVIZIO_PARTE_SPECIFICA.equals(provenienza)){
					fruitoriList = new ArrayList<Fruitore>();
				
					// add
					as = this.archiveEngine.getAccordoServizioParteSpecifica(idAccordoServizio,true);
					if(as.getTipoSoggettoErogatore()==null){
						as.setTipoSoggettoErogatore(tipoSoggetto);
					}
					if(as.getNomeSoggettoErogatore()==null){
						as.setNomeSoggettoErogatore(nomeSoggetto);
					}
					ArchiveAccordoServizioParteSpecifica archiveAs = new ArchiveAccordoServizioParteSpecifica(as, this.idCorrelazione);
					while(as.sizeFruitoreList()>0){
						fruitoriList.add(as.removeFruitore(0));
					}
					archive.getAccordiServizioParteSpecifica().add(archiveAs);
					
					
					// porteApplicative associate
					List<IDPortaApplicativa> listIDPA = this.archiveEngine.getIDPorteApplicativeAssociateErogazione(idAccordoServizio);
					if(listIDPA!=null && listIDPA.size()>0){
						if(archiveAs.getMappingPorteApplicativeAssociate()==null) {
							archiveAs.setMappingPorteApplicativeAssociate(new ArrayList<>());
						}
						for (IDPortaApplicativa idPortaApplicativa : listIDPA) {
							archiveAs.getMappingPorteApplicativeAssociate().add(this.archiveEngine.getMappingErogazionePortaApplicativa(idAccordoServizio, idPortaApplicativa));
						}
					}
					
					
					// *** dipendenze: oggetti necessari per la creazione dell'oggetto sopra aggiunto ***
					
					// soggetto proprietario
					this.readSoggetto(archive, idAccordoServizio.getSoggettoErogatore(), cascadeConfig, false, ArchiveType.ACCORDO_SERVIZIO_PARTE_SPECIFICA); // per evitare loop
				
					// accordoServizioParteComune
					IDAccordo idAccordoServizioParteComune = IDAccordoFactory.getInstance().getIDAccordoFromUri(as.getAccordoServizioParteComune());
					AccordoServizioParteComune aspc = this.archiveEngine.getAccordoServizioParteComune(idAccordoServizioParteComune);
					if(aspc.getServizioComposto()==null){
						this.readAccordoServizioParteComune(archive, idAccordoServizioParteComune, cascadeConfig, false, ArchiveType.ACCORDO_SERVIZIO_PARTE_SPECIFICA); // per evitare loop
					}
					else{
						this.readAccordoServizioComposto(archive, idAccordoServizioParteComune, cascadeConfig, false, ArchiveType.ACCORDO_SERVIZIO_PARTE_SPECIFICA); // per evitare loop
					}
				}
					
			}catch(Exception e){
				throw new ProtocolException("(AccordoServizioParteSpecifica "+IDServizioFactory.getInstance().getUriFromIDServizio(idAccordoServizio)+") "+e.getMessage(),e);
			}
		}
		
		
		// *** cascade in avanti ***
		
		if(cascadeAvanti){
		
			// fruitori
			if(fruitoriList==null){
				// devo rileggere l'accordo. Quello salvato non contiene i fruitori
				fruitoriList = new ArrayList<Fruitore>();
				as = this.archiveEngine.getAccordoServizioParteSpecifica(idAccordoServizio,true);
				while(as.sizeFruitoreList()>0){
					fruitoriList.add(as.removeFruitore(0));
				}
			}
			for (Fruitore fruitore : fruitoriList) {
				IDSoggetto idFruitore = new IDSoggetto(fruitore.getTipo(), fruitore.getNome());
				this.readFruitore(archive, idAccordoServizio, idFruitore, fruitore, cascadeConfig, ArchiveType.ACCORDO_SERVIZIO_PARTE_SPECIFICA);
			}
			
			// porteDelegate
			FiltroRicercaPorteDelegate filtroRicercaPorteDelegate = new FiltroRicercaPorteDelegate();
			filtroRicercaPorteDelegate.setTipoSoggettoErogatore(idAccordoServizio.getSoggettoErogatore().getTipo());
			filtroRicercaPorteDelegate.setNomeSoggettoErogatore(idAccordoServizio.getSoggettoErogatore().getNome());
			filtroRicercaPorteDelegate.setTipoServizio(as.getTipo());
			filtroRicercaPorteDelegate.setNomeServizio(as.getNome());
			filtroRicercaPorteDelegate.setVersioneServizio(as.getVersione());
			try{
				List<IDPortaDelegata> idsPD = this.archiveEngine.getAllIdPorteDelegate(filtroRicercaPorteDelegate);
				if(idsPD!=null && idsPD.size()>0){
					for (IDPortaDelegata idPortaDelegata : idsPD) {
						this.readPortaDelegata(archive, idPortaDelegata, cascadeConfig, ArchiveType.ACCORDO_SERVIZIO_PARTE_SPECIFICA);
					}
				}
			}catch(DriverConfigurazioneNotFound notFound){}
			
			// porteApplicative associate
			List<IDPortaApplicativa> listIDPA_associate = this.archiveEngine.getIDPorteApplicativeAssociateErogazione(idAccordoServizio);
			if(listIDPA_associate!=null && listIDPA_associate.size()>0){
				for (IDPortaApplicativa idPA_associata : listIDPA_associate) {
					this.readPortaApplicativa(archive, idPA_associata, cascadeConfig, ArchiveType.ACCORDO_SERVIZIO_PARTE_SPECIFICA);
				}
			}
			
			// porteApplicative "normali"
			FiltroRicercaPorteApplicative filtroRicercaPorteApplicative = new FiltroRicercaPorteApplicative();
			filtroRicercaPorteApplicative.setTipoSoggetto(idAccordoServizio.getSoggettoErogatore().getTipo());
			filtroRicercaPorteApplicative.setNomeSoggetto(idAccordoServizio.getSoggettoErogatore().getNome());
			filtroRicercaPorteApplicative.setTipoServizio(as.getTipo());
			filtroRicercaPorteApplicative.setNomeServizio(as.getNome());
			filtroRicercaPorteApplicative.setVersioneServizio(as.getVersione());
			try{
				List<IDPortaApplicativa> idsPA = this.archiveEngine.getAllIdPorteApplicative(filtroRicercaPorteApplicative);
				if(idsPA!=null && idsPA.size()>0){
					for (IDPortaApplicativa idPortaApplicativa : idsPA) {
						if(listIDPA_associate!=null && listIDPA_associate.size()>0){
							if(listIDPA_associate.contains(idPortaApplicativa)){
								continue;
							}
						}
						this.readPortaApplicativa(archive, idPortaApplicativa, cascadeConfig, ArchiveType.ACCORDO_SERVIZIO_PARTE_SPECIFICA);
					}
				}
			}catch(DriverConfigurazioneNotFound notFound){}
				
			// porteApplicative "virtuali"
			filtroRicercaPorteApplicative = new FiltroRicercaPorteApplicative();
			filtroRicercaPorteApplicative.setTipoSoggettoVirtuale(idAccordoServizio.getSoggettoErogatore().getTipo());
			filtroRicercaPorteApplicative.setNomeSoggettoVirtuale(idAccordoServizio.getSoggettoErogatore().getNome());
			filtroRicercaPorteApplicative.setTipoServizio(as.getTipo());
			filtroRicercaPorteApplicative.setNomeServizio(as.getNome());
			filtroRicercaPorteApplicative.setVersioneServizio(as.getVersione());
			try{
				List<IDPortaApplicativa> idsPA = this.archiveEngine.getAllIdPorteApplicative(filtroRicercaPorteApplicative);
				if(idsPA!=null && idsPA.size()>0){
					for (IDPortaApplicativa idPortaApplicativa : idsPA) {
						this.readPortaApplicativa(archive, idPortaApplicativa, cascadeConfig, ArchiveType.ACCORDO_SERVIZIO_PARTE_SPECIFICA);
					}
				}
			}catch(DriverConfigurazioneNotFound notFound){}
						
		}
		
	}
	
	
	private void readFruitore(Archive archive, IDServizio idAccordoServizio, IDSoggetto idFruitore, Fruitore fruitore, 
			ArchiveCascadeConfiguration cascadeConfig, ArchiveType provenienza) throws Exception{
		this.readFruitore(archive, idAccordoServizio, idFruitore, fruitore, cascadeConfig, true, provenienza);
	}
	private void readFruitore(Archive archive, IDServizio idAccordoServizio, IDSoggetto idFruitore, Fruitore fruitore, 
			ArchiveCascadeConfiguration cascadeConfig, boolean cascadeAvanti, ArchiveType provenienza) throws Exception{
		
		String tipoSoggetto = null;
		String nomeSoggetto = null;
		if(idAccordoServizio.getSoggettoErogatore()!=null){
			tipoSoggetto = idAccordoServizio.getSoggettoErogatore().getTipo();
			nomeSoggetto = idAccordoServizio.getSoggettoErogatore().getNome();
		}
		String key =  ArchiveFruitore.buildKey(idFruitore.getTipo(),idFruitore.getNome(),
				tipoSoggetto,nomeSoggetto,
				idAccordoServizio.getTipo(),idAccordoServizio.getNome(),idAccordoServizio.getVersione());
		if(archive.getAccordiFruitori().containsKey(key)){
			// gia gestito
		}
		else{
			try{
				if(cascadeConfig.isCascadeFruizioni() || ArchiveType.FRUITORE.equals(provenienza)){
				
					// add
					if(fruitore==null){
						org.openspcoop2.core.registry.AccordoServizioParteSpecifica as = this.archiveEngine.getAccordoServizioParteSpecifica(idAccordoServizio,true);
						for (Fruitore fruitoreCheck : as.getFruitoreList()) {
							if(fruitoreCheck.getTipo().equals(idFruitore.getTipo()) &&
									fruitoreCheck.getNome().equals(idFruitore.getNome())){
								fruitore = fruitoreCheck;
								break;
							}
						}
					}
					
					ArchiveFruitore archiveFruitore = new ArchiveFruitore(idAccordoServizio, fruitore, this.idCorrelazione);
					archive.getAccordiFruitori().add(archiveFruitore);
					
					// porteDelegate associate
					List<IDPortaDelegata> listIDPD = this.archiveEngine.getIDPorteDelegateAssociateFruizione(idAccordoServizio, idFruitore);
					if(listIDPD!=null && listIDPD.size()>0){
						if(archiveFruitore.getMappingPorteDelegateAssociate()==null) {
							archiveFruitore.setMappingPorteDelegateAssociate(new ArrayList<>());
						}
						for (IDPortaDelegata idPortaDelegata : listIDPD) {
							archiveFruitore.getMappingPorteDelegateAssociate().add( this.archiveEngine.getMappingFruizionePortaDelegata(idAccordoServizio, idFruitore, idPortaDelegata));
						}
					}
					
					// *** dipendenze: oggetti necessari per la creazione dell'oggetto sopra aggiunto ***
					
					// soggetto
					this.readSoggetto(archive, idFruitore, cascadeConfig, false, ArchiveType.FRUITORE); // per evitare loop
					
					// accordoServizioParteSpecifica
					this.readAccordoServizioParteSpecifica(archive, idAccordoServizio, cascadeConfig, false, ArchiveType.FRUITORE); // per evitare loop
					
				}
					
			}catch(Exception e){
				throw new ProtocolException("(Fruitore "+idFruitore+" dell'accordo "+IDServizioFactory.getInstance().getUriFromIDServizio(idAccordoServizio)+") "+e.getMessage(),e);
			}
		}
		
		
		// *** cascade in avanti ***
		
		if(cascadeAvanti){
		
			// porteDelegate associate
			List<IDPortaDelegata> listIDPD = this.archiveEngine.getIDPorteDelegateAssociateFruizione(idAccordoServizio, idFruitore);
			if(listIDPD!=null && listIDPD.size()>0){
				for (IDPortaDelegata idPortaDelegata_associata : listIDPD) {
					this.readPortaDelegata(archive, idPortaDelegata_associata, cascadeConfig, ArchiveType.FRUITORE);	
				}
			}
			
		}
		
	}
	
	private void readPortaDelegata(Archive archive, IDPortaDelegata idPortaDelegata, 
			ArchiveCascadeConfiguration cascadeConfig, ArchiveType provenienza) throws Exception{
		this.readPortaDelegata(archive, idPortaDelegata, cascadeConfig, true, provenienza);
	}
	private void readPortaDelegata(Archive archive, IDPortaDelegata idPortaDelegata, 
			ArchiveCascadeConfiguration cascadeConfig, boolean cascadeAvanti, ArchiveType provenienza) throws Exception{
	
		IDSoggetto idSoggettoFruitore = idPortaDelegata.getIdentificativiFruizione().getSoggettoFruitore();
		
		String key =  ArchivePortaDelegata.buildKey(idSoggettoFruitore.getTipo(),
				idSoggettoFruitore.getNome(), 
				idPortaDelegata.getNome());
		org.openspcoop2.core.config.PortaDelegata pd = null;
		if(archive.getPorteDelegate().containsKey(key)){
			// gia gestito
			pd = archive.getPorteDelegate().get(key).getPortaDelegata();
		}
		else{
			try{
				pd = this.archiveEngine.getPortaDelegata(idPortaDelegata);
				
				if(cascadeConfig.isCascadePorteDelegate() || ArchiveType.PORTA_DELEGATA.equals(provenienza)){
				
					// add
					if(pd.getTipoSoggettoProprietario()==null){
						pd.setTipoSoggettoProprietario(idSoggettoFruitore.getTipo());
					}
					if(pd.getNomeSoggettoProprietario()==null){
						pd.setNomeSoggettoProprietario(idSoggettoFruitore.getNome());
					}
					ArchivePortaDelegata archivePd = new ArchivePortaDelegata(pd, this.idCorrelazione);
					archive.getPorteDelegate().add(archivePd);
					
					// *** dipendenze: oggetti necessari per la creazione dell'oggetto sopra aggiunto ***
					
					// soggetto proprietario
					this.readSoggetto(archive, idSoggettoFruitore, cascadeConfig, false, ArchiveType.PORTA_DELEGATA); // per evitare loop
					
					// eventuale servizio riferito
					if(pd.getSoggettoErogatore()!=null && 
							pd.getSoggettoErogatore().getTipo()!=null && !"".equals(pd.getSoggettoErogatore().getTipo()) &&
							pd.getSoggettoErogatore().getNome()!=null && !"".equals(pd.getSoggettoErogatore().getNome()) &&
							pd.getServizio()!=null && 
							pd.getServizio().getTipo()!=null && !"".equals(pd.getServizio().getTipo()) &&
							pd.getServizio().getNome()!=null && !"".equals(pd.getServizio().getNome()) &&
							pd.getServizio().getVersione()!=null
							){
						
						IDServizio idServizio = IDServizioFactory.getInstance().getIDServizioFromValues(pd.getServizio().getTipo(), pd.getServizio().getNome(), 
								pd.getSoggettoErogatore().getTipo(), pd.getSoggettoErogatore().getNome(), 
								pd.getServizio().getVersione()); 
						this.readAccordoServizioParteSpecifica(archive, idServizio, cascadeConfig, false, ArchiveType.PORTA_DELEGATA); // per evitare loop
					}
					
					// ruoli
					if(pd!=null && pd.getRuoli()!=null && 
							pd.getRuoli().sizeRuoloList()>0){
						for (int i = 0; i < pd.getRuoli().sizeRuoloList(); i++) {
							Ruolo ruolo = pd.getRuoli().getRuolo(i);
							FiltroRicercaRuoli filtroRuolo = new FiltroRicercaRuoli();
							filtroRuolo.setNome(ruolo.getNome());
							try{
								List<IDRuolo> idsRuoli = this.archiveEngine.getAllIdRuoli(filtroRuolo);
								if(idsRuoli!=null && idsRuoli.size()>0){
									for (IDRuolo idRuolo : idsRuoli) {
										this.readRuolo(archive, idRuolo, cascadeConfig, false, ArchiveType.PORTA_DELEGATA); // per evitare loop
									}
								}
							}catch(DriverRegistroServiziNotFound notFound){}	
						}
					}
				}
					
			}catch(Exception e){
				throw new ProtocolException("(PortaDelegata "+idPortaDelegata+") "+e.getMessage(),e);
			}
		}
		
		
		// *** cascade in avanti ***
		
		if(cascadeAvanti){
		
			// serviziApplicativi
			if(pd.sizeServizioApplicativoList()>0){
				for (PortaDelegataServizioApplicativo sa: pd.getServizioApplicativoList()) {
					IDServizioApplicativo idSA = new IDServizioApplicativo();
					idSA.setIdSoggettoProprietario(idSoggettoFruitore);
					idSA.setNome(sa.getNome());
					this.readServizioApplicativo(archive, idSA, cascadeConfig, ArchiveType.PORTA_DELEGATA);
				}
			}
			
		}
		
	}
	
	
	private void readPortaApplicativa(Archive archive, IDPortaApplicativa idPortaApplicativa, 
			ArchiveCascadeConfiguration cascadeConfig, ArchiveType provenienza) throws Exception{
		this.readPortaApplicativa(archive, idPortaApplicativa, cascadeConfig, true, provenienza);
	}
	private void readPortaApplicativa(Archive archive, IDPortaApplicativa idPortaApplicativa, 
			ArchiveCascadeConfiguration cascadeConfig, boolean cascadeAvanti, ArchiveType provenienza) throws Exception{
	
		IDSoggetto idSoggettoErogatore = idPortaApplicativa.getIdentificativiErogazione().getIdServizio().getSoggettoErogatore();
		
		String key =  ArchivePortaApplicativa.buildKey(idSoggettoErogatore.getTipo(),
				idSoggettoErogatore.getNome(), 
				idPortaApplicativa.getNome());
		org.openspcoop2.core.config.PortaApplicativa pa = null;
		if(archive.getPorteApplicative().containsKey(key)){
			// gia gestito
			pa = archive.getPorteApplicative().get(key).getPortaApplicativa();
		}
		else{
			try{
				
				pa = this.archiveEngine.getPortaApplicativa(idPortaApplicativa);
				
				if(cascadeConfig.isCascadePorteApplicative() || ArchiveType.PORTA_APPLICATIVA.equals(provenienza)){
				
					// add
					if(pa.getTipoSoggettoProprietario()==null){
						pa.setTipoSoggettoProprietario(idSoggettoErogatore.getTipo());
					}
					if(pa.getNomeSoggettoProprietario()==null){
						pa.setNomeSoggettoProprietario(idSoggettoErogatore.getNome());
					}
					ArchivePortaApplicativa archivePa = new ArchivePortaApplicativa(pa, this.idCorrelazione);
					archive.getPorteApplicative().add(archivePa);
				
					// *** dipendenze: oggetti necessari per la creazione dell'oggetto sopra aggiunto ***
					
					// proprietario
					this.readSoggetto(archive, idSoggettoErogatore, cascadeConfig, false, ArchiveType.PORTA_APPLICATIVA); // per evitare loop
						
					// virtuale
					if(pa.getSoggettoVirtuale()!=null && 
							pa.getSoggettoVirtuale().getTipo()!=null && !"".equals(pa.getSoggettoVirtuale().getTipo()) &&
							pa.getSoggettoVirtuale().getNome()!=null && !"".equals(pa.getSoggettoVirtuale().getNome()) ){
						IDSoggetto idSoggetto = new IDSoggetto(pa.getSoggettoVirtuale().getTipo(),pa.getSoggettoVirtuale().getNome());
						this.readSoggetto(archive, idSoggetto, cascadeConfig, false, ArchiveType.PORTA_APPLICATIVA); // per evitare loop
					}
										
					// eventuale servizio riferito
					if(pa.getServizio()!=null && 
							pa.getServizio().getTipo()!=null && !"".equals(pa.getServizio().getTipo()) &&
							pa.getServizio().getNome()!=null && !"".equals(pa.getServizio().getNome()) &&
							pa.getServizio().getVersione()!=null
							){
						if(pa.getSoggettoVirtuale()!=null && 
								pa.getSoggettoVirtuale().getTipo()!=null && !"".equals(pa.getSoggettoVirtuale().getTipo()) &&
								pa.getSoggettoVirtuale().getNome()!=null && !"".equals(pa.getSoggettoVirtuale().getNome()) ){
							
							IDServizio idServizio = IDServizioFactory.getInstance().getIDServizioFromValues(pa.getServizio().getTipo(), pa.getServizio().getNome(), 
									pa.getSoggettoVirtuale().getTipo(), pa.getSoggettoVirtuale().getNome(), 
									pa.getServizio().getVersione());
							this.readAccordoServizioParteSpecifica(archive, idServizio, cascadeConfig, false, ArchiveType.PORTA_APPLICATIVA); // per evitare loop
							
						}
						else {
							
							IDServizio idServizio = IDServizioFactory.getInstance().getIDServizioFromValues(pa.getServizio().getTipo(), pa.getServizio().getNome(), 
									idSoggettoErogatore, 
									pa.getServizio().getVersione());
							this.readAccordoServizioParteSpecifica(archive, idServizio, cascadeConfig, false, ArchiveType.PORTA_APPLICATIVA); // per evitare loop
							
						}
					}
					
					// ruoli
					if(pa!=null && pa.getRuoli()!=null && 
							pa.getRuoli().sizeRuoloList()>0){
						for (int i = 0; i < pa.getRuoli().sizeRuoloList(); i++) {
							Ruolo ruolo = pa.getRuoli().getRuolo(i);
							FiltroRicercaRuoli filtroRuolo = new FiltroRicercaRuoli();
							filtroRuolo.setNome(ruolo.getNome());
							try{
								List<IDRuolo> idsRuoli = this.archiveEngine.getAllIdRuoli(filtroRuolo);
								if(idsRuoli!=null && idsRuoli.size()>0){
									for (IDRuolo idRuolo : idsRuoli) {
										this.readRuolo(archive, idRuolo, cascadeConfig, false, ArchiveType.PORTA_DELEGATA); // per evitare loop
									}
								}
							}catch(DriverRegistroServiziNotFound notFound){}	
						}
					}

				}
			}catch(Exception e){
				throw new ProtocolException("(PortaApplicativa "+idPortaApplicativa+") "+e.getMessage(),e);
			}
		}
		
		
		// *** cascade in avanti ***
		
		if(cascadeAvanti){
		
			// serviziApplicativi
			if(pa.sizeServizioApplicativoList()>0){
				for (PortaApplicativaServizioApplicativo sa: pa.getServizioApplicativoList()) {
					IDServizioApplicativo idSA = new IDServizioApplicativo();
					idSA.setIdSoggettoProprietario(idSoggettoErogatore);
					idSA.setNome(sa.getNome());
					this.readServizioApplicativo(archive, idSA, cascadeConfig, ArchiveType.PORTA_APPLICATIVA);
				}
			}
			
		}
		
	}
}
