package org.openspcoop2.protocol.sdk.properties;

import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDPortType;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.constants.ConsoleInterfaceType;
import org.openspcoop2.protocol.sdk.constants.ConsoleOperationType;
import org.openspcoop2.protocol.sdk.registry.IRegistryReader;

public interface IConsoleDynamicConfiguration {

	public ConsoleConfiguration getDynamicConfigSoggetto(ConsoleOperationType consoleOperationType, ConsoleInterfaceType consoleInterfaceType, 
			IRegistryReader registryReader, IDSoggetto id) throws ProtocolException;
	public void updateDynamicConfigSoggetto(ConsoleConfiguration consoleConfiguration, ConsoleOperationType consoleOperationType, ConsoleInterfaceType consoleInterfaceType, 
			ProtocolProperties properties, 
			IRegistryReader registryReader, IDSoggetto id) throws ProtocolException;
	public void validateDynamicConfigSoggetto(ConsoleConfiguration consoleConfiguration, ConsoleOperationType consoleOperationType, ProtocolProperties properties, 
			IRegistryReader registryReader, IDSoggetto id) throws ProtocolException;
	
	public ConsoleConfiguration getDynamicConfigAccordoCooperazione(ConsoleOperationType consoleOperationType, ConsoleInterfaceType consoleInterfaceType, 
			IRegistryReader registryReader, IDAccordo id) throws ProtocolException;
	public void updateDynamicConfigAccordoCooperazione(ConsoleConfiguration consoleConfiguration, ConsoleOperationType consoleOperationType, ConsoleInterfaceType consoleInterfaceType, 
			ProtocolProperties properties, 
			IRegistryReader registryReader, IDAccordo id) throws ProtocolException;
	public void validateDynamicConfigCooperazione(ConsoleConfiguration consoleConfiguration, ConsoleOperationType consoleOperationType, ProtocolProperties properties, 
			IRegistryReader registryReader, IDAccordo id) throws ProtocolException;
	
	public ConsoleConfiguration getDynamicConfigAccordoServizioParteComune(ConsoleOperationType consoleOperationType, ConsoleInterfaceType consoleInterfaceType, 
			IRegistryReader registryReader, IDAccordo id) throws ProtocolException;
	public void updateDynamicConfigAccordoServizioParteComune(ConsoleConfiguration consoleConfiguration, ConsoleOperationType consoleOperationType, ConsoleInterfaceType consoleInterfaceType, 
			ProtocolProperties properties, 
			IRegistryReader registryReader, IDAccordo id) throws ProtocolException;
	public void validateDynamicConfigAccordoServizioParteComune(ConsoleConfiguration consoleConfiguration, ConsoleOperationType consoleOperationType, ProtocolProperties properties, 
			IRegistryReader registryReader, IDAccordo id) throws ProtocolException;
	
	public ConsoleConfiguration getDynamicConfigAccordoServizioComposto(ConsoleOperationType consoleOperationType, ConsoleInterfaceType consoleInterfaceType, 
			IRegistryReader registryReader, IDAccordo id) throws ProtocolException;
	public void updateDynamicConfigAccordoServizioComposto(ConsoleConfiguration consoleConfiguration, ConsoleOperationType consoleOperationType, ConsoleInterfaceType consoleInterfaceType, 
			ProtocolProperties properties, 
			IRegistryReader registryReader, IDAccordo id) throws ProtocolException;
	public void validateDynamicConfigAccordoServizioComposto(ConsoleConfiguration consoleConfiguration, ConsoleOperationType consoleOperationType, ProtocolProperties properties, 
			IRegistryReader registryReader, IDAccordo id) throws ProtocolException;
	
