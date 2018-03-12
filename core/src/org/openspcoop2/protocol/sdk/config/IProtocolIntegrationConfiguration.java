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

package org.openspcoop2.protocol.sdk.config;

import java.util.List;

import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.constants.PortaApplicativaAzioneIdentificazione;
import org.openspcoop2.core.config.constants.PortaDelegataAzioneIdentificazione;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.protocol.sdk.IComponentFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.constants.ConsoleInterfaceType;

/**
 * Interfaccia di Configurazione dei componenti di integration, valori prelevati dal file openspcoop2-manifest
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author: apoli $
 * @version $Rev: 13574 $, $Date: 2018-01-26 12:24:34 +0100 (Fri, 26 Jan 2018) $
 */

public interface IProtocolIntegrationConfiguration extends IComponentFactory {

	// SUBSCRIPTION
	
	public Subscription createDefaultSubscription(ServiceBinding serviceBinding, IDSoggetto idFruitore, IDServizio idServizio) throws ProtocolException;
	
	public Subscription createSubscription(ServiceBinding serviceBinding, IDSoggetto idFruitore, IDServizio idServizio,
			PortaDelegata portaDelegataDefault, 
			String ruleName, String ... azione ) throws ProtocolException;
	public Subscription createSubscription(ServiceBinding serviceBinding, IDSoggetto idFruitore, IDServizio idServizio,
			PortaDelegata portaDelegataDefault, PortaDelegata portaDelegataDaCopiare,
			String ruleName, String ... azione ) throws ProtocolException;
	
	public List<PortaDelegataAzioneIdentificazione> getAllSubscriptionIdentificationResourceModes(ServiceBinding serviceBinding, ConsoleInterfaceType consoleType) throws ProtocolException;
	
	public boolean useInterfaceNameInSubscriptionInvocationURL(ServiceBinding serviceBinding) throws ProtocolException;
	
	
	// IMPLEMENTATION
	
	public Implementation createDefaultImplementation(ServiceBinding serviceBinding, IDServizio idServizio) throws ProtocolException;
	
	public Implementation createImplementation(ServiceBinding serviceBinding, IDServizio idServizio,
			PortaApplicativa portaApplicativaDefault, 
			String ruleName, String ... azione ) throws ProtocolException;
	public Implementation createImplementation(ServiceBinding serviceBinding, IDServizio idServizio,
			PortaApplicativa portaApplicativaDefault, PortaApplicativa portaApplicativaDaCopiare,
			String ruleName, String ... azione ) throws ProtocolException;
	
	public List<PortaApplicativaAzioneIdentificazione> getAllImplementationIdentificationResourceModes(ServiceBinding serviceBinding, ConsoleInterfaceType consoleType) throws ProtocolException;
	
	public boolean useInterfaceNameInImplementationInvocationURL(ServiceBinding serviceBinding) throws ProtocolException;
}
