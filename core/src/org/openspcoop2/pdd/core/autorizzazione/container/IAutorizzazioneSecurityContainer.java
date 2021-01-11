/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it). 
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



package org.openspcoop2.pdd.core.autorizzazione.container;

import javax.servlet.http.HttpServletRequest;

import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.utils.xacml.XacmlRequest;

/**
 * Interfaccia che definisce un processo di autorizzazione tramite container
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public interface IAutorizzazioneSecurityContainer  {

	public void init(HttpServletRequest req, PdDContext pddContext, IProtocolFactory<?> protocolFactory);
	
	public String getUserPrincipal();
	
	public boolean isUserInRole(String role);
	
	public void fillXacmlRequest(XacmlRequest request);

	
}
