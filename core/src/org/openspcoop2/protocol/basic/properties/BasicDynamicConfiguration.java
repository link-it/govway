/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2017 Link.it srl (http://link.it). 
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

package org.openspcoop2.protocol.basic.properties;

import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDAccordoAzione;
import org.openspcoop2.core.id.IDFruizione;
import org.openspcoop2.core.id.IDPortType;
import org.openspcoop2.core.id.IDPortTypeAzione;
import org.openspcoop2.core.id.IDResource;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.protocol.basic.BasicComponentFactory;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.constants.ConsoleInterfaceType;
import org.openspcoop2.protocol.sdk.constants.ConsoleOperationType;
import org.openspcoop2.protocol.sdk.properties.ConsoleConfiguration;
import org.openspcoop2.protocol.sdk.properties.ProtocolProperties;
import org.openspcoop2.protocol.sdk.registry.IRegistryReader;


/**	
 * BasicTraduttore
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class BasicDynamicConfiguration extends BasicComponentFactory implements org.openspcoop2.protocol.sdk.properties.IConsoleDynamicConfiguration {


	public BasicDynamicConfiguration(IProtocolFactory<?> factory) throws ProtocolException{
		super(factory);
	}

	
	@Override
	public ConsoleConfiguration getDynamicConfigSoggetto(ConsoleOperationType consoleOperationType, ConsoleInterfaceType consoleInterfaceType, 
			IRegistryReader registryReader, IDSoggetto id) throws ProtocolException{
		return new ConsoleConfiguration();
	}
	@Override
	public void updateDynamicConfigSoggetto(ConsoleConfiguration consoleConfiguration, ConsoleOperationType consoleOperationType, ConsoleInterfaceType consoleInterfaceType, 
			ProtocolProperties properties, 
			IRegistryReader registryReader, IDSoggetto id) throws ProtocolException{
	}
	@Override
	public void validateDynamicConfigSoggetto(ConsoleConfiguration consoleConfiguration, ConsoleOperationType consoleOperationType, ProtocolProperties properties, 
			IRegistryReader registryReader, IDSoggetto id) throws ProtocolException{
	}
	
	@Override
	public ConsoleConfiguration getDynamicConfigAccordoCooperazione(ConsoleOperationType consoleOperationType, ConsoleInterfaceType consoleInterfaceType, 
			IRegistryReader registryReader, IDAccordo id) throws ProtocolException{
		return new ConsoleConfiguration();
	}
	@Override
	public void updateDynamicConfigAccordoCooperazione(ConsoleConfiguration consoleConfiguration, ConsoleOperationType consoleOperationType, ConsoleInterfaceType consoleInterfaceType, 
			ProtocolProperties properties, 
			IRegistryReader registryReader, IDAccordo id) throws ProtocolException{
	}
	@Override
	public void validateDynamicConfigCooperazione(ConsoleConfiguration consoleConfiguration, ConsoleOperationType consoleOperationType, ProtocolProperties properties, 
			IRegistryReader registryReader, IDAccordo id) throws ProtocolException{
	}
	
	@Override
	public ConsoleConfiguration getDynamicConfigAccordoServizioParteComune(ConsoleOperationType consoleOperationType, ConsoleInterfaceType consoleInterfaceType, 
			IRegistryReader registryReader, IDAccordo id) throws ProtocolException{
		return new ConsoleConfiguration();
	}
	@Override
	public void updateDynamicConfigAccordoServizioParteComune(ConsoleConfiguration consoleConfiguration, ConsoleOperationType consoleOperationType, ConsoleInterfaceType consoleInterfaceType, 
			ProtocolProperties properties, 
			IRegistryReader registryReader, IDAccordo id) throws ProtocolException{
	}
	@Override
	public void validateDynamicConfigAccordoServizioParteComune(ConsoleConfiguration consoleConfiguration, ConsoleOperationType consoleOperationType, ProtocolProperties properties, 
			IRegistryReader registryReader, IDAccordo id) throws ProtocolException{
	}
	
	@Override
	public ConsoleConfiguration getDynamicConfigAccordoServizioComposto(ConsoleOperationType consoleOperationType, ConsoleInterfaceType consoleInterfaceType, 
			IRegistryReader registryReader, IDAccordo id) throws ProtocolException{
		return new ConsoleConfiguration();
	}
	@Override
	public void updateDynamicConfigAccordoServizioComposto(ConsoleConfiguration consoleConfiguration, ConsoleOperationType consoleOperationType, ConsoleInterfaceType consoleInterfaceType, 
			ProtocolProperties properties, 
			IRegistryReader registryReader, IDAccordo id) throws ProtocolException{
	}
	@Override
	public void validateDynamicConfigAccordoServizioComposto(ConsoleConfiguration consoleConfiguration, ConsoleOperationType consoleOperationType, ProtocolProperties properties, 
			IRegistryReader registryReader, IDAccordo id) throws ProtocolException{
	}
	
	@Override
	public ConsoleConfiguration getDynamicConfigPortType(ConsoleOperationType consoleOperationType, ConsoleInterfaceType consoleInterfaceType, 
			IRegistryReader registryReader, IDPortType id) throws ProtocolException{
		return new ConsoleConfiguration();
	}
	@Override
	public void updateDynamicConfigPortType(ConsoleConfiguration consoleConfiguration, ConsoleOperationType consoleOperationType,  ConsoleInterfaceType consoleInterfaceType, 
			ProtocolProperties properties, 
			IRegistryReader registryReader, IDPortType id) throws ProtocolException{
	}
	@Override
	public void validateDynamicConfigPortType(ConsoleConfiguration consoleConfiguration, ConsoleOperationType consoleOperationType,  ProtocolProperties properties, 
			IRegistryReader registryReader, IDPortType id) throws ProtocolException{
	}
	
	@Override
	public ConsoleConfiguration getDynamicConfigOperation(ConsoleOperationType consoleOperationType, ConsoleInterfaceType consoleInterfaceType, 
			IRegistryReader registryReader, IDPortTypeAzione id) throws ProtocolException{
		return new ConsoleConfiguration();
	}
	@Override
	public void updateDynamicConfigOperation(ConsoleConfiguration consoleConfiguration, ConsoleOperationType consoleOperationType,  ConsoleInterfaceType consoleInterfaceType, 
			ProtocolProperties properties, 
			IRegistryReader registryReader, IDPortTypeAzione id) throws ProtocolException{
	}
	@Override
	public void validateDynamicConfigOperation(ConsoleConfiguration consoleConfiguration, ConsoleOperationType consoleOperationType,  ProtocolProperties properties, 
			IRegistryReader registryReader, IDPortTypeAzione id) throws ProtocolException{
	}
	
	@Override
	public ConsoleConfiguration getDynamicConfigAzione(ConsoleOperationType consoleOperationType, ConsoleInterfaceType consoleInterfaceType, 
			IRegistryReader registryReader, IDAccordoAzione id) throws ProtocolException{
		return new ConsoleConfiguration();
	}
	@Override
	public void updateDynamicConfigAzione(ConsoleConfiguration consoleConfiguration, ConsoleOperationType consoleOperationType,  ConsoleInterfaceType consoleInterfaceType, 
			ProtocolProperties properties, 
			IRegistryReader registryReader, IDAccordoAzione id) throws ProtocolException{
	}
	@Override
	public void validateDynamicConfigAzione(ConsoleConfiguration consoleConfiguration, ConsoleOperationType consoleOperationType,  ProtocolProperties properties, 
			IRegistryReader registryReader, IDAccordoAzione id) throws ProtocolException{
	}
	
	@Override
	public ConsoleConfiguration getDynamicConfigResource(ConsoleOperationType consoleOperationType, ConsoleInterfaceType consoleInterfaceType, 
			IRegistryReader registryReader, IDResource id) throws ProtocolException{
		return new ConsoleConfiguration();
	}
	
	@Override
	public void updateDynamicConfigResource(ConsoleConfiguration consoleConfiguration, ConsoleOperationType consoleOperationType,  ConsoleInterfaceType consoleInterfaceType, 
			ProtocolProperties properties, 
			IRegistryReader registryReader, IDResource id) throws ProtocolException{
	}
	
	@Override
	public void validateDynamicConfigResource(ConsoleConfiguration consoleConfiguration, ConsoleOperationType consoleOperationType,  ProtocolProperties properties, 
			IRegistryReader registryReader, IDResource id) throws ProtocolException{
	}
	
	@Override
	public ConsoleConfiguration getDynamicConfigAccordoServizioParteSpecifica(ConsoleOperationType consoleOperationType, ConsoleInterfaceType consoleInterfaceType, 
			IRegistryReader registryReader, IDServizio id) throws ProtocolException{
		return new ConsoleConfiguration();
	}
	@Override
	public void updateDynamicConfigAccordoServizioParteSpecifica(ConsoleConfiguration consoleConfiguration, ConsoleOperationType consoleOperationType, ConsoleInterfaceType consoleInterfaceType, 
			ProtocolProperties properties, 
			IRegistryReader registryReader, IDServizio id) throws ProtocolException{
	}
	@Override
	public void validateDynamicConfigAccordoServizioParteSpecifica(ConsoleConfiguration consoleConfiguration, ConsoleOperationType consoleOperationType, ProtocolProperties properties, 
			IRegistryReader registryReader, IDServizio id) throws ProtocolException{
	}
	
	@Override
	public ConsoleConfiguration getDynamicConfigFruizioneAccordoServizioParteSpecifica(ConsoleOperationType consoleOperationType, ConsoleInterfaceType consoleInterfaceType, 
			IRegistryReader registryReader, IDFruizione id) throws ProtocolException{
		return new ConsoleConfiguration();
	}
	@Override
	public void updateDynamicConfigFruizioneAccordoServizioParteSpecifica(ConsoleConfiguration consoleConfiguration, ConsoleOperationType consoleOperationType, ConsoleInterfaceType consoleInterfaceType, 
			ProtocolProperties properties,  
			IRegistryReader registryReader, IDFruizione id) throws ProtocolException{
	}
	@Override
	public void validateDynamicConfigFruizioneAccordoServizioParteSpecifica(ConsoleConfiguration consoleConfiguration, ConsoleOperationType consoleOperationType, ProtocolProperties properties, 
			IRegistryReader registryReader, IDFruizione id) throws ProtocolException{
	}

}
