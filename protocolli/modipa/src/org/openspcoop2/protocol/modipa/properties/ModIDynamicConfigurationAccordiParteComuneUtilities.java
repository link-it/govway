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
package org.openspcoop2.protocol.modipa.properties;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDPortType;
import org.openspcoop2.core.id.IDPortTypeAzione;
import org.openspcoop2.core.id.IDResource;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.PortType;
import org.openspcoop2.core.registry.Resource;
import org.openspcoop2.core.registry.beans.AccordoServizioParteComuneSintetico;
import org.openspcoop2.core.registry.constants.ServiceBinding;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.protocol.engine.constants.Costanti;
import org.openspcoop2.protocol.engine.utils.AzioniUtils;
import org.openspcoop2.protocol.engine.utils.NamingUtils;
import org.openspcoop2.protocol.modipa.config.ModIProperties;
import org.openspcoop2.protocol.modipa.constants.ModIConsoleCostanti;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.constants.ConsoleItemType;
import org.openspcoop2.protocol.sdk.constants.ConsoleItemValueType;
import org.openspcoop2.protocol.sdk.constants.ConsoleOperationType;
import org.openspcoop2.protocol.sdk.properties.AbstractConsoleItem;
import org.openspcoop2.protocol.sdk.properties.ConsoleConfiguration;
import org.openspcoop2.protocol.sdk.properties.IConsoleHelper;
import org.openspcoop2.protocol.sdk.properties.ProtocolProperties;
import org.openspcoop2.protocol.sdk.properties.ProtocolPropertiesFactory;
import org.openspcoop2.protocol.sdk.properties.ProtocolPropertiesUtils;
import org.openspcoop2.protocol.sdk.properties.StringConsoleItem;
import org.openspcoop2.protocol.sdk.properties.StringProperty;
import org.openspcoop2.protocol.sdk.registry.IRegistryReader;
import org.openspcoop2.protocol.sdk.registry.ProtocolFiltroRicercaAccordi;
import org.openspcoop2.protocol.sdk.registry.ProtocolFiltroRicercaPortTypeAzioni;
import org.openspcoop2.protocol.sdk.registry.ProtocolFiltroRicercaRisorse;
import org.openspcoop2.protocol.sdk.registry.RegistryException;
import org.openspcoop2.protocol.sdk.registry.RegistryNotFound;
import org.openspcoop2.utils.SortedMap;
import org.slf4j.Logger;

