/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it). 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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
import org.openspcoop2.core.id.IDPortType;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.protocol.basic.BasicComponentFactory;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.constants.ConsoleInterfaceType;
import org.openspcoop2.protocol.sdk.constants.ConsoleOperationType;
import org.openspcoop2.protocol.sdk.properties.ConsoleConfiguration;
import org.openspcoop2.protocol.sdk.properties.ProtocolProperties;


/**	
 * BasicTraduttore
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author: apoli $
 * @version $Rev: 12362 $, $Date: 2016-11-21 15:44:14 +0100 (Mon, 21 Nov 2016) $
 */
public class BasicDynamicConfiguration extends BasicComponentFactory implements org.openspcoop2.protocol.sdk.properties.IConsoleDynamicConfiguration {


	public BasicDynamicConfiguration(IProtocolFactory<?> factory) throws ProtocolException{
		super(factory);
	}

	
	@Override
	public ConsoleConfiguration getDynamicConfigSoggetto(ConsoleOperationType consoleOperationType, ConsoleInterfaceType consoleInterfaceType, 
			IDSoggetto id) throws ProtocolException{
		return new ConsoleConfiguration();
	}
	@Override
	public void updateDynamicConfigSoggetto(ConsoleConfiguration consoleConfiguration, ConsoleOperationType consoleOperationType, ConsoleInterfaceType consoleInterfaceType, 
			ProtocolProperties properties, 
			IDSoggetto id) throws ProtocolException{
	}
	@Override
	public void validateDynamicConfigSoggetto(ConsoleConfiguration consoleConfiguration, ConsoleOperationType consoleOperationType, ProtocolProperties properties, 
			IDSoggetto id) throws ProtocolException{
	}
	
	@Override
	public ConsoleConfiguration getDynamicConfigAccordoCooperazione(ConsoleOperationType consoleOperationType, ConsoleInterfaceType consoleInterfaceType, 
			IDAccordo id) throws ProtocolException{
		return new ConsoleConfiguration();
	}
	@Override
	public void updateDynamicConfigAccordoCooperazione(ConsoleConfiguration consoleConfiguration, ConsoleOperationType consoleOperationType, ConsoleInterfaceType consoleInterfaceType, 
			ProtocolProperties properties, 
			IDAccordo id) throws ProtocolException{
	}
	@Override
	public void validateDynamicConfigCooperazione(ConsoleConfiguration consoleConfiguration, ConsoleOperationType consoleOperationType, ProtocolProperties properties, 
			IDAccordo id) throws ProtocolException{
	}
	
	@Override
	public ConsoleConfiguration getDynamicConfigAccordoServizioParteComune(ConsoleOperationType consoleOperationType, ConsoleInterfaceType consoleInterfaceType, 
			IDAccordo id) throws ProtocolException{
		return new ConsoleConfiguration();
	}
	@Override
	public void updateDynamicConfigAccordoServizioParteComune(ConsoleConfiguration consoleConfiguration, ConsoleOperationType consoleOperationType, ConsoleInterfaceType consoleInterfaceType, 
			ProtocolProperties properties, 
			IDAccordo id) throws ProtocolException{
	}
	@Override
	public void validateDynamicConfigAccordoServizioParteComune(ConsoleConfiguration consoleConfiguration, ConsoleOperationType consoleOperationType, ProtocolProperties properties, 
			IDAccordo id) throws ProtocolException{
	}
	
	@Override
	public ConsoleConfiguration getDynamicConfigAccordoServizioComposto(ConsoleOperationType consoleOperationType, ConsoleInterfaceType consoleInterfaceType, 
			IDAccordo id) throws ProtocolException{
		return new ConsoleConfiguration();
	}
	@Override
	public void updateDynamicConfigAccordoServizioComposto(ConsoleConfiguration consoleConfiguration, ConsoleOperationType consoleOperationType, ConsoleInterfaceType consoleInterfaceType, 
			ProtocolProperties properties, 
			IDAccordo id) throws ProtocolException{
	}
	@Override
	public void validateDynamicConfigAccordoServizioComposto(ConsoleConfiguration consoleConfiguration, ConsoleOperationType consoleOperationType, ProtocolProperties properties, 
			IDAccordo id) throws ProtocolException{
	}
	
