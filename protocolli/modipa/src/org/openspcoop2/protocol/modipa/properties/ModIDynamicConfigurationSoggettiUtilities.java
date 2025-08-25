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

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.mvc.properties.provider.InputValidationUtils;
import org.openspcoop2.protocol.modipa.config.ModIProperties;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.core.registry.constants.PddTipologia;
import org.openspcoop2.protocol.engine.constants.Costanti;
import org.openspcoop2.protocol.modipa.constants.ModIConsoleCostanti;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.constants.ConsoleItemType;
import org.openspcoop2.protocol.sdk.constants.ConsoleItemValueType;
import org.openspcoop2.protocol.sdk.constants.ConsoleOperationType;
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
import org.openspcoop2.protocol.sdk.registry.RegistryException;
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
	
	static ConsoleConfiguration getDynamicConfigSoggetto(ConsoleOperationType consoleOperationType, IConsoleHelper consoleHelper, IRegistryReader registryReader,
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
			StringConsoleItem soggettoPdndTracingEnabledItem = (StringConsoleItem) 
					ProtocolPropertiesFactory.newConsoleItem(ConsoleItemValueType.STRING,
					ConsoleItemType.SELECT,
					ModIConsoleCostanti.MODIPA_SOGGETTI_PDND_TRACING_ID, 
					ModIConsoleCostanti.MODIPA_SOGGETTI_PDND_TRACING_LABEL);
			soggettoPdndTracingEnabledItem.addLabelValue(ModIConsoleCostanti.MODIPA_SOGGETTI_PDND_TRACING_DEFAULT_LABEL, ModIConsoleCostanti.MODIPA_SOGGETTI_PDND_TRACING_DEFAULT_ID);
			soggettoPdndTracingEnabledItem.addLabelValue(ModIConsoleCostanti.MODIPA_SOGGETTI_PDND_TRACING_ENABLE_LABEL, ModIConsoleCostanti.MODIPA_SOGGETTI_PDND_TRACING_ENABLE_ID);
			soggettoPdndTracingEnabledItem.addLabelValue(ModIConsoleCostanti.MODIPA_SOGGETTI_PDND_TRACING_DISABLE_LABEL, ModIConsoleCostanti.MODIPA_SOGGETTI_PDND_TRACING_DISABLE_ID);
			soggettoPdndTracingEnabledItem.setDefaultValue(ModIConsoleCostanti.MODIPA_SOGGETTI_PDND_TRACING_DEFAULT_ID);
			configuration.addConsoleItem(soggettoPdndTracingEnabledItem);
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
	
	
	static void validateDynamicConfigSoggetto(ConsoleConfiguration consoleConfiguration,
			ConsoleOperationType consoleOperationType, IConsoleHelper consoleHelper, ProtocolProperties properties, 
			IConfigIntegrationReader configIntegrationReader, IDSoggetto id,
			IRegistryReader registryReader) throws ProtocolException {
		
		if(consoleConfiguration!=null && consoleOperationType!=null && consoleHelper!=null && configIntegrationReader!=null && id!=null) {
			// nop
		}
		
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
	
	private static List<String> getPddOperative(IRegistryReader registryReader) throws RegistryException{
		List<String> pddOperative = null;
		try {
			pddOperative = registryReader.findIdPorteDominio(true);
		}catch(RegistryNotFound notFound) {
			// ignore
		}
		return pddOperative;
	}
}
