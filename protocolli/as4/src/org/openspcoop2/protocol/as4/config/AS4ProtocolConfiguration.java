/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it). 
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

package org.openspcoop2.protocol.as4.config;

import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.protocol.basic.config.BasicConfiguration;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.constants.FunzionalitaProtocollo;
import org.openspcoop2.utils.transport.TransportRequestContext;

/**
 * Classe che implementa, in base al protocollo AS4, l'interfaccia {@link org.openspcoop2.protocol.sdk.config.IProtocolConfiguration} 
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AS4ProtocolConfiguration extends BasicConfiguration {

	private AS4Properties properties = null;
	
	public AS4ProtocolConfiguration(IProtocolFactory<?> factory) throws ProtocolException {
		super(factory);
		this.properties = AS4Properties.getInstance();
	}
	
	@Override
	public ServiceBinding getProtocolServiceBinding(ServiceBinding integrationServiceBinding, TransportRequestContext transportRequest) throws ProtocolException{
		return ServiceBinding.SOAP; // protocol è forzatamente SOAP1.2
	}
	
	@Override
	public boolean isIntegrationInfoRequired(TipoPdD tipoPdD, ServiceBinding serviceBinding, FunzionalitaProtocollo funzionalitaProtocollo) throws ProtocolException{
		
		if (FunzionalitaProtocollo.RIFERIMENTO_ID_RICHIESTA.equals(funzionalitaProtocollo)){
			if(TipoPdD.DELEGATA.equals(tipoPdD)) {
				return this.properties.isRiferimentoIDRichiesta_PD_Required();
			}
			else {
				return this.properties.isRiferimentoIDRichiesta_PA_Required();
			}
		}
		else {
			return super.isIntegrationInfoRequired(tipoPdD, serviceBinding, funzionalitaProtocollo);
		}
		
	}
	
	@Override
	public boolean isDataPresenteInIdentificativoMessaggio() {
		return !this.properties.generateIDasUUID();
	}
	
}