/**
 * ModIDynamicConfigurationAccordiParteComuneUtilities
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ModIDynamicConfigurationAccordiParteComuneUtilities {
	
	private ModIDynamicConfigurationAccordiParteComuneUtilities() {}

	static void addProfiloInterazione(ModIProperties modiProperties,ConsoleConfiguration configuration, boolean rest, String httpMethod) throws ProtocolException {
		
		configuration.addConsoleItem(ProtocolPropertiesFactory.newSubTitleItem(ModIConsoleCostanti.MODIPA_API_PROFILO_INTERAZIONE_ID, 
				ModIConsoleCostanti.MODIPA_API_PROFILO_INTERAZIONE_LABEL));
		
		ModIProfiliInterazioneRESTConfig config = null;
		if(rest) {
			config = new ModIProfiliInterazioneRESTConfig(modiProperties, httpMethod, null);
		}
	
		StringConsoleItem profiloInterazioneItem = (StringConsoleItem) 
				ProtocolPropertiesFactory.newConsoleItem(ConsoleItemValueType.STRING,
				ConsoleItemType.SELECT,
				ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ID, 
				ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_LABEL);
		boolean addBloccante = true;
		boolean addNonBloccantePush = true;
		boolean addNonBloccantePull = true;
		if(rest) {
			addBloccante = config.isCompatibileBloccante();
			addNonBloccantePush = config.isCompatibileNonBloccantePush();
			addNonBloccantePull = config.isCompatibileNonBloccantePull();
		}
		if(rest) {
			profiloInterazioneItem.addLabelValue(ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_LABEL_CRUD,
					ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_VALUE_CRUD);
		}
		if(addBloccante) {
			profiloInterazioneItem.addLabelValue(ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_LABEL_BLOCCANTE,
					ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_VALUE_BLOCCANTE);
		}
		if(addNonBloccantePush || addNonBloccantePull) {
			profiloInterazioneItem.addLabelValue(ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_LABEL_NON_BLOCCANTE,
					ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_VALUE_NON_BLOCCANTE);
		}
		profiloInterazioneItem.setDefaultValue(rest ? ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_DEFAULT_REST_VALUE : ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_DEFAULT_SOAP_VALUE);
		profiloInterazioneItem.setReloadOnChange(true);
		configuration.addConsoleItem(profiloInterazioneItem);
		
		StringConsoleItem profiloInterazioneItemReadOnly = (StringConsoleItem) 
				ProtocolPropertiesFactory.newConsoleItem(ConsoleItemValueType.STRING,
				ConsoleItemType.HIDDEN,
				ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ID_INUSE_READONLY, 
				ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_LABEL);
		configuration.addConsoleItem(profiloInterazioneItemReadOnly);
		
		StringConsoleItem profiloInterazioneAsincronaItem = (StringConsoleItem) 
				ProtocolPropertiesFactory.newConsoleItem(ConsoleItemValueType.STRING,
				ConsoleItemType.HIDDEN,
				ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_ID, 
				ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_LABEL);
		profiloInterazioneAsincronaItem.setDefaultValue(ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_DEFAULT_VALUE);
		profiloInterazioneAsincronaItem.setReloadOnChange(true);
		configuration.addConsoleItem(profiloInterazioneAsincronaItem);
		
		StringConsoleItem profiloInterazioneAsincronaItemReadOnly = (StringConsoleItem) 
				ProtocolPropertiesFactory.newConsoleItem(ConsoleItemValueType.STRING,
				ConsoleItemType.HIDDEN,
				ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_ID_INUSE_READONLY, 
				ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_LABEL);
		configuration.addConsoleItem(profiloInterazioneAsincronaItemReadOnly);
		
		StringConsoleItem profiloInterazioneAsincronaRelazioneItem = (StringConsoleItem) 
				ProtocolPropertiesFactory.newConsoleItem(ConsoleItemValueType.STRING,
				ConsoleItemType.HIDDEN,
				ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO_ID, 
				ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO_LABEL);
		profiloInterazioneAsincronaRelazioneItem.setDefaultValue(ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO_DEFAULT_VALUE);
		profiloInterazioneAsincronaRelazioneItem.setReloadOnChange(true);
		configuration.addConsoleItem(profiloInterazioneAsincronaRelazioneItem);
		
		StringConsoleItem profiloInterazioneAsincronaRelazioneItemReadOnly = (StringConsoleItem) 
				ProtocolPropertiesFactory.newConsoleItem(ConsoleItemValueType.STRING,
				ConsoleItemType.HIDDEN,
				ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO_ID_INUSE_READONLY, 
				ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO_LABEL);
		configuration.addConsoleItem(profiloInterazioneAsincronaRelazioneItemReadOnly);
		
		StringConsoleItem profiloInterazioneAsincronaCorrelataApiItem = (StringConsoleItem) 
				ProtocolPropertiesFactory.newConsoleItem(ConsoleItemValueType.STRING,
				ConsoleItemType.HIDDEN,
				ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_API_RICHIESTA_CORRELATA_ID, 
				"");
		profiloInterazioneAsincronaCorrelataApiItem.setDefaultValue(ModIConsoleCostanti.MODIPA_VALUE_UNDEFINED);
		profiloInterazioneAsincronaCorrelataApiItem.setReloadOnChange(true);
		configuration.addConsoleItem(profiloInterazioneAsincronaCorrelataApiItem);
		
		StringConsoleItem profiloInterazioneAsincronaCorrelataServizioItem = (StringConsoleItem) 
				ProtocolPropertiesFactory.newConsoleItem(ConsoleItemValueType.STRING,
				ConsoleItemType.HIDDEN,
				ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_SERVIZIO_RICHIESTA_CORRELATA_ID, 
				"");
		profiloInterazioneAsincronaCorrelataServizioItem.setDefaultValue(ModIConsoleCostanti.MODIPA_VALUE_UNDEFINED);
		profiloInterazioneAsincronaCorrelataServizioItem.setReloadOnChange(true);
		configuration.addConsoleItem(profiloInterazioneAsincronaCorrelataServizioItem);
		
		StringConsoleItem profiloInterazioneAsincronaCorrelataAzioneItem = (StringConsoleItem) 
				ProtocolPropertiesFactory.newConsoleItem(ConsoleItemValueType.STRING,
				ConsoleItemType.HIDDEN,
				ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_AZIONE_RICHIESTA_CORRELATA_ID, 
				"");
		profiloInterazioneAsincronaCorrelataAzioneItem.setDefaultValue(ModIConsoleCostanti.MODIPA_VALUE_UNDEFINED);
		profiloInterazioneAsincronaCorrelataAzioneItem.setReloadOnChange(true);
		configuration.addConsoleItem(profiloInterazioneAsincronaCorrelataAzioneItem);
		
	}
	
	private static AccordoServizioParteComune getAccordoServizioParteComune(IRegistryReader registryReader, IDAccordo idAccordoSelected) throws RegistryException{
		AccordoServizioParteComune aspc = null;
		try {
			aspc = registryReader.getAccordoServizioParteComune(idAccordoSelected);
		}catch(RegistryNotFound notFound) {
			// ignore
		}
		return aspc;
	}
	private static List<IDAccordo> findIdAccordiServizioParteComune(IRegistryReader registryReader, ProtocolFiltroRicercaAccordi filtro) throws RegistryException{
		List<IDAccordo> list = null;
		try {
			list = registryReader.findIdAccordiServizioParteComune(filtro);
		}catch(RegistryNotFound notFound) {
			// ignore
		}
		return list;
	}
	
	static void updateProfiloInterazione(ModIProperties modiProperties,IProtocolFactory<?> protocolFactory,Logger log,
			ConsoleConfiguration consoleConfiguration, ConsoleOperationType consoleOperationType, ProtocolProperties properties,
			IRegistryReader registryReader, IDAccordo idAccordo, String idPortType, String idAzione, boolean rest, String httpMethod) throws ProtocolException {
		
		AbstractConsoleItem<?> profiloInterazioneItem = 	
				ProtocolPropertiesUtils.getAbstractConsoleItem(consoleConfiguration.getConsoleItem(), ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ID);
		AbstractConsoleItem<?> profiloInterazioneAsincronaItem = 	
				ProtocolPropertiesUtils.getAbstractConsoleItem(consoleConfiguration.getConsoleItem(), ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_ID);
		AbstractConsoleItem<?> profiloInterazioneAsincronaRelazioneItem = 	
				ProtocolPropertiesUtils.getAbstractConsoleItem(consoleConfiguration.getConsoleItem(), ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO_ID);
		AbstractConsoleItem<?> profiloInterazioneAsincronaCorrelataApiItem = 	
				ProtocolPropertiesUtils.getAbstractConsoleItem(consoleConfiguration.getConsoleItem(), ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_API_RICHIESTA_CORRELATA_ID);
		AbstractConsoleItem<?> profiloInterazioneAsincronaCorrelataServizioItem = 	
				ProtocolPropertiesUtils.getAbstractConsoleItem(consoleConfiguration.getConsoleItem(), ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_SERVIZIO_RICHIESTA_CORRELATA_ID);
		AbstractConsoleItem<?> profiloInterazioneAsincronaCorrelataAzioneItem = 	
				ProtocolPropertiesUtils.getAbstractConsoleItem(consoleConfiguration.getConsoleItem(), ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_AZIONE_RICHIESTA_CORRELATA_ID);
		
		ModIProfiliInterazioneRESTConfig config = null;
		boolean inUse = false;
		if(rest) {
			Resource resource = null;
			if(ConsoleOperationType.CHANGE.equals(consoleOperationType)) {
				try {
					IDResource id = new IDResource();
					id.setIdAccordo(idAccordo);
					id.setNome(idAzione);
					resource = registryReader.getResourceAccordo(id);
					
					inUse = registryReader.inUso(id);
				}catch(Exception e) {
					throw new ProtocolException(e.getMessage(), e);
				}
			}
			config = new ModIProfiliInterazioneRESTConfig(modiProperties, httpMethod, resource);
		}
		else {
			if(ConsoleOperationType.CHANGE.equals(consoleOperationType)) {
				try {
					IDPortType idPT = new IDPortType();
					idPT.setIdAccordo(idAccordo);
					idPT.setNome(idPortType);
					
					IDPortTypeAzione id = new IDPortTypeAzione();
					id.setIdPortType(idPT);
					id.setNome(idAzione);
					
					inUse = registryReader.inUso(id);
				}catch(Exception e) {
					throw new ProtocolException(e.getMessage(), e);
				}
			}
		}
		
		boolean addBloccante = true;
		boolean addNonBloccantePush = true;
		boolean addNonBloccantePull = true;
		if(rest) {
			addBloccante = config.isCompatibileBloccante();
			addNonBloccantePush = config.isCompatibileNonBloccantePush();
			addNonBloccantePull = config.isCompatibileNonBloccantePull();
		}
		
		boolean allHidden = true;
		StringProperty profiloInterazioneItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ID);
		String profiloInterazione = rest ? ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_DEFAULT_REST_VALUE : ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_DEFAULT_SOAP_VALUE;
		if(profiloInterazioneItemValue!=null && profiloInterazioneItemValue.getValue()!=null && !"".equals(profiloInterazioneItemValue.getValue())) {
			profiloInterazione = profiloInterazioneItemValue.getValue();
		}	
		
		if(!addBloccante) {
			profiloInterazioneItem.removeLabelValue(ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_LABEL_BLOCCANTE);
		}
		if(!addNonBloccantePush && !addNonBloccantePull) {
			profiloInterazioneItem.removeLabelValue(ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_LABEL_NON_BLOCCANTE);
		}
		
		if( 
				(
						ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_VALUE_BLOCCANTE.equals(profiloInterazione) 
						&&
						!addBloccante
				) 
				||
				(
						ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_VALUE_NON_BLOCCANTE.equals(profiloInterazione) 
						&&
						!addNonBloccantePush 
						&& 
						!addNonBloccantePull
				)
		) {
			profiloInterazione = rest ? ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_DEFAULT_REST_VALUE : ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_DEFAULT_SOAP_VALUE;
		}
		
		if(ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_VALUE_NON_BLOCCANTE.equals(profiloInterazione)) {
			
			allHidden = false;
			
			profiloInterazioneAsincronaItem.setType(ConsoleItemType.SELECT);
			if(addNonBloccantePush) {
				((StringConsoleItem)profiloInterazioneAsincronaItem).addLabelValue(ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_LABEL_PUSH,
						ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_VALUE_PUSH);
			}
			if(addNonBloccantePull) {
				((StringConsoleItem)profiloInterazioneAsincronaItem).addLabelValue(ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_LABEL_PULL,
						ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_VALUE_PULL);
			}
			
			StringProperty profiloInterazioneAsincronaItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_ID);
			String interazioneMode = ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_DEFAULT_VALUE;
			if(profiloInterazioneAsincronaItemValue!=null && profiloInterazioneAsincronaItemValue.getValue()!=null && !"".equals(profiloInterazioneAsincronaItemValue.getValue())) {
				interazioneMode = profiloInterazioneAsincronaItemValue.getValue();
				// verifico compatibilita
				if(ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_VALUE_PUSH.equals(interazioneMode) &&
					!addNonBloccantePush) {
					interazioneMode = ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_VALUE_PULL;
				}
				else if(ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_VALUE_PULL.equals(interazioneMode) &&
					!addNonBloccantePull) {
					interazioneMode = ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_VALUE_PUSH;
				}
			}		
			else {
				// verifico compatibilita default
				if(ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_VALUE_PUSH.equals(interazioneMode) &&
					!addNonBloccantePush) {
					interazioneMode = ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_VALUE_PULL;
				}
				else if(ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_VALUE_PULL.equals(interazioneMode) &&
					!addNonBloccantePull) {
					interazioneMode = ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_VALUE_PUSH;
				}
				if(profiloInterazioneAsincronaItemValue!=null) {
					profiloInterazioneAsincronaItemValue.setValue(interazioneMode); // imposto il default
				}
			}
			boolean push = ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_VALUE_PUSH.equals(interazioneMode);
			
			boolean addRichiesta = true;
			boolean addRichiestaStato = true;
			boolean addRisposta = true;
			if(push) {
				addRichiestaStato = false;
				if(rest) {
					addRichiesta = config.isCompatibileNonBloccantePushRequest();
					addRisposta = config.isCompatibileNonBloccantePushResponse();
				}
			}
			else {
				if(rest) {
					addRichiesta = config.isCompatibileNonBloccantePullRequest();
					addRichiestaStato = config.isCompatibileNonBloccantePullRequestState();
					addRisposta = config.isCompatibileNonBloccantePullResponse();
				}
			}
			
			profiloInterazioneAsincronaRelazioneItem.setType(ConsoleItemType.SELECT);
			if(addRichiesta) {
				((StringConsoleItem)profiloInterazioneAsincronaRelazioneItem).addLabelValue(ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO_LABEL_RICHIESTA,
						ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO_VALUE_RICHIESTA);
			}
			if(addRichiestaStato) {
				((StringConsoleItem)profiloInterazioneAsincronaRelazioneItem).addLabelValue(ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO_LABEL_RICHIESTA_STATO,
						ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO_VALUE_RICHIESTA_STATO);
			}
			if(addRisposta) {
				((StringConsoleItem)profiloInterazioneAsincronaRelazioneItem).addLabelValue(ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO_LABEL_RISPOSTA,
						ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO_VALUE_RISPOSTA);
			}
			
			StringProperty profiloRelazioneAsincronaItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO_ID);
			String relazioneMode = ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO_DEFAULT_VALUE;
			if(profiloRelazioneAsincronaItemValue!=null && profiloRelazioneAsincronaItemValue.getValue()!=null && !"".equals(profiloRelazioneAsincronaItemValue.getValue())) {
				relazioneMode = profiloRelazioneAsincronaItemValue.getValue();
			}	
			if(ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO_VALUE_RICHIESTA.equals(relazioneMode) && !addRichiesta) {
				if(addRisposta) {
					relazioneMode = ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO_VALUE_RISPOSTA;
				}
				else if(addRichiestaStato) {
					relazioneMode = ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO_VALUE_RICHIESTA_STATO;
				}
			}
			else if(ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO_VALUE_RICHIESTA_STATO.equals(relazioneMode) && !addRichiestaStato) {
				if(addRichiesta) {
					relazioneMode = ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO_VALUE_RICHIESTA;
				}
				else if(addRisposta) {
					relazioneMode = ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO_VALUE_RISPOSTA;
				}
			}
			else if(ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO_VALUE_RISPOSTA.equals(relazioneMode) && !addRisposta) {
				if(addRichiesta) {
					relazioneMode = ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO_VALUE_RICHIESTA;
				}
				else if(addRichiestaStato) {
					relazioneMode = ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO_VALUE_RICHIESTA_STATO;
				}
			}
			
			boolean request = ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO_VALUE_RICHIESTA.equals(relazioneMode);			
			if(request) {
				profiloInterazioneAsincronaCorrelataApiItem.setType(ConsoleItemType.HIDDEN);
				profiloInterazioneAsincronaCorrelataServizioItem.setType(ConsoleItemType.HIDDEN);
				profiloInterazioneAsincronaCorrelataAzioneItem.setType(ConsoleItemType.HIDDEN);
			}
			else {
				try {
										
					if(push) {
						// *** PUSH ***
						
						profiloInterazioneAsincronaCorrelataApiItem.setType(ConsoleItemType.SELECT);
						((StringConsoleItem)profiloInterazioneAsincronaCorrelataApiItem).addLabelValue(ModIConsoleCostanti.MODIPA_LABEL_UNDEFINED,ModIConsoleCostanti.MODIPA_VALUE_UNDEFINED);
						profiloInterazioneAsincronaCorrelataApiItem.setLabel(ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_API_CORRELATA_A_API_LABEL);
												
						profiloInterazioneAsincronaCorrelataServizioItem.setType(ConsoleItemType.HIDDEN);
						profiloInterazioneAsincronaCorrelataAzioneItem.setType(ConsoleItemType.HIDDEN);
												
						// listo le altre API
						ProtocolFiltroRicercaAccordi filtro = new ProtocolFiltroRicercaAccordi();
						filtro.setServiceBinding(idPortType!=null ? ServiceBinding.SOAP : ServiceBinding.REST);
						filtro.setSoggetto(new IDSoggetto(protocolFactory.createProtocolConfiguration().getTipoSoggettoDefault(), null));
						List<IDAccordo> list = findIdAccordiServizioParteComune(registryReader, filtro);
						if(list!=null && !list.isEmpty()) {
							for (IDAccordo idAccordoTrovato : list) {
								String uri = IDAccordoFactory.getInstance().getUriFromIDAccordo(idAccordoTrovato);
								((StringConsoleItem)profiloInterazioneAsincronaCorrelataApiItem).addLabelValue(NamingUtils.getLabelAccordoServizioParteComune(idAccordoTrovato),uri);
							}
						}
						
						StringProperty apiValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_API_RICHIESTA_CORRELATA_ID);
						if(apiValue!=null && apiValue.getValue()!=null && !"".equals(apiValue.getValue()) &&
								!ModIConsoleCostanti.MODIPA_VALUE_UNDEFINED.equals(apiValue.getValue())) {
							String uriAPI = apiValue.getValue();
							IDAccordo idAccordoSelected = IDAccordoFactory.getInstance().getIDAccordoFromUri(uriAPI);
						
							// Utility
							AccordoServizioParteSpecifica asps = new AccordoServizioParteSpecifica();
							asps.setTipoSoggettoErogatore(idAccordoSelected.getSoggettoReferente().getTipo());
							asps.setNomeSoggettoErogatore(idAccordoSelected.getSoggettoReferente().getNome());
							asps.setNome(idAccordoSelected.getNome());
							asps.setVersione(idAccordoSelected.getVersione());
							AccordoServizioParteComune aspcNormale = registryReader.getAccordoServizioParteComune(idAccordoSelected,false,false);
							AccordoServizioParteComuneSintetico aspcSelected = new AccordoServizioParteComuneSintetico(aspcNormale);
							boolean addTrattinoSelezioneNonEffettuata = true;
							boolean throwException = true;
							
							AccordoServizioParteComune aspc = getAccordoServizioParteComune(registryReader, idAccordoSelected);
							
							if(idPortType!=null) {
								// SOAP
								profiloInterazioneAsincronaCorrelataServizioItem.setType(ConsoleItemType.SELECT);
								((StringConsoleItem)profiloInterazioneAsincronaCorrelataServizioItem).addLabelValue(ModIConsoleCostanti.MODIPA_LABEL_UNDEFINED,ModIConsoleCostanti.MODIPA_VALUE_UNDEFINED);
								profiloInterazioneAsincronaCorrelataServizioItem.setLabel(ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_API_CORRELATA_A_SERVIZIO_LABEL);
								
								if(aspc!=null && aspc.sizePortTypeList()>0) {
									for (PortType pt : aspc.getPortTypeList()) {
										if(pt.getNome().equals(idPortType)) {
											continue;
										}
										((StringConsoleItem)profiloInterazioneAsincronaCorrelataServizioItem).addLabelValue(pt.getNome(), pt.getNome());
									}
								}
								
								StringProperty ptValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_SERVIZIO_RICHIESTA_CORRELATA_ID);
								if(ptValue!=null && ptValue.getValue()!=null && !"".equals(ptValue.getValue()) &&
										!ModIConsoleCostanti.MODIPA_VALUE_UNDEFINED.equals(ptValue.getValue())) {
								
									profiloInterazioneAsincronaCorrelataAzioneItem.setType(ConsoleItemType.SELECT);
									((StringConsoleItem)profiloInterazioneAsincronaCorrelataAzioneItem).addLabelValue(ModIConsoleCostanti.MODIPA_LABEL_UNDEFINED,ModIConsoleCostanti.MODIPA_VALUE_UNDEFINED);
									profiloInterazioneAsincronaCorrelataAzioneItem.setLabel(ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_API_CORRELATA_A_AZIONE_LABEL);
																		
									asps.setPortType(ptValue.getValue());
																		
									// listo le altre azioni
									List<String> filtraAzioniUtilizzate = new ArrayList<>();
									filtraAzioniUtilizzate.add(idAzione);
									Map<String,String> azioni = AzioniUtils.getAzioniConLabel(asps, aspcSelected, addTrattinoSelezioneNonEffettuata, throwException, 
											filtraAzioniUtilizzate, ModIConsoleCostanti.MODIPA_VALUE_UNDEFINED, ModIConsoleCostanti.MODIPA_LABEL_UNDEFINED, log); 
									if(azioni!=null && !azioni.isEmpty()) {
										for (Map.Entry<String,String> entry : azioni.entrySet()) {
											String azioneId = entry.getKey();
											String tmpInterazione = AzioniUtils.getProtocolPropertyStringValue(aspc, ptValue.getValue(), azioneId, ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ID);
											if(!ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_VALUE_NON_BLOCCANTE.equals(tmpInterazione)) {
												continue;
											}
											String tmpRuolo = AzioniUtils.getProtocolPropertyStringValue(aspc, ptValue.getValue(), azioneId, ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_ID);
											if(!ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_VALUE_PUSH.equals(tmpRuolo)) {
												continue;
											}
											String tmpRelazione = AzioniUtils.getProtocolPropertyStringValue(aspc, ptValue.getValue(), azioneId, ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO_ID);
											if(!ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO_VALUE_RICHIESTA.equals(tmpRelazione)) {
												continue;
											}
											String azioneLabel = azioni.get(azioneId);
											((StringConsoleItem)profiloInterazioneAsincronaCorrelataAzioneItem).addLabelValue(azioneLabel,azioneId);
										}
									}
								}
							}
							else {
								// REST
								
								profiloInterazioneAsincronaCorrelataAzioneItem.setType(ConsoleItemType.SELECT);
								((StringConsoleItem)profiloInterazioneAsincronaCorrelataAzioneItem).addLabelValue(ModIConsoleCostanti.MODIPA_LABEL_UNDEFINED,ModIConsoleCostanti.MODIPA_VALUE_UNDEFINED);
								profiloInterazioneAsincronaCorrelataAzioneItem.setLabel(ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_API_CORRELATA_A_RISORSA_LABEL);
								
								// listo le altre azioni
								List<String> filtraAzioniUtilizzate = new ArrayList<>();
								filtraAzioniUtilizzate.add(idAzione);
								Map<String,String> azioni = AzioniUtils.getAzioniConLabel(asps, aspcSelected, addTrattinoSelezioneNonEffettuata, throwException, 
										filtraAzioniUtilizzate, ModIConsoleCostanti.MODIPA_VALUE_UNDEFINED, ModIConsoleCostanti.MODIPA_LABEL_UNDEFINED, log); 
								if(azioni!=null && !azioni.isEmpty()) {
									for (String azioneId : azioni.keySet()) {
										String tmpInterazione = AzioniUtils.getProtocolPropertyStringValue(aspc, null, azioneId, ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ID);
										if(!ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_VALUE_NON_BLOCCANTE.equals(tmpInterazione)) {
											continue;
										}
										String tmpRuolo = AzioniUtils.getProtocolPropertyStringValue(aspc, null, azioneId, ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_ID);
										if(!ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_VALUE_PUSH.equals(tmpRuolo)) {
											continue;
										}
										String tmpRelazione = AzioniUtils.getProtocolPropertyStringValue(aspc, null, azioneId, ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO_ID);
										if(!ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO_VALUE_RICHIESTA.equals(tmpRelazione)) {
											continue;
										}
										String azioneLabel = azioni.get(azioneId);
										((StringConsoleItem)profiloInterazioneAsincronaCorrelataAzioneItem).addLabelValue(azioneLabel,azioneId);
									}
								}
							}
						}		
					}
					else {
						// *** PULL ***
						
						profiloInterazioneAsincronaCorrelataApiItem.setType(ConsoleItemType.HIDDEN);
						profiloInterazioneAsincronaCorrelataServizioItem.setType(ConsoleItemType.HIDDEN);
						
						profiloInterazioneAsincronaCorrelataAzioneItem.setType(ConsoleItemType.SELECT);
						((StringConsoleItem)profiloInterazioneAsincronaCorrelataAzioneItem).addLabelValue(ModIConsoleCostanti.MODIPA_LABEL_UNDEFINED,ModIConsoleCostanti.MODIPA_VALUE_UNDEFINED);
						profiloInterazioneAsincronaCorrelataAzioneItem.setLabel(ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_API_CORRELATA_A_LABEL);
						
						// Utility
						AccordoServizioParteSpecifica asps = new AccordoServizioParteSpecifica();
						asps.setTipoSoggettoErogatore(idAccordo.getSoggettoReferente().getTipo());
						asps.setNomeSoggettoErogatore(idAccordo.getSoggettoReferente().getNome());
						asps.setNome(idAccordo.getNome());
						asps.setVersione(idAccordo.getVersione());
						asps.setPortType(idPortType);
						AccordoServizioParteComune aspcNormale = registryReader.getAccordoServizioParteComune(idAccordo,false,false);
						AccordoServizioParteComuneSintetico aspc = new AccordoServizioParteComuneSintetico(aspcNormale);
						boolean addTrattinoSelezioneNonEffettuata = true;
						boolean throwException = true;
						
						// listo le altre azioni
						List<String> filtraAzioniUtilizzate = new ArrayList<>();
						filtraAzioniUtilizzate.add(idAzione);
						Map<String,String> azioni = AzioniUtils.getAzioniConLabel(asps, aspc, addTrattinoSelezioneNonEffettuata, throwException, 
								filtraAzioniUtilizzate, ModIConsoleCostanti.MODIPA_VALUE_UNDEFINED, ModIConsoleCostanti.MODIPA_LABEL_UNDEFINED, log); 
						if(azioni!=null && !azioni.isEmpty()) {
							for (String azioneId : azioni.keySet()) {
								String tmpInterazione = AzioniUtils.getProtocolPropertyStringValue(aspcNormale, idPortType, azioneId, ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ID);
								if(!ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_VALUE_NON_BLOCCANTE.equals(tmpInterazione)) {
									continue;
								}
								String tmpRuolo = AzioniUtils.getProtocolPropertyStringValue(aspcNormale, idPortType, azioneId, ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_ID);
								if(!ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_VALUE_PULL.equals(tmpRuolo)) {
									continue;
								}
								String tmpRelazione = AzioniUtils.getProtocolPropertyStringValue(aspcNormale, idPortType, azioneId, ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO_ID);
								if(!ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO_VALUE_RICHIESTA.equals(tmpRelazione)) {
									continue;
								}
								String azioneLabel = azioni.get(azioneId);
								((StringConsoleItem)profiloInterazioneAsincronaCorrelataAzioneItem).addLabelValue(azioneLabel,azioneId);
							}
						}

					}
				}catch(Exception e) {
					throw new ProtocolException(e.getMessage(),e);
				}
			}
		}
		
		if(allHidden) {
			profiloInterazioneAsincronaItem.setType(ConsoleItemType.HIDDEN);
			profiloInterazioneAsincronaRelazioneItem.setType(ConsoleItemType.HIDDEN);
			profiloInterazioneAsincronaCorrelataApiItem.setType(ConsoleItemType.HIDDEN);
			profiloInterazioneAsincronaCorrelataServizioItem.setType(ConsoleItemType.HIDDEN);
			profiloInterazioneAsincronaCorrelataAzioneItem.setType(ConsoleItemType.HIDDEN);
		}
		else if(inUse) {
			
			setLabelInUse(consoleConfiguration, properties, profiloInterazioneItem, 
					ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ID,
					ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ID_INUSE_READONLY);
			
			setLabelInUse(consoleConfiguration, properties, profiloInterazioneAsincronaItem, 
					ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_ID,
					ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_ID_INUSE_READONLY);
			
			setLabelInUse(consoleConfiguration, properties, profiloInterazioneAsincronaRelazioneItem, 
					ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO_ID,
					ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO_ID_INUSE_READONLY);

		}
		
	}

	static void setLabelInUse(ConsoleConfiguration consoleConfiguration, ProtocolProperties properties,
			AbstractConsoleItem<?> item, String id, String idReadOnly) throws ProtocolException {
		if(!ConsoleItemType.HIDDEN.equals(item.getType())){
			
			StringProperty itemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, id);
						
			item.setType(ConsoleItemType.HIDDEN);
			AbstractConsoleItem<?> itemReadOnly = 	
					ProtocolPropertiesUtils.getAbstractConsoleItem(consoleConfiguration.getConsoleItem(), idReadOnly);
			itemReadOnly.setType(ConsoleItemType.TEXT);
			String label = null;
			String labelDefault = null;
			if(item instanceof StringConsoleItem) {
				StringConsoleItem sci = (StringConsoleItem) item;
				SortedMap<String> map = sci.getMapLabelValues();
				if(map!=null && !map.isEmpty()) {
					for (String l : map.keys()) {
						String v = map.get(l);
						if(v!=null && v.equals(itemValue.getValue())) {
							label = l;
						}
						if(v!=null && v.equals(item.getDefaultValue())) {
							labelDefault = l;
						}
					}
				}
			}

			StringProperty itemValueReadOnly = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, idReadOnly);
			if(label!=null) {
				itemValueReadOnly.setValue(label);
			}
			else {
				itemValueReadOnly.setValue(labelDefault);
			}
		}
	}
		
	static void validateProfiloInterazione(ProtocolProperties properties,
			String idPortType, boolean rest) throws ProtocolException {
	
		StringProperty profiloInterazioneItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ID);
		String profiloInterazione = rest ? ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_DEFAULT_REST_VALUE : ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_DEFAULT_SOAP_VALUE;
		if(profiloInterazioneItemValue!=null && profiloInterazioneItemValue.getValue()!=null && !"".equals(profiloInterazioneItemValue.getValue())) {
			profiloInterazione = profiloInterazioneItemValue.getValue();
		}	
		if(ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_VALUE_NON_BLOCCANTE.equals(profiloInterazione)) {
			
			StringProperty profiloInterazioneAsincronaItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_ID);
			String interazioneMode = ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_DEFAULT_VALUE;
			if(profiloInterazioneAsincronaItemValue!=null && profiloInterazioneAsincronaItemValue.getValue()!=null && !"".equals(profiloInterazioneAsincronaItemValue.getValue())) {
				interazioneMode = profiloInterazioneAsincronaItemValue.getValue();
			}		
			boolean push = ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_VALUE_PUSH.equals(interazioneMode);
			
			StringProperty profiloRelazioneAsincronaItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO_ID);
			String relazioneMode = ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO_DEFAULT_VALUE;
			if(profiloRelazioneAsincronaItemValue!=null && profiloRelazioneAsincronaItemValue.getValue()!=null && !"".equals(profiloRelazioneAsincronaItemValue.getValue())) {
				relazioneMode = profiloRelazioneAsincronaItemValue.getValue();
			}		
			boolean request = ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO_VALUE_RICHIESTA.equals(relazioneMode);
			
			if(!request) {
				
				String labelRelazione = ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO_LABEL_RISPOSTA;
				if(ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO_VALUE_RICHIESTA_STATO.equals(relazioneMode)) {
					labelRelazione = ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO_LABEL_RICHIESTA_STATO;	
				}
				
				if(push) {
					// *** PUSH ***
					
					String prefixPushNonBloccante = "Il profilo non bloccante 'PUSH', relazione '"+labelRelazione+"'";
					
					StringProperty apiItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_API_RICHIESTA_CORRELATA_ID);
					if(apiItemValue==null || apiItemValue.getValue()==null || "".equals(apiItemValue.getValue()) ||
							ModIConsoleCostanti.MODIPA_VALUE_UNDEFINED.equals(apiItemValue.getValue())) {
						throw new ProtocolException(prefixPushNonBloccante+", richiede che sia perfezionata una correlazione verso una API che implementa il servizio di risposta");
					}
					
					if(idPortType!=null) {
						StringProperty ptItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_SERVIZIO_RICHIESTA_CORRELATA_ID);
						if(ptItemValue==null || ptItemValue.getValue()==null || "".equals(ptItemValue.getValue()) ||
								ModIConsoleCostanti.MODIPA_VALUE_UNDEFINED.equals(ptItemValue.getValue())) {
							throw new ProtocolException(prefixPushNonBloccante+", richiede che sia perfezionata una correlazione verso un servizio di risposta");
						}
					}
					
					StringProperty azioneItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_AZIONE_RICHIESTA_CORRELATA_ID);
					if(azioneItemValue==null || azioneItemValue.getValue()==null || "".equals(azioneItemValue.getValue()) ||
							ModIConsoleCostanti.MODIPA_VALUE_UNDEFINED.equals(azioneItemValue.getValue())) {
						String az = null;
						if(idPortType!=null) {
							az = "un'azione";
						}
						else {
							az = "una risorsa";
						}
						throw new ProtocolException(prefixPushNonBloccante+", richiede che sia perfezionata una correlazione verso "+az+" con relazione 'Richiesta'");
					}
				}
				else {
					// *** PULL ***
					StringProperty azioneItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_AZIONE_RICHIESTA_CORRELATA_ID);
					if(azioneItemValue==null || azioneItemValue.getValue()==null || "".equals(azioneItemValue.getValue()) ||
							ModIConsoleCostanti.MODIPA_VALUE_UNDEFINED.equals(azioneItemValue.getValue())) {
						String az = null;
						if(idPortType!=null) {
							az = "un'azione";
						}
						else {
							az = "una risorsa";
						}
						throw new ProtocolException("Il profilo non bloccante 'PULL', relazione '"+labelRelazione+"', richiede che sia perfezionata una correlazione verso "+az+" con relazione 'Richiesta'");
					}
				}
				
			}
		}
		
	}
	
	static void updateProfiloSicurezzaCanale(ConsoleConfiguration consoleConfiguration, ProtocolProperties properties) {
				
		AbstractConsoleItem<?> profiloSicurezzaCanaleItem = 	
				ProtocolPropertiesUtils.getAbstractConsoleItem(consoleConfiguration.getConsoleItem(), ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_CANALE_ID);
		StringProperty sicurezzaCanaleItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_CANALE_ID);
		
		String value = sicurezzaCanaleItemValue!=null ? sicurezzaCanaleItemValue.getValue() : null;
		if(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_CANALE_VALUE_IDAC01.equals(value)) {
			profiloSicurezzaCanaleItem.setNote(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_CANALE_LABEL_IDAC01_NOTE);
		}
		else if(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_CANALE_VALUE_IDAC02.equals(value)) {
			profiloSicurezzaCanaleItem.setNote(ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_CANALE_LABEL_IDAC02_NOTE);
		}

	}
	
	private static boolean permettiModificaNomeAccordo = true;
	public static boolean isPermettiModificaNomeAccordo() {
		return permettiModificaNomeAccordo;
	}
	public static void setPermettiModificaNomeAccordo(boolean permettiModificaNomeAccordo) {
		ModIDynamicConfigurationAccordiParteComuneUtilities.permettiModificaNomeAccordo = permettiModificaNomeAccordo;
	}

	private static int valueOfVersione(String versioneS) {
		int versione = -1;
		try {
			versione = Integer.valueOf(versioneS);
		}catch(Exception e) {
			// ignore
		}
		return versione;
	}
	
	private static List<IDResource> findIdResourceAccordo(IRegistryReader registryReader,ProtocolFiltroRicercaRisorse filtro) throws RegistryException {
		List<IDResource> list = null;
		try {
			list = registryReader.findIdResourceAccordo(filtro);
		}catch(RegistryNotFound notFound) {
			// ignore
		}
		return list;
	}
	private static List<IDPortTypeAzione> findIdAzionePortType(IRegistryReader registryReader,ProtocolFiltroRicercaPortTypeAzioni filtro) throws RegistryException {
		List<IDPortTypeAzione> list = null;
		try {
			list = registryReader.findIdAzionePortType(filtro);
		}catch(RegistryNotFound notFound) {
			// ignore
		}
		return list;
	}
	
	static void validateDynamicConfigAccordoServizioParteComune(ConsoleOperationType consoleOperationType, IConsoleHelper consoleHelper, ProtocolProperties properties, 
			IRegistryReader registryReader, IDAccordo id) throws ProtocolException{
		
		boolean rest = isApiRest(consoleOperationType, consoleHelper, registryReader, id);
		
		// Lascio il codice se servisse, ma è stato aggiunto la gestione sull'update dell'API
		if(!permettiModificaNomeAccordo && ConsoleOperationType.CHANGE.equals(consoleOperationType) && id!=null) {
			try {
				String apiGestioneParziale = consoleHelper.getParameter(Costanti.CONSOLE_PARAMETRO_APC_API_GESTIONE_PARZIALE);
				if(Costanti.CONSOLE_VALORE_PARAMETRO_APC_API_INFORMAZIONI_GENERALI.equals(apiGestioneParziale)) {
					String nome = consoleHelper.getParameter(Costanti.CONSOLE_PARAMETRO_APC_NOME);
					String versioneS = consoleHelper.getParameter(Costanti.CONSOLE_PARAMETRO_APC_VERSIONE);
					int versione = valueOfVersione(versioneS);
					if(nome!=null && versione>0 &&
						(!id.getNome().equals(nome) || id.getVersione().intValue()!=versione)
						) {
							
						AccordoServizioParteComune as = registryReader.getAccordoServizioParteComune(id);
						if(ServiceBinding.REST.equals(as.getServiceBinding())){
							
							ProtocolFiltroRicercaRisorse filtro = new ProtocolFiltroRicercaRisorse();
							ProtocolProperties protocolPropertiesResources = new ProtocolProperties();
							protocolPropertiesResources.addProperty(ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_API_RICHIESTA_CORRELATA_ID, IDAccordoFactory.getInstance().getUriFromIDAccordo(id));
							filtro.setProtocolPropertiesRisorsa(protocolPropertiesResources);
							List<IDResource> list = findIdResourceAccordo(registryReader, filtro);
							if(list!=null && !list.isEmpty()) {
								// ne dovrebbe esistere solo una.
								IDResource idR = list.get(0);
								String uriAPI = NamingUtils.getLabelAccordoServizioParteComune(idR.getIdAccordo());
								Resource resource = registryReader.getResourceAccordo(idR);
								String labelR = NamingUtils.getLabelResource(resource);
								throw new ProtocolException("Non è possibile modificare le informazioni generali dell'API poichè riferita dalla risorsa '"+labelR+"' dell'API '"+uriAPI+"' (Profilo non bloccante PUSH)");
							}
							
						}
						else {
						
							ProtocolFiltroRicercaPortTypeAzioni filtro = new ProtocolFiltroRicercaPortTypeAzioni();
							ProtocolProperties protocolPropertiesAzioni = new ProtocolProperties();
							protocolPropertiesAzioni.addProperty(ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_API_RICHIESTA_CORRELATA_ID, IDAccordoFactory.getInstance().getUriFromIDAccordo(id));
							filtro.setProtocolPropertiesAzione(protocolPropertiesAzioni);
							List<IDPortTypeAzione> list = findIdAzionePortType(registryReader,filtro);
							if(list!=null && !list.isEmpty()) {
								// ne dovrebbe esistere solo una.
								IDPortTypeAzione idA = list.get(0);
								String uriAPI = NamingUtils.getLabelAccordoServizioParteComune(idA.getIdPortType().getIdAccordo());
								throw new ProtocolException("Non è possibile modificare le informazioni generali dell'API poichè riferita dall'azione '"+idA.getNome()+"' del Servizio '"+idA.getIdPortType().getNome()+"' nell'API '"+uriAPI+"' (Profilo non bloccante PUSH)");
							}
							
						}
					}
				}
			}catch(Exception e) {
				throw new ProtocolException(e.getMessage(),e);
			}
			
		}
		
		ModIDynamicConfigurationAccordiParteComuneSicurezzaMessaggioUtilities.validateProfiloSicurezzaMessaggio(properties, rest);
	}
	
	static boolean isApiRest(ConsoleOperationType consoleOperationType, IConsoleHelper consoleHelper,
			IRegistryReader registryReader, IDAccordo id) throws ProtocolException {
		boolean rest = true;
		AccordoServizioParteComune aspc = null;
		if(ConsoleOperationType.CHANGE.equals(consoleOperationType) && id!=null) {
			try {
				aspc = registryReader.getAccordoServizioParteComune(id, false, false);
			}catch(RegistryNotFound notFound) {
				// ignore
			}
			catch(Exception e) {
				throw new ProtocolException(e.getMessage(),e);
			}
		}
		if(aspc!=null) {
			rest = ServiceBinding.REST.equals(aspc.getServiceBinding());
		}
		else {
			try {
				String serviceBinding = consoleHelper.getParameter(Costanti.CONSOLE_PARAMETRO_SERVICE_BINDING);
				rest = !ServiceBinding.SOAP.name().equals(serviceBinding);
			}catch(Exception e) {
				throw new ProtocolException(e.getMessage(),e);
			}
		}
		return rest;
	}
}
