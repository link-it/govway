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

package org.openspcoop2.protocol.trasparente.properties;

import java.util.List;

import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDAccordoAzione;
import org.openspcoop2.core.id.IDFruizione;
import org.openspcoop2.core.id.IDPortType;
import org.openspcoop2.core.id.IDPortTypeAzione;
import org.openspcoop2.core.id.IDResource;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.ProtocolProperty;
import org.openspcoop2.protocol.basic.properties.BasicDynamicConfiguration;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.constants.ConsoleOperationType;
import org.openspcoop2.protocol.sdk.properties.ConsoleConfiguration;
import org.openspcoop2.protocol.sdk.properties.IConsoleHelper;
import org.openspcoop2.protocol.sdk.properties.ProtocolProperties;
import org.openspcoop2.protocol.sdk.registry.IConfigIntegrationReader;
import org.openspcoop2.protocol.sdk.registry.IRegistryReader;

/**
 * TrasparenteTestsuiteDynamicConfiguration
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TrasparenteTestsuiteDynamicConfiguration extends BasicDynamicConfiguration implements org.openspcoop2.protocol.sdk.properties.IConsoleDynamicConfiguration {

	public TrasparenteTestsuiteDynamicConfiguration(IProtocolFactory<?> factory) throws ProtocolException{
		super(factory);
	}

	/*** SOGGETTO ***/

	@Override
	public ConsoleConfiguration getDynamicConfigSoggetto(ConsoleOperationType consoleOperationType, IConsoleHelper consoleHelper, IRegistryReader registryReader, IConfigIntegrationReader configIntegrationReader, IDSoggetto id)
					throws ProtocolException {
		return TrasparenteConfigurazioneTest.getDynamicConfigTest(consoleOperationType, consoleHelper, registryReader, this.protocolFactory, null);
	}

	@Override
	public void updateDynamicConfigSoggetto(ConsoleConfiguration consoleConfiguration,
			ConsoleOperationType consoleOperationType, IConsoleHelper consoleHelper, 
			ProtocolProperties properties, IRegistryReader registryReader, IConfigIntegrationReader configIntegrationReader, IDSoggetto id) throws ProtocolException {
		TrasparenteConfigurazioneTest.updateDynamicConfig(consoleConfiguration, consoleOperationType, consoleHelper, properties, registryReader);
	}

	@Override
	public void validateDynamicConfigSoggetto(ConsoleConfiguration consoleConfiguration,
			ConsoleOperationType consoleOperationType, IConsoleHelper consoleHelper, ProtocolProperties properties, IRegistryReader registryReader, IConfigIntegrationReader configIntegrationReader,
			IDSoggetto id) throws ProtocolException {
		TrasparenteConfigurazioneTest.validateDynamicConfig(consoleConfiguration, consoleOperationType, properties, registryReader);
	}


	/*** ACCORDO SERVIZIO PARTE COMUNE ***/

	@Override
	public ConsoleConfiguration getDynamicConfigAccordoServizioParteComune(ConsoleOperationType consoleOperationType, IConsoleHelper consoleHelper, IRegistryReader registryReader, IConfigIntegrationReader configIntegrationReader, IDAccordo id)
					throws ProtocolException {

		List<ProtocolProperty> protocolPropertyList = null;
		return TrasparenteConfigurazioneTest.getDynamicConfigTest(consoleOperationType, consoleHelper, registryReader, this.protocolFactory, protocolPropertyList);

	}

	@Override
	public void updateDynamicConfigAccordoServizioParteComune(ConsoleConfiguration consoleConfiguration,
			ConsoleOperationType consoleOperationType, IConsoleHelper consoleHelper, 
			ProtocolProperties properties, IRegistryReader registryReader, IConfigIntegrationReader configIntegrationReader, IDAccordo id) throws ProtocolException {
		TrasparenteConfigurazioneTest.updateDynamicConfig(consoleConfiguration, consoleOperationType, consoleHelper, properties, registryReader);
	}

	@Override
	public void validateDynamicConfigAccordoServizioParteComune(ConsoleConfiguration consoleConfiguration,
			ConsoleOperationType consoleOperationType, IConsoleHelper consoleHelper, ProtocolProperties properties, IRegistryReader registryReader, IConfigIntegrationReader configIntegrationReader,
			IDAccordo id) throws ProtocolException {
		TrasparenteConfigurazioneTest.validateDynamicConfig(consoleConfiguration, consoleOperationType, properties, registryReader);
	}

	/*** ACCORDO SERVIZIO COMPOSTO ***/

	@Override
	public ConsoleConfiguration getDynamicConfigAccordoServizioComposto(ConsoleOperationType consoleOperationType, IConsoleHelper consoleHelper, IRegistryReader registryReader, IConfigIntegrationReader configIntegrationReader, IDAccordo id)
					throws ProtocolException {

		List<ProtocolProperty> protocolPropertyList = null;
		return TrasparenteConfigurazioneTest.getDynamicConfigTest(consoleOperationType, consoleHelper, registryReader, this.protocolFactory, protocolPropertyList);
	}

	@Override
	public void updateDynamicConfigAccordoServizioComposto(ConsoleConfiguration consoleConfiguration,
			ConsoleOperationType consoleOperationType, IConsoleHelper consoleHelper, 
			ProtocolProperties properties, IRegistryReader registryReader, IConfigIntegrationReader configIntegrationReader, IDAccordo id) throws ProtocolException {
		TrasparenteConfigurazioneTest.updateDynamicConfig(consoleConfiguration, consoleOperationType, consoleHelper, properties, registryReader);
	}
	
	@Override
	public void validateDynamicConfigAccordoServizioComposto(ConsoleConfiguration consoleConfiguration,
			ConsoleOperationType consoleOperationType, IConsoleHelper consoleHelper, ProtocolProperties properties, IRegistryReader registryReader, IConfigIntegrationReader configIntegrationReader,
			IDAccordo id) throws ProtocolException {
		TrasparenteConfigurazioneTest.validateDynamicConfig(consoleConfiguration, consoleOperationType, properties, registryReader);
	}

	/*** AZIONE ACCORDO ***/

	@Override
	public ConsoleConfiguration getDynamicConfigAzione(ConsoleOperationType consoleOperationType, IConsoleHelper consoleHelper, IRegistryReader registryReader, IConfigIntegrationReader configIntegrationReader, IDAccordoAzione id)
					throws ProtocolException {
		List<ProtocolProperty> protocolPropertyList = null;
		return TrasparenteConfigurazioneTest.getDynamicConfigTest(consoleOperationType, consoleHelper, registryReader, this.protocolFactory, protocolPropertyList);
	}

	@Override
	public void updateDynamicConfigAzione(ConsoleConfiguration consoleConfiguration,
			ConsoleOperationType consoleOperationType, IConsoleHelper consoleHelper, 
			ProtocolProperties properties, IRegistryReader registryReader, IConfigIntegrationReader configIntegrationReader, IDAccordoAzione id)
					throws ProtocolException {
		TrasparenteConfigurazioneTest.updateDynamicConfig(consoleConfiguration, consoleOperationType, consoleHelper, properties, registryReader);
	}
	
	@Override
	public void validateDynamicConfigAzione(ConsoleConfiguration consoleConfiguration,
			ConsoleOperationType consoleOperationType, IConsoleHelper consoleHelper, ProtocolProperties properties, IRegistryReader registryReader, IConfigIntegrationReader configIntegrationReader,
			IDAccordoAzione id) throws ProtocolException {
		TrasparenteConfigurazioneTest.validateDynamicConfig(consoleConfiguration, consoleOperationType, properties, registryReader);
	}


	/*** OPERATION PORT TYPE ***/

	@Override
	public ConsoleConfiguration getDynamicConfigOperation(ConsoleOperationType consoleOperationType, IConsoleHelper consoleHelper, IRegistryReader registryReader, IConfigIntegrationReader configIntegrationReader, IDPortTypeAzione id)
					throws ProtocolException {
		List<ProtocolProperty> protocolPropertyList = null;
		return TrasparenteConfigurazioneTest.getDynamicConfigTest(consoleOperationType, consoleHelper, registryReader, this.protocolFactory, protocolPropertyList);
	}

	@Override
	public void updateDynamicConfigOperation(ConsoleConfiguration consoleConfiguration,
			ConsoleOperationType consoleOperationType, IConsoleHelper consoleHelper, 
			ProtocolProperties properties, IRegistryReader registryReader, IConfigIntegrationReader configIntegrationReader, IDPortTypeAzione id)
					throws ProtocolException {
		TrasparenteConfigurazioneTest.updateDynamicConfig(consoleConfiguration, consoleOperationType, consoleHelper, properties, registryReader);
	}
	
	@Override
	public void validateDynamicConfigOperation(ConsoleConfiguration consoleConfiguration,
			ConsoleOperationType consoleOperationType, IConsoleHelper consoleHelper, ProtocolProperties properties, IRegistryReader registryReader, IConfigIntegrationReader configIntegrationReader,
			IDPortTypeAzione id) throws ProtocolException {
		TrasparenteConfigurazioneTest.validateDynamicConfig(consoleConfiguration, consoleOperationType, properties, registryReader);
	}


	/*** PORT TYPE ***/

	@Override
	public ConsoleConfiguration getDynamicConfigPortType(ConsoleOperationType consoleOperationType, IConsoleHelper consoleHelper, IRegistryReader registryReader, IConfigIntegrationReader configIntegrationReader, IDPortType id)
					throws ProtocolException {
		List<ProtocolProperty> protocolPropertyList = null;
		return TrasparenteConfigurazioneTest.getDynamicConfigTest(consoleOperationType, consoleHelper, registryReader, this.protocolFactory, protocolPropertyList);
	}

	@Override
	public void updateDynamicConfigPortType(ConsoleConfiguration consoleConfiguration,
			ConsoleOperationType consoleOperationType, IConsoleHelper consoleHelper, 
			ProtocolProperties properties, IRegistryReader registryReader, IConfigIntegrationReader configIntegrationReader, IDPortType id) throws ProtocolException {
		TrasparenteConfigurazioneTest.updateDynamicConfig(consoleConfiguration, consoleOperationType, consoleHelper, properties, registryReader);
	}
	
	@Override
	public void validateDynamicConfigPortType(ConsoleConfiguration consoleConfiguration,
			ConsoleOperationType consoleOperationType, IConsoleHelper consoleHelper, ProtocolProperties properties, IRegistryReader registryReader, IConfigIntegrationReader configIntegrationReader,
			IDPortType id) throws ProtocolException {
		TrasparenteConfigurazioneTest.validateDynamicConfig(consoleConfiguration, consoleOperationType, properties, registryReader);
	}
	
	/*** RESOURCE ***/

	@Override
	public ConsoleConfiguration getDynamicConfigResource(ConsoleOperationType consoleOperationType, IConsoleHelper consoleHelper, IRegistryReader registryReader, IConfigIntegrationReader configIntegrationReader, 
			IDResource id, String httpMethod, String path)
					throws ProtocolException {
		List<ProtocolProperty> protocolPropertyList = null;
		return TrasparenteConfigurazioneTest.getDynamicConfigTest(consoleOperationType, consoleHelper, registryReader, this.protocolFactory, protocolPropertyList);
	}

	@Override
	public void updateDynamicConfigResource(ConsoleConfiguration consoleConfiguration,
			ConsoleOperationType consoleOperationType, IConsoleHelper consoleHelper, 
			ProtocolProperties properties, IRegistryReader registryReader, IConfigIntegrationReader configIntegrationReader, IDResource id, String httpMethod, String path) throws ProtocolException {
		TrasparenteConfigurazioneTest.updateDynamicConfig(consoleConfiguration, consoleOperationType, consoleHelper, properties, registryReader);
	}
	
	@Override
	public void validateDynamicConfigResource(ConsoleConfiguration consoleConfiguration,
			ConsoleOperationType consoleOperationType, IConsoleHelper consoleHelper, ProtocolProperties properties, IRegistryReader registryReader, IConfigIntegrationReader configIntegrationReader,
			IDResource id, String httpMethod, String path) throws ProtocolException {
		TrasparenteConfigurazioneTest.validateDynamicConfig(consoleConfiguration, consoleOperationType, properties, registryReader);
	}
	
	/*** ACCORDO COOPERAZIONE ***/
	
	@Override
	public ConsoleConfiguration getDynamicConfigAccordoCooperazione(ConsoleOperationType consoleOperationType, IConsoleHelper consoleHelper, IRegistryReader registryReader, IConfigIntegrationReader configIntegrationReader, IDAccordo id)
			throws ProtocolException {
		List<ProtocolProperty> protocolPropertyList = null;
		return TrasparenteConfigurazioneTest.getDynamicConfigTest(consoleOperationType, consoleHelper, registryReader, this.protocolFactory, protocolPropertyList);
	}
	
	@Override
	public void updateDynamicConfigAccordoCooperazione(ConsoleConfiguration consoleConfiguration,
			ConsoleOperationType consoleOperationType, IConsoleHelper consoleHelper, 
			ProtocolProperties properties, IRegistryReader registryReader, IConfigIntegrationReader configIntegrationReader, IDAccordo id) throws ProtocolException {
		TrasparenteConfigurazioneTest.updateDynamicConfig(consoleConfiguration, consoleOperationType, consoleHelper, properties, registryReader);
	}
	
	@Override
	public void validateDynamicConfigCooperazione(ConsoleConfiguration consoleConfiguration,
			ConsoleOperationType consoleOperationType, IConsoleHelper consoleHelper, ProtocolProperties properties, IRegistryReader registryReader, IConfigIntegrationReader configIntegrationReader,
			IDAccordo id) throws ProtocolException {
		TrasparenteConfigurazioneTest.validateDynamicConfig(consoleConfiguration, consoleOperationType, properties, registryReader);
	}
	
	
	/*** ACCORDI SERVIZIO PARTE SPECIFICA ***/
	
	@Override
	public ConsoleConfiguration getDynamicConfigAccordoServizioParteSpecifica(ConsoleOperationType consoleOperationType, IConsoleHelper consoleHelper, IRegistryReader registryReader, IConfigIntegrationReader configIntegrationReader, IDServizio id)
			throws ProtocolException {
		List<ProtocolProperty> protocolPropertyList = null;
		return TrasparenteConfigurazioneTest.getDynamicConfigTest(consoleOperationType, consoleHelper, registryReader, this.protocolFactory, protocolPropertyList);
	}
	
	@Override
	public void updateDynamicConfigAccordoServizioParteSpecifica(ConsoleConfiguration consoleConfiguration,
			ConsoleOperationType consoleOperationType, IConsoleHelper consoleHelper, 
			ProtocolProperties properties, IRegistryReader registryReader, IConfigIntegrationReader configIntegrationReader, IDServizio id) throws ProtocolException {
		TrasparenteConfigurazioneTest.updateDynamicConfig(consoleConfiguration, consoleOperationType, consoleHelper, properties, registryReader);
	}
	
	@Override
	public void validateDynamicConfigAccordoServizioParteSpecifica(ConsoleConfiguration consoleConfiguration,
			ConsoleOperationType consoleOperationType, IConsoleHelper consoleHelper, ProtocolProperties properties, IRegistryReader registryReader, IConfigIntegrationReader configIntegrationReader,
			IDServizio id) throws ProtocolException {
		TrasparenteConfigurazioneTest.validateDynamicConfig(consoleConfiguration, consoleOperationType, properties, registryReader);
	}
	
	
	/*** FRUITORE ***/
	
	@Override
	public ConsoleConfiguration getDynamicConfigFruizioneAccordoServizioParteSpecifica(
			ConsoleOperationType consoleOperationType, IConsoleHelper consoleHelper, 
			IRegistryReader registryReader, IConfigIntegrationReader configIntegrationReader, IDFruizione id) throws ProtocolException {
		List<ProtocolProperty> protocolPropertyList = null;
		return TrasparenteConfigurazioneTest.getDynamicConfigTest(consoleOperationType, consoleHelper, registryReader, this.protocolFactory, protocolPropertyList);
	}
	
	@Override
	public void updateDynamicConfigFruizioneAccordoServizioParteSpecifica(ConsoleConfiguration consoleConfiguration,
			ConsoleOperationType consoleOperationType, IConsoleHelper consoleHelper, 
			ProtocolProperties properties, IRegistryReader registryReader, IConfigIntegrationReader configIntegrationReader, IDFruizione id) throws ProtocolException {
		TrasparenteConfigurazioneTest.updateDynamicConfig(consoleConfiguration, consoleOperationType, consoleHelper, properties, registryReader);
	}
	
	@Override
	public void validateDynamicConfigFruizioneAccordoServizioParteSpecifica(ConsoleConfiguration consoleConfiguration,
			ConsoleOperationType consoleOperationType, IConsoleHelper consoleHelper, ProtocolProperties properties, IRegistryReader registryReader, IConfigIntegrationReader configIntegrationReader,
			IDFruizione id) throws ProtocolException {
		TrasparenteConfigurazioneTest.validateDynamicConfig(consoleConfiguration, consoleOperationType, properties, registryReader);
	}
	
	
	
	/*** SERVIZIO APPLICATIVO  ***/
	
	@Override
	public ConsoleConfiguration getDynamicConfigServizioApplicativo(ConsoleOperationType consoleOperationType,
			IConsoleHelper consoleHelper, IRegistryReader registryReader,
			IConfigIntegrationReader configIntegrationReader, IDServizioApplicativo id) throws ProtocolException {
		List<ProtocolProperty> protocolPropertyList = null;
		return TrasparenteConfigurazioneTest.getDynamicConfigTest(consoleOperationType, consoleHelper, registryReader, this.protocolFactory, protocolPropertyList);
	}

	@Override
	public void updateDynamicConfigServizioApplicativo(ConsoleConfiguration consoleConfiguration,
			ConsoleOperationType consoleOperationType, IConsoleHelper consoleHelper, ProtocolProperties properties,
			IRegistryReader registryReader, IConfigIntegrationReader configIntegrationReader, IDServizioApplicativo id)
			throws ProtocolException {
		TrasparenteConfigurazioneTest.updateDynamicConfig(consoleConfiguration, consoleOperationType, consoleHelper, properties, registryReader);
	}

	@Override
	public void validateDynamicConfigServizioApplicativo(ConsoleConfiguration consoleConfiguration,
			ConsoleOperationType consoleOperationType, IConsoleHelper consoleHelper, ProtocolProperties properties, IRegistryReader registryReader,
			IConfigIntegrationReader configIntegrationReader, IDServizioApplicativo id) throws ProtocolException {
		TrasparenteConfigurazioneTest.validateDynamicConfig(consoleConfiguration, consoleOperationType, properties, registryReader);
	}
}