	public ConsoleConfiguration getDynamicConfigPortType(ConsoleOperationType consoleOperationType, ConsoleInterfaceType consoleInterfaceType, 
			IRegistryReader registryReader, IDPortType id) throws ProtocolException;
	public void updateDynamicConfigPortType(ConsoleConfiguration consoleConfiguration, ConsoleOperationType consoleOperationType,  ConsoleInterfaceType consoleInterfaceType, 
			ProtocolProperties properties, 
			IRegistryReader registryReader, IDPortType id) throws ProtocolException;
	public void validateDynamicConfigPortType(ConsoleConfiguration consoleConfiguration, ConsoleOperationType consoleOperationType,  ProtocolProperties properties, 
			IRegistryReader registryReader, IDPortType id) throws ProtocolException;
	
	public ConsoleConfiguration getDynamicConfigOperation(ConsoleOperationType consoleOperationType, ConsoleInterfaceType consoleInterfaceType, 
			IRegistryReader registryReader, IDPortType id, String azione) throws ProtocolException;
	public void updateDynamicConfigOperation(ConsoleConfiguration consoleConfiguration, ConsoleOperationType consoleOperationType,  ConsoleInterfaceType consoleInterfaceType, 
			ProtocolProperties properties, 
			IRegistryReader registryReader, IDPortType id, String azione) throws ProtocolException;
	public void validateDynamicConfigOperation(ConsoleConfiguration consoleConfiguration, ConsoleOperationType consoleOperationType,  ProtocolProperties properties, 
			IRegistryReader registryReader, IDPortType id, String azione) throws ProtocolException;
	
	public ConsoleConfiguration getDynamicConfigAzione(ConsoleOperationType consoleOperationType, ConsoleInterfaceType consoleInterfaceType, 
			IRegistryReader registryReader, String azione) throws ProtocolException;
	public void updateDynamicConfigAzione(ConsoleConfiguration consoleConfiguration, ConsoleOperationType consoleOperationType,  ConsoleInterfaceType consoleInterfaceType, 
			ProtocolProperties properties, 
			IRegistryReader registryReader, String azione) throws ProtocolException;
	public void validateDynamicConfigAzione(ConsoleConfiguration consoleConfiguration, ConsoleOperationType consoleOperationType,  ProtocolProperties properties, 
			IRegistryReader registryReader, String azione) throws ProtocolException;
	
	public ConsoleConfiguration getDynamicConfigAccordoServizioParteSpecifica(ConsoleOperationType consoleOperationType, ConsoleInterfaceType consoleInterfaceType, 
			IRegistryReader registryReader, IDServizio id) throws ProtocolException;
	public void updateDynamicConfigAccordoServizioParteSpecifica(ConsoleConfiguration consoleConfiguration, ConsoleOperationType consoleOperationType, ConsoleInterfaceType consoleInterfaceType, 
			ProtocolProperties properties, 
			IRegistryReader registryReader, IDServizio id) throws ProtocolException;
	public void validateDynamicConfigAccordoServizioParteSpecifica(ConsoleConfiguration consoleConfiguration, ConsoleOperationType consoleOperationType, ProtocolProperties properties, 
			IRegistryReader registryReader, IDServizio id) throws ProtocolException;
	
	public ConsoleConfiguration getDynamicConfigFruizioneAccordoServizioParteSpecifica(ConsoleOperationType consoleOperationType, ConsoleInterfaceType consoleInterfaceType, 
			IRegistryReader registryReader, IDServizio idServizio, IDSoggetto fruitore) throws ProtocolException;
	public void updateDynamicConfigFruizioneAccordoServizioParteSpecifica(ConsoleConfiguration consoleConfiguration, ConsoleOperationType consoleOperationType, ConsoleInterfaceType consoleInterfaceType, 
			ProtocolProperties properties,  
			IRegistryReader registryReader, IDServizio idServizio, IDSoggetto fruitore) throws ProtocolException;
	public void validateDynamicConfigFruizioneAccordoServizioParteSpecifica(ConsoleConfiguration consoleConfiguration, ConsoleOperationType consoleOperationType, ProtocolProperties properties, 
			IRegistryReader registryReader, IDServizio idServizio, IDSoggetto fruitore) throws ProtocolException;
	
}
