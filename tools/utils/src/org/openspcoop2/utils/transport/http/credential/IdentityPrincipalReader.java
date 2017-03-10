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
package org.openspcoop2.utils.transport.http.credential;

import javax.servlet.http.HttpServletRequest;

import org.openspcoop2.utils.transport.http.HttpServletCredential;
import org.slf4j.Logger;

/**
 * Implementazione dell'interfaccia {@link IPrincipalReader} che utilizza la classe {@link HttpServletCredential} come strumento per leggere il principal dalla request.
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author: mergefairy $
 * @version $Rev: 12774 $, $Date: 2017-03-10 10:44:01 +0100 (Fri, 10 Mar 2017) $
 */
public class IdentityPrincipalReader implements IPrincipalReader{


	private Logger log = null;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public IdentityPrincipalReader(Logger log){
		this.log = log;
		this.log.debug("IdentityPrincipalReader inizializzato.");
	}

	@Override
	public void init(Object... parametri) throws PrincipalReaderException {
	}

	@Override
	public String getPrincipal(HttpServletRequest request) throws PrincipalReaderException {
		this.log.debug("Estrazione principal in corso...");

		try{
			HttpServletCredential identity = new HttpServletCredential(request,this.log);
			String username = identity.getPrincipal();

			this.log.debug("Username trovato nel principal [identity.getPrincipal()]: ["+username+"]");
			this.log.debug("Username trovato nel principal [identity.getUsername()]: ["+identity.getUsername()+"]");

			return username;
		}catch (Exception e) {
			this.log.error("Errore durante la lettura del principal: "+ e.getMessage(),e);
			throw new PrincipalReaderException(e);
		}
	}
}
