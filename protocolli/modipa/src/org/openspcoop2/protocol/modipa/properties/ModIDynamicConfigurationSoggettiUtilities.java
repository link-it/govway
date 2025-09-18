/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it). 
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

import org.apache.commons.lang3.StringUtils;
import org.openspcoop2.core.config.ConfigurazioneMultitenant;
import org.openspcoop2.core.constants.CostantiLabel;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.mvc.properties.provider.InputValidationUtils;
import org.openspcoop2.core.registry.ProtocolProperty;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.core.registry.constants.PddTipologia;
import org.openspcoop2.protocol.engine.constants.Costanti;
import org.openspcoop2.protocol.modipa.config.ModIProperties;
import org.openspcoop2.protocol.modipa.constants.ModIConsoleCostanti;
import org.openspcoop2.protocol.modipa.constants.ModICostanti;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.constants.ConsoleItemType;
import org.openspcoop2.protocol.sdk.constants.ConsoleItemValueType;
import org.openspcoop2.protocol.sdk.constants.ConsoleOperationType;
import org.openspcoop2.protocol.sdk.properties.AbstractConsoleItem;
import org.openspcoop2.protocol.sdk.properties.BaseConsoleItem;
import org.openspcoop2.protocol.sdk.properties.ConsoleConfiguration;
import org.openspcoop2.protocol.sdk.properties.IConsoleHelper;
import org.openspcoop2.protocol.sdk.properties.ProtocolProperties;
import org.openspcoop2.protocol.sdk.properties.ProtocolPropertiesFactory;
import org.openspcoop2.protocol.sdk.properties.ProtocolPropertiesUtils;
import org.openspcoop2.protocol.sdk.properties.StringConsoleItem;
import org.openspcoop2.protocol.sdk.properties.StringProperty;
import org.openspcoop2.protocol.sdk.registry.IConfigIntegrationReader;
import org.openspcoop2.protocol.sdk.registry.IRegistryReader;
import org.openspcoop2.protocol.sdk.registry.ProtocolFiltroRicercaSoggetti;
import org.openspcoop2.protocol.sdk.registry.RegistryNotFound;

