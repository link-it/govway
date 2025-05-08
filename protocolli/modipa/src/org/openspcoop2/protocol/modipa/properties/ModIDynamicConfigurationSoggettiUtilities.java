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

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.mvc.properties.provider.InputValidationUtils;
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
			IConfigIntegrationReader configIntegrationReader, IDSoggetto id) throws ProtocolException {
		
		if(consoleOperationType!=null && consoleHelper!=null && registryReader!=null && configIntegrationReader!=null && id!=null) {
			// nop
		}
		
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
		
		return configuration;
	}
	
	
	static void validateDynamicConfigSoggetto(ConsoleConfiguration consoleConfiguration,
			ConsoleOperationType consoleOperationType, IConsoleHelper consoleHelper, ProtocolProperties properties, 
			IConfigIntegrationReader configIntegrationReader, IDSoggetto id) throws ProtocolException {
		
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
		}
	}
}
