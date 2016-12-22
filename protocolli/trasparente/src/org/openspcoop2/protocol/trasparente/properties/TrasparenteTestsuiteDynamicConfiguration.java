package org.openspcoop2.protocol.trasparente.properties;

import java.util.List;

import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.ProtocolProperty;
import org.openspcoop2.protocol.basic.properties.BasicDynamicConfiguration;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.constants.ConsoleInterfaceType;
import org.openspcoop2.protocol.sdk.constants.ConsoleOperationType;
import org.openspcoop2.protocol.sdk.properties.ConsoleConfiguration;
import org.openspcoop2.protocol.sdk.properties.ProtocolProperties;
import org.openspcoop2.protocol.sdk.registry.IRegistryReader;

public class TrasparenteTestsuiteDynamicConfiguration extends BasicDynamicConfiguration implements org.openspcoop2.protocol.sdk.properties.IConsoleDynamicConfiguration {

	public TrasparenteTestsuiteDynamicConfiguration(IProtocolFactory<?> factory) throws ProtocolException{
		super(factory);
	}

	@Override
	public ConsoleConfiguration getDynamicConfigSoggetto(ConsoleOperationType consoleOperationType,
			ConsoleInterfaceType consoleInterfaceType, IRegistryReader registryReader, IDSoggetto id)
					throws ProtocolException {
		return TrasparenteConfigurazioneTest.getDynamicConfigTest(consoleOperationType, consoleInterfaceType, registryReader, this.protocolFactory, null);
	}

	@Override
	public void updateDynamicConfigSoggetto(ConsoleConfiguration consoleConfiguration,
			ConsoleOperationType consoleOperationType, ConsoleInterfaceType consoleInterfaceType,
			ProtocolProperties properties, IRegistryReader registryReader, IDSoggetto id) throws ProtocolException {
		TrasparenteConfigurazioneTest.updateDynamicConfig(consoleConfiguration, consoleOperationType, consoleInterfaceType, properties, registryReader);
	}

	@Override
	public void validateDynamicConfigSoggetto(ConsoleConfiguration consoleConfiguration,
			ConsoleOperationType consoleOperationType, ProtocolProperties properties, IRegistryReader registryReader,
			IDSoggetto id) throws ProtocolException {
		TrasparenteConfigurazioneTest.validateDynamicConfig(consoleConfiguration, consoleOperationType, properties, registryReader);
	}


	@Override
	public ConsoleConfiguration getDynamicConfigAccordoServizioParteComune(ConsoleOperationType consoleOperationType,
			ConsoleInterfaceType consoleInterfaceType, IRegistryReader registryReader, IDAccordo id)
					throws ProtocolException {

		List<ProtocolProperty> protocolPropertyList = null;
		return TrasparenteConfigurazioneTest.getDynamicConfigTest(consoleOperationType, consoleInterfaceType, registryReader, this.protocolFactory, protocolPropertyList);

	}
	
	@Override
	public void updateDynamicConfigAccordoServizioParteComune(ConsoleConfiguration consoleConfiguration,
			ConsoleOperationType consoleOperationType, ConsoleInterfaceType consoleInterfaceType,
			ProtocolProperties properties, IRegistryReader registryReader, IDAccordo id) throws ProtocolException {
		TrasparenteConfigurazioneTest.updateDynamicConfig(consoleConfiguration, consoleOperationType, consoleInterfaceType, properties, registryReader);
	}
	
	@Override
	public void validateDynamicConfigAccordoServizioParteComune(ConsoleConfiguration consoleConfiguration,
			ConsoleOperationType consoleOperationType, ProtocolProperties properties, IRegistryReader registryReader,
			IDAccordo id) throws ProtocolException {
		TrasparenteConfigurazioneTest.validateDynamicConfig(consoleConfiguration, consoleOperationType, properties, registryReader);
	}
}