	@Override
	public ConsoleConfiguration getDynamicConfigPortType(ConsoleOperationType consoleOperationType, ConsoleInterfaceType consoleInterfaceType, 
			IDPortType id) throws ProtocolException{
		return new ConsoleConfiguration();
	}
	@Override
	public void updateDynamicConfigPortType(ConsoleConfiguration consoleConfiguration, ConsoleOperationType consoleOperationType,  ConsoleInterfaceType consoleInterfaceType, 
			ProtocolProperties properties, 
			IDPortType id) throws ProtocolException{
	}
	@Override
	public void validateDynamicConfigPortType(ConsoleConfiguration consoleConfiguration, ConsoleOperationType consoleOperationType,  ProtocolProperties properties, 
			IDPortType id) throws ProtocolException{
	}
	
	@Override
	public ConsoleConfiguration getDynamicConfigOperation(ConsoleOperationType consoleOperationType, ConsoleInterfaceType consoleInterfaceType, 
			IDPortType id, String azione) throws ProtocolException{
		return new ConsoleConfiguration();
	}
	@Override
	public void updateDynamicConfigOperation(ConsoleConfiguration consoleConfiguration, ConsoleOperationType consoleOperationType,  ConsoleInterfaceType consoleInterfaceType, 
			ProtocolProperties properties, 
			IDPortType id, String azione) throws ProtocolException{
	}
	@Override
	public void validateDynamicConfigOperation(ConsoleConfiguration consoleConfiguration, ConsoleOperationType consoleOperationType,  ProtocolProperties properties, 
			IDPortType id, String azione) throws ProtocolException{
	}
	
	@Override
	public ConsoleConfiguration getDynamicConfigAzione(ConsoleOperationType consoleOperationType, ConsoleInterfaceType consoleInterfaceType, 
			String azione) throws ProtocolException{
		return new ConsoleConfiguration();
	}
	@Override
	public void updateDynamicConfigAzione(ConsoleConfiguration consoleConfiguration, ConsoleOperationType consoleOperationType,  ConsoleInterfaceType consoleInterfaceType, 
			ProtocolProperties properties, 
			String azione) throws ProtocolException{
	}
	@Override
	public void validateDynamicConfigAzione(ConsoleConfiguration consoleConfiguration, ConsoleOperationType consoleOperationType,  ProtocolProperties properties, 
			String azione) throws ProtocolException{
	}
	
	@Override
	public ConsoleConfiguration getDynamicConfigAccordoServizioParteSpecifica(ConsoleOperationType consoleOperationType, ConsoleInterfaceType consoleInterfaceType, 
			IDServizio id) throws ProtocolException{
		return new ConsoleConfiguration();
	}
	@Override
	public void updateDynamicConfigAccordoServizioParteSpecifica(ConsoleConfiguration consoleConfiguration, ConsoleOperationType consoleOperationType, ConsoleInterfaceType consoleInterfaceType, 
			ProtocolProperties properties, 
			IDServizio id) throws ProtocolException{
	}
	@Override
	public void validateDynamicConfigAccordoServizioParteSpecifica(ConsoleConfiguration consoleConfiguration, ConsoleOperationType consoleOperationType, ProtocolProperties properties, 
			IDServizio id) throws ProtocolException{
	}
	
	@Override
	public ConsoleConfiguration getDynamicConfigFruizioneAccordoServizioParteSpecifica(ConsoleOperationType consoleOperationType, ConsoleInterfaceType consoleInterfaceType, 
			IDServizio idServizio, IDSoggetto fruitore) throws ProtocolException{
		return new ConsoleConfiguration();
	}
	@Override
	public void updateDynamicConfigFruizioneAccordoServizioParteSpecifica(ConsoleConfiguration consoleConfiguration, ConsoleOperationType consoleOperationType, ConsoleInterfaceType consoleInterfaceType, 
			ProtocolProperties properties,  
			IDServizio idServizio, IDSoggetto fruitore) throws ProtocolException{
	}
	@Override
	public void validateDynamicConfigFruizioneAccordoServizioParteSpecifica(ConsoleConfiguration consoleConfiguration, ConsoleOperationType consoleOperationType, ProtocolProperties properties, 
			IDServizio idServizio, IDSoggetto fruitore) throws ProtocolException{
	}

}
