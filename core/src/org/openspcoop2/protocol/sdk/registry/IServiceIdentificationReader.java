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
package org.openspcoop2.protocol.sdk.registry;

import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.utils.transport.TransportRequestContext;

/**
 * IRegistryReader
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public interface IServiceIdentificationReader {

	// PORTA DELEGATA
	
	public IDPortaDelegata findPortaDelegata(TransportRequestContext transportRequestContext, boolean portaUrlBased) throws RegistryNotFound; 
	
	public IDSoggetto convertToIDSoggettoFruitore(IDPortaDelegata idPortaDelegata) throws RegistryNotFound; 
	
	public IDServizio convertToIDServizio(IDPortaDelegata idPortaDelegata) throws RegistryNotFound; 
	
	
	// PORTA APPLICATIVA
	
	public IDPortaApplicativa findPortaApplicativa(TransportRequestContext transportRequestContext, boolean portaUrlBased) throws RegistryNotFound; 
	
	public IDServizio convertToIDServizio(IDPortaApplicativa idPortaApplicativa) throws RegistryNotFound; 
	
}
