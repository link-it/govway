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

package org.openspcoop2.protocol.trasparente.properties;

import java.util.List;

import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDAccordoAzione;
import org.openspcoop2.core.id.IDFruizione;
import org.openspcoop2.core.id.IDPortType;
import org.openspcoop2.core.id.IDPortTypeAzione;
import org.openspcoop2.core.id.IDResource;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.ProtocolProperty;
import org.openspcoop2.protocol.basic.properties.BasicDynamicConfiguration;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.constants.ConsoleInterfaceType;
import org.openspcoop2.protocol.sdk.constants.ConsoleOperationType;
import org.openspcoop2.protocol.sdk.properties.ConsoleConfiguration;
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
	public ConsoleConfiguration getDynamicConfigSoggetto(ConsoleOperationType consoleOperationType,
			ConsoleInterfaceType consoleInterfaceType, IRegistryReader registryReader, IConfigIntegrationReader configIntegrationReader, IDSoggetto id)
					throws ProtocolException {
		return TrasparenteConfigurazioneTest.getDynamicConfigTest(consoleOperationType, consoleInterfaceType, registryReader, this.protocolFactory, null);
	}

	@Override
	public void updateDynamicConfigSoggetto(ConsoleConfiguration consoleConfiguration,
			ConsoleOperationType consoleOperationType, ConsoleInterfaceType consoleInterfaceType,
			ProtocolProperties properties, IRegistryReader registryReader, IConfigIntegrationReader configIntegrationReader, IDSoggetto id) throws ProtocolException {
		TrasparenteConfigurazioneTest.updateDynamicConfig(consoleConfiguration, consoleOperationType, consoleInterfaceType, properties, registryReader);
	}

	@Override
	public void validateDynamicConfigSoggetto(ConsoleConfiguration consoleConfiguration,
			ConsoleOperationType consoleOperationType, ProtocolProperties properties, IRegistryReader registryReader, IConfigIntegrationReader configIntegrationReader,
			IDSoggetto id) throws ProtocolException {
		TrasparenteConfigurazioneTest.validateDynamicConfig(consoleConfiguration, consoleOperationType, properties, registryReader);
	}


	/*** ACCORDO SERVIZIO PARTE COMUNE ***/

	@Override
	public ConsoleConfiguration getDynamicConfigAccordoServizioParteComune(ConsoleOperationType consoleOperationType,
			ConsoleInterfaceType consoleInterfaceType, IRegistryReader registryReader, IConfigIntegrationReader configIntegrationReader, IDAccordo id)
					throws ProtocolException {

		List<ProtocolProperty> protocolPropertyList = null;
		return TrasparenteConfigurazioneTest.getDynamicConfigTest(consoleOperationType, consoleInterfaceType, registryReader, this.protocolFactory, protocolPropertyList);

	}

	@Override
	public void updateDynamicConfigAccordoServizioParteComune(ConsoleConfiguration consoleConfiguration,
			ConsoleOperationType consoleOperationType, ConsoleInterfaceType consoleInterfaceType,
			ProtocolProperties properties, IRegistryReader registryReader, IConfigIntegrationReader configIntegrationReader, IDAccordo id) throws ProtocolException {
		TrasparenteConfigurazioneTest.updateDynamicConfig(consoleConfiguration, consoleOperationType, consoleInterfaceType, properties, registryReader);
	}

	@Override
	public void validateDynamicConfigAccordoServizioParteComune(ConsoleConfiguration consoleConfiguration,
			ConsoleOperationType consoleOperationType, ProtocolProperties properties, IRegistryReader registryReader, IConfigIntegrationReader configIntegrationReader,
			IDAccordo id) throws ProtocolException {
		TrasparenteConfigurazioneTest.validateDynamicConfig(consoleConfiguration, consoleOperationType, properties, registryReader);
	}

	/*** ACCORDO SERVIZIO COMPOSTO ***/

	@Override
	public ConsoleConfiguration getDynamicConfigAccordoServizioComposto(ConsoleOperationType consoleOperationType,
			ConsoleInterfaceType consoleInterfaceType, IRegistryReader registryReader, IConfigIntegrationReader configIntegrationReader, IDAccordo id)
					throws ProtocolException {

		List<ProtocolProperty> protocolPropertyList = null;
		return TrasparenteConfigurazioneTest.getDynamicConfigTest(consoleOperationType, consoleInterfaceType, registryReader, this.protocolFactory, protocolPropertyList);
	}

	@Override
	public void updateDynamicConfigAccordoServizioComposto(ConsoleConfiguration consoleConfiguration,
			ConsoleOperationType consoleOperationType, ConsoleInterfaceType consoleInterfaceType,
			ProtocolProperties properties, IRegistryReader registryReader, IConfigIntegrationReader configIntegrationReader, IDAccordo id) throws ProtocolException {
		TrasparenteConfigurazioneTest.updateDynamicConfig(consoleConfiguration, consoleOperationType, consoleInterfaceType, properties, registryReader);
	}
	
	@Override
	public void validateDynamicConfigAccordoServizioComposto(ConsoleConfiguration consoleConfiguration,
			ConsoleOperationType consoleOperationType, ProtocolProperties properties, IRegistryReader registryReader, IConfigIntegrationReader configIntegrationReader,
			IDAccordo id) throws ProtocolException {
		TrasparenteConfigurazioneTest.validateDynamicConfig(consoleConfiguration, consoleOperationType, properties, registryReader);
	}

	/*** AZIONE ACCORDO ***/

	@Override
	public ConsoleConfiguration getDynamicConfigAzione(ConsoleOperationType consoleOperationType,
			ConsoleInterfaceType consoleInterfaceType, IRegistryReader registryReader, IConfigIntegrationReader configIntegrationReader, IDAccordoAzione id)
					throws ProtocolException {
		List<ProtocolProperty> protocolPropertyList = null;
		return TrasparenteConfigurazioneTest.getDynamicConfigTest(consoleOperationType, consoleInterfaceType, registryReader, this.protocolFactory, protocolPropertyList);
	}

	@Override
	public void updateDynamicConfigAzione(ConsoleConfiguration consoleConfiguration,
			ConsoleOperationType consoleOperationType, ConsoleInterfaceType consoleInterfaceType,
			ProtocolProperties properties, IRegistryReader registryReader, IConfigIntegrationReader configIntegrationReader, IDAccordoAzione id)
					throws ProtocolException {
		TrasparenteConfigurazioneTest.updateDynamicConfig(consoleConfiguration, consoleOperationType, consoleInterfaceType, properties, registryReader);
	}
	
	@Override
	public void validateDynamicConfigAzione(ConsoleConfiguration consoleConfiguration,
			ConsoleOperationType consoleOperationType, ProtocolProperties properties, IRegistryReader registryReader, IConfigIntegrationReader configIntegrationReader,
			IDAccordoAzione id) throws ProtocolException {
		TrasparenteConfigurazioneTest.validateDynamicConfig(consoleConfiguration, consoleOperationType, properties, registryReader);
	}


	/*** OPERATION PORT TYPE ***/

	@Override
	public ConsoleConfiguration getDynamicConfigOperation(ConsoleOperationType consoleOperationType,
			ConsoleInterfaceType consoleInterfaceType, IRegistryReader registryReader, IConfigIntegrationReader configIntegrationReader, IDPortTypeAzione id)
					throws ProtocolException {
		List<ProtocolProperty> protocolPropertyList = null;
		return TrasparenteConfigurazioneTest.getDynamicConfigTest(consoleOperationType, consoleInterfaceType, registryReader, this.protocolFactory, protocolPropertyList);
	}

	@Override
	public void updateDynamicConfigOperation(ConsoleConfiguration consoleConfiguration,
			ConsoleOperationType consoleOperationType, ConsoleInterfaceType consoleInterfaceType,
			ProtocolProperties properties, IRegistryReader registryReader, IConfigIntegrationReader configIntegrationReader, IDPortTypeAzione id)
					throws ProtocolException {
		TrasparenteConfigurazioneTest.updateDynamicConfig(consoleConfiguration, consoleOperationType, consoleInterfaceType, properties, registryReader);
	}
	
	@Override
	public void validateDynamicConfigOperation(ConsoleConfiguration consoleConfiguration,
			ConsoleOperationType consoleOperationType, ProtocolProperties properties, IRegistryReader registryReader, IConfigIntegrationReader configIntegrationReader,
			IDPortTypeAzione id) throws ProtocolException {
		TrasparenteConfigurazioneTest.validateDynamicConfig(consoleConfiguration, consoleOperationType, properties, registryReader);
	}


	/*** PORT TYPE ***/

	@Override
	public ConsoleConfiguration getDynamicConfigPortType(ConsoleOperationType consoleOperationType,
			ConsoleInterfaceType consoleInterfaceType, IRegistryReader registryReader, IConfigIntegrationReader configIntegrationReader, IDPortType id)
					throws ProtocolException {
		List<ProtocolProperty> protocolPropertyList = null;
		return TrasparenteConfigurazioneTest.getDynamicConfigTest(consoleOperationType, consoleInterfaceType, registryReader, this.protocolFactory, protocolPropertyList);
	}

	@Override
	public void updateDynamicConfigPortType(ConsoleConfiguration consoleConfiguration,
			ConsoleOperationType consoleOperationType, ConsoleInterfaceType consoleInterfaceType,
			ProtocolProperties properties, IRegistryReader registryReader, IConfigIntegrationReader configIntegrationReader, IDPortType id) throws ProtocolException {
		TrasparenteConfigurazioneTest.updateDynamicConfig(consoleConfiguration, consoleOperationType, consoleInterfaceType, properties, registryReader);
	}
	
	@Override
	public void validateDynamicConfigPortType(ConsoleConfiguration consoleConfiguration,
			ConsoleOperationType consoleOperationType, ProtocolProperties properties, IRegistryReader registryReader, IConfigIntegrationReader configIntegrationReader,
			IDPortType id) throws ProtocolException {
		TrasparenteConfigurazioneTest.validateDynamicConfig(consoleConfiguration, consoleOperationType, properties, registryReader);
	}
	
	/*** RESOURCE ***/

	@Override
	public ConsoleConfiguration getDynamicConfigResource(ConsoleOperationType consoleOperationType,
			ConsoleInterfaceType consoleInterfaceType, IRegistryReader registryReader, IConfigIntegrationReader configIntegrationReader, IDResource id)
					throws ProtocolException {
		List<ProtocolProperty> protocolPropertyList = null;
		return TrasparenteConfigurazioneTest.getDynamicConfigTest(consoleOperationType, consoleInterfaceType, registryReader, this.protocolFactory, protocolPropertyList);
	}

	@Override
	public void updateDynamicConfigResource(ConsoleConfiguration consoleConfiguration,
			ConsoleOperationType consoleOperationType, ConsoleInterfaceType consoleInterfaceType,
			ProtocolProperties properties, IRegistryReader registryReader, IConfigIntegrationReader configIntegrationReader, IDResource id) throws ProtocolException {
		TrasparenteConfigurazioneTest.updateDynamicConfig(consoleConfiguration, consoleOperationType, consoleInterfaceType, properties, registryReader);
	}
	
	@Override
	public void validateDynamicConfigResource(ConsoleConfiguration consoleConfiguration,
			ConsoleOperationType consoleOperationType, ProtocolProperties properties, IRegistryReader registryReader, IConfigIntegrationReader configIntegrationReader,
			IDResource id) throws ProtocolException {
		TrasparenteConfigurazioneTest.validateDynamicConfig(consoleConfiguration, consoleOperationType, properties, registryReader);
	}
	
	/*** ACCORDO COOPERAZIONE ***/
	
	@Override
	public ConsoleConfiguration getDynamicConfigAccordoCooperazione(ConsoleOperationType consoleOperationType,
			ConsoleInterfaceType consoleInterfaceType, IRegistryReader registryReader, IConfigIntegrationReader configIntegrationReader, IDAccordo id)
			throws ProtocolException {
		List<ProtocolProperty> protocolPropertyList = null;
		return TrasparenteConfigurazioneTest.getDynamicConfigTest(consoleOperationType, consoleInterfaceType, registryReader, this.protocolFactory, protocolPropertyList);
	}
	
	@Override
	public void updateDynamicConfigAccordoCooperazione(ConsoleConfiguration consoleConfiguration,
			ConsoleOperationType consoleOperationType, ConsoleInterfaceType consoleInterfaceType,
			ProtocolProperties properties, IRegistryReader registryReader, IConfigIntegrationReader configIntegrationReader, IDAccordo id) throws ProtocolException {
		TrasparenteConfigurazioneTest.updateDynamicConfig(consoleConfiguration, consoleOperationType, consoleInterfaceType, properties, registryReader);
	}
	
	@Override
	public void validateDynamicConfigCooperazione(ConsoleConfiguration consoleConfiguration,
			ConsoleOperationType consoleOperationType, ProtocolProperties properties, IRegistryReader registryReader, IConfigIntegrationReader configIntegrationReader,
			IDAccordo id) throws ProtocolException {
		TrasparenteConfigurazioneTest.validateDynamicConfig(consoleConfiguration, consoleOperationType, properties, registryReader);
	}
	
	
	/*** ACCORDI SERVIZIO PARTE SPECIFICA ***/
	
	@Override
	public ConsoleConfiguration getDynamicConfigAccordoServizioParteSpecifica(ConsoleOperationType consoleOperationType,
			ConsoleInterfaceType consoleInterfaceType, IRegistryReader registryReader, IConfigIntegrationReader configIntegrationReader, IDServizio id)
			throws ProtocolException {
		List<ProtocolProperty> protocolPropertyList = null;
		return TrasparenteConfigurazioneTest.getDynamicConfigTest(consoleOperationType, consoleInterfaceType, registryReader, this.protocolFactory, protocolPropertyList);
	}
	
	@Override
	public void updateDynamicConfigAccordoServizioParteSpecifica(ConsoleConfiguration consoleConfiguration,
			ConsoleOperationType consoleOperationType, ConsoleInterfaceType consoleInterfaceType,
			ProtocolProperties properties, IRegistryReader registryReader, IConfigIntegrationReader configIntegrationReader, IDServizio id) throws ProtocolException {
		TrasparenteConfigurazioneTest.updateDynamicConfig(consoleConfiguration, consoleOperationType, consoleInterfaceType, properties, registryReader);
	}
	
	@Override
	public void validateDynamicConfigAccordoServizioParteSpecifica(ConsoleConfiguration consoleConfiguration,
			ConsoleOperationType consoleOperationType, ProtocolProperties properties, IRegistryReader registryReader, IConfigIntegrationReader configIntegrationReader,
			IDServizio id) throws ProtocolException {
		TrasparenteConfigurazioneTest.validateDynamicConfig(consoleConfiguration, consoleOperationType, properties, registryReader);
	}
	
	
	/*** FRUITORE ***/
	
	@Override
	public ConsoleConfiguration getDynamicConfigFruizioneAccordoServizioParteSpecifica(
			ConsoleOperationType consoleOperationType, ConsoleInterfaceType consoleInterfaceType,
			IRegistryReader registryReader, IConfigIntegrationReader configIntegrationReader, IDFruizione id) throws ProtocolException {
		List<ProtocolProperty> protocolPropertyList = null;
		return TrasparenteConfigurazioneTest.getDynamicConfigTest(consoleOperationType, consoleInterfaceType, registryReader, this.protocolFactory, protocolPropertyList);
	}
	
	
	@Override
	public void updateDynamicConfigFruizioneAccordoServizioParteSpecifica(ConsoleConfiguration consoleConfiguration,
			ConsoleOperationType consoleOperationType, ConsoleInterfaceType consoleInterfaceType,
			ProtocolProperties properties, IRegistryReader registryReader, IConfigIntegrationReader configIntegrationReader, IDFruizione id) throws ProtocolException {
		TrasparenteConfigurazioneTest.updateDynamicConfig(consoleConfiguration, consoleOperationType, consoleInterfaceType, properties, registryReader);
	}
	
	@Override
	public void validateDynamicConfigFruizioneAccordoServizioParteSpecifica(ConsoleConfiguration consoleConfiguration,
			ConsoleOperationType consoleOperationType, ProtocolProperties properties, IRegistryReader registryReader, IConfigIntegrationReader configIntegrationReader,
			IDFruizione id) throws ProtocolException {
		TrasparenteConfigurazioneTest.validateDynamicConfig(consoleConfiguration, consoleOperationType, properties, registryReader);
	}
}
