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


package org.openspcoop2.testsuite.wssecurity;

import java.io.IOException;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import org.apache.ws.security.WSPasswordCallback;

/**
 * Gestore delle password dei certificati scambiati con wssecurity, gestore di esempio.
 * 	
 * @author Montebove Luciano (L.Montebove@finsiel.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class PWCallbackSend implements CallbackHandler {

    @Override
	public void handle(Callback[] callbacks) throws IOException,
            UnsupportedCallbackException {
        for (int i = 0; i < callbacks.length; i++) {
            if (callbacks[i] instanceof WSPasswordCallback) {
                WSPasswordCallback pc = (WSPasswordCallback) callbacks[i];
		//System.out.println("Alias ["+pc.getIdentifer()+"]");
                // setta la password per l'alias della chiave 
//                if ("erogatore".equals(pc.getIdentifer())) {
//                    pc.setPassword("foobar");
//                }
                //System.out.println("Alias ["+pc.getIdentifer()+"]");
                if ("testsuiteclient".equals(pc.getIdentifier())) {
                	pc.setPassword("certclient");
                }else if ("testsuiteserver".equals(pc.getIdentifier())) {
                	pc.setPassword("certserver");
                }
                
                
              //pc.setPassword("foobar");
            } else {
                throw new UnsupportedCallbackException(callbacks[i],
                        "Unrecognized Callback");
            }
        }
    }
}
