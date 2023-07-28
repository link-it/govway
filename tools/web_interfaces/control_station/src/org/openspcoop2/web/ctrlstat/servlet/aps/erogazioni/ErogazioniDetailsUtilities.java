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

package org.openspcoop2.web.ctrlstat.servlet.aps.erogazioni;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.allarmi.constants.RuoloPorta;
import org.openspcoop2.core.config.AttributeAuthority;
import org.openspcoop2.core.config.CanaleConfigurazione;
import org.openspcoop2.core.config.CanaliConfigurazione;
import org.openspcoop2.core.config.ConfigurazionePortaHandler;
import org.openspcoop2.core.config.CorrelazioneApplicativa;
import org.openspcoop2.core.config.CorrelazioneApplicativaRisposta;
import org.openspcoop2.core.config.DumpConfigurazione;
import org.openspcoop2.core.config.GestioneToken;
import org.openspcoop2.core.config.GestioneTokenAutenticazione;
import org.openspcoop2.core.config.InvocazioneServizio;
import org.openspcoop2.core.config.MessageSecurity;
import org.openspcoop2.core.config.MtomProcessor;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaApplicativaAutorizzazioneServizioApplicativo;
import org.openspcoop2.core.config.PortaApplicativaAutorizzazioneSoggetto;
import org.openspcoop2.core.config.PortaApplicativaServizioApplicativo;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.PortaDelegataLocalForward;
import org.openspcoop2.core.config.PortaDelegataServizioApplicativo;
import org.openspcoop2.core.config.PortaTracciamento;
import org.openspcoop2.core.config.Proprieta;
import org.openspcoop2.core.config.ResponseCachingConfigurazione;
import org.openspcoop2.core.config.Ruolo;
import org.openspcoop2.core.config.Scope;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.TrasformazioneRegola;
import org.openspcoop2.core.config.Trasformazioni;
import org.openspcoop2.core.config.ValidazioneContenutiApplicativi;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.config.constants.MTOMProcessorType;
import org.openspcoop2.core.config.constants.RuoloContesto;
import org.openspcoop2.core.config.constants.RuoloTipoMatch;
import org.openspcoop2.core.config.constants.ScopeTipoMatch;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.config.constants.StatoFunzionalitaConWarning;
import org.openspcoop2.core.config.constants.TipoAutenticazione;
import org.openspcoop2.core.config.constants.TipoAutenticazionePrincipal;
import org.openspcoop2.core.config.constants.TipoAutorizzazione;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.constants.CostantiLabel;
import org.openspcoop2.core.constants.TipiConnettore;
import org.openspcoop2.core.controllo_traffico.AttivazionePolicy;
import org.openspcoop2.core.controllo_traffico.constants.RuoloPolicy;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.mapping.MappingErogazionePortaApplicativa;
import org.openspcoop2.core.mapping.MappingFruizionePortaDelegata;
import org.openspcoop2.core.mvc.properties.utils.ConfigManager;
import org.openspcoop2.core.mvc.properties.utils.PropertiesSourceConfiguration;
import org.openspcoop2.core.plugins.Plugin;
import org.openspcoop2.core.plugins.constants.TipoPlugin;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.ConfigurazioneServizioAzione;
import org.openspcoop2.core.registry.Connettore;
import org.openspcoop2.core.registry.Fruitore;
import org.openspcoop2.core.registry.Property;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.core.registry.beans.AccordoServizioParteComuneSintetico;
import org.openspcoop2.core.registry.beans.GruppoSintetico;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziNotFound;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.monitor.engine.alarm.wrapper.ConfigurazioneAllarmeBean;
import org.openspcoop2.pdd.config.UrlInvocazioneAPI;
import org.openspcoop2.pdd.core.autenticazione.ParametriAutenticazioneApiKey;
import org.openspcoop2.pdd.core.autorizzazione.CostantiAutorizzazione;
import org.openspcoop2.pdd.core.connettori.ConnettoreUtils;
import org.openspcoop2.pdd.logger.LogLevels;
import org.openspcoop2.protocol.engine.constants.Costanti;
import org.openspcoop2.protocol.utils.EsitiConfigUtils;
import org.openspcoop2.protocol.utils.ModIUtils;
import org.openspcoop2.utils.certificate.ocsp.OCSPManager;
import org.openspcoop2.utils.properties.PropertiesUtilities;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.ConsoleSearch;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.plugins.IExtendedListServlet;
import org.openspcoop2.web.ctrlstat.servlet.ConsoleHelper;
import org.openspcoop2.web.ctrlstat.servlet.apc.AccordiServizioParteComuneCore;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaCore;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaCostanti;
import org.openspcoop2.web.ctrlstat.servlet.config.ConfigurazioneCore;
import org.openspcoop2.web.ctrlstat.servlet.config.ConfigurazioneCostanti;
import org.openspcoop2.web.ctrlstat.servlet.gruppi.GruppiCostanti;
import org.openspcoop2.web.ctrlstat.servlet.pa.PorteApplicativeCore;
import org.openspcoop2.web.ctrlstat.servlet.pa.PorteApplicativeCostanti;
import org.openspcoop2.web.ctrlstat.servlet.pd.PorteDelegateCore;
import org.openspcoop2.web.ctrlstat.servlet.pd.PorteDelegateCostanti;
import org.openspcoop2.web.ctrlstat.servlet.pdd.PddCore;
import org.openspcoop2.web.ctrlstat.servlet.ruoli.RuoliCostanti;
import org.openspcoop2.web.ctrlstat.servlet.sa.ServiziApplicativiCore;
import org.openspcoop2.web.ctrlstat.servlet.scope.ScopeCostanti;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCore;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.ServletUtils;

