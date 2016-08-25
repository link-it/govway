/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
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
package org.openspcoop2.example.pdd.client.trasparente.richiestastatofamiglia;

import java.io.IOException;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;

import org.apache.wss4j.common.ext.WSPasswordCallback;


/**
 * ClientCallback
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ClientCallback implements CallbackHandler {


	@Override
	public void handle(Callback[] callbacks) throws IOException {
		for (int i = 0; i < callbacks.length; i++) {
			WSPasswordCallback pwcb = (WSPasswordCallback)callbacks[i];
			String id = pwcb.getIdentifier();
			int usage = pwcb.getUsage();
			if (usage == WSPasswordCallback.DECRYPT || usage == WSPasswordCallback.SIGNATURE) {

				// used to retrieve password for private key
				if ("clientkey".equals(id)) {
					pwcb.setPassword("clientpass");
				}

			}
		}
	}
}

