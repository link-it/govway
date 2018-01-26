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
package org.openspcoop2.protocol.sdk.registry;

import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.protocol.sdk.IProtocolFactory;

/**
 * IRegistryReader
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public interface IConfigIntegrationReader {
	
	// SERVIZI APPLICATIVI
	
	public boolean existsServizioApplicativo(IDServizioApplicativo idServizioApplicativo);
	public ServizioApplicativo getServizioApplicativo(IDServizioApplicativo idServizioApplicativo) throws RegistryNotFound,RegistryException;
	
	public boolean existsServizioApplicativoByCredenzialiBasic(String username, String password);
	public ServizioApplicativo getServizioApplicativoByCredenzialiBasic(String username, String password) throws RegistryNotFound,RegistryException;
	
	public boolean existsServizioApplicativoByCredenzialiSsl(String subject);
	public ServizioApplicativo getServizioApplicativoByCredenzialiSsl(String subject) throws RegistryNotFound,RegistryException;
	
	public boolean existsServizioApplicativoByCredenzialiPrincipal(String principal);
	public ServizioApplicativo getServizioApplicativoByCredenzialiPrincipal(String principal) throws RegistryNotFound,RegistryException;
	
	
	// PORTA DELEGATA
	
	public IDPortaDelegata getIdPortaDelegata(String nome, IProtocolFactory<?> protocolFactory) throws RegistryNotFound,RegistryException;
	public boolean existsPortaDelegata(IDPortaDelegata idPortaDelegata); 
	public PortaDelegata getPortaDelegata(IDPortaDelegata idPortaDelegata) throws RegistryNotFound,RegistryException; 
	
	
	// PORTA APPLICATIVA
	
	public IDPortaApplicativa getIdPortaApplicativa(String nome, IProtocolFactory<?> protocolFactory) throws RegistryNotFound,RegistryException;
	public boolean existsPortaApplicativa(IDPortaApplicativa idPortaApplicativa); 
	public PortaApplicativa getPortaApplicativa(IDPortaApplicativa idPortaApplicativa) throws RegistryNotFound,RegistryException; 
	
	
}
