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

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.driver.DriverConfigurazioneNotFound;
import org.openspcoop2.core.config.driver.FiltroRicercaPorteApplicative;
import org.openspcoop2.core.config.driver.FiltroRicercaPorteDelegate;
import org.openspcoop2.core.config.driver.FiltroRicercaServiziApplicativi;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDAccordoCooperazione;
import org.openspcoop2.core.id.IDAccordoCooperazioneWithSoggetto;
import org.openspcoop2.core.id.IDPortaApplicativaByNome;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.IdSoggetto;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteComuneServizioCompostoServizioComponente;
import org.openspcoop2.core.registry.Fruitore;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziNotFound;
import org.openspcoop2.core.registry.driver.FiltroRicerca;
import org.openspcoop2.core.registry.driver.FiltroRicercaAccordi;
import org.openspcoop2.core.registry.driver.FiltroRicercaServizi;
import org.openspcoop2.core.registry.driver.FiltroRicercaSoggetti;
import org.openspcoop2.core.registry.driver.IDAccordoCooperazioneFactory;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.archive.Archive;
import org.openspcoop2.protocol.sdk.archive.ArchiveAccordoCooperazione;
import org.openspcoop2.protocol.sdk.archive.ArchiveAccordoServizioComposto;
import org.openspcoop2.protocol.sdk.archive.ArchiveAccordoServizioParteComune;
import org.openspcoop2.protocol.sdk.archive.ArchiveAccordoServizioParteSpecifica;
import org.openspcoop2.protocol.sdk.archive.ArchiveCascadeConfiguration;
import org.openspcoop2.protocol.sdk.archive.ArchiveFruitore;
import org.openspcoop2.protocol.sdk.archive.ArchiveIdCorrelazione;
import org.openspcoop2.protocol.sdk.archive.ArchiveMode;
import org.openspcoop2.protocol.sdk.archive.ArchivePdd;
import org.openspcoop2.protocol.sdk.archive.ArchivePortaApplicativa;
import org.openspcoop2.protocol.sdk.archive.ArchivePortaDelegata;
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
	@SuppressWarnings("unused")
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
		IProtocolFactory protocolFactory = this.protocolFactoryManager.getProtocolFactoryByName(protocol);
		IArchive archiveEngine = protocolFactory.createArchive();
		ArchiveRegistryReader archiveRegistryReader = new ArchiveRegistryReader(this.archiveEngine.getDriverRegistroServizi(),this.archiveEngine.getDriverConfigurazione());
		archiveEngine.exportArchive(archive, out, mode, archiveRegistryReader);
	}
	
	public byte[] export(String protocol,Archive archive,ArchiveMode mode) throws Exception{
		IProtocolFactory protocolFactory = this.protocolFactoryManager.getProtocolFactoryByName(protocol);
		IArchive archiveEngine = protocolFactory.createArchive();
		ArchiveRegistryReader archiveRegistryReader = new ArchiveRegistryReader(this.archiveEngine.getDriverRegistroServizi(),this.archiveEngine.getDriverConfigurazione());
		return archiveEngine.exportArchive(archive, mode, archiveRegistryReader);
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
				this.readPortaApplicativa(archive, (IDPortaApplicativaByNome)object, cascadeConfig, exportSourceArchiveType);
			}
			break;
		case ACCORDO_COOPERAZIONE:
			for (Object object : listObject) {
				this.readAccordoCooperazione(archive, (IDAccordoCooperazioneWithSoggetto)object, cascadeConfig, exportSourceArchiveType);
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
				this.readAccordoServizioParteSpecifica(archive, (IDAccordo)object, cascadeConfig, exportSourceArchiveType);
			}
			break;
		case CONFIGURAZIONE:
			archive.setConfigurazionePdD(this.archiveEngine.getConfigurazione());
			break;
		case ALL:
		case ALL_WITHOUT_CONFIGURAZIONE:
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
			if(exportSourceArchiveType.equals(ArchiveType.ALL)){
				archive.setConfigurazionePdD(this.archiveEngine.getConfigurazione());
			}
			break;
		default:
			break;
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
						IDAccordoCooperazioneWithSoggetto idAccordoCooperazioneWithSoggetto = new IDAccordoCooperazioneWithSoggetto(idAccordoCooperazione);
						idAccordoCooperazioneWithSoggetto.setSoggettoReferente(idSoggetto);
						this.readAccordoCooperazione(archive, idAccordoCooperazioneWithSoggetto, cascadeConfig, ArchiveType.SOGGETTO);
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
				List<IDAccordo> idsAccordi = this.archiveEngine.getAllIdAccordiServizioParteSpecifica(filtroAccordiServizioParteSpecifica);
				if(idsAccordi!=null && idsAccordi.size()>0){
					for (IDAccordo idAccordo : idsAccordi) {
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
				List<IDPortaApplicativaByNome> idsPA = this.archiveEngine.getAllIdPorteApplicative(filtroPorteApplicative);
				if(idsPA!=null && idsPA.size()>0){
					for (IDPortaApplicativaByNome idPortaApplicativa : idsPA) {
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
	
	private void readAccordoCooperazione(Archive archive, IDAccordoCooperazioneWithSoggetto idAccordoCooperazione, 
			ArchiveCascadeConfiguration cascadeConfig, ArchiveType provenienza) throws Exception{
		this.readAccordoCooperazione(archive, idAccordoCooperazione, cascadeConfig, true, provenienza);
	}
	private void readAccordoCooperazione(Archive archive, IDAccordoCooperazioneWithSoggetto idAccordoCooperazione, 
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
			filtroRicercaAccordi.setIdAccordo(idAccordoServizio);
			try{
				List<IDAccordo> idsAS = this.archiveEngine.getAllIdAccordiServizioParteSpecifica(filtroRicercaAccordi);
				if(idsAS!=null && idsAS.size()>0){
					for (IDAccordo idAccordo : idsAS) {
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
			filtroRicercaAccordi.setIdAccordo(idAccordoServizio);
			try{
				List<IDAccordo> idsAS = this.archiveEngine.getAllIdAccordiServizioParteSpecifica(filtroRicercaAccordi);
				if(idsAS!=null && idsAS.size()>0){
					for (IDAccordo idAccordo : idsAS) {
						this.readAccordoServizioParteSpecifica(archive, idAccordo, cascadeConfig, ArchiveType.ACCORDO_SERVIZIO_COMPOSTO);
					}
				}
			}catch(DriverRegistroServiziNotFound notFound){}

			// servizi componenti
			for (AccordoServizioParteComuneServizioCompostoServizioComponente servizioComponente : as.getServizioComposto().getServizioComponenteList()) {
				IDServizio idServizio = new IDServizio(servizioComponente.getTipoSoggetto(),servizioComponente.getNomeSoggetto(),
						servizioComponente.getTipo(),servizioComponente.getNome());
				this.readAccordoServizioParteSpecifica(archive, idServizio, cascadeConfig, ArchiveType.ACCORDO_SERVIZIO_COMPOSTO); 
			}
			
		}
		

	}

	private void readAccordoServizioParteSpecifica(Archive archive, IDAccordo idAccordoServizio, 
			ArchiveCascadeConfiguration cascadeConfig, ArchiveType provenienza) throws Exception{
		this.readAccordoServizioParteSpecifica(archive, idAccordoServizio, cascadeConfig, true, provenienza);
	}
	private void readAccordoServizioParteSpecifica(Archive archive, IDAccordo idAccordoServizio, 
			ArchiveCascadeConfiguration cascadeConfig, boolean cascadeAvanti, ArchiveType provenienza) throws Exception{
		
		String tipoSoggetto = null;
		String nomeSoggetto = null;
		if(idAccordoServizio.getSoggettoReferente()!=null){
			tipoSoggetto = idAccordoServizio.getSoggettoReferente().getTipo();
			nomeSoggetto = idAccordoServizio.getSoggettoReferente().getNome();
		}
		String key =  ArchiveAccordoServizioParteSpecifica.buildKey(tipoSoggetto,nomeSoggetto,
				idAccordoServizio.getNome(),idAccordoServizio.getVersione());
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
					if(as.getServizio().getTipoSoggettoErogatore()==null){
						as.getServizio().setTipoSoggettoErogatore(tipoSoggetto);
					}
					if(as.getServizio().getNomeSoggettoErogatore()==null){
						as.getServizio().setNomeSoggettoErogatore(nomeSoggetto);
					}
					ArchiveAccordoServizioParteSpecifica archiveAs = new ArchiveAccordoServizioParteSpecifica(as, this.idCorrelazione);
					while(as.sizeFruitoreList()>0){
						fruitoriList.add(as.removeFruitore(0));
					}
					archive.getAccordiServizioParteSpecifica().add(archiveAs);
					
					// *** dipendenze: oggetti necessari per la creazione dell'oggetto sopra aggiunto ***
					
					// soggetto proprietario
					this.readSoggetto(archive, idAccordoServizio.getSoggettoReferente(), cascadeConfig, false, ArchiveType.ACCORDO_SERVIZIO_PARTE_SPECIFICA); // per evitare loop
				
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
				throw new ProtocolException("(AccordoServizioParteSpecifica "+IDAccordoFactory.getInstance().getUriFromIDAccordo(idAccordoServizio)+") "+e.getMessage(),e);
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
			filtroRicercaPorteDelegate.setTipoSoggettoErogatore(idAccordoServizio.getSoggettoReferente().getTipo());
			filtroRicercaPorteDelegate.setNomeSoggettoErogatore(idAccordoServizio.getSoggettoReferente().getNome());
			filtroRicercaPorteDelegate.setTipoServizio(as.getServizio().getTipo());
			filtroRicercaPorteDelegate.setNomeServizio(as.getServizio().getNome());
			try{
				List<IDPortaDelegata> idsPD = this.archiveEngine.getAllIdPorteDelegate(filtroRicercaPorteDelegate);
				if(idsPD!=null && idsPD.size()>0){
					for (IDPortaDelegata idPortaDelegata : idsPD) {
						this.readPortaDelegata(archive, idPortaDelegata, cascadeConfig, ArchiveType.ACCORDO_SERVIZIO_PARTE_SPECIFICA);
					}
				}
			}catch(DriverConfigurazioneNotFound notFound){}
			
			// porteApplicative "normali"
			FiltroRicercaPorteApplicative filtroRicercaPorteApplicative = new FiltroRicercaPorteApplicative();
			filtroRicercaPorteApplicative.setTipoSoggetto(idAccordoServizio.getSoggettoReferente().getTipo());
			filtroRicercaPorteApplicative.setNomeSoggetto(idAccordoServizio.getSoggettoReferente().getNome());
			filtroRicercaPorteApplicative.setTipoServizio(as.getServizio().getTipo());
			filtroRicercaPorteApplicative.setNomeServizio(as.getServizio().getNome());
			try{
				List<IDPortaApplicativaByNome> idsPA = this.archiveEngine.getAllIdPorteApplicative(filtroRicercaPorteApplicative);
				if(idsPA!=null && idsPA.size()>0){
					for (IDPortaApplicativaByNome idPortaApplicativa : idsPA) {
						this.readPortaApplicativa(archive, idPortaApplicativa, cascadeConfig, ArchiveType.ACCORDO_SERVIZIO_PARTE_SPECIFICA);
					}
				}
			}catch(DriverConfigurazioneNotFound notFound){}
				
			// porteApplicative "virtuali"
			filtroRicercaPorteApplicative = new FiltroRicercaPorteApplicative();
			filtroRicercaPorteApplicative.setTipoSoggettoVirtuale(idAccordoServizio.getSoggettoReferente().getTipo());
			filtroRicercaPorteApplicative.setNomeSoggettoVirtuale(idAccordoServizio.getSoggettoReferente().getNome());
			filtroRicercaPorteApplicative.setTipoServizio(as.getServizio().getTipo());
			filtroRicercaPorteApplicative.setNomeServizio(as.getServizio().getNome());
			try{
				List<IDPortaApplicativaByNome> idsPA = this.archiveEngine.getAllIdPorteApplicative(filtroRicercaPorteApplicative);
				if(idsPA!=null && idsPA.size()>0){
					for (IDPortaApplicativaByNome idPortaApplicativa : idsPA) {
						this.readPortaApplicativa(archive, idPortaApplicativa, cascadeConfig, ArchiveType.ACCORDO_SERVIZIO_PARTE_SPECIFICA);
					}
				}
			}catch(DriverConfigurazioneNotFound notFound){}
			
		}
		
	}
	
	private void readAccordoServizioParteSpecifica(Archive archive, IDServizio idServizio, 
			ArchiveCascadeConfiguration cascadeConfig, ArchiveType provenienza) throws Exception{
		this.readAccordoServizioParteSpecifica(archive, idServizio, cascadeConfig, true, provenienza);
	}
	private void readAccordoServizioParteSpecifica(Archive archive, IDServizio idServizio, 
			ArchiveCascadeConfiguration cascadeConfig, boolean cascadeAvanti, ArchiveType provenienza) throws Exception{
		
		org.openspcoop2.core.registry.AccordoServizioParteSpecifica as = this.archiveEngine.getAccordoServizioParteSpecifica(idServizio,false);
		IDAccordo idAccordo = IDAccordoFactory.getInstance().getIDAccordoFromAccordo(as);		
		this.readAccordoServizioParteSpecifica(archive, idAccordo, cascadeConfig, cascadeAvanti, provenienza);
		
	}
	
	private void readFruitore(Archive archive, IDAccordo idAccordoServizio, IDSoggetto idFruitore, Fruitore fruitore, 
			ArchiveCascadeConfiguration cascadeConfig, ArchiveType provenienza) throws Exception{
		this.readFruitore(archive, idAccordoServizio, idFruitore, fruitore, cascadeConfig, true, provenienza);
	}
	private void readFruitore(Archive archive, IDAccordo idAccordoServizio, IDSoggetto idFruitore, Fruitore fruitore, 
			ArchiveCascadeConfiguration cascadeConfig, boolean cascadeAvanti, ArchiveType provenienza) throws Exception{
		
		String tipoSoggetto = null;
		String nomeSoggetto = null;
		if(idAccordoServizio.getSoggettoReferente()!=null){
			tipoSoggetto = idAccordoServizio.getSoggettoReferente().getTipo();
			nomeSoggetto = idAccordoServizio.getSoggettoReferente().getNome();
		}
		String key =  ArchiveFruitore.buildKey(idFruitore.getTipo(),idFruitore.getNome(),
				tipoSoggetto,nomeSoggetto,
				idAccordoServizio.getNome(),idAccordoServizio.getVersione());
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
					
					// serviziApplicativi autorizzati
					try{
						List<IDServizioApplicativo> list = this.archiveEngine.getAllIdServiziApplicativiAutorizzati(idAccordoServizio, idFruitore);
						if(list!=null && list.size()>0){
							for (IDServizioApplicativo idServizioApplicativo : list) {
								archiveFruitore.getServiziApplicativiAutorizzati().add(idServizioApplicativo.getNome());
							}
						}
					}catch(DriverRegistroServiziNotFound notFound){}
					
					// *** dipendenze: oggetti necessari per la creazione dell'oggetto sopra aggiunto ***
					
					// soggetto
					this.readSoggetto(archive, idFruitore, cascadeConfig, false, ArchiveType.FRUITORE); // per evitare loop
					
					// accordoServizioParteSpecifica
					this.readAccordoServizioParteSpecifica(archive, idAccordoServizio, cascadeConfig, false, ArchiveType.FRUITORE); // per evitare loop
					
				}
					
			}catch(Exception e){
				throw new ProtocolException("(Fruitore "+idFruitore+" dell'accordo "+IDAccordoFactory.getInstance().getUriFromIDAccordo(idAccordoServizio)+") "+e.getMessage(),e);
			}
		}
		
		
		// *** cascade in avanti ***
		
		if(cascadeAvanti){
		
			// servizi applicativi autorizzati
			try{
				List<IDServizioApplicativo> idSA = this.archiveEngine.getAllIdServiziApplicativiAutorizzati(idAccordoServizio, idFruitore);
				if(idSA!=null && idSA.size()>0){
					for (IDServizioApplicativo idServizioApplicativo : idSA) {
						this.readServizioApplicativo(archive, idServizioApplicativo, cascadeConfig, ArchiveType.FRUITORE);
					}
				}
			}catch(DriverRegistroServiziNotFound notFound){}
			
		}
		
	}
	
	private void readPortaDelegata(Archive archive, IDPortaDelegata idPortaDelegata, 
			ArchiveCascadeConfiguration cascadeConfig, ArchiveType provenienza) throws Exception{
		this.readPortaDelegata(archive, idPortaDelegata, cascadeConfig, true, provenienza);
	}
	private void readPortaDelegata(Archive archive, IDPortaDelegata idPortaDelegata, 
			ArchiveCascadeConfiguration cascadeConfig, boolean cascadeAvanti, ArchiveType provenienza) throws Exception{
	
		String key =  ArchivePortaDelegata.buildKey(idPortaDelegata.getSoggettoFruitore().getTipo(),
				idPortaDelegata.getSoggettoFruitore().getNome(), 
				idPortaDelegata.getLocationPD());
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
						pd.setTipoSoggettoProprietario(idPortaDelegata.getSoggettoFruitore().getTipo());
					}
					if(pd.getNomeSoggettoProprietario()==null){
						pd.setNomeSoggettoProprietario(idPortaDelegata.getSoggettoFruitore().getNome());
					}
					ArchivePortaDelegata archivePd = new ArchivePortaDelegata(pd, this.idCorrelazione);
					archive.getPorteDelegate().add(archivePd);
					
					// *** dipendenze: oggetti necessari per la creazione dell'oggetto sopra aggiunto ***
					
					// soggetto proprietario
					this.readSoggetto(archive, idPortaDelegata.getSoggettoFruitore(), cascadeConfig, false, ArchiveType.PORTA_DELEGATA); // per evitare loop
					
					// eventuale servizio riferito
					if(pd.getSoggettoErogatore()!=null && 
							pd.getSoggettoErogatore().getTipo()!=null && !"".equals(pd.getSoggettoErogatore().getTipo()) &&
							pd.getSoggettoErogatore().getNome()!=null && !"".equals(pd.getSoggettoErogatore().getNome()) &&
							pd.getServizio()!=null && 
							pd.getServizio().getTipo()!=null && !"".equals(pd.getServizio().getTipo()) &&
							pd.getServizio().getNome()!=null && !"".equals(pd.getServizio().getNome())
							){
						
						IDServizio idServizio = new IDServizio(pd.getSoggettoErogatore().getTipo(), pd.getSoggettoErogatore().getNome(),
								pd.getServizio().getTipo(), pd.getServizio().getNome());
						this.readAccordoServizioParteSpecifica(archive, idServizio, cascadeConfig, false, ArchiveType.PORTA_DELEGATA); // per evitare loop
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
				for (ServizioApplicativo sa: pd.getServizioApplicativoList()) {
					IDServizioApplicativo idSA = new IDServizioApplicativo();
					idSA.setIdSoggettoProprietario(idPortaDelegata.getSoggettoFruitore());
					idSA.setNome(sa.getNome());
					this.readServizioApplicativo(archive, idSA, cascadeConfig, ArchiveType.PORTA_DELEGATA);
				}
			}
			
		}
		
	}
	
	
	private void readPortaApplicativa(Archive archive, IDPortaApplicativaByNome idPortaApplicativa, 
			ArchiveCascadeConfiguration cascadeConfig, ArchiveType provenienza) throws Exception{
		this.readPortaApplicativa(archive, idPortaApplicativa, cascadeConfig, true, provenienza);
	}
	private void readPortaApplicativa(Archive archive, IDPortaApplicativaByNome idPortaApplicativa, 
			ArchiveCascadeConfiguration cascadeConfig, boolean cascadeAvanti, ArchiveType provenienza) throws Exception{
	
		String key =  ArchivePortaApplicativa.buildKey(idPortaApplicativa.getSoggetto().getTipo(),
				idPortaApplicativa.getSoggetto().getNome(), 
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
						pa.setTipoSoggettoProprietario(idPortaApplicativa.getSoggetto().getTipo());
					}
					if(pa.getNomeSoggettoProprietario()==null){
						pa.setNomeSoggettoProprietario(idPortaApplicativa.getSoggetto().getNome());
					}
					ArchivePortaApplicativa archivePa = new ArchivePortaApplicativa(pa, this.idCorrelazione);
					archive.getPorteApplicative().add(archivePa);
				
					// *** dipendenze: oggetti necessari per la creazione dell'oggetto sopra aggiunto ***
					
					// proprietario
					this.readSoggetto(archive, idPortaApplicativa.getSoggetto(), cascadeConfig, false, ArchiveType.PORTA_APPLICATIVA); // per evitare loop
						
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
							pa.getServizio().getNome()!=null && !"".equals(pa.getServizio().getNome())
							){
						if(pa.getSoggettoVirtuale()!=null && 
								pa.getSoggettoVirtuale().getTipo()!=null && !"".equals(pa.getSoggettoVirtuale().getTipo()) &&
								pa.getSoggettoVirtuale().getNome()!=null && !"".equals(pa.getSoggettoVirtuale().getNome()) ){
							
							IDServizio idServizio = new IDServizio(pa.getSoggettoVirtuale().getTipo(), pa.getSoggettoVirtuale().getNome(),
									pa.getServizio().getTipo(), pa.getServizio().getNome());
							this.readAccordoServizioParteSpecifica(archive, idServizio, cascadeConfig, false, ArchiveType.PORTA_APPLICATIVA); // per evitare loop
							
						}
						else {
							
							IDServizio idServizio = new IDServizio(idPortaApplicativa.getSoggetto(),
									pa.getServizio().getTipo(), pa.getServizio().getNome());
							this.readAccordoServizioParteSpecifica(archive, idServizio, cascadeConfig, false, ArchiveType.PORTA_APPLICATIVA); // per evitare loop
							
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
				for (ServizioApplicativo sa: pa.getServizioApplicativoList()) {
					IDServizioApplicativo idSA = new IDServizioApplicativo();
					idSA.setIdSoggettoProprietario(idPortaApplicativa.getSoggetto());
					idSA.setNome(sa.getNome());
					this.readServizioApplicativo(archive, idSA, cascadeConfig, ArchiveType.PORTA_APPLICATIVA);
				}
			}
			
		}
		
	}
}
