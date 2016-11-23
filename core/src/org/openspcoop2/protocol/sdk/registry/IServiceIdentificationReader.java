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
package org.openspcoop2.protocol.sdk.registry;

import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.utils.transport.TransportRequestContext;

/**
 * IRegistryReader
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author: apoli $
 * @version $Rev: 12237 $, $Date: 2016-10-04 11:41:45 +0200 (Tue, 04 Oct 2016) $
 */
public interface IServiceIdentificationReader {

	// PORTA DELEGATA
	
	public IDPortaDelegata findPortaDelegata(TransportRequestContext transportRequestContext, boolean portaUrlBased) throws RegistryNotFound; 
	
	public IDServizio convertToIDServizio(IDPortaDelegata idPortaDelegata) throws RegistryNotFound; 
	
	
	// PORTA APPLICATIVA
	
	public IDPortaApplicativa findPortaApplicativa(TransportRequestContext transportRequestContext, boolean portaUrlBased) throws RegistryNotFound; 
	
	public IDServizio convertToIDServizio(IDPortaApplicativa idPortaApplicativa) throws RegistryNotFound; 
	
}
