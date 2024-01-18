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

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.allarmi.Allarme;
import org.openspcoop2.core.allarmi.IdAllarme;
import org.openspcoop2.core.config.AttributeAuthority;
import org.openspcoop2.core.config.Configurazione;
import org.openspcoop2.core.config.ConfigurazioneHandler;
import org.openspcoop2.core.config.ConfigurazioneUrlInvocazione;
import org.openspcoop2.core.config.ConfigurazioneUrlInvocazioneRegola;
import org.openspcoop2.core.config.GenericProperties;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaApplicativaAutorizzazioneServizioApplicativo;
import org.openspcoop2.core.config.PortaApplicativaAutorizzazioneSoggetto;
import org.openspcoop2.core.config.PortaApplicativaServizioApplicativo;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.PortaDelegataServizioApplicativo;
import org.openspcoop2.core.config.Property;
import org.openspcoop2.core.config.Ruolo;
import org.openspcoop2.core.config.Scope;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.TrasformazioneRegola;
import org.openspcoop2.core.config.TrasformazioneRegolaApplicabilitaServizioApplicativo;
import org.openspcoop2.core.config.TrasformazioneRegolaApplicabilitaSoggetto;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.config.constants.TipoAutenticazione;
import org.openspcoop2.core.config.constants.TipoAutorizzazione;
import org.openspcoop2.core.config.constants.TipoBehaviour;
import org.openspcoop2.core.config.driver.DriverConfigurazioneNotFound;
import org.openspcoop2.core.config.driver.FiltroRicercaPorteApplicative;
import org.openspcoop2.core.config.driver.FiltroRicercaPorteDelegate;
import org.openspcoop2.core.config.driver.FiltroRicercaServiziApplicativi;
import org.openspcoop2.core.constants.CostantiConnettori;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.constants.TipiConnettore;
import org.openspcoop2.core.controllo_traffico.AttivazionePolicy;
import org.openspcoop2.core.controllo_traffico.ConfigurazionePolicy;
import org.openspcoop2.core.controllo_traffico.IdActivePolicy;
import org.openspcoop2.core.controllo_traffico.IdPolicy;
import org.openspcoop2.core.controllo_traffico.constants.TipoFiltroApplicativo;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDAccordoCooperazione;
import org.openspcoop2.core.id.IDFruizione;
import org.openspcoop2.core.id.IDGenericProperties;
import org.openspcoop2.core.id.IDGruppo;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDRuolo;
import org.openspcoop2.core.id.IDScope;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.plugins.IdPlugin;
import org.openspcoop2.core.plugins.constants.TipoPlugin;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteComuneServizioCompostoServizioComponente;
import org.openspcoop2.core.registry.ConfigurazioneServizioAzione;
import org.openspcoop2.core.registry.Fruitore;
import org.openspcoop2.core.registry.GruppoAccordo;
import org.openspcoop2.core.registry.IdSoggetto;
import org.openspcoop2.core.registry.RuoloSoggetto;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziNotFound;
import org.openspcoop2.core.registry.driver.FiltroRicerca;
import org.openspcoop2.core.registry.driver.FiltroRicercaAccordi;
import org.openspcoop2.core.registry.driver.FiltroRicercaGruppi;
import org.openspcoop2.core.registry.driver.FiltroRicercaRuoli;
import org.openspcoop2.core.registry.driver.FiltroRicercaScope;
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
import org.openspcoop2.protocol.sdk.archive.ArchiveAllarme;
import org.openspcoop2.protocol.sdk.archive.ArchiveAttributeAuthority;
import org.openspcoop2.protocol.sdk.archive.ArchiveCascadeConfiguration;
import org.openspcoop2.protocol.sdk.archive.ArchiveConfigurationPolicy;
import org.openspcoop2.protocol.sdk.archive.ArchiveFruitore;
import org.openspcoop2.protocol.sdk.archive.ArchiveGruppo;
import org.openspcoop2.protocol.sdk.archive.ArchiveIdCorrelazione;
import org.openspcoop2.protocol.sdk.archive.ArchiveMode;
import org.openspcoop2.protocol.sdk.archive.ArchivePdd;
import org.openspcoop2.protocol.sdk.archive.ArchivePluginArchivio;
import org.openspcoop2.protocol.sdk.archive.ArchivePluginClasse;
import org.openspcoop2.protocol.sdk.archive.ArchivePortaApplicativa;
import org.openspcoop2.protocol.sdk.archive.ArchivePortaDelegata;
import org.openspcoop2.protocol.sdk.archive.ArchiveRuolo;
import org.openspcoop2.protocol.sdk.archive.ArchiveScope;
import org.openspcoop2.protocol.sdk.archive.ArchiveServizioApplicativo;
import org.openspcoop2.protocol.sdk.archive.ArchiveSoggetto;
import org.openspcoop2.protocol.sdk.archive.ArchiveTokenPolicy;
import org.openspcoop2.protocol.sdk.archive.ArchiveUrlInvocazioneRegola;
import org.openspcoop2.protocol.sdk.archive.IArchive;
import org.openspcoop2.protocol.sdk.constants.ArchiveType;
import org.slf4j.Logger;


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
		case GRUPPO:
			for (Object object : listObject) {
				this.readGruppo(archive, (IDGruppo)object, cascadeConfig, exportSourceArchiveType);
			}
			break;
		case RUOLO:
			for (Object object : listObject) {
				this.readRuolo(archive, (IDRuolo)object, cascadeConfig, exportSourceArchiveType);
			}
			break;
		case SCOPE:
			for (Object object : listObject) {
				this.readScope(archive, (IDScope)object, cascadeConfig, exportSourceArchiveType);
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
		case EROGAZIONE:
			for (Object object : listObject) {
				this.readErogazione(archive, (IDServizio)object, cascadeConfig);
			}
			break;
		case FRUIZIONE:
			for (Object object : listObject) {
				this.readFruizione(archive, (IDFruizione)object, cascadeConfig);
			}
			break;
		case CONFIGURAZIONE_CONTROLLO_TRAFFICO_CONFIG_POLICY:
			for (Object object : listObject) {
				this.readControlloTraffico_configurationPolicy(archive, (IdPolicy) object, cascadeConfig, exportSourceArchiveType);
			}
			break;
		case CONFIGURAZIONE_CONTROLLO_TRAFFICO_ACTIVE_POLICY:
			for (Object object : listObject) {
				this.readControlloTraffico_activePolicy(archive, (IdActivePolicy) object, cascadeConfig, exportSourceArchiveType);
			}
			break;
		case ALLARME:
			for (Object object : listObject) {
				this.readAllarme(archive, (IdAllarme) object, cascadeConfig, exportSourceArchiveType);
			}
			break;
		case CONFIGURAZIONE_TOKEN_POLICY:
			for (Object object : listObject) {
				this.readTokenPolicy(archive, (IDGenericProperties) object, cascadeConfig, exportSourceArchiveType);
			}
			break;
		case CONFIGURAZIONE_ATTRIBUTE_AUTHORITY:
			for (Object object : listObject) {
				this.readAttributeAuthority(archive, (IDGenericProperties) object, cascadeConfig, exportSourceArchiveType);
			}
			break;
		case CONFIGURAZIONE_PLUGIN_CLASSE:
			for (Object object : listObject) {
				this.readPlugin_classe(archive, (IdPlugin) object, cascadeConfig, exportSourceArchiveType);
			}
			break;
		case CONFIGURAZIONE_PLUGIN_ARCHVIO:
			for (Object object : listObject) {
				this.readPlugin_archivio(archive, (String) object, cascadeConfig, exportSourceArchiveType);
			}
			break;
		case CONFIGURAZIONE_URL_INVOCAZIONE_REGOLA:
			for (Object object : listObject) {
				this.readUrlInvocazioneRegola(archive, (String) object, cascadeConfig);
			}
			break;
		case CONFIGURAZIONE_URL_INVOCAZIONE:
			setConfigurazioneUrlInvocazione(archive);
			break;
		case CONFIGURAZIONE:
			readControlloTrafficoConfigurazione(archive, true, false, exportSourceArchiveType);
			readAllarmiConfigurazione(archive, true, false, exportSourceArchiveType);
			readTokenPolicyConfigurazione(archive, exportSourceArchiveType);
			readAttributeAuthorityConfigurazione(archive, exportSourceArchiveType);
			readPluginConfigurazione(archive, exportSourceArchiveType);
			
			setConfigurazione(archive);
			
			// aggiungo gruppi 'zombie'
			FiltroRicercaGruppi filtroRicercaGruppi = new FiltroRicercaGruppi();
			List<IDGruppo> idGruppi = null;
			try{
				idGruppi = this.archiveEngine.getAllIdGruppi(filtroRicercaGruppi);
				for (IDGruppo idGruppo : idGruppi) {
					boolean found = false;
					if(archive.getGruppi()!=null && archive.getGruppi().size()>0){
						for (String idArchiveGruppo : archive.getGruppi().keys()) {
							ArchiveGruppo archiveGruppo = archive.getGruppi().get(idArchiveGruppo);
							if(archiveGruppo.getIdGruppo().equals(idGruppo)){
								found = true;
								break;
							}
						}
					}
					if(!found){
						this.readGruppo(archive, idGruppo, cascadeConfig, false, ArchiveType.GRUPPO);
					}
				}
			}catch(DriverRegistroServiziNotFound notFound){}
			
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
			//boolean loadActivePolicyFruizioneErogazione = true;
			boolean loadActivePolicyFruizioneErogazione = false; // vengono gia' caricate da sopra con il cascade sui soggetti, che arriva sulle PD e PA
			boolean loadAllarmiErogazioneFruizione = false; // vale lo stesso motivo delle policy, vengono già caricate sopra con il cascade dei soggetti
			if(exportSourceArchiveType.equals(ArchiveType.ALL)){
				readControlloTrafficoConfigurazione(archive, true, loadActivePolicyFruizioneErogazione, exportSourceArchiveType);
				readAllarmiConfigurazione(archive, true, loadAllarmiErogazioneFruizione, exportSourceArchiveType);
				readTokenPolicyConfigurazione(archive, exportSourceArchiveType);
				readAttributeAuthorityConfigurazione(archive, exportSourceArchiveType);
				readPluginConfigurazione(archive, exportSourceArchiveType);
				setConfigurazione(archive);
			}
			else if(exportSourceArchiveType.equals(ArchiveType.ALL_WITHOUT_CONFIGURAZIONE)){
				readControlloTrafficoConfigurazione(archive, false, loadActivePolicyFruizioneErogazione, exportSourceArchiveType);
				readAllarmiConfigurazione(archive, false, loadAllarmiErogazioneFruizione, exportSourceArchiveType);
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
			
			// aggiungo gruppi 'zombie'
			if(exportSourceArchiveType.equals(ArchiveType.ALL)){
				filtroRicercaGruppi = new FiltroRicercaGruppi();
				idGruppi = null;
				try{
					idGruppi = this.archiveEngine.getAllIdGruppi(filtroRicercaGruppi);
					for (IDGruppo idGruppo : idGruppi) {
						boolean found = false;
						if(archive.getGruppi()!=null && archive.getGruppi().size()>0){
							for (String idArchiveGruppo : archive.getGruppi().keys()) {
								ArchiveGruppo archiveGruppo = archive.getGruppi().get(idArchiveGruppo);
								if(archiveGruppo.getIdGruppo().equals(idGruppo)){
									found = true;
									break;
								}
							}
						}
						if(!found){
							this.readGruppo(archive, idGruppo, cascadeConfig, false, ArchiveType.GRUPPO);
						}
					}
				}catch(DriverRegistroServiziNotFound notFound){}
			}
			
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
			
			// aggiungo scope 'zombie'
			FiltroRicercaScope filtroRicercaScope = new FiltroRicercaScope();
			List<IDScope> idScopes = null;
			try{
				idScopes = this.archiveEngine.getAllIdScope(filtroRicercaScope);
				for (IDScope idScope : idScopes) {
					boolean found = false;
					if(archive.getScope()!=null && archive.getScope().size()>0){
						for (String idArchiveScope : archive.getScope().keys()) {
							ArchiveScope archiveScope = archive.getScope().get(idArchiveScope);
							if(archiveScope.getIdScope().equals(idScope)){
								found = true;
								break;
							}
						}
					}
					if(!found){
						this.readScope(archive, idScope, cascadeConfig, false, ArchiveType.SCOPE);
					}
				}
			}catch(DriverRegistroServiziNotFound notFound){}
				
			break;
		default:
			break;
		}
		
	}
	
	private void setConfigurazione(Archive archive) throws Exception {
		Configurazione configurazione = this.archiveEngine.getConfigurazione();
		_setConfigurazioneUrlInvocazione(archive, configurazione);	
		archive.setConfigurazionePdD(configurazione);
	}
	
	private void setConfigurazioneUrlInvocazione(Archive archive) throws Exception {
		Configurazione configurazione = this.archiveEngine.getConfigurazione();
		_setConfigurazioneUrlInvocazione(archive, configurazione);	
	}
	private void _setConfigurazioneUrlInvocazione(Archive archive, Configurazione configurazione) throws Exception {
		if(configurazione!=null && configurazione.getUrlInvocazione()!=null) {
			ConfigurazioneUrlInvocazione urlInvocazione = configurazione.getUrlInvocazione();
			while(urlInvocazione.sizeRegolaList()>0) {
				ConfigurazioneUrlInvocazioneRegola regola = urlInvocazione.removeRegola(0);
				
				String key = ArchiveUrlInvocazioneRegola.buildKey(regola.getNome());
				if(!archive.getConfigurazionePdD_urlInvocazione_regole().containsKey(key)){
					archive.getConfigurazionePdD_urlInvocazione_regole().add(new ArchiveUrlInvocazioneRegola(regola, this.idCorrelazione));
				}
			}
			archive.setConfigurazionePdD_urlInvocazione(urlInvocazione);
		}
	}
	
	private void readUrlInvocazioneRegola(Archive archive, String nome, ArchiveCascadeConfiguration cascadeConfig) throws Exception {
		
		String key = ArchiveUrlInvocazioneRegola.buildKey(nome);
		if(archive.getConfigurazionePdD_urlInvocazione_regole().containsKey(key)){
			// gia gestito
		}
		else{
			archive.getConfigurazionePdD_urlInvocazione_regole().add(new ArchiveUrlInvocazioneRegola(this.archiveEngine.getUrlInvocazioneRegola(nome), this.idCorrelazione));
		}
		
	}
	
	private void readControlloTrafficoConfigurazione(Archive archive, boolean policyGlobali, boolean policyFruizioniErogazioni, ArchiveType provenienza) throws Exception {
		
		archive.setControlloTraffico_configurazione(this.archiveEngine.getControlloTraffico_Configurazione());
		
		List<IdPolicy> listControlloTraffico_configurationPolicies = null;
		try {
			listControlloTraffico_configurationPolicies = this.archiveEngine.getAllIdControlloTraffico_configurationPolicies();
		}catch(DriverConfigurazioneNotFound notFound) {}
		if(policyGlobali) {
			if(listControlloTraffico_configurationPolicies!=null && listControlloTraffico_configurationPolicies.size()>0) {
				for (IdPolicy idPolicy : listControlloTraffico_configurationPolicies) {
					
					String keyPolicyConfig = ArchiveConfigurationPolicy.buildKey(idPolicy.getNome());
					if(archive.getControlloTraffico_configurationPolicies().containsKey(keyPolicyConfig)==false) {
					
						archive.getControlloTraffico_configurationPolicies().add(new ArchiveConfigurationPolicy(
								this.archiveEngine.getControlloTraffico_configurationPolicy(idPolicy),
								this.idCorrelazione));
						
					}
				}
			}
		}
		else {
			// le aggiungo dopo: aggiungo le policy configurate solamente realmente utilizzate.
		}
		
		List<IdActivePolicy> listControlloTraffico_activePolicies = null;
		try {
			if(policyGlobali && policyFruizioniErogazioni) {
				listControlloTraffico_activePolicies = this.archiveEngine.getAllIdControlloTraffico_activePolicies_all(null);
			}
			else if(policyGlobali) {
				listControlloTraffico_activePolicies = this.archiveEngine.getAllIdControlloTraffico_activePolicies_globali(null);
			}
			else if(policyFruizioniErogazioni) {
				listControlloTraffico_activePolicies = this.archiveEngine.getAllIdControlloTraffico_activePolicies_erogazioniFruizioni(null);
			}
		}catch(DriverConfigurazioneNotFound notFound) {}
		if(listControlloTraffico_activePolicies!=null && listControlloTraffico_activePolicies.size()>0) {
			for (IdActivePolicy idPolicy : listControlloTraffico_activePolicies) {
				
				AttivazionePolicy policy = null;
				
				String keyActivePolicy = ArchiveActivePolicy.buildKey(idPolicy.getFiltroRuoloPorta(), idPolicy.getFiltroNomePorta(), idPolicy.getAlias());
				if(archive.getControlloTraffico_activePolicies().containsKey(keyActivePolicy)==false) {
					
					policy = this.archiveEngine.getControlloTraffico_activePolicy(idPolicy.getFiltroRuoloPorta(), idPolicy.getFiltroNomePorta(), idPolicy.getAlias());
					
					archive.getControlloTraffico_activePolicies().add(new ArchiveActivePolicy(
							policy,
							this.idCorrelazione));	
				}
								
				if(!policyGlobali) {
					// aggiungo le policy configurate solamente realmente utilizzate.
					
					String keyPolicyConfig = ArchiveConfigurationPolicy.buildKey(idPolicy.getIdPolicy());
					if(archive.getControlloTraffico_configurationPolicies().containsKey(keyPolicyConfig)==false) {
					
						if(listControlloTraffico_configurationPolicies!=null && listControlloTraffico_configurationPolicies.size()>0) {
							for (IdPolicy idConfigPolicy : listControlloTraffico_configurationPolicies) {
								if(idConfigPolicy.getNome().equals(idPolicy.getIdPolicy())) {
									archive.getControlloTraffico_configurationPolicies().add(new ArchiveConfigurationPolicy(
											this.archiveEngine.getControlloTraffico_configurationPolicy(idConfigPolicy),
											this.idCorrelazione));
									break;
								}
							}
						}
						
					}
					
					// aggiungo i plugin configurate solamente realmente utilizzate.
					if(policy!=null && policy.getFiltro()!=null && policy.getFiltro().isEnabled() && 
							policy.getFiltro().isInformazioneApplicativaEnabled() &&
							StringUtils.isNotEmpty(policy.getFiltro().getInformazioneApplicativaTipo())) {
						TipoFiltroApplicativo tipoFiltro = TipoFiltroApplicativo.toEnumConstant(policy.getFiltro().getInformazioneApplicativaTipo(), false);
						if(TipoFiltroApplicativo.PLUGIN_BASED.equals(tipoFiltro)) {
							try {
								ArchiveCascadeConfiguration cascadeConfig = new ArchiveCascadeConfiguration();
								cascadeConfig.setCascadePluginConfigurazione(true);
								readPlugin_classe(archive, TipoPlugin.RATE_LIMITING.getValue(), policy.getFiltro().getInformazioneApplicativaNome(), cascadeConfig, provenienza);
							}catch(DriverConfigurazioneNotFound notFound) {}
						}
					}
					if(policy!=null && policy.getGroupBy()!=null && policy.getGroupBy().isEnabled() && 
							policy.getGroupBy().isInformazioneApplicativaEnabled() &&
							StringUtils.isNotEmpty(policy.getGroupBy().getInformazioneApplicativaTipo())) {
						TipoFiltroApplicativo tipoFiltro = TipoFiltroApplicativo.toEnumConstant(policy.getGroupBy().getInformazioneApplicativaTipo(), false);
						if(TipoFiltroApplicativo.PLUGIN_BASED.equals(tipoFiltro)) {
							try {
								ArchiveCascadeConfiguration cascadeConfig = new ArchiveCascadeConfiguration();
								cascadeConfig.setCascadePluginConfigurazione(true);
								readPlugin_classe(archive, TipoPlugin.RATE_LIMITING.getValue(), policy.getGroupBy().getInformazioneApplicativaNome(), cascadeConfig, provenienza);
							}catch(DriverConfigurazioneNotFound notFound) {}
						}
					}
					
				}
			}
		}
		
	}
	
	private void readControlloTrafficoPolicyPorta(Archive archive, boolean delegata, String nomePorta,  ArchiveCascadeConfiguration cascadeConfig, ArchiveType provenienza) throws Exception {
		
		List<IdActivePolicy> listControlloTraffico_activePolicies = null;
		try {
			if(delegata) {
				listControlloTraffico_activePolicies = this.archiveEngine.getAllIdControlloTraffico_activePolicies_fruizione(nomePorta);
			}
			else {
				listControlloTraffico_activePolicies = this.archiveEngine.getAllIdControlloTraffico_activePolicies_erogazione(nomePorta);
			}
		}catch(DriverConfigurazioneNotFound notFound) {}
		if(listControlloTraffico_activePolicies!=null && listControlloTraffico_activePolicies.size()>0) {
			for (IdActivePolicy idPolicy : listControlloTraffico_activePolicies) {
				this.readControlloTraffico_activePolicy(archive, idPolicy, cascadeConfig, provenienza);				
			}
		}
	}
	
	private void readControlloTraffico_configurationPolicy(Archive archive, IdPolicy idPolicy, ArchiveCascadeConfiguration cascadeConfig, ArchiveType provenienza) throws Exception {
		
		ConfigurazionePolicy policy = null;
		
		String key = ArchiveConfigurationPolicy.buildKey(idPolicy.getNome());
		if(archive.getControlloTraffico_configurationPolicies().containsKey(key)){
			// gia gestito
		}
		else{
			policy = this.archiveEngine.getControlloTraffico_configurationPolicy(idPolicy.getNome());
			archive.getControlloTraffico_configurationPolicies().add(new ArchiveConfigurationPolicy(policy, this.idCorrelazione));
		}
		
	}
	
	
	private void readControlloTraffico_activePolicy(Archive archive, IdActivePolicy idPolicy, ArchiveCascadeConfiguration cascadeConfig, ArchiveType provenienza) throws Exception {
		
		AttivazionePolicy policy = null;
		
		String keyActivePolicy = ArchiveActivePolicy.buildKey(idPolicy.getFiltroRuoloPorta(), idPolicy.getFiltroNomePorta(), idPolicy.getAlias());
		if(archive.getControlloTraffico_activePolicies().containsKey(keyActivePolicy)==false) {
			policy = this.archiveEngine.getControlloTraffico_activePolicy(idPolicy.getFiltroRuoloPorta(), idPolicy.getFiltroNomePorta(), idPolicy.getAlias());
			archive.getControlloTraffico_activePolicies().add(new ArchiveActivePolicy(
					policy,
					this.idCorrelazione));
		}
		
		// aggiungo anche la policy configurata.
		if(cascadeConfig.isCascadePolicyConfigurazione()) {
			IdPolicy idPolicyConfig = new IdPolicy();
			idPolicyConfig.setNome(idPolicy.getIdPolicy());
			readControlloTraffico_configurationPolicy(archive, idPolicyConfig, cascadeConfig, provenienza);
		}
		
		// aggiungo plugin
		if(cascadeConfig.isCascadePluginConfigurazione()) {
			if(policy!=null && policy.getFiltro()!=null && policy.getFiltro().isEnabled() && 
					policy.getFiltro().isInformazioneApplicativaEnabled() &&
					StringUtils.isNotEmpty(policy.getFiltro().getInformazioneApplicativaTipo())) {
				TipoFiltroApplicativo tipoFiltro = TipoFiltroApplicativo.toEnumConstant(policy.getFiltro().getInformazioneApplicativaTipo(), false);
				if(TipoFiltroApplicativo.PLUGIN_BASED.equals(tipoFiltro)) {
					try {
						readPlugin_classe(archive, TipoPlugin.RATE_LIMITING.getValue(), policy.getFiltro().getInformazioneApplicativaNome(), cascadeConfig, provenienza);
					}catch(DriverConfigurazioneNotFound notFound) {}
				}
			}
			if(policy!=null && policy.getGroupBy()!=null && policy.getGroupBy().isEnabled() && 
					policy.getGroupBy().isInformazioneApplicativaEnabled() &&
					StringUtils.isNotEmpty(policy.getGroupBy().getInformazioneApplicativaTipo())) {
				TipoFiltroApplicativo tipoFiltro = TipoFiltroApplicativo.toEnumConstant(policy.getGroupBy().getInformazioneApplicativaTipo(), false);
				if(TipoFiltroApplicativo.PLUGIN_BASED.equals(tipoFiltro)) {
					try {
						readPlugin_classe(archive, TipoPlugin.RATE_LIMITING.getValue(), policy.getGroupBy().getInformazioneApplicativaNome(), cascadeConfig, provenienza);
					}catch(DriverConfigurazioneNotFound notFound) {}
				}
			}
		}
		
	}
	
	
	
	private void readAllarmiConfigurazione(Archive archive, boolean policyGlobali, boolean policyFruizioniErogazioni, ArchiveType provenienza) throws Exception {
		
		if(!CostantiDB.isAllarmiEnabled()) {
			return;
		}
		
		List<IdAllarme> listAllarmi = null;
		try {
			if(policyGlobali && policyFruizioniErogazioni) {
				listAllarmi = this.archiveEngine.getAllIdAllarmi_all(null);
			}
			else if(policyGlobali) {
				listAllarmi = this.archiveEngine.getAllIdAllarmi_globali(null);
			}
			else if(policyFruizioniErogazioni) {
				listAllarmi = this.archiveEngine.getAllIdAllarmi_erogazioniFruizioni(null);
			}
		}catch(DriverConfigurazioneNotFound notFound) {}
		if(listAllarmi!=null && !listAllarmi.isEmpty()) {
			for (IdAllarme idAllarme : listAllarmi) {
				String keyAllarme = ArchiveAllarme.buildKey(idAllarme.getFiltroRuoloPorta(), idAllarme.getFiltroNomePorta(), idAllarme.getAlias());
				if(archive.getAllarmi().containsKey(keyAllarme)==false) {
					archive.getAllarmi().add(new ArchiveAllarme(
							this.archiveEngine.getAllarme(idAllarme.getFiltroRuoloPorta(), idAllarme.getFiltroNomePorta(), idAllarme.getAlias()),
							this.idCorrelazione));	
				}
								
				if(!policyGlobali) {
					// aggiungo le policy configurate solamente realmente utilizzate.
					
					// aggiungo anche il plugin configurato.
					try {
						ArchiveCascadeConfiguration cascadeConfig = new ArchiveCascadeConfiguration();
						cascadeConfig.setCascadePluginConfigurazione(true);
						readPlugin_classe(archive, TipoPlugin.ALLARME.getValue(), idAllarme.getTipo(), cascadeConfig, provenienza);
					}catch(DriverConfigurazioneNotFound notFound) {}
					
				}
			}
		}
		
	}
	
	private void readAllarmiPorta(Archive archive, boolean delegata, String nomePorta,  ArchiveCascadeConfiguration cascadeConfig, ArchiveType provenienza) throws Exception {
		
		if(!CostantiDB.isAllarmiEnabled()) {
			return;
		}
		
		List<IdAllarme> listAllarmi = null;
		try {
			if(delegata) {
				listAllarmi = this.archiveEngine.getAllIdAllarmi_fruizione(nomePorta);
			}
			else {
				listAllarmi = this.archiveEngine.getAllIdAllarmi_erogazione(nomePorta);
			}
		}catch(DriverConfigurazioneNotFound notFound) {}
		if(listAllarmi!=null && listAllarmi.size()>0) {
			for (IdAllarme idAllarme : listAllarmi) {
				
				String keyAllarme = ArchiveAllarme.buildKey(idAllarme.getFiltroRuoloPorta(), idAllarme.getFiltroNomePorta(), idAllarme.getAlias());
				if(archive.getAllarmi().containsKey(keyAllarme)==false) {
					archive.getAllarmi().add(new ArchiveAllarme(
							this.archiveEngine.getAllarme(idAllarme.getFiltroRuoloPorta(), idAllarme.getFiltroNomePorta(), idAllarme.getAlias()),
							this.idCorrelazione));
				}
				
				// aggiungo anche il plugin configurato.
				if(cascadeConfig.isCascadePluginConfigurazione()) {
					try {
						readPlugin_classe(archive, TipoPlugin.ALLARME.getValue(), idAllarme.getTipo(), cascadeConfig, provenienza);
					}catch(DriverConfigurazioneNotFound notFound) {}
				}
			}
		}
	}
	
	private void readAllarme(Archive archive, IdAllarme idAllarme, ArchiveCascadeConfiguration cascadeConfig, ArchiveType provenienza) throws Exception {
		
		Allarme allarme = null;
		
		String key = ArchiveAllarme.buildKey(idAllarme.getFiltroRuoloPorta(),idAllarme.getFiltroNomePorta(),idAllarme.getAlias());
		if(archive.getAllarmi().containsKey(key)){
			// gia gestito
		}
		else{
			allarme = this.archiveEngine.getAllarme(idAllarme.getFiltroRuoloPorta(),idAllarme.getFiltroNomePorta(),idAllarme.getAlias());
			archive.getAllarmi().add(new ArchiveAllarme(allarme, this.idCorrelazione));
		}
		
		// aggiungo anche il plugin configurato.
		if(cascadeConfig.isCascadePluginConfigurazione()) {
			try {
				readPlugin_classe(archive, TipoPlugin.ALLARME.getValue(), idAllarme.getTipo(), cascadeConfig, provenienza);
			}catch(DriverConfigurazioneNotFound notFound) {}
		}
		
	}
	
	
	private void readTokenPolicyConfigurazione(Archive archive, ArchiveType provenienza) throws Exception {
		
		ArchiveCascadeConfiguration cascadeConfigPolicy = new ArchiveCascadeConfiguration();
		cascadeConfigPolicy.setCascadePolicyConfigurazione(true);
		
		List<IDGenericProperties> listPolicies_validation = null;
		try {
			listPolicies_validation = this.archiveEngine.getAllIdGenericProperties_validation();
		}catch(DriverConfigurazioneNotFound notFound) {}
		if(listPolicies_validation!=null && listPolicies_validation.size()>0) {
			for (IDGenericProperties idGP : listPolicies_validation) {
				readTokenPolicy_validation(archive,idGP.getNome(), cascadeConfigPolicy, provenienza);
			}
		}
		
		List<IDGenericProperties> listPolicies_retrieve = null;
		try {
			listPolicies_retrieve = this.archiveEngine.getAllIdGenericProperties_retrieve();
		}catch(DriverConfigurazioneNotFound notFound) {}
		if(listPolicies_retrieve!=null && listPolicies_retrieve.size()>0) {
			for (IDGenericProperties idGP : listPolicies_retrieve) {
				readTokenPolicy_retrieve(archive,idGP.getNome(), cascadeConfigPolicy, provenienza);
			}
		}
		
	}
	
	private void readTokenPolicy(Archive archive,IDGenericProperties idGP, ArchiveCascadeConfiguration cascadeConfig, ArchiveType provenienza) throws Exception {
		if(CostantiConfigurazione.GENERIC_PROPERTIES_TOKEN_TIPOLOGIA_VALIDATION.equals(idGP.getTipologia())) {
			readTokenPolicy_validation(archive, idGP.getNome(), cascadeConfig, provenienza);
		}
		else if(CostantiConfigurazione.GENERIC_PROPERTIES_TOKEN_TIPOLOGIA_RETRIEVE.equals(idGP.getTipologia())) {
			readTokenPolicy_retrieve(archive, idGP.getNome(), cascadeConfig, provenienza);
		}
	}
	
	private void readTokenPolicy_validation(Archive archive, String nomePolicy, ArchiveCascadeConfiguration cascadeConfig, ArchiveType provenienza) throws Exception {
		
		if(cascadeConfig.isCascadePolicyConfigurazione() || ArchiveType.CONFIGURAZIONE_TOKEN_POLICY.equals(provenienza)) {
		
			String key = ArchiveTokenPolicy.buildKey(CostantiConfigurazione.GENERIC_PROPERTIES_TOKEN_TIPOLOGIA_VALIDATION, nomePolicy);
			if(archive.getToken_validation_policies().containsKey(key)){
				// gia gestito
			}
			else{
				GenericProperties policy = this.archiveEngine.getGenericProperties_validation(nomePolicy);
				
				archive.getToken_validation_policies().add(new ArchiveTokenPolicy(policy, this.idCorrelazione));
				
				// aggiungo plugin
				if(policy!=null && policy.sizePropertyList()>0) {
					for (int i = 0; i < policy.sizePropertyList(); i++) {
						Property p = policy.getProperty(i);
						if(CostantiConfigurazione.POLICY_VALIDAZIONE_CLAIMS_PARSER_TYPE.equals(p.getNome()) && 
								CostantiConfigurazione.POLICY_VALIDAZIONE_CLAIMS_PARSER_TYPE_CUSTOM.equals(p.getValore())) {
							for (int j = 0; j < policy.sizePropertyList(); j++) {
								Property pJ = policy.getProperty(j);
								if(CostantiConfigurazione.POLICY_VALIDAZIONE_CLAIMS_PARSER_PLUGIN_TYPE.equals(pJ.getNome())) {
									try {
										ArchiveCascadeConfiguration cascadeConfigPlugin = new ArchiveCascadeConfiguration();
										cascadeConfigPlugin.setCascadePluginConfigurazione(true);
										readPlugin_classe(archive, TipoPlugin.TOKEN_VALIDAZIONE.getValue(), pJ.getValore(), cascadeConfigPlugin, provenienza);
									}catch(DriverConfigurazioneNotFound notFound) {}		
								}
							}
						}
						else if(CostantiConfigurazione.POLICY_INTROSPECTION_CLAIMS_PARSER_TYPE.equals(p.getNome()) && 
								CostantiConfigurazione.POLICY_INTROSPECTION_CLAIMS_PARSER_TYPE_CUSTOM.equals(p.getValore())) {
							for (int j = 0; j < policy.sizePropertyList(); j++) {
								Property pJ = policy.getProperty(j);
								if(CostantiConfigurazione.POLICY_INTROSPECTION_CLAIMS_PARSER_PLUGIN_TYPE.equals(pJ.getNome())) {
									try {
										ArchiveCascadeConfiguration cascadeConfigPlugin = new ArchiveCascadeConfiguration();
										cascadeConfigPlugin.setCascadePluginConfigurazione(true);
										readPlugin_classe(archive, TipoPlugin.TOKEN_VALIDAZIONE.getValue(), pJ.getValore(), cascadeConfigPlugin, provenienza);
									}catch(DriverConfigurazioneNotFound notFound) {}		
								}
							}
						}
						else if(CostantiConfigurazione.POLICY_USER_INFO_CLAIMS_PARSER_TYPE.equals(p.getNome()) && 
								CostantiConfigurazione.POLICY_USER_INFO_CLAIMS_PARSER_TYPE_CUSTOM.equals(p.getValore())) {
							for (int j = 0; j < policy.sizePropertyList(); j++) {
								Property pJ = policy.getProperty(j);
								if(CostantiConfigurazione.POLICY_USER_INFO_CLAIMS_PARSER_PLUGIN_TYPE.equals(pJ.getNome())) {
									try {
										ArchiveCascadeConfiguration cascadeConfigPlugin = new ArchiveCascadeConfiguration();
										cascadeConfigPlugin.setCascadePluginConfigurazione(true);
										readPlugin_classe(archive, TipoPlugin.TOKEN_VALIDAZIONE.getValue(), pJ.getValore(), cascadeConfigPlugin, provenienza);
									}catch(DriverConfigurazioneNotFound notFound) {}		
								}
							}
						}
					}
				}	

			}
			
		}
				
	}
	
	private void readTokenPolicy_retrieve(Archive archive, String nomePolicy, ArchiveCascadeConfiguration cascadeConfig, ArchiveType provenienza) throws Exception {
		
		if(cascadeConfig.isCascadePolicyConfigurazione() || ArchiveType.CONFIGURAZIONE_TOKEN_POLICY.equals(provenienza)) {
		
			String key = ArchiveTokenPolicy.buildKey(CostantiConfigurazione.GENERIC_PROPERTIES_TOKEN_TIPOLOGIA_RETRIEVE, nomePolicy);
			if(archive.getToken_retrieve_policies().containsKey(key)){
				// gia gestito
			}
			else{
				GenericProperties policy = this.archiveEngine.getGenericProperties_retrieve(nomePolicy);
				
				archive.getToken_retrieve_policies().add(new ArchiveTokenPolicy(policy, this.idCorrelazione));
				
				// aggiungo plugin
				if(policy!=null && policy.sizePropertyList()>0) {
					for (int i = 0; i < policy.sizePropertyList(); i++) {
						Property p = policy.getProperty(i);
						if(CostantiConfigurazione.POLICY_RETRIEVE_TOKEN_PARSER_TYPE_CUSTOM.equals(p.getNome()) && 
								CostantiConfigurazione.POLICY_RETRIEVE_TOKEN_PARSER_TYPE_CUSTOM_CYSTOM.equals(p.getValore())) {
							for (int j = 0; j < policy.sizePropertyList(); j++) {
								Property pJ = policy.getProperty(j);
								if(CostantiConfigurazione.POLICY_RETRIEVE_TOKEN_PARSER_PLUGIN_TYPE.equals(pJ.getNome())) {
									try {
										ArchiveCascadeConfiguration cascadeConfigPlugin = new ArchiveCascadeConfiguration();
										cascadeConfigPlugin.setCascadePluginConfigurazione(true);
										readPlugin_classe(archive, TipoPlugin.TOKEN_NEGOZIAZIONE.getValue(), pJ.getValore(), cascadeConfigPlugin, provenienza);
									}catch(DriverConfigurazioneNotFound notFound) {}		
								}
							}
						}
					}
				}
			}
			
		}
		
	}
	
	
	private void readAttributeAuthorityConfigurazione(Archive archive, ArchiveType provenienza) throws Exception {
		
		ArchiveCascadeConfiguration cascadeConfigPolicy = new ArchiveCascadeConfiguration();
		cascadeConfigPolicy.setCascadePolicyConfigurazione(true);
		
		List<IDGenericProperties> listAttributeAuthorities = null;
		try {
			listAttributeAuthorities = this.archiveEngine.getAllIdGenericProperties_attributeAuthorities();
		}catch(DriverConfigurazioneNotFound notFound) {}
		if(listAttributeAuthorities!=null && listAttributeAuthorities.size()>0) {
			for (IDGenericProperties idGP : listAttributeAuthorities) {
				readAttributeAuthority(archive,idGP.getNome(), cascadeConfigPolicy, provenienza);
			}
		}
		
	}
	
	private void readAttributeAuthority(Archive archive,IDGenericProperties idGP, ArchiveCascadeConfiguration cascadeConfig, ArchiveType provenienza) throws Exception {
		if(CostantiConfigurazione.GENERIC_PROPERTIES_ATTRIBUTE_AUTHORITY.equals(idGP.getTipologia())) {
			readAttributeAuthority(archive, idGP.getNome(), cascadeConfig, provenienza);
		}
	}
	
	private void readAttributeAuthority(Archive archive, String nomePolicy, ArchiveCascadeConfiguration cascadeConfig, ArchiveType provenienza) throws Exception {
		
		if(cascadeConfig.isCascadePolicyConfigurazione() || ArchiveType.CONFIGURAZIONE_ATTRIBUTE_AUTHORITY.equals(provenienza)) {
		
			String key = ArchiveAttributeAuthority.buildKey(CostantiConfigurazione.GENERIC_PROPERTIES_ATTRIBUTE_AUTHORITY, nomePolicy);
			if(archive.getAttributeAuthorities().containsKey(key)){
				// gia gestito
			}
			else{
				GenericProperties policy = this.archiveEngine.getGenericProperties_attributeAuthority(nomePolicy);
				
				archive.getAttributeAuthorities().add(new ArchiveAttributeAuthority(policy, this.idCorrelazione));
				
				// aggiungo plugin
				if(policy!=null && policy.sizePropertyList()>0) {
					for (int i = 0; i < policy.sizePropertyList(); i++) {
						Property p = policy.getProperty(i);
						if(CostantiConfigurazione.AA_RESPONSE_TYPE.equals(p.getNome()) && 
								CostantiConfigurazione.AA_RESPONSE_TYPE_VALUE_CUSTOM.equals(p.getValore())) {
							for (int j = 0; j < policy.sizePropertyList(); j++) {
								Property pJ = policy.getProperty(j);
								if(CostantiConfigurazione.AA_RESPONSE_PARSER_PLUGIN_TYPE.equals(pJ.getNome())) {
									try {
										// aggiungo anche il plugin configurato.
										ArchiveCascadeConfiguration cascadeConfigPlugin = new ArchiveCascadeConfiguration();
										cascadeConfigPlugin.setCascadePluginConfigurazione(true);
										readPlugin_classe(archive, TipoPlugin.ATTRIBUTE_AUTHORITY.getValue(), pJ.getValore(), cascadeConfigPlugin, provenienza);
									}catch(DriverConfigurazioneNotFound notFound) {}		
								}
							}
						}
					}
				}
			}
			
		}
		
	}
	
	private void readPluginConfigurazione(Archive archive, ArchiveType provenienza) throws Exception {
		
		ArchiveCascadeConfiguration cascadeConfigPlugin = new ArchiveCascadeConfiguration();
		cascadeConfigPlugin.setCascadePluginConfigurazione(true);
		
		List<IdPlugin> listPluginClasse = null;
		try {
			listPluginClasse = this.archiveEngine.getAllIdPluginClasse();
		}catch(DriverConfigurazioneNotFound notFound) {}
		if(listPluginClasse!=null && listPluginClasse.size()>0) {
			for (IdPlugin idP : listPluginClasse) {
				readPlugin_classe(archive,idP.getTipoPlugin(), idP.getTipo(), cascadeConfigPlugin, provenienza);
			}
		}
		
		List<String> listPluginArchivi = null;
		try {
			listPluginArchivi = this.archiveEngine.getAllIdPluginArchivio();
		}catch(DriverConfigurazioneNotFound notFound) {}
		if(listPluginArchivi!=null && listPluginArchivi.size()>0) {
			for (String idP : listPluginArchivi) {
				readPlugin_archivio(archive,idP, cascadeConfigPlugin, provenienza);
			}
		}
		
		
	}
	
	private void readPlugin_classe(Archive archive, IdPlugin idPlugin, ArchiveCascadeConfiguration cascadeConfig, ArchiveType provenienza) throws Exception {
		this.readPlugin_classe(archive, idPlugin.getTipoPlugin(), idPlugin.getTipo(), cascadeConfig, provenienza);
	}
	private void readPlugin_classe(Archive archive, String tipoPlugin, String tipo, ArchiveCascadeConfiguration cascadeConfig, ArchiveType provenienza) throws Exception {
		
		if(cascadeConfig.isCascadePluginConfigurazione() || ArchiveType.CONFIGURAZIONE_PLUGIN_CLASSE.equals(provenienza)) {
		
			String key = ArchivePluginClasse.buildKey(tipoPlugin, tipo);
			if(archive.getPlugin_classi().containsKey(key)){
				// gia gestito
			}
			else{
				archive.getPlugin_classi().add(new ArchivePluginClasse(this.archiveEngine.getPluginClasse(tipoPlugin, tipo), this.idCorrelazione));
			}
			
		}
		
	}
	
	private void readPlugin_archivio(Archive archive, String nome, ArchiveCascadeConfiguration cascadeConfig, ArchiveType provenienza) throws Exception {
		
		if(cascadeConfig.isCascadePluginConfigurazione() || ArchiveType.CONFIGURAZIONE_PLUGIN_ARCHVIO.equals(provenienza)) {
		
			String key = ArchivePluginArchivio.buildKey(nome);
			if(archive.getPlugin_archivi().containsKey(key)){
				// gia gestito
			}
			else{
				archive.getPlugin_archivi().add(new ArchivePluginArchivio(this.archiveEngine.getPluginArchivio(nome), this.idCorrelazione));
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
	
	
	
	private void readGruppo(Archive archive, IDGruppo idGruppo, ArchiveCascadeConfiguration cascadeConfig, ArchiveType provenienza) throws Exception{
		this.readGruppo(archive, idGruppo, cascadeConfig, true, provenienza);
	}
	private void readGruppo(Archive archive, IDGruppo idGruppo, ArchiveCascadeConfiguration cascadeConfig, boolean cascadeAvanti, ArchiveType provenienza) throws Exception{
		
		String key = ArchiveGruppo.buildKey(idGruppo.getNome());
		if(archive.getGruppi().containsKey(key)){
			// gia gestito
		}
		else{
			try{
				if(cascadeConfig.isCascadeGruppi() || ArchiveType.GRUPPO.equals(provenienza)){
					
					// add
					org.openspcoop2.core.registry.Gruppo gruppo = this.archiveEngine.getGruppo(idGruppo);
					ArchiveGruppo archiveGruppo = new ArchiveGruppo(gruppo, this.idCorrelazione);
					archive.getGruppi().add(archiveGruppo);
					
					// *** dipendenze: oggetti necessari per la creazione dell'oggetto sopra aggiunto ***
					
					// non vi sono oggetti che possiedono come figlio il gruppo
					
				}
			}catch(Exception e){
				throw new ProtocolException("(Gruppo "+idGruppo.getNome()+") "+e.getMessage(),e);
			}
		}
		
		
		// *** cascade in avanti ***
		
		if(cascadeAvanti){
		
			// accordi servizio parte comune
			FiltroRicercaAccordi filtroAccordiServizioParteComune = new FiltroRicercaAccordi();
			filtroAccordiServizioParteComune.setIdGruppo(idGruppo);
			filtroAccordiServizioParteComune.setServizioComposto(false);
			try{
				List<IDAccordo> idsAccordi = this.archiveEngine.getAllIdAccordiServizioParteComune(filtroAccordiServizioParteComune);
				if(idsAccordi!=null && idsAccordi.size()>0){
					for (IDAccordo idAccordo : idsAccordi) {
						this.readAccordoServizioParteComune(archive, idAccordo, cascadeConfig, ArchiveType.GRUPPO);
					}
				}
			}catch(DriverRegistroServiziNotFound notFound){}	
			
			// accordi servizio composto
			FiltroRicercaAccordi filtroAccordiServizioComposto = new FiltroRicercaAccordi();
			filtroAccordiServizioComposto.setIdGruppo(idGruppo);
			filtroAccordiServizioComposto.setServizioComposto(true);
			try{
				List<IDAccordo> idsAccordi = this.archiveEngine.getAllIdAccordiServizioParteComune(filtroAccordiServizioComposto);
				if(idsAccordi!=null && idsAccordi.size()>0){
					for (IDAccordo idAccordo : idsAccordi) {
						this.readAccordoServizioComposto(archive, idAccordo, cascadeConfig, ArchiveType.GRUPPO);
					}
				}
			}catch(DriverRegistroServiziNotFound notFound){}	
			
			if(cascadeConfig.isCascadePolicyConfigurazione()) {
			
				// rate limiting
				List<IdActivePolicy> listControlloTraffico_activePolicies = null;
				try {
					listControlloTraffico_activePolicies = this.archiveEngine.getAllIdControlloTraffico_activePolicies_all(idGruppo.getNome());
				}catch(DriverConfigurazioneNotFound notFound) {}
				if(listControlloTraffico_activePolicies!=null && listControlloTraffico_activePolicies.size()>0) {
					for (IdActivePolicy idPolicy : listControlloTraffico_activePolicies) {
						this.readControlloTraffico_activePolicy(archive, idPolicy, cascadeConfig, provenienza);				
					}
				}
				
				// allarmi
				List<IdAllarme> listAllarmi = null;
				try {
					listAllarmi = this.archiveEngine.getAllIdAllarmi_all(idGruppo.getNome());
				}catch(DriverConfigurazioneNotFound notFound) {}
				if(listAllarmi!=null && listAllarmi.size()>0) {
					for (IdAllarme idAllarme : listAllarmi) {
						this.readAllarme(archive, idAllarme, cascadeConfig, provenienza);				
					}
				}
				
			}
			
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
			
			List<IDPortaDelegata> idsPD = new ArrayList<IDPortaDelegata>();
			
			FiltroRicercaPorteDelegate filtroPorteDelegate = new FiltroRicercaPorteDelegate();
			filtroPorteDelegate.setIdRuolo(idRuolo);
			try{
				List<IDPortaDelegata> _idsPD = this.archiveEngine.getAllIdPorteDelegate(filtroPorteDelegate);
				if(_idsPD!=null && _idsPD.size()>0){
					idsPD.addAll(_idsPD);
				}
			}catch(DriverConfigurazioneNotFound notFound){}	
			
			filtroPorteDelegate = new FiltroRicercaPorteDelegate();
			filtroPorteDelegate.setIdRuoloToken(idRuolo);
			try{
				List<IDPortaDelegata> _idsPD = this.archiveEngine.getAllIdPorteDelegate(filtroPorteDelegate);
				if(_idsPD!=null && _idsPD.size()>0){
					for (IDPortaDelegata idPortaDelegataCheck : _idsPD) {
						if(!idsPD.contains(idPortaDelegataCheck)) {
							idsPD.add(idPortaDelegataCheck);
						}
					}
				}
			}catch(DriverConfigurazioneNotFound notFound){}	
			
			if(idsPD!=null && idsPD.size()>0){
				for (IDPortaDelegata idPD : idsPD) {
					this.readPortaDelegata(archive, idPD, cascadeConfig, ArchiveType.RUOLO);
				}
			}
			
			
			// porteApplicative
			
			List<IDPortaApplicativa> idsPA = new ArrayList<IDPortaApplicativa>();
			
			FiltroRicercaPorteApplicative filtroPorteApplicative = new FiltroRicercaPorteApplicative();
			filtroPorteApplicative.setIdRuolo(idRuolo);
			try{
				List<IDPortaApplicativa> _idsPA = this.archiveEngine.getAllIdPorteApplicative(filtroPorteApplicative);
				if(_idsPA!=null && !_idsPA.isEmpty()) {
					idsPA.addAll(_idsPA);
				}
			}catch(DriverConfigurazioneNotFound notFound){}
			
			filtroPorteApplicative = new FiltroRicercaPorteApplicative();
			filtroPorteApplicative.setIdRuoloToken(idRuolo);
			try{
				List<IDPortaApplicativa> _idsPA = this.archiveEngine.getAllIdPorteApplicative(filtroPorteApplicative);
				if(_idsPA!=null && !_idsPA.isEmpty()) {
					for (IDPortaApplicativa idPortaApplicativaCheck : _idsPA) {
						if(!idsPA.contains(idPortaApplicativaCheck)) {
							idsPA.add(idPortaApplicativaCheck);
						}
					}
				}
			}catch(DriverConfigurazioneNotFound notFound){}
			
			if(idsPA!=null && idsPA.size()>0){
				for (IDPortaApplicativa idPA : idsPA) {
					this.readPortaApplicativa(archive, idPA, cascadeConfig, ArchiveType.RUOLO);
				}
			}
			
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
			}catch(DriverConfigurazioneNotFound notFound){}
		}
		
		
	}
	
	
	private void readScope(Archive archive, IDScope idScope, ArchiveCascadeConfiguration cascadeConfig, ArchiveType provenienza) throws Exception{
		this.readScope(archive, idScope, cascadeConfig, true, provenienza);
	}
	private void readScope(Archive archive, IDScope idScope, ArchiveCascadeConfiguration cascadeConfig, boolean cascadeAvanti, ArchiveType provenienza) throws Exception{
		
		String key = ArchiveScope.buildKey(idScope.getNome());
		if(archive.getScope().containsKey(key)){
			// gia gestito
		}
		else{
			try{
				if(cascadeConfig.isCascadeScope() || ArchiveType.SCOPE.equals(provenienza)){
					
					// add
					org.openspcoop2.core.registry.Scope scope = this.archiveEngine.getScope(idScope);
					ArchiveScope archiveScope = new ArchiveScope(scope, this.idCorrelazione);
					archive.getScope().add(archiveScope);
					
					// *** dipendenze: oggetti necessari per la creazione dell'oggetto sopra aggiunto ***
					
					// non vi sono oggetti che possiedono come figlio il scope
					
				}
			}catch(Exception e){
				throw new ProtocolException("(Scope "+idScope.getNome()+") "+e.getMessage(),e);
			}
		}
		
		
		// *** cascade in avanti ***
		
		if(cascadeAvanti){
		

			// porteDelegate
			
			FiltroRicercaPorteDelegate filtroPorteDelegate = new FiltroRicercaPorteDelegate();
			filtroPorteDelegate.setIdScope(idScope);
			try{
				List<IDPortaDelegata> idsPD = this.archiveEngine.getAllIdPorteDelegate(filtroPorteDelegate);
				if(idsPD!=null && idsPD.size()>0){
					for (IDPortaDelegata idPD : idsPD) {
						this.readPortaDelegata(archive, idPD, cascadeConfig, ArchiveType.SCOPE);
					}
				}
			}catch(DriverConfigurazioneNotFound notFound){}	
			
			
			// porteApplicative
			
			FiltroRicercaPorteApplicative filtroPorteApplicative = new FiltroRicercaPorteApplicative();
			filtroPorteApplicative.setIdScope(idScope);
			try{
				List<IDPortaApplicativa> idsPA = this.archiveEngine.getAllIdPorteApplicative(filtroPorteApplicative);
				if(idsPA!=null && idsPA.size()>0){
					for (IDPortaApplicativa idPA : idsPA) {
						this.readPortaApplicativa(archive, idPA, cascadeConfig, ArchiveType.SCOPE);
					}
				}
			}catch(DriverConfigurazioneNotFound notFound){}
			

			
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
					if(sa==null) {
						throw new Exception("Servizio Applicativo '"+idServizioApplicativo+"' non esistente");
					}
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
					
					// token policy di negoziazione
					if(cascadeConfig.isCascadePolicyConfigurazione()) {
						if(sa!=null && sa.getInvocazioneServizio()!=null) {
							if(sa.getInvocazioneServizio().getConnettore()!=null &&
									sa.getInvocazioneServizio().getConnettore().getProperties()!=null && sa.getInvocazioneServizio().getConnettore().getProperties().containsKey(CostantiConnettori.CONNETTORE_TOKEN_POLICY)) {
								this.readTokenPolicy_retrieve(archive, sa.getInvocazioneServizio().getConnettore().getProperties().get(CostantiConnettori.CONNETTORE_TOKEN_POLICY),
										cascadeConfig, provenienza);
							}
						}
						if(sa!=null && sa.getRispostaAsincrona()!=null) {
							if(sa.getRispostaAsincrona().getConnettore()!=null &&
									sa.getRispostaAsincrona().getConnettore().getProperties()!=null && sa.getRispostaAsincrona().getConnettore().getProperties().containsKey(CostantiConnettori.CONNETTORE_TOKEN_POLICY)) {
								this.readTokenPolicy_retrieve(archive, sa.getRispostaAsincrona().getConnettore().getProperties().get(CostantiConnettori.CONNETTORE_TOKEN_POLICY),
										cascadeConfig, provenienza);
							}
						}
					}
					
					// aggiungo eventuale plugin configurato.
					if(cascadeConfig.isCascadePluginConfigurazione()) {
						if(sa!=null && sa.getInvocazioneServizio()!=null) {
							if(sa.getInvocazioneServizio().getConnettore()!=null &&
									sa.getInvocazioneServizio().getConnettore().getCustom()!=null &&
									sa.getInvocazioneServizio().getConnettore().getCustom() &&
									sa.getInvocazioneServizio().getConnettore().getTipo()!=null) {
								TipiConnettore tipo = TipiConnettore.toEnumFromName(sa.getInvocazioneServizio().getConnettore().getTipo());
								if(tipo==null) {
									try {
										readPlugin_classe(archive, TipoPlugin.CONNETTORE.getValue(), sa.getInvocazioneServizio().getConnettore().getTipo(), cascadeConfig, provenienza);
									}catch(DriverConfigurazioneNotFound notFound) {}
								}
							}
						}
						if(sa!=null && sa.getRispostaAsincrona()!=null) {
							if(sa.getRispostaAsincrona().getConnettore()!=null  &&
									sa.getRispostaAsincrona().getConnettore().getCustom()!=null &&
									sa.getRispostaAsincrona().getConnettore().getCustom() &&
									sa.getRispostaAsincrona().getConnettore().getTipo()!=null) {
								TipiConnettore tipo = TipiConnettore.toEnumFromName(sa.getRispostaAsincrona().getConnettore().getTipo());
								if(tipo==null) {
									try {
										readPlugin_classe(archive, TipoPlugin.CONNETTORE.getValue(), sa.getRispostaAsincrona().getConnettore().getTipo(), cascadeConfig, provenienza);
									}catch(DriverConfigurazioneNotFound notFound) {}
								}
							}
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
					org.openspcoop2.core.registry.AccordoServizioParteComune as = this.archiveEngine.getAccordoServizioParteComune(idAccordoServizio,true,true);
					ArchiveAccordoServizioParteComune archiveAs = new ArchiveAccordoServizioParteComune(as, this.idCorrelazione);
					archive.getAccordiServizioParteComune().add(archiveAs);
					
					// *** dipendenze: oggetti necessari per la creazione dell'oggetto sopra aggiunto ***
					
					this.readSoggetto(archive, idAccordoServizio.getSoggettoReferente(), cascadeConfig, false, ArchiveType.ACCORDO_SERVIZIO_PARTE_COMUNE); // per evitare loop
					
					if(as.getGruppi()!=null && as.getGruppi().sizeGruppoList()>0) {
						for (GruppoAccordo gruppo : as.getGruppi().getGruppoList()) {
							IDGruppo idGruppo = new IDGruppo(gruppo.getNome());
							this.readGruppo(archive, idGruppo, cascadeConfig, false,
									ArchiveType.GRUPPO); // voglio che sia aggiunto sempre e comunque il tag (si tratta di un caso eccezionale, essendo il tag un elemento che non contiene aspetti di configurazione)
									//ArchiveType.ACCORDO_SERVIZIO_PARTE_COMUNE); // per evitare loop
						}
					}
					
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
					as = this.archiveEngine.getAccordoServizioParteComune(idAccordoServizio,true,true);
					ArchiveAccordoServizioComposto archiveAs = new ArchiveAccordoServizioComposto(as, this.idCorrelazione);
					archive.getAccordiServizioComposto().add(archiveAs);
				
					// *** dipendenze: oggetti necessari per la creazione dell'oggetto sopra aggiunto ***
					
					this.readSoggetto(archive, idAccordoServizio.getSoggettoReferente(), cascadeConfig, false, ArchiveType.ACCORDO_SERVIZIO_COMPOSTO); // per evitare loop
					
				}
				else{
					as = this.archiveEngine.getAccordoServizioParteComune(idAccordoServizio,false,true);
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
				if(cascadeConfig.isCascadeAccordoServizioParteSpecifica() 
						|| ArchiveType.ACCORDO_SERVIZIO_PARTE_SPECIFICA.equals(provenienza) 
						|| ArchiveType.EROGAZIONE.equals(provenienza)
						|| ArchiveType.FRUIZIONE.equals(provenienza)){
					fruitoriList = new ArrayList<Fruitore>();
				
					// add
					as = this.archiveEngine.getAccordoServizioParteSpecifica(idAccordoServizio,true);
					if(as==null) {
						throw new Exception("Accordo di servizio '"+idAccordoServizio+"' non esistente");
					}
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
					if(!ArchiveType.FRUIZIONE.equals(provenienza)) {
						List<IDPortaApplicativa> listIDPA = this.archiveEngine.getIDPorteApplicativeAssociateErogazione(idAccordoServizio);
						if(listIDPA!=null && listIDPA.size()>0){
							if(archiveAs.getMappingPorteApplicativeAssociate()==null) {
								archiveAs.setMappingPorteApplicativeAssociate(new ArrayList<>());
							}
							for (IDPortaApplicativa idPortaApplicativa : listIDPA) {
								archiveAs.getMappingPorteApplicativeAssociate().add(this.archiveEngine.getMappingErogazionePortaApplicativa(idAccordoServizio, idPortaApplicativa));
							}
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
					
					// token policy di negoziazione (aggiunte solamente se abilitato il cascade di configurazione)
					if(cascadeConfig.isCascadePolicyConfigurazione()) {
						if(as!=null && as.getConfigurazioneServizio()!=null) {
							if(as.getConfigurazioneServizio().getConnettore()!=null &&
									as.getConfigurazioneServizio().getConnettore().getProperties()!=null && as.getConfigurazioneServizio().getConnettore().getProperties().containsKey(CostantiConnettori.CONNETTORE_TOKEN_POLICY)) {
								this.readTokenPolicy_retrieve(archive, as.getConfigurazioneServizio().getConnettore().getProperties().get(CostantiConnettori.CONNETTORE_TOKEN_POLICY),
										cascadeConfig, provenienza);
							}
							if(as.getConfigurazioneServizio().sizeConfigurazioneAzioneList()>0) {
								for (ConfigurazioneServizioAzione confAzione : as.getConfigurazioneServizio().getConfigurazioneAzioneList()) {
									if(confAzione.getConnettore()!=null &&
											confAzione.getConnettore().getProperties()!=null && confAzione.getConnettore().getProperties().containsKey(CostantiConnettori.CONNETTORE_TOKEN_POLICY)) {
										this.readTokenPolicy_retrieve(archive, confAzione.getConnettore().getProperties().get(CostantiConnettori.CONNETTORE_TOKEN_POLICY),
												cascadeConfig, provenienza);
									}
								}
							}
						}
					}
					
					// aggiungo eventuale plugin configurato.
					if(cascadeConfig.isCascadePluginConfigurazione()) {
						if(as!=null && as.getConfigurazioneServizio()!=null) {
							if(as.getConfigurazioneServizio().getConnettore()!=null &&
									as.getConfigurazioneServizio().getConnettore().getCustom()!=null &&
									as.getConfigurazioneServizio().getConnettore().getCustom() &&
									as.getConfigurazioneServizio().getConnettore().getTipo()!=null) {
								TipiConnettore tipo = TipiConnettore.toEnumFromName(as.getConfigurazioneServizio().getConnettore().getTipo());
								if(tipo==null) {
									try {
										readPlugin_classe(archive, TipoPlugin.CONNETTORE.getValue(), as.getConfigurazioneServizio().getConnettore().getTipo(), cascadeConfig, provenienza);
									}catch(DriverConfigurazioneNotFound notFound) {}
								}
							}
							if(as.getConfigurazioneServizio().sizeConfigurazioneAzioneList()>0) {
								for (ConfigurazioneServizioAzione confAzione : as.getConfigurazioneServizio().getConfigurazioneAzioneList()) {
									if(confAzione.getConnettore()!=null &&
											confAzione.getConnettore().getCustom()!=null &&
											confAzione.getConnettore().getCustom() &&
											confAzione.getConnettore().getTipo()!=null) {
										TipiConnettore tipo = TipiConnettore.toEnumFromName(confAzione.getConnettore().getTipo());
										if(tipo==null) {
											try {
												readPlugin_classe(archive, TipoPlugin.CONNETTORE.getValue(), confAzione.getConnettore().getTipo(), cascadeConfig, provenienza);
											}catch(DriverConfigurazioneNotFound notFound) {}
										}
									}
								}
							}
						}
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
					
					// token policy di negoziazione
					if(cascadeConfig.isCascadePolicyConfigurazione()) {
						if(fruitore!=null) {
							if(fruitore.getConnettore()!=null &&
									fruitore.getConnettore().getProperties()!=null && fruitore.getConnettore().getProperties().containsKey(CostantiConnettori.CONNETTORE_TOKEN_POLICY)) {
								this.readTokenPolicy_retrieve(archive, fruitore.getConnettore().getProperties().get(CostantiConnettori.CONNETTORE_TOKEN_POLICY),
										cascadeConfig, provenienza);
							}
							if(fruitore.sizeConfigurazioneAzioneList()>0) {
								for (ConfigurazioneServizioAzione confAzione : fruitore.getConfigurazioneAzioneList()) {
									if(confAzione.getConnettore()!=null &&
											confAzione.getConnettore().getProperties()!=null && confAzione.getConnettore().getProperties().containsKey(CostantiConnettori.CONNETTORE_TOKEN_POLICY)) {
										this.readTokenPolicy_retrieve(archive, confAzione.getConnettore().getProperties().get(CostantiConnettori.CONNETTORE_TOKEN_POLICY),
												cascadeConfig, provenienza);
									}
								}
							}
						}
					}
					
					// aggiungo eventuale plugin configurato.
					if(cascadeConfig.isCascadePluginConfigurazione()) {
						if(fruitore!=null) {
							if(fruitore.getConnettore()!=null &&
									fruitore.getConnettore().getCustom()!=null &&
									fruitore.getConnettore().getCustom() &&
									fruitore.getConnettore().getTipo()!=null) {
								TipiConnettore tipo = TipiConnettore.toEnumFromName(fruitore.getConnettore().getTipo());
								if(tipo==null) {
									try {
										readPlugin_classe(archive, TipoPlugin.CONNETTORE.getValue(), fruitore.getConnettore().getTipo(), cascadeConfig, provenienza);
									}catch(DriverConfigurazioneNotFound notFound) {}
								}
							}
							if(fruitore.sizeConfigurazioneAzioneList()>0) {
								for (ConfigurazioneServizioAzione confAzione : fruitore.getConfigurazioneAzioneList()) {
									if(confAzione.getConnettore()!=null &&
											confAzione.getConnettore().getCustom()!=null &&
											confAzione.getConnettore().getCustom() &&
											confAzione.getConnettore().getTipo()!=null) {
										TipiConnettore tipo = TipiConnettore.toEnumFromName(confAzione.getConnettore().getTipo());
										if(tipo==null) {
											try {
												readPlugin_classe(archive, TipoPlugin.CONNETTORE.getValue(), confAzione.getConnettore().getTipo(), cascadeConfig, provenienza);
											}catch(DriverConfigurazioneNotFound notFound) {}
										}
									}
								}
							}
						}
					}
					
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
	
	private PortaDelegata readPortaDelegata(Archive archive, IDPortaDelegata idPortaDelegata, 
			ArchiveCascadeConfiguration cascadeConfig, ArchiveType provenienza) throws Exception{
		return this.readPortaDelegata(archive, idPortaDelegata, cascadeConfig, true, provenienza);
	}
	private PortaDelegata readPortaDelegata(Archive archive, IDPortaDelegata idPortaDelegata, 
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
				
					if(pd==null) {
						throw new Exception("Porta delegata '"+idPortaDelegata+"' non esistente");
					}
					
					// add
					if(pd.getTipoSoggettoProprietario()==null){
						pd.setTipoSoggettoProprietario(idSoggettoFruitore.getTipo());
					}
					if(pd.getNomeSoggettoProprietario()==null){
						pd.setNomeSoggettoProprietario(idSoggettoFruitore.getNome());
					}
					ArchivePortaDelegata archivePd = new ArchivePortaDelegata(pd, this.idCorrelazione);
					archive.getPorteDelegate().add(archivePd);
					
					// add rateLimiting
					readControlloTrafficoPolicyPorta(archive, true, pd.getNome(), cascadeConfig, provenienza);
					
					// add allarmi
					readAllarmiPorta(archive, true, pd.getNome(), cascadeConfig, provenienza);
					
					
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
					
					// serviziApplicativi autorizzati
					if(pd.getAutorizzazioneToken()!=null && pd.getAutorizzazioneToken().getServiziApplicativi()!=null &&  
							pd.getAutorizzazioneToken().getServiziApplicativi().sizeServizioApplicativoList()>0){
						for (PortaDelegataServizioApplicativo sa: pd.getAutorizzazioneToken().getServiziApplicativi().getServizioApplicativoList()) {
							IDServizioApplicativo idSA = new IDServizioApplicativo();
							idSA.setIdSoggettoProprietario(idSoggettoFruitore);
							idSA.setNome(sa.getNome());
							this.readServizioApplicativo(archive, idSA, cascadeConfig, ArchiveType.PORTA_DELEGATA);
						}
					}
					
					// ruoli token
					if(pd!=null && pd.getAutorizzazioneToken()!=null && pd.getAutorizzazioneToken().getRuoli()!=null && 
							pd.getAutorizzazioneToken().getRuoli().sizeRuoloList()>0){
						for (int i = 0; i < pd.getAutorizzazioneToken().getRuoli().sizeRuoloList(); i++) {
							Ruolo ruolo = pd.getAutorizzazioneToken().getRuoli().getRuolo(i);
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
					
					// scope
					if(pd!=null && pd.getScope()!=null && 
							pd.getScope().sizeScopeList()>0){
						for (int i = 0; i < pd.getScope().sizeScopeList(); i++) {
							Scope scope = pd.getScope().getScope(i);
							FiltroRicercaScope filtroScope = new FiltroRicercaScope();
							filtroScope.setNome(scope.getNome());
							try{
								List<IDScope> idsScope = this.archiveEngine.getAllIdScope(filtroScope);
								if(idsScope!=null && idsScope.size()>0){
									for (IDScope idScope : idsScope) {
										this.readScope(archive, idScope, cascadeConfig, false, ArchiveType.PORTA_DELEGATA); // per evitare loop
									}
								}
							}catch(DriverRegistroServiziNotFound notFound){}	
						}
					}
					
					// token policy di validazione
					if(cascadeConfig.isCascadePolicyConfigurazione()) {
						if(pd!=null && pd.getGestioneToken()!=null && pd.getGestioneToken().getPolicy()!=null && !"".equals(pd.getGestioneToken().getPolicy())) {
							this.readTokenPolicy_validation(archive, pd.getGestioneToken().getPolicy(), cascadeConfig, provenienza);
						}
					}
					
					// attribute authority
					if(cascadeConfig.isCascadePolicyConfigurazione()) {
						if(pd!=null && pd.sizeAttributeAuthorityList()>0) {
							for (AttributeAuthority aa : pd.getAttributeAuthorityList()) {
								String nomeAA = aa.getNome();
								if(nomeAA!=null && !"".equals(nomeAA)) {
									this.readAttributeAuthority(archive, nomeAA, cascadeConfig, provenienza);
								}
							}
						}
					}
					
					// trasformazioni
					if(pd!=null && pd.getTrasformazioni()!=null &&
							pd.getTrasformazioni().sizeRegolaList()>0) {
						for (int i = 0; i < pd.getTrasformazioni().sizeRegolaList(); i++) {
							TrasformazioneRegola regola = pd.getTrasformazioni().getRegola(i);
							if(regola.getApplicabilita()!=null) {
								
								if(regola.getApplicabilita().sizeServizioApplicativoList()>0) {
									for (int j = 0; j < regola.getApplicabilita().sizeServizioApplicativoList(); j++) {
										TrasformazioneRegolaApplicabilitaServizioApplicativo trSa = regola.getApplicabilita().getServizioApplicativo(j);
										if(trSa.getTipoSoggettoProprietario()!=null && trSa.getNomeSoggettoProprietario()!=null && trSa.getNome()!=null) {
											IDServizioApplicativo idServizioApplicativo = new IDServizioApplicativo();
											idServizioApplicativo.setNome(trSa.getNome());
											idServizioApplicativo.setIdSoggettoProprietario(new IDSoggetto(trSa.getTipoSoggettoProprietario(), trSa.getNomeSoggettoProprietario()));
											this.readServizioApplicativo(archive, idServizioApplicativo, cascadeConfig, false, ArchiveType.PORTA_DELEGATA); // per evitare loop
										}
									}
								}	
								
								if(regola.getApplicabilita().sizeSoggettoList()>0) {
									for (int j = 0; j < regola.getApplicabilita().sizeSoggettoList(); j++) {
										TrasformazioneRegolaApplicabilitaSoggetto trSoggetto = regola.getApplicabilita().getSoggetto(j);
										if(trSoggetto.getTipo()!=null && trSoggetto.getNome()!=null) {
											IDSoggetto idSoggetto = new IDSoggetto(trSoggetto.getTipo(), trSoggetto.getNome());
											this.readSoggetto(archive, idSoggetto, cascadeConfig, false, ArchiveType.PORTA_DELEGATA); // per evitare loop
										}
									}
								}
							}
						}
					}
					
					// aggiungo eventuale plugin configurato.
					if(cascadeConfig.isCascadePluginConfigurazione()) {
						
						if(pd!=null && StringUtils.isNotEmpty(pd.getAutenticazione())) {
							TipoAutenticazione tipo = TipoAutenticazione.toEnumConstant(pd.getAutenticazione(), false);
							if(tipo==null) {
								try {
									readPlugin_classe(archive, TipoPlugin.AUTENTICAZIONE.getValue(), pd.getAutenticazione(), cascadeConfig, provenienza);
								}catch(DriverConfigurazioneNotFound notFound) {}
							}
						}
						if(pd!=null && StringUtils.isNotEmpty(pd.getAutorizzazione())) {
							TipoAutorizzazione tipo = TipoAutorizzazione.toEnumConstant(pd.getAutorizzazione(), false);
							if(tipo==null) {
								try {
									readPlugin_classe(archive, TipoPlugin.AUTORIZZAZIONE.getValue(), pd.getAutorizzazione(), cascadeConfig, provenienza);
								}catch(DriverConfigurazioneNotFound notFound) {}
							}
						}
						if(pd!=null && StringUtils.isNotEmpty(pd.getAutorizzazioneContenuto())) {
							if(!CostantiConfigurazione.AUTORIZZAZIONE_CONTENUTO_BUILT_IN.equals(pd.getAutorizzazioneContenuto())) {
								try {
									readPlugin_classe(archive, TipoPlugin.AUTORIZZAZIONE_CONTENUTI.getValue(), pd.getAutorizzazione(), cascadeConfig, provenienza);
								}catch(DriverConfigurazioneNotFound notFound) {}
							}
						}
						if(pd!=null && pd.getIntegrazione()!=null && StringUtils.isNotEmpty(pd.getIntegrazione())) {
							String[]tipi = pd.getIntegrazione().trim().split(",");
							if(tipi!=null && tipi.length>0) {
								for (String t : tipi) {
									if(this.archiveEngine.existsPluginClasse(TipoPlugin.INTEGRAZIONE.getValue(), t)){
										try {
											readPlugin_classe(archive, TipoPlugin.INTEGRAZIONE.getValue(), t, cascadeConfig, provenienza);
										}catch(DriverConfigurazioneNotFound notFound) {}
									}
								}
							}
						}
						if(pd!=null && pd.getConfigurazioneHandler()!=null) {
							if(pd.getConfigurazioneHandler().getRequest()!=null) {
								if(pd.getConfigurazioneHandler().getRequest().sizePreInList()>0) {
									for (ConfigurazioneHandler ch : pd.getConfigurazioneHandler().getRequest().getPreInList()) {
										if(this.archiveEngine.existsPluginClasse(TipoPlugin.MESSAGE_HANDLER.getValue(), ch.getTipo())){
											try {
												readPlugin_classe(archive, TipoPlugin.MESSAGE_HANDLER.getValue(), ch.getTipo(), cascadeConfig, provenienza);
											}catch(DriverConfigurazioneNotFound notFound) {}
										}
									}
								}
								if(pd.getConfigurazioneHandler().getRequest().sizeInList()>0) {
									for (ConfigurazioneHandler ch : pd.getConfigurazioneHandler().getRequest().getInList()) {
										if(this.archiveEngine.existsPluginClasse(TipoPlugin.MESSAGE_HANDLER.getValue(), ch.getTipo())){
											try {
												readPlugin_classe(archive, TipoPlugin.MESSAGE_HANDLER.getValue(), ch.getTipo(), cascadeConfig, provenienza);
											}catch(DriverConfigurazioneNotFound notFound) {}
										}
									}
								}
								if(pd.getConfigurazioneHandler().getRequest().sizeInProtocolInfoList()>0) {
									for (ConfigurazioneHandler ch : pd.getConfigurazioneHandler().getRequest().getInProtocolInfoList()) {
										if(this.archiveEngine.existsPluginClasse(TipoPlugin.MESSAGE_HANDLER.getValue(), ch.getTipo())){
											try {
												readPlugin_classe(archive, TipoPlugin.MESSAGE_HANDLER.getValue(), ch.getTipo(), cascadeConfig, provenienza);
											}catch(DriverConfigurazioneNotFound notFound) {}
										}
									}
								}
								if(pd.getConfigurazioneHandler().getRequest().sizeOutList()>0) {
									for (ConfigurazioneHandler ch : pd.getConfigurazioneHandler().getRequest().getOutList()) {
										if(this.archiveEngine.existsPluginClasse(TipoPlugin.MESSAGE_HANDLER.getValue(), ch.getTipo())){
											try {
												readPlugin_classe(archive, TipoPlugin.MESSAGE_HANDLER.getValue(), ch.getTipo(), cascadeConfig, provenienza);
											}catch(DriverConfigurazioneNotFound notFound) {}
										}
									}
								}
								if(pd.getConfigurazioneHandler().getRequest().sizePostOutList()>0) {
									for (ConfigurazioneHandler ch : pd.getConfigurazioneHandler().getRequest().getPostOutList()) {
										if(this.archiveEngine.existsPluginClasse(TipoPlugin.MESSAGE_HANDLER.getValue(), ch.getTipo())){
											try {
												readPlugin_classe(archive, TipoPlugin.MESSAGE_HANDLER.getValue(), ch.getTipo(), cascadeConfig, provenienza);
											}catch(DriverConfigurazioneNotFound notFound) {}
										}
									}
								}
							}
							if(pd.getConfigurazioneHandler().getResponse()!=null) {
								if(pd.getConfigurazioneHandler().getResponse().sizePreInList()>0) {
									for (ConfigurazioneHandler ch : pd.getConfigurazioneHandler().getResponse().getPreInList()) {
										if(this.archiveEngine.existsPluginClasse(TipoPlugin.MESSAGE_HANDLER.getValue(), ch.getTipo())){
											try {
												readPlugin_classe(archive, TipoPlugin.MESSAGE_HANDLER.getValue(), ch.getTipo(), cascadeConfig, provenienza);
											}catch(DriverConfigurazioneNotFound notFound) {}
										}
									}
								}
								if(pd.getConfigurazioneHandler().getResponse().sizeInList()>0) {
									for (ConfigurazioneHandler ch : pd.getConfigurazioneHandler().getResponse().getInList()) {
										if(this.archiveEngine.existsPluginClasse(TipoPlugin.MESSAGE_HANDLER.getValue(), ch.getTipo())){
											try {
												readPlugin_classe(archive, TipoPlugin.MESSAGE_HANDLER.getValue(), ch.getTipo(), cascadeConfig, provenienza);
											}catch(DriverConfigurazioneNotFound notFound) {}
										}
									}
								}
//								if(pd.getConfigurazioneHandler().getResponse().sizeInProtocolInfoList()>0) {
//									for (ConfigurazioneHandler ch : pd.getConfigurazioneHandler().getResponse().getInProtocolInfoList()) {
//										if(this.archiveEngine.existsPluginClasse(TipoPlugin.MESSAGE_HANDLER.getValue(), ch.getTipo())){
//											try {
//												readPlugin_classe(archive, TipoPlugin.MESSAGE_HANDLER.getValue(), ch.getTipo(), cascadeConfig, provenienza);
//											}catch(DriverConfigurazioneNotFound notFound) {}
//										}
//									}
//								}
								if(pd.getConfigurazioneHandler().getResponse().sizeOutList()>0) {
									for (ConfigurazioneHandler ch : pd.getConfigurazioneHandler().getResponse().getOutList()) {
										if(this.archiveEngine.existsPluginClasse(TipoPlugin.MESSAGE_HANDLER.getValue(), ch.getTipo())){
											try {
												readPlugin_classe(archive, TipoPlugin.MESSAGE_HANDLER.getValue(), ch.getTipo(), cascadeConfig, provenienza);
											}catch(DriverConfigurazioneNotFound notFound) {}
										}
									}
								}
								if(pd.getConfigurazioneHandler().getResponse().sizePostOutList()>0) {
									for (ConfigurazioneHandler ch : pd.getConfigurazioneHandler().getResponse().getPostOutList()) {
										if(this.archiveEngine.existsPluginClasse(TipoPlugin.MESSAGE_HANDLER.getValue(), ch.getTipo())){
											try {
												readPlugin_classe(archive, TipoPlugin.MESSAGE_HANDLER.getValue(), ch.getTipo(), cascadeConfig, provenienza);
											}catch(DriverConfigurazioneNotFound notFound) {}
										}
									}
								}
							}
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
				
		return pd;
	}
	
	
	private PortaApplicativa readPortaApplicativa(Archive archive, IDPortaApplicativa idPortaApplicativa, 
			ArchiveCascadeConfiguration cascadeConfig, ArchiveType provenienza) throws Exception{
		return this.readPortaApplicativa(archive, idPortaApplicativa, cascadeConfig, true, provenienza);
	}
	private PortaApplicativa readPortaApplicativa(Archive archive, IDPortaApplicativa idPortaApplicativa, 
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
				
					if(pa==null) {
						throw new Exception("Porta applicativa '"+idPortaApplicativa+"' non esistente");
					}
					
					// add
					if(pa.getTipoSoggettoProprietario()==null){
						pa.setTipoSoggettoProprietario(idSoggettoErogatore.getTipo());
					}
					if(pa.getNomeSoggettoProprietario()==null){
						pa.setNomeSoggettoProprietario(idSoggettoErogatore.getNome());
					}
					ArchivePortaApplicativa archivePa = new ArchivePortaApplicativa(pa, this.idCorrelazione);
					archive.getPorteApplicative().add(archivePa);
					
					// add rateLimiting
					readControlloTrafficoPolicyPorta(archive, false, pa.getNome(), cascadeConfig, provenienza);
					
					// add allarmi
					readAllarmiPorta(archive, false, pa.getNome(), cascadeConfig, provenienza);
					
				
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
										this.readRuolo(archive, idRuolo, cascadeConfig, false, ArchiveType.PORTA_APPLICATIVA); // per evitare loop
									}
								}
							}catch(DriverRegistroServiziNotFound notFound){}	
						}
					}
					
					// serviziApplicativi autorizzati token
					if(pa!=null && pa.getAutorizzazioneToken()!=null && pa.getAutorizzazioneToken().getServiziApplicativi()!=null &&
							pa.getAutorizzazioneToken().getServiziApplicativi().sizeServizioApplicativoList()>0) {
						for (int i = 0; i < pa.getAutorizzazioneToken().getServiziApplicativi().sizeServizioApplicativoList(); i++) {
							PortaApplicativaAutorizzazioneServizioApplicativo paSa = pa.getAutorizzazioneToken().getServiziApplicativi().getServizioApplicativo(i);
							if(paSa.getTipoSoggettoProprietario()!=null && paSa.getNomeSoggettoProprietario()!=null && paSa.getNome()!=null) {
								IDServizioApplicativo idServizioApplicativo = new IDServizioApplicativo();
								idServizioApplicativo.setNome(paSa.getNome());
								idServizioApplicativo.setIdSoggettoProprietario(new IDSoggetto(paSa.getTipoSoggettoProprietario(), paSa.getNomeSoggettoProprietario()));
								this.readServizioApplicativo(archive, idServizioApplicativo, cascadeConfig, false, ArchiveType.PORTA_APPLICATIVA); // per evitare loop
							}
						}
					}
					
					// ruoli token
					if(pa!=null && pa.getAutorizzazioneToken()!=null && pa.getAutorizzazioneToken().getRuoli()!=null && 
							pa.getAutorizzazioneToken().getRuoli().sizeRuoloList()>0){
						for (int i = 0; i < pa.getAutorizzazioneToken().getRuoli().sizeRuoloList(); i++) {
							Ruolo ruolo = pa.getAutorizzazioneToken().getRuoli().getRuolo(i);
							FiltroRicercaRuoli filtroRuolo = new FiltroRicercaRuoli();
							filtroRuolo.setNome(ruolo.getNome());
							try{
								List<IDRuolo> idsRuoli = this.archiveEngine.getAllIdRuoli(filtroRuolo);
								if(idsRuoli!=null && idsRuoli.size()>0){
									for (IDRuolo idRuolo : idsRuoli) {
										this.readRuolo(archive, idRuolo, cascadeConfig, false, ArchiveType.PORTA_APPLICATIVA); // per evitare loop
									}
								}
							}catch(DriverRegistroServiziNotFound notFound){}	
						}
					}
					
					// scope
					if(pa!=null && pa.getScope()!=null && 
							pa.getScope().sizeScopeList()>0){
						for (int i = 0; i < pa.getScope().sizeScopeList(); i++) {
							Scope scope = pa.getScope().getScope(i);
							FiltroRicercaScope filtroScope = new FiltroRicercaScope();
							filtroScope.setNome(scope.getNome());
							try{
								List<IDScope> idsScope = this.archiveEngine.getAllIdScope(filtroScope);
								if(idsScope!=null && idsScope.size()>0){
									for (IDScope idScope : idsScope) {
										this.readScope(archive, idScope, cascadeConfig, false, ArchiveType.PORTA_APPLICATIVA); // per evitare loop
									}
								}
							}catch(DriverRegistroServiziNotFound notFound){}	
						}
					}
					
					// token policy di validazione
					if(cascadeConfig.isCascadePolicyConfigurazione()) {
						if(pa!=null && pa.getGestioneToken()!=null && pa.getGestioneToken().getPolicy()!=null && !"".equals(pa.getGestioneToken().getPolicy())) {
							this.readTokenPolicy_validation(archive, pa.getGestioneToken().getPolicy(), cascadeConfig, provenienza);
						}
					}
					
					// attribute authority
					if(cascadeConfig.isCascadePolicyConfigurazione()) {
						if(pa!=null && pa.sizeAttributeAuthorityList()>0) {
							for (AttributeAuthority aa : pa.getAttributeAuthorityList()) {
								String nomeAA = aa.getNome();
								if(nomeAA!=null && !"".equals(nomeAA)) {
									this.readAttributeAuthority(archive, nomeAA, cascadeConfig, provenienza);
								}
							}
						}
					}
										
					// serviziApplicativi autorizzati
					if(pa!=null && pa.getServiziApplicativiAutorizzati()!=null && 
							pa.getServiziApplicativiAutorizzati().sizeServizioApplicativoList()>0) {
						for (int i = 0; i < pa.getServiziApplicativiAutorizzati().sizeServizioApplicativoList(); i++) {
							PortaApplicativaAutorizzazioneServizioApplicativo paSa = pa.getServiziApplicativiAutorizzati().getServizioApplicativo(i);
							if(paSa.getTipoSoggettoProprietario()!=null && paSa.getNomeSoggettoProprietario()!=null && paSa.getNome()!=null) {
								IDServizioApplicativo idServizioApplicativo = new IDServizioApplicativo();
								idServizioApplicativo.setNome(paSa.getNome());
								idServizioApplicativo.setIdSoggettoProprietario(new IDSoggetto(paSa.getTipoSoggettoProprietario(), paSa.getNomeSoggettoProprietario()));
								this.readServizioApplicativo(archive, idServizioApplicativo, cascadeConfig, false, ArchiveType.PORTA_APPLICATIVA); // per evitare loop
							}
						}
					}
					
					// soggetti autorizzati
					if(pa!=null && pa.getSoggetti()!=null && 
							pa.getSoggetti().sizeSoggettoList()>0) {
						for (int i = 0; i < pa.getSoggetti().sizeSoggettoList(); i++) {
							PortaApplicativaAutorizzazioneSoggetto paSoggetto = pa.getSoggetti().getSoggetto(i);
							if(paSoggetto.getTipo()!=null && paSoggetto.getNome()!=null) {
								IDSoggetto idSoggetto = new IDSoggetto(paSoggetto.getTipo(), paSoggetto.getNome());
								this.readSoggetto(archive, idSoggetto, cascadeConfig, false, ArchiveType.PORTA_APPLICATIVA); // per evitare loop
							}
						}
					}
					
					// trasformazioni
					if(pa!=null && pa.getTrasformazioni()!=null &&
							pa.getTrasformazioni().sizeRegolaList()>0) {
						for (int i = 0; i < pa.getTrasformazioni().sizeRegolaList(); i++) {
							TrasformazioneRegola regola = pa.getTrasformazioni().getRegola(i);
							if(regola.getApplicabilita()!=null) {
								
								if(regola.getApplicabilita().sizeServizioApplicativoList()>0) {
									for (int j = 0; j < regola.getApplicabilita().sizeServizioApplicativoList(); j++) {
										TrasformazioneRegolaApplicabilitaServizioApplicativo trSa = regola.getApplicabilita().getServizioApplicativo(j);
										if(trSa.getTipoSoggettoProprietario()!=null && trSa.getNomeSoggettoProprietario()!=null && trSa.getNome()!=null) {
											IDServizioApplicativo idServizioApplicativo = new IDServizioApplicativo();
											idServizioApplicativo.setNome(trSa.getNome());
											idServizioApplicativo.setIdSoggettoProprietario(new IDSoggetto(trSa.getTipoSoggettoProprietario(), trSa.getNomeSoggettoProprietario()));
											this.readServizioApplicativo(archive, idServizioApplicativo, cascadeConfig, false, ArchiveType.PORTA_APPLICATIVA); // per evitare loop
										}
									}
								}	
								
								if(regola.getApplicabilita().sizeSoggettoList()>0) {
									for (int j = 0; j < regola.getApplicabilita().sizeSoggettoList(); j++) {
										TrasformazioneRegolaApplicabilitaSoggetto trSoggetto = regola.getApplicabilita().getSoggetto(j);
										if(trSoggetto.getTipo()!=null && trSoggetto.getNome()!=null) {
											IDSoggetto idSoggetto = new IDSoggetto(trSoggetto.getTipo(), trSoggetto.getNome());
											this.readSoggetto(archive, idSoggetto, cascadeConfig, false, ArchiveType.PORTA_APPLICATIVA); // per evitare loop
										}
									}
								}
							}
						}
					}
					
					// aggiungo eventuale plugin configurato.
					if(cascadeConfig.isCascadePluginConfigurazione()) {
						
						if(pa!=null && StringUtils.isNotEmpty(pa.getAutenticazione())) {
							TipoAutenticazione tipo = TipoAutenticazione.toEnumConstant(pa.getAutenticazione(), false);
							if(tipo==null) {
								try {
									readPlugin_classe(archive, TipoPlugin.AUTENTICAZIONE.getValue(), pa.getAutenticazione(), cascadeConfig, provenienza);
								}catch(DriverConfigurazioneNotFound notFound) {}
							}
						}
						if(pa!=null && StringUtils.isNotEmpty(pa.getAutorizzazione())) {
							TipoAutorizzazione tipo = TipoAutorizzazione.toEnumConstant(pa.getAutorizzazione(), false);
							if(tipo==null) {
								try {
									readPlugin_classe(archive, TipoPlugin.AUTORIZZAZIONE.getValue(), pa.getAutorizzazione(), cascadeConfig, provenienza);
								}catch(DriverConfigurazioneNotFound notFound) {}
							}
						}
						if(pa!=null && StringUtils.isNotEmpty(pa.getAutorizzazioneContenuto())) {
							if(!CostantiConfigurazione.AUTORIZZAZIONE_CONTENUTO_BUILT_IN.equals(pa.getAutorizzazioneContenuto())) {
								try {
									readPlugin_classe(archive, TipoPlugin.AUTORIZZAZIONE_CONTENUTI.getValue(), pa.getAutorizzazione(), cascadeConfig, provenienza);
								}catch(DriverConfigurazioneNotFound notFound) {}
							}
						}
						if(pa!=null && pa.getBehaviour()!=null && pa.getBehaviour().getNome()!=null) {
							TipoBehaviour behaviourType = TipoBehaviour.toEnumConstant(pa.getBehaviour().getNome());
							if(TipoBehaviour.CUSTOM.equals(behaviourType)){
								try {
									readPlugin_classe(archive, TipoPlugin.BEHAVIOUR.getValue(), pa.getBehaviour().getNome(), cascadeConfig, provenienza);
								}catch(DriverConfigurazioneNotFound notFound) {}
							}
						}
						if(pa!=null && pa.getIntegrazione()!=null && StringUtils.isNotEmpty(pa.getIntegrazione())) {
							String[]tipi = pa.getIntegrazione().trim().split(",");
							if(tipi!=null && tipi.length>0) {
								for (String t : tipi) {
									if(this.archiveEngine.existsPluginClasse(TipoPlugin.INTEGRAZIONE.getValue(), t)){
										try {
											readPlugin_classe(archive, TipoPlugin.INTEGRAZIONE.getValue(), t, cascadeConfig, provenienza);
										}catch(DriverConfigurazioneNotFound notFound) {}
									}
								}
							}
						}
						if(pa!=null && pa.getConfigurazioneHandler()!=null) {
							if(pa.getConfigurazioneHandler().getRequest()!=null) {
								if(pa.getConfigurazioneHandler().getRequest().sizePreInList()>0) {
									for (ConfigurazioneHandler ch : pa.getConfigurazioneHandler().getRequest().getPreInList()) {
										if(this.archiveEngine.existsPluginClasse(TipoPlugin.MESSAGE_HANDLER.getValue(), ch.getTipo())){
											try {
												readPlugin_classe(archive, TipoPlugin.MESSAGE_HANDLER.getValue(), ch.getTipo(), cascadeConfig, provenienza);
											}catch(DriverConfigurazioneNotFound notFound) {}
										}
									}
								}
								if(pa.getConfigurazioneHandler().getRequest().sizeInList()>0) {
									for (ConfigurazioneHandler ch : pa.getConfigurazioneHandler().getRequest().getInList()) {
										if(this.archiveEngine.existsPluginClasse(TipoPlugin.MESSAGE_HANDLER.getValue(), ch.getTipo())){
											try {
												readPlugin_classe(archive, TipoPlugin.MESSAGE_HANDLER.getValue(), ch.getTipo(), cascadeConfig, provenienza);
											}catch(DriverConfigurazioneNotFound notFound) {}
										}
									}
								}
								if(pa.getConfigurazioneHandler().getRequest().sizeInProtocolInfoList()>0) {
									for (ConfigurazioneHandler ch : pa.getConfigurazioneHandler().getRequest().getInProtocolInfoList()) {
										if(this.archiveEngine.existsPluginClasse(TipoPlugin.MESSAGE_HANDLER.getValue(), ch.getTipo())){
											try {
												readPlugin_classe(archive, TipoPlugin.MESSAGE_HANDLER.getValue(), ch.getTipo(), cascadeConfig, provenienza);
											}catch(DriverConfigurazioneNotFound notFound) {}
										}
									}
								}
								if(pa.getConfigurazioneHandler().getRequest().sizeOutList()>0) {
									for (ConfigurazioneHandler ch : pa.getConfigurazioneHandler().getRequest().getOutList()) {
										if(this.archiveEngine.existsPluginClasse(TipoPlugin.MESSAGE_HANDLER.getValue(), ch.getTipo())){
											try {
												readPlugin_classe(archive, TipoPlugin.MESSAGE_HANDLER.getValue(), ch.getTipo(), cascadeConfig, provenienza);
											}catch(DriverConfigurazioneNotFound notFound) {}
										}
									}
								}
								if(pa.getConfigurazioneHandler().getRequest().sizePostOutList()>0) {
									for (ConfigurazioneHandler ch : pa.getConfigurazioneHandler().getRequest().getPostOutList()) {
										if(this.archiveEngine.existsPluginClasse(TipoPlugin.MESSAGE_HANDLER.getValue(), ch.getTipo())){
											try {
												readPlugin_classe(archive, TipoPlugin.MESSAGE_HANDLER.getValue(), ch.getTipo(), cascadeConfig, provenienza);
											}catch(DriverConfigurazioneNotFound notFound) {}
										}
									}
								}
							}
							if(pa.getConfigurazioneHandler().getResponse()!=null) {
								if(pa.getConfigurazioneHandler().getResponse().sizePreInList()>0) {
									for (ConfigurazioneHandler ch : pa.getConfigurazioneHandler().getResponse().getPreInList()) {
										if(this.archiveEngine.existsPluginClasse(TipoPlugin.MESSAGE_HANDLER.getValue(), ch.getTipo())){
											try {
												readPlugin_classe(archive, TipoPlugin.MESSAGE_HANDLER.getValue(), ch.getTipo(), cascadeConfig, provenienza);
											}catch(DriverConfigurazioneNotFound notFound) {}
										}
									}
								}
								if(pa.getConfigurazioneHandler().getResponse().sizeInList()>0) {
									for (ConfigurazioneHandler ch : pa.getConfigurazioneHandler().getResponse().getInList()) {
										if(this.archiveEngine.existsPluginClasse(TipoPlugin.MESSAGE_HANDLER.getValue(), ch.getTipo())){
											try {
												readPlugin_classe(archive, TipoPlugin.MESSAGE_HANDLER.getValue(), ch.getTipo(), cascadeConfig, provenienza);
											}catch(DriverConfigurazioneNotFound notFound) {}
										}
									}
								}
//								if(pa.getConfigurazioneHandler().getResponse().sizeInProtocolInfoList()>0) {
//									for (ConfigurazioneHandler ch : pa.getConfigurazioneHandler().getResponse().getInProtocolInfoList()) {
//										if(this.archiveEngine.existsPluginClasse(TipoPlugin.MESSAGE_HANDLER.getValue(), ch.getTipo())){
//											try {
//												readPlugin_classe(archive, TipoPlugin.MESSAGE_HANDLER.getValue(), ch.getTipo(), cascadeConfig, provenienza);
//											}catch(DriverConfigurazioneNotFound notFound) {}
//										}
//									}
//								}
								if(pa.getConfigurazioneHandler().getResponse().sizeOutList()>0) {
									for (ConfigurazioneHandler ch : pa.getConfigurazioneHandler().getResponse().getOutList()) {
										if(this.archiveEngine.existsPluginClasse(TipoPlugin.MESSAGE_HANDLER.getValue(), ch.getTipo())){
											try {
												readPlugin_classe(archive, TipoPlugin.MESSAGE_HANDLER.getValue(), ch.getTipo(), cascadeConfig, provenienza);
											}catch(DriverConfigurazioneNotFound notFound) {}
										}
									}
								}
								if(pa.getConfigurazioneHandler().getResponse().sizePostOutList()>0) {
									for (ConfigurazioneHandler ch : pa.getConfigurazioneHandler().getResponse().getPostOutList()) {
										if(this.archiveEngine.existsPluginClasse(TipoPlugin.MESSAGE_HANDLER.getValue(), ch.getTipo())){
											try {
												readPlugin_classe(archive, TipoPlugin.MESSAGE_HANDLER.getValue(), ch.getTipo(), cascadeConfig, provenienza);
											}catch(DriverConfigurazioneNotFound notFound) {}
										}
									}
								}
							}
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
			if(pa.getServizioApplicativoDefault()!=null && !"".equals(pa.getServizioApplicativoDefault())) {
				IDServizioApplicativo idSA = new IDServizioApplicativo();
				idSA.setIdSoggettoProprietario(idSoggettoErogatore);
				idSA.setNome(pa.getServizioApplicativoDefault());
				this.readServizioApplicativo(archive, idSA, cascadeConfig, ArchiveType.PORTA_APPLICATIVA);
			}
			
		}
		
		return pa;
	}
	
	
	private void readErogazione(Archive archive, IDServizio idServizio, ArchiveCascadeConfiguration cascadeConfig) throws Exception{
	
		this.readAccordoServizioParteSpecifica(archive, idServizio, cascadeConfig, ArchiveType.EROGAZIONE);
		
		// porteApplicative associate
		List<IDPortaApplicativa> listIDPA = this.archiveEngine.getIDPorteApplicativeAssociateErogazione(idServizio);
		if(listIDPA!=null && listIDPA.size()>0){
			for (IDPortaApplicativa idPortaApplicativa : listIDPA) {
				PortaApplicativa pa = this.readPortaApplicativa(archive, idPortaApplicativa, cascadeConfig, ArchiveType.PORTA_APPLICATIVA);
				if(pa!=null && pa.sizeServizioApplicativoList()>0) {
					for (PortaApplicativaServizioApplicativo pasa : pa.getServizioApplicativoList()) {
						IDServizioApplicativo idSA = new IDServizioApplicativo();
						idSA.setNome(pasa.getNome());
						idSA.setIdSoggettoProprietario(new IDSoggetto(pa.getTipoSoggettoProprietario(), pa.getNomeSoggettoProprietario()));
						ServizioApplicativo sa = this.archiveEngine.getServizioApplicativo(idSA);
						if(!CostantiConfigurazione.SERVER.equals(sa.getTipo()) && !CostantiConfigurazione.CLIENT_OR_SERVER.equals(sa.getTipo())) {
							// i server verranno inclusi solamente se viene scelto di includere tutti gli elementi riferiti
							this.readServizioApplicativo(archive, idSA, cascadeConfig, ArchiveType.SERVIZIO_APPLICATIVO);
						}
					}
				}
				if(pa!=null && pa.getServizioApplicativoDefault()!=null && !"".equals(pa.getServizioApplicativoDefault())) {
					IDServizioApplicativo idSA = new IDServizioApplicativo();
					idSA.setNome(pa.getServizioApplicativoDefault());
					idSA.setIdSoggettoProprietario(new IDSoggetto(pa.getTipoSoggettoProprietario(), pa.getNomeSoggettoProprietario()));
					ServizioApplicativo sa = this.archiveEngine.getServizioApplicativo(idSA);
					if(!CostantiConfigurazione.SERVER.equals(sa.getTipo()) && !CostantiConfigurazione.CLIENT_OR_SERVER.equals(sa.getTipo())) {
						// i server verranno inclusi solamente se viene scelto di includere tutti gli elementi riferiti
						this.readServizioApplicativo(archive, idSA, cascadeConfig, ArchiveType.SERVIZIO_APPLICATIVO);
					}
				}
			}
		}
		
	}
	
	private void readFruizione(Archive archive, IDFruizione idFruizione, ArchiveCascadeConfiguration cascadeConfig) throws Exception{
		
		this.readAccordoServizioParteSpecifica(archive, idFruizione.getIdServizio(), cascadeConfig, ArchiveType.FRUIZIONE);
		
		this.readFruitore(archive, idFruizione.getIdServizio(), idFruizione.getIdFruitore(), null, cascadeConfig, ArchiveType.FRUITORE);
		
		// porteDelegate associate
		List<IDPortaDelegata> listIDPD = this.archiveEngine.getIDPorteDelegateAssociateFruizione(idFruizione.getIdServizio(), idFruizione.getIdFruitore());
		if(listIDPD!=null && listIDPD.size()>0){
			for (IDPortaDelegata idPortaDelegata : listIDPD) {
				this.readPortaDelegata(archive, idPortaDelegata, cascadeConfig, ArchiveType.PORTA_DELEGATA);
			}
		}
		
	}
}