/**
 * ModIDynamicConfigurationSoggettiUtilities
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ModIDynamicConfigurationSoggettiUtilities {

	private ModIDynamicConfigurationSoggettiUtilities() {}
	
	static ConsoleConfiguration getDynamicConfigSoggetto(ConsoleOperationType consoleOperationType, IConsoleHelper consoleHelper, 
			IRegistryReader registryReader, IConfigIntegrationReader configIntegrationReader,
			IDSoggetto id) throws ProtocolException {
		
		boolean esterno = isEsterno(consoleOperationType, consoleHelper, registryReader, id);
		
		ConsoleConfiguration configuration = new ConsoleConfiguration();
		
		BaseConsoleItem titolo = ProtocolPropertiesFactory.newTitleItem(
				ModIConsoleCostanti.MODIPA_SOGGETTI_ID, 
				ModIConsoleCostanti.MODIPA_SOGGETTI_LABEL);
		configuration.addConsoleItem(titolo );
		
		
		BaseConsoleItem subTitlePdnd = ProtocolPropertiesFactory.newSubTitleItem(
				ModIConsoleCostanti.MODIPA_SOGGETTI_PDND_ID, 
				ModIConsoleCostanti.MODIPA_SOGGETTI_PDND_LABEL);
		configuration.addConsoleItem(subTitlePdnd );
		
		
		StringConsoleItem tokenClientIdItem = (StringConsoleItem) 
				ProtocolPropertiesFactory.newConsoleItem(ConsoleItemValueType.STRING,
				ConsoleItemType.TEXT_AREA,
				ModIConsoleCostanti.MODIPA_SOGGETTI_ID_ENTE_ID, 
				ModIConsoleCostanti.MODIPA_SOGGETTI_ID_ENTE_LABEL);
		tokenClientIdItem.setRows(ModIConsoleCostanti.MODIPA_SOGGETTI_ID_ENTE_ROWS);
		tokenClientIdItem.setRequired(false);
		configuration.addConsoleItem(tokenClientIdItem);
		
		
		if (!esterno && ModIProperties.getInstance().isTracingPDNDEnabled()) {
			
			ConfigurazioneMultitenant multitenant = null;
			try {
				if(configIntegrationReader!=null) {
					multitenant = configIntegrationReader.getConfigurazioneMultitenant();
				}
			}catch(Exception e) {
				throw new ProtocolException(e.getMessage(),e);
			}
			boolean multitenantEnabled = multitenant!=null && org.openspcoop2.core.config.constants.StatoFunzionalita.ABILITATO.equals(multitenant.getStato());
			
			getDynamicConfigSoggettoTracciamentoPdnd(multitenantEnabled, configuration,
					registryReader,
					id);
		}
		return configuration;
	}
	private static boolean isEsterno(ConsoleOperationType consoleOperationType, IConsoleHelper consoleHelper, IRegistryReader registryReader, IDSoggetto id) throws ProtocolException {
		boolean esterno = false;
		try {
			String dominio = consoleHelper.getParameter(Costanti.CONSOLE_PARAMETRO_SOGGETTO_DOMINIO);
			if( 
					(dominio==null || "".equals(dominio)) 
					&&
					ConsoleOperationType.CHANGE.equals(consoleOperationType)
				) {
				Soggetto soggetto = registryReader.getSoggetto(id);
				if(soggetto.getPortaDominio()==null || "".equals(soggetto.getPortaDominio())) {
					dominio = PddTipologia.ESTERNO.toString();
				}
				else {
					List<String> pddOperative = getPddOperative(registryReader);
					if(pddOperative==null || pddOperative.isEmpty() || !pddOperative.contains(soggetto.getPortaDominio())) {
						dominio = PddTipologia.ESTERNO.toString();	
					}
					else {
						dominio = PddTipologia.OPERATIVO.toString();
					}
				}
			}
			esterno = PddTipologia.ESTERNO.toString().equals(dominio);
		}catch(Exception e) {
			throw new ProtocolException(e.getMessage(),e);
		}
		return esterno;
	}
	private static void getDynamicConfigSoggettoTracciamentoPdnd(boolean multitenantEnabled, ConsoleConfiguration configuration,
			IRegistryReader registryReader,
			IDSoggetto id) throws ProtocolException {
		if(multitenantEnabled) {
			BaseConsoleItem subTitleTracciamentoPdnd = ProtocolPropertiesFactory.newSubTitleItem(
					ModIConsoleCostanti.MODIPA_SOGGETTI_PDND_TRACING_TITLE_ID, 
					ModIConsoleCostanti.MODIPA_SOGGETTI_PDND_TRACING_TITLE);
			configuration.addConsoleItem(subTitleTracciamentoPdnd );
		}
		
		StringConsoleItem soggettoPdndTracingEnabledItem = (StringConsoleItem) 
				ProtocolPropertiesFactory.newConsoleItem(ConsoleItemValueType.STRING,
				ConsoleItemType.SELECT,
				ModIConsoleCostanti.MODIPA_SOGGETTI_PDND_TRACING_ID, 
				multitenantEnabled ? ModIConsoleCostanti.MODIPA_SOGGETTI_PDND_TRACING_LABEL : ModIConsoleCostanti.MODIPA_SOGGETTI_PDND_TRACING_TITLE);
		soggettoPdndTracingEnabledItem.addLabelValue(ModIConsoleCostanti.MODIPA_SOGGETTI_PDND_TRACING_DEFAULT_LABEL, ModIConsoleCostanti.MODIPA_SOGGETTI_PDND_TRACING_DEFAULT_ID);
		soggettoPdndTracingEnabledItem.addLabelValue(ModIConsoleCostanti.MODIPA_SOGGETTI_PDND_TRACING_ENABLE_LABEL, ModIConsoleCostanti.MODIPA_SOGGETTI_PDND_TRACING_ENABLE_ID);
		soggettoPdndTracingEnabledItem.addLabelValue(ModIConsoleCostanti.MODIPA_SOGGETTI_PDND_TRACING_DISABLE_LABEL, ModIConsoleCostanti.MODIPA_SOGGETTI_PDND_TRACING_DISABLE_ID);
		soggettoPdndTracingEnabledItem.setDefaultValue(ModIConsoleCostanti.MODIPA_SOGGETTI_PDND_TRACING_DEFAULT_ID);
		if(multitenantEnabled) {
			soggettoPdndTracingEnabledItem.setReloadOnChange(true);
		}
		configuration.addConsoleItem(soggettoPdndTracingEnabledItem);
		
		
		if(multitenantEnabled) {
			StringConsoleItem soggettoPdndTracingAggregatoItem = (StringConsoleItem) 
					ProtocolPropertiesFactory.newConsoleItem(ConsoleItemValueType.STRING,
					ConsoleItemType.SELECT,
					ModIConsoleCostanti.MODIPA_SOGGETTI_PDND_TRACING_AGGREGATO_ID, 
					ModIConsoleCostanti.MODIPA_SOGGETTI_PDND_TRACING_AGGREGATO_LABEL);
			soggettoPdndTracingAggregatoItem.setNote(ModIConsoleCostanti.MODIPA_SOGGETTI_PDND_TRACING_AGGREGATO_NOTE);
			List<String> soggettiOperativiNonAggregati = getSoggettiOperativiNonAggregati(registryReader);
			soggettoPdndTracingAggregatoItem.addLabelValue(ModIConsoleCostanti.MODIPA_LABEL_UNDEFINED,ModIConsoleCostanti.MODIPA_VALUE_UNDEFINED);
			if(!soggettiOperativiNonAggregati.isEmpty()) {
				for (String s : soggettiOperativiNonAggregati) {
					if(id!=null && id.getNome()!=null && id.getNome().equals(s)) {
						continue;
					}
					soggettoPdndTracingAggregatoItem.addLabelValue(s,s);
				}
			}
			soggettoPdndTracingAggregatoItem.setDefaultValue(ModIConsoleCostanti.MODIPA_VALUE_UNDEFINED);
			configuration.addConsoleItem(soggettoPdndTracingAggregatoItem);
			
			
			StringConsoleItem soggettoPdndTracingAggregatoNoteItem = (StringConsoleItem) 
					ProtocolPropertiesFactory.newConsoleItem(ConsoleItemValueType.STRING,
					ConsoleItemType.TEXT,
					ModIConsoleCostanti.MODIPA_SOGGETTI_PDND_TRACING_AGGREGATO_NOTE_ID, 
					ModIConsoleCostanti.MODIPA_SOGGETTI_PDND_TRACING_AGGREGATO_NOTE_LABEL);
			configuration.addConsoleItem(soggettoPdndTracingAggregatoNoteItem);
		}
	}
	
	
	static void updateDynamicConfigSoggetto(ConsoleConfiguration consoleConfiguration,
			ConsoleOperationType consoleOperationType, IConsoleHelper consoleHelper, 
			ProtocolProperties properties,
			IConfigIntegrationReader configIntegrationReader, IRegistryReader registryReader, IDSoggetto id) throws ProtocolException {
		
		if(consoleOperationType!=null || consoleHelper!=null) {
			// nop
		}
		
		ConfigurazioneMultitenant multitenant = null;
		try {
			if(configIntegrationReader!=null) {
				multitenant = configIntegrationReader.getConfigurazioneMultitenant();
			}
		}catch(Exception e) {
			throw new ProtocolException(e.getMessage(),e);
		}
		boolean multitenantEnabled = multitenant!=null && org.openspcoop2.core.config.constants.StatoFunzionalita.ABILITATO.equals(multitenant.getStato());
		
		if(multitenantEnabled) {
			
			StringProperty idTracciamentoItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_SOGGETTI_PDND_TRACING_ID);
			boolean tracciamentoAbilitato = idTracciamentoItemValue!=null && idTracciamentoItemValue.getValue()!=null && ModIConsoleCostanti.MODIPA_SOGGETTI_PDND_TRACING_ENABLE_ID.equals(idTracciamentoItemValue.getValue());
			
			boolean isSoggettoAggregatore = false;
			if(id!=null && tracciamentoAbilitato) {
				isSoggettoAggregatore = isSoggettoAggregatore(id.getNome(), registryReader);
			}
			updateDynamicConfigSoggettoAggregato(tracciamentoAbilitato, isSoggettoAggregatore, consoleConfiguration, properties,
					id, registryReader);
		}
		
	}
	private static void updateDynamicConfigSoggettoAggregato(boolean tracciamentoAbilitato, boolean isSoggettoAggregatore, ConsoleConfiguration consoleConfiguration, ProtocolProperties properties,
			IDSoggetto idSoggetto, IRegistryReader registryReader) throws ProtocolException {
				
		updateDynamicConfigSoggettoAggregatoGestioneSelectList(consoleConfiguration, properties,
				tracciamentoAbilitato, isSoggettoAggregatore);
		
		updateDynamicConfigSoggettoAggregatoGestioneNote(consoleConfiguration, properties,
				tracciamentoAbilitato, isSoggettoAggregatore, 
				idSoggetto, registryReader);
		
	}
	private static void updateDynamicConfigSoggettoAggregatoGestioneSelectList(ConsoleConfiguration consoleConfiguration, ProtocolProperties properties,
			boolean tracciamentoAbilitato, boolean isSoggettoAggregatore) throws ProtocolException {
		
		AbstractConsoleItem<?> idAggregatoItemV = null; 	
		if(consoleConfiguration!=null) {
			idAggregatoItemV = ProtocolPropertiesUtils.getAbstractConsoleItem(consoleConfiguration.getConsoleItem(),
						ModIConsoleCostanti.MODIPA_SOGGETTI_PDND_TRACING_AGGREGATO_ID
						);
		}
		StringProperty idAggregatoItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_SOGGETTI_PDND_TRACING_AGGREGATO_ID);
		
		if(!tracciamentoAbilitato || isSoggettoAggregatore) {
			if(idAggregatoItemV!=null) {
				idAggregatoItemV.setType(ConsoleItemType.HIDDEN);
			}
			
			if(idAggregatoItemValue!=null) {
				idAggregatoItemValue.setValue(null);
			}
		}
		else {
			if(idAggregatoItemV!=null) {
				idAggregatoItemV.setType(ConsoleItemType.SELECT);
			}
		}
	}
	private static void updateDynamicConfigSoggettoAggregatoGestioneNote(ConsoleConfiguration consoleConfiguration, ProtocolProperties properties,
			boolean tracciamentoAbilitato, boolean isSoggettoAggregatore, 
			IDSoggetto idSoggetto, IRegistryReader registryReader) throws ProtocolException {
		
		AbstractConsoleItem<?> idAggregatoNoteItemV = null; 	
		if(consoleConfiguration!=null) {
			idAggregatoNoteItemV = ProtocolPropertiesUtils.getAbstractConsoleItem(consoleConfiguration.getConsoleItem(),
					ModIConsoleCostanti.MODIPA_SOGGETTI_PDND_TRACING_AGGREGATO_NOTE_ID
					);
		}
		StringProperty idAggregatoNoteItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_SOGGETTI_PDND_TRACING_AGGREGATO_NOTE_ID);
		
		if(tracciamentoAbilitato && isSoggettoAggregatore) {
			if(idAggregatoNoteItemV!=null) {
				idAggregatoNoteItemV.setType(ConsoleItemType.TEXT);
			}
			
			processAggregateNote(idAggregatoNoteItemV, idAggregatoNoteItemValue,
					idSoggetto, registryReader);
		}
		else {
			if(idAggregatoNoteItemV!=null) {
				idAggregatoNoteItemV.setType(ConsoleItemType.HIDDEN);
			}
			if(idAggregatoNoteItemValue!=null) {
				idAggregatoNoteItemValue.setValue(null);
			}
		}
	}
	private static void processAggregateNote(AbstractConsoleItem<?> idAggregatoNoteItemV, StringProperty idAggregatoNoteItemValue,
			IDSoggetto idSoggetto, IRegistryReader registryReader) throws ProtocolException {
		if(idAggregatoNoteItemValue!=null) {
			boolean addNote = false;
			if(idSoggetto!=null && idSoggetto.getNome()!=null) {
				List<String> l = getSoggettiOperativiRiferisconoSoggettoAggregato(idSoggetto.getNome(), registryReader);
				if(!l.isEmpty()) {
					idAggregatoNoteItemValue.setValue(StringUtils.join(l, ", "));
					addNote = true;
				}
			}
			if(!addNote) {
				idAggregatoNoteItemValue.setValue(null);
				if(idAggregatoNoteItemV!=null) {
					idAggregatoNoteItemV.setType(ConsoleItemType.HIDDEN);
				}
			}
		}
	}
	
	static void validateDynamicConfigSoggetto(ConsoleConfiguration consoleConfiguration,
			ConsoleOperationType consoleOperationType, IConsoleHelper consoleHelper, ProtocolProperties properties, 
			IConfigIntegrationReader configIntegrationReader, IDSoggetto id,
			IRegistryReader registryReader) throws ProtocolException {
		
		if(consoleConfiguration!=null && consoleOperationType!=null && consoleHelper!=null && configIntegrationReader!=null && id!=null) {
			// nop
		}
		
		validateDynamicConfigSoggettoIdEnte(properties, id, registryReader);
		
		ConfigurazioneMultitenant multitenant = null;
		try {
			if(configIntegrationReader!=null) {
				multitenant = configIntegrationReader.getConfigurazioneMultitenant();
			}
		}catch(Exception e) {
			throw new ProtocolException(e.getMessage(),e);
		}
		boolean multitenantEnabled = multitenant!=null && org.openspcoop2.core.config.constants.StatoFunzionalita.ABILITATO.equals(multitenant.getStato());

		if(multitenantEnabled) {
			
			StringProperty idTracciamentoItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_SOGGETTI_PDND_TRACING_ID);
			boolean tracciamentoAbilitato = idTracciamentoItemValue!=null && idTracciamentoItemValue.getValue()!=null && ModIConsoleCostanti.MODIPA_SOGGETTI_PDND_TRACING_ENABLE_ID.equals(idTracciamentoItemValue.getValue());
			
			boolean isSoggettoAggregatore = false;
			if(id!=null) {
				isSoggettoAggregatore = isSoggettoAggregatore(id.getNome(), registryReader);
			}
			if(!tracciamentoAbilitato && isSoggettoAggregatore) {
				throw new ProtocolException(ModIConsoleCostanti.MODIPA_SOGGETTI_PDND_TRACING_AGGREGATO_ERROR);
			}
			
			validateDynamicConfigSoggettoAggregato( properties, registryReader);
			
		}

	}
	private static void validateDynamicConfigSoggettoIdEnte(ProtocolProperties properties, IDSoggetto id, IRegistryReader registryReader) throws ProtocolException {
		StringProperty idEnteItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_SOGGETTI_ID_ENTE_ID);
		if(idEnteItemValue!=null && idEnteItemValue.getValue()!=null && StringUtils.isNotEmpty(idEnteItemValue.getValue())) {
			try {
				InputValidationUtils.validateTextAreaInput(idEnteItemValue.getValue(), 
						ModIConsoleCostanti.MODIPA_SOGGETTI_ID_ENTE_LABEL);
			}catch(Exception e) {
				throw new ProtocolException(e.getMessage(),e);
			}
			if(ModIProperties.getInstance().isPdndProducerIdCheckUnique()) {
				validatePdndInfoIdExists(registryReader, id, 
						ModIConsoleCostanti.MODIPA_SOGGETTI_ID_ENTE_ID, ModIConsoleCostanti.MODIPA_SOGGETTI_ID_ENTE_LABEL, idEnteItemValue.getValue());
			}
		}
	}
	private static void validateDynamicConfigSoggettoAggregato(ProtocolProperties properties, IRegistryReader registryReader) throws ProtocolException {
		
		StringProperty idAggregatoItemValue = (StringProperty) ProtocolPropertiesUtils.getAbstractPropertyById(properties, ModIConsoleCostanti.MODIPA_SOGGETTI_PDND_TRACING_AGGREGATO_ID);
		
		if(idAggregatoItemValue!=null && idAggregatoItemValue.getValue()!=null && StringUtils.isNotEmpty(idAggregatoItemValue.getValue()) && !ModICostanti.MODIPA_VALUE_UNDEFINED.equals(idAggregatoItemValue.getValue())) {
			String soggettoSelezionato = idAggregatoItemValue.getValue();
			// Devo verificare che sia un soggetto non aggregato
			List<String> soggettiOperativiNonAggregati = getSoggettiOperativiNonAggregati(registryReader);
			if(!soggettiOperativiNonAggregati.contains(soggettoSelezionato)) {
				throw new ProtocolException("Il soggetto selezionato '"+soggettoSelezionato+"' risulta già aggregato in altri report");
			}
		}
		
	}
	
	private static void validatePdndInfoIdExists(IRegistryReader registryReader, IDSoggetto idSoggetto, 
			String id, String label, String idValue) throws ProtocolException {
		ProtocolFiltroRicercaSoggetti filtro = new ProtocolFiltroRicercaSoggetti();
		filtro.setProtocolProperties(new ProtocolProperties());
		filtro.getProtocolProperties().addProperty(id, idValue);
		List<IDSoggetto> list = null;
		try {
			list = registryReader.findIdSoggetti(filtro);
			if(list!=null && !list.isEmpty()) {
				for (IDSoggetto check : list) {
					if(!check.equals(idSoggetto)) {
						String msg = "Il soggetto '"+check.getNome()+"' risulta già registrata con il campo '"+label+"' valorizzato con l'identificativo fornito";
						msg = msg + " '"+idValue+"'";
						throw new ProtocolException(msg);
					}
				}
			}
		}catch(RegistryNotFound notFound) {
			// ignore
		}catch(Exception e) {
			throw new ProtocolException(e.getMessage(),e);
		}
		
	}
	
	public static List<String> getPddOperative(IRegistryReader registryReader) throws ProtocolException{
		List<String> pddOperative = null;
		try {
			pddOperative = registryReader.findIdPorteDominio(true);
		}catch(RegistryNotFound notFound) {
			// ignore
		}catch(Exception e) {
			throw new ProtocolException(e.getMessage(),e);
		}
		return pddOperative;
	}
	
	public static List<String> getSoggettiOperativi(IRegistryReader registryReader) throws ProtocolException{
		List<String> listPddOperative = getPddOperative(registryReader);
		if(listPddOperative==null || listPddOperative.isEmpty()) {
			throw new ProtocolException("Non risultano configurate porte di dominio di tipo 'operativo'"); // non dovrebbe succedere
		}
		List<String> listSoggettiOperativi = new ArrayList<>();
		ProtocolFiltroRicercaSoggetti filtro = new ProtocolFiltroRicercaSoggetti();
		filtro.setTipo(CostantiLabel.MODIPA_PROTOCOL_NAME);
		for (String pdd : listPddOperative) {
			fillSoggettiOperativi(registryReader, listSoggettiOperativi, filtro, pdd);
		}
		if(listSoggettiOperativi.isEmpty()) {
			throw new ProtocolException("Non risultano configurati soggetti di tipo 'operativo'"); // non dovrebbe succedere
		}
		return listSoggettiOperativi;
	}
	private static void fillSoggettiOperativi(IRegistryReader registryReader, List<String> listSoggettiOperativi, ProtocolFiltroRicercaSoggetti filtro, String pdd){
		filtro.setNomePdd( pdd);
		try {
			List<IDSoggetto> l = registryReader.findIdSoggetti(filtro);
			if(l!=null && !l.isEmpty()) {
				for (IDSoggetto idS : l) {
					if(!listSoggettiOperativi.contains(idS.getNome())) {
						listSoggettiOperativi.add(idS.getNome());
					}
				}
			}
		}catch(Exception notFound) {
			// ignore		
		}
	}
	
	public static List<String> getSoggettiOperativiAggregati(IRegistryReader registryReader) throws ProtocolException{
		List<String> listSoggettiOperativi = getSoggettiOperativi(registryReader);
		return filtraSoggettiAggregati(listSoggettiOperativi, registryReader);
	}
	public static List<String> getSoggettiOperativiNonAggregati(IRegistryReader registryReader) throws ProtocolException{
		List<String> listSoggettiOperativi = getSoggettiOperativi(registryReader);
		return filtraSoggettiNonAggregati(listSoggettiOperativi, registryReader);
	}
	
	private static List<String> filtraSoggettiAggregati(List<String> list, IRegistryReader registryReader) {
		return filtraSoggettiAggregati(list, registryReader, true);
	}
	private static List<String> filtraSoggettiNonAggregati(List<String> list, IRegistryReader registryReader) {
		return filtraSoggettiAggregati(list, registryReader, false);
	}
	private static List<String> filtraSoggettiAggregati(List<String> list, IRegistryReader registryReader, boolean includiSoloSoggettiAggregati) {
		List<String> newList = new ArrayList<>();
		if(list!=null && !list.isEmpty()) {
			filtraSoggettiAggregati(list, registryReader, includiSoloSoggettiAggregati, newList);
		}
		return newList;
	}
	private static void filtraSoggettiAggregati(List<String> list, IRegistryReader registryReader, boolean includiSoloSoggettiAggregati, List<String> newList){
		for (String s : list) {
			boolean soggettoAggregato = isSoggettoAggregato(s, registryReader);
			if(soggettoAggregato) {
				// aggregato
				if(includiSoloSoggettiAggregati) {
					newList.add(s);
				}
			}
			else {
				// non aggregato
				if(!includiSoloSoggettiAggregati) {
					newList.add(s);
				}
			}
		}
	}
	
	private static boolean isSoggettoAggregato(String nome, IRegistryReader registryReader) {
		return readInfoSoggettoAggregato(nome, registryReader)!=null;
	}
	
	private static boolean isSoggettoAggregatore(String nome, IRegistryReader registryReader) throws ProtocolException {
		List<String> listSoggettiOperativi = getSoggettiOperativi(registryReader);
		if(!listSoggettiOperativi.isEmpty()) {
			for (String s : listSoggettiOperativi) {
				String check = readInfoSoggettoAggregato(s, registryReader);
				if(nome!=null && nome.equals(check)) {
					return true;
				}
			}
		}
		return false;
	}
	
	private static String readInfoSoggettoAggregato(String nome, IRegistryReader registryReader) {
		IDSoggetto idS = new IDSoggetto(Costanti.MODIPA_PROTOCOL_NAME, nome);
		Soggetto sog = null;
		try {
			sog = registryReader.getSoggetto(idS);
			if(sog!=null && sog.sizeProtocolPropertyList()>0) {
				for (ProtocolProperty pp : sog.getProtocolProperty()) {
					if(ModICostanti.MODIPA_SOGGETTI_PDND_TRACING_AGGREGATO_ID.equals(pp.getName())) {
						if(pp.getValue()!=null &&
								org.apache.commons.lang3.StringUtils.isNoneEmpty(pp.getValue()) &&
								!ModICostanti.MODIPA_VALUE_UNDEFINED.equals(pp.getValue())) {
							return pp.getValue();
						}
						break;
					}
				}
			}
		}catch(Exception e) {
			// ignore
		}
		return null;
	}
	
	
	public static List<String> getSoggettiOperativiRiferisconoSoggettoAggregato(String nomeSoggetto, IRegistryReader registryReader) throws ProtocolException{
		List<String> listSoggetti = new ArrayList<>();
		List<String> listSoggettiOperativi = getSoggettiOperativi(registryReader);
		if(!listSoggettiOperativi.isEmpty()) {
			for (String s : listSoggettiOperativi) {
				String check = readInfoSoggettoAggregato(s, registryReader);
				if(nomeSoggetto.equals(check)) {
					listSoggetti.add(s);
				}
			}
		}
		return listSoggetti;
	}
}
