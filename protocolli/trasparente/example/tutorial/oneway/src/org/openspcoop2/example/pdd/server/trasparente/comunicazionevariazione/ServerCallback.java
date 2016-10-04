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
package org.openspcoop2.example.pdd.server.trasparente.comunicazionevariazione;

import java.io.IOException;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;

import org.apache.wss4j.common.ext.WSPasswordCallback;


/**
 * ServerCallback
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ServerCallback implements CallbackHandler {


	@Override
	@SuppressWarnings("deprecation")
	public void handle(Callback[] callbacks) throws IOException,
	UnsupportedCallbackException {
		System.out.println("Callback");
		for (int i = 0; i < callbacks.length; i++) {
			WSPasswordCallback pwcb = (WSPasswordCallback)callbacks[i];
			String id = pwcb.getIdentifier();
			switch (pwcb.getUsage()) {
			case WSPasswordCallback.USERNAME_TOKEN_UNKNOWN:

				// used when plaintext password in message
				if (!"libuser".equals(id) || !"books".equals(pwcb.getPassword())) {
					throw new UnsupportedCallbackException(callbacks[i], "check failed");
				}
				break;

			case WSPasswordCallback.DECRYPT:
			case WSPasswordCallback.SIGNATURE:

				// used to retrieve password for private key
				if ("serverkey".equals(id)) {
					pwcb.setPassword("serverpass");
				}
				break;
			}
		}
	}
}