/**
 * ErogazioniDetailsUtilities
 * 
 * @author Andrea Poli (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class ErogazioniDetailsUtilities {

	
	public static String getDetailsErogazione(IDServizio idServizio,
			SoggettiCore soggettiCore, ConsoleHelper consoleHelper) throws Exception {
		return _getDetails(idServizio, null,
				soggettiCore, consoleHelper, 
				null, null);
	}
	public static String getDetailsErogazione(IDServizio idServizio,
			SoggettiCore soggettiCore, ConsoleHelper consoleHelper,
			String separator, String newLine) throws Exception {
		return _getDetails(idServizio, null,
				soggettiCore, consoleHelper, 
				separator, newLine);
	}
	public static String getDetailsFruizione(IDServizio idServizio, IDSoggetto idSoggettoFruitore,
			SoggettiCore soggettiCore, ConsoleHelper consoleHelper) throws Exception {
		return _getDetails(idServizio, idSoggettoFruitore,
				soggettiCore, consoleHelper, 
				null, null);
	}
	public static String getDetailsFruizione(IDServizio idServizio, IDSoggetto idSoggettoFruitore,
			SoggettiCore soggettiCore, ConsoleHelper consoleHelper,
			String separator, String newLine) throws Exception {
		return _getDetails(idServizio, idSoggettoFruitore,
				soggettiCore, consoleHelper, 
				separator, newLine);
	}
	private static String _getDetails(IDServizio idServizio, IDSoggetto idSoggettoFruitore,
			SoggettiCore soggettiCore, ConsoleHelper consoleHelper, 
			String separator, String newLine) throws Exception {
		
		if(separator == null) {
			separator = ": ";
		}
		if(newLine == null) {
			newLine = "\n";
		}
		
		StringBuilder sb = new StringBuilder();
		
		boolean gestioneFruitori = idSoggettoFruitore!=null;
		boolean gestioneErogatori = !gestioneFruitori;
		
		PddCore pddCore = new PddCore(soggettiCore);
		AccordiServizioParteComuneCore apcCore = new AccordiServizioParteComuneCore(soggettiCore);
		AccordiServizioParteSpecificaCore apsCore = new AccordiServizioParteSpecificaCore(soggettiCore);
		ConfigurazioneCore confCore = new ConfigurazioneCore(soggettiCore);
		PorteApplicativeCore porteApplicativeCore = null;
		PorteDelegateCore porteDelegateCore = null;
		ServiziApplicativiCore saCore = null;
		if(gestioneErogatori) {
			porteApplicativeCore = new PorteApplicativeCore(soggettiCore);
			saCore = new ServiziApplicativiCore(soggettiCore);
		}
		else {
			porteDelegateCore = new PorteDelegateCore(soggettiCore);
		}
		
		boolean showSoggettoErogatoreInErogazioni = false;
		boolean showSoggettoFruitoreInFruizioni = false;
		if(gestioneErogatori) {
			showSoggettoErogatoreInErogazioni = soggettiCore.isMultitenant() && 
					!consoleHelper.isSoggettoMultitenantSelezionato();
		}
		else {
			showSoggettoFruitoreInFruizioni = gestioneFruitori &&  soggettiCore.isMultitenant() && 
					!consoleHelper.isSoggettoMultitenantSelezionato();
		}
		
		
		// *** inizializzo oggetti ***
		
		String protocollo = soggettiCore.getProtocolloAssociatoTipoSoggetto(idServizio.getSoggettoErogatore().getTipo());
		//org.openspcoop2.protocol.sdk.IProtocolFactory<?> protocolFactory = org.openspcoop2.protocol.engine.ProtocolFactoryManager.getInstance().getProtocolFactoryByName(protocollo);
		String labelProtocollo = consoleHelper.getLabelProtocollo(protocollo);
		boolean showProtocolli = consoleHelper.getSession()!=null ? (soggettiCore.countProtocolli(consoleHelper.getRequest(), consoleHelper.getSession())>1) : true;
		
		AccordoServizioParteSpecifica asps = apsCore.getServizio(idServizio);
		AccordoServizioParteComuneSintetico as = apcCore.getAccordoServizioSintetico(asps.getIdAccordo());
		AccordoServizioParteComune asFull = apcCore.getAccordoServizioFull(asps.getIdAccordo());
		ServiceBinding serviceBinding = apcCore.toMessageServiceBinding(as.getServiceBinding());
		
		idServizio.setPortType(asps.getPortType());
		idServizio.setUriAccordoServizioParteComune(asps.getAccordoServizioParteComune());
		
		Soggetto sog = soggettiCore.getSoggettoRegistro(asps.getIdSoggetto());
		boolean isPddEsterna = pddCore.isPddEsterna(sog.getPortaDominio());
		
		List<MappingErogazionePortaApplicativa> listaMappingErogazionePortaApplicativa = new ArrayList<>();
		List<PortaApplicativa> listaPorteApplicativeAssociate = new ArrayList<>();
		if(gestioneErogatori) {
			// lettura delle configurazioni associate
			listaMappingErogazionePortaApplicativa = apsCore.mappingServiziPorteAppList(idServizio,asps.getId(), null);
			for(MappingErogazionePortaApplicativa mappinErogazione : listaMappingErogazionePortaApplicativa) {
				listaPorteApplicativeAssociate.add(porteApplicativeCore.getPortaApplicativa(mappinErogazione.getIdPortaApplicativa()));
			}
		}

		List<MappingFruizionePortaDelegata> listaMappingFruzionePortaDelegata = new ArrayList<>();
		List<PortaDelegata> listaPorteDelegateAssociate = new ArrayList<>();
		Fruitore fruitore = null;
		if(gestioneFruitori) {
			// In questa modalità ci deve essere un fruitore indirizzato
			for (Fruitore check : asps.getFruitoreList()) {
				if(check.getTipo().equals(idSoggettoFruitore.getTipo()) && check.getNome().equals(idSoggettoFruitore.getNome())) {
					fruitore = check;
					break;
				}
			}
			if(fruitore!=null) {
				listaMappingFruzionePortaDelegata = apsCore.serviziFruitoriMappingList(fruitore.getId(), idSoggettoFruitore, idServizio, null);	
				for(MappingFruizionePortaDelegata mappingFruizione : listaMappingFruzionePortaDelegata) {
					listaPorteDelegateAssociate.add(porteDelegateCore.getPortaDelegata(mappingFruizione.getIdPortaDelegata()));
				}
			}
		}
		
		
		// *** maschera principale ***
		
		sb.append("=== Informazioni Generali ===");
		
		// Nome
		//String labelServizio = gestioneFruitori ? consoleHelper.getLabelIdServizioSenzaErogatore(idServizio) :  consoleHelper.getLabelIdServizioSenzaErogatore(idServizio);
		String labelServizio = consoleHelper.getLabelIdServizioSenzaErogatore(idServizio);
		String labelServizioConPortType = labelServizio;
		if(asps.getPortType()!=null && !"".equals(asps.getPortType()) && !asps.getNome().equals(asps.getPortType())) {
			labelServizioConPortType = labelServizioConPortType +" ("+asps.getPortType()+")";
		}
		sb.append(newLine);
		sb.append(ErogazioniCostanti.LABEL_ASPS_MODIFICA_SERVIZIO_NOME);
		sb.append(separator);
		sb.append(labelServizioConPortType);
		
		// Stato
		if(gestioneErogatori && listaMappingErogazionePortaApplicativa.size()==1) {
			PortaApplicativa pa = porteApplicativeCore.getPortaApplicativa(listaMappingErogazionePortaApplicativa.get(0).getIdPortaApplicativa());
			boolean statoPA = pa.getStato().equals(StatoFunzionalita.ABILITATO);
			String statoMapping = statoPA ? CostantiControlStation.LABEL_STATO_ABILITATO : CostantiControlStation.LABEL_STATO_DISABILITATO;
			sb.append(newLine);
			sb.append(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_STATO_PORTA);
			sb.append(separator);
			sb.append(statoMapping);
		}
		else if(gestioneFruitori && listaPorteDelegateAssociate.size()==1) {
			PortaDelegata pd = porteDelegateCore.getPortaDelegata(listaMappingFruzionePortaDelegata.get(0).getIdPortaDelegata());
			boolean statoPD = pd.getStato().equals(StatoFunzionalita.ABILITATO);
			String statoMapping = statoPD ? CostantiControlStation.LABEL_STATO_ABILITATO : CostantiControlStation.LABEL_STATO_DISABILITATO;
			sb.append(newLine);
			sb.append(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_STATO_PORTA);
			sb.append(separator);
			sb.append(statoMapping);
		}
		
		// soggetto erogatore
		if(gestioneFruitori || showSoggettoErogatoreInErogazioni) {
			String labelSoggetto = consoleHelper.getLabelNomeSoggetto(protocollo,asps.getTipoSoggettoErogatore(),asps.getNomeSoggettoErogatore());
			sb.append(newLine);
			sb.append(AccordiServizioParteSpecificaCostanti.LABEL_APS_SOGGETTO_EROGATORE);
			sb.append(separator);
			sb.append(labelSoggetto);
		}
		
		// API
		String labelAPI = consoleHelper.getLabelIdAccordo(as);
		String labelServiceBinding = null;
		switch (serviceBinding) {
		case REST:
			labelServiceBinding= CostantiControlStation.LABEL_PARAMETRO_SERVICE_BINDING_REST;
			break;
		case SOAP:
		default:
			labelServiceBinding= CostantiControlStation.LABEL_PARAMETRO_SERVICE_BINDING_SOAP;
			break;
		}
		sb.append(newLine);
		sb.append(AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_ACCORDO);
		sb.append(separator);
		sb.append(MessageFormat.format(ErogazioniCostanti.MESSAGE_METADATI_SERVIZIO_EROGAZIONI_EDIT, labelServiceBinding, labelAPI));
		
		// Tag
		List<GruppoSintetico> gruppi = as.getGruppo();
		if(gruppi != null && !gruppi.isEmpty()) {
			StringBuilder sbTags = new StringBuilder();
			for (int i = 0; i < gruppi.size(); i++) {
				GruppoSintetico gruppo = gruppi.get(i);
				if(sbTags.length()>0) {
					sbTags.append(",");
				}
				sbTags.append(gruppo.getNome());
			}
			sb.append(newLine);
			sb.append(GruppiCostanti.LABEL_GRUPPI);
			sb.append(separator);
			sb.append(sbTags.toString());
		}	
		
		// Profilo Interoperabilità
		if(showProtocolli) {
			sb.append(newLine);
			sb.append(AccordiServizioParteSpecificaCostanti.LABEL_PARAMETRO_APS_PROTOCOLLO);
			sb.append(separator);
			sb.append(labelProtocollo);
		}
		
		// Fruitore
		if(showSoggettoFruitoreInFruizioni) {
			sb.append(newLine);
			sb.append(AccordiServizioParteSpecificaCostanti.LABEL_APS_SOGGETTO_FRUITORE);
			sb.append(separator);
			sb.append(consoleHelper.getLabelNomeSoggetto(protocollo,fruitore.getTipo(),fruitore.getNome()));
		}
		
		boolean connettoreStatic = false;
		if(gestioneFruitori) {
			connettoreStatic = apsCore.isConnettoreStatic(protocollo);
		}
		
		String urlInvocazione = null;
		String urlConnettoreFruitoreModI = null;
		if(gestioneErogatori) {
			
			IDPortaApplicativa idPA = null;
			PortaApplicativa paDefault = null;
			String canalePorta = null;
			
			// Url Invocazione
			if(!isPddEsterna){
				idPA = porteApplicativeCore.getIDPortaApplicativaAssociataDefault(idServizio);
				paDefault = porteApplicativeCore.getPortaApplicativa(idPA);
				canalePorta = paDefault.getCanale();
				UrlInvocazioneAPI urlInvocazioneConfig = confCore.getConfigurazioneUrlInvocazione(protocollo, RuoloContesto.PORTA_APPLICATIVA, serviceBinding, paDefault.getNome(), 
						new IDSoggetto(paDefault.getTipoSoggettoProprietario(), paDefault.getNomeSoggettoProprietario()),
								as, paDefault.getCanale());
				urlInvocazione = urlInvocazioneConfig.getUrl();
			} else {
				urlInvocazione = "-";
			}
			sb.append(newLine);
			sb.append(PorteApplicativeCostanti.LABEL_PARAMETRO_TITOLO_PORTE_APPLICATIVE_DATI_INVOCAZIONE);
			sb.append(separator);
			sb.append(urlInvocazione);
						
			// CORS
			sb.append(newLine);
			sb.append(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_CORS);
			sb.append(separator);
			sb.append(consoleHelper.getStatoGestioneCorsPortaApplicativa(paDefault, false));
			
			// Canale
			for (int i = 0; i < listaPorteApplicativeAssociate.size(); i++) {
				PortaApplicativa paAssociata = listaPorteApplicativeAssociate.get(i);
				MappingErogazionePortaApplicativa mapping = listaMappingErogazionePortaApplicativa.get(i);
				if(mapping.isDefault()) {
					canalePorta = paAssociata.getCanale();
					break;
				}
			}
			CanaliConfigurazione gestioneCanali = confCore.getCanaliConfigurazione(false);
			boolean gestioneCanaliEnabled = gestioneCanali != null && org.openspcoop2.core.config.constants.StatoFunzionalita.ABILITATO.equals(gestioneCanali.getStato());
			if(gestioneCanaliEnabled) {
				sb.append(newLine);
				sb.append(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_CANALE);
				sb.append(separator);
				String canaleNome = canalePorta;
				if(canaleNome == null) { // default
					String canaleAPINome = as.getCanale();
					if(canaleAPINome == null) { // default sistema
						List<CanaleConfigurazione> canaleList = gestioneCanali != null ? gestioneCanali.getCanaleList() : new ArrayList<>();
						CanaleConfigurazione canaleConfigurazioneDefault = consoleHelper.getCanaleDefault(canaleList);
						canaleNome =  canaleConfigurazioneDefault.getNome();
					}
					else { // default API
						canaleNome = canaleAPINome;
					}
				}
				sb.append(canaleNome);
			}
			
			// Opzioni Avanzate
			if(!consoleHelper.isModalitaStandard() && apsCore.getMessageEngines()!=null && !apsCore.getMessageEngines().isEmpty()) {
				sb.append(newLine);
				sb.append(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_OPZIONI_AVANZATE);
				sb.append(separator);
				sb.append(consoleHelper.getStatoOpzioniAvanzatePortaApplicativaDefault(paDefault));
			}
			
			
			// Connettore
			// Quello di default si visualizza sempre in questa posizione.
			// Eventuali altri connettori redefiniti, vengono visualizzati dopo la configurazione del gruppo
			
			sb.append(newLine);
			sb.append(newLine);
			sb.append("=== Connettore ===");
			
			printConnettoreErogazione(paDefault, paDefault, 
					saCore, consoleHelper, 
					sb,
					separator, newLine);
			
		}
		else {
			
			IDPortaDelegata idPD = porteDelegateCore.getIDPortaDelegataAssociataDefault(idServizio, idSoggettoFruitore);
			PortaDelegata pdDefault = porteDelegateCore.getPortaDelegata(idPD);
			String canalePorta = pdDefault.getCanale();
						
			// Url Invocazione
			UrlInvocazioneAPI urlInvocazioneConf = confCore.getConfigurazioneUrlInvocazione(protocollo, RuoloContesto.PORTA_DELEGATA, serviceBinding, pdDefault.getNome(), idSoggettoFruitore,
					as, pdDefault.getCanale());
			urlInvocazione = urlInvocazioneConf.getUrl();
			sb.append(newLine);
			sb.append(PorteDelegateCostanti.LABEL_PARAMETRO_TITOLO_PORTE_DELEGATE_DATI_INVOCAZIONE);
			sb.append(separator);
			sb.append(urlInvocazione);
						
			// CORS
			sb.append(newLine);
			sb.append(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_CORS);
			sb.append(separator);
			sb.append(consoleHelper.getStatoGestioneCorsPortaDelegata(pdDefault, false));
			
			// Canale
			CanaliConfigurazione gestioneCanali = confCore.getCanaliConfigurazione(false);
			boolean gestioneCanaliEnabled = gestioneCanali != null && org.openspcoop2.core.config.constants.StatoFunzionalita.ABILITATO.equals(gestioneCanali.getStato());
			if(gestioneCanaliEnabled) {
				sb.append(newLine);
				sb.append(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_CANALE);
				sb.append(separator);
				String canaleNome = canalePorta;
				if(canaleNome == null) { // default
					String canaleAPINome = as.getCanale();
					if(canaleAPINome == null) { // default sistema
						List<CanaleConfigurazione> canaleList = gestioneCanali != null ? gestioneCanali.getCanaleList() : new ArrayList<>();
						CanaleConfigurazione canaleConfigurazioneDefault = canaleList.stream().filter((c) -> c.isCanaleDefault()).findFirst().get();
						canaleNome =  canaleConfigurazioneDefault.getNome();
					}
					else { // default API
						canaleNome = canaleAPINome;
					}
				}
				sb.append(canaleNome);
			}
			
			// Opzioni Avanzate
			if(!consoleHelper.isModalitaStandard() && apsCore.getMessageEngines()!=null && !apsCore.getMessageEngines().isEmpty()) {
				sb.append(newLine);
				sb.append(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_OPZIONI_AVANZATE);
				sb.append(separator);
				sb.append(consoleHelper.getStatoOpzioniAvanzatePortaDelegataDefault(pdDefault));
			}
			
			// Connettore
			// Quello di default si visualizza sempre in questa posizione.
			// Eventuali altri connettori redefiniti, vengono visualizzati dopo la configurazione del gruppo
			
			org.openspcoop2.core.registry.Connettore connettore = null;
			if(fruitore!=null) {
				connettore = fruitore.getConnettore();
			}
			if(connettore!=null && connettore.sizePropertyList()>0) {
				for (Property p : connettore.getPropertyList()) {
					if(CostantiDB.CONNETTORE_HTTP_LOCATION.equals(p.getNome())) {
						urlConnettoreFruitoreModI = p.getValore();
					}
				}
			}
			
			sb.append(newLine);
			sb.append(newLine);
			sb.append("=== Connettore ===");
			
			printConnettoreFruizione(consoleHelper,
					connettore, null,
					connettoreStatic,
					sb,
					separator, newLine);
			
		}
		
		
		
		// Configurazioni (+ connetori ridefiniti)
		
		if(gestioneErogatori) {
			PortaApplicativa paDefault = null;
			PortaApplicativaServizioApplicativo paSADefault=null;
			for (int i = 0; i < listaPorteApplicativeAssociate.size(); i++) {
				MappingErogazionePortaApplicativa mapping = listaMappingErogazionePortaApplicativa.get(i);
				if(mapping.isDefault()) {
					paDefault = listaPorteApplicativeAssociate.get(i);
					paSADefault = paDefault.getServizioApplicativoList().get(0);
					break;
				}
			}
			for (int i = 0; i < listaPorteApplicativeAssociate.size(); i++) {
							
				PortaApplicativa paAssociata = listaPorteApplicativeAssociate.get(i);
				MappingErogazionePortaApplicativa mapping = listaMappingErogazionePortaApplicativa.get(i);
				
				sb.append(newLine);
				sb.append(newLine);
				String labelGruppo = null;
				if(listaPorteApplicativeAssociate.size()==1) {
					sb.append("=== Configurazione ===");
				}
				else {
					labelGruppo = mapping.getDescrizione();
					if(mapping.isDefault()) {
						if(labelGruppo==null || StringUtils.isEmpty(labelGruppo)) {
							labelGruppo = org.openspcoop2.core.constants.Costanti.MAPPING_DESCRIZIONE_DEFAULT;
						}
					}
					sb.append("=== Configurazione Gruppo '"+labelGruppo+"' ===");
				}
				
				printConfigurazioneControlloAccessiErogazione(consoleHelper, soggettiCore, protocollo,
						paAssociata,
						sb,
						separator, newLine);
				
				printConfigurazioneRateLimiting(consoleHelper, soggettiCore, 
						false, paAssociata.getNome(),
						sb,
						separator, newLine);
				
				printConfigurazioneValidazioneContenutiApplicativi(consoleHelper, soggettiCore, 
						asFull, paAssociata.getValidazioneContenutiApplicativi(),
						sb,
						separator, newLine);
				
				printConfigurazioneResponseCaching(consoleHelper, soggettiCore, 
						paAssociata.getResponseCaching(),
						sb,
						separator, newLine);
				
				printConfigurazioneMessageSecurity(consoleHelper, soggettiCore, 
						paAssociata.getMessageSecurity(),
						sb,
						separator, newLine);
				
				printConfigurazioneMTOM(consoleHelper, soggettiCore, 
						paAssociata.getMtomProcessor(),
						sb,
						separator, newLine);
				
				printConfigurazioneTrasformazioni(consoleHelper, soggettiCore, 
						asFull, paAssociata.getTrasformazioni(),
						sb,
						separator, newLine);
				
				printConfigurazioneTracciamento(consoleHelper, soggettiCore, 
						paAssociata.getCorrelazioneApplicativa(),
						paAssociata.getCorrelazioneApplicativaRisposta(),
						paAssociata.getTracciamento(),
						sb,
						separator, newLine);
				
				printConfigurazioneRegistrazioneMessaggi(consoleHelper, soggettiCore, 
						paAssociata.getDump(),
						sb,
						separator, newLine);
				
				printConfigurazioneAllarmi(consoleHelper, soggettiCore, 
						false, paAssociata.getNome(),
						sb,
						separator, newLine);
				
				printConfigurazioneProperties(consoleHelper, soggettiCore, 
						paAssociata.getProprietaList(),
						sb,
						separator, newLine);
				
				printConfigurazionePortaApplicativaExtended(consoleHelper, soggettiCore, 
						protocollo, paAssociata, mapping,
						sb,
						separator, newLine);
				
				String behaviour = (!soggettiCore.isConnettoriMultipliEnabled() && paAssociata.getBehaviour()!=null) ? paAssociata.getBehaviour().getNome() : null;
				printConfigurazioneAltro(consoleHelper, soggettiCore, 
						protocollo, asFull, 
						paAssociata.getAllegaBody(), paAssociata.getScartaBody(), 
						paAssociata.getIntegrazione(), behaviour, 
						paAssociata.getProprietaRateLimitingList(),
						paAssociata.getStateless(), null, 
						paAssociata.getRicevutaAsincronaSimmetrica(), paAssociata.getRicevutaAsincronaAsimmetrica(),
						paAssociata.getGestioneManifest(), paAssociata.getConfigurazioneHandler(),
						sb,
						separator, newLine);
				
				PortaApplicativaServizioApplicativo portaApplicativaAssociataServizioApplicativo = paAssociata.getServizioApplicativoList().get(0);
				boolean connettoreConfigurazioneRidefinito = consoleHelper.isConnettoreRidefinito(paDefault, paSADefault, paAssociata, portaApplicativaAssociataServizioApplicativo, paAssociata.getServizioApplicativoList());
				if(!mapping.isDefault() && connettoreConfigurazioneRidefinito) {
				
					sb.append(newLine);
					sb.append(newLine);
					sb.append("=== Connettore Gruppo '"+labelGruppo+"' ===");
					
					printConnettoreErogazione(paDefault, paAssociata, 
							saCore, consoleHelper, 
							sb,
							separator, newLine);
				}
			}
		}
		else {
			for (int i = 0; i < listaPorteDelegateAssociate.size(); i++) {
				PortaDelegata pdAssociata = listaPorteDelegateAssociate.get(i);
				MappingFruizionePortaDelegata mapping = listaMappingFruzionePortaDelegata.get(i);
				
				sb.append(newLine);
				sb.append(newLine);
				String labelGruppo = null;
				if(listaPorteDelegateAssociate.size()==1) {
					sb.append("=== Configurazione ===");
				}
				else {
					labelGruppo = mapping.getDescrizione();
					if(mapping.isDefault()) {
						if(labelGruppo==null || StringUtils.isEmpty(labelGruppo)) {
							labelGruppo = org.openspcoop2.core.constants.Costanti.MAPPING_DESCRIZIONE_DEFAULT;
						}
					}
					sb.append("=== Configurazione Gruppo '"+labelGruppo+"' ===");
				}
				
				printConfigurazioneControlloAccessiFruizione(consoleHelper, soggettiCore, protocollo,
						pdAssociata,
						sb,
						separator, newLine);
				
				printConfigurazioneRateLimiting(consoleHelper, soggettiCore, 
						true, pdAssociata.getNome(),
						sb,
						separator, newLine);
				
				printConfigurazioneValidazioneContenutiApplicativi(consoleHelper, soggettiCore, 
						asFull, pdAssociata.getValidazioneContenutiApplicativi(),
						sb,
						separator, newLine);
				
				printConfigurazioneResponseCaching(consoleHelper, soggettiCore, 
						pdAssociata.getResponseCaching(),
						sb,
						separator, newLine);
				
				printConfigurazioneMessageSecurity(consoleHelper, soggettiCore, 
						pdAssociata.getMessageSecurity(),
						sb,
						separator, newLine);
				
				printConfigurazioneMTOM(consoleHelper, soggettiCore, 
						pdAssociata.getMtomProcessor(),
						sb,
						separator, newLine);
				
				printConfigurazioneTrasformazioni(consoleHelper, soggettiCore, 
						asFull, pdAssociata.getTrasformazioni(),
						sb,
						separator, newLine);
				
				printConfigurazioneTracciamento(consoleHelper, soggettiCore, 
						pdAssociata.getCorrelazioneApplicativa(),
						pdAssociata.getCorrelazioneApplicativaRisposta(),
						pdAssociata.getTracciamento(),
						sb,
						separator, newLine);
				
				printConfigurazioneRegistrazioneMessaggi(consoleHelper, soggettiCore, 
						pdAssociata.getDump(),
						sb,
						separator, newLine);
				
				printConfigurazioneAllarmi(consoleHelper, soggettiCore, 
						true, pdAssociata.getNome(),
						sb,
						separator, newLine);
				
				printConfigurazioneProperties(consoleHelper, soggettiCore, 
						pdAssociata.getProprietaList(),
						sb,
						separator, newLine);

				printConfigurazioneAltro(consoleHelper, soggettiCore, 
						protocollo, asFull, 
						pdAssociata.getAllegaBody(), pdAssociata.getScartaBody(), 
						pdAssociata.getIntegrazione(), null,  
						pdAssociata.getProprietaRateLimitingList(),
						pdAssociata.getStateless(), pdAssociata.getLocalForward(), 
						pdAssociata.getRicevutaAsincronaSimmetrica(), pdAssociata.getRicevutaAsincronaAsimmetrica(),
						pdAssociata.getGestioneManifest(), pdAssociata.getConfigurazioneHandler(),
						sb,
						separator, newLine);
				
				printConfigurazionePortaDelegataExtended(consoleHelper, soggettiCore, 
						protocollo, pdAssociata, mapping,
						sb,
						separator, newLine);
				
				boolean connettoreConfigurazioneRidefinito = false;
				Connettore connettore = fruitore.getConnettore();
				if(!mapping.isDefault()) {
					List<String> listaAzioni = pdAssociata.getAzione().getAzioneDelegataList();
					
					String azioneConnettore =  null;
					if(listaAzioni!=null && listaAzioni.size()>0) {
						azioneConnettore = listaAzioni.get(0);
					}
					
					if(azioneConnettore!=null && !"".equals(azioneConnettore)) {
						for (ConfigurazioneServizioAzione check : fruitore.getConfigurazioneAzioneList()) {
							if(check.getAzioneList().contains(azioneConnettore)) {
								connettore = check.getConnettore();
								connettoreConfigurazioneRidefinito = true;
								break;
							}
						}
					}
				}
				if(!mapping.isDefault() && connettoreConfigurazioneRidefinito) {
					
					sb.append(newLine);
					sb.append(newLine);
					sb.append("=== Connettore Gruppo '"+labelGruppo+"' ===");
					
					printConnettoreFruizione(consoleHelper,
							connettore, labelGruppo,
							connettoreStatic,
							sb,
							separator, newLine);
							
				}
			}
		}
		
		
		// Altra sezione dedicata a ModI
		/**org.openspcoop2.protocol.sdk.properties.IConsoleDynamicConfiguration consoleDynamicConfiguration = protocolFactory.createDynamicConfigurationConsole();
		org.openspcoop2.protocol.sdk.registry.IRegistryReader registryReader = apcCore.getRegistryReader(protocolFactory); 
		org.openspcoop2.protocol.sdk.registry.IConfigIntegrationReader configRegistryReader = apcCore.getConfigIntegrationReader(protocolFactory);
		org.openspcoop2.protocol.sdk.properties.ConsoleConfiguration consoleConfiguration = null;
		if(gestioneErogatori) {
			consoleConfiguration = consoleDynamicConfiguration.getDynamicConfigAccordoServizioParteSpecifica(org.openspcoop2.protocol.sdk.constants.ConsoleOperationType.CHANGE, consoleHelper, 
					registryReader, configRegistryReader, idServizio );
		}
		else {
			org.openspcoop2.core.id.IDFruizione idFruizione = new org.openspcoop2.core.id.IDFruizione();
			idFruizione.setIdServizio(idServizio);
			idFruizione.setIdFruitore(idSoggettoFruitore);
			consoleConfiguration = consoleDynamicConfiguration.getDynamicConfigFruizioneAccordoServizioParteSpecifica(org.openspcoop2.protocol.sdk.constants.ConsoleOperationType.CHANGE, consoleHelper,  
					registryReader, configRegistryReader, idFruizione);
		}
		boolean modificaDatiProfilo = false;
		if(consoleConfiguration!=null && consoleConfiguration.getConsoleItem()!=null && !consoleConfiguration.getConsoleItem().isEmpty()) {
			modificaDatiProfilo = true;
		}*/
		boolean modificaDatiProfilo = true; // va sempre controllato, anche solo per visualizzare la sicurezza canale
		if(modificaDatiProfilo && Costanti.MODIPA_PROTOCOL_NAME.equals(protocollo)) {
			
			Map<String, String> map = ModIUtils.configToMap(asFull, asps, urlInvocazione, fruitore, urlConnettoreFruitoreModI);
			
			boolean rest = org.openspcoop2.core.registry.constants.ServiceBinding.REST.equals(as.getServiceBinding());
						
			boolean digest = "true".equals(map.get(ModIUtils.INTEGRITY));
			boolean corniceSicurezza = "true".equals(map.get(ModIUtils.CORNICE_SICUREZZA));
			String patternDatiCorniceSicurezza = null;
			String schemaDatiCorniceSicurezza = null;
			if(corniceSicurezza) {
				patternDatiCorniceSicurezza = map.get(ModIUtils.CORNICE_SICUREZZA_PATTERN);
				schemaDatiCorniceSicurezza = map.get(ModIUtils.CORNICE_SICUREZZA_SCHEMA);
			}
			boolean headerDuplicati = "true".equals(map.get(ModIUtils.HEADER_DUPLICATI));
			
			sb.append(newLine);
			sb.append(newLine);
			sb.append("=== "+labelProtocollo+" ===");
			
			// sicurezza canale
			sb.append(newLine);
			sb.append(CostantiLabel.MODIPA_API_PROFILO_CANALE_LABEL);
			sb.append(separator);
			String v = map.get(ModIUtils.API_SICUREZZA_CANALE_PATTERN);
			if(StringUtils.isNotEmpty(v)) {
				sb.append(v);
			}
			else {
				sb.append("-");
			}
			
			// sicurezza messaggio
			boolean sicurezzaMessaggio = false;
			sb.append(newLine);
			sb.append(CostantiLabel.MODIPA_API_PROFILO_SICUREZZA_MESSAGGIO_LABEL);
			sb.append(separator);
			v = map.get(ModIUtils.API_SICUREZZA_MESSAGGIO_PATTERN);
			if(StringUtils.isNotEmpty(v)) {
				sb.append(v);
				sicurezzaMessaggio = true;
			}
			else {
				sb.append("-");
			}
						
			if(sicurezzaMessaggio) {
				
				// Sorgente Token
				v = map.get(ModIUtils.API_SICUREZZA_MESSAGGIO_SORGENTE_TOKEN_ID_AUTH);
				if(StringUtils.isNotEmpty(v)) {
					sb.append(newLine);
					sb.append(CostantiLabel.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SORGENTE_TOKEN_IDAUTH);
					sb.append(separator);
					sb.append(v);
				}
				
				if(rest) {
					
					// Header
					v = map.get(ModIUtils.API_SICUREZZA_MESSAGGIO_HTTP_HEADER);
					if(StringUtils.isNotEmpty(v)) {
						sb.append(newLine);
						sb.append(CostantiLabel.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_LABEL);
						sb.append(separator);
						sb.append(v);
					}
					
				}
				
				// Applicabilita
				v = map.get(ModIUtils.API_SICUREZZA_MESSAGGIO_APPLICABILITA);
				if(StringUtils.isNotEmpty(v)) {
					sb.append(newLine);
					sb.append(CostantiLabel.MODIPA_API_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_LABEL);
					sb.append(separator);
					sb.append(v);
				}
				
				// Request Digest
				sb.append(newLine);
				sb.append(CostantiLabel.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REQUEST_DIGEST_LABEL);
				sb.append(separator);
				sb.append(map.get(ModIUtils.API_SICUREZZA_MESSAGGIO_REQUEST_DIGEST));
				
				// CorniceSicurezza
				sb.append(newLine);
				sb.append(CostantiLabel.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_LABEL);
				sb.append(separator);
				sb.append(map.get(ModIUtils.API_SICUREZZA_MESSAGGIO_USER_INFO));
								
				boolean request = true;
				addProfiloModISicurezza(sb,
						map,
						labelProtocollo, rest, gestioneFruitori, request, 
						digest, 
						patternDatiCorniceSicurezza, schemaDatiCorniceSicurezza, 
						headerDuplicati,
						separator, newLine);
				addProfiloModISicurezza(sb, 
						map,
						labelProtocollo, rest, gestioneFruitori, !request, 
						digest, 
						patternDatiCorniceSicurezza, schemaDatiCorniceSicurezza, 
						headerDuplicati,
						separator, newLine);
			}
			else {
				
				// Sicurezza OAuth
				
				if( gestioneFruitori ) {
				
					v = map.get(ModIUtils.API_IMPL_SICUREZZA_OAUTH_IDENTIFICATIVO);
					if(StringUtils.isNotEmpty(v)) {
						sb.append(newLine);
						/**sb.append(CostantiLabel.LABEL_CREDENZIALI_AUTENTICAZIONE_TOKEN_CLIENT_ID);*/
						sb.append(CostantiLabel.LABEL_CREDENZIALI_AUTENTICAZIONE_TOKEN_CLIENT_ID_SEARCH);
						sb.append(separator);
						sb.append(v);
					}
					
					v = map.get(ModIUtils.API_IMPL_SICUREZZA_OAUTH_KID);
					if(StringUtils.isNotEmpty(v)) {
						sb.append(newLine);
						sb.append(CostantiLabel.LABEL_CREDENZIALI_AUTENTICAZIONE_TOKEN_KID);
						sb.append(separator);
						sb.append(v);
					}
					
					addStore(sb, map, 
							"", false, false,
							separator, newLine);
				}
				
			}
		}

		return sb.toString();
	}
	
	
	private static void printConfigurazioneControlloAccessiErogazione(ConsoleHelper consoleHelper, SoggettiCore soggettiCore, String protocollo,
			PortaApplicativa paAssociata,
			StringBuilder sb,
			String separator, String newLine) throws DriverRegistroServiziNotFound, DriverRegistroServiziException {
		// Controllo Accessi
		
		List<IDSoggetto> authzSoggetti = null;
		List<IDServizioApplicativo> authzApplicativi = null;
		List<String> authzRuoli = null;
		boolean allRuoli = false;
		boolean authzApplicativiTokenEnabled = false;
		List<IDServizioApplicativo> authzApplicativiToken = null; 
		boolean authzRuoliTokenEnabled = false;
		List<String> authzRuoliToken = null;
		boolean allRuoliToken = false; 
		boolean authzScopeTokenEnabled = false;
		List<String> authzScope = null;
		boolean allScope = false;
		List<String> authzTokenClaims = null;
		if(paAssociata.getSoggetti()!=null && paAssociata.getSoggetti().sizeSoggettoList()>0) {
			authzSoggetti = new ArrayList<>();
			for (PortaApplicativaAutorizzazioneSoggetto soggetto : paAssociata.getSoggetti().getSoggettoList()) {
				authzSoggetti.add(new IDSoggetto(soggetto.getTipo(), soggetto.getNome()));
			}
		}
		if(paAssociata.getServiziApplicativiAutorizzati()!=null && paAssociata.getServiziApplicativiAutorizzati().sizeServizioApplicativoList()>0) {
			authzApplicativi = new ArrayList<>();
			for (PortaApplicativaAutorizzazioneServizioApplicativo sa : paAssociata.getServiziApplicativiAutorizzati().getServizioApplicativoList()) {
				IDServizioApplicativo idSA = new IDServizioApplicativo();
				idSA.setIdSoggettoProprietario(new IDSoggetto(sa.getTipoSoggettoProprietario(), sa.getNomeSoggettoProprietario()));
				idSA.setNome(sa.getNome());
				authzApplicativi.add(idSA);
			}
		}
		if(paAssociata.getRuoli()!=null) {
			allRuoli = paAssociata.getRuoli().getMatch()==null || RuoloTipoMatch.ALL.equals(paAssociata.getRuoli().getMatch());
		}
		if(paAssociata.getRuoli()!=null && paAssociata.getRuoli().sizeRuoloList()>0) {
			authzRuoli = new ArrayList<>();
			for (Ruolo ruolo: paAssociata.getRuoli().getRuoloList()) {
				authzRuoli.add(ruolo.getNome());
			}
		}
		if(paAssociata.getAutorizzazioneToken()!=null) {
			authzApplicativiTokenEnabled = StatoFunzionalita.ABILITATO.equals(paAssociata.getAutorizzazioneToken().getAutorizzazioneApplicativi());
			if(authzApplicativiTokenEnabled) {
				if(paAssociata.getAutorizzazioneToken().getServiziApplicativi()!=null && paAssociata.getAutorizzazioneToken().getServiziApplicativi().sizeServizioApplicativoList()>0) {
					authzApplicativiToken = new ArrayList<>();
					for (PortaApplicativaAutorizzazioneServizioApplicativo sa : paAssociata.getAutorizzazioneToken().getServiziApplicativi().getServizioApplicativoList()) {
						IDServizioApplicativo idSA = new IDServizioApplicativo();
						idSA.setIdSoggettoProprietario(new IDSoggetto(sa.getTipoSoggettoProprietario(), sa.getNomeSoggettoProprietario()));
						idSA.setNome(sa.getNome());
						authzApplicativiToken.add(idSA);
					}
				}
			}
			authzRuoliTokenEnabled = StatoFunzionalita.ABILITATO.equals(paAssociata.getAutorizzazioneToken().getAutorizzazioneRuoli());
			if(authzRuoliTokenEnabled) {
				if(paAssociata.getAutorizzazioneToken().getRuoli()!=null) {
					allRuoliToken = paAssociata.getAutorizzazioneToken().getRuoli().getMatch()==null || RuoloTipoMatch.ALL.equals(paAssociata.getAutorizzazioneToken().getRuoli().getMatch());
				}
				if(paAssociata.getAutorizzazioneToken().getRuoli()!=null && paAssociata.getAutorizzazioneToken().getRuoli().sizeRuoloList()>0) {
					authzRuoliToken = new ArrayList<>();
					for (Ruolo ruolo: paAssociata.getAutorizzazioneToken().getRuoli().getRuoloList()) {
						authzRuoliToken.add(ruolo.getNome());
					}
				}
			}
		}
		if(paAssociata.getScope()!=null) {
			authzScopeTokenEnabled = StatoFunzionalita.ABILITATO.equals(paAssociata.getScope().getStato());
			if(authzScopeTokenEnabled) {
				allScope = paAssociata.getScope().getMatch()==null || ScopeTipoMatch.ALL.equals(paAssociata.getScope().getMatch());
				if(paAssociata.getScope().sizeScopeList()>0) {
					authzScope = new ArrayList<>();
					for (Scope scope: paAssociata.getScope().getScopeList()) {
						authzScope.add(scope.getNome());
					}
				}
			}
		}
		if(paAssociata.getGestioneToken()!=null && paAssociata.getGestioneToken().getOptions()!=null) {
			Properties properties = PropertiesUtilities.convertTextToProperties(paAssociata.getGestioneToken().getOptions());
			if(properties!=null && properties.size()>0) {
				authzTokenClaims = new ArrayList<>();
				for (Object key : properties.keySet()) {
					if(key instanceof String) {
						authzTokenClaims.add((String)key);
					}
				}
				Collections.sort(authzTokenClaims);
			}
		}
		
		printConfigurazioneControlloAccessi(consoleHelper, soggettiCore, false,
				protocollo,
				paAssociata.getGestioneToken(),
				paAssociata.getAutenticazione(), paAssociata.getAutenticazioneOpzionale(), paAssociata.getProprietaAutenticazioneList(),
				paAssociata.getAttributeAuthorityList(),
				paAssociata.getAutorizzazione(), authzSoggetti, authzApplicativi, 
				authzRuoli, allRuoli,
				authzApplicativiTokenEnabled, authzApplicativiToken, 
				authzRuoliTokenEnabled, authzRuoliToken, allRuoliToken, 
				authzScopeTokenEnabled, authzScope, allScope,
				authzTokenClaims,
				paAssociata.getAutorizzazioneContenuto(),
				sb,
				separator, newLine);
	}
	
	private static void printConfigurazioneControlloAccessiFruizione(ConsoleHelper consoleHelper, SoggettiCore soggettiCore, String protocollo,
			PortaDelegata pdAssociata,
			StringBuilder sb,
			String separator, String newLine) {
		// Controllo Accessi
		List<IDSoggetto> authzSoggetti = null;
		List<IDServizioApplicativo> authzApplicativi = null;
		List<String> authzRuoli = null;
		boolean allRuoli = false;
		boolean authzApplicativiTokenEnabled = false;
		List<IDServizioApplicativo> authzApplicativiToken = null; 
		boolean authzRuoliTokenEnabled = false;
		List<String> authzRuoliToken = null;
		boolean allRuoliToken = false; 
		boolean authzScopeTokenEnabled = false;
		List<String> authzScope = null;
		boolean allScope = false;
		List<String> authzTokenClaims = null;
		if(pdAssociata.getServizioApplicativoList()!=null && pdAssociata.sizeServizioApplicativoList()>0) {
			authzApplicativi = new ArrayList<IDServizioApplicativo>();
			for (PortaDelegataServizioApplicativo sa : pdAssociata.getServizioApplicativoList()) {
				IDServizioApplicativo idSA = new IDServizioApplicativo();
				idSA.setIdSoggettoProprietario(new IDSoggetto(pdAssociata.getTipoSoggettoProprietario(), pdAssociata.getNomeSoggettoProprietario()));
				idSA.setNome(sa.getNome());
				authzApplicativi.add(idSA);
			}
		}
		if(pdAssociata.getRuoli()!=null) {
			allRuoli = pdAssociata.getRuoli().getMatch()==null || RuoloTipoMatch.ALL.equals(pdAssociata.getRuoli().getMatch());
		}
		if(pdAssociata.getRuoli()!=null && pdAssociata.getRuoli().sizeRuoloList()>0) {
			authzRuoli = new ArrayList<>();
			for (Ruolo ruolo: pdAssociata.getRuoli().getRuoloList()) {
				authzRuoli.add(ruolo.getNome());
			}
		}
		if(pdAssociata.getAutorizzazioneToken()!=null) {
			authzApplicativiTokenEnabled = StatoFunzionalita.ABILITATO.equals(pdAssociata.getAutorizzazioneToken().getAutorizzazioneApplicativi());
			if(authzApplicativiTokenEnabled) {
				if(pdAssociata.getAutorizzazioneToken().getServiziApplicativi()!=null && pdAssociata.getAutorizzazioneToken().getServiziApplicativi().sizeServizioApplicativoList()>0) {
					authzApplicativiToken = new ArrayList<IDServizioApplicativo>();
					for (PortaDelegataServizioApplicativo sa : pdAssociata.getAutorizzazioneToken().getServiziApplicativi().getServizioApplicativoList()) {
						IDServizioApplicativo idSA = new IDServizioApplicativo();
						idSA.setIdSoggettoProprietario(new IDSoggetto(pdAssociata.getTipoSoggettoProprietario(), pdAssociata.getNomeSoggettoProprietario()));
						idSA.setNome(sa.getNome());
						authzApplicativiToken.add(idSA);
					}
				}
			}
			authzRuoliTokenEnabled = StatoFunzionalita.ABILITATO.equals(pdAssociata.getAutorizzazioneToken().getAutorizzazioneRuoli());
			if(authzRuoliTokenEnabled) {
				if(pdAssociata.getAutorizzazioneToken().getRuoli()!=null) {
					allRuoliToken = pdAssociata.getAutorizzazioneToken().getRuoli().getMatch()==null || RuoloTipoMatch.ALL.equals(pdAssociata.getAutorizzazioneToken().getRuoli().getMatch());
				}
				if(pdAssociata.getAutorizzazioneToken().getRuoli()!=null && pdAssociata.getAutorizzazioneToken().getRuoli().sizeRuoloList()>0) {
					authzRuoliToken = new ArrayList<>();
					for (Ruolo ruolo: pdAssociata.getAutorizzazioneToken().getRuoli().getRuoloList()) {
						authzRuoliToken.add(ruolo.getNome());
					}
				}
			}
		}
		if(pdAssociata.getScope()!=null) {
			authzScopeTokenEnabled = StatoFunzionalita.ABILITATO.equals(pdAssociata.getScope().getStato());
			if(authzScopeTokenEnabled) {
				allScope = pdAssociata.getScope().getMatch()==null || ScopeTipoMatch.ALL.equals(pdAssociata.getScope().getMatch());
				if(pdAssociata.getScope().sizeScopeList()>0) {
					authzScope = new ArrayList<>();
					for (Scope scope: pdAssociata.getScope().getScopeList()) {
						authzScope.add(scope.getNome());
					}
				}
			}
		}
		if(pdAssociata.getGestioneToken()!=null && pdAssociata.getGestioneToken().getOptions()!=null) {
			Properties properties = PropertiesUtilities.convertTextToProperties(pdAssociata.getGestioneToken().getOptions());
			if(properties!=null && properties.size()>0) {
				authzTokenClaims = new ArrayList<>();
				for (Object key : properties.keySet()) {
					if(key!=null && key instanceof String) {
						authzTokenClaims.add((String)key);
					}
				}
				Collections.sort(authzTokenClaims);
			}
		}
		
		printConfigurazioneControlloAccessi(consoleHelper, soggettiCore, true,
				protocollo,
				pdAssociata.getGestioneToken(),
				pdAssociata.getAutenticazione(), pdAssociata.getAutenticazioneOpzionale(), pdAssociata.getProprietaAutenticazioneList(),
				pdAssociata.getAttributeAuthorityList(),
				pdAssociata.getAutorizzazione(), authzSoggetti, authzApplicativi, 
				authzRuoli, allRuoli, 
				authzApplicativiTokenEnabled, authzApplicativiToken, 
				authzRuoliTokenEnabled, authzRuoliToken, allRuoliToken, 
				authzScopeTokenEnabled, authzScope, allScope,
				authzTokenClaims,
				pdAssociata.getAutorizzazioneContenuto(),
				sb,
				separator, newLine);
	}
	
	private static void printConfigurazioneControlloAccessi(ConsoleHelper consoleHelper, SoggettiCore soggettiCore, boolean gestioneFruitore,
			String protocollo,
			GestioneToken gestioneToken,
			String autenticazioneTrasporto, StatoFunzionalita autenticazioneTrasportoOpzionale, List<Proprieta> autenticazioneProprieta,
			List<AttributeAuthority> listAA,
			String autorizzazione, List<IDSoggetto> authzSoggetti, List<IDServizioApplicativo> authzApplicativi, 
			List<String> authzRuoli, boolean allRuoli, 
			boolean authzApplicativiTokenEnabled, List<IDServizioApplicativo> authzApplicativiToken, 
			boolean authzRuoliTokenEnabled, List<String> authzRuoliToken, boolean allRuoliToken, 
			boolean authzScopeTokenEnabled, List<String> authzScope, boolean allScope, 
			List<String> authzTokenClaims,
			String autorizzazioneContenuti,
			StringBuilder sb,
			String separator, String newLine) {
		
		boolean modipa = consoleHelper.isProfiloModIPA(protocollo);
		
		// Token
		
		if(gestioneToken!=null && gestioneToken.getPolicy()!=null) {
			String policy = gestioneToken.getPolicy();
			
			//sb.append(newLine);
			//sb.append("- "+CostantiControlStation.LABEL_PARAMETRO_PORTE_CONTROLLO_ACCESSI_GESTIONE_TOKEN+" -");
			
			sb.append(newLine);
			//sb.append(CostantiControlStation.LABEL_PARAMETRO_PORTE_GESTIONE_TOKEN_POLICY);
			sb.append(CostantiControlStation.LABEL_PARAMETRO_PORTE_GESTIONE_TOKEN_POLICY_LABEL_COMPLETA);
			sb.append(separator);
			sb.append(policy);
			
			if(StatoFunzionalita.ABILITATO.equals(gestioneToken.getTokenOpzionale())) {
				sb.append(newLine);
				sb.append(CostantiControlStation.LABEL_PARAMETRO_PORTE_GESTIONE_TOKEN_OPZIONALE);
				sb.append(separator);
				sb.append(gestioneToken.getTokenOpzionale());
			}
			
			StringBuilder bf = new StringBuilder();
			if(StatoFunzionalitaConWarning.ABILITATO.equals(gestioneToken.getValidazione()) || StatoFunzionalitaConWarning.WARNING_ONLY.equals(gestioneToken.getValidazione())) {
				if(bf.length()>0) {
					bf.append(", ");
				}
				bf.append(CostantiControlStation.LABEL_PARAMETRO_PORTE_GESTIONE_TOKEN_VALIDAZIONE_INPUT);
				if(StatoFunzionalitaConWarning.WARNING_ONLY.equals(gestioneToken.getValidazione())) {
					bf.append(" [WarningOnly]");
				}
			}
			if(StatoFunzionalitaConWarning.ABILITATO.equals(gestioneToken.getIntrospection()) || StatoFunzionalitaConWarning.WARNING_ONLY.equals(gestioneToken.getIntrospection())) {
				if(bf.length()>0) {
					bf.append(", ");
				}
				bf.append(CostantiControlStation.LABEL_PARAMETRO_PORTE_GESTIONE_TOKEN_INTROSPECTION);
				if(StatoFunzionalitaConWarning.WARNING_ONLY.equals(gestioneToken.getIntrospection())) {
					bf.append(" [WarningOnly]");
				}
			}
			if(StatoFunzionalitaConWarning.ABILITATO.equals(gestioneToken.getUserInfo()) || StatoFunzionalitaConWarning.WARNING_ONLY.equals(gestioneToken.getUserInfo())) {
				if(bf.length()>0) {
					bf.append(", ");
				}
				bf.append(CostantiControlStation.LABEL_PARAMETRO_PORTE_GESTIONE_TOKEN_USERINFO);
				if(StatoFunzionalitaConWarning.WARNING_ONLY.equals(gestioneToken.getUserInfo())) {
					bf.append(" [WarningOnly]");
				}
			}
			if(StatoFunzionalita.ABILITATO.equals(gestioneToken.getForward())) {
				if(bf.length()>0) {
					bf.append(", ");
				}
				bf.append(CostantiControlStation.LABEL_PARAMETRO_PORTE_GESTIONE_TOKEN_TOKEN_FORWARD);
			}
				
			if(bf.length()>0) {
				sb.append(newLine);
				//sb.append("Configurazione");
				sb.append(CostantiControlStation.LABEL_PARAMETRO_PORTE_CONTROLLO_ACCESSI_MODALITA_GESTIONE_TOKEN);
				sb.append(separator);
				sb.append(bf.toString());
			}
			
			GestioneTokenAutenticazione gestioneTokenAutenticazione = gestioneToken.getAutenticazione();
			if(gestioneTokenAutenticazione!=null) {
				StringBuilder bfAuth = new StringBuilder();
				if(StatoFunzionalita.ABILITATO.equals(gestioneTokenAutenticazione.getIssuer())) {
					if(bfAuth.length()>0) {
						bfAuth.append(", ");
					}
					bfAuth.append(CostantiControlStation.LABEL_PARAMETRO_PORTE_AUTENTICAZIONE_TOKEN_ISSUER);
				}
				if(StatoFunzionalita.ABILITATO.equals(gestioneTokenAutenticazione.getSubject())) {
					if(bfAuth.length()>0) {
						bfAuth.append(", ");
					}
					bfAuth.append(CostantiControlStation.LABEL_PARAMETRO_PORTE_AUTENTICAZIONE_TOKEN_SUBJECT);
				}
				if(StatoFunzionalita.ABILITATO.equals(gestioneTokenAutenticazione.getClientId())) {
					if(bfAuth.length()>0) {
						bfAuth.append(", ");
					}
					bfAuth.append(CostantiControlStation.LABEL_PARAMETRO_PORTE_AUTENTICAZIONE_TOKEN_CLIENT_ID);
				}
				if(StatoFunzionalita.ABILITATO.equals(gestioneTokenAutenticazione.getUsername())) {
					if(bfAuth.length()>0) {
						bfAuth.append(", ");
					}
					bfAuth.append(CostantiControlStation.LABEL_PARAMETRO_PORTE_AUTENTICAZIONE_TOKEN_USERNAME);
				}
				if(StatoFunzionalita.ABILITATO.equals(gestioneTokenAutenticazione.getEmail())) {
					if(bfAuth.length()>0) {
						bfAuth.append(", ");
					}
					bfAuth.append(CostantiControlStation.LABEL_PARAMETRO_PORTE_AUTENTICAZIONE_TOKEN_MAIL);
				}
				
				if(bfAuth.length()>0) {
					sb.append(newLine);
					//sb.append(CostantiControlStation.LABEL_PARAMETRO_PORTE_CONTROLLO_ACCESSI_AUTENTICAZIONE);
					sb.append(CostantiControlStation.LABEL_PARAMETRO_PORTE_CONTROLLO_ACCESSI_GESTIONE_TOKEN);
					sb.append(separator);
					sb.append(bfAuth.toString());
				}
			}
		}
		
		// Autenticazione Trasporto
		if(autenticazioneTrasporto!=null) {
			
			//sb.append(newLine);
			String label = null;
			if(modipa && !gestioneFruitore) {
				label = CostantiControlStation.LABEL_PARAMETRO_PORTE_CONTROLLO_ACCESSI_AUTENTICAZIONE+" "+CostantiControlStation.LABEL_PARAMETRO_PORTE_CONTROLLO_ACCESSI_AUTENTICAZIONE_CANALE;
			}
			else {
				label = CostantiControlStation.LABEL_PARAMETRO_PORTE_CONTROLLO_ACCESSI_AUTENTICAZIONE+" "+CostantiControlStation.LABEL_PARAMETRO_PORTE_CONTROLLO_ACCESSI_AUTENTICAZIONE_TRASPORTO;
			}
			//sb.append("- "+label+" -");
			
			sb.append(newLine);
			//sb.append(CostantiControlStation.LABEL_PARAMETRO_PORTE_AUTENTICAZIONE);
			sb.append(label);
			sb.append(separator);
			TipoAutenticazione tipoAutenticazione = TipoAutenticazione.toEnumConstant(autenticazioneTrasporto);
			if(TipoAutenticazione.PRINCIPAL.equals(tipoAutenticazione)) {
				TipoAutenticazionePrincipal autenticazionePrincipal = consoleHelper.getCore().getTipoAutenticazionePrincipal(autenticazioneProprieta);
				sb.append(tipoAutenticazione.getLabel()).append(" - ").append(autenticazionePrincipal.getLabel());	
			}
			else if(TipoAutenticazione.APIKEY.equals(tipoAutenticazione)) {
				String appIdV = getProprieta(ParametriAutenticazioneApiKey.APP_ID, autenticazioneProprieta);
				boolean appId = ServletUtils.isCheckBoxEnabled(appIdV);
				if(appId) {
					sb.append(tipoAutenticazione.getLabel()).append(" + ").append("app-id");
				}
				else {
					sb.append(tipoAutenticazione.getLabel());
				}
			}
			else {
				if(tipoAutenticazione!=null) {
					sb.append(tipoAutenticazione.getLabel());
				}
				else {
					try {
						ConfigurazioneCore confCore = new ConfigurazioneCore(consoleHelper.getCore());
						Plugin p = confCore.getPlugin(TipoPlugin.AUTENTICAZIONE, autenticazioneTrasporto, false);
						if(p!=null) {
							sb.append(p.getLabel());
						}
						else {
							sb.append(autenticazioneTrasporto);
						}
					}catch(Throwable t) {
						sb.append(autenticazioneTrasporto);
					}
				}
			}
						
			if(StatoFunzionalita.ABILITATO.equals(autenticazioneTrasportoOpzionale)) {
				sb.append(newLine);
				sb.append(CostantiControlStation.LABEL_PARAMETRO_PORTE_CONTROLLO_ACCESSI_AUTENTICAZIONE+" "+CostantiControlStation.LABEL_PARAMETRO_PORTE_AUTENTICAZIONE_OPZIONALE);
				sb.append(separator);
				sb.append(autenticazioneTrasportoOpzionale);
			}
		}
		
		// AttributeAuthority (attributi)
		
		if(listAA!=null && listAA.size()>0) {
			StringBuilder sbAA = new StringBuilder();
			for (AttributeAuthority aa : listAA) {
				if(sbAA.length()>0) {
					sbAA.append(", ");
				}
				sbAA.append(aa.getNome());
			}
			if(sbAA.length()>0) {
				
				//sb.append(newLine);
				//sb.append("- "+CostantiControlStation.LABEL_PARAMETRO_PORTE_ATTRIBUTI_TITLE+" -");
								
				sb.append(newLine);
				sb.append(CostantiControlStation.LABEL_PARAMETRO_PORTE_ATTRIBUTI_AUTHORITY);
				sb.append(separator);
				sb.append(sbAA.toString());
			}
		}
		
		// Autorizzazione Trasporto
		if(autorizzazione!=null) {
			
			//sb.append(newLine);
			String label = null;
			if(modipa && !gestioneFruitore) {
				label = CostantiControlStation.LABEL_PARAMETRO_PORTE_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CANALE;
			}
			else {
				if(autorizzazione.toLowerCase().contains(TipoAutorizzazione.ROLES.getValue().toLowerCase())
						||
						autorizzazione.toLowerCase().contains(TipoAutorizzazione.AUTHENTICATED.getValue().toLowerCase())){
					label = CostantiControlStation.LABEL_PARAMETRO_PORTE_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TRASPORTO;
				}
				else {
					label = CostantiControlStation.LABEL_PARAMETRO_PORTE_CONTROLLO_ACCESSI_AUTORIZZAZIONE_DIFFERENTE_DA_TRASPORTO_E_TOKEN;
				}
			}
			//sb.append("- "+label+" -");
			
			String value = null;
			if(CostantiConfigurazione.AUTORIZZAZIONE_NONE.equals(autorizzazione)){
				value = CostantiConfigurazione.DISABILITATO.getValue();
			}
			else if(autorizzazione.toLowerCase().contains(TipoAutorizzazione.XACML_POLICY.getValue().toLowerCase())){
				value = TipoAutorizzazione.XACML_POLICY.getValue();
			}
			else if(autorizzazione.toLowerCase().contains(TipoAutorizzazione.ROLES.getValue().toLowerCase())
					||
					autorizzazione.toLowerCase().contains(TipoAutorizzazione.AUTHENTICATED.getValue().toLowerCase())){
				value = CostantiConfigurazione.ABILITATO.getValue();
				value = value + " ("; 
				if(autorizzazione.toLowerCase().contains(TipoAutorizzazione.AUTHENTICATED.getValue().toLowerCase())) {
					value = value + CostantiControlStation.LABEL_PARAMETRO_PORTE_AUTORIZZAZIONE_SERVIZI_APPLICATIVI_SUFFIX;
					if(autorizzazione.toLowerCase().contains(TipoAutorizzazione.ROLES.getValue().toLowerCase())) {
						value = value + ", ";
					}
				}
				if(autorizzazione.toLowerCase().contains(TipoAutorizzazione.ROLES.getValue().toLowerCase())) {
					value = value + CostantiControlStation.LABEL_PARAMETRO_PORTE_AUTORIZZAZIONE_RUOLI_SUFFIX;
				}
				value = value + ")"; 
			}
			else{
				try {
					ConfigurazioneCore confCore = new ConfigurazioneCore(consoleHelper.getCore());
					Plugin p = confCore.getPlugin(TipoPlugin.AUTORIZZAZIONE, autorizzazione, false);
					if(p!=null) {
						value = p.getLabel();
					}
					else {
						value = autorizzazione;
					}
				}catch(Throwable t) {
					value = autorizzazione;
				}
			}
			sb.append(newLine);
			//sb.append(CostantiControlStation.LABEL_PARAMETRO_PORTE_AUTORIZZAZIONE);
			sb.append(label);
			sb.append(separator);
			sb.append(value);
			
			//if(autorizzazione.toLowerCase().contains(TipoAutorizzazione.AUTHENTICATED.getValue().toLowerCase())){
			// servono anche in custom auth
			// basta che siano definiti
				
			boolean richiedentiDefiniti = false;
			
			// devo elencare i soggetti autorizzati
			if(authzSoggetti!=null && !authzSoggetti.isEmpty()) {
				StringBuilder sbSog = new StringBuilder();
				for (IDSoggetto soggetto : authzSoggetti) {
					if(sbSog.length()>0) sb.append(", ");
					try {
						sbSog.append(consoleHelper.getLabelNomeSoggetto(protocollo, soggetto.getTipo(), soggetto.getNome()));
					}catch(Throwable t) {
						sbSog.append(soggetto.getTipo() +"/" +soggetto.getNome());
					}
				}
				if(sbSog.toString().length()>0) {
					sb.append(newLine);
					sb.append(CostantiControlStation.LABEL_PARAMETRO_SOGGETTI).append(" autorizzati");
					sb.append(separator);
					sb.append(sbSog.toString());
					richiedentiDefiniti=true;
				}
			}

			// devo elencare gli applicativi autorizzati
			if(!modipa || gestioneFruitore) {
				if(authzApplicativi!=null && !authzApplicativi.isEmpty()) {
					StringBuilder sbApp = new StringBuilder();
					for (IDServizioApplicativo sa : authzApplicativi) {
						if(sbApp.length()>0) sbApp.append(", ");
						if(gestioneFruitore) {
							sbApp.append(sa.getNome());
						}
						else {
							String sog = null;
							try {
								sog = consoleHelper.getLabelNomeSoggetto(protocollo, sa.getIdSoggettoProprietario().getTipo(), sa.getIdSoggettoProprietario().getNome());
							}catch(Throwable t) {
								sog = sa.getIdSoggettoProprietario().getTipo()+"/"+sa.getIdSoggettoProprietario().getNome();
							}
							sbApp.append(sa.getNome() + "@"+sog+"");
						}
					}
					if(sbApp.toString().length()>0) {
						sb.append(newLine);
						sb.append(CostantiControlStation.LABEL_APPLICATIVI).append(" autorizzati");
						sb.append(separator);
						sb.append(sbApp.toString());
						richiedentiDefiniti=true;
					}
				}
			}
			
			if(autorizzazione.toLowerCase().contains(TipoAutorizzazione.AUTHENTICATED.getValue().toLowerCase()) && !richiedentiDefiniti) {
				sb.append(newLine);
				sb.append(CostantiControlStation.LABEL_PARAMETRO_PORTE_AUTORIZZAZIONE_RICHIEDENTI).append(" autorizzati");
				sb.append(separator);
				sb.append("-");
			}
				
			//if(autorizzazione.toLowerCase().contains(TipoAutorizzazione.ROLES.getValue().toLowerCase())){
			// servono anche in xacml policy. 
			// comunque basta che siano definiti
			if(autorizzazione.toLowerCase().contains(TipoAutorizzazione.ROLES.getValue().toLowerCase())
					||
					autorizzazione.toLowerCase().contains(TipoAutorizzazione.XACML_POLICY.getValue().toLowerCase())) {
				StringBuilder sbRuoli = new StringBuilder();
				if(authzRuoli!=null && !authzRuoli.isEmpty()){
					for (String ruolo : authzRuoli) {
						if(sbRuoli.length()>0) sbRuoli.append(", ");
						sbRuoli.append(ruolo);
					}
				}
				else {
					if(autorizzazione.toLowerCase().contains(TipoAutorizzazione.ROLES.getValue().toLowerCase())) {
						sbRuoli.append("-");
					}
				}
				if(sbRuoli.toString().length()>0) {
					sb.append(newLine);
					sb.append(RuoliCostanti.LABEL_RUOLI).append(" (").
						append(allRuoli ? CostantiControlStation.LABEL_PARAMETRO_RUOLO_MATCH_ALL : CostantiControlStation.LABEL_PARAMETRO_RUOLO_MATCH_ANY).
						append(")");
					sb.append(separator);
					sb.append(sbRuoli.toString());
				}
			}
			//}
			
			if(modipa && !gestioneFruitore) {
				
				// backwardCompatibility
				if(!authzApplicativiTokenEnabled && 
					authzApplicativi!=null && !authzApplicativi.isEmpty()) {
					authzApplicativiTokenEnabled = true;
				}
				
				boolean autorizzazioneMessaggio = (authzApplicativiTokenEnabled)
						||
						(authzRuoliTokenEnabled);
				
				if(autorizzazioneMessaggio) {
					label = CostantiControlStation.LABEL_PARAMETRO_PORTE_CONTROLLO_ACCESSI_AUTORIZZAZIONE_MESSAGGIO;
					value = CostantiConfigurazione.ABILITATO.getValue();
					value = value + " ("; 
					boolean first = true;
					if(authzApplicativiTokenEnabled) {
						if(!first) {
							value = value + ", ";
						}
						value = value + CostantiControlStation.LABEL_PARAMETRO_PORTE_AUTORIZZAZIONE_SERVIZI_APPLICATIVI_SUFFIX;
						first=false;
					}
					if(authzRuoliTokenEnabled) {
						if(!first) {
							value = value + ", ";
						}
						value = value + CostantiControlStation.LABEL_PARAMETRO_PORTE_AUTORIZZAZIONE_RUOLI_SUFFIX;
						first=false;
					}
					value = value + ")"; 
					sb.append(newLine);
					//sb.append(CostantiControlStation.LABEL_PARAMETRO_PORTE_AUTORIZZAZIONE);
					sb.append(label);
					sb.append(separator);
					sb.append(value);
				}
			}
			else {
							
				boolean autorizzazioneToken = (authzApplicativiTokenEnabled)
						||
						(authzRuoliTokenEnabled)
						||
						(authzScopeTokenEnabled)
						||
						(authzTokenClaims!=null && !authzTokenClaims.isEmpty());
				
				if(autorizzazioneToken) {
					label = CostantiControlStation.LABEL_PARAMETRO_PORTE_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN;
					value = CostantiConfigurazione.ABILITATO.getValue();
					value = value + " ("; 
					boolean first = true;
					if(authzApplicativiTokenEnabled) {
						if(!first) {
							value = value + ", ";
						}
						value = value + CostantiControlStation.LABEL_PARAMETRO_PORTE_AUTORIZZAZIONE_SERVIZI_APPLICATIVI_SUFFIX;
						first=false;
					}
					if(authzRuoliTokenEnabled) {
						if(!first) {
							value = value + ", ";
						}
						value = value + CostantiControlStation.LABEL_PARAMETRO_PORTE_AUTORIZZAZIONE_RUOLI_SUFFIX;
						first=false;
					}
					if(authzScopeTokenEnabled) {
						if(!first) {
							value = value + ", ";
						}
						value = value + ScopeCostanti.LABEL_SCOPE;
						first=false;
					}
					if(authzTokenClaims!=null && !authzTokenClaims.isEmpty()) {
						if(!first) {
							value = value + ", ";
						}
						value = value + CostantiControlStation.LABEL_PARAMETRO_PORTE_AUTORIZZAZIONE_TOKEN_CLAIMS_SUBTITLE_SUFFIX;
						first=false;
					}
					value = value + ")"; 
					sb.append(newLine);
					//sb.append(CostantiControlStation.LABEL_PARAMETRO_PORTE_AUTORIZZAZIONE);
					sb.append(label);
					sb.append(separator);
					sb.append(value);
				}
			}
					
			if(authzApplicativiTokenEnabled) {
				StringBuilder sbApp = new StringBuilder();
				List<IDServizioApplicativo> ll = authzApplicativiToken;
				if(modipa && !gestioneFruitore) {
					ll = authzApplicativi;
				}
				if(ll!=null && !ll.isEmpty()) {
					for (IDServizioApplicativo sa : ll) {
						if(sbApp.length()>0) sbApp.append(", ");
						if(gestioneFruitore) {
							sbApp.append(sa.getNome());
						}
						else {
							String sog = null;
							try {
								sog = consoleHelper.getLabelNomeSoggetto(protocollo, sa.getIdSoggettoProprietario().getTipo(), sa.getIdSoggettoProprietario().getNome());
							}catch(Throwable t) {
								sog = sa.getIdSoggettoProprietario().getTipo()+"/"+sa.getIdSoggettoProprietario().getNome();
							}
							sbApp.append(sa.getNome() + "@"+sog+"");
						}
					}
				}
				else {
					sbApp.append("-");
				}
				if(sbApp.toString().length()>0) {
					sb.append(newLine);
					sb.append(CostantiControlStation.LABEL_APPLICATIVI).append(" autorizzati");
					sb.append(separator);
					sb.append(sbApp.toString());
				}
			}
			
			if(authzRuoliTokenEnabled){
				StringBuilder sbRuoli = new StringBuilder();
				if(authzRuoliToken!=null && !authzRuoliToken.isEmpty()){
					for (String ruolo : authzRuoliToken) {
						if(sbRuoli.length()>0) sbRuoli.append(", ");
						sbRuoli.append(ruolo);
					}
				}
				else {
					sbRuoli.append("-");
				}
				if(sbRuoli.toString().length()>0) {
					sb.append(newLine);
					sb.append(RuoliCostanti.LABEL_RUOLI).append(" (").
						append(allRuoliToken ? CostantiControlStation.LABEL_PARAMETRO_RUOLO_MATCH_ALL : CostantiControlStation.LABEL_PARAMETRO_RUOLO_MATCH_ANY).
						append(")");
					sb.append(separator);
					sb.append(sbRuoli.toString());
				}
			}
			
			if(modipa && !gestioneFruitore) {
				boolean autorizzazioneToken = (authzScopeTokenEnabled)
						||
						(authzTokenClaims!=null && !authzTokenClaims.isEmpty());
				
				if(autorizzazioneToken) {
					label = CostantiControlStation.LABEL_PARAMETRO_PORTE_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN;
					value = CostantiConfigurazione.ABILITATO.getValue();
					value = value + " ("; 
					boolean first = true;
					if(authzScopeTokenEnabled) {
						if(!first) {
							value = value + ", ";
						}
						value = value + ScopeCostanti.LABEL_SCOPE;
						first=false;
					}
					if(authzTokenClaims!=null && !authzTokenClaims.isEmpty()) {
						if(!first) {
							value = value + ", ";
						}
						value = value + CostantiControlStation.LABEL_PARAMETRO_PORTE_AUTORIZZAZIONE_TOKEN_CLAIMS_SUBTITLE_SUFFIX;
						first=false;
					}
					value = value + ")"; 
					sb.append(newLine);
					//sb.append(CostantiControlStation.LABEL_PARAMETRO_PORTE_AUTORIZZAZIONE);
					sb.append(label);
					sb.append(separator);
					sb.append(value);
				}
			}
			
			if(authzScopeTokenEnabled){
				StringBuilder sbScope = new StringBuilder();
				if(authzScope!=null && !authzScope.isEmpty()){
					for (String scope : authzScope) {
						if(sbScope.length()>0) sbScope.append(", ");
						sbScope.append(scope);
					}
				}
				else {
					sbScope.append("-");
				}
				if(sbScope.toString().length()>0) {
					sb.append(newLine);
					sb.append(ScopeCostanti.LABEL_SCOPE).append(" (").
						append(allScope ? CostantiControlStation.LABEL_PARAMETRO_SCOPE_MATCH_ALL : CostantiControlStation.LABEL_PARAMETRO_SCOPE_MATCH_ANY).
						append(")");
					sb.append(separator);
					sb.append(sbScope.toString());
				}
			}
			
			if(authzTokenClaims!=null && !authzTokenClaims.isEmpty()){
				StringBuilder sbTokenClaims = new StringBuilder();
				for (String claim : authzTokenClaims) {
					if(sbTokenClaims.length()>0) sbTokenClaims.append(", ");
					sbTokenClaims.append(claim);
				}
				if(sbTokenClaims.toString().length()>0) {
					sb.append(newLine);
					sb.append(CostantiControlStation.LABEL_PARAMETRO_PORTE_AUTORIZZAZIONE_TOKEN_CLAIMS_SUBTITLE_SUFFIX);
					sb.append(separator);
					sb.append(sbTokenClaims.toString());
				}
			}
		}
				
		// Autorizzazione Contenuti
		if(autorizzazioneContenuti!=null && !CostantiConfigurazione.AUTORIZZAZIONE_NONE.equals(autorizzazioneContenuti)){
			
			//sb.append(newLine);
			String label = CostantiControlStation.LABEL_PARAMETRO_PORTE_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI;
			//sb.append("- "+label+" -");
			
			sb.append(newLine);
			//sb.append(CostantiControlStation.LABEL_PARAMETRO_PORTE_AUTORIZZAZIONE);
			sb.append(label);
			sb.append(separator);
			if(CostantiAutorizzazione.AUTORIZZAZIONE_CONTENUTO_BUILT_IN.equals(autorizzazioneContenuti)){
				sb.append(CostantiConfigurazione.ABILITATO.getValue());
			}
			else {
				try {
					ConfigurazioneCore confCore = new ConfigurazioneCore(consoleHelper.getCore());
					Plugin p = confCore.getPlugin(TipoPlugin.AUTORIZZAZIONE_CONTENUTI, autorizzazioneContenuti, false);
					if(p!=null) {
						sb.append(p.getLabel());
					}
					else {
						sb.append(autorizzazioneContenuti);
					}
				}catch(Throwable t) {
					sb.append(autorizzazioneContenuti);
				}
			}
		}
	}
	
	private static void printConfigurazioneRateLimiting(ConsoleHelper consoleHelper, SoggettiCore soggettiCore, 
			boolean gestioneFruitore, String nomePorta,
			StringBuilder sb,
			String separator, String newLine) throws Exception {
		ConsoleSearch searchPolicy = new ConsoleSearch(true);
		ConfigurazioneCore confCore = new ConfigurazioneCore(soggettiCore);
		List<AttivazionePolicy> listaPolicy = confCore.attivazionePolicyList(searchPolicy, 
				gestioneFruitore ? RuoloPolicy.DELEGATA : RuoloPolicy.APPLICATIVA, nomePorta);
		if(listaPolicy!=null && !listaPolicy.isEmpty()) {
			DataElement de = new DataElement();
			consoleHelper.setStatoRateLimiting(de, listaPolicy, false);
			printConfigurazioneFromStatusValues(de, ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_RATE_LIMITING,
					sb,
					separator, newLine);
		}
	}
	
	private static void printConfigurazioneValidazioneContenutiApplicativi(ConsoleHelper consoleHelper, SoggettiCore soggettiCore, 
			AccordoServizioParteComune aspc, ValidazioneContenutiApplicativi validazioneContenutiApplicativi,
			StringBuilder sb,
			String separator, String newLine) throws Exception {
		if(validazioneContenutiApplicativi!=null) {
			if(StatoFunzionalitaConWarning.ABILITATO.equals(validazioneContenutiApplicativi.getStato()) || StatoFunzionalitaConWarning.WARNING_ONLY.equals(validazioneContenutiApplicativi.getStato())) {
				String value = null;
				switch (validazioneContenutiApplicativi.getTipo()) {
				case INTERFACE:
					switch (aspc.getFormatoSpecifica()) {
					case OPEN_API_3:
						value=CostantiControlStation.LABEL_PARAMETRO_INTERFACE_TYPE_OPEN_API_3;
						break;
					case SWAGGER_2:
						value=CostantiControlStation.LABEL_PARAMETRO_INTERFACE_TYPE_SWAGGER_2;
						break;
					case WSDL_11:
						value=CostantiControlStation.LABEL_PARAMETRO_INTERFACE_TYPE_WSDL_11;
						break;
					}
					break;
				case XSD:
					value=CostantiControlStation.LABEL_PARAMETRO_SCHEMI_XSD;
					break;
				case OPENSPCOOP:
					value=CostantiControlStation.LABEL_PARAMETRO_REGISTRO_OPENSPCOOP;
					break;
				}
				if(StatoFunzionalitaConWarning.WARNING_ONLY.equals(validazioneContenutiApplicativi.getStato())) {
					value = value + " [WarningOnly]";
				}
				
				if(org.openspcoop2.core.registry.constants.ServiceBinding.SOAP.equals(aspc.getServiceBinding()) &&
						validazioneContenutiApplicativi.getAcceptMtomMessage()!=null && StatoFunzionalita.ABILITATO.equals(validazioneContenutiApplicativi.getAcceptMtomMessage())) {
					value = value + " [Accept-MTOM]";
				}
				
				sb.append(newLine);
				sb.append(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_VALIDAZIONE_CONTENUTI_APPLICATIVI);
				sb.append(separator);
				sb.append(value);
			}
		}
	}
	
	private static void printConfigurazioneResponseCaching(ConsoleHelper consoleHelper, SoggettiCore soggettiCore, 
			ResponseCachingConfigurazione responseCachingConfigurazione,
			StringBuilder sb,
			String separator, String newLine) throws Exception {
		if(responseCachingConfigurazione!=null) {
			if(StatoFunzionalita.ABILITATO.equals(responseCachingConfigurazione.getStato())){
				sb.append(newLine);
				sb.append(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_RESPONSE_CACHING);
				sb.append(separator);
				sb.append(StatoFunzionalitaConWarning.ABILITATO.getValue());
			}
		}
	}
	
	private static void printConfigurazioneMessageSecurity(ConsoleHelper consoleHelper, SoggettiCore soggettiCore, 
			MessageSecurity messageSecurity,
			StringBuilder sb,
			String separator, String newLine) throws Exception {
		
		if(messageSecurity!=null && (
				(messageSecurity.getRequestFlow()!=null && messageSecurity.getRequestFlow().getMode()!=null)
				||
				(messageSecurity.getResponseFlow()!=null && messageSecurity.getResponseFlow().getMode()!=null)
			)
		) {
			AccordiServizioParteSpecificaCore apsCore = new AccordiServizioParteSpecificaCore(soggettiCore);
			PropertiesSourceConfiguration propertiesSourceConfiguration = apsCore.getMessageSecurityPropertiesSourceConfiguration();
			ConfigManager configManager = ConfigManager.getinstance(ControlStationCore.getLog());
			configManager.leggiConfigurazioni(propertiesSourceConfiguration, true);
			DataElement de = new DataElement();
			consoleHelper.setStatoSicurezzaMessaggio(de, messageSecurity, configManager, propertiesSourceConfiguration, false);
			printConfigurazioneFromStatusValues(de, PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MESSAGE_SECURITY,
					sb,
					separator, newLine);
		}
	}
	
	private static void printConfigurazioneMTOM(ConsoleHelper consoleHelper, SoggettiCore soggettiCore, 
			MtomProcessor mtomProcessor,
			StringBuilder sb,
			String separator, String newLine) throws Exception {
		
		if(mtomProcessor!=null && (
				(mtomProcessor.getRequestFlow()!=null && mtomProcessor.getRequestFlow().getMode()!=null && !MTOMProcessorType.DISABLE.equals(mtomProcessor.getRequestFlow().getMode()))
				||
				(mtomProcessor.getResponseFlow()!=null && mtomProcessor.getResponseFlow().getMode()!=null && !MTOMProcessorType.DISABLE.equals(mtomProcessor.getResponseFlow().getMode()))
			)
		) {
			DataElement de = new DataElement();
			consoleHelper.setStatoMTOM(de, mtomProcessor, false);
			printConfigurazioneFromStatusValues(de, PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MTOM_CONFIG,
					sb,
					separator, newLine);
		}
	}
	
	private static void printConfigurazioneTrasformazioni(ConsoleHelper consoleHelper, SoggettiCore soggettiCore, 
			AccordoServizioParteComune aspc, Trasformazioni trasformazioni,
			StringBuilder sb,
			String separator, String newLine) throws Exception {
		
		if(trasformazioni!=null && trasformazioni.sizeRegolaList()>0) {
			
			boolean almostOneEnable = false;
			for (TrasformazioneRegola tRegola : trasformazioni.getRegolaList()) {
				if(StatoFunzionalita.ABILITATO.equals(tRegola.getStato())){
					almostOneEnable = true;
					break;
				}
			}
			
			if(almostOneEnable) {
				DataElement de = new DataElement();
				consoleHelper.setStatoTrasformazioni(de, trasformazioni, ServiceBinding.valueOf(aspc.getServiceBinding().name()), false);
				printConfigurazioneFromStatusValues(de, CostantiControlStation.LABEL_CONFIGURAZIONE_TRASFORMAZIONI,
						sb,
						separator, newLine);
			}
		}
	}
	
	private static void printConfigurazioneTracciamento(ConsoleHelper consoleHelper, SoggettiCore soggettiCore, 
			CorrelazioneApplicativa correlazioneApplicativa,
			CorrelazioneApplicativaRisposta correlazioneApplicativaRisposta,
			PortaTracciamento tracciamentoConfig, 
			//Configurazione configurazioneGenerale,
			StringBuilder sb,
			String separator, String newLine) throws Exception {
		
		// Transazioni
		boolean ridefinito = false;
		boolean enable = false;
		if(tracciamentoConfig!=null && tracciamentoConfig.getEsiti()!=null) {
			ridefinito = true;
			enable = !(EsitiConfigUtils.TUTTI_ESITI_DISABILITATI+"").equals(tracciamentoConfig.getEsiti());
		}
		else {
			/*ridefinito = false;
			String esitiTransazioni = consoleHelper.readConfigurazioneRegistrazioneEsitiFromHttpParameters((configurazioneGenerale!=null && configurazioneGenerale.getTracciamento()!=null) ? configurazioneGenerale.getTracciamento().getEsiti() : null , true);
			enable = (
					esitiTransazioni!=null 
					&& 
					!(EsitiConfigUtils.TUTTI_ESITI_DISABILITATI+"").equals(esitiTransazioni)
			);*/
		}
		if(ridefinito) {
			sb.append(newLine);
			sb.append(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_REGISTRAZIONE_TRANSAZIONI);
			sb.append(separator);
			sb.append(enable ? StatoFunzionalita.ABILITATO.getValue() : StatoFunzionalita.DISABILITATO.getValue());
		}
		
		// Diagnostici
		ridefinito = false;
		enable = false;
		String labelDiagnostici = null;
		if(tracciamentoConfig!=null && tracciamentoConfig.getSeverita()!=null) {
			ridefinito = true;
			labelDiagnostici = ConfigurazioneCostanti.LABEL_SEVERITA+" "+tracciamentoConfig.getSeverita().getValue();
			enable = (!LogLevels.LIVELLO_OFF.equals(tracciamentoConfig.getSeverita().getValue()));
		}
		else {
			/*ridefinito = false;
			if(configurazioneGenerale!=null && configurazioneGenerale.getMessaggiDiagnostici()!=null && configurazioneGenerale.getMessaggiDiagnostici().getSeverita()!=null) {
				labelDiagnostici = ConfigurazioneCostanti.LABEL_SEVERITA+" "+configurazioneGenerale.getMessaggiDiagnostici().getSeverita().getValue();
			}
			enable = (
					configurazioneGenerale!=null && configurazioneGenerale.getMessaggiDiagnostici()!=null &&
							configurazioneGenerale.getMessaggiDiagnostici().getSeverita()!=null 
					&& 
					!(LogLevels.LIVELLO_OFF.equals(configurazioneGenerale.getMessaggiDiagnostici().getSeverita().getValue()))
					);*/
		}
		if(ridefinito) {
			sb.append(newLine);
			sb.append(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_DIAGNOSTICI);
			sb.append(separator);
			sb.append(enable ? StatoFunzionalita.ABILITATO.getValue() + " ["+labelDiagnostici+"]" : StatoFunzionalita.DISABILITATO.getValue());
		}

		// Correlazione
		boolean isCorrelazioneApplicativaAbilitataReq = false;
		if (correlazioneApplicativa != null) {
			isCorrelazioneApplicativaAbilitataReq = correlazioneApplicativa.sizeElementoList() > 0;
		}
		boolean isCorrelazioneApplicativaAbilitataRes = false;
		if (correlazioneApplicativaRisposta != null) {
			isCorrelazioneApplicativaAbilitataRes = correlazioneApplicativaRisposta.sizeElementoList() > 0;
		}
		if(isCorrelazioneApplicativaAbilitataReq || isCorrelazioneApplicativaAbilitataRes) {
			sb.append(newLine);
			sb.append(CostantiControlStation.LABEL_PARAMETRO_CORRELAZIONE_APPLICATIVA);
			sb.append(separator);
			StringBuilder sbCor = new StringBuilder();
			if(isCorrelazioneApplicativaAbilitataReq) {
				sb.append(CostantiControlStation.LABEL_PARAMETRO_RICHIESTA.toLowerCase());
				if(correlazioneApplicativa.sizeElementoList()>1) {
					sb.append(" (").append(correlazioneApplicativa.sizeElementoList()).append(")");
				}
			}
			if(isCorrelazioneApplicativaAbilitataRes) {
				if(isCorrelazioneApplicativaAbilitataReq) {
					sb.append(", ");
				}
				sb.append(CostantiControlStation.LABEL_PARAMETRO_RISPOSTA.toLowerCase());
				if(correlazioneApplicativaRisposta.sizeElementoList()>1) {
					sb.append(" (").append(correlazioneApplicativaRisposta.sizeElementoList()).append(")");
				}
			}
			sb.append(sbCor.toString());
		}
		
	}
	
	private static void printConfigurazioneRegistrazioneMessaggi(ConsoleHelper consoleHelper, SoggettiCore soggettiCore, 
			DumpConfigurazione dumpConfigurazione,
			//Configurazione configurazioneGenerale = null; // non viene usato, poiche' il metodo viene chiamato solo con dumpConfigurazione diverso da null
			StringBuilder sb,
			String separator, String newLine) throws Exception {
		
		if(dumpConfigurazione!=null) {
			DataElement de = new DataElement();
			consoleHelper.setStatoDump(de, dumpConfigurazione, 
					null, //configurazioneGenerale, 
					false, false);
			printConfigurazioneFromStatusValues(de, CostantiControlStation.LABEL_REGISTRAZIONE_MESSAGGI,
					sb,
					separator, newLine);
		}
	}
	
	private static void printConfigurazioneAllarmi(ConsoleHelper consoleHelper, SoggettiCore soggettiCore, 
			boolean gestioneFruitore, String nomePorta,
			StringBuilder sb,
			String separator, String newLine) throws Exception {
		boolean visualizzaAllarmi = soggettiCore.isConfigurazioneAllarmiEnabled();
		if(visualizzaAllarmi) {
			List<ConfigurazioneAllarmeBean> listaAllarmi = null;
			ConsoleSearch searchPolicy = new ConsoleSearch(true);
			ConfigurazioneCore confCore = new ConfigurazioneCore(soggettiCore);
			listaAllarmi = confCore.allarmiList(searchPolicy, gestioneFruitore ? RuoloPorta.DELEGATA : RuoloPorta.APPLICATIVA , nomePorta);
			if(listaAllarmi!=null && !listaAllarmi.isEmpty()) {
				DataElement de = new DataElement();
				consoleHelper.setStatoAllarmi(de, listaAllarmi);
				printConfigurazioneFromStatusValues(de, ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_ALLARMI,
						sb,
						separator, newLine);
			}
		}
	}
	
	private static void printConfigurazioneProperties(ConsoleHelper consoleHelper, SoggettiCore soggettiCore, 
			List<Proprieta> list,
			StringBuilder sb,
			String separator, String newLine) throws Exception {
		if(list!=null && list.size()>0) {
			StringBuilder sbProp = new StringBuilder();
			if(list.size()<=5) {
				for (Proprieta proprieta : list) {
					if(sbProp.length()>0) {
						sbProp.append(", ");
					}
					sbProp.append(proprieta.getNome());
					sbProp.append("=");
					int limitValore = 30;
					if(proprieta.getValore().length()<limitValore) {
						sbProp.append(proprieta.getValore());
					}
					else {
						sbProp.append(proprieta.getValore().substring(0, limitValore-3)+"...");
					}
				}
			}
			else if(list.size()<=10) {
				for (Proprieta proprieta : list) {
					if(sbProp.length()>0) {
						sbProp.append(", ");
					}
					sbProp.append(proprieta.getNome());
				}
			}
			else {
				sbProp.append(StatoFunzionalita.ABILITATO.getValue()).append(" (").append(list.size()).append(")");
			}
			sb.append(newLine);
			sb.append(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_PROPRIETA_LABEL2);
			sb.append(separator);
			sb.append(sbProp.toString());
		}
	}
	
	private static void printConfigurazioneAltro(ConsoleHelper consoleHelper, SoggettiCore soggettiCore, 
			String protocollo, AccordoServizioParteComune aspc, 
			StatoFunzionalita allegaBody, StatoFunzionalita scartaBody, 
			String integrazione, String behaviour,
			List<Proprieta> proprietaRateLimiting,
			StatoFunzionalita stateless, PortaDelegataLocalForward localForward,
			StatoFunzionalita ricevutaAsincronaSimmetrica, StatoFunzionalita ricevutaAsincronaAsimmetrica,
			StatoFunzionalita gestioneManifest, ConfigurazionePortaHandler configPortaHandler,
			StringBuilder sb,
			String separator, String newLine) throws Exception {
		DataElement de = new DataElement();
		consoleHelper.setStatoOpzioniAvanzate(de, 
				protocollo, ServiceBinding.valueOf(aspc.getServiceBinding().name()),
				allegaBody, scartaBody, 
				integrazione, behaviour, 
				proprietaRateLimiting,
				stateless, localForward, 
				ricevutaAsincronaSimmetrica, ricevutaAsincronaAsimmetrica,
				gestioneManifest, configPortaHandler,
				false);
		List<String> l = de.getStatusValuesAsList();
		boolean print = false;
		if(l!=null && !l.isEmpty()) {
			if(l.size()>1) {
				print = true;
			}
			else {
				String s = l.get(0);
				if(s!=null && !"".equalsIgnoreCase(s)) {
					print = !CostantiConfigurazione.DISABILITATO.getValue().toLowerCase().equals(s.toLowerCase());
				}
			}
		}
		if(print) {
			printConfigurazioneFromStatusValues(de, PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_OPZIONI_AVANZATE,
					sb,
					separator, newLine);
		}
	}
	
	private static void printConfigurazionePortaDelegataExtended(ConsoleHelper consoleHelper, SoggettiCore soggettiCore, 
			String protocollo, PortaDelegata portaDelegata, MappingFruizionePortaDelegata mapping,
			StringBuilder sb,
			String separator, String newLine) throws Exception {
		IExtendedListServlet extendedServletList = soggettiCore.getExtendedServletPortaDelegata();
		if(extendedServletList!=null && extendedServletList.showExtendedInfo(consoleHelper,protocollo)){
			String stato = extendedServletList.getStatoTab(consoleHelper,portaDelegata,mapping.isDefault());
			String statoTooltip = extendedServletList.getStatoTab(consoleHelper,portaDelegata,mapping.isDefault());
			int numExtended = extendedServletList.sizeList(portaDelegata);
			DataElement de = new DataElement();
			consoleHelper.setStatoExtendedList(de, numExtended, stato, statoTooltip, false);
			String label = extendedServletList.getListTitle(consoleHelper);
			printConfigurazioneFromStatusValues(de, label,
					sb,
					separator, newLine);
		}
	}
	private static void printConfigurazionePortaApplicativaExtended(ConsoleHelper consoleHelper, SoggettiCore soggettiCore, 
			String protocollo, PortaApplicativa portaApplicativa, MappingErogazionePortaApplicativa mapping,
			StringBuilder sb,
			String separator, String newLine) throws Exception {
		IExtendedListServlet extendedServletList = soggettiCore.getExtendedServletPortaApplicativa();
		if(extendedServletList!=null && extendedServletList.showExtendedInfo(consoleHelper,protocollo)){
			String stato = extendedServletList.getStatoTab(consoleHelper,portaApplicativa,mapping.isDefault());
			String statoTooltip = extendedServletList.getStatoTab(consoleHelper,portaApplicativa,mapping.isDefault());
			int numExtended = extendedServletList.sizeList(portaApplicativa);
			DataElement de = new DataElement();
			consoleHelper.setStatoExtendedList(de, numExtended, stato, statoTooltip, false);
			String label = extendedServletList.getListTitle(consoleHelper);
			printConfigurazioneFromStatusValues(de, label,
					sb,
					separator, newLine);
		}
	}
	
	private static void printConfigurazioneFromStatusValues(DataElement de, String label,
			StringBuilder sb,
			String separator, String newLine) {
		List<String> l = de.getStatusValuesAsList();
		if(l!=null && !l.isEmpty()) {
			StringBuilder sbRT = new StringBuilder();
			for (String s : l) {
				if(sbRT.length()>0) {
					sbRT.append(", ");
				}
				sbRT.append(s);
			}
			if(sbRT.length()>0) {
				sb.append(newLine);
				sb.append(label);
				sb.append(separator);
				sb.append(sbRT.toString());
			}
		}
	}
	
	private static void printConnettoreErogazione(PortaApplicativa paDefault, PortaApplicativa paAssociata, 
			ServiziApplicativiCore saCore, ConsoleHelper consoleHelper, 
			StringBuilder sb,
			String separator, String newLine) throws Exception {
				
		if(paDefault==null) {
			throw new Exception("Param paDefault is null");
		}
		if(paAssociata==null) {
			throw new Exception("Param paAssociata is null");
		}
		if(saCore==null) {
			throw new Exception("Param saCore is null");
		}
		
		boolean connettoreMultiploEnabled = paAssociata.getBehaviour() != null;
		
		if(connettoreMultiploEnabled) {
			
			//sb.append(newLine);
			//sb.append("- "+PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI+" -");
			
			sb.append(newLine);
			//sb.append("Tipo");
			//sb.append(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI);
			//sb.append(separator);
			sb.append(consoleHelper.getNomiConnettoriMultipliPortaApplicativa(paAssociata));
			
			/*
			sb.append(newLine);
			sb.append("Elenco "+PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI);
			sb.append(separator);
			sb.append(consoleHelper.getToolTipConnettoriMultipliPortaApplicativa(paAssociata));
			*/
			
			for (PortaApplicativaServizioApplicativo paSA : paAssociata.getServizioApplicativoList()) {
				String nomeConnettore = CostantiConfigurazione.NOME_CONNETTORE_DEFAULT;
				if(paSA.getDatiConnettore() != null && paSA.getDatiConnettore().getNome() != null) {
					nomeConnettore = paSA.getDatiConnettore().getNome();
				}
				
				IDServizioApplicativo idServizioApplicativo = new IDServizioApplicativo();
				idServizioApplicativo.setIdSoggettoProprietario(new IDSoggetto(paDefault.getTipoSoggettoProprietario(), paDefault.getNomeSoggettoProprietario()));
				idServizioApplicativo.setNome(paSA.getNome());
				ServizioApplicativo sa = saCore.getServizioApplicativo(idServizioApplicativo);
				InvocazioneServizio is = sa.getInvocazioneServizio();
				org.openspcoop2.core.config.Connettore connettore = is.getConnettore();
				
				printConnettoreErogazione(consoleHelper,
						connettore, nomeConnettore,
						sa,
						sb,
						separator, newLine,
						true);
			}
		}
		else {
			IDServizioApplicativo idServizioApplicativo = new IDServizioApplicativo();
			idServizioApplicativo.setIdSoggettoProprietario(new IDSoggetto(paDefault.getTipoSoggettoProprietario(), paDefault.getNomeSoggettoProprietario()));
			idServizioApplicativo.setNome(paAssociata.getServizioApplicativo(0).getNome());
			ServizioApplicativo sa = saCore.getServizioApplicativo(idServizioApplicativo);
			InvocazioneServizio is = sa.getInvocazioneServizio();
			org.openspcoop2.core.config.Connettore connettore = is.getConnettore();
			
			printConnettoreErogazione(consoleHelper,
					connettore, null,
					sa,
					sb,
					separator, newLine,
					false);
		}
		
	}

	private static void printConnettoreFruizione(ConsoleHelper consoleHelper,
			Connettore connettore, String labelNomeConnettore,
			boolean connettoreStatic,
			StringBuilder sb,
			String separator, String newLine) throws Exception {
		
		if(connettore==null) {
			throw new Exception("Param connettore is null");
		}
		
		_printConnettore(consoleHelper,
				connettore.mappingIntoConnettoreConfigurazione(), labelNomeConnettore,
				null,
				connettoreStatic,
				sb,
				separator, newLine,
				false);
	}
	private static void printConnettoreErogazione(ConsoleHelper consoleHelper,
			org.openspcoop2.core.config.Connettore connettore, String labelNomeConnettore,
			ServizioApplicativo sa, 
			StringBuilder sb,
			String separator, String newLine,
			boolean printIntestazione) throws Exception {
		_printConnettore(consoleHelper,
				connettore, labelNomeConnettore,
				sa,
				false,
				sb,
				separator, newLine,
				printIntestazione);
	}
	private static void _printConnettore(ConsoleHelper consoleHelper,
			org.openspcoop2.core.config.Connettore connettore, String labelNomeConnettore,
			ServizioApplicativo sa, 
			boolean connettoreStatic,
			StringBuilder sb,
			String separator, String newLine,
			boolean printIntestazione) throws Exception {

		if(connettore==null) {
			throw new Exception("Param connettore is null");
		}
		
		TipiConnettore tipo = TipiConnettore.toEnumFromName(connettore.getTipo());
		if(tipo==null) {
			tipo = TipiConnettore.CUSTOM;
		}
		String labelTipoConnettore = tipo.getLabel();
		if(TipiConnettore.CUSTOM.equals(tipo)){
			try {
				ConfigurazioneCore confCore = new ConfigurazioneCore(consoleHelper.getCore());
				Plugin p = confCore.getPlugin(TipoPlugin.CONNETTORE, connettore.getTipo(), false);
				if(p!=null) {
					labelTipoConnettore = p.getLabel();
				}
				else {
					labelTipoConnettore = connettore.getTipo();
				}
			}catch(Throwable t) {
				labelTipoConnettore = connettore.getTipo();
			}
		}
		
		ConnettoreUtils.printDatiConnettore(connettore, labelTipoConnettore, labelNomeConnettore,
				sa, 
				connettoreStatic,
				sb,
				separator, newLine,
				printIntestazione);

	}
	private static String getProprieta(String nome,List<org.openspcoop2.core.config.Proprieta> list){
		if(list!=null && !list.isEmpty()){
			for (org.openspcoop2.core.config.Proprieta property : list) {
				if(property.getNome().equals(nome)){
					return property.getValore();
				}
			}
		}
		return null;
	}
	
	private static void addProfiloModISicurezza(StringBuilder sbParam, Map<String, String> map,
			String labelProtocollo, boolean rest, boolean fruizione, boolean request, 
			boolean digest, 
			String patternDatiCorniceSicurezza, String schemaDatiCorniceSicurezza,
			boolean headerDuplicati,
			String separator, String newLine) {
		
		String prefixKey = ModIUtils.getPrefixKey(fruizione, request);
								
		StringBuilder sb = new StringBuilder();
		
		// Ttl (Iat / Expiration)
		String ttlV = map.get(prefixKey+ModIUtils.API_IMPL_SICUREZZA_MESSAGGIO_TTL);
		if(StringUtils.isNotEmpty(ttlV)) {
			sb.append(newLine);
			sb.append(CostantiLabel.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_TTL_LABEL);
			sb.append(separator);
			sb.append(ttlV);
		}
		
		// Audit
		boolean audit = false;
		if(request) {
			sb.append(newLine);
			String label = rest ? CostantiLabel.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_AUDIENCE_RICHIESTA_REST_LABEL :  CostantiLabel.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_AUDIENCE_RICHIESTA_SOAP_LABEL;
			sb.append(label);
			sb.append(separator);
			sb.append(map.get(prefixKey+ModIUtils.API_IMPL_SICUREZZA_MESSAGGIO_AUDIENCE));
			audit = true;
		}
		else {
			if(fruizione) {
				
				audit = true;
				
				sb.append(newLine);
				String label = rest ? CostantiLabel.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_AUDIENCE_RISPOSTA_REST_LABEL :  CostantiLabel.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_AUDIENCE_RISPOSTA_SOAP_LABEL;
				sb.append(label);
				sb.append(separator);
				sb.append(map.get(prefixKey+ModIUtils.API_IMPL_SICUREZZA_MESSAGGIO_VERIFICA_AUDIENCE));
		
				String vAud = map.get(prefixKey+ModIUtils.API_IMPL_SICUREZZA_MESSAGGIO_AUDIENCE);
				if(StringUtils.isNotEmpty(vAud)) {
					label = rest ? CostantiLabel.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_AUDIENCE_RICHIESTA_REST_LABEL :  CostantiLabel.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_AUDIENCE_RICHIESTA_SOAP_LABEL;
					sb.append(newLine);
					sb.append(label);
					sb.append(separator);
					sb.append(vAud);
				}
			}
		}
		if(audit) {
			String label = rest ? CostantiLabel.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_AUDIENCE_RICHIESTA_REST_LABEL :  CostantiLabel.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_AUDIENCE_RICHIESTA_SOAP_LABEL;
			label = CostantiLabel.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_AUDIENCE_INTEGRITY_REST_LABEL + " " +label;
			String vAud = map.get(prefixKey+ModIUtils.API_IMPL_SICUREZZA_MESSAGGIO_INTEGRITY_AUDIENCE);
			if(StringUtils.isNotEmpty(vAud)) {
				sb.append(newLine);
				sb.append(label);
				sb.append(separator);
				sb.append(vAud);
			}
		}
				
		// TrustStore
		if( (fruizione && !request) || (!fruizione && request) ) {
			
			// truststore per i certificati
			addStore(sb, map, 
					prefixKey, false, true,
					separator, newLine);
			
			if(rest) {
				// ssl per le url (x5u)
				addStore(sb, map, 
						prefixKey, true, false,
						separator, newLine);
			}
			
		}
		
		// KeyStore
		if(!fruizione && !request) {
			addStore(sb, map, 
					prefixKey, false, false,
					separator, newLine);
		}
		
		if(fruizione && request) {
			String v = map.get(prefixKey+ModIUtils.API_IMPL_SICUREZZA_MESSAGGIO_FRUIZIONE_KEYSTORE_MODE);
			if(StringUtils.isNotEmpty(v)) {
				sb.append(newLine);
				sb.append(CostantiLabel.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_FRUIZIONE_KEYSTORE_MODE_LABEL);
				sb.append(separator);
				sb.append(v);
				
				if(CostantiLabel.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_FRUIZIONE_KEYSTORE_MODE_LABEL_FRUIZIONE.equals(v)) {
					addStore(sb, map, 
							prefixKey, false, false,
							separator, newLine);
				}
			}
		}
		
		// Firma
		if(rest) {
			
			// Algoritmo
			String v = map.get(prefixKey+ModIUtils.API_IMPL_SICUREZZA_MESSAGGIO_SIGNATURE_ALGORITHM);
			if(StringUtils.isNotEmpty(v)) {
				sb.append(newLine);
				sb.append(CostantiLabel.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_ALG_LABEL);
				sb.append(separator);
				sb.append(v);
			}
			
			// Codifica Digest
			v = map.get(prefixKey+ModIUtils.API_IMPL_SICUREZZA_MESSAGGIO_DIGEST_ENCODING);
			if(StringUtils.isNotEmpty(v)) {
				sb.append(newLine);
				sb.append(CostantiLabel.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_DIGEST_ENCODING_LABEL);
				sb.append(separator);
				sb.append(v);
			}
			
			// Header Firmati
			v = map.get(prefixKey+ModIUtils.API_IMPL_SICUREZZA_MESSAGGIO_HTTP_HEADER_FIRMATI);
			if(StringUtils.isNotEmpty(v)) {
				sb.append(newLine);
				sb.append(CostantiLabel.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_HTTP_HEADERS_REST_LABEL);
				sb.append(separator);
				sb.append(v);
			}
			
			// Posizione Certificato
			v = map.get(prefixKey+ModIUtils.API_IMPL_SICUREZZA_MESSAGGIO_RIFERIMENTO_X509);
			if(StringUtils.isNotEmpty(v)) {
				sb.append(newLine);
				sb.append(CostantiLabel.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_RIFERIMENTO_X509_LABEL);
				sb.append(separator);
				sb.append(v);
			}
			
			// Certificate Chain
			v = map.get(prefixKey+ModIUtils.API_IMPL_SICUREZZA_MESSAGGIO_CATENA_CERTIFICATI_X509);
			if(StringUtils.isNotEmpty(v)) {
				sb.append(newLine);
				sb.append(CostantiLabel.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_RIFERIMENTO_X509_X5C_USE_CERTIFICATE_CHAIN_LABEL);
				sb.append(separator);
				sb.append(v);
			}
			
			// X5U Certificate URL
			v = map.get(prefixKey+ModIUtils.API_IMPL_SICUREZZA_MESSAGGIO_X5U_CERTIFICATE_URL);
			if(StringUtils.isNotEmpty(v)) {
				sb.append(newLine);
				sb.append(CostantiLabel.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_X5U_URL_LABEL);
				sb.append(separator);
				sb.append(v);
			}
			
		}
		else {
			
			// Algoritmo
			String v = map.get(prefixKey+ModIUtils.API_IMPL_SICUREZZA_MESSAGGIO_SIGNATURE_ALGORITHM);
			if(StringUtils.isNotEmpty(v)) {
				sb.append(newLine);
				sb.append(CostantiLabel.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_ALG_LABEL);
				sb.append(separator);
				sb.append(v);
			}
			
			// Algoritmo C14N
			v = map.get(prefixKey+ModIUtils.API_IMPL_SICUREZZA_MESSAGGIO_CANONICALIZATION_ALGORITHM);
			if(StringUtils.isNotEmpty(v)) {
				sb.append(newLine);
				sb.append(CostantiLabel.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_CANONICALIZATION_ALG_LABEL);
				sb.append(separator);
				sb.append(v);
			}
			
			// Header Firmati
			v = map.get(prefixKey+ModIUtils.API_IMPL_SICUREZZA_MESSAGGIO_SOAP_HEADER_FIRMATI);
			if(StringUtils.isNotEmpty(v)) {
				sb.append(newLine);
				sb.append(CostantiLabel.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_HEADERS_SOAP_LABEL);
				sb.append(separator);
				sb.append(v);
			}
			
			// Posizione Certificato
			v = map.get(prefixKey+ModIUtils.API_IMPL_SICUREZZA_MESSAGGIO_RIFERIMENTO_X509);
			if(StringUtils.isNotEmpty(v)) {
				sb.append(newLine);
				sb.append(CostantiLabel.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RIFERIMENTO_X509_LABEL);
				sb.append(separator);
				sb.append(v);
			}
			
			// Certificate Chain
			v = map.get(prefixKey+ModIUtils.API_IMPL_SICUREZZA_MESSAGGIO_CATENA_CERTIFICATI_X509);
			if(StringUtils.isNotEmpty(v)) {
				sb.append(newLine);
				sb.append(CostantiLabel.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RIFERIMENTO_X509_BINARY_SECURITY_TOKEN_USE_CERTIFICATE_CHAIN_LABEL);
				sb.append(separator);
				sb.append(v);
			}
			
			// Signature Token
			v = map.get(prefixKey+ModIUtils.API_IMPL_SICUREZZA_MESSAGGIO_INCLUDE_SIGNATURE_TOKEN);
			if(StringUtils.isNotEmpty(v)) {
				sb.append(newLine);
				sb.append(CostantiLabel.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RIFERIMENTO_X509_BINARY_SECURITY_TOKEN_INCLUDE_SIGNATURE_TOKEN_LABEL);
				sb.append(separator);
				sb.append(v);
			}
			
		}
		
		// Info Audit
		
		if(patternDatiCorniceSicurezza!=null) {
			
			if(!CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_PATTERN_OLD.equals(patternDatiCorniceSicurezza) && schemaDatiCorniceSicurezza!=null) {
				String label = CostantiLabel.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_AUDIT_AUDIENCE_LABEL;
				String vAud = map.get(prefixKey+ModIUtils.API_IMPL_SICUREZZA_MESSAGGIO_AUDIT_AUDIENCE);
				if(StringUtils.isNotEmpty(vAud)) {
					sb.append(newLine);
					sb.append(label);
					sb.append(separator);
					sb.append(vAud);
				}
			}
			
			
			for (Map.Entry<String,String> entry : map.entrySet()) {
				String key = entry.getKey();
				String sKey = prefixKey+ModIUtils.API_IMPL_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_PREFIX;
				if(key.startsWith(sKey)) {
					String claim = key.substring(sKey.length());
					sb.append(newLine);
					sb.append(claim);
					sb.append(separator);
					String v = entry.getValue();
					sb.append(v);
				}
			}
		}
		
		// Sicurezza OAuth
		
		if( fruizione && request ) {
		
			String v = map.get(prefixKey+ModIUtils.API_IMPL_SICUREZZA_OAUTH_IDENTIFICATIVO);
			if(StringUtils.isNotEmpty(v)) {
				sb.append(newLine);
				//sb.append(CostantiLabel.LABEL_CREDENZIALI_AUTENTICAZIONE_TOKEN_CLIENT_ID);
				sb.append(CostantiLabel.LABEL_CREDENZIALI_AUTENTICAZIONE_TOKEN_CLIENT_ID_SEARCH);
				sb.append(separator);
				sb.append(v);
			}
			
			v = map.get(prefixKey+ModIUtils.API_IMPL_SICUREZZA_OAUTH_KID);
			if(StringUtils.isNotEmpty(v)) {
				sb.append(newLine);
				sb.append(CostantiLabel.LABEL_CREDENZIALI_AUTENTICAZIONE_TOKEN_KID);
				sb.append(separator);
				sb.append(v);
			}
			
		}
		
		// Intestazione e fine
		if(sb.length()>0) {
			sbParam.append(newLine);
			sbParam.append("- "+(request? "Request" : "Response")+" -");
			
			sbParam.append(sb.toString());
		}
	}
	
	private static void addStore(StringBuilder sb,Map<String, String> map, 
			String prefixKeyParam, boolean ssl, boolean truststore,
			String separator, String newLine) {
		
		String label = null;
		if(ssl) {
			label = CostantiLabel.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_MODE_LABEL;
		}
		else if(truststore) {
			label = CostantiLabel.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_MODE_LABEL;
		}
		else {
			label = CostantiLabel.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_KEYSTORE_MODE_LABEL;
		}
		
		String store = ModIUtils.getPrefixKeyStore(false, prefixKeyParam, ssl, truststore);
		String mode = map.get(store);
		if(CostantiLabel.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_STORE_MODE_LABEL_DEFAULT.equals(mode)) {
			sb.append(newLine);
			sb.append(label);
			sb.append(separator);
			sb.append(CostantiLabel.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_STORE_MODE_LABEL_DEFAULT);
		}
		else {
			
			String prefixKey = ModIUtils.getPrefixKeyStore(true, prefixKeyParam, ssl, truststore);
						
			String type = map.get(prefixKey+ModIUtils.API_IMPL_SICUREZZA_MESSAGGIO_STORE_TYPE);
			String path = map.get(prefixKey+ModIUtils.API_IMPL_SICUREZZA_MESSAGGIO_STORE_PATH);
			if(StringUtils.isNotEmpty(type)) {
			
				sb.append(newLine);
				sb.append(label);
				sb.append(separator);
				sb.append("(").append(type).append(")");
				if(StringUtils.isNotEmpty(path)) {
					sb.append(" ").append(path);
				}
				
				// OCSP
				String ocsp = map.get(prefixKey+ModIUtils.API_IMPL_SICUREZZA_MESSAGGIO_STORE_OCSP_POLICY);
				if(StringUtils.isNotEmpty(ocsp)) {
					sb.append(newLine);
					sb.append(label).append(" ").append(CostantiLabel.MODIPA_TRUSTSTORE_OCSP_LABEL);
					sb.append(separator);
					try {
						String labelOcsp = OCSPManager.getInstance().getOCSPConfig(ocsp).getLabel();
						sb.append((labelOcsp!=null && StringUtils.isNotEmpty(labelOcsp)) ? labelOcsp : ocsp);
					}catch(Throwable t) {
						sb.append(ocsp);
					}
				}
				
				// CRL
				String crl = map.get(prefixKey+ModIUtils.API_IMPL_SICUREZZA_MESSAGGIO_STORE_CRLS);
				if(StringUtils.isNotEmpty(crl)) {
					sb.append(newLine);
					sb.append(label).append(" ").append(CostantiLabel.MODIPA_TRUSTSTORE_CRLS_LABEL);
					sb.append(separator);
					sb.append(crl);
				}
				
				// AliasKey
				String aliasKey = map.get(prefixKey+ModIUtils.API_IMPL_SICUREZZA_MESSAGGIO_KEY_ALIAS);
				if(StringUtils.isNotEmpty(aliasKey)) {
					sb.append(newLine);
					sb.append(CostantiLabel.MODIPA_KEY_ALIAS_LABEL);
					sb.append(separator);
					sb.append(aliasKey);
				}
			}
		}
	}
}
